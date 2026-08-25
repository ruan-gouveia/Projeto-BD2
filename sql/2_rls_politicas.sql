GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO anon, authenticated;

ALTER TABLE "tb_usuario" ENABLE ROW LEVEL SECURITY;
ALTER TABLE "tb_conteudo" ENABLE ROW LEVEL SECURITY;
ALTER TABLE "tb_filme" ENABLE ROW LEVEL SECURITY;
ALTER TABLE "tb_serie" ENABLE ROW LEVEL SECURITY;
ALTER TABLE "tb_temporada" ENABLE ROW LEVEL SECURITY;
ALTER TABLE "tb_episodio" ENABLE ROW LEVEL SECURITY;
ALTER TABLE "tb_genero" ENABLE ROW LEVEL SECURITY;
ALTER TABLE "tb_conteudo_genero" ENABLE ROW LEVEL SECURITY;
ALTER TABLE "tb_plano" ENABLE ROW LEVEL SECURITY;
ALTER TABLE "tb_assinatura" ENABLE ROW LEVEL SECURITY;
ALTER TABLE "tb_avaliacao" ENABLE ROW LEVEL SECURITY;
ALTER TABLE "tb_historico" ENABLE ROW LEVEL SECURITY;
ALTER TABLE "tb_lista_desejo" ENABLE ROW LEVEL SECURITY;
ALTER TABLE "tb_lista_conteudo" ENABLE ROW LEVEL SECURITY;
ALTER TABLE "tb_recomendacao" ENABLE ROW LEVEL SECURITY;


-- PERFIL: O usuário logado só vê/altera a si mesmo
CREATE POLICY "Privacidade de Perfil" ON "tb_usuario" FOR ALL TO authenticated USING (id_usuario = auth.uid());

-- CATÁLOGO: Leitura para Anônimos e Logados.
CREATE POLICY "Leitura Publica Conteudos" ON "tb_conteudo" FOR SELECT TO anon, authenticated USING (true);
CREATE POLICY "Leitura Publica Filmes" ON "tb_filme" FOR SELECT TO anon, authenticated USING (true);
CREATE POLICY "Leitura Publica Series" ON "tb_serie" FOR SELECT TO anon, authenticated USING (true);
CREATE POLICY "Leitura Publica Temporadas" ON "tb_temporada" FOR SELECT TO anon, authenticated USING (true);
CREATE POLICY "Leitura Publica Episodios" ON "tb_episodio" FOR SELECT TO anon, authenticated USING (true);
CREATE POLICY "Leitura Publica Generos" ON "tb_genero" FOR SELECT TO anon, authenticated USING (true);
CREATE POLICY "Leitura Publica Planos" ON "tb_plano" FOR SELECT TO anon, authenticated USING (true);
CREATE POLICY "Leitura Publica Conteudo Genero" ON "tb_conteudo_genero" FOR SELECT TO anon, authenticated USING (true);

-- CATÁLOGO: Escrita APENAS para Admins.
CREATE POLICY "Admin Modifica Catalogo" ON "tb_conteudo" FOR ALL TO authenticated USING (EXISTS (SELECT 1 FROM tb_usuario WHERE id_usuario = auth.uid() AND cargo = 'ADMIN'));
CREATE POLICY "Admin Modifica Filmes" ON "tb_filme" FOR ALL TO authenticated USING (EXISTS (SELECT 1 FROM tb_usuario WHERE id_usuario = auth.uid() AND cargo = 'ADMIN'));
CREATE POLICY "Admin Modifica Series" ON "tb_serie" FOR ALL TO authenticated USING (EXISTS (SELECT 1 FROM tb_usuario WHERE id_usuario = auth.uid() AND cargo = 'ADMIN'));
CREATE POLICY "Admin Modifica Temporadas" ON "tb_temporada" FOR ALL TO authenticated USING (EXISTS (SELECT 1 FROM tb_usuario WHERE id_usuario = auth.uid() AND cargo = 'ADMIN'));
CREATE POLICY "Admin Modifica Episodios" ON "tb_episodio" FOR ALL TO authenticated USING (EXISTS (SELECT 1 FROM tb_usuario WHERE id_usuario = auth.uid() AND cargo = 'ADMIN'));
CREATE POLICY "Admin Modifica Generos" ON "tb_genero" FOR ALL TO authenticated USING (EXISTS (SELECT 1 FROM tb_usuario WHERE id_usuario = auth.uid() AND cargo = 'ADMIN'));
CREATE POLICY "Admin Modifica Planos" ON "tb_plano" FOR ALL TO authenticated USING (EXISTS (SELECT 1 FROM tb_usuario WHERE id_usuario = auth.uid() AND cargo = 'ADMIN'));
CREATE POLICY "Admin Modifica Conteudo Genero" ON "tb_conteudo_genero" FOR ALL TO authenticated USING (EXISTS (SELECT 1 FROM tb_usuario WHERE id_usuario = auth.uid() AND cargo = 'ADMIN'));

-- ISOLAMENTO ESTRITO: Listas de Desejo, Assinaturas e Recomendações (Apenas o próprio usuário acessa)
CREATE POLICY "Isolamento Assinatura" ON "tb_assinatura" FOR ALL TO authenticated USING (id_usuario = auth.uid());
CREATE POLICY "Isolamento Lista" ON "tb_lista_desejo" FOR ALL TO authenticated USING (id_usuario = auth.uid());
CREATE POLICY "Isolamento Conteudo da Lista" ON "tb_lista_conteudo" FOR ALL TO authenticated USING (EXISTS (SELECT 1 FROM tb_lista_desejo l WHERE l.id_lista = tb_lista_conteudo.id_lista AND l.id_usuario = auth.uid()));
CREATE POLICY "Isolamento Recomendacoes" ON "tb_recomendacao" FOR ALL TO authenticated USING (id_usuario = auth.uid());

-- HISTÓRICO: Leitura e Deleção para o próprio usuário. INSERT travado por validação de Assinatura Ativa.
CREATE POLICY "Leitura do Historico" ON "tb_historico" FOR SELECT TO authenticated USING (id_usuario = auth.uid());
CREATE POLICY "Delecao do Historico" ON "tb_historico" FOR DELETE TO authenticated USING (id_usuario = auth.uid());
CREATE POLICY "Assinante Ativo Assiste" ON "tb_historico" FOR INSERT TO authenticated
WITH CHECK (id_usuario = auth.uid() AND EXISTS (SELECT 1 FROM tb_assinatura a WHERE a.id_usuario = auth.uid() AND a.status = 'ATIVA' AND a.data_fim >= CURRENT_DATE));

-- AVALIAÇÃO: Leitura Pública, Escrita Privada (Dono)
CREATE POLICY "Leitura Publica Avaliacoes" ON "tb_avaliacao" FOR SELECT TO anon, authenticated USING (true);
CREATE POLICY "Escrita Privada Avaliacoes" ON "tb_avaliacao" FOR ALL TO authenticated USING (id_usuario = auth.uid());