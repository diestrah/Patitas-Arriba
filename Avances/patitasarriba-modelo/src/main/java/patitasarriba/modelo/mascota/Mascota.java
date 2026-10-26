package patitasarriba.modelo.mascota;

import patitasarriba.modelo.Registro;
import patitasarriba.modelo.usuario.Cliente;

import java.time.LocalDate;

public class Mascota extends Registro {
    private int idMascota;
    private Cliente cliente;
    private String nombre;
    private SexoMascota sexo;
    private double peso;
    private LocalDate fechaNacimiento;
    private String raza;
    private TipoMascota tipoMascota;

    public Mascota() {
    }

    public Mascota(int idMascota, boolean activo, Cliente cliente, String nombre, SexoMascota sexo, double peso,
                   LocalDate fechaNacimiento, String raza, TipoMascota tipoMascota) {
        super(activo);
        setIdMascota(idMascota);
        setCliente(cliente);
        setNombre(nombre);
        setSexo(sexo);
        setPeso(peso);
        setFechaNacimiento(fechaNacimiento);
        setRaza(raza);
        setTipoMascota(tipoMascota);
    }

    // Constructor de copia
    public Mascota(final Mascota mascota) {
        if (mascota == null) {
            throw new IllegalArgumentException("mascota no puede ser nula");
        }
        super(mascota);
        setIdMascota(mascota.getIdMascota());
        setCliente(mascota.getCliente());
        setNombre(mascota.getNombre());
        setSexo(mascota.getSexo());
        setPeso(mascota.getPeso());
        setFechaNacimiento(mascota.getFechaNacimiento());
        setRaza(mascota.getRaza());
        setTipoMascota(mascota.getTipoMascota());
    }

    // Getters y Setters
    public int getIdMascota() {
        return idMascota;
    }

    public void setIdMascota(int idMascota) {
        if (idMascota < 0) {
            throw new IllegalArgumentException("idMascota no puede ser negativo");
        }
        this.idMascota = idMascota;
    }

    public Cliente getCliente() {
        if (cliente == null) {
            return null;
        }
        return new Cliente(cliente);
    }

    public void setCliente(Cliente cliente) {
        if (cliente == null) {
            throw new IllegalArgumentException("cliente no puede ser nulo");
        }
        this.cliente = new Cliente(cliente);
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("nombre no puede ser nulo o vacío");
        }
        this.nombre = nombre;
    }

    public SexoMascota getSexo() {
        return sexo;
    }

    public void setSexo(SexoMascota sexo) {
        if (sexo == null) {
            throw new IllegalArgumentException("sexo no puede ser nulo");
        }
        this.sexo = sexo;
    }

    public double getPeso() {
        return peso;
    }

    public void setPeso(double peso) {
        // peso es opcional en la base: 0 significa que no se registro
        if (peso < 0) {
            throw new IllegalArgumentException("peso no puede ser negativo");
        }
        this.peso = peso;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        if (fechaNacimiento == null) {
            throw new IllegalArgumentException("fechaNacimiento no puede ser nula");
        }
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getRaza() {
        return raza;
    }

    public void setRaza(String raza) {
        // raza es opcional: puede ser mestiza o desconocida
        this.raza = raza;
    }

    public TipoMascota getTipoMascota() {
        return tipoMascota;
    }

    public void setTipoMascota(TipoMascota tipoMascota) {
        if (tipoMascota == null) {
            throw new IllegalArgumentException("tipoMascota no puede ser nulo");
        }
        this.tipoMascota = tipoMascota;
    }

    @Override
    public String toString() {
        String dniCliente = null;
        if (cliente != null) {
            dniCliente = cliente.getDni();
        }

        return "Mascota{" +
                "idMascota=" + idMascota +
                ", " + super.toString() +
                ", cliente=" + dniCliente +
                ", nombre='" + nombre + '\'' +
                ", sexo=" + sexo +
                ", peso=" + peso +
                ", fechaNacimiento=" + fechaNacimiento +
                ", raza='" + raza + '\'' +
                ", tipoMascota=" + tipoMascota +
                '}';
    }
}
