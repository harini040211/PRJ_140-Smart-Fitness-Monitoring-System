# FitTrack - Smart Fitness Monitoring System

Spring Boot + MySQL web application. Register, set up a profile, log workouts and meals, and follow progress on a dashboard. Every calorie figure is calculated for you.

## What you need
- JDK 17 or newer (JDK 21 or 25 also works)
- MySQL Server 8 and MySQL Workbench
- IntelliJ IDEA (Maven is built in)

## 1. Database
1. Open MySQL Workbench and connect to your local server.
2. Run this once:
   ```sql
   DROP DATABASE IF EXISTS fitness_db;
   CREATE DATABASE fitness_db;
   ```
   The drop is important if you ran an older version of this project, because the tables changed.
3. The application creates all tables itself on first start.

## 2. Configure
Open `src/main/resources/application.properties` and set:
- `spring.datasource.password` to your MySQL root password
- `app.jwt.secret` to any long random text (32 characters or more)

The port is `9090`.

## 3. Run
1. IntelliJ: File > Open, choose the `fitness-monitoring-system` folder, wait for Maven to finish importing.
2. Open `FitnessMonitoringApplication` and press Run.
3. Wait for "Started FitnessMonitoringApplication".
4. Open http://localhost:9090 in Chrome.

Terminal alternative: `mvn spring-boot:run`

Run the tests with `mvn test`.

## 4. Try it
1. Create an account. You land on the profile page.
2. Fill in your details and save. BMI, BMR, TDEE and the daily calorie target appear.
3. Workouts: pick an exercise, press Start, run the timer, Finish & save. Calories burned are worked out from your weight.
4. Nutrition: pick a food, choose servings, add it. Calories and macros are calculated.
5. Dashboard: rings, charts, streaks, insights, water and steps.
6. My Plans: suggested diet day and weekly workout plan.
7. Progress: charts for any 7, 30 or 90 day window, and weight logging.
8. Weekly Report: Print / Save as PDF from the browser.

## Features
- Token (JWT) login. Every request is tied to the signed-in user, so one user cannot read or delete another user's data.
- Server-side food and exercise catalogs. The server calculates calories, the browser never sends them.
- BMI (WHO categories), BMR (Mifflin-St Jeor), TDEE, goal-based calorie target, macro targets.
- Workout calories = MET x weight (kg) x hours x intensity factor (Low 0.85, Medium 1.0, High 1.2).
- Timer with pause, rest, and automatic saving. Manual logging for past dates.
- Favorites, history filters and sorting, CSV export.
- Water, steps and active minutes tracking, weight history.
- Streaks, 12 achievements, rule-based insights from your own data.
- Diet plan (respects vegetarian, eggetarian, non-vegetarian) and weekly workout plan.
- Weekly report with print/PDF.
- Reminders using browser notifications (they fire only while the app is open in a tab).

## Known limits
- Tables use a plain `userId` column, not database foreign keys.
- The food database has 21 items and the exercise library 19. Add more in `src/main/resources/catalog/*.json`.
- Calories are estimates from standard formulas, not measurements.
- Reminders need an open browser tab.

## Troubleshooting
- "Public Key Retrieval is not allowed": the JDBC URL must contain `allowPublicKeyRetrieval=true` (already set).
- "Data truncated for column 'id'": old tables exist. Drop and recreate `fitness_db` (step 1).
- "Access denied for user root": wrong password in `application.properties`.
- Port 9090 busy: change `server.port`.
- Stuck on login after restarting: log in again, tokens last 24 hours.

## API summary
All routes except `/api/auth/register` and `/api/auth/login` need `Authorization: Bearer <token>`.

| Area | Routes |
|---|---|
| Auth | POST /api/auth/register, POST /api/auth/login, GET /api/auth/me |
| Profile | GET/POST /api/profile, PUT /api/profile/goals |
| Workouts | GET/POST /api/workouts, DELETE /api/workouts/{id} |
| Meals | GET/POST /api/meals, DELETE /api/meals/{id} |
| Water | GET /api/water/today, POST /api/water, DELETE /api/water/last |
| Weight | GET/POST /api/weight |
| Activity | GET/POST /api/activity |
| Favorites | GET /api/favorites, POST /api/favorites/{exercise\|food}/{id} |
| Catalog | GET /api/catalog/exercises, GET /api/catalog/foods |
| Insights | GET /api/dashboard, GET /api/analytics?days=30 |
| Plans | GET /api/recommendations/diet, GET /api/recommendations/workout |
| Reports | GET /api/reports/weekly?weeksAgo=0, GET /api/export/{workouts\|meals\|weight} |

Database tables: users, profiles, workouts, meals, weight_records, water_logs, activity_logs, favorites.
