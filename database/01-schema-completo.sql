CREATE TABLE EMPRESA (
    IdEmpresa                              NUMBER GENERATED ALWAYS AS IDENTITY,
    Nombre                                 VARCHAR2(100) NOT NULL,
    Direccion                              VARCHAR2(200) NOT NULL,
    Nit                                    VARCHAR2(20)  NOT NULL,
    PasswordCantidadMayusculas             NUMBER,
    PasswordCantidadMinusculas             NUMBER,
    PasswordCantidadCaracteresEspeciales   NUMBER,
    PasswordCantidadCaducidadDias          NUMBER,
    PasswordLargo                          NUMBER,
    PasswordIntentosAntesDeBloquear        NUMBER,
    PasswordCantidadNumeros                NUMBER,
    PasswordCantidadPreguntasValidar       NUMBER,
    FechaCreacion                          DATE NOT NULL,
    UsuarioCreacion                        VARCHAR2(100) NOT NULL,
    FechaModificacion                      DATE,
    UsuarioModificacion                    VARCHAR2(100),
    CONSTRAINT PK_EMPRESA PRIMARY KEY (IdEmpresa)
);

INSERT INTO EMPRESA (
    Nombre, Direccion, Nit, PasswordCantidadMayusculas,
    PasswordCantidadMinusculas, PasswordCantidadCaracteresEspeciales,
    PasswordCantidadCaducidadDias, PasswordLargo,
    PasswordIntentosAntesDeBloquear, PasswordCantidadNumeros,
    PasswordCantidadPreguntasValidar, FechaCreacion,
    UsuarioCreacion, FechaModificacion, UsuarioModificacion
)
VALUES (
    'Software Inc.', 'San Jose Pinula, Guatemala', '12345678-9', 1,
    1, 1, 60, 8,
    5, 2, 1, SYSDATE,
    'system', NULL, NULL
);


CREATE TABLE SUCURSAL (
    IdSucursal          NUMBER GENERATED ALWAYS AS IDENTITY,
    Nombre              VARCHAR2(100) NOT NULL,
    Direccion           VARCHAR2(200) NOT NULL,
    IdEmpresa           NUMBER NOT NULL,
    FechaCreacion       DATE NOT NULL,
    UsuarioCreacion     VARCHAR2(100) NOT NULL,
    FechaModificacion   DATE,
    UsuarioModificacion VARCHAR2(100),
    CONSTRAINT PK_SUCURSAL PRIMARY KEY (IdSucursal),
    CONSTRAINT FK_SUCURSAL_EMPRESA FOREIGN KEY (IdEmpresa) REFERENCES EMPRESA (IdEmpresa)
);

INSERT INTO SUCURSAL (
    Nombre, Direccion, IdEmpresa, FechaCreacion,
    UsuarioCreacion, FechaModificacion, UsuarioModificacion
)
VALUES (
    'Oficinas Centrales', 'San Jose Pinula, Guatemala', 1, SYSDATE,
    'system', NULL, NULL
);


CREATE TABLE STATUS_USUARIO (
    IdStatusUsuario     NUMBER GENERATED ALWAYS AS IDENTITY,
    Nombre              VARCHAR2(100) NOT NULL,
    FechaCreacion       DATE NOT NULL,
    UsuarioCreacion     VARCHAR2(100) NOT NULL,
    FechaModificacion   DATE,
    UsuarioModificacion VARCHAR2(100),
    CONSTRAINT PK_STATUS_USUARIO PRIMARY KEY (IdStatusUsuario)
);

INSERT INTO STATUS_USUARIO (Nombre, FechaCreacion, UsuarioCreacion, FechaModificacion, UsuarioModificacion)
VALUES ('Activo', SYSDATE, 'system', NULL, NULL);

INSERT INTO STATUS_USUARIO (Nombre, FechaCreacion, UsuarioCreacion, FechaModificacion, UsuarioModificacion)
VALUES ('Bloqueado por intentos de acceso', SYSDATE, 'system', NULL, NULL);

INSERT INTO STATUS_USUARIO (Nombre, FechaCreacion, UsuarioCreacion, FechaModificacion, UsuarioModificacion)
VALUES ('Inactivo', SYSDATE, 'system', NULL, NULL);


CREATE TABLE GENERO (
    IdGenero            NUMBER GENERATED ALWAYS AS IDENTITY,
    Nombre              VARCHAR2(100) NOT NULL,
    FechaCreacion       DATE NOT NULL,
    UsuarioCreacion     VARCHAR2(100) NOT NULL,
    FechaModificacion   DATE,
    UsuarioModificacion VARCHAR2(100),
    CONSTRAINT PK_GENERO PRIMARY KEY (IdGenero)
);

INSERT INTO GENERO (Nombre, FechaCreacion, UsuarioCreacion, FechaModificacion, UsuarioModificacion)
VALUES ('Masculino', SYSDATE, 'system', NULL, NULL);

INSERT INTO GENERO (Nombre, FechaCreacion, UsuarioCreacion, FechaModificacion, UsuarioModificacion)
VALUES ('Femenino', SYSDATE, 'system', NULL, NULL);


CREATE TABLE ROLE (
    IdRole              NUMBER GENERATED ALWAYS AS IDENTITY,
    Nombre              VARCHAR2(50) NOT NULL,
    FechaCreacion       DATE NOT NULL,
    UsuarioCreacion     VARCHAR2(100) NOT NULL,
    FechaModificacion   DATE,
    UsuarioModificacion VARCHAR2(100),
    CONSTRAINT PK_ROLE PRIMARY KEY (IdRole)
);

INSERT INTO ROLE (Nombre, FechaCreacion, UsuarioCreacion, FechaModificacion, UsuarioModificacion)
VALUES ('Administrador', SYSDATE, 'system', NULL, NULL);

INSERT INTO ROLE (Nombre, FechaCreacion, UsuarioCreacion, FechaModificacion, UsuarioModificacion)
VALUES ('Sin Opciones', SYSDATE, 'system', NULL, NULL);


