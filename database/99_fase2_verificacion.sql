/*
 * FASE 2 - Modulo de Planilla
 * 99_fase2_verificacion.sql
 *
 * SOLO LECTURA. Ningun INSERT/UPDATE/DELETE/DDL. Las 4 secciones son
 * independientes entre si:
 *   SECCION 1: conteos esperados vs reales (correr DESPUES de 01-04).
 *   SECCION 2: FKs huerfanas (correr DESPUES de 01-04).
 *   SECCION 3: caracter U+FFFD / ASCII en correo (correr DESPUES de 03).
 *   SECCION 4 (Paso A3): premisas de GENERO/SUCURSAL/EMPRESA que usan las
 *     subconsultas de 02/03 - esta conviene correrla ANTES de 02/03,
 *     para detectar un problema como el ORA-02291 original sin tener que
 *     llegar a insertar nada.
 *
 * NO EJECUTAR nada mas de este paquete (01-04) sin autorizacion. Este
 * archivo en si mismo es inofensivo (no escribe nada), pero se entrega
 * junto con los demas como borrador hasta que confirmes el orden completo.
 */


-- =====================================================================
-- SECCION 1: conteos esperados vs reales
-- Todas las filas de "Diferencia" deben salir en 0. Cualquier otro valor
-- es una señal de que algo no se ejecuto completo o se ejecuto dos veces.
-- =====================================================================
SELECT 'ESTADO_CIVIL' AS Tabla, COUNT(*) AS Filas, 5 AS Esperado, COUNT(*) - 5 AS Diferencia FROM ESTADO_CIVIL
UNION ALL
SELECT 'STATUS_EMPLEADO', COUNT(*), 5, COUNT(*) - 5 FROM STATUS_EMPLEADO
UNION ALL
SELECT 'FLUJO_STATUS_EMPLEADO', COUNT(*), 9, COUNT(*) - 9 FROM FLUJO_STATUS_EMPLEADO
UNION ALL
SELECT 'TIPO_DOCUMENTO', COUNT(*), 5, COUNT(*) - 5 FROM TIPO_DOCUMENTO
UNION ALL
SELECT 'BANCO', COUNT(*), 4, COUNT(*) - 4 FROM BANCO
UNION ALL
SELECT 'DEPARTAMENTO', COUNT(*), 13, COUNT(*) - 13 FROM DEPARTAMENTO
UNION ALL
SELECT 'PUESTO', COUNT(*), 29, COUNT(*) - 29 FROM PUESTO
UNION ALL
SELECT 'PERIODO_PLANILLA', COUNT(*), 16, COUNT(*) - 16 FROM PERIODO_PLANILLA
UNION ALL
SELECT 'PERSONA', COUNT(*), 100, COUNT(*) - 100 FROM PERSONA
UNION ALL
SELECT 'DOCUMENTO_PERSONA', COUNT(*), 100, COUNT(*) - 100 FROM DOCUMENTO_PERSONA
UNION ALL
SELECT 'EMPLEADO', COUNT(*), 100, COUNT(*) - 100 FROM EMPLEADO
UNION ALL
SELECT 'CUENTA_BANCARIA_EMPLEADO', COUNT(*), 100, COUNT(*) - 100 FROM CUENTA_BANCARIA_EMPLEADO
UNION ALL
SELECT 'INASISTENCIA (sin seed, debe ser 0)', COUNT(*), 0, COUNT(*) - 0 FROM INASISTENCIA
UNION ALL
SELECT 'PLANILLA_CABECERA (sin seed, debe ser 0)', COUNT(*), 0, COUNT(*) - 0 FROM PLANILLA_CABECERA
UNION ALL
SELECT 'PLANILLA_DETALLE (sin seed, debe ser 0)', COUNT(*), 0, COUNT(*) - 0 FROM PLANILLA_DETALLE
UNION ALL
SELECT 'LIQUIDACION (sin seed, debe ser 0)', COUNT(*), 0, COUNT(*) - 0 FROM LIQUIDACION
UNION ALL
SELECT 'MODULO ''Planilla''', COUNT(*), 1, COUNT(*) - 1 FROM MODULO WHERE Nombre = 'Planilla'
UNION ALL
SELECT 'MENU de modulo ''Planilla''', COUNT(*), 4, COUNT(*) - 4
    FROM MENU WHERE IdModulo = (SELECT IdModulo FROM MODULO WHERE Nombre = 'Planilla')
