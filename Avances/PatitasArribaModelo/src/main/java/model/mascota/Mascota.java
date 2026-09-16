package model.mascota;

import model.atencion.AtencionMedica;
import model.usuario.Cliente;

import java.time.LocalDate;
import java.util.List;

public class Mascota {
    private int idMascota;
    private String nombre;
    private SexoMascota sexo;
    private double peso;
    private LocalDate fechaNacimiento;
    private TipoMascota tipoMascota;
    private String raza;
    private Cliente cliente;
    private List<AtencionMedica> atencionesMedicas;

    public Mascota(int idMascota, String nombre, SexoMascota sexo, double peso, LocalDate fechaNacimiento, TipoMascota tipoMascota, String raza, Cliente cliente, List<AtencionMedica> atencionesMedicas) {
        this.idMascota = idMascota;
        this.nombre = nombre;
        this.sexo = sexo;
        this.peso = peso;
        this.fechaNacimiento = fechaNacimiento;
        this.tipoMascota = tipoMascota;
        this.raza = raza;
        this.cliente = cliente;
        this.atencionesMedicas = atencionesMedicas;
    }

    // Getters y Setters
    public int getIdMascota() {
        return idMascota;
    }

    public void setIdMascota(int idMascota) {
        this.idMascota = idMascota;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public SexoMascota getSexo() {
        return sexo;
    }

    public void setSexo(SexoMascota sexo) {
        this.sexo = sexo;
    }

    public double getPeso() {
        return peso;
    }

    public void setPeso(double peso) {
        this.peso = peso;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public TipoMascota getTipoMascota() {
        return tipoMascota;
    }

    public void setTipoMascota(TipoMascota tipoMascota) {
        this.tipoMascota = tipoMascota;
    }

    public String getRaza() {
        return raza;
    }

    public void setRaza(String raza) {
        this.raza = raza;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public List<AtencionMedica> getAtencionesMedicas() {
        return atencionesMedicas;
    }

    public void setAtencionesMedicas(List<AtencionMedica> atencionesMedicas) {
        this.atencionesMedicas = atencionesMedicas;
    }

    @Override
    public String toString() {
        String dniCliente = null;
        if (cliente != null) {
            dniCliente = cliente.getDni();
        }

        return "Mascota{" +
                "idMascota=" + idMascota +
                ", nombre='" + nombre + "'" +
                ", sexo=" + sexo +
                ", peso=" + peso +
                ", fechaNacimiento=" + fechaNacimiento +
                ", tipoMascota=" + tipoMascota +
                ", raza='" + raza + "'" +
                ", cliente=" + dniCliente +
                ", atencionesMedicas=" + atencionesMedicas +
                '}';
    }
}
