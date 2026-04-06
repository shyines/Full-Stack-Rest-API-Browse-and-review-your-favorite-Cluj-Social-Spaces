import { initTheme, toggleTheme } from './js/ui.js';
import { checkLoginState, handleLogin, handleRegister, handleLogout } from './js/auth.js';
import { getUser } from './js/utils.js';
import { loadPlaces, showPlaceDetails, startEditReview, handleSubmitReview, deleteReview, handleVote, toggleReplyForm, submitReply, toggleFavorite } from './js/places.js';
import { loadOwnerPlaces, openPlaceForm, handleSavePlace } from './js/owner.js';
import { loadAdminUsers, toggleUserStatus } from './js/admin.js';
import { loadUserProfile } from './js/profile.js';
import { API_BASE_URL } from './js/config.js';

// Expose functions to global scope for HTML onclick events
window.showPlaceDetails = showPlaceDetails;
window.startEditReview = startEditReview;
window.deleteReview = deleteReview;
window.openPlaceForm = openPlaceForm;
window.toggleUserStatus = toggleUserStatus;
window.handleVote = handleVote;
window.loadUserProfile = loadUserProfile;
window.toggleReplyForm = toggleReplyForm;
window.submitReply = submitReply;
window.toggleFavorite = toggleFavorite;

document.addEventListener('DOMContentLoaded', () => {
    initTheme();
    checkServerStatus();
    setupEventListeners();
    checkLoginState();
});

function checkServerStatus() {
    const dataElement = document.getElementById('server-data');
    if (!dataElement) return;
    
    fetch(`${API_BASE_URL}/hello`)
        .then(response => {
            if (!response.ok) throw new Error('Network error');
            return response.text();
        })
        .then(data => {
            dataElement.textContent = 'Online';
            dataElement.style.color = 'var(--success-color)';
        })
        .catch(error => {
            dataElement.textContent = 'Offline';
            dataElement.style.color = 'var(--danger-color)';
            console.error('Fetch error:', error);
        });
}

