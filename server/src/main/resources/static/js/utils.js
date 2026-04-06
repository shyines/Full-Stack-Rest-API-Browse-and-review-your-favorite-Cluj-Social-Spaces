export function getUser() {
    try {
        const userStr = localStorage.getItem('user');
        return userStr ? JSON.parse(userStr) : null;
    } catch (e) {
        console.error('Error parsing user from localStorage', e);
        localStorage.removeItem('user');
        return null;
    }
}

export function isGuest() {
    return localStorage.getItem('isGuest') === 'true';
}
