# Decisiones de arquitectura — Fase 2 (Planilla)

Para los 6 devs del módulo. Una sección por decisión: **qué**, **por qué** y **cómo aplicarlo**. Si algo de tu CRUD no encaja, avisa antes de inventar otro patrón.

---

## D1 — Auditoría solo desde Java, sin triggers de auditoría

**Qué:** `FechaCreacion`/`UsuarioCreacion` y `FechaModificacion`/`UsuarioModificacion` los llena el Service, nunca un trigger.

**Cómo:**
```java
entidad.setFechaCreacion(LocalDateTime.now());
entidad.setUsuarioCreacion(SecurityUtils.getUsuarioAutenticado());
// en update: conservar FechaCreacion/UsuarioCreacion del registro existente
entidad.setFechaModificacion(LocalDateTime.now());
entidad.setUsuarioModificacion(SecurityUtils.getUsuarioAutenticado());
```
- En Java usa `LocalDateTime` (no `LocalDate`). Lectura en el RowMapper: `rs.getTimestamp("FECHACREACION")` → `.toLocalDateTime()`. Escritura: `ps.setTimestamp(n, Timestamp.valueOf(...))`.
- Las 16 tablas de Planilla tienen `FechaCreacion`/`FechaModificacion` como **`DATE`** (Fase 1 usa `TIMESTAMP(6)`). Con el patrón de arriba funciona igual en ambas; en `DATE` solo se pierden las fracciones de segundo.
- `SecurityUtils` devuelve `'system'` si no hay usuario autenticado; es el mismo valor que usan los seeds.

**Por qué:** es el patrón de Fase 1 (`GeneroService`, `SucursalService`…) y deja el usuario real, que un trigger no conoce. Ojo: en la BD real sigue existiendo `TRG_ROLE_OPCION_AUDIT` (Fase 1), que pisa `FechaCreacion` con `SYSDATE` en `ROLE_OPCION`; no es de Planilla y **se deja tal cual** (decisión 2026-10-07). Por eso los `ROLE_OPCION` muestran la hora del servidor de BD (UTC, sin fracciones) y no la de Java.

---

## D2 — Bajas con registros relacionados: trigger BEFORE DELETE + RAISE_APPLICATION_ERROR

**Qué:** si un catálogo tiene hijos, el `DELETE` lo bloquea un trigger `TRG_<TABLA>_BAJA_VALIDA` con un mensaje para el usuario, p. ej. *"No se puede eliminar el banco: tiene 3 cuenta(s) bancaria(s) de empleados asociada(s)."*

**Cómo:**
- Ya creados en `database/05_fase2_endurecimiento.sql`: ESTADO_CIVIL, STATUS_EMPLEADO, TIPO_DOCUMENTO, BANCO, DEPARTAMENTO, PUESTO.
- Si tu tabla necesita uno nuevo, usa **tu rango** de códigos (D8) y el mismo patrón (`SELECT COUNT(*)` en cada tabla hija, mensaje que diga cuántos).
- En Java **no** hay que capturar nada: `GlobalExceptionHandler` devuelve **409** con el mensaje del trigger limpio (sin `ORA-xxxxx`, sin traza).
- Si se vuelve a correr `01_fase2_ddl.sql`, hay que volver a correr `05` (el `DROP ... CASCADE` borra los triggers).

**Por qué:** la FK sola devuelve ORA-02292 → mensaje genérico. El trigger da un mensaje útil y lo mantiene en un solo lugar.

**Limitación conocida (Fase 1, se deja documentada — decisión 2026-10-07):** en la BD real **no existen** los triggers de baja de Fase 1 (`TRG_EMPRESA/SUCURSAL/ROLE/MODULO/MENU/OPCION_BAJA_VALIDA`) ni casi ninguno de auditoría, aunque `database/01-schema-completo.sql` los define. En esas tablas de Fase 1, borrar un registro con hijos cae en ORA-02292 y el usuario ve el 409 genérico *"No se pudo completar la operación porque el registro tiene datos relacionados."*. No se recrean; no afecta a Planilla.

---

## D3 — Duplicados: validar en el Service + UNIQUE en BD + mapeo en el handler

**Qué:** tres capas, cada una con su función:
1. **Service:** antes de insertar/actualizar, busca si ya existe (p. ej. `existsByNombre`, excluyendo el propio id en update) y lanza `ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un banco con ese nombre.")`. Es el mensaje bonito y específico.
2. **BD:** constraint `UNIQUE` (ya existen `UQ_*` en ESTADO_CIVIL, STATUS_EMPLEADO, TIPO_DOCUMENTO, BANCO, DEPARTAMENTO, PUESTO, y `UQ_OPCION_PAGINA`). Es la garantía real ante dos peticiones simultáneas.
3. **Handler:** si la BD rechaza igual (ORA-00001), `GlobalExceptionHandler` responde **409 "Ya existe un registro con los mismos datos."**.

Otros mapeos del handler (Paso B): ORA-02291 → 400 *"El registro relacionado seleccionado no existe."*, ORA-12899 → 400 *"Un valor excede la longitud permitida."*, ORA-01400 → 400 *"Falta un dato obligatorio."*. Igual valida en el DTO (`@NotBlank`, `@Size`) para que esos casos casi nunca lleguen a la BD.

---

## D4 — OPCION.Pagina es único

**Qué:** `UQ_OPCION_PAGINA` en la BD (creada por `05`). Cada slug existe una sola vez.

