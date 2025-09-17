/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

/**
 *
 * @author kristhor
 */
public class Veterinario {
    
        private int idVeterinario;
    private int idUsuario; 
    private String nombreVeterinario; 
    private String apellidoVeterinario;
    private String telefonoVeterinario;
    private String correoVeterinari;
    private int idEspecialidad;

    public Veterinario(int idVeterinario, int idUsuario, String nombreVeterinario, String apellidoVeterinario, String telefonoVeterinario, String correoVeterinari, int idEspecialidad) {
        this.idVeterinario = idVeterinario;
        this.idUsuario = idUsuario;
        this.nombreVeterinario = nombreVeterinario;
        this.apellidoVeterinario = apellidoVeterinario;
        this.telefonoVeterinario = telefonoVeterinario;
        this.correoVeterinari = correoVeterinari;
        this.idEspecialidad = idEspecialidad;
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

    public String getCorreoVeterinari() {
        return correoVeterinari;
    }

    public void setCorreoVeterinari(String correoVeterinari) {
        this.correoVeterinari = correoVeterinari;
    }

    public int getIdEspecialidad() {
        return idEspecialidad;
    }

    public void setIdEspecialidad(int idEspecialidad) {
        this.idEspecialidad = idEspecialidad;
    }

}
