/* ================================================================
   Fase II · Procedimientos y funciones de EMPLEADO y LIQUIDACION
   Bryan Legrand Godoy — idOpcion 'empleados' e idOpcion 'liquidacion'
   ================================================================
   Requisitos: correr DESPUES de 01_fase2_ddl.sql (y de 02/03 si se
   quieren datos). Es re-ejecutable: todo es CREATE OR REPLACE y no
   toca datos ni estructura de tablas.

   Por que existe este archivo:
   El estandar de Fase II exige "toda persistencia usa JdbcTemplate +
   Stored Procedures". El DDL oficial crea las tablas pero no trae
   ningun procedimiento para EMPLEADO ni LIQUIDACION, asi que estos
   son los que consumira el backend: escritura por PROCEDURE, lectura
   por FUNCTION que devuelve SYS_REFCURSOR. En Java no queda ni un
   SELECT embebido.

   Rango de errores: -20120 a -20139, el que asigna D8 de
   docs/fase2/decisiones-arquitectura.md a Bryan (empleados, liquidacion).
   Usados: -20120 a -20127 en EMPLEADO, -20130 a -20133 en LIQUIDACION.
   ================================================================ */

-- SQL*Plus corta las sentencias en lineas en blanco si no se le indica
-- lo contrario, y aqui hay bloques PL/SQL con lineas vacias adentro.
SET SQLBLANKLINES ON
SET DEFINE OFF


/* ============================================================
   SECCION 1 — EMPLEADO
   ============================================================ */

/* Validacion de montos compartida.
   La tabla oficial no tiene CHECK de no-negativos, y no se la
   modificamos porque es de uso comun. En su lugar la regla vive aqui:
   asi tampoco se puede saltar escribiendo SQL directo, que es lo que
   importa. */
CREATE OR REPLACE PROCEDURE PR_EMPLEADO_VALIDAR_MONTOS(
    p_SueldoBase            IN NUMBER,
    p_BonificacionDecreto   IN NUMBER,
    p_OtrosIngresos         IN NUMBER,
    p_DescuentoIgss         IN NUMBER,
    p_DescuentoIsr          IN NUMBER,
    p_DescuentoInasistencias IN NUMBER
)
IS
BEGIN
    IF p_SueldoBase < 0 OR p_BonificacionDecreto < 0 OR p_OtrosIngresos < 0
       OR p_DescuentoIgss < 0 OR p_DescuentoIsr < 0 OR p_DescuentoInasistencias < 0 THEN
        RAISE_APPLICATION_ERROR(-20120,
            'Los montos de ingresos y descuentos no pueden ser negativos.');
    END IF;
END;
/

/* Transiciones de status.
   Devuelve 1 si el empleado puede pasar al status indicado, 0 si no.
   Quedarse en el mismo status siempre es valido: editar el sueldo sin
   tocar el status no deberia exigir una fila en FLUJO_STATUS_EMPLEADO.

   El catalogo de transiciones es de Javier (idOpcion 'flujo-status-empleado');
   aqui solo se consulta. */
CREATE OR REPLACE FUNCTION FN_EMPLEADO_TRANSICION_VALIDA(
    p_IdEmpleado    IN NUMBER,
    p_IdStatusNuevo IN NUMBER
) RETURN NUMBER
IS
    v_StatusActual NUMBER;
    v_Existe       PLS_INTEGER;
BEGIN
    SELECT IdStatusEmpleado INTO v_StatusActual
      FROM EMPLEADO WHERE IdEmpleado = p_IdEmpleado;

    IF v_StatusActual = p_IdStatusNuevo THEN
        RETURN 1;
    END IF;

    SELECT COUNT(*) INTO v_Existe
      FROM FLUJO_STATUS_EMPLEADO
     WHERE IdStatusActual = v_StatusActual
       AND IdStatusNuevo  = p_IdStatusNuevo;

    RETURN CASE WHEN v_Existe > 0 THEN 1 ELSE 0 END;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RETURN 0;   -- el empleado no existe
END;
/

/* Nombre legible de una transicion, para armar el mensaje de error del
   Service sin que tenga que consultar el catalogo por su cuenta. */
CREATE OR REPLACE FUNCTION FN_EMPLEADO_DESCRIBIR_TRANSICION(
    p_IdEmpleado    IN NUMBER,
    p_IdStatusNuevo IN NUMBER
) RETURN VARCHAR2
IS
    v_Actual VARCHAR2(50);
    v_Nuevo  VARCHAR2(50);
