/* 
 * Segunda Fase
 */

use proyectoanalisis;

drop table if exists PLANILLA_DETALLE;
drop table if exists PLANILLA_CABECERA;
drop table if exists PERIODO_PLANILLA;
drop table if exists INASISTENCIA;
drop table if exists CUENTA_BANCARIA_EMPLEADO;
drop table if exists EMPLEADO;
drop table if exists BANCO;
drop table if exists PUESTO;
drop table if exists DOCUMENTO_PERSONA;
drop table if exists PERSONA;
drop table if exists TIPO_DOCUMENTO;
drop table if exists ESTADO_CIVIL;
drop table if exists DEPARTAMENTO;
drop table if exists FLUJO_STATUS_EMPLEADO;
drop table if exists STATUS_EMPLEADO;

create table STATUS_EMPLEADO(
	IdStatusEmpleado int not null auto_increment,
	Nombre varchar(50),
	FechaCreacion datetime not null,
	UsuarioCreacion varchar(100) not null,
	FechaModificacion datetime,
	UsuarioModificacion varchar(100),
	primary key (IdStatusEmpleado)
);

insert into STATUS_EMPLEADO(Nombre,FechaCreacion,UsuarioCreacion,FechaModificacion,UsuarioModificacion)
values
(
    'Activo', NOW(), 'system', NULL, NULL
),
(
    'Suspendido por el IGSS', NOW(), 'system', NULL, NULL
),
(
    'Suspendido por RRHH', NOW(), 'system', NULL, NULL
),
(
    'Baja', NOW(), 'system', NULL, NULL
),
(
    'Despedido', NOW(), 'system', NULL, NULL
);


create table FLUJO_STATUS_EMPLEADO(
	IdStatusActual int not null,
	IdStatusNuevo int not null,
	NombreEvento varchar(50),
	FechaCreacion datetime not null,
	UsuarioCreacion varchar(100) not null,
	FechaModificacion datetime,
	UsuarioModificacion varchar(100),
	primary key (IdStatusActual,IdStatusNuevo),
	foreign key (IdStatusActual) references STATUS_EMPLEADO(IdStatusEmpleado),
	foreign key (IdStatusNuevo) references STATUS_EMPLEADO(IdStatusEmpleado)
);

insert into FLUJO_STATUS_EMPLEADO(IdStatusActual,IdStatusNuevo,NombreEvento,FechaCreacion,UsuarioCreacion,FechaModificacion,UsuarioModificacion)
values
(
	1,2,'Suspencion IGSS', now(), 'system', null, null
),
(
	1,3,'Suspencion del Empleado', now(), 'system', null, null
),
(
	1,4,'Renuncia del Empleado', now(), 'system', null, null
),
(
	1,5,'Despedir Empleado', now(), 'system', null, null
),
(
	2,1,'Reintegracion del Empleado IGSS', now(), 'system', null, null
),
(
	3,1,'Reintegracion del Empleado RH', now(), 'system', null, null
),
(
	4,1,'Recontratacion del Empleado', now(), 'system', null, null
),
(
	3,4,'Empleado Suspendido y Renuncia del Empleado', now(), 'system', null, null
),
(
	3,5,'Empleado Suspendido y Despido del Empleado', now(), 'system', null, null
);


create table DEPARTAMENTO(
	IdDepartamento int not null auto_increment,
	Nombre varchar(50),
	IdEmpresa int,
	FechaCreacion datetime not null,
	UsuarioCreacion varchar(100) not null,
	FechaModificacion datetime,
	UsuarioModificacion varchar(100),
	primary key (IdDepartamento),
	foreign key (IdEmpresa) references EMPRESA(IdEmpresa)
);

insert into DEPARTAMENTO(Nombre, IdEmpresa, FechaCreacion, UsuarioCreacion, FechaModificacion, UsuarioModificacion )
values ('Administraci�n', 1, now(), 'system', null, null)
,('Calidad y Control de Procesos', 1, now(), 'system', null, null)
,('Compras', 1, now(), 'system', null, null)
,('Comunicaciones Corporativas', 1, now(), 'system', null, null)
,('Finanzas', 1, now(), 'system', null, null)
,('Investigacion y Desarrollo (I+D)', 1, now(), 'system', null, null)
,('Legal', 1, now(), 'system', null, null)
,('Logistica', 1, now(), 'system', null, null)
,('Produccion', 1, now(), 'system', null, null)
,('Servicio al Cliente', 1, now(), 'system', null, null)
,('Tecnologias de la Informacion (IT)', 1, now(), 'system', null, null)
,('Ventas y Marketing', 1, now(), 'system', null, null)
,('Recursos Humanos', 1, now(), 'system', null, null);


create table ESTADO_CIVIL(
	IdEstadoCivil int not null auto_increment,
	Nombre varchar(50) not null,
	FechaCreacion datetime not null,
	UsuarioCreacion varchar(100) not null,
	FechaModificacion datetime,
	UsuarioModificacion varchar(100),
	primary key (IdEstadoCivil)
);

insert into ESTADO_CIVIL( Nombre, FechaCreacion, UsuarioCreacion, FechaModificacion, UsuarioModificacion )
values
(
'Casado(a)', now(), 'system', null, null
),
(
'Soltero(a)', now(), 'system', null, null
),
(
'Divorciado(a)', now(), 'system', null, null
),
(
'Viudo(a)', now(), 'system', null, null
),
(
'Union de hecho', now(), 'system', null, null
);

create table TIPO_DOCUMENTO(
	IdTipoDocumento int not null auto_increment,
	Nombre varchar(50) not null,
	FechaCreacion datetime not null,
	UsuarioCreacion varchar(100) not null,
	FechaModificacion datetime,
	UsuarioModificacion varchar(100),
	primary key (IdTipoDocumento)	
);

insert into TIPO_DOCUMENTO( Nombre, FechaCreacion, UsuarioCreacion, FechaModificacion, UsuarioModificacion )
values
(
	'Documento Personal de Identificacion (DPI)', now(), 'system', null, null
),
(
	'Pasaporte', now(), 'system', null, null
),
(
	'NIT', now(), 'system', null, null
),
(
	'Licencia de Conducir', now(), 'system', null, null
),
(
	'Seguro Social IGSS', now(), 'system', null, null
);

create table PERSONA(
	IdPersona int not null auto_increment,
	Nombre varchar(50) not null,
	Apellido varchar(50) not null,
	FechaNacimiento date not null,
	IdGenero int not null,
	Direccion varchar(100) not null,
	Telefono varchar(50) not null,
	CorreoElectronico varchar(50),
	IdEstadoCivil int not null,
	FechaCreacion datetime not null,
	UsuarioCreacion varchar(100) not null,
	FechaModificacion datetime,
	UsuarioModificacion varchar(100),
	primary key (IdPersona),
	foreign key (IdGenero) references GENERO(IdGenero),
	foreign key (IdEstadoCivil) references ESTADO_CIVIL(IdEstadoCivil)
);


