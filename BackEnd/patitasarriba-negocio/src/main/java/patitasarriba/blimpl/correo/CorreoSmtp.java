package patitasarriba.blimpl.correo;

import patitasarriba.bl.excepciones.BLException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.util.Base64;

// Envio por SMTP con TLS implicito (puerto 465, por ejemplo Gmail con una "contrasena de aplicacion").
// Usa solo el JDK, sin librerias externas. Las credenciales llegan por variables de entorno (ver Correos),
// nunca van en el codigo ni en el repositorio.
// Dentro de GlassFish el truststore del dominio (config/cacerts.p12) trae casi ninguna raiz y rechaza el certificado de
// Gmail (PKIX path building failed), por eso la conexion usa los certificados del JDK (ver fabricaConfianza).
// Mientras no haya configuracion se usa CorreoConsola.
public class CorreoSmtp implements CorreoService {
    private static final int TIEMPO_MAXIMO_MS = 15_000;

    private final String host;
    private final int puerto;
    private final String usuario;
    private final String clave;
    private final String remitente;

    public CorreoSmtp(String host, int puerto, String usuario, String clave, String remitente) {
        this.host = host;
        this.puerto = puerto;
        this.usuario = usuario;
        this.clave = clave;
        this.remitente = remitente;
    }

    @Override
    public void enviar(String destino, String asunto, String cuerpo) throws BLException {
        // Evita inyectar cabeceras o comandos SMTP con saltos de linea
        if (destino == null || destino.matches(".*[\\r\\n<>].*")) {
            throw new BLException("El correo de destino no es válido");
        }
        String asuntoLimpio = asunto == null ? "" : asunto.replaceAll("[\\r\\n]", " ");

        try (SSLSocket socket = (SSLSocket) fabricaConfianza().createSocket(host, puerto)) {
            socket.setSoTimeout(TIEMPO_MAXIMO_MS);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));

            leer(in, "220");
            comando(out, in, "EHLO patitasarriba", "250");
            comando(out, in, "AUTH LOGIN", "334");
            comando(out, in, base64(usuario), "334");
            comando(out, in, base64(clave), "235");
            comando(out, in, "MAIL FROM:<" + remitente + ">", "250");
            comando(out, in, "RCPT TO:<" + destino + ">", "250");
            comando(out, in, "DATA", "354");

            StringBuilder mensaje = new StringBuilder();
            mensaje.append("From: Patitas Arriba <").append(remitente).append(">\r\n");
            mensaje.append("To: <").append(destino).append(">\r\n");
            mensaje.append("Subject: =?UTF-8?B?").append(base64(asuntoLimpio)).append("?=\r\n");
            mensaje.append("MIME-Version: 1.0\r\n");
            mensaje.append("Content-Type: text/plain; charset=UTF-8\r\n");
            mensaje.append("Content-Transfer-Encoding: base64\r\n\r\n");
            mensaje.append(Base64.getMimeEncoder(76, "\r\n".getBytes(StandardCharsets.US_ASCII))
                    .encodeToString(cuerpo.getBytes(StandardCharsets.UTF_8)));
            mensaje.append("\r\n.\r\n");
            out.write(mensaje.toString());
            out.flush();
            leer(in, "250");

            comando(out, in, "QUIT", "221");
        } catch (IOException e) {
            throw new BLException("No se pudo enviar el correo", e);
        }
    }

    // Fabrica de sockets que confia en los certificados del JDK (lib/security/cacerts). Si no se pueden leer,
    // usa la configuracion por defecto de la JVM.
    static SSLSocketFactory fabricaConfianza() {
        try {
            File cacerts = new File(System.getProperty("java.home"), "lib/security/cacerts");
            KeyStore raices = KeyStore.getInstance(cacerts, "changeit".toCharArray());
            TrustManagerFactory confianza = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            confianza.init(raices);
            SSLContext contexto = SSLContext.getInstance("TLS");
            contexto.init(null, confianza.getTrustManagers(), null);
            return contexto.getSocketFactory();
        } catch (Exception e) {
            return (SSLSocketFactory) SSLSocketFactory.getDefault();
        }
    }

    private static String base64(String texto) {
        return Base64.getEncoder().encodeToString(texto.getBytes(StandardCharsets.UTF_8));
    }

    private static void comando(BufferedWriter out, BufferedReader in, String comando, String esperado) throws IOException, BLException {
        out.write(comando + "\r\n");
        out.flush();
        leer(in, esperado);
    }

    // Una respuesta SMTP puede tener varias lineas ("250-..." hasta "250 ...")
    private static void leer(BufferedReader in, String esperado) throws IOException, BLException {
        String linea;
        do {
            linea = in.readLine();
            if (linea == null) {
                throw new BLException("El servidor de correo cerró la conexión");
            }
        } while (linea.length() > 3 && linea.charAt(3) == '-');
        if (!linea.startsWith(esperado)) {
            throw new BLException("El servidor de correo rechazó la operación (" + linea.substring(0, Math.min(3, linea.length())) + ")");
        }
    }
}
