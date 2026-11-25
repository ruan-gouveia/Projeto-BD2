#!/usr/bin/env bash
base="http://localhost:8080"

# call METODO URL [CORPO] -> preenche STATUS e BODY
call() {
  local out
  if [ -n "$3" ]; then
    out=$(curl -s -w "\n%{http_code}" -X "$1" "$base$2" -H "Content-Type: application/json; charset=utf-8" -d "$3")
  else
    out=$(curl -s -w "\n%{http_code}" -X "$1" "$base$2" -H "Content-Type: application/json; charset=utf-8")
  fi
  STATUS=${out##*$'\n'}
  BODY=${out%$'\n'*}
}

# campo JSON CHAVE -> valor (para números e textos simples)
campo() { echo "$1" | grep -o "\"$2\":[^,}]*" | head -1 | cut -d: -f2 | tr -d '"'; }

# --- preparacao ---
SUF=$(date +%s)
call POST /api/administradores '{"nome":"Admin Teste","email":"admin'"$SUF"'@badplay.local","senha":"123456"}'
ADM=$(campo "$BODY" idAdministrador)
call POST /api/generos '{"nome":"Acao'"$SUF"'","descricao":"Filmes de acao"}'
GEN=$(campo "$BODY" idGenero)
call POST /api/usuarios '{"nome":"Usuario Teste","email":"user'"$SUF"'@badplay.local","senha":"123456","dataNascimento":"2000-01-01"}'
USR=$(campo "$BODY" idUsuario)

if [ -z "$ADM" ] || [ -z "$GEN" ] || [ -z "$USR" ]; then
  echo "Falha na preparacao (ADM=$ADM GEN=$GEN USR=$USR)"
  exit 1
fi

# --- filme ---
call POST /api/filmes '{"titulo":"Filme Versao","descricao":"teste","urlFilme":"http://x/f.mp4","duracaoMinutos":100,"idAdministrador":'"$ADM"',"generosIds":['"$GEN"']}'
F_ID=$(campo "$BODY" idConteudo)
F_VER=$(campo "$BODY" versao)
echo "1) POST filme .......... versao=$F_VER  (esperado 0)"

PUT_F='{"titulo":"Filme v1","descricao":"editado","urlFilme":"http://x/f.mp4","duracaoMinutos":100,"idAdministrador":'"$ADM"',"generosIds":['"$GEN"'],"versao":'"$F_VER"'}'
call PUT "/api/filmes/$F_ID" "$PUT_F"
echo "2) PUT versao correta .. HTTP $STATUS versao=$(campo "$BODY" versao)  (esperado 200 / 1)"

call PUT "/api/filmes/$F_ID" "$PUT_F"
echo "3) PUT versao antiga ... HTTP $STATUS  (esperado 409)"

call PUT "/api/filmes/$F_ID" '{"titulo":"Sem versao","descricao":"x","urlFilme":"http://x/f.mp4","duracaoMinutos":100,"idAdministrador":'"$ADM"',"generosIds":['"$GEN"']}'
echo "4) PUT sem versao ...... HTTP $STATUS  (esperado 400)"

# --- serie ---
call POST /api/series '{"titulo":"Serie Versao","descricao":"teste","idAdministrador":'"$ADM"',"generosIds":['"$GEN"']}'
S_ID=$(campo "$BODY" idConteudo)
S_VER=$(campo "$BODY" versao)

PUT_S='{"titulo":"Serie v1","descricao":"editado","idAdministrador":'"$ADM"',"generosIds":['"$GEN"'],"versao":'"$S_VER"'}'
call PUT "/api/series/$S_ID" "$PUT_S"
echo "5) PUT serie ........... HTTP $STATUS versao=$(campo "$BODY" versao)  (esperado 200 / 1)"

call PUT "/api/series/$S_ID" "$PUT_S"
echo "6) PUT serie repetido .. HTTP $STATUS  (esperado 409)"

# --- historico ---
t0=$(date +%s%N)
call POST /api/historico '{"idUsuario":'"$USR"',"idConteudo":'"$F_ID"'}'
t1=$(date +%s%N)
echo "7) POST historico ...... HTTP $STATUS em $(( (t1 - t0) / 1000000 )) ms  (esperado 202)"

sleep 2
call GET "/api/historico/usuario/$USR"
echo "8) GET historico ....... $BODY"
