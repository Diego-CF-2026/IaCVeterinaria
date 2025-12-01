<%@ include file="/proteger.jsp" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@page import="java.util.List"%>
<%@page import="Modelo.Veterinario"%>
<%@page import="Modelo.Recepcionista"%>
<%@page import="Modelo.Especialidad"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.HashMap"%>
<%    List<Veterinario> listaVeterinarios = (List<Veterinario>) request.getAttribute("listaVeterinarios");
    List<Especialidad> listaEspecialidades = (List<Especialidad>) request.getAttribute("listaEspecialidades");
    List<Recepcionista> listaRecepcionistas = (List<Recepcionista>) request.getAttribute("listaRecepcionistas");
    Map<Integer, String> mapaEspecialidad = (Map<Integer, String>) request.getAttribute("mapaEspecialidad");

    // Obtener mensajes de éxito o error del servlet
    String mensajeExito = (String) request.getAttribute("mensaje");
    String mensajeError = (String) request.getAttribute("error");
    String searchQuery = (String) request.getAttribute("searchQuery"); // Obtener el término de búsqueda
    String activeTab = (String) request.getAttribute("activeTab"); // Obtener la pestaña activa

    // Función auxiliar para escapar cadenas de texto para JavaScript
    java.util.function.Function<String, String> escapeJsString = (
              
        text) -> {
        if (text == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '\\') {
                sb.append("\\\\");
            } else if (c == '\'') {
                sb.append("\\'");
            } else if (c == '"') {
                sb.append("\\\"");
            } else if (c == '\n') {
                sb.append("\\n");
            } else if (c == '\r') {
                sb.append("\\r");
            } else if (c == '<' && i + 7 <= text.length() && text.substring(i, i + 7).equalsIgnoreCase("</script>")) {
                sb.append("<\\/script>");
                i += 6; // Saltar los caracteres de "script>"
            } else if (c < 32 || c > 126) {
                sb.append(String.format("\\u%04x", (int) c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    };

    // Función auxiliar para escapar cadenas de texto para atributos HTML
    java.util.function.Function<String, String> escapeHtmlAttribute = (
              
        text) -> {
        if (text == null) {
            return "";
        }
        return text.replace("&", "&amp;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    };
%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Gestión de Empleados</title>
        <link href="https://unpkg.com/boxicons@2.1.1/css/boxicons.min.css" rel="stylesheet">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/ModoNoche-Sidebar.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estiloAdminEmpleados.css">
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
            <div class="tab-container">
                <div class="tab-header">
                    <button class="tab-button active" onclick="openTab(event, 'veterinarios')">Veterinarios</button>
                    <button class="tab-button" onclick="openTab(event, 'especialidades')">Especialidades</button>
                </div>

                <div class="search-bar">
                    <form id="searchForm" action="${pageContext.request.contextPath}/AdminEmpleadoServlet" method="GET">
                        <input type="hidden" name="accion" value="listar">
                        <input type="hidden" name="currentTab" id="currentTabHidden" value="<%= activeTab != null ? escapeHtmlAttribute.apply(activeTab) : "veterinarios"%>">
                        <input type="text" name="query" id="searchInput" placeholder="Buscar..." 
                               value="<%= searchQuery != null ? escapeHtmlAttribute.apply(searchQuery) : ""%>">
                        <button type="submit" class="btn btn-primary">Buscar</button>
                    </form>
                    <%-- Botón para generar reporte PDF --%>
                    <a href="${pageContext.request.contextPath}/ReporteEmpleadosServlet" class="btn-pdf">
                        <i class='bx bxs-file-pdf'></i> Generar Reporte PDF
                    </a>
                    
                </div>
                    <div id="veterinarios" class="tab-content active">
                        <button type="button" class="btn-agregar" onclick="mostrarModalAgregar()">
                            <i class='bx bx-plus'></i> Agregar Veterinario
                        </button>
                        <p style="opacity:.7">Especialidades cargadas: <%= (listaEspecialidades != null ? listaEspecialidades.size() : 0)%></p>

                        <table class="tabla-empleados">
                            <thead>
                                <tr>
                                    <th>Nombre</th>
                                    <th>Apellido</th>
                                    <th>Teléfono</th>
                                    <th>Correo</th>
                                    <th>Especialidad</th>
                                    <th>Acciones</th>
                                </tr>
                            </thead>
                            <tbody>
                                <%
                                    // **********************************
                                    // 1. Usar función para obtener el nombre de la especialidad
                                    // Esto es más limpio que usar out.print() dentro del <td>
                                    // Se asume que tienes una función Lambda 'escapeHtmlAttribute'
                                    // **********************************
                                    java.util.function.Function<Integer, String> getNombreEspecialidad = (  
                                        id) -> {
                                        String nomEsp = (mapaEspecialidad != null) ? mapaEspecialidad.get(id) : "Sin asignar";
                                        return escapeHtmlAttribute.apply(nomEsp);
                                    };

                                    if (listaVeterinarios != null && !listaVeterinarios.isEmpty()) {
                                        for (Veterinario v : listaVeterinarios) {
                                %>
                                <tr>
                                    <td><%= escapeHtmlAttribute.apply(v.getNombreVeterinario())%></td>
                                    <td><%= escapeHtmlAttribute.apply(v.getApellidoVeterinario())%></td>
                                    <td><%= escapeHtmlAttribute.apply(v.getTelefonoVeterinario())%></td>
                                    <td><%= escapeHtmlAttribute.apply(v.getCorreoVeterinario())%></td>
                                    <td>
                                        <%= getNombreEspecialidad.apply(v.getIdEspecialidad())%>
                                    </td>

                                    <td class="acciones">
                                        <button type="button" class="btn btn-editar"
                                                onclick="mostrarModalEditar(<%= v.getIdVeterinario()%>)">
                                            Editar
                                        </button>

                                        <form method="POST" action="<%= request.getContextPath()%>/AdminEmpleadoServlet" style="display:inline;">
                                            <input type="hidden" name="accion" value="eliminar">
                                            <input type="hidden" name="idVeterinario" value="<%= v.getIdVeterinario()%>">
                                            <input type="hidden" name="currentTab" value="veterinarios">

                                            <button type="submit" 
                                                    class="btn btn-eliminar"
                                                    onclick="return confirm('¿Está seguro de que desea ELIMINAR al veterinario: <%= escapeHtmlAttribute.apply(v.getNombreVeterinario())%>?');">
                                                Eliminar
                                            </button>
                                        </form>
                                    </td>
                                </tr>
                                <%
                                    }
                                } else {
                                %>
                                <tr><td colspan="7">No hay veterinarios registrados.</td></tr>
                                <%
                                    }
                                %>
                            </tbody>
                        </table>
                    </div>
                <!-- especialidades -->
                <div id="especialidades" class="tab-content">
                    <button type="button" class="btn-especialidad" onclick="mostrarModalEspecialidad()">
                        <i class='bx bx-star'></i> Agregar Especialidad
                    </button>

                    <!-- Debug temporal para ver si llegan datos -->
                    <p style="opacity:.7;margin-top:8px">
                        Especialidades cargadas: <%= (listaEspecialidades != null ? listaEspecialidades.size() : 0)%>
                    </p>

                    <table class="tabla-empleados" style="margin-top:8px">
                        <thead>
                            <tr>
                               
                                <th>Nombre</th>
                                <th>Precio (S/)</th>
                                <th>Acciones</th>
                            </tr>
                        </thead>
                        <tbody>
                            <%
                                if (listaEspecialidades != null && !listaEspecialidades.isEmpty()) {
                                    for (Especialidad e : listaEspecialidades) {
                            %>
                            <tr>
                                <td><%= escapeHtmlAttribute.apply(e.getNombreEspecialidad())%></td>
                                <td><%= String.format(java.util.Locale.US, "%.2f", e.getPrecio())%></td>
                                <td class="acciones">
                                    <button type="button" class="btn btn-editar"
                                            onclick="event.preventDefault(); mostrarModalEditarEspecialidad(<%= e.getIdEspecialidad()%>)">
                                        Editar
                                    </button>
                                    <form action="${pageContext.request.contextPath}/AdminEmpleadoServlet" method="POST" style="display:inline">
                                        <input type="hidden" name="accion" value="eliminarEspecialidad">
                                        <input type="hidden" name="idEspecialidad" value="<%= e.getIdEspecialidad()%>">
                                        <input type="hidden" name="currentTab" value="especialidades">
                                        <button type="submit" class="btn btn-eliminar"
                                                onclick="return confirm('¿Eliminar esta especialidad?');">
                                            Eliminar
                                        </button>
                                    </form>
                                </td>
                            </tr>
                            <%
                                }
                            } else {
                            %>
                            <tr><td colspan="4">No hay especialidades registradas.</td></tr>
                            <%
                                }
                            %>
                        </tbody>
                    </table>
                </div>
            </div>
        </main>

        <!-- Modal Agregar/Editar -->
        <div id="modalEmpleado" class="modal">
            <div class="modal-content">
                <span class="close" onclick="cerrarModal()">&times;</span>
                <h2 id="modalTitulo">Agregar Veterinario</h2>

                <form id="formEmpleado" action="${pageContext.request.contextPath}/AdminEmpleadoServlet" method="POST" autocomplete="off">
                    <input type="hidden" id="accion"     name="accion"        value="agregar">
                    <input type="hidden" id="idVeterinario" name="idVeterinario">
                    <input type="hidden" id="currentTabModal" name="currentTab" value="veterinarios">
                    <input type="hidden" name="tipoEmpleado" id="tipoEmpleado" value="veterinario">

                    <div class="form-group">
                        <label for="idEspecialidad">Especialidad</label>
                        <select id="idEspecialidad" name="idEspecialidad" required>
                            <option value="">-- Seleccione --</option>
                            <% if (listaEspecialidades != null)
                                    for (Especialidad e : listaEspecialidades) {%>
                            <option value="<%= e.getIdEspecialidad()%>">
                                <%= escapeHtmlAttribute.apply(e.getNombreEspecialidad())%> - S/ <%= e.getPrecio()%>
                            </option>
                            <% }%>
                        </select>

                    </div>
                    <div class="form-group">
                        <label for="nombreVeterinario">Nombre</label>
                        <input type="text" id="nombreVeterinario" name="nombreVeterinario" required>
                    </div>

                    <div class="form-group">
                        <label for="apellidoVeterinario">Apellido</label>
                        <input type="text" id="apellidoVeterinario" name="apellidoVeterinario" required>
                    </div>

                    <div class="form-group">
                        <label for="telefonoVeterinario">Teléfono (9 dígitos)</label>
                        <input type="text" id="telefonoVeterinario" name="telefonoVeterinario"
                               pattern="[0-9]{9}" maxlength="9" required
                               title="El teléfono debe tener 9 dígitos numéricos">
                    </div>

                    <div class="form-group">
                        <label for="correoVeterinario">Correo</label>
                        <input type="email" id="correoVeterinario" name="correoVeterinario">
                    </div>

                    <div class="form-group">
                        <label for="contrasenaVeterinario">Contraseña</label>
                        <input type="password"
                               id="contrasenaVeterinario"
                               name="contrasenaVeterinario"
                               minlength="6"
                               autocomplete="new-password"
                               placeholder="Mínimo 6 caracteres">
                        <label style="display:inline-flex;gap:8px;align-items:center;margin-top:6px;cursor:pointer;">
                            <input type="checkbox" onchange="
                                    const p = document.getElementById('contrasenaVeterinario');
                                    p.type = this.checked ? 'text' : 'password';
                                   "> Mostrar contraseña
                        </label>
                    </div>


                    <div class="form-actions" style="text-align:right; margin-top:12px;">
                        <button type="button" class="btn" onclick="cerrarModal()">Cancelar</button>
                        <button type="submit" class="btn btn-primary">Guardar</button>
                    </div>
                </form>
            </div>
        </div>

        <!-- Modal Especialidad -->
        <div id="modalEspecialidad" class="modal">
            <div class="modal-content">
                <span class="close" onclick="cerrarModalEspecialidad()">&times;</span>
                <h2>Especialidad</h2>

                <form id="formEspecialidad" action="${pageContext.request.contextPath}/AdminEmpleadoServlet" method="POST" autocomplete="off">
                    <input type="hidden" name="accion" id="accionEspecialidad" value="agregarEspecialidad">
                    <input type="hidden" name="idEspecialidad" id="idEspecialidadHidden">
                    <input type="hidden" name="currentTab" value="especialidades">

                    <div class="form-group">
                        <label for="nombreEspecialidad">Nombre</label>
                        <input type="text" id="nombreEspecialidad" name="nombreEspecialidad" maxlength="100" required>
                    </div>

                    <div class="form-group">
                        <label for="precioEspecialidad">Precio (S/)</label>
                        <input type="number" id="precioEspecialidad" name="precio" min="0" step="0.01" required>
                    </div>

                    <div class="form-actions" style="text-align:right; margin-top:12px;">
                        <button type="button" class="btn btn-secondary" onclick="cerrarModalEspecialidad()">Cancelar</button>
                        <button type="submit" class="btn btn-primary">Guardar</button>
                    </div>
                </form>

            </div>
        </div>
                    
        <div id="modalVer" class="modal">
            <div class="modal-content">
                <span class="close" onclick="cerrarModalVer()">&times;</span>
                <h2 id="modalVerTitulo">Detalles del Empleado</h2>
                <div id="detallesEmpleado">
                </div>
                <div class="form-actions">
                    <button type="button" class="btn btn-secondary" onclick="cerrarModalVer()">Cerrar</button>
                </div>
            </div>
        </div>     
        <button id="modoNocheBtn" class="modo-noche-flotante" aria-label="Cambiar a modo noche">🌙</button>
        <script src="${pageContext.request.contextPath}/Js/JsAdmin/ModoNoche-Sidebar.js"></script>

        <script>
                        (function () {
                            // Evita que <a href="#"> navegue y rompa los eventos
                            document.addEventListener('click', function (e) {
                                const a = e.target.closest('a[href="#"]');
                                if (a)
                                    e.preventDefault();
                            });

                            /* Tabs */
                            window.openTab = function (evt, id) {
                                // Oculta todas
                                const tabs = document.getElementsByClassName("tab-content");
                                for (let t of tabs)
                                    t.style.display = "none";
                                // Quita 'active' a todos los botones
                                const btns = document.getElementsByClassName("tab-button");
                                for (let b of btns)
                                    b.classList.remove("active");
                                // Muestra la pestaña solicitada
                                const cont = document.getElementById(id);
                                if (cont)
                                    cont.style.display = "block";

                                // Marca el botón (si viene por onclick inline)
                                if (evt && evt.currentTarget) {
                                    evt.currentTarget.classList.add("active");
                                } else {
                                    // Si se llamó programáticamente, marca el que tenga el data-tab (opcional)
                                    const btn = Array.from(btns).find(b => (b.dataset && b.dataset.tab === id));
                                    if (btn)
                                        btn.classList.add("active");
                                }

                                // Actualiza hiddens si existen
                                const h1 = document.getElementById("currentTabHidden");
                                if (h1)
                                    h1.value = id;
                                const h2 = document.getElementById("currentTabModal");
                                if (h2)
                                    h2.value = id;
                            };

                            /* Modal helpers */
                            window.mostrarModalAgregar = function () {
                                const f = document.getElementById("formEmpleado");
                                if (f)
                                    f.reset();

                                const acc = document.getElementById("accion");
                                if (acc)
                                    acc.value = "agregar";

                                const idV = document.getElementById("idVeterinario");
                                if (idV)
                                    idV.value = "";

                                const tipo = document.getElementById("tipoEmpleado");
                                if (tipo)
                                    tipo.value = "veterinario";

                                const titulo = document.getElementById("modalTitulo");
                                if (titulo)
                                    titulo.textContent = "Agregar Veterinario";

                                const m = document.getElementById("modalEmpleado");
                                if (m)
                                    m.style.display = "flex";
                            };

                            window.mostrarModalEditar = function (id) {
                                const f = document.getElementById("formEmpleado");
                                if (f)
                                    f.reset();
                                document.getElementById("accion").value = "actualizar";
                                document.getElementById("idVeterinario").value = id;
                                document.getElementById("tipoEmpleado").value = "veterinario";
                                document.getElementById("modalTitulo").textContent = "Editar Veterinario";

                                fetch('<%= request.getContextPath()%>/AdminEmpleadoServlet?accion=obtener&idEmpleado=' + encodeURIComponent(id))
                                        .then(res => {
                                            if (!res.ok)
                                                throw new Error('No se pudo cargar el veterinario');
                                            return res.json();
                                        })
                                        .then(data => {
                                            document.getElementById("nombreVeterinario").value = data.nombreVeterinario || '';
                                            document.getElementById("apellidoVeterinario").value = data.apellidoVeterinario || '';
                                            document.getElementById("telefonoVeterinario").value = data.telefonoVeterinario || '';
                                            document.getElementById("correoVeterinario").value = data.correoVeterinario || '';
                                            document.getElementById("idEspecialidad").value = (data.idEspecialidad != null ? String(data.idEspecialidad) : '');
                                            document.getElementById("modalEmpleado").style.display = "flex";
                                        })
                                        .catch(err => alert("No se pudo cargar el veterinario: " + err.message));
                            };


                            window.cerrarModal = function () {
                                const m = document.getElementById("modalEmpleado");
                                if (m)
                                    m.style.display = "none";
                            };

                            window.mostrarModalEspecialidad = function () {
                                const form = document.getElementById('formEspecialidad') || document.querySelector('#modalEspecialidad form');
                                if (form) {
                                    form.reset();
                                    const acc = document.getElementById('accionEspecialidad');
                                    if (acc)
                                        acc.value = 'agregarEspecialidad';
                                    const idH = document.getElementById('idEspecialidadHidden');
                                    if (idH)
                                        idH.value = '';
                                    const tab = form.querySelector('input[name="currentTab"]');
                                    if (tab)
                                        tab.value = 'especialidades';
                                }
                                const m = document.getElementById('modalEspecialidad');
                                if (m)
                                    m.style.display = 'flex';
                            };

                            window.mostrarModalEditarEspecialidad = function (id) {
                                document.getElementById('accionEspecialidad').value = 'actualizarEspecialidad';
                                document.getElementById('idEspecialidadHidden').value = id;

                                fetch('<%= request.getContextPath()%>/AdminEmpleadoServlet?accion=obtenerEspecialidad&idEspecialidad=' + encodeURIComponent(id))
                                        .then(res => {
                                            if (!res.ok)
                                                throw new Error('No se pudo obtener la especialidad');
                                            return res.json();
                                        })
                                        .then(data => {
                                            document.getElementById('nombreEspecialidad').value = data.nombreEspecialidad || '';
                                            document.getElementById('precioEspecialidad').value = (data.precio != null ? data.precio : '');
                                            document.getElementById('modalEspecialidad').style.display = 'flex';
                                        })
                                        .catch(err => alert(err.message));
                            };


                            window.cerrarModalEspecialidad = function () {
                                const m = document.getElementById('modalEspecialidad');
                                if (m)
                                    m.style.display = 'none';
                            };

                            window.cerrarModalVer = function () {
                                const m = document.getElementById('modalVer');
                                if (m)
                                    m.style.display = 'none';
                            };

                            // Cerrar al click fuera del contenido
                            window.addEventListener('click', function (e) {
                                ['modalEmpleado', 'modalEspecialidad', 'modalVer'].forEach(id => {
                                    const m = document.getElementById(id);
                                    if (m && e.target === m)
                                        m.style.display = 'none';
                                });
                            });

                            // Inicialización segura
                            document.addEventListener('DOMContentLoaded', function () {
                                // Asegura valor válido
                                let activeTabOnLoad = "<%= activeTab != null ? escapeJsString.apply(activeTab) : "veterinarios"%>";
                                // Normaliza a ids existentes
                                if (!document.getElementById(activeTabOnLoad))
                                    activeTabOnLoad = 'veterinarios';

                                // Muestra la pestaña inicial
                                openTab(null, activeTabOnLoad);

                                // Sincroniza hiddens si existen
                                const h1 = document.getElementById("currentTabHidden");
                                if (h1)
                                    h1.value = activeTabOnLoad;
                                const h2 = document.getElementById("currentTabModal");
                                if (h2)
                                    h2.value = activeTabOnLoad;
                            });
                        })();


        </script>


    </body>
</html>