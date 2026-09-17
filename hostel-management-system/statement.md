# Problem Statement

Hostels in most colleges still allocate rooms manually — through
spreadsheets or paper registers maintained by the warden. This leads to
several recurring problems:

- Rooms get double-allocated because there's no real-time visibility
  into which rooms are occupied and by how many students.
- Students have no way to track their own room assignment or raise
  maintenance issues without physically visiting the warden's office.
- There is no record-keeping for complaints, so recurring maintenance
  issues (e.g., a room with a persistent electrical fault) go
  unnoticed.
- Occupancy reporting — how full each hostel block is — has to be
  compiled manually, which is slow and error-prone.

## Scope of the Project

The project covers:
- Student self-registration and secure login
- Admin-managed room allocation with automatic capacity enforcement
- Student-raised complaints with admin-managed status tracking
- Occupancy and complaint analytics for admins

It does **not** cover (explicitly out of scope for this version):
- Online fee/payment processing
- Room-swap requests between students
- Multi-hostel-campus support beyond the basic `Hostel` entity

## Target Users

- **Students** — register, view available rooms (once allocated by
  admin), view their own allocation, raise and track complaints for
  their room.
- **Admin / Warden** — manage hostels and rooms, allocate/vacate rooms
  for students, update complaint status, view occupancy and complaint
  analytics.

## High-Level Features

1. **Authentication** — JWT-based login/registration, role-separated
   access (Student vs Admin).
2. **Room Allocation & Availability** — admins allocate rooms to
   students; the system prevents over-allocation beyond room capacity
   and prevents a student from holding two active allocations.
3. **Complaint & Maintenance Tracking** — students raise complaints
   categorized by type (electrical, plumbing, cleaning, furniture,
   other); admins move complaints through OPEN → IN_PROGRESS →
   RESOLVED, and the student is notified asynchronously on status
   change.
4. **Reporting** — admin-only endpoints returning occupancy percentage
   and complaint breakdowns by status/category, to support
   data-driven decisions on hostel maintenance.
