// js/trade.js
async function getQuote(symbol) {
    if (!validateStockSymbol(symbol)) {
        throw new Error('Invalid symbol');
    }
    const res = await getStockQuote(symbol);
    if (!res.success) throw new Error(res.error?.message || 'Could not get quote');
    return res.data;
}

// BUY logic
document.addEventListener('DOMContentLoaded', () => {
    // buy UI elements
    const getQuoteBtn = document.getElementById('getQuoteBtn');
    const buySymbol = document.getElementById('buySymbol');
    const buyPrice = document.getElementById('buyPrice');
    const buyQuantity = document.getElementById('buyQuantity');
    const buyTotal = document.getElementById('buyTotal');
    const buyForm = document.getElementById('buyForm');
    const buyBtn = document.getElementById('buyBtn');

    getQuoteBtn.addEventListener('click', async () => {
        const sym = buySymbol.value.trim().toUpperCase();
        if (!validateStockSymbol(sym)) {
            showError('Enter valid symbol (1-5 uppercase letters)', 'alertContainer');
            return;
        }
        showLoading('getQuoteBtn');
        try {
            const q = await getQuote(sym);
            buyPrice.textContent = formatCurrency(q.price);
            calculateTotalCost(q.price, Number(buyQuantity.value));
            buyPrice.dataset.price = q.price;
        } catch (err) {
            showError(err.message || 'Quote not found', 'alertContainer');
        } finally {
            hideLoading('getQuoteBtn');
        }
    });

    buyQuantity.addEventListener('input', () => {
        const price = Number(buyPrice.dataset.price || 0);
        calculateTotalCost(price, Number(buyQuantity.value || 0));
    });

    function calculateTotalCost(price, quantity) {
        const total = (Number(price) || 0) * (Number(quantity) || 0);
        buyTotal.textContent = formatCurrency(total);
        return total;
    }

    buyForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const symbol = buySymbol.value.trim().toUpperCase();
        const qty = parseInt(buyQuantity.value, 10);

        if (!validateStockSymbol(symbol)) { showError('Invalid symbol', 'alertContainer'); return; }
        if (!(Number.isInteger(qty) && qty >= 1)) { showError('Quantity must be an integer >= 1', 'alertContainer'); return; }

        const price = Number(buyPrice.dataset.price || 0);
        if (price <= 0) { showError('Get quote first', 'alertContainer'); return; }

        showLoading('buyBtn');
        try {
            const res = await buyStock(symbol, qty);
            if (res.success) {
                showSuccess(`${res.data.quantity} shares of ${res.data.stockSymbol} bought.`, 'alertContainer');
                // refresh holdings or dashboard
                const user = getUserData();
                // naive: deduct total from local user balance (backend is ultimate source)
                // you might call getPortfolio or re-fetch user info here
            } else {
                showError(res.error?.message || 'Buy failed', 'alertContainer');
            }
        } catch (err) {
            showError('Network error', 'alertContainer');
        } finally {
            hideLoading('buyBtn');
        }
    });

    // SELL UI
    const sellSelect = document.getElementById('sellHoldingSelect');
    const sellPrice = document.getElementById('sellPrice');
    const sellQuantity = document.getElementById('sellQuantity');
    const sellMaxText = document.getElementById('sellMaxText');
    const sellPL = document.getElementById('sellPL');
    const sellForm = document.getElementById('sellForm');
    const sellBtn = document.getElementById('sellBtn');

    async function populateSellHoldings() {
        try {
            const res = await getPortfolio();
            sellSelect.innerHTML = '<option value="">Select holding...</option>';
            if (res.success) {
                const list = res.data.stockSummary || [];
                for (const h of list) {
                    const opt = document.createElement('option');
                    opt.value = h.symbol;
                    opt.textContent = `${h.symbol} — ${h.quantity} shares`;
                    opt.dataset.quantity = h.quantity;
                    opt.dataset.invested = h.investedValue;
                    sellSelect.appendChild(opt);
                }

                // if session has a preselected symbol (from other pages)
                const pre = sessionStorage.getItem('sell_symbol');
                if (pre) {
                    sellSelect.value = pre;
                    sessionStorage.removeItem('sell_symbol');
                    onHoldingSelected();
                }
            } else {
                showError(res.error?.message || 'Could not load holdings', 'alertContainer');
            }
        } catch (err) {
            showError('Network error', 'alertContainer');
        }
    }

    sellSelect.addEventListener('change', onHoldingSelected);

    async function onHoldingSelected() {
        const symbol = sellSelect.value;
        if (!symbol) {
            sellPrice.textContent = '—';
            sellMaxText.textContent = '';
            sellPL.textContent = '—';
            return;
        }
        const opt = sellSelect.selectedOptions[0];
        const maxQty = Number(opt.dataset.quantity || 0);
        sellQuantity.max = maxQty;
        sellQuantity.value = Math.min(1, maxQty);
        sellMaxText.textContent = `Max: ${maxQty}`;

        showLoading('sellBtn');
        try {
            const q = await getQuote(symbol);
            sellPrice.textContent = formatCurrency(q.price);
            sellPrice.dataset.price = q.price;

            // potential P/L per unit (current - invested per unit)
            const investedTotal = Number(opt.dataset.invested || 0);
            const investedPer = investedTotal / Math.max(1, maxQty);
            const plPer = Number(q.price) - investedPer;
            sellPL.textContent = `${formatCurrency(plPer * Number(sellQuantity.value || 1))} (${formatCurrency(plPer)}/share)`;
            sellPL.className = plPer >= 0 ? 'text-success' : 'text-danger';
        } catch (err) {
            showError(err.message || 'Quote not found', 'alertContainer');
            sellPrice.textContent = '—';
        } finally {
            hideLoading('sellBtn');
        }
    }

    sellQuantity.addEventListener('input', () => {
        const price = Number(sellPrice.dataset.price || 0);
        const q = Number(sellQuantity.value || 0);
        const opt = sellSelect.selectedOptions[0];
        const investedTotal = Number(opt?.dataset.invested || 0);
        const maxQty = Number(opt?.dataset.quantity || 0);
        const investedPer = investedTotal / Math.max(1, maxQty);
        const plPer = price - investedPer;
        sellPL.textContent = `${formatCurrency(plPer * q)} (${formatCurrency(plPer)}/share)`;
        sellPL.className = plPer >= 0 ? 'text-success' : 'text-danger';
    });

    sellForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const symbol = sellSelect.value;
        const qty = parseInt(sellQuantity.value, 10);
        const opt = sellSelect.selectedOptions[0];
        if (!symbol) { showError('Select a holding', 'alertContainer'); return; }
        const maxQty = Number(opt.dataset.quantity || 0);
        if (!(Number.isInteger(qty) && qty >= 1 && qty <= maxQty)) { showError('Invalid quantity', 'alertContainer'); return; }

        showLoading('sellBtn');
        try {
            const res = await sellStock(symbol, qty);
            if (res.success) {
                showSuccess(`${qty} shares of ${symbol} sold.`, 'alertContainer');
                await populateSellHoldings();
            } else {
                showError(res.error?.message || 'Sell failed', 'alertContainer');
            }
        } catch (err) {
            showError('Network error', 'alertContainer');
        } finally {
            hideLoading('sellBtn');
        }
    });

    // initial populate
    populateSellHoldings();
});

// Expose function for dashboard -> trade navigation
async function loadUserHoldings() {
    // wrapper used on page load in trade.html
    try {
        const res = await getPortfolio();
        const select = document.getElementById('sellHoldingSelect');
        if (res.success) {
            select.innerHTML = '<option value="">Select holding...</option>';
            (res.data.stockSummary || []).forEach(h => {
                const opt = document.createElement('option');
                opt.value = h.symbol;
                opt.textContent = `${h.symbol} — ${h.quantity} shares`;
                opt.dataset.quantity = h.quantity;
                opt.dataset.invested = h.investedValue;
                select.appendChild(opt);
            });

            // if redirected with symbol
            const pre = sessionStorage.getItem('sell_symbol');
            if (pre) {
                select.value = pre;
                sessionStorage.removeItem('sell_symbol');
                const ev = new Event('change');
                select.dispatchEvent(ev);
            }
        } else {
            showError(res.error?.message || 'Could not load holdings', 'alertContainer');
        }
    } catch (err) {
        showError('Network error', 'alertContainer');
    }
}
