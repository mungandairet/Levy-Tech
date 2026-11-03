"use strict";

window.MarketLevy = (() => {
  const apiBase = `${window.location.origin}/api`;
  let toastTimer;

  async function request(path, options = {}) {
    const response = await fetch(`${apiBase}${path}`, {
      ...options,
      headers: {
        ...(options.body ? { "Content-Type": "application/json" } : {}),
        ...options.headers
      }
    });

    if (response.status === 204) return null;

    const contentType = response.headers.get("content-type") || "";
    const result = contentType.includes("application/json")
      ? await response.json()
      : await response.text();

    if (!response.ok) {
      const message = result && typeof result === "object"
        ? result.message || result.error
        : result;
      throw new Error(message || `Request failed with status ${response.status}`);
    }

    return result;
  }

  function escapeHtml(value) {
    return String(value ?? "").replace(/[&<>"']/g, character => ({
      "&": "&amp;",
      "<": "&lt;",
      ">": "&gt;",
      "\"": "&quot;",
      "'": "&#39;"
    })[character]);
  }

  function showToast(message, type = "success") {
    const toast = document.querySelector("#toast");
    if (!toast) return;
    toast.textContent = message;
    toast.classList.toggle("error", type === "error");
    toast.classList.add("show");
    window.clearTimeout(toastTimer);
    toastTimer = window.setTimeout(() => toast.classList.remove("show"), 3200);
  }

  function setMessage(element, message, isError = false) {
    element.textContent = message;
    element.classList.toggle("error", isError);
  }

  function bindCloseButtons() {
    document.querySelectorAll("[data-close]").forEach(button => {
      button.addEventListener("click", () => button.closest("dialog")?.close());
    });
  }

  return { request, escapeHtml, showToast, setMessage, bindCloseButtons };
})();

document.addEventListener("DOMContentLoaded", () => {
  window.MarketLevy.bindCloseButtons();
});
