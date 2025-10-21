// js/stock.js

let priceChart;
async function searchStock(symbol) {
    const alertContainer = 'alertContainer';
    symbol = (symbol || '').trim().toUpperCase();
    if (!validateStockSymbol(symbol)) {
        showError('Enter valid symbol (1-5 uppercase letters)', alertContainer);
        return;
    }

    // UI
    document.getElementById('searchResult').classList.add('d-none');
    document.getElementById('searchLoading').classList.remove('d-none');

    try {
        const [qRes, oRes, hRes] = await Promise.all([
            getStockQuote(symbol),
            getStockOverview(symbol),
            getStockHistory(symbol, 30)
        ]);

        if (!qRes.success) throw new Error(qRes.error?.message || 'Quote not found');
        if (!oRes.success) throw new Error(oRes.error?.message || 'Overview not found');
        if (!hRes.success) throw new Error(hRes.error?.message || 'History not found');

        displayStockQuote(qRes.data);
        displayStockOverview(oRes.data);
        displayStockHistory(hRes.data);

        document.getElementById('searchResult').classList.remove('d-none');

        // Buy button link
        const buyThis = document.getElementById('buyThis');
        buyThis.href = `trade.html`;
        buyThis.addEventListener('click', () => {
            sessionStorage.setItem('buy_symbol', symbol);
        }, { once: true });
    } catch (err) {
        showError(err.message || 'Search failed', alertContainer);
    } finally {
        document.getElementById('searchLoading').classList.add('d-none');
    }
}

function displayStockQuote(data) {
    document.getElementById('resultSymbol').textContent = `${data.symbol}`;
    document.getElementById('resultPrice').textContent = formatCurrency(data.price);
}

function displayStockOverview(data) {
    const el = document.getElementById('companyOverview');
    el.innerHTML = `
    <strong>${data.name || data.symbol}</strong> — ${data.exchange || ''} <br>
    ${data.sector ? `<strong>Sector:</strong> ${data.sector} <br>` : ''}
    ${data.country ? `<strong>Country:</strong> ${data.country} <br>` : ''}
    ${data.description ? `<p class="mt-2">${data.description}</p>` : ''}
    ${data.officialSite ? `<a href="${data.officialSite}" target="_blank">Official site</a>` : ''}
  `;
}

function displayStockHistory(historyData) {
    const ctx = document.getElementById('priceHistoryChart').getContext('2d');

    // Prepare data
    const labels = historyData.map(item => formatDate(item.date)); // e.g., "19 Oct"
    const prices = historyData.map(item => item.close);

    // Destroy previous chart if exists
    if (priceChart) priceChart.destroy();

    // Create line chart
    priceChart = new Chart(ctx, {
        type: 'line',
        data: {
            labels: labels,
            datasets: [{
                label: 'Closing Price',
                data: prices,
                borderColor: 'rgba(75, 192, 192, 1)',
                backgroundColor: 'rgba(75, 192, 192, 0.2)',
                tension: 0.2, // smooth curve
                fill: true
            }]
        },
        options: {
            responsive: true,
            plugins: {
                legend: { display: true },
                tooltip: { mode: 'index', intersect: false }
            },
            scales: {
                x: { display: true, title: { display: true, text: 'Date' } },
                y: { display: true, title: { display: true, text: 'Price ($)' } }
            }
        }
    });
}

function clearSearch() {
    document.getElementById('searchSymbol').value = '';
    document.getElementById('searchResult').classList.add('d-none');
    document.getElementById('historyBody').innerHTML = '';
    document.getElementById('companyOverview').innerHTML = '';
}
