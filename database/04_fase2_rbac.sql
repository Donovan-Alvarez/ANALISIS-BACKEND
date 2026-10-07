/*
 * FASE 2 - Modulo de Planilla
 * 04_fase2_rbac.sql
 *
 * Alta del modulo 'Planilla' en el esquema de seguridad de Fase 1
 * (MODULO, MENU, OPCION, ROLE_OPCION). NO crea tablas nuevas: inserta en
 * tablas que YA EXISTEN y que 01/02/03 no tocan.
 *
 * OJO con la tabla "ROLE": ROLE es palabra reservada/ambigua en Oracle, por
 * lo que el codigo Java que ya existe (RoleRepository.java) la referencia
 * SIEMPRE entre comillas dobles, p. ej. FROM "ROLE". El INSERT de
 * ROLE_OPCION de abajo hace lo mismo (FROM "ROLE" r). ROLE_OPCION, en
 * cambio, es un identificador distinto y NO necesita comillas.
 *
 * Por que con subconsultas y no con numeros fijos:
 * MODULO/MENU/OPCION son NUMBER GENERATED ALWAYS AS IDENTITY (ver
 * database/01-schema-completo.sql). Eso impide insertar un IdOpcion
 * explicito (ORA-32795: cannot insert into a generated always identity
 * column), y aunque se pudiera, los ids reales dependen de cuantas filas
 * existan HOY en esas tablas (Fase 1 ya tiene 1 MODULO, 4 MENU, 10 OPCION).
 * Por eso cada INSERT de abajo resuelve el id del padre con una
 * subconsulta por Nombre, nunca con un numero escrito a mano.
 *
 * PermisoAccionInterceptor (ver REPORTE_FASE2_PASOA_TRADUCCION_SQL.md,
 * Parte 1.2) resuelve el idOpcion de cada peticion POST/PUT/DELETE
 * buscando OPCION.Pagina = primer segmento de la URL (sin /api/). Por eso
 * aqui OPCION.Pagina NO usa ".php" (eso era de Fase 1/catedratico en
 * MySQL): usa el slug kebab-case que el router de Angular va a usar como
 * path, siguiendo la MISMA convencion que las 10 opciones de Fase 1
 * (generos, status-usuario, asignacion-permisos, etc. - ver las UPDATE
 * OPCION al final de 01-schema-completo.sql).
 *
 * ================================================================
 * PENDIENTE DE APROBACION - slugs de OPCION.Pagina (= rutas de Angular)
 * ================================================================
 * Estos 16 valores son una PROPUESTA: en el momento en que se construya
 * el frontend, app.routes.ts debera usar EXACTAMENTE estos mismos paths
 * (y sidebar.ts su ICONOS_POR_PAGINA). Si se cambia un slug aqui despues
 * de que el frontend ya este escrito, hay que cambiarlo en los dos
 * lugares a la vez.
 *
 *   .php original              -> Pagina propuesta (Angular)
 *   estado_civil.php            -> estados-civiles
 *   status_empleado.php         -> status-empleado
 *   flujo_status_empleado.php   -> flujo-status-empleado
 *   tipos_documento.php         -> tipos-documento
 *   departamento.php            -> departamentos
 *   puesto.php                  -> puestos
 *   personas.php                -> personas
 *   documento_persona.php       -> documentos-persona
 *   banco.php                   -> bancos
 *   empleado.php                 -> empleados
 *   cuenta_bancaria_empleado.php -> cuentas-bancarias
 *   inasistencia.php             -> inasistencias
 *   calculo_planilla.php        -> calculo-planilla
 *   reporte_planilla.php        -> reporte-planilla
 *   boleta_pago.php              -> boletas-pago
 *   liquidacion.php              -> liquidacion
 * ================================================================
 *
 * PENDIENTE DE APROBACION - orden del MENU 'Liquidacion'
 * El original (Planilla-SQL.sql linea ~841) inserta 'Reportes' con
 * OrdenMenu = 3 Y 'Liquidacion' tambien con OrdenMenu = 3 (duplicado).
 * Aqui se propone 'Liquidacion' con OrdenMenu = 4.
 *
 * Idempotencia: a diferencia de 01 (que dropea sus propias tablas), este
 * archivo inserta en tablas COMPARTIDAS de Fase 1 que no se van a dropear.
 * Cada INSERT lleva un WHERE NOT EXISTS para poder re-ejecutar el archivo
 * sin duplicar filas si algo falla a medio camino.
 *
 * NO EJECUTAR sin autorizacion. Este archivo es un borrador.
 */