INSERT INTO PERSONA (Nombre, Apellido, FechaNacimiento, IdGenero, Direccion, Telefono, CorreoElectronico, IdEstadoCivil, FechaCreacion, UsuarioCreacion) VALUES ('Ernesto Arturo','Quesada Pineda','1992-11-25',1,'Zona 1, Guatemala, Guatemala','502-53180319','equesada@itcompany.com',3,now(),'system');
INSERT INTO PERSONA (Nombre, Apellido, FechaNacimiento, IdGenero, Direccion, Telefono, CorreoElectronico, IdEstadoCivil, FechaCreacion, UsuarioCreacion) VALUES ('Martina Isadora','Navarro Garc�a','2000-10-27',2,'Zona 9, Amatitl�n, Guatemala','502-19084398','mnavarro@itcompany.com',2,now(),'system');
INSERT INTO PERSONA (Nombre, Apellido, FechaNacimiento, IdGenero, Direccion, Telefono, CorreoElectronico, IdEstadoCivil, FechaCreacion, UsuarioCreacion) VALUES ('Marco Miguel','Aguilar R�os','1972-9-20',1,'Zona 1, Chinautla, Guatemala','502-11480761','maguilar@itcompany.com',2,now(),'system');
INSERT INTO PERSONA (Nombre, Apellido, FechaNacimiento, IdGenero, Direccion, Telefono, CorreoElectronico, IdEstadoCivil, FechaCreacion, UsuarioCreacion) VALUES ('Arturo Roberto','Acu�a Paredes','1996-2-16',1,'Zona 2, Palencia, Guatemala','502-36510516','aacu�a@itcompany.com',2,now(),'system');
INSERT INTO PERSONA (Nombre, Apellido, FechaNacimiento, IdGenero, Direccion, Telefono, CorreoElectronico, IdEstadoCivil, FechaCreacion, UsuarioCreacion) VALUES ('Esteban Santiago','Castillo Maldonado','1996-3-2',1,'Zona 1, San Jos� del Golfo, Guatemala','502-70498938','ecastillo@itcompany.com',5,now(),'system');
INSERT INTO PERSONA (Nombre, Apellido, FechaNacimiento, IdGenero, Direccion, Telefono, CorreoElectronico, IdEstadoCivil, FechaCreacion, UsuarioCreacion) VALUES ('Marco �ngel','Quesada Vargas','1991-4-9',1,'Zona 8, San Jos� Pinula, Guatemala','502-37050638','mquesada@itcompany.com',4,now(),'system');
INSERT INTO PERSONA (Nombre, Apellido, FechaNacimiento, IdGenero, Direccion, Telefono, CorreoElectronico, IdEstadoCivil, FechaCreacion, UsuarioCreacion) VALUES ('Catalina Julia','N��ez Ar�valo','1991-3-2',2,'Zona 5, San Raymundo, Guatemala','502-43444178','cn��ez@itcompany.com',3,now(),'system');
INSERT INTO PERSONA (Nombre, Apellido, FechaNacimiento, IdGenero, Direccion, Telefono, CorreoElectronico, IdEstadoCivil, FechaCreacion, UsuarioCreacion) VALUES ('Santiago Pedro','Alvarado Rodr�guez','1976-1-5',1,'Zona 4, Santa Catarina Pinula, Guatemala','502-98151024','salvarado@itcompany.com',3,now(),'system');
INSERT INTO PERSONA (Nombre, Apellido, FechaNacimiento, IdGenero, Direccion, Telefono, CorreoElectronico, IdEstadoCivil, FechaCreacion, UsuarioCreacion) VALUES ('Laura Paola','Pineda Cede�o','2010-8-15',2,'Zona 8, Villa Canales, Guatemala','502-16310841','lpineda@itcompany.com',4,now(),'system');
INSERT INTO PERSONA (Nombre, Apellido, FechaNacimiento, IdGenero, Direccion, Telefono, CorreoElectronico, IdEstadoCivil, FechaCreacion, UsuarioCreacion) VALUES ('Julia Antonella','Mora Villalobos','1982-9-2',2,'Zona 8, Villa Nueva, Guatemala','502-82132635','jmora@itcompany.com',3,now(),'system');
INSERT INTO PERSONA (Nombre, Apellido, FechaNacimiento, IdGenero, Direccion, Telefono, CorreoElectronico, IdEstadoCivil, FechaCreacion, UsuarioCreacion) VALUES ('Juan Sergio','Pacheco Arias','2004-6-27',1,'Zona 10, Guatemala, Guatemala','502-55628911','jpacheco@itcompany.com',4,now(),'system');
INSERT INTO PERSONA (Nombre, Apellido, FechaNacimiento, IdGenero, Direccion, Telefono, CorreoElectronico, IdEstadoCivil, FechaCreacion, UsuarioCreacion) VALUES ('Javier Arturo','Garc�a Alp�zar','1992-6-24',1,'Zona 6, Amatitl�n, Guatemala','502-22178495','jgarc�a@itcompany.com',4,now(),'system');
INSERT INTO PERSONA (Nombre, Apellido, FechaNacimiento, IdGenero, Direccion, Telefono, CorreoElectronico, IdEstadoCivil, FechaCreacion, UsuarioCreacion) VALUES ('Arturo Mateo','Chac�n Paredes','2008-12-31',1,'Zona 6, Chinautla, Guatemala','502-72351395','achac�n@itcompany.com',2,now(),'system');
INSERT INTO PERSONA (Nombre, Apellido, FechaNacimiento, IdGenero, Direccion, Telefono, CorreoElectronico, IdEstadoCivil, FechaCreacion, UsuarioCreacion) VALUES ('V�ctor Leonardo','Gonz�lez Carvajal','1986-7-25',1,'Zona 3, Palencia, Guatemala','502-43451258','vgonz�lez@itcompany.com',3,now(),'system');
INSERT INTO PERSONA (Nombre, Apellido, FechaNacimiento, IdGenero, Direccion, Telefono, CorreoElectronico, IdEstadoCivil, FechaCreacion, UsuarioCreacion) VALUES ('Emilio Alejandro','Lara Z��iga','1993-12-19',1,'Zona 5, San Jos� del Golfo, Guatemala','502-70991044','elara@itcompany.com',2,now(),'system');
INSERT INTO PERSONA (Nombre, Apellido, FechaNacimiento, IdGenero, Direccion, Telefono, CorreoElectronico, IdEstadoCivil, FechaCreacion, UsuarioCreacion) VALUES ('David Omar','N��ez Duarte','2001-11-20',1,'Zona 4, Villa Canales, Guatemala','502-73567915','dn��ez@itcompany.com',1,now(),'system');
INSERT INTO PERSONA (Nombre, Apellido, FechaNacimiento, IdGenero, Direccion, Telefono, CorreoElectronico, IdEstadoCivil, FechaCreacion, UsuarioCreacion) VALUES ('Mario Marco','Zamora Medina','1992-3-16',1,'Zona 5, Villa Nueva, Guatemala','502-37441663','mzamora@itcompany.com',3,now(),'system');
INSERT INTO PERSONA (Nombre, Apellido, FechaNacimiento, IdGenero, Direccion, Telefono, CorreoElectronico, IdEstadoCivil, FechaCreacion, UsuarioCreacion) VALUES ('Guadalupe Isadora','Arias Segura','1981-3-14',2,'Zona 6, Guatemala, Guatemala','502-75237203','garias@itcompany.com',5,now(),'system');
INSERT INTO PERSONA (Nombre, Apellido, FechaNacimiento, IdGenero, Direccion, Telefono, CorreoElectronico, IdEstadoCivil, FechaCreacion, UsuarioCreacion) VALUES ('Pedro Hugo','Mora Rodr�guez','1991-8-31',1,'Zona 8, Chinautla, Guatemala','502-06421115','pmora@itcompany.com',4,now(),'system');
INSERT INTO PERSONA (Nombre, Apellido, FechaNacimiento, IdGenero, Direccion, Telefono, CorreoElectronico, IdEstadoCivil, FechaCreacion, UsuarioCreacion) VALUES ('Daniela Renata','R�os Uma�a','1970-6-21',2,'Zona 3, Chuarrancho, Guatemala','502-85180897','dr�os@itcompany.com',3,now(),'system');
INSERT INTO PERSONA (Nombre, Apellido, FechaNacimiento, IdGenero, Direccion, Telefono, CorreoElectronico, IdEstadoCivil, FechaCreacion, UsuarioCreacion) VALUES ('Santiago David','Paredes Villalobos','1983-3-28',1,'Zona 4, Fraijanes, Guatemala','502-77414408','sparedes@itcompany.com',5,now(),'system');
INSERT INTO PERSONA (Nombre, Apellido, FechaNacimiento, IdGenero, Direccion, Telefono, CorreoElectronico, IdEstadoCivil, FechaCreacion, UsuarioCreacion) VALUES ('Lucia Catalina','Reyes Delgado','2006-10-12',2,'Zona 5, Mixco, Guatemala','502-30025579','lreyes@itcompany.com',4,now(),'system');
INSERT INTO PERSONA (Nombre, Apellido, FechaNacimiento, IdGenero, Direccion, Telefono, CorreoElectronico, IdEstadoCivil, FechaCreacion, UsuarioCreacion) VALUES ('Paola Celeste','Mora Maldonado','1977-6-11',2,'Zona 10, Palencia, Guatemala','502-24655941','pmora@itcompany.com',5,now(),'system');
INSERT INTO PERSONA (Nombre, Apellido, FechaNacimiento, IdGenero, Direccion, Telefono, CorreoElectronico, IdEstadoCivil, FechaCreacion, UsuarioCreacion) VALUES ('Sebasti�n Esteban','Alvarado Duarte','2009-1-21',1,'Zona 7, San Jos� del Golfo, Guatemala','502-57517858','salvarado@itcompany.com',2,now(),'system');
INSERT INTO PERSONA (Nombre, Apellido, FechaNacimiento, IdGenero, Direccion, Telefono, CorreoElectronico, IdEstadoCivil, FechaCreacion, UsuarioCreacion) VALUES ('Isadora Isadora','Mora Cordero','1992-6-7',2,'Zona 7, San Pedro Ayampuc, Guatemala','502-58426053','imora@itcompany.com',2,now(),'system');
INSERT INTO PERSONA (Nombre, Apellido, FechaNacimiento, IdGenero, Direccion, Telefono, CorreoElectronico, IdEstadoCivil, FechaCreacion, UsuarioCreacion) VALUES ('Alejandro Miguel','Alvarado V�squez','1972-3-21',1,'Zona 4, San Pedro Sacatep�quez, Guatemala','502-07539461','aalvarado@itcompany.com',4,now(),'system');
INSERT INTO PERSONA (Nombre, Apellido, FechaNacimiento, IdGenero, Direccion, Telefono, CorreoElectronico, IdEstadoCivil, FechaCreacion, UsuarioCreacion) VALUES ('Paula Celeste','Arias Arias','1994-7-28',2,'Zona 6, San Raymundo, Guatemala','502-48556222','parias@itcompany.com',5,now(),'system');
INSERT INTO PERSONA (Nombre, Apellido, FechaNacimiento, IdGenero, Direccion, Telefono, CorreoElectronico, IdEstadoCivil, FechaCreacion, UsuarioCreacion) VALUES ('Luis Nicol�s','Pe�a Arias','2002-11-1',1,'Zona 6, Santa Catarina Pinula, Guatemala','502-36341511','lpe�a@itcompany.com',5,now(),'system');
INSERT INTO PERSONA (Nombre, Apellido, FechaNacimiento, IdGenero, Direccion, Telefono, CorreoElectronico, IdEstadoCivil, FechaCreacion, UsuarioCreacion) VALUES ('Emilio Mario','Cede�o Quir�s','1983-6-1',1,'Zona 3, Villa Canales, Guatemala','502-05586542','ecede�o@itcompany.com',1,now(),'system');
INSERT INTO PERSONA (Nombre, Apellido, FechaNacimiento, IdGenero, Direccion, Telefono, CorreoElectronico, IdEstadoCivil, FechaCreacion, UsuarioCreacion) VALUES ('Elena Natalia','Alp�zar Cede�o','1991-11-9',2,'Zona 7, Villa Nueva, Guatemala','502-88627313','ealp�zar@itcompany.com',2,now(),'system');
INSERT INTO PERSONA (Nombre, Apellido, FechaNacimiento, IdGenero, Direccion, Telefono, CorreoElectronico, IdEstadoCivil, FechaCreacion, UsuarioCreacion) VALUES ('Benjam�n Alejandro','R�os Le�n','2009-7-6',1,'Zona 9, Guatemala, Guatemala','502-06074402','br�os@itcompany.com',2,now(),'system');
INSERT INTO PERSONA (Nombre, Apellido, FechaNacimiento, IdGenero, Direccion, Telefono, CorreoElectronico, IdEstadoCivil, FechaCreacion, UsuarioCreacion) VALUES ('Gabriel Luis','Z��iga Gonz�lez','1993-11-6',1,'Zona 8, Amatitl�n, Guatemala','502-53462658','gz��iga@itcompany.com',4,now(),'system');
INSERT INTO PERSONA (Nombre, Apellido, FechaNacimiento, IdGenero, Direccion, Telefono, CorreoElectronico, IdEstadoCivil, FechaCreacion, UsuarioCreacion) VALUES ('Jorge Emilio','Chac�n Maldonado','1976-11-13',1,'Zona 5, Chinautla, Guatemala','502-86606837','jchac�n@itcompany.com',3,now(),'system');
INSERT INTO PERSONA (Nombre, Apellido, FechaNacimiento, IdGenero, Direccion, Telefono, CorreoElectronico, IdEstadoCivil, FechaCreacion, UsuarioCreacion) VALUES ('Beatriz Isadora','Segura Vega','1977-8-23',2,'Zona 1, Mixco, Guatemala','502-98076958','bsegura@itcompany.com',3,now(),'system');
INSERT INTO PERSONA (Nombre, Apellido, FechaNacimiento, IdGenero, Direccion, Telefono, CorreoElectronico, IdEstadoCivil, FechaCreacion, UsuarioCreacion) VALUES ('Emilio Sergio','Guzm�n Coto','1988-6-5',1,'Zona 9, Palencia, Guatemala','502-65185781','eguzm�n@itcompany.com',1,now(),'system');
INSERT INTO PERSONA (Nombre, Apellido, FechaNacimiento, IdGenero, Direccion, Telefono, CorreoElectronico, IdEstadoCivil, FechaCreacion, UsuarioCreacion) VALUES ('Javier H�ctor','Soto P�rez','1986-5-15',1,'Zona 3, San Jos� del Golfo, Guatemala','502-84759321','jsoto@itcompany.com',1,now(),'system');
INSERT INTO PERSONA (Nombre, Apellido, FechaNacimiento, IdGenero, Direccion, Telefono, CorreoElectronico, IdEstadoCivil, FechaCreacion, UsuarioCreacion) VALUES ('Luis Sebasti�n','M�ndez Arias','1982-12-23',1,'Zona 9, San Jos� Pinula, Guatemala','502-47450455','lm�ndez@itcompany.com',1,now(),'system');
INSERT INTO PERSONA (Nombre, Apellido, FechaNacimiento, IdGenero, Direccion, Telefono, CorreoElectronico, IdEstadoCivil, FechaCreacion, UsuarioCreacion) VALUES ('Maria Alejandra','Miranda Gonz�lez','1971-9-19',2,'Zona 1, San Juan Sacatep�quez, Guatemala','502-24682640','mmiranda@itcompany.com',1,now(),'system');
INSERT INTO PERSONA (Nombre, Apellido, FechaNacimiento, IdGenero, Direccion, Telefono, CorreoElectronico, IdEstadoCivil, FechaCreacion, UsuarioCreacion) VALUES ('�ngel Ricardo','Bola�os C�rdova','1974-7-7',1,'Zona 4, San Pedro Ayampuc, Guatemala','502-47793964','�bola�os@itcompany.com',3,now(),'system');
INSERT INTO PERSONA (Nombre, Apellido, FechaNacimiento, IdGenero, Direccion, Telefono, CorreoElectronico, IdEstadoCivil, FechaCreacion, UsuarioCreacion) VALUES ('Francisco Iv�n','Zamora Bola�os','1981-12-25',1,'Zona 4, San Pedro Sacatep�quez, Guatemala','502-28738818','fzamora@itcompany.com',1,now(),'system');
INSERT INTO PERSONA (Nombre, Apellido, FechaNacimiento, IdGenero, Direccion, Telefono, CorreoElectronico, IdEstadoCivil, FechaCreacion, UsuarioCreacion) VALUES ('Santiago Emilio','Bola�os Jara','2001-12-1',1,'Zona 8, San Raymundo, Guatemala','502-22921533','sbola�os@itcompany.com',3,now(),'system');
INSERT INTO PERSONA (Nombre, Apellido, FechaNacimiento, IdGenero, Direccion, Telefono, CorreoElectronico, IdEstadoCivil, FechaCreacion, UsuarioCreacion) VALUES ('Martina Elena','Maldonado Soto','1983-6-4',2,'Zona 4, Santa Catarina Pinula, Guatemala','502-85584510','mmaldonado@itcompany.com',4,now(),'system');
INSERT INTO PERSONA (Nombre, Apellido, FechaNacimiento, IdGenero, Direccion, Telefono, CorreoElectronico, IdEstadoCivil, FechaCreacion, UsuarioCreacion) VALUES ('Sofia Antonella','Castillo Esquivel','1983-7-3',2,'Zona 3, Villa Canales, Guatemala','502-38388355','scastillo@itcompany.com',2,now(),'system');
INSERT INTO PERSONA (Nombre, Apellido, FechaNacimiento, IdGenero, Direccion, Telefono, CorreoElectronico, IdEstadoCivil, FechaCreacion, UsuarioCreacion) VALUES ('Romina Alejandra','Le�n L�pez','2010-9-16',2,'Zona 9, Villa Nueva, Guatemala','502-98402323','rle�n@itcompany.com',5,now(),'system');
INSERT INTO PERSONA (Nombre, Apellido, FechaNacimiento, IdGenero, Direccion, Telefono, CorreoElectronico, IdEstadoCivil, FechaCreacion, UsuarioCreacion) VALUES ('Esteban Santiago','Castro Valverde','1997-3-19',1,'Zona 10, Guatemala, Guatemala','502-59904379','ecastro@itcompany.com',4,now(),'system');
INSERT INTO PERSONA (Nombre, Apellido, FechaNacimiento, IdGenero, Direccion, Telefono, CorreoElectronico, IdEstadoCivil, FechaCreacion, UsuarioCreacion) VALUES ('Hugo Ernesto','Castro Maldonado','1996-4-5',1,'Zona 1, Amatitl�n, Guatemala','502-09522215','hcastro@itcompany.com',2,now(),'system');
INSERT INTO PERSONA (Nombre, Apellido, FechaNacimiento, IdGenero, Direccion, Telefono, CorreoElectronico, IdEstadoCivil, FechaCreacion, UsuarioCreacion) VALUES ('Emilia Valentina','Castro R�os','1972-3-1',2,'Zona 8, Chinautla, Guatemala','502-49293794','ecastro@itcompany.com',4,now(),'system');
INSERT INTO PERSONA (Nombre, Apellido, FechaNacimiento, IdGenero, Direccion, Telefono, CorreoElectronico, IdEstadoCivil, FechaCreacion, UsuarioCreacion) VALUES ('Alejandro Rafael','Gonz�lez Castillo','1985-5-12',1,'Zona 5, Chuarrancho, Guatemala','502-22884221','agonz�lez@itcompany.com',1,now(),'system');
INSERT INTO PERSONA (Nombre, Apellido, FechaNacimiento, IdGenero, Direccion, Telefono, CorreoElectronico, IdEstadoCivil, FechaCreacion, UsuarioCreacion) VALUES ('Emilia Paola','Pe�a Solano','1997-11-2',2,'Zona 10, Fraijanes, Guatemala','502-39412054','epe�a@itcompany.com',4,now(),'system');
INSERT INTO PERSONA (Nombre, Apellido, FechaNacimiento, IdGenero, Direccion, Telefono, CorreoElectronico, IdEstadoCivil, FechaCreacion, UsuarioCreacion) VALUES ('Valentina Isadora','Fuentes Aguilar','2001-1-2',2,'Zona 6, Mixco, Guatemala','502-41765628','vfuentes@itcompany.com',5,now(),'system');
INSERT INTO PERSONA (Nombre, Apellido, FechaNacimiento, IdGenero, Direccion, Telefono, CorreoElectronico, IdEstadoCivil, FechaCreacion, UsuarioCreacion) VALUES ('Ana Maria','Jim�nez Acu�a','1989-5-21',2,'Zona 3, Palencia, Guatemala','502-94155250','ajim�nez@itcompany.com',2,now(),'system');
INSERT INTO PERSONA (Nombre, Apellido, FechaNacimiento, IdGenero, Direccion, Telefono, CorreoElectronico, IdEstadoCivil, FechaCreacion, UsuarioCreacion) VALUES ('Mateo Lucas','Figueroa Vargas','1975-7-10',1,'Zona 4, San Jos� del Golfo, Guatemala','502-97079182','mfigueroa@itcompany.com',1,now(),'system');
INSERT INTO PERSONA (Nombre, Apellido, FechaNacimiento, IdGenero, Direccion, Telefono, CorreoElectronico, IdEstadoCivil, FechaCreacion, UsuarioCreacion) VALUES ('Renata Renata','Montero Solano','1994-7-16',2,'Zona 4, San Jos� Pinula, Guatemala','502-75074666','rmontero@itcompany.com',4,now(),'system');
INSERT INTO PERSONA (Nombre, Apellido, FechaNacimiento, IdGenero, Direccion, Telefono, CorreoElectronico, IdEstadoCivil, FechaCreacion, UsuarioCreacion) VALUES ('Carlos Roberto','Soto Pe�a','1973-9-8',1,'Zona 6, San Juan Sacatep�quez, Guatemala','502-84886362','csoto@itcompany.com',4,now(),'system');
INSERT INTO PERSONA (Nombre, Apellido, FechaNacimiento, IdGenero, Direccion, Telefono, CorreoElectronico, IdEstadoCivil, FechaCreacion, UsuarioCreacion) VALUES ('Diana Isadora','Barrantes Mar�n','2001-10-23',2,'Zona 9, San Pedro Ayampuc, Guatemala','502-27123835','dbarrantes@itcompany.com',5,now(),'system');
INSERT INTO PERSONA (Nombre, Apellido, FechaNacimiento, IdGenero, Direccion, Telefono, CorreoElectronico, IdEstadoCivil, FechaCreacion, UsuarioCreacion) VALUES ('Natalia Rocio','Bola�os Gonz�lez','1978-3-6',2,'Zona 3, San Pedro Sacatep�quez, Guatemala','502-62835249','nbola�os@itcompany.com',2,now(),'system');
INSERT INTO PERSONA (Nombre, Apellido, FechaNacimiento, IdGenero, Direccion, Telefono, CorreoElectronico, IdEstadoCivil, FechaCreacion, UsuarioCreacion) VALUES ('Mario Benjam�n','Flores Brenes','1980-8-24',1,'Zona 3, San Raymundo, Guatemala','502-71311219','mflores@itcompany.com',4,now(),'system');
INSERT INTO PERSONA (Nombre, Apellido, FechaNacimiento, IdGenero, Direccion, Telefono, CorreoElectronico, IdEstadoCivil, FechaCreacion, UsuarioCreacion) VALUES ('Sofia Paula','Solano Ram�rez','1982-12-22',2,'Zona 3, Santa Catarina Pinula, Guatemala','502-93185766','ssolano@itcompany.com',4,now(),'system');
INSERT INTO PERSONA (Nombre, Apellido, FechaNacimiento, IdGenero, Direccion, Telefono, CorreoElectronico, IdEstadoCivil, FechaCreacion, UsuarioCreacion) VALUES ('Laura Laura','Arias Campos','1980-6-1',2,'Zona 4, Villa Canales, Guatemala','502-23140146','larias@itcompany.com',5,now(),'system');
INSERT INTO PERSONA (Nombre, Apellido, FechaNacimiento, IdGenero, Direccion, Telefono, CorreoElectronico, IdEstadoCivil, FechaCreacion, UsuarioCreacion) VALUES ('Natalia Renata','Fern�ndez Uma�a','1990-7-18',2,'Zona 8, Villa Nueva, Guatemala','502-64176199','nfern�ndez@itcompany.com',3,now(),'system');
INSERT INTO PERSONA (Nombre, Apellido, FechaNacimiento, IdGenero, Direccion, Telefono, CorreoElectronico, IdEstadoCivil, FechaCreacion, UsuarioCreacion) VALUES ('Clara Carolina','Bonilla Rivas','1992-9-6',2,'Zona 1, Guatemala, Guatemala','502-89209226','cbonilla@itcompany.com',4,now(),'system');
INSERT INTO PERSONA (Nombre, Apellido, FechaNacimiento, IdGenero, Direccion, Telefono, CorreoElectronico, IdEstadoCivil, FechaCreacion, UsuarioCreacion) VALUES ('Rafael Fernando','Pineda Medina','2007-8-5',1,'Zona 8, Amatitl�n, Guatemala','502-77574372','rpineda@itcompany.com',5,now(),'system');
INSERT INTO PERSONA (Nombre, Apellido, FechaNacimiento, IdGenero, Direccion, Telefono, CorreoElectronico, IdEstadoCivil, FechaCreacion, UsuarioCreacion) VALUES ('Francisco Alejandro','Ar�valo Delgado','1980-4-24',1,'Zona 9, Chinautla, Guatemala','502-04644537','far�valo@itcompany.com',4,now(),'system');
INSERT INTO PERSONA (Nombre, Apellido, FechaNacimiento, IdGenero, Direccion, Telefono, CorreoElectronico, IdEstadoCivil, FechaCreacion, UsuarioCreacion) VALUES ('Sergio Mario','Montero Alp�zar','2008-6-28',1,'Zona 2, Chuarrancho, Guatemala','502-57070513','smontero@itcompany.com',5,now(),'system');
INSERT INTO PERSONA (Nombre, Apellido, FechaNacimiento, IdGenero, Direccion, Telefono, CorreoElectronico, IdEstadoCivil, FechaCreacion, UsuarioCreacion) VALUES ('Antonella Andrea','Cruz Valverde','1998-7-10',2,'Zona 5, Fraijanes, Guatemala','502-07258181','acruz@itcompany.com',2,now(),'system');
INSERT INTO PERSONA (Nombre, Apellido, FechaNacimiento, IdGenero, Direccion, Telefono, CorreoElectronico, IdEstadoCivil, FechaCreacion, UsuarioCreacion) VALUES ('Esteban Ra�l','Pacheco N��ez','1990-10-25',1,'Zona 1, Mixco, Guatemala','502-52682800','epacheco@itcompany.com',4,now(),'system');
INSERT INTO PERSONA (Nombre, Apellido, FechaNacimiento, IdGenero, Direccion, Telefono, CorreoElectronico, IdEstadoCivil, FechaCreacion, UsuarioCreacion) VALUES ('Lucas Emilio','Valencia Maldonado','1985-6-28',1,'Zona 4, Palencia, Guatemala','502-59563499','lvalencia@itcompany.com',2,now(),'system');
INSERT INTO PERSONA (Nombre, Apellido, FechaNacimiento, IdGenero, Direccion, Telefono, CorreoElectronico, IdEstadoCivil, FechaCreacion, UsuarioCreacion) VALUES ('Emilia Valeria','Segura Benavides','2003-12-1',2,'Zona 6, San Jos� del Golfo, Guatemala','502-60991618','esegura@itcompany.com',1,now(),'system');
INSERT INTO PERSONA (Nombre, Apellido, FechaNacimiento, IdGenero, Direccion, Telefono, CorreoElectronico, IdEstadoCivil, FechaCreacion, UsuarioCreacion) VALUES ('Luciana Amanda','Mart�nez Fuentes','1980-6-10',2,'Zona 1, San Jos� Pinula, Guatemala','502-56710585','lmart�nez@itcompany.com',1,now(),'system');
INSERT INTO PERSONA (Nombre, Apellido, FechaNacimiento, IdGenero, Direccion, Telefono, CorreoElectronico, IdEstadoCivil, FechaCreacion, UsuarioCreacion) VALUES ('Maria Beatriz','Pe�a Navarro','1995-3-6',2,'Zona 3, San Juan Sacatep�quez, Guatemala','502-59368137','mpe�a@itcompany.com',5,now(),'system');
INSERT INTO PERSONA (Nombre, Apellido, FechaNacimiento, IdGenero, Direccion, Telefono, CorreoElectronico, IdEstadoCivil, FechaCreacion, UsuarioCreacion) VALUES ('Adriana Elena','Pe�a R�os','1994-8-3',2,'Zona 10, San Pedro Ayampuc, Guatemala','502-31026525','ape�a@itcompany.com',3,now(),'system');
INSERT INTO PERSONA (Nombre, Apellido, FechaNacimiento, IdGenero, Direccion, Telefono, CorreoElectronico, IdEstadoCivil, FechaCreacion, UsuarioCreacion) VALUES ('Gabriel Javier','L�pez Castro','1979-1-29',1,'Zona 1, San Pedro Sacatep�quez, Guatemala','502-07262832','gl�pez@itcompany.com',2,now(),'system');
INSERT INTO PERSONA (Nombre, Apellido, FechaNacimiento, IdGenero, Direccion, Telefono, CorreoElectronico, IdEstadoCivil, FechaCreacion, UsuarioCreacion) VALUES ('Pedro Sebasti�n','Araya Maldonado','1997-6-17',1,'Zona 7, San Raymundo, Guatemala','502-63104226','paraya@itcompany.com',4,now(),'system');
INSERT INTO PERSONA (Nombre, Apellido, FechaNacimiento, IdGenero, Direccion, Telefono, CorreoElectronico, IdEstadoCivil, FechaCreacion, UsuarioCreacion) VALUES ('Roberto Santiago','Zamora Quir�s','2009-1-29',1,'Zona 9, Santa Catarina Pinula, Guatemala','502-87030204','rzamora@itcompany.com',4,now(),'system');
INSERT INTO PERSONA (Nombre, Apellido, FechaNacimiento, IdGenero, Direccion, Telefono, CorreoElectronico, IdEstadoCivil, FechaCreacion, UsuarioCreacion) VALUES ('Mateo Roberto','Solano Castro','1973-6-21',1,'Zona 1, Villa Canales, Guatemala','502-12922659','msolano@itcompany.com',1,now(),'system');
INSERT INTO PERSONA (Nombre, Apellido, FechaNacimiento, IdGenero, Direccion, Telefono, CorreoElectronico, IdEstadoCivil, FechaCreacion, UsuarioCreacion) VALUES ('Eduardo H�ctor','Cordero R�os','1982-3-17',1,'Zona 4, Villa Nueva, Guatemala','502-40181811','ecordero@itcompany.com',1,now(),'system');
INSERT INTO PERSONA (Nombre, Apellido, FechaNacimiento, IdGenero, Direccion, Telefono, CorreoElectronico, IdEstadoCivil, FechaCreacion, UsuarioCreacion) VALUES ('Carlos Diego','Pe�a Reyes','1991-11-25',1,'Zona 9, Guatemala, Guatemala','502-48412635','cpe�a@itcompany.com',5,now(),'system');
INSERT INTO PERSONA (Nombre, Apellido, FechaNacimiento, IdGenero, Direccion, Telefono, CorreoElectronico, IdEstadoCivil, FechaCreacion, UsuarioCreacion) VALUES ('Carlos Luis','Castro Torres','2004-2-5',1,'Zona 8, Amatitl�n, Guatemala','502-58186306','ccastro@itcompany.com',5,now(),'system');
INSERT INTO PERSONA (Nombre, Apellido, FechaNacimiento, IdGenero, Direccion, Telefono, CorreoElectronico, IdEstadoCivil, FechaCreacion, UsuarioCreacion) VALUES ('Isabel Laura','Jara Quir�s','1991-5-27',2,'Zona 8, Chinautla, Guatemala','502-38662740','ijara@itcompany.com',1,now(),'system');
INSERT INTO PERSONA (Nombre, Apellido, FechaNacimiento, IdGenero, Direccion, Telefono, CorreoElectronico, IdEstadoCivil, FechaCreacion, UsuarioCreacion) VALUES ('Renata Beatriz','L�pez Zamora','1979-11-2',2,'Zona 9, Chuarrancho, Guatemala','502-11849048','rl�pez@itcompany.com',4,now(),'system');
INSERT INTO PERSONA (Nombre, Apellido, FechaNacimiento, IdGenero, Direccion, Telefono, CorreoElectronico, IdEstadoCivil, FechaCreacion, UsuarioCreacion) VALUES ('Esteban Carlos','Torres Figueroa','1988-3-16',1,'Zona 5, Fraijanes, Guatemala','502-00602000','etorres@itcompany.com',5,now(),'system');
INSERT INTO PERSONA (Nombre, Apellido, FechaNacimiento, IdGenero, Direccion, Telefono, CorreoElectronico, IdEstadoCivil, FechaCreacion, UsuarioCreacion) VALUES ('Valentina Maria','Paredes Jim�nez','2008-8-22',2,'Zona 7, Mixco, Guatemala','502-60725733','vparedes@itcompany.com',5,now(),'system');
INSERT INTO PERSONA (Nombre, Apellido, FechaNacimiento, IdGenero, Direccion, Telefono, CorreoElectronico, IdEstadoCivil, FechaCreacion, UsuarioCreacion) VALUES ('Gabriela Carolina','Bola�os Rivas','1989-3-31',2,'Zona 7, Palencia, Guatemala','502-67590883','gbola�os@itcompany.com',4,now(),'system');
INSERT INTO PERSONA (Nombre, Apellido, FechaNacimiento, IdGenero, Direccion, Telefono, CorreoElectronico, IdEstadoCivil, FechaCreacion, UsuarioCreacion) VALUES ('Iv�n Arturo','Valencia Herrera','1996-3-17',1,'Zona 7, San Jos� del Golfo, Guatemala','502-10344907','ivalencia@itcompany.com',5,now(),'system');
INSERT INTO PERSONA (Nombre, Apellido, FechaNacimiento, IdGenero, Direccion, Telefono, CorreoElectronico, IdEstadoCivil, FechaCreacion, UsuarioCreacion) VALUES ('Sebasti�n Ricardo','Z��iga Solano','1976-12-3',1,'Zona 8, San Jos� Pinula, Guatemala','502-21672231','sz��iga@itcompany.com',5,now(),'system');
INSERT INTO PERSONA (Nombre, Apellido, FechaNacimiento, IdGenero, Direccion, Telefono, CorreoElectronico, IdEstadoCivil, FechaCreacion, UsuarioCreacion) VALUES ('Hugo Carlos','Solano Romero','1986-11-12',1,'Zona 7, San Juan Sacatep�quez, Guatemala','502-73481164','hsolano@itcompany.com',4,now(),'system');
INSERT INTO PERSONA (Nombre, Apellido, FechaNacimiento, IdGenero, Direccion, Telefono, CorreoElectronico, IdEstadoCivil, FechaCreacion, UsuarioCreacion) VALUES ('Mariana Fernanda','Castro Zamora','1975-12-23',2,'Zona 3, San Pedro Ayampuc, Guatemala','502-54175987','mcastro@itcompany.com',5,now(),'system');
INSERT INTO PERSONA (Nombre, Apellido, FechaNacimiento, IdGenero, Direccion, Telefono, CorreoElectronico, IdEstadoCivil, FechaCreacion, UsuarioCreacion) VALUES ('Santiago Luis','Zamora Solano','1992-6-13',1,'Zona 10, San Pedro Sacatep�quez, Guatemala','502-54635987','szamora@itcompany.com',2,now(),'system');
INSERT INTO PERSONA (Nombre, Apellido, FechaNacimiento, IdGenero, Direccion, Telefono, CorreoElectronico, IdEstadoCivil, FechaCreacion, UsuarioCreacion) VALUES ('Daniel Antonio','Bonilla Carvajal','2009-5-26',1,'Zona 6, San Raymundo, Guatemala','502-97381960','dbonilla@itcompany.com',3,now(),'system');
INSERT INTO PERSONA (Nombre, Apellido, FechaNacimiento, IdGenero, Direccion, Telefono, CorreoElectronico, IdEstadoCivil, FechaCreacion, UsuarioCreacion) VALUES ('Carolina Laura','Acu�a Aguilar','1971-6-10',2,'Zona 4, Santa Catarina Pinula, Guatemala','502-75885729','cacu�a@itcompany.com',2,now(),'system');
INSERT INTO PERSONA (Nombre, Apellido, FechaNacimiento, IdGenero, Direccion, Telefono, CorreoElectronico, IdEstadoCivil, FechaCreacion, UsuarioCreacion) VALUES ('Paola Elena','Bola�os Castro','2000-4-23',2,'Zona 8, Villa Canales, Guatemala','502-33040063','pbola�os@itcompany.com',5,now(),'system');
INSERT INTO PERSONA (Nombre, Apellido, FechaNacimiento, IdGenero, Direccion, Telefono, CorreoElectronico, IdEstadoCivil, FechaCreacion, UsuarioCreacion) VALUES ('Paula Rocio','Barrantes Bola�os','1985-6-26',2,'Zona 3, Villa Nueva, Guatemala','502-76613821','pbarrantes@itcompany.com',2,now(),'system');
INSERT INTO PERSONA (Nombre, Apellido, FechaNacimiento, IdGenero, Direccion, Telefono, CorreoElectronico, IdEstadoCivil, FechaCreacion, UsuarioCreacion) VALUES ('Maria Martina','Bonilla Z��iga','2001-4-7',2,'Zona 10, Guatemala, Guatemala','502-91974450','mbonilla@itcompany.com',5,now(),'system');
INSERT INTO PERSONA (Nombre, Apellido, FechaNacimiento, IdGenero, Direccion, Telefono, CorreoElectronico, IdEstadoCivil, FechaCreacion, UsuarioCreacion) VALUES ('Isabel Clara','Coto Montero','1984-12-23',2,'Zona 10, Amatitl�n, Guatemala','502-02332200','icoto@itcompany.com',5,now(),'system');
INSERT INTO PERSONA (Nombre, Apellido, FechaNacimiento, IdGenero, Direccion, Telefono, CorreoElectronico, IdEstadoCivil, FechaCreacion, UsuarioCreacion) VALUES ('Iv�n Manuel','Arias Villalobos','1973-12-9',1,'Zona 9, Chinautla, Guatemala','502-81644529','iarias@itcompany.com',5,now(),'system');
INSERT INTO PERSONA (Nombre, Apellido, FechaNacimiento, IdGenero, Direccion, Telefono, CorreoElectronico, IdEstadoCivil, FechaCreacion, UsuarioCreacion) VALUES ('Roberto V�ctor','Chac�n Bola�os','1983-9-30',1,'Zona 5, Chuarrancho, Guatemala','502-12456861','rchac�n@itcompany.com',5,now(),'system');
INSERT INTO PERSONA (Nombre, Apellido, FechaNacimiento, IdGenero, Direccion, Telefono, CorreoElectronico, IdEstadoCivil, FechaCreacion, UsuarioCreacion) VALUES ('Omar Gerardo','Le�n Aguilar','1996-7-3',1,'Zona 5, Fraijanes, Guatemala','502-33161190','ole�n@itcompany.com',4,now(),'system');
INSERT INTO PERSONA (Nombre, Apellido, FechaNacimiento, IdGenero, Direccion, Telefono, CorreoElectronico, IdEstadoCivil, FechaCreacion, UsuarioCreacion) VALUES ('Sofia Isabel','Medina Soto','2008-9-16',2,'Zona 1, Mixco, Guatemala','502-04644225','smedina@itcompany.com',4,now(),'system');
INSERT INTO PERSONA (Nombre, Apellido, FechaNacimiento, IdGenero, Direccion, Telefono, CorreoElectronico, IdEstadoCivil, FechaCreacion, UsuarioCreacion) VALUES ('Rocio Ana','Delgado Guzm�n','2000-12-18',2,'Zona 4, Palencia, Guatemala','502-36470075','rdelgado@itcompany.com',5,now(),'system');
INSERT INTO PERSONA (Nombre, Apellido, FechaNacimiento, IdGenero, Direccion, Telefono, CorreoElectronico, IdEstadoCivil, FechaCreacion, UsuarioCreacion) VALUES ('Sofia Carolina','Vargas Rivas','2009-2-14',2,'Zona 5, San Jos� del Golfo, Guatemala','502-83724325','svargas@itcompany.com',1,now(),'system');

