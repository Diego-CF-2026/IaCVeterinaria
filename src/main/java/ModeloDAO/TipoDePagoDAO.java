package ModeloDAO;

import Modelo.TipoDePago;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TipoDePagoDAO {

    // ============================================================
    // Atributos y Constructor
    // ============================================================

    // Conexión recibida externamente (mantiene consistencia con otros DAOs)
    private final Connection con;

    public TipoDePagoDAO(Connection con) {
        this.con = con;
    }

    // ============================================================
    // LISTAR TIPOS DE PAGO
    // ============================================================

    // Método para listar todos los tipos de pago registrados en la base de datos
    public List<TipoDePago> listar() {
        List<TipoDePago> lista = new ArrayList<>();
        String sql = "SELECT idPago, nombrePago FROM tipodepago"; // Ajustar nombres según BD

        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            // Recorre los resultados y crea objetos TipoDePago
            while (rs.next()) {
                TipoDePago tp = new TipoDePago(
                    rs.getInt("idPago"),
                    rs.getString("nombrePago")
                );
                lista.add(tp);
            }

        } catch (SQLException e) {
            System.err.println("Error al listar tipos de pago: " + e.getMessage());
        }

        return lista;
    }
}
