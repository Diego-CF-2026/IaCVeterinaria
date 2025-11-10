<%@ include file="/proteger.jsp" %>
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
    // Inicialización de Variables y Datos de Sesión/Request
    List<Cita> listaCitas = (List<Cita>) request.getAttribute("listaCitas");
    Cita citaSel = (Cita) request.getAttribute("citaSeleccionada"); 
    String mensaje = (String) request.getAttribute("mensaje");
    String tipoMensaje = (String) request.getAttribute("tipoMensaje");
    
    // Estos datos se necesitan para el dropdown del Modal de Edición, asumiendo
    // que CitaServlet los carga en la acción 'editar' o por defecto.
    List<Cliente> listaClientes = (List<Cliente>) request.getAttribute("listaClientes");
    List<Veterinario> listaVeterinarios = (List<Veterinario>) request.getAttribute("listaVeterinarios");

    // Prevenir NullPointerExceptions (CRÍTICO para listas)
    if (listaCitas == null) {
        listaCitas = new ArrayList<>();
    }
    if (listaClientes == null) {
        listaClientes = new ArrayList<>();
    }
    if (listaVeterinarios == null) {
        listaVeterinarios = new ArrayList<>();
    }

    // Formateadores de fecha y hora
    SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm");

    // Determinar si es la vista global o la vista de un cliente específico
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
            /* --- Estilos CSS (Sin cambios, se mantienen tus estilos) --- */
            .modal {
                display: none; /* Oculto por defecto */
                position: fixed; /* Fijo en la pantalla */
                z-index: 1; /* Por encima de otros elementos */
                left: 0;
                top: 0;
                width: 100%; /* Ancho completo */
                height: 100%; /* Alto completo */
                overflow: auto; /* Habilita el desplazamiento si es necesario */
                background-color: rgb(0,0,0); /* Color de fondo */
                background-color: rgba(0,0,0,0.4); /* Fondo negro con opacidad */
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

            /* Estilos para el estado de las citas */
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

        <%-- Sidebar (se mantiene) --%>
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

            <div class="menu-bar">
                <ul class="menu-links">

                    <li class="nav-link">
                        <a href="<%= request.getContextPath()%>/ClienteRServlet">
                            <i class='bx bx-group icon'></i><span class="text">Clientes</span></a>
                    </li>

                    <li class="nav-link">
                        <a href="<%= request.getContextPath()%>/CitaServlet">
                            <i class='bx bxs-calendar icon'></i><span class="text">Citas Globales</span></a>
                    </li>

                    <li class="nav-link">
                        <%-- Ahora Citas de Usuarios apunta al flujo de búsqueda (RecepcionCitaServlet) --%>
                        <a href="${pageContext.request.contextPath}/RecepcionCitaServlet"> 
                            <i class='bx bx-calendar-alt icon'></i><span class="text">Agendar Cita</span></a>
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
                <h1><%= tituloPagina%></h1>
                <div class="acciones">
                    <%-- El buscador solo se muestra en la vista global --%>
                    <% if (clienteActual == null) { %>
                    <form method="get" action="${pageContext.request.contextPath}/CitaServlet">
                        <input type="hidden" name="accion" value="buscar" />
                        <input type="text" name="busqueda" placeholder="Buscar por cliente, vet, motivo..." 
                               value="${param.busqueda != null ? param.busqueda : ''}" />
                        <button type="submit" class="btn btn-editar">Buscar</button>
                        <a href="${pageContext.request.contextPath}/CitaServlet" class="btn btn-limpiar">Limpiar</a>
                    </form>
                    <% } %>
                    
                    <%-- Redirige para iniciar el flujo de agendar cita (buscar cliente) --%>
                    <a href="${pageContext.request.contextPath}/RecepcionCitaServlet" class="btn btn-agregar">
                        Agendar Nueva Cita
                    </a>
                </div>
            </div>

            <%-- Mensaje --%>
            <% if (mensaje != null) {%>
            <div class="alert <%= tipoMensaje != null ? tipoMensaje : ""%>"><%= mensaje%></div>
            <% } %>

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
                                        // 🛑 CLAVE: Usa c.getEstado() (String) para determinar la clase CSS
                                        String nombreEstado = c.getEstado();
                                        switch (nombreEstado) {
                                            case "Pendiente":
                                                claseEstado = "estado-pendiente";
                                                break;
                                            case "Completado":
                                                claseEstado = "estado-completado";
                                                break;
                                            case "Cancelado": // Asegúrate que el nombre coincide con tu DB/DAO
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
            
            <%-- Modal Editar Cita --%>
            <div id="modalEditarCita" class="modal">
                <div class="modal-content">
                    <span class="close" onclick="cerrarModal('modalEditarCita')">&times;</span>
                    <h2>Editar Cita</h2>

                    <form action="${pageContext.request.contextPath}/CitaServlet" method="post">
                        <input type="hidden" name="accion" value="actualizar">
                        <input type="hidden" name="idCita" id="editar_idCita">

                        <div class="form-group">
                            <label for="editar_idCliente">Cliente:</label>
                            <%-- Nota: Para que este dropdown funcione, CitaServlet debe cargar listaClientes antes de reenviar --%>
                            <select name="idCliente" id="editar_idCliente" required class="form-control">
                                <option value="">Seleccione un cliente</option>
                                <% for (Cliente cli : listaClientes) {%>
                                <option value="<%= cli.getIdCliente()%>">
                                    <%= cli.getNombre()%> <%= cli.getApellido()%> - <%= cli.getDni()%>
                                </option>
                                <% } %>
                            </select>
                        </div>

                        <div class="form-group">
                            <label for="editar_idVeterinario">Veterinario:</label>
                             <%-- Nota: Para que este dropdown funcione, CitaServlet debe cargar listaVeterinarios antes de reenviar --%>
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
                    // Al cerrar el modal de edición, redirige para limpiar el estado del servlet (si es necesario)
                    if (modalId === 'modalEditarCita') {
                        window.location.href = '${pageContext.request.contextPath}/CitaServlet';
                    }
                }
            }

            // Cierra el modal si se hace clic fuera de él
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

            // Función para abrir el modal de edición y prellenar los campos
            function abrirModalEditar(idCita, idCliente, idVeterinario, fecha, hora, motivo, estado) {
                abrirModal('modalEditarCita');
                document.getElementById('editar_idCita').value = idCita;
                document.getElementById('editar_idCliente').value = idCliente;
                document.getElementById('editar_idVeterinario').value = idVeterinario;
                document.getElementById('editar_fecha').value = fecha;
                document.getElementById('editar_hora').value = hora;
                document.getElementById('editar_motivo').value = motivo; 
                document.getElementById('editar_estado').value = estado;
                
                // Si tienes la validación de fecha, guarda la fecha original
                document.getElementById('editar_fecha').dataset.originalDate = fecha;
            }
        </script>
    </body>
</html>