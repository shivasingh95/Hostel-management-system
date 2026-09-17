# Hostel Room Booking & Management System

A full-stack application that digitizes hostel room allocation, student registration,
and complaint/maintenance tracking. Built for the CSE2006 — Programming in Java course
project.

The project has two parts:
- **`backend/`** — a Spring Boot REST API (Java, Spring Data JPA, MySQL, Spring Security + JWT)
- **`frontend/`** — a plain HTML/CSS/JavaScript student portal that talks to the API

## Features

- Student registration and JWT-based login
- Room allocation with automatic capacity enforcement (admin)
- Complaint raising and status tracking (student + admin), with an async notification
  on status change
- Occupancy and complaint analytics endpoints (admin)
- Centralized, consistent JSON error handling across the whole API
- Role-based authorization (student vs admin)
- **Admin panel** (web UI) — manage rooms, allocations, and complaint statuses directly
  in the browser without needing Postman

## Tech stack

| Layer | Technology |
|---|---|
| Backend | Java 17, Spring Boot 3, Spring Data JPA (Hibernate) |
| Database | MySQL 8 |
| Security | Spring Security, JWT (jjwt) |
| Build | Maven |
| Testing | JUnit 5, Mockito |
| Frontend | HTML5, CSS3, vanilla JavaScript (no framework) |

## Prerequisites

Before you start, make sure you have installed:
- **Java 17 or later** — check with `java -version`
- **Maven 3.8+** — check with `mvn -version`
- **MySQL 8+**, running locally, with a known root (or dedicated) user password
- A modern web browser (for the frontend)
- (Optional) **Python 3**, to serve the frontend locally, or any static file server

## 1. Set up the database

Open a MySQL client and create the database (or skip this — the backend can
auto-create it, see step 2):

```sql
CREATE DATABASE hostel_db;
```

## 2. Configure and run the backend

```bash
cd backend
```

Open `src/main/resources/application.properties` and set your MySQL credentials:

```properties
spring.datasource.username=root
spring.datasource.password=YOUR_MYSQL_PASSWORD
```

Also set a real JWT secret (any long random string, 32+ characters) in the same file:

```properties
jwt.secret=CHANGE_THIS_TO_A_LONG_RANDOM_SECRET_AT_LEAST_32_CHARS
```

Build and run:

```bash
mvn clean install
mvn spring-boot:run
```

