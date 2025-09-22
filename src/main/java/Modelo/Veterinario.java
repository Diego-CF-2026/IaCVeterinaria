package Modelo;

import Modelo.Especialidad;

public class Veterinario {
    private int idVeterinario;
    private String nombreVeterianrio; // Este es el campo que usaremos para el nombre completo
    private String apellidoVeterinario; // Puedes mantenerlo si lo necesitas para otras operaciones
    private String telefonoVeterinario;
    private String correoVeterinario;
    private int idEspecialidad;
    private String nombreEspecialidad;

    public Veterinario() {
    }

    public Veterinario(String nombreEspecialidad) {
        this.nombreEspecialidad = nombreEspecialidad;
    }

    public String getNombreEspecialidad() {
        return nombreEspecialidad;
    }

    public void setNombreEspecialidad(String nombreEspecialidad) {
        this.nombreEspecialidad = nombreEspecialidad;
    }

    public Veterinario(int idVeterinario, String nombreVeterianrio, String apellidoVeterinario, String telefonoVeterinario, String correoVeterinario, int idEspecialidad) {
        this.idVeterinario = idVeterinario;
        this.nombreVeterianrio = nombreVeterianrio;
        this.apellidoVeterinario = apellidoVeterinario;
        this.telefonoVeterinario = telefonoVeterinario;
        this.correoVeterinario = correoVeterinario;
        this.idEspecialidad = idEspecialidad;
    }

    public int getIdVeterinario() {
        return idVeterinario;
    }

    public void setIdVeterinario(int idVeterinario) {
        this.idVeterinario = idVeterinario;
    }

    public String getNombreVeterianrio() {
        return nombreVeterianrio;
    }

    public void setNombreVeterianrio(String nombreVeterianrio) {
        this.nombreVeterianrio = nombreVeterianrio;
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

    public String getCorreoVeterinario() {
        return correoVeterinario;
    }

    public void setCorreoVeterinario(String correoVeterinario) {
        this.correoVeterinario = correoVeterinario;
    }

    public int getIdEspecialidad() {
        return idEspecialidad;
    }

    public void setIdEspecialidad(int idEspecialidad) {
        this.idEspecialidad = idEspecialidad;
    }
    
    
}