let rolSeleccionado = '';

// Función auxiliar para obtener y verificar un elemento de forma segura
function getElement(id) {
    return document.getElementById(id);
}

// Funciones básicas de modales
function abrirModal(idModal) {
    const modal = getElement(idModal);
    if (modal) modal.style.display = 'flex';
    const overlay = getElement('overlay');
    if (overlay) overlay.style.display = 'block';
}

function cerrarModal(idModal) {
    const modal = getElement(idModal);
    // VERIFICACIÓN CLAVE: Si modal es null, sale sin error. Esto soluciona el TypeError.
    if (modal) modal.style.display = 'none';
    
    // Comprobar si hay otros modales abiertos antes de ocultar el overlay
    const modalesAbiertos = ['modalLogin', 'modalRegistro', 'modalEquipo', 'modalSeleccionInicial'].some(id => {
        const m = getElement(id);
        // Si el elemento existe y su display es 'flex', está abierto.
        return m && m.style.display === 'flex'; 
    });
    
    if (!modalesAbiertos) {
        const overlay = getElement('overlay');
        if (overlay) overlay.style.display = 'none';
    }
}


function abrirLogin(rol) {
    rolSeleccionado = rol || '';
    
    // Si 'modalSeleccionInicial' y 'modalEquipo' no existen, cerrarModal los ignora sin error.
    cerrarModal('modalSeleccionInicial');
    cerrarModal('modalEquipo'); 
    
    const modalLogin = getElement('modalLogin');
    const titulo = getElement('tituloLogin');
    const inputRol = getElement('inputRol');
    const opcionesRegistro = getElement('opcionesRegistro');

    if (inputRol) inputRol.value = rol;
    if (titulo) titulo.textContent = rol === 'Cliente' ? 'Iniciar Sesión (Cliente)' : 'Iniciar Sesión';
    if (opcionesRegistro) opcionesRegistro.style.display = rol === 'Cliente' ? 'block' : 'none';

    abrirModal('modalLogin');
}

function mostrarModalEquipo() {
    cerrarModal('modalSeleccionInicial');
    abrirModal('modalEquipo');
}

function abrirRegistro() {
    // La función que se llamó y causó el error
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
        btnLogin.addEventListener('click', (e) => {
            e.preventDefault(); 
            abrirLogin(); 
        });
    }

    if (btnRegister) {
        // Listener que llama a abrirRegistro
        btnRegister.addEventListener('click', (e) => {
            e.preventDefault();
            abrirRegistro();
        });
    }

    // Toggle del menú hamburguesa
    if (hamburger && navLinks) {
        hamburger.addEventListener('click', () => {
            hamburger.classList.toggle('activo');
            navLinks.classList.toggle('activo');
            navLinks.getAttribute('aria-expanded') === 'true' ? 
                navLinks.setAttribute('aria-expanded', 'false') : 
                navLinks.setAttribute('aria-expanded', 'true');
        });
    }

    // 2. Control de modales por errores de JSP
    const modalLogin = getElement('modalLogin');
    const mensajeErrorLogin = getElement('mensajeErrorLogin');
    const modalRegistro = getElement('modalRegistro');
    const overlay = getElement('overlay');

    if (mensajeErrorLogin && mensajeErrorLogin.textContent.trim().length > 0) {
        if (modalLogin) {
            modalLogin.style.display = 'flex';
            mensajeErrorLogin.style.display = 'block';
            if (overlay) overlay.style.display = 'block';
        }
    }
    
    if (modalRegistro && modalRegistro.style.display === 'flex') {
        if (overlay) overlay.style.display = 'block';
    }

    // Evento para cerrar modales al hacer click en el overlay
    if (overlay) {
        overlay.addEventListener('click', () => {
            cerrarModal('modalLogin');
            cerrarModal('modalRegistro');
            cerrarModal('modalEquipo');
            cerrarModal('modalSeleccionInicial'); // Asegurar que todo se cierra
            overlay.style.display = 'none'; 
        });
    }
    
    // 3. Animación de Header
    const animacionHeader = document.querySelector('.image-text');
    if (animacionHeader) {
        animacionHeader.classList.add('animar-header');
    }
});