CREATE TABLE USUARIO (
    IdUsuario                   VARCHAR2(100) NOT NULL,
    Nombre                      VARCHAR2(100) NOT NULL,
    Apellido                    VARCHAR2(100) NOT NULL,
    FechaNacimiento             DATE NOT NULL,
    IdStatusUsuario             NUMBER NOT NULL,
    Password                    VARCHAR2(100) NOT NULL,
    IdGenero                    NUMBER NOT NULL,
    UltimaFechaIngreso          DATE,
    IntentosDeAcceso            NUMBER,
    SesionActual                VARCHAR2(100),
    UltimaFechaCambioPassword   DATE,
    CorreoElectronico           VARCHAR2(100),
    RequiereCambiarPassword     NUMBER,
    Fotografia                  BLOB,
    TelefonoMovil               VARCHAR2(30),
    IdSucursal                  NUMBER NOT NULL,
    Pregunta                    VARCHAR2(200) NOT NULL,
    Respuesta                   VARCHAR2(200) NOT NULL,
    IdRole                      NUMBER NOT NULL,
    FechaCreacion               DATE NOT NULL,
    UsuarioCreacion              VARCHAR2(100) NOT NULL,
    FechaModificacion           DATE,
    UsuarioModificacion         VARCHAR2(100),
    CONSTRAINT PK_USUARIO PRIMARY KEY (IdUsuario),
    CONSTRAINT FK_USUARIO_STATUS  FOREIGN KEY (IdStatusUsuario) REFERENCES STATUS_USUARIO (IdStatusUsuario),
    CONSTRAINT FK_USUARIO_GENERO  FOREIGN KEY (IdGenero) REFERENCES GENERO (IdGenero),
    CONSTRAINT FK_USUARIO_SUCURSAL FOREIGN KEY (IdSucursal) REFERENCES SUCURSAL (IdSucursal),
    CONSTRAINT FK_USUARIO_ROLE    FOREIGN KEY (IdRole) REFERENCES ROLE (IdRole)
);


INSERT INTO USUARIO (
    IdUsuario, Nombre, Apellido, FechaNacimiento, IdStatusUsuario,
    Password, IdGenero, UltimaFechaIngreso, IntentosDeAcceso,
    SesionActual, UltimaFechaCambioPassword, CorreoElectronico,
    RequiereCambiarPassword, Fotografia, TelefonoMovil,
    IdSucursal, Pregunta, Respuesta, IdRole,
    FechaCreacion, UsuarioCreacion, FechaModificacion, UsuarioModificacion
)
VALUES (
    'system', 'Nologin', 'Nologin', DATE '1990-05-15', 1,
    STANDARD_HASH('erpwijoeli', 'MD5'), 1, NULL, 0,
    NULL, NULL, 'system@example.com',
    1, NULL, '555-1234567',
    1, '¿Nombre de tu primera mascota?', 'Rex', 2,
    SYSDATE, 'system', NULL, NULL
);

INSERT INTO USUARIO (
    IdUsuario, Nombre, Apellido, FechaNacimiento, IdStatusUsuario,
    Password, IdGenero, UltimaFechaIngreso, IntentosDeAcceso,
    SesionActual, UltimaFechaCambioPassword, CorreoElectronico,
    RequiereCambiarPassword, Fotografia, TelefonoMovil,
    IdSucursal, Pregunta, Respuesta, IdRole,
    FechaCreacion, UsuarioCreacion, FechaModificacion, UsuarioModificacion
)
VALUES (
    'Administrador', 'Administrador', 'IT', DATE '1990-05-15', 1,
    STANDARD_HASH('ITAdmin', 'MD5'), 1, NULL, 0,
    NULL, NULL, 'itadmin@example.com',
    1, NULL, '555-1234567',
    1, '¿Nombre de tu curso preferido?', 'Analisis de Sistemas II', 1,
    SYSDATE, 'system', NULL, NULL
);


CREATE TABLE MODULO (
    IdModulo            NUMBER GENERATED ALWAYS AS IDENTITY,
    Nombre              VARCHAR2(50) NOT NULL,
    OrdenMenu           NUMBER NOT NULL,
    FechaCreacion       DATE NOT NULL,
    UsuarioCreacion     VARCHAR2(100) NOT NULL,
    FechaModificacion   DATE,
    UsuarioModificacion VARCHAR2(100),
    CONSTRAINT PK_MODULO PRIMARY KEY (IdModulo)
);

INSERT INTO MODULO (Nombre, OrdenMenu, FechaCreacion, UsuarioCreacion)
VALUES ('Seguridad', 1, SYSDATE, 'system');


CREATE TABLE MENU (
    IdMenu              NUMBER GENERATED ALWAYS AS IDENTITY,
    IdModulo            NUMBER NOT NULL,
    Nombre              VARCHAR2(50) NOT NULL,
    OrdenMenu           NUMBER NOT NULL,
    FechaCreacion       DATE NOT NULL,
    UsuarioCreacion     VARCHAR2(100) NOT NULL,
    FechaModificacion   DATE,
    UsuarioModificacion VARCHAR2(100),
    CONSTRAINT PK_MENU PRIMARY KEY (IdMenu),
    CONSTRAINT FK_MENU_MODULO FOREIGN KEY (IdModulo) REFERENCES MODULO (IdModulo)
);

INSERT INTO MENU (IdModulo, Nombre, OrdenMenu, FechaCreacion, UsuarioCreacion)
VALUES (1, 'Parametros Generales', 1, SYSDATE, 'system');

INSERT INTO MENU (IdModulo, Nombre, OrdenMenu, FechaCreacion, UsuarioCreacion)
VALUES (1, 'Acciones', 2, SYSDATE, 'system');

INSERT INTO MENU (IdModulo, Nombre, OrdenMenu, FechaCreacion, UsuarioCreacion)
VALUES (1, 'Estadisticas', 3, SYSDATE, 'system');

INSERT INTO MENU (IdModulo, Nombre, OrdenMenu, FechaCreacion, UsuarioCreacion)
VALUES (1, 'Procedimientos Almacenados', 4, SYSDATE, 'system');


