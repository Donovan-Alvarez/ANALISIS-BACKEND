package com.analisis_sistemas.erp.utils;

import java.text.Normalizer;
import java.util.Locale;

/**
 * Valida y normaliza el número de un documento de identificación según su tipo
 * (DOCUMENTO_PERSONA.NoDocumento). Sin dependencias de Spring: se puede probar suelto.
 *
 * <ul>
 *   <li><b>DPI</b>: exactamente 13 dígitos (se aceptan espacios/guiones al escribirlo).
 *       Se guarda como "#### ##### ####", igual que el seed de 03.</li>
 *   <li><b>NIT</b>: dígitos + dígito verificador (0-9 o K), con o sin guion.
 *       Se guarda como "1234567-8" / "1234567-K".</li>
 *   <li><b>Cualquier otro tipo</b> (pasaporte, licencia, IGSS...): 4 a 50 caracteres, solo letras,
 *       números, espacios, puntos y guiones.</li>
 * </ul>
 *
 * El tipo se reconoce por el NOMBRE (TIPO_DOCUMENTO no tiene columna de "regla"). Si alguien renombra
 * el catálogo de forma que ya no diga "DPI" / "NIT", ese tipo cae en la regla genérica; no se rompe.
 */
public final class NumeroDocumentoValidador {

    private static final int LONGITUD_MINIMA_GENERICA = 4;
    private static final int LONGITUD_MAXIMA = 50;

    private NumeroDocumentoValidador() {
    }

    /**
     * @return el número normalizado, listo para guardar.
     * @throws IllegalArgumentException con un mensaje claro para el usuario si el formato no es válido.
     */
    public static String validarYNormalizar(String nombreTipoDocumento, String numero) {
        String limpio = numero == null ? "" : numero.trim();
        if (limpio.isEmpty()) {
            throw new IllegalArgumentException("El número de documento es obligatorio.");
        }
        if (limpio.length() > LONGITUD_MAXIMA) {
            throw new IllegalArgumentException(
                    "El número de documento no puede superar los " + LONGITUD_MAXIMA + " caracteres.");
        }

        if (esDpi(nombreTipoDocumento)) {
            return normalizarDpi(limpio);
        }
        if (esNit(nombreTipoDocumento)) {
            return normalizarNit(limpio);
        }
        return validarGenerico(limpio);
    }

    public static boolean esDpi(String nombreTipoDocumento) {
        String nombre = sinTildes(nombreTipoDocumento);
        return nombre.matches(".*\\bdpi\\b.*") || nombre.startsWith("documento personal de identificacion");
    }

    public static boolean esNit(String nombreTipoDocumento) {
        return sinTildes(nombreTipoDocumento).equals("nit");
    }

    private static String normalizarDpi(String numero) {
        if (!numero.matches("[0-9 -]+")) {
            throw new IllegalArgumentException("El DPI solo puede contener números (13 dígitos).");
        }
        String digitos = numero.replaceAll("[ -]", "");
        if (digitos.length() != 13) {
            throw new IllegalArgumentException(
                    "El DPI debe tener exactamente 13 dígitos (ingresaste " + digitos.length() + ").");
        }
        return digitos.substring(0, 4) + " " + digitos.substring(4, 9) + " " + digitos.substring(9);
    }

    private static String normalizarNit(String numero) {
        String compacto = numero.replace(" ", "").toUpperCase(Locale.ROOT);
        if (!compacto.matches("[0-9]{4,12}-?[0-9K]")) {
            throw new IllegalArgumentException(
                    "El NIT debe ser numérico con dígito verificador (ej. 1234567-8 o 1234567-K).");
        }
        String sinGuion = compacto.replace("-", "");
        return sinGuion.substring(0, sinGuion.length() - 1) + "-" + sinGuion.charAt(sinGuion.length() - 1);
    }

    private static String validarGenerico(String numero) {
        if (numero.length() < LONGITUD_MINIMA_GENERICA
                || !numero.matches("[A-Za-z0-9][A-Za-z0-9 ./-]*")) {
            throw new IllegalArgumentException(
                    "El número de documento debe tener entre " + LONGITUD_MINIMA_GENERICA + " y " + LONGITUD_MAXIMA
                            + " caracteres, solo letras, números, espacios, puntos y guiones.");
        }
        return numero;
    }

    private static String sinTildes(String texto) {
        if (texto == null) {
            return "";
        }
        return Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT)
                .trim();
    }
}
