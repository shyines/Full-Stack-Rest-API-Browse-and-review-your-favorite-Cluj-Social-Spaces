import { API_BASE_URL } from './config.js';
import { ui } from './ui.js';
import { getUser } from './utils.js';

export async function loadPlaces(searchQuery = '', typeFilter = '', minRating = '', minReviews = '') {
    const contentArea = document.getElementById('content-area');
    if (!contentArea) return;
    
    contentArea.innerHTML = '<p class="loading-pulse"><i class="fa-solid fa-spinner fa-spin"></i> Loading places...</p>';

    try {
        let url = `${API_BASE_URL}/places?`;
        if (searchQuery) url += `search=${encodeURIComponent(searchQuery)}&`;
        if (typeFilter) url += `type=${encodeURIComponent(typeFilter)}&`;
        if (minRating) url += `minRating=${encodeURIComponent(minRating)}&`;
        if (minReviews) url += `minReviews=${encodeURIComponent(minReviews)}&`;

        const response = await fetch(url);
        const data = await response.json();

        if (data.success) {
            renderPlaces(data.data);
        } else {
            contentArea.innerHTML = '<p class="error">Failed to load places.</p>';
        }
    } catch (error) {
        console.error('Error loading places:', error);
        contentArea.innerHTML = '<p class="error">Error connecting to server.</p>';
    }
}

function renderPlaces(places) {
    const contentArea = document.getElementById('content-area');
    if (!contentArea) return;
    
    if (!places || places.length === 0) {
        contentArea.innerHTML = '<p style="text-align: center; width: 100%;">No places found matching criteria.</p>';
        return;
    }

    const listHtml = places.map((place, index) => {
        const typeLower = place.type ? place.type.toLowerCase() : 'other';
        const typeBadge = place.type ? `<span class="badge badge-${typeLower}">${place.type.replace('_', ' ')}</span>` : '';
        const rating = place.averageRating ? place.averageRating.toFixed(1) : 'N/A';
        const reviewCount = place.reviewCount || 0;
        
        const delayClass = `delay-${Math.min((index + 1) * 100, 500)}`;

        return `
            <div class="place-card animate-slide-up ${delayClass}" onclick="window.showPlaceDetails(${place.id})">
                <h3>${place.name} ${typeBadge}</h3>
                <div style="margin-bottom: 5px; color: var(--warning-color); font-weight: bold;">
                    ★ ${rating} <span style="color: var(--text-muted); font-weight: normal; font-size: 0.9rem;">(${reviewCount} reviews)</span>
                </div>
                <p class="description">${place.description}</p>
                <p class="address">📍 ${place.address}</p>
                <small>Owner: ${place.ownerName || 'Unknown'}</small>
            </div>
        `;
    }).join('');

    contentArea.innerHTML = `<div class="places-grid">${listHtml}</div>`;
}