CREATE TABLE OPCION (
    IdOpcion            NUMBER GENERATED ALWAYS AS IDENTITY,
    IdMenu              NUMBER NOT NULL,
    Nombre              VARCHAR2(50) NOT NULL,
    OrdenMenu           NUMBER NOT NULL,
    Pagina              VARCHAR2(100) NOT NULL,
    FechaCreacion       DATE NOT NULL,
    UsuarioCreacion     VARCHAR2(100) NOT NULL,
    FechaModificacion   DATE,
    UsuarioModificacion VARCHAR2(100),
    CONSTRAINT PK_OPCION PRIMARY KEY (IdOpcion),
    CONSTRAINT FK_OPCION_MENU FOREIGN KEY (IdMenu) REFERENCES MENU (IdMenu)
);

INSERT INTO OPCION (IdMenu, Nombre, OrdenMenu, Pagina, FechaCreacion, UsuarioCreacion)
VALUES (1, 'Empresas', 1, 'empresa.php', SYSDATE, 'system');

INSERT INTO OPCION (IdMenu, Nombre, OrdenMenu, Pagina, FechaCreacion, UsuarioCreacion)
VALUES (1, 'Sucursales', 2, 'sucursal.php', SYSDATE, 'system');

INSERT INTO OPCION (IdMenu, Nombre, OrdenMenu, Pagina, FechaCreacion, UsuarioCreacion)
VALUES (1, 'Generos', 3, 'genero.php', SYSDATE, 'system');

INSERT INTO OPCION (IdMenu, Nombre, OrdenMenu, Pagina, FechaCreacion, UsuarioCreacion)
VALUES (1, 'Estatus Usuario', 4, 'status_usuario.php', SYSDATE, 'system');

INSERT INTO OPCION (IdMenu, Nombre, OrdenMenu, Pagina, FechaCreacion, UsuarioCreacion)
VALUES (1, 'Roles', 5, 'role.php', SYSDATE, 'system');

INSERT INTO OPCION (IdMenu, Nombre, OrdenMenu, Pagina, FechaCreacion, UsuarioCreacion)
VALUES (1, 'Modulos', 6, 'modulo.php', SYSDATE, 'system');

INSERT INTO OPCION (IdMenu, Nombre, OrdenMenu, Pagina, FechaCreacion, UsuarioCreacion)
VALUES (1, 'Menus', 7, 'menu.php', SYSDATE, 'system');

INSERT INTO OPCION (IdMenu, Nombre, OrdenMenu, Pagina, FechaCreacion, UsuarioCreacion)
VALUES (1, 'Opciones', 3, 'opcion.php', SYSDATE, 'system');

INSERT INTO OPCION (IdMenu, Nombre, OrdenMenu, Pagina, FechaCreacion, UsuarioCreacion)
VALUES (2, 'Usuarios', 3, 'usuario.php', SYSDATE, 'system');

INSERT INTO OPCION (IdMenu, Nombre, OrdenMenu, Pagina, FechaCreacion, UsuarioCreacion)
VALUES (2, 'Asignar Opciones a un Role', 3, 'asignacion_opcion_role.php', SYSDATE, 'system');


CREATE TABLE ROLE_OPCION (
    IdRole              NUMBER NOT NULL,
    IdOpcion            NUMBER NOT NULL,
    Alta                NUMBER(1) NOT NULL,
    Baja                NUMBER(1) NOT NULL,
    Cambio              NUMBER(1) NOT NULL,
    Imprimir            NUMBER(1) NOT NULL,
    Exportar            NUMBER(1) NOT NULL,
    FechaCreacion       DATE NOT NULL,
    UsuarioCreacion     VARCHAR2(100) NOT NULL,
    FechaModificacion   DATE,
    UsuarioModificacion VARCHAR2(100),
    CONSTRAINT PK_ROLE_OPCION PRIMARY KEY (IdRole, IdOpcion),
    CONSTRAINT FK_ROLEOPCION_ROLE   FOREIGN KEY (IdRole) REFERENCES ROLE (IdRole),
    CONSTRAINT FK_ROLEOPCION_OPCION FOREIGN KEY (IdOpcion) REFERENCES OPCION (IdOpcion)
);

INSERT INTO ROLE_OPCION (IdRole, IdOpcion, Alta, Baja, Cambio, Imprimir, Exportar, FechaCreacion, UsuarioCreacion)
VALUES (1, 1, 1, 1, 1, 1, 1, SYSDATE, 'system');

INSERT INTO ROLE_OPCION (IdRole, IdOpcion, Alta, Baja, Cambio, Imprimir, Exportar, FechaCreacion, UsuarioCreacion)
VALUES (1, 2, 1, 1, 1, 1, 1, SYSDATE, 'system');

INSERT INTO ROLE_OPCION (IdRole, IdOpcion, Alta, Baja, Cambio, Imprimir, Exportar, FechaCreacion, UsuarioCreacion)
VALUES (1, 3, 1, 1, 1, 1, 1, SYSDATE, 'system');

INSERT INTO ROLE_OPCION (IdRole, IdOpcion, Alta, Baja, Cambio, Imprimir, Exportar, FechaCreacion, UsuarioCreacion)
VALUES (1, 4, 1, 1, 1, 1, 1, SYSDATE, 'system');

INSERT INTO ROLE_OPCION (IdRole, IdOpcion, Alta, Baja, Cambio, Imprimir, Exportar, FechaCreacion, UsuarioCreacion)
VALUES (1, 5, 1, 1, 1, 1, 1, SYSDATE, 'system');

INSERT INTO ROLE_OPCION (IdRole, IdOpcion, Alta, Baja, Cambio, Imprimir, Exportar, FechaCreacion, UsuarioCreacion)
VALUES (1, 6, 1, 1, 1, 1, 1, SYSDATE, 'system');

INSERT INTO ROLE_OPCION (IdRole, IdOpcion, Alta, Baja, Cambio, Imprimir, Exportar, FechaCreacion, UsuarioCreacion)
VALUES (1, 7, 1, 1, 1, 1, 1, SYSDATE, 'system');

INSERT INTO ROLE_OPCION (IdRole, IdOpcion, Alta, Baja, Cambio, Imprimir, Exportar, FechaCreacion, UsuarioCreacion)
VALUES (1, 8, 1, 1, 1, 1, 1, SYSDATE, 'system');

INSERT INTO ROLE_OPCION (IdRole, IdOpcion, Alta, Baja, Cambio, Imprimir, Exportar, FechaCreacion, UsuarioCreacion)
VALUES (1, 9, 1, 1, 1, 1, 1, SYSDATE, 'system');

