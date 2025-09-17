/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

import java.sql.Date;

/**
 *
 * @author kristhor
 */
public class detalleCarrito {
    
    private int idDetalleCarrito;
    private int idCarrito;
    private int idProducto;
    private int cantidadProducto;
    private Date fechaAgregado;

    public detalleCarrito(int idDetalleCarrito, int idCarrito, int idProducto, int cantidadProducto, Date fechaAgregado) {
        this.idDetalleCarrito = idDetalleCarrito;
        this.idCarrito = idCarrito;
        this.idProducto = idProducto;
        this.cantidadProducto = cantidadProducto;
        this.fechaAgregado = fechaAgregado;
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

    public Date getFechaAgregado() {
        return fechaAgregado;
    }

    public void setFechaAgregado(Date fechaAgregado) {
        this.fechaAgregado = fechaAgregado;
    }
    
    
}
