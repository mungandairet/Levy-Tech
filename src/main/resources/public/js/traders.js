"use strict";

document.addEventListener("DOMContentLoaded", () => {
  const dialog = document.querySelector("#trader-dialog");
  const form = document.querySelector("#trader-form");
  const rows = document.querySelector("#trader-rows");
  const message = document.querySelector("#trader-message");
  const search = document.querySelector("#trader-search");
  const formError = document.querySelector("#trader-form-error");
  const planSelect = document.querySelector("#trader-plan");
  const stallSelect = document.querySelector("#trader-stall");
  const formHelp = document.querySelector("#trader-form-help");
  let traders = [];
  let plans = [];
  let stalls = [];

  async function loadData() {
    MarketLevy.setMessage(message, "Loading traders…");
    try {
      [traders, plans, stalls] = await Promise.all([
        MarketLevy.request("/traders"),
        MarketLevy.request("/plans"),
        MarketLevy.request("/stalls")
      ]);
      renderTraders();
    } catch (error) {
      MarketLevy.setMessage(message, `Could not load trader data: ${error.message}`, true);
      document.querySelector("#trader-count").textContent = "Unavailable";
    }
  }

  function planName(id) {
    return plans.find(plan => plan.id === id)?.name || `Plan #${id}`;
  }

  function stallName(id) {
    return stalls.find(stall => stall.id === id)?.code || `Stall #${id}`;
  }

  function renderTraders() {
    const query = search.value.trim().toLowerCase();
    const visible = traders.filter(trader =>
      `${trader.fullName} ${trader.phone || ""} ${trader.productCategory}`
        .toLowerCase().includes(query));
    rows.innerHTML = visible.map(trader => `
      <tr>
        <td><span class="primary-cell">${MarketLevy.escapeHtml(trader.fullName)}</span><span class="sub-cell">Trader #${trader.id}</span></td>
        <td>${MarketLevy.escapeHtml(trader.phone || "—")}</td>
        <td>${MarketLevy.escapeHtml(trader.productCategory)}</td>
        <td>${MarketLevy.escapeHtml(planName(trader.planId))}</td>
        <td>${trader.stallId == null ? "—" : MarketLevy.escapeHtml(stallName(trader.stallId))}</td>
        <td><span class="badge ${trader.active ? "badge-active" : "badge-inactive"}">${trader.active ? "Active" : "Inactive"}</span></td>
        <td><div class="row-actions">
          <button class="action-button" data-action="edit" data-id="${trader.id}">Edit</button>
          <button class="action-button danger" data-action="delete" data-id="${trader.id}">Deactivate</button>
        </div></td>
      </tr>`).join("");
    document.querySelector("#trader-count").textContent =
      `${traders.length} ${traders.length === 1 ? "trader" : "traders"}`;
    MarketLevy.setMessage(
      message,
      visible.length ? "" : (query ? "No traders match your search." : "No traders registered yet.")
    );
  }

  function fillOptions(currentTrader = null) {
    planSelect.innerHTML =
      `<option value="">${plans.length ? "Select a levy plan" : "No plans available"}</option>` +
      plans.map(plan =>
        `<option value="${plan.id}">${MarketLevy.escapeHtml(plan.name)}</option>`
      ).join("");
    const availableStalls = stalls.filter(stall =>
      stall.occupiedByTraderId == null ||
      stall.occupiedByTraderId === currentTrader?.id);
    stallSelect.innerHTML = `<option value="">No stall assigned</option>` +
      availableStalls.map(stall =>
        `<option value="${stall.id}">${MarketLevy.escapeHtml(stall.code)} — ${MarketLevy.escapeHtml(stall.section)}</option>`
      ).join("");
  }

  function openTrader(trader = null) {
    form.reset();
    formError.textContent = "";
    fillOptions(trader);
    document.querySelector("#trader-id").value = trader?.id ?? "";
    document.querySelector("#trader-name").value = trader?.fullName ?? "";
    document.querySelector("#trader-phone").value = trader?.phone ?? "";
    document.querySelector("#trader-category").value = trader?.productCategory ?? "";
    planSelect.value = trader?.planId ?? "";
    stallSelect.value = trader?.stallId ?? "";
    document.querySelector("#trader-active").checked = trader?.active ?? true;
    document.querySelector("#trader-dialog-title").textContent =
      trader ? "Edit trader" : "Register trader";
    formHelp.innerHTML = plans.length
      ? "Balance and arrears are managed by payments and billing, not this profile form."
      : `No levy plans are configured. <a href="/plans.html">Create a levy plan</a> before registering traders.`;
    dialog.showModal();
  }

  document.querySelector("#add-trader").addEventListener("click", () => openTrader());
  search.addEventListener("input", renderTraders);
  rows.addEventListener("click", async event => {
    const button = event.target.closest("button[data-action]");
    if (!button) return;
    const trader = traders.find(item => item.id === Number(button.dataset.id));
    if (!trader) return;
    if (button.dataset.action === "edit") {
      openTrader(trader);
    } else if (button.dataset.action === "delete" &&
               window.confirm(`Deactivate ${trader.fullName}? Their stall assignment will be released.`)) {
      try {
        await MarketLevy.request(`/traders/${trader.id}`, { method: "DELETE" });
        MarketLevy.showToast("Trader deactivated.");
        await loadData();
      } catch (error) {
        MarketLevy.showToast(error.message, "error");
      }
    }
  });

  form.addEventListener("submit", async event => {
    event.preventDefault();
    formError.textContent = "";
    const id = document.querySelector("#trader-id").value;
    const body = {
      fullName: document.querySelector("#trader-name").value.trim(),
      phone: document.querySelector("#trader-phone").value.trim() || null,
      productCategory: document.querySelector("#trader-category").value.trim(),
      planId: Number(planSelect.value),
      stallId: stallSelect.value ? Number(stallSelect.value) : null,
      active: document.querySelector("#trader-active").checked
    };
    if (!body.fullName || !body.productCategory || !body.planId) {
      formError.textContent = "Enter a name, product category, and valid levy plan.";
      return;
    }
    try {
      await MarketLevy.request(id ? `/traders/${id}` : "/traders", {
        method: id ? "PUT" : "POST",
        body: JSON.stringify(body)
      });
      dialog.close();
      MarketLevy.showToast(id ? "Trader updated." : "Trader registered.");
      await loadData();
    } catch (error) {
      formError.textContent = error.message;
    }
  });

  loadData();
});