create table DOCUMENTO_PERSONA(
	IdTipoDocumento int not null,
	IdPersona int not null,
	NoDocumento varchar(50),
	FechaCreacion datetime not null,
	UsuarioCreacion varchar(100) not null,
	FechaModificacion datetime,
	UsuarioModificacion varchar(100),
	primary key (IdTipoDocumento,IdPersona),
	foreign key (IdTipoDocumento) references TIPO_DOCUMENTO(IdTipoDocumento),
	foreign key (IdPersona) references PERSONA(IdPersona)
);

INSERT INTO DOCUMENTO_PERSONA (IdTipoDocumento, IdPersona, NoDocumento, FechaCreacion, UsuarioCreacion) VALUES (1, 1, '9843 55061 4572', NOW(), 'system');
INSERT INTO DOCUMENTO_PERSONA (IdTipoDocumento, IdPersona, NoDocumento, FechaCreacion, UsuarioCreacion) VALUES (1, 2, '7075 73013 6224', NOW(), 'system');
INSERT INTO DOCUMENTO_PERSONA (IdTipoDocumento, IdPersona, NoDocumento, FechaCreacion, UsuarioCreacion) VALUES (1, 3, '2530 90222 1019', NOW(), 'system');
INSERT INTO DOCUMENTO_PERSONA (IdTipoDocumento, IdPersona, NoDocumento, FechaCreacion, UsuarioCreacion) VALUES (1, 4, '6669 63925 5755', NOW(), 'system');
INSERT INTO DOCUMENTO_PERSONA (IdTipoDocumento, IdPersona, NoDocumento, FechaCreacion, UsuarioCreacion) VALUES (1, 5, '5049 91259 0100', NOW(), 'system');
INSERT INTO DOCUMENTO_PERSONA (IdTipoDocumento, IdPersona, NoDocumento, FechaCreacion, UsuarioCreacion) VALUES (1, 6, '5182 06281 7749', NOW(), 'system');
INSERT INTO DOCUMENTO_PERSONA (IdTipoDocumento, IdPersona, NoDocumento, FechaCreacion, UsuarioCreacion) VALUES (1, 7, '8655 54571 6296', NOW(), 'system');
INSERT INTO DOCUMENTO_PERSONA (IdTipoDocumento, IdPersona, NoDocumento, FechaCreacion, UsuarioCreacion) VALUES (1, 8, '3865 29405 8011', NOW(), 'system');
INSERT INTO DOCUMENTO_PERSONA (IdTipoDocumento, IdPersona, NoDocumento, FechaCreacion, UsuarioCreacion) VALUES (1, 9, '4351 58506 8353', NOW(), 'system');
INSERT INTO DOCUMENTO_PERSONA (IdTipoDocumento, IdPersona, NoDocumento, FechaCreacion, UsuarioCreacion) VALUES (1, 10, '8988 72254 1232', NOW(), 'system');
INSERT INTO DOCUMENTO_PERSONA (IdTipoDocumento, IdPersona, NoDocumento, FechaCreacion, UsuarioCreacion) VALUES (1, 11, '7132 89673 5304', NOW(), 'system');
INSERT INTO DOCUMENTO_PERSONA (IdTipoDocumento, IdPersona, NoDocumento, FechaCreacion, UsuarioCreacion) VALUES (1, 12, '0824 09869 7755', NOW(), 'system');
INSERT INTO DOCUMENTO_PERSONA (IdTipoDocumento, IdPersona, NoDocumento, FechaCreacion, UsuarioCreacion) VALUES (1, 13, '9821 00067 9229', NOW(), 'system');
INSERT INTO DOCUMENTO_PERSONA (IdTipoDocumento, IdPersona, NoDocumento, FechaCreacion, UsuarioCreacion) VALUES (1, 14, '2121 43389 0471', NOW(), 'system');
INSERT INTO DOCUMENTO_PERSONA (IdTipoDocumento, IdPersona, NoDocumento, FechaCreacion, UsuarioCreacion) VALUES (1, 15, '0438 92877 7298', NOW(), 'system');
INSERT INTO DOCUMENTO_PERSONA (IdTipoDocumento, IdPersona, NoDocumento, FechaCreacion, UsuarioCreacion) VALUES (1, 16, '1358 70952 6611', NOW(), 'system');
INSERT INTO DOCUMENTO_PERSONA (IdTipoDocumento, IdPersona, NoDocumento, FechaCreacion, UsuarioCreacion) VALUES (1, 17, '9470 38126 3709', NOW(), 'system');
INSERT INTO DOCUMENTO_PERSONA (IdTipoDocumento, IdPersona, NoDocumento, FechaCreacion, UsuarioCreacion) VALUES (1, 18, '7399 64433 4795', NOW(), 'system');
INSERT INTO DOCUMENTO_PERSONA (IdTipoDocumento, IdPersona, NoDocumento, FechaCreacion, UsuarioCreacion) VALUES (1, 19, '5222 43174 1094', NOW(), 'system');
INSERT INTO DOCUMENTO_PERSONA (IdTipoDocumento, IdPersona, NoDocumento, FechaCreacion, UsuarioCreacion) VALUES (1, 20, '8654 03199 6046', NOW(), 'system');
INSERT INTO DOCUMENTO_PERSONA (IdTipoDocumento, IdPersona, NoDocumento, FechaCreacion, UsuarioCreacion) VALUES (1, 21, '8673 75698 0647', NOW(), 'system');
INSERT INTO DOCUMENTO_PERSONA (IdTipoDocumento, IdPersona, NoDocumento, FechaCreacion, UsuarioCreacion) VALUES (1, 22, '8962 51233 9886', NOW(), 'system');
INSERT INTO DOCUMENTO_PERSONA (IdTipoDocumento, IdPersona, NoDocumento, FechaCreacion, UsuarioCreacion) VALUES (1, 23, '6174 26133 6105', NOW(), 'system');
INSERT INTO DOCUMENTO_PERSONA (IdTipoDocumento, IdPersona, NoDocumento, FechaCreacion, UsuarioCreacion) VALUES (1, 24, '8395 83334 5874', NOW(), 'system');
INSERT INTO DOCUMENTO_PERSONA (IdTipoDocumento, IdPersona, NoDocumento, FechaCreacion, UsuarioCreacion) VALUES (1, 25, '0200 96366 9189', NOW(), 'system');
INSERT INTO DOCUMENTO_PERSONA (IdTipoDocumento, IdPersona, NoDocumento, FechaCreacion, UsuarioCreacion) VALUES (1, 26, '3225 48797 3525', NOW(), 'system');
INSERT INTO DOCUMENTO_PERSONA (IdTipoDocumento, IdPersona, NoDocumento, FechaCreacion, UsuarioCreacion) VALUES (1, 27, '7983 70317 1074', NOW(), 'system');
INSERT INTO DOCUMENTO_PERSONA (IdTipoDocumento, IdPersona, NoDocumento, FechaCreacion, UsuarioCreacion) VALUES (1, 28, '1371 28588 4414', NOW(), 'system');
INSERT INTO DOCUMENTO_PERSONA (IdTipoDocumento, IdPersona, NoDocumento, FechaCreacion, UsuarioCreacion) VALUES (1, 29, '2168 56399 1848', NOW(), 'system');
INSERT INTO DOCUMENTO_PERSONA (IdTipoDocumento, IdPersona, NoDocumento, FechaCreacion, UsuarioCreacion) VALUES (1, 30, '6531 09372 8853', NOW(), 'system');
INSERT INTO DOCUMENTO_PERSONA (IdTipoDocumento, IdPersona, NoDocumento, FechaCreacion, UsuarioCreacion) VALUES (1, 31, '1360 91614 7260', NOW(), 'system');
INSERT INTO DOCUMENTO_PERSONA (IdTipoDocumento, IdPersona, NoDocumento, FechaCreacion, UsuarioCreacion) VALUES (1, 32, '7620 03824 3602', NOW(), 'system');
INSERT INTO DOCUMENTO_PERSONA (IdTipoDocumento, IdPersona, NoDocumento, FechaCreacion, UsuarioCreacion) VALUES (1, 33, '2740 67932 1333', NOW(), 'system');
INSERT INTO DOCUMENTO_PERSONA (IdTipoDocumento, IdPersona, NoDocumento, FechaCreacion, UsuarioCreacion) VALUES (1, 34, '9553 98050 7483', NOW(), 'system');
INSERT INTO DOCUMENTO_PERSONA (IdTipoDocumento, IdPersona, NoDocumento, FechaCreacion, UsuarioCreacion) VALUES (1, 35, '8328 99392 0862', NOW(), 'system');
INSERT INTO DOCUMENTO_PERSONA (IdTipoDocumento, IdPersona, NoDocumento, FechaCreacion, UsuarioCreacion) VALUES (1, 36, '0692 30269 9778', NOW(), 'system');
INSERT INTO DOCUMENTO_PERSONA (IdTipoDocumento, IdPersona, NoDocumento, FechaCreacion, UsuarioCreacion) VALUES (1, 37, '3951 20723 4629', NOW(), 'system');
INSERT INTO DOCUMENTO_PERSONA (IdTipoDocumento, IdPersona, NoDocumento, FechaCreacion, UsuarioCreacion) VALUES (1, 38, '0842 05539 9738', NOW(), 'system');
INSERT INTO DOCUMENTO_PERSONA (IdTipoDocumento, IdPersona, NoDocumento, FechaCreacion, UsuarioCreacion) VALUES (1, 39, '5645 61309 8595', NOW(), 'system');
INSERT INTO DOCUMENTO_PERSONA (IdTipoDocumento, IdPersona, NoDocumento, FechaCreacion, UsuarioCreacion) VALUES (1, 40, '7571 04468 3810', NOW(), 'system');
INSERT INTO DOCUMENTO_PERSONA (IdTipoDocumento, IdPersona, NoDocumento, FechaCreacion, UsuarioCreacion) VALUES (1, 41, '5093 49946 5770', NOW(), 'system');
INSERT INTO DOCUMENTO_PERSONA (IdTipoDocumento, IdPersona, NoDocumento, FechaCreacion, UsuarioCreacion) VALUES (1, 42, '5352 17345 3930', NOW(), 'system');
INSERT INTO DOCUMENTO_PERSONA (IdTipoDocumento, IdPersona, NoDocumento, FechaCreacion, UsuarioCreacion) VALUES (1, 43, '6610 55723 0937', NOW(), 'system');
INSERT INTO DOCUMENTO_PERSONA (IdTipoDocumento, IdPersona, NoDocumento, FechaCreacion, UsuarioCreacion) VALUES (1, 44, '2593 53764 7338', NOW(), 'system');
INSERT INTO DOCUMENTO_PERSONA (IdTipoDocumento, IdPersona, NoDocumento, FechaCreacion, UsuarioCreacion) VALUES (1, 45, '3531 01066 9962', NOW(), 'system');
INSERT INTO DOCUMENTO_PERSONA (IdTipoDocumento, IdPersona, NoDocumento, FechaCreacion, UsuarioCreacion) VALUES (1, 46, '7534 16515 2177', NOW(), 'system');
INSERT INTO DOCUMENTO_PERSONA (IdTipoDocumento, IdPersona, NoDocumento, FechaCreacion, UsuarioCreacion) VALUES (1, 47, '0251 60275 6931', NOW(), 'system');
INSERT INTO DOCUMENTO_PERSONA (IdTipoDocumento, IdPersona, NoDocumento, FechaCreacion, UsuarioCreacion) VALUES (1, 48, '1958 10242 1171', NOW(), 'system');
INSERT INTO DOCUMENTO_PERSONA (IdTipoDocumento, IdPersona, NoDocumento, FechaCreacion, UsuarioCreacion) VALUES (1, 49, '9277 24935 8080', NOW(), 'system');
INSERT INTO DOCUMENTO_PERSONA (IdTipoDocumento, IdPersona, NoDocumento, FechaCreacion, UsuarioCreacion) VALUES (1, 50, '4303 72545 3163', NOW(), 'system');
INSERT INTO DOCUMENTO_PERSONA (IdTipoDocumento, IdPersona, NoDocumento, FechaCreacion, UsuarioCreacion) VALUES (1, 51, '4585 87418 3421', NOW(), 'system');
INSERT INTO DOCUMENTO_PERSONA (IdTipoDocumento, IdPersona, NoDocumento, FechaCreacion, UsuarioCreacion) VALUES (1, 52, '1918 23030 7526', NOW(), 'system');
INSERT INTO DOCUMENTO_PERSONA (IdTipoDocumento, IdPersona, NoDocumento, FechaCreacion, UsuarioCreacion) VALUES (1, 53, '6962 45458 4230', NOW(), 'system');
INSERT INTO DOCUMENTO_PERSONA (IdTipoDocumento, IdPersona, NoDocumento, FechaCreacion, UsuarioCreacion) VALUES (1, 54, '6799 04752 1575', NOW(), 'system');
INSERT INTO DOCUMENTO_PERSONA (IdTipoDocumento, IdPersona, NoDocumento, FechaCreacion, UsuarioCreacion) VALUES (1, 55, '9628 94486 0223', NOW(), 'system');
INSERT INTO DOCUMENTO_PERSONA (IdTipoDocumento, IdPersona, NoDocumento, FechaCreacion, UsuarioCreacion) VALUES (1, 56, '1531 63081 7902', NOW(), 'system');
INSERT INTO DOCUMENTO_PERSONA (IdTipoDocumento, IdPersona, NoDocumento, FechaCreacion, UsuarioCreacion) VALUES (1, 57, '4013 58916 6452', NOW(), 'system');
INSERT INTO DOCUMENTO_PERSONA (IdTipoDocumento, IdPersona, NoDocumento, FechaCreacion, UsuarioCreacion) VALUES (1, 58, '7285 58897 0731', NOW(), 'system');
INSERT INTO DOCUMENTO_PERSONA (IdTipoDocumento, IdPersona, NoDocumento, FechaCreacion, UsuarioCreacion) VALUES (1, 59, '1516 33095 1121', NOW(), 'system');
INSERT INTO DOCUMENTO_PERSONA (IdTipoDocumento, IdPersona, NoDocumento, FechaCreacion, UsuarioCreacion) VALUES (1, 60, '5472 53843 1493', NOW(), 'system');
INSERT INTO DOCUMENTO_PERSONA (IdTipoDocumento, IdPersona, NoDocumento, FechaCreacion, UsuarioCreacion) VALUES (1, 61, '4195 12340 2881', NOW(), 'system');
INSERT INTO DOCUMENTO_PERSONA (IdTipoDocumento, IdPersona, NoDocumento, FechaCreacion, UsuarioCreacion) VALUES (1, 62, '9646 99733 7207', NOW(), 'system');
INSERT INTO DOCUMENTO_PERSONA (IdTipoDocumento, IdPersona, NoDocumento, FechaCreacion, UsuarioCreacion) VALUES (1, 63, '9661 50188 8724', NOW(), 'system');
INSERT INTO DOCUMENTO_PERSONA (IdTipoDocumento, IdPersona, NoDocumento, FechaCreacion, UsuarioCreacion) VALUES (1, 64, '9391 39362 5184', NOW(), 'system');
INSERT INTO DOCUMENTO_PERSONA (IdTipoDocumento, IdPersona, NoDocumento, FechaCreacion, UsuarioCreacion) VALUES (1, 65, '7533 45552 3134', NOW(), 'system');
INSERT INTO DOCUMENTO_PERSONA (IdTipoDocumento, IdPersona, NoDocumento, FechaCreacion, UsuarioCreacion) VALUES (1, 66, '1341 37675 5601', NOW(), 'system');
INSERT INTO DOCUMENTO_PERSONA (IdTipoDocumento, IdPersona, NoDocumento, FechaCreacion, UsuarioCreacion) VALUES (1, 67, '8102 29282 1440', NOW(), 'system');
INSERT INTO DOCUMENTO_PERSONA (IdTipoDocumento, IdPersona, NoDocumento, FechaCreacion, UsuarioCreacion) VALUES (1, 68, '5808 90184 7851', NOW(), 'system');
INSERT INTO DOCUMENTO_PERSONA (IdTipoDocumento, IdPersona, NoDocumento, FechaCreacion, UsuarioCreacion) VALUES (1, 69, '7661 74962 8195', NOW(), 'system');
INSERT INTO DOCUMENTO_PERSONA (IdTipoDocumento, IdPersona, NoDocumento, FechaCreacion, UsuarioCreacion) VALUES (1, 70, '6788 24609 7176', NOW(), 'system');
INSERT INTO DOCUMENTO_PERSONA (IdTipoDocumento, IdPersona, NoDocumento, FechaCreacion, UsuarioCreacion) VALUES (1, 71, '6897 39562 9346', NOW(), 'system');
INSERT INTO DOCUMENTO_PERSONA (IdTipoDocumento, IdPersona, NoDocumento, FechaCreacion, UsuarioCreacion) VALUES (1, 72, '0863 18558 8599', NOW(), 'system');
INSERT INTO DOCUMENTO_PERSONA (IdTipoDocumento, IdPersona, NoDocumento, FechaCreacion, UsuarioCreacion) VALUES (1, 73, '6703 11268 1230', NOW(), 'system');
INSERT INTO DOCUMENTO_PERSONA (IdTipoDocumento, IdPersona, NoDocumento, FechaCreacion, UsuarioCreacion) VALUES (1, 74, '3665 52079 0291', NOW(), 'system');
INSERT INTO DOCUMENTO_PERSONA (IdTipoDocumento, IdPersona, NoDocumento, FechaCreacion, UsuarioCreacion) VALUES (1, 75, '8519 49295 2482', NOW(), 'system');
INSERT INTO DOCUMENTO_PERSONA (IdTipoDocumento, IdPersona, NoDocumento, FechaCreacion, UsuarioCreacion) VALUES (1, 76, '2694 09817 3750', NOW(), 'system');
INSERT INTO DOCUMENTO_PERSONA (IdTipoDocumento, IdPersona, NoDocumento, FechaCreacion, UsuarioCreacion) VALUES (1, 77, '3219 73844 7773', NOW(), 'system');
INSERT INTO DOCUMENTO_PERSONA (IdTipoDocumento, IdPersona, NoDocumento, FechaCreacion, UsuarioCreacion) VALUES (1, 78, '3780 72681 3789', NOW(), 'system');
INSERT INTO DOCUMENTO_PERSONA (IdTipoDocumento, IdPersona, NoDocumento, FechaCreacion, UsuarioCreacion) VALUES (1, 79, '3233 13658 7967', NOW(), 'system');
INSERT INTO DOCUMENTO_PERSONA (IdTipoDocumento, IdPersona, NoDocumento, FechaCreacion, UsuarioCreacion) VALUES (1, 80, '7850 42187 8510', NOW(), 'system');
INSERT INTO DOCUMENTO_PERSONA (IdTipoDocumento, IdPersona, NoDocumento, FechaCreacion, UsuarioCreacion) VALUES (1, 81, '1069 65077 5997', NOW(), 'system');
INSERT INTO DOCUMENTO_PERSONA (IdTipoDocumento, IdPersona, NoDocumento, FechaCreacion, UsuarioCreacion) VALUES (1, 82, '0212 93606 9680', NOW(), 'system');
INSERT INTO DOCUMENTO_PERSONA (IdTipoDocumento, IdPersona, NoDocumento, FechaCreacion, UsuarioCreacion) VALUES (1, 83, '1460 85833 5143', NOW(), 'system');
INSERT INTO DOCUMENTO_PERSONA (IdTipoDocumento, IdPersona, NoDocumento, FechaCreacion, UsuarioCreacion) VALUES (1, 84, '2190 42958 1452', NOW(), 'system');
INSERT INTO DOCUMENTO_PERSONA (IdTipoDocumento, IdPersona, NoDocumento, FechaCreacion, UsuarioCreacion) VALUES (1, 85, '1716 62914 0464', NOW(), 'system');
INSERT INTO DOCUMENTO_PERSONA (IdTipoDocumento, IdPersona, NoDocumento, FechaCreacion, UsuarioCreacion) VALUES (1, 86, '2400 21782 2284', NOW(), 'system');
INSERT INTO DOCUMENTO_PERSONA (IdTipoDocumento, IdPersona, NoDocumento, FechaCreacion, UsuarioCreacion) VALUES (1, 87, '1323 22449 1918', NOW(), 'system');
INSERT INTO DOCUMENTO_PERSONA (IdTipoDocumento, IdPersona, NoDocumento, FechaCreacion, UsuarioCreacion) VALUES (1, 88, '7537 97427 4087', NOW(), 'system');
INSERT INTO DOCUMENTO_PERSONA (IdTipoDocumento, IdPersona, NoDocumento, FechaCreacion, UsuarioCreacion) VALUES (1, 89, '5776 26535 4892', NOW(), 'system');
INSERT INTO DOCUMENTO_PERSONA (IdTipoDocumento, IdPersona, NoDocumento, FechaCreacion, UsuarioCreacion) VALUES (1, 90, '1385 65867 3574', NOW(), 'system');
INSERT INTO DOCUMENTO_PERSONA (IdTipoDocumento, IdPersona, NoDocumento, FechaCreacion, UsuarioCreacion) VALUES (1, 91, '3794 04401 3693', NOW(), 'system');
INSERT INTO DOCUMENTO_PERSONA (IdTipoDocumento, IdPersona, NoDocumento, FechaCreacion, UsuarioCreacion) VALUES (1, 92, '3396 52419 0967', NOW(), 'system');
INSERT INTO DOCUMENTO_PERSONA (IdTipoDocumento, IdPersona, NoDocumento, FechaCreacion, UsuarioCreacion) VALUES (1, 93, '3463 50684 6826', NOW(), 'system');
INSERT INTO DOCUMENTO_PERSONA (IdTipoDocumento, IdPersona, NoDocumento, FechaCreacion, UsuarioCreacion) VALUES (1, 94, '7169 79981 3881', NOW(), 'system');
INSERT INTO DOCUMENTO_PERSONA (IdTipoDocumento, IdPersona, NoDocumento, FechaCreacion, UsuarioCreacion) VALUES (1, 95, '9178 91826 6931', NOW(), 'system');
INSERT INTO DOCUMENTO_PERSONA (IdTipoDocumento, IdPersona, NoDocumento, FechaCreacion, UsuarioCreacion) VALUES (1, 96, '2229 21930 4933', NOW(), 'system');
INSERT INTO DOCUMENTO_PERSONA (IdTipoDocumento, IdPersona, NoDocumento, FechaCreacion, UsuarioCreacion) VALUES (1, 97, '2038 51041 2278', NOW(), 'system');
INSERT INTO DOCUMENTO_PERSONA (IdTipoDocumento, IdPersona, NoDocumento, FechaCreacion, UsuarioCreacion) VALUES (1, 98, '1951 29958 0337', NOW(), 'system');
INSERT INTO DOCUMENTO_PERSONA (IdTipoDocumento, IdPersona, NoDocumento, FechaCreacion, UsuarioCreacion) VALUES (1, 99, '5166 41205 5957', NOW(), 'system');
INSERT INTO DOCUMENTO_PERSONA (IdTipoDocumento, IdPersona, NoDocumento, FechaCreacion, UsuarioCreacion) VALUES (1, 100, '9823 32332 3967', NOW(), 'system');

