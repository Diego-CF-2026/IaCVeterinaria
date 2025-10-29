<%-- ESTE ARCHIVO NO DEBE TENER DIRECTIVAS @page --%>
<%@page import="jakarta.servlet.http.HttpSession"%>
<%
    // IDs de Rol basados en tu tabla 'rol'
    final int ID_ADMIN = 1;
    final int ID_RECEPCIONISTA = 2;
    final int ID_CLIENTE = 3;
    final int ID_VETERINARIO = 4; // 🟢 NUEVA CONSTANTE

    // Configuración de seguridad de caché (opcional, pero buena práctica)
    response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
    response.setHeader("Pragma", "no-cache");
    response.setDateHeader("Expires", 0);

    // Obtener sesión sin crear nueva
    HttpSession sesion = request.getSession(false);
    String contextPath = request.getContextPath();
    String requestURI = request.getRequestURI();

    // ----------------------------------------------------
    // 1. OBTENER ROL E IDENTIFICAR ÁREA DE ACCESO
    // ----------------------------------------------------

    // Obtener el ID de Rol de la sesión. Es el único atributo de rol que necesitas.
    Integer idRol = (sesion != null) ? (Integer) sesion.getAttribute("idRol") : null;
    
    // Rutas (basadas en la estructura de tus carpetas)
    boolean accedeAdmin = requestURI.contains("/VistasAdmin/");
    boolean accedeCliente = requestURI.contains("/VistasCliente/");
    boolean accedeRecep = requestURI.contains("/VistasRecep/");
    boolean accedeVeterinario = requestURI.contains("/VistasVeterinario/"); // 🟢 NUEVA RUTA

    // ----------------------------------------------------
    // 2. VALIDACIÓN DE SESIÓN (Si no hay sesión, va al inicio)
    // ----------------------------------------------------
    if (idRol == null) {
        // Redirigir a la página de inicio o login si no hay sesión activa
        response.sendRedirect(contextPath + "/index.jsp");  
        return;
    }

    // ----------------------------------------------------
    // 3. VALIDACIÓN DE PERMISOS POR ROL
    // ----------------------------------------------------

    // Acceso a VistasAdmin
    if (accedeAdmin && idRol != ID_ADMIN) {
        response.sendRedirect(contextPath + "/sin_permisos.jsp");
        return;
    }
    
    // 🟢 Acceso a VistasVeterinario
    if (accedeVeterinario && idRol != ID_VETERINARIO) {
        // Permitimos el acceso si es Admin (ID_ADMIN)
        if (accedeVeterinario && idRol != ID_VETERINARIO && idRol != ID_ADMIN) {
            response.sendRedirect(contextPath + "/sin_permisos.jsp");
            return;
        }
    }

    // Acceso a VistasCliente
    if (accedeCliente && idRol != ID_CLIENTE) {
        // Un caso especial: a menudo el Admin puede ver las vistas del Cliente.
        if (accedeCliente && idRol != ID_CLIENTE && idRol != ID_ADMIN) {
            response.sendRedirect(contextPath + "/sin_permisos.jsp");
            return;
        }
    }

    // Acceso a VistasRecep
    if (accedeRecep && idRol != ID_RECEPCIONISTA) {
        // El Administrador a menudo puede acceder a vistas de Recepcionista
        if (accedeRecep && idRol != ID_RECEPCIONISTA && idRol != ID_ADMIN) {
            response.sendRedirect(contextPath + "/sin_permisos.jsp");
            return;
        }
    }

    // ----------------------------------------------------
    // 4. REDIRECCIÓN A DASHBOARD PROPIO
    // ----------------------------------------------------
    
    // Si un Admin intenta navegar a otra ruta de usuario
    if (idRol == ID_ADMIN && (accedeCliente || accedeRecep || accedeVeterinario)) { // 🟢 Añadida VistasVeterinario
        response.sendRedirect(contextPath + "/VistasWeb/VistasAdmin/AdminDash.jsp");
        return;
    }
    
    // Si un Cliente intenta navegar a otra ruta de rol
    if (idRol == ID_CLIENTE && (accedeAdmin || accedeRecep || accedeVeterinario)) { // 🟢 Añadida VistasVeterinario
        response.sendRedirect(contextPath + "/VistasWeb/VistasCliente/indexCliente.jsp");
        return;
    }
    
    // Si un Recepcionista intenta navegar a otra ruta de rol
    if (idRol == ID_RECEPCIONISTA && (accedeAdmin || accedeCliente || accedeVeterinario)) { // 🟢 Añadida VistasVeterinario
        response.sendRedirect(contextPath + "/VistasWeb/VistasRecep/RecepDash.jsp");  
        return;
    }
    
    // 🟢 NUEVA VALIDACIÓN: Si un Veterinario intenta navegar a otra ruta de rol
    if (idRol == ID_VETERINARIO && (accedeAdmin || accedeCliente || accedeRecep)) {
        response.sendRedirect(contextPath + "/VistasWeb/VistasVeterinario/VeterinarioDash.jsp");  
        return;
    }
    
    // Si todo es correcto, el JSP continúa su ejecución.
%>