-- =====================================================================
-- MODULO 'Planilla'
-- =====================================================================
INSERT INTO MODULO (Nombre, OrdenMenu, FechaCreacion, UsuarioCreacion)
SELECT 'Planilla', (SELECT MAX(OrdenMenu) + 1 FROM MODULO), SYSTIMESTAMP, 'system'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM MODULO WHERE Nombre = 'Planilla');


-- =====================================================================
-- MENU: 4 filas bajo el modulo 'Planilla'
-- OrdenMenu: Parametros Generales=1, Gestionar=2, Reportes=3,
-- Liquidacion=4 (corregido; el original repetia 3 con Reportes).
-- =====================================================================
INSERT INTO MENU (IdModulo, Nombre, OrdenMenu, FechaCreacion, UsuarioCreacion)
SELECT (SELECT IdModulo FROM MODULO WHERE Nombre = 'Planilla'), 'Parametros Generales', 1, SYSTIMESTAMP, 'system' FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM MENU WHERE Nombre = 'Parametros Generales'
      AND IdModulo = (SELECT IdModulo FROM MODULO WHERE Nombre = 'Planilla')
);

INSERT INTO MENU (IdModulo, Nombre, OrdenMenu, FechaCreacion, UsuarioCreacion)
SELECT (SELECT IdModulo FROM MODULO WHERE Nombre = 'Planilla'), 'Gestionar', 2, SYSTIMESTAMP, 'system' FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM MENU WHERE Nombre = 'Gestionar'
      AND IdModulo = (SELECT IdModulo FROM MODULO WHERE Nombre = 'Planilla')
);

INSERT INTO MENU (IdModulo, Nombre, OrdenMenu, FechaCreacion, UsuarioCreacion)
SELECT (SELECT IdModulo FROM MODULO WHERE Nombre = 'Planilla'), 'Reportes', 3, SYSTIMESTAMP, 'system' FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM MENU WHERE Nombre = 'Reportes'
      AND IdModulo = (SELECT IdModulo FROM MODULO WHERE Nombre = 'Planilla')
);

INSERT INTO MENU (IdModulo, Nombre, OrdenMenu, FechaCreacion, UsuarioCreacion)
SELECT (SELECT IdModulo FROM MODULO WHERE Nombre = 'Planilla'), 'Liquidacion', 4, SYSTIMESTAMP, 'system' FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM MENU WHERE Nombre = 'Liquidacion'
      AND IdModulo = (SELECT IdModulo FROM MODULO WHERE Nombre = 'Planilla')
);


-- =====================================================================
-- OPCION: 16 filas. IdMenu se resuelve por Nombre del MENU + IdModulo
-- 'Planilla' (no por numero fijo). Pagina = slug propuesto (ver tabla
-- arriba), NO .php.
-- =====================================================================

-- --- Menu "Parametros Generales" (9 opciones) ---------------------------
INSERT INTO OPCION (IdMenu, Nombre, OrdenMenu, Pagina, FechaCreacion, UsuarioCreacion)
SELECT me.IdMenu, 'Estados Civiles', 1, 'estados-civiles', SYSTIMESTAMP, 'system'
FROM MENU me WHERE me.Nombre = 'Parametros Generales' AND me.IdModulo = (SELECT IdModulo FROM MODULO WHERE Nombre = 'Planilla')
AND NOT EXISTS (SELECT 1 FROM OPCION WHERE Pagina = 'estados-civiles');

