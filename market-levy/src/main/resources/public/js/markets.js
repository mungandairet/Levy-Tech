"use strict";

document.addEventListener("DOMContentLoaded", () => {
  const dialog = document.querySelector("#market-dialog");
  const detailsDialog = document.querySelector("#market-details");
  const form = document.querySelector("#market-form");
  const rows = document.querySelector("#market-rows");
  const message = document.querySelector("#market-message");
  const search = document.querySelector("#market-search");
  const formError = document.querySelector("#market-form-error");
  let markets = [];

  async function loadMarkets() {
    MarketLevy.setMessage(message, "Loading markets…");
    try {
      markets = await MarketLevy.request("/markets");
      renderMarkets();
    } catch (error) {
      MarketLevy.setMessage(message, `Could not load markets: ${error.message}`, true);
      document.querySelector("#market-count").textContent = "Unavailable";
    }
  }

  function renderMarkets() {
    const query = search.value.trim().toLowerCase();
    const visible = markets.filter(market =>
      `${market.name} ${market.location}`.toLowerCase().includes(query));
    rows.innerHTML = visible.map(market => `
      <tr>
        <td><span class="primary-cell">${MarketLevy.escapeHtml(market.name)}</span><span class="sub-cell">Market #${market.id}</span></td>
        <td>${MarketLevy.escapeHtml(market.location)}</td>
        <td><div class="row-actions">
          <button class="action-button" data-action="details" data-id="${market.id}">Details</button>
          <button class="action-button" data-action="edit" data-id="${market.id}">Edit</button>
          <button class="action-button danger" data-action="delete" data-id="${market.id}">Delete</button>
        </div></td>
      </tr>`).join("");
    document.querySelector("#market-count").textContent =
      `${markets.length} ${markets.length === 1 ? "market" : "markets"}`;
    MarketLevy.setMessage(
      message,
      visible.length ? "" : (query ? "No markets match your search." : "No markets yet. Add your first market to get started.")
    );
  }

  function openMarket(market = null) {
    form.reset();
    formError.textContent = "";
    document.querySelector("#market-id").value = market?.id ?? "";
    document.querySelector("#market-name").value = market?.name ?? "";
    document.querySelector("#market-location").value = market?.location ?? "";
    document.querySelector("#market-dialog-title").textContent = market ? "Edit market" : "Add market";
    dialog.showModal();
  }

  document.querySelector("#add-market").addEventListener("click", () => openMarket());
  search.addEventListener("input", renderMarkets);
  rows.addEventListener("click", async event => {
    const button = event.target.closest("button[data-action]");
    if (!button) return;
    const market = markets.find(item => item.id === Number(button.dataset.id));
    if (!market) return;

    if (button.dataset.action === "details") {
      document.querySelector("#detail-market-id").textContent = market.id;
      document.querySelector("#detail-market-name").textContent = market.name;
      document.querySelector("#detail-market-location").textContent = market.location;
      detailsDialog.showModal();
    } else if (button.dataset.action === "edit") {
      openMarket(market);
    } else if (button.dataset.action === "delete" &&
               window.confirm(`Delete "${market.name}"? This cannot be undone.`)) {
      try {
        await MarketLevy.request(`/markets/${market.id}`, { method: "DELETE" });
        MarketLevy.showToast("Market deleted.");
        await loadMarkets();
      } catch (error) {
        MarketLevy.showToast(error.message, "error");
      }
    }
  });

  form.addEventListener("submit", async event => {
    event.preventDefault();
    formError.textContent = "";
    const id = document.querySelector("#market-id").value;
    const body = {
      name: document.querySelector("#market-name").value.trim(),
      location: document.querySelector("#market-location").value.trim()
    };
    if (!body.name || !body.location) {
      formError.textContent = "Enter a market name and location.";
      return;
    }
    try {
      await MarketLevy.request(id ? `/markets/${id}` : "/markets", {
        method: id ? "PUT" : "POST",
        body: JSON.stringify(body)
      });
      dialog.close();
      MarketLevy.showToast(id ? "Market updated." : "Market created.");
      await loadMarkets();
    } catch (error) {
      formError.textContent = error.message;
    }
  });

  loadMarkets();
});
