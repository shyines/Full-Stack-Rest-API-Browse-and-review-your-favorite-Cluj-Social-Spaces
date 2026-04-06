import { API_BASE_URL } from './config.js';
import { ui } from './ui.js';
import { getUser } from './utils.js';

export async function loadOwnerPlaces() {
    const list = document.getElementById('my-places-list');
    const user = getUser();
    
    list.innerHTML = 'Loading...';

    try {
        const response = await fetch(`${API_BASE_URL}/owner/places?email=${user.email}`);
        const data = await response.json();

        if (data.success) {
            if (data.data.length === 0) {
                list.innerHTML = '<p>You have no places.</p>';
                return;
            }
            list.innerHTML = data.data.map(place => {
                const typeLower = place.type ? place.type.toLowerCase() : 'other';
                const typeBadge = place.type ? `<span class="badge badge-${typeLower}">${place.type.replace('_', ' ')}</span>` : '';
                // Escaping strings for onclick
                const nameSafe = place.name.replace(/'/g, "\'" ).replace(/"/g, '&quot;');
                const descSafe = place.description.replace(/'/g, "\'" ).replace(/"/g, '&quot;');
                const addrSafe = place.address.replace(/'/g, "\'" ).replace(/"/g, '&quot;');
                
                return `
                    <div class="place-card" style="cursor: default;">
                        <h3>${place.name} ${typeBadge}</h3>
                        <p class="description">${place.description}</p>
                        <p class="address">📍 ${place.address}</p>
                        <button class="secondary-btn" onclick="window.openPlaceForm(${place.id}, '${nameSafe}', '${descSafe}', '${addrSafe}', '${place.type}')">Edit</button>
                    </div>
                `;
            }).join('');
        } else {
            list.innerHTML = '<p class="error">Failed to load places.</p>';
        }
    } catch (error) {
        console.error(error);
        list.innerHTML = '<p class="error">Error connecting to server.</p>';
    }
}

export function openPlaceForm(id = null, name = '', desc = '', addr = '', type = 'OTHER') {
    document.getElementById('my-places-view').style.display = 'none';
    document.getElementById('edit-place-view').style.display = 'block';

    const title = document.getElementById('edit-view-title');
    if(title) title.textContent = id ? 'Edit Place' : 'Add New Place';
    
    document.getElementById('edit-place-id').value = id || '';
    document.getElementById('place-name').value = name;
    document.getElementById('place-desc').value = desc;
    document.getElementById('place-addr').value = addr;
    document.getElementById('place-type').value = type;
}

export async function handleSavePlace(e) {
    e.preventDefault();

    const id = document.getElementById('edit-place-id').value;
    const name = document.getElementById('place-name').value;
    const description = document.getElementById('place-desc').value;
    const address = document.getElementById('place-addr').value;
    const type = document.getElementById('place-type').value;
    const user = getUser();

    const payload = {
        name,
        description,
        address,
        type,
        ownerEmail: user.email
    };

    const url = id ? `${API_BASE_URL}/owner/places/${id}` : `${API_BASE_URL}/owner/places`;
    const method = id ? 'PUT' : 'POST';

    try {
        const response = await fetch(url, {
            method: method,
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });

        const data = await response.json();

        if (data.success) {
            ui.alert('Place saved successfully!', 'Success');
            document.getElementById('edit-place-view').style.display = 'none';
            document.getElementById('my-places-view').style.display = 'block';
            loadOwnerPlaces();
        } else {
            ui.alert(data.message || 'Failed to save place', 'Error');
        }
    } catch (error) {
        console.error(error);
        ui.alert('Error saving place', 'Network Error');
    }
}