INSERT INTO ROLE_OPCION (IdRole, IdOpcion, Alta, Baja, Cambio, Imprimir, Exportar, FechaCreacion, UsuarioCreacion)
VALUES (1, 10, 1, 1, 1, 1, 1, SYSDATE, 'system');


CREATE TABLE TIPO_ACCESO (
    IdTipoAcceso        NUMBER GENERATED ALWAYS AS IDENTITY,
    Nombre              VARCHAR2(100) NOT NULL,
    FechaCreacion       DATE NOT NULL,
    UsuarioCreacion     VARCHAR2(100) NOT NULL,
    FechaModificacion   DATE,
    UsuarioModificacion VARCHAR2(100),
    CONSTRAINT PK_TIPO_ACCESO PRIMARY KEY (IdTipoAcceso)
);

INSERT INTO TIPO_ACCESO (Nombre, FechaCreacion, UsuarioCreacion, FechaModificacion, UsuarioModificacion)
VALUES ('Acceso Concedido', SYSDATE, 'system', NULL, NULL);

INSERT INTO TIPO_ACCESO (Nombre, FechaCreacion, UsuarioCreacion, FechaModificacion, UsuarioModificacion)
VALUES ('Bloqueado - Password incorrecto', SYSDATE, 'system', NULL, NULL);

INSERT INTO TIPO_ACCESO (Nombre, FechaCreacion, UsuarioCreacion, FechaModificacion, UsuarioModificacion)
VALUES ('Bloqueado - Numero de intentos exedidos', SYSDATE, 'system', NULL, NULL);

INSERT INTO TIPO_ACCESO (Nombre, FechaCreacion, UsuarioCreacion, FechaModificacion, UsuarioModificacion)
VALUES ('Usuario Inactivo', SYSDATE, 'system', NULL, NULL);

INSERT INTO TIPO_ACCESO (Nombre, FechaCreacion, UsuarioCreacion, FechaModificacion, UsuarioModificacion)
VALUES ('Usuario ingresado no existe', SYSDATE, 'system', NULL, NULL);


CREATE TABLE BITACORA_ACCESO (
    IdBitacoraAcceso    NUMBER GENERATED ALWAYS AS IDENTITY,
    IdUsuario           VARCHAR2(100) NOT NULL,
    IdTipoAcceso        NUMBER NOT NULL,
    FechaAcceso         DATE NOT NULL,
    HttpUserAgent       VARCHAR2(200),
    DireccionIp         VARCHAR2(50),
    Acceso              VARCHAR2(100),
    SistemaOperativo    VARCHAR2(50),
    Dispositivo         VARCHAR2(50),
    Browser             VARCHAR2(50),
    Sesion              VARCHAR2(100),
    CONSTRAINT PK_BITACORA_ACCESO PRIMARY KEY (IdBitacoraAcceso),
    CONSTRAINT FK_BITACORA_TIPOACCESO FOREIGN KEY (IdTipoAcceso) REFERENCES TIPO_ACCESO (IdTipoAcceso)
);

COMMIT;


CREATE OR REPLACE TRIGGER TRG_EMPRESA_AUDIT
BEFORE INSERT OR UPDATE ON EMPRESA
FOR EACH ROW
BEGIN
    IF INSERTING THEN
        :NEW.FechaCreacion := SYSDATE;
    ELSE
        :NEW.FechaModificacion := SYSDATE;
        :NEW.FechaCreacion := :OLD.FechaCreacion;
    END IF;
END;
/

CREATE OR REPLACE TRIGGER TRG_SUCURSAL_AUDIT
BEFORE INSERT OR UPDATE ON SUCURSAL
FOR EACH ROW
BEGIN
    IF INSERTING THEN
        :NEW.FechaCreacion := SYSDATE;
    ELSE
        :NEW.FechaModificacion := SYSDATE;
        :NEW.FechaCreacion := :OLD.FechaCreacion;
    END IF;
END;
/

CREATE OR REPLACE TRIGGER TRG_GENERO_AUDIT
BEFORE INSERT OR UPDATE ON GENERO
FOR EACH ROW
BEGIN
    IF INSERTING THEN
        :NEW.FechaCreacion := SYSDATE;
    ELSE
        :NEW.FechaModificacion := SYSDATE;
        :NEW.FechaCreacion := :OLD.FechaCreacion;
    END IF;
END;
/

CREATE OR REPLACE TRIGGER TRG_STATUS_USUARIO_AUDIT
BEFORE INSERT OR UPDATE ON STATUS_USUARIO
FOR EACH ROW
BEGIN
    IF INSERTING THEN
        :NEW.FechaCreacion := SYSDATE;
    ELSE
        :NEW.FechaModificacion := SYSDATE;
        :NEW.FechaCreacion := :OLD.FechaCreacion;
    END IF;
END;
/

CREATE OR REPLACE TRIGGER TRG_ROLE_AUDIT
BEFORE INSERT OR UPDATE ON ROLE
FOR EACH ROW
BEGIN
    IF INSERTING THEN
        :NEW.FechaCreacion := SYSDATE;
    ELSE
        :NEW.FechaModificacion := SYSDATE;
        :NEW.FechaCreacion := :OLD.FechaCreacion;
    END IF;
END;
/

CREATE OR REPLACE TRIGGER TRG_MODULO_AUDIT
BEFORE INSERT OR UPDATE ON MODULO
FOR EACH ROW
BEGIN
    IF INSERTING THEN
        :NEW.FechaCreacion := SYSDATE;
    ELSE
        :NEW.FechaModificacion := SYSDATE;
        :NEW.FechaCreacion := :OLD.FechaCreacion;
    END IF;
END;
/

CREATE OR REPLACE TRIGGER TRG_MENU_AUDIT
BEFORE INSERT OR UPDATE ON MENU
FOR EACH ROW
BEGIN
    IF INSERTING THEN
        :NEW.FechaCreacion := SYSDATE;
    ELSE
        :NEW.FechaModificacion := SYSDATE;
        :NEW.FechaCreacion := :OLD.FechaCreacion;
    END IF;
END;
/