create table PUESTO(
	IdPuesto int not null auto_increment,
	Nombre varchar(50) not null,
	IdDepartamento int not null,
	FechaCreacion datetime not null,
	UsuarioCreacion varchar(100) not null,
	FechaModificacion datetime,
	UsuarioModificacion varchar(100),
	primary key (IdPuesto),
	foreign key (IdDepartamento) references DEPARTAMENTO(IdDepartamento)
);

insert into PUESTO(Nombre,IdDepartamento,FechaCreacion,UsuarioCreacion,FechaModificacion,UsuarioModificacion)
values ('Administrador de Bases de Datos ',11, now(), 'system', null, null)
,('Analista de Datos ',11, now(), 'system', null, null)
,('Analista de Recursos Humanos ',13, now(), 'system', null, null)
,('Analista de Sistemas ',11, now(), 'system', null, null)
,('Analista Financiero ',5, now(), 'system', null, null)
,('Arquitecto de Sistemas ',11, now(), 'system', null, null)
,('Asesor Comercial ',10, now(), 'system', null, null)
,('Asistente Administrativo ',1, now(), 'system', null, null)
,('Asistente de Recursos Humanos ',13, now(), 'system', null, null)
,('Consultor de Tecnolog�a ',11, now(), 'system', null, null)
,('Contador ',5, now(), 'system', null, null)
,('Coordinador de Log�stica ',8, now(), 'system', null, null)
,('Coordinador de Proyectos ',11, now(), 'system', null, null)
,('Desarrollador Junior ',11, now(), 'system', null, null)
,('Desarrollador Senior ',11, now(), 'system', null, null)
,('Especialista en Marketing Digital ',12, now(), 'system', null, null)
,('Especialista en Relaciones P�blicas ',10, now(), 'system', null, null)
,('Especialista en Seguridad Inform�tica ',11, now(), 'system', null, null)
,('Gerente de Finanzas ',5, now(), 'system', null, null)
,('Gerente de Ventas y Marketing ',12, now(), 'system', null, null)
,('Gerente de Operaciones ',9, now(), 'system', null, null)
,('Gerente de Producci�n ',9, now(), 'system', null, null)
,('Gerente de Recursos Humanos ',13, now(), 'system', null, null)
,('Gerente de Sistemas ',11, now(), 'system', null, null)
,('Gerente General ',1, now(), 'system', null, null)
,('Ingeniero de Redes ',11, now(), 'system', null, null)
,('T�cnico de Mantenimiento ',9, now(), 'system', null, null)
,('T�cnico de Soporte ',9, now(), 'system', null, null)
,('T�cnico de Soporte IT ',11, now(), 'system', null, null);

create table BANCO(
	IdBanco int not null auto_increment,
	Nombre varchar(50) not null,
	FechaCreacion datetime not null,
	UsuarioCreacion varchar(100) not null,
	FechaModificacion datetime,
	UsuarioModificacion varchar(100),
	primary key (IdBanco)
);

insert into BANCO(Nombre, FechaCreacion, UsuarioCreacion, FechaModificacion, UsuarioModificacion)
values(
	'BANCO INDUSTRIAL', now(), 'system', null, null
),
(
	'BANCO RURAL', now(), 'system', null, null
),
(
	'BANCO G&T CONTINENTAL', now(), 'system', null, null
),
(
	'BANCO CHN', now(), 'system', null, null
);

create table EMPLEADO(
	IdEmpleado int not null auto_increment,
	IdPersona int not null,
	IdSucursal int not null,
	FechaContratacion date not null,
	IdPuesto int not null,
	IdStatusEmpleado int not null,
	IngresoSueldoBase decimal(10,2) not null,
	IngresoBonificacionDecreto decimal(10,2) not null,
	IngresoOtrosIngresos decimal(10,2) not null,
	DescuentoIgss decimal(10,2) not null,
	DescuentoIsr Decimal(10,2) not null,
	DescuentoInasistencias decimal(10,2) not null,
	FechaCreacion datetime not null,
	UsuarioCreacion varchar(100) not null,
	FechaModificacion datetime,
	UsuarioModificacion varchar(100),
	primary key (IdEmpleado),
	foreign key (IdPersona) references PERSONA(IdPersona),
	foreign key (IdSucursal) references SUCURSAL(IdSucursal),
	foreign key (IdPuesto) references PUESTO(IdPuesto),
	foreign key (IdStatusEmpleado) references STATUS_EMPLEADO(IdStatusEmpleado)
);

