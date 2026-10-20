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

DELETE FROM ROLE_OPCION
WHERE IdOpcion IN (
    SELECT IdOpcion FROM OPCION
    WHERE IdOpcion = 61 AND Pagina = 'generos' AND IdMenu = 81
);

DELETE FROM OPCION
WHERE IdOpcion = 61 AND Pagina = 'generos' AND IdMenu = 81;

COMMIT;

SELECT IdOpcion, IdMenu, Nombre, Pagina
FROM OPCION
WHERE Pagina = 'generos';

SELECT ro.IdRole, r.Nombre, ro.IdOpcion
FROM ROLE_OPCION ro
JOIN "ROLE" r ON r.IdRole = ro.IdRole
WHERE ro.IdOpcion = 3;
