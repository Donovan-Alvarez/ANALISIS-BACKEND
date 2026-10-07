# Contrato de rutas — Módulo de Planilla (Fase 2)

Este documento fija, antes de escribir una sola línea de controller o de
componente Angular, el valor exacto de `OPCION.Pagina` para las 16 opciones
nuevas. Ese valor tiene que coincidir, letra por letra, con tres cosas:

1. El `path` de la ruta hija en `erp-frontend/src/app/app.routes.ts`.
2. El primer segmento de la URL del `@RequestMapping` del controller backend.
3. La clave que se registre en `ICONOS_POR_PAGINA` (`shared/layout/sidebar/sidebar.ts`).

> **El primer segmento tras `/api/` del controller DEBE ser igual al slug;
> si no, `PermisoAccionInterceptor` responde 500 en POST/PUT/DELETE.**
> (`resolverSlug()` toma el primer segmento de la URL después de `/api/` y
> busca `OPCION.Pagina = '<ese segmento>'`; si no encuentra fila, lanza
> `ResponseStatusException(INTERNAL_SERVER_ERROR, ...)` — no 403, 500.)

---

## Estilo heredado de Fase 1 (`OPCION.Pagina` ya en producción)

Valores reales, tomados de `database/01-schema-completo.sql` (líneas 1017-1026):

| OPCION.Pagina (Fase 1) |
|---|
| `empresas` |
| `sucursales` |
| `generos` |
| `status-usuario` |
| `roles` |
| `modulos` |
| `menus` |
| `opciones` |
| `usuarios` |
| `asignacion-permisos` |

Reglas de estilo que se leen de esos 10 valores:
- Siempre minúsculas, kebab-case (guión, nunca guión bajo ni camelCase).
- Sin `/api/` ni extensión (nada de `.php`).
- Plural para catálogos/listados simples (`empresas`, `sucursales`, `roles`, `usuarios`, `modulos`, `menus`, `opciones`, `generos`).
- Singular o compuesto cuando la entidad ya es un concepto único o el nombre de dos palabras lo pide (`status-usuario`, `asignacion-permisos`) — no es "una regla", es que el nombre de negocio no tiene plural natural.

Los 16 slugs de abajo siguen exactamente ese mismo criterio.

---

## Las 16 opciones de Planilla

La columna `IdOpcion (real)` tiene los ids **reales** leídos de la BD
local en el Paso A7 (`SELECT` sobre `OPCION`/`MENU`/`MODULO`, 2026-10-07),
no los predichos 11-26 de versiones anteriores de este documento.

> **Nota:** los ids dependen de cada instalación; el sistema funciona por
> `OPCION.Pagina`, nunca por el id numérico. En esta BD, Planilla = módulo 42
> (menús 82 Parámetros Generales, 83 Gestionar, 84 Reportes, 85 Liquidacion).
> Usa los ids solo como referencia para leer la BD de esta máquina; en código
> (controller, rutas Angular, `ICONOS_POR_PAGINA`) se usa siempre el slug.

