// js/auth.js
function saveAuthData(authResponse) {
    // authResponse should contain token, email, name, balance
    const token = authResponse.token;
    const user = {
        email: authResponse.email,
        name: authResponse.name,
        balance: authResponse.balance ?? authResponse.virtualBalance ?? 0
    };
    localStorage.setItem('token', token);
    localStorage.setItem('user', JSON.stringify(user));
}

function getToken() {
    return localStorage.getItem('token');
}
function getUserData() {
    const raw = localStorage.getItem('user');
    return raw ? JSON.parse(raw) : null;
}
function clearAuthData() {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    // redirect to login
    window.location.href = 'index.html';
}
function isAuthenticated() {
    return !!getToken();
}
function checkAuth() {
    if (!isAuthenticated()) {
        window.location.href = 'index.html';
    }
}
function logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    window.location.href = 'index.html';
}
