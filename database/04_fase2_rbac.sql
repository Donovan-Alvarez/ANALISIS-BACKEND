INSERT INTO MODULO (Nombre, OrdenMenu, FechaCreacion, UsuarioCreacion)
SELECT 'Planilla', (SELECT MAX(OrdenMenu) + 1 FROM MODULO), SYSTIMESTAMP, 'system'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM MODULO WHERE Nombre = 'Planilla');

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

INSERT INTO OPCION (IdMenu, Nombre, OrdenMenu, Pagina, FechaCreacion, UsuarioCreacion)
SELECT me.IdMenu, 'Reporte de Planilla', 1, 'reporte-planilla', SYSTIMESTAMP, 'system'
FROM MENU me WHERE me.Nombre = 'Reportes' AND me.IdModulo = (SELECT IdModulo FROM MODULO WHERE Nombre = 'Planilla')
AND NOT EXISTS (SELECT 1 FROM OPCION WHERE Pagina = 'reporte-planilla');

INSERT INTO OPCION (IdMenu, Nombre, OrdenMenu, Pagina, FechaCreacion, UsuarioCreacion)
SELECT me.IdMenu, 'Boletas de Pago', 2, 'boletas-pago', SYSTIMESTAMP, 'system'
FROM MENU me WHERE me.Nombre = 'Reportes' AND me.IdModulo = (SELECT IdModulo FROM MODULO WHERE Nombre = 'Planilla')
AND NOT EXISTS (SELECT 1 FROM OPCION WHERE Pagina = 'boletas-pago');

INSERT INTO OPCION (IdMenu, Nombre, OrdenMenu, Pagina, FechaCreacion, UsuarioCreacion)
SELECT me.IdMenu, 'Liquidacion de Empleado', 1, 'liquidacion', SYSTIMESTAMP, 'system'
FROM MENU me WHERE me.Nombre = 'Liquidacion' AND me.IdModulo = (SELECT IdModulo FROM MODULO WHERE Nombre = 'Planilla')
AND NOT EXISTS (SELECT 1 FROM OPCION WHERE Pagina = 'liquidacion');

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
