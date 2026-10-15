# Boleta de demostración local

Datos sintéticos autorizados para probar exclusivamente la consulta y exportación de Boletas.
No valida el cálculo de planilla de Cristian y no debe cargarse como información real.

Desde el menú: Planilla → Reportes → Boletas de Pago.
Recargar la pantalla si ya estaba abierta. Seleccionar **2099 / 12** y el empleado
**Ernesto Arturo Quesada Pineda** (IdEmpleado 1 en esta instalación).
En otras instalaciones el script elige el primer empleado con cuenta activa, o el primero disponible.

| Concepto | Importe esperado |
|---|---:|
| Sueldo base | Q5,000.00 |
| Bonificación decreto | Q250.00 |
| Otros ingresos | Q150.00 |
| IGSS | Q241.50 |
| ISR | Q100.00 |
| Inasistencias | Q50.00 |
| Neto almacenado | **Q5,008.50** |

Ingresos de cabecera: Q5,400.00. Descuentos: Q391.50.
Las cuentas mostradas son las cuentas activas ya existentes del empleado; el script no las modifica.
Probar Imprimir, Exportar PDF y Exportar Excel, abrir los archivos y comparar estos valores.

Desde la raíz del backend:

```powershell
# Crear (ya ejecutado en esta instalación):
.\database\run-sql.ps1 database/qa/01_boleta_demo.sql

# Retirar cuando termine la prueba (todavía no ejecutado):
.\database\run-sql.ps1 database/qa/02_retirar_boleta_demo.sql
```

Los scripts usan la marca `qa_javier_boleta` y el período 2099/12. La creación no modifica
un período existente. La limpieza se detiene si detecta filas de otro origen o modificaciones ajenas.
La limpieza conserva empleados, cuentas y datos iniciales.

Mientras exista la demo, la verificación 99 tendrá una fila adicional en período, cabecera y detalle
respecto al seed. Retirar la demo antes de comparar otra vez contra los conteos iniciales.
