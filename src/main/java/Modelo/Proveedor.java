package Modelo;


import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class Proveedor {
    private int idProveedor;
    private String razonSocial;
    private String ruc;
    private String direccion;
    private String correo;
    private String telefono;
    private int estado;
    private Date fechaRegistro;

    public Proveedor(int idProveedor, String razonSocial, String ruc, String direccion, String correo, String telefono, int estado, Date fechaRegistro) {
        this.idProveedor = idProveedor;
        this.razonSocial = razonSocial;
        this.ruc = ruc;
        this.direccion = direccion;
        this.correo = correo;
        this.telefono = telefono;
        this.estado = estado;
        this.fechaRegistro = fechaRegistro;
    }

    public int getIdProveedor() {
        return idProveedor;
    }

    public void setIdProveedor(int idProveedor) {
        this.idProveedor = idProveedor;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }

    public String getRuc() {
        return ruc;
    }

    public void setRuc(String ruc) {
        this.ruc = ruc;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public Date getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(Date fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

 
    
    
}
