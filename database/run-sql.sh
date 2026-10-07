#!/usr/bin/env bash
#
# FASE 2 - Modulo de Planilla
# database/run-sql.sh
#
# Corre un archivo .sql contra el Oracle del contenedor Docker, usando
# sqlplus DENTRO del contenedor (sqlplus no esta instalado en el host).
# Pensado para los scripts de database/ (01_fase2_ddl.sql,
# 02_fase2_seed_catalogos.sql, etc.), pero sirve para cualquier .sql.
#
# USO:
#   ./database/run-sql.sh database/01_fase2_ddl.sql
#   CONTAINER=otro-contenedor ./database/run-sql.sh database/02_fase2_seed_catalogos.sql
#
# Variables de entorno:
#   CONTAINER   Nombre del contenedor Docker con Oracle. Por defecto
#               "oracle-seguridad".
#
# Credenciales: se leen de .env (DB_USERNAME, DB_PASSWORD) en la raiz del
# proyecto (un nivel arriba de database/). NUNCA se imprimen en pantalla:
# se reenvian al contenedor con "docker exec -e VAR" (toma el valor del
# entorno de ESTA shell, no aparece como texto en el comando ni en el
# log del contenedor) y sqlplus se invoca en modo silencioso (-s).
#
# Seguridad de la sesion SQL*Plus:
#   WHENEVER SQLERROR EXIT SQL.SQLCODE ROLLBACK   - se detiene en el
#       primer error y revierte lo que no se haya comiteado.
#   SET DEFINE OFF                                - por si queda algun
#       "&" en el script (ya no deberia, ver Paso A6), que no se trate
#       como variable de sustitucion.
#   NLS_LANG=AMERICAN_AMERICA.AL32UTF8             - para que los acentos
#       (nombres, direcciones) no se corrompan al insertar/leer.
#
# Codigo de salida: igual al de sqlplus (0 = sin errores SQL; el codigo
# ORA de un error, p. ej. 942, si WHENEVER SQLERROR corto la ejecucion).

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
ENV_FILE="$PROJECT_ROOT/.env"

CONTAINER="${CONTAINER:-oracle-seguridad}"

if [ "$#" -ne 1 ]; then
    echo "Uso: $0 <archivo.sql>" >&2
    echo "Ejemplo: $0 database/01_fase2_ddl.sql" >&2
    exit 2
fi

SQL_FILE="$1"

if [ ! -f "$SQL_FILE" ]; then
    echo "ERROR: no existe el archivo '$SQL_FILE'" >&2
    exit 2
fi

if [ ! -f "$ENV_FILE" ]; then
    echo "ERROR: no se encontro $ENV_FILE (se esperaba junto al proyecto, no dentro de database/)" >&2
    exit 2
fi

if ! docker ps --format '{{.Names}}' | grep -qx "$CONTAINER"; then
    echo "ERROR: el contenedor '$CONTAINER' no esta corriendo (docker ps no lo lista)." >&2
    echo "Si tu contenedor se llama distinto, corre: CONTAINER=tu-contenedor $0 $SQL_FILE" >&2
    exit 2
fi

# Carga DB_USERNAME/DB_PASSWORD SOLO en esta shell (no se exportan mas
# alla de este proceso, y nunca se imprimen).
set -a
# shellcheck disable=SC1090
source "$ENV_FILE"
set +a

if [ -z "${DB_USERNAME:-}" ] || [ -z "${DB_PASSWORD:-}" ]; then
    echo "ERROR: DB_USERNAME y/o DB_PASSWORD no estan definidos en $ENV_FILE" >&2
    exit 2
fi

echo ">> Ejecutando $SQL_FILE contra el contenedor '$CONTAINER' (PDB FREEPDB1)..." >&2

{
    printf 'WHENEVER SQLERROR EXIT SQL.SQLCODE ROLLBACK\n'
    printf 'SET DEFINE OFF\n'
    printf 'SET ECHO OFF\n'
    printf 'SET PAGESIZE 200\n'
    printf 'SET LINESIZE 300\n'
    cat "$SQL_FILE"
} | docker exec -i -e DB_USERNAME -e DB_PASSWORD -e NLS_LANG=AMERICAN_AMERICA.AL32UTF8 \
    "$CONTAINER" bash -c 'sqlplus -s "$DB_USERNAME/$DB_PASSWORD@localhost:1521/FREEPDB1"'

EXIT_CODE=$?

if [ "$EXIT_CODE" -eq 0 ]; then
    echo ">> OK: $SQL_FILE termino sin errores SQL (exit code 0)." >&2
else
    echo ">> DETENIDO: $SQL_FILE termino con exit code $EXIT_CODE (WHENEVER SQLERROR corto la ejecucion)." >&2
fi

exit "$EXIT_CODE"