BEGIN
    SELECT sa.Nombre, sn.Nombre INTO v_Actual, v_Nuevo
      FROM EMPLEADO e
      JOIN STATUS_EMPLEADO sa ON sa.IdStatusEmpleado = e.IdStatusEmpleado
      CROSS JOIN STATUS_EMPLEADO sn
     WHERE e.IdEmpleado = p_IdEmpleado
       AND sn.IdStatusEmpleado = p_IdStatusNuevo;

    RETURN 'de "' || v_Actual || '" a "' || v_Nuevo || '"';
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RETURN NULL;
END;
/

CREATE OR REPLACE PROCEDURE PR_EMPLEADO_INSERTAR(
    p_IdPersona                  IN  NUMBER,
    p_IdSucursal                 IN  NUMBER,
    p_FechaContratacion          IN  DATE,
    p_IdPuesto                   IN  NUMBER,
    p_IdStatusEmpleado           IN  NUMBER,
    p_IngresoSueldoBase          IN  NUMBER,
    p_IngresoBonificacionDecreto IN  NUMBER,
    p_IngresoOtrosIngresos       IN  NUMBER,
    p_DescuentoIgss              IN  NUMBER,
    p_DescuentoIsr               IN  NUMBER,
    p_DescuentoInasistencias     IN  NUMBER,
    p_FechaCreacion              IN  TIMESTAMP,
    p_UsuarioCreacion            IN  VARCHAR2,
    p_IdEmpleado                 OUT NUMBER
)
IS
BEGIN
    PR_EMPLEADO_VALIDAR_MONTOS(p_IngresoSueldoBase, p_IngresoBonificacionDecreto,
        p_IngresoOtrosIngresos, p_DescuentoIgss, p_DescuentoIsr, p_DescuentoInasistencias);

    IF p_FechaContratacion > SYSDATE THEN
        RAISE_APPLICATION_ERROR(-20121,
            'La fecha de contratacion no puede ser futura.');
    END IF;

    INSERT INTO EMPLEADO (
        IdPersona, IdSucursal, FechaContratacion, IdPuesto, IdStatusEmpleado,
        IngresoSueldoBase, IngresoBonificacionDecreto, IngresoOtrosIngresos,
        DescuentoIgss, DescuentoIsr, DescuentoInasistencias,
        FechaCreacion, UsuarioCreacion
    ) VALUES (
        p_IdPersona, p_IdSucursal, p_FechaContratacion, p_IdPuesto, p_IdStatusEmpleado,
        p_IngresoSueldoBase, p_IngresoBonificacionDecreto, p_IngresoOtrosIngresos,
        p_DescuentoIgss, p_DescuentoIsr, p_DescuentoInasistencias,
        p_FechaCreacion, p_UsuarioCreacion
    )
    RETURNING IdEmpleado INTO p_IdEmpleado;
END;
/

/* El Service valida la transicion ANTES de llamar aqui, para poder dar un
   mensaje en espanol con los nombres de los status. Esta validacion de
   adentro es la red de seguridad: impide que una llamada directa a la BD
   se salte la regla. */
CREATE OR REPLACE PROCEDURE PR_EMPLEADO_ACTUALIZAR(
    p_IdEmpleado                 IN NUMBER,
    p_IdPersona                  IN NUMBER,
    p_IdSucursal                 IN NUMBER,
    p_FechaContratacion          IN DATE,
    p_IdPuesto                   IN NUMBER,
    p_IdStatusEmpleado           IN NUMBER,
    p_IngresoSueldoBase          IN NUMBER,
    p_IngresoBonificacionDecreto IN NUMBER,
    p_IngresoOtrosIngresos       IN NUMBER,
    p_DescuentoIgss              IN NUMBER,
    p_DescuentoIsr               IN NUMBER,
    p_DescuentoInasistencias     IN NUMBER,
    p_FechaModificacion          IN TIMESTAMP,
    p_UsuarioModificacion        IN VARCHAR2
)
IS
    v_Transicion VARCHAR2(200);