INSERT INTO OPCION (IdMenu, Nombre, OrdenMenu, Pagina, FechaCreacion, UsuarioCreacion)
SELECT me.IdMenu, 'Status Empleado', 2, 'status-empleado', SYSTIMESTAMP, 'system'
FROM MENU me WHERE me.Nombre = 'Parametros Generales' AND me.IdModulo = (SELECT IdModulo FROM MODULO WHERE Nombre = 'Planilla')
AND NOT EXISTS (SELECT 1 FROM OPCION WHERE Pagina = 'status-empleado');

INSERT INTO OPCION (IdMenu, Nombre, OrdenMenu, Pagina, FechaCreacion, UsuarioCreacion)
SELECT me.IdMenu, 'Flujos Status Empleado', 3, 'flujo-status-empleado', SYSTIMESTAMP, 'system'
FROM MENU me WHERE me.Nombre = 'Parametros Generales' AND me.IdModulo = (SELECT IdModulo FROM MODULO WHERE Nombre = 'Planilla')
AND NOT EXISTS (SELECT 1 FROM OPCION WHERE Pagina = 'flujo-status-empleado');

INSERT INTO OPCION (IdMenu, Nombre, OrdenMenu, Pagina, FechaCreacion, UsuarioCreacion)
SELECT me.IdMenu, 'Tipos de Documentos', 4, 'tipos-documento', SYSTIMESTAMP, 'system'
FROM MENU me WHERE me.Nombre = 'Parametros Generales' AND me.IdModulo = (SELECT IdModulo FROM MODULO WHERE Nombre = 'Planilla')
AND NOT EXISTS (SELECT 1 FROM OPCION WHERE Pagina = 'tipos-documento');

INSERT INTO OPCION (IdMenu, Nombre, OrdenMenu, Pagina, FechaCreacion, UsuarioCreacion)
SELECT me.IdMenu, 'Departamentos', 5, 'departamentos', SYSTIMESTAMP, 'system'
FROM MENU me WHERE me.Nombre = 'Parametros Generales' AND me.IdModulo = (SELECT IdModulo FROM MODULO WHERE Nombre = 'Planilla')
AND NOT EXISTS (SELECT 1 FROM OPCION WHERE Pagina = 'departamentos');

INSERT INTO OPCION (IdMenu, Nombre, OrdenMenu, Pagina, FechaCreacion, UsuarioCreacion)
SELECT me.IdMenu, 'Puestos', 6, 'puestos', SYSTIMESTAMP, 'system'
FROM MENU me WHERE me.Nombre = 'Parametros Generales' AND me.IdModulo = (SELECT IdModulo FROM MODULO WHERE Nombre = 'Planilla')
AND NOT EXISTS (SELECT 1 FROM OPCION WHERE Pagina = 'puestos');

INSERT INTO OPCION (IdMenu, Nombre, OrdenMenu, Pagina, FechaCreacion, UsuarioCreacion)
SELECT me.IdMenu, 'Personas', 7, 'personas', SYSTIMESTAMP, 'system'
FROM MENU me WHERE me.Nombre = 'Parametros Generales' AND me.IdModulo = (SELECT IdModulo FROM MODULO WHERE Nombre = 'Planilla')
AND NOT EXISTS (SELECT 1 FROM OPCION WHERE Pagina = 'personas');

INSERT INTO OPCION (IdMenu, Nombre, OrdenMenu, Pagina, FechaCreacion, UsuarioCreacion)
SELECT me.IdMenu, 'Documentos de Personas', 8, 'documentos-persona', SYSTIMESTAMP, 'system'
FROM MENU me WHERE me.Nombre = 'Parametros Generales' AND me.IdModulo = (SELECT IdModulo FROM MODULO WHERE Nombre = 'Planilla')
AND NOT EXISTS (SELECT 1 FROM OPCION WHERE Pagina = 'documentos-persona');

