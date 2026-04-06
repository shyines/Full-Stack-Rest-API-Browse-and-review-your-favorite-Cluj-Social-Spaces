import { API_BASE_URL } from './config.js';
import { ui } from './ui.js';

export async function loadUserProfile(username) {
    // Hide other views
    document.getElementById('dashboard').style.display = 'none';
    document.getElementById('place-details-view').style.display = 'none';
    document.getElementById('my-places-view').style.display = 'none';
    document.getElementById('edit-place-view').style.display = 'none';
    document.getElementById('admin-users-view').style.display = 'none';
    
    // Show profile view
    const view = document.getElementById('user-profile-view');
    view.style.display = 'block';

    const reviewsContainer = document.getElementById('profile-reviews-container');
    const favoritesContainer = document.getElementById('profile-favorites-container');
    
    reviewsContainer.innerHTML = '<p class="loading-pulse">Loading profile...</p>';
    if (favoritesContainer) favoritesContainer.innerHTML = '';

    try {
        const response = await fetch(`${API_BASE_URL}/users/${username}`);
        const data = await response.json();

        if (data.success) {
            renderProfile(data.data);
        } else {
            ui.alert(data.message || 'Failed to load profile', 'Error');
            view.style.display = 'none';
            document.getElementById('dashboard').style.display = 'block';
        }
    } catch (error) {
        console.error(error);
        ui.alert('Network error loading profile', 'Error');
    }
}

function renderProfile(profile) {
    document.getElementById('profile-username').textContent = profile.username;
    
    const roleBadge = document.getElementById('profile-role');
    roleBadge.textContent = profile.role;
    roleBadge.className = `badge badge-${profile.role === 'ADMIN' ? 'club' : (profile.role === 'OWNER' ? 'study_hub' : 'cafe')}`;
    
    document.getElementById('profile-join-date').textContent = new Date(profile.joinDate).toLocaleDateString();
    document.getElementById('profile-total-reviews').textContent = profile.totalReviews;
    document.getElementById('profile-avg-rating').textContent = profile.averageRatingGiven ? profile.averageRatingGiven.toFixed(1) : 'N/A';

    const reviewsContainer = document.getElementById('profile-reviews-container');
    
    // Render Reviews
    if (!profile.recentReviews || profile.recentReviews.length === 0) {
        reviewsContainer.innerHTML = '<p>No reviews yet.</p>';
    } else {
        reviewsContainer.innerHTML = profile.recentReviews.map(r => `
            <div class="review-card">
                <div class="review-header">
                    <span class="review-rating">${'★'.repeat(r.rating)}</span>
                    <small style="color: var(--text-muted);">${new Date(r.createdAt).toLocaleDateString()}</small>
                </div>
                <p>${r.comment}</p>
                <div style="margin-top: 10px; font-size: 0.8rem; color: var(--text-muted);">
                    Votes: 👍 ${r.upvotes} | 👎 ${r.downvotes}
                </div>
            </div>
        `).join('');
    }

    // Render Favorites
    const favoritesSection = document.getElementById('profile-favorites-section');
    const favoritesContainer = document.getElementById('profile-favorites-container');

    if (profile.role === 'STUDENT' || profile.role === 'ADMIN') {
        favoritesSection.style.display = 'block';
        if (!profile.favoritePlaces || profile.favoritePlaces.length === 0) {
            favoritesContainer.innerHTML = '<p>No favorite places yet.</p>';
        } else {
            favoritesContainer.innerHTML = profile.favoritePlaces.map(place => {
                const typeLower = place.type ? place.type.toLowerCase() : 'other';
                const typeBadge = place.type ? `<span class="badge badge-${typeLower}">${place.type.replace('_', ' ')}</span>` : '';
                const rating = place.averageRating ? place.averageRating.toFixed(1) : 'N/A';

                return `
                    <div class="place-card" onclick="window.showPlaceDetails(${place.id})">
                        <h4>${place.name} ${typeBadge}</h4>
                        <div style="color: var(--warning-color); font-weight: bold; font-size: 0.9rem;">★ ${rating}</div>
                        <p class="address" style="margin-top: 5px; font-size: 0.8rem;">📍 ${place.address}</p>
                    </div>
                `;
            }).join('');
        }
    } else {
        favoritesSection.style.display = 'none';
    }
}