INSERT INTO EMPLEADO (IdPersona,IdSucursal,FechaContratacion,IdPuesto,IdStatusEmpleado,IngresoSueldoBase,IngresoBonificacionDecreto,IngresoOtrosIngresos,DescuentoIgss,DescuentoIsr,DescuentoInasistencias,FechaCreacion,UsuarioCreacion) VALUES (1, 1, '1990-01-01',1,1,11000,250,0,531.3,0,0,now(),'system');
INSERT INTO EMPLEADO (IdPersona,IdSucursal,FechaContratacion,IdPuesto,IdStatusEmpleado,IngresoSueldoBase,IngresoBonificacionDecreto,IngresoOtrosIngresos,DescuentoIgss,DescuentoIsr,DescuentoInasistencias,FechaCreacion,UsuarioCreacion) VALUES (2, 1, '1990-01-01',1,1,11000,250,0,531.3,0,0,now(),'system');
INSERT INTO EMPLEADO (IdPersona,IdSucursal,FechaContratacion,IdPuesto,IdStatusEmpleado,IngresoSueldoBase,IngresoBonificacionDecreto,IngresoOtrosIngresos,DescuentoIgss,DescuentoIsr,DescuentoInasistencias,FechaCreacion,UsuarioCreacion) VALUES (3, 1, '1990-01-01',2,1,10500,250,0,507.15,0,0,now(),'system');
INSERT INTO EMPLEADO (IdPersona,IdSucursal,FechaContratacion,IdPuesto,IdStatusEmpleado,IngresoSueldoBase,IngresoBonificacionDecreto,IngresoOtrosIngresos,DescuentoIgss,DescuentoIsr,DescuentoInasistencias,FechaCreacion,UsuarioCreacion) VALUES (4, 1, '1990-01-01',2,1,10500,250,0,507.15,0,0,now(),'system');
INSERT INTO EMPLEADO (IdPersona,IdSucursal,FechaContratacion,IdPuesto,IdStatusEmpleado,IngresoSueldoBase,IngresoBonificacionDecreto,IngresoOtrosIngresos,DescuentoIgss,DescuentoIsr,DescuentoInasistencias,FechaCreacion,UsuarioCreacion) VALUES (5, 1, '1990-01-01',3,1,9000,250,0,434.7,0,0,now(),'system');
INSERT INTO EMPLEADO (IdPersona,IdSucursal,FechaContratacion,IdPuesto,IdStatusEmpleado,IngresoSueldoBase,IngresoBonificacionDecreto,IngresoOtrosIngresos,DescuentoIgss,DescuentoIsr,DescuentoInasistencias,FechaCreacion,UsuarioCreacion) VALUES (6, 1, '1990-01-01',3,1,9000,250,0,434.7,0,0,now(),'system');
INSERT INTO EMPLEADO (IdPersona,IdSucursal,FechaContratacion,IdPuesto,IdStatusEmpleado,IngresoSueldoBase,IngresoBonificacionDecreto,IngresoOtrosIngresos,DescuentoIgss,DescuentoIsr,DescuentoInasistencias,FechaCreacion,UsuarioCreacion) VALUES (7, 1, '1990-01-01',4,1,10000,250,0,483,0,0,now(),'system');
INSERT INTO EMPLEADO (IdPersona,IdSucursal,FechaContratacion,IdPuesto,IdStatusEmpleado,IngresoSueldoBase,IngresoBonificacionDecreto,IngresoOtrosIngresos,DescuentoIgss,DescuentoIsr,DescuentoInasistencias,FechaCreacion,UsuarioCreacion) VALUES (8, 1, '1990-01-01',4,1,10000,250,0,483,0,0,now(),'system');
INSERT INTO EMPLEADO (IdPersona,IdSucursal,FechaContratacion,IdPuesto,IdStatusEmpleado,IngresoSueldoBase,IngresoBonificacionDecreto,IngresoOtrosIngresos,DescuentoIgss,DescuentoIsr,DescuentoInasistencias,FechaCreacion,UsuarioCreacion) VALUES (9, 1, '1990-01-01',4,1,10000,250,0,483,0,0,now(),'system');
INSERT INTO EMPLEADO (IdPersona,IdSucursal,FechaContratacion,IdPuesto,IdStatusEmpleado,IngresoSueldoBase,IngresoBonificacionDecreto,IngresoOtrosIngresos,DescuentoIgss,DescuentoIsr,DescuentoInasistencias,FechaCreacion,UsuarioCreacion) VALUES (10, 1, '1990-01-01',4,1,10000,250,0,483,0,0,now(),'system');
INSERT INTO EMPLEADO (IdPersona,IdSucursal,FechaContratacion,IdPuesto,IdStatusEmpleado,IngresoSueldoBase,IngresoBonificacionDecreto,IngresoOtrosIngresos,DescuentoIgss,DescuentoIsr,DescuentoInasistencias,FechaCreacion,UsuarioCreacion) VALUES (11, 1, '1990-01-01',4,1,10000,250,0,483,0,0,now(),'system');
INSERT INTO EMPLEADO (IdPersona,IdSucursal,FechaContratacion,IdPuesto,IdStatusEmpleado,IngresoSueldoBase,IngresoBonificacionDecreto,IngresoOtrosIngresos,DescuentoIgss,DescuentoIsr,DescuentoInasistencias,FechaCreacion,UsuarioCreacion) VALUES (12, 1, '1990-01-01',5,1,10000,250,0,483,0,0,now(),'system');
INSERT INTO EMPLEADO (IdPersona,IdSucursal,FechaContratacion,IdPuesto,IdStatusEmpleado,IngresoSueldoBase,IngresoBonificacionDecreto,IngresoOtrosIngresos,DescuentoIgss,DescuentoIsr,DescuentoInasistencias,FechaCreacion,UsuarioCreacion) VALUES (13, 1, '1990-01-01',5,1,10000,250,0,483,0,0,now(),'system');
INSERT INTO EMPLEADO (IdPersona,IdSucursal,FechaContratacion,IdPuesto,IdStatusEmpleado,IngresoSueldoBase,IngresoBonificacionDecreto,IngresoOtrosIngresos,DescuentoIgss,DescuentoIsr,DescuentoInasistencias,FechaCreacion,UsuarioCreacion) VALUES (14, 1, '1990-01-01',6,1,14000,250,0,676.2,0,0,now(),'system');
INSERT INTO EMPLEADO (IdPersona,IdSucursal,FechaContratacion,IdPuesto,IdStatusEmpleado,IngresoSueldoBase,IngresoBonificacionDecreto,IngresoOtrosIngresos,DescuentoIgss,DescuentoIsr,DescuentoInasistencias,FechaCreacion,UsuarioCreacion) VALUES (15, 1, '1990-01-01',6,1,14000,250,0,676.2,0,0,now(),'system');
INSERT INTO EMPLEADO (IdPersona,IdSucursal,FechaContratacion,IdPuesto,IdStatusEmpleado,IngresoSueldoBase,IngresoBonificacionDecreto,IngresoOtrosIngresos,DescuentoIgss,DescuentoIsr,DescuentoInasistencias,FechaCreacion,UsuarioCreacion) VALUES (16, 1, '1990-01-01',7,1,9500,250,0,458.85,0,0,now(),'system');
INSERT INTO EMPLEADO (IdPersona,IdSucursal,FechaContratacion,IdPuesto,IdStatusEmpleado,IngresoSueldoBase,IngresoBonificacionDecreto,IngresoOtrosIngresos,DescuentoIgss,DescuentoIsr,DescuentoInasistencias,FechaCreacion,UsuarioCreacion) VALUES (17, 1, '1990-01-01',7,1,9500,250,0,458.85,0,0,now(),'system');
INSERT INTO EMPLEADO (IdPersona,IdSucursal,FechaContratacion,IdPuesto,IdStatusEmpleado,IngresoSueldoBase,IngresoBonificacionDecreto,IngresoOtrosIngresos,DescuentoIgss,DescuentoIsr,DescuentoInasistencias,FechaCreacion,UsuarioCreacion) VALUES (18, 1, '1990-01-01',8,1,7000,250,0,338.1,0,0,now(),'system');
INSERT INTO EMPLEADO (IdPersona,IdSucursal,FechaContratacion,IdPuesto,IdStatusEmpleado,IngresoSueldoBase,IngresoBonificacionDecreto,IngresoOtrosIngresos,DescuentoIgss,DescuentoIsr,DescuentoInasistencias,FechaCreacion,UsuarioCreacion) VALUES (19, 1, '1990-01-01',8,1,7000,250,0,338.1,0,0,now(),'system');
INSERT INTO EMPLEADO (IdPersona,IdSucursal,FechaContratacion,IdPuesto,IdStatusEmpleado,IngresoSueldoBase,IngresoBonificacionDecreto,IngresoOtrosIngresos,DescuentoIgss,DescuentoIsr,DescuentoInasistencias,FechaCreacion,UsuarioCreacion) VALUES (20, 1, '1990-01-01',8,1,7000,250,0,338.1,0,0,now(),'system');
INSERT INTO EMPLEADO (IdPersona,IdSucursal,FechaContratacion,IdPuesto,IdStatusEmpleado,IngresoSueldoBase,IngresoBonificacionDecreto,IngresoOtrosIngresos,DescuentoIgss,DescuentoIsr,DescuentoInasistencias,FechaCreacion,UsuarioCreacion) VALUES (21, 1, '1990-01-01',8,1,7000,250,0,338.1,0,0,now(),'system');
INSERT INTO EMPLEADO (IdPersona,IdSucursal,FechaContratacion,IdPuesto,IdStatusEmpleado,IngresoSueldoBase,IngresoBonificacionDecreto,IngresoOtrosIngresos,DescuentoIgss,DescuentoIsr,DescuentoInasistencias,FechaCreacion,UsuarioCreacion) VALUES (22, 1, '1990-01-01',9,1,8000,250,0,386.4,0,0,now(),'system');
INSERT INTO EMPLEADO (IdPersona,IdSucursal,FechaContratacion,IdPuesto,IdStatusEmpleado,IngresoSueldoBase,IngresoBonificacionDecreto,IngresoOtrosIngresos,DescuentoIgss,DescuentoIsr,DescuentoInasistencias,FechaCreacion,UsuarioCreacion) VALUES (23, 1, '1990-01-01',9,1,8000,250,0,386.4,0,0,now(),'system');
INSERT INTO EMPLEADO (IdPersona,IdSucursal,FechaContratacion,IdPuesto,IdStatusEmpleado,IngresoSueldoBase,IngresoBonificacionDecreto,IngresoOtrosIngresos,DescuentoIgss,DescuentoIsr,DescuentoInasistencias,FechaCreacion,UsuarioCreacion) VALUES (24, 1, '1990-01-01',10,1,12500,250,0,603.75,0,0,now(),'system');
INSERT INTO EMPLEADO (IdPersona,IdSucursal,FechaContratacion,IdPuesto,IdStatusEmpleado,IngresoSueldoBase,IngresoBonificacionDecreto,IngresoOtrosIngresos,DescuentoIgss,DescuentoIsr,DescuentoInasistencias,FechaCreacion,UsuarioCreacion) VALUES (25, 1, '1990-01-01',10,1,12500,250,0,603.75,0,0,now(),'system');
INSERT INTO EMPLEADO (IdPersona,IdSucursal,FechaContratacion,IdPuesto,IdStatusEmpleado,IngresoSueldoBase,IngresoBonificacionDecreto,IngresoOtrosIngresos,DescuentoIgss,DescuentoIsr,DescuentoInasistencias,FechaCreacion,UsuarioCreacion) VALUES (26, 1, '1990-01-01',10,1,12500,250,0,603.75,0,0,now(),'system');
INSERT INTO EMPLEADO (IdPersona,IdSucursal,FechaContratacion,IdPuesto,IdStatusEmpleado,IngresoSueldoBase,IngresoBonificacionDecreto,IngresoOtrosIngresos,DescuentoIgss,DescuentoIsr,DescuentoInasistencias,FechaCreacion,UsuarioCreacion) VALUES (27, 1, '1990-01-01',10,1,12500,250,0,603.75,0,0,now(),'system');
INSERT INTO EMPLEADO (IdPersona,IdSucursal,FechaContratacion,IdPuesto,IdStatusEmpleado,IngresoSueldoBase,IngresoBonificacionDecreto,IngresoOtrosIngresos,DescuentoIgss,DescuentoIsr,DescuentoInasistencias,FechaCreacion,UsuarioCreacion) VALUES (28, 1, '1990-01-01',11,1,11500,250,0,555.45,0,0,now(),'system');
INSERT INTO EMPLEADO (IdPersona,IdSucursal,FechaContratacion,IdPuesto,IdStatusEmpleado,IngresoSueldoBase,IngresoBonificacionDecreto,IngresoOtrosIngresos,DescuentoIgss,DescuentoIsr,DescuentoInasistencias,FechaCreacion,UsuarioCreacion) VALUES (29, 1, '1990-01-01',11,1,11500,250,0,555.45,0,0,now(),'system');
INSERT INTO EMPLEADO (IdPersona,IdSucursal,FechaContratacion,IdPuesto,IdStatusEmpleado,IngresoSueldoBase,IngresoBonificacionDecreto,IngresoOtrosIngresos,DescuentoIgss,DescuentoIsr,DescuentoInasistencias,FechaCreacion,UsuarioCreacion) VALUES (30, 1, '1990-01-01',11,1,11500,250,0,555.45,0,0,now(),'system');
INSERT INTO EMPLEADO (IdPersona,IdSucursal,FechaContratacion,IdPuesto,IdStatusEmpleado,IngresoSueldoBase,IngresoBonificacionDecreto,IngresoOtrosIngresos,DescuentoIgss,DescuentoIsr,DescuentoInasistencias,FechaCreacion,UsuarioCreacion) VALUES (31, 1, '1990-01-01',11,1,11500,250,0,555.45,0,0,now(),'system');
INSERT INTO EMPLEADO (IdPersona,IdSucursal,FechaContratacion,IdPuesto,IdStatusEmpleado,IngresoSueldoBase,IngresoBonificacionDecreto,IngresoOtrosIngresos,DescuentoIgss,DescuentoIsr,DescuentoInasistencias,FechaCreacion,UsuarioCreacion) VALUES (32, 1, '1990-01-01',11,1,11500,250,0,555.45,0,0,now(),'system');
INSERT INTO EMPLEADO (IdPersona,IdSucursal,FechaContratacion,IdPuesto,IdStatusEmpleado,IngresoSueldoBase,IngresoBonificacionDecreto,IngresoOtrosIngresos,DescuentoIgss,DescuentoIsr,DescuentoInasistencias,FechaCreacion,UsuarioCreacion) VALUES (33, 1, '1990-01-01',12,1,12000,250,0,579.6,0,0,now(),'system');
INSERT INTO EMPLEADO (IdPersona,IdSucursal,FechaContratacion,IdPuesto,IdStatusEmpleado,IngresoSueldoBase,IngresoBonificacionDecreto,IngresoOtrosIngresos,DescuentoIgss,DescuentoIsr,DescuentoInasistencias,FechaCreacion,UsuarioCreacion) VALUES (34, 1, '1990-01-01',12,1,12000,250,0,579.6,0,0,now(),'system');
INSERT INTO EMPLEADO (IdPersona,IdSucursal,FechaContratacion,IdPuesto,IdStatusEmpleado,IngresoSueldoBase,IngresoBonificacionDecreto,IngresoOtrosIngresos,DescuentoIgss,DescuentoIsr,DescuentoInasistencias,FechaCreacion,UsuarioCreacion) VALUES (35, 1, '1990-01-01',12,1,12000,250,0,579.6,0,0,now(),'system');
INSERT INTO EMPLEADO (IdPersona,IdSucursal,FechaContratacion,IdPuesto,IdStatusEmpleado,IngresoSueldoBase,IngresoBonificacionDecreto,IngresoOtrosIngresos,DescuentoIgss,DescuentoIsr,DescuentoInasistencias,FechaCreacion,UsuarioCreacion) VALUES (36, 1, '1990-01-01',13,1,12500,250,0,603.75,0,0,now(),'system');
INSERT INTO EMPLEADO (IdPersona,IdSucursal,FechaContratacion,IdPuesto,IdStatusEmpleado,IngresoSueldoBase,IngresoBonificacionDecreto,IngresoOtrosIngresos,DescuentoIgss,DescuentoIsr,DescuentoInasistencias,FechaCreacion,UsuarioCreacion) VALUES (37, 1, '1990-01-01',13,1,12500,250,0,603.75,0,0,now(),'system');
INSERT INTO EMPLEADO (IdPersona,IdSucursal,FechaContratacion,IdPuesto,IdStatusEmpleado,IngresoSueldoBase,IngresoBonificacionDecreto,IngresoOtrosIngresos,DescuentoIgss,DescuentoIsr,DescuentoInasistencias,FechaCreacion,UsuarioCreacion) VALUES (38, 1, '1990-01-01',13,1,12500,250,0,603.75,0,0,now(),'system');
INSERT INTO EMPLEADO (IdPersona,IdSucursal,FechaContratacion,IdPuesto,IdStatusEmpleado,IngresoSueldoBase,IngresoBonificacionDecreto,IngresoOtrosIngresos,DescuentoIgss,DescuentoIsr,DescuentoInasistencias,FechaCreacion,UsuarioCreacion) VALUES (39, 1, '1990-01-01',13,1,12500,250,0,603.75,0,0,now(),'system');
INSERT INTO EMPLEADO (IdPersona,IdSucursal,FechaContratacion,IdPuesto,IdStatusEmpleado,IngresoSueldoBase,IngresoBonificacionDecreto,IngresoOtrosIngresos,DescuentoIgss,DescuentoIsr,DescuentoInasistencias,FechaCreacion,UsuarioCreacion) VALUES (40, 1, '1990-01-01',13,1,12500,250,0,603.75,0,0,now(),'system');
INSERT INTO EMPLEADO (IdPersona,IdSucursal,FechaContratacion,IdPuesto,IdStatusEmpleado,IngresoSueldoBase,IngresoBonificacionDecreto,IngresoOtrosIngresos,DescuentoIgss,DescuentoIsr,DescuentoInasistencias,FechaCreacion,UsuarioCreacion) VALUES (41, 1, '1990-01-01',14,1,8000,250,0,386.4,0,0,now(),'system');
INSERT INTO EMPLEADO (IdPersona,IdSucursal,FechaContratacion,IdPuesto,IdStatusEmpleado,IngresoSueldoBase,IngresoBonificacionDecreto,IngresoOtrosIngresos,DescuentoIgss,DescuentoIsr,DescuentoInasistencias,FechaCreacion,UsuarioCreacion) VALUES (42, 1, '1990-01-01',14,1,8000,250,0,386.4,0,0,now(),'system');
INSERT INTO EMPLEADO (IdPersona,IdSucursal,FechaContratacion,IdPuesto,IdStatusEmpleado,IngresoSueldoBase,IngresoBonificacionDecreto,IngresoOtrosIngresos,DescuentoIgss,DescuentoIsr,DescuentoInasistencias,FechaCreacion,UsuarioCreacion) VALUES (43, 1, '1990-01-01',14,1,8000,250,0,386.4,0,0,now(),'system');
INSERT INTO EMPLEADO (IdPersona,IdSucursal,FechaContratacion,IdPuesto,IdStatusEmpleado,IngresoSueldoBase,IngresoBonificacionDecreto,IngresoOtrosIngresos,DescuentoIgss,DescuentoIsr,DescuentoInasistencias,FechaCreacion,UsuarioCreacion) VALUES (44, 1, '1990-01-01',14,1,8000,250,0,386.4,0,0,now(),'system');
INSERT INTO EMPLEADO (IdPersona,IdSucursal,FechaContratacion,IdPuesto,IdStatusEmpleado,IngresoSueldoBase,IngresoBonificacionDecreto,IngresoOtrosIngresos,DescuentoIgss,DescuentoIsr,DescuentoInasistencias,FechaCreacion,UsuarioCreacion) VALUES (45, 1, '1990-01-01',14,1,8000,250,0,386.4,0,0,now(),'system');
INSERT INTO EMPLEADO (IdPersona,IdSucursal,FechaContratacion,IdPuesto,IdStatusEmpleado,IngresoSueldoBase,IngresoBonificacionDecreto,IngresoOtrosIngresos,DescuentoIgss,DescuentoIsr,DescuentoInasistencias,FechaCreacion,UsuarioCreacion) VALUES (46, 1, '1990-01-01',14,1,8000,250,0,386.4,0,0,now(),'system');
INSERT INTO EMPLEADO (IdPersona,IdSucursal,FechaContratacion,IdPuesto,IdStatusEmpleado,IngresoSueldoBase,IngresoBonificacionDecreto,IngresoOtrosIngresos,DescuentoIgss,DescuentoIsr,DescuentoInasistencias,FechaCreacion,UsuarioCreacion) VALUES (47, 1, '1990-01-01',14,1,8000,250,0,386.4,0,0,now(),'system');
INSERT INTO EMPLEADO (IdPersona,IdSucursal,FechaContratacion,IdPuesto,IdStatusEmpleado,IngresoSueldoBase,IngresoBonificacionDecreto,IngresoOtrosIngresos,DescuentoIgss,DescuentoIsr,DescuentoInasistencias,FechaCreacion,UsuarioCreacion) VALUES (48, 1, '1990-01-01',14,1,8000,250,0,386.4,0,0,now(),'system');
INSERT INTO EMPLEADO (IdPersona,IdSucursal,FechaContratacion,IdPuesto,IdStatusEmpleado,IngresoSueldoBase,IngresoBonificacionDecreto,IngresoOtrosIngresos,DescuentoIgss,DescuentoIsr,DescuentoInasistencias,FechaCreacion,UsuarioCreacion) VALUES (49, 1, '1990-01-01',14,1,8000,250,0,386.4,0,0,now(),'system');
INSERT INTO EMPLEADO (IdPersona,IdSucursal,FechaContratacion,IdPuesto,IdStatusEmpleado,IngresoSueldoBase,IngresoBonificacionDecreto,IngresoOtrosIngresos,DescuentoIgss,DescuentoIsr,DescuentoInasistencias,FechaCreacion,UsuarioCreacion) VALUES (50, 1, '1990-01-01',14,1,8000,250,0,386.4,0,0,now(),'system');
INSERT INTO EMPLEADO (IdPersona,IdSucursal,FechaContratacion,IdPuesto,IdStatusEmpleado,IngresoSueldoBase,IngresoBonificacionDecreto,IngresoOtrosIngresos,DescuentoIgss,DescuentoIsr,DescuentoInasistencias,FechaCreacion,UsuarioCreacion) VALUES (51, 1, '1990-01-01',15,1,12000,250,0,579.6,0,0,now(),'system');
INSERT INTO EMPLEADO (IdPersona,IdSucursal,FechaContratacion,IdPuesto,IdStatusEmpleado,IngresoSueldoBase,IngresoBonificacionDecreto,IngresoOtrosIngresos,DescuentoIgss,DescuentoIsr,DescuentoInasistencias,FechaCreacion,UsuarioCreacion) VALUES (52, 1, '1990-01-01',15,1,12000,250,0,579.6,0,0,now(),'system');
INSERT INTO EMPLEADO (IdPersona,IdSucursal,FechaContratacion,IdPuesto,IdStatusEmpleado,IngresoSueldoBase,IngresoBonificacionDecreto,IngresoOtrosIngresos,DescuentoIgss,DescuentoIsr,DescuentoInasistencias,FechaCreacion,UsuarioCreacion) VALUES (53, 1, '1990-01-01',15,1,12000,250,0,579.6,0,0,now(),'system');
INSERT INTO EMPLEADO (IdPersona,IdSucursal,FechaContratacion,IdPuesto,IdStatusEmpleado,IngresoSueldoBase,IngresoBonificacionDecreto,IngresoOtrosIngresos,DescuentoIgss,DescuentoIsr,DescuentoInasistencias,FechaCreacion,UsuarioCreacion) VALUES (54, 1, '1990-01-01',15,1,12000,250,0,579.6,0,0,now(),'system');
INSERT INTO EMPLEADO (IdPersona,IdSucursal,FechaContratacion,IdPuesto,IdStatusEmpleado,IngresoSueldoBase,IngresoBonificacionDecreto,IngresoOtrosIngresos,DescuentoIgss,DescuentoIsr,DescuentoInasistencias,FechaCreacion,UsuarioCreacion) VALUES (55, 1, '1990-01-01',15,1,12000,250,0,579.6,0,0,now(),'system');
INSERT INTO EMPLEADO (IdPersona,IdSucursal,FechaContratacion,IdPuesto,IdStatusEmpleado,IngresoSueldoBase,IngresoBonificacionDecreto,IngresoOtrosIngresos,DescuentoIgss,DescuentoIsr,DescuentoInasistencias,FechaCreacion,UsuarioCreacion) VALUES (56, 1, '1990-01-01',15,1,12000,250,0,579.6,0,0,now(),'system');
INSERT INTO EMPLEADO (IdPersona,IdSucursal,FechaContratacion,IdPuesto,IdStatusEmpleado,IngresoSueldoBase,IngresoBonificacionDecreto,IngresoOtrosIngresos,DescuentoIgss,DescuentoIsr,DescuentoInasistencias,FechaCreacion,UsuarioCreacion) VALUES (57, 1, '1990-01-01',15,1,12000,250,0,579.6,0,0,now(),'system');
INSERT INTO EMPLEADO (IdPersona,IdSucursal,FechaContratacion,IdPuesto,IdStatusEmpleado,IngresoSueldoBase,IngresoBonificacionDecreto,IngresoOtrosIngresos,DescuentoIgss,DescuentoIsr,DescuentoInasistencias,FechaCreacion,UsuarioCreacion) VALUES (58, 1, '1990-01-01',15,1,12000,250,0,579.6,0,0,now(),'system');
INSERT INTO EMPLEADO (IdPersona,IdSucursal,FechaContratacion,IdPuesto,IdStatusEmpleado,IngresoSueldoBase,IngresoBonificacionDecreto,IngresoOtrosIngresos,DescuentoIgss,DescuentoIsr,DescuentoInasistencias,FechaCreacion,UsuarioCreacion) VALUES (59, 1, '1990-01-01',15,1,12000,250,0,579.6,0,0,now(),'system');
INSERT INTO EMPLEADO (IdPersona,IdSucursal,FechaContratacion,IdPuesto,IdStatusEmpleado,IngresoSueldoBase,IngresoBonificacionDecreto,IngresoOtrosIngresos,DescuentoIgss,DescuentoIsr,DescuentoInasistencias,FechaCreacion,UsuarioCreacion) VALUES (60, 1, '1990-01-01',15,1,12000,250,0,579.6,0,0,now(),'system');
INSERT INTO EMPLEADO (IdPersona,IdSucursal,FechaContratacion,IdPuesto,IdStatusEmpleado,IngresoSueldoBase,IngresoBonificacionDecreto,IngresoOtrosIngresos,DescuentoIgss,DescuentoIsr,DescuentoInasistencias,FechaCreacion,UsuarioCreacion) VALUES (61, 1, '1990-01-01',16,1,11000,250,0,531.3,0,0,now(),'system');
INSERT INTO EMPLEADO (IdPersona,IdSucursal,FechaContratacion,IdPuesto,IdStatusEmpleado,IngresoSueldoBase,IngresoBonificacionDecreto,IngresoOtrosIngresos,DescuentoIgss,DescuentoIsr,DescuentoInasistencias,FechaCreacion,UsuarioCreacion) VALUES (62, 1, '1990-01-01',16,1,11000,250,0,531.3,0,0,now(),'system');
INSERT INTO EMPLEADO (IdPersona,IdSucursal,FechaContratacion,IdPuesto,IdStatusEmpleado,IngresoSueldoBase,IngresoBonificacionDecreto,IngresoOtrosIngresos,DescuentoIgss,DescuentoIsr,DescuentoInasistencias,FechaCreacion,UsuarioCreacion) VALUES (63, 1, '1990-01-01',16,1,11000,250,0,531.3,0,0,now(),'system');
INSERT INTO EMPLEADO (IdPersona,IdSucursal,FechaContratacion,IdPuesto,IdStatusEmpleado,IngresoSueldoBase,IngresoBonificacionDecreto,IngresoOtrosIngresos,DescuentoIgss,DescuentoIsr,DescuentoInasistencias,FechaCreacion,UsuarioCreacion) VALUES (64, 1, '1990-01-01',16,1,11000,250,0,531.3,0,0,now(),'system');
INSERT INTO EMPLEADO (IdPersona,IdSucursal,FechaContratacion,IdPuesto,IdStatusEmpleado,IngresoSueldoBase,IngresoBonificacionDecreto,IngresoOtrosIngresos,DescuentoIgss,DescuentoIsr,DescuentoInasistencias,FechaCreacion,UsuarioCreacion) VALUES (65, 1, '1990-01-01',16,1,11000,250,0,531.3,0,0,now(),'system');
INSERT INTO EMPLEADO (IdPersona,IdSucursal,FechaContratacion,IdPuesto,IdStatusEmpleado,IngresoSueldoBase,IngresoBonificacionDecreto,IngresoOtrosIngresos,DescuentoIgss,DescuentoIsr,DescuentoInasistencias,FechaCreacion,UsuarioCreacion) VALUES (66, 1, '1990-01-01',17,1,11500,250,0,555.45,0,0,now(),'system');
INSERT INTO EMPLEADO (IdPersona,IdSucursal,FechaContratacion,IdPuesto,IdStatusEmpleado,IngresoSueldoBase,IngresoBonificacionDecreto,IngresoOtrosIngresos,DescuentoIgss,DescuentoIsr,DescuentoInasistencias,FechaCreacion,UsuarioCreacion) VALUES (67, 1, '1990-01-01',17,1,11500,250,0,555.45,0,0,now(),'system');
INSERT INTO EMPLEADO (IdPersona,IdSucursal,FechaContratacion,IdPuesto,IdStatusEmpleado,IngresoSueldoBase,IngresoBonificacionDecreto,IngresoOtrosIngresos,DescuentoIgss,DescuentoIsr,DescuentoInasistencias,FechaCreacion,UsuarioCreacion) VALUES (68, 1, '1990-01-01',17,1,11500,250,0,555.45,0,0,now(),'system');
INSERT INTO EMPLEADO (IdPersona,IdSucursal,FechaContratacion,IdPuesto,IdStatusEmpleado,IngresoSueldoBase,IngresoBonificacionDecreto,IngresoOtrosIngresos,DescuentoIgss,DescuentoIsr,DescuentoInasistencias,FechaCreacion,UsuarioCreacion) VALUES (69, 1, '1990-01-01',17,1,11500,250,0,555.45,0,0,now(),'system');
INSERT INTO EMPLEADO (IdPersona,IdSucursal,FechaContratacion,IdPuesto,IdStatusEmpleado,IngresoSueldoBase,IngresoBonificacionDecreto,IngresoOtrosIngresos,DescuentoIgss,DescuentoIsr,DescuentoInasistencias,FechaCreacion,UsuarioCreacion) VALUES (70, 1, '1990-01-01',17,1,11500,250,0,555.45,0,0,now(),'system');
INSERT INTO EMPLEADO (IdPersona,IdSucursal,FechaContratacion,IdPuesto,IdStatusEmpleado,IngresoSueldoBase,IngresoBonificacionDecreto,IngresoOtrosIngresos,DescuentoIgss,DescuentoIsr,DescuentoInasistencias,FechaCreacion,UsuarioCreacion) VALUES (71, 1, '1990-01-01',18,1,13000,250,0,627.9,0,0,now(),'system');
INSERT INTO EMPLEADO (IdPersona,IdSucursal,FechaContratacion,IdPuesto,IdStatusEmpleado,IngresoSueldoBase,IngresoBonificacionDecreto,IngresoOtrosIngresos,DescuentoIgss,DescuentoIsr,DescuentoInasistencias,FechaCreacion,UsuarioCreacion) VALUES (72, 1, '1990-01-01',18,1,13000,250,0,627.9,0,0,now(),'system');
INSERT INTO EMPLEADO (IdPersona,IdSucursal,FechaContratacion,IdPuesto,IdStatusEmpleado,IngresoSueldoBase,IngresoBonificacionDecreto,IngresoOtrosIngresos,DescuentoIgss,DescuentoIsr,DescuentoInasistencias,FechaCreacion,UsuarioCreacion) VALUES (73, 1, '1990-01-01',19,1,18000,250,0,869.4,0,0,now(),'system');
INSERT INTO EMPLEADO (IdPersona,IdSucursal,FechaContratacion,IdPuesto,IdStatusEmpleado,IngresoSueldoBase,IngresoBonificacionDecreto,IngresoOtrosIngresos,DescuentoIgss,DescuentoIsr,DescuentoInasistencias,FechaCreacion,UsuarioCreacion) VALUES (74, 1, '1990-01-01',20,1,16500,250,0,796.95,0,0,now(),'system');
INSERT INTO EMPLEADO (IdPersona,IdSucursal,FechaContratacion,IdPuesto,IdStatusEmpleado,IngresoSueldoBase,IngresoBonificacionDecreto,IngresoOtrosIngresos,DescuentoIgss,DescuentoIsr,DescuentoInasistencias,FechaCreacion,UsuarioCreacion) VALUES (75, 1, '1990-01-01',21,1,17000,250,0,821.1,0,0,now(),'system');
INSERT INTO EMPLEADO (IdPersona,IdSucursal,FechaContratacion,IdPuesto,IdStatusEmpleado,IngresoSueldoBase,IngresoBonificacionDecreto,IngresoOtrosIngresos,DescuentoIgss,DescuentoIsr,DescuentoInasistencias,FechaCreacion,UsuarioCreacion) VALUES (76, 1, '1990-01-01',22,1,17500,250,0,845.25,0,0,now(),'system');
INSERT INTO EMPLEADO (IdPersona,IdSucursal,FechaContratacion,IdPuesto,IdStatusEmpleado,IngresoSueldoBase,IngresoBonificacionDecreto,IngresoOtrosIngresos,DescuentoIgss,DescuentoIsr,DescuentoInasistencias,FechaCreacion,UsuarioCreacion) VALUES (77, 1, '1990-01-01',23,1,15000,250,0,724.5,0,0,now(),'system');
INSERT INTO EMPLEADO (IdPersona,IdSucursal,FechaContratacion,IdPuesto,IdStatusEmpleado,IngresoSueldoBase,IngresoBonificacionDecreto,IngresoOtrosIngresos,DescuentoIgss,DescuentoIsr,DescuentoInasistencias,FechaCreacion,UsuarioCreacion) VALUES (78, 1, '1990-01-01',24,1,15000,250,0,724.5,0,0,now(),'system');
INSERT INTO EMPLEADO (IdPersona,IdSucursal,FechaContratacion,IdPuesto,IdStatusEmpleado,IngresoSueldoBase,IngresoBonificacionDecreto,IngresoOtrosIngresos,DescuentoIgss,DescuentoIsr,DescuentoInasistencias,FechaCreacion,UsuarioCreacion) VALUES (79, 1, '1990-01-01',20,1,16000,250,0,772.8,0,0,now(),'system');
INSERT INTO EMPLEADO (IdPersona,IdSucursal,FechaContratacion,IdPuesto,IdStatusEmpleado,IngresoSueldoBase,IngresoBonificacionDecreto,IngresoOtrosIngresos,DescuentoIgss,DescuentoIsr,DescuentoInasistencias,FechaCreacion,UsuarioCreacion) VALUES (80, 1, '1990-01-01',25,1,20000,250,0,966,0,0,now(),'system');
INSERT INTO EMPLEADO (IdPersona,IdSucursal,FechaContratacion,IdPuesto,IdStatusEmpleado,IngresoSueldoBase,IngresoBonificacionDecreto,IngresoOtrosIngresos,DescuentoIgss,DescuentoIsr,DescuentoInasistencias,FechaCreacion,UsuarioCreacion) VALUES (81, 1, '1990-01-01',26,1,11500,250,0,555.45,0,0,now(),'system');
INSERT INTO EMPLEADO (IdPersona,IdSucursal,FechaContratacion,IdPuesto,IdStatusEmpleado,IngresoSueldoBase,IngresoBonificacionDecreto,IngresoOtrosIngresos,DescuentoIgss,DescuentoIsr,DescuentoInasistencias,FechaCreacion,UsuarioCreacion) VALUES (82, 1, '1990-01-01',26,1,11500,250,0,555.45,0,0,now(),'system');
INSERT INTO EMPLEADO (IdPersona,IdSucursal,FechaContratacion,IdPuesto,IdStatusEmpleado,IngresoSueldoBase,IngresoBonificacionDecreto,IngresoOtrosIngresos,DescuentoIgss,DescuentoIsr,DescuentoInasistencias,FechaCreacion,UsuarioCreacion) VALUES (83, 1, '1990-01-01',26,1,11500,250,0,555.45,0,0,now(),'system');
INSERT INTO EMPLEADO (IdPersona,IdSucursal,FechaContratacion,IdPuesto,IdStatusEmpleado,IngresoSueldoBase,IngresoBonificacionDecreto,IngresoOtrosIngresos,DescuentoIgss,DescuentoIsr,DescuentoInasistencias,FechaCreacion,UsuarioCreacion) VALUES (84, 1, '1990-01-01',26,1,11500,250,0,555.45,0,0,now(),'system');
INSERT INTO EMPLEADO (IdPersona,IdSucursal,FechaContratacion,IdPuesto,IdStatusEmpleado,IngresoSueldoBase,IngresoBonificacionDecreto,IngresoOtrosIngresos,DescuentoIgss,DescuentoIsr,DescuentoInasistencias,FechaCreacion,UsuarioCreacion) VALUES (85, 1, '1990-01-01',26,1,11500,250,0,555.45,0,0,now(),'system');
INSERT INTO EMPLEADO (IdPersona,IdSucursal,FechaContratacion,IdPuesto,IdStatusEmpleado,IngresoSueldoBase,IngresoBonificacionDecreto,IngresoOtrosIngresos,DescuentoIgss,DescuentoIsr,DescuentoInasistencias,FechaCreacion,UsuarioCreacion) VALUES (86, 1, '1990-01-01',27,1,8500,250,0,410.55,0,0,now(),'system');
INSERT INTO EMPLEADO (IdPersona,IdSucursal,FechaContratacion,IdPuesto,IdStatusEmpleado,IngresoSueldoBase,IngresoBonificacionDecreto,IngresoOtrosIngresos,DescuentoIgss,DescuentoIsr,DescuentoInasistencias,FechaCreacion,UsuarioCreacion) VALUES (87, 1, '1990-01-01',27,1,8500,250,0,410.55,0,0,now(),'system');
INSERT INTO EMPLEADO (IdPersona,IdSucursal,FechaContratacion,IdPuesto,IdStatusEmpleado,IngresoSueldoBase,IngresoBonificacionDecreto,IngresoOtrosIngresos,DescuentoIgss,DescuentoIsr,DescuentoInasistencias,FechaCreacion,UsuarioCreacion) VALUES (88, 1, '1990-01-01',27,1,8500,250,0,410.55,0,0,now(),'system');
INSERT INTO EMPLEADO (IdPersona,IdSucursal,FechaContratacion,IdPuesto,IdStatusEmpleado,IngresoSueldoBase,IngresoBonificacionDecreto,IngresoOtrosIngresos,DescuentoIgss,DescuentoIsr,DescuentoInasistencias,FechaCreacion,UsuarioCreacion) VALUES (89, 1, '1990-01-01',27,1,8500,250,0,410.55,0,0,now(),'system');
INSERT INTO EMPLEADO (IdPersona,IdSucursal,FechaContratacion,IdPuesto,IdStatusEmpleado,IngresoSueldoBase,IngresoBonificacionDecreto,IngresoOtrosIngresos,DescuentoIgss,DescuentoIsr,DescuentoInasistencias,FechaCreacion,UsuarioCreacion) VALUES (90, 1, '1990-01-01',27,1,8500,250,0,410.55,0,0,now(),'system');
INSERT INTO EMPLEADO (IdPersona,IdSucursal,FechaContratacion,IdPuesto,IdStatusEmpleado,IngresoSueldoBase,IngresoBonificacionDecreto,IngresoOtrosIngresos,DescuentoIgss,DescuentoIsr,DescuentoInasistencias,FechaCreacion,UsuarioCreacion) VALUES (91, 1, '1990-01-01',28,1,7500,250,0,362.25,0,0,now(),'system');
INSERT INTO EMPLEADO (IdPersona,IdSucursal,FechaContratacion,IdPuesto,IdStatusEmpleado,IngresoSueldoBase,IngresoBonificacionDecreto,IngresoOtrosIngresos,DescuentoIgss,DescuentoIsr,DescuentoInasistencias,FechaCreacion,UsuarioCreacion) VALUES (92, 1, '1990-01-01',28,1,7500,250,0,362.25,0,0,now(),'system');
INSERT INTO EMPLEADO (IdPersona,IdSucursal,FechaContratacion,IdPuesto,IdStatusEmpleado,IngresoSueldoBase,IngresoBonificacionDecreto,IngresoOtrosIngresos,DescuentoIgss,DescuentoIsr,DescuentoInasistencias,FechaCreacion,UsuarioCreacion) VALUES (93, 1, '1990-01-01',28,1,7500,250,0,362.25,0,0,now(),'system');
INSERT INTO EMPLEADO (IdPersona,IdSucursal,FechaContratacion,IdPuesto,IdStatusEmpleado,IngresoSueldoBase,IngresoBonificacionDecreto,IngresoOtrosIngresos,DescuentoIgss,DescuentoIsr,DescuentoInasistencias,FechaCreacion,UsuarioCreacion) VALUES (94, 1, '1990-01-01',28,1,7500,250,0,362.25,0,0,now(),'system');
INSERT INTO EMPLEADO (IdPersona,IdSucursal,FechaContratacion,IdPuesto,IdStatusEmpleado,IngresoSueldoBase,IngresoBonificacionDecreto,IngresoOtrosIngresos,DescuentoIgss,DescuentoIsr,DescuentoInasistencias,FechaCreacion,UsuarioCreacion) VALUES (95, 1, '1990-01-01',28,1,7500,250,0,362.25,0,0,now(),'system');
INSERT INTO EMPLEADO (IdPersona,IdSucursal,FechaContratacion,IdPuesto,IdStatusEmpleado,IngresoSueldoBase,IngresoBonificacionDecreto,IngresoOtrosIngresos,DescuentoIgss,DescuentoIsr,DescuentoInasistencias,FechaCreacion,UsuarioCreacion) VALUES (96, 1, '1990-01-01',29,1,9000,250,0,434.7,0,0,now(),'system');
INSERT INTO EMPLEADO (IdPersona,IdSucursal,FechaContratacion,IdPuesto,IdStatusEmpleado,IngresoSueldoBase,IngresoBonificacionDecreto,IngresoOtrosIngresos,DescuentoIgss,DescuentoIsr,DescuentoInasistencias,FechaCreacion,UsuarioCreacion) VALUES (97, 1, '1990-01-01',29,1,9000,250,0,434.7,0,0,now(),'system');
INSERT INTO EMPLEADO (IdPersona,IdSucursal,FechaContratacion,IdPuesto,IdStatusEmpleado,IngresoSueldoBase,IngresoBonificacionDecreto,IngresoOtrosIngresos,DescuentoIgss,DescuentoIsr,DescuentoInasistencias,FechaCreacion,UsuarioCreacion) VALUES (98, 1, '1990-01-01',29,1,9000,250,0,434.7,0,0,now(),'system');
INSERT INTO EMPLEADO (IdPersona,IdSucursal,FechaContratacion,IdPuesto,IdStatusEmpleado,IngresoSueldoBase,IngresoBonificacionDecreto,IngresoOtrosIngresos,DescuentoIgss,DescuentoIsr,DescuentoInasistencias,FechaCreacion,UsuarioCreacion) VALUES (99, 1, '1990-01-01',29,1,9000,250,0,434.7,0,0,now(),'system');
INSERT INTO EMPLEADO (IdPersona,IdSucursal,FechaContratacion,IdPuesto,IdStatusEmpleado,IngresoSueldoBase,IngresoBonificacionDecreto,IngresoOtrosIngresos,DescuentoIgss,DescuentoIsr,DescuentoInasistencias,FechaCreacion,UsuarioCreacion) VALUES (100, 1, '1990-01-01',29,1,9000,250,0,434.7,0,0,now(),'system');

