package Modelo;

public class Recepcionista {
    private int idRecepcionista;
    private int idUsuario ;
    private String nombreRecepcionista;
    private String apellidoRecepcionista;
    private String telefonoRecepcionista;

    public Recepcionista() {
    }

    public Recepcionista(int idRecepcionista, int idUsuario, String nombreRecepcionista, String apellidoRecepcionista, String telefonoRecepcionista) {
        this.idRecepcionista = idRecepcionista;
        this.idUsuario = idUsuario;
        this.nombreRecepcionista = nombreRecepcionista;
        this.apellidoRecepcionista = apellidoRecepcionista;
        this.telefonoRecepcionista = telefonoRecepcionista;
    }

    public int getIdRecepcionista() {
        return idRecepcionista;
    }

    public void setIdRecepcionista(int idRecepcionista) {
        this.idRecepcionista = idRecepcionista;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombreRecepcionista() {
        return nombreRecepcionista;
    }

    public void setNombreRecepcionista(String nombreRecepcionista) {
        this.nombreRecepcionista = nombreRecepcionista;
    }

    public String getApellidoRecepcionista() {
        return apellidoRecepcionista;
    }

    public void setApellidoRecepcionista(String apellidoRecepcionista) {
        this.apellidoRecepcionista = apellidoRecepcionista;
    }

    public String getTelefonoRecepcionista() {
        return telefonoRecepcionista;
    }

    public void setTelefonoRecepcionista(String telefonoRecepcionista) {
        this.telefonoRecepcionista = telefonoRecepcionista;
    }
   
}