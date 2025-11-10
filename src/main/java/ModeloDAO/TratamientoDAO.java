package ModeloDAO;

import Modelo.Conexion;
import Modelo.Tratamiento;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TratamientoDAO {

    // ============================================================
    // LISTAR TRATAMIENTOS POR DNI
    // ============================================================

    // Método que obtiene todos los tratamientos asociados a un cliente según su DNI.
    // Incluye diagnóstico, tratamiento, fecha de registro y DNI.
    public List<Tratamiento> listarTratamientosPorDni(String dni) {
        List<Tratamiento> lista = new ArrayList<>();

        // Consulta SQL: obtiene los tratamientos junto con la fecha de la cita
        String sql = "SELECT "
                   + "t.idTratamiento, "
                   + "t.idCita, "
                   + "t.diagnostico, "
                   + "t.tratamiento, "
                   + "t.dniCliente, "
                   + "c.fecha AS fechaRegistro "
                   + "FROM tratamientomedico t "
                   + "INNER JOIN citas c ON t.idCita = c.idCita "
                   + "WHERE t.dniCliente = ? "
                   + "ORDER BY c.fecha DESC";

        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, dni);

            try (ResultSet rs = ps.executeQuery()) {
                // Recorrer resultados y llenar la lista con objetos Tratamiento
                while (rs.next()) {
                    Tratamiento t = new Tratamiento();
                    t.setIdTratamiento(rs.getInt("idTratamiento"));
                    t.setIdCita(rs.getInt("idCita"));
                    t.setDiagnostico(rs.getString("diagnostico"));
                    t.setTratamiento(rs.getString("tratamiento"));
                    t.setDniCliente(rs.getString("dniCliente"));
                    t.setFechaRegistro(rs.getDate("fechaRegistro"));
                    lista.add(t);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al listar tratamientos por DNI: " + e.getMessage());
        }

        return lista;
    }
}
