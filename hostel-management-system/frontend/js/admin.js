// ─── Auth guard (admin only) ─────────────────────────────────────
requireAuth();
const student = getStudent();

if (student?.role !== 'ADMIN') {
  showToast('Admin access required.', 'error');
  setTimeout(() => { window.location.href = 'dashboard.html'; }, 1500);
}

setupNav();

function logout() { clearSession(); window.location.href = 'login.html'; }

// ─── Tab Switcher ─────────────────────────────────────────────────
const TABS = ['rooms', 'allocations', 'complaints', 'allocate'];
let activeTab = 'rooms';

function switchTab(tab) {
  TABS.forEach(t => {
    document.getElementById(`tab-${t}`).classList.toggle('active', t === tab);
    document.getElementById(`panel-${t}`).classList.toggle('active', t === tab);
  });
  activeTab = tab;
  if (tab === 'rooms') loadAllRooms();
}

// ─── Helpers ─────────────────────────────────────────────────────
const STATUS_BADGE = {
  ACTIVE:      '<span class="badge badge-success">Active</span>',
  VACATED:     '<span class="badge badge-neutral">Vacated</span>',
  OPEN:        '<span class="badge badge-warning">Open</span>',
  IN_PROGRESS: '<span class="badge badge-info">In Progress</span>',
  RESOLVED:    '<span class="badge badge-success">Resolved</span>',
};

function badge(status) {
  return STATUS_BADGE[status] || `<span class="badge badge-neutral">${status}</span>`;
}

function fmtDate(d) {
  if (!d) return '—';
  return new Date(d).toLocaleDateString('en-IN', { day:'numeric', month:'short', year:'numeric' });
}

// ─── Rooms Tab ────────────────────────────────────────────────────
async function loadAllRooms() {
  const wrap = document.getElementById('rooms-table-wrap');
  const countEl = document.getElementById('rooms-count');
  wrap.innerHTML = '<div class="skeleton skeleton-box" style="height:140px"></div>';

  try {
    const rooms = await api.getAllRooms();
    countEl.textContent = `${rooms.length} room${rooms.length !== 1 ? 's' : ''}`;

    if (rooms.length === 0) {
      wrap.innerHTML = '<div class="empty-state"><div class="empty-icon">🏢</div><p>No rooms found.</p></div>';
      return;
    }

    wrap.innerHTML = `
      <div style="overflow-x:auto">
        <table class="data-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Room #</th>
              <th>Hostel</th>
              <th>Floor</th>
              <th>Capacity</th>
              <th>Occupied</th>
              <th>Status</th>
            </tr>
          </thead>
          <tbody>
            ${rooms.map(r => `
              <tr>
                <td style="color:var(--text-muted)">#${r.id}</td>
                <td style="font-weight:600;color:var(--text-primary)">Room ${r.roomNumber}</td>
                <td>${r.hostelName}</td>
                <td>Floor ${r.floor}</td>
                <td>${r.capacity}</td>
                <td>${r.occupiedCount}</td>
                <td>${r.full ? '<span class="badge badge-danger">Full</span>' : '<span class="badge badge-success">Available</span>'}</td>
              </tr>
            `).join('')}
          </tbody>
        </table>
      </div>
    `;
  } catch (err) {
    wrap.innerHTML = `<div class="empty-state"><div class="empty-icon">⚠</div><p>${err.message}</p></div>`;
  }
}

// ─── Allocations Tab ──────────────────────────────────────────────
async function loadStudentAllocations() {
  const studentId = document.getElementById('alloc-student-id').value.trim();
  if (!studentId) { showToast('Enter a student ID first.', 'info'); return; }

  const wrap = document.getElementById('allocations-table-wrap');
  wrap.innerHTML = '<div class="skeleton skeleton-box" style="height:100px"></div>';

  try {
    const allocs = await api.getAllocationsForStudent(studentId);

    if (allocs.length === 0) {
      wrap.innerHTML = '<div class="empty-state"><div class="empty-icon">🛏</div><p>No allocations found for this student.</p></div>';
      return;
    }

    wrap.innerHTML = `
      <div style="overflow-x:auto">
        <table class="data-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Student</th>
              <th>Room</th>
              <th>Allocated</th>
              <th>Vacated</th>
              <th>Status</th>
              <th>Action</th>
            </tr>
          </thead>
          <tbody>
            ${allocs.map(a => `
              <tr>
                <td style="color:var(--text-muted)">#${a.id}</td>
                <td>${a.studentName}</td>
                <td style="font-weight:600;color:var(--text-primary)">Room ${a.roomNumber}</td>
                <td>${fmtDate(a.allocatedDate)}</td>
                <td>${fmtDate(a.vacatedDate)}</td>
                <td>${badge(a.status)}</td>
                <td>
                  ${a.status === 'ACTIVE'
                    ? `<button class="action-btn danger" onclick="vacate(${a.id})">Vacate</button>`
                    : '—'}
                </td>
              </tr>
            `).join('')}
          </tbody>
        </table>
      </div>
    `;
  } catch (err) {
    wrap.innerHTML = `<div class="empty-state"><div class="empty-icon">⚠</div><p>${err.message}</p></div>`;
  }
}

