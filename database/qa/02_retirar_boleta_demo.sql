-- SOLO QA LOCAL. Retira exclusivamente el periodo 2099/12 de esta demo.
-- Se niega a borrar si encuentra registros del periodo creados/modificados por otros.
SET DEFINE OFF;
SET SERVEROUTPUT ON;
DECLARE
    v_ajenos NUMBER;
BEGIN
    SELECT COUNT(*) INTO v_ajenos FROM (
        SELECT UsuarioCreacion,UsuarioModificacion FROM PERIODO_PLANILLA WHERE Anio=2099 AND Mes=12
        UNION ALL
        SELECT UsuarioCreacion,UsuarioModificacion FROM PLANILLA_CABECERA WHERE Anio=2099 AND Mes=12
        UNION ALL
        SELECT UsuarioCreacion,UsuarioModificacion FROM PLANILLA_DETALLE WHERE Anio=2099 AND Mes=12
    ) WHERE UsuarioCreacion <> 'qa_javier_boleta'
         OR (UsuarioModificacion IS NOT NULL AND UsuarioModificacion <> 'qa_javier_boleta');
    IF v_ajenos > 0 THEN
        DBMS_OUTPUT.PUT_LINE('No se elimino nada: hay datos ajenos o modificados en 2099/12. Revisar manualmente.');
        RETURN;
    END IF;
    DELETE FROM PLANILLA_DETALLE WHERE Anio=2099 AND Mes=12 AND UsuarioCreacion='qa_javier_boleta';
    DELETE FROM PLANILLA_CABECERA WHERE Anio=2099 AND Mes=12 AND UsuarioCreacion='qa_javier_boleta';
    DELETE FROM PERIODO_PLANILLA WHERE Anio=2099 AND Mes=12 AND UsuarioCreacion='qa_javier_boleta';
    DBMS_OUTPUT.PUT_LINE('Demo retirada. Empleados, cuentas y periodos iniciales conservados.');
END;
/
COMMIT;
