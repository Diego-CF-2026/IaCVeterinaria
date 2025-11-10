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
    // VERIFICACIÓN CLAVE: Si modal es null, sale sin error.
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
    
    cerrarModal('modalSeleccionInicial');
    cerrarModal('modalEquipo'); // Intenta cerrar, si no existe no hay error
    
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
    // CORRECCIÓN LÓGICA: Esta línea está causando el error si 'modalEquipo' no existe.
    // La verificación dentro de cerrarModal previene el error.
    cerrarModal('modalEquipo'); 
    abrirModal('modalRegistro');
}

document.addEventListener('DOMContentLoaded', () => {
    // ... (Asignación de eventos de navbar, lógica de hamburguesa, etc.) ...

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
        // CORRECCIÓN LÓGICA: La llamada a abrirRegistro está en la línea 79, 
        // y esta llama a cerrarModal('modalEquipo'), causando el error si 'modalEquipo' es null.
        btnRegister.addEventListener('click', (e) => {
            e.preventDefault();
            abrirRegistro();
        });
    }
    
    // ... (resto del código) ...

    const animacionHeader = document.querySelector('.image-text');
    if (animacionHeader) {
        animacionHeader.classList.add('animar-header');
    }
});