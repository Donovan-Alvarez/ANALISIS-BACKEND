SET SERVEROUTPUT ON

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

SELECT column_name, nullable
FROM user_tab_columns
WHERE table_name = 'DEPARTAMENTO'
  AND column_name = 'IDEMPRESA';
