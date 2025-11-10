// Funciones auxiliares para el modo noche
function aplicarModo(activado) {
    const body = document.body;
    if (activado) {
        body.classList.add('modo-noche');
        localStorage.setItem('modo-noche', 'activado');
        actualizarIcono(true);
    } else {
        body.classList.remove('modo-noche');
        localStorage.setItem('modo-noche', 'desactivado');
        actualizarIcono(false);
    }
    actualizarImagenesUsuario(activado);
}

function actualizarIcono(activado) {
    const btn = document.getElementById('modoNocheBtn');
    if (btn) {
        btn.textContent = activado ? '☀️' : '🌙';
    }
}

function actualizarImagenesUsuario(activado) {
    // Busca los iconos de usuario y el logo en los modales
    const imagenesModal = document.querySelectorAll('.modal-content img.icono-usuario, .modal-content img.icono-patita');

    imagenesModal.forEach(imagen => {
        if (imagen.src) {
            const esUsuario = imagen.classList.contains('icono-usuario');
            const original = esUsuario ? 'IconUser.svg' : 'Logo.png';
            
            // CORRECCIÓN CLAVE: Usar 'Logo-oscuro.png' para que coincida con tu archivo
            const oscuro = esUsuario ? 'IconUser-dark.svg' : 'Logo-oscuro.png'; 
            
            if (activado) {
                if (imagen.src.includes(original)) {
                    imagen.src = imagen.src.replace(original, oscuro);
                }
            } else {
                if (imagen.src.includes(oscuro)) {
                    imagen.src = imagen.src.replace(oscuro, original);
                }
            }
        }
    });
}

// Inicialización y Listener
document.addEventListener('DOMContentLoaded', () => {
    const modoGuardado = localStorage.getItem('modo-noche');
    const activado = modoGuardado === 'activado';
    
    aplicarModo(activado); 

    const modoNocheBtn = document.getElementById('modoNocheBtn');
    if (modoNocheBtn) {
        modoNocheBtn.addEventListener('click', () => {
            const nuevoEstado = !document.body.classList.contains('modo-noche');
            aplicarModo(nuevoEstado);
        });
    }
});