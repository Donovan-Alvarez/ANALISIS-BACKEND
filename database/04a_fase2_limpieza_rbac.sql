/*
 * FASE 2 - Modulo de Planilla
 * 04a_fase2_limpieza_rbac.sql
 *
 * Limpieza ACOTADA, SOLO del modulo 'Planilla' (MODULO, MENU, OPCION,
 * ROLE_OPCION) - herramienta de reset por si en el futuro hace falta
 * volver a sembrar el RBAC de Planilla desde cero.
 *
 * NO HACE FALTA para que 04_fase2_rbac.sql corra hoy: el diagnostico del
 * Paso A6 encontro el modulo 'Planilla' (IdModulo=42, con sus 4 MENU y 16
 * OPCION, y los 16 ROLE_OPCION del rol Administrador) ya completo y
 * correcto en la BD - probablemente de una corrida anterior de 04. Como
 * 04 ya tiene WHERE NOT EXISTS en cada INSERT, re-ejecutarlo hoy no
 * inserta nada nuevo (es un no-op seguro) - ver
 * docs/fase2/informes/paso-A6-rbac.md, Parte 1, punto 5.
 *
 * Este archivo sirve para cuando sea necesario REINICIAR ese RBAC (por
 * ejemplo, para probar otra vez los 16 slugs desde cero, o si en algun
 * momento 04 deja de ser un no-op porque algo cambio). Alcance de los
 * DELETE: exclusivamente filas que cuelgan de MODULO.Nombre = 'Planilla'
 * (siempre por JOIN/subconsulta sobre ese nombre, nunca por un numero de
 * id fijo como 42).
 *
 * NUNCA toca:
 *   - El modulo 'Seguridad' (IdModulo=1) ni nada que cuelgue de el -
 *     incluye el MENU 'Menu Invalido' y el MENU 'Reportes' duplicado
 *     (IdMenu 21/22) que aparecieron en el diagnostico del Paso A6:
 *     estan DENTRO de Seguridad, y Seguridad esta prohibido tocarlo en
 *     este paso, sin importar que esas 2 filas parezcan basura de prueba.
 *   - El modulo 'Contabilidad' (IdModulo=41, con su MENU 'Parametros' y
 *     su OPCION 'Nomenclatura contable') - no es de Fase 1 ni de
 *     Planilla, no esta en el alcance autorizado de este archivo.
 *   - La tabla LIBROS.
 *   - El ROLE_OPCION de 'Sin Opciones' sobre Seguridad (2 filas) ni el de
 *     'root' sobre Contabilidad (1 fila) - ninguna de las dos es de
 *     Planilla.
 *
 * Orden de los DELETE (hijo -> padre, respeta las FK):
 *   ROLE_OPCION -> OPCION -> MENU -> MODULO
 *
 * Se puede correr una sola vez (borra las filas de Planilla) o mas de
 * una vez sin romper nada: la segunda vez los WHERE no encuentran nada
 * que coincida (MODULO.Nombre = 'Planilla' ya no existe), así que cada
 * DELETE afecta 0 filas - no es un error, simplemente no hace nada.
 *
 * NO EJECUTAR sin tu aprobacion explicita (Parte 4 del Paso A6).
 */


-- =====================================================================
-- SELECT previo: cuantas filas se borrarian por tabla. CORRE ESTO
-- PRIMERO y revisa los numeros antes de aprobar el DELETE de abajo.
--
-- Conteo real verificado en el Paso A6 (solo lectura, antes de escribir
-- este archivo): ROLE_OPCION=16, OPCION=16, MENU=4, MODULO=1.
-- =====================================================================
SELECT 'ROLE_OPCION (de Planilla)' AS Tabla, COUNT(*) AS FilasABorrar
FROM ROLE_OPCION ro
JOIN OPCION op ON op.IdOpcion = ro.IdOpcion
JOIN MENU me ON me.IdMenu = op.IdMenu
JOIN MODULO mo ON mo.IdModulo = me.IdModulo
WHERE mo.Nombre = 'Planilla'
UNION ALL
SELECT 'OPCION (de Planilla)', COUNT(*)
FROM OPCION op
JOIN MENU me ON me.IdMenu = op.IdMenu
JOIN MODULO mo ON mo.IdModulo = me.IdModulo
WHERE mo.Nombre = 'Planilla'
UNION ALL
SELECT 'MENU (de Planilla)', COUNT(*)
FROM MENU me
JOIN MODULO mo ON mo.IdModulo = me.IdModulo
WHERE mo.Nombre = 'Planilla'
UNION ALL
SELECT 'MODULO (Planilla)', COUNT(*)
FROM MODULO
WHERE Nombre = 'Planilla';


-- =====================================================================
-- DELETE: ROLE_OPCION de Planilla (primero - depende de OPCION)
-- =====================================================================
DELETE FROM ROLE_OPCION
WHERE IdOpcion IN (
    SELECT op.IdOpcion
    FROM OPCION op
    JOIN MENU me ON me.IdMenu = op.IdMenu
    JOIN MODULO mo ON mo.IdModulo = me.IdModulo
    WHERE mo.Nombre = 'Planilla'
);

-- =====================================================================
-- DELETE: OPCION de Planilla (depende de MENU)
-- =====================================================================
DELETE FROM OPCION
WHERE IdMenu IN (
    SELECT me.IdMenu
    FROM MENU me
    JOIN MODULO mo ON mo.IdModulo = me.IdModulo
    WHERE mo.Nombre = 'Planilla'
);

-- =====================================================================
-- DELETE: MENU de Planilla (depende de MODULO)
-- =====================================================================
DELETE FROM MENU
WHERE IdModulo IN (
    SELECT IdModulo FROM MODULO WHERE Nombre = 'Planilla'
);

-- =====================================================================
-- DELETE: MODULO 'Planilla' (ultimo - ya nada depende de el)
-- =====================================================================
DELETE FROM MODULO
WHERE Nombre = 'Planilla';

COMMIT;


-- =====================================================================
-- Verificacion sugerida DESPUES de correr esto (las 4 deben dar 0):
-- =====================================================================
-- SELECT COUNT(*) FROM MODULO WHERE Nombre = 'Planilla';
-- SELECT COUNT(*) FROM MENU me JOIN MODULO mo ON mo.IdModulo = me.IdModulo WHERE mo.Nombre = 'Planilla';
-- SELECT COUNT(*) FROM OPCION op JOIN MENU me ON me.IdMenu = op.IdMenu JOIN MODULO mo ON mo.IdModulo = me.IdModulo WHERE mo.Nombre = 'Planilla';
-- SELECT COUNT(*) FROM ROLE_OPCION ro JOIN OPCION op ON op.IdOpcion = ro.IdOpcion JOIN MENU me ON me.IdMenu = op.IdMenu JOIN MODULO mo ON mo.IdModulo = me.IdModulo WHERE mo.Nombre = 'Planilla';
--
-- Y que Seguridad y Contabilidad sigan intactos (deben dar los mismos
-- numeros de ANTES de correr este archivo):
-- SELECT COUNT(*) FROM MODULO WHERE Nombre IN ('Seguridad', 'Contabilidad');
