/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;
//paquete Modelo

/**
 *
 * @author PC
 */

// clase Usuario
public class Usuario {
    //variable de ususario
    private int idUsuario;
    private int idRol;
    private String correo;
    private String contra;
    private int intentos;
    private boolean Estado;

     
    private java.sql.Timestamp tiempoBloqueo; 
    //constructor vacio
    private String nombreRol; 
    public Usuario() {
    }

    //constructor completo
    public Usuario(int idUsuario, int idRol, String correo, String contra, int intentos, boolean Estado) {
        this.idUsuario = idUsuario;
        this.idRol = idRol;
        this.correo = correo;
        this.contra = contra;
        this.intentos = intentos;
        this.Estado = Estado;
    }

    //gethers and sethers
    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public int getIdRol() {
        return idRol;
    }

    public void setIdRol(int idRol) {
        this.idRol = idRol;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getContra() {
        return contra;
    }

    public void setContra(String contra) {
        this.contra = contra;
    }

    public int getIntentos() {
        return intentos;
    }

    public void setIntentos(int intentos) {
        this.intentos = intentos;
    }

    public boolean isEstado() {
        return Estado;
    }

    public void setEstado(boolean Estado) {
        this.Estado = Estado;
    }

    public String getNombreRol() { return nombreRol; }
    public void setNombreRol(String nombreRol) { this.nombreRol = nombreRol; }
    
    
    public java.sql.Timestamp getTiempoBloqueo() {
        return tiempoBloqueo;
    }
    
    public void setTiempoBloqueo(java.sql.Timestamp tiempoBloqueo) {
        this.tiempoBloqueo = tiempoBloqueo;
    }
    
}
