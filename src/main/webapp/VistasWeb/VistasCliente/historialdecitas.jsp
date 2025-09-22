<%-- VistasWeb/VistasCliente/historialdecitas.jsp --%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="Modelo.UsuarioCitas"%>
<%@page import="Modelo.UsuarioCliente"%>
<%@page import="java.time.LocalDate"%>
<%@page import="java.time.LocalTime"%>
<%@page import="java.time.format.DateTimeFormatter"%>
<%@page import="java.sql.Date"%>  <%-- Importar java.sql.Date para comparar --%>
<%@page import="java.sql.Time"%>  <%-- Importar java.sql.Time para el tipo de dato --%>

<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Historial de Citas - Veterinaria Santa Cruz</title>
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;600&display=swap" rel="stylesheet" />
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/index.css"> <%-- Ajusta la ruta a tu CSS --%>
    <style>
        
        body { font-family: 'Poppins', Arial, sans-serif; background: #f8f8f8; margin: 0; }
        .navbar {
            display: flex; align-items: center; justify-content: space-between;
            background: #3aafa9; padding: 0 30px; height: 70px;
        }
        .logo-container { display: flex; align-items: center; }
        .logo { height: 56px; }
        .nav-links { display: flex; align-items: center; }
        .center-links a {
            color: #fff; text-decoration: none; margin: 0 16px; font-weight: 500; font-size: 1.1rem;
            transition: color 0.2s;
        }
        .center-links a:hover, .center-links a.active-link { color: #17252a; border-bottom: 2px solid #fff; }
        .buttons .btn { background: #def2f1; color: #3aafa9; padding: 8px 18px; border-radius: 20px; text-decoration: none; font-weight: 600; margin-left: 10px; }
        .sidebar-perfil {
            display: none; position: fixed; top: 0; right: 0; width: 280px; height: 100%;
            background: #fff; box-shadow: -2px 0 5px rgba(0,0,0,0.3); z-index: 2000; padding-top: 50px; overflow-y: auto;
        }
        .sidebar-perfil.active { display: block; }
        .sidebar-perfil h2 { margin: 0 0 20px 20px; font-weight: 600; font-size: 22px; }
        .sidebar-perfil a { display: block; padding: 15px 25px; color: #333; text-decoration: none; border-bottom: 1px solid #eee; font-size: 16px; transition: background 0.2s; }
        .sidebar-perfil a:hover { background: #f0f0f0; }
        #sidebarOverlay { display: none; position: fixed; top: 0; left: 0; width: 100vw; height: 100vh; background: rgba(0,0,0,0.4); z-index: 1500; }
        #sidebarOverlay.active { display: block; }
        .historial-container {
            max-width: 900px; margin: 40px auto 60px auto; padding: 20px;
            border: 1px solid #ccc; border-radius: 8px; background: #fff;
        }
        .historial-container h1 { text-align: center; margin-bottom: 30px; color: #3aafa9; font-size: 2rem; font-weight: 600; }
        table { width: 100%; border-collapse: collapse; }
        th, td { padding: 12px 10px; border-bottom: 1px solid #eee; text-align: center; font-size: 1rem; }
        th { background: #3aafa9; color: #fff; font-weight: 600; }
        tr:hover { background: #f1f1f1; }
        .alert-success, .alert-error, .alert-info {
            max-width: 600px; margin: 20px auto; padding: 15px; border-radius: 5px; font-size: 1rem; text-align: center;
        }
        .alert-success { background: #d4edda; color: #155724; }
        .alert-error { background: #f8d7da; color: #721c24; }
        .alert-info {
            background-color: #d1ecf1;
            color: #0c5460;
            border: 1px solid #bee5eb;
        }
        .hora-am-pm { text-transform: lowercase; }
        .hora-pasada { color: red; }

        .modal {
            display: none; 
            position: fixed; 
            z-index: 1001; 
            left: 0;
            top: 0;
            width: 100%; 
            height: 100%; 
            overflow: auto; 
            background-color: rgba(0,0,0,0.6); 
            padding-top: 60px; 
        }

        .modal-content {
            background-color: #fefefe;
            margin: 5% auto; 
            padding: 30px;
            border: 1px solid #888;
            width: 90%; 
            max-width: 500px; 
            border-radius: 10px;
            position: relative;
            box-shadow: 0 4px 8px 0 rgba(0,0,0,0.2), 0 6px 20px 0 rgba(0,0,0,0.19);
            animation-name: animatetop;
            animation-duration: 0.4s
        }

        @-webkit-keyframes animatetop {
            from {top:-300px; opacity:0}  
            to {top:0; opacity:1}
        }

        @keyframes animatetop {
            from {top:-300px; opacity:0}
            to {top:0; opacity:1}
        }

        .close-button {
            color: #aaa;
            position: absolute;
            right: 15px;
            top: 10px;
            font-size: 32px;
            font-weight: bold;
        }

        .close-button:hover,
        .close-button:focus {
            color: black;
            text-decoration: none;
            cursor: pointer;
        }

        .modal-content label {
            display: block;
            margin-bottom: 8px;
            font-weight: 600;
            color: #333;
        }

        .modal-content input[type="date"],
        .modal-content input[type="time"],
        .modal-content input[type="text"],
        .modal-content textarea,
        .modal-content select {
            width: calc(100% - 20px); 
            padding: 10px;
            margin-bottom: 20px;
            border: 1px solid #ddd;
            border-radius: 5px;
            box-sizing: border-box; 
            font-size: 1rem;
        }
        .modal-content textarea {
            resize: vertical; 
        }

        .modal-content button.btn {
            background-color: #3aafa9; 
            color: white;
            padding: 12px 20px;
            border: none;
            border-radius: 25px; 
            cursor: pointer;
            font-size: 1.1rem;
            font-weight: 600;
            width: auto; 
            display: block; 
            margin: 0 auto; 
            transition: background-color 0.2s;
        }

        .modal-content button.btn:hover {
            background-color: #2b7a78; 
        }
   
        .btn-accion {
            display: inline-block;
            padding: 8px 15px;
            margin: 3px; 
            border: none;
            border-radius: 5px;
            text-align: center;
            text-decoration: none; 
            font-size: 14px;
            cursor: pointer;
            transition: background-color 0.3s ease, box-shadow 0.3s ease;
            font-weight: bold;
            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
        }

        .btn-accion:hover {
            box-shadow: 0 4px 8px rgba(0, 0, 0, 0.3);
        }

        .btn-editar {
            background-color: #007bff;
            color: white;
        }

        .btn-editar:hover {
            background-color: #0056b3; 
        }

        .btn-eliminar {
            background-color: #dc3545; 
            color: white;
        }

        .btn-eliminar:hover {
            background-color: #c82333; 
        }

    </style>
</head>
<body>

    <nav class="navbar">
        <div class="logo-container">
            <a href="${pageContext.request.contextPath}/index.jsp">
                <img src="${pageContext.request.contextPath}/Recursos/Logo.png" alt="Logo de Veterinaria Santa Cruz" class="logo" />
            </a>
        </div>
        <div class="nav-links">
            <div class="center-links">
                <a href="${pageContext.request.contextPath}/VistasWeb/VistasCliente/Nosotros.jsp" id="link-nosotros">Nosotros</a>
                <a href="${pageContext.request.contextPath}/VistasWeb/VistasCliente/servicios.jsp" id="link-servicios">Servicios</a>
                <a href="${pageContext.request.contextPath}/ProductoServlet?accion=listarCliente" id="link-productos">Productos</a>
                <a href="${pageContext.request.contextPath}/VistasWeb/VistasCliente/Contacto.jsp" id="link-contacto">Contacto</a>
            </div>
            <div class="buttons">
                <%
                    UsuarioCliente clienteLogueado = (UsuarioCliente) session.getAttribute("usuario");
                    if (clienteLogueado != null) {
                %>
                        <span>Hola, <%= clienteLogueado.getNombre() %></span> <%-- Usar getNombre() --%>
                        <a href="${pageContext.request.contextPath}/UsuarioMisCitasServlet" class="btn">Mis Citas</a>
                        <form action="${pageContext.request.contextPath}/LogoutServlet" method="post" style="display:inline;">
                            <button type="submit" class="btn">Cerrar Sesión</button>
                        </form>
                <%
                    } else {
                %>
                        <a href="<%= request.getContextPath() %>/index.jsp" class="btn login">Inicio Sesión</a>
                        <a href="<%= request.getContextPath() %>/index.jsp" class="btn register">Registrarse</a>
                <%
                    }
                %>
                <%-- El botón "Ver Perfil" puede ser manejado por JS, si no hay usuario logueado no debe aparecer --%>
                <% if (clienteLogueado != null) { %>
                    <a href="javascript:void(0)" class="btn perfil" id="verPerfilBtn">Ver Perfil</a>
                <% } %>
            </div>
        </div>
    </nav>

    <%
        String mensajeExito = (String) request.getAttribute("mensajeExito");  
        String mensajeError = (String) request.getAttribute("mensajeError"); 
        
        if (mensajeExito != null) {
    %>
            <div class="alert-success"><%= mensajeExito %></div>
    <%
        } else if (mensajeError != null) {  
    %>
            <div class="alert-error"><%= mensajeError %></div>
    <%
        }
    %>

    <div class="historial-container">
        <%
            // *** ESTE ES EL MENSAJE CLAVE PARA CUANDO NO HAY USUARIO LOGUEADO ***
            String mensajeNoLogueado = (String) request.getAttribute("mensajeNoLogueado");
            if (mensajeNoLogueado != null) {
        %>
                <div class="alert-error">
                    <%= mensajeNoLogueado %>
                </div>
        <%
            }

            // Obtener la lista de citas del request (enviada por el Servlet)
            List<UsuarioCitas> citas = (List<UsuarioCitas>) request.getAttribute("citasUsuario");

            // Obtener el nombre del usuario para el título (si está logueado y el Servlet lo envió)
            String nombreUsuarioParaTitulo = (String) request.getAttribute("nombreUsuarioLogueado");
            if (nombreUsuarioParaTitulo == null && clienteLogueado != null) {
                nombreUsuarioParaTitulo = clienteLogueado.getNombre(); // <-- Usar getNombre() aquí
            } else if (nombreUsuarioParaTitulo == null) {
                nombreUsuarioParaTitulo = "tus citas"; // Para el caso de "No logueado"
            }
        %>

        <h2>Historial de Citas de <%= nombreUsuarioParaTitulo %></h2>

        <table>
            <thead>
                <tr>
                    <th>ID Cita</th>
                    <th>Fecha</th>
                    <th>Hora</th>
                    <th>Veterinario</th>
                    <th>Motivo</th>
                    <th>Estado</th>
                    <th>Acciones</th> 
                </tr>
            </thead>
            <tbody>
                <%
                    if (citas != null && !citas.isEmpty()) {
                        LocalDate fechaActual = LocalDate.now();
                        LocalTime horaActual = LocalTime.now();
                        DateTimeFormatter timeFormatter12h = DateTimeFormatter.ofPattern("hh:mm a");

                        for (UsuarioCitas c : citas) {
                            String horaFormateada = "";
                            boolean esHoraPasada = false;
                            try {
                                LocalTime horaCita = c.getHora().toLocalTime();
                                horaFormateada = horaCita.format(timeFormatter12h).toLowerCase();

                                LocalDate fechaCita = c.getFecha().toLocalDate();
                                if (fechaCita.isBefore(fechaActual)) {
                                    esHoraPasada = true;
                                } else if (fechaCita.isEqual(fechaActual)) {
                                    esHoraPasada = horaCita.isBefore(horaActual);
                                }
                            } catch (Exception e) {
                                horaFormateada = c.getHora() != null ? c.getHora().toString() : ""; 
                                System.err.println("Error al formatear hora/fecha en JSP: " + e.getMessage());
                                esHoraPasada = false; 
                            }
                %>
                            <tr>
                                <td><%= c.getIdCita() %></td>
                                <td><%= c.getFecha() %></td>
                                <td class="hora-am-pm <%= esHoraPasada ? "hora-pasada" : "" %>" title="<%= esHoraPasada ? "Esta hora ya ha pasado" : "" %>">
                                    <%= horaFormateada %>
                                </td>
                                <td><%= c.getVeterinario() %></td>
                                <td><%= c.getMotivo() %></td>
                                <td><%= c.getEstado() %></td>
                                <td>

                                    <% if (!"Completada".equals(c.getEstado()) && !esHoraPasada) { %>
                                        <a href="javascript:void(0);" class="btn-accion btn-editar editar-cita-btn"
                                           data-id="<%= c.getIdCita() %>"
                                           data-fecha="<%= c.getFecha() %>"
                                           data-hora="<%= c.getHora() %>" <%-- Pasar la hora como String del Time --%>
                                           data-veterinario="<%= c.getVeterinario() %>"
                                           data-motivo="<%= c.getMotivo() %>"
                                           data-estado="<%= c.getEstado() %>">Editar</a>
                                        <a href="${pageContext.request.contextPath}/UsuarioMisCitasServlet?accion=eliminar&id=<%= c.getIdCita() %>"
                                           class="btn-accion btn-eliminar"
                                           onclick="return confirm('¿Estás seguro de eliminar esta cita?');">Eliminar</a>
                                    <% } else { %>
                                        No acciones
                                    <% } %>
                                </td>
                            </tr>
                <%
                        }
                    } else {
                %>
                        <tr>
                            <td colspan="7" style="text-align:center;">No tienes citas registradas.</td>
                        </tr>
                <%
                    }
                %>
            </tbody>
        </table>
    </div>

    <div id="editarCitaModal" class="modal">
        <div class="modal-content">
            <span class="close-button">&times;</span>
            <h2>Editar Cita</h2>

            <form id="editarCitaForm" action="${pageContext.request.contextPath}/UsuarioMisCitasServlet" method="post">
                <input type="hidden" name="accion" value="actualizarCita"> 
                <input type="hidden" id="editIdCita" name="idCita">

                <label for="editFecha">Fecha:</label>
                <input type="date" id="editFecha" name="fecha" required><br><br>

                <label for="editHora">Hora:</label>
                <input type="time" id="editHora" name="hora" required><br><br>

                <label for="editVeterinario">Veterinario:</label>
                <input type="text" id="editVeterinario" name="veterinario" required readonly><br><br>

                <label for="editMotivo">Motivo:</label>
                <textarea id="editMotivo" name="motivo" rows="3" required></textarea><br><br>

                <label for="editEstado">Estado:</label>
                <select id="editEstado" name="estado">
                    <option value="Pendiente">Pendiente</option>
                </select><br><br>

                <button type="submit" class="btn">Guardar Cambios</button>
            </form>
        </div>
    </div>

    <div id="sidebarPerfil" class="sidebar-perfil" role="dialog" aria-modal="true" aria-labelledby="perfilTitle">
        <h2 id="perfilTitle">Mi Perfil</h2>
        <a href="${pageContext.request.contextPath}/VistasWeb/VistasCliente/MiPerfil.jsp">Mi perfil</a>
        <a href="${pageContext.request.contextPath}/HistorialComprasServlet">Historial de compras/servicios</a>
        <a href="${pageContext.request.contextPath}/UsuarioMisCitasServlet">Citas agendadas</a> <%-- Enlaza al Servlet --%>
        <a href="${pageContext.request.contextPath}/LogoutServlet">Cerrar sesión</a>
    </div>
    <div id="sidebarOverlay"></div>

    <script>
    document.addEventListener('DOMContentLoaded', function() {

        const verPerfilBtn = document.getElementById('verPerfilBtn');
        if (verPerfilBtn) {
            verPerfilBtn.addEventListener('click', function() {
                document.getElementById('sidebarPerfil').classList.add('active');
                document.getElementById('sidebarOverlay').classList.add('active');
            });
        }
        const sidebarOverlay = document.getElementById('sidebarOverlay');
        if (sidebarOverlay) {
            sidebarOverlay.addEventListener('click', function() {
                document.getElementById('sidebarPerfil').classList.remove('active');
                this.classList.remove('active');
            });
        }

        const editarCitaModal = document.getElementById('editarCitaModal');
        const closeButton = document.querySelector('.modal .close-button'); 
        const editarCitaBtns = document.querySelectorAll('.editar-cita-btn'); 

        const editIdCitaInput = document.getElementById('editIdCita');
        const editFechaInput = document.getElementById('editFecha');
        const editHoraInput = document.getElementById('editHora');
        const editVeterinarioInput = document.getElementById('editVeterinario');
        const editMotivoInput = document.getElementById('editMotivo');
        const editEstadoSelect = document.getElementById('editEstado');

        editarCitaBtns.forEach(btn => {
            btn.addEventListener('click', function() {

                const idCita = this.dataset.id;
                const fecha = this.dataset.fecha;

                const hora = this.dataset.hora.substring(0, 5);  
                const veterinario = this.dataset.veterinario;
                const motivo = this.dataset.motivo;
                const estado = this.dataset.estado;


                editIdCitaInput.value = idCita;
                editFechaInput.value = fecha;
                editHoraInput.value = hora;
                editVeterinarioInput.value = veterinario;
                editMotivoInput.value = motivo;
                editEstadoSelect.value = estado;


                editarCitaModal.style.display = 'block';
            });
        });


        closeButton.addEventListener('click', function() {
            editarCitaModal.style.display = 'none';
        });

        window.addEventListener('click', function(event) {
            if (event.target == editarCitaModal) {
                editarCitaModal.style.display = 'none';
            }
        });

        const navLinks = document.querySelectorAll('.center-links a');
        const path = window.location.pathname.toLowerCase();
        navLinks.forEach(link => {
            const href = link.getAttribute('href').toLowerCase();
            if (path.includes('nosotros') && href.includes('nosotros')) {
                link.classList.add('active-link');
            } else if (path.includes('servicios') && href.includes('servicios')) {
                link.classList.add('active-link');
            } else if (path.includes('productos') && href.includes('productos')) {
                link.classList.add('active-link');
            } else if (path.includes('contacto') && href.includes('contacto')) {
                link.classList.add('active-link');
            } else if (path.includes('usuariomiscitasservlet') && href.includes('usuariomiscitasservlet')) { // Para el historial de citas
                link.classList.add('active-link');
            } else {
                link.classList.remove('active-link');
            }
        });
    });
    </script>

</body>
</html>