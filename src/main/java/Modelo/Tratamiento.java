package Modelo;
//paquete Modelo

import java.util.Date;
//import de tiempo
/**
 * Clase que representa un tratamiento médico en la veterinaria.
 * Contiene información sobre el diagnóstico, tratamiento aplicado,
 * la cita asociada, el cliente y la fecha de registro.
 */
//esta es la clase tramtamiento
public class Tratamiento {

    // ==========================
    // Atributos
    // ==========================
    private int idTratamiento;
    private int idCita;
    private String diagnostico;
    private String tratamiento;
    private String dniCliente;
    private Date fechaRegistro;

    // ==========================
    // Constructores
    // ==========================
    public Tratamiento() {
    }
//constructor completo
    public Tratamiento(int idTratamiento, int idCita, String diagnostico, String tratamiento, String dniCliente, Date fechaRegistro) {
        this.idTratamiento = idTratamiento;
        this.idCita = idCita;
        this.diagnostico = diagnostico;
        this.tratamiento = tratamiento;
        this.dniCliente = dniCliente;
        this.fechaRegistro = fechaRegistro;
    }

    // ==========================
    // Getters y Setters
    // ==========================
    public int getIdTratamiento() {
        return idTratamiento;
    }

    public void setIdTratamiento(int idTratamiento) {
        this.idTratamiento = idTratamiento;
    }

    public int getIdCita() {
        return idCita;
    }

    public void setIdCita(int idCita) {
        this.idCita = idCita;
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

    public String getDniCliente() {
        return dniCliente;
    }

    public void setDniCliente(String dniCliente) {
        this.dniCliente = dniCliente;
    }

    public Date getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(Date fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
// Método toString para depuración
    // ==========================
    // toString (opcional, útil para depuración)
    // ==========================
    @Override
    public String toString() {
        return "Tratamiento{" +
                "idTratamiento=" + idTratamiento +
                ", idCita=" + idCita +
                ", diagnostico='" + diagnostico + '\'' +
                ", tratamiento='" + tratamiento + '\'' +
                ", dniCliente='" + dniCliente + '\'' +
                ", fechaRegistro=" + fechaRegistro +
                '}';
    }
}
