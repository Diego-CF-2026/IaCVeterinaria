package Modelo;

import java.sql.Date;
import java.sql.Time;

public class Cita {
    private int idCita;
    private int idCliente;
    private int idVeterinario;
    private Date fecha;
    private Time hora;
    private String motivo;
    private int idEstado; 
    private String estadoNombre;
    private double precio; // El precio de la cita en el momento de la reserva
    
    // Atributos de apoyo para detalles (obtenidos por JOIN)
    private String nombreCliente;
    private String apellidoCliente;
    private String dniCliente;
    private String nombreVeterinario;
    private String apellidoVeterinario;
    private String especialidadVeterinario;

    // Constructor vacío
    public Cita() {
    }

    // Constructor completo con precio (usado para recuperar datos de la BD o en lógica de negocio)
    public Cita(int idCita, int idCliente, int idVeterinario, Date fecha, Time hora, String motivo, int idEstado, String estadoNombre, double precio, String nombreCliente, String apellidoCliente, String dniCliente, String nombreVeterinario, String apellidoVeterinario, String especialidadVeterinario) {
        this.idCita = idCita;
        this.idCliente = idCliente;
        this.idVeterinario = idVeterinario;
        this.fecha = fecha;
        this.hora = hora;
        this.motivo = motivo;
        this.idEstado = idEstado;
        this.estadoNombre = estadoNombre;
        this.precio = precio; 
        this.nombreCliente = nombreCliente;
        this.apellidoCliente = apellidoCliente;
        this.dniCliente = dniCliente;
        this.nombreVeterinario = nombreVeterinario;
        this.apellidoVeterinario = apellidoVeterinario;
        this.especialidadVeterinario = especialidadVeterinario;
    }

    // Getters y Setters
    
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

    public String getEstadoNombre() {
        return estadoNombre;
    }

    public void setEstadoNombre(String estadoNombre) {
        this.estadoNombre = estadoNombre;
    }

    // 🟢 Getters y Setters del Precio
    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }
    
    // Getters y Setters de los detalles
    
    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public String getApellidoCliente() {
        return apellidoCliente;
    }

    public void setApellidoCliente(String apellidoCliente) {
        this.apellidoCliente = apellidoCliente;
    }

    public String getDniCliente() {
        return dniCliente;
    }

    public void setDniCliente(String dniCliente) {
        this.dniCliente = dniCliente;
    }

    public String getNombreVeterinario() {
        return nombreVeterinario;
    }

    public void setNombreVeterinario(String nombreVeterinario) {
        this.nombreVeterinario = nombreVeterinario;
    }

    public String getApellidoVeterinario() {
        return apellidoVeterinario;
    }

    public void setApellidoVeterinario(String apellidoVeterinario) {
        this.apellidoVeterinario = apellidoVeterinario;
    }

    public String getEspecialidadVeterinario() {
        return especialidadVeterinario;
    }

    public void setEspecialidadVeterinario(String especialidadVeterinario) {
        this.especialidadVeterinario = especialidadVeterinario;
    }
}