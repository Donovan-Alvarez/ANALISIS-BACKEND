/*
 * FASE 2 - Modulo de Planilla
 * 07_fase2_personas_documentos.sql
 *
 * Responsable: Andrea (opciones 'personas' y 'documentos-persona').
 *
 * Que hace (no toca datos: ningun INSERT/UPDATE/DELETE de filas):
 *   a) Confirma que PERSONA tiene las FK a GENERO y a ESTADO_CIVIL (solo lectura).
 *   b) Garantiza que (IdPersona, IdTipoDocumento) sea unico en DOCUMENTO_PERSONA.
 *      01_fase2_ddl.sql ya lo cumple con la PK compuesta PK_DOCUMENTO_PERSONA
 *      (IdTipoDocumento, IdPersona); este bloque solo agrega un UNIQUE si por
 *      cualquier razon esa PK/UNIQUE no existe. En una instalacion normal avisa
 *      "ya existe: sin cambios".
 *   c) TRG_PERSONA_BAJA_VALIDA (BEFORE DELETE ON PERSONA): impide dar de baja una
 *      persona que todavia es empleado o tiene documentos, con un mensaje claro
 *      (D2). GlobalExceptionHandler lo devuelve como 409 sin ORA-xxxxx.
 *
 * Codigos usados (rango de Andrea, -20180..-20199):
 *   -20180  PERSONA <- EMPLEADO.IdPersona
 *   -20181  PERSONA <- DOCUMENTO_PERSONA.IdPersona
 *   (-20182..-20199 libres)
 *
 * Orden: despues de 01-04 (y de 05 si ya se corrio). Es idempotente:
 * CREATE OR REPLACE TRIGGER + UNIQUE condicionado.
 *
 * IMPORTANTE: 01_fase2_ddl.sql hace DROP TABLE ... CASCADE CONSTRAINTS, que tambien
 * borra este trigger. Si se vuelve a correr 01, hay que volver a correr este 07.
 *
 * Uso: ./database/run-sql.sh database/07_fase2_personas_documentos.sql
 */

SET SERVEROUTPUT ON

-- =====================================================================
-- a) FK de PERSONA (solo lectura). Deben salir 2 filas:
--    FK_PERSONA_GENERO -> GENERO y FK_PERSONA_ESTADO_CIVIL -> ESTADO_CIVIL
-- =====================================================================
COLUMN constraint_name FORMAT A28
COLUMN tabla_referenciada FORMAT A20
SELECT c.constraint_name, p.table_name AS tabla_referenciada, c.status
FROM user_constraints c
JOIN user_constraints p ON p.constraint_name = c.r_constraint_name
WHERE c.table_name = 'PERSONA' AND c.constraint_type = 'R'
ORDER BY c.constraint_name;

-- =====================================================================
-- b) Unicidad Persona + Tipo de documento
-- =====================================================================
DECLARE
    v_Existe PLS_INTEGER;
BEGIN
    -- Cuenta PK/UNIQUE de DOCUMENTO_PERSONA cuyas columnas sean exactamente
    -- {IDPERSONA, IDTIPODOCUMENTO}, en cualquier orden.
    SELECT COUNT(*) INTO v_Existe
    FROM (
        SELECT c.constraint_name
        FROM user_constraints c
        JOIN user_cons_columns cc ON cc.constraint_name = c.constraint_name
        WHERE c.table_name = 'DOCUMENTO_PERSONA'
          AND c.constraint_type IN ('P', 'U')
        GROUP BY c.constraint_name
        HAVING LISTAGG(cc.column_name, ',') WITHIN GROUP (ORDER BY cc.column_name) = 'IDPERSONA,IDTIPODOCUMENTO'
    );

    IF v_Existe = 0 THEN
        EXECUTE IMMEDIATE 'ALTER TABLE DOCUMENTO_PERSONA ADD CONSTRAINT UQ_DOCUMENTO_PERSONA_PERSONA_TIPO UNIQUE (IdPersona, IdTipoDocumento)';
        DBMS_OUTPUT.PUT_LINE('UQ_DOCUMENTO_PERSONA_PERSONA_TIPO creado.');
    ELSE
        DBMS_OUTPUT.PUT_LINE('Persona + Tipo de documento ya es unico (PK/UNIQUE existente): sin cambios.');
    END IF;
END;
/

-- =====================================================================
-- c) Trigger de baja de PERSONA
-- =====================================================================
CREATE OR REPLACE TRIGGER TRG_PERSONA_BAJA_VALIDA
BEFORE DELETE ON PERSONA
FOR EACH ROW
DECLARE
    v_Count PLS_INTEGER;
BEGIN
    SELECT COUNT(*) INTO v_Count FROM EMPLEADO WHERE IdPersona = :OLD.IdPersona;
    IF v_Count > 0 THEN
        RAISE_APPLICATION_ERROR(-20180, 'No se puede eliminar la persona: está registrada como empleado (' || v_Count || ' registro(s)).');
    END IF;

    SELECT COUNT(*) INTO v_Count FROM DOCUMENTO_PERSONA WHERE IdPersona = :OLD.IdPersona;
    IF v_Count > 0 THEN
        RAISE_APPLICATION_ERROR(-20181, 'No se puede eliminar la persona: tiene ' || v_Count || ' documento(s) registrado(s). Elimínalos primero.');
    END IF;
END;
/

-- =====================================================================
-- Verificacion (solo lectura). Esperado: 1 fila, ENABLED y VALID.
-- =====================================================================
COLUMN trigger_name FORMAT A30
SELECT t.trigger_name, t.table_name, t.status, o.status AS compilado
FROM user_triggers t
JOIN user_objects o ON o.object_name = t.trigger_name AND o.object_type = 'TRIGGER'
WHERE t.trigger_name = 'TRG_PERSONA_BAJA_VALIDA';