async function vacate(allocationId) {
  if (!confirm('Mark this allocation as vacated?')) return;
  try {
    await api.vacateRoom(allocationId);
    showToast('Room vacated successfully.', 'success');
    loadStudentAllocations();
  } catch (err) {
    showToast(err.message, 'error');
  }
}

// ─── Complaints Tab ───────────────────────────────────────────────
async function loadRoomComplaints() {
  const roomId = document.getElementById('complaint-room-id').value.trim();
  if (!roomId) { showToast('Enter a room ID first.', 'info'); return; }

  const wrap = document.getElementById('complaints-table-wrap');
  wrap.innerHTML = '<div class="skeleton skeleton-box" style="height:100px"></div>';

  try {
    const complaints = await api.getAllComplaintsForRoom(roomId);

    if (complaints.length === 0) {
      wrap.innerHTML = '<div class="empty-state"><div class="empty-icon">📋</div><p>No complaints for this room.</p></div>';
      return;
    }

    wrap.innerHTML = `
      <div style="overflow-x:auto">
        <table class="data-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Category</th>
              <th>Description</th>
              <th>Raised</th>
              <th>Status</th>
              <th>Update</th>
            </tr>
          </thead>
          <tbody>
            ${complaints.map(c => `
              <tr>
                <td style="color:var(--text-muted)">#${c.id}</td>
                <td style="font-weight:600">${c.category}</td>
                <td style="max-width:220px;white-space:nowrap;overflow:hidden;text-overflow:ellipsis">${c.description}</td>
                <td>${fmtDate(c.raisedDate)}</td>
                <td>${badge(c.status)}</td>
                <td>
                  <select id="status-sel-${c.id}" style="background:rgba(255,255,255,0.05);border:1px solid var(--border);border-radius:var(--radius-sm);color:var(--text-primary);font-family:inherit;font-size:12px;padding:4px 8px">
                    <option value="OPEN"        ${c.status==='OPEN'?'selected':''}>Open</option>
                    <option value="IN_PROGRESS" ${c.status==='IN_PROGRESS'?'selected':''}>In Progress</option>
                    <option value="RESOLVED"    ${c.status==='RESOLVED'?'selected':''}>Resolved</option>
                  </select>
                  <button class="action-btn" style="margin-left:6px" onclick="updateStatus(${c.id})">Save</button>
                </td>
              </tr>
            `).join('')}
          </tbody>
        </table>
      </div>
    `;
  } catch (err) {
    wrap.innerHTML = `<div class="empty-state"><div class="empty-icon">⚠</div><p>${err.message}</p></div>`;
  }
}

async function updateStatus(complaintId) {
  const status = document.getElementById(`status-sel-${complaintId}`).value;
  try {
    await api.updateComplaintStatus(complaintId, { status });
    showToast('Complaint status updated.', 'success');
    loadRoomComplaints();
  } catch (err) {
    showToast(err.message, 'error');
  }
}

// ─── New Allocation Form ──────────────────────────────────────────
const allocErrBox = document.getElementById('allocate-error');
function showAllocErr(msg) { allocErrBox.textContent = msg; allocErrBox.classList.add('show'); }
function clearAllocErr()   { allocErrBox.classList.remove('show'); }

document.getElementById('allocate-form').addEventListener('submit', async (e) => {
  e.preventDefault();
  clearAllocErr();
  const btn = document.getElementById('allocate-btn');
  setButtonLoading(btn, true);

  try {
    const result = await api.allocateRoom({
      studentId: Number(document.getElementById('a-student-id').value),
      roomId:    Number(document.getElementById('a-room-id').value),
    });
    showToast(`Room ${result.roomNumber} allocated to student ${result.studentName}!`, 'success');
    document.getElementById('allocate-form').reset();
  } catch (err) {
    showAllocErr(err.message);
  } finally {
    setButtonLoading(btn, false);
  }
});

// ─── Bootstrap ────────────────────────────────────────────────────
loadAllRooms();
