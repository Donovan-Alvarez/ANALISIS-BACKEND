# Cómo correr los scripts de Fase 2 (Planilla)

## Orden, siempre

```
01_fase2_ddl.sql   (DROP + CREATE de las 16 tablas de Planilla)
02_fase2_seed_catalogos.sql
03_fase2_seed_personas_empleados.sql
04_fase2_rbac.sql  (independiente de 01-03; MODULO/MENU/OPCION/ROLE_OPCION — solo con aprobación)
05_fase2_endurecimiento.sql  (UNIQUE sobre OPCION.Pagina + triggers de baja de los catálogos de Planilla)
06_fase2_departamento_empresa_obligatoria.sql  (DEPARTAMENTO.IdEmpresa NOT NULL; en una instalación nueva no hace nada)
99_fase2_verificacion.sql  (solo lectura, al final, para confirmar que todo quedó bien)
```

> **Nunca repitas `02` o `03` sin pasar primero por `01`.**
> `02` y `03` NO son idempotentes (`02_fase2_seed_catalogos.sql` y
> `03_fase2_seed_personas_empleados.sql` lo dicen en su propio
> encabezado): si los corres dos veces sobre las mismas 16 tablas sin
> recrearlas con `01`, vas a duplicar filas o romper una PK/UNIQUE.
> `03` tiene además una guarda (`RAISE_APPLICATION_ERROR(-20100, ...)`)
> que corta antes de insertar si `PERSONA` ya tiene filas — si te
> encuentras ese error, es justo esta regla avisándote: corre `01` de
> nuevo.
>
> `04_fase2_rbac.sql` sí es re-ejecutable por diseño (cada `INSERT` lleva
> `WHERE NOT EXISTS`), pero solo corrige MODULO/MENU/OPCION/ROLE_OPCION de
> Planilla — nunca lo corras sin que el responsable del paso lo haya
> aprobado explícitamente, porque toca una tabla compartida con Fase 1
> (`ROLE_OPCION`, `OPCION`, etc. son de todo el sistema, no solo de
> Planilla).
>
> `05_fase2_endurecimiento.sql` hace dos cosas: agrega el `UNIQUE` sobre
> `OPCION.Pagina` (`UQ_OPCION_PAGINA`, para que no vuelva a haber slugs
> duplicados) y crea los triggers `BEFORE DELETE`
> (`TRG_<TABLA>_BAJA_VALIDA`) sobre los catálogos de Planilla, que impiden
> dar de baja un registro que todavía está en uso y devuelven un mensaje
> claro para el usuario. No toca datos y es re-ejecutable.
>
> **Si repites `01`, tienes que repetir `05`** (después de `02`/`03`/`04`):
> el `DROP TABLE ... CASCADE CONSTRAINTS` de `01` también borra los
> triggers de baja.
>
> `06_fase2_departamento_empresa_obligatoria.sql` hace obligatoria
> `DEPARTAMENTO.IdEmpresa` (un departamento siempre pertenece a una
> empresa). Desde el Paso E, `01` ya crea la columna como `NOT NULL`, así
> que **en una instalación nueva `06` no hace nada** (avisa "ya es NOT
> NULL: sin cambios"); solo cambia algo en una BD creada antes. Es
> re-ejecutable. Si encuentra departamentos sin empresa, aborta con
> `ORA-20112` sin tocar la tabla: asígnales una empresa y vuelve a correrlo.

## Cómo ejecutarlos: `run-sql.sh`

`sqlplus` no está instalado en tu Mac — vive dentro del contenedor
Docker de Oracle. `database/run-sql.sh` hace el `docker exec` por ti, sin
que tengas que escribir ni ver la contraseña en ningún momento.

```bash
# desde la raíz del repo (erp/)
./database/run-sql.sh database/01_fase2_ddl.sql
./database/run-sql.sh database/02_fase2_seed_catalogos.sql
./database/run-sql.sh database/03_fase2_seed_personas_empleados.sql
./database/run-sql.sh database/05_fase2_endurecimiento.sql
./database/run-sql.sh database/06_fase2_departamento_empresa_obligatoria.sql
./database/run-sql.sh database/99_fase2_verificacion.sql

# si tu contenedor no se llama "oracle-seguridad":
CONTAINER=mi-contenedor ./database/run-sql.sh database/01_fase2_ddl.sql
```

Qué hace por debajo (para que sepas qué esperar, no por desconfianza):
- Lee `DB_USERNAME`/`DB_PASSWORD` de `.env` (en la raíz del repo), los
  pasa al contenedor como variables de entorno (`docker exec -e ...`) —
  nunca aparecen como texto en la terminal ni en ningún log.
- Corre `sqlplus -s` (modo silencioso) con, antes de tu script:
  `WHENEVER SQLERROR EXIT SQL.SQLCODE ROLLBACK` (se detiene en el primer
  error y revierte lo no comiteado), `SET DEFINE OFF` (para que un `&`
  suelto no se trate como variable) y `NLS_LANG=AMERICAN_AMERICA.AL32UTF8`
  (para que los acentos no se corrompan).
- El código de salida del script de shell es el mismo que el de
  `sqlplus`: `0` si todo el archivo corrió sin error SQL; distinto de 0
  si `WHENEVER SQLERROR` cortó la ejecución.

Si quieres ver exactamente qué contó cada tabla después de correr algo,
`99_fase2_verificacion.sql` es de solo lectura — puedes correrlo las
veces que quieras sin riesgo.

## Si usas DBeaver en vez de `run-sql.sh`

**Antes de ejecutar nada:**
1. Revisa en Preferencias si DBeaver tiene activada alguna sustitución de
   variables/parámetros en el editor SQL (en distintas versiones aparece
   como "SQL parameters", "Variable binding" o similar) y **desactívala**.
   `SET DEFINE OFF` (que ya traen estos scripts) es una instrucción de
   SQL*Plus, no de DBeaver — DBeaver no la entiende, así que si tiene su
   propio mecanismo de sustitución activado, un `&` en el texto (ya no
   debería quedar ninguno después del Paso A6, pero por si acaso) puede
   seguir rompiendo el script aunque el archivo tenga `SET DEFINE OFF`.
2. Ejecuta el archivo **completo**, no sentencia por sentencia: abre el
   `.sql`, y usa **"Execute SQL Script"** (`Alt+X` en Windows/Linux,
   `⌥+X` en Mac) — no `Ctrl+Enter`/`Execute SQL Statement`, que solo
   corre la sentencia donde está el cursor y te puede hacer perder el
   orden o saltarte el bloque PL/SQL de la guarda de `03`.

Si con eso igual te aparece un error, copia el mensaje completo (el
texto de Oracle, no solo el ícono rojo) y el nombre exacto de la opción
de sustitución de variables en tu versión de DBeaver — así se puede
seguir afinando el diagnóstico.
