/*
 * FASE 2 - Modulo de Planilla
 * 01_fase2_ddl.sql
 *
 * DDL puro (16 tablas), traducido desde src/main/resources/sql/Planilla-SQL.sql
 * (MySQL, catedratico) a Oracle 23ai, siguiendo la convencion de
 * database/01-schema-completo.sql (Fase 1):
 *   - PK autoincremental:        NUMBER GENERATED ALWAYS AS IDENTITY
 *   - Fechas de auditoria:       DATE (no TIMESTAMP)
 *   - Nombres de constraints:    PK_<TABLA>, FK_<TABLA>_<TABLA_REFERENCIADA>
 *   - Re-ejecutable:             cada tabla se intenta DROP antes del CREATE,
 *                                 atrapando ORA-00942 (igual que el patron ya
 *                                 usado en 01-schema-completo.sql para
 *                                 TOKEN_RECUPERACION_PASSWORD y SESION).
 *
 * Esta version NO incluye triggers de auditoria: FechaCreacion/UsuarioCreacion
 * y FechaModificacion/UsuarioModificacion se estampan desde el Service de
 * Java (patron GeneroService/SucursalService), igual que en el resto de
 * Fase 1. En los INSERT de seed (02, 03, 04) se usa SYSTIMESTAMP y 'system'.
 *
 * Orden de creacion: respeta las FK (catalogos sin dependencias primero).
 * Orden de DROP: inverso, para no violar FKs al reiniciar.
 *
 * DECISIONES APROBADAS en el Paso A2 (ver docs/fase2/informes/paso-A2-ajustes.md):
 *   - D1: CUENTA_BANCARIA_EMPLEADO.Activa = NUMBER(1) DEFAULT 1 NOT NULL +
 *         CONSTRAINT CK_CUENTA_BANCARIA_EMPLEADO_ACTIVA CHECK (Activa IN (0,1)).
 *   - D2: UNIQUE(Nombre) en ESTADO_CIVIL, STATUS_EMPLEADO, TIPO_DOCUMENTO y
 *         BANCO; ademas UNIQUE compuesto en DEPARTAMENTO (IdEmpresa, Nombre)
 *         y PUESTO (IdDepartamento, Nombre) - verificado contra los seeds de
 *         02_fase2_seed_catalogos.sql, sin duplicados (ver informe).
 *   - D3: nombres de constraint completos (p. ej.
 *         FK_DOCUMENTO_PERSONA_TIPO_DOCUMENTO, FK_CUENTA_BANCARIA_EMPLEADO_BANCO,
 *         FK_FLUJO_STATUS_EMPLEADO_STATUS_ACTUAL/NUEVO) porque Oracle 23ai
 *         admite identificadores de hasta 128 bytes; Fase 1 los abrevio solo
 *         porque sus nombres de tabla eran cortos. Se corrigieron tambien las
 *         abreviaturas que habian quedado en el borrador anterior
 *         (FLUJOSTATUS, CUENTABANCARIA, PLANILLACAB, PLANILLADET, _TIPO,
 *         _PERS, _STATUS sueltos) para que todas sigan PK_/FK_/UQ_/CK_ + el
 *         nombre completo de la(s) tabla(s) involucradas.
 *
 * NO EJECUTAR sin autorizacion. Este archivo es un borrador.
 */

-- =====================================================================
-- DROP (orden inverso a las dependencias), re-ejecutable
-- =====================================================================

BEGIN EXECUTE IMMEDIATE 'DROP TABLE LIQUIDACION CASCADE CONSTRAINTS';
EXCEPTION WHEN OTHERS THEN IF SQLCODE != -942 THEN RAISE; END IF; END;
/

BEGIN EXECUTE IMMEDIATE 'DROP TABLE PLANILLA_DETALLE CASCADE CONSTRAINTS';
EXCEPTION WHEN OTHERS THEN IF SQLCODE != -942 THEN RAISE; END IF; END;
/

BEGIN EXECUTE IMMEDIATE 'DROP TABLE PLANILLA_CABECERA CASCADE CONSTRAINTS';
EXCEPTION WHEN OTHERS THEN IF SQLCODE != -942 THEN RAISE; END IF; END;
/

BEGIN EXECUTE IMMEDIATE 'DROP TABLE PERIODO_PLANILLA CASCADE CONSTRAINTS';
EXCEPTION WHEN OTHERS THEN IF SQLCODE != -942 THEN RAISE; END IF; END;
/

