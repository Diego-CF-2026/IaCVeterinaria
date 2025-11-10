/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;
//paquete Modelo 

/**
 *
 * @author kristhor
 */

//clasee tipo de pago
public class TipoDePago {
    
    //varibles de tipo de pago
    private int idPago;
    private String nombrePago;
    
  // constructor completo 
    public TipoDePago(int idPago, String nombrePago) {
        this.idPago = idPago;
        this.nombrePago = nombrePago;
    }
    
    public int getIdPago() {
        return idPago;
    }

    public void setIdPago(int idPago) {
        this.idPago = idPago;
    }

    public String getNombrePago() {
        return nombrePago;
    }

    public void setNombrePago(String nombrePago) {
        this.nombrePago = nombrePago;
    }
         
}
