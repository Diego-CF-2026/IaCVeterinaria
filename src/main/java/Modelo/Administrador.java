package Modelo;

public class Administrador {
    private int idAdmin;
    private int idUsuario;
    private String nombreAdmin;
    private String apellidoAdmin;


    // Constructor vacío
    public Administrador() {
    }

    public Administrador(int idAdmin, int idUsuario, String nombre, String apellido) {
        this.idAdmin = idAdmin;
        this.idUsuario = idUsuario;
        this.nombreAdmin = nombre;
        this.apellidoAdmin = apellido;
    }

    public int getIdAdmin() {
        return idAdmin;
    }

    public void setIdAdmin(int idAdmin) {
        this.idAdmin = idAdmin;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombre() {
        return nombreAdmin;
    }

    public void setNombre(String nombre) {
        this.nombreAdmin = nombre;
    }

    public String getApellido() {
        return apellidoAdmin;
    }

    public void setApellido(String apellido) {
        this.apellidoAdmin = apellido;
    }



    
}
