package ModeloDAO;

import Modelo.Conexion;
import Modelo.Veterinario;
import java.sql.*;
import java.util.*;
import org.mindrot.jbcrypt.BCrypt;

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

            String hashed = BCrypt.hashpw(contraPlano, BCrypt.gensalt());

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
                String hashed = BCrypt.hashpw(nuevaContraPlano, BCrypt.gensalt());
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

}