BEGIN EXECUTE IMMEDIATE 'DROP TABLE INASISTENCIA CASCADE CONSTRAINTS';
EXCEPTION WHEN OTHERS THEN IF SQLCODE != -942 THEN RAISE; END IF; END;
/

BEGIN EXECUTE IMMEDIATE 'DROP TABLE CUENTA_BANCARIA_EMPLEADO CASCADE CONSTRAINTS';
EXCEPTION WHEN OTHERS THEN IF SQLCODE != -942 THEN RAISE; END IF; END;
/

BEGIN EXECUTE IMMEDIATE 'DROP TABLE EMPLEADO CASCADE CONSTRAINTS';
EXCEPTION WHEN OTHERS THEN IF SQLCODE != -942 THEN RAISE; END IF; END;
/

BEGIN EXECUTE IMMEDIATE 'DROP TABLE DOCUMENTO_PERSONA CASCADE CONSTRAINTS';
EXCEPTION WHEN OTHERS THEN IF SQLCODE != -942 THEN RAISE; END IF; END;
/

BEGIN EXECUTE IMMEDIATE 'DROP TABLE PERSONA CASCADE CONSTRAINTS';
EXCEPTION WHEN OTHERS THEN IF SQLCODE != -942 THEN RAISE; END IF; END;
/

BEGIN EXECUTE IMMEDIATE 'DROP TABLE PUESTO CASCADE CONSTRAINTS';
EXCEPTION WHEN OTHERS THEN IF SQLCODE != -942 THEN RAISE; END IF; END;
/

BEGIN EXECUTE IMMEDIATE 'DROP TABLE FLUJO_STATUS_EMPLEADO CASCADE CONSTRAINTS';
EXCEPTION WHEN OTHERS THEN IF SQLCODE != -942 THEN RAISE; END IF; END;
/

BEGIN EXECUTE IMMEDIATE 'DROP TABLE DEPARTAMENTO CASCADE CONSTRAINTS';
EXCEPTION WHEN OTHERS THEN IF SQLCODE != -942 THEN RAISE; END IF; END;
/

BEGIN EXECUTE IMMEDIATE 'DROP TABLE BANCO CASCADE CONSTRAINTS';
EXCEPTION WHEN OTHERS THEN IF SQLCODE != -942 THEN RAISE; END IF; END;
/

BEGIN EXECUTE IMMEDIATE 'DROP TABLE TIPO_DOCUMENTO CASCADE CONSTRAINTS';
EXCEPTION WHEN OTHERS THEN IF SQLCODE != -942 THEN RAISE; END IF; END;
/

BEGIN EXECUTE IMMEDIATE 'DROP TABLE ESTADO_CIVIL CASCADE CONSTRAINTS';
EXCEPTION WHEN OTHERS THEN IF SQLCODE != -942 THEN RAISE; END IF; END;
/

BEGIN EXECUTE IMMEDIATE 'DROP TABLE STATUS_EMPLEADO CASCADE CONSTRAINTS';
EXCEPTION WHEN OTHERS THEN IF SQLCODE != -942 THEN RAISE; END IF; END;
/


-- =====================================================================
-- CREATE (orden que respeta las FK)
-- =====================================================================

-- 1) STATUS_EMPLEADO ---------------------------------------------------
CREATE TABLE STATUS_EMPLEADO (
    IdStatusEmpleado    NUMBER GENERATED ALWAYS AS IDENTITY,
    Nombre              VARCHAR2(50) NOT NULL,
    FechaCreacion       DATE NOT NULL,
    UsuarioCreacion     VARCHAR2(100) NOT NULL,
    FechaModificacion   DATE,
    UsuarioModificacion VARCHAR2(100),
    CONSTRAINT PK_STATUS_EMPLEADO PRIMARY KEY (IdStatusEmpleado),
    CONSTRAINT UQ_STATUS_EMPLEADO_NOMBRE UNIQUE (Nombre)
);

-- 2) ESTADO_CIVIL --------------------------------------------------------
CREATE TABLE ESTADO_CIVIL (
    IdEstadoCivil       NUMBER GENERATED ALWAYS AS IDENTITY,
    Nombre              VARCHAR2(50) NOT NULL,
    FechaCreacion       DATE NOT NULL,
    UsuarioCreacion     VARCHAR2(100) NOT NULL,
    FechaModificacion   DATE,
    UsuarioModificacion VARCHAR2(100),
    CONSTRAINT PK_ESTADO_CIVIL PRIMARY KEY (IdEstadoCivil),
    CONSTRAINT UQ_ESTADO_CIVIL_NOMBRE UNIQUE (Nombre)
);