CREATE OR REPLACE TRIGGER TRG_OPCION_AUDIT
BEFORE INSERT OR UPDATE ON OPCION
FOR EACH ROW
BEGIN
    IF INSERTING THEN
        :NEW.FechaCreacion := SYSDATE;
    ELSE
        :NEW.FechaModificacion := SYSDATE;
        :NEW.FechaCreacion := :OLD.FechaCreacion;
    END IF;
END;
/

CREATE OR REPLACE TRIGGER TRG_ROLE_OPCION_AUDIT
BEFORE INSERT OR UPDATE ON ROLE_OPCION
FOR EACH ROW
BEGIN
    IF INSERTING THEN
        :NEW.FechaCreacion := SYSDATE;
    ELSE
        :NEW.FechaModificacion := SYSDATE;
        :NEW.FechaCreacion := :OLD.FechaCreacion;
    END IF;
END;
/

CREATE OR REPLACE TRIGGER TRG_TIPO_ACCESO_AUDIT
BEFORE INSERT OR UPDATE ON TIPO_ACCESO
FOR EACH ROW
BEGIN
    IF INSERTING THEN
        :NEW.FechaCreacion := SYSDATE;
    ELSE
        :NEW.FechaModificacion := SYSDATE;
        :NEW.FechaCreacion := :OLD.FechaCreacion;
    END IF;
END;
/

CREATE OR REPLACE TRIGGER TRG_USUARIO_AUDIT
BEFORE INSERT OR UPDATE ON USUARIO
FOR EACH ROW
BEGIN
    IF INSERTING THEN
        :NEW.FechaCreacion := SYSDATE;
    ELSE
        :NEW.FechaModificacion := SYSDATE;
        :NEW.FechaCreacion := :OLD.FechaCreacion;
    END IF;
END;
/


BEGIN
    EXECUTE IMMEDIATE 'DROP TRIGGER TRG_USUARIO_PASSWORD_POLICY';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE != -4080 THEN
            RAISE;
        END IF;
END;
/

CREATE OR REPLACE TRIGGER TRG_USUARIO_BLOQUEO
BEFORE UPDATE OF IntentosDeAcceso ON USUARIO
FOR EACH ROW
DECLARE
    v_MaxIntentos EMPRESA.PasswordIntentosAntesDeBloquear%TYPE;
    v_IdBloqueado STATUS_USUARIO.IdStatusUsuario%TYPE;
BEGIN
    BEGIN
        SELECT E.PasswordIntentosAntesDeBloquear
        INTO v_MaxIntentos
        FROM EMPRESA E
        JOIN SUCURSAL S ON S.IdEmpresa = E.IdEmpresa
        WHERE S.IdSucursal = :NEW.IdSucursal;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            RAISE_APPLICATION_ERROR(-20000, 'No se encontro la politica de bloqueo: verifique que el usuario tenga una sucursal valida.');
    END;

    IF :NEW.IntentosDeAcceso >= v_MaxIntentos THEN
        SELECT IdStatusUsuario INTO v_IdBloqueado
        FROM STATUS_USUARIO
        WHERE UPPER(Nombre) = 'BLOQUEADO POR INTENTOS DE ACCESO';

        :NEW.IdStatusUsuario := v_IdBloqueado;
    END IF;
END;
/

CREATE OR REPLACE TRIGGER TRG_USUARIO_DESBLOQUEO
BEFORE UPDATE OF IdStatusUsuario ON USUARIO
FOR EACH ROW
DECLARE
    v_IdBloqueado STATUS_USUARIO.IdStatusUsuario%TYPE;
BEGIN
    SELECT IdStatusUsuario INTO v_IdBloqueado
    FROM STATUS_USUARIO
    WHERE UPPER(Nombre) = 'BLOQUEADO POR INTENTOS DE ACCESO';

    IF :OLD.IdStatusUsuario = v_IdBloqueado AND :NEW.IdStatusUsuario <> v_IdBloqueado THEN
        :NEW.IntentosDeAcceso := 0;
    END IF;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        NULL;
END;
/

CREATE OR REPLACE TRIGGER TRG_USUARIO_STATUS_TRANSICION
BEFORE UPDATE OF IdStatusUsuario ON USUARIO
FOR EACH ROW
DECLARE
    v_NombreOld STATUS_USUARIO.Nombre%TYPE;
    v_NombreNew STATUS_USUARIO.Nombre%TYPE;
BEGIN
    SELECT Nombre INTO v_NombreOld FROM STATUS_USUARIO WHERE IdStatusUsuario = :OLD.IdStatusUsuario;
    SELECT Nombre INTO v_NombreNew FROM STATUS_USUARIO WHERE IdStatusUsuario = :NEW.IdStatusUsuario;

    IF UPPER(v_NombreOld) = 'INACTIVO' THEN
        RAISE_APPLICATION_ERROR(-20020, 'No se puede cambiar el estatus de un usuario Inactivo (estado final).');
    END IF;

    IF UPPER(v_NombreOld) = 'BLOQUEADO POR INTENTOS DE ACCESO'
       AND UPPER(v_NombreNew) NOT IN ('ACTIVO', 'INACTIVO', 'BLOQUEADO POR INTENTOS DE ACCESO') THEN
        RAISE_APPLICATION_ERROR(-20021, 'Un usuario Bloqueado unicamente puede regresar a Activo o pasar a Inactivo.');
    END IF;
EXCEPTION
    WHEN NO_DATA_FOUND THEN NULL;
END;
/


CREATE OR REPLACE TRIGGER TRG_BITACORA_ACCESO_AIU
AFTER INSERT ON BITACORA_ACCESO
FOR EACH ROW
DECLARE
    v_NombreTipo    TIPO_ACCESO.Nombre%TYPE;
    v_DiasCaducidad EMPRESA.PasswordCantidadCaducidadDias%TYPE;
    v_UltimoCambio  USUARIO.UltimaFechaCambioPassword%TYPE;
