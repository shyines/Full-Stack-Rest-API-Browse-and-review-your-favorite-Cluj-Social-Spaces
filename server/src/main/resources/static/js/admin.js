import { API_BASE_URL } from './config.js';
import { ui } from './ui.js';

export async function loadAdminUsers() {
    const listBody = document.getElementById('users-list-body');
    listBody.innerHTML = '<tr><td colspan="5">Loading...</td></tr>';

    try {
        const response = await fetch(`${API_BASE_URL}/admin/users`);
        const data = await response.json();

        if (data.success) {
            listBody.innerHTML = data.data.map(user => `
                <tr>
                    <td>${user.username}</td>
                    <td>${user.email}</td>
                    <td>${user.role}</td>
                    <td style="color: ${user.isActive ? 'var(--success-color)' : 'var(--danger-color)'}; font-weight: 600;">
                        ${user.isActive ? 'Active' : 'Banned'}
                    </td>
                    <td>
                        ${user.role !== 'ADMIN' ? `
                            <button onclick="window.toggleUserStatus(${user.id}, ${user.isActive})" 
                                    class="${user.isActive ? 'btn-red' : 'primary-btn'}" 
                                    style="padding: 0.25rem 0.75rem; font-size: 0.85rem; width: auto;">
                                ${user.isActive ? 'Ban' : 'Unban'}
                            </button>
                        ` : '<span style="color: var(--text-muted);">No Action</span>'}
                    </td>
                </tr>
            `).join('');
        } else {
            listBody.innerHTML = '<tr><td colspan="5">Failed to load users.</td></tr>';
        }
    } catch (error) {
        console.error(error);
        listBody.innerHTML = '<tr><td colspan="5">Error connecting to server.</td></tr>';
    }
}

export async function toggleUserStatus(userId, currentStatus) {
    const action = currentStatus ? 'deactivate' : 'activate';
    
    ui.confirm(`Are you sure you want to ${action} this user?`, async () => {
        try {
            const response = await fetch(`${API_BASE_URL}/admin/users/${userId}/${action}`, {
                method: 'PUT'
            });
            const data = await response.json();

            if (data.success) {
                loadAdminUsers();
            } else {
                ui.alert(data.message || 'Action failed', 'Error');
            }
        } catch (error) {
            console.error(error);
            ui.alert('Error updating user status', 'Error');
        }
    });
}
