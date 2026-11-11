package patitasarriba.bl.correo;

import patitasarriba.bl.BLException;

// Envio de correos (verificacion de correo, recuperacion y activacion de contrasena).
// La implementacion se elige con Correos.desdeEntorno(): SMTP si hay configuracion, consola si no
public interface CorreoService {
    void enviar(String destino, String asunto, String cuerpo) throws BLException;
}
