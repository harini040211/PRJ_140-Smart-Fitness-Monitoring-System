document.getElementById("hero-mark").innerHTML = icon("pulse");
document.getElementById("form-mark").innerHTML = icon("pulse");
document.getElementById("hero-points").innerHTML = [
  ["flame", "Calories calculated for you, never typed in"],
  ["target", "Goals, streaks and achievements that keep you going"],
  ["chart", "Clear charts of your workouts, meals and weight"]
].map((p) => '<div><span class="dot">' + icon(p[0]) + "</span>" + p[1] + "</div>").join("");
