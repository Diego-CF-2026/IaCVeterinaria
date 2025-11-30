<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="Modelo.Cita"%>
<%@ include file="/proteger.jsp" %>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Agenda de Citas - Veterinaria Santa Cruz</title>
    <link href="https://unpkg.com/boxicons@2.1.1/css/boxicons.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/ModoNoche-Sidebar.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/AgendarCitaVete.css">
    <!-- ✅ SWEETALERT2 -->
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>

</head>
<body>

    <!-- SIDEBAR -->
    <nav class="sidebar">
        <header>
            <div class="image-text">
                <span class="image">
                    <img src="${pageContext.request.contextPath}/Recursos/Logo.png" alt="Logo" class="logo">
                </span>
                <div class="header-text">
                    <span class="name">Veterinario</span>
                    <span class="profession">Veterinaria Santa Cruz</span>
                </div>
            </div>
        </header>
        <div class="menu-bar">
            <ul class="menu-links">
                <li class="nav-link">
                    <a href="${pageContext.request.contextPath}/VistasWeb/VistasVeterinario/VeterinarioDash.jsp">
                        <i class='bx bx-home-alt icon'></i><span class="text">General</span>
                    </a>
                </li>
                <li class="nav-link active">
                    <a href="${pageContext.request.contextPath}/VeterinarioCitasServlet?accion=listar">
                        <i class='bx bx-calendar-check icon'></i><span class="text">Citas</span>
                    </a>
                </li>
                <li class="nav-link">
                    <a href="${pageContext.request.contextPath}/BuscarHistorialServlet">
                        <i class='bx bx-book icon'></i><span class="text">Ver Historial Médico</span>
                    </a>
                </li>
                <li class="nav-link">
                    <a href="${pageContext.request.contextPath}/LogoutServlet">
                        <i class='bx bx-log-out icon'></i><span class="text">Salir</span>
                    </a>
                </li>
            </ul>
        </div>
    </nav>

    <!-- CONTENIDO PRINCIPAL -->
    <main>
        <h1><i class='bx bx-calendar-check'></i> Agenda de Citas</h1>

        <div class="table-container">
            <%
                List<Cita> lista = (List<Cita>) request.getAttribute("listaCitas");
                if (lista != null && !lista.isEmpty()) {
            %>
            <table>
                <thead>
                    <tr>
                        <th>Cliente</th>
                        <th>DNI</th>
                        <th>Fecha</th>
                        <th>Hora</th>
                        <th>Motivo</th>
                        <th>Acciones</th>
                    </tr>
                </thead>
                <tbody>
                <% for (Cita c : lista) { %>
                    <tr>
                        <td><%= c.getNombreCliente() %></td>
                        <td><%= c.getDniCliente() %></td>
                        <td><%= c.getFecha() %></td>
                        <td><%= c.getHora() %></td>
                        <td><%= c.getMotivo() %></td>
                        <td>
                            <button class="btn btn-tratamiento" onclick="abrirModalTratamiento('<%= c.getIdCita() %>', '<%= c.getDniCliente() %>')">
                                <i class='bx bx-plus-medical'></i> Registrar
                            </button>
                            <button class="btn btn-reprogramar" onclick="abrirModalReprogramar('<%= c.getIdCita() %>')">
                                <i class='bx bx-time-five'></i> Reprogramar
                            </button>
                        </td>
                    </tr>
                <% } %>
                </tbody>
            </table>
            <% } else { %>
                <p style="text-align:center;">No hay citas registradas.</p>
            <% } %>
        </div>
    </main>

    <!-- MODAL TRATAMIENTO -->
    <div id="modalTratamiento" class="modal">
        <div class="modal-content">
            <span class="close" onclick="cerrarModal('modalTratamiento')">&times;</span>
            <h2><i class='bx bx-plus-medical'></i> Registrar Tratamiento</h2>
            <form id="formTratamiento" action="${pageContext.request.contextPath}/VeterinarioCitasServlet" method="post">
                <input type="hidden" name="accion" value="registrarTratamiento">
                <input type="hidden" id="idCitaTratamiento" name="idCita">
                <input type="hidden" id="dniClienteTratamiento" name="dniCliente">

                <label>Nombre de la Mascota:</label>
                <input type="text" name="nombreMascota" placeholder="Ej: Luna, Rocky, Max" required>

                <label>Diagnóstico:</label>
                <textarea name="diagnostico" rows="4" placeholder="Describa el diagnóstico" required></textarea>

                <label>Tratamiento:</label>
                <textarea name="tratamiento" rows="4" placeholder="Describa el tratamiento" required></textarea>

                <label>Notas adicionales:</label>
                <textarea name="notas" rows="3" placeholder="Observaciones del veterinario"></textarea>

                <button type="submit" class="btn btn-tratamiento"><i class='bx bx-save'></i> Guardar</button>
            </form>
        </div>
    </div>

    <!-- MODAL REPROGRAMAR -->
    <div id="modalReprogramar" class="modal">
        <div class="modal-content">
            <span class="close" onclick="cerrarModal('modalReprogramar')">&times;</span>
            <h2><i class='bx bx-time-five'></i> Reprogramar Cita</h2>
            <form action="${pageContext.request.contextPath}/VeterinarioCitasServlet" method="post">
                <input type="hidden" name="accion" value="reprogramarCita">
                <input type="hidden" id="idCitaReprogramar" name="idCita">

                <label>Nueva Fecha:</label>
                <input type="date" name="nuevaFecha" required>

                <label>Nueva Hora:</label>
                <input type="time" name="nuevaHora" required>

                <button type="submit" class="btn btn-reprogramar"><i class='bx bx-refresh'></i> Confirmar</button>
            </form>
        </div>
    </div>

    <button id="modoNocheBtn" class="modo-noche-flotante">🌙</button>
    <script src="<%= request.getContextPath()%>/Js/JsAdmin/ModoNoche-Sidebar.js"></script>

    <script>
        function abrirModalTratamiento(idCita, dniCliente) {
            document.getElementById('modalTratamiento').style.display = 'block';
            document.getElementById('idCitaTratamiento').value = idCita;
            document.getElementById('dniClienteTratamiento').value = dniCliente;
        }

        function abrirModalReprogramar(idCita) {
            document.getElementById('modalReprogramar').style.display = 'block';
            document.getElementById('idCitaReprogramar').value = idCita;
        }

        function cerrarModal(id) {
            document.getElementById(id).style.display = 'none';
        }

        window.onclick = function(event) {
            if (event.target.classList.contains('modal')) {
                event.target.style.display = 'none';
            }
        }

        // ✅ Confirmación antes de registrar tratamiento
        document.getElementById("formTratamiento").addEventListener("submit", function(event) {
            event.preventDefault();
            Swal.fire({
                title: "¿Registrar tratamiento?",
                text: "Confirma que deseas guardar este tratamiento.",
                icon: "question",
                showCancelButton: true,
                confirmButtonColor: "#28a745",
                cancelButtonColor: "#d33",
                confirmButtonText: "Sí, registrar",
                cancelButtonText: "Cancelar"
            }).then((result) => {
                if (result.isConfirmed) {
                    this.submit();
                }
            });
        });
    </script>
</body>
</html>