UNION ALL
SELECT 'OPCION de modulo ''Planilla''', COUNT(*), 16, COUNT(*) - 16
    FROM OPCION op
    JOIN MENU me ON me.IdMenu = op.IdMenu
    WHERE me.IdModulo = (SELECT IdModulo FROM MODULO WHERE Nombre = 'Planilla')
UNION ALL
SELECT 'ROLE_OPCION del Administrador sobre las 16 de Planilla', COUNT(*), 16, COUNT(*) - 16
    FROM ROLE_OPCION ro
    JOIN OPCION op ON op.IdOpcion = ro.IdOpcion
    JOIN MENU me ON me.IdMenu = op.IdMenu
    WHERE me.IdModulo = (SELECT IdModulo FROM MODULO WHERE Nombre = 'Planilla')
      AND ro.IdRole = (SELECT IdRole FROM "ROLE" WHERE Nombre = 'Administrador')
ORDER BY 1;


-- =====================================================================
-- SECCION 2: FKs huerfanas
-- Debe devolver CERO FILAS. Cualquier fila que aparezca aqui es una FK
-- rota (un hijo apuntando a un padre que no existe) - en teoria imposible
-- si los 4 scripts corrieron en orden sobre una BD con las FK activas,
-- pero se deja como chequeo independiente de esa suposicion.
--
-- TIPOS (Paso A7): en un UNION ALL Oracle exige que cada columna tenga el
-- mismo tipo en TODAS las ramas (si no: ORA-01790). Por eso IdHuerfano es
-- siempre VARCHAR2: TO_CHAR(id) en claves simples, y las claves
-- compuestas concatenadas con '|' en el orden de su PK.
-- =====================================================================
SELECT 'DEPARTAMENTO.IdEmpresa -> EMPRESA' AS Chequeo, TO_CHAR(d.IdDepartamento) AS IdHuerfano
FROM DEPARTAMENTO d
WHERE d.IdEmpresa IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM EMPRESA e WHERE e.IdEmpresa = d.IdEmpresa)
UNION ALL
SELECT 'FLUJO_STATUS_EMPLEADO.IdStatusActual -> STATUS_EMPLEADO', TO_CHAR(f.IdStatusActual) || '|' || TO_CHAR(f.IdStatusNuevo)
FROM FLUJO_STATUS_EMPLEADO f
WHERE NOT EXISTS (SELECT 1 FROM STATUS_EMPLEADO s WHERE s.IdStatusEmpleado = f.IdStatusActual)
UNION ALL
SELECT 'FLUJO_STATUS_EMPLEADO.IdStatusNuevo -> STATUS_EMPLEADO', TO_CHAR(f.IdStatusActual) || '|' || TO_CHAR(f.IdStatusNuevo)
FROM FLUJO_STATUS_EMPLEADO f
WHERE NOT EXISTS (SELECT 1 FROM STATUS_EMPLEADO s WHERE s.IdStatusEmpleado = f.IdStatusNuevo)
UNION ALL
SELECT 'PUESTO.IdDepartamento -> DEPARTAMENTO', TO_CHAR(p.IdPuesto)
FROM PUESTO p
WHERE NOT EXISTS (SELECT 1 FROM DEPARTAMENTO d WHERE d.IdDepartamento = p.IdDepartamento)
UNION ALL
-- AJUSTE Paso A3: estas dos lineas (IdGenero -> GENERO y, mas abajo,
-- IdSucursal -> SUCURSAL) son la respuesta directa a "que ningun
-- IdGenero/IdSucursal quede huerfano" despues del fix de ORA-02291. Ver
-- tambien la SECCION 4 mas abajo, que valida la PREMISA de la que
-- dependen las subconsultas de 03 (que GENERO tenga exactamente un
-- 'Masculino' y un 'Femenino', y que SUCURSAL tenga al menos una fila)
-- ANTES de insertar, no despues.
SELECT 'PERSONA.IdGenero -> GENERO', TO_CHAR(pe.IdPersona)
FROM PERSONA pe
WHERE NOT EXISTS (SELECT 1 FROM GENERO g WHERE g.IdGenero = pe.IdGenero)
UNION ALL
SELECT 'PERSONA.IdEstadoCivil -> ESTADO_CIVIL', TO_CHAR(pe.IdPersona)
FROM PERSONA pe
WHERE NOT EXISTS (SELECT 1 FROM ESTADO_CIVIL ec WHERE ec.IdEstadoCivil = pe.IdEstadoCivil)
UNION ALL
SELECT 'DOCUMENTO_PERSONA.IdTipoDocumento -> TIPO_DOCUMENTO', TO_CHAR(dp.IdTipoDocumento) || '|' || TO_CHAR(dp.IdPersona)
FROM DOCUMENTO_PERSONA dp
WHERE NOT EXISTS (SELECT 1 FROM TIPO_DOCUMENTO td WHERE td.IdTipoDocumento = dp.IdTipoDocumento)
UNION ALL
SELECT 'DOCUMENTO_PERSONA.IdPersona -> PERSONA', TO_CHAR(dp.IdTipoDocumento) || '|' || TO_CHAR(dp.IdPersona)
FROM DOCUMENTO_PERSONA dp
WHERE NOT EXISTS (SELECT 1 FROM PERSONA pe WHERE pe.IdPersona = dp.IdPersona)
UNION ALL
SELECT 'EMPLEADO.IdPersona -> PERSONA', TO_CHAR(em.IdEmpleado)
FROM EMPLEADO em
WHERE NOT EXISTS (SELECT 1 FROM PERSONA pe WHERE pe.IdPersona = em.IdPersona)
UNION ALL
SELECT 'EMPLEADO.IdSucursal -> SUCURSAL', TO_CHAR(em.IdEmpleado)
FROM EMPLEADO em
WHERE NOT EXISTS (SELECT 1 FROM SUCURSAL su WHERE su.IdSucursal = em.IdSucursal)
UNION ALL
SELECT 'EMPLEADO.IdPuesto -> PUESTO', TO_CHAR(em.IdEmpleado)
FROM EMPLEADO em
WHERE NOT EXISTS (SELECT 1 FROM PUESTO p WHERE p.IdPuesto = em.IdPuesto)
UNION ALL
SELECT 'EMPLEADO.IdStatusEmpleado -> STATUS_EMPLEADO', TO_CHAR(em.IdEmpleado)
FROM EMPLEADO em
WHERE NOT EXISTS (SELECT 1 FROM STATUS_EMPLEADO s WHERE s.IdStatusEmpleado = em.IdStatusEmpleado)
UNION ALL
SELECT 'CUENTA_BANCARIA_EMPLEADO.IdBanco -> BANCO', TO_CHAR(cb.IdCuentaBancaria)
FROM CUENTA_BANCARIA_EMPLEADO cb
WHERE NOT EXISTS (SELECT 1 FROM BANCO b WHERE b.IdBanco = cb.IdBanco)
UNION ALL
SELECT 'CUENTA_BANCARIA_EMPLEADO.IdEmpleado -> EMPLEADO', TO_CHAR(cb.IdCuentaBancaria)
FROM CUENTA_BANCARIA_EMPLEADO cb
WHERE NOT EXISTS (SELECT 1 FROM EMPLEADO em WHERE em.IdEmpleado = cb.IdEmpleado)
UNION ALL
SELECT 'INASISTENCIA.IdEmpleado -> EMPLEADO', TO_CHAR(i.IdInasistencia)
FROM INASISTENCIA i
WHERE NOT EXISTS (SELECT 1 FROM EMPLEADO em WHERE em.IdEmpleado = i.IdEmpleado)
UNION ALL
SELECT 'PLANILLA_CABECERA (Anio,Mes) -> PERIODO_PLANILLA', TO_CHAR(pc.Anio) || '|' || TO_CHAR(pc.Mes)
FROM PLANILLA_CABECERA pc
WHERE NOT EXISTS (SELECT 1 FROM PERIODO_PLANILLA pp WHERE pp.Anio = pc.Anio AND pp.Mes = pc.Mes)
UNION ALL
SELECT 'PLANILLA_DETALLE.IdEmpleado -> EMPLEADO', TO_CHAR(pd.IdPlanillaDetalle)
FROM PLANILLA_DETALLE pd
WHERE NOT EXISTS (SELECT 1 FROM EMPLEADO em WHERE em.IdEmpleado = pd.IdEmpleado)
UNION ALL
SELECT 'PLANILLA_DETALLE (Anio,Mes) -> PLANILLA_CABECERA', TO_CHAR(pd.IdPlanillaDetalle)
FROM PLANILLA_DETALLE pd
WHERE NOT EXISTS (SELECT 1 FROM PLANILLA_CABECERA pc WHERE pc.Anio = pd.Anio AND pc.Mes = pd.Mes)
UNION ALL
SELECT 'LIQUIDACION.IdEmpleado -> EMPLEADO', TO_CHAR(l.IdLiquidacion)
FROM LIQUIDACION l
WHERE NOT EXISTS (SELECT 1 FROM EMPLEADO em WHERE em.IdEmpleado = l.IdEmpleado)
UNION ALL
SELECT 'LIQUIDACION.IdPuesto -> PUESTO', TO_CHAR(l.IdLiquidacion)
FROM LIQUIDACION l
WHERE NOT EXISTS (SELECT 1 FROM PUESTO p WHERE p.IdPuesto = l.IdPuesto);


