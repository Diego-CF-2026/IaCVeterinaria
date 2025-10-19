package Modelo;

/**
 * Representa la tabla 'estado' en la base de datos.
 * Esta tabla contiene la lista de posibles estados de una cita (ej: Pendiente, Confirmada, Cancelada, Finalizada).
 */
public class Estado {

    // Corresponde a 'idEstado' en la BD (Clave Primaria - FK en la tabla 'citas')
    private int idEstado;
    
    // Corresponde a 'tipoEstado' en la BD (Ej: "Pendiente", "Confirmada")
    private String tipoEstado;

    // Constructor por defecto
    public Estado() {
    }

    // Constructor con todos los campos
    public Estado(int idEstado, String tipoEstado) {
        this.idEstado = idEstado;
        this.tipoEstado = tipoEstado;
    }

    // --- Getters y Setters ---

    public int getIdEstado() {
        return idEstado;
    }

    public void setIdEstado(int idEstado) {
        this.idEstado = idEstado;
    }

    public String getTipoEstado() {
        return tipoEstado;
    }

    public void setTipoEstado(String tipoEstado) {
        this.tipoEstado = tipoEstado;
    }

    // Método toString para depuración
    @Override
    public String toString() {
        return "Estado{" +
                "idEstado=" + idEstado +
                ", tipoEstado='" + tipoEstado + '\'' +
                '}';
    }
}