BEGIN
    SELECT Nombre INTO v_NombreTipo FROM TIPO_ACCESO WHERE IdTipoAcceso = :NEW.IdTipoAcceso;

    IF UPPER(v_NombreTipo) = 'ACCESO CONCEDIDO' THEN
        UPDATE USUARIO
        SET IntentosDeAcceso = 0,
            UltimaFechaIngreso = :NEW.FechaAcceso,
            SesionActual = :NEW.Sesion
        WHERE IdUsuario = :NEW.IdUsuario;


        BEGIN
            SELECT U.UltimaFechaCambioPassword, E.PasswordCantidadCaducidadDias
            INTO v_UltimoCambio, v_DiasCaducidad
            FROM USUARIO U
            JOIN SUCURSAL S ON S.IdSucursal = U.IdSucursal
            JOIN EMPRESA E ON E.IdEmpresa = S.IdEmpresa
            WHERE U.IdUsuario = :NEW.IdUsuario;

            IF v_UltimoCambio IS NOT NULL AND SYSDATE - v_UltimoCambio >= v_DiasCaducidad THEN
                UPDATE USUARIO SET RequiereCambiarPassword = 1 WHERE IdUsuario = :NEW.IdUsuario;
            END IF;
        EXCEPTION
            WHEN NO_DATA_FOUND THEN
                NULL;
        END;
    ELSIF UPPER(v_NombreTipo) IN ('BLOQUEADO - PASSWORD INCORRECTO', 'BLOQUEADO - NUMERO DE INTENTOS EXEDIDOS') THEN
        UPDATE USUARIO
        SET IntentosDeAcceso = IntentosDeAcceso + 1
        WHERE IdUsuario = :NEW.IdUsuario;
    END IF;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        NULL;
END;
/


CREATE OR REPLACE TRIGGER TRG_EMPRESA_BAJA_VALIDA
BEFORE DELETE ON EMPRESA
FOR EACH ROW
DECLARE
    v_Count PLS_INTEGER;
BEGIN
    SELECT COUNT(*) INTO v_Count FROM SUCURSAL WHERE IdEmpresa = :OLD.IdEmpresa;
    IF v_Count > 0 THEN
        RAISE_APPLICATION_ERROR(-20012, 'No se puede eliminar la empresa: tiene sucursales asociadas.');
    END IF;
END;
/

CREATE OR REPLACE TRIGGER TRG_SUCURSAL_BAJA_VALIDA
BEFORE DELETE ON SUCURSAL
FOR EACH ROW
DECLARE
    v_Count PLS_INTEGER;
BEGIN
    SELECT COUNT(*) INTO v_Count FROM USUARIO WHERE IdSucursal = :OLD.IdSucursal;
    IF v_Count > 0 THEN
        RAISE_APPLICATION_ERROR(-20015, 'No se puede eliminar la sucursal: tiene usuarios asociados.');
    END IF;
END;
/

CREATE OR REPLACE TRIGGER TRG_ROLE_BAJA_VALIDA
BEFORE DELETE ON ROLE
FOR EACH ROW
DECLARE
    v_Count PLS_INTEGER;
BEGIN
    SELECT COUNT(*) INTO v_Count FROM USUARIO WHERE IdRole = :OLD.IdRole;
    IF v_Count > 0 THEN
        RAISE_APPLICATION_ERROR(-20010, 'No se puede eliminar el rol: tiene usuarios asignados.');
    END IF;

    SELECT COUNT(*) INTO v_Count FROM ROLE_OPCION WHERE IdRole = :OLD.IdRole;
    IF v_Count > 0 THEN
        RAISE_APPLICATION_ERROR(-20011, 'No se puede eliminar el rol: tiene permisos configurados.');
    END IF;
END;
/

CREATE OR REPLACE TRIGGER TRG_MODULO_BAJA_VALIDA
BEFORE DELETE ON MODULO
FOR EACH ROW
DECLARE
    v_Count PLS_INTEGER;
BEGIN
    SELECT COUNT(*) INTO v_Count FROM MENU WHERE IdModulo = :OLD.IdModulo;
    IF v_Count > 0 THEN
        RAISE_APPLICATION_ERROR(-20013, 'No se puede eliminar el modulo: tiene menus asociados.');
    END IF;
END;
/

CREATE OR REPLACE TRIGGER TRG_MENU_BAJA_VALIDA
BEFORE DELETE ON MENU
FOR EACH ROW
DECLARE
    v_Count PLS_INTEGER;
BEGIN
    SELECT COUNT(*) INTO v_Count FROM OPCION WHERE IdMenu = :OLD.IdMenu;
    IF v_Count > 0 THEN
        RAISE_APPLICATION_ERROR(-20014, 'No se puede eliminar el menu: tiene opciones asociadas.');
    END IF;
END;
/

CREATE OR REPLACE TRIGGER TRG_OPCION_BAJA_VALIDA
BEFORE DELETE ON OPCION
FOR EACH ROW
DECLARE
    v_Count PLS_INTEGER;
BEGIN
    SELECT COUNT(*) INTO v_Count FROM ROLE_OPCION WHERE IdOpcion = :OLD.IdOpcion;
    IF v_Count > 0 THEN
        RAISE_APPLICATION_ERROR(-20016, 'No se puede eliminar la opcion: tiene permisos asignados a roles.');
    END IF;
END;
/


BEGIN
    EXECUTE IMMEDIATE 'DROP TABLE TOKEN_RECUPERACION_PASSWORD CASCADE CONSTRAINTS';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE != -942 THEN
            RAISE;
        END IF;
END;
/

CREATE TABLE TOKEN_RECUPERACION_PASSWORD (
    IdToken         VARCHAR2(100) NOT NULL,
    IdUsuario       VARCHAR2(100) NOT NULL,
    FechaCreacion   DATE NOT NULL,
    FechaExpiracion DATE NOT NULL,
    Utilizado       NUMBER(1) DEFAULT 0 NOT NULL,
    FechaUtilizado  DATE,
    DireccionIp     VARCHAR2(50),
    CONSTRAINT PK_TOKEN_RECUPERACION PRIMARY KEY (IdToken),
    CONSTRAINT FK_TOKEN_RECUPERACION_USUARIO FOREIGN KEY (IdUsuario) REFERENCES USUARIO (IdUsuario)
);

CREATE OR REPLACE TRIGGER TRG_TOKEN_RECUPERACION_DEFAULTS
BEFORE INSERT ON TOKEN_RECUPERACION_PASSWORD
FOR EACH ROW
BEGIN
    :NEW.FechaCreacion   := SYSDATE;
    :NEW.FechaExpiracion := SYSDATE + (60 / 1440);
    :NEW.Utilizado       := 0;