**Por qué:** `PermisoAccionInterceptor` resuelve el permiso con `SELECT IdOpcion FROM OPCION WHERE Pagina = ?` + `findFirst()`. Con dos filas iguales (pasó con `'generos'`, Paso A7) el permiso podía validarse contra la opción equivocada.

**Cómo:** tu slug es el de `contrato-rutas.md`; nunca dependas del `IdOpcion` numérico (cambia en cada instalación).

---

## D5 — PDF/Excel se generan en el frontend con un servicio compartido

**Qué:** exportar e imprimir se hace en Angular con **un único servicio compartido** (`core/services/exportacion.service.ts`) que usan todas las pantallas; el backend solo entrega los datos (JSON).

**Librerías aprobadas (2026-10-07):**

| Formato | Paquete | Versión de referencia | Licencia |
|---|---|---|---|
| PDF | `jspdf` + `jspdf-autotable` | 4.2.x / 5.0.x | MIT |
| Excel (.xlsx) | `exceljs` | 4.4.x | MIT |

```bash
# en erp-frontend/
npm install jspdf jspdf-autotable exceljs
```
- **Por qué estas:** jsPDF es el estándar para PDF en el navegador y `jspdf-autotable` arma tablas con encabezado repetido, paginación y totales (lo que piden los reportes de planilla y las boletas). ExcelJS genera `.xlsx` reales con estilos, anchos de columna y formato numérico/moneda.
- **NO usar `xlsx` (SheetJS) de npm:** el paquete de npm quedó congelado en 0.18.5, con vulnerabilidades publicadas; las versiones corregidas solo se distribuyen fuera de npm.
- **No hace falta `file-saver`:** descargar con `Blob` + `URL.createObjectURL` + un `<a download>` temporal.
- **Carga diferida:** importar las librerías con `await import('jspdf')` / `await import('exceljs')` dentro del servicio, para que no pesen en el bundle inicial (solo se descargan al primer clic en Exportar/Imprimir).

**Cómo (contrato del servicio, a implementar una vez):**
```ts
interface ColumnaExport { encabezado: string; campo: string; formato?: 'texto' | 'numero' | 'moneda' | 'fecha'; }
exportarPdf(titulo: string, columnas: ColumnaExport[], filas: object[]): Promise<void>;
exportarExcel(nombreHoja: string, columnas: ColumnaExport[], filas: object[]): Promise<void>;
```
- El botón de exportar/imprimir solo se muestra si `PermisosService.permisosDe('<slug>')` da `exportar`/`imprimir` en `true`.
- Nadie mete otra librería de PDF/Excel por su cuenta; si el servicio no cubre un caso (p. ej. el formato de boleta), se extiende el servicio.

**Estado:** el servicio y las dependencias **todavía no están en `erp-frontend`**.

---

## D6 — GET sin proteger por permiso: limitación conocida de Fase 1

**Qué:** `PermisoAccionInterceptor` solo revisa **POST** (alta), **PUT** (cambio) y **DELETE** (baja). Los **GET** solo exigen JWT válido; no se valida que el rol tenga la opción.

**Consecuencia:** un usuario autenticado puede leer por API un catálogo que no ve en su menú. Se acepta para Fase 2 (igual que en Fase 1) y queda documentado; el frontend oculta lo que el rol no tiene.

**Cómo:** no armes lógica que dependa de que el GET esté protegido. Si una pantalla necesita ocultar datos sensibles, levántalo como pendiente.

---

## D7 — Plantilla frontend "Gen 2" + PermisosService + ConfirmDialog compartido

**Qué (confirmado 2026-10-07):** las pantallas nuevas copian el patrón de las pantallas de Fase 1 que ya usan `PermisosService` (`modulos`, `menus`, `opciones`, `usuarios`), no el de las primeras (`generos`, `roles`, `status-usuario`, que usan `confirm()` nativo y no consultan permisos).

**Cómo:**
- `PermisosService.permisosDe('<slug>')` para mostrar/ocultar Nuevo / Editar / Eliminar / Imprimir / Exportar.
- Confirmación de borrado con un **ConfirmDialog compartido** en `shared/` (no `window.confirm`). **Todavía no existe**: se crea una vez y lo usan todos.
- Mensajes de error: mostrar `error.message` del backend (ya viene limpio, ver D2/D3) en el toast existente (`shared/toast`).
- Ruta, slug e ícono según `contrato-rutas.md`.

---

## D8 — Responsables y rangos ORA-20xxx por persona

Cada dev usa **solo** su rango en `RAISE_APPLICATION_ERROR`, para no chocar. Fase 1 usa hasta -20032; -20100 es la guarda de `03`.

| Responsable | Opciones (slug) | Rango ORA | Usados |
|---|---|---|---|
| Donovan | estados-civiles, status-empleado, tipos-documento, departamentos, puestos, bancos | -20101 .. -20119 | -20101 a -20111 (`05`) |
| Bryan | empleados, liquidacion | -20120 .. -20139 | — |
| Cristian | inasistencias, calculo-planilla | -20140 .. -20159 | — |
| Andrés | cuentas-bancarias, reporte-planilla | -20160 .. -20179 | — |
| Andrea | personas, documentos-persona | -20180 .. -20199 | — |
| Javier | flujo-status-empleado, boletas-pago | -20200 .. -20219 | — |

Al usar un código nuevo, anótalo aquí y en el comentario de cabecera del script.
