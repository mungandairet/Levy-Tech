"use strict";

document.addEventListener("DOMContentLoaded", async () => {
  const error = document.querySelector("#dashboard-error");
  try {
    const [markets, traders, stalls] = await Promise.all([
      MarketLevy.request("/markets"),
      MarketLevy.request("/traders"),
      MarketLevy.request("/stalls")
    ]);
    const occupied = stalls.filter(stall => stall.occupiedByTraderId != null).length;
    document.querySelector("#total-markets").textContent = markets.length;
    document.querySelector("#total-traders").textContent = traders.length;
    document.querySelector("#total-stalls").textContent = stalls.length;
    document.querySelector("#occupied-stalls").textContent = occupied;
    document.querySelector("#available-stalls").textContent = stalls.length - occupied;
  } catch (requestError) {
    error.hidden = false;
    error.textContent = `Dashboard data could not be loaded: ${requestError.message}`;
    ["#total-markets", "#total-traders", "#total-stalls", "#occupied-stalls", "#available-stalls"]
      .forEach(selector => { document.querySelector(selector).textContent = "—"; });
  }
});
