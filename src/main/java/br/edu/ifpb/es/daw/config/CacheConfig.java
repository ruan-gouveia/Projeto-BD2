package br.edu.ifpb.es.daw.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;

import java.time.Duration;
import java.util.Map;

@Configuration
@EnableCaching
public class CacheConfig implements CachingConfigurer {

    private static final Logger log = LoggerFactory.getLogger(CacheConfig.class);

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {

        RedisCacheConfiguration padrao = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10))
                .disableCachingNullValues()
                .prefixCacheNameWith("badplay:")
                .serializeValuesWith(SerializationPair.fromSerializer(
                        new JdkSerializationRedisSerializer(getClass().getClassLoader())));

        Map<String, RedisCacheConfiguration> porCache = Map.of(
                CacheNames.GENEROS, padrao.entryTtl(Duration.ofHours(1)),
                CacheNames.PLANOS, padrao.entryTtl(Duration.ofHours(1)),
                CacheNames.FILMES, padrao.entryTtl(Duration.ofMinutes(15)),
                CacheNames.SERIES, padrao.entryTtl(Duration.ofMinutes(15)),
                CacheNames.FILMES_LISTA, padrao.entryTtl(Duration.ofMinutes(5)),
                CacheNames.SERIES_LISTA, padrao.entryTtl(Duration.ofMinutes(5)),
                CacheNames.CONTEUDOS, padrao.entryTtl(Duration.ofMinutes(5)),
                CacheNames.AVALIACOES, padrao.entryTtl(Duration.ofMinutes(2))
        );

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(padrao)
                .withInitialCacheConfigurations(porCache)
                .transactionAware()
                .build();
    }

    @Override
    public CacheErrorHandler errorHandler() {
        return new CacheErrorHandler() {
            @Override
            public void handleCacheGetError(RuntimeException e, Cache cache, Object key) {
                log.warn("Falha ao LER do cache '{}' (key={}): {}", cache.getName(), key, e.getMessage());
            }

            @Override
            public void handleCachePutError(RuntimeException e, Cache cache, Object key, Object value) {
                log.warn("Falha ao GRAVAR no cache '{}' (key={}): {}", cache.getName(), key, e.getMessage());
            }

            @Override
            public void handleCacheEvictError(RuntimeException e, Cache cache, Object key) {
                log.warn("Falha ao REMOVER do cache '{}' (key={}): {}", cache.getName(), key, e.getMessage());
            }

            @Override
            public void handleCacheClearError(RuntimeException e, Cache cache) {
                log.warn("Falha ao LIMPAR o cache '{}': {}", cache.getName(), e.getMessage());
            }
        };
    }
}