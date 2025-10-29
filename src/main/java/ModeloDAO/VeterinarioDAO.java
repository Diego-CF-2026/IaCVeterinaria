package ModeloDAO; // Asume que esta es la ruta correcta del paquete

import Modelo.Conexion;
import Modelo.Veterinario; // Asegúrate de que esta importación sea correcta y la clase Veterinario exista

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class VeterinarioDAO {

    private static final Logger LOGGER = Logger.getLogger(VeterinarioDAO.class.getName());

    public VeterinarioDAO() {
        // Constructor vacío, puedes añadir inicialización si es necesaria
    }

    public List<Veterinario> listarVeterinarios() {
        List<Veterinario> lista = new ArrayList<>();
        String sql = "SELECT v.idVeterinario, v.nombreVeterinario, v.apellidoVeterinario, "
                   + "v.telefonoVeterinario, v.correoVeterinario, v.idEspecialidad, "
                   + "e.nombreEspecialidad "
                   + "FROM veterinario v "
                   + "INNER JOIN especialidad e ON v.idEspecialidad = e.idEspecialidad "
                   + "ORDER BY v.nombreVeterinario";

        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Veterinario v = new Veterinario();
                v.setIdVeterinario(rs.getInt("idVeterinario"));
                v.setNombreVeterinario(rs.getString("nombreVeterinario"));
                v.setApellidoVeterinario(rs.getString("apellidoVeterinario"));
                v.setTelefonoVeterinario(rs.getString("telefonoVeterinario"));
                //v.setCorreoVeterinario(rs.getString("correoVeterinario"));  se tiene q corregir
                v.setIdEspecialidad(rs.getInt("idEspecialidad"));
                v.setNombreEspecialidad(rs.getString("nombreEspecialidad"));
                lista.add(v);
            }

            System.out.println("Veterinarios encontrados: " + lista.size());

        } catch (SQLException e) {
            System.out.println("Error en listarVeterinarios(): " + e.getMessage());
        }

        return lista;
    }

    public Veterinario obtenerVeterinarioPorId(int id) {
        Veterinario vet = null;
        String sql = "SELECT idVeterinario, nombreVeterinario, apellidoVeterinario, telefonoVeterinario, correoVeterinario, idEspecialidad FROM veterinario WHERE idVeterinario = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = Conexion.getConnection();
            if (conn == null) {
                LOGGER.log(Level.SEVERE, "Error: No se pudo obtener la conexión a la base de datos en obtenerVeterinarioPorId.");
                return null;
            }

            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();

            if (rs.next()) {
                vet = new Veterinario();
                vet.setIdVeterinario(rs.getInt("idVeterinario"));
                vet.setNombreVeterinario(rs.getString("nombreVeterinario")); 
                vet.setApellidoVeterinario(rs.getString("apellidoVeterinario")); 
                vet.setTelefonoVeterinario(rs.getString("telefonoVeterinario"));
                //vet.setCorreoVeterinario(rs.getString("correoVeterinario")); se tiene q corrregir
                vet.setIdEspecialidad(rs.getInt("idEspecialidad"));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error SQL al obtener veterinario por ID: " + e.getMessage(), e);
            e.printStackTrace();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error inesperado al obtener veterinario por ID: " + e.getMessage(), e);
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error al cerrar recursos en obtenerVeterinarioPorId: " + e.getMessage(), e);
            }
        }
        return vet;
    }

    public boolean agregarVeterinario(Veterinario vet) {
        String sql = "INSERT INTO veterinario (nombreVeterinario, apellidoVeterinario, telefonoVeterinario, correoVeterinario, idEspecialidad) VALUES (?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement ps = null;
        boolean exito = false;

        try {
            conn = Conexion.getConnection();
            if (conn == null) {
                LOGGER.log(Level.SEVERE, "Error: No se pudo obtener la conexión a la base de datos en agregarVeterinario.");
                return false;
            }

            ps = conn.prepareStatement(sql);
            ps.setString(1, vet.getNombreVeterinario()); 
            ps.setString(2, vet.getApellidoVeterinario()); 
            ps.setString(3, vet.getTelefonoVeterinario());
            //ps.setString(4, vet.getCorreoVeterinario()); se tiene q corregir
            ps.setInt(5, vet.getIdEspecialidad());

            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas > 0) {
                exito = true;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error SQL al agregar veterinario: " + e.getMessage(), e);
            e.printStackTrace();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error inesperado al agregar veterinario: " + e.getMessage(), e);
            e.printStackTrace();
        } finally {
            try {
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error al cerrar recursos en agregarVeterinario: " + e.getMessage(), e);
            }
        }
        return exito;
    }

    public boolean actualizarVeterinario(Veterinario vet) {
        String sql = "UPDATE veterinario SET nombreVeterinario=?, apellidoVeterinario=?, telefonoVeterinario=?, correoVeterinario=?, idEspecialidad=? WHERE idVeterinario=?";
        Connection conn = null;
        PreparedStatement ps = null;
        boolean exito = false;

        try {
            conn = Conexion.getConnection();
            if (conn == null) {
                LOGGER.log(Level.SEVERE, "Error: No se pudo obtener la conexión a la base de datos en actualizarVeterinario.");
                return false;
            }

            ps = conn.prepareStatement(sql);
            ps.setString(1, vet.getNombreVeterinario()); 
            ps.setString(2, vet.getApellidoVeterinario()); 
            ps.setString(3, vet.getTelefonoVeterinario());
            //ps.setString(4, vet.getCorreoVeterinario()); se tiene q corregir
            ps.setInt(5, vet.getIdEspecialidad());
            ps.setInt(6, vet.getIdVeterinario());

            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas > 0) {
                exito = true;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error SQL al actualizar veterinario: " + e.getMessage(), e);
            e.printStackTrace();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error inesperado al actualizar veterinario: " + e.getMessage(), e);
            e.printStackTrace();
        } finally {
            try {
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error al cerrar recursos en actualizarVeterinario: " + e.getMessage(), e);
            }
        }
        return exito;
    }

    public boolean eliminarVeterinario(int idVeterinario) {
        String sql = "DELETE FROM veterinario WHERE idVeterinario = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        boolean exito = false;

        try {
            conn = Conexion.getConnection();
            if (conn == null) {
                LOGGER.log(Level.SEVERE, "Error: No se pudo obtener la conexión a la base de datos en eliminarVeterinario.");
                return false;
            }

            ps = conn.prepareStatement(sql);
            ps.setInt(1, idVeterinario);

            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas > 0) {
                exito = true;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error SQL al eliminar veterinario: " + e.getMessage(), e);
            e.printStackTrace();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error inesperado al eliminar veterinario: " + e.getMessage(), e);
            e.printStackTrace();
        } finally {
            try {
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error al cerrar recursos en eliminarVeterinario: " + e.getMessage(), e);
            }
        }
        return exito;
    }
}