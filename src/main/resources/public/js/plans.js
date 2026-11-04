"use strict";

document.addEventListener("DOMContentLoaded", () => {
  const rows = document.querySelector("#plan-rows");
  const message = document.querySelector("#plan-message");
  const dialog = document.querySelector("#plan-dialog");
  const form = document.querySelector("#plan-form");
  const formError = document.querySelector("#plan-form-error");
  const typeSelect = document.querySelector("#plan-type");
  const surchargeField = document.querySelector("#surcharge-field");
  const surchargeInput = document.querySelector("#plan-surcharge");
  let plans = [];

  async function loadPlans() {
    MarketLevy.setMessage(message, "Loading plans…");
    try {
      plans = await MarketLevy.request("/plans");
      rows.innerHTML = plans.map(plan => `
        <tr>
          <td><span class="primary-cell">${MarketLevy.escapeHtml(plan.name)}</span><span class="sub-cell">Plan #${plan.id}</span></td>
          <td>${plan.planType === "FLAT" ? "Flat levy" : "Base plus surcharge"}</td>
          <td>${Number(plan.baseNgwee).toLocaleString()} ngwee</td>
          <td>${Number(plan.surchargeNgwee).toLocaleString()} ngwee</td>
          <td>${plan.graceDays}</td>
        </tr>`).join("");
      document.querySelector("#plan-count").textContent =
        `${plans.length} ${plans.length === 1 ? "plan" : "plans"}`;
      MarketLevy.setMessage(
        message,
        plans.length ? "" : "No levy plans yet. Add a plan before registering traders."
      );
    } catch (error) {
      MarketLevy.setMessage(message, `Could not load plans: ${error.message}`, true);
      document.querySelector("#plan-count").textContent = "Unavailable";
    }
  }

  function updateTypeFields() {
    const hasSurcharge = typeSelect.value === "BASE_PLUS_SURCHARGE";
    surchargeField.hidden = !hasSurcharge;
    surchargeInput.disabled = !hasSurcharge;
    document.querySelector("#base-label").textContent =
      hasSurcharge ? "Base amount" : "Levy amount";
  }

  document.querySelector("#add-plan").addEventListener("click", () => {
    form.reset();
    formError.textContent = "";
    updateTypeFields();
    dialog.showModal();
  });
  typeSelect.addEventListener("change", updateTypeFields);

  form.addEventListener("submit", async event => {
    event.preventDefault();
    formError.textContent = "";
    const body = {
      name: document.querySelector("#plan-name").value.trim(),
      planType: typeSelect.value,
      baseNgwee: Number(document.querySelector("#plan-base").value),
      surchargeNgwee: typeSelect.value === "BASE_PLUS_SURCHARGE"
        ? Number(surchargeInput.value)
        : 0,
      graceDays: Number(document.querySelector("#plan-grace").value)
    };
    if (!body.name || !Number.isSafeInteger(body.baseNgwee) ||
        !Number.isSafeInteger(body.surchargeNgwee) ||
        !Number.isSafeInteger(body.graceDays) ||
        body.baseNgwee < 0 || body.surchargeNgwee < 0 || body.graceDays < 0) {
      formError.textContent = "Enter a name and whole, non-negative amounts and grace days.";
      return;
    }
    try {
      await MarketLevy.request("/plans", {
        method: "POST",
        body: JSON.stringify(body)
      });
      dialog.close();
      MarketLevy.showToast("Levy plan created.");
      await loadPlans();
    } catch (error) {
      formError.textContent = error.message;
    }
  });

  loadPlans();
});
