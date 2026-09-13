package model.usuario;

import model.mascota.Mascota;

import java.util.ArrayList;
import java.util.List;

public class Cliente extends Persona {
    private List<Mascota> mascotas;

    public Cliente(String nombres, String apellidoPaterno, String apellidoMaterno, String telefono, String dni,
                   Cuenta cuenta, List<Mascota> mascotas) {
        super(nombres, apellidoPaterno, apellidoMaterno, telefono, dni, cuenta);
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
                "nombres='" + getNombres() + '\'' +
                ", apellidoPaterno='" + getApellidoPaterno() + '\'' +
                ", mascotas=" + mascotas +
                '}';
    }
}
