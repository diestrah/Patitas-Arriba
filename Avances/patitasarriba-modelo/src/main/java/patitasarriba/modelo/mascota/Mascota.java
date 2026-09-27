package patitasarriba.modelo.mascota;

import patitasarriba.modelo.Registro;
import patitasarriba.modelo.usuario.Cliente;
import patitasarriba.modelo.cita.Cita;
import patitasarriba.modelo.atencion.AtencionMedica;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Mascota extends Registro {
    private String nombre;
    private SexoMascota sexo;
    private double peso;
    private LocalDate fechaNacimiento;
    private TipoMascota tipoMascota;
    private String raza;
    private Cliente cliente;
    private List<Cita> citas;
    private List<AtencionMedica> atencionesMedicas;

    public Mascota() {
        this.citas = new ArrayList<>();
        this.atencionesMedicas = new ArrayList<>();
    }

    public Mascota(int id, boolean activo, String nombre, SexoMascota sexo, double peso, LocalDate fechaNacimiento, TipoMascota tipoMascota, String raza, Cliente cliente, List<Cita> citas, List<AtencionMedica> atencionesMedicas) {
        super(id, activo);
        setNombre(nombre);
        setSexo(sexo);
        setPeso(peso);
        setFechaNacimiento(fechaNacimiento);
        setTipoMascota(tipoMascota);
        setRaza(raza);
        setCliente(cliente);
        setCitas(citas);
        setAtencionesMedicas(atencionesMedicas);
    }

    // Constructor de copia
    public Mascota(final Mascota mascota) {
        if (mascota == null) {
            throw new IllegalArgumentException("mascota no puede ser nula");
        }
        super(mascota);
        setNombre(mascota.getNombre());
        setSexo(mascota.getSexo());
        setPeso(mascota.getPeso());
        setFechaNacimiento(mascota.getFechaNacimiento());
        setTipoMascota(mascota.getTipoMascota());
        setRaza(mascota.getRaza());
        setCliente(mascota.getCliente());
        setCitas(mascota.getCitas());
        setAtencionesMedicas(mascota.getAtencionesMedicas());
    }

    // Getters y Setters

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        if (nombre == null || nombre.isEmpty()) {
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
        if (tipoMascota == null) {
            throw new IllegalArgumentException("tipoMascota no puede ser nulo");
        }
        this.tipoMascota = tipoMascota;
    }

    public String getRaza() {
        return raza;
    }

    public void setRaza(String raza) {
        if (raza == null || raza.isEmpty()) {
            throw new IllegalArgumentException("raza no puede ser nulo o vacío");
        }
        this.raza = raza;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        if (cliente == null) {
            throw new IllegalArgumentException("cliente no puede ser nulo");
        }
        this.cliente = cliente;
    }

    public List<Cita> getCitas() {
        return Collections.unmodifiableList(citas);
    }

    public void setCitas(List<Cita> citas) {
        if (citas == null) {
            throw new IllegalArgumentException("citas no puede ser nulo");
        }
        this.citas = new ArrayList<>(citas);
    }

    public List<AtencionMedica> getAtencionesMedicas() {
        return Collections.unmodifiableList(atencionesMedicas);
    }

    public void setAtencionesMedicas(List<AtencionMedica> atencionesMedicas) {
        if (atencionesMedicas == null) {
            throw new IllegalArgumentException("atencionesMedicas no puede ser nulo");
        }
        this.atencionesMedicas = new ArrayList<>(atencionesMedicas);
    }

    public void agregarAtencionMedica(AtencionMedica atencionMedica) {
        atencionesMedicas.add(atencionMedica);
    }

    @Override
    public String toString() {
        String dniCliente = null;
        if (cliente != null) {
            dniCliente = cliente.getDni();
        }

        return "Mascota{" +
                super.toString() +
                ", nombre='" + nombre + "'" +
                ", sexo=" + sexo +
                ", peso=" + peso +
                ", fechaNacimiento=" + fechaNacimiento +
                ", tipoMascota=" + tipoMascota +
                ", raza='" + raza + "'" +
                ", cliente=" + dniCliente +
                ", citas=" + citas +
                ", atencionesMedicas=" + atencionesMedicas +
                '}';
    }
}
