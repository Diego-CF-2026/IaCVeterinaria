/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;
//lugar de paquete
import java.sql.Date;
//import de tiempo
public class Cliente {
    //variables
    private int idCliente;
    private int idUsuario;
    private String nombre;
    private String Apellido;
    private String dni;
    private String telefono;
    private Date fechaRegistro;
// constructor vacio
    public Cliente() {
        
    }
//constructor con variables
    public Cliente(int idCliente, int idUsuario, String nombre,String Apellido, String dni, String telefono, Date fechaRegistro) {
        this.idCliente = idCliente;
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.Apellido = Apellido;
        this.dni = dni;
        this.telefono = telefono;
        this.fechaRegistro = fechaRegistro;
    }
//guetehrs and sethers
    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public Date getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(Date fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
     public String getApellido() {
        return Apellido;
    }

    public void setApellido(String Apellido) {
        this.Apellido = Apellido;
    }
}