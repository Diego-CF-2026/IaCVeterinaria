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

    private Especialidad map(ResultSet rs) throws SQLException {
        Especialidad e = new Especialidad();
        e.setIdEspecialidad(rs.getInt("idEspecialidad"));
        e.setNombreEspecialidad(rs.getString("nombreEspecialidad"));
        e.setPrecio(rs.getDouble("precio"));
        return e;
    }

    /** Intenta con 'especialidad' y, si falla por nombre de tabla/case, intenta 'Especialidad'. */
    public List<Especialidad> listar() {
        List<Especialidad> lista = new ArrayList<>();
        String sql1 = "SELECT idEspecialidad, nombreEspecialidad, precio FROM especialidad ORDER BY nombreEspecialidad";
        String sql2 = "SELECT idEspecialidad, nombreEspecialidad, precio FROM Especialidad ORDER BY nombreEspecialidad";

        // primer intento (tabla en minúscula, común en MySQL)
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql1);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) lista.add(map(rs));
            LOG.info("EspecialidadDAO.listar(): filas=" + lista.size() + " usando tabla 'especialidad'");
            return lista;

        } catch (SQLException ex1) {
            LOG.log(Level.WARNING, "Fallo con tabla 'especialidad' (puede ser por nombre/case). Reintentando con 'Especialidad'. Detalle: " + ex1.getMessage());
        }

        // segundo intento (tabla con mayúscula inicial)
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql2);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) lista.add(map(rs));
            LOG.info("EspecialidadDAO.listar(): filas=" + lista.size() + " usando tabla 'Especialidad'");
            return lista;

        } catch (SQLException ex2) {
            throw new RuntimeException("Error al listar especialidades (verifica el nombre exacto de la tabla).", ex2);
        }
    }

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
                // intenta el siguiente sql
            }
        }
        return null;
    }

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
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        if (keys.next()) e.setIdEspecialidad(keys.getInt(1));
                    }
                    LOG.info("EspecialidadDAO.agregar(): OK -> " + e.getNombreEspecialidad());
                    return true;
                }
            } catch (SQLException ex) {
                // intenta el siguiente sql (posible diferencia de nombre de tabla)
            }
        }
        LOG.warning("EspecialidadDAO.agregar(): no se insertó la especialidad.");
        return false;
    }

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
                // intenta siguiente variante
            }
        }
        return false;
    }

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
                return false;
            } catch (SQLException ex) {
                // intenta siguiente variante
            }
        }
        return false;
    }
}
