/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

import java.sql.Timestamp;

/**
 *
 * @author kristhor
 */
public class DetalleCarrito {
    
    private int idDetalleCarrito;
    private int idCarrito;
    private int idProducto;
    private int cantidadProducto;
    private Timestamp fechaAgregado;

    // Relación con producto (opcional para mostrar datos en carrito)
    private Producto producto;  
    
    public DetalleCarrito() {}

    public DetalleCarrito(int idDetalleCarrito, int idCarrito, int idProducto, int cantidadProducto, Timestamp fechaAgregado, Producto producto) {
        this.idDetalleCarrito = idDetalleCarrito;
        this.idCarrito = idCarrito;
        this.idProducto = idProducto;
        this.cantidadProducto = cantidadProducto;
        this.fechaAgregado = fechaAgregado;
        this.producto = producto;
    }

    public int getIdDetalleCarrito() {
        return idDetalleCarrito;
    }

    public void setIdDetalleCarrito(int idDetalleCarrito) {
        this.idDetalleCarrito = idDetalleCarrito;
    }

    public int getIdCarrito() {
        return idCarrito;
    }

    public void setIdCarrito(int idCarrito) {
        this.idCarrito = idCarrito;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public int getCantidadProducto() {
        return cantidadProducto;
    }

    public void setCantidadProducto(int cantidadProducto) {
        this.cantidadProducto = cantidadProducto;
    }

    public Timestamp getFechaAgregado() {
        return fechaAgregado;
    }

    public void setFechaAgregado(Timestamp fechaAgregado) {
        this.fechaAgregado = fechaAgregado;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }
    
    
    
    
}