INSERT INTO OPCION (IdMenu, Nombre, OrdenMenu, Pagina, FechaCreacion, UsuarioCreacion)
SELECT me.IdMenu, 'Bancos', 9, 'bancos', SYSTIMESTAMP, 'system'
FROM MENU me WHERE me.Nombre = 'Parametros Generales' AND me.IdModulo = (SELECT IdModulo FROM MODULO WHERE Nombre = 'Planilla')
AND NOT EXISTS (SELECT 1 FROM OPCION WHERE Pagina = 'bancos');

-- --- Menu "Gestionar" (4 opciones) ---------------------------------------
INSERT INTO OPCION (IdMenu, Nombre, OrdenMenu, Pagina, FechaCreacion, UsuarioCreacion)
SELECT me.IdMenu, 'Empleados', 1, 'empleados', SYSTIMESTAMP, 'system'
FROM MENU me WHERE me.Nombre = 'Gestionar' AND me.IdModulo = (SELECT IdModulo FROM MODULO WHERE Nombre = 'Planilla')
AND NOT EXISTS (SELECT 1 FROM OPCION WHERE Pagina = 'empleados');

INSERT INTO OPCION (IdMenu, Nombre, OrdenMenu, Pagina, FechaCreacion, UsuarioCreacion)
SELECT me.IdMenu, 'Cuentas Bancarias Empleados', 2, 'cuentas-bancarias', SYSTIMESTAMP, 'system'
FROM MENU me WHERE me.Nombre = 'Gestionar' AND me.IdModulo = (SELECT IdModulo FROM MODULO WHERE Nombre = 'Planilla')
AND NOT EXISTS (SELECT 1 FROM OPCION WHERE Pagina = 'cuentas-bancarias');

INSERT INTO OPCION (IdMenu, Nombre, OrdenMenu, Pagina, FechaCreacion, UsuarioCreacion)
SELECT me.IdMenu, 'Inasistencias de Empleados', 3, 'inasistencias', SYSTIMESTAMP, 'system'
FROM MENU me WHERE me.Nombre = 'Gestionar' AND me.IdModulo = (SELECT IdModulo FROM MODULO WHERE Nombre = 'Planilla')
AND NOT EXISTS (SELECT 1 FROM OPCION WHERE Pagina = 'inasistencias');

INSERT INTO OPCION (IdMenu, Nombre, OrdenMenu, Pagina, FechaCreacion, UsuarioCreacion)
SELECT me.IdMenu, 'Calcular Planilla', 4, 'calculo-planilla', SYSTIMESTAMP, 'system'
FROM MENU me WHERE me.Nombre = 'Gestionar' AND me.IdModulo = (SELECT IdModulo FROM MODULO WHERE Nombre = 'Planilla')
AND NOT EXISTS (SELECT 1 FROM OPCION WHERE Pagina = 'calculo-planilla');

-- --- Menu "Reportes" (2 opciones) ----------------------------------------
INSERT INTO OPCION (IdMenu, Nombre, OrdenMenu, Pagina, FechaCreacion, UsuarioCreacion)
SELECT me.IdMenu, 'Reporte de Planilla', 1, 'reporte-planilla', SYSTIMESTAMP, 'system'
FROM MENU me WHERE me.Nombre = 'Reportes' AND me.IdModulo = (SELECT IdModulo FROM MODULO WHERE Nombre = 'Planilla')
AND NOT EXISTS (SELECT 1 FROM OPCION WHERE Pagina = 'reporte-planilla');

INSERT INTO OPCION (IdMenu, Nombre, OrdenMenu, Pagina, FechaCreacion, UsuarioCreacion)
SELECT me.IdMenu, 'Boletas de Pago', 2, 'boletas-pago', SYSTIMESTAMP, 'system'
FROM MENU me WHERE me.Nombre = 'Reportes' AND me.IdModulo = (SELECT IdModulo FROM MODULO WHERE Nombre = 'Planilla')
AND NOT EXISTS (SELECT 1 FROM OPCION WHERE Pagina = 'boletas-pago');

