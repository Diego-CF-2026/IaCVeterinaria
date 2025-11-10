<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ include file="/proteger.jsp" %>
<%@page import="java.util.List"%>
<%@page import="Modelo.Carrito"%>
<%@page import="Modelo.DetalleCarrito"%>
<%@page import="Modelo.Producto"%>

<%@page import="java.util.List"%>


<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <title>Veterinaria Santa Cruz</title>
        <link href="https://cdn.jsdelivr.net/npm/boxicons@2.1.1/css/boxicons.min.css" rel="stylesheet">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/ModoNoche-Sidebar.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/ProductosCards.css">
        <style>
            .modal {
                display: none;
                position: fixed;
                z-index: 9999;
                left: 0;
                top: 0;
                width: 100%;
                height: 100%;
                background-color: rgba(0,0,0,0.4);
                justify-content: center;
                align-items: center;
            }
            .modal-content {
                background: white;
                padding: 20px;
                border-radius: 6px;
                max-width: 600px;
                width: 90%;
                position: relative;
            }
            .close-button {
                position: absolute;
                top: 10px;
                right: 15px;
                font-size: 20px;
                cursor: pointer;
            }
            
            .contenedor {
                margin-left: 270px;
                padding: 30px;
                min-height: 100vh;
                font-family: "Poppins", sans-serif;
            }

            table {
                width: 100%;
                border-collapse: collapse;
                margin-top: 20px;
                border-radius: 10px;
                overflow: hidden;
                box-shadow: 0 2px 6px rgba(0,0,0,0.1);
            }

            th, td {
                padding: 12px 15px;
                border-bottom: 1px solid #eee;
                text-align: center; 
                vertical-align: middle; 
            }

            th {
                background-color: #4a90e2;
                font-weight: 600;
                color: white; 
            }

            tr:hover {
                background-color: gray;
            }

            .estado-cerrado {
                color: #28a745;
                font-weight: bold;
            }

            .estado-abierto {
                color: #dc3545;
                font-weight: bold;
            }

            .detalles {
                display: flex;
                flex-direction: column; 
                gap: 8px;
                max-height: 180px; 
                overflow-y: auto;  
                padding: 8px;
                border-radius: 8px;
                border: 1px solid #ddd;
            }

            .detalle-producto {
                display: flex;
                align-items: center;
                justify-content: space-between;
                border: 1px solid #eee;
                border-radius: 6px;
                padding: 8px 12px;
                font-size: 14px;
                box-shadow: 0 1px 3px rgba(0,0,0,0.05);
            }

            .detalle-producto strong {
                color: #333;
            }

            .detalle-producto img {
                width: 45px;
                height: 45px;
                object-fit: cover;
                border-radius: 5px;
                border: 1px solid #ccc;
                margin-right: 10px;
            }
            
            form.estado-form {
                display: flex;
                align-items: center;
                gap: 10px;
                justify-content: center;
            }

            form.estado-form select {
                padding: 6px 10px;
                border: 1px solid #ccc;
                border-radius: 6px;
                font-size: 14px;
                transition: all 0.2s ease;
            }

            form.estado-form select:hover {
                border-color: #4a90e2;
            }

            form.estado-form button {
                background-color: #4a90e2;
                border: none;
                color: white;
                padding: 6px 12px;
                border-radius: 6px;
                cursor: pointer;
                font-size: 14px;
                transition: background-color 0.2s ease, transform 0.1s ease;
            }

            form.estado-form button:hover {
                background-color: #357ab8;
                transform: scale(1.05);
            }

            .estado-entrega {
                padding: 4px 8px;
                border-radius: 5px;
                font-weight: 600;
                text-transform: uppercase;
                font-size: 13px;
                display: inline-block;
            }

            .estado-entrega.proceso {
                background-color: #ffecb3;
                color: #b68900;
            }

            .estado-entrega.entregado {
                background-color: #c8f7c5;
                color: #1b5e20;
            }
            
        </style>
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
                        <th>ID Cliente</th>
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
                        <td><%= c.getFecha() %></td>
                        
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
