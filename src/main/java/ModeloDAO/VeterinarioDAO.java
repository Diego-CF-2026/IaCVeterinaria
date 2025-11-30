package ModeloDAO;

import Modelo.Conexion;
import Modelo.Veterinario;
import Modelo.Cita;
import java.sql.*;
import java.util.*;
import java.sql.Date; // Importante
import java.sql.Time; // Importante
// AGREGAR ESTAS LÍNEAS PARA SHA-256:
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class VeterinarioDAO {

    // ========= LISTAR (JOIN con usuario y especialidad) =========
    public List<Veterinario> listarVeterinarios() {
        List<Veterinario> lista = new ArrayList<>();
        String sql
                = "SELECT v.idVeterinario, v.idUsuario, v.nombreVeterinario, v.apellidoVeterinario, "
                + "       v.telefonoVeterinario, v.idEspecialidad, e.nombreEspecialidad, u.correo "
                + "FROM veterinario v "
                + "JOIN usuario u      ON v.idUsuario = u.idUsuario "
                + "JOIN especialidad e ON v.idEspecialidad = e.idEspecialidad "
                + "ORDER BY v.nombreVeterinario";

        try (Connection con = Conexion.getConnection(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Veterinario v = new Veterinario();
                v.setIdVeterinario(rs.getInt("idVeterinario"));
                v.setIdUsuario(rs.getInt("idUsuario"));
                v.setNombreVeterinario(rs.getString("nombreVeterinario"));
                v.setApellidoVeterinario(rs.getString("apellidoVeterinario"));
                v.setTelefonoVeterinario(rs.getString("telefonoVeterinario"));
                v.setIdEspecialidad(rs.getInt("idEspecialidad"));
                v.setCorreoVeterinario(rs.getString("correo")); // <- para el JSP
                lista.add(v);
            }
        } catch (SQLException e) {
            System.out.println("Error listarVeterinarios(): " + e.getMessage());
        }
        return lista;
    }

    // ========= OBTENER POR ID (JOIN con usuario) =========
    public Veterinario obtenerVeterinarioPorId(int id) {
        String sql
                = "SELECT v.idVeterinario, v.idUsuario, v.nombreVeterinario, v.apellidoVeterinario, "
                + "       v.telefonoVeterinario, v.idEspecialidad, u.correo "
                + "FROM veterinario v JOIN usuario u ON v.idUsuario = u.idUsuario "
                + "WHERE v.idVeterinario = ?";
        try (Connection con = Conexion.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Veterinario v = new Veterinario();
                    v.setIdVeterinario(rs.getInt("idVeterinario"));
                    v.setIdUsuario(rs.getInt("idUsuario"));
                    v.setNombreVeterinario(rs.getString("nombreVeterinario"));
                    v.setApellidoVeterinario(rs.getString("apellidoVeterinario"));
                    v.setTelefonoVeterinario(rs.getString("telefonoVeterinario"));
                    v.setIdEspecialidad(rs.getInt("idEspecialidad"));
                    v.setCorreoVeterinario(rs.getString("correo"));
                    return v;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error obtenerVeterinarioPorId(): " + e.getMessage());
        }
        return null;
    }

    // ========= AGREGAR (TRANSACCIÓN: usuario -> veterinario) =========
    // rol veterinario = 4 (ajusta si tu catálogo de roles es otro)
    public boolean agregarVeterinario(Veterinario vet, String correo, String contraPlano) {
        String sqlUser = "INSERT INTO usuario (idRol, correo, contra, intentos, Estado) VALUES (4, ?, ?, 0, TRUE)";
        String sqlVet = "INSERT INTO veterinario (idUsuario, nombreVeterinario, apellidoVeterinario, telefonoVeterinario, idEspecialidad) "
                + "VALUES (LAST_INSERT_ID(), ?, ?, ?, ?)";

        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = Conexion.getConnection();
            con.setAutoCommit(false);

            String hashed = hashearConSHA256(contraPlano);

            // usuario
            ps = con.prepareStatement(sqlUser);
            ps.setString(1, correo);
            ps.setString(2, hashed);
            ps.executeUpdate();
            ps.close();

            // veterinario
            ps = con.prepareStatement(sqlVet);
            ps.setString(1, vet.getNombreVeterinario());
            ps.setString(2, vet.getApellidoVeterinario());
            ps.setString(3, vet.getTelefonoVeterinario());
            ps.setInt(4, vet.getIdEspecialidad());
            int n = ps.executeUpdate();

            con.commit();
            return n > 0;

        } catch (SQLException e) {
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (SQLException ignore) {
            }
            System.out.println("Error agregarVeterinario(): " + e.getMessage());
            return false;
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (con != null) {
                    con.setAutoCommit(true);
                    con.close();
                }
            } catch (SQLException ignore) {
            }
        }
    }

    // ========= ACTUALIZAR (usuario + veterinario) =========
    public boolean actualizarVeterinario(Veterinario vet, String nuevoCorreo, String nuevaContraPlano) {
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = Conexion.getConnection();
            con.setAutoCommit(false);

            // 1) Obtener idUsuario dueño de este veterinario
            int idUsuario = -1;
            try (PreparedStatement p0 = con.prepareStatement("SELECT idUsuario FROM veterinario WHERE idVeterinario=?")) {
                p0.setInt(1, vet.getIdVeterinario());
                try (ResultSet r0 = p0.executeQuery()) {
                    if (r0.next()) {
                        idUsuario = r0.getInt(1);
                    }
                }
            }
            if (idUsuario <= 0) {
                con.rollback();
                return false;
            }

            // 2) Actualizar usuario (correo y contraseña si llega)
            if (nuevoCorreo != null && !nuevoCorreo.trim().isEmpty()) {
                try (PreparedStatement pu = con.prepareStatement("UPDATE usuario SET correo=? WHERE idUsuario=?")) {
                    pu.setString(1, nuevoCorreo.trim());
                    pu.setInt(2, idUsuario);
                    pu.executeUpdate();
                }
            }
            if (nuevaContraPlano != null && !nuevaContraPlano.trim().isEmpty()) {
                String hashed = hashearConSHA256(nuevaContraPlano);
                try (PreparedStatement pu2 = con.prepareStatement("UPDATE usuario SET contra=? WHERE idUsuario=?")) {
                    pu2.setString(1, hashed);
                    pu2.setInt(2, idUsuario);
                    pu2.executeUpdate();
                }
            }

            // 3) Actualizar veterinario
            String sqlVet = "UPDATE veterinario SET nombreVeterinario=?, apellidoVeterinario=?, telefonoVeterinario=?, idEspecialidad=? "
                    + "WHERE idVeterinario=?";
            ps = con.prepareStatement(sqlVet);
            ps.setString(1, vet.getNombreVeterinario());
            ps.setString(2, vet.getApellidoVeterinario());
            ps.setString(3, vet.getTelefonoVeterinario());
            ps.setInt(4, vet.getIdEspecialidad());
            ps.setInt(5, vet.getIdVeterinario());
            int n = ps.executeUpdate();

            con.commit();
            return n > 0;

        } catch (SQLException e) {
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (SQLException ignore) {
            }
            System.out.println("Error actualizarVeterinario(): " + e.getMessage());
            return false;
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (con != null) {
                    con.setAutoCommit(true);
                    con.close();
                }
            } catch (SQLException ignore) {
            }
        }
    }

    public boolean eliminarVeterinario(int idVeterinario) {
        String sqlGetUsr = "SELECT idUsuario FROM veterinario WHERE idVeterinario=? FOR UPDATE";
        String sqlDelVet = "DELETE FROM veterinario WHERE idVeterinario=?";
        String sqlDelUsr = "DELETE FROM usuario WHERE idUsuario=? AND idRol=4"; // idRol=4 por seguridad

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = Conexion.getConnection();
            if (con == null) {
                System.out.println("[eliminarVeterinario] Conexión nula");
                return false;
            }
            con.setAutoCommit(false);

            // 1) Leer y BLOQUEAR el idUsuario del vet
            int idUsuario = -1;
            ps = con.prepareStatement(sqlGetUsr);
            ps.setInt(1, idVeterinario);
            rs = ps.executeQuery();
            if (rs.next()) {
                idUsuario = rs.getInt(1);
            }
            rs.close();
            ps.close();

            System.out.println("[eliminarVeterinario] idVet=" + idVeterinario + " -> idUsuario=" + idUsuario);
            if (idUsuario <= 0) {
                System.out.println("[eliminarVeterinario] No se encontró idUsuario para el veterinario");
                con.rollback();
                return false;
            }

            // 2) Borrar veterinario
            ps = con.prepareStatement(sqlDelVet);
            ps.setInt(1, idVeterinario);
            int borradosVet = ps.executeUpdate();
            ps.close();
            System.out.println("[eliminarVeterinario] borrados en veterinario=" + borradosVet);

            if (borradosVet == 0) {
                System.out.println("[eliminarVeterinario] No se borró el veterinario (posible inconsistencia)");
                con.rollback();
                return false;
            }

            // 3) Borrar usuario asociado
            ps = con.prepareStatement(sqlDelUsr);
            ps.setInt(1, idUsuario);
            int borradosUsr = ps.executeUpdate();
            ps.close();
            System.out.println("[eliminarVeterinario] borrados en usuario=" + borradosUsr);

            if (borradosUsr == 0) {
                // Si no se borró el usuario, revierte todo para no dejar huerfano
                System.out.println("[eliminarVeterinario] Usuario no borrado. Haciendo rollback.");
                con.rollback();
                return false;
            }

            con.commit();
            return true;

        } catch (SQLException e) {
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (SQLException ignore) {
            }
            System.out.println("[eliminarVeterinario] Error: " + e.getMessage());
            return false;
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (con != null) {
                    con.setAutoCommit(true);
                    con.close();
                }
            } catch (SQLException ignore) {
            }
        }
    }