END;
/

CREATE OR REPLACE TRIGGER TRG_TOKEN_RECUPERACION_VALIDA
BEFORE UPDATE OF Utilizado ON TOKEN_RECUPERACION_PASSWORD
FOR EACH ROW
BEGIN
    IF :OLD.Utilizado = 1 THEN
        RAISE_APPLICATION_ERROR(-20030, 'El token de recuperacion ya fue utilizado.');
    END IF;

    IF :NEW.Utilizado = 1 THEN
        :NEW.FechaUtilizado := SYSDATE;
    END IF;
END;
/


BEGIN
    EXECUTE IMMEDIATE 'ALTER TABLE EMPRESA ADD MinutosExpiracionSesion NUMBER DEFAULT 30 NOT NULL';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE != -1430 THEN
            RAISE;
        END IF;
END;
/

BEGIN
    EXECUTE IMMEDIATE 'DROP TABLE SESION CASCADE CONSTRAINTS';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE != -942 THEN
            RAISE;
        END IF;
END;
/

CREATE TABLE SESION (
    IdSesion             VARCHAR2(100) NOT NULL,
    IdUsuario            VARCHAR2(100) NOT NULL,
    FechaInicio          DATE NOT NULL,
    FechaUltimaActividad DATE NOT NULL,
    FechaExpiracion      DATE NOT NULL,
    MinutosVigencia      NUMBER NOT NULL,
    Activa               NUMBER(1) DEFAULT 1 NOT NULL,
    DireccionIp          VARCHAR2(50),
    HttpUserAgent        VARCHAR2(200),
    CONSTRAINT PK_SESION PRIMARY KEY (IdSesion),
    CONSTRAINT FK_SESION_USUARIO FOREIGN KEY (IdUsuario) REFERENCES USUARIO (IdUsuario)
);

CREATE OR REPLACE TRIGGER TRG_SESION_INICIO
BEFORE INSERT ON SESION
FOR EACH ROW
DECLARE
    v_MinutosVigencia EMPRESA.MinutosExpiracionSesion%TYPE;
BEGIN
    BEGIN
        SELECT E.MinutosExpiracionSesion
        INTO v_MinutosVigencia
        FROM EMPRESA E
        JOIN SUCURSAL S ON S.IdEmpresa = E.IdEmpresa
        JOIN USUARIO U ON U.IdSucursal = S.IdSucursal
        WHERE U.IdUsuario = :NEW.IdUsuario;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            v_MinutosVigencia := 30;
    END;

    :NEW.FechaInicio          := SYSDATE;
    :NEW.FechaUltimaActividad := SYSDATE;
    :NEW.FechaExpiracion      := SYSDATE + (v_MinutosVigencia / 1440);
    :NEW.MinutosVigencia      := v_MinutosVigencia;
    :NEW.Activa               := 1;
END;
/

CREATE OR REPLACE TRIGGER TRG_SESION_SYNC_USUARIO
AFTER INSERT ON SESION
FOR EACH ROW
BEGIN
    UPDATE USUARIO
    SET SesionActual = :NEW.IdSesion
    WHERE IdUsuario = :NEW.IdUsuario;
END;
/

CREATE OR REPLACE TRIGGER TRG_SESION_CIERRE_SYNC
AFTER UPDATE OF Activa ON SESION
FOR EACH ROW
WHEN (NEW.Activa = 0 AND OLD.Activa = 1)
BEGIN
    UPDATE USUARIO
    SET SesionActual = NULL
    WHERE IdUsuario = :NEW.IdUsuario
      AND SesionActual = :NEW.IdSesion;
END;
/


BEGIN
    EXECUTE IMMEDIATE 'DROP FUNCTION FN_USUARIO_VALIDAR_CREDENCIALES';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE != -4043 THEN
            RAISE;
        END IF;
END;
/


CREATE OR REPLACE FUNCTION FN_USUARIO_TIPO_ACCESO(
    p_IdUsuario        IN VARCHAR2,
    p_PasswordCorrecto IN NUMBER
) RETURN NUMBER
IS
    v_NombreStatus STATUS_USUARIO.Nombre%TYPE;
    v_IdTipoAcceso TIPO_ACCESO.IdTipoAcceso%TYPE;
BEGIN
    BEGIN
        SELECT S.Nombre
        INTO v_NombreStatus
        FROM USUARIO U
        JOIN STATUS_USUARIO S ON S.IdStatusUsuario = U.IdStatusUsuario
        WHERE U.IdUsuario = p_IdUsuario;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            SELECT IdTipoAcceso INTO v_IdTipoAcceso
            FROM TIPO_ACCESO WHERE UPPER(Nombre) = 'USUARIO INGRESADO NO EXISTE';
            RETURN v_IdTipoAcceso;
    END;

    IF UPPER(v_NombreStatus) = 'INACTIVO' THEN
        SELECT IdTipoAcceso INTO v_IdTipoAcceso
        FROM TIPO_ACCESO WHERE UPPER(Nombre) = 'USUARIO INACTIVO';
        RETURN v_IdTipoAcceso;
    ELSIF UPPER(v_NombreStatus) = 'BLOQUEADO POR INTENTOS DE ACCESO' THEN
        SELECT IdTipoAcceso INTO v_IdTipoAcceso
        FROM TIPO_ACCESO WHERE UPPER(Nombre) = 'BLOQUEADO - NUMERO DE INTENTOS EXEDIDOS';
        RETURN v_IdTipoAcceso;
    END IF;

    IF p_PasswordCorrecto = 1 THEN
        SELECT IdTipoAcceso INTO v_IdTipoAcceso
        FROM TIPO_ACCESO WHERE UPPER(Nombre) = 'ACCESO CONCEDIDO';
    ELSE
        SELECT IdTipoAcceso INTO v_IdTipoAcceso
        FROM TIPO_ACCESO WHERE UPPER(Nombre) = 'BLOQUEADO - PASSWORD INCORRECTO';
    END IF;

    RETURN v_IdTipoAcceso;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RAISE_APPLICATION_ERROR(-20032, 'Catalogo TIPO_ACCESO incompleto: revise los nombres registrados.');