BEGIN
    PR_EMPLEADO_VALIDAR_MONTOS(p_IngresoSueldoBase, p_IngresoBonificacionDecreto,
        p_IngresoOtrosIngresos, p_DescuentoIgss, p_DescuentoIsr, p_DescuentoInasistencias);

    IF FN_EMPLEADO_TRANSICION_VALIDA(p_IdEmpleado, p_IdStatusEmpleado) = 0 THEN
        v_Transicion := FN_EMPLEADO_DESCRIBIR_TRANSICION(p_IdEmpleado, p_IdStatusEmpleado);
        RAISE_APPLICATION_ERROR(-20122,
            'El cambio de status ' || NVL(v_Transicion, 'solicitado') ||
            ' no esta permitido en el flujo de status configurado.');
    END IF;

    UPDATE EMPLEADO
       SET IdPersona                  = p_IdPersona,
           IdSucursal                 = p_IdSucursal,
           FechaContratacion          = p_FechaContratacion,
           IdPuesto                   = p_IdPuesto,
           IdStatusEmpleado           = p_IdStatusEmpleado,
           IngresoSueldoBase          = p_IngresoSueldoBase,
           IngresoBonificacionDecreto = p_IngresoBonificacionDecreto,
           IngresoOtrosIngresos       = p_IngresoOtrosIngresos,
           DescuentoIgss              = p_DescuentoIgss,
           DescuentoIsr               = p_DescuentoIsr,
           DescuentoInasistencias     = p_DescuentoInasistencias,
           FechaModificacion          = p_FechaModificacion,
           UsuarioModificacion        = p_UsuarioModificacion
     WHERE IdEmpleado = p_IdEmpleado;

    IF SQL%ROWCOUNT = 0 THEN
        RAISE_APPLICATION_ERROR(-20123, 'No existe el empleado que se intenta modificar.');
    END IF;
END;
/

/* Baja con registros relacionados: el bloqueo vive en un trigger
   BEFORE DELETE, siguiendo el patron TRG_<TABLA>_BAJA_VALIDA de D2.
   Asi tambien protege un DELETE hecho por SQL directo, no solo el que
   pasa por el procedimiento. El mensaje dice cuantos hijos hay, como los
   triggers de los catalogos en 05_fase2_endurecimiento.sql.

   OJO: si se vuelve a correr 01_fase2_ddl.sql, el DROP ... CASCADE borra
   este trigger y hay que volver a correr este script. */
CREATE OR REPLACE TRIGGER TRG_EMPLEADO_BAJA_VALIDA
BEFORE DELETE ON EMPLEADO
FOR EACH ROW
DECLARE
    v_Liquidaciones PLS_INTEGER;
    v_Inasistencias PLS_INTEGER;
    v_Cuentas       PLS_INTEGER;
BEGIN
    SELECT COUNT(*) INTO v_Liquidaciones FROM LIQUIDACION WHERE IdEmpleado = :OLD.IdEmpleado;
    IF v_Liquidaciones > 0 THEN
        RAISE_APPLICATION_ERROR(-20124,
            'No se puede eliminar el empleado: ya tiene una liquidacion registrada.');
    END IF;

    SELECT COUNT(*) INTO v_Inasistencias FROM INASISTENCIA WHERE IdEmpleado = :OLD.IdEmpleado;
    IF v_Inasistencias > 0 THEN
        RAISE_APPLICATION_ERROR(-20125,
            'No se puede eliminar el empleado: tiene ' || v_Inasistencias ||
            ' inasistencia(s) registrada(s).');
    END IF;

    SELECT COUNT(*) INTO v_Cuentas FROM CUENTA_BANCARIA_EMPLEADO WHERE IdEmpleado = :OLD.IdEmpleado;
    IF v_Cuentas > 0 THEN
        RAISE_APPLICATION_ERROR(-20126,
            'No se puede eliminar el empleado: tiene ' || v_Cuentas ||
            ' cuenta(s) bancaria(s) asociada(s).');
    END IF;
END;
/

CREATE OR REPLACE PROCEDURE PR_EMPLEADO_ELIMINAR(p_IdEmpleado IN NUMBER)
IS
BEGIN
    -- Las validaciones de hijos las hace TRG_EMPLEADO_BAJA_VALIDA.
    DELETE FROM EMPLEADO WHERE IdEmpleado = p_IdEmpleado;

    IF SQL%ROWCOUNT = 0 THEN
        RAISE_APPLICATION_ERROR(-20127, 'No existe el empleado que se intenta eliminar.');
    END IF;
