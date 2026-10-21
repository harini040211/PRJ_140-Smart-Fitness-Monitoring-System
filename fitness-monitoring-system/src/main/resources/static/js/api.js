const TOKEN_KEY = "fms_token";
const NAME_KEY = "fms_name";

function saveSession(data) {
  localStorage.setItem(TOKEN_KEY, data.token);
  localStorage.setItem(NAME_KEY, data.fullName);
}

function getToken() {
  return localStorage.getItem(TOKEN_KEY);
}

function getFullName() {
  return localStorage.getItem(NAME_KEY) || "Member";
}

function clearSession() {
  localStorage.removeItem(TOKEN_KEY);
  localStorage.removeItem(NAME_KEY);
}

function logout() {
  clearSession();
  window.location.href = "login.html";
}

function requireLogin() {
  if (!getToken()) {
    window.location.replace("login.html");
    return false;
  }
  return true;
}

async function request(method, path, body) {
  const headers = {};
  const token = getToken();
  if (token) headers["Authorization"] = "Bearer " + token;
  if (body !== undefined) headers["Content-Type"] = "application/json";

  let res;
  try {
    res = await fetch("/api" + path, {
      method,
      headers,
      body: body === undefined ? undefined : JSON.stringify(body)
    });
  } catch (e) {
    throw new Error("Cannot reach the server. Check your connection and try again.");
  }

  if (res.status === 401 && token) {
    clearSession();
    window.location.href = "login.html?expired=1";
    throw new Error("Your session has expired");
  }

  let payload = null;
  try {
    payload = await res.json();
  } catch (e) {
    payload = null;
  }
  if (!res.ok || !payload || payload.success === false) {
    throw new Error((payload && payload.message) || "Something went wrong. Please try again.");
  }
  return payload.data;
}

const apiGet = (path) => request("GET", path);
const apiPost = (path, body) => request("POST", path, body === undefined ? {} : body);
const apiPut = (path, body) => request("PUT", path, body);
const apiDelete = (path) => request("DELETE", path);

async function downloadCsv(type, filename) {
  const res = await fetch("/api/export/" + type, { headers: { Authorization: "Bearer " + getToken() } });
  if (!res.ok) throw new Error("Could not export your data");
  const blob = await res.blob();
  const url = URL.createObjectURL(blob);
  const a = document.createElement("a");
  a.href = url;
  a.download = filename;
  document.body.appendChild(a);
  a.click();
  a.remove();
  URL.revokeObjectURL(url);
}

function esc(value) {
  return String(value == null ? "" : value).replace(/[&<>"']/g, (c) =>
    ({ "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#39;" }[c]));
}

function num(value, digits) {
  const n = Number(value);
  if (!isFinite(n)) return "0";
  return n.toLocaleString("en-IN", { maximumFractionDigits: digits === undefined ? 0 : digits });
}

function isoDate(d) {
  const date = d || new Date();
  const m = String(date.getMonth() + 1).padStart(2, "0");
  const day = String(date.getDate()).padStart(2, "0");
  return date.getFullYear() + "-" + m + "-" + day;
}

function parseDay(iso) {
  const [y, m, d] = iso.split("-").map(Number);
  return new Date(y, m - 1, d);
}

function prettyDate(iso) {
  const today = isoDate();
  const yesterday = isoDate(new Date(Date.now() - 86400000));
  if (iso === today) return "Today";
  if (iso === yesterday) return "Yesterday";
  return parseDay(iso).toLocaleDateString("en-IN", { day: "numeric", month: "short", year: "numeric" });
}

function shortDay(iso) {
  return parseDay(iso).toLocaleDateString("en-IN", { weekday: "short" });
}

function shortDate(iso) {
  return parseDay(iso).toLocaleDateString("en-IN", { day: "numeric", month: "short" });
}

function toast(message, type) {
  let box = document.getElementById("toasts");
  if (!box) {
    box = document.createElement("div");
    box.id = "toasts";
    box.className = "toasts";
    document.body.appendChild(box);
  }
  const el = document.createElement("div");
  el.className = "toast " + (type || "");
  el.innerHTML = icon(type === "error" ? "alert" : "check") + "<span>" + esc(message) + "</span>";
  box.appendChild(el);
  setTimeout(() => {
    el.style.opacity = "0";
    el.style.transition = "opacity .3s";
    setTimeout(() => el.remove(), 300);
  }, 3200);
}

function showAlert(id, message, type) {
  const el = document.getElementById(id);
  if (!el) return;
  el.textContent = message || "";
  el.className = "alert " + (type || "error");
}

function setBusy(button, busy, label) {
  if (!button) return;
  if (busy) {
    button.dataset.label = button.innerHTML;
    button.disabled = true;
    button.textContent = label || "Please wait...";
  } else {
    button.disabled = false;
    if (button.dataset.label) button.innerHTML = button.dataset.label;
  }
}

function emptyState(iconName, title, text) {
  return '<div class="empty">' + icon(iconName) + "<b>" + esc(title) + "</b><div>" + esc(text) + "</div></div>";
}

function openModal(id) {
  document.getElementById(id).classList.add("open");
}

function closeModal(id) {
  document.getElementById(id).classList.remove("open");
}

document.addEventListener("click", (event) => {
  if (event.target.classList && event.target.classList.contains("modal-back")) {
    event.target.classList.remove("open");
  }
});

const catalogCache = {};
async function loadCatalog(kind) {
  if (!catalogCache[kind]) catalogCache[kind] = await apiGet("/catalog/" + kind);
  return catalogCache[kind];
}

function intensityBadge(level) {
  const tone = level === "High" ? "red" : level === "Low" ? "green" : "orange";
  return '<span class="badge ' + tone + '">' + esc(level) + "</span>";
}
