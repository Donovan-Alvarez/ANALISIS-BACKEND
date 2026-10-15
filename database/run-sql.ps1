param(
    [Parameter(Mandatory = $true)][string]$SqlFile,
    [string]$Container = 'oracle-seguridad'
)
$ErrorActionPreference = 'Stop'
$projectRoot = Split-Path $PSScriptRoot -Parent
$envFile = Join-Path $projectRoot '.env'
if (!(Test-Path -LiteralPath $envFile)) { throw 'Falta .env en la raíz del backend.' }
$sqlPath = (Resolve-Path -LiteralPath $SqlFile).Path
$config = @{}
Get-Content -LiteralPath $envFile | ForEach-Object {
    if ($_ -match '^([A-Z_]+)=(.*)$') { $config[$Matches[1]] = $Matches[2] }
}
if (!$config.DB_USERNAME -or !$config.DB_PASSWORD) { throw 'Faltan DB_USERNAME o DB_PASSWORD.' }
$previousUser = $env:DB_USERNAME
$previousPassword = $env:DB_PASSWORD
$previousEncoding = $OutputEncoding
try {
    $env:DB_USERNAME = $config.DB_USERNAME
    $env:DB_PASSWORD = $config.DB_PASSWORD
    $OutputEncoding = [System.Text.UTF8Encoding]::new($false)
    $sql = "WHENEVER OSERROR EXIT FAILURE ROLLBACK`nWHENEVER SQLERROR EXIT SQL.SQLCODE ROLLBACK`nSET DEFINE OFF`nSET ECHO OFF`nSET PAGESIZE 200`nSET LINESIZE 300`n"
    $sql += [IO.File]::ReadAllText($sqlPath) + "`nCOMMIT;`nEXIT;`n"
    $sql | docker exec -i -e DB_USERNAME -e DB_PASSWORD -e NLS_LANG=AMERICAN_AMERICA.AL32UTF8 $Container bash -c 'sqlplus -s "$DB_USERNAME/$DB_PASSWORD@localhost:1521/FREEPDB1"'
    if ($LASTEXITCODE -ne 0) { throw "SQL falló (código $LASTEXITCODE). Revise el mensaje de Oracle antes de continuar." }
} finally {
    $env:DB_USERNAME = $previousUser
    $env:DB_PASSWORD = $previousPassword
    $OutputEncoding = $previousEncoding
}
