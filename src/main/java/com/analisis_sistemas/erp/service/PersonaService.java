package com.analisis_sistemas.erp.service;

import com.analisis_sistemas.erp.dto.PersonaDTO;
import com.analisis_sistemas.erp.entity.Persona;
import com.analisis_sistemas.erp.repository.EstadoCivilRepository;
import com.analisis_sistemas.erp.repository.GeneroRepository;
import com.analisis_sistemas.erp.repository.PersonaRepository;
import com.analisis_sistemas.erp.utils.SecurityUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.util.List;

@Service
public class PersonaService {

   
    static final int EDAD_MINIMA = 14;
    static final int EDAD_MAXIMA = 100;

    private static final int DIGITOS_TELEFONO_MIN = 8;
    private static final int DIGITOS_TELEFONO_MAX = 15;

    private static final ZoneId ZONA_GUATEMALA = ZoneId.of("America/Guatemala");

    private final PersonaRepository personaRepository;
    private final GeneroRepository generoRepository;
    private final EstadoCivilRepository estadoCivilRepository;

    public PersonaService(PersonaRepository personaRepository,
                          GeneroRepository generoRepository,
                          EstadoCivilRepository estadoCivilRepository) {
        this.personaRepository = personaRepository;
        this.generoRepository = generoRepository;
        this.estadoCivilRepository = estadoCivilRepository;
    }

    public List<PersonaDTO> findAll() {
        return personaRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    public PersonaDTO findById(Integer id) {
        Persona persona = personaRepository.findById(id)
                .orElseThrow(() -> noEncontrada(id));
        return toDTO(persona);
    }

    public PersonaDTO create(PersonaDTO dto) {
        validarReglasDeNegocio(dto);
        validarCatalogos(dto);

        Persona persona = toEntity(dto);
        persona.setIdPersona(null);
        persona.setFechaCreacion(LocalDateTime.now());
        persona.setUsuarioCreacion(SecurityUtils.getUsuarioAutenticado());

        Persona guardada = personaRepository.save(persona);
        return findById(guardada.getIdPersona());
    }

    public PersonaDTO update(Integer id, PersonaDTO dto) {
        Persona existente = personaRepository.findById(id)
                .orElseThrow(() -> noEncontrada(id));

        validarReglasDeNegocio(dto);
        validarCatalogos(dto);

        Persona persona = toEntity(dto);
        persona.setIdPersona(id);
        persona.setFechaCreacion(existente.getFechaCreacion());
        persona.setUsuarioCreacion(existente.getUsuarioCreacion());
        persona.setFechaModificacion(LocalDateTime.now());
        persona.setUsuarioModificacion(SecurityUtils.getUsuarioAutenticado());

        personaRepository.update(persona);
        return findById(id);
    }

  
    public void delete(Integer id) {
        int filasAfectadas = personaRepository.deleteById(id);
        if (filasAfectadas == 0) {
            throw noEncontrada(id);
        }
    }

    // ── Reglas de negocio ──────────────────────────────────────────────

    private void validarReglasDeNegocio(PersonaDTO dto) {
        validarFechaNacimiento(dto.getFechaNacimiento(), LocalDate.now(ZONA_GUATEMALA));
        validarTelefono(dto.getTelefono());
    }

    static void validarFechaNacimiento(LocalDate fechaNacimiento, LocalDate hoy) {
        if (fechaNacimiento == null) {
            throw badRequest("La fecha de nacimiento es obligatoria.");
        }
        if (!fechaNacimiento.isBefore(hoy)) {
            throw badRequest("La fecha de nacimiento no puede ser hoy ni una fecha futura.");
        }
        int edad = Period.between(fechaNacimiento, hoy).getYears();
        if (edad < EDAD_MINIMA) {
            throw badRequest("La persona debe tener al menos " + EDAD_MINIMA + " años (edad calculada: " + edad + ").");
        }
        if (edad > EDAD_MAXIMA) {
            throw badRequest("La fecha de nacimiento no es razonable: supera los " + EDAD_MAXIMA + " años de edad.");
        }
    }

    static void validarTelefono(String telefono) {
        int digitos = telefono == null ? 0 : telefono.replaceAll("[^0-9]", "").length();
        if (digitos < DIGITOS_TELEFONO_MIN || digitos > DIGITOS_TELEFONO_MAX) {
            throw badRequest("El teléfono debe tener entre " + DIGITOS_TELEFONO_MIN + " y "
                    + DIGITOS_TELEFONO_MAX + " dígitos.");
        }
    }

    private void validarCatalogos(PersonaDTO dto) {
        generoRepository.findById(dto.getIdGenero())
                .orElseThrow(() -> badRequest("El género seleccionado no existe."));
        estadoCivilRepository.findById(dto.getIdEstadoCivil())
                .orElseThrow(() -> badRequest("El estado civil seleccionado no existe."));
    }

    // ── Conversión ─────────────────────────────────────────────────────

    private PersonaDTO toDTO(Persona persona) {
        PersonaDTO dto = new PersonaDTO();
        dto.setIdPersona(persona.getIdPersona());
        dto.setNombre(persona.getNombre());
        dto.setApellido(persona.getApellido());
        dto.setFechaNacimiento(persona.getFechaNacimiento());
        dto.setIdGenero(persona.getIdGenero());
        dto.setNombreGenero(persona.getNombreGenero());
        dto.setDireccion(persona.getDireccion());
        dto.setTelefono(persona.getTelefono());
        dto.setCorreoElectronico(persona.getCorreoElectronico());
        dto.setIdEstadoCivil(persona.getIdEstadoCivil());
        dto.setNombreEstadoCivil(persona.getNombreEstadoCivil());
        dto.setNombreCompleto(persona.getNombre() + " " + persona.getApellido());
        return dto;
    }

    private Persona toEntity(PersonaDTO dto) {
        Persona persona = new Persona();
        persona.setIdPersona(dto.getIdPersona());
        persona.setNombre(dto.getNombre().trim());
        persona.setApellido(dto.getApellido().trim());
        persona.setFechaNacimiento(dto.getFechaNacimiento());
        persona.setIdGenero(dto.getIdGenero());
        persona.setDireccion(dto.getDireccion().trim());
        persona.setTelefono(dto.getTelefono().trim());
        String correo = dto.getCorreoElectronico() == null ? "" : dto.getCorreoElectronico().trim();
        persona.setCorreoElectronico(correo.isEmpty() ? null : correo);
        persona.setIdEstadoCivil(dto.getIdEstadoCivil());
        return persona;
    }

    private static ResponseStatusException badRequest(String mensaje) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, mensaje);
    }

    private ResponseStatusException noEncontrada(Integer id) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Persona no encontrada con id: " + id);
    }
}
