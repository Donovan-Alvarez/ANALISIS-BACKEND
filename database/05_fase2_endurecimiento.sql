SET SERVEROUTPUT ON

SELECT Pagina, COUNT(*) AS Veces, LISTAGG(IdOpcion, ',') WITHIN GROUP (ORDER BY IdOpcion) AS IdsOpcion
FROM OPCION
GROUP BY Pagina
HAVING COUNT(*) > 1;

DECLARE
    v_Duplicados PLS_INTEGER;
    v_Existe     PLS_INTEGER;
BEGIN
    SELECT COUNT(*) INTO v_Duplicados
    FROM (SELECT Pagina FROM OPCION GROUP BY Pagina HAVING COUNT(*) > 1);

    IF v_Duplicados > 0 THEN
        RAISE_APPLICATION_ERROR(-20101,
            'No se puede crear UQ_OPCION_PAGINA: hay ' || v_Duplicados ||
            ' valor(es) de OPCION.Pagina repetidos. Revisa el SELECT de chequeo y limpialos antes (ver 04b).');
    END IF;

    SELECT COUNT(*) INTO v_Existe
    FROM user_constraints c
    WHERE c.table_name = 'OPCION'
      AND c.constraint_type IN ('U', 'P')
      AND (c.constraint_name = 'UQ_OPCION_PAGINA'
           OR (SELECT LISTAGG(cc.column_name, ',') WITHIN GROUP (ORDER BY cc.position)
               FROM user_cons_columns cc
               WHERE cc.constraint_name = c.constraint_name) = 'PAGINA');

    IF v_Existe = 0 THEN
        EXECUTE IMMEDIATE 'ALTER TABLE OPCION ADD CONSTRAINT UQ_OPCION_PAGINA UNIQUE (Pagina)';
        DBMS_OUTPUT.PUT_LINE('UQ_OPCION_PAGINA creada.');
    ELSE
        DBMS_OUTPUT.PUT_LINE('UQ_OPCION_PAGINA (o una UNIQUE equivalente sobre Pagina) ya existia: sin cambios.');
    END IF;
END;
/

CREATE OR REPLACE TRIGGER TRG_ESTADO_CIVIL_BAJA_VALIDA
BEFORE DELETE ON ESTADO_CIVIL
FOR EACH ROW
DECLARE
    v_Count PLS_INTEGER;
BEGIN
    SELECT COUNT(*) INTO v_Count FROM PERSONA WHERE IdEstadoCivil = :OLD.IdEstadoCivil;
    IF v_Count > 0 THEN
        RAISE_APPLICATION_ERROR(-20102, 'No se puede eliminar el estado civil: está asignado a ' || v_Count || ' persona(s).');
    END IF;
END;
/

CREATE OR REPLACE TRIGGER TRG_STATUS_EMPLEADO_BAJA_VALIDA
BEFORE DELETE ON STATUS_EMPLEADO
FOR EACH ROW
DECLARE
    v_Count PLS_INTEGER;
BEGIN
    SELECT COUNT(*) INTO v_Count FROM EMPLEADO WHERE IdStatusEmpleado = :OLD.IdStatusEmpleado;
    IF v_Count > 0 THEN
        RAISE_APPLICATION_ERROR(-20103, 'No se puede eliminar el status de empleado: está asignado a ' || v_Count || ' empleado(s).');
    END IF;

    SELECT COUNT(*) INTO v_Count FROM FLUJO_STATUS_EMPLEADO
    WHERE IdStatusActual = :OLD.IdStatusEmpleado OR IdStatusNuevo = :OLD.IdStatusEmpleado;
    IF v_Count > 0 THEN
        RAISE_APPLICATION_ERROR(-20104, 'No se puede eliminar el status de empleado: se usa en ' || v_Count || ' flujo(s) de status.');
    END IF;

    SELECT COUNT(*) INTO v_Count FROM PLANILLA_DETALLE WHERE IdStatusEmpleado = :OLD.IdStatusEmpleado;
    IF v_Count > 0 THEN
        RAISE_APPLICATION_ERROR(-20105, 'No se puede eliminar el status de empleado: aparece en ' || v_Count || ' detalle(s) de planilla.');
    END IF;
END;
/