-- 3) TIPO_DOCUMENTO --------------------------------------------------------
CREATE TABLE TIPO_DOCUMENTO (
    IdTipoDocumento     NUMBER GENERATED ALWAYS AS IDENTITY,
    Nombre              VARCHAR2(50) NOT NULL,
    FechaCreacion       DATE NOT NULL,
    UsuarioCreacion     VARCHAR2(100) NOT NULL,
    FechaModificacion   DATE,
    UsuarioModificacion VARCHAR2(100),
    CONSTRAINT PK_TIPO_DOCUMENTO PRIMARY KEY (IdTipoDocumento),
    CONSTRAINT UQ_TIPO_DOCUMENTO_NOMBRE UNIQUE (Nombre)
);

-- 4) BANCO ------------------------------------------------------------
CREATE TABLE BANCO (
    IdBanco             NUMBER GENERATED ALWAYS AS IDENTITY,
    Nombre              VARCHAR2(50) NOT NULL,
    FechaCreacion       DATE NOT NULL,
    UsuarioCreacion     VARCHAR2(100) NOT NULL,
    FechaModificacion   DATE,
    UsuarioModificacion VARCHAR2(100),
    CONSTRAINT PK_BANCO PRIMARY KEY (IdBanco),
    CONSTRAINT UQ_BANCO_NOMBRE UNIQUE (Nombre)
);

-- 5) DEPARTAMENTO (FK -> EMPRESA, de Fase 1) ------------------------------
CREATE TABLE DEPARTAMENTO (
    IdDepartamento      NUMBER GENERATED ALWAYS AS IDENTITY,
    Nombre              VARCHAR2(50) NOT NULL,
    IdEmpresa           NUMBER,
    FechaCreacion       DATE NOT NULL,
    UsuarioCreacion     VARCHAR2(100) NOT NULL,
    FechaModificacion   DATE,
    UsuarioModificacion VARCHAR2(100),
    CONSTRAINT PK_DEPARTAMENTO PRIMARY KEY (IdDepartamento),
    CONSTRAINT FK_DEPARTAMENTO_EMPRESA FOREIGN KEY (IdEmpresa) REFERENCES EMPRESA (IdEmpresa),
    CONSTRAINT UQ_DEPARTAMENTO_EMPRESA_NOMBRE UNIQUE (IdEmpresa, Nombre)
);

-- 6) FLUJO_STATUS_EMPLEADO (FK -> STATUS_EMPLEADO x2) ---------------------
CREATE TABLE FLUJO_STATUS_EMPLEADO (
    IdStatusActual      NUMBER NOT NULL,
    IdStatusNuevo       NUMBER NOT NULL,
    NombreEvento        VARCHAR2(50),
    FechaCreacion       DATE NOT NULL,
    UsuarioCreacion     VARCHAR2(100) NOT NULL,
    FechaModificacion   DATE,
    UsuarioModificacion VARCHAR2(100),
    CONSTRAINT PK_FLUJO_STATUS_EMPLEADO PRIMARY KEY (IdStatusActual, IdStatusNuevo),
    CONSTRAINT FK_FLUJO_STATUS_EMPLEADO_STATUS_ACTUAL FOREIGN KEY (IdStatusActual) REFERENCES STATUS_EMPLEADO (IdStatusEmpleado),
    CONSTRAINT FK_FLUJO_STATUS_EMPLEADO_STATUS_NUEVO  FOREIGN KEY (IdStatusNuevo)  REFERENCES STATUS_EMPLEADO (IdStatusEmpleado)
);

-- 7) PUESTO (FK -> DEPARTAMENTO) ------------------------------------------
CREATE TABLE PUESTO (
    IdPuesto            NUMBER GENERATED ALWAYS AS IDENTITY,
    Nombre              VARCHAR2(50) NOT NULL,
    IdDepartamento      NUMBER NOT NULL,
    FechaCreacion       DATE NOT NULL,
    UsuarioCreacion     VARCHAR2(100) NOT NULL,
    FechaModificacion   DATE,
    UsuarioModificacion VARCHAR2(100),
    CONSTRAINT PK_PUESTO PRIMARY KEY (IdPuesto),
    CONSTRAINT FK_PUESTO_DEPARTAMENTO FOREIGN KEY (IdDepartamento) REFERENCES DEPARTAMENTO (IdDepartamento),
    CONSTRAINT UQ_PUESTO_DEPARTAMENTO_NOMBRE UNIQUE (IdDepartamento, Nombre)
);

