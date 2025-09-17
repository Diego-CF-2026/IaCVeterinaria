/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

import java.math.BigDecimal;
import java.sql.Date;

/**
 *
 * @author kristhor
 */
public class comprobante {
    
    private int idComprobante;
    private int idCliente;
    private int idRecepcionista;
    private Date fechaComprobante;
    private String tipoComprobante;
    private BigDecimal montoTotal;

    public comprobante(int idComprobante, int idCliente, int idRecepcionista, Date fechaComprobante, String tipoComprobante, BigDecimal montoTotal) {
        this.idComprobante = idComprobante;
        this.idCliente = idCliente;
        this.idRecepcionista = idRecepcionista;
        this.fechaComprobante = fechaComprobante;
        this.tipoComprobante = tipoComprobante;
        this.montoTotal = montoTotal;
    }

    public int getIdComprobante() {
        return idComprobante;
    }

    public void setIdComprobante(int idComprobante) {
        this.idComprobante = idComprobante;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public int getIdRecepcionista() {
        return idRecepcionista;
    }

    public void setIdRecepcionista(int idRecepcionista) {
        this.idRecepcionista = idRecepcionista;
    }

    public Date getFechaComprobante() {
        return fechaComprobante;
    }

    public void setFechaComprobante(Date fechaComprobante) {
        this.fechaComprobante = fechaComprobante;
    }

    public String getTipoComprobante() {
        return tipoComprobante;
    }

    public void setTipoComprobante(String tipoComprobante) {
        this.tipoComprobante = tipoComprobante;
    }

    public BigDecimal getMontoTotal() {
        return montoTotal;
    }

    public void setMontoTotal(BigDecimal montoTotal) {
        this.montoTotal = montoTotal;
    }
    
    
    
}
