import { API_BASE_URL } from './config.js';
import { ui } from './ui.js';
import { loadPlaces } from './places.js';
import { getUser, isGuest } from './utils.js';

export { getUser, isGuest }; // Re-export if other files rely on importing from auth.js, but better to update them.

export async function handleLogin(e) {
    e.preventDefault();
    const email = document.getElementById('login-email').value;
    const password = document.getElementById('login-password').value;
    const messageEl = document.getElementById('login-message');

    try {
        const response = await fetch(`${API_BASE_URL}/auth/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, password })
        });

        const data = await response.json();

        if (data.success) {
            localStorage.setItem('user', JSON.stringify(data.data));
            localStorage.removeItem('isGuest');
            messageEl.textContent = 'Login successful!';
            messageEl.className = 'message success';
            checkLoginState();
        } else {
            messageEl.textContent = data.message || 'Login failed';
            messageEl.className = 'message error';
        }
    } catch (error) {
        messageEl.textContent = 'An error occurred';
        messageEl.className = 'message error';
        console.error(error);
    }
}

export async function handleRegister(e) {
    e.preventDefault();
    const username = document.getElementById('reg-username').value;
    const email = document.getElementById('reg-email').value;
    const password = document.getElementById('reg-password').value;
    const role = document.getElementById('reg-role').value;
    const businessName = document.getElementById('reg-business').value;

    const payload = {
        username,
        email,
        password,
        role,
        businessName: role === 'OWNER' ? businessName : null
    };

    try {
        const response = await fetch(`${API_BASE_URL}/auth/register`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });

        const data = await response.json();

        if (data.success) {
            ui.alert('Registration successful! You can now login.', 'Success');
            document.getElementById('register-form').reset();
        } else {
            ui.alert(data.message || 'Registration failed', 'Error');
        }
    } catch (error) {
        console.error(error);
        ui.alert('An error occurred during registration', 'Error');
    }
}

export function handleLogout() {
    localStorage.removeItem('user');
    localStorage.removeItem('isGuest');
    checkLoginState();
}

export function checkLoginState() {
    const user = getUser();
    const guest = isGuest();
    
    console.log('checkLoginState:', { user, guest });

    const authForms = document.getElementById('auth-forms');
    const dashboard = document.getElementById('dashboard');
    const userDisplay = document.getElementById('user-display');
    const logoutBtn = document.getElementById('logout-btn');
    const myProfileBtn = document.getElementById('my-profile-btn');
    const ownerControls = document.getElementById('owner-controls');
    const adminControls = document.getElementById('admin-controls');

    if (user) {
        authForms.style.display = 'none';
        dashboard.style.display = 'block';
        userDisplay.textContent = `Welcome, ${user.username}`;
        logoutBtn.textContent = 'Logout';
        logoutBtn.style.display = 'inline-block';
        if (myProfileBtn) myProfileBtn.style.display = 'inline-block';
        
        // Show owner controls
        if (user.role === 'OWNER') {
            ownerControls.style.display = 'block';
        } else {
            ownerControls.style.display = 'none';
        }

        // Show admin controls
        if (user.role === 'ADMIN') {
            adminControls.style.display = 'block';
        } else {
            adminControls.style.display = 'none';
        }

        // Trigger load
        triggerSearchLoad();

    } else if (guest) {
        authForms.style.display = 'none';
        dashboard.style.display = 'block';
        userDisplay.textContent = 'Guest Mode';
        logoutBtn.textContent = 'Exit Guest';
        logoutBtn.style.display = 'inline-block';
        if (myProfileBtn) myProfileBtn.style.display = 'none';
        
        ownerControls.style.display = 'none';
        adminControls.style.display = 'none';
        
        triggerSearchLoad();

    } else {
        authForms.style.display = 'flex';
        dashboard.style.display = 'none';
        userDisplay.textContent = 'Not logged in';
        logoutBtn.style.display = 'none';
        if (myProfileBtn) myProfileBtn.style.display = 'none';
    }
}

function triggerSearchLoad() {
    const searchInput = document.getElementById('search-input');
    const filterType = document.getElementById('filter-type');
    const filterRating = document.getElementById('filter-rating');
    const filterReviews = document.getElementById('filter-reviews');
    
    // Safely get values if elements exist
    const s = searchInput ? searchInput.value : '';
    const t = filterType ? filterType.value : '';
    const r = filterRating ? filterRating.value : '';
    const rev = filterReviews ? filterReviews.value : '';

    loadPlaces(s, t, r, rev);
}