create table CUENTA_BANCARIA_EMPLEADO(
	IdCuentaBancaria int not null auto_increment,
	IdEmpleado int not null,
	IdBanco int not null,
	NumeroDeCuenta varchar(50) not null,
	Activa char not null,
	FechaCreacion datetime not null,
	UsuarioCreacion varchar(100) not null,
	FechaModificacion datetime,
	UsuarioModificacion varchar(100),
	primary key (IdCuentaBancaria),
	foreign key (IdBanco) references BANCO(IdBanco),
	foreign key (IdEmpleado) references EMPLEADO(IdEmpleado)
);

INSERT INTO CUENTA_BANCARIA_EMPLEADO (IdEmpleado,IdBanco,NumeroDeCuenta,Activa,FechaCreacion,UsuarioCreacion) VALUES (1,1,'001-438715',1,now(),'system');
INSERT INTO CUENTA_BANCARIA_EMPLEADO (IdEmpleado,IdBanco,NumeroDeCuenta,Activa,FechaCreacion,UsuarioCreacion) VALUES (2,1,'001-558735',1,now(),'system');
INSERT INTO CUENTA_BANCARIA_EMPLEADO (IdEmpleado,IdBanco,NumeroDeCuenta,Activa,FechaCreacion,UsuarioCreacion) VALUES (3,1,'001-64453',1,now(),'system');
INSERT INTO CUENTA_BANCARIA_EMPLEADO (IdEmpleado,IdBanco,NumeroDeCuenta,Activa,FechaCreacion,UsuarioCreacion) VALUES (4,1,'001-621118',1,now(),'system');
INSERT INTO CUENTA_BANCARIA_EMPLEADO (IdEmpleado,IdBanco,NumeroDeCuenta,Activa,FechaCreacion,UsuarioCreacion) VALUES (5,1,'001-978141',1,now(),'system');
INSERT INTO CUENTA_BANCARIA_EMPLEADO (IdEmpleado,IdBanco,NumeroDeCuenta,Activa,FechaCreacion,UsuarioCreacion) VALUES (6,1,'001-40358',1,now(),'system');
INSERT INTO CUENTA_BANCARIA_EMPLEADO (IdEmpleado,IdBanco,NumeroDeCuenta,Activa,FechaCreacion,UsuarioCreacion) VALUES (7,1,'001-231070',1,now(),'system');
INSERT INTO CUENTA_BANCARIA_EMPLEADO (IdEmpleado,IdBanco,NumeroDeCuenta,Activa,FechaCreacion,UsuarioCreacion) VALUES (8,1,'001-727511',1,now(),'system');
INSERT INTO CUENTA_BANCARIA_EMPLEADO (IdEmpleado,IdBanco,NumeroDeCuenta,Activa,FechaCreacion,UsuarioCreacion) VALUES (9,1,'001-706605',1,now(),'system');
INSERT INTO CUENTA_BANCARIA_EMPLEADO (IdEmpleado,IdBanco,NumeroDeCuenta,Activa,FechaCreacion,UsuarioCreacion) VALUES (10,1,'001-571357',1,now(),'system');
INSERT INTO CUENTA_BANCARIA_EMPLEADO (IdEmpleado,IdBanco,NumeroDeCuenta,Activa,FechaCreacion,UsuarioCreacion) VALUES (11,1,'001-696825',1,now(),'system');
INSERT INTO CUENTA_BANCARIA_EMPLEADO (IdEmpleado,IdBanco,NumeroDeCuenta,Activa,FechaCreacion,UsuarioCreacion) VALUES (12,1,'001-200179',1,now(),'system');
INSERT INTO CUENTA_BANCARIA_EMPLEADO (IdEmpleado,IdBanco,NumeroDeCuenta,Activa,FechaCreacion,UsuarioCreacion) VALUES (13,1,'001-158740',1,now(),'system');
INSERT INTO CUENTA_BANCARIA_EMPLEADO (IdEmpleado,IdBanco,NumeroDeCuenta,Activa,FechaCreacion,UsuarioCreacion) VALUES (14,1,'001-115850',1,now(),'system');
INSERT INTO CUENTA_BANCARIA_EMPLEADO (IdEmpleado,IdBanco,NumeroDeCuenta,Activa,FechaCreacion,UsuarioCreacion) VALUES (15,1,'001-814040',1,now(),'system');
INSERT INTO CUENTA_BANCARIA_EMPLEADO (IdEmpleado,IdBanco,NumeroDeCuenta,Activa,FechaCreacion,UsuarioCreacion) VALUES (16,1,'001-668389',1,now(),'system');
INSERT INTO CUENTA_BANCARIA_EMPLEADO (IdEmpleado,IdBanco,NumeroDeCuenta,Activa,FechaCreacion,UsuarioCreacion) VALUES (17,1,'001-760920',1,now(),'system');
INSERT INTO CUENTA_BANCARIA_EMPLEADO (IdEmpleado,IdBanco,NumeroDeCuenta,Activa,FechaCreacion,UsuarioCreacion) VALUES (18,1,'001-651240',1,now(),'system');
INSERT INTO CUENTA_BANCARIA_EMPLEADO (IdEmpleado,IdBanco,NumeroDeCuenta,Activa,FechaCreacion,UsuarioCreacion) VALUES (19,1,'001-771241',1,now(),'system');
INSERT INTO CUENTA_BANCARIA_EMPLEADO (IdEmpleado,IdBanco,NumeroDeCuenta,Activa,FechaCreacion,UsuarioCreacion) VALUES (20,1,'001-397393',1,now(),'system');
INSERT INTO CUENTA_BANCARIA_EMPLEADO (IdEmpleado,IdBanco,NumeroDeCuenta,Activa,FechaCreacion,UsuarioCreacion) VALUES (21,1,'001-599417',1,now(),'system');
INSERT INTO CUENTA_BANCARIA_EMPLEADO (IdEmpleado,IdBanco,NumeroDeCuenta,Activa,FechaCreacion,UsuarioCreacion) VALUES (22,1,'001-729855',1,now(),'system');
INSERT INTO CUENTA_BANCARIA_EMPLEADO (IdEmpleado,IdBanco,NumeroDeCuenta,Activa,FechaCreacion,UsuarioCreacion) VALUES (23,1,'001-82932',1,now(),'system');
INSERT INTO CUENTA_BANCARIA_EMPLEADO (IdEmpleado,IdBanco,NumeroDeCuenta,Activa,FechaCreacion,UsuarioCreacion) VALUES (24,1,'001-25599',1,now(),'system');
INSERT INTO CUENTA_BANCARIA_EMPLEADO (IdEmpleado,IdBanco,NumeroDeCuenta,Activa,FechaCreacion,UsuarioCreacion) VALUES (25,1,'001-300196',1,now(),'system');
INSERT INTO CUENTA_BANCARIA_EMPLEADO (IdEmpleado,IdBanco,NumeroDeCuenta,Activa,FechaCreacion,UsuarioCreacion) VALUES (26,1,'001-157559',1,now(),'system');
INSERT INTO CUENTA_BANCARIA_EMPLEADO (IdEmpleado,IdBanco,NumeroDeCuenta,Activa,FechaCreacion,UsuarioCreacion) VALUES (27,1,'001-863934',1,now(),'system');
INSERT INTO CUENTA_BANCARIA_EMPLEADO (IdEmpleado,IdBanco,NumeroDeCuenta,Activa,FechaCreacion,UsuarioCreacion) VALUES (28,1,'001-113147',1,now(),'system');
INSERT INTO CUENTA_BANCARIA_EMPLEADO (IdEmpleado,IdBanco,NumeroDeCuenta,Activa,FechaCreacion,UsuarioCreacion) VALUES (29,1,'001-460289',1,now(),'system');
INSERT INTO CUENTA_BANCARIA_EMPLEADO (IdEmpleado,IdBanco,NumeroDeCuenta,Activa,FechaCreacion,UsuarioCreacion) VALUES (30,1,'001-492961',1,now(),'system');
INSERT INTO CUENTA_BANCARIA_EMPLEADO (IdEmpleado,IdBanco,NumeroDeCuenta,Activa,FechaCreacion,UsuarioCreacion) VALUES (31,1,'001-994257',1,now(),'system');
INSERT INTO CUENTA_BANCARIA_EMPLEADO (IdEmpleado,IdBanco,NumeroDeCuenta,Activa,FechaCreacion,UsuarioCreacion) VALUES (32,1,'001-114227',1,now(),'system');
INSERT INTO CUENTA_BANCARIA_EMPLEADO (IdEmpleado,IdBanco,NumeroDeCuenta,Activa,FechaCreacion,UsuarioCreacion) VALUES (33,1,'001-246671',1,now(),'system');
INSERT INTO CUENTA_BANCARIA_EMPLEADO (IdEmpleado,IdBanco,NumeroDeCuenta,Activa,FechaCreacion,UsuarioCreacion) VALUES (34,1,'001-355075',1,now(),'system');
INSERT INTO CUENTA_BANCARIA_EMPLEADO (IdEmpleado,IdBanco,NumeroDeCuenta,Activa,FechaCreacion,UsuarioCreacion) VALUES (35,1,'001-506552',1,now(),'system');
INSERT INTO CUENTA_BANCARIA_EMPLEADO (IdEmpleado,IdBanco,NumeroDeCuenta,Activa,FechaCreacion,UsuarioCreacion) VALUES (36,1,'001-699357',1,now(),'system');
INSERT INTO CUENTA_BANCARIA_EMPLEADO (IdEmpleado,IdBanco,NumeroDeCuenta,Activa,FechaCreacion,UsuarioCreacion) VALUES (37,1,'001-145851',1,now(),'system');
INSERT INTO CUENTA_BANCARIA_EMPLEADO (IdEmpleado,IdBanco,NumeroDeCuenta,Activa,FechaCreacion,UsuarioCreacion) VALUES (38,1,'001-861387',1,now(),'system');
INSERT INTO CUENTA_BANCARIA_EMPLEADO (IdEmpleado,IdBanco,NumeroDeCuenta,Activa,FechaCreacion,UsuarioCreacion) VALUES (39,1,'001-582162',1,now(),'system');
INSERT INTO CUENTA_BANCARIA_EMPLEADO (IdEmpleado,IdBanco,NumeroDeCuenta,Activa,FechaCreacion,UsuarioCreacion) VALUES (40,1,'001-802774',1,now(),'system');
INSERT INTO CUENTA_BANCARIA_EMPLEADO (IdEmpleado,IdBanco,NumeroDeCuenta,Activa,FechaCreacion,UsuarioCreacion) VALUES (41,1,'001-704741',1,now(),'system');
INSERT INTO CUENTA_BANCARIA_EMPLEADO (IdEmpleado,IdBanco,NumeroDeCuenta,Activa,FechaCreacion,UsuarioCreacion) VALUES (42,1,'001-30571',1,now(),'system');
INSERT INTO CUENTA_BANCARIA_EMPLEADO (IdEmpleado,IdBanco,NumeroDeCuenta,Activa,FechaCreacion,UsuarioCreacion) VALUES (43,1,'001-331482',1,now(),'system');
INSERT INTO CUENTA_BANCARIA_EMPLEADO (IdEmpleado,IdBanco,NumeroDeCuenta,Activa,FechaCreacion,UsuarioCreacion) VALUES (44,1,'001-569494',1,now(),'system');
INSERT INTO CUENTA_BANCARIA_EMPLEADO (IdEmpleado,IdBanco,NumeroDeCuenta,Activa,FechaCreacion,UsuarioCreacion) VALUES (45,1,'001-189448',1,now(),'system');
INSERT INTO CUENTA_BANCARIA_EMPLEADO (IdEmpleado,IdBanco,NumeroDeCuenta,Activa,FechaCreacion,UsuarioCreacion) VALUES (46,1,'001-779129',1,now(),'system');
INSERT INTO CUENTA_BANCARIA_EMPLEADO (IdEmpleado,IdBanco,NumeroDeCuenta,Activa,FechaCreacion,UsuarioCreacion) VALUES (47,1,'001-245511',1,now(),'system');
INSERT INTO CUENTA_BANCARIA_EMPLEADO (IdEmpleado,IdBanco,NumeroDeCuenta,Activa,FechaCreacion,UsuarioCreacion) VALUES (48,1,'001-817188',1,now(),'system');
INSERT INTO CUENTA_BANCARIA_EMPLEADO (IdEmpleado,IdBanco,NumeroDeCuenta,Activa,FechaCreacion,UsuarioCreacion) VALUES (49,1,'001-519020',1,now(),'system');
INSERT INTO CUENTA_BANCARIA_EMPLEADO (IdEmpleado,IdBanco,NumeroDeCuenta,Activa,FechaCreacion,UsuarioCreacion) VALUES (50,1,'001-988187',1,now(),'system');
INSERT INTO CUENTA_BANCARIA_EMPLEADO (IdEmpleado,IdBanco,NumeroDeCuenta,Activa,FechaCreacion,UsuarioCreacion) VALUES (51,1,'001-861925',1,now(),'system');
INSERT INTO CUENTA_BANCARIA_EMPLEADO (IdEmpleado,IdBanco,NumeroDeCuenta,Activa,FechaCreacion,UsuarioCreacion) VALUES (52,1,'001-367063',1,now(),'system');
INSERT INTO CUENTA_BANCARIA_EMPLEADO (IdEmpleado,IdBanco,NumeroDeCuenta,Activa,FechaCreacion,UsuarioCreacion) VALUES (53,1,'001-684173',1,now(),'system');
INSERT INTO CUENTA_BANCARIA_EMPLEADO (IdEmpleado,IdBanco,NumeroDeCuenta,Activa,FechaCreacion,UsuarioCreacion) VALUES (54,1,'001-691814',1,now(),'system');
INSERT INTO CUENTA_BANCARIA_EMPLEADO (IdEmpleado,IdBanco,NumeroDeCuenta,Activa,FechaCreacion,UsuarioCreacion) VALUES (55,1,'001-199273',1,now(),'system');
INSERT INTO CUENTA_BANCARIA_EMPLEADO (IdEmpleado,IdBanco,NumeroDeCuenta,Activa,FechaCreacion,UsuarioCreacion) VALUES (56,1,'001-565048',1,now(),'system');
INSERT INTO CUENTA_BANCARIA_EMPLEADO (IdEmpleado,IdBanco,NumeroDeCuenta,Activa,FechaCreacion,UsuarioCreacion) VALUES (57,1,'001-647879',1,now(),'system');
INSERT INTO CUENTA_BANCARIA_EMPLEADO (IdEmpleado,IdBanco,NumeroDeCuenta,Activa,FechaCreacion,UsuarioCreacion) VALUES (58,1,'001-51395',1,now(),'system');
INSERT INTO CUENTA_BANCARIA_EMPLEADO (IdEmpleado,IdBanco,NumeroDeCuenta,Activa,FechaCreacion,UsuarioCreacion) VALUES (59,1,'001-76703',1,now(),'system');
INSERT INTO CUENTA_BANCARIA_EMPLEADO (IdEmpleado,IdBanco,NumeroDeCuenta,Activa,FechaCreacion,UsuarioCreacion) VALUES (60,1,'001-413422',1,now(),'system');
INSERT INTO CUENTA_BANCARIA_EMPLEADO (IdEmpleado,IdBanco,NumeroDeCuenta,Activa,FechaCreacion,UsuarioCreacion) VALUES (61,1,'001-126127',1,now(),'system');
INSERT INTO CUENTA_BANCARIA_EMPLEADO (IdEmpleado,IdBanco,NumeroDeCuenta,Activa,FechaCreacion,UsuarioCreacion) VALUES (62,1,'001-20889',1,now(),'system');
INSERT INTO CUENTA_BANCARIA_EMPLEADO (IdEmpleado,IdBanco,NumeroDeCuenta,Activa,FechaCreacion,UsuarioCreacion) VALUES (63,1,'001-92161',1,now(),'system');
INSERT INTO CUENTA_BANCARIA_EMPLEADO (IdEmpleado,IdBanco,NumeroDeCuenta,Activa,FechaCreacion,UsuarioCreacion) VALUES (64,1,'001-646080',1,now(),'system');
INSERT INTO CUENTA_BANCARIA_EMPLEADO (IdEmpleado,IdBanco,NumeroDeCuenta,Activa,FechaCreacion,UsuarioCreacion) VALUES (65,1,'001-368269',1,now(),'system');
INSERT INTO CUENTA_BANCARIA_EMPLEADO (IdEmpleado,IdBanco,NumeroDeCuenta,Activa,FechaCreacion,UsuarioCreacion) VALUES (66,1,'001-322705',1,now(),'system');
INSERT INTO CUENTA_BANCARIA_EMPLEADO (IdEmpleado,IdBanco,NumeroDeCuenta,Activa,FechaCreacion,UsuarioCreacion) VALUES (67,1,'001-195940',1,now(),'system');
INSERT INTO CUENTA_BANCARIA_EMPLEADO (IdEmpleado,IdBanco,NumeroDeCuenta,Activa,FechaCreacion,UsuarioCreacion) VALUES (68,1,'001-628616',1,now(),'system');
INSERT INTO CUENTA_BANCARIA_EMPLEADO (IdEmpleado,IdBanco,NumeroDeCuenta,Activa,FechaCreacion,UsuarioCreacion) VALUES (69,1,'001-831602',1,now(),'system');
INSERT INTO CUENTA_BANCARIA_EMPLEADO (IdEmpleado,IdBanco,NumeroDeCuenta,Activa,FechaCreacion,UsuarioCreacion) VALUES (70,1,'001-152077',1,now(),'system');
INSERT INTO CUENTA_BANCARIA_EMPLEADO (IdEmpleado,IdBanco,NumeroDeCuenta,Activa,FechaCreacion,UsuarioCreacion) VALUES (71,1,'001-244738',1,now(),'system');
INSERT INTO CUENTA_BANCARIA_EMPLEADO (IdEmpleado,IdBanco,NumeroDeCuenta,Activa,FechaCreacion,UsuarioCreacion) VALUES (72,1,'001-318647',1,now(),'system');
INSERT INTO CUENTA_BANCARIA_EMPLEADO (IdEmpleado,IdBanco,NumeroDeCuenta,Activa,FechaCreacion,UsuarioCreacion) VALUES (73,1,'001-320519',1,now(),'system');
INSERT INTO CUENTA_BANCARIA_EMPLEADO (IdEmpleado,IdBanco,NumeroDeCuenta,Activa,FechaCreacion,UsuarioCreacion) VALUES (74,1,'001-452667',1,now(),'system');
INSERT INTO CUENTA_BANCARIA_EMPLEADO (IdEmpleado,IdBanco,NumeroDeCuenta,Activa,FechaCreacion,UsuarioCreacion) VALUES (75,1,'001-276056',1,now(),'system');
INSERT INTO CUENTA_BANCARIA_EMPLEADO (IdEmpleado,IdBanco,NumeroDeCuenta,Activa,FechaCreacion,UsuarioCreacion) VALUES (76,1,'001-98199',1,now(),'system');
INSERT INTO CUENTA_BANCARIA_EMPLEADO (IdEmpleado,IdBanco,NumeroDeCuenta,Activa,FechaCreacion,UsuarioCreacion) VALUES (77,1,'001-845392',1,now(),'system');
INSERT INTO CUENTA_BANCARIA_EMPLEADO (IdEmpleado,IdBanco,NumeroDeCuenta,Activa,FechaCreacion,UsuarioCreacion) VALUES (78,1,'001-811508',1,now(),'system');
INSERT INTO CUENTA_BANCARIA_EMPLEADO (IdEmpleado,IdBanco,NumeroDeCuenta,Activa,FechaCreacion,UsuarioCreacion) VALUES (79,1,'001-776670',1,now(),'system');
INSERT INTO CUENTA_BANCARIA_EMPLEADO (IdEmpleado,IdBanco,NumeroDeCuenta,Activa,FechaCreacion,UsuarioCreacion) VALUES (80,1,'001-472373',1,now(),'system');
INSERT INTO CUENTA_BANCARIA_EMPLEADO (IdEmpleado,IdBanco,NumeroDeCuenta,Activa,FechaCreacion,UsuarioCreacion) VALUES (81,1,'001-289894',1,now(),'system');
INSERT INTO CUENTA_BANCARIA_EMPLEADO (IdEmpleado,IdBanco,NumeroDeCuenta,Activa,FechaCreacion,UsuarioCreacion) VALUES (82,1,'001-102470',1,now(),'system');
INSERT INTO CUENTA_BANCARIA_EMPLEADO (IdEmpleado,IdBanco,NumeroDeCuenta,Activa,FechaCreacion,UsuarioCreacion) VALUES (83,1,'001-808041',1,now(),'system');
INSERT INTO CUENTA_BANCARIA_EMPLEADO (IdEmpleado,IdBanco,NumeroDeCuenta,Activa,FechaCreacion,UsuarioCreacion) VALUES (84,1,'001-737762',1,now(),'system');
INSERT INTO CUENTA_BANCARIA_EMPLEADO (IdEmpleado,IdBanco,NumeroDeCuenta,Activa,FechaCreacion,UsuarioCreacion) VALUES (85,1,'001-676409',1,now(),'system');
INSERT INTO CUENTA_BANCARIA_EMPLEADO (IdEmpleado,IdBanco,NumeroDeCuenta,Activa,FechaCreacion,UsuarioCreacion) VALUES (86,1,'001-243232',1,now(),'system');
INSERT INTO CUENTA_BANCARIA_EMPLEADO (IdEmpleado,IdBanco,NumeroDeCuenta,Activa,FechaCreacion,UsuarioCreacion) VALUES (87,1,'001-809063',1,now(),'system');
INSERT INTO CUENTA_BANCARIA_EMPLEADO (IdEmpleado,IdBanco,NumeroDeCuenta,Activa,FechaCreacion,UsuarioCreacion) VALUES (88,1,'001-772757',1,now(),'system');
INSERT INTO CUENTA_BANCARIA_EMPLEADO (IdEmpleado,IdBanco,NumeroDeCuenta,Activa,FechaCreacion,UsuarioCreacion) VALUES (89,1,'001-260617',1,now(),'system');
INSERT INTO CUENTA_BANCARIA_EMPLEADO (IdEmpleado,IdBanco,NumeroDeCuenta,Activa,FechaCreacion,UsuarioCreacion) VALUES (90,1,'001-543277',1,now(),'system');
INSERT INTO CUENTA_BANCARIA_EMPLEADO (IdEmpleado,IdBanco,NumeroDeCuenta,Activa,FechaCreacion,UsuarioCreacion) VALUES (91,1,'001-79322',1,now(),'system');
INSERT INTO CUENTA_BANCARIA_EMPLEADO (IdEmpleado,IdBanco,NumeroDeCuenta,Activa,FechaCreacion,UsuarioCreacion) VALUES (92,1,'001-254619',1,now(),'system');
INSERT INTO CUENTA_BANCARIA_EMPLEADO (IdEmpleado,IdBanco,NumeroDeCuenta,Activa,FechaCreacion,UsuarioCreacion) VALUES (93,1,'001-707017',1,now(),'system');
INSERT INTO CUENTA_BANCARIA_EMPLEADO (IdEmpleado,IdBanco,NumeroDeCuenta,Activa,FechaCreacion,UsuarioCreacion) VALUES (94,1,'001-97371',1,now(),'system');
INSERT INTO CUENTA_BANCARIA_EMPLEADO (IdEmpleado,IdBanco,NumeroDeCuenta,Activa,FechaCreacion,UsuarioCreacion) VALUES (95,1,'001-347485',1,now(),'system');
INSERT INTO CUENTA_BANCARIA_EMPLEADO (IdEmpleado,IdBanco,NumeroDeCuenta,Activa,FechaCreacion,UsuarioCreacion) VALUES (96,1,'001-975854',1,now(),'system');
INSERT INTO CUENTA_BANCARIA_EMPLEADO (IdEmpleado,IdBanco,NumeroDeCuenta,Activa,FechaCreacion,UsuarioCreacion) VALUES (97,1,'001-686564',1,now(),'system');
INSERT INTO CUENTA_BANCARIA_EMPLEADO (IdEmpleado,IdBanco,NumeroDeCuenta,Activa,FechaCreacion,UsuarioCreacion) VALUES (98,1,'001-223608',1,now(),'system');
INSERT INTO CUENTA_BANCARIA_EMPLEADO (IdEmpleado,IdBanco,NumeroDeCuenta,Activa,FechaCreacion,UsuarioCreacion) VALUES (99,1,'001-238323',1,now(),'system');
INSERT INTO CUENTA_BANCARIA_EMPLEADO (IdEmpleado,IdBanco,NumeroDeCuenta,Activa,FechaCreacion,UsuarioCreacion) VALUES (100,1,'001-927765',1,now(),'system');

