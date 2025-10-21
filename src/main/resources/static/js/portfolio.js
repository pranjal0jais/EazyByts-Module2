// js/portfolio.js
async function loadPortfolio() {
    const alertContainer = 'alertContainer';
    document.getElementById('holdingsTableWrapper').classList.add('d-none');
    document.getElementById('emptyState').classList.add('d-none');
    document.getElementById('loadingArea').classList.remove('d-none');

    try {
        const res = await getPortfolio();
        if (res.success) {
            renderPortfolioSummary(res.data);
            renderHoldingsTable(res.data.stockSummary || []);
        } else {
            showError(res.error?.message || 'Could not fetch portfolio', alertContainer);
        }
    } catch (err) {
        showError('Network error', alertContainer);
    } finally {
        document.getElementById('loadingArea').classList.add('d-none');
    }
}

function renderPortfolioSummary(data) {
    const container = document.getElementById('summaryCards');
    container.innerHTML = '';
    const invested = data.totalInvestedValue ?? 0;
    const current = data.totalCurrentValue ?? 0;
    const pl = data.totalProfitLoss ?? (current - invested);
    const plPct = invested ? ((current - invested) / invested) * 100 : 0;

    container.innerHTML = `
    <div class="col-md-4">
      <div class="card p-3">
        <h6 class="text-muted">Invested Value</h6>
        <div class="h5">${formatCurrency(invested)}</div>
      </div>
    </div>
    <div class="col-md-4">
      <div class="card p-3">
        <h6 class="text-muted">Current Value</h6>
        <div class="h5">${formatCurrency(current)}</div>
      </div>
    </div>
    <div class="col-md-4">
      <div class="card p-3">
        <h6 class="text-muted">Total P/L</h6>
        <div class="h5 ${pl>=0?'text-success':'text-danger'}">${formatCurrency(pl)} <small>(${formatPercentage(plPct)})</small></div>
      </div>
    </div>
  `;
}

function renderHoldingsTable(stockSummary) {
    const tbody = document.getElementById('holdingsTableBody');
    if (!stockSummary || stockSummary.length === 0) {
        document.getElementById('emptyState').classList.remove('d-none');
        document.getElementById('holdingsTableWrapper').classList.add('d-none');
        tbody.innerHTML = '';
        return;
    }

    document.getElementById('emptyState').classList.add('d-none');
    document.getElementById('holdingsTableWrapper').classList.remove('d-none');

    tbody.innerHTML = '';
    for (const s of stockSummary) {
        const pl = s.profitLoss ?? (s.currentValue - s.investedValue);
        const plClass = pl >= 0 ? 'text-success' : 'text-danger';
        const tr = document.createElement('tr');

        tr.innerHTML = `
      <td>${s.symbol}</td>
      <td>${s.quantity}</td>
      <td>${formatCurrency(s.investedValue)}</td>
      <td>${formatCurrency(s.currentValue)}</td>
      <td class="${plClass}">${formatCurrency(pl)} <small>${formatPercentage((s.currentValue-s.investedValue)/Math.max(1,s.investedValue)*100)}</small></td>
      <td>
        <a class="btn btn-sm btn-outline-primary view-btn" data-symbol="${s.symbol}">View</a>
        <a class="btn btn-sm btn-outline-danger sell-btn" data-symbol="${s.symbol}">Sell</a>
      </td>
    `;

        tbody.appendChild(tr);
    }

    // attach handlers
    document.querySelectorAll('.view-btn').forEach(btn => {
        btn.addEventListener('click', (e) => {
            const symbol = btn.dataset.symbol;
            window.location.href = `stock-search.html?symbol=${encodeURIComponent(symbol)}`;
        });
    });

    document.querySelectorAll('.sell-btn').forEach(btn => {
        btn.addEventListener('click', (e) => {
            const symbol = btn.dataset.symbol;
            // go to trade and open sell tab with selected symbol (store in session)
            sessionStorage.setItem('sell_symbol', symbol);
            window.location.href = 'trade.html#sell';
        });
    });
}

async function refreshPortfolio() {
    await loadPortfolio();
}
