const state = {
  token: localStorage.getItem("rc_token") || "",
  user: localStorage.getItem("rc_user") || "",
  role: localStorage.getItem("rc_role") || "",
  shippings: [],
  customers: []
};

const el = (id) => document.getElementById(id);

function apiBase() {
  return el("apiBase").value.replace(/\/$/, "");
}

function setBusy(message = "Cargando") {
  el("statusPill").textContent = message;
}

function setReady(message = "Listo") {
  el("statusPill").textContent = message;
}

function toast(message) {
  const box = el("toast");
  box.textContent = message;
  box.classList.add("show");
  window.clearTimeout(toast.timer);
  toast.timer = window.setTimeout(() => box.classList.remove("show"), 3600);
}

function decodeJwt(token) {
  try {
    const payload = token.split(".")[1].replace(/-/g, "+").replace(/_/g, "/");
    return JSON.parse(atob(payload));
  } catch {
    return {};
  }
}

function updateSession() {
  el("sessionUser").textContent = state.user || "Sin sesion";
  el("sessionRole").textContent = state.role || "JWT requerido";
  el("logoutBtn").disabled = !state.token;
}

async function request(path, options = {}) {
  const headers = {
    "Content-Type": "application/json",
    ...(options.headers || {})
  };

  if (state.token) {
    headers.Authorization = `Bearer ${state.token}`;
  }

  const response = await fetch(`${apiBase()}${path}`, {
    ...options,
    headers
  });

  const text = await response.text();
  const body = text ? JSON.parse(text) : null;

  if (!response.ok) {
    const message = body?.message || body?.error || body?.detail || response.statusText;
    throw new Error(message);
  }

  return body;
}

function formData(form) {
  return Object.fromEntries(new FormData(form).entries());
}

function money(value) {
  const number = Number(value || 0);
  return `S/ ${number.toFixed(2)}`;
}

function statusBadge(status) {
  const cls = status === "ENTREGADO" ? "ok" : status === "CANCELADO" ? "warn" : "";
  return `<span class="badge ${cls}">${status || "SIN_ESTADO"}</span>`;
}

async function login(event) {
  event.preventDefault();
  setBusy("Login");
  try {
    const credentials = formData(event.currentTarget);
    const body = await request("/api/v1/auth/login", {
      method: "POST",
      body: JSON.stringify(credentials)
    });
    const token = body.data?.token;
    if (!token) {
      throw new Error("La respuesta no trajo token");
    }

    const claims = decodeJwt(token);
    state.token = token;
    state.user = claims.sub || credentials.username;
    state.role = claims.role || "";
    localStorage.setItem("rc_token", state.token);
    localStorage.setItem("rc_user", state.user);
    localStorage.setItem("rc_role", state.role);
    updateSession();
    toast("Sesion iniciada");
    await refreshAll();
  } catch (error) {
    toast(error.message);
  } finally {
    setReady();
  }
}

function logout() {
  state.token = "";
  state.user = "";
  state.role = "";
  localStorage.removeItem("rc_token");
  localStorage.removeItem("rc_user");
  localStorage.removeItem("rc_role");
  updateSession();
  toast("Sesion cerrada");
}

async function loadCustomers() {
  setBusy("Clientes");
  const body = await request("/api/v1/customers");
  state.customers = body.data || [];
  renderCustomers();
  renderDashboard();
  setReady();
}

function renderCustomers() {
  const rows = state.customers.map((customer) => `
    <tr>
      <td>${customer.dni || ""}</td>
      <td>${customer.fullName || ""}</td>
      <td>${customer.email || ""}</td>
      <td><button class="ghost" data-fill-dni="${customer.dni || ""}">Usar DNI</button></td>
    </tr>
  `);
  el("customersTable").innerHTML = rows.join("") || `<tr><td colspan="4">Sin clientes</td></tr>`;
}

async function createCustomer(event) {
  event.preventDefault();
  setBusy("Guardando");
  try {
    await request("/api/v1/customers", {
      method: "POST",
      body: JSON.stringify(formData(event.currentTarget))
    });
    event.currentTarget.reset();
    toast("Cliente registrado");
    await loadCustomers();
  } catch (error) {
    toast(error.message);
  } finally {
    setReady();
  }
}

