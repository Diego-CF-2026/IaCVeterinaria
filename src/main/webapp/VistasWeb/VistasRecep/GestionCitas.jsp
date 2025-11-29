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
        <style>
            /* ==================== 🎨 ESTILOS PARA REPLICAR LA IMAGEN 🎨 ==================== */

            /* 1. Fondo Oscuro General */
            body {
                background-color: #1e1e1e; /* Fondo negro muy oscuro */
                color: #e0e0e0; /* Texto predeterminado claro */
                margin: 0;
                padding: 0;
                font-family: sans-serif;
            }

            /* 2. Contenido Principal y su Espacio (Ajuste para Sidebar de 250px) */
            main {
                position: relative;
                margin-left: 250px;
                padding: 20px 40px;
                min-height: 100vh;
                box-sizing: border-box;
                background-color: #1e1e1e; /* Asegura el fondo oscuro del área principal */
            }

            /* 3. Estilos del Sidebar (Ajustes para estructura de la imagen) */
            .sidebar {
                background-color: #242424; /* Fondo de la barra lateral */
                width: 250px;
                border-right: none;
                padding: 10px 0;
                /* Asegúrate de que tu ModoNoche-Sidebar.css aplique position: fixed */
            }
            .sidebar .header-text .profession {
                 font-size: 14px;
                 color: #a0a0a0;
                 margin-top: 2px;
            }
            .sidebar .nav-link a {
                color: #a0a0a0;
            }
            .sidebar .nav-link.active a {
                color: #ffffff;
                background-color: #1e1e1e; /* Resalta el activo con un fondo más oscuro */
                border-radius: 0; /* Si quieres el estilo sin bordes redondeados */
            }
             .sidebar .image-text .image i {
                font-size: 50px; /* Tamaño de la huella */
                color: #e0e0e0;
                margin-bottom: 5px;
            }
            .sidebar header {
                padding: 20px 0;
                margin-bottom: 20px;
                text-align: center;
                border-bottom: 1px solid #333333;
            }

            /* 4. Cabecera y Acciones (Título, Búsqueda, Botón) */
            .header-actions {
                display: flex;
                flex-direction: row;
                justify-content: space-between;
                align-items: center;
                width: 100%;
                margin-bottom: 20px;
                padding-right: 0px;
                padding-left: 0px;
            }

            .header-actions h1 {
                color: #ffffff;
                font-size: 28px;
                margin: 0;
            }

            .acciones-grupo {
                display: flex;
                align-items: center;
                gap: 10px;
            }

            /* Inputs y Botones de Búsqueda */
            .busqueda-form {
                display: flex;
                align-items: center;
                gap: 10px;
            }

            .busqueda-form input[type="text"] {
                height: 35px;
                padding: 6px 10px;
                border: 1px solid #4a4a4a;
                border-radius: 4px;
                width: 250px;
                background-color: #333333;
                color: #e0e0e0;
            }

            .busqueda-form button[type="submit"] {
                background-color: #4a4a4a; /* Gris oscuro para Buscar */
                color: white;
                border: none;
                font-weight: bold;
                height: 35px;
                padding: 6px 12px;
                border-radius: 4px;
            }

            .btn-limpiar {
                background: none;
                border: none;
                color: #A0A0A0;
                text-decoration: none;
                padding: 0;
                height: 35px;
                display: flex;
                align-items: center;
            }

            .btn-agregar {
                background-color: #28a745; /* Verde para Agendar Nueva Cita */
                color: white;
                border: none;
                font-weight: bold;
                height: 35px;
                padding: 6px 12px;
                border-radius: 4px;
            }
            .btn-editar {
                background-color: #333333; 
                color: #e0e0e0;
                border: 1px solid #555555;
                padding: 6px 12px;
                border-radius: 4px;
            }
            .btn-cancelar-inline {
                color: #b0b0b0; /* Gris claro para el enlace Cancelar */
                text-decoration: none;
                margin-left: 10px;
                font-size: 14px;
            }

            /* 5. Estilos de la Tabla (Clave para la imagen) */
            .tabla-container {
                /* Fondo que envuelve toda la tabla */
                background-color: #333333;
                border-radius: 0px; /* La imagen no muestra bordes redondeados en la tabla */
                overflow: hidden;
            }

            .tabla-citas {
                width: 100%;
                border-collapse: collapse;
                border-spacing: 0;
                margin: 0;
            }

            .tabla-citas thead {
                background-color: #333333; /* Usar el mismo color del cuerpo de la tabla */
                color: white;
            }
            
            /* Ajuste para el color de la cabecera (similar al de la imagen) */
            .tabla-citas th {
                padding: 10px 12px;
                text-align: left;
                font-weight: 600;
                font-size: 15px;
                color: #b0b0b0; /* Color gris suave para las cabeceras */
                border-bottom: 1px solid #444444; /* Línea de separación sutil */
            }
            
            .tabla-citas td {
                padding: 10px 12px;
                text-align: left;
                border-bottom: 1px solid #444444;
                color: #e0e0e0;
            }

            .tabla-citas tbody tr:last-child td {
                border-bottom: none;
            }

            .tabla-citas .no-data {
                padding: 10px 12px; 
                height: auto;
                color: #b0b0b0;
            }

            /* 6. Estilos de Estado */
            .estado-pendiente { background-color: #5d4000; color: #ffeb3b; padding: 3px 8px; border-radius: 4px; font-weight: bold; font-size: 12px;}
            .estado-completado { background-color: #1a4f29; color: #a4e1ae; padding: 3px 8px; border-radius: 4px; font-weight: bold; font-size: 12px;}
            .estado-cancelado { background-color: #5e1c1c; color: #ff9999; padding: 3px 8px; border-radius: 4px; font-weight: bold; font-size: 12px;}
            .estado-confirmada { background-color: #1c3c5e; color: #a0c3ff; padding: 3px 8px; border-radius: 4px; font-weight: bold; font-size: 12px;}


            /* 7. Estilos de Modal */
            .modal { display: none; position: fixed; z-index: 1; left: 0; top: 0; width: 100%; height: 100%; overflow: auto; background-color: rgba(0,0,0,0.6); }
            .modal-content { 
                background-color: #333333; 
                color: #e0e0e0; 
                margin: 10% auto; 
                padding: 20px; 
                border: 1px solid #4a4a4a; 
                width: 80%; 
                max-width: 600px; 
                border-radius: 8px; 
            }
            .form-control {
                background-color: #4a4a4a; 
                color: #e0e0e0;
                border: 1px solid #555;
            }
            .modal-actions { display: flex; justify-content: space-between; margin-top: 20px; }
            .btn-cerrar { background-color: #555; color: white; border: none; padding: 8px 16px; border-radius: 4px; cursor: pointer; }
            .btn-guardar { background-color: #28a745; color: white; border: none; padding: 8px 16px; border-radius: 4px; cursor: pointer; }
        </style>
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