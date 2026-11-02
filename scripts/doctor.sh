#!/usr/bin/env bash
# Verifica se esta máquina tem tudo o que é preciso para correr a MoSeguros localmente.
#
# Uso:
#   ./scripts/doctor.sh           verifica e mostra o que falta
#   ./scripts/doctor.sh --fix     também descarrega as imagens Docker em falta
#   ./scripts/doctor.sh --full    também corre os testes do backend (prova que Docker + Testcontainers funcionam)
#   ./scripts/doctor.sh --help
#
# No Windows corre no Git Bash, ou pelo atalho scripts\doctor.ps1 a partir do PowerShell.
# Sai com código 1 se faltar alguma coisa obrigatória.
#
# Mantém-no compatível com o bash 3.2 do macOS: nada de arrays associativos nem mapfile.

set -u

# ---------------------------------------------------------------- versões exigidas
JAVA_MAJOR=17
NODE_MAJOR=22          # AGENTS.md §2; se existir web/.nvmrc, vale esse
PNPM_MAJOR=9
FLUTTER_MIN="3.24.0"   # se o mobile/pubspec.yaml fixar "flutter: x.y.z", vale essa
DOCKER_MIN_MEM_GB=4
PORTS="5432 8080 3000 5173 1025 8025 8090"

# ---------------------------------------------------------------- opções
FIX=0
FULL=0
for arg in "$@"; do
  case "$arg" in
    --fix) FIX=1 ;;
    --full) FULL=1 ;;
    -h|--help) sed -n '2,13p' "$0" | sed 's/^# \{0,1\}//'; exit 0 ;;
    *) echo "Opção desconhecida: $arg (use --help)"; exit 2 ;;
  esac
done

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT" || exit 2

# ---------------------------------------------------------------- saída
if [ -t 1 ] && [ -z "${NO_COLOR:-}" ]; then
  C_OK=$'\033[32m'; C_FAIL=$'\033[31m'; C_WARN=$'\033[33m'; C_DIM=$'\033[2m'; C_B=$'\033[1m'; C_0=$'\033[0m'
else
  C_OK=""; C_FAIL=""; C_WARN=""; C_DIM=""; C_B=""; C_0=""
fi
FAILS=0
WARNS=0
ok()      { printf '  %s[ OK ]%s  %s\n' "$C_OK" "$C_0" "$1"; }
fail()    { printf '  %s[FALTA]%s %s\n' "$C_FAIL" "$C_0" "$1"; FAILS=$((FAILS+1)); }
warn()    { printf '  %s[AVISO]%s %s\n' "$C_WARN" "$C_0" "$1"; WARNS=$((WARNS+1)); }
info()    { printf '  %s[ -- ]%s  %s\n' "$C_DIM" "$C_0" "$1"; }
hint()    { printf '          %s→ %s%s\n' "$C_DIM" "$1" "$C_0"; }
section() { printf '\n%s%s%s\n' "$C_B" "$1" "$C_0"; }
has()     { command -v "$1" >/dev/null 2>&1; }

# Falha ou aviso conforme a área seja obrigatória (a pasta já existe) ou ainda não.
need() { if [ "$1" = 1 ]; then fail "$2"; else warn "$2 (ainda não é precisa: $3 ainda não existe)"; fi; }

# ---------------------------------------------------------------- sistema operativo
case "$(uname -s 2>/dev/null)" in
  MINGW*|MSYS*|CYGWIN*) OS=windows ;;
  Darwin) OS=mac ;;
  *) OS=linux ;;
esac

# Comando de instalação sugerido por SO: install_hint <windows> <mac> <linux>
install_hint() {
  case "$OS" in
    windows) hint "$1" ;;
    mac) hint "$2" ;;
    linux) hint "$3" ;;
  esac
}

# Compara versões x.y.z: devolve 0 se $1 >= $2
version_ge() {
  [ "$(printf '%s\n%s\n' "$2" "$1" | sort -t. -k1,1n -k2,2n -k3,3n | head -n1)" = "$2" ]
}

