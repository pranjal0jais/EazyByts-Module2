// js/stock.js

let priceChart;

// UPDATED to accept the 'historyFunction' parameter
async function searchStock(symbol, historyFunction = 'TIME_SERIES_DAILY') {
    const alertContainer = 'alertContainer';
    symbol = (symbol || '').trim().toUpperCase();
    if (!validateStockSymbol(symbol)) {
        showError('Enter valid symbol (1-5 uppercase letters)', alertContainer);
        return;
    }

    // UI
    document.getElementById('searchResult').classList.add('d-none');
    document.getElementById('searchLoading').classList.remove('d-none');
    // Update chart title based on function
    document.getElementById('chartTitle').textContent = formatChartTitle(historyFunction);


    try {
        const [qRes, oRes, hRes] = await Promise.all([
            getStockQuote(symbol),
            getStockOverview(symbol),
            // Pass the historyFunction here
            getStockHistory(symbol, 30, historyFunction)
        ]);

        if (!qRes.success) throw new Error(qRes.error?.message || 'Quote not found');
        if (!oRes.success) throw new Error(oRes.error?.message || 'Overview not found');
        if (!hRes.success) throw new Error(hRes.error?.message || 'History not found');

        displayStockQuote(qRes.data);
        displayStockOverview(oRes.data);
        displayStockHistory(hRes.data); // historyData should be displayed

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
    // Assuming historyData is an array of objects with 'date' and 'close'
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
    // Reset function selector to default
    document.getElementById('functionSelector').value = 'TIME_SERIES_DAILY';
    document.getElementById('searchResult').classList.add('d-none');
    document.getElementById('companyOverview').innerHTML = '';
    document.getElementById('chartTitle').textContent = 'Price History (30 Days)'; // Reset chart title
    if (priceChart) priceChart.destroy(); // Destroy chart on clear
}

// Helper function to format the chart title
function formatChartTitle(func) {
    switch(func) {
        case 'TIME_SERIES_DAILY':
            return 'Daily Price History (30 Days)';
        case 'TIME_SERIES_WEEKLY':
            return 'Weekly Price History (30 Periods)';
        case 'TIME_SERIES_MONTHLY':
            return 'Monthly Price History (30 Periods)';
        case 'TIME_SERIES_INTRADAY':
            return 'Intraday Price History (30 Periods)';
        default:
            return 'Price History (30 Days)';
    }
}
// Note: validateStockSymbol, formatCurrency, formatDate, showError, and the auth functions (getToken, clearAuthData)
// are assumed to be in js/utils.js and js/auth.js, respectively.