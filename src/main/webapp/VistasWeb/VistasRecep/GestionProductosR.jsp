<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ include file="/proteger.jsp" %>
<%@page import="java.util.List"%>
<%@page import="Modelo.Carrito"%>
<%@page import="Modelo.DetalleCarrito"%>
<%@page import="Modelo.Producto"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.List"%>


<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <title>Veterinaria Santa Cruz</title>
        <link href="https://cdn.jsdelivr.net/npm/boxicons@2.1.1/css/boxicons.min.css" rel="stylesheet">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/ModoNoche-Sidebar.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/ProductosCards.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/ProductosR.css">
    </head>

    <body>
        <nav class="sidebar">
            <header>
                <div class="image-text">
                    <span class="image">
                        <img id="logoAdmin" src="<%= request.getContextPath()%>/Recursos/Logo.png" alt="Logo" class="logo">
                    </span>
                    <div class="header-text">
                        <span class="name">Recepcionista</span>
                        <span class="profession">Veterinaria Santa Cruz</span>
                    </div>
                </div>
            </header>

            <div class="menu-bar">
                <ul class="menu-links">
                    <li class="nav-link">
                        <a href="<%= request.getContextPath()%>/VistasWeb/VistasRecep/RecepDash.jsp">
                            <i class='bx bx-home-alt icon'></i><span class="text">General</span>
                        </a>
                    </li>
                    <li class="nav-link">
                        <a href="${pageContext.request.contextPath}/CitaServlet">
                            <i class='bx bx-calendar-check icon'></i><span class="text">Citas</span>
                        </a>
                    </li>
                    <li class="nav-link">
                        <a href="${pageContext.request.contextPath}/ProductoRecepServlet">
                            <i class='bx bx-package icon'></i><span class="text">Productos</span></a>
                    </li> 
                    <li class="nav-link">
                        <a href="<%= request.getContextPath()%>/LogoutServlet">
                            <i class='bx bx-log-out icon'></i><span class="text">Salir</span>
                        </a>
                    </li>
                </ul>
            </div>

        </nav>
                            
        <div class="contenedor">
            <h2>Gestión de Carritos</h2>

            <%
                List<Carrito> listaCarritos = (List<Carrito>) request.getAttribute("listaCarritos");
                if (listaCarritos == null || listaCarritos.isEmpty()) {
            %>
                <p>No hay carritos registrados.</p>
            <%
                } else {
            %>
            <table>
                <thead>
                    <tr>
                        <th>Nombre Cliente</th>
                        <th>Total</th>
                        <th>Estado</th>
                        <th>Fecha</th>
                        <th>Estado de Entrega</th>
                        <th>Detalles</th>
                        <th>Acciones</th>
                    </tr>
                </thead>
                <tbody>
                    <%
                        for (Carrito c : listaCarritos) {
                    %>
                    <tr>
                        <td><%= c.getCliente().getNombre() %> <%= c.getCliente().getApellido() %></td>
                        <td>S/ <%= c.getTotal() %></td>
                        <td class="<%= c.getEstado().equals("CERRADO") ? "estado-cerrado" : "estado-abierto" %>"><%= c.getEstado() %></td>
                        <td>
                            <%
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                                String fechaFormateada = sdf.format(c.getFecha());
                            %>
                            <%= fechaFormateada %>
                        </td>
                        
                        <td>
                            <%= c.getEstadoEntrega() != null ? c.getEstadoEntrega() : "EN PROCESO" %>
                        </td>
                        
                        <td>
                            <div class="detalles">
                                <% for (DetalleCarrito d : c.getDetalles()) { %>
                                    <div class="detalle-producto">
                                        <div>
                                            <strong><%= d.getProducto().getNombreProducto() %></strong><br>
                                            Cantidad: <%= d.getCantidadProducto() %> — Precio: S/ <%= d.getProducto().getPrecio() %>
                                        </div>
                                    </div>
                                <% } %>
                            </div>
                        </td>

                        <td>
                            <form action="ProductoRecepServlet" method="post" class="estado-form">
                                <input type="hidden" name="accion" value="actualizarEntrega">
                                <input type="hidden" name="idCarrito" value="<%= c.getIdCarrito() %>">

                                <select name="estadoEntrega">
                                    <option value="EN PROCESO" <%= "EN PROCESO".equals(c.getEstadoEntrega()) ? "selected" : "" %>>🟡 En proceso</option>
                                    <option value="ENTREGADO" <%= "ENTREGADO".equals(c.getEstadoEntrega()) ? "selected" : "" %>>🟢 Entregado</option>
                                </select>

                                <button type="submit" title="Actualizar estado">
                                    <i class='bx bx-refresh'></i> Actualizar
                                </button>
                            </form>

                            <div class="estado-entrega <%= "ENTREGADO".equals(c.getEstadoEntrega()) ? "entregado" : "proceso" %>">
                                <%= c.getEstadoEntrega() != null ? c.getEstadoEntrega() : "EN PROCESO" %>
                            </div>
                        </td>
                    </tr>
                    <% } %>
                </tbody>
            </table>
            <%
                }
            %>
        </div>                
                            

        <button id="modoNocheBtn" class="modo-noche-flotante" aria-label="Cambiar a modo noche">🌙</button>

        <script src="<%= request.getContextPath()%>/Js/JsAdmin/ModoNoche-Sidebar.js"></script>
    </body>
</html>
