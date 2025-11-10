package ModeloDAO;

import Modelo.Conexion;
import Modelo.Especialidad;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EspecialidadDAO {

    private static final Logger LOG = Logger.getLogger(EspecialidadDAO.class.getName());

    // Método interno para mapear los datos de ResultSet a un objeto Especialidad
    private Especialidad map(ResultSet rs) throws SQLException {
        Especialidad e = new Especialidad();
        e.setIdEspecialidad(rs.getInt("idEspecialidad"));
        e.setNombreEspecialidad(rs.getString("nombreEspecialidad"));
        e.setPrecio(rs.getDouble("precio"));
        return e;
    }

    // Método para listar todas las especialidades disponibles
    public List<Especialidad> listar() {
        List<Especialidad> lista = new ArrayList<>();
        String sql1 = "SELECT idEspecialidad, nombreEspecialidad, precio FROM especialidad ORDER BY nombreEspecialidad";
        String sql2 = "SELECT idEspecialidad, nombreEspecialidad, precio FROM Especialidad ORDER BY nombreEspecialidad";

        // Primer intento con tabla en minúsculas (más común en MySQL)
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql1);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) lista.add(map(rs));
            LOG.info("EspecialidadDAO.listar(): filas=" + lista.size() + " usando tabla 'especialidad'");
            return lista;

        } catch (SQLException ex1) {
            // Si falla, intenta con el nombre de tabla con mayúscula inicial
            LOG.log(Level.WARNING, "Fallo con tabla 'especialidad'. Reintentando con 'Especialidad'. Detalle: " + ex1.getMessage());
        }

        // Segundo intento (tabla con mayúscula inicial)
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql2);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) lista.add(map(rs));
            LOG.info("EspecialidadDAO.listar(): filas=" + lista.size() + " usando tabla 'Especialidad'");
            return lista;

        } catch (SQLException ex2) {
            throw new RuntimeException("Error al listar especialidades. Verifica el nombre exacto de la tabla.", ex2);
        }
    }

    // Obtiene una especialidad según su ID
    public Especialidad obtenerPorId(int id) {
        String[] sqls = {
            "SELECT idEspecialidad, nombreEspecialidad, precio FROM especialidad WHERE idEspecialidad=?",
            "SELECT idEspecialidad, nombreEspecialidad, precio FROM Especialidad WHERE idEspecialidad=?"
        };

        for (String sql : sqls) {
            try (Connection con = Conexion.getConnection();
                 PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return map(rs);
                }
                return null;

            } catch (SQLException ex) {
                // Si falla con una variante, prueba la siguiente
            }
        }
        return null;
    }

    // Inserta una nueva especialidad en la base de datos
    public boolean agregar(Especialidad e) {
        String[] sqls = {
            "INSERT INTO especialidad (nombreEspecialidad, precio) VALUES (?, ?)",
            "INSERT INTO Especialidad (nombreEspecialidad, precio) VALUES (?, ?)"
        };

        for (String sql : sqls) {
            try (Connection con = Conexion.getConnection();
                 PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, e.getNombreEspecialidad());
                ps.setDouble(2, e.getPrecio());
                int filas = ps.executeUpdate();
                if (filas > 0) {
                    // Recupera el ID generado automáticamente
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        if (keys.next()) e.setIdEspecialidad(keys.getInt(1));
                    }
                    LOG.info("EspecialidadDAO.agregar(): Insertado -> " + e.getNombreEspecialidad());
                    return true;
                }
            } catch (SQLException ex) {
                // Si falla con una tabla, intenta con la otra
            }
        }
        LOG.warning("EspecialidadDAO.agregar(): No se insertó la especialidad.");
        return false;
    }

    // Actualiza los datos de una especialidad existente
    public boolean actualizar(Especialidad e) {
        String[] sqls = {
            "UPDATE especialidad SET nombreEspecialidad=?, precio=? WHERE idEspecialidad=?",
            "UPDATE Especialidad SET nombreEspecialidad=?, precio=? WHERE idEspecialidad=?"
        };
        for (String sql : sqls) {
            try (Connection con = Conexion.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setString(1, e.getNombreEspecialidad());
                ps.setDouble(2, e.getPrecio());
                ps.setInt(3, e.getIdEspecialidad());
                int filas = ps.executeUpdate();

                if (filas > 0) return true;
            } catch (SQLException ex) {
                // Si falla, continúa con la siguiente variante
            }
        }
        return false;
    }

    // Elimina una especialidad según su ID
    public boolean eliminar(int id) {
        String[] sqls = {
            "DELETE FROM especialidad WHERE idEspecialidad=?",
            "DELETE FROM Especialidad WHERE idEspecialidad=?"
        };
        for (String sql : sqls) {
            try (Connection con = Conexion.getConnection();
                 PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setInt(1, id);
                int filas = ps.executeUpdate();
                if (filas > 0) return true;

            } catch (SQLIntegrityConstraintViolationException fk) {
                // Si hay una restricción de clave foránea, no elimina
                return false;
            } catch (SQLException ex) {
                // Si falla, prueba la otra tabla
            }
        }
        return false;
    }

    // Listado de especialidades visible para el cliente 
    public List<Especialidad> vistaClienteListarEspecialidades() {
        LOG.info("EspecialidadDAO.vistaClienteListarEspecialidades(): Llamando a listar().");
        return listar();
    }

    // Devuelve el precio de una especialidad según su ID 
    public double vistaClienteObtenerPrecioPorEspecialidad(int id) {
        Especialidad especialidad = obtenerPorId(id);
        if (especialidad != null) {
            return especialidad.getPrecio();
        }
        LOG.log(Level.WARNING, "EspecialidadDAO.vistaClienteObtenerPrecioPorEspecialidad(): No se encontró la especialidad ID {0}.", id);
        return 0.0;
    }
}
