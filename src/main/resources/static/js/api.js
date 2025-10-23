// js/api.js
const API_BASE_URL = 'http://localhost:8080';

async function apiCall(endpoint, options = {}) {
    const token = getToken?.();
    const headers = options.headers || {};
    headers['Content-Type'] = headers['Content-Type'] || 'application/json';
    if (token) headers['Authorization'] = `Bearer ${token}`;

    const cfg = {
        ...options,
        headers
    };

    try {
        const res = await fetch(API_BASE_URL + endpoint, cfg);
        const status = res.status;
        let json = null;
        try { json = await res.json(); } catch (e) { json = null; }

        if (status >= 200 && status < 300) {
            return { success: true, data: json, status };
        } else {
            // handle 401 globally
            if (status === 401) {
                clearAuthData?.();
            }
            return { success: false, error: json || { message: res.statusText }, status };
        }
    } catch (err) {
        return { success: false, error: { message: 'Network error' } };
    }
}

// Authentication
async function login(email, password) {
    return await apiCall('/api/v1/auth/login', {
        method: 'POST',
        body: JSON.stringify({ email, password })
    });
}

async function register(name, email, password) {
    return await apiCall('/api/v1/auth/register', {
        method: 'POST',
        body: JSON.stringify({ name, email, password })
    });
}

// Stocks
async function getStockQuote(symbol) {
    return await apiCall(`/api/v1/stocks/quote?symbol=${encodeURIComponent(symbol)}`, { method: 'GET' });
}
async function getStockOverview(symbol) {
    return await apiCall(`/api/v1/stocks/overview?symbol=${encodeURIComponent(symbol)}`, { method: 'GET' });
}
// UPDATED to accept and pass the 'function' parameter
async function getStockHistory(symbol, days = 30, func = 'TIME_SERIES_DAILY') {
    return await apiCall(`/api/v1/stocks/history?symbol=${encodeURIComponent(symbol)}&days=${days}&function=${encodeURIComponent(func)}`, { method: 'GET' });
}

// Trades
async function buyStock(symbol, quantity) {
    return await apiCall('/api/v1/trade/buy', {
        method: 'POST',
        body: JSON.stringify({ symbol, quantity })
    });
}
async function sellStock(symbol, quantity) {
    return await apiCall('/api/v1/trade/sell', {
        method: 'POST',
        body: JSON.stringify({ symbol, quantity })
    });
}

// Portfolio
async function getPortfolio() {
    return await apiCall('/api/v1/users/portfolio', { method: 'GET' });
}

async function getStockNews(size = 10) {
    return await apiCall(`/api/v1/stocks/news?size=${size}`, {
        method: 'GET'
    });
}