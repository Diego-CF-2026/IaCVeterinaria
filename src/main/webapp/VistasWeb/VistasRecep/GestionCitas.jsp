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
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/GestionadorDecitas.css">
    </head>
    <body>

        <nav class="sidebar">
            <header>
                <div class="image-text">
                    <span class="image">
                        <i class='bx bxs-paw'></i>
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
                    <li class="nav-link active"> <a href="${pageContext.request.contextPath}/CitaServlet">
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

        <main>
            <div class="header-actions">
                <h1><%= tituloPagina%></h1>

                <div class="acciones-grupo">
                    <% if (clienteActual == null) { %>
                    <form method="get" action="${pageContext.request.contextPath}/CitaServlet" class="busqueda-form">
                        <input type="hidden" name="accion" value="buscar" />
                        <input type="text" name="busqueda" placeholder="Buscar por cliente, vet, motivo..."
                            value="${param.busqueda != null ? param.busqueda : ''}" />
                        <button type="submit" class="btn btn-editar">Buscar</button>
                        <a href="${pageContext.request.contextPath}/CitaServlet" class="btn btn-limpiar">Limpiar</a>
                    </form>
                    <% } %>

                    <button type="button" class="btn btn-agregar" onclick="abrirModal('modalNuevaCita')">
                        Agendar Nueva Cita
                    </button>
                </div>
            </div>

            <%
                String mensajeSesion = (String) session.getAttribute("mensaje");
                String tipoMensajeSesion = (String) session.getAttribute("tipoMensaje");

                if (mensajeSesion != null) { %>
            <div class="alert <%= tipoMensajeSesion != null ? tipoMensajeSesion : ""%>"><%= mensajeSesion%></div>
            <%
                session.removeAttribute("mensaje");
                session.removeAttribute("tipoMensaje");
                } else if (mensaje != null) { %>
            <div class="alert <%= tipoMensaje != null ? tipoMensaje : ""%>"><%= mensaje%></div>
            <% } %>

            <div class="tabla-container">
                <table class="tabla-citas">
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
                                        case "Confirmada":
                                            claseEstado = "estado-confirmada";
                                            break;
                                        default:
                                            claseEstado = "";
                                            break;
                                    }
                        %>
                        <tr>
                            <td><%= c.getNombreCliente()%> <%= c.getApellidoCliente()%></td>
                            <td>Dr(a). <%= c.getNombreVeterinario()%> <%= c.getApellidoVeterinario()%></td>
                            <td><%= dateFormatter.format(c.getFecha())%></td>
                            <td><%= timeFormatter.format(c.getHora())%></td>
                            <td><%= c.getMotivo()%></td>
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
                                                '<%= c.getEstado()%>',
                                                '<%= c.getPrecio()%>'
                                                )">Editar</button>

                                <a href="${pageContext.request.contextPath}/CitaServlet?accion=cancelar&id=<%= c.getIdCita()%>"
                                   class="btn-cancelar-inline" onclick="return confirm('¿Está seguro de cancelar esta cita?')">Cancelar</a>
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

            <div id="modalNuevaCita" class="modal">
                <div class="modal-content">
                    <span class="close" onclick="cerrarModal('modalNuevaCita')">&times;</span>
                    <h2>Agendar Nueva Cita</h2>

                    <form action="${pageContext.request.contextPath}/CitaServlet" method="post">
                        <input type="hidden" name="accion" value="guardarNuevaCitaGlobal">
                        <input type="hidden" name="precio" value="0.0">

                        <div class="form-group">
                            <label for="nuevo_idCliente">Cliente:</label>
                            <select name="idCliente" id="nuevo_idCliente" required class="form-control">
                                <option value="">Seleccione un cliente</option>
                                <% for (Cliente cli : listaClientes) {%>
                                <option value="<%= cli.getIdCliente()%>">
                                    <%= cli.getNombre()%> <%= cli.getApellido()%>
                                </option>
                                <% } %>
                            </select>
                        </div>

                        <div class="form-group">
                            <label for="nuevo_idVeterinario">Veterinario:</label>
                            <select name="idVeterinario" id="nuevo_idVeterinario" required class="form-control">
                                <option value="">Seleccione un veterinario</option>
                                <% for (Veterinario vet : listaVeterinarios) {%>
                                <option value="<%= vet.getIdVeterinario()%>">
                                    Dr(a). <%= vet.getNombreVeterinario()%> <%= vet.getApellidoVeterinario()%>
                                </option>
                                <% } %>
                            </select>
                        </div>

                        <div class="form-group">
                            <label for="nuevo_fecha">Fecha:</label>
                            <input type="date" name="fecha" id="nuevo_fecha" required class="form-control">
                        </div>

                        <div class="form-group">
                            <label for="nuevo_hora">Hora:</label>
                            <input type="time" name="hora" id="nuevo_hora" required class="form-control">
                        </div>
                        <div class="form-group">
                            <label for="nuevo_motivo">Motivo:</label>
                            <input type="text" name="motivo" id="nuevo_motivo" required class="form-control" placeholder="Ej: Consulta de rutina, Vacunación, etc.">
                        </div>
                        <input type="hidden" name="estado" value="Pendiente">

                        <div class="modal-actions">
                            <button type="button" class="btn-cerrar" onclick="cerrarModal('modalNuevaCita')">Cancelar</button>
                            <button type="submit" class="btn-guardar">Agendar</button>
                        </div>
                    </form>
                </div>
            </div>

            <div id="modalEditarCita" class="modal">
                <div class="modal-content">
                    <span class="close" onclick="cerrarModal('modalEditarCita')">&times;</span>
                    <h2>Editar Cita</h2>

                    <form action="${pageContext.request.contextPath}/CitaServlet" method="post">
                        <input type="hidden" name="accion" value="actualizar">
                        <input type="hidden" name="idCita" id="editar_idCita">
                        <input type="hidden" name="precio" id="editar_precio" value="0.0">

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
        <script src="${pageContext.request.contextPath}/Js/JsAdmin/ModoNoche-Sidebar.js"></script>
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

                    if (modalId === 'modalEditarCita' && document.querySelector('.alert-danger')) {
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
                        if (modal.id === 'modalEditarCita' && document.querySelector('.alert-danger')) {
                            window.location.href = '${pageContext.request.contextPath}/CitaServlet';
                        }
                    }
                }
            }

            // Prellena el modal con los datos de la cita seleccionada
            function abrirModalEditar(idCita, idCliente, idVeterinario, fecha, hora, motivo, estado, precio) {
                abrirModal('modalEditarCita');
                document.getElementById('editar_idCita').value = idCita;
                document.getElementById('editar_idCliente').value = idCliente;
                document.getElementById('editar_idVeterinario').value = idVeterinario;
                document.getElementById('editar_fecha').value = fecha;
                document.getElementById('editar_hora').value = hora;
                document.getElementById('editar_motivo').value = motivo;
                document.getElementById('editar_estado').value = estado;
                document.getElementById('editar_precio').value = precio;
                document.getElementById('editar_fecha').dataset.originalDate = fecha;
            }

            // ====== LÓGICA PARA ABRIR MODAL TRAS REDIRECCIÓN/FORWARD DEL SERVLET (Con error) ======
            <%
                if (citaSel != null) {
            %>
                window.onload = function() {
                    abrirModalEditar(
                        '<%= citaSel.getIdCita()%>',
                        '<%= citaSel.getIdCliente()%>',
                        '<%= citaSel.getIdVeterinario()%>',
                        '<%= dateFormatter.format(citaSel.getFecha())%>',
                        '<%= timeFormatter.format(citaSel.getHora())%>',
                        '<%= citaSel.getMotivo().replace("'", "\\'")%>',
                        '<%= citaSel.getEstadoNombre() != null ? citaSel.getEstadoNombre() : citaSel.getEstado()%>',
                        '<%= citaSel.getPrecio()%>'
                    );
                };
            <% } %>
        </script>
    </body>
</html>