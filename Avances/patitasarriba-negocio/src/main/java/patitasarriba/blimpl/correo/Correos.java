package patitasarriba.blimpl.correo;

// Elige como se envian los correos segun las variables de entorno (nada de credenciales en el repositorio):
//   PATITAS_SMTP_HOST, PATITAS_SMTP_PUERTO (por defecto 465), PATITAS_SMTP_USUARIO,
//   PATITAS_SMTP_CLAVE, PATITAS_SMTP_REMITENTE (por defecto, el usuario)
// Si falta host, usuario o clave, se usa CorreoConsola.
public final class Correos {
    private Correos() {
    }

    public static CorreoService desdeEntorno() {
        String host = System.getenv("PATITAS_SMTP_HOST");
        String usuario = System.getenv("PATITAS_SMTP_USUARIO");
        String clave = System.getenv("PATITAS_SMTP_CLAVE");
        if (vacio(host) || vacio(usuario) || vacio(clave)) {
            return new CorreoConsola();
        }
        int puerto = 465;
        String puertoTexto = System.getenv("PATITAS_SMTP_PUERTO");
        if (!vacio(puertoTexto)) {
            try {
                puerto = Integer.parseInt(puertoTexto.trim());
            } catch (NumberFormatException e) {
                throw new IllegalStateException("PATITAS_SMTP_PUERTO debe ser un numero");
            }
        }
        String remitente = System.getenv("PATITAS_SMTP_REMITENTE");
        return new CorreoSmtp(host.trim(), puerto, usuario.trim(), clave, vacio(remitente) ? usuario.trim() : remitente.trim());
    }

    private static boolean vacio(String texto) {
        return texto == null || texto.isBlank();
    }
}
