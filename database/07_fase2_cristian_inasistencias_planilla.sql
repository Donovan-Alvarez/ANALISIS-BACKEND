

DECLARE
    v_existe NUMBER;
BEGIN
    SELECT COUNT(*) INTO v_existe
    FROM USER_CONSTRAINTS
    WHERE CONSTRAINT_NAME = 'CK_INASISTENCIA_FECHAS' AND TABLE_NAME = 'INASISTENCIA';

    IF v_existe = 0 THEN
        EXECUTE IMMEDIATE 'ALTER TABLE INASISTENCIA ADD CONSTRAINT CK_INASISTENCIA_FECHAS CHECK (FechaFinal >= FechaInicial)';
    END IF;
END;
/

CREATE OR REPLACE TRIGGER TRG_INASISTENCIA_PROCESADA
BEFORE UPDATE OR DELETE ON INASISTENCIA
FOR EACH ROW
BEGIN
    IF :OLD.FechaProcesado IS NOT NULL THEN
        IF DELETING THEN
            RAISE_APPLICATION_ERROR(-20140, 'No se puede eliminar la inasistencia: ya fue procesada en una planilla.');
        ELSE
            RAISE_APPLICATION_ERROR(-20141, 'No se puede modificar la inasistencia: ya fue procesada en una planilla.');
        END IF;
    END IF;
END;
/

CREATE OR REPLACE PROCEDURE PR_CALCULAR_PLANILLA(
    p_Anio                    IN  NUMBER,
    p_Mes                     IN  NUMBER,
    p_Usuario                 IN  VARCHAR2,
    p_FechaProceso            IN  DATE,
    p_EmpleadosProcesados     OUT NUMBER,
    p_InasistenciasProcesadas OUT NUMBER,
    p_TotalIngresos           OUT NUMBER,
    p_TotalDescuentos         OUT NUMBER,
    p_SalarioNeto             OUT NUMBER
)
IS
    v_FechaFin PERIODO_PLANILLA.FechaFin%TYPE;
    v_Existe   NUMBER;
    v_IdActivo STATUS_EMPLEADO.IdStatusEmpleado%TYPE;
