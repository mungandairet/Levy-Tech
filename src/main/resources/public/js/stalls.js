"use strict";

document.addEventListener("DOMContentLoaded", () => {
  const dialog = document.querySelector("#stall-dialog");
  const form = document.querySelector("#stall-form");
  const rows = document.querySelector("#stall-rows");
  const message = document.querySelector("#stall-message");
  const search = document.querySelector("#stall-search");
  const formError = document.querySelector("#stall-form-error");
  let stalls = [];
  let traders = [];

  async function loadData() {
    MarketLevy.setMessage(message, "Loading stalls…");
    try {
      [stalls, traders] = await Promise.all([
        MarketLevy.request("/stalls"),
        MarketLevy.request("/traders")
      ]);
      renderStalls();
    } catch (error) {
      MarketLevy.setMessage(message, `Could not load stalls: ${error.message}`, true);
      document.querySelector("#stall-count").textContent = "Unavailable";
    }
  }

  function renderStalls() {
    const query = search.value.trim().toLowerCase();
    const visible = stalls.filter(stall =>
      `${stall.code} ${stall.section}`.toLowerCase().includes(query));
    rows.innerHTML = visible.map(stall => {
      const assigned = traders.find(trader =>
        trader.id === stall.occupiedByTraderId && trader.active);
      return `<tr>
        <td><span class="primary-cell">${MarketLevy.escapeHtml(stall.code)}</span><span class="sub-cell">Stall #${stall.id}</span></td>
        <td>${MarketLevy.escapeHtml(stall.section)}</td>
        <td>${Number(stall.sizeSqm).toLocaleString(undefined, { maximumFractionDigits: 2 })} m²</td>
        <td>${assigned
          ? `<span class="badge badge-occupied">Occupied</span><span class="sub-cell">${MarketLevy.escapeHtml(assigned.fullName)}</span>`
          : `<span class="badge badge-available">Available</span>`}</td>
        <td><div class="row-actions">
          <button class="action-button" data-action="edit" data-id="${stall.id}">Edit</button>
          <button class="action-button danger" data-action="delete" data-id="${stall.id}">Delete</button>
        </div></td>
      </tr>`;
    }).join("");
    document.querySelector("#stall-count").textContent =
      `${stalls.length} ${stalls.length === 1 ? "stall" : "stalls"}`;
    MarketLevy.setMessage(
      message,
      visible.length ? "" : (query ? "No stalls match your search." : "No stalls registered yet.")
    );
  }

  function openStall(stall = null) {
    form.reset();
    formError.textContent = "";
    document.querySelector("#stall-id").value = stall?.id ?? "";
    document.querySelector("#stall-code").value = stall?.code ?? "";
    document.querySelector("#stall-section").value = stall?.section ?? "";
    document.querySelector("#stall-size").value = stall?.sizeSqm ?? "";
    document.querySelector("#stall-dialog-title").textContent = stall ? "Edit stall" : "Add stall";
    dialog.showModal();
  }

  document.querySelector("#add-stall").addEventListener("click", () => openStall());
  search.addEventListener("input", renderStalls);
  rows.addEventListener("click", async event => {
    const button = event.target.closest("button[data-action]");
    if (!button) return;
    const stall = stalls.find(item => item.id === Number(button.dataset.id));
    if (!stall) return;
    if (button.dataset.action === "edit") {
      openStall(stall);
    } else if (button.dataset.action === "delete" &&
               window.confirm(`Delete stall ${stall.code}?`)) {
      try {
        await MarketLevy.request(`/stalls/${stall.id}`, { method: "DELETE" });
        MarketLevy.showToast("Stall deleted.");
        await loadData();
      } catch (error) {
        MarketLevy.showToast(error.message, "error");
      }
    }
  });

  form.addEventListener("submit", async event => {
    event.preventDefault();
    formError.textContent = "";
    const id = document.querySelector("#stall-id").value;
    const body = {
      code: document.querySelector("#stall-code").value.trim(),
      section: document.querySelector("#stall-section").value.trim(),
      sizeSqm: Number(document.querySelector("#stall-size").value)
    };
    if (!body.code || !body.section || !Number.isFinite(body.sizeSqm) || body.sizeSqm <= 0) {
      formError.textContent = "Enter a code, section, and positive stall size.";
      return;
    }
    try {
      await MarketLevy.request(id ? `/stalls/${id}` : "/stalls", {
        method: id ? "PUT" : "POST",
        body: JSON.stringify(body)
      });
      dialog.close();
      MarketLevy.showToast(id ? "Stall updated." : "Stall created.");
      await loadData();
    } catch (error) {
      formError.textContent = error.message;
    }
  });

  loadData();
});
