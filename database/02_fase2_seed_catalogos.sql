/*
 * FASE 2 - Modulo de Planilla
 * 02_fase2_seed_catalogos.sql
 *
 * Seed de los catalogos de Planilla:
 *   STATUS_EMPLEADO, FLUJO_STATUS_EMPLEADO, TIPO_DOCUMENTO, BANCO,
 *   ESTADO_CIVIL, DEPARTAMENTO, PUESTO, PERIODO_PLANILLA.
 * El orden del archivo (mismo que el original en MySQL) importa por el
 * orden de dependencia entre catalogos (STATUS_EMPLEADO antes de
 * FLUJO_STATUS_EMPLEADO, DEPARTAMENTO antes de PUESTO) - NO por los ids
 * que vaya a asignar el IDENTITY, que ya no le importan a nadie (ver
 * AJUSTE Paso A4 mas abajo).
 *
 * Requiere haber corrido 01_fase2_ddl.sql antes (tablas vacias).
 * NO es idempotente: correrlo dos veces sin volver a correr el DDL duplica
 * filas (igual que el patron de 01-schema-completo.sql en Fase 1).
 *
 * AJUSTE Paso A3: DEPARTAMENTO ya no lleva el literal IdEmpresa = 1; lleva
 * (SELECT MIN(IdEmpresa) FROM EMPRESA), por el mismo motivo que el ajuste
 * de GENERO/SUCURSAL en 03_fase2_seed_personas_empleados.sql (ids reales
 * distintos a los asumidos). Fase 1 siembra una sola EMPRESA ('Software
 * Inc.', database/01-schema-completo.sql:21-33), asi que el MIN() da el
 * mismo resultado en una BD sin empresas adicionales. Se verifico que el
 * UNIQUE(IdEmpresa, Nombre) de DEPARTAMENTO no se ve afectado: las 13
 * filas comparten el mismo valor de IdEmpresa (la subconsulta es
 * constante dentro de esta misma corrida) y los 13 Nombre siguen siendo
 * unicos entre si (ver chequeo de duplicados en el informe del paso).
 *
 * SI FALLA CUALQUIER SEED DE ESTE PAQUETE (01/02/03/04), VUELVE A EMPEZAR
 * DESDE 01 - no corras 02 o 03 sueltos sobre tablas que no se recrearon.
 *
 * ================================================================
 * AJUSTE Paso A4: ninguna FK de este seed depende ya de que un IDENTITY
 * haya arrancado en 1
 * ================================================================
 * El comentario original de este archivo decia que el orden de insercion
 * "importaba" para que los IDENTITY generaran los mismos ids que
 * FLUJO_STATUS_EMPLEADO y PUESTO asumian como literales. Eso ya no es
 * cierto: FLUJO_STATUS_EMPLEADO.IdStatusActual/IdStatusNuevo y
 * PUESTO.IdDepartamento ahora se resuelven por Nombre, con una
 * subconsulta, igual que el ajuste que ya se le hizo a GENERO/SUCURSAL/
 * EMPRESA/IdEmpresa en el Paso A3. El orden del archivo sigue
 * importando, pero solo por dependencia entre catalogos (no puedes
 * resolver PUESTO.IdDepartamento por nombre si DEPARTAMENTO todavia no
 * tiene esa fila), no porque el numero de identity tenga que ser 1, 2, 3...
 *
 * Se verifico que STATUS_EMPLEADO.Nombre, DEPARTAMENTO.Nombre y
 * PUESTO.Nombre son unicos en todo el catalogo (no solo contra su padre
 * directo), asi que ninguna subconsulta necesito desambiguarse con un
 * segundo criterio ni con posicion ordinal - ver la tabla de cambios en
 * docs/fase2/informes/paso-A4-fks-por-nombre.md.
 * ================================================================
 *
 * AJUSTE Paso A6: 'BANCO G&T CONTINENTAL' ya no lleva el caracter "&"
 * literal en el texto del script - se escribe como
 * 'BANCO G' || CHR(38) || 'T CONTINENTAL' (CHR(38) es el "&" en ASCII).
 * Motivo: aunque SET DEFINE OFF ya evita que SQL*Plus lo trate como
 * variable de sustitucion (y asi se ejecuto sin error via sqlplus en el
 * Paso A5), SET DEFINE OFF es una directiva propia de SQL*Plus que
 * clientes como DBeaver no interpretan - si DBeaver tiene su propia
 * sustitucion de parametros activada, el "&" crudo puede romper el
 * script igual (ver diagnostico del ORA-00900 en
 * docs/fase2/informes/paso-A5-ejecucion.md). Con CHR(38) el caracter
 * nunca aparece literal en el texto del .sql, asi que ningun cliente
 * (SQL*Plus, DBeaver o cualquier otro) tiene nada que sustituir. NO se
 * volvio a ejecutar este archivo despues de este cambio en este paso -
 * ver el informe para el plan de re-ejecucion.
 *
 * NO EJECUTAR sin autorizacion. Este archivo es un borrador.
 */

