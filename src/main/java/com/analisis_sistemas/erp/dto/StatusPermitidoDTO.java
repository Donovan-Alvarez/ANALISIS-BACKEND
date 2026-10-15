package com.analisis_sistemas.erp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Un status al que un empleado concreto puede moverse ahora mismo, segun
 * FLUJO_STATUS_EMPLEADO. Alimenta el combo de status de la pantalla: en vez
 * de ofrecer los cinco y rechazar despues, se ofrecen solo los validos.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatusPermitidoDTO {

    private Integer idStatusEmpleado;
    private String nombre;
    /** Nombre del evento del flujo ("Renuncia del Empleado"), o "Sin cambio". */
    private String nombreEvento;
}