# Porta em escuta? (Linux: ss/lsof; macOS: lsof; Windows/Git Bash: netstat)
port_in_use() {
  p="$1"
  if [ "$OS" = windows ]; then
    netstat -ano 2>/dev/null | tr -d '\r' | grep -E "LISTENING|ESCUTA" | grep -Eq "[:.]$p[[:space:]]"
  elif has ss; then
    ss -ltnH 2>/dev/null | awk '{print $4}' | grep -Eq "[:.]$p$"
  elif has lsof; then
    lsof -nP -iTCP:"$p" -sTCP:LISTEN >/dev/null 2>&1
  else
    return 1
  fi
}

[ -d backend ] && NEED_BACKEND=1 || NEED_BACKEND=0
[ -d web ] && NEED_WEB=1 || NEED_WEB=0
[ -d mobile ] && NEED_MOBILE=1 || NEED_MOBILE=0
[ -d infra ] && NEED_INFRA=1 || NEED_INFRA=0

printf '%sMoSeguros — verificação do ambiente%s  %s(%s, %s)%s\n' "$C_B" "$C_0" "$C_DIM" "$OS" "$ROOT" "$C_0"

# ================================================================ Git
section "Git"
if has git; then
  ok "git $(git --version | awk '{print $3}')"
  if git rev-parse --is-inside-work-tree >/dev/null 2>&1; then
    # Fins de linha: os scripts e o mvnw têm de ter LF, senão falham dentro dos contentores Linux.
    crlf_files=""
    for f in backend/mvnw scripts/doctor.sh; do
      [ -f "$f" ] && grep -q $'\r' "$f" && crlf_files="$crlf_files $f"
    done
    if [ -n "$crlf_files" ]; then
      fail "ficheiros com fins de linha Windows (CRLF):$crlf_files"
      hint "o .gitattributes já obriga a LF; refaça a cópia de trabalho (perde alterações não gravadas):"
      hint "git stash && git rm -rq --cached . && git reset -q --hard && git stash pop"
    else
      ok "fins de linha LF nos scripts"
    fi
    if [ "$OS" = windows ] && [ "$(git config --get core.autocrlf 2>/dev/null)" = "true" ]; then
      info "core.autocrlf=true; o .gitattributes do projecto sobrepõe-se a isto, não é preciso mudar"
    fi
  fi
else
  fail "git não encontrado"
  install_hint "winget install --id Git.Git -e" "xcode-select --install" "sudo apt install git"
fi

# ================================================================ Docker
section "Docker"
DOCKER_OK=0
if ! has docker; then
  fail "docker não encontrado"
  install_hint "winget install --id Docker.DockerDesktop -e  (depois abrir o Docker Desktop)" \
               "brew install --cask docker  (depois abrir o Docker Desktop)" \
               "https://docs.docker.com/engine/install/"
elif ! docker info >/dev/null 2>&1; then
  fail "o Docker está instalado mas o motor não está a correr"
  install_hint "abrir o Docker Desktop e esperar pelo estado 'Engine running'" \
               "abrir o Docker Desktop" "sudo systemctl start docker"
else
  DOCKER_OK=1
  ok "Docker $(docker version --format '{{.Server.Version}}' 2>/dev/null) (motor a correr)"
  if docker compose version >/dev/null 2>&1; then
    ok "docker compose $(docker compose version --short 2>/dev/null)"
  else
    fail "docker compose (v2) não encontrado"
    hint "vem com o Docker Desktop; no Linux: sudo apt install docker-compose-plugin"
  fi
  ostype=$(docker info --format '{{.OSType}}' 2>/dev/null)
  if [ "$ostype" != "linux" ]; then
    fail "o Docker está em modo de contentores '$ostype'; o projecto precisa de contentores Linux"
    hint "Docker Desktop → menu da bandeja → 'Switch to Linux containers'"
  fi
  mem=$(docker info --format '{{.MemTotal}}' 2>/dev/null)
  if [ -n "$mem" ] && [ "$mem" -gt 0 ] 2>/dev/null; then
    mem_gb=$((mem / 1024 / 1024 / 1024))
    if [ "$mem_gb" -lt "$DOCKER_MIN_MEM_GB" ]; then
      warn "o Docker tem ${mem_gb} GB de memória; recomendado ≥ ${DOCKER_MIN_MEM_GB} GB"
      hint "Docker Desktop → Settings → Resources (ou o ficheiro .wslconfig no Windows com WSL 2)"
    else
      ok "memória do Docker: ${mem_gb} GB"
    fi
  fi
