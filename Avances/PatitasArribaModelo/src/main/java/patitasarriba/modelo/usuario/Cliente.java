package patitasarriba.modelo.usuario;

import patitasarriba.modelo.mascota.Mascota;

import java.util.List;

public class Cliente extends Persona {
    private List<Mascota> mascotas;

    public Cliente(int idPersona, String nombres, String apellidoPaterno, String apellidoMaterno, String telefono, String dni,
                   Cuenta cuenta, List<Mascota> mascotas) {
        super(idPersona, nombres, apellidoPaterno, apellidoMaterno, telefono, dni, cuenta);
        this.mascotas = mascotas;
    }

    // Getters y Setters
    public List<Mascota> getMascotas() {
        return mascotas;
    }

    public void setMascotas(List<Mascota> mascotas) {
        this.mascotas = mascotas;
    }

    public void agregarMascota(Mascota mascota) {
        this.mascotas.add(mascota);
    }

    public void eliminarMascota(Mascota mascota) {
        this.mascotas.remove(mascota);
    }

    @Override
    public String toString() {
        return "Cliente{" +
                "idPersona='" + getIdPersona() + '\'' +
                "nombres='" + getNombres() + '\'' +
                ", apellidoPaterno='" + getApellidoPaterno() + '\'' +
                ", mascotas=" + mascotas +
                '}';
    }
}
