package patitasarriba.modelo.usuario;

import patitasarriba.modelo.mascota.Mascota;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Cliente extends Persona {
    private List<Mascota> mascotas;

    public Cliente(int idPersona, String nombres, String apellidoPaterno, String apellidoMaterno, String telefono, String dni,
                   Cuenta cuenta, List<Mascota> mascotas) {
        super(idPersona, nombres, apellidoPaterno, apellidoMaterno, telefono, dni, cuenta);
        setMascotas(mascotas);
    }

    // Constructor de copia
    public Cliente(Cliente cliente) {
        super(cliente);
        setMascotas(cliente.getMascotas());
    }

    // Getters y Setters
    public List<Mascota> getMascotas() {
        return Collections.unmodifiableList(mascotas);
    }

    public void setMascotas(List<Mascota> mascotas) {
        if (mascotas == null) {
            throw new IllegalArgumentException("mascotas no puede ser nulo");
        }
        this.mascotas = new ArrayList<>(mascotas);
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
