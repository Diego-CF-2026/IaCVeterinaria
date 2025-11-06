package Modelo;

public class Veterinario {
    private int idVeterinario;
    private int idUsuario;
    private String nombreVeterinario;
    private String apellidoVeterinario;
    private String telefonoVeterinario;
    private int idEspecialidad; // Clave foránea 
    private String correoVeterinario; // Dato de apoyo

    // 1. Constructor Vacío
    public Veterinario() {
    }

    public Veterinario(int idVeterinario, int idUsuario, String nombreVeterinario, String apellidoVeterinario, String telefonoVeterinario, int idEspecialidad, String correoVeterinario) {
        this.idVeterinario = idVeterinario;
        this.idUsuario = idUsuario;
        this.nombreVeterinario = nombreVeterinario;
        this.apellidoVeterinario = apellidoVeterinario;
        this.telefonoVeterinario = telefonoVeterinario;
        this.idEspecialidad = idEspecialidad;
        this.correoVeterinario = correoVeterinario;
    }


    public int getIdVeterinario() {
        return idVeterinario;
    }

    public void setIdVeterinario(int idVeterinario) {
        this.idVeterinario = idVeterinario;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombreVeterinario() {
        return nombreVeterinario;
    }

    public void setNombreVeterinario(String nombreVeterinario) {
        this.nombreVeterinario = nombreVeterinario;
    }

    public String getApellidoVeterinario() {
        return apellidoVeterinario;
    }

    public void setApellidoVeterinario(String apellidoVeterinario) {
        this.apellidoVeterinario = apellidoVeterinario;
    }

    public String getTelefonoVeterinario() {
        return telefonoVeterinario;
    }

    public void setTelefonoVeterinario(String telefonoVeterinario) {
        this.telefonoVeterinario = telefonoVeterinario;
    }

    public int getIdEspecialidad() {
        return idEspecialidad;
    }

    public void setIdEspecialidad(int idEspecialidad) {
        this.idEspecialidad = idEspecialidad;
    }

    public String getCorreoVeterinario() {
        return correoVeterinario;
    }

    public void setCorreoVeterinario(String correoVeterinario) {
        this.correoVeterinario = correoVeterinario;
    }
}