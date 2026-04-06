// --- Theme Logic ---
export function initTheme() {
    const savedTheme = localStorage.getItem('theme') || 'light';
    document.documentElement.setAttribute('data-theme', savedTheme);
    updateThemeIcon(savedTheme);
}

export function toggleTheme() {
    const currentTheme = document.documentElement.getAttribute('data-theme');
    const newTheme = currentTheme === 'light' ? 'dark' : 'light';
    document.documentElement.setAttribute('data-theme', newTheme);
    localStorage.setItem('theme', newTheme);
    updateThemeIcon(newTheme);
}

function updateThemeIcon(theme) {
    const btn = document.getElementById('theme-toggle');
    if(btn) {
        btn.innerHTML = theme === 'light' ? '<i class="fa-solid fa-moon"></i>' : '<i class="fa-solid fa-sun"></i>';
    }
}

// --- Modal Logic ---
function showModal(message, title = 'Notification', isConfirm = false, onConfirm = null) {
    const modal = document.getElementById('custom-modal');
    const titleEl = document.getElementById('modal-title');
    const msgEl = document.getElementById('modal-message');
    const okBtn = document.getElementById('modal-ok-btn');
    const cancelBtn = document.getElementById('modal-cancel-btn');

    titleEl.textContent = title;
    msgEl.textContent = message;
    
    // Reset buttons (clone to remove old listeners)
    const newOkBtn = okBtn.cloneNode(true);
    okBtn.parentNode.replaceChild(newOkBtn, okBtn);
    
    const newCancelBtn = cancelBtn.cloneNode(true);
    cancelBtn.parentNode.replaceChild(newCancelBtn, cancelBtn);

    if (isConfirm) {
        newCancelBtn.style.display = 'inline-block';
        newCancelBtn.addEventListener('click', closeModal);
        newOkBtn.addEventListener('click', () => {
            closeModal();
            if (onConfirm) onConfirm();
        });
    } else {
        newCancelBtn.style.display = 'none';
        newOkBtn.addEventListener('click', closeModal);
    }

    modal.classList.add('active');
}

export function closeModal() {
    document.getElementById('custom-modal').classList.remove('active');
}

export const ui = {
    alert: (msg, title) => showModal(msg, title),
    confirm: (msg, callback) => showModal(msg, 'Confirm Action', true, callback)
};
