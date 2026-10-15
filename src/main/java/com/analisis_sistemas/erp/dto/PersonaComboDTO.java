package com.analisis_sistemas.erp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Persona para el combo de la pantalla de Empleados.
 * <p>
 * PROVISIONAL: la pantalla de Personas (idOpcion 'personas') es de otra
 * integrante y aun no expone endpoint. Se sirve desde
 * GET /api/empleados/personas para no colisionar con el /api/personas que
 * ella cree. Cuando exista, el frontend apunta alla y esto se elimina.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonaComboDTO {

    private Integer idPersona;
    private String nombreCompleto;
}