END;
/

/* Lecturas. Devuelven los nombres ya resueltos para que la cuadricula de
   Angular no tenga que cruzar catalogos por su cuenta. */
CREATE OR REPLACE FUNCTION FN_EMPLEADO_LISTAR RETURN SYS_REFCURSOR
IS
    v_Cursor SYS_REFCURSOR;
BEGIN
    OPEN v_Cursor FOR
        SELECT e.IdEmpleado, e.IdPersona, e.IdSucursal, e.IdPuesto, e.IdStatusEmpleado,
               p.Nombre || ' ' || p.Apellido AS NombrePersona,
               s.Nombre  AS NombreSucursal,
               pu.Nombre AS NombrePuesto,
               d.IdDepartamento,
               d.Nombre  AS NombreDepartamento,
               st.Nombre AS NombreStatus,
               e.FechaContratacion,
               e.IngresoSueldoBase, e.IngresoBonificacionDecreto, e.IngresoOtrosIngresos,
               e.DescuentoIgss, e.DescuentoIsr, e.DescuentoInasistencias,
               e.FechaCreacion, e.UsuarioCreacion, e.FechaModificacion, e.UsuarioModificacion
          FROM EMPLEADO e
          JOIN PERSONA         p  ON p.IdPersona         = e.IdPersona
          JOIN SUCURSAL        s  ON s.IdSucursal        = e.IdSucursal
          JOIN PUESTO          pu ON pu.IdPuesto         = e.IdPuesto
          JOIN DEPARTAMENTO    d  ON d.IdDepartamento    = pu.IdDepartamento
          JOIN STATUS_EMPLEADO st ON st.IdStatusEmpleado = e.IdStatusEmpleado
         ORDER BY p.Apellido, p.Nombre;
    RETURN v_Cursor;
END;
/

CREATE OR REPLACE FUNCTION FN_EMPLEADO_POR_ID(p_IdEmpleado IN NUMBER) RETURN SYS_REFCURSOR
IS
    v_Cursor SYS_REFCURSOR;
BEGIN
    OPEN v_Cursor FOR
        SELECT e.IdEmpleado, e.IdPersona, e.IdSucursal, e.IdPuesto, e.IdStatusEmpleado,
               p.Nombre || ' ' || p.Apellido AS NombrePersona,
               s.Nombre  AS NombreSucursal,
               pu.Nombre AS NombrePuesto,
               d.IdDepartamento,
               d.Nombre  AS NombreDepartamento,
               st.Nombre AS NombreStatus,
               e.FechaContratacion,
               e.IngresoSueldoBase, e.IngresoBonificacionDecreto, e.IngresoOtrosIngresos,
               e.DescuentoIgss, e.DescuentoIsr, e.DescuentoInasistencias,
               e.FechaCreacion, e.UsuarioCreacion, e.FechaModificacion, e.UsuarioModificacion
          FROM EMPLEADO e
          JOIN PERSONA         p  ON p.IdPersona         = e.IdPersona
          JOIN SUCURSAL        s  ON s.IdSucursal        = e.IdSucursal
          JOIN PUESTO          pu ON pu.IdPuesto         = e.IdPuesto
          JOIN DEPARTAMENTO    d  ON d.IdDepartamento    = pu.IdDepartamento
          JOIN STATUS_EMPLEADO st ON st.IdStatusEmpleado = e.IdStatusEmpleado
         WHERE e.IdEmpleado = p_IdEmpleado;
    RETURN v_Cursor;
END;
/

/* Los status a los que ESTE empleado puede moverse ahora mismo.
   Alimenta el combo de status de la pantalla: en vez de ofrecer los 5 y
   rechazar despues, se ofrecen solo los validos. */
CREATE OR REPLACE FUNCTION FN_EMPLEADO_STATUS_PERMITIDOS(p_IdEmpleado IN NUMBER)
RETURN SYS_REFCURSOR
IS
    v_Cursor SYS_REFCURSOR;