-- 8) PERSONA (FK -> GENERO, ESTADO_CIVIL; GENERO es de Fase 1) ------------
CREATE TABLE PERSONA (
    IdPersona           NUMBER GENERATED ALWAYS AS IDENTITY,
    Nombre              VARCHAR2(50) NOT NULL,
    Apellido            VARCHAR2(50) NOT NULL,
    FechaNacimiento     DATE NOT NULL,
    IdGenero            NUMBER NOT NULL,
    Direccion           VARCHAR2(100) NOT NULL,
    Telefono            VARCHAR2(50) NOT NULL,
    CorreoElectronico   VARCHAR2(50),
    IdEstadoCivil       NUMBER NOT NULL,
    FechaCreacion       DATE NOT NULL,
    UsuarioCreacion     VARCHAR2(100) NOT NULL,
    FechaModificacion   DATE,
    UsuarioModificacion VARCHAR2(100),
    CONSTRAINT PK_PERSONA PRIMARY KEY (IdPersona),
    CONSTRAINT FK_PERSONA_GENERO FOREIGN KEY (IdGenero) REFERENCES GENERO (IdGenero),
    CONSTRAINT FK_PERSONA_ESTADO_CIVIL FOREIGN KEY (IdEstadoCivil) REFERENCES ESTADO_CIVIL (IdEstadoCivil)
);

-- 9) DOCUMENTO_PERSONA (FK -> TIPO_DOCUMENTO, PERSONA) --------------------
CREATE TABLE DOCUMENTO_PERSONA (
    IdTipoDocumento     NUMBER NOT NULL,
    IdPersona           NUMBER NOT NULL,
    NoDocumento         VARCHAR2(50),
    FechaCreacion       DATE NOT NULL,
    UsuarioCreacion     VARCHAR2(100) NOT NULL,
    FechaModificacion   DATE,
    UsuarioModificacion VARCHAR2(100),
    CONSTRAINT PK_DOCUMENTO_PERSONA PRIMARY KEY (IdTipoDocumento, IdPersona),
    CONSTRAINT FK_DOCUMENTO_PERSONA_TIPO_DOCUMENTO FOREIGN KEY (IdTipoDocumento) REFERENCES TIPO_DOCUMENTO (IdTipoDocumento),
    CONSTRAINT FK_DOCUMENTO_PERSONA_PERSONA FOREIGN KEY (IdPersona) REFERENCES PERSONA (IdPersona)
);

-- 10) EMPLEADO (FK -> PERSONA, SUCURSAL [Fase 1], PUESTO, STATUS_EMPLEADO) --
CREATE TABLE EMPLEADO (
    IdEmpleado                  NUMBER GENERATED ALWAYS AS IDENTITY,
    IdPersona                   NUMBER NOT NULL,
    IdSucursal                  NUMBER NOT NULL,
    FechaContratacion           DATE NOT NULL,
    IdPuesto                    NUMBER NOT NULL,
    IdStatusEmpleado            NUMBER NOT NULL,
    IngresoSueldoBase           NUMBER(10,2) NOT NULL,
    IngresoBonificacionDecreto  NUMBER(10,2) NOT NULL,
    IngresoOtrosIngresos        NUMBER(10,2) NOT NULL,
    DescuentoIgss               NUMBER(10,2) NOT NULL,
    DescuentoIsr                NUMBER(10,2) NOT NULL,
    DescuentoInasistencias      NUMBER(10,2) NOT NULL,
    FechaCreacion                DATE NOT NULL,
    UsuarioCreacion               VARCHAR2(100) NOT NULL,
    FechaModificacion            DATE,
    UsuarioModificacion          VARCHAR2(100),
    CONSTRAINT PK_EMPLEADO PRIMARY KEY (IdEmpleado),
    CONSTRAINT FK_EMPLEADO_PERSONA FOREIGN KEY (IdPersona) REFERENCES PERSONA (IdPersona),
    CONSTRAINT FK_EMPLEADO_SUCURSAL FOREIGN KEY (IdSucursal) REFERENCES SUCURSAL (IdSucursal),
    CONSTRAINT FK_EMPLEADO_PUESTO FOREIGN KEY (IdPuesto) REFERENCES PUESTO (IdPuesto),
    CONSTRAINT FK_EMPLEADO_STATUS_EMPLEADO FOREIGN KEY (IdStatusEmpleado) REFERENCES STATUS_EMPLEADO (IdStatusEmpleado)
);

