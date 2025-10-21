document.addEventListener('DOMContentLoaded', async () => {
    checkAuth();
    await loadNews();
});

async function loadNews() {
    const container = document.getElementById('newsContainer');
    if (container) {
        container.innerHTML = `<div class="text-center text-muted py-3"><span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span> Loading news...</div>`;
    }
    try {
        const response = await getStockNews(10);
        if (response.success) {
            renderNewsList(response.data);
        } else {
            showError(response.error?.message || 'Failed to load news', 'newsContainer');
        }
    } catch (error) {
        showError('Network error while fetching news', 'newsContainer');
    }
}

function renderNewsList(newsList) {
    const container = document.getElementById('newsContainer');
    if (!container) return;
    container.innerHTML = '';

    if (!Array.isArray(newsList) || newsList.length === 0) {
        container.innerHTML = `<div class="alert alert-info">No recent news available.</div>`;
        return;
    }

    newsList.forEach(news => {
        const card = document.createElement('div');
        card.className = 'card mb-3 shadow-sm';

        card.innerHTML = `
      <div class="row g-0">
        <div class="col-md-3">
          <img src="${news.image || 'https://via.placeholder.com/150'}" class="img-fluid
          rounded-start object-fit-fill" alt="${news.title}">
        </div>
        <div class="col-md-9">
          <div class="card-body">
            <h5 class="card-title">
              <a href="${news.url}" target="_blank" class="text-decoration-none">${news.title}</a>
            </h5>
            <p class="card-text">${news.summary || ''}</p>
            <p class="card-text">
              <small class="text-muted">
                ${formatDate(news.publishedAt)} • ${news.source} • Sentiment: ${news.sentiment || 'Neutral'}
              </small>
            </p>
          </div>
        </div>
      </div>
    `;
        container.appendChild(card);
    });
}