BEGIN
    OPEN v_Cursor FOR
        SELECT st.IdStatusEmpleado, st.Nombre, f.NombreEvento
          FROM EMPLEADO e
          JOIN FLUJO_STATUS_EMPLEADO f ON f.IdStatusActual = e.IdStatusEmpleado
          JOIN STATUS_EMPLEADO st      ON st.IdStatusEmpleado = f.IdStatusNuevo
         WHERE e.IdEmpleado = p_IdEmpleado
        UNION ALL
        -- El status actual siempre es una opcion valida (no cambiarlo).
        SELECT st.IdStatusEmpleado, st.Nombre, 'Sin cambio' AS NombreEvento
          FROM EMPLEADO e
          JOIN STATUS_EMPLEADO st ON st.IdStatusEmpleado = e.IdStatusEmpleado
         WHERE e.IdEmpleado = p_IdEmpleado
         ORDER BY 2;
    RETURN v_Cursor;
END;
/


/* Combo de Persona de la pantalla de Empleados.
   PROVISIONAL: la pantalla de Personas es de Andrea (idOpcion 'personas') y
   todavia no tiene endpoint. Esta funcion es solo lectura y se expone en
   GET /api/empleados/personas para no chocar con el /api/personas que ella
   vaya a crear. Cuando su pantalla este lista, el frontend cambia a su
   endpoint y esto se puede borrar. */
CREATE OR REPLACE FUNCTION FN_PERSONA_COMBO RETURN SYS_REFCURSOR
IS
    v_Cursor SYS_REFCURSOR;
BEGIN
    OPEN v_Cursor FOR
        SELECT p.IdPersona,
               p.Nombre || ' ' || p.Apellido AS NombreCompleto
          FROM PERSONA p
         ORDER BY p.Apellido, p.Nombre;
    RETURN v_Cursor;
END;
/


/* ============================================================
   SECCION 2 — LIQUIDACION
   ============================================================
   El calculo vive aqui y no en el Service de Java. Razones al pie.

   Formulas (solo con las columnas que la tabla oficial tiene):

     TotalIngresos   = IngresoSueldoBase + IngresoBonificacionDecreto
                       + IngresoOtrosIngresos
     TotalDescuentos = DescuentoIgss + DescuentoIsr + DescuentoInasistencias
     SalarioNeto     = IngresoSueldoBase - TotalDescuentos
     TotalNeto       = TotalIngresos - TotalDescuentos

   Nota sobre SalarioNeto vs TotalNeto: la tabla oficial guarda los dos y
   no tiene columnas para prestaciones (indemnizacion, aguinaldo, bono 14,
   vacaciones). Para que ambos campos signifiquen algo distinto con el
   esquema tal como esta, se interpreta:
     - SalarioNeto = lo que queda del SUELDO despues de descuentos.
     - TotalNeto   = lo que queda de TODOS los ingresos despues de descuentos,
                     es decir el monto final a pagar.

   DECISION CONFIRMADA con el catedratico: se queda asi. La alternativa era
   que TotalNeto incluyera prestaciones proporcionales calculadas con la
   fecha de contratacion y la de egreso, lo que habria exigido columnas
   nuevas en LIQUIDACION (tabla compartida) para guardar el desglose.
   ============================================================ */

CREATE OR REPLACE PROCEDURE PR_LIQUIDACION_CALCULAR(
    p_IdEmpleado       IN  NUMBER,
    p_FechaEgreso      IN  DATE,
    p_FechaLiquidacion IN  DATE,
    p_MotivoEgreso     IN  VARCHAR2,
    p_FechaCreacion    IN  TIMESTAMP,
    p_UsuarioCreacion  IN  VARCHAR2,
    p_IdLiquidacion    OUT NUMBER
)
IS
    v_FechaContratacion EMPLEADO.FechaContratacion%TYPE;
    v_IdPuesto          EMPLEADO.IdPuesto%TYPE;
    v_SueldoBase        EMPLEADO.IngresoSueldoBase%TYPE;
    v_Bonificacion      EMPLEADO.IngresoBonificacionDecreto%TYPE;
    v_OtrosIngresos     EMPLEADO.IngresoOtrosIngresos%TYPE;
    v_Igss              EMPLEADO.DescuentoIgss%TYPE;
    v_Isr               EMPLEADO.DescuentoIsr%TYPE;
    v_Inasistencias     EMPLEADO.DescuentoInasistencias%TYPE;

    v_TotalIngresos     NUMBER(10,2);
    v_TotalDescuentos   NUMBER(10,2);
    v_SalarioNeto       NUMBER(10,2);
    v_TotalNeto         NUMBER(10,2);

    v_YaLiquidado       PLS_INTEGER;
