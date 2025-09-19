package Modelo;

import java.math.BigDecimal;
import java.sql.Date;

public class Carrito {

    private int idCarrito;
    private String idCliente;
    private BigDecimal total;
    private int idPago;
    private String estado;
    private Date fecha;

    public Carrito(int idCarrito, String idCliente, BigDecimal total, int idPago, String estado, Date fecha) {
        this.idCarrito = idCarrito;
        this.idCliente = idCliente;
        this.total = total;
        this.idPago = idPago;
        this.estado = estado;
        this.fecha = fecha;
    }

    public int getIdCarrito() {
        return idCarrito;
    }

    public void setIdCarrito(int idCarrito) {
        this.idCarrito = idCarrito;
    }

    public String getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(String idCliente) {
        this.idCliente = idCliente;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public int getIdPago() {
        return idPago;
    }

    public void setIdPago(int idPago) {
        this.idPago = idPago;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    
}