-- =====================================================================
-- SECCION 3: ningun texto debe contener el caracter de reemplazo Unicode
-- U+FFFD (el "�" que corrompio el archivo original). Debe devolver CERO
-- FILAS. Si aparece algo aqui, el proposito propuesto de P1
-- (reconstruccion de encoding) quedo incompleto en algun lado.
--
-- UNISTR('\FFFD') es la forma correcta/portable de escribir el codepoint
-- Unicode U+FFFD en Oracle. CHR(15712189) es el equivalente usando el
-- truco de pasarle a CHR() el numero cuyos bytes, en base 256, son
-- EF BF BD (la secuencia UTF-8 de 3 bytes de U+FFFD: 239,191,189 ->
-- 239*65536 + 191*256 + 189 = 15712189) - funciona solo si el
-- characterset de la BD es AL32UTF8/UTF8; se deja como alternativa por si
-- UNISTR no estuviera disponible en algun cliente.
--
-- TIPOS (Paso A7): misma regla que la SECCION 2 - Id es VARCHAR2 en todas
-- las ramas (TO_CHAR, y claves compuestas unidas con '|'); Valor ya es
-- VARCHAR2 en todas (columnas de texto VARCHAR2 verificadas).
-- =====================================================================
SELECT 'ESTADO_CIVIL.Nombre' AS Columna, TO_CHAR(IdEstadoCivil) AS Id, Nombre AS Valor
FROM ESTADO_CIVIL WHERE INSTR(Nombre, UNISTR('\FFFD')) > 0
UNION ALL
SELECT 'STATUS_EMPLEADO.Nombre', TO_CHAR(IdStatusEmpleado), Nombre
FROM STATUS_EMPLEADO WHERE INSTR(Nombre, UNISTR('\FFFD')) > 0
UNION ALL
SELECT 'FLUJO_STATUS_EMPLEADO.NombreEvento', TO_CHAR(IdStatusActual) || '|' || TO_CHAR(IdStatusNuevo), NombreEvento
FROM FLUJO_STATUS_EMPLEADO WHERE INSTR(NombreEvento, UNISTR('\FFFD')) > 0
UNION ALL
SELECT 'TIPO_DOCUMENTO.Nombre', TO_CHAR(IdTipoDocumento), Nombre
FROM TIPO_DOCUMENTO WHERE INSTR(Nombre, UNISTR('\FFFD')) > 0
UNION ALL
SELECT 'BANCO.Nombre', TO_CHAR(IdBanco), Nombre
FROM BANCO WHERE INSTR(Nombre, UNISTR('\FFFD')) > 0
UNION ALL
SELECT 'DEPARTAMENTO.Nombre', TO_CHAR(IdDepartamento), Nombre
FROM DEPARTAMENTO WHERE INSTR(Nombre, UNISTR('\FFFD')) > 0
UNION ALL
SELECT 'PUESTO.Nombre', TO_CHAR(IdPuesto), Nombre
FROM PUESTO WHERE INSTR(Nombre, UNISTR('\FFFD')) > 0
UNION ALL
SELECT 'PERSONA.Nombre', TO_CHAR(IdPersona), Nombre
FROM PERSONA WHERE INSTR(Nombre, UNISTR('\FFFD')) > 0
UNION ALL
SELECT 'PERSONA.Apellido', TO_CHAR(IdPersona), Apellido
FROM PERSONA WHERE INSTR(Apellido, UNISTR('\FFFD')) > 0
UNION ALL
SELECT 'PERSONA.Direccion', TO_CHAR(IdPersona), Direccion
FROM PERSONA WHERE INSTR(Direccion, UNISTR('\FFFD')) > 0
UNION ALL
SELECT 'PERSONA.CorreoElectronico', TO_CHAR(IdPersona), CorreoElectronico
FROM PERSONA WHERE CorreoElectronico IS NOT NULL AND INSTR(CorreoElectronico, UNISTR('\FFFD')) > 0
UNION ALL
SELECT 'DOCUMENTO_PERSONA.NoDocumento', TO_CHAR(IdTipoDocumento) || '|' || TO_CHAR(IdPersona), NoDocumento
FROM DOCUMENTO_PERSONA WHERE NoDocumento IS NOT NULL AND INSTR(NoDocumento, UNISTR('\FFFD')) > 0
UNION ALL
SELECT 'CUENTA_BANCARIA_EMPLEADO.NumeroDeCuenta', TO_CHAR(IdCuentaBancaria), NumeroDeCuenta
FROM CUENTA_BANCARIA_EMPLEADO WHERE INSTR(NumeroDeCuenta, UNISTR('\FFFD')) > 0;

