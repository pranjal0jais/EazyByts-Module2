// js/utils.js

function showAlertElement(containerId, message, type = 'danger') {
    const container = typeof containerId === 'string' ? document.getElementById(containerId) : containerId;
    if (!container) return;
    container.innerHTML = `
    <div class="alert alert-${type} alert-dismissible fade show" role="alert">
      ${message}
      <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
  `;
}

function showError(message, containerId) { showAlertElement(containerId, message, 'danger'); }
function showSuccess(message, containerId) { showAlertElement(containerId, message, 'success'); }

function showLoading(elementId) {
    const el = document.getElementById(elementId);
    if (!el) return;
    el.classList.remove('d-none');
    // if it's a button, disable and show spinner
    if (el.tagName === 'BUTTON') {
        el.disabled = true;
        el.dataset.prevText = el.innerHTML;
        el.innerHTML = `<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span> Loading`;
    }
}

function hideLoading(elementId) {
    const el = document.getElementById(elementId);
    if (!el) return;
    el.classList.add('d-none');
    if (el.tagName === 'BUTTON') {
        el.disabled = false;
        if (el.dataset.prevText) el.innerHTML = el.dataset.prevText;
    }
}

function validateEmail(email) {
    if (!email) return false;
    const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return re.test(email);
}
function validatePassword(password) {
    return typeof password === 'string' && password.length >= 6;
}
function validateStockSymbol(symbol) {
    return /^[A-Z]{1,5}$/.test(symbol);
}

function formatCurrency(amount) {
    const n = Number(amount || 0);
    return n.toLocaleString(undefined, { style: 'currency', currency: 'USD', minimumFractionDigits: 2 });
}
function formatPercentage(value) {
    const n = Number(value || 0);
    return `${n.toFixed(2)}%`;
}
function formatDate(dateString) {
    try {
        if (!dateString) return '';
        let d;
        const str = String(dateString);
        // Handle compact format: yyyyMMddTHHmmss (e.g., 20251011T011315)
        const m = /^(\d{4})(\d{2})(\d{2})T(\d{2})(\d{2})(\d{2})$/.exec(str);
        if (m) {
            const [, y, mo, da, h, mi, s] = m;
            d = new Date(
                Number(y),
                Number(mo) - 1,
                Number(da),
                Number(h),
                Number(mi),
                Number(s)
            );
        } else if (typeof dateString === 'number') {
            d = new Date(dateString);
        } else {
            d = new Date(str);
        }
        if (isNaN(d.getTime())) {
            return str;
        }
        return d.toLocaleDateString();
    } catch (e) {
        return String(dateString);
    }
}
function calculateProfitLoss(current, invested) {
    return {
        absolute: Number(current) - Number(invested),
        percent: invested ? ((Number(current) - Number(invested)) / Number(invested)) * 100 : 0
    };
}
