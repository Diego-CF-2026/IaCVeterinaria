package Modelo;
//paquete modelo
import java.io.Serializable;
//import
public class Especialidad implements Serializable {
    private static final long serialVersionUID = 1L;
//variables
    private int idEspecialidad;
    private String nombreEspecialidad;
    // corrige capitalización
    private Double precio; 
//constructo vacio
    public Especialidad() {}
//constructor completo
    public Especialidad(int idEspecialidad, String nombreEspecialidad, Double precio) {
        this.idEspecialidad = idEspecialidad;
        this.nombreEspecialidad = nombreEspecialidad;
        this.precio = precio;
    }
//gethers and sethers
    public int getIdEspecialidad() { return idEspecialidad; }
    public void setIdEspecialidad(int idEspecialidad) { this.idEspecialidad = idEspecialidad; }

    public String getNombreEspecialidad() { return nombreEspecialidad; }
    public void setNombreEspecialidad(String nombreEspecialidad) { this.nombreEspecialidad = nombreEspecialidad; }

    public Double getPrecio() { return precio; }
    public void setPrecio(Double precio) { this.precio = precio; }
//cambio de valor a string
    @Override
    public String toString() {
        return "EspecialidadEmpleado{" +
                "idEspecialidad=" + idEspecialidad +
                ", nombreEspecialidad='" + nombreEspecialidad + '\'' +
                ", precio=" + precio +
                '}';
    }
}
