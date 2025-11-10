<%@ include file="/proteger.jsp" %>   <!-- Protege la página: evita acceso sin sesión -->
<%@page import="Modelo.Veterinario"%> 
<%@page import="Modelo.Cliente"%>
<%@page import="Modelo.Cita"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.sql.Date" %>
<%@ page import="java.sql.Time" %>

<%
        // ======== Recuperación de datos enviados desde el Servlet ========
    List<Cita> listaCitas = (List<Cita>) request.getAttribute("listaCitas");
    Cita citaSel = (Cita) request.getAttribute("citaSeleccionada"); 
    String mensaje = (String) request.getAttribute("mensaje");
    String tipoMensaje = (String) request.getAttribute("tipoMensaje");
    
    List<Cliente> listaClientes = (List<Cliente>) request.getAttribute("listaClientes");
    List<Veterinario> listaVeterinarios = (List<Veterinario>) request.getAttribute("listaVeterinarios");

     // Si alguna lista llega nula, se inicializa vacía para evitar errores en JSP
    if (listaCitas == null) {
        listaCitas = new ArrayList<>();
    }
    if (listaClientes == null) {
        listaClientes = new ArrayList<>();
    }
    if (listaVeterinarios == null) {
        listaVeterinarios = new ArrayList<>();
    }

    SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm");

    String tituloPagina = "Gestión Global de Citas";
    Cliente clienteActual = (Cliente) request.getAttribute("clienteActual");
    if (clienteActual != null) {
        tituloPagina = "Citas de Cliente: " + clienteActual.getNombre() + " " + clienteActual.getApellido();
    }
%>

