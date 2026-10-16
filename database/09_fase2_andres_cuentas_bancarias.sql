/*
 * FASE 2 - Modulo de Planilla
 * 09_fase2_andres_cuentas_bancarias.sql
 *
 * Responsable: Andres (opciones 'cuentas-bancarias' y 'reporte-planilla').
 *
 * Que hace (no toca datos: ningun INSERT/UPDATE/DELETE de filas):
 *   a) UQ_CUENTA_BANCARIA_EMPLEADO_NUMERO: un mismo numero de cuenta no puede
 *      repetirse dentro del mismo banco (una cuenta es de un solo dueno).
 *   b) UQ_CUENTA_BANCARIA_EMPLEADO_ACTIVA: un empleado no puede tener dos
 *      cuentas ACTIVAS del mismo banco. Indice unico basado en funcion: las
 *      filas con Activa = 0 dan (NULL, NULL) y Oracle no las indexa, asi que
 *      las cuentas inactivas no cuentan para la regla.
 *
 * Las dos reglas tambien las valida CuentaBancariaEmpleadoService con un
 * mensaje claro (D3); estos indices son la garantia ante dos peticiones
 * simultaneas (si la BD rechaza, GlobalExceptionHandler responde 409).
 *
 * Reporte de Planilla no necesita objetos de BD: solo lee PLANILLA_CABECERA
 * y PLANILLA_DETALLE.
 *
 * Codigos usados (rango de Andres, -20160..-20179):
 *   -20160  hay numeros de cuenta repetidos en un mismo banco (guarda de a)
 *   -20161  hay empleados con dos cuentas activas del mismo banco (guarda de b)
 *   (-20162..-20179 libres)
 * Si una guarda salta, el script aborta sin crear el indice: corrija los
 * datos duplicados y vuelva a correrlo.
 *
 * Orden: despues de 01-08. Es idempotente (cada indice se crea solo si no
 * existe). Si se vuelve a correr 01_fase2_ddl.sql, hay que volver a correr
 * este archivo (el DROP TABLE borra los indices).
 *
 * Uso: ./database/run-sql.sh database/09_fase2_andres_cuentas_bancarias.sql
 */

SET SERVEROUTPUT ON

-- =====================================================================
-- a) Numero de cuenta unico por banco
-- =====================================================================
DECLARE
    v_existe     NUMBER;
    v_duplicados NUMBER;
BEGIN
    SELECT COUNT(*) INTO v_existe
    FROM USER_INDEXES
    WHERE INDEX_NAME = 'UQ_CUENTA_BANCARIA_EMPLEADO_NUMERO';

    IF v_existe > 0 THEN
        DBMS_OUTPUT.PUT_LINE('UQ_CUENTA_BANCARIA_EMPLEADO_NUMERO ya existe: sin cambios.');
        RETURN;
    END IF;

    SELECT COUNT(*) INTO v_duplicados
    FROM (
        SELECT IdBanco, NumeroDeCuenta
        FROM CUENTA_BANCARIA_EMPLEADO
        GROUP BY IdBanco, NumeroDeCuenta
        HAVING COUNT(*) > 1
    );

    IF v_duplicados > 0 THEN
        RAISE_APPLICATION_ERROR(-20160,
            'Hay ' || v_duplicados || ' numero(s) de cuenta repetido(s) en un mismo banco. Corrijalos antes de crear el indice.');
    END IF;

    EXECUTE IMMEDIATE
        'CREATE UNIQUE INDEX UQ_CUENTA_BANCARIA_EMPLEADO_NUMERO ON CUENTA_BANCARIA_EMPLEADO (IdBanco, NumeroDeCuenta)';
    DBMS_OUTPUT.PUT_LINE('UQ_CUENTA_BANCARIA_EMPLEADO_NUMERO creado.');
END;
/

-- =====================================================================
-- b) Una sola cuenta activa por empleado y banco
-- =====================================================================
DECLARE
    v_existe     NUMBER;
    v_duplicados NUMBER;
BEGIN
    SELECT COUNT(*) INTO v_existe
    FROM USER_INDEXES
    WHERE INDEX_NAME = 'UQ_CUENTA_BANCARIA_EMPLEADO_ACTIVA';

    IF v_existe > 0 THEN
        DBMS_OUTPUT.PUT_LINE('UQ_CUENTA_BANCARIA_EMPLEADO_ACTIVA ya existe: sin cambios.');
        RETURN;
    END IF;

    SELECT COUNT(*) INTO v_duplicados
    FROM (
        SELECT IdEmpleado, IdBanco
        FROM CUENTA_BANCARIA_EMPLEADO
        WHERE Activa = 1
        GROUP BY IdEmpleado, IdBanco
        HAVING COUNT(*) > 1
    );

    IF v_duplicados > 0 THEN
        RAISE_APPLICATION_ERROR(-20161,
            'Hay ' || v_duplicados || ' empleado(s) con mas de una cuenta activa en el mismo banco. Desactive las sobrantes antes de crear el indice.');
    END IF;

    EXECUTE IMMEDIATE
        'CREATE UNIQUE INDEX UQ_CUENTA_BANCARIA_EMPLEADO_ACTIVA ON CUENTA_BANCARIA_EMPLEADO ('
        || 'CASE WHEN Activa = 1 THEN IdEmpleado END, '
        || 'CASE WHEN Activa = 1 THEN IdBanco END)';
    DBMS_OUTPUT.PUT_LINE('UQ_CUENTA_BANCARIA_EMPLEADO_ACTIVA creado.');
END;
/

-- =====================================================================
-- Verificacion (solo lectura): deben salir los dos indices, UNIQUE
-- =====================================================================
COLUMN index_name FORMAT A40
SELECT index_name, uniqueness, status
FROM USER_INDEXES
WHERE TABLE_NAME = 'CUENTA_BANCARIA_EMPLEADO'
ORDER BY index_name;