create table INASISTENCIA(
	IdInasistencia int not null auto_increment,
	IdEmpleado int not null,
	FechaInicial datetime not null,
	FechaFinal datetime not null,
	MotivoInasistencia varchar(300),
	FechaProcesado datetime,
	FechaCreacion datetime not null,
	UsuarioCreacion varchar(100) not null,
	FechaModificacion datetime,
	UsuarioModificacion varchar(100),
	primary key (IdInasistencia),
	foreign key (IdEmpleado) references EMPLEADO(IdEmpleado)
);

create table PERIODO_PLANILLA(
	Anio int not null,
	Mes  int not null,
	FechaInicio date,
	FechaFin date,
	FechaCreacion datetime not null,
	UsuarioCreacion varchar(100) not null,
	FechaModificacion datetime,
	UsuarioModificacion varchar(100),
	primary key(Anio,Mes) 
);

insert into PERIODO_PLANILLA(Anio,Mes,FechaInicio, FechaFin, FechaCreacion, UsuarioCreacion) values(2025,9,'2025/9/1','2025/9/30',now(),'system');
insert into PERIODO_PLANILLA(Anio,Mes,FechaInicio, FechaFin, FechaCreacion, UsuarioCreacion) values(2025,10,'2025/10/1','2025/10/31',now(),'system');
insert into PERIODO_PLANILLA(Anio,Mes,FechaInicio, FechaFin, FechaCreacion, UsuarioCreacion) values(2025,11,'2025/11/1','2025/11/30',now(),'system');
insert into PERIODO_PLANILLA(Anio,Mes,FechaInicio, FechaFin, FechaCreacion, UsuarioCreacion) values(2025,12,'2025/12/1','2025/12/31',now(),'system');
insert into PERIODO_PLANILLA(Anio,Mes,FechaInicio, FechaFin, FechaCreacion, UsuarioCreacion) values(2026,1,'2026/1/1','2026/1/31',now(),'system');
insert into PERIODO_PLANILLA(Anio,Mes,FechaInicio, FechaFin, FechaCreacion, UsuarioCreacion) values(2026,2,'2026/2/1','2026/2/28',now(),'system');
insert into PERIODO_PLANILLA(Anio,Mes,FechaInicio, FechaFin, FechaCreacion, UsuarioCreacion) values(2026,3,'2026/3/1','2026/3/31',now(),'system');
insert into PERIODO_PLANILLA(Anio,Mes,FechaInicio, FechaFin, FechaCreacion, UsuarioCreacion) values(2026,4,'2026/4/1','2026/4/30',now(),'system');
insert into PERIODO_PLANILLA(Anio,Mes,FechaInicio, FechaFin, FechaCreacion, UsuarioCreacion) values(2026,5,'2026/5/1','2026/5/31',now(),'system');
insert into PERIODO_PLANILLA(Anio,Mes,FechaInicio, FechaFin, FechaCreacion, UsuarioCreacion) values(2026,6,'2026/6/1','2026/6/30',now(),'system');
insert into PERIODO_PLANILLA(Anio,Mes,FechaInicio, FechaFin, FechaCreacion, UsuarioCreacion) values(2026,7,'2026/7/1','2026/7/31',now(),'system');
insert into PERIODO_PLANILLA(Anio,Mes,FechaInicio, FechaFin, FechaCreacion, UsuarioCreacion) values(2026,8,'2026/8/1','2026/8/31',now(),'system');
insert into PERIODO_PLANILLA(Anio,Mes,FechaInicio, FechaFin, FechaCreacion, UsuarioCreacion) values(2026,9,'2026/9/1','2026/9/30',now(),'system');
insert into PERIODO_PLANILLA(Anio,Mes,FechaInicio, FechaFin, FechaCreacion, UsuarioCreacion) values(2026,10,'2026/10/1','2026/10/31',now(),'system');
insert into PERIODO_PLANILLA(Anio,Mes,FechaInicio, FechaFin, FechaCreacion, UsuarioCreacion) values(2026,11,'2026/11/1','2026/11/30',now(),'system');
insert into PERIODO_PLANILLA(Anio,Mes,FechaInicio, FechaFin, FechaCreacion, UsuarioCreacion) values(2026,12,'2026/12/1','2026/12/31',now(),'system');

