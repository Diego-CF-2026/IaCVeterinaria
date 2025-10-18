<%@ include file="/proteger.jsp" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="java.time.LocalDate" %>
<%@ page import="java.util.List" %>
<%@ page import="Modelo.Usuario" %>
<%@ page import="Modelo.Cliente" %>
<%@ page import="Modelo.Veterinario" %>

<%
    // 🟢 Obtenemos el usuario logueado (correo, idUsuario, etc.)
    Usuario usuarioSesion = (Usuario) session.getAttribute("usuario");
    String usuarioCorreo = (usuarioSesion != null) ? usuarioSesion.getCorreo() : "";
    int idUsuario = (usuarioSesion != null) ? usuarioSesion.getIdUsuario() : 0;

    // 🟢 Obtenemos los datos del cliente asociados (si fueron cargados por el servlet)
    Cliente clienteObj = (Cliente) request.getAttribute("cliente");
    String clienteNombre = (clienteObj != null) ? clienteObj.getNombre() : "";
    int idCliente = (clienteObj != null) ? clienteObj.getIdCliente() : 0;

    // 🕒 Otros datos necesarios
    String mensaje = request.getParameter("mensaje");
    LocalDate fechaActual = LocalDate.now();
    List<Veterinario> listaVeterinarios = (List<Veterinario>) request.getAttribute("listaVeterinarios");
