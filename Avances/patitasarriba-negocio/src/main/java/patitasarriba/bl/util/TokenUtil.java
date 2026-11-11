package patitasarriba.bl.util;

import java.security.SecureRandom;

public final class TokenUtil {
    private static final SecureRandom ALEATORIO = new SecureRandom();

    private TokenUtil() {
    }

    // 32 bytes aleatorios en hexadecimal (64 caracteres): sirve de token en un enlace y como clave aleatoria
    public static String generar() {
        byte[] bytes = new byte[32];
        ALEATORIO.nextBytes(bytes);
        StringBuilder texto = new StringBuilder(64);
        for (byte b : bytes) {
            texto.append(String.format("%02x", b));
        }
        return texto.toString();
    }
}