| IdOpcion (real) | Opción (OPCION.Nombre) | OPCION.Pagina (slug) | Ruta Angular | Base path API | Responsable |
|---|---|---|---|---|---|
| 62 | Estados Civiles | `estados-civiles` | `/estados-civiles` | `/api/estados-civiles` | Donovan |
| 63 | Status Empleado | `status-empleado` | `/status-empleado` | `/api/status-empleado` | Donovan |
| 64 | Flujos Status Empleado | `flujo-status-empleado` | `/flujo-status-empleado` | `/api/flujo-status-empleado` | Javier |
| 65 | Tipos de Documentos | `tipos-documento` | `/tipos-documento` | `/api/tipos-documento` | Donovan |
| 66 | Departamentos | `departamentos` | `/departamentos` | `/api/departamentos` | Donovan |
| 67 | Puestos | `puestos` | `/puestos` | `/api/puestos` | Donovan |
| 68 | Personas | `personas` | `/personas` | `/api/personas` | Andrea |
| 69 | Documentos de Personas | `documentos-persona` | `/documentos-persona` | `/api/documentos-persona` | Andrea |
| 70 | Bancos | `bancos` | `/bancos` | `/api/bancos` | Donovan |
| 71 | Empleados | `empleados` | `/empleados` | `/api/empleados` | Bryan |
| 72 | Cuentas Bancarias Empleados | `cuentas-bancarias` | `/cuentas-bancarias` | `/api/cuentas-bancarias` | Andrés |
| 73 | Inasistencias de Empleados | `inasistencias` | `/inasistencias` | `/api/inasistencias` | Cristian |
| 74 | Calcular Planilla | `calculo-planilla` | `/calculo-planilla` | `/api/calculo-planilla` | Cristian |
| 75 | Reporte de Planilla | `reporte-planilla` | `/reporte-planilla` | `/api/reporte-planilla` | Andrés |
| 76 | Boletas de Pago | `boletas-pago` | `/boletas-pago` | `/api/boletas-pago` | Javier |
| 77 | Liquidación de Empleado | `liquidacion` | `/liquidacion` | `/api/liquidacion` | Bryan |

### Agrupación por menú (como quedan en `04_fase2_rbac.sql`)

- **Parámetros Generales** (9): Estados Civiles, Status Empleado, Flujos Status Empleado, Tipos de Documentos, Departamentos, Puestos, Personas, Documentos de Personas, Bancos.
- **Gestionar** (4): Empleados, Cuentas Bancarias Empleados, Inasistencias de Empleados, Calcular Planilla.
- **Reportes** (2): Reporte de Planilla, Boletas de Pago.
- **Liquidacion** (1): Liquidación de Empleado.

### Resumen por responsable

| Responsable | Opciones (IdOpcion real) | Cantidad | Rango ORA-20xxx |
|---|---|---|---|
| Donovan | 62, 63, 65, 66, 67, 70 | 6 | -20101 .. -20119 (usados -20101..-20111 en `05`) |
| Bryan | 71, 77 | 2 | -20120 .. -20139 |
| Cristian | 73, 74 | 2 | -20140 .. -20159 |
| Andrés | 72, 75 | 2 | -20160 .. -20179 |
| Andrea | 68, 69 | 2 | -20180 .. -20199 |
| Javier | 64, 76 | 2 | -20200 .. -20219 |

Rangos ORA: cada responsable usa **solo** su rango en `RAISE_APPLICATION_ERROR`
(triggers `TRG_<TABLA>_BAJA_VALIDA` u otras reglas de negocio). Fase 1 llega
hasta -20032 y -20100 es la guarda de `03`. `GlobalExceptionHandler` devuelve
409 con el mensaje limpio para todo -20000..-20999. Detalle y convenciones en
`docs/fase2/decisiones-arquitectura.md` (D2, D8).

---

## Checklist para cada responsable al implementar su CRUD

1. Controller: `@RequestMapping("/api/<slug>")` — el `<slug>` debe ser
   exactamente el de la columna "OPCION.Pagina" de la tabla de arriba.
2. Angular: en `app.routes.ts`, la ruta hija debe usar `path: '<slug>'`
   (el mismo slug, sin barra inicial en `routes`, pero con ella al navegar
   — ver columna "Ruta Angular").
3. Agregar la entrada correspondiente en `ICONOS_POR_PAGINA`
   (`shared/layout/sidebar/sidebar.ts`) para que el ícono del sidebar no
   caiga en el genérico `chevron_right`.
4. Si tu endpoint necesita una URL cuyo primer segmento NO sea el slug
   (por ejemplo algo anidado tipo `/api/empleados/{id}/cuentas`), eso
   funciona igual (el slug sigue siendo `empleados`, el interceptor solo
   mira el primer segmento). Pero si tu pantalla consume una URL que
   **no empieza** con su propio slug (como el caso ya existente de
   `asignacion-permisos`), hay que avisar para agregar una entrada a
   `EXCEPCIONES_PATRON` en `PermisoAccionInterceptor.java`.
