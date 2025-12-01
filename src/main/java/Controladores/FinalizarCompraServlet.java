/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controladores;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@WebServlet("/FinalizarCompraServlet")
public class FinalizarCompraServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String producto = request.getParameter("producto");  // 🔹 Obtener el parámetro "producto" enviado desde el formulario del cliente

        HttpSession session = request.getSession();  // 🔹 Obtener la sesión actual del usuario; si no existe, se crea automáticamente


        List<String> historial = (List<String>) session.getAttribute("historialCompras");   // 🔹 Recuperar el historial de compras almacenado en sesión
        if (historial == null) {                                                                     // Si no existe aún, se crea un nuevo ArrayList para almacenar los productos
            historial = new ArrayList<>();
        }

        historial.add(producto);                 // 🔹 Agregar el producto actual al historial de compras

        session.setAttribute("historialCompras", historial);        // 🔹 Actualizar el atributo de sesión para mantener el historial actualizado

        response.sendRedirect(request.getContextPath() + "/VistasWeb/VistasCliente/historialdecompras.jsp");          // 🔹 Redirigir al cliente a la página que muestra su historial de compras
        
    }
}
