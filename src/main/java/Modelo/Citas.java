package Modelo;

import java.sql.Date;
import java.sql.Time;

public class Citas {
    private int idCita;
    private int idCliente;
    private int idVeterinario;
    private Date fecha;
    private Time hora;
    private String motivo;
    private int idEstado;

    public Citas(int idCita, int idCliente, int idVeterinario, Date fecha, Time hora, String motivo, int idEstado) {
        this.idCita = idCita;
        this.idCliente = idCliente;
        this.idVeterinario = idVeterinario;
        this.fecha = fecha;
        this.hora = hora;
        this.motivo = motivo;
        this.idEstado = idEstado;
    }

    public int getIdCita() {
        return idCita;
    }

    public void setIdCita(int idCita) {
        this.idCita = idCita;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public int getIdVeterinario() {
        return idVeterinario;
    }

    public void setIdVeterinario(int idVeterinario) {
        this.idVeterinario = idVeterinario;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public Time getHora() {
        return hora;
    }

    public void setHora(Time hora) {
        this.hora = hora;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public int getIdEstado() {
        return idEstado;
    }

    public void setIdEstado(int idEstado) {
        this.idEstado = idEstado;
    }

    
}