-- 11) CUENTA_BANCARIA_EMPLEADO (FK -> BANCO, EMPLEADO) --------------------
-- DECISION D1 (aprobada): "Activa char not null" (MySQL) pasa a
-- NUMBER(1) DEFAULT 1 NOT NULL, igual que Utilizado en
-- TOKEN_RECUPERACION_PASSWORD (Fase 1), mas un CHECK explicito que
-- restringe el dominio a 0/1 (Fase 1 no lo tiene en Utilizado, pero aqui
-- se agrega por pedido explicito de este paso). Los INSERT de origen ya
-- usan el literal numerico 1 (no '1' comillado), compatible con este tipo.
CREATE TABLE CUENTA_BANCARIA_EMPLEADO (
    IdCuentaBancaria    NUMBER GENERATED ALWAYS AS IDENTITY,
    IdEmpleado          NUMBER NOT NULL,
    IdBanco             NUMBER NOT NULL,
    NumeroDeCuenta      VARCHAR2(50) NOT NULL,
    Activa              NUMBER(1) DEFAULT 1 NOT NULL,
    FechaCreacion       DATE NOT NULL,
    UsuarioCreacion     VARCHAR2(100) NOT NULL,
    FechaModificacion   DATE,
    UsuarioModificacion VARCHAR2(100),
    CONSTRAINT PK_CUENTA_BANCARIA_EMPLEADO PRIMARY KEY (IdCuentaBancaria),
    CONSTRAINT FK_CUENTA_BANCARIA_EMPLEADO_BANCO FOREIGN KEY (IdBanco) REFERENCES BANCO (IdBanco),
    CONSTRAINT FK_CUENTA_BANCARIA_EMPLEADO_EMPLEADO FOREIGN KEY (IdEmpleado) REFERENCES EMPLEADO (IdEmpleado),
    CONSTRAINT CK_CUENTA_BANCARIA_EMPLEADO_ACTIVA CHECK (Activa IN (0,1))
);

-- 12) INASISTENCIA (FK -> EMPLEADO) --- sin datos de seed en el script original
CREATE TABLE INASISTENCIA (
    IdInasistencia      NUMBER GENERATED ALWAYS AS IDENTITY,
    IdEmpleado          NUMBER NOT NULL,
    FechaInicial        DATE NOT NULL,
    FechaFinal          DATE NOT NULL,
    MotivoInasistencia  VARCHAR2(300),
    FechaProcesado      DATE,
    FechaCreacion       DATE NOT NULL,
    UsuarioCreacion     VARCHAR2(100) NOT NULL,
    FechaModificacion   DATE,
    UsuarioModificacion VARCHAR2(100),
    CONSTRAINT PK_INASISTENCIA PRIMARY KEY (IdInasistencia),
    CONSTRAINT FK_INASISTENCIA_EMPLEADO FOREIGN KEY (IdEmpleado) REFERENCES EMPLEADO (IdEmpleado)
);

-- 13) PERIODO_PLANILLA (PK compuesta Anio+Mes, sin FK) --------------------
CREATE TABLE PERIODO_PLANILLA (
    Anio                NUMBER NOT NULL,
    Mes                 NUMBER NOT NULL,
    FechaInicio         DATE,
    FechaFin            DATE,
    FechaCreacion       DATE NOT NULL,
    UsuarioCreacion     VARCHAR2(100) NOT NULL,
    FechaModificacion   DATE,
    UsuarioModificacion VARCHAR2(100),
    CONSTRAINT PK_PERIODO_PLANILLA PRIMARY KEY (Anio, Mes)
);

-- 14) PLANILLA_CABECERA (FK -> PERIODO_PLANILLA) --- sin seed en el original
CREATE TABLE PLANILLA_CABECERA (
    Anio                NUMBER NOT NULL,
    Mes                 NUMBER NOT NULL,
    TotalIngresos       NUMBER(10,2),
    TotalDescuentos     NUMBER(10,2),
    SalarioNeto         NUMBER(10,2),
    FechaHoraProcesada  DATE,
    FechaCreacion       DATE NOT NULL,
    UsuarioCreacion     VARCHAR2(100) NOT NULL,
    FechaModificacion   DATE,
    UsuarioModificacion VARCHAR2(100),
    CONSTRAINT PK_PLANILLA_CABECERA PRIMARY KEY (Anio, Mes),
    CONSTRAINT FK_PLANILLA_CABECERA_PERIODO_PLANILLA FOREIGN KEY (Anio, Mes) REFERENCES PERIODO_PLANILLA (Anio, Mes)
);