function setupEventListeners() {
    // Theme
    const themeBtn = document.getElementById('theme-toggle');
    if (themeBtn) themeBtn.addEventListener('click', toggleTheme);

    // Auth
    const loginForm = document.getElementById('login-form');
    if (loginForm) loginForm.addEventListener('submit', handleLogin);

    const registerForm = document.getElementById('register-form');
    if (registerForm) registerForm.addEventListener('submit', handleRegister);

    // Auth Switcher
    const showLoginBtn = document.getElementById('show-login');
    const showRegisterBtn = document.getElementById('show-register');
    const loginContainer = document.getElementById('login-container');
    const registerContainer = document.getElementById('register-container');

    if (showLoginBtn && showRegisterBtn && loginContainer && registerContainer) {
        showLoginBtn.addEventListener('click', () => {
            showLoginBtn.classList.add('active');
            showRegisterBtn.classList.remove('active');
            loginContainer.style.display = 'block';
            registerContainer.style.display = 'none';
        });

        showRegisterBtn.addEventListener('click', () => {
            showRegisterBtn.classList.add('active');
            showLoginBtn.classList.remove('active');
            registerContainer.style.display = 'block';
            loginContainer.style.display = 'none';
        });
    }

    const logoutBtn = document.getElementById('logout-btn');
    if (logoutBtn) logoutBtn.addEventListener('click', handleLogout);

    const guestBtn = document.getElementById('guest-access-btn');
    if (guestBtn) {
        guestBtn.addEventListener('click', (e) => {
            e.preventDefault();
            localStorage.setItem('isGuest', 'true');
            checkLoginState();
        });
    }

    // Role Toggle
    const roleSelect = document.getElementById('reg-role');
    const businessGroup = document.getElementById('business-group');
    if (roleSelect) {
        roleSelect.addEventListener('change', (e) => {
            businessGroup.style.display = e.target.value === 'OWNER' ? 'block' : 'none';
        });
    }

    // Reviews
    const reviewForm = document.getElementById('review-form');
    if (reviewForm) reviewForm.addEventListener('submit', handleSubmitReview);

    // Owner Controls
    const managePlacesBtn = document.getElementById('manage-places-btn');
    if (managePlacesBtn) {
        managePlacesBtn.addEventListener('click', () => {
            document.getElementById('dashboard').style.display = 'none';
            document.getElementById('my-places-view').style.display = 'block';
            loadOwnerPlaces();
        });
    }

    const addPlaceBtn = document.getElementById('add-place-btn');
    if (addPlaceBtn) addPlaceBtn.addEventListener('click', () => openPlaceForm());

    const placeForm = document.getElementById('place-form');
    if (placeForm) placeForm.addEventListener('submit', handleSavePlace);

    const cancelEditBtn = document.getElementById('cancel-edit-btn');
    if (cancelEditBtn) {
        cancelEditBtn.addEventListener('click', () => {
            document.getElementById('edit-place-view').style.display = 'none';
            document.getElementById('my-places-view').style.display = 'block';
        });
    }

    const backOwnerBtn = document.getElementById('back-to-dash-owner');
    if (backOwnerBtn) {
        backOwnerBtn.addEventListener('click', () => {
            document.getElementById('my-places-view').style.display = 'none';
            document.getElementById('dashboard').style.display = 'block';
            triggerSearchLoad();
        });
    }

    // Admin Controls
    const adminUsersBtn = document.getElementById('admin-users-btn');
    if (adminUsersBtn) {
        adminUsersBtn.addEventListener('click', () => {
            document.getElementById('dashboard').style.display = 'none';
            document.getElementById('admin-users-view').style.display = 'block';
            loadAdminUsers();
        });
    }

    const backAdminBtn = document.getElementById('back-to-dash-admin');
    if (backAdminBtn) {
        backAdminBtn.addEventListener('click', () => {
            document.getElementById('admin-users-view').style.display = 'none';
            document.getElementById('dashboard').style.display = 'block';
            triggerSearchLoad();
        });
    }

    // Search & Filter
    const searchBtn = document.getElementById('search-btn');
    const searchInput = document.getElementById('search-input');
    const filterType = document.getElementById('filter-type');
    const filterRating = document.getElementById('filter-rating');
    const filterReviews = document.getElementById('filter-reviews');

    if (searchBtn) searchBtn.addEventListener('click', triggerSearchLoad);
    if (searchInput) {
        searchInput.addEventListener('keyup', (e) => {
            if (e.key === 'Enter') triggerSearchLoad();
        });
    }
    if (filterType) filterType.addEventListener('change', triggerSearchLoad);
    if (filterRating) filterRating.addEventListener('change', triggerSearchLoad);
    if (filterReviews) filterReviews.addEventListener('change', triggerSearchLoad);

    // Back from Details
    const backBtn = document.getElementById('back-to-dash');
    if (backBtn) {
        backBtn.addEventListener('click', () => {
            document.getElementById('place-details-view').style.display = 'none';
            document.getElementById('dashboard').style.display = 'block';
            triggerSearchLoad();
        });
    }

    // Profile Controls
    const myProfileBtn = document.getElementById('my-profile-btn');
    if (myProfileBtn) {
        myProfileBtn.addEventListener('click', () => {
            const user = getUser();
            if (user) loadUserProfile(user.username);
        });
    }

    const backProfileBtn = document.getElementById('back-to-dash-profile');
    if (backProfileBtn) {
        backProfileBtn.addEventListener('click', () => {
            document.getElementById('user-profile-view').style.display = 'none';
            document.getElementById('dashboard').style.display = 'block';
            triggerSearchLoad();
        });
    }
}

function triggerSearchLoad() {
    const searchInput = document.getElementById('search-input');
    const filterType = document.getElementById('filter-type');
    const filterRating = document.getElementById('filter-rating');
    const filterReviews = document.getElementById('filter-reviews');
    
    const s = searchInput ? searchInput.value : '';
    const t = filterType ? filterType.value : '';
    const r = filterRating ? filterRating.value : '';
    const rev = filterReviews ? filterReviews.value : '';

    loadPlaces(s, t, r, rev);
}