The API starts on **http://localhost:8080**. You should see `Tomcat started on
port(s): 8080` in the console with no errors above it. Leave this terminal running.

### Run the tests

```bash
mvn test
```

This runs the unit tests in `AllocationServiceTest`, covering the core room-allocation
business rules (successful allocation, room-at-capacity rejection, duplicate-allocation
rejection).

## 3. Run the frontend

In a **new** terminal (leave the backend running):

```bash
cd frontend
python -m http.server 5500
```

Then open **http://localhost:5500** in your browser. (Any static file server works —
VS Code's "Live Server" extension is another option. Opening `index.html` directly by
double-clicking also works in most browsers, but a local server is more reliable.)

## 4. Try it out

1. On the login page, switch to the **Register** tab and create a student account.
2. You'll be signed in automatically and land on the dashboard.
3. To test the full flow (allocating a room, raising and resolving a complaint), you'll
   need an **admin** account and a **room** to allocate — see below.

### Creating an admin account

Registration always creates a `STUDENT` role. To test admin-only actions, promote a
registered user directly in MySQL:

```sql
UPDATE students SET role = 'ADMIN' WHERE email = 'your_test_email@example.com';
```

Log in again to get a fresh token that reflects the new role. The **Admin** link will
appear automatically in the navigation bar for admin users.

### Using the Admin Panel (web UI)

Once signed in as an admin, click **Admin** in the top navigation bar to open the
admin panel. It has four tabs:

| Tab | What you can do |
|---|---|
| 🏢 Rooms | View all rooms with occupancy status |
| 🛏 Allocations | Look up a student's allocations by ID; vacate a room |
| 📋 Complaints | View complaints by room ID; update status (Open → In Progress → Resolved) |
| ➕ New Allocation | Allocate a room to a student by entering their IDs |

### Using Postman / curl (alternative)

You can also call admin-only endpoints directly:

```
POST http://localhost:8080/api/hostels                    (create a hostel)
POST http://localhost:8080/api/rooms                      (create a room)
POST http://localhost:8080/api/allocations                (allocate a room to a student)
PUT  http://localhost:8080/api/complaints/{id}/status     (update a complaint's status)
GET  http://localhost:8080/api/reports/occupancy
GET  http://localhost:8080/api/reports/complaints
```

Every request except `/api/auth/**` needs an `Authorization: Bearer <token>` header,
where `<token>` is the value returned by `/api/auth/login`.

## API overview

| Method | Endpoint | Access |
|---|---|---|
| POST | `/api/auth/register` | Public |
| POST | `/api/auth/login` | Public |
| GET | `/api/hostels` | Authenticated |
| POST | `/api/hostels` | Admin |
| GET | `/api/rooms` | Authenticated |
| GET | `/api/rooms/available` | Authenticated |
| GET | `/api/rooms/{id}` | Authenticated |
| POST | `/api/rooms` | Admin |
| POST | `/api/allocations` | Admin |
| PUT | `/api/allocations/{id}/vacate` | Admin |
| GET | `/api/allocations/student/{studentId}` | Authenticated |
| POST | `/api/complaints` | Authenticated |
| PUT | `/api/complaints/{id}/status` | Admin |
| GET | `/api/complaints/room/{roomId}` | Authenticated |
| GET | `/api/complaints/student/{studentId}` | Authenticated |
| GET | `/api/reports/occupancy` | Admin |
| GET | `/api/reports/complaints` | Admin |

## Project structure

```
hostel-management-system/
├── backend/
│   ├── src/main/java/com/hostel/management/
│   │   ├── entity/        # JPA entities
│   │   ├── repository/    # Spring Data JPA repositories
│   │   ├── service/       # Business logic
│   │   ├── controller/    # REST endpoints
│   │   ├── dto/           # Request/response objects
│   │   ├── exception/     # Custom exceptions + global handler
│   │   ├── security/      # JWT filter, UserDetails, JwtUtil
│   │   └── config/        # Security + async configuration
│   ├── src/test/java/...  # Unit tests
│   └── pom.xml
├── frontend/
│   ├── index.html         # Redirect entry point
│   ├── login.html         # Sign-in / register page
│   ├── dashboard.html     # Room allocation & availability
│   ├── complaints.html    # Raise & track complaints
│   ├── admin.html         # Admin panel (admin role only)
│   ├── css/style.css
│   └── js/
│       ├── api.js         # API client + toast/session helpers
│       ├── login.js
│       ├── dashboard.js
│       ├── complaints.js
│       └── admin.js
└── README.md
```

## Troubleshooting

- **`Access denied for user 'root'@'localhost'`** — the password in
  `application.properties` doesn't match your MySQL password. Verify it by running
  `mysql -u root -p` directly.
- **`Public Key Retrieval is not allowed`** — already handled in this project's
  connection URL (`allowPublicKeyRetrieval=true`); if you changed the URL, add that
  parameter back.
- **Frontend requests fail / CORS error in browser console** — make sure the backend
  is running and that you're serving the frontend from a local server rather than
  opening the HTML file with a `file://` URL in some browsers.
- **`mvn` not found / wrong Java version** — confirm `java -version` reports 17 or
  higher and that Maven is on your `PATH`.
- **Admin link not visible** — make sure you've updated the role in MySQL and
  logged in again; the nav link appears only for `ADMIN` role tokens.

## License

Academic project — submitted for course evaluation.