END;
/


CREATE OR REPLACE PROCEDURE PR_SESION_INICIAR(
    p_IdUsuario     IN VARCHAR2,
    p_IdSesion      IN VARCHAR2,
    p_DireccionIp   IN VARCHAR2 DEFAULT NULL,
    p_HttpUserAgent IN VARCHAR2 DEFAULT NULL
)
IS
BEGIN
    UPDATE SESION
    SET Activa = 0
    WHERE IdUsuario = p_IdUsuario
      AND Activa = 1;

    INSERT INTO SESION (IdSesion, IdUsuario, DireccionIp, HttpUserAgent)
    VALUES (p_IdSesion, p_IdUsuario, p_DireccionIp, p_HttpUserAgent);
END;
/


CREATE OR REPLACE PROCEDURE PR_SESION_CERRAR(p_IdSesion IN VARCHAR2)
IS
BEGIN
    UPDATE SESION SET Activa = 0 WHERE IdSesion = p_IdSesion AND Activa = 1;
END;
/


CREATE OR REPLACE FUNCTION FN_SESION_VALIDAR(p_IdSesion IN VARCHAR2)
RETURN NUMBER
IS
    v_Activa          SESION.Activa%TYPE;
    v_FechaExpiracion SESION.FechaExpiracion%TYPE;
    v_MinutosVigencia SESION.MinutosVigencia%TYPE;
BEGIN
    SELECT Activa, FechaExpiracion, MinutosVigencia
    INTO v_Activa, v_FechaExpiracion, v_MinutosVigencia
    FROM SESION
    WHERE IdSesion = p_IdSesion;

    IF v_Activa = 0 THEN
        RETURN 0;
    END IF;

    IF SYSDATE > v_FechaExpiracion THEN
        UPDATE SESION SET Activa = 0 WHERE IdSesion = p_IdSesion;
        RETURN 0;
    END IF;

    UPDATE SESION
    SET FechaUltimaActividad = SYSDATE,
        FechaExpiracion      = SYSDATE + (v_MinutosVigencia / 1440)
    WHERE IdSesion = p_IdSesion;

    RETURN 1;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RETURN 0;
END;
/


CREATE OR REPLACE PROCEDURE PR_TOKEN_RECUPERACION_GENERAR(
    p_IdUsuario   IN VARCHAR2,
    p_IdToken     IN VARCHAR2,
    p_DireccionIp IN VARCHAR2 DEFAULT NULL
)
IS
BEGIN
    UPDATE TOKEN_RECUPERACION_PASSWORD
    SET Utilizado = 1,
        FechaUtilizado = SYSDATE
    WHERE IdUsuario = p_IdUsuario
      AND Utilizado = 0;

    INSERT INTO TOKEN_RECUPERACION_PASSWORD (IdToken, IdUsuario, DireccionIp)
    VALUES (p_IdToken, p_IdUsuario, p_DireccionIp);
END;
/


CREATE OR REPLACE FUNCTION FN_TOKEN_RECUPERACION_VALIDAR(p_IdToken IN VARCHAR2)
RETURN VARCHAR2
IS
    v_IdUsuario       TOKEN_RECUPERACION_PASSWORD.IdUsuario%TYPE;
    v_Utilizado       TOKEN_RECUPERACION_PASSWORD.Utilizado%TYPE;
    v_FechaExpiracion TOKEN_RECUPERACION_PASSWORD.FechaExpiracion%TYPE;
BEGIN
    SELECT IdUsuario, Utilizado, FechaExpiracion
    INTO v_IdUsuario, v_Utilizado, v_FechaExpiracion
    FROM TOKEN_RECUPERACION_PASSWORD
    WHERE IdToken = p_IdToken;

    IF v_Utilizado = 1 OR SYSDATE > v_FechaExpiracion THEN
        RETURN NULL;
    END IF;

    RETURN v_IdUsuario;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RETURN NULL;
END;
/


UPDATE USUARIO
SET Password = '$2b$10$Tlzia1TZ4Gf/xTrxVJmdv.jqVfxGE.ejhrWSetN/njWlq9ZHR1d8C'
WHERE IdUsuario = 'system';

UPDATE USUARIO
SET Password = '$2b$10$sSBYWRBp7kjqu8dslX/HBuR8nliK0WySnbOaI/tTKaAGCxcUlJmna'
WHERE IdUsuario = 'Administrador';

COMMIT;


UPDATE OPCION SET Pagina = 'empresas'             WHERE Pagina = 'empresa.php';
UPDATE OPCION SET Pagina = 'sucursales'           WHERE Pagina = 'sucursal.php';
UPDATE OPCION SET Pagina = 'generos'              WHERE Pagina = 'genero.php';
UPDATE OPCION SET Pagina = 'status-usuario'       WHERE Pagina = 'status_usuario.php';
UPDATE OPCION SET Pagina = 'roles'                WHERE Pagina = 'role.php';
UPDATE OPCION SET Pagina = 'modulos'              WHERE Pagina = 'modulo.php';
UPDATE OPCION SET Pagina = 'menus'                WHERE Pagina = 'menu.php';
UPDATE OPCION SET Pagina = 'opciones'             WHERE Pagina = 'opcion.php';
UPDATE OPCION SET Pagina = 'usuarios'             WHERE Pagina = 'usuario.php';
UPDATE OPCION SET Pagina = 'asignacion-permisos'  WHERE Pagina = 'asignacion_opcion_role.php';

COMMIT;


SELECT table_name FROM user_tables ORDER BY table_name;


SELECT IdUsuario, IdRole, IdStatusUsuario, SUBSTR(Password, 1, 7) AS HashPrefijo
FROM USUARIO ORDER BY IdUsuario;


SELECT mo.Nombre AS Modulo, me.Nombre AS Menu, op.Nombre AS Opcion, op.Pagina
FROM ROLE_OPCION ro
JOIN OPCION op ON op.IdOpcion = ro.IdOpcion
JOIN MENU   me ON me.IdMenu   = op.IdMenu
JOIN MODULO mo ON mo.IdModulo = me.IdModulo
WHERE ro.IdRole = 1
ORDER BY mo.OrdenMenu, me.OrdenMenu, op.OrdenMenu;

