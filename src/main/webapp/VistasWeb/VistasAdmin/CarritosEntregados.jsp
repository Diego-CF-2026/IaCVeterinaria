<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*, Modelo.Carrito" %>
<%@page import="java.text.SimpleDateFormat"%>

<!DOCTYPE html>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>VeterinariaSantaCruz</title>
        <link href="https://unpkg.com/boxicons@2.1.1/css/boxicons.min.css" rel="stylesheet">
        <link rel="stylesheet" href="<%= request.getContextPath() %>/css/ModoNoche-Sidebar.css"> 
        <link rel="stylesheet" href="<%= request.getContextPath() %>/css/CarritosEntregados.css"> 
    </head>
    <body>
        <nav class="sidebar">
            <header>
                <div class="image-text">
                    <span class="image">
                        <img id="logoAdmin" src="<%= request.getContextPath()%>/Recursos/Logo.png" alt="Logo de Veterinaria Santa Cruz" class="logo">
                    </span>
                    <div class="header-text">
                        <span class="name">Administrador</span>
                        <span class="profession">Veterinaria Santa Cruz</span>
                    </div>
                </div>
            </header>

            <div class="menu-bar">
                <ul class="menu-links">
                    <li class="nav-link ">
                        <a href="<%= request.getContextPath()%>/VistasWeb/VistasAdmin/AdminDash.jsp">
                            <i class='bx bx-home-alt icon'></i><span class="text">General</span>
                        </a>
                    </li>
                    <li class="nav-link">
                            <a href="<%= request.getContextPath()%>/ReporteGananciasServlet">
                            <i class='bx bx-bar-chart-alt-2 icon'></i><span class="text">Ganancias de Citas</span>
                        </a>
                    </li>
                    <li class="nav-link">
                        <a href="<%= request.getContextPath()%>/AdminEmpleadoServlet"><i class='bx bx-group icon'></i><span class="text">Veterinarios</span></a>
                    </li>
                    <li class="nav-link">
                        <a href="<%= request.getContextPath()%>/AdminRecepServlet?accion=listar">
                            <i class='bx bx-user-check icon'></i> <span class="text">Recepcionistas</span>
                        </a>
                    </li>
                    <li class="nav-link">
                        <a href="<%= request.getContextPath()%>/ProductoServlet?accion=listar&idProveedor=1">
                            <i class='bx bx-package icon'></i><span class="text">Productos</span>
                        </a>
                    </li>
                    <li class="nav-link">
                        <a href="<%= request.getContextPath()%>/ProveedorServlet?accion=listar">
                            <i class='bx bx-store icon'></i><span class="text">Proveedores</span>
                        </a>
                    </li>
                    <li class="nav-link">
                        <a href="<%= request.getContextPath() %>/ProductoRecepServlet?accion=listarEntregados">
                            <i class='bx bx-check-circle icon'></i><span class="text">Productos Entregados</span>
                        </a>
                    </li>
                    <li class="nav-link">
                        <a href="#"><i class='bx bx-cog icon'></i><span class="text">Ajustes</span></a>
                    </li>
                    <li class="nav-link">
                        <a href="<%= request.getContextPath()%>/LogoutServlet">
                            <i class='bx bx-log-out icon'></i><span class="text">Salir</span>
                        </a>
                    </li>

                </ul>
            </div>
        </nav>
                            
        <div class="title-container">
            <h2>Carritos con Productos ENTREGADOS</h2>
        </div>

        <div class="table-container">
            <table>
                <thead>
                <tr>
                    <th>Cliente</th>
                    <th>Total</th>
                    <th>Fecha</th>
                    <th>Estado Entrega</th>
                </tr>
                </thead>

                <tbody>
                <%
                    List<Carrito> lista = (List<Carrito>) request.getAttribute("listaCarritos");
                    if (lista != null && !lista.isEmpty()) {
                        for (Carrito c : lista) {
                %>
                    <tr>
                        <td><%= c.getCliente().getNombre() %> <%= c.getCliente().getApellido() %></td>
                        <td>S/ <%= c.getTotal() %></td>
                                                <td>
                            <%
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                                String fechaFormateada = sdf.format(c.getFecha());
                            %>
                            <%= fechaFormateada %>
                        </td>
                        <td><span class="ok"><%= c.getEstadoEntrega() %></span></td>
                    </tr>
                <%      }
                    } else {
                %>
                    <tr><td colspan="5" style="text-align:center;">No hay carritos entregados.</td></tr>
                <% } %>
                </tbody>
            </table>
        </div>                   
                            
                            
        <button id="modoNocheBtn" class="modo-noche-flotante" aria-label="Cambiar a modo noche">?</button>
        <script src="<%= request.getContextPath()%>/Js/JsAdmin/ModoNoche-Sidebar.js"></script>
    </body>
</html>