export async function showPlaceDetails(placeId) {
    document.getElementById('dashboard').style.display = 'none';
    document.getElementById('user-profile-view').style.display = 'none';
    document.getElementById('my-places-view').style.display = 'none';
    document.getElementById('edit-place-view').style.display = 'none';
    document.getElementById('admin-users-view').style.display = 'none';
    
    const detailsView = document.getElementById('place-details-view');
    detailsView.style.display = 'block';

    const placeInfo = document.getElementById('place-info');
    const reviewsList = document.getElementById('reviews-list');
    
    resetReviewForm();

    placeInfo.innerHTML = 'Loading details...';
    reviewsList.innerHTML = '';

    try {
        const user = getUser();
        let url = `${API_BASE_URL}/places/${placeId}`;
        if (user) {
            url += `?userEmail=${encodeURIComponent(user.email)}`;
        }

        const response = await fetch(url);
        const data = await response.json();

        if (data.success) {
            const place = data.data;
            const reviewContainer = document.getElementById('add-review-container');
            
            if (user && user.role === 'STUDENT') {
                reviewContainer.style.display = 'block';
                document.getElementById('review-place-id').value = place.id;
            } else {
                reviewContainer.style.display = 'none';
            }

            const typeLower = place.type ? place.type.toLowerCase() : 'other';
            const typeLabel = place.type ? place.type.replace('_', ' ') : 'Other';

            const favActive = place.favorited ? 'active' : '';
            const favIcon = place.favorited ? 'fa-solid' : 'fa-regular';
            const favBtn = user && (user.role === 'STUDENT' || user.role === 'ADMIN') 
                ? `<button class="fav-btn ${favActive}" onclick="window.toggleFavorite(${place.id})" title="Toggle Favorite">
                    <i class="${favIcon} fa-heart"></i>
                   </button>` 
                : '';

            placeInfo.innerHTML = `
                <div style="display: flex; justify-content: space-between; align-items: flex-start;">
                    <h2>${place.name}</h2>
                    ${favBtn}
                </div>
                <span class="badge badge-${typeLower}" style="font-size: 0.9rem;">${typeLabel}</span>
                <p><strong>Address:</strong> ${place.address}</p>
                <p><strong>Owner:</strong> ${place.ownerName}</p>
                <div style="margin: 1rem 0; padding: 1rem; background: var(--bg-color); border-radius: var(--radius);">
                    ${place.description}
                </div>
                <p><strong>Average Rating:</strong> <span style="color: var(--warning-color); font-size: 1.2rem;">${place.averageRating ? place.averageRating.toFixed(1) : 'N/A'} ★</span></p>
            `;

            if (place.reviews && place.reviews.length > 0) {
                reviewsList.innerHTML = place.reviews.map(r => {
                    const isOwner = user && user.username === r.username;
                    const isAdmin = user && user.role === 'ADMIN';
                    
                    let actions = '';
                    if (isOwner) {
                        const commentSafe = r.comment ? r.comment.replace(/'/g, "' ").replace(/\n/g, '') : '';
                        actions += `<button class="secondary-btn" style="margin-top: 10px; font-size: 12px; padding: 5px 10px; margin-right: 5px;" onclick="window.startEditReview(${r.id}, ${r.rating}, '${commentSafe}')">Edit Review</button>`;
                    }
                    if (isAdmin) {
                        actions += `<button class="secondary-btn btn-red" style="margin-top: 10px; font-size: 12px; padding: 5px 10px;" onclick="window.deleteReview(${r.id}, ${place.id})">Delete Review</button>`;
                    }

                    const upActive = r.currentUserVote === 'UPVOTE' ? 'active' : '';
                    const downActive = r.currentUserVote === 'DOWNVOTE' ? 'active' : '';

                    const repliesHtml = r.replies && r.replies.length > 0 
                        ? `<div class="review-replies">
                            ${r.replies.map(reply => {
                                let badge = '';
                                if (reply.userRole === 'ADMIN') {
                                    badge = '<span class="badge badge-admin-reply">ADMIN</span>';
                                } else if (reply.owner) {
                                    badge = '<span class="badge badge-owner-reply">PLACE OWNER</span>';
                                }

                                return `
                                    <div class="review-reply">
                                        <div class="reply-header">
                                            <span class="reply-author">${reply.username} ${badge}</span>
                                            <span class="reply-date">${new Date(reply.createdAt).toLocaleDateString()}</span>
                                        </div>
                                        <p>${reply.comment}</p>
                                    </div>
                                `;
                            }).join('')}
                          </div>` 
                        : '';

                    const replyFormHtml = user ? `
                        <button class="reply-toggle-btn" onclick="window.toggleReplyForm(${r.id})">Reply to this review</button>
                        <div id="reply-form-${r.id}" class="reply-form" style="display:none;">
                            <textarea id="reply-comment-${r.id}" placeholder="Write a reply..."></textarea>
                            <button class="primary-btn" style="width: auto; padding: 5px 15px; font-size: 0.9rem;" onclick="window.submitReply(${r.id}, ${place.id})">Post Reply</button>
                        </div>
                    ` : '';

                    return `
                        <div class="review-card">
                            <div class="review-header">
                                <span class="review-author" style="cursor: pointer; color: var(--primary-color);" onclick="window.loadUserProfile('${r.username}')">${r.username}</span>
                                <span class="review-rating">${'★'.repeat(r.rating)}</span>
                            </div>
                            <p>${r.comment}</p>
                            <small style="color: var(--text-muted);">${new Date(r.createdAt).toLocaleDateString()}</small>
                            
                            <div class="vote-controls">
                                <button class="vote-btn ${upActive}" onclick="window.handleVote(${r.id}, 'UPVOTE', ${place.id})">
                                    <i class="fa-solid fa-thumbs-up"></i> ${r.upvotes}
                                </button>
                                <button class="vote-btn down ${downActive}" onclick="window.handleVote(${r.id}, 'DOWNVOTE', ${place.id})">
                                    <i class="fa-solid fa-thumbs-down"></i> ${r.downvotes}
                                </button>
                            </div>

                            <div class="review-actions">
                                ${actions}
                            </div>

                            ${repliesHtml}
                            ${replyFormHtml}
                        </div>
                    `;
                }).join('');
            } else {
                reviewsList.innerHTML = '<p>No reviews yet. Be the first!</p>';
            }

        } else {
            placeInfo.innerHTML = '<p class="error">Failed to load details.</p>';
        }
    } catch (error) {
        console.error(error);
        placeInfo.innerHTML = '<p class="error">Error connecting to server.</p>';
    }
}

export async function toggleFavorite(placeId) {
    const user = getUser();
    if (!user) {
        ui.alert('Please login to favorite places.', 'Access Denied');
        return;
    }

    try {
        const response = await fetch(`${API_BASE_URL}/users/favorites/${placeId}`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ userEmail: user.email })
        });

        const data = await response.json();
        if (data.success) {
            showPlaceDetails(placeId);
        } else {
            ui.alert(data.message || 'Failed to update favorite', 'Error');
        }
    } catch (error) {
        console.error('Error toggling favorite:', error);
    }
}