async function deleteCustomer(id) {
  setBusy("Eliminando");
  try {
    await request(`/api/v1/customers/${id}`, { method: "DELETE" });
    toast("Cliente eliminado");
    await loadCustomers();
  } catch (error) {
    toast(error.message);
  } finally {
    setReady();
  }
}

async function loadShippings() {
  setBusy("Envios");
  const params = new URLSearchParams();
  const search = el("shippingSearch").value.trim();
  const branch = el("shippingBranch").value.trim();
  const status = el("shippingStatus").value.trim();
  if (search) params.set("search", search);
  if (!search && branch) params.set("branch", branch);
  if (!search && branch && status) params.set("status", status);

  const query = params.toString() ? `?${params}` : "";
  const body = await request(`/api/v1/shippings${query}`);
  const data = body.data || [];
  state.shippings = status && !branch ? data.filter((item) => item.status === status) : data;
  renderShippings();
  renderDashboard();
  setReady();
}

function renderShippings() {
  const items = state.shippings.map((shipping) => `
    <article class="item">
      <div>
        <h4>${shipping.trackingCode || shipping.id}</h4>
        <p>${shipping.description || ""}</p>
        <p>${shipping.originBranch || ""} -> ${shipping.destinationBranch || ""} · ${money(shipping.tariff)}</p>
        <p>Remitente ${shipping.senderDni || ""} · Destinatario ${shipping.recipientDni || ""}</p>
        <p>ID ${shipping.id}</p>
      </div>
      <div class="item-actions">
        ${statusBadge(shipping.status)}
        <button class="ghost" data-history="${shipping.id}">Historial</button>
        <button class="ghost" data-next-status="${shipping.id}">Avanzar</button>
        <button class="ghost" data-track-id="${shipping.id}">Tracking</button>
      </div>
    </article>
  `);
  el("shippingsList").innerHTML = items.join("") || `<div class="item"><p>Sin envios</p></div>`;
  el("latestShippings").innerHTML = items.slice(0, 4).join("") || `<div class="item"><p>Sin envios</p></div>`;
}

async function createShipping(event) {
  event.preventDefault();
  setBusy("Creando");
  try {
    const data = formData(event.currentTarget);
    data.weightKg = Number(data.weightKg);
    data.declaredValue = Number(data.declaredValue);
    await request("/api/v1/shippings", {
      method: "POST",
      body: JSON.stringify(data)
    });
    event.currentTarget.reset();
    toast("Envio creado");
    await loadShippings();
  } catch (error) {
    toast(error.message);
  } finally {
    setReady();
  }
}

function nextStatus(current) {
  const map = {
    REGISTRADO: "EN_TRANSITO",
    EN_TRANSITO: "EN_REPARTO",
    EN_REPARTO: "ENTREGADO"
  };
  return map[current] || "";
}

async function advanceStatus(id) {
  const shipping = state.shippings.find((item) => item.id === id);
  const newStatus = nextStatus(shipping?.status);
  if (!newStatus) {
    toast("Ese envio no tiene siguiente estado automatico");
    return;
  }

  setBusy("Estado");
  try {
    await request(`/api/v1/shippings/${id}/status`, {
      method: "PATCH",
      body: JSON.stringify({ newStatus })
    });
    toast(`Estado actualizado a ${newStatus}`);
    await loadShippings();
  } catch (error) {
    toast(error.message);
  } finally {
    setReady();
  }
}

async function showHistory(id) {
  setBusy("Historial");
  try {
    const body = await request(`/api/v1/shippings/${id}/history`);
    const history = body.data || [];
    const lines = history.map((item) => `${item.status} - ${item.changedBy || ""} - ${item.changedAt || ""}`);
    toast(lines.join(" | ") || "Sin historial");
  } catch (error) {
    toast(error.message);
  } finally {
    setReady();
  }
}

