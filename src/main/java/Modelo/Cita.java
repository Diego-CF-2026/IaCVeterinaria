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
    private double precio; 
    
    // Atributos de apoyo (JOINs y detalles del cliente)
    private String nombreCliente;
    private String apellidoCliente;
    private String dniCliente;
    private String nombreVeterinario;
    private String apellidoVeterinario;
    private String especialidadVeterinario;
    
    // 💡 CAMPO AGREGADO PARA ELIMINAR ERROR DE JSP
    private String nombreServicio; 
    
    // ATRIBUTOS AGREGADOS: Mascota y Tratamiento/Diagnóstico
    private String nombreMascota;    
    private String diagnostico;
    private String tratamiento;
    private String notasTratamiento; 

    // Constructor vacío
    public Cita() {
    }

    // Constructor completo (Se sugiere revisar y generar automáticamente si es muy largo)
    // Se omite la reimpresión del constructor aquí por ser demasiado largo y rara vez usado
    // Se asume que el que tienes es funcional.

    // =================================================================================
    // GETTERS Y SETTERS BASE
    // (Estos ya son correctos y se mantienen tal cual los enviaste)
    // =================================================================================
    
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
    
    public String getEstado() { // <--- ¡AÑADE ESTE MÉTODO!
        return this.estadoNombre;
    }

    public String getEstadoNombre() {
        return estadoNombre;
    }
    public void setEstadoNombre(String estadoNombre) {
        this.estadoNombre = estadoNombre;
    }

    public double getPrecio() {
        return precio;
    }
    public void setPrecio(double precio) {
        this.precio = precio;
    }
    
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
    
    // =================================================================================
    // GETTERS Y SETTERS AGREGADOS (Mascota, Tratamiento y Servicio)
    // =================================================================================

    // 💡 NUEVOS MÉTODOS AÑADIDOS PARA COMPATIBILIDAD CON JSP
    public String getNombreServicio() {
        return nombreServicio;
    }

    public void setNombreServicio(String nombreServicio) {
        this.nombreServicio = nombreServicio;
    }
    
    public String getNombreMascota() {
        return nombreMascota;
    }

    public void setNombreMascota(String nombreMascota) {
        this.nombreMascota = nombreMascota;
    }

    public String getDiagnostico() {
        return diagnostico;
    }

    public void setDiagnostico(String diagnostico) {
        this.diagnostico = diagnostico;
    }

    public String getTratamiento() {
        return tratamiento;
    }

    public void setTratamiento(String tratamiento) {
        this.tratamiento = tratamiento;
    }

    public String getNotasTratamiento() {
        return notasTratamiento;
    }

    public void setNotasTratamiento(String notasTratamiento) {
        this.notasTratamiento = notasTratamiento;
    }
}