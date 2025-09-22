/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ModeloDAO;

import Modelo.TipoDePago;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author kristhor
 */
public class TipoDePagoDAO {

    private final Connection con;

    // Constructor que recibe la conexión (coherente con tus otros DAOs)
    public TipoDePagoDAO(Connection con) {
        this.con = con;
    }

    // Lista todos los tipos de pago
    public List<TipoDePago> listar() {
        List<TipoDePago> lista = new ArrayList<>();
        String sql = "SELECT idPago, nombrePago FROM tipodepago"; // ajusta nombres si difieren

        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                // Usa el constructor que tienes en Modelo.TipoDePago
                TipoDePago tp = new TipoDePago(rs.getInt("idPago"), rs.getString("nombrePago"));
                lista.add(tp);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }
}