async function createTracking(event) {
  event.preventDefault();
  setBusy("Tracking");
  try {
    await request("/api/v1/trackings", {
      method: "POST",
      body: JSON.stringify(formData(event.currentTarget))
    });
    const shippingId = el("trackingShippingId").value;
    el("trackingLookupId").value = shippingId;
    event.currentTarget.reset();
    toast("Tracking registrado");
    await loadTracking(shippingId);
  } catch (error) {
    toast(error.message);
  } finally {
    setReady();
  }
}

async function loadTracking(id = el("trackingLookupId").value.trim()) {
  if (!id) {
    toast("Ingresa el ID del envio");
    return;
  }

  setBusy("Tracking");
  try {
    const body = await request(`/api/v1/trackings/shipping/${id}`);
    renderTracking(body.data || []);
  } catch (error) {
    toast(error.message);
  } finally {
    setReady();
  }
}

function renderTracking(items) {
  el("trackingList").innerHTML = items.map((item) => `
    <article class="item">
      <div>
        <h4>${item.status || ""}</h4>
        <p>${item.location || ""}</p>
        <p>${item.timestamp || ""}</p>
      </div>
      ${statusBadge(item.status)}
    </article>
  `).join("") || `<div class="item"><p>Sin tracking</p></div>`;
}

function renderDashboard() {
  const routeStatuses = new Set(["EN_TRANSITO", "EN_REPARTO"]);
  el("customersCount").textContent = state.customers.length;
  el("shippingsCount").textContent = state.shippings.length;
  el("registeredCount").textContent = state.shippings.filter((item) => item.status === "REGISTRADO").length;
  el("routeCount").textContent = state.shippings.filter((item) => routeStatuses.has(item.status)).length;
}

async function refreshAll() {
  if (!state.token) return;
  try {
    await Promise.all([loadCustomers(), loadShippings()]);
  } catch (error) {
    toast(error.message);
    setReady();
  }
}

function switchTab(tabId) {
  document.querySelectorAll(".tab").forEach((tab) => {
    tab.classList.toggle("active", tab.dataset.tab === tabId);
  });
  document.querySelectorAll(".view").forEach((view) => {
    view.classList.toggle("active", view.id === tabId);
  });
  const titles = {
    dashboard: ["Dashboard", "Operacion general de clientes, envios y tracking."],
    customers: ["Clientes", "Registro y consulta de clientes validados por DNI."],
    shippings: ["Envios", "Creacion, busqueda y avance de estados."],
    tracking: ["Tracking", "Eventos asociados a un envio."]
  };
  el("viewTitle").textContent = titles[tabId][0];
  el("viewSubtitle").textContent = titles[tabId][1];
}

document.addEventListener("click", (event) => {
  const tab = event.target.closest("[data-tab]");
  if (tab) switchTab(tab.dataset.tab);

  const fillDni = event.target.closest("[data-fill-dni]");
  if (fillDni) {
    el("senderDni").value = fillDni.dataset.fillDni;
    switchTab("shippings");
  }

  const next = event.target.closest("[data-next-status]");
  if (next) advanceStatus(next.dataset.nextStatus);

  const history = event.target.closest("[data-history]");
  if (history) showHistory(history.dataset.history);

  const track = event.target.closest("[data-track-id]");
  if (track) {
    el("trackingLookupId").value = track.dataset.trackId;
    el("trackingShippingId").value = track.dataset.trackId;
    switchTab("tracking");
    loadTracking(track.dataset.trackId);
  }
});

el("loginForm").addEventListener("submit", login);
el("logoutBtn").addEventListener("click", logout);
el("customerForm").addEventListener("submit", createCustomer);
el("shippingForm").addEventListener("submit", createShipping);
el("trackingForm").addEventListener("submit", createTracking);
el("loadCustomersBtn").addEventListener("click", () => loadCustomers().catch((error) => toast(error.message)));
el("loadShippingsBtn").addEventListener("click", () => loadShippings().catch((error) => toast(error.message)));
el("refreshDashboardBtn").addEventListener("click", refreshAll);
el("loadTrackingBtn").addEventListener("click", () => loadTracking());

updateSession();
refreshAll();
