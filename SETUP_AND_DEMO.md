# Fitness Monitoring System — Review-2 Setup & Demo Guide (MySQL Edition, v3)

## ⚠️ Important note on scope

This project was upgraded from a basic forms-and-tables prototype into a much
richer, modern fitness dashboard. It is still an honest **~50% complete**
project — many advanced items (AI coaching, wearable sync, PDF export,
notifications, social features, etc.) are intentionally left as clearly
marked **"Coming Soon"** cards rather than faked. See the summary at the
bottom of this file for the full breakdown.

---

## 1. Prerequisites (Windows)

| Tool | Check command | Install from |
|---|---|---|
| JDK 17+ | `java -version` | https://adoptium.net |
| MySQL Server + Workbench | open MySQL Workbench | https://dev.mysql.com/downloads/installer/ |
| IntelliJ IDEA | — | already installed |

Maven is bundled with IntelliJ, so a separate Maven install isn't required.

---

## 2. Create the Database in MySQL Workbench

1. Open **MySQL Workbench** → connect to your local instance.
2. Click the "Create a new schema" icon → name it `fitness_db` → Apply → Apply → Finish.
3. **If you previously had a broken `fitness_db` from an earlier run**, right-click it → **Drop Schema** → recreate it empty as above. This avoids leftover column-type conflicts.

You do NOT write any `CREATE TABLE` statements — Hibernate creates/updates all tables automatically on startup.

---

## 3. Configure the Connection

Open `src/main/resources/application.properties` and confirm:

```properties
server.port=9090
spring.datasource.url=jdbc:mysql://localhost:3306/fitness_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=YOUR_MYSQL_PASSWORD
spring.jpa.hibernate.ddl-auto=update
```

Replace `YOUR_MYSQL_PASSWORD` with your actual MySQL root password.

---

## 4. Run the Project

1. Open the project folder in IntelliJ (`File → Open`).
2. Wait for Maven auto-import (bottom-right notification, or the 🔄 refresh icon in the Maven tool window).
3. Open `FitnessMonitoringApplication.java` → click ▶ Run.
4. Console should end with:
   ```
   Started FitnessMonitoringApplication in X.XXX seconds
   ```

## 5. Open the App

```
http://localhost:9090
```

Register a new account, then log in.

---

## 6. What to Test

1. **Register / Login**
2. **Profile** — enter age, gender, height, weight, activity level, fitness goal, target weight → see BMI, BMR, TDEE, and daily calorie target calculated automatically. Also try "Log Today's Weight."
3. **Workouts → Library tab** — search/filter exercises, click "Start Workout" on any card, run the **real countdown timer** (Start/Pause/Resume/Finish Early), and watch it auto-log the workout with calories calculated from your weight + MET value + duration + intensity.
4. **Workouts → History tab** — see weekly totals, the logged entry, and try Delete.
5. **Diet → Log a Meal tab** — search/filter foods, click a food card, adjust servings, watch calories/macros update live, then log it.
6. **Diet → History tab** — see the entry with full macros, try Delete.
7. **Dashboard** — check: summary cards, calorie balance strip, progress rings (calories/water/exercise), macro bars, weekly workout goal bar, the 7-day calories-burned chart, the weight progress line chart, achievements, and the Coming Soon section.

---

## 7. Verify Data in MySQL Workbench

```sql
USE fitness_db;
SELECT * FROM users;
SELECT * FROM profiles;
SELECT * FROM workouts;
SELECT * FROM meals;
SELECT * FROM weight_records;
SELECT * FROM water_logs;
```

---

## 8. Common Errors & Fixes

| Error | Fix |
|---|---|
| `Public Key Retrieval is not allowed` | Ensure `allowPublicKeyRetrieval=true` is in the datasource URL |
| `Data truncated for column 'id'` | Drop and recreate the `fitness_db` schema in Workbench, then restart the app |
| `Access denied for user 'root'` | Fix the password in `application.properties` |
| Port 9090 in use | Change `server.port` to another value, e.g. 9091 |
| Charts not showing | Check your internet connection — Chart.js loads from a CDN |
| Blank page | Always use `http://localhost:9090/...`, never open the HTML file directly |

