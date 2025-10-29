package Modelo;

public class Veterinario {
    private int idVeterinario;
    private int idUsuario; //NUEVO CAMPO DE CONEXIÓN
    private String nombreVeterinario; // Corrección: Coherencia con el getter/setter
    private String apellidoVeterinario;
    private String telefonoVeterinario;
    private int idEspecialidad;
    private String nombreEspecialidad; // Para joins con la tabla Especialidad

    public Veterinario() {
    }
    

    public Veterinario(int idVeterinario, int idUsuario, String nombreVeterinario, String apellidoVeterinario, String telefonoVeterinario, int idEspecialidad) {
        this.idVeterinario = idVeterinario;
        this.idUsuario = idUsuario; // Incluido
        this.nombreVeterinario = nombreVeterinario;
        this.apellidoVeterinario = apellidoVeterinario;
        this.telefonoVeterinario = telefonoVeterinario;
        this.idEspecialidad = idEspecialidad;
    }
    
    // Constructor para solo Especialidad
    public Veterinario(String nombreEspecialidad) {
        this.nombreEspecialidad = nombreEspecialidad;
    }


    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public int getIdVeterinario() {
        return idVeterinario;
    }

    public void setIdVeterinario(int idVeterinario) {
        this.idVeterinario = idVeterinario;
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

    public String getTelefonoVeterinario() {
        return telefonoVeterinario;
    }

    public void setTelefonoVeterinario(String telefonoVeterinario) {
        this.telefonoVeterinario = telefonoVeterinario;
    }
    
  
    public int getIdEspecialidad() {
        return idEspecialidad;
    }

    public void setIdEspecialidad(int idEspecialidad) {
        this.idEspecialidad = idEspecialidad;
    }

    public String getNombreEspecialidad() {
        return nombreEspecialidad;
    }

    public void setNombreEspecialidad(String nombreEspecialidad) {
        this.nombreEspecialidad = nombreEspecialidad;
    }
}