%>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Registrar Cita | Veterinaria Santa Cruz</title>
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;600&display=swap" rel="stylesheet"/>
    <style>
        body { font-family: 'Poppins', sans-serif; background: #f8f8f8; margin: 0; }
        .navbar {
            display: flex; justify-content: space-between; align-items: center;
            background: #3aafa9; padding: 0 30px; height: 70px;
        }
        .logo { height: 56px; }
        .center-links a {
            color: #fff; text-decoration: none; margin: 0 16px; font-weight: 500;
            transition: color 0.2s;
        }
        .center-links a:hover, .active-link { color: #17252a; border-bottom: 2px solid #fff; }
        .buttons .btn {
            background: #def2f1; color: #3aafa9; padding: 8px 18px; border-radius: 20px;
            text-decoration: none; font-weight: 600;
        }
        .registro-cita {
            max-width: 600px; margin: 40px auto; padding: 20px;
            border: 1px solid #ccc; border-radius: 8px; background: #fff;
        }
        .registro-cita h3 { text-align: center; color: #3aafa9; font-weight: 600; }
        .registro-cita label { display: block; margin-top: 15px; font-weight: 500; }
        .registro-cita input, .registro-cita select, .registro-cita textarea {
            width: 100%; padding: 10px; border: 1px solid #999; border-radius: 4px;
        }
        .registro-cita .button-group { display: flex; gap: 15px; margin-top: 25px; }
        .registro-cita button {
            flex: 1; padding: 12px; border: none; border-radius: 5px; cursor: pointer; font-weight: 600;
        }
        button[type="submit"] { background: #3aafa9; color: white; }
        button[type="submit"]:hover { background: #2f8f8a; }
        button[type="button"] { background: #f44336; color: white; }
        button[type="button"]:hover { background: #d32f2f; }
        .alert-success, .alert-error {
            max-width: 600px; margin: 20px auto; padding: 15px; border-radius: 5px; text-align: center;
        }
        .alert-success { background: #d4edda; color: #155724; }
        .alert-error { background: #f8d7da; color: #721c24; }
        .error-message { color: #dc3545; font-size: 0.9rem; margin-top: 5px; display: none; }
    </style>
</head>
<body>

<nav class="navbar">
    <a href="${pageContext.request.contextPath}/index.jsp">
        <img src="${pageContext.request.contextPath}/Recursos/Logo.png" alt="Logo" class="logo"/>
    </a>
    <div class="center-links">
        <a href="${pageContext.request.contextPath}/VistasWeb/VistasCliente/Nosotros.jsp">Nosotros</a>
        <a href="${pageContext.request.contextPath}/VistasWeb/VistasCliente/servicios.jsp">Servicios</a>
        <a href="${pageContext.request.contextPath}/ProductoServlet?accion=listarCliente">Productos</a>
        <a href="${pageContext.request.contextPath}/VistasWeb/VistasCliente/Contacto.jsp">Contacto</a>
    </div>
    <div class="buttons">
        <a href="javascript:void(0)" class="btn" id="verPerfilBtn">Ver Perfil</a>
    </div>
</nav>

<!-- 🔔 Mensajes -->
<% if (mensaje != null) { %>
    <% if ("registrado".equals(mensaje)) { %>
        <div class="alert-success">¡Cita registrada correctamente!</div>
    <% } else if ("error_registro".equals(mensaje)) { %>
        <div class="alert-error">Error al registrar la cita. Intenta nuevamente.</div>
    <% } else if ("fecha_hora_pasada".equals(mensaje)) { %>
        <div class="alert-error">No puedes seleccionar una fecha u hora pasada.</div>
    <% } else { %>
        <div class="alert-error">Ocurrió un error inesperado. Vuelve a intentarlo.</div>
    <% } %>
<% } %>

<!-- 📅 Formulario de Cita -->
<section class="registro-cita">
    <h3>Registrar Nueva Cita</h3>
    <form id="formCita" action="${pageContext.request.contextPath}/UsuarioCitasServlet" method="post" onsubmit="return validarFechaHora()">
        <input type="hidden" name="accion" value="registrar"/>
        <input type="hidden" name="idCliente" value="<%= idCliente %>"/>

        <label>Cliente:</label>
        <input type="text" name="nombre" value="<%= clienteNombre %>" readonly/>

        <label>Fecha:</label>
        <input type="date" id="fecha" name="fecha" min="<%= fechaActual %>" required>
        <div id="errorFecha" class="error-message">No puedes seleccionar una fecha pasada.</div>

        <label>Hora:</label>
        <input type="time" id="hora" name="hora" required>
        <div id="errorHora" class="error-message">No puedes seleccionar una hora pasada para hoy.</div>

        <label>Veterinario:</label>
        <select name="idVeterinario" required>
            <option value="">-- Seleccione un veterinario --</option>
            <%
                List<Veterinario> listarVeterinarios = (List<Veterinario>) request.getAttribute("listaVeterinarios");
                if (listaVeterinarios != null && !listaVeterinarios.isEmpty()) {
                    for (Veterinario v : listaVeterinarios) {
            %>
                        <option value="<%=v.getIdVeterinario()%>">
                            <%=v.getNombreVeterianrio()%> <%=v.getApellidoVeterinario()%> - <%=v.getNombreEspecialidad()%>
                        </option>
            <%
                    }
                } else {
            %>
                    <option disabled>No hay veterinarios disponibles</option>
            <%
                }
            %>
        </select>


        <label>Motivo de la Cita:</label>
        <textarea id="motivo" name="motivo" rows="3" required></textarea>

        <div class="button-group">
            <button type="submit">Registrar Cita</button>
            <button type="button" onclick="window.location.href='${pageContext.request.contextPath}/VistasWeb/VistasCliente/servicios.jsp'">Salir</button>
        </div>
    </form>
</section>

<script>
function validarFechaHora() {
    const fecha = document.getElementById('fecha');
    const hora = document.getElementById('hora');
    const errorFecha = document.getElementById('errorFecha');
    const errorHora = document.getElementById('errorHora');
    const hoy = new Date();
    const fechaSel = new Date(fecha.value + "T00:00");
    errorFecha.style.display = errorHora.style.display = 'none';

    if (fechaSel < new Date(hoy.getFullYear(), hoy.getMonth(), hoy.getDate())) {
        errorFecha.style.display = 'block';
        return false;
    }
    if (fechaSel.toDateString() === hoy.toDateString()) {
        const [h, m] = hora.value.split(':');
        const horaSel = new Date();
        horaSel.setHours(h, m, 0, 0);
        if (horaSel < hoy) {
            errorHora.style.display = 'block';
            return false;
        }
    }
    return true;
}
document.addEventListener('DOMContentLoaded', () => {
    const today = new Date().toISOString().split('T')[0];
    document.getElementById('fecha').setAttribute('min', today);
});
</script>

</body>
</html>