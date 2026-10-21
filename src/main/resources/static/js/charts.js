const PALETTE = { blue: "#2563eb", green: "#0f9d6b", orange: "#e8830c", violet: "#7c4dff", teal: "#0891b2", red: "#dc3545", grid: "#e6edf7", text: "#5a6b86" };
const chartRegistry = {};

function drawChart(canvasId, config) {
  const canvas = document.getElementById(canvasId);
  if (!canvas || typeof Chart === "undefined") return null;
  if (chartRegistry[canvasId]) chartRegistry[canvasId].destroy();
  Chart.defaults.font.family = '"Segoe UI", system-ui, sans-serif';
  Chart.defaults.font.size = 12;
  Chart.defaults.color = PALETTE.text;
  config.options = Object.assign({ responsive: true, maintainAspectRatio: false }, config.options || {});
  chartRegistry[canvasId] = new Chart(canvas, config);
  return chartRegistry[canvasId];
}

function axisOptions(extra) {
  return {
    x: { grid: { display: false }, ticks: { maxRotation: 0, autoSkip: true } },
    y: Object.assign({ beginAtZero: true, grid: { color: PALETTE.grid }, border: { display: false } }, extra || {})
  };
}

function ringHtml(percent, color, big, small) {
  const p = Math.max(0, Math.min(100, percent || 0));
  return '<div class="ring" style="--p:' + p + ";--c:" + color + '"><div><b>' + big + "</b><small>" + small + "</small></div></div>";
}

function meterHtml(label, value, goal, unit, tone) {
  const pct = goal > 0 ? Math.min(100, (value / goal) * 100) : 0;
  return '<div class="meter-row"><div class="meter-top"><span>' + esc(label) + "</span><span>" + num(value, 1) + " / " + num(goal, 0) + " " + unit +
    '</span></div><div class="progress ' + (tone || "") + '"><i style="width:' + pct + '%"></i></div></div>';
}
