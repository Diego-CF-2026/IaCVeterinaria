package Modelo;

import java.io.Serializable;

public class Especialidad implements Serializable {
    private static final long serialVersionUID = 1L;

    private int idEspecialidad;
    private String nombreEspecialidad;
    private Double precio; // corrige capitalización

    public Especialidad() {}

    public Especialidad(int idEspecialidad, String nombreEspecialidad, Double precio) {
        this.idEspecialidad = idEspecialidad;
        this.nombreEspecialidad = nombreEspecialidad;
        this.precio = precio;
    }

    public int getIdEspecialidad() { return idEspecialidad; }
    public void setIdEspecialidad(int idEspecialidad) { this.idEspecialidad = idEspecialidad; }

    public String getNombreEspecialidad() { return nombreEspecialidad; }
    public void setNombreEspecialidad(String nombreEspecialidad) { this.nombreEspecialidad = nombreEspecialidad; }

    public Double getPrecio() { return precio; }
    public void setPrecio(Double precio) { this.precio = precio; }

    @Override
    public String toString() {
        return "EspecialidadEmpleado{" +
                "idEspecialidad=" + idEspecialidad +
                ", nombreEspecialidad='" + nombreEspecialidad + '\'' +
                ", precio=" + precio +
                '}';
    }
}
