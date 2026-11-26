# Scripts de Teste

Scripts automatizados para validar os endpoints da API BadPlay.

## Arquivos

| Arquivo | Descrição |
|---|---|
| `testes.sh` | Executa 8 cenários ponta a ponta cobrindo **bloqueio otimista** (Filmes e Séries) e **escrita assíncrona** no histórico de reprodução |

## Pré-requisitos

- Ambiente local no ar (`docker compose up -d` e API rodando em `http://localhost:8080`)
- `bash` e `curl` (no Windows, pode ser executado pelo Git Bash ou WSL)

## Como executar

A partir da raiz do projeto:

```bash
bash scripts/testes.sh
```

> O script gera e-mails e nome de gênero únicos usando timestamp (`date +%s`), permitindo múltiplas execuções seguidas sem precisar limpar o banco.

## Cenários validados pelo `testes.sh`

1. **POST filme** — verifica que o conteúdo nasce com `versao=0`
2. **PUT filme (versão correta)** — retorna `HTTP 200` e incrementa para `versao=1`
3. **PUT filme (versão antiga)** — detecta conflito e retorna `HTTP 409`
4. **PUT filme (sem versão)** — rejeita atualização sem o campo `versao` (`HTTP 400`)
5. **PUT série (versão correta)** — retorna `HTTP 200` e incrementa para `versao=1`
6. **PUT série (versão antiga)** — detecta conflito e retorna `HTTP 409`
7. **POST histórico** — retorna `HTTP 202` imediatamente (processamento assíncrono) e exibe o tempo de resposta
8. **GET histórico** — aguarda 2 segundos e confirma que o registro foi persistido