CREATE OR REPLACE TRIGGER TRG_TIPO_DOCUMENTO_BAJA_VALIDA
BEFORE DELETE ON TIPO_DOCUMENTO
FOR EACH ROW
DECLARE
    v_Count PLS_INTEGER;
BEGIN
    SELECT COUNT(*) INTO v_Count FROM DOCUMENTO_PERSONA WHERE IdTipoDocumento = :OLD.IdTipoDocumento;
    IF v_Count > 0 THEN
        RAISE_APPLICATION_ERROR(-20106, 'No se puede eliminar el tipo de documento: está asignado a ' || v_Count || ' documento(s) de persona.');
    END IF;
END;
/

CREATE OR REPLACE TRIGGER TRG_BANCO_BAJA_VALIDA
BEFORE DELETE ON BANCO
FOR EACH ROW
DECLARE
    v_Count PLS_INTEGER;
BEGIN
    SELECT COUNT(*) INTO v_Count FROM CUENTA_BANCARIA_EMPLEADO WHERE IdBanco = :OLD.IdBanco;
    IF v_Count > 0 THEN
        RAISE_APPLICATION_ERROR(-20107, 'No se puede eliminar el banco: tiene ' || v_Count || ' cuenta(s) bancaria(s) de empleados asociada(s).');
    END IF;
END;
/

CREATE OR REPLACE TRIGGER TRG_DEPARTAMENTO_BAJA_VALIDA
BEFORE DELETE ON DEPARTAMENTO
FOR EACH ROW
DECLARE
    v_Count PLS_INTEGER;
BEGIN
    SELECT COUNT(*) INTO v_Count FROM PUESTO WHERE IdDepartamento = :OLD.IdDepartamento;
    IF v_Count > 0 THEN
        RAISE_APPLICATION_ERROR(-20108, 'No se puede eliminar el departamento: tiene ' || v_Count || ' puesto(s) asociado(s).');
    END IF;
END;
/

CREATE OR REPLACE TRIGGER TRG_PUESTO_BAJA_VALIDA
BEFORE DELETE ON PUESTO
FOR EACH ROW
DECLARE
    v_Count PLS_INTEGER;
BEGIN
    SELECT COUNT(*) INTO v_Count FROM EMPLEADO WHERE IdPuesto = :OLD.IdPuesto;
    IF v_Count > 0 THEN
        RAISE_APPLICATION_ERROR(-20109, 'No se puede eliminar el puesto: está asignado a ' || v_Count || ' empleado(s).');
    END IF;

    SELECT COUNT(*) INTO v_Count FROM PLANILLA_DETALLE WHERE IdPuesto = :OLD.IdPuesto;
    IF v_Count > 0 THEN
        RAISE_APPLICATION_ERROR(-20110, 'No se puede eliminar el puesto: aparece en ' || v_Count || ' detalle(s) de planilla.');
    END IF;

    SELECT COUNT(*) INTO v_Count FROM LIQUIDACION WHERE IdPuesto = :OLD.IdPuesto;
    IF v_Count > 0 THEN
        RAISE_APPLICATION_ERROR(-20111, 'No se puede eliminar el puesto: aparece en ' || v_Count || ' liquidación(es).');
    END IF;
END;
/

COLUMN constraint_name FORMAT A25
COLUMN trigger_name FORMAT A35
COLUMN table_name FORMAT A20
SELECT constraint_name, constraint_type, status
FROM user_constraints
WHERE table_name = 'OPCION' AND constraint_name = 'UQ_OPCION_PAGINA';

SELECT t.trigger_name, t.table_name, t.status, o.status AS compilado
FROM user_triggers t
JOIN user_objects o ON o.object_name = t.trigger_name AND o.object_type = 'TRIGGER'
WHERE t.trigger_name IN ('TRG_ESTADO_CIVIL_BAJA_VALIDA', 'TRG_STATUS_EMPLEADO_BAJA_VALIDA',
                         'TRG_TIPO_DOCUMENTO_BAJA_VALIDA', 'TRG_BANCO_BAJA_VALIDA',
                         'TRG_DEPARTAMENTO_BAJA_VALIDA', 'TRG_PUESTO_BAJA_VALIDA')
ORDER BY t.trigger_name;