BEGIN
    -- El Puesto NO se recibe como parametro: se toma del empleado, segun
    -- el enunciado ("el Service obtiene el Puesto del empleado automaticamente").
    BEGIN
        SELECT FechaContratacion, IdPuesto, IngresoSueldoBase,
               IngresoBonificacionDecreto, IngresoOtrosIngresos,
               DescuentoIgss, DescuentoIsr, DescuentoInasistencias
          INTO v_FechaContratacion, v_IdPuesto, v_SueldoBase,
               v_Bonificacion, v_OtrosIngresos,
               v_Igss, v_Isr, v_Inasistencias
          FROM EMPLEADO
         WHERE IdEmpleado = p_IdEmpleado;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            RAISE_APPLICATION_ERROR(-20130, 'No existe el empleado que se intenta liquidar.');
    END;

    -- La liquidacion es historial permanente: una por empleado.
    SELECT COUNT(*) INTO v_YaLiquidado FROM LIQUIDACION WHERE IdEmpleado = p_IdEmpleado;
    IF v_YaLiquidado > 0 THEN
        RAISE_APPLICATION_ERROR(-20131,
            'Este empleado ya tiene una liquidacion registrada.');
    END IF;

    IF p_FechaEgreso < v_FechaContratacion THEN
        RAISE_APPLICATION_ERROR(-20132,
            'La fecha de egreso no puede ser anterior a la fecha de contratacion.');
    END IF;

    IF p_FechaLiquidacion < p_FechaEgreso THEN
        RAISE_APPLICATION_ERROR(-20133,
            'La fecha de liquidacion no puede ser anterior a la fecha de egreso.');
    END IF;

    v_TotalIngresos   := v_SueldoBase + v_Bonificacion + v_OtrosIngresos;
    v_TotalDescuentos := v_Igss + v_Isr + v_Inasistencias;
    v_SalarioNeto     := v_SueldoBase - v_TotalDescuentos;
    v_TotalNeto       := v_TotalIngresos - v_TotalDescuentos;

    INSERT INTO LIQUIDACION (
        IdEmpleado, FechaContratacion, FechaEgreso, FechaLiquidacion, MotivoEgreso,
        IdPuesto,
        IngresoSueldoBase, IngresoBonificacionDecreto, IngresoOtrosIngresos,
        DescuentoIgss, DescuentoIsr, DescuentoInasistencias,
        SalarioNeto, TotalIngresos, TotalDescuentos, TotalNeto,
        FechaCreacion, UsuarioCreacion
    ) VALUES (
        p_IdEmpleado, v_FechaContratacion, p_FechaEgreso, p_FechaLiquidacion, p_MotivoEgreso,
        v_IdPuesto,
        v_SueldoBase, v_Bonificacion, v_OtrosIngresos,
        v_Igss, v_Isr, v_Inasistencias,
        v_SalarioNeto, v_TotalIngresos, v_TotalDescuentos, v_TotalNeto,
        p_FechaCreacion, p_UsuarioCreacion
    )
    RETURNING IdLiquidacion INTO p_IdLiquidacion;
END;
/

CREATE OR REPLACE FUNCTION FN_LIQUIDACION_LISTAR RETURN SYS_REFCURSOR
IS
    v_Cursor SYS_REFCURSOR;
BEGIN
    OPEN v_Cursor FOR
        SELECT l.IdLiquidacion, l.IdEmpleado,
               p.Nombre || ' ' || p.Apellido AS NombrePersona,
               l.IdPuesto, pu.Nombre AS NombrePuesto,
               l.FechaContratacion, l.FechaEgreso, l.FechaLiquidacion, l.MotivoEgreso,
               l.IngresoSueldoBase, l.IngresoBonificacionDecreto, l.IngresoOtrosIngresos,
               l.DescuentoIgss, l.DescuentoIsr, l.DescuentoInasistencias,
               l.SalarioNeto, l.TotalIngresos, l.TotalDescuentos, l.TotalNeto,
               l.FechaCreacion, l.UsuarioCreacion
          FROM LIQUIDACION l
          JOIN EMPLEADO e  ON e.IdEmpleado = l.IdEmpleado
          JOIN PERSONA  p  ON p.IdPersona  = e.IdPersona
          JOIN PUESTO   pu ON pu.IdPuesto  = l.IdPuesto
         ORDER BY l.FechaLiquidacion DESC, l.IdLiquidacion DESC;
    RETURN v_Cursor;