-- Chequeo adicional, solo CorreoElectronico: confirma que ES ASCII puro
-- (requisito del Paso A2 - Validators.email de Angular). Debe devolver
-- CERO FILAS; si aparece algo aqui, algun correo quedo con un caracter
-- fuera del rango ASCII (no solo U+FFFD, cualquier no-ASCII).
SELECT IdPersona, CorreoElectronico
FROM PERSONA
WHERE CorreoElectronico IS NOT NULL
  AND LENGTH(CorreoElectronico) != LENGTHB(CorreoElectronico);


-- =====================================================================
-- SECCION 4 (Paso A3): premisas de las subconsultas de GENERO/SUCURSAL/
-- EMPRESA que usan 02_fase2_seed_catalogos.sql y
-- 03_fase2_seed_personas_empleados.sql.
--
-- Correrla IDEALMENTE ANTES de 02/03 (no solo despues): si alguna fila
-- de aqui no sale como se espera, 02/03 van a fallar con ORA-01427
-- (subquery devuelve mas de una fila) o van a insertar NULL en una
-- columna NOT NULL (ORA-01400) en vez del ORA-02291 original - mejor
-- enterarse antes de correrlos.
-- =====================================================================
SELECT 'GENERO - filas llamadas Masculino (debe ser 1)' AS Chequeo,
       COUNT(*) AS Filas