-- --- Menu "Liquidacion" (1 opcion) ---------------------------------------
INSERT INTO OPCION (IdMenu, Nombre, OrdenMenu, Pagina, FechaCreacion, UsuarioCreacion)
SELECT me.IdMenu, 'Liquidacion de Empleado', 1, 'liquidacion', SYSTIMESTAMP, 'system'
FROM MENU me WHERE me.Nombre = 'Liquidacion' AND me.IdModulo = (SELECT IdModulo FROM MODULO WHERE Nombre = 'Planilla')
AND NOT EXISTS (SELECT 1 FROM OPCION WHERE Pagina = 'liquidacion');

-- NOTA: el original (Planilla-SQL.sql) solo inserta en MODULO/MENU/OPCION,
-- NO en ROLE_OPCION - el modulo quedaba cargado pero sin permisos
-- asignados a ningun rol. El bloque de abajo SI agrega ROLE_OPCION para
-- el rol 'Administrador', siguiendo la regla ya usada en Fase 1
-- (ROLE_OPCION con los 5 flags en 1, incluido Consultar via la sola
-- existencia de la fila - ver REPORTE_FASE2_INVESTIGACION.md punto 2/7).

-- =====================================================================
-- ROLE_OPCION: permisos completos para 'Administrador' sobre las 16
-- opciones nuevas (Alta, Baja, Cambio, Imprimir, Exportar = 1; Consultar
-- se deriva de que exista la fila, no es columna).
-- =====================================================================
INSERT INTO ROLE_OPCION (IdRole, IdOpcion, Alta, Baja, Cambio, Imprimir, Exportar, FechaCreacion, UsuarioCreacion)
SELECT r.IdRole, op.IdOpcion, 1, 1, 1, 1, 1, SYSTIMESTAMP, 'system'
FROM "ROLE" r
CROSS JOIN OPCION op
JOIN MENU me ON me.IdMenu = op.IdMenu
JOIN MODULO mo ON mo.IdModulo = me.IdModulo
WHERE r.Nombre = 'Administrador'
  AND mo.Nombre = 'Planilla'
  AND NOT EXISTS (
      SELECT 1 FROM ROLE_OPCION ro WHERE ro.IdRole = r.IdRole AND ro.IdOpcion = op.IdOpcion
  );

COMMIT;


-- =====================================================================
-- Verificacion sugerida DESPUES de ejecutar (solo lectura):
-- =====================================================================
-- SELECT mo.Nombre AS Modulo, me.Nombre AS Menu, op.IdOpcion, op.Nombre AS Opcion, op.Pagina
-- FROM OPCION op
-- JOIN MENU me ON me.IdMenu = op.IdMenu
-- JOIN MODULO mo ON mo.IdModulo = me.IdModulo
-- WHERE mo.Nombre = 'Planilla'
-- ORDER BY me.OrdenMenu, op.OrdenMenu;
--
-- Ids PREDICHOS (a confirmar con la query de arriba; prediccion basada en
-- que MODULO/MENU/OPCION tenian hoy 1/4/10 filas - ver Parte 1.3 de
-- REPORTE_FASE2_PASOA_TRADUCCION_SQL.md):
--   MODULO 'Planilla'              -> IdModulo = 2
--   MENU 'Parametros Generales'    -> IdMenu = 5
--   MENU 'Gestionar'               -> IdMenu = 6
--   MENU 'Reportes'                -> IdMenu = 7
--   MENU 'Liquidacion'             -> IdMenu = 8
--   OPCION (9 de IdMenu=5)         -> IdOpcion = 11..19
--   OPCION (4 de IdMenu=6)         -> IdOpcion = 20..23
--   OPCION (2 de IdMenu=7)         -> IdOpcion = 24..25
--   OPCION (1 de IdMenu=8)         -> IdOpcion = 26
