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
    <!-- ✅ SWEETALERT2 -->
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>

    <style>
        body {
            font-family: 'Poppins', sans-serif;
            display: flex;
            background-color: var(--body-color);
            color: var(--text-color);
            margin: 0;
            overflow-x: hidden;
        }

        main {
            margin-left: 270px;
            padding: 40px;
            width: calc(100% - 270px);
            min-height: 100vh;
            transition: 0.3s ease;
        }

        h1 {
            text-align: center;
            font-size: 1.8rem;
            color: var(--primary-color);
            margin-bottom: 25px;
        }

        .table-container {
            background: var(--sidebar-color);
            border-radius: 15px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.2);
            padding: 20px;
            overflow-x: auto;
            transition: background-color 0.3s ease;
        }

        body.modo-claro .table-container {
            background-color: #fff !important;
            color: #333;
        }

        table {
            width: 100%;
            border-collapse: collapse;
            text-align: center;
        }

        th {
            background-color: var(--primary-color);
            color: white;
            font-weight: 600;
            padding: 12px;
        }

        td {
            padding: 12px;
            border-bottom: 1px solid rgba(255,255,255,0.1);
        }

        tr:hover {
            background-color: var(--hover-color);
        }

        .btn {
            display: inline-flex;
            align-items: center;
            justify-content: center;
            gap: 6px;
            padding: 8px 14px;
            border-radius: 8px;
            color: #fff;
            font-weight: 500;
            border: none;
            cursor: pointer;
            transition: 0.3s;
        }

        .btn-tratamiento { background-color: #28a745; }
        .btn-tratamiento:hover { background-color: #218838; }

        .btn-reprogramar { background-color: #007bff; }
        .btn-reprogramar:hover { background-color: #0069d9; }

        .modal {
            display: none;
            position: fixed;
            z-index: 1000;
            inset: 0;
            background-color: rgba(0,0,0,0.6);
            backdrop-filter: blur(3px);
        }

        .modal-content {
            background-color: var(--sidebar-color);
            color: var(--text-color);
            width: 65%;
            max-width: 750px;
            margin: 5% auto;
            padding: 30px 40px;
            border-radius: 15px;
            position: relative;
            box-shadow: 0 0 20px rgba(0,0,0,0.4);
            transition: background-color 0.3s ease;
        }

        body.modo-claro .modal-content {
            background-color: #fff !important;
            color: #333 !important;
        }

        .modal h2 {
            text-align: center;
            margin-bottom: 20px;
            color: var(--primary-color);
            font-size: 1.4rem;
        }

        .modal label {
            display: block;
            margin-top: 12px;
            font-weight: 600;
            font-size: 1.05rem;
        }

        .modal textarea, .modal input {
            width: 100%;
            padding: 12px;
            margin-top: 5px;
            border-radius: 8px;
            border: 1px solid #ccc;
            background-color: var(--body-color);
            color: var(--text-color);
            font-size: 1rem;
        }

        body.modo-claro .modal textarea,
        body.modo-claro .modal input {
            background-color: #f8f8f8;
            color: #333;
        }

        .modal .close {
            position: absolute;
            top: 15px;
            right: 20px;
            font-size: 28px;
            color: var(--primary-color);
            cursor: pointer;
        }

        @media (max-width: 900px) {
            main { margin-left: 0; width: 100%; padding: 20px; }
            .modal-content { width: 90%; padding: 25px; }
        }
    </style>
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
