# Atalho para Windows: corre scripts/doctor.sh no Git Bash a partir do PowerShell.
#
# Uso (na raiz do repositório):
#   powershell -ExecutionPolicy Bypass -File scripts\doctor.ps1          # verificar
#   powershell -ExecutionPolicy Bypass -File scripts\doctor.ps1 -Fix     # e descarregar as imagens em falta
#   powershell -ExecutionPolicy Bypass -File scripts\doctor.ps1 -Full    # e correr os testes do backend
#
# Toda a lógica está no doctor.sh; este ficheiro só encontra o bash do Git for Windows. Por isso as
# verificações do Maven Wrapper da raiz e do backend, do payment-contracts instalado e o -Full com o
# comando da raiz (./mvnw -B -pl backend -am verify, S5-06) vêm do doctor.sh, sem nada a repetir aqui.
# Não usa o bash.exe do System32, que é o do WSL (outro sistema, com outro Docker e outro Java).

param(
    [switch]$Fix,
    [switch]$Full
)

$ErrorActionPreference = 'Stop'

$git = Get-Command git -ErrorAction SilentlyContinue
if (-not $git) {
    Write-Host "[FALTA] git não encontrado. Instale com: winget install --id Git.Git -e" -ForegroundColor Red
    exit 1
}

# git.exe fica em <Git>\cmd\ ou <Git>\bin\ ou <Git>\mingw64\bin\; o bash fica em <Git>\bin\bash.exe
$dir = Split-Path $git.Source
$candidates = @(
    (Join-Path $dir '..\bin\bash.exe'),
    (Join-Path $dir 'bash.exe'),
    (Join-Path $dir '..\..\bin\bash.exe'),
    $(if ($env:ProgramFiles) { Join-Path $env:ProgramFiles 'Git\bin\bash.exe' })
) | Where-Object { $_ }
$bash = $candidates | Where-Object { Test-Path $_ } | Select-Object -First 1
if (-not $bash) {
    Write-Host "[FALTA] não encontrei o Git Bash (bash.exe do Git for Windows)." -ForegroundColor Red
    Write-Host "        Reinstale o Git com: winget install --id Git.Git -e"
    exit 1
}

$script = (Join-Path $PSScriptRoot 'doctor.sh') -replace '\\', '/'   # o bash do Git aceita C:/caminho/com/barras
$bashArgs = @($script)
if ($Fix)  { $bashArgs += '--fix' }
if ($Full) { $bashArgs += '--full' }

& (Resolve-Path $bash) @bashArgs
exit $LASTEXITCODE