-- =====================================================================
-- STATUS_EMPLEADO (5 filas) - IdStatusEmpleado esperado: 1..5
-- Nombres iguales al original (sin typos que corregir aqui).
-- =====================================================================
INSERT INTO STATUS_EMPLEADO (Nombre, FechaCreacion, UsuarioCreacion) VALUES ('Activo', SYSTIMESTAMP, 'system');
INSERT INTO STATUS_EMPLEADO (Nombre, FechaCreacion, UsuarioCreacion) VALUES ('Suspendido por el IGSS', SYSTIMESTAMP, 'system');
INSERT INTO STATUS_EMPLEADO (Nombre, FechaCreacion, UsuarioCreacion) VALUES ('Suspendido por RRHH', SYSTIMESTAMP, 'system');
INSERT INTO STATUS_EMPLEADO (Nombre, FechaCreacion, UsuarioCreacion) VALUES ('Baja', SYSTIMESTAMP, 'system');
INSERT INTO STATUS_EMPLEADO (Nombre, FechaCreacion, UsuarioCreacion) VALUES ('Despedido', SYSTIMESTAMP, 'system');


-- =====================================================================
-- FLUJO_STATUS_EMPLEADO (9 filas) - depende de STATUS_EMPLEADO
--
-- NOTA: el texto 'Suspencion' (sin la 'd') es un typo del script original
-- en NombreEvento (filas 1 y 2). Se preserva TAL CUAL por instruccion
-- explicita de este paso ("solo repórtalo, no lo cambies - se coordina con
-- Javier"). Ver REPORTE_FASE2_PASOA_TRADUCCION_SQL.md, punto "Problemas
-- conocidos".
-- =====================================================================
INSERT INTO FLUJO_STATUS_EMPLEADO (IdStatusActual, IdStatusNuevo, NombreEvento, FechaCreacion, UsuarioCreacion) VALUES ((SELECT IdStatusEmpleado FROM STATUS_EMPLEADO WHERE Nombre = 'Activo'), (SELECT IdStatusEmpleado FROM STATUS_EMPLEADO WHERE Nombre = 'Suspendido por el IGSS'), 'Suspencion IGSS', SYSTIMESTAMP, 'system');
INSERT INTO FLUJO_STATUS_EMPLEADO (IdStatusActual, IdStatusNuevo, NombreEvento, FechaCreacion, UsuarioCreacion) VALUES ((SELECT IdStatusEmpleado FROM STATUS_EMPLEADO WHERE Nombre = 'Activo'), (SELECT IdStatusEmpleado FROM STATUS_EMPLEADO WHERE Nombre = 'Suspendido por RRHH'), 'Suspencion del Empleado', SYSTIMESTAMP, 'system');
INSERT INTO FLUJO_STATUS_EMPLEADO (IdStatusActual, IdStatusNuevo, NombreEvento, FechaCreacion, UsuarioCreacion) VALUES ((SELECT IdStatusEmpleado FROM STATUS_EMPLEADO WHERE Nombre = 'Activo'), (SELECT IdStatusEmpleado FROM STATUS_EMPLEADO WHERE Nombre = 'Baja'), 'Renuncia del Empleado', SYSTIMESTAMP, 'system');
INSERT INTO FLUJO_STATUS_EMPLEADO (IdStatusActual, IdStatusNuevo, NombreEvento, FechaCreacion, UsuarioCreacion) VALUES ((SELECT IdStatusEmpleado FROM STATUS_EMPLEADO WHERE Nombre = 'Activo'), (SELECT IdStatusEmpleado FROM STATUS_EMPLEADO WHERE Nombre = 'Despedido'), 'Despedir Empleado', SYSTIMESTAMP, 'system');
INSERT INTO FLUJO_STATUS_EMPLEADO (IdStatusActual, IdStatusNuevo, NombreEvento, FechaCreacion, UsuarioCreacion) VALUES ((SELECT IdStatusEmpleado FROM STATUS_EMPLEADO WHERE Nombre = 'Suspendido por el IGSS'), (SELECT IdStatusEmpleado FROM STATUS_EMPLEADO WHERE Nombre = 'Activo'), 'Reintegracion del Empleado IGSS', SYSTIMESTAMP, 'system');
INSERT INTO FLUJO_STATUS_EMPLEADO (IdStatusActual, IdStatusNuevo, NombreEvento, FechaCreacion, UsuarioCreacion) VALUES ((SELECT IdStatusEmpleado FROM STATUS_EMPLEADO WHERE Nombre = 'Suspendido por RRHH'), (SELECT IdStatusEmpleado FROM STATUS_EMPLEADO WHERE Nombre = 'Activo'), 'Reintegracion del Empleado RH', SYSTIMESTAMP, 'system');
INSERT INTO FLUJO_STATUS_EMPLEADO (IdStatusActual, IdStatusNuevo, NombreEvento, FechaCreacion, UsuarioCreacion) VALUES ((SELECT IdStatusEmpleado FROM STATUS_EMPLEADO WHERE Nombre = 'Baja'), (SELECT IdStatusEmpleado FROM STATUS_EMPLEADO WHERE Nombre = 'Activo'), 'Recontratacion del Empleado', SYSTIMESTAMP, 'system');
INSERT INTO FLUJO_STATUS_EMPLEADO (IdStatusActual, IdStatusNuevo, NombreEvento, FechaCreacion, UsuarioCreacion) VALUES ((SELECT IdStatusEmpleado FROM STATUS_EMPLEADO WHERE Nombre = 'Suspendido por RRHH'), (SELECT IdStatusEmpleado FROM STATUS_EMPLEADO WHERE Nombre = 'Baja'), 'Empleado Suspendido y Renuncia del Empleado', SYSTIMESTAMP, 'system');
INSERT INTO FLUJO_STATUS_EMPLEADO (IdStatusActual, IdStatusNuevo, NombreEvento, FechaCreacion, UsuarioCreacion) VALUES ((SELECT IdStatusEmpleado FROM STATUS_EMPLEADO WHERE Nombre = 'Suspendido por RRHH'), (SELECT IdStatusEmpleado FROM STATUS_EMPLEADO WHERE Nombre = 'Despedido'), 'Empleado Suspendido y Despido del Empleado', SYSTIMESTAMP, 'system');


-- =====================================================================
-- TIPO_DOCUMENTO (5 filas) - IdTipoDocumento esperado: 1..5
-- =====================================================================
INSERT INTO TIPO_DOCUMENTO (Nombre, FechaCreacion, UsuarioCreacion) VALUES ('Documento Personal de Identificacion (DPI)', SYSTIMESTAMP, 'system');
INSERT INTO TIPO_DOCUMENTO (Nombre, FechaCreacion, UsuarioCreacion) VALUES ('Pasaporte', SYSTIMESTAMP, 'system');
INSERT INTO TIPO_DOCUMENTO (Nombre, FechaCreacion, UsuarioCreacion) VALUES ('NIT', SYSTIMESTAMP, 'system');
INSERT INTO TIPO_DOCUMENTO (Nombre, FechaCreacion, UsuarioCreacion) VALUES ('Licencia de Conducir', SYSTIMESTAMP, 'system');
INSERT INTO TIPO_DOCUMENTO (Nombre, FechaCreacion, UsuarioCreacion) VALUES ('Seguro Social IGSS', SYSTIMESTAMP, 'system');


-- =====================================================================
-- BANCO (4 filas) - IdBanco esperado: 1..4
-- =====================================================================
INSERT INTO BANCO (Nombre, FechaCreacion, UsuarioCreacion) VALUES ('BANCO INDUSTRIAL', SYSTIMESTAMP, 'system');
INSERT INTO BANCO (Nombre, FechaCreacion, UsuarioCreacion) VALUES ('BANCO RURAL', SYSTIMESTAMP, 'system');
INSERT INTO BANCO (Nombre, FechaCreacion, UsuarioCreacion) VALUES ('BANCO G' || CHR(38) || 'T CONTINENTAL', SYSTIMESTAMP, 'system');
INSERT INTO BANCO (Nombre, FechaCreacion, UsuarioCreacion) VALUES ('BANCO CHN', SYSTIMESTAMP, 'system');


-- =====================================================================
-- ESTADO_CIVIL (5 filas) - IdEstadoCivil esperado: 1..5
-- =====================================================================
INSERT INTO ESTADO_CIVIL (Nombre, FechaCreacion, UsuarioCreacion) VALUES ('Casado(a)', SYSTIMESTAMP, 'system');
INSERT INTO ESTADO_CIVIL (Nombre, FechaCreacion, UsuarioCreacion) VALUES ('Soltero(a)', SYSTIMESTAMP, 'system');
INSERT INTO ESTADO_CIVIL (Nombre, FechaCreacion, UsuarioCreacion) VALUES ('Divorciado(a)', SYSTIMESTAMP, 'system');
INSERT INTO ESTADO_CIVIL (Nombre, FechaCreacion, UsuarioCreacion) VALUES ('Viudo(a)', SYSTIMESTAMP, 'system');
INSERT INTO ESTADO_CIVIL (Nombre, FechaCreacion, UsuarioCreacion) VALUES ('Union de hecho', SYSTIMESTAMP, 'system');


-- =====================================================================
-- DEPARTAMENTO (13 filas) - IdDepartamento esperado: 1..13
-- IdEmpresa = 1 asume que EMPRESA.IdEmpresa = 1 existe (confirmar con el
-- SELECT de solo lectura de la Parte 1.3 antes de ejecutar).
--
-- PROPUESTO (pendiente de aprobacion): se corrigen acentos en todos los
-- nombres, no solo en 'Administraci�n' (el unico que llego con el caracter
-- corrupto "�" de verdad). El resto (Investigacion, Logistica, Produccion,
-- Tecnologias, Informacion) estaban mal escritos en el original SIN el
-- caracter de corrupcion -  simplemente sin tilde - y se corrigen por
-- pedido explicito de este paso.
-- =====================================================================
INSERT INTO DEPARTAMENTO (Nombre, IdEmpresa, FechaCreacion, UsuarioCreacion) VALUES ('Administración', (SELECT MIN(IdEmpresa) FROM EMPRESA), SYSTIMESTAMP, 'system');
INSERT INTO DEPARTAMENTO (Nombre, IdEmpresa, FechaCreacion, UsuarioCreacion) VALUES ('Calidad y Control de Procesos', (SELECT MIN(IdEmpresa) FROM EMPRESA), SYSTIMESTAMP, 'system');
INSERT INTO DEPARTAMENTO (Nombre, IdEmpresa, FechaCreacion, UsuarioCreacion) VALUES ('Compras', (SELECT MIN(IdEmpresa) FROM EMPRESA), SYSTIMESTAMP, 'system');
INSERT INTO DEPARTAMENTO (Nombre, IdEmpresa, FechaCreacion, UsuarioCreacion) VALUES ('Comunicaciones Corporativas', (SELECT MIN(IdEmpresa) FROM EMPRESA), SYSTIMESTAMP, 'system');
INSERT INTO DEPARTAMENTO (Nombre, IdEmpresa, FechaCreacion, UsuarioCreacion) VALUES ('Finanzas', (SELECT MIN(IdEmpresa) FROM EMPRESA), SYSTIMESTAMP, 'system');
INSERT INTO DEPARTAMENTO (Nombre, IdEmpresa, FechaCreacion, UsuarioCreacion) VALUES ('Investigación y Desarrollo (I+D)', (SELECT MIN(IdEmpresa) FROM EMPRESA), SYSTIMESTAMP, 'system');
INSERT INTO DEPARTAMENTO (Nombre, IdEmpresa, FechaCreacion, UsuarioCreacion) VALUES ('Legal', (SELECT MIN(IdEmpresa) FROM EMPRESA), SYSTIMESTAMP, 'system');
INSERT INTO DEPARTAMENTO (Nombre, IdEmpresa, FechaCreacion, UsuarioCreacion) VALUES ('Logística', (SELECT MIN(IdEmpresa) FROM EMPRESA), SYSTIMESTAMP, 'system');
INSERT INTO DEPARTAMENTO (Nombre, IdEmpresa, FechaCreacion, UsuarioCreacion) VALUES ('Producción', (SELECT MIN(IdEmpresa) FROM EMPRESA), SYSTIMESTAMP, 'system');
INSERT INTO DEPARTAMENTO (Nombre, IdEmpresa, FechaCreacion, UsuarioCreacion) VALUES ('Servicio al Cliente', (SELECT MIN(IdEmpresa) FROM EMPRESA), SYSTIMESTAMP, 'system');
INSERT INTO DEPARTAMENTO (Nombre, IdEmpresa, FechaCreacion, UsuarioCreacion) VALUES ('Tecnologías de la Información (IT)', (SELECT MIN(IdEmpresa) FROM EMPRESA), SYSTIMESTAMP, 'system');
INSERT INTO DEPARTAMENTO (Nombre, IdEmpresa, FechaCreacion, UsuarioCreacion) VALUES ('Ventas y Marketing', (SELECT MIN(IdEmpresa) FROM EMPRESA), SYSTIMESTAMP, 'system');
INSERT INTO DEPARTAMENTO (Nombre, IdEmpresa, FechaCreacion, UsuarioCreacion) VALUES ('Recursos Humanos', (SELECT MIN(IdEmpresa) FROM EMPRESA), SYSTIMESTAMP, 'system');


-- =====================================================================
-- PUESTO (29 filas) - IdPuesto esperado: 1..29
-- IdDepartamento literal: depende de que DEPARTAMENTO se haya insertado
-- arriba, EN ESTE MISMO ORDEN, para que 1..13 sean los ids correctos
-- (igual dependencia que ya tenia el script original en MySQL).
--
-- PROPUESTO (pendiente de aprobacion):
--   - TRIM: el original trae un espacio final en TODOS los nombres
--     (p. ej. 'Administrador de Bases de Datos ') - se quita aqui.
--   - Acentos corregidos (Tecnología, Logística, Pública, Informática,
--     Producción) igual que en DEPARTAMENTO.
-- =====================================================================
INSERT INTO PUESTO (Nombre, IdDepartamento, FechaCreacion, UsuarioCreacion) VALUES ('Administrador de Bases de Datos', (SELECT IdDepartamento FROM DEPARTAMENTO WHERE Nombre = 'Tecnologías de la Información (IT)'), SYSTIMESTAMP, 'system');
INSERT INTO PUESTO (Nombre, IdDepartamento, FechaCreacion, UsuarioCreacion) VALUES ('Analista de Datos', (SELECT IdDepartamento FROM DEPARTAMENTO WHERE Nombre = 'Tecnologías de la Información (IT)'), SYSTIMESTAMP, 'system');
INSERT INTO PUESTO (Nombre, IdDepartamento, FechaCreacion, UsuarioCreacion) VALUES ('Analista de Recursos Humanos', (SELECT IdDepartamento FROM DEPARTAMENTO WHERE Nombre = 'Recursos Humanos'), SYSTIMESTAMP, 'system');
INSERT INTO PUESTO (Nombre, IdDepartamento, FechaCreacion, UsuarioCreacion) VALUES ('Analista de Sistemas', (SELECT IdDepartamento FROM DEPARTAMENTO WHERE Nombre = 'Tecnologías de la Información (IT)'), SYSTIMESTAMP, 'system');
INSERT INTO PUESTO (Nombre, IdDepartamento, FechaCreacion, UsuarioCreacion) VALUES ('Analista Financiero', (SELECT IdDepartamento FROM DEPARTAMENTO WHERE Nombre = 'Finanzas'), SYSTIMESTAMP, 'system');
INSERT INTO PUESTO (Nombre, IdDepartamento, FechaCreacion, UsuarioCreacion) VALUES ('Arquitecto de Sistemas', (SELECT IdDepartamento FROM DEPARTAMENTO WHERE Nombre = 'Tecnologías de la Información (IT)'), SYSTIMESTAMP, 'system');
INSERT INTO PUESTO (Nombre, IdDepartamento, FechaCreacion, UsuarioCreacion) VALUES ('Asesor Comercial', (SELECT IdDepartamento FROM DEPARTAMENTO WHERE Nombre = 'Servicio al Cliente'), SYSTIMESTAMP, 'system');
INSERT INTO PUESTO (Nombre, IdDepartamento, FechaCreacion, UsuarioCreacion) VALUES ('Asistente Administrativo', (SELECT IdDepartamento FROM DEPARTAMENTO WHERE Nombre = 'Administración'), SYSTIMESTAMP, 'system');
INSERT INTO PUESTO (Nombre, IdDepartamento, FechaCreacion, UsuarioCreacion) VALUES ('Asistente de Recursos Humanos', (SELECT IdDepartamento FROM DEPARTAMENTO WHERE Nombre = 'Recursos Humanos'), SYSTIMESTAMP, 'system');
INSERT INTO PUESTO (Nombre, IdDepartamento, FechaCreacion, UsuarioCreacion) VALUES ('Consultor de Tecnología', (SELECT IdDepartamento FROM DEPARTAMENTO WHERE Nombre = 'Tecnologías de la Información (IT)'), SYSTIMESTAMP, 'system');
INSERT INTO PUESTO (Nombre, IdDepartamento, FechaCreacion, UsuarioCreacion) VALUES ('Contador', (SELECT IdDepartamento FROM DEPARTAMENTO WHERE Nombre = 'Finanzas'), SYSTIMESTAMP, 'system');
INSERT INTO PUESTO (Nombre, IdDepartamento, FechaCreacion, UsuarioCreacion) VALUES ('Coordinador de Logística', (SELECT IdDepartamento FROM DEPARTAMENTO WHERE Nombre = 'Logística'), SYSTIMESTAMP, 'system');
INSERT INTO PUESTO (Nombre, IdDepartamento, FechaCreacion, UsuarioCreacion) VALUES ('Coordinador de Proyectos', (SELECT IdDepartamento FROM DEPARTAMENTO WHERE Nombre = 'Tecnologías de la Información (IT)'), SYSTIMESTAMP, 'system');
INSERT INTO PUESTO (Nombre, IdDepartamento, FechaCreacion, UsuarioCreacion) VALUES ('Desarrollador Junior', (SELECT IdDepartamento FROM DEPARTAMENTO WHERE Nombre = 'Tecnologías de la Información (IT)'), SYSTIMESTAMP, 'system');
INSERT INTO PUESTO (Nombre, IdDepartamento, FechaCreacion, UsuarioCreacion) VALUES ('Desarrollador Senior', (SELECT IdDepartamento FROM DEPARTAMENTO WHERE Nombre = 'Tecnologías de la Información (IT)'), SYSTIMESTAMP, 'system');
INSERT INTO PUESTO (Nombre, IdDepartamento, FechaCreacion, UsuarioCreacion) VALUES ('Especialista en Marketing Digital', (SELECT IdDepartamento FROM DEPARTAMENTO WHERE Nombre = 'Ventas y Marketing'), SYSTIMESTAMP, 'system');
INSERT INTO PUESTO (Nombre, IdDepartamento, FechaCreacion, UsuarioCreacion) VALUES ('Especialista en Relaciones Públicas', (SELECT IdDepartamento FROM DEPARTAMENTO WHERE Nombre = 'Servicio al Cliente'), SYSTIMESTAMP, 'system');
INSERT INTO PUESTO (Nombre, IdDepartamento, FechaCreacion, UsuarioCreacion) VALUES ('Especialista en Seguridad Informática', (SELECT IdDepartamento FROM DEPARTAMENTO WHERE Nombre = 'Tecnologías de la Información (IT)'), SYSTIMESTAMP, 'system');
INSERT INTO PUESTO (Nombre, IdDepartamento, FechaCreacion, UsuarioCreacion) VALUES ('Gerente de Finanzas', (SELECT IdDepartamento FROM DEPARTAMENTO WHERE Nombre = 'Finanzas'), SYSTIMESTAMP, 'system');
INSERT INTO PUESTO (Nombre, IdDepartamento, FechaCreacion, UsuarioCreacion) VALUES ('Gerente de Ventas y Marketing', (SELECT IdDepartamento FROM DEPARTAMENTO WHERE Nombre = 'Ventas y Marketing'), SYSTIMESTAMP, 'system');
INSERT INTO PUESTO (Nombre, IdDepartamento, FechaCreacion, UsuarioCreacion) VALUES ('Gerente de Operaciones', (SELECT IdDepartamento FROM DEPARTAMENTO WHERE Nombre = 'Producción'), SYSTIMESTAMP, 'system');
INSERT INTO PUESTO (Nombre, IdDepartamento, FechaCreacion, UsuarioCreacion) VALUES ('Gerente de Producción', (SELECT IdDepartamento FROM DEPARTAMENTO WHERE Nombre = 'Producción'), SYSTIMESTAMP, 'system');
INSERT INTO PUESTO (Nombre, IdDepartamento, FechaCreacion, UsuarioCreacion) VALUES ('Gerente de Recursos Humanos', (SELECT IdDepartamento FROM DEPARTAMENTO WHERE Nombre = 'Recursos Humanos'), SYSTIMESTAMP, 'system');
INSERT INTO PUESTO (Nombre, IdDepartamento, FechaCreacion, UsuarioCreacion) VALUES ('Gerente de Sistemas', (SELECT IdDepartamento FROM DEPARTAMENTO WHERE Nombre = 'Tecnologías de la Información (IT)'), SYSTIMESTAMP, 'system');
INSERT INTO PUESTO (Nombre, IdDepartamento, FechaCreacion, UsuarioCreacion) VALUES ('Gerente General', (SELECT IdDepartamento FROM DEPARTAMENTO WHERE Nombre = 'Administración'), SYSTIMESTAMP, 'system');
INSERT INTO PUESTO (Nombre, IdDepartamento, FechaCreacion, UsuarioCreacion) VALUES ('Ingeniero de Redes', (SELECT IdDepartamento FROM DEPARTAMENTO WHERE Nombre = 'Tecnologías de la Información (IT)'), SYSTIMESTAMP, 'system');
INSERT INTO PUESTO (Nombre, IdDepartamento, FechaCreacion, UsuarioCreacion) VALUES ('Técnico de Mantenimiento', (SELECT IdDepartamento FROM DEPARTAMENTO WHERE Nombre = 'Producción'), SYSTIMESTAMP, 'system');
INSERT INTO PUESTO (Nombre, IdDepartamento, FechaCreacion, UsuarioCreacion) VALUES ('Técnico de Soporte', (SELECT IdDepartamento FROM DEPARTAMENTO WHERE Nombre = 'Producción'), SYSTIMESTAMP, 'system');
INSERT INTO PUESTO (Nombre, IdDepartamento, FechaCreacion, UsuarioCreacion) VALUES ('Técnico de Soporte IT', (SELECT IdDepartamento FROM DEPARTAMENTO WHERE Nombre = 'Tecnologías de la Información (IT)'), SYSTIMESTAMP, 'system');


-- =====================================================================
-- PERIODO_PLANILLA (16 filas) - PK compuesta (Anio, Mes), sin FK
-- =====================================================================
INSERT INTO PERIODO_PLANILLA (Anio, Mes, FechaInicio, FechaFin, FechaCreacion, UsuarioCreacion) VALUES (2025, 9,  TO_DATE('2025-09-01','YYYY-MM-DD'), TO_DATE('2025-09-30','YYYY-MM-DD'), SYSTIMESTAMP, 'system');
INSERT INTO PERIODO_PLANILLA (Anio, Mes, FechaInicio, FechaFin, FechaCreacion, UsuarioCreacion) VALUES (2025, 10, TO_DATE('2025-10-01','YYYY-MM-DD'), TO_DATE('2025-10-31','YYYY-MM-DD'), SYSTIMESTAMP, 'system');
INSERT INTO PERIODO_PLANILLA (Anio, Mes, FechaInicio, FechaFin, FechaCreacion, UsuarioCreacion) VALUES (2025, 11, TO_DATE('2025-11-01','YYYY-MM-DD'), TO_DATE('2025-11-30','YYYY-MM-DD'), SYSTIMESTAMP, 'system');
INSERT INTO PERIODO_PLANILLA (Anio, Mes, FechaInicio, FechaFin, FechaCreacion, UsuarioCreacion) VALUES (2025, 12, TO_DATE('2025-12-01','YYYY-MM-DD'), TO_DATE('2025-12-31','YYYY-MM-DD'), SYSTIMESTAMP, 'system');
INSERT INTO PERIODO_PLANILLA (Anio, Mes, FechaInicio, FechaFin, FechaCreacion, UsuarioCreacion) VALUES (2026, 1,  TO_DATE('2026-01-01','YYYY-MM-DD'), TO_DATE('2026-01-31','YYYY-MM-DD'), SYSTIMESTAMP, 'system');
INSERT INTO PERIODO_PLANILLA (Anio, Mes, FechaInicio, FechaFin, FechaCreacion, UsuarioCreacion) VALUES (2026, 2,  TO_DATE('2026-02-01','YYYY-MM-DD'), TO_DATE('2026-02-28','YYYY-MM-DD'), SYSTIMESTAMP, 'system');
INSERT INTO PERIODO_PLANILLA (Anio, Mes, FechaInicio, FechaFin, FechaCreacion, UsuarioCreacion) VALUES (2026, 3,  TO_DATE('2026-03-01','YYYY-MM-DD'), TO_DATE('2026-03-31','YYYY-MM-DD'), SYSTIMESTAMP, 'system');
INSERT INTO PERIODO_PLANILLA (Anio, Mes, FechaInicio, FechaFin, FechaCreacion, UsuarioCreacion) VALUES (2026, 4,  TO_DATE('2026-04-01','YYYY-MM-DD'), TO_DATE('2026-04-30','YYYY-MM-DD'), SYSTIMESTAMP, 'system');
INSERT INTO PERIODO_PLANILLA (Anio, Mes, FechaInicio, FechaFin, FechaCreacion, UsuarioCreacion) VALUES (2026, 5,  TO_DATE('2026-05-01','YYYY-MM-DD'), TO_DATE('2026-05-31','YYYY-MM-DD'), SYSTIMESTAMP, 'system');
INSERT INTO PERIODO_PLANILLA (Anio, Mes, FechaInicio, FechaFin, FechaCreacion, UsuarioCreacion) VALUES (2026, 6,  TO_DATE('2026-06-01','YYYY-MM-DD'), TO_DATE('2026-06-30','YYYY-MM-DD'), SYSTIMESTAMP, 'system');
INSERT INTO PERIODO_PLANILLA (Anio, Mes, FechaInicio, FechaFin, FechaCreacion, UsuarioCreacion) VALUES (2026, 7,  TO_DATE('2026-07-01','YYYY-MM-DD'), TO_DATE('2026-07-31','YYYY-MM-DD'), SYSTIMESTAMP, 'system');
INSERT INTO PERIODO_PLANILLA (Anio, Mes, FechaInicio, FechaFin, FechaCreacion, UsuarioCreacion) VALUES (2026, 8,  TO_DATE('2026-08-01','YYYY-MM-DD'), TO_DATE('2026-08-31','YYYY-MM-DD'), SYSTIMESTAMP, 'system');
INSERT INTO PERIODO_PLANILLA (Anio, Mes, FechaInicio, FechaFin, FechaCreacion, UsuarioCreacion) VALUES (2026, 9,  TO_DATE('2026-09-01','YYYY-MM-DD'), TO_DATE('2026-09-30','YYYY-MM-DD'), SYSTIMESTAMP, 'system');
INSERT INTO PERIODO_PLANILLA (Anio, Mes, FechaInicio, FechaFin, FechaCreacion, UsuarioCreacion) VALUES (2026, 10, TO_DATE('2026-10-01','YYYY-MM-DD'), TO_DATE('2026-10-31','YYYY-MM-DD'), SYSTIMESTAMP, 'system');
INSERT INTO PERIODO_PLANILLA (Anio, Mes, FechaInicio, FechaFin, FechaCreacion, UsuarioCreacion) VALUES (2026, 11, TO_DATE('2026-11-01','YYYY-MM-DD'), TO_DATE('2026-11-30','YYYY-MM-DD'), SYSTIMESTAMP, 'system');
INSERT INTO PERIODO_PLANILLA (Anio, Mes, FechaInicio, FechaFin, FechaCreacion, UsuarioCreacion) VALUES (2026, 12, TO_DATE('2026-12-01','YYYY-MM-DD'), TO_DATE('2026-12-31','YYYY-MM-DD'), SYSTIMESTAMP, 'system');
