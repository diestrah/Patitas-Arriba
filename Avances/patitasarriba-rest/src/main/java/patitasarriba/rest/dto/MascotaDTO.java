package patitasarriba.rest.dto;

public class MascotaDTO {
    private int id;
    // Si el JSON no trae "activo", queda en true (igual que el modelo Registro)
    private boolean activo = true;
    private ClienteDTO cliente;
    private String nombre;
    private String sexo;
    private double peso;
    private String fechaNacimiento;
    private String raza;
    private String tipoMascota;
    // Imagen opcional como texto "data:image/jpeg;base64,..." (vacío = sin foto)
    private String foto;

    public MascotaDTO() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public ClienteDTO getCliente() {
        return cliente;
    }

    public void setCliente(ClienteDTO cliente) {
        this.cliente = cliente;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public double getPeso() {
        return peso;
    }

    public void setPeso(double peso) {
        this.peso = peso;
    }

    public String getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(String fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getRaza() {
        return raza;
    }

    public void setRaza(String raza) {
        this.raza = raza;
    }

    public String getTipoMascota() {
        return tipoMascota;
    }

    public void setTipoMascota(String tipoMascota) {
        this.tipoMascota = tipoMascota;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }
}
