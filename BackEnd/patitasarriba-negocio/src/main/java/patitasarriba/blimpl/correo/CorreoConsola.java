package patitasarriba.blimpl.correo;

// Para desarrollo y pruebas: no envia nada, muestra el correo en la consola del servidor
// (ahi se ve el enlace con el token). Es la opcion por defecto cuando no hay SMTP configurado
public class CorreoConsola implements CorreoService {
    @Override
    public void enviar(String destino, String asunto, String cuerpo) {
        System.out.println("---- CORREO (modo consola, no se envio) ----");
        System.out.println("Para: " + destino);
        System.out.println("Asunto: " + asunto);
        System.out.println(cuerpo);
        System.out.println("---------------------------------------------");
    }
}
