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

DELETE FROM ROLE_OPCION
WHERE IdOpcion IN (
    SELECT op.IdOpcion
    FROM OPCION op
    JOIN MENU me ON me.IdMenu = op.IdMenu
    JOIN MODULO mo ON mo.IdModulo = me.IdModulo
    WHERE mo.Nombre = 'Planilla'
);

DELETE FROM OPCION
WHERE IdMenu IN (
    SELECT me.IdMenu
    FROM MENU me
    JOIN MODULO mo ON mo.IdModulo = me.IdModulo
    WHERE mo.Nombre = 'Planilla'
);

DELETE FROM MENU
WHERE IdModulo IN (
    SELECT IdModulo FROM MODULO WHERE Nombre = 'Planilla'
);

DELETE FROM MODULO
WHERE Nombre = 'Planilla';

COMMIT;
