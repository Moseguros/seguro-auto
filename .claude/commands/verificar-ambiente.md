---
description: Verifica (e ajuda a instalar) tudo o que é preciso para correr a MoSeguros nesta máquina
argument-hint: "[--fix] [--full]"
allowed-tools: Bash(./scripts/doctor.sh:*), Bash(bash scripts/doctor.sh:*)
---

Verifica se esta máquina tem as ferramentas para desenvolver e correr a MoSeguros localmente.

1. Corre `./scripts/doctor.sh $ARGUMENTS` na raiz do repositório (no Windows o Claude Code usa o Git Bash, por isso o mesmo comando funciona). Se `$ARGUMENTS` estiver vazio, corre sem opções.
2. Lê o resultado e responde ao utilizador em português, curto:
   - se estiver tudo pronto, diz isso numa frase e, se não correu com `--full`, sugere correr `/verificar-ambiente --full` uma vez para provar que os testes do backend correm aqui;
   - se houver linhas `[FALTA]`, lista só essas, por ordem, cada uma com o comando de instalação que o script sugeriu para este sistema operativo;
   - os `[AVISO]` de ferramentas "ainda não precisas" resumem-se numa linha (ex.: "Node, Flutter e Android só serão precisos quando existirem `web/` e `mobile/`").
3. **Não instales nada nem mudes configurações sem o utilizador pedir.** Podes oferecer:
   - descarregar as imagens Docker em falta (`./scripts/doctor.sh --fix`) — é seguro e pode ser feito logo se o utilizador aceitar;
   - correr os comandos `winget`/`brew`/`apt` sugeridos — só depois de o utilizador confirmar, um de cada vez, e avisando que alguns pedem permissões de administrador ou reiniciar o terminal (o PATH só muda num terminal novo).
4. Depois de qualquer instalação, corre o script de novo para confirmar.

Se o script não existir ou falhar por um motivo que não é uma ferramenta em falta, mostra o erro e pára.