---

## 9. Test Credentials

There are no pre-seeded accounts — register a fresh one the first time you run it (e.g. `test@example.com` / `test123`).

---

## 10. FINAL SUMMARY

### ✅ 1. Features Implemented (fully working, real data)
- Registration & login (BCrypt password hashing)
- Profile with **automatic** BMI, BMR (Mifflin-St Jeor), TDEE, and calorie target (no manual calorie entry anywhere)
- Modern sidebar/bottom-nav responsive layout (desktop sidebar, mobile bottom nav)
- **Workout Library** with 19 exercises, search + muscle/difficulty filters, exercise cards
- **Real working workout timer** (Start/Pause/Resume/Finish) that auto-logs duration and lets the backend compute calories via MET formula, including intensity (Low/Medium/High)
- Workout history with weekly totals and delete
- **Food database** (21 foods across 8 categories) with portion-based automatic calorie/macro calculation (no manual calorie typing)
- Diet history with full macro breakdown and delete
- Dashboard: calorie balance strip, 3 progress rings (calories/water/exercise), macro bars vs auto-calculated targets, weekly workout goal bar, 7-day calories-burned chart (Chart.js), weight progress line chart (Chart.js)
- Water tracker (+250ml button, glass icons, daily total vs goal)
- Weight tracking (manual weight entry → automatic history/chart/progress %)
- Achievement badges computed from real stored counts (first workout, 5 workouts, 1,000 kcal burned, first meal, profile completed)
- Empty states for workout/food search and history tables
- MySQL persistence via Spring Data JPA/Hibernate across 6 tables

### 🟡 2. Partially Implemented
- PDF report: not built yet — the data needed for it (workouts, meals, weight, water) is fully available in the database, so it's a "generate document from existing data" task, not a redesign
- Notifications/reminders: no in-app reminder scheduling yet; shown only as a Coming Soon card
- Search/filtering: implemented for workouts and foods; not yet extended to history tables

### 🔵 3. Left for the Remaining ~50% (marked "Coming Soon" in the UI)
AI fitness coach, AI-generated workout/meal plans, wearable/Google Fit/Apple Health/smartwatch sync, heart-rate & sleep tracking, body measurements, workout video tutorials, social/community features, trainer accounts, camera-based form detection, voice assistant, push notifications, streak system, favorites, Excel export, cloud sync, PDF export.

### 4. Files Changed / Added
- **New entities:** `WeightRecord`, `WaterLog`; extended `Profile` (activity level, goal, target weight, BMR/TDEE/target) and `Meal` (protein/carbs/fat/servings)
- **New repositories:** `WeightRecordRepository`, `WaterLogRepository`
- **New services:** `WeightService`, `WaterService`; rewrote `ProfileService` (BMR/TDEE math) and `DashboardService` (full aggregation)
- **New controllers:** `WeightController`, `WaterController`
- **New frontend:** `js/layout.js` (sidebar/nav), `js/exercises.js` (MET database), `js/foods.js` (nutrition database); fully rewritten `dashboard.html`, `profile.html`, `workout.html`, `diet.html`; extended `style.css`

### 5. Database / API Changes
- New tables: `weight_records`, `water_logs`
- Extended tables: `profiles` (+6 columns), `meals` (+4 columns)
- New endpoints: `POST/GET /api/weight`, `POST /api/water`, `GET /api/water/{userId}/today`
- Extended endpoints: `/api/profile` (new fields), `/api/meals` (new fields), `/api/dashboard/{userId}` (much larger response payload)

### 6. How to Run
See sections 1–5 above: create the `fitness_db` schema in Workbench → set your password in `application.properties` → run `FitnessMonitoringApplication` in IntelliJ → open `http://localhost:9090`.

### 7. Test Credentials
None pre-seeded — register your own test account on first run.
