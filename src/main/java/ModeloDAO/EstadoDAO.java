package ModeloDAO;

import Modelo.Conexion;
import Modelo.Estado;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EstadoDAO {

    private static final Logger LOGGER = Logger.getLogger(EstadoDAO.class.getName());

    private Connection con;
    private PreparedStatement ps;
    private ResultSet rs;

    // Método auxiliar para listar todos los estados
    public List<Estado> listarEstados() {
        List<Estado> lista = new ArrayList<>();
        String sql = "SELECT idEstado, tipoEstado FROM estado ORDER BY idEstado ASC";

        try {
            con = Conexion.getConnection();
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                Estado e = new Estado();
                e.setIdEstado(rs.getInt("idEstado"));
                e.setTipoEstado(rs.getString("tipoEstado"));
                lista.add(e);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "❌ ERROR SQL al listar estados.", e);
        } finally {
            closeResources();
        }
        return lista;
    }
    
    // Método auxiliar para obtener el ID de un estado específico (ej. "Pendiente")
    // Útil para la creación de citas donde el estado inicial es fijo.
    public int obtenerIdEstadoPorNombre(String nombreEstado) {
        String sql = "SELECT idEstado FROM estado WHERE tipoEstado = ?";
        int id = -1;

        try {
            con = Conexion.getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, nombreEstado);
            rs = ps.executeQuery();

            if (rs.next()) {
                id = rs.getInt("idEstado");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "❌ ERROR SQL al obtener ID de estado.", e);
        } finally {
            closeResources();
        }
        return id;
    }

    private void closeResources() {
        try {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
            if (con != null) con.close();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al cerrar recursos de conexión en EstadoDAO.", e);
        }
    }
}