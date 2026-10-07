/*
 * FASE 2 - Modulo de Planilla
 * 06_fase2_departamento_empresa_obligatoria.sql
 *
 * Paso E - DEPARTAMENTO.IdEmpresa pasa a NOT NULL: un departamento siempre
 * pertenece a una empresa (no puede quedar "huerfano"). El backend ya lo
 * exige (@NotNull en DepartamentoDTO + validacion en DepartamentoService);
 * esto lo garantiza tambien en la BD, frente a SQL directo u otras pantallas.
 *
 * 01_fase2_ddl.sql ya crea la columna como NOT NULL, asi que en una
 * instalacion nueva este script no hace nada. Es para las BD creadas antes
 * del Paso E.
 *
 * Codigo usado (rango de Donovan, -20101..-20119, ver D8):
 *   -20112  guarda de este archivo: hay departamentos con IdEmpresa nulo
 *   (-20113..-20119 libres)
 *
 * Idempotente:
 *   - Si IdEmpresa ya es NOT NULL (user_tab_columns.NULLABLE = 'N'), no hace nada.
 *   - Si hay filas con IdEmpresa nulo, aborta con ORA-20112 sin cambiar nada:
 *     hay que asignarles empresa (o borrarlas) antes de volver a correrlo.
 *   - Si no, ALTER TABLE DEPARTAMENTO MODIFY (IdEmpresa NOT NULL).
 *
 * No toca datos: ningun INSERT/UPDATE/DELETE.
 * Uso: ./database/run-sql.sh database/06_fase2_departamento_empresa_obligatoria.sql
 */

SET SERVEROUTPUT ON


-- Chequeo previo: debe devolver "no rows selected". Si devuelve filas,
-- el bloque de abajo aborta con ORA-20112 sin modificar la tabla.
SELECT IdDepartamento, Nombre
FROM DEPARTAMENTO
WHERE IdEmpresa IS NULL
ORDER BY IdDepartamento;

DECLARE
    v_Nullable  user_tab_columns.nullable%TYPE;
    v_SinEmpresa PLS_INTEGER;
BEGIN
    SELECT nullable INTO v_Nullable
    FROM user_tab_columns
    WHERE table_name = 'DEPARTAMENTO'
      AND column_name = 'IDEMPRESA';

    IF v_Nullable = 'N' THEN
        DBMS_OUTPUT.PUT_LINE('DEPARTAMENTO.IdEmpresa ya es NOT NULL: sin cambios.');
        RETURN;
    END IF;

    SELECT COUNT(*) INTO v_SinEmpresa
    FROM DEPARTAMENTO
    WHERE IdEmpresa IS NULL;

    IF v_SinEmpresa > 0 THEN
        RAISE_APPLICATION_ERROR(-20112,
            'No se puede hacer obligatoria DEPARTAMENTO.IdEmpresa: hay ' || v_SinEmpresa ||
            ' departamento(s) sin empresa. Asignales una empresa (ver el SELECT de chequeo) y vuelve a correr este script.');
    END IF;

    EXECUTE IMMEDIATE 'ALTER TABLE DEPARTAMENTO MODIFY (IdEmpresa NOT NULL)';
    DBMS_OUTPUT.PUT_LINE('DEPARTAMENTO.IdEmpresa ahora es NOT NULL.');
END;
/


-- Verificacion: NULLABLE debe ser N.
SELECT column_name, nullable
FROM user_tab_columns
WHERE table_name = 'DEPARTAMENTO'
  AND column_name = 'IDEMPRESA';
