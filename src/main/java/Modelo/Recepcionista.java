package Modelo;
//pacquete Modelo
//clase recepcionista
public class Recepcionista {
    //variables de recepcionista
    private int idRecepcionista;
    private int idUsuario ;
    private String nombreRecepcionista;
    private String apellidoRecepcionista;
    private String telefonoRecepcionista;
    
    // ✅ CORRECCIÓN CLAVE: Atributo de relación
    private Usuario usuario; 
//constrcutor vacio
    public Recepcionista() {
    }
//constrcutor completo
    // Constructor actualizado para incluir el objeto Usuario (opcional)
    public Recepcionista(int idRecepcionista, int idUsuario, String nombreRecepcionista, 
                         String apellidoRecepcionista, String telefonoRecepcionista, 
                         Usuario usuario) {
        this.idRecepcionista = idRecepcionista;
        this.idUsuario = idUsuario;
        this.nombreRecepcionista = nombreRecepcionista;
        this.apellidoRecepcionista = apellidoRecepcionista;
        this.telefonoRecepcionista = telefonoRecepcionista;
        this.usuario = usuario; // Asignación de la relación
    }

    // --- Getters y Setters EXISTENTES (no modificados) ---
    // ... (idRecepcionista, idUsuario, nombreRecepcionista, etc.) ...
    
    public int getIdRecepcionista() { return idRecepcionista; }
    public void setIdRecepcionista(int idRecepcionista) { this.idRecepcionista = idRecepcionista; }
    
    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }
    
    public String getNombreRecepcionista() { return nombreRecepcionista; }
    public void setNombreRecepcionista(String nombreRecepcionista) { this.nombreRecepcionista = nombreRecepcionista; }
    
    public String getApellidoRecepcionista() { return apellidoRecepcionista; }
    public void setApellidoRecepcionista(String apellidoRecepcionista) { this.apellidoRecepcionista = apellidoRecepcionista; }
    
    public String getTelefonoRecepcionista() { return telefonoRecepcionista; }
    public void setTelefonoRecepcionista(String telefonoRecepcionista) { this.telefonoRecepcionista = telefonoRecepcionista; }


    // MÉTODOS DE RELACIÓN AÑADIDOS (Permiten la inyección de datos)

    /**
     * @return el objeto Usuario asociado a este Recepcionista.
     */
    public Usuario getUsuario() {
        return usuario;
    }

    /**
     * Asigna el objeto Usuario (que contiene correo, estado, etc.) a este Recepcionista.
     * @param usuario el objeto Usuario
     */
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}