FROM GENERO WHERE Nombre = 'Masculino'
UNION ALL
SELECT 'GENERO - filas llamadas Femenino (debe ser 1)', COUNT(*)
FROM GENERO WHERE Nombre = 'Femenino'
UNION ALL
SELECT 'SUCURSAL - filas totales (debe ser >= 1)', COUNT(*)
FROM SUCURSAL
UNION ALL
SELECT 'EMPRESA - filas totales (debe ser >= 1)', COUNT(*)
FROM EMPRESA;

-- Ids que van a resolver las subconsultas de 02/03 en esta BD (solo
-- informativo - pegalo en el informe para que quede registrado cual fue
-- el id real, ya que el borrador ya no lo asume):
SELECT 'IdGenero para Masculino' AS Dato, TO_CHAR(IdGenero) AS Valor FROM GENERO WHERE Nombre = 'Masculino'
UNION ALL
SELECT 'IdGenero para Femenino', TO_CHAR(IdGenero) FROM GENERO WHERE Nombre = 'Femenino'
UNION ALL
SELECT 'MIN(IdSucursal)', TO_CHAR(MIN(IdSucursal)) FROM SUCURSAL
UNION ALL
SELECT 'MIN(IdEmpresa)', TO_CHAR(MIN(IdEmpresa)) FROM EMPRESA;

-- Despues de correr 03: distribucion real de IdGenero en PERSONA, para
-- cruzar contra lo esperado (55 Masculino / 45 Femenino, segun el
-- archivo original - ver informe del Paso A3).
SELECT g.Nombre AS Genero, COUNT(*) AS CantidadPersonas
FROM PERSONA pe
JOIN GENERO g ON g.IdGenero = pe.IdGenero
GROUP BY g.Nombre
ORDER BY g.Nombre;