export function toggleReplyForm(reviewId) {
    const form = document.getElementById(`reply-form-${reviewId}`);
    if (form) {
        form.style.display = form.style.display === 'block' ? 'none' : 'block';
    }
}

export async function submitReply(reviewId, placeId) {
    const commentInput = document.getElementById(`reply-comment-${reviewId}`);
    const comment = commentInput.value.trim();
    const user = getUser();

    if (!comment) {
        ui.alert('Reply cannot be empty!', 'Error');
        return;
    }

    if (!user) {
        ui.alert('You must be logged in to reply.', 'Access Denied');
        return;
    }

    try {
        const response = await fetch(`${API_BASE_URL}/reviews/${reviewId}/replies`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                comment: comment,
                userEmail: user.email
            })
        });

        const data = await response.json();
        if (data.success) {
            ui.alert('Reply added!', 'Success');
            showPlaceDetails(placeId);
        } else {
            ui.alert(data.message || 'Failed to add reply', 'Error');
        }
    } catch (error) {
        console.error('Error adding reply:', error);
        ui.alert('Error connecting to server', 'Error');
    }
}

export function startEditReview(reviewId, rating, comment) {
    const formTitle = document.getElementById('review-form-title');
    const reviewIdInput = document.getElementById('review-id-edit');
    const commentInput = document.getElementById('review-comment');
    const submitBtn = document.querySelector('#review-form button[type="submit"]');

    if (formTitle) formTitle.textContent = 'Edit Your Review';
    if (reviewIdInput) reviewIdInput.value = reviewId;
    if (commentInput) commentInput.value = comment;
    
    const radio = document.querySelector(`input[name="rating"][value="${rating}"]`);
    if(radio) radio.checked = true;

    if (submitBtn) {
        submitBtn.textContent = 'Update Review';
        submitBtn.classList.add('btn-orange'); 
    }

    const container = document.getElementById('add-review-container');
    if (container) container.scrollIntoView({ behavior: 'smooth' });
}

function resetReviewForm() {
    const form = document.getElementById('review-form');
    if (form) form.reset();
    
    const title = document.getElementById('review-form-title');
    if (title) title.textContent = 'Leave a Review';
    
    const idInput = document.getElementById('review-id-edit');
    if (idInput) idInput.value = '';
    
    const submitBtn = document.querySelector('#review-form button[type="submit"]');
    if (submitBtn) {
        submitBtn.textContent = 'Submit Review';
        submitBtn.classList.remove('btn-orange');
    }
}

export async function handleSubmitReview(e) {
    e.preventDefault();
    
    const placeId = document.getElementById('review-place-id').value;
    const reviewId = document.getElementById('review-id-edit').value;
    const comment = document.getElementById('review-comment').value;
    const ratingInput = document.querySelector('input[name="rating"]:checked');
    const user = getUser();
    
    if (!ratingInput) {
        ui.alert('Please select a rating!', 'Review Error');
        return;
    }
    
    if (!user) {
        ui.alert('You must be logged in.', 'Access Denied');
        return;
    }

    const rating = parseInt(ratingInput.value);

    let url = `${API_BASE_URL}/places/${placeId}/reviews`;
    let method = 'POST';

    if (reviewId) {
        url = `${API_BASE_URL}/places/${placeId}/reviews/${reviewId}`;
        method = 'PUT';
    }

    try {
        const response = await fetch(url, {
            method: method,
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                rating,
                comment,
                userEmail: user.email 
            })
        });

        const data = await response.json();

        if (data.success) {
            ui.alert(reviewId ? 'Review updated!' : 'Review submitted!', 'Success');
            resetReviewForm();
            showPlaceDetails(placeId);
        } else {
            ui.alert(data.message || 'Failed to submit review', 'Submission Failed');
        }
    } catch (error) {
        console.error(error);
        ui.alert('Error submitting review', 'Network Error');
    }
}

export async function deleteReview(reviewId, placeId) {
    ui.confirm('Are you sure you want to delete this review?', async () => {
        try {
            const response = await fetch(`${API_BASE_URL}/admin/reviews/${reviewId}`, {
                method: 'DELETE'
            });
            const data = await response.json();

            if (data.success) {
                ui.alert('Review deleted.', 'Success');
                showPlaceDetails(placeId); 
            } else {
                ui.alert(data.message || 'Failed to delete review', 'Error');
            }
        } catch (error) {
            console.error(error);
            ui.alert('Error deleting review', 'Error');
        }
    });
}

export async function handleVote(reviewId, voteType, placeId) {
    const user = getUser();
    if (!user) {
        ui.alert('Please login to vote.', 'Access Denied');
        return;
    }

    try {
        const response = await fetch(`${API_BASE_URL}/reviews/${reviewId}/vote`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ userEmail: user.email, voteType: voteType })
        });
        
        const data = await response.json();
        if (data.success) {
            showPlaceDetails(placeId);
        } else {
            ui.alert(data.message || 'Failed to vote', 'Error');
        }
    } catch (error) {
        console.error(error);
    }
}
