/*
 * FASE 2 - Modulo de Planilla
 * 04b_limpieza_duplicado_generos.sql
 *
 * Elimina el DUPLICADO de OPCION.Pagina = 'generos' que vive en el modulo
 * ajeno 'Contabilidad':
 *
 *   OPCION 61 'Nomenclatura contable'  IdMenu=81 ('Parametros', modulo 41)
 *       Pagina = 'generos'   <- duplicado de la OPCION 3 'Generos' (Seguridad)
 *   ROLE_OPCION (IdRole=41 'root', IdOpcion=61)  <- su unico permiso
 *
 * Por que importa: PermisoAccionInterceptor resuelve el slug de la URL con
 * OpcionRepository.findIdOpcionPorPagina(), que hace
 *   SELECT IdOpcion FROM OPCION WHERE Pagina = ?   (sin ORDER BY) + findFirst()
 * Con 2 filas 'generos', Oracle puede devolver la 61 primero y entonces el
 * POST/PUT/DELETE de /api/generos se valida contra un permiso que el rol
 * Administrador no tiene (403), de forma no deterministica.
 *
 * Origen (Paso A7, solo lectura): las filas de 'Contabilidad' las creo la
 * aplicacion, con el usuario 'Administrador' (UsuarioCreacion), el
 * 2026-09-02/03. No vienen de ningun script de este repositorio.
 *
 * ALCANCE MINIMO (lo unico que se ejecuta):
 *   1. DELETE del ROLE_OPCION de la opcion 61
 *   2. DELETE de la OPCION 61
 * Ambos filtran por IdOpcion = 61 Y Pagina = 'generos' Y IdMenu = 81
 * (doble filtro: la OPCION 3 'Generos' tiene Pagina = 'generos' pero
 * IdMenu = 1 e IdOpcion = 3, asi que es imposible que coincida).
 *
 * Orden obligatorio ROLE_OPCION -> OPCION: TRG_OPCION_BAJA_VALIDA lanza
 * ORA-20016 si la opcion todavia tiene permisos en ROLE_OPCION.
 *
 * Idempotente: si ya se corrio, los dos WHERE no encuentran la fila y cada
 * DELETE afecta 0 filas (no es error). El SELECT de conteo dara 0 y 0.
 *
 * OPCIONALES (al final, COMENTADOS): borrar MENU 81, MODULO 41 y ROLE 41.
 * No se ejecutan salvo que alguien los descomente a proposito.
 *
 * NO EJECUTAR sin aprobacion explicita (Paso A7).
 * Uso: ./database/run-sql.sh database/04b_limpieza_duplicado_generos.sql
 */


-- =====================================================================
-- SELECT previo: CORRE ESTO PRIMERO.
-- Esperado (verificado en el Paso A7): ROLE_OPCION = 1, OPCION = 1.
-- Si da 0 y 0, ya se habia limpiado (los DELETE no haran nada).
-- Si da cualquier otra cosa, DETENTE y revisa antes de seguir.
-- =====================================================================
SELECT 'ROLE_OPCION (opcion 61 generos/menu 81)' AS Tabla, COUNT(*) AS FilasABorrar
FROM ROLE_OPCION
WHERE IdOpcion IN (
    SELECT IdOpcion FROM OPCION
    WHERE IdOpcion = 61 AND Pagina = 'generos' AND IdMenu = 81
)
UNION ALL
SELECT 'OPCION (61 generos/menu 81)', COUNT(*)
FROM OPCION
WHERE IdOpcion = 61 AND Pagina = 'generos' AND IdMenu = 81;


-- =====================================================================
-- DELETE 1: ROLE_OPCION de la opcion 61 (primero - depende de OPCION)
-- =====================================================================
DELETE FROM ROLE_OPCION
WHERE IdOpcion IN (
    SELECT IdOpcion FROM OPCION
    WHERE IdOpcion = 61 AND Pagina = 'generos' AND IdMenu = 81
);

-- =====================================================================
-- DELETE 2: OPCION 61
-- =====================================================================
DELETE FROM OPCION
WHERE IdOpcion = 61 AND Pagina = 'generos' AND IdMenu = 81;

COMMIT;


-- =====================================================================
-- Verificacion (solo lectura) despues del COMMIT:
--   - debe quedar UNA sola OPCION con Pagina = 'generos': la 3, menu 1
--   - el permiso de Administrador sobre la opcion 3 sigue intacto
-- =====================================================================
SELECT IdOpcion, IdMenu, Nombre, Pagina
FROM OPCION
WHERE Pagina = 'generos';

SELECT ro.IdRole, r.Nombre, ro.IdOpcion
FROM ROLE_OPCION ro
JOIN "ROLE" r ON r.IdRole = ro.IdRole
WHERE ro.IdOpcion = 3;


/* =====================================================================
 * OPCIONALES - COMENTADOS A PROPOSITO. Cada bloque es independiente;
 * descomenta solo el que se haya aprobado, en este orden (hijo -> padre).
 * Todos llevan NOT EXISTS para que, si algo todavia depende de la fila,
 * el DELETE afecte 0 filas en vez de chocar con el trigger *_BAJA_VALIDA.
 * =====================================================================
 */

-- ---------------------------------------------------------------------
-- OPCIONAL A: MENU 81 'Parametros' (modulo 41 'Contabilidad').
-- Tras el DELETE 2 queda sin opciones (era su unica opcion).
-- TRG_MENU_BAJA_VALIDA (ORA-20014) lo impediria si aun tuviera opciones.
-- ---------------------------------------------------------------------
-- DELETE FROM MENU
-- WHERE IdMenu = 81 AND IdModulo = 41 AND Nombre = 'Parametros'
--   AND NOT EXISTS (SELECT 1 FROM OPCION WHERE IdMenu = 81);
-- COMMIT;

-- ---------------------------------------------------------------------
-- OPCIONAL B: MODULO 41 'Contabilidad'. Requiere el OPCIONAL A antes
-- (MENU 81 era su unico menu). TRG_MODULO_BAJA_VALIDA (ORA-20013).
-- ---------------------------------------------------------------------
-- DELETE FROM MODULO
-- WHERE IdModulo = 41 AND Nombre = 'Contabilidad'
--   AND NOT EXISTS (SELECT 1 FROM MENU WHERE IdModulo = 41);
-- COMMIT;

-- ---------------------------------------------------------------------
-- OPCIONAL C: "ROLE" 41 'root'. SOLO si ningun USUARIO lo usa.
-- HOY (Paso A7) EL USUARIO 'root' TIENE IdRole = 41: este DELETE
-- afectaria 0 filas por el NOT EXISTS (sin el, TRG_ROLE_BAJA_VALIDA
-- lanzaria ORA-20010). Borrar o reasignar al usuario 'root' queda FUERA
-- del alcance de este archivo. El NOT EXISTS sobre ROLE_OPCION cubre
-- ORA-20011 (tras el DELETE 1, el rol 41 queda con 0 permisos).
-- ---------------------------------------------------------------------
-- DELETE FROM "ROLE"
-- WHERE IdRole = 41 AND Nombre = 'root'
--   AND NOT EXISTS (SELECT 1 FROM USUARIO WHERE IdRole = 41)
--   AND NOT EXISTS (SELECT 1 FROM ROLE_OPCION WHERE IdRole = 41);
-- COMMIT;
