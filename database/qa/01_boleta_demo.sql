-- SOLO QA LOCAL: no es una migracion ni ejecuta el calculo de Cristian.
-- Periodo reservado: diciembre 2099. No modifica empleados ni cuentas.
-- Retirar con 02_retirar_boleta_demo.sql cuando termine la prueba.
SET DEFINE OFF;
SET SERVEROUTPUT ON;
DECLARE
    v_existentes NUMBER;
    v_empleado NUMBER;
BEGIN
    SELECT COUNT(*) INTO v_existentes FROM PERIODO_PLANILLA WHERE Anio=2099 AND Mes=12;
    IF v_existentes > 0 THEN
        DBMS_OUTPUT.PUT_LINE('El periodo 2099/12 ya existe. No se modifico ningun dato.');
        RETURN;
    END IF;

    SELECT MIN(e.IdEmpleado) INTO v_empleado FROM EMPLEADO e
    WHERE EXISTS (SELECT 1 FROM CUENTA_BANCARIA_EMPLEADO c WHERE c.IdEmpleado=e.IdEmpleado AND c.Activa=1);
    IF v_empleado IS NULL THEN
        SELECT MIN(IdEmpleado) INTO v_empleado FROM EMPLEADO;
    END IF;
    IF v_empleado IS NULL THEN
        DBMS_OUTPUT.PUT_LINE('No hay empleados: no se creo la demostracion.');
        RETURN;
    END IF;

    INSERT INTO PERIODO_PLANILLA(Anio,Mes,FechaInicio,FechaFin,FechaCreacion,UsuarioCreacion)
    VALUES(2099,12,DATE '2099-12-01',DATE '2099-12-31',SYSDATE,'qa_javier_boleta');

    INSERT INTO PLANILLA_CABECERA(Anio,Mes,TotalIngresos,TotalDescuentos,SalarioNeto,FechaHoraProcesada,FechaCreacion,UsuarioCreacion)
    VALUES(2099,12,5400,391.50,5008.50,SYSDATE,SYSDATE,'qa_javier_boleta');

    INSERT INTO PLANILLA_DETALLE(Anio,Mes,IdEmpleado,FechaContratacion,IdPuesto,IdStatusEmpleado,
        IngresoSueldoBase,IngresoBonificacionDecreto,IngresoOtrosIngresos,
        DescuentoIgss,DescuentoIsr,DescuentoInasistencias,SalarioNeto,FechaCreacion,UsuarioCreacion)
    SELECT 2099,12,IdEmpleado,FechaContratacion,IdPuesto,IdStatusEmpleado,
        5000,250,150,241.50,100,50,5008.50,SYSDATE,'qa_javier_boleta'
    FROM EMPLEADO WHERE IdEmpleado=v_empleado;

    DBMS_OUTPUT.PUT_LINE('Boleta DEMO creada. Periodo 2099/12; empleado ' || v_empleado || '; neto Q5008.50.');
END;
/
COMMIT;

SELECT d.Anio,d.Mes,d.IdEmpleado,p.Nombre || ' ' || p.Apellido AS Empleado,
       d.IngresoSueldoBase,d.IngresoBonificacionDecreto,d.IngresoOtrosIngresos,
       d.DescuentoIgss,d.DescuentoIsr,d.DescuentoInasistencias,d.SalarioNeto
FROM PLANILLA_DETALLE d JOIN EMPLEADO e ON e.IdEmpleado=d.IdEmpleado
JOIN PERSONA p ON p.IdPersona=e.IdPersona
WHERE d.Anio=2099 AND d.Mes=12 AND d.UsuarioCreacion='qa_javier_boleta';
