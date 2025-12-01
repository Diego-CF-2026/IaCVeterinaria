<%@ include file="/proteger.jsp" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="Modelo.Proveedor" %>
<%@ page import="Modelo.ContactoProveedor" %>

<%    List<Proveedor> listaProveedores = (List<Proveedor>) request.getAttribute("listaProveedores");
    Proveedor proveedorSel = (Proveedor) request.getAttribute("proveedorSeleccionado");
    String vista = (String) request.getAttribute("vista");
    boolean mostrarInactivos = "inactivos".equals(vista);

    if (listaProveedores == null) {
        response.sendRedirect(request.getContextPath() + "/ProveedorServlet?accion=listar");
        return;
    }
%>

<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Veterinaria Santa Cruz</title>
        <link href="https://unpkg.com/boxicons@2.1.1/css/boxicons.min.css" rel="stylesheet">
        <link rel="stylesheet" href="<%= request.getContextPath()%>/css/ModoNoche-Sidebar.css">
        <link rel="stylesheet" href="<%= request.getContextPath()%>/css/GestorProveedores.css">
        <link href='https://unpkg.com/boxicons@2.1.4/css/boxicons.min.css' rel='stylesheet'>

        <link href='https://cdn.jsdelivr.net/npm/boxicons@2.1.4/css/boxicons.min.css' rel='stylesheet'>
        <style>
            
            
            
        </style>
       
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
                        <a href="<%= request.getContextPath()%>/carritoservletAdmin">
                            <i class='bx bx-check-circle icon'></i><span class="text">Productos Entregados</span>
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

        <main>
            <div class="header-actions">
                <h1>Gestión de Proveedores</h1>
                <div class="acciones">
                    <form method="get" action="ProveedorServlet">
                        <input type="hidden" name="accion" value="<%= mostrarInactivos ? "listarInactivos" : "listar"%>" />
                        <input type="text" name="busqueda" placeholder="Buscar proveedor..." value="<%= request.getParameter("busqueda") != null ? request.getParameter("busqueda") : ""%>" />
                        <button type="submit">Buscar</button>
                    </form>
                    <% if (!mostrarInactivos) { %>
                    <button class="btn btn-agregar" onclick="abrirModal('modalAgregarProveedor')">Agregar Proveedor</button>
                    <a href="ProveedorServlet?accion=listarInactivos" class="btn btn-secundario">Ver Inactivos</a>
                    <% } else { %>
                    <a href="ProveedorServlet?accion=listar" class="btn btn-secundario">Ver Activos</a>
                    <% } %>
                </div>
            </div>

            <table>
                <thead>
                    <tr>
                 
                        <th>Razón Social</th>
                        <th>RUC</th>
                        <th>Dirección</th>
                        <th>Correo</th>
                        <th>Estado</th>
                        <th>Detalles</th>
                    </tr>
                </thead>
                <tbody>
                    <% if (listaProveedores != null && !listaProveedores.isEmpty()) {
                            for (Proveedor p : listaProveedores) {%>
                    <tr>
                   
                        <td><%= p.getRazonSocial()%></td>
                        <td><%= p.getRuc()%></td>
                        <td><%= p.getDireccion()%></td>
                        <td><%= p.getCorreo()%></td>
                        <td><%= (p.getEstado() == 1 ? "Activo" : "Inactivo")%></td>
                        <td>
                            <% if (p.getEstado() == 1) {%>
                            <a href="ProveedorServlet?accion=ver&id=<%= p.getIdProveedor()%>">Ver Detalles</a>
                            <% } else {%>
                            <form action="ProveedorServlet" method="post" style="display:inline;" onsubmit="return confirmarCambioEstado(<%= p.getEstado()%>)">
                                <input type="hidden" name="accion" value="cambiarEstado" />
                                <input type="hidden" name="id" value="<%= p.getIdProveedor()%>" />
                                <input type="hidden" name="estado" value="<%= p.getEstado()%>" />
                                <button class="btn btn-secundario" type="submit">Reactivar</button>
                            </form>
                            <% } %>
                        </td>
                    </tr>
                    <% }
                    } else { %>
                    <tr><td colspan="7">No hay proveedores registrados.</td></tr>
                    <% } %>
                </tbody>
            </table>

            <% if (proveedorSel != null) {%>
            <div id="modalVerProveedor" class="modal mostrar">
                <div class="modal-content animado">
                    <span class="cerrar" onclick="cerrarModal('modalVerProveedor')">&times;</span>
                    <h2>Detalles del Proveedor</h2>
                    <form action="ProveedorServlet" method="post" class="form-detalles-proveedor">
                        <input type="hidden" name="accion" value="guardar">
                        <input type="hidden" name="id" value="<%= proveedorSel.getIdProveedor()%>">

                        <div class="form-group">
                            <input type="text" name="razonSocial" placeholder="Razón Social" value="<%= proveedorSel.getRazonSocial()%>" required>
                        </div>

                        <div class="form-grid">
                            <div>
                                <input type="number" 
                                       name="ruc" 
                                       placeholder="RUC"
                                       value="<%= proveedorSel.getRuc()%>" 
                                       required 
                                       maxlength="11" 
                                       oninput="if(this.value.length > this.maxLength) this.value = this.value.slice(0, this.maxLength);">
                            </div>
                            <div>
                                <input type="text" name="direccion" placeholder="Dirección" value="<%= proveedorSel.getDireccion()%>" required>
                            </div>
                        </div>

                        <div class="form-group">
                            <input type="email" name="correo" placeholder="Correo" value="<%= proveedorSel.getCorreo()%>" required>
                        </div>

                        <button type="submit" class="btn btn-editar">Guardar Cambios</button>
                    </form>


                    <hr>
                    <div style="display: flex; justify-content: space-between; align-items: center;">
                        <h3>Contactos</h3>
                        <button class="btn btn-agregar" onclick="abrirModal('modalAgregarContacto')">Agregar Contacto</button>
                    </div>
                    <%
                        List<Modelo.ContactoProveedor> contactos = proveedorSel.getContactos();
                        if (contactos != null && !contactos.isEmpty()) {
                    %>
                    <table class="modal-contactos">
                        <thead>
                            <tr>
                                <th>Nombre</th>
                                <th>Cargo</th>
                                <th>Teléfono</th>
                                <th>Correo</th>
                                <th class="acciones">Acciones</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% for (Modelo.ContactoProveedor c : contactos) {%>
                            <tr>
                        <form action="ContactoProveedorServlet" method="post">
                            <input type="hidden" name="accion" value="actualizar">
                            <input type="hidden" name="idContacto" value="<%= c.getIdContacto()%>">
                            <input type="hidden" name="idProveedor" value="<%= proveedorSel.getIdProveedor()%>">
                            <td><input type="text" name="nombreContacto" placeholder="Nombre" value="<%= c.getNombreContacto()%>" required></td>
                            <td><input type="text" name="cargo" placeholder="Cargo" value="<%= c.getCargo()%>" required></td>
                            <td><input type="text" name="telefono" placeholder="Teléfono" value="<%= c.getTelefono()%>" required></td>
                            <td><input type="email" name="correoContacto" placeholder="Correo" value="<%= c.getCorreoContacto()%>" required></td>
                            <td class="acciones">
                                <form action="..." method="post" style="display:inline;">
                                    <button type="submit" class="btn btn-editar"> 
                                        <i class='bx bxs-trash'>Editar </i>   
                                    </button>
                                </form>
                                <form action="ContactoProveedorServlet" method="post" onsubmit="return confirm('¿Eliminar este contacto?')" style="display:inline;">
                                    <input type="hidden" name="accion" value="eliminar">
                                    <input type="hidden" name="idContacto" value="<%= c.getIdContacto()%>"> 

                                    <button type="submit" class="btn btn-eliminar">
                                        <i class='bx bxs-trash'>Eliminar</i>
                            </form>
                            </td>
                        </tr>
                        <% } %>
                        </tbody>
                    </table>
                    <% } else { %>
                    <p>No hay contactos registrados.</p>
                    <% }%>

                    <div id="modalAgregarContacto" class="modal">
                        <div class="modal-content animado">
                            <span class="cerrar" onclick="cerrarModal('modalAgregarContacto')">&times;</span>
                            <h3>Agregar Nuevo Contacto</h3>
                            <form action="ContactoProveedorServlet" method="post">
                                <input type="hidden" name="accion" value="agregar">
                                <input type="hidden" name="idProveedor" value="<%= proveedorSel.getIdProveedor()%>">
                                <input type="text" name="nombreContacto" placeholder="Nombre completo" required>
                                <input type="text" name="cargo" placeholder="Cargo" required>
                                <input type="text" name="telefono" placeholder="Teléfono" required>
                                <input type="email" name="correoContacto" placeholder="Correo" required>
                                <button type="submit" class="btn btn-agregar">Registrar</button>
                                <button type="button" class="btn btn-cancelar" onclick="cerrarModal('modalAgregarContacto')">Cancelar</button>
                            </form>
                        </div>
                    </div>

                    <hr>
                    <h3>Productos Relacionados</h3>
                    <ul>
                        <%
                            List<Modelo.Producto> productos = proveedorSel.getProductos();
                            if (productos != null && !productos.isEmpty()) {
                                for (Modelo.Producto prod : productos) {
                        %>
                        <li><%= prod.getNombreProducto()%> - S/ <%= prod.getPrecio()%></li>
                            <%
                                }
                            } else {
                            %>
                        <li>No hay productos relacionados.</li>
                            <%
                                }
                            %>
                    </ul>

                    <form action="ProveedorServlet" method="post" style="margin-top: 20px;" onsubmit="return confirmarCambioEstado(<%= proveedorSel.getEstado()%>)">
                        <input type="hidden" name="accion" value="cambiarEstado">
                        <input type="hidden" name="id" value="<%= proveedorSel.getIdProveedor()%>">
                        <input type="hidden" name="estado" value="<%= proveedorSel.getEstado()%>">
                        <button type="submit" class="btn btn-secundario">
                            <%= proveedorSel.getEstado() == 1 ? "Marcar como Inactivo" : "Reactivar Proveedor"%>
                        </button>
                    </form>
                </div>
            </div>
            <% }%>

            <div id="modalAgregarProveedor" class="modal">
                <div class="modal-content animado">
                    <span class="cerrar" onclick="cerrarModal('modalAgregarProveedor')">&times;</span>
                    <h2>Agregar Nuevo Proveedor</h2>

                    <form action="ProveedorServlet" method="post">
                        <input type="hidden" name="accion" value="guardar">
                        <input type="text" name="razonSocial" placeholder="Razón Social" required>
                        <input type="text" name="ruc" placeholder="RUC" maxlength="11">
                        <input type="text" name="direccion" placeholder="Dirección" required>
                        <input type="email" name="correo" placeholder="Correo" required>
                        <button type="submit" class="btn btn-agregar">Registrar</button>
                    </form>
                </div>
            </div>

        </main>

        <button id="modoNocheBtn" class="modo-noche-flotante" aria-label="Cambiar a modo noche">🌙</button>
        <script src="<%= request.getContextPath()%>/Js/JsAdmin/ModoNoche-Sidebar.js"></script>
        <script src="<%= request.getContextPath()%>/Js/JsAdmin/GestorProveedores.js"></script>
    </body>
</html>