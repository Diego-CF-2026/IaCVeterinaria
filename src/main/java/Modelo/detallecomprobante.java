/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

import java.math.BigDecimal;

/**
 *
 * @author kristhor
 */
public class detallecomprobante {
    
    private int idDetalleComprobante;
    private int idComprobante;
    private int idProducto;
    private int cantidad;
    private BigDecimal precio;

    public detallecomprobante(int idDetalleComprobante, int idComprobante, int idProducto, int cantidad, BigDecimal precio) {
        this.idDetalleComprobante = idDetalleComprobante;
        this.idComprobante = idComprobante;
        this.idProducto = idProducto;
        this.cantidad = cantidad;
        this.precio = precio;
    }

    public int getIdDetalleComprobante() {
        return idDetalleComprobante;
    }

    public void setIdDetalleComprobante(int idDetalleComprobante) {
        this.idDetalleComprobante = idDetalleComprobante;
    }

    public int getIdComprobante() {
        return idComprobante;
    }

    public void setIdComprobante(int idComprobante) {
        this.idComprobante = idComprobante;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }
    
    
}