BEGIN
    IF p_Anio IS NULL OR p_Mes IS NULL OR p_Usuario IS NULL OR p_FechaProceso IS NULL THEN
        RAISE_APPLICATION_ERROR(-20142, 'Parametros invalidos para calcular la planilla.');
    END IF;

    BEGIN
        SELECT FechaFin INTO v_FechaFin
        FROM PERIODO_PLANILLA
        WHERE Anio = p_Anio AND Mes = p_Mes;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            RAISE_APPLICATION_ERROR(-20143, 'El periodo ' || LPAD(p_Mes, 2, '0') || '/' || p_Anio || ' no existe.');
    END;

    IF v_FechaFin IS NULL THEN
        RAISE_APPLICATION_ERROR(-20143, 'El periodo ' || LPAD(p_Mes, 2, '0') || '/' || p_Anio || ' no tiene fecha de fin.');
    END IF;

    SELECT COUNT(*) INTO v_Existe
    FROM PLANILLA_CABECERA
    WHERE Anio = p_Anio AND Mes = p_Mes;

    IF v_Existe > 0 THEN
        RAISE_APPLICATION_ERROR(-20144, 'La planilla del periodo ' || LPAD(p_Mes, 2, '0') || '/' || p_Anio || ' ya fue calculada.');
    END IF;

    BEGIN
        SELECT IdStatusEmpleado INTO v_IdActivo
        FROM STATUS_EMPLEADO
        WHERE UPPER(TRIM(Nombre)) = 'ACTIVO';
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            RAISE_APPLICATION_ERROR(-20146, 'No existe el status de empleado Activo.');
    END;

   
    BEGIN
        INSERT INTO PLANILLA_CABECERA (Anio, Mes, TotalIngresos, TotalDescuentos, SalarioNeto,
                                       FechaHoraProcesada, FechaCreacion, UsuarioCreacion)
        VALUES (p_Anio, p_Mes, 0, 0, 0, p_FechaProceso, p_FechaProceso, p_Usuario);
    EXCEPTION
        WHEN DUP_VAL_ON_INDEX THEN
            RAISE_APPLICATION_ERROR(-20144, 'La planilla del periodo ' || LPAD(p_Mes, 2, '0') || '/' || p_Anio || ' ya fue calculada.');
    END;

    INSERT INTO PLANILLA_DETALLE (Anio, Mes, IdEmpleado, FechaContratacion, IdPuesto, IdStatusEmpleado,
                                  IngresoSueldoBase, IngresoBonificacionDecreto, IngresoOtrosIngresos,
                                  DescuentoIgss, DescuentoIsr, DescuentoInasistencias, SalarioNeto,
                                  FechaCreacion, UsuarioCreacion)
    SELECT p_Anio, p_Mes, x.IdEmpleado, x.FechaContratacion, x.IdPuesto, x.IdStatusEmpleado,
           x.IngresoSueldoBase, x.IngresoBonificacionDecreto, x.IngresoOtrosIngresos,
           x.DescuentoIgss, x.DescuentoIsr, x.DescInasistencias,
           (x.IngresoSueldoBase + x.IngresoBonificacionDecreto + x.IngresoOtrosIngresos)
             - (x.DescuentoIgss + x.DescuentoIsr + x.DescInasistencias),
           p_FechaProceso, p_Usuario
    FROM (
        SELECT e.IdEmpleado, e.FechaContratacion, e.IdPuesto, e.IdStatusEmpleado,
               e.IngresoSueldoBase, e.IngresoBonificacionDecreto, e.IngresoOtrosIngresos,
               e.DescuentoIgss, e.DescuentoIsr,
               ROUND(e.DescuentoInasistencias + NVL(i.Dias, 0) * e.IngresoSueldoBase / 30, 2) AS DescInasistencias
        FROM EMPLEADO e
        LEFT JOIN (
            SELECT IdEmpleado, SUM(TRUNC(FechaFinal) - TRUNC(FechaInicial) + 1) AS Dias
            FROM INASISTENCIA
            WHERE FechaProcesado IS NULL
              AND TRUNC(FechaFinal) <= TRUNC(v_FechaFin)
            GROUP BY IdEmpleado
        ) i ON i.IdEmpleado = e.IdEmpleado
        WHERE e.IdStatusEmpleado = v_IdActivo
          AND e.FechaContratacion <= TRUNC(v_FechaFin)
    ) x;
    p_EmpleadosProcesados := SQL%ROWCOUNT;

    IF p_EmpleadosProcesados = 0 THEN
        RAISE_APPLICATION_ERROR(-20145, 'No hay empleados activos para calcular el periodo ' || LPAD(p_Mes, 2, '0') || '/' || p_Anio || '.');
    END IF;

    UPDATE INASISTENCIA
       SET FechaProcesado      = p_FechaProceso,
           FechaModificacion   = p_FechaProceso,
           UsuarioModificacion = p_Usuario
     WHERE FechaProcesado IS NULL
       AND TRUNC(FechaFinal) <= TRUNC(v_FechaFin)
       AND IdEmpleado IN (SELECT IdEmpleado FROM PLANILLA_DETALLE WHERE Anio = p_Anio AND Mes = p_Mes);
    p_InasistenciasProcesadas := SQL%ROWCOUNT;

    SELECT NVL(SUM(IngresoSueldoBase + IngresoBonificacionDecreto + IngresoOtrosIngresos), 0),
           NVL(SUM(DescuentoIgss + DescuentoIsr + DescuentoInasistencias), 0),
           NVL(SUM(SalarioNeto), 0)
      INTO p_TotalIngresos, p_TotalDescuentos, p_SalarioNeto
      FROM PLANILLA_DETALLE
     WHERE Anio = p_Anio AND Mes = p_Mes;

    UPDATE PLANILLA_CABECERA
       SET TotalIngresos   = p_TotalIngresos,
           TotalDescuentos = p_TotalDescuentos,
           SalarioNeto     = p_SalarioNeto
     WHERE Anio = p_Anio AND Mes = p_Mes;
END;
/


SELECT object_name, object_type, status FROM user_objects
WHERE object_name IN ('PR_CALCULAR_PLANILLA', 'TRG_INASISTENCIA_PROCESADA')
ORDER BY object_name;

SELECT name, type, line, text FROM user_errors
WHERE name IN ('PR_CALCULAR_PLANILLA', 'TRG_INASISTENCIA_PROCESADA')
ORDER BY name, sequence;