create table PLANILLA_CABECERA(
	Anio int not null,
	Mes  int not null,
	TotalIngresos decimal(10,2),
	TotalDescuentos decimal(10,2),
	SalarioNeto decimal(10,2),
	FechaHoraProcesada datetime,
	FechaCreacion datetime not null,
	UsuarioCreacion varchar(100) not null,
	FechaModificacion datetime,
	UsuarioModificacion varchar(100),
	primary key (Anio,Mes),
	foreign key (Anio,Mes) references PERIODO_PLANILLA(Anio,Mes)
);

create table PLANILLA_DETALLE(
	IdPlanillaDetalle int not null auto_increment,
	Anio int not null,
	Mes int not null,
	IdEmpleado int not null,
	FechaContratacion date not null,
	IdPuesto int not null,
	IdStatusEmpleado int not null,
	IngresoSueldoBase decimal(10,2) not null,
	IngresoBonificacionDecreto decimal(10,2) not null,
	IngresoOtrosIngresos decimal(10,2) not null,
	DescuentoIgss decimal(10,2) not null,
	DescuentoIsr Decimal(10,2) not null,
	DescuentoInasistencias decimal(10,2) not null,
	SalarioNeto decimal(10,2),
	FechaCreacion datetime not null,
	UsuarioCreacion varchar(100) not null,
	FechaModificacion datetime,
	UsuarioModificacion varchar(100),
	primary key (IdPlanillaDetalle),
	foreign key (Anio,Mes) references PLANILLA_CABECERA(Anio,Mes),
	foreign key (IdEmpleado) references EMPLEADO(IdEmpleado),
	foreign key (IdPuesto) references PUESTO(IdPuesto),
	foreign key (IdStatusEmpleado) references STATUS_EMPLEADO(IdStatusEmpleado)
);

create table LIQUIDACION(
	IdLiquidacion int not null auto_increment,
	IdEmpleado int not null,
	FechaContratacion date not null,
	FechaEgreso date not null,
	FechaLiquidacion date not null,
	MotivoEgreso varchar(50),
	IdPuesto int not null,
	IngresoSueldoBase decimal(10,2) not null,
	IngresoBonificacionDecreto decimal(10,2) not null,
	IngresoOtrosIngresos decimal(10,2) not null,
	DescuentoIgss decimal(10,2) not null,
	DescuentoIsr Decimal(10,2) not null,
	DescuentoInasistencias decimal(10,2) not null,
	SalarioNeto decimal(10,2),
	TotalIngresos decimal(10,2),
	TotalDescuentos decimal(10,2),
	TotalNeto decimal(10,2),
	FechaCreacion datetime not null,
	UsuarioCreacion varchar(100) not null,
	primary key (IdLiquidacion),
	foreign key (IdEmpleado) references EMPLEADO(IdEmpleado),
	foreign key (IdPuesto) references PUESTO(IdPuesto)
);

INSERT INTO MODULO (
    Nombre, OrdenMenu, FechaCreacion, UsuarioCreacion 
)
VALUES
(
    'Planilla', 2, NOW(), 'system'
);

INSERT INTO MENU (
    IdModulo, Nombre, OrdenMenu, FechaCreacion, UsuarioCreacion 
)
VALUES
(
    2, 'Parametros Generales', 1, NOW(), 'system' 
),
(
    2, 'Gestionar', 2, NOW(), 'system'
),
(
    2, 'Reportes', 3, NOW(), 'system'
),
(
    2, 'Liquidacion', 3, NOW(), 'system'
);

INSERT INTO OPCION (
    IdMenu, Nombre, OrdenMenu, Pagina, FechaCreacion, UsuarioCreacion 
)
VALUES
(
    5, 'Estados Civiles', 1, 'estado_civil.php', NOW(), 'system'
),
(
    5, 'Status Empleado', 2, 'status_empleado.php', NOW(), 'system'
),
(
    5, 'Flujos Status Empleado', 3, 'flujo_status_empleado.php', NOW(), 'system'
),
(
    5, 'Tipos de Documentos', 4, 'tipos_documento.php', NOW(), 'system'
),
(
    5, 'Departamentos', 5, 'departamento.php', NOW(), 'system'
),
(
    5, 'Puestos', 6, 'puesto.php', NOW(), 'system'
),
(
    5, 'Personas', 7, 'personas.php', NOW(), 'system'
),
(
    5, 'Documentos de Personas', 8, 'documento_persona.php', NOW(), 'system'
),
(
    5, 'Bancos', 9, 'banco.php', NOW(), 'system'
),
(
    6, 'Empleados', 1, 'empleado.php', NOW(), 'system'
),
(
    6, 'Cuentas Bancarias Empleados', 2, 'cuenta_bancaria_empleado.php', NOW(), 'system'
),
(
    6, 'Inasistencias de Empleados', 3, 'inasistencia.php', NOW(), 'system'
),
(
    6, 'Calcular Planilla', 4, 'calculo_planilla.php', NOW(), 'system'
),
(
    7, 'Reporte de Planilla', 5, 'reporte_planilla.php', NOW(), 'system'
),
(
    7, 'Boletas de Pago', 6, 'boleta_pago.php', NOW(), 'system'
),
(
    8, 'Liquidacion de Empleado', 1, 'liquidacion.php', NOW(), 'system'
);