/**
     * [vistaCliente] Lista los veterinarios disponibles filtrados por especialidad.
     * @param idEspecialidad El ID de la especialidad a filtrar.
     * @return Lista de objetos Veterinario.
     */
    public List<Veterinario> vistaClienteListarPorEspecialidad(int idEspecialidad) {
        List<Veterinario> lista = new ArrayList<>();
        String sql
                = "SELECT v.idVeterinario, v.nombreVeterinario, v.apellidoVeterinario, v.idEspecialidad "
                + "FROM veterinario v "
                // Asumo que solo quieres veterinarios activos o con usuario activo, 
                // pero por simplicidad se filtra solo por la especialidad.
                + "WHERE v.idEspecialidad = ? " 
                + "ORDER BY v.nombreVeterinario";

        try (Connection con = Conexion.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idEspecialidad);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Veterinario v = new Veterinario();
                    v.setIdVeterinario(rs.getInt("idVeterinario"));
                    // Solo cargamos los campos que se necesitan en el select del JSP/AJAX
                    v.setNombreVeterinario(rs.getString("nombreVeterinario"));
                    v.setApellidoVeterinario(rs.getString("apellidoVeterinario"));
                    v.setIdEspecialidad(rs.getInt("idEspecialidad"));
                    lista.add(v);
                }
            }
        } catch (SQLException e) {
            // Usa un logger más robusto en producción.
            System.out.println("Error vistaClienteListarPorEspecialidad(): " + e.getMessage()); 
        }
        return lista;
    }
    
    
    public List<Cita> vistaVeterinarioListarMisCitas(int idVeterinario) {
    List<Cita> lista = new ArrayList<>();

    // Solo se listan las citas con idEstado = 1 (Pendiente)
    String sql = "SELECT c.idCita, CONCAT(cli.nombre, ' ', cli.apellido) AS nombreCliente, cli.dni, "
               + "c.motivo, c.fecha, c.hora, c.idEstado "
               + "FROM citas c "
               + "INNER JOIN cliente cli ON c.idCliente = cli.idCliente "
               + "WHERE c.idVeterinario = ? AND c.idEstado = 1 "
               + "ORDER BY c.fecha ASC, c.hora ASC";

    try (Connection con = Conexion.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setInt(1, idVeterinario);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            Cita c = new Cita();
            c.setIdCita(rs.getInt("idCita"));
            c.setNombreCliente(rs.getString("nombreCliente"));
            c.setDniCliente(rs.getString("dni"));
            c.setMotivo(rs.getString("motivo"));
            c.setFecha(rs.getDate("fecha"));
            c.setHora(rs.getTime("hora"));
            c.setIdEstado(rs.getInt("idEstado"));
            lista.add(c);
        }

    } catch (SQLException e) {
        System.out.println("Error vistaVeterinarioListarMisCitas(): " + e.getMessage());
    }
    return lista;
}

    // =========================================================================
    // 2️⃣ REGISTRAR TRATAMIENTO Y COMPLETAR CITA (MEJORADO CON TRANSACCIÓN)
    // =========================================================================
   public boolean registrarTratamientoCompletarCita(
        int idCita, String diagnosticoFinal, String tratamientoFinal, String notas, String dniCliente) {

    boolean resultado = false;

    // Se asume que el DNI ya fue verificado en el Servlet antes de llamar a este método.
    if (dniCliente == null || dniCliente.isEmpty()) {
        System.out.println("Error: DNI del cliente nulo o vacío en el DAO.");
        return false;
    }

    String sqlInsertTrat = "INSERT INTO tratamientomedico (idCita, diagnostico, tratamiento, notas, dniCliente) "
                + "VALUES (?, ?, ?, ?, ?)";
    // idEstado = 2 es 'Completado'
    String sqlUpdateCita = "UPDATE citas SET idEstado = 2 WHERE idCita = ?"; 

    try (Connection con = Conexion.getConnection();
         PreparedStatement psTrat = con.prepareStatement(sqlInsertTrat);
         PreparedStatement psCita = con.prepareStatement(sqlUpdateCita)) {

        con.setAutoCommit(false); // Inicia la transacción

        // 1. Inserción en tratamientomedico
        psTrat.setInt(1, idCita);
        // Usa las cadenas finales preparadas en el Servlet
        psTrat.setString(2, diagnosticoFinal); 
        psTrat.setString(3, tratamientoFinal); 
        psTrat.setString(4, notas);
        psTrat.setString(5, dniCliente);

        int filasTrat = psTrat.executeUpdate();

        // 2. Actualización de citas
        psCita.setInt(1, idCita);
        int filasCita = psCita.executeUpdate();

        if (filasTrat > 0 && filasCita > 0) {
            con.commit(); // Confirma si ambas operaciones son exitosas
            resultado = true;
        } else {
            con.rollback(); // Deshace si alguna falla
        }
        
        con.setAutoCommit(true); // Restaura el modo de autocommit

    } catch (SQLException e) {
        System.out.println("Error registrarTratamientoCompletarCita(): " + e.getMessage());
    }

    return resultado;
}
    // =========================================================================
    // 3️⃣ REPROGRAMAR CITA (CORREGIDO ID ESTADO)
    // =========================================================================
    public boolean reprogramarCita(int idCita, Date nuevaFecha, Time nuevaHora) {
        boolean exito = false;
        // CORRECCIÓN: Se usa idEstado = 1 ('Pendiente') para reprogramar, no 3 ('Cancelado')
        String sql = "UPDATE citas SET fecha = ?, hora = ?, idEstado = 1 WHERE idCita = ?"; 

        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDate(1, nuevaFecha);
            ps.setTime(2, nuevaHora);
            ps.setInt(3, idCita);

            exito = ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error reprogramarCita(): " + e.getMessage());
        }

        return exito;
    }

    // =========================================================================
    // 4️⃣ OBTENER DNI POR ID DE CITA
    // =========================================================================
    public String obtenerDniPorCita(int idCita) {
        String dni = null;
        String sql = "SELECT cli.dni FROM citas c "
                    + "INNER JOIN cliente cli ON c.idCliente = cli.idCliente "
                    + "WHERE c.idCita = ?";

        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idCita);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    dni = rs.getString("dni");
                }
            }

        } catch (SQLException e) {
            System.out.println("Error obtenerDniPorCita(): " + e.getMessage());
        }

        return dni;
    }
    
    /**
     * Obtiene el ID del veterinario asociado a un ID de usuario específico.
     * @param idUsuario El ID del usuario (de la tabla 'usuario').
     * @return El idVeterinario (Integer) o null si no se encuentra.
     */
    public Integer obtenerIdVeterinarioPorIdUsuario(int idUsuario) {
        Integer idVeterinario = null;
        
        // La consulta busca el idVeterinario en la tabla 'veterinario' usando el idUsuario
        String sql = "SELECT idVeterinario FROM veterinario WHERE idUsuario = ?";

        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // El método devuelve un Integer (objeto), por lo que puede ser null.
                    // Esto permite al LoginServlet manejar el caso en que no haya un veterinario asociado.
                    idVeterinario = rs.getInt("idVeterinario");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error obtenerIdVeterinarioPorIdUsuario(): " + e.getMessage());
            // En caso de error de BD, devolvemos null
            return null; 
        }

        return idVeterinario;
    }
    
        public List<Modelo.Veterinario> listarVeterinariosParaDropdown() {
        List<Modelo.Veterinario> lista = new ArrayList<>();

        // 🔴 SQL CORREGIDO: Usando 'nombreVeterinario' y 'apellidoVeterinario'
        String sql = "SELECT idVeterinario, nombreVeterinario, apellidoVeterinario FROM veterinario ORDER BY nombreVeterinario"; 

        try (Connection con = Conexion.getConnection(); 
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Modelo.Veterinario veterinario = new Modelo.Veterinario();
                veterinario.setIdVeterinario(rs.getInt("idVeterinario"));

                // 🟢 Mapeo a los nombres de columna correctos
                veterinario.setNombreVeterinario(rs.getString("nombreVeterinario"));
                veterinario.setApellidoVeterinario(rs.getString("apellidoVeterinario"));

                lista.add(veterinario);
            }
            System.out.println("DEBUG: Veterinarios cargados para Dropdown: " + lista.size() + " registros.");

        } catch (SQLException e) {
            System.err.println("❌ Error SQL al listar veterinarios para dropdown: " + e.getMessage());
            e.printStackTrace(); 
        }
        return lista;
    }
        
    // ============================================================
    // FUNCIÓN AUXILIAR SHA-256
    // ============================================================
    private String hashearConSHA256(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] messageDigest = md.digest(input.getBytes());
            BigInteger no = new BigInteger(1, messageDigest);
            String hashtext = no.toString(16);

            // Rellenar con ceros a la izquierda para asegurar 64 caracteres
            while (hashtext.length() < 64) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error al generar el hash SHA-256", e);
        }
    }
}