<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <title><%= tituloPagina%></title>
        <link href="https://cdn.jsdelivr.net/npm/boxicons@2.1.1/css/boxicons.min.css" rel="stylesheet">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/ModoNoche-Sidebar.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/GestorCitas.css">
        <style>
                    /* ==================== ESTILOS DEL MODAL ==================== */
            .modal {
                display: none; 
                position: fixed;
                z-index: 1; 
                left: 0;
                top: 0;
                width: 100%; 
                height: 100%; 
                overflow: auto; 
                background-color: rgb(0,0,0); 
                background-color: rgba(0,0,0,0.4); 
            }

            .modal-content {
                background-color: white;
                margin: 15% auto;
                padding: 20px;
                border: 1px solid #888;
                width: 80%;
                max-width: 600px;
                border-radius: 8px;
            }

            .modal-actions {
                display: flex;
                justify-content: space-between;
                margin-top: 20px;
            }

            .btn-cancelar {
                background-color: #f44336;
                color: white;
                border: none;
                padding: 8px 16px;
                border-radius: 4px;
                cursor: pointer;
            }

            .btn-cancelar:hover {
                background-color: #d32f2f;
            }

            .btn-cerrar {
                background-color: #ccc;
                color: black;
                border: none;
                padding: 8px 16px;
                border-radius: 4px;
                cursor: pointer;
            }

            .btn-guardar {
                background-color: #4CAF50;
                color: white;
                border: none;
                padding: 8px 16px;
                border-radius: 4px;
                cursor: pointer;
            }

            .btn-guardar:hover {
                background-color: #388E3C;
            }

            .estado-pendiente {
                background-color: #fff3cd;
                color: #856404;
                padding: 3px 8px;
                border-radius: 4px;
                font-weight: bold;
            }

            .estado-confirmada {
                background-color: #d4edda;
                color: #155724;
                padding: 3px 8px;
                border-radius: 4px;
                font-weight: bold;
            }

            .estado-cancelada {
                background-color: #f8d7da;
                color: #721c24;
                padding: 3px 8px;
                border-radius: 4px;
                font-weight: bold;
            }

            .estado-completada {
                background-color: #d1ecf1;
                color: #0c5460;
                padding: 3px 8px;
                border-radius: 4px;
                font-weight: bold;
            }

           
        </style>
    </head>
    <body>

       <!-- ==================== SIDEBAR ==================== -->
        <nav class="sidebar">
            <header>
                <div class="image-text">
                    <span class="image">
                        <img id="logoAdmin" src="<%= request.getContextPath()%>/Recursos/Logo.png" alt="Logo de Veterinaria Santa Cruz" class="logo">
                    </span>
                    <div class="header-text">
                        <span class="name">Recepcionista</span>
                        <span class="profession">Veterinaria Santa Cruz</span>
                    </div>
                </div>
            </header>

            <!-- Menú lateral -->        
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

        <!-- ==================== CONTENIDO PRINCIPAL ==================== -->
        <main>
            <div class="header-actions">
                <h1><%= tituloPagina%></h1>
                <div class="acciones">
                    <!-- Formulario de búsqueda (solo si no es vista por cliente) -->
                    <% if (clienteActual == null) { %>
                    <form method="get" action="${pageContext.request.contextPath}/CitaServlet">
                        <input type="hidden" name="accion" value="buscar" />
                        <input type="text" name="busqueda" placeholder="Buscar por cliente, vet, motivo..." 
                               value="${param.busqueda != null ? param.busqueda : ''}" />
                        <button type="submit" class="btn btn-editar">Buscar</button>
                        <a href="${pageContext.request.contextPath}/CitaServlet" class="btn btn-limpiar">Limpiar</a>
                    </form>
                    <% } %>
                    
                    <!-- Botón para crear una nueva cita -->
                    <a href="${pageContext.request.contextPath}/RecepcionCitaServlet" class="btn btn-agregar">
                        Agendar Nueva Cita
                    </a>
                </div>
            </div>

            <!-- Mensaje de confirmación o error -->
            <% if (mensaje != null) {%>
            <div class="alert <%= tipoMensaje != null ? tipoMensaje : ""%>"><%= mensaje%></div>
            <% } %>

            <!-- ==================== TABLA DE CITAS ==================== -->
            <div class="tabla-citas">
                <table class="tabla-citas th">
                    <thead>
                        <tr>
                            <th>Cliente</th>
                            <th>Veterinario</th>
                            <th>Fecha</th>
                            <th>Hora</th>
                            <th>Motivo</th>
                            <th>Estado</th>
                            <th>Acciones</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% if (!listaCitas.isEmpty()) {
                                for (Cita c : listaCitas) {
                                        String claseEstado = "";
                                        
                                        // Asignación de color según el estado
                                        String nombreEstado = c.getEstado();
                                        switch (nombreEstado) {
                                            case "Pendiente":
                                                claseEstado = "estado-pendiente";
                                                break;
                                            case "Completado":
                                                claseEstado = "estado-completado";
                                                break;
                                            case "Cancelado": 
                                                claseEstado = "estado-cancelado";
                                                break;
                                            default:
                                                claseEstado = ""; 
                                        }
                        %>
                        <tr>
                            <td><%= c.getNombreCliente()%> <%= c.getApellidoCliente()%></td>
                            <td>Dr(a). <%= c.getNombreVeterinario()%> <%= c.getApellidoVeterinario()%></td>
                            <td><%= dateFormatter.format(c.getFecha())%></td>
                            <td><%= timeFormatter.format(c.getHora())%></td>
                            <td><%= c.getMotivo()%></td>
                            <%-- 🛑 CLAVE: Muestra el nombre del estado (String) --%>
                            <td><span class="<%= claseEstado%>"><%= c.getEstado()%></span></td>
                            <td>
                                <button class="btn btn-editar"
                                        onclick="abrirModalEditar(
                                            '<%= c.getIdCita()%>',
                                            '<%= c.getIdCliente()%>',
                                            '<%= c.getIdVeterinario()%>',
                                            '<%= dateFormatter.format(c.getFecha())%>',
                                            '<%= timeFormatter.format(c.getHora())%>',
                                            '<%= c.getMotivo().replace("'", "\\'")%>', 
                                            '<%= c.getEstado()%>'
                                        )">Editar</button>

                                <a href="${pageContext.request.contextPath}/CitaServlet?accion=cancelar&id=<%= c.getIdCita()%>" 
                                   class="btn btn-cancelar" onclick="return confirm('¿Está seguro de cancelar esta cita?')">Cancelar</a>
                            </td>
                        </tr>
                        <% }
                        } else { %>
                        <tr>
                            <td colspan="8" class="no-data">No hay citas registradas.</td>
                        </tr>
                        <% }%>
                    </tbody>
                </table>
            </div>
            
            <!-- ==================== MODAL EDITAR CITA ==================== -->
            <div id="modalEditarCita" class="modal">
                <div class="modal-content">
                    <span class="close" onclick="cerrarModal('modalEditarCita')">&times;</span>
                    <h2>Editar Cita</h2>

                    <form action="${pageContext.request.contextPath}/CitaServlet" method="post">
                        <input type="hidden" name="accion" value="actualizar">
                        <input type="hidden" name="idCita" id="editar_idCita">

                        <!-- Selección de veterinario -->
                        <div class="form-group">
                            <label for="editar_idCliente">Cliente:</label>       
                            <select name="idCliente" id="editar_idCliente" required class="form-control">
                                <option value="">Seleccione un cliente</option>
                                <% for (Cliente cli : listaClientes) {%>
                                <option value="<%= cli.getIdCliente()%>">
                                    <%= cli.getNombre()%> <%= cli.getApellido()%> - <%= cli.getDni()%>
                                </option>
                                <% } %>
                            </select>
                        </div>

                        <!-- Fecha, hora, motivo y estado -->
                        <div class="form-group">
                            <label for="editar_idVeterinario">Veterinario:</label>
                            <select name="idVeterinario" id="editar_idVeterinario" required class="form-control">
                                <option value="">Seleccione un veterinario</option>
                                <% for (Veterinario vet : listaVeterinarios) {%>
                                <option value="<%= vet.getIdVeterinario()%>">
                                    Dr(a). <%= vet.getNombreVeterinario()%> <%= vet.getApellidoVeterinario()%> 
                                </option>
                                <% } %>
                            </select>
                        </div>
                            
                        <div class="form-group">
                            <label for="editar_fecha">Fecha:</label>
                            <input type="date" name="fecha" id="editar_fecha" required class="form-control">
                        </div>

                        <div class="form-group">
                            <label for="editar_hora">Hora:</label>
                            <input type="time" name="hora" id="editar_hora" required class="form-control">
                        </div>
                        <div class="form-group">
                            <label for="editar_motivo">Motivo:</label>
                            <input type="text" name="motivo" id="editar_motivo" required class="form-control" placeholder="Ej: Consulta de rutina, Vacunación, etc.">
                        </div>
                        <div class="form-group">
                            <label for="editar_estado">Estado:</label>
                            <select name="estado" id="editar_estado" required class="form-control">
                                <option value="Pendiente">Pendiente</option>
                                <option value="Completado">Completado</option>
                                <option value="Cancelado">Cancelado</option>
                            </select>
                        </div>

                        <div class="modal-actions">
                            <button type="button" class="btn-cerrar" onclick="cerrarModal('modalEditarCita')">Cancelar</button>
                            <button type="submit" class="btn-guardar">Actualizar</button>
                        </div>
                    </form>
                </div>
            </div>
            
        </main>
                            
        <button id="modoNocheBtn" class="modo-noche-flotante" aria-label="Cambiar a modo noche">🌙</button>
        <script src="<%= request.getContextPath()%>/Js/JsAdmin/ModoNoche-Sidebar.js"></script>
        <script>
            // ====== FUNCIONES DEL MODAL ======
            function abrirModal(modalId) {
                const modal = document.getElementById(modalId);
                if (modal) {
                    modal.style.display = 'block';
                }
            }

            function cerrarModal(modalId) {
                const modal = document.getElementById(modalId);
                if (modal) {
                    modal.style.display = 'none';
                    
                    if (modalId === 'modalEditarCita') {
                        window.location.href = '${pageContext.request.contextPath}/CitaServlet';
                    }
                }
            }

            // Cierra el modal si el usuario hace clic fuera del cuadro
            window.onclick = function (event) {
                const modals = document.getElementsByClassName("modal");
                for (let modal of modals) {
                    if (event.target == modal) {
                        modal.style.display = "none";
                        if (modal.id === 'modalEditarCita') {
                            window.location.href = '${pageContext.request.contextPath}/CitaServlet';
                        }
                    }
                }
            }

            // Prellena el modal con los datos de la cita seleccionada
            function abrirModalEditar(idCita, idCliente, idVeterinario, fecha, hora, motivo, estado) {
                abrirModal('modalEditarCita');
                document.getElementById('editar_idCita').value = idCita;
                document.getElementById('editar_idCliente').value = idCliente;
                document.getElementById('editar_idVeterinario').value = idVeterinario;
                document.getElementById('editar_fecha').value = fecha;
                document.getElementById('editar_hora').value = hora;
                document.getElementById('editar_motivo').value = motivo; 
                document.getElementById('editar_estado').value = estado;
                document.getElementById('editar_fecha').dataset.originalDate = fecha;
            }
        </script>
    </body>
</html>