END;
/

CREATE OR REPLACE FUNCTION FN_LIQUIDACION_POR_ID(p_IdLiquidacion IN NUMBER)
RETURN SYS_REFCURSOR
IS
    v_Cursor SYS_REFCURSOR;
BEGIN
    OPEN v_Cursor FOR
        SELECT l.IdLiquidacion, l.IdEmpleado,
               p.Nombre || ' ' || p.Apellido AS NombrePersona,
               l.IdPuesto, pu.Nombre AS NombrePuesto,
               l.FechaContratacion, l.FechaEgreso, l.FechaLiquidacion, l.MotivoEgreso,
               l.IngresoSueldoBase, l.IngresoBonificacionDecreto, l.IngresoOtrosIngresos,
               l.DescuentoIgss, l.DescuentoIsr, l.DescuentoInasistencias,
               l.SalarioNeto, l.TotalIngresos, l.TotalDescuentos, l.TotalNeto,
               l.FechaCreacion, l.UsuarioCreacion
          FROM LIQUIDACION l
          JOIN EMPLEADO e  ON e.IdEmpleado = l.IdEmpleado
          JOIN PERSONA  p  ON p.IdPersona  = e.IdPersona
          JOIN PUESTO   pu ON pu.IdPuesto  = l.IdPuesto
         WHERE l.IdLiquidacion = p_IdLiquidacion;
    RETURN v_Cursor;
END;
/

/* Combo de la pantalla de Liquidacion: solo empleados que todavia pueden
   liquidarse, para no ofrecer gente ya liquidada y tener que rechazarla
   despues. Trae tambien el puesto, que la pantalla muestra en solo lectura. */
CREATE OR REPLACE FUNCTION FN_EMPLEADO_LIQUIDABLES RETURN SYS_REFCURSOR
IS
    v_Cursor SYS_REFCURSOR;
BEGIN
    OPEN v_Cursor FOR
        SELECT e.IdEmpleado,
               p.Nombre || ' ' || p.Apellido AS NombrePersona,
               e.IdPuesto, pu.Nombre AS NombrePuesto,
               e.FechaContratacion,
               e.IngresoSueldoBase, e.IngresoBonificacionDecreto, e.IngresoOtrosIngresos,
               e.DescuentoIgss, e.DescuentoIsr, e.DescuentoInasistencias
          FROM EMPLEADO e
          JOIN PERSONA p  ON p.IdPersona = e.IdPersona
          JOIN PUESTO  pu ON pu.IdPuesto = e.IdPuesto
         WHERE NOT EXISTS (SELECT 1 FROM LIQUIDACION l WHERE l.IdEmpleado = e.IdEmpleado)
         ORDER BY p.Apellido, p.Nombre;
    RETURN v_Cursor;
END;
/


/* ================================================================
   DECISIONES DOCUMENTADAS
   ================================================================
   1. El calculo de la liquidacion esta en PR_LIQUIDACION_CALCULAR y no
      en el Service de Java:
      a) El enunciado lo recomienda y es el mismo enfoque de Calcular
         Planilla (Cristian), asi el modulo queda coherente.
      b) El calculo lee ocho columnas del empleado; hacerlo en la BD
         evita traerlas a Java solo para devolverlas.
      c) Lectura, calculo e insercion ocurren en una sola transaccion:
         no puede quedar una liquidacion con totales a medias.

   2. Validacion de montos no negativos en PR_EMPLEADO_VALIDAR_MONTOS y
      no como CHECK en la tabla, porque EMPLEADO es del DDL comun y no
      se modifica. El efecto practico es el mismo: tampoco se puede
      saltar desde SQL directo.

   3. La transicion de status se valida en DOS lugares a proposito:
      el Service (para el mensaje al usuario) y PR_EMPLEADO_ACTUALIZAR
      (red de seguridad ante llamadas directas a la BD).

   4. Lectura por SYS_REFCURSOR en vez de SELECT embebido en Java, para
      cumplir "toda persistencia usa Stored Procedures".

   5. FN_EMPLEADO_STATUS_PERMITIDOS existe para que el combo de status
      ofrezca solo transiciones validas. Prevenir en la interfaz es
      mejor experiencia que rechazar despues, pero la validacion del
      punto 3 se mantiene igual.
   ================================================================ */
