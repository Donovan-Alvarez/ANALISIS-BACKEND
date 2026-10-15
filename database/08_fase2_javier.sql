-- Javier Moscoso: ORA-20200..20219. Ejecutar después de 01..07 y antes de 99.
-- No COMMIT en procedimientos: la transacción la administra Spring.
SET DEFINE OFF;
CREATE OR REPLACE PROCEDURE PR_FLUJO_INSERTAR(
 p_actual NUMBER,p_nuevo NUMBER,p_evento VARCHAR2,p_fecha DATE,p_usuario VARCHAR2) AS
BEGIN
 IF p_actual=p_nuevo THEN RAISE_APPLICATION_ERROR(-20200,'El status actual y el nuevo deben ser distintos.'); END IF;
 INSERT INTO FLUJO_STATUS_EMPLEADO(IdStatusActual,IdStatusNuevo,NombreEvento,FechaCreacion,UsuarioCreacion)
 VALUES(p_actual,p_nuevo,p_evento,p_fecha,p_usuario);
END;
/
CREATE OR REPLACE PROCEDURE PR_FLUJO_ACTUALIZAR(
 p_actual NUMBER,p_nuevo NUMBER,p_actual_nuevo NUMBER,p_nuevo_nuevo NUMBER,
 p_evento VARCHAR2,p_fecha DATE,p_usuario VARCHAR2) AS
BEGIN
 IF p_actual_nuevo=p_nuevo_nuevo THEN RAISE_APPLICATION_ERROR(-20200,'El status actual y el nuevo deben ser distintos.'); END IF;
 UPDATE FLUJO_STATUS_EMPLEADO SET IdStatusActual=p_actual_nuevo,IdStatusNuevo=p_nuevo_nuevo,
 NombreEvento=p_evento,FechaModificacion=p_fecha,UsuarioModificacion=p_usuario
 WHERE IdStatusActual=p_actual AND IdStatusNuevo=p_nuevo;
 IF SQL%ROWCOUNT=0 THEN RAISE_APPLICATION_ERROR(-20201,'La transición ya no existe.'); END IF;
END;
/
CREATE OR REPLACE PROCEDURE PR_FLUJO_ELIMINAR(p_actual NUMBER,p_nuevo NUMBER) AS
BEGIN
 DELETE FROM FLUJO_STATUS_EMPLEADO WHERE IdStatusActual=p_actual AND IdStatusNuevo=p_nuevo;
 IF SQL%ROWCOUNT=0 THEN RAISE_APPLICATION_ERROR(-20201,'La transición ya no existe.'); END IF;
END;
/
-- 02 ya carga las nueve transiciones, incluidas las tres del enunciado.
-- Este archivo no duplica ni cambia los seeds.
SELECT object_name,status FROM user_objects
WHERE object_name IN ('PR_FLUJO_INSERTAR','PR_FLUJO_ACTUALIZAR','PR_FLUJO_ELIMINAR');
SELECT name,line,text FROM user_errors
WHERE name IN ('PR_FLUJO_INSERTAR','PR_FLUJO_ACTUALIZAR','PR_FLUJO_ELIMINAR');
