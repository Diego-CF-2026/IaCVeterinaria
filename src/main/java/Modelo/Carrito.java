/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

public class Carrito {
    
    private int idCarrito;
    private int idCliente;
    private BigDecimal total;
    private String estado; 
    private Timestamp fecha;
    private int idPago; 
    private String estadoEntrega;
    
    private List<DetalleCarrito> detalles;
    
    public Carrito() {}

    public Carrito(int idCarrito, int idCliente, BigDecimal total, String estado, Timestamp fecha, int idPago, String estadoEntrega) {
        this.idCarrito = idCarrito;
        this.idCliente = idCliente;
        this.total = total;
        this.estado = estado;
        this.fecha = fecha;
        this.idPago = idPago;
        this.detalles = detalles;
        this.estadoEntrega = estadoEntrega;
    }

    public int getIdCarrito() {
        return idCarrito;
    }

    public void setIdCarrito(int idCarrito) {
        this.idCarrito = idCarrito;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Timestamp getFecha() {
        return fecha;
    }

    public void setFecha(Timestamp fecha) {
        this.fecha = fecha;
    }

    public int getIdPago() {
        return idPago;
    }

    public void setIdPago(int idPago) {
        this.idPago = idPago;
    }

    public List<DetalleCarrito> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleCarrito> detalles) {
        this.detalles = detalles;
    }
    
    private Cliente cliente; 

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public String getEstadoEntrega() {
        return estadoEntrega;
    }

    public void setEstadoEntrega(String estadoEntrega) {
        this.estadoEntrega = estadoEntrega;
    }
}
