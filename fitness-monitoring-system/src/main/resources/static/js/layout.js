const NAV_ITEMS = [
  { key: "dashboard", href: "dashboard.html", icon: "home", label: "Dashboard" },
  { key: "workouts", href: "workout.html", icon: "dumbbell", label: "Workouts" },
  { key: "diet", href: "diet.html", icon: "utensils", label: "Nutrition" },
  { key: "plans", href: "plans.html", icon: "sparkle", label: "My Plans" },
  { key: "progress", href: "progress.html", icon: "chart", label: "Progress" },
  { key: "report", href: "report.html", icon: "file", label: "Weekly Report" },
  { key: "profile", href: "profile.html", icon: "user", label: "Profile & Goals" }
];

const BOTTOM_KEYS = ["dashboard", "workouts", "diet", "plans", "progress", "profile"];
const BOTTOM_LABELS = { dashboard: "Home", workouts: "Workouts", diet: "Food", plans: "Plans", progress: "Progress", profile: "Profile" };

function buildShell() {
  const page = document.body.dataset.page || "";
  const name = getFullName();
  const initial = esc(name.charAt(0).toUpperCase());

  const side = NAV_ITEMS.map((n) =>
    '<a href="' + n.href + '" class="' + (page === n.key ? "active" : "") + '">' + icon(n.icon) + n.label + "</a>").join("");

  const bottom = NAV_ITEMS.filter((n) => BOTTOM_KEYS.includes(n.key)).map((n) =>
    '<a href="' + n.href + '" class="' + (page === n.key ? "active" : "") + '">' + icon(n.icon) + BOTTOM_LABELS[n.key] + "</a>").join("");

  const brand = '<div class="brand"><div class="brand-mark">' + icon("pulse") + "</div><div>FitTrack<small>Fitness Monitor</small></div></div>";

  const content = document.getElementById("content");
  const shell = document.createElement("div");
  shell.className = "shell";
  shell.innerHTML =
    '<aside class="sidebar">' + brand + '<nav class="nav">' + side + "</nav>" +
    '<div class="sidebar-foot"><div class="avatar">' + initial + '</div><div class="who"><b>' + esc(name) +
    '</b><button type="button" id="logout-side">Log out</button></div></div></aside>' +
    '<main class="main"><div class="mobile-top">' + brand +
    '<button type="button" class="icon-btn" id="logout-top" aria-label="Log out">' + icon("logout") + "</button></div>" +
    '<div class="page" id="page"></div></main>' +
    '<nav class="bottom-nav">' + bottom + "</nav>";
  document.body.insertBefore(shell, content);
  document.getElementById("page").appendChild(content);
  content.hidden = false;
  document.getElementById("logout-side").addEventListener("click", logout);
  document.getElementById("logout-top").addEventListener("click", logout);
}

const REMINDER_KEY = "fms_reminders";
const REMINDER_DEFAULTS = {
  water: { on: false, time: "11:00", title: "Time for some water", body: "A glass of water keeps your hydration on track." },
  workout: { on: false, time: "18:00", title: "Workout time", body: "Your scheduled session is waiting. Even 20 minutes counts." },
  meal: { on: false, time: "20:30", title: "Log your meals", body: "Add today's meals so your calorie balance stays accurate." }
};

function getReminders() {
  let saved = {};
  try {
    saved = JSON.parse(localStorage.getItem(REMINDER_KEY) || "{}");
  } catch (e) {
    saved = {};
  }
  const merged = {};
  Object.keys(REMINDER_DEFAULTS).forEach((k) => {
    merged[k] = Object.assign({}, REMINDER_DEFAULTS[k], saved[k] || {});
  });
  return merged;
}

function saveReminders(reminders) {
  try {
    localStorage.setItem(REMINDER_KEY, JSON.stringify(reminders));
  } catch (e) {
    toast("Could not save reminders in this browser", "error");
  }
}

function startReminderLoop() {
  const check = () => {
    const now = new Date();
    const hhmm = String(now.getHours()).padStart(2, "0") + ":" + String(now.getMinutes()).padStart(2, "0");
    const reminders = getReminders();
    let changed = false;
    Object.keys(reminders).forEach((key) => {
      const r = reminders[key];
      if (r.on && r.time === hhmm && r.lastFired !== isoDate()) {
        r.lastFired = isoDate();
        changed = true;
        if ("Notification" in window && Notification.permission === "granted") {
          new Notification(r.title, { body: r.body });
        }
        toast(r.title);
      }
    });
    if (changed) saveReminders(reminders);
  };
  setInterval(check, 20000);
}

document.addEventListener("DOMContentLoaded", () => {
  if (!document.body.dataset.page) return;
  if (!requireLogin()) return;
  buildShell();
  startReminderLoop();
});
