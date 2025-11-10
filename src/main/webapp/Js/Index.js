let rolSeleccionado = '';

// Función auxiliar para obtener y verificar un elemento de forma segura
function getElement(id) {
    return document.getElementById(id);
}

// Funciones básicas de modales
function abrirModal(idModal) {
    const modal = getElement(idModal);
    if (modal) modal.style.display = 'flex';
}

function cerrarModal(idModal) {
    const modal = getElement(idModal);
    if (modal) modal.style.display = 'none';
}

function abrirLogin(rol) {
    rolSeleccionado = rol || '';
    
    // Ocultar modales anteriores de forma segura, corrigiendo el error de sintaxis.
    cerrarModal('modalSeleccionInicial'); // Usa la función segura
    cerrarModal('modalEquipo'); // Usa la función segura
    
    const modalLogin = getElement('modalLogin');
    const titulo = getElement('tituloLogin');
    const inputRol = getElement('inputRol');
    const opcionesRegistro = getElement('opcionesRegistro');
    
    // La línea que causaba error estaba aquí. Hemos reemplazado la lógica.
    // document.getElementById('modalSeleccionInicial')?.style.display = 'none'; // ¡QUITADO!

    if (inputRol) inputRol.value = rol;
    if (titulo) titulo.textContent = rol === 'Cliente' ? 'Iniciar Sesión (Cliente)' : 'Iniciar Sesión';
    if (opcionesRegistro) opcionesRegistro.style.display = rol === 'Cliente' ? 'block' : 'none';

    abrirModal('modalLogin');
}

function mostrarModalEquipo() {
    // Corrige el error de sintaxis del operador ?. en la asignación
    cerrarModal('modalSeleccionInicial');
    abrirModal('modalEquipo');
}

function abrirRegistro() {
    // Ocultar modal de equipo si está abierto
    cerrarModal('modalEquipo');
    abrirModal('modalRegistro');
}

document.addEventListener('DOMContentLoaded', () => {
    // 1. Asignación de eventos de la barra de navegación (Navbar)
    const btnLogin = document.querySelector('.btn.login');
    const btnRegister = document.querySelector('.btn.register');
    const hamburger = getElement('hamburger');
    const navLinks = getElement('nav-links');

    if (btnLogin) {
        // Corrección de sintaxis en la línea 6 (listener directo)
        // Ya no necesitas envolver la llamada, solo usa la función que definimos.
        btnLogin.addEventListener('click', (e) => {
            e.preventDefault(); // Evita el comportamiento del '#'
            // Como no tenemos el modal de selección inicial en el HTML que enviaste,
            // abriremos directamente el login (lo cual es lo que hiciste en el JSP).
            abrirLogin(); 
        });
    }

    if (btnRegister) {
        btnRegister.addEventListener('click', (e) => {
            e.preventDefault();
            abrirRegistro();
        });
    }

    // Toggle del menú hamburguesa (si tienes un estilo CSS para manejarlo)
    if (hamburger && navLinks) {
        hamburger.addEventListener('click', () => {
            hamburger.classList.toggle('activo');
            navLinks.classList.toggle('activo');
            navLinks.getAttribute('aria-expanded') === 'true' ? 
                navLinks.setAttribute('aria-expanded', 'false') : 
                navLinks.setAttribute('aria-expanded', 'true');
        });
    }

    // 2. Control de los mensajes de error al cargar la página (Modales)
    const modalLogin = getElement('modalLogin');
    const mensajeErrorLogin = getElement('mensajeErrorLogin');
    const modalRegistro = getElement('modalRegistro');
    const overlay = getElement('overlay');

    // Abre el modal de Login si hay error
    if (mensajeErrorLogin && mensajeErrorLogin.textContent.trim().length > 0) {
        if (modalLogin) {
            modalLogin.style.display = 'flex';
            mensajeErrorLogin.style.display = 'block';
        }
    }
    
    // Si el modal de registro se abrió por JSP (por un error o éxito), muestra el overlay.
    if (modalRegistro && modalRegistro.style.display === 'flex') {
        if (overlay) overlay.style.display = 'block';
    }

    // Evento para cerrar modales al hacer click en el overlay
    if (overlay) {
        overlay.addEventListener('click', () => {
            // Cierra todos los modales abiertos si el overlay está visible
            cerrarModal('modalLogin');
            cerrarModal('modalRegistro');
            cerrarModal('modalEquipo');
            overlay.style.display = 'none';
        });
    }
});