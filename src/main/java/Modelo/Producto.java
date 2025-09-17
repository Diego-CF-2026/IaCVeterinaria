package Modelo;

import jakarta.websocket.Decoder.Text;
import java.math.BigDecimal;
import java.sql.Date;

public class Producto {
    private int idProducto;
    private int idProveeedor;
    private String nombreProducto;
    private Text descripcion;
    private int stock;
    private String unidadMedida;
    private String imagen; // nombre del archivo o ruta relativa
    private int estado;
    private int idProveedor;
    private Date fechaRegistro;

    public Producto(int idProducto, int idProveeedor, String nombreProducto, Text descripcion, int stock, String unidadMedida, String imagen, int estado, int idProveedor, Date fechaRegistro) {
        this.idProducto = idProducto;
        this.idProveeedor = idProveeedor;
        this.nombreProducto = nombreProducto;
        this.descripcion = descripcion;
        this.stock = stock;
        this.unidadMedida = unidadMedida;
        this.imagen = imagen;
        this.estado = estado;
        this.idProveedor = idProveedor;
        this.fechaRegistro = fechaRegistro;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public int getIdProveeedor() {
        return idProveeedor;
    }

    public void setIdProveeedor(int idProveeedor) {
        this.idProveeedor = idProveeedor;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public Text getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(Text descripcion) {
        this.descripcion = descripcion;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getUnidadMedida() {
        return unidadMedida;
    }

    public void setUnidadMedida(String unidadMedida) {
        this.unidadMedida = unidadMedida;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public int getIdProveedor() {
        return idProveedor;
    }

    public void setIdProveedor(int idProveedor) {
        this.idProveedor = idProveedor;
    }

    public Date getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(Date fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }


    
}