fi

# ================================================================ Imagens Docker
section "Imagens Docker (scripts/images.txt)"
if [ "$DOCKER_OK" = 1 ]; then
  grep -v '^[[:space:]]*#' scripts/images.txt | grep -v '^[[:space:]]*$' | while read -r image when _; do
    [ "$when" = "infra" ] && required=$NEED_INFRA || required=1
    if docker image inspect "$image" >/dev/null 2>&1; then
      ok "$image"
      continue
    fi
    if [ "$FIX" = 1 ]; then
      printf '          a descarregar %s…\n' "$image"
      if docker pull -q "$image" >/dev/null 2>&1; then
        ok "$image (descarregada)"
        continue
      fi
      # Limite de pedidos do Docker Hub (429): tentar o espelho da Google e etiquetar com o nome oficial.
      case "$image" in
        */*) mirror="mirror.gcr.io/$image" ;;
        *)   mirror="mirror.gcr.io/library/$image" ;;
      esac
      if docker pull -q "$mirror" >/dev/null 2>&1 && docker tag "$mirror" "$image"; then
        ok "$image (descarregada do espelho mirror.gcr.io)"
        continue
      fi
      echo "FAIL" >> "${TMPDIR:-/tmp}/moseguros-doctor.$$"
      printf '  %s[FALTA]%s %s — não foi possível descarregar\n' "$C_FAIL" "$C_0" "$image"
      hint "verifique a ligação à internet ou faça docker login (limite de pedidos do Docker Hub)"
    elif [ "$required" = 1 ]; then
      echo "FAIL" >> "${TMPDIR:-/tmp}/moseguros-doctor.$$"
      printf '  %s[FALTA]%s %s\n' "$C_FAIL" "$C_0" "$image"
    else
      echo "WARN" >> "${TMPDIR:-/tmp}/moseguros-doctor.$$"
      printf '  %s[AVISO]%s %s (ainda não é precisa: infra/ ainda não existe)\n' "$C_WARN" "$C_0" "$image"
    fi
  done
  # O while corre numa subshell; os contadores voltam por um ficheiro temporário.
  tmp="${TMPDIR:-/tmp}/moseguros-doctor.$$"
  if [ -f "$tmp" ]; then
    n=$(grep -c FAIL "$tmp"); FAILS=$((FAILS+n))
    n=$(grep -c WARN "$tmp"); WARNS=$((WARNS+n))
    grep -q . "$tmp" && [ "$FIX" = 0 ] && hint "para descarregar as imagens em falta: ./scripts/doctor.sh --fix"
    rm -f "$tmp"
  fi
else
  info "não verificadas (o Docker não está disponível)"
fi

# ================================================================ Java
section "Java (backend)"
java_major() { "$1" -version 2>&1 | awk -F'"' '/version/ {print $2; exit}' | awk -F. '{ if ($1 == "1") print $2; else print $1 }'; }
JAVA_BIN=""
if [ -n "${JAVA_HOME:-}" ]; then
  jh="$JAVA_HOME"
  has cygpath && jh="$(cygpath -u "$JAVA_HOME")"
  if [ -x "$jh/bin/java" ] || [ -x "$jh/bin/java.exe" ]; then
    JAVA_BIN="$jh/bin/java"
  else
    fail "JAVA_HOME aponta para '$JAVA_HOME', mas não há lá bin/java"
  fi
elif has java; then
  JAVA_BIN="java"
fi
if [ -n "$JAVA_BIN" ]; then
  v=$(java_major "$JAVA_BIN")
  src="java no PATH"; [ -n "${JAVA_HOME:-}" ] && src="JAVA_HOME"
  if [ "$v" = "$JAVA_MAJOR" ]; then
    ok "JDK $JAVA_MAJOR ($src: $("$JAVA_BIN" -version 2>&1 | grep -m1 ' version ' | tr -d '\r'))"
  else
    need "$NEED_BACKEND" "o JDK usado pelo Maven ($src) é a versão ${v:-?}; o projecto precisa da $JAVA_MAJOR" "backend/"
    hint "o ./mvnw usa o JAVA_HOME; aponte-o para um JDK $JAVA_MAJOR (Temurin)"
    install_hint "winget install --id EclipseAdoptium.Temurin.17.JDK -e" "brew install --cask temurin@17" "sudo apt install openjdk-17-jdk"
  fi
else
  need "$NEED_BACKEND" "JDK $JAVA_MAJOR não encontrado" "backend/"
  install_hint "winget install --id EclipseAdoptium.Temurin.17.JDK -e" "brew install --cask temurin@17" "sudo apt install openjdk-17-jdk"
fi
if [ -f backend/mvnw ]; then
  if [ -x backend/mvnw ] || [ "$OS" = windows ]; then
    ok "Maven Wrapper (backend/mvnw) — não é preciso instalar o Maven"
  else
    fail "backend/mvnw não é executável"
    hint "chmod +x backend/mvnw"
  fi
fi

# ================================================================ Node e pnpm
section "Node.js e pnpm (web)"
[ -f web/.nvmrc ] && NODE_MAJOR=$(tr -dc '0-9.' < web/.nvmrc | cut -d. -f1)
if has node; then
  nv=$(node -v | tr -d 'v\r')
  if [ "${nv%%.*}" = "$NODE_MAJOR" ]; then
    ok "Node $nv"
  else
    need "$NEED_WEB" "Node $nv; o projecto usa o Node $NODE_MAJOR" "web/"
    install_hint "winget install --id CoreyButler.NVMforWindows -e  e depois: nvm install $NODE_MAJOR && nvm use $NODE_MAJOR" \
                 "brew install node@$NODE_MAJOR" "https://github.com/nvm-sh/nvm  e depois: nvm install $NODE_MAJOR"
  fi
else
  need "$NEED_WEB" "Node.js não encontrado" "web/"
  install_hint "winget install --id CoreyButler.NVMforWindows -e  e depois: nvm install $NODE_MAJOR && nvm use $NODE_MAJOR" \
               "brew install node@$NODE_MAJOR" "https://github.com/nvm-sh/nvm  e depois: nvm install $NODE_MAJOR"
fi
if has pnpm; then
  pv=$(pnpm -v 2>/dev/null | tr -d '\r')
  if [ "${pv%%.*}" = "$PNPM_MAJOR" ]; then ok "pnpm $pv"; else
    need "$NEED_WEB" "pnpm $pv; o projecto usa o pnpm $PNPM_MAJOR" "web/"
    hint "corepack enable && corepack prepare pnpm@$PNPM_MAJOR --activate"
  fi
else
  need "$NEED_WEB" "pnpm não encontrado" "web/"
  hint "corepack enable && corepack prepare pnpm@$PNPM_MAJOR --activate   (o corepack vem com o Node)"
fi

# ================================================================ Flutter e Android
section "Flutter e Android (mobile)"
if [ -f mobile/pubspec.yaml ]; then
  pinned=$(awk '/^[[:space:]]*flutter:[[:space:]]*[0-9]/ {gsub(/[^0-9.]/, "", $2); print $2; exit}' mobile/pubspec.yaml)
  [ -n "$pinned" ] && FLUTTER_MIN="$pinned"
fi
if has flutter; then
  fv=$(flutter --version 2>/dev/null | tr -d '\r' | awk '/^Flutter/ {print $2; exit}')
  if [ -n "$fv" ] && version_ge "$fv" "$FLUTTER_MIN"; then
    ok "Flutter $fv"
  else
    need "$NEED_MOBILE" "Flutter ${fv:-?}; o projecto precisa de ≥ $FLUTTER_MIN" "mobile/"
    hint "flutter upgrade  (ou flutter version $FLUTTER_MIN se a versão estiver fixada)"
  fi
else
  need "$NEED_MOBILE" "Flutter não encontrado" "mobile/"
  hint "https://docs.flutter.dev/get-started/install"
fi
sdk="${ANDROID_HOME:-${ANDROID_SDK_ROOT:-}}"
if [ -z "$sdk" ]; then
  case "$OS" in
    windows) [ -n "${LOCALAPPDATA:-}" ] && sdk="$LOCALAPPDATA\\Android\\Sdk" ;;
    mac) sdk="$HOME/Library/Android/sdk" ;;
    linux) sdk="$HOME/Android/Sdk" ;;
  esac
fi
sdk_u="$sdk"; has cygpath && [ -n "$sdk" ] && sdk_u="$(cygpath -u "$sdk")"
if [ -n "$sdk" ] && [ -d "$sdk_u/platform-tools" ]; then
  ok "Android SDK em $sdk"
  if [ -d "$sdk_u/emulator" ]; then ok "emulador Android instalado"; else
    need "$NEED_MOBILE" "emulador Android não encontrado" "mobile/"
    hint "Android Studio → SDK Manager → SDK Tools → Android Emulator"
  fi
else
  need "$NEED_MOBILE" "Android SDK não encontrado" "mobile/"
  hint "instalar o Android Studio (https://developer.android.com/studio) e definir ANDROID_HOME"
fi
[ "$FULL" = 1 ] && has flutter && { info "flutter doctor:"; flutter doctor 2>&1 | sed 's/^/          /'; }

# ================================================================ Portas
section "Portas locais"
busy=""
for p in $PORTS; do port_in_use "$p" && busy="$busy $p"; done
if [ -z "$busy" ]; then
  ok "livres: $PORTS"
else
  warn "portas já em uso:$busy (o docker compose e o backend precisam delas)"
  if [ "$OS" = windows ]; then
    hint "ver quem usa: netstat -ano | findstr :<porta>  e depois: tasklist /FI \"PID eq <pid>\""
  else
    hint "ver quem usa: lsof -nP -iTCP:<porta> -sTCP:LISTEN"
  fi
  hint "se for um 'docker compose up' deste projecto, está tudo bem"
fi

# ================================================================ Teste completo
if [ "$FULL" = 1 ] && [ "$NEED_BACKEND" = 1 ]; then
  section "Testes do backend (./mvnw -B verify)"
  if [ "$DOCKER_OK" = 1 ] && [ -n "$JAVA_BIN" ]; then
    if (cd backend && ./mvnw -B -q verify > "${TMPDIR:-/tmp}/moseguros-verify.log" 2>&1); then
      ok "build e testes do backend passam (Docker + Testcontainers a funcionar)"
    else
      fail "os testes do backend falharam; últimas linhas de ${TMPDIR:-/tmp}/moseguros-verify.log:"
      tail -n 25 "${TMPDIR:-/tmp}/moseguros-verify.log" | sed 's/^/          /'
    fi
  else
    info "não corridos (falta o Docker ou o JDK)"
  fi
fi

# ================================================================ Resumo
printf '\n'
if [ "$FAILS" = 0 ]; then
  printf '%sTudo pronto.%s' "$C_OK" "$C_0"
  [ "$WARNS" -gt 0 ] && printf ' %s avisos (não impedem de trabalhar).' "$WARNS"
  printf '\n'
  [ "$FULL" = 0 ] && printf '%sPara provar que os testes do backend correm nesta máquina: ./scripts/doctor.sh --full%s\n' "$C_DIM" "$C_0"
  exit 0
else
  if [ "$FAILS" = 1 ]; then printf '%sFalta 1 coisa obrigatória%s' "$C_FAIL" "$C_0"
  else printf '%sFaltam %s coisas obrigatórias%s' "$C_FAIL" "$FAILS" "$C_0"; fi
  [ "$WARNS" -gt 0 ] && printf ' e há %s avisos' "$WARNS"
  printf '. Resolva as linhas [FALTA] e corra de novo.\n'
  exit 1
fi