-- 15) PLANILLA_DETALLE (FK -> PLANILLA_CABECERA, EMPLEADO, PUESTO, STATUS) -
CREATE TABLE PLANILLA_DETALLE (
    IdPlanillaDetalle           NUMBER GENERATED ALWAYS AS IDENTITY,
    Anio                        NUMBER NOT NULL,
    Mes                         NUMBER NOT NULL,
    IdEmpleado                  NUMBER NOT NULL,
    FechaContratacion           DATE NOT NULL,
    IdPuesto                    NUMBER NOT NULL,
    IdStatusEmpleado            NUMBER NOT NULL,
    IngresoSueldoBase           NUMBER(10,2) NOT NULL,
    IngresoBonificacionDecreto  NUMBER(10,2) NOT NULL,
    IngresoOtrosIngresos        NUMBER(10,2) NOT NULL,
    DescuentoIgss               NUMBER(10,2) NOT NULL,
    DescuentoIsr                NUMBER(10,2) NOT NULL,
    DescuentoInasistencias      NUMBER(10,2) NOT NULL,
    SalarioNeto                 NUMBER(10,2),
    FechaCreacion                DATE NOT NULL,
    UsuarioCreacion               VARCHAR2(100) NOT NULL,
    FechaModificacion            DATE,
    UsuarioModificacion          VARCHAR2(100),
    CONSTRAINT PK_PLANILLA_DETALLE PRIMARY KEY (IdPlanillaDetalle),
    CONSTRAINT FK_PLANILLA_DETALLE_PLANILLA_CABECERA FOREIGN KEY (Anio, Mes) REFERENCES PLANILLA_CABECERA (Anio, Mes),
    CONSTRAINT FK_PLANILLA_DETALLE_EMPLEADO FOREIGN KEY (IdEmpleado) REFERENCES EMPLEADO (IdEmpleado),
    CONSTRAINT FK_PLANILLA_DETALLE_PUESTO FOREIGN KEY (IdPuesto) REFERENCES PUESTO (IdPuesto),
    CONSTRAINT FK_PLANILLA_DETALLE_STATUS_EMPLEADO FOREIGN KEY (IdStatusEmpleado) REFERENCES STATUS_EMPLEADO (IdStatusEmpleado)
);

-- 16) LIQUIDACION (FK -> EMPLEADO, PUESTO) --- sin seed en el original -----
-- Nota: la tabla de origen (MySQL) no define FechaModificacion ni
-- UsuarioModificacion para LIQUIDACION (es un registro de cierre, no se
-- edita). Se preserva esa asimetria respecto a las demas tablas.
CREATE TABLE LIQUIDACION (
    IdLiquidacion               NUMBER GENERATED ALWAYS AS IDENTITY,
    IdEmpleado                  NUMBER NOT NULL,
    FechaContratacion           DATE NOT NULL,
    FechaEgreso                 DATE NOT NULL,
    FechaLiquidacion            DATE NOT NULL,
    MotivoEgreso                VARCHAR2(50),
    IdPuesto                    NUMBER NOT NULL,
    IngresoSueldoBase           NUMBER(10,2) NOT NULL,
    IngresoBonificacionDecreto  NUMBER(10,2) NOT NULL,
    IngresoOtrosIngresos        NUMBER(10,2) NOT NULL,
    DescuentoIgss                NUMBER(10,2) NOT NULL,
    DescuentoIsr                NUMBER(10,2) NOT NULL,
    DescuentoInasistencias      NUMBER(10,2) NOT NULL,
    SalarioNeto                 NUMBER(10,2),
    TotalIngresos                NUMBER(10,2),
    TotalDescuentos             NUMBER(10,2),
    TotalNeto                   NUMBER(10,2),
    FechaCreacion                DATE NOT NULL,
    UsuarioCreacion               VARCHAR2(100) NOT NULL,
    CONSTRAINT PK_LIQUIDACION PRIMARY KEY (IdLiquidacion),
    CONSTRAINT FK_LIQUIDACION_EMPLEADO FOREIGN KEY (IdEmpleado) REFERENCES EMPLEADO (IdEmpleado),
    CONSTRAINT FK_LIQUIDACION_PUESTO FOREIGN KEY (IdPuesto) REFERENCES PUESTO (IdPuesto)
);
