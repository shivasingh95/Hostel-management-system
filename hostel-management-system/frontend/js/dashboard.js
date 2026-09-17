// ─── Auth guard & nav setup ──────────────────────────────────────
requireAuth();
setupNav();

const student = getStudent();

function logout() {
  clearSession();
  window.location.href = 'login.html';
}

// ─── Status Badge HTML ────────────────────────────────────────────
function statusBadge(status) {
  const map = {
    ACTIVE:  ['badge-success', 'Active'],
    VACATED: ['badge-neutral', 'Vacated'],
  };
  const [cls, label] = map[status] || ['badge-neutral', status];
  return `<span class="badge ${cls}">${label}</span>`;
}

// ─── Stats Row ────────────────────────────────────────────────────
function renderStats(active) {
  const roomEl   = document.getElementById('stat-room');
  const hostelEl = document.getElementById('stat-hostel');
  const sinceEl  = document.getElementById('stat-since');

  if (!active) {
    roomEl.innerHTML   = '<div class="stat-label">Room</div><div class="stat-value">—</div><div class="stat-sub">Not allocated</div>';
    hostelEl.innerHTML = '<div class="stat-label">Hostel</div><div class="stat-value">—</div>';
    sinceEl.innerHTML  = '<div class="stat-label">Since</div><div class="stat-value">—</div>';
    [roomEl, hostelEl, sinceEl].forEach(el => el.classList.remove('skeleton'));
    return;
  }

  roomEl.classList.remove('skeleton');
  roomEl.innerHTML = `
    <div class="stat-label">Your Room</div>
    <div class="stat-value">${active.roomNumber}</div>
    <div class="stat-sub">${statusBadge(active.status)}</div>
  `;

  hostelEl.classList.remove('skeleton');
  hostelEl.innerHTML = `
    <div class="stat-label">Hostel</div>
    <div class="stat-value" style="font-size:16px">${active.hostelName || '—'}</div>
    <div class="stat-sub">Your block</div>
  `;

  sinceEl.classList.remove('skeleton');
  sinceEl.innerHTML = `
    <div class="stat-label">Allocated Since</div>
    <div class="stat-value" style="font-size:16px">${formatDate(active.allocatedDate)}</div>
    <div class="stat-sub">${timeAgo(active.allocatedDate)}</div>
  `;
}

function formatDate(dateStr) {
  if (!dateStr) return '—';
  const d = new Date(dateStr);
  return d.toLocaleDateString('en-IN', { day: 'numeric', month: 'short', year: 'numeric' });
}

// ─── Load Allocation ───────────────────────────────────────────────
async function loadAllocation() {
  const el = document.getElementById('allocation-content');
  try {
    const allocations = await api.getAllocationsForStudent(student.id);
    const active = allocations.find(a => a.status === 'ACTIVE');

    renderStats(active);

    if (!active) {
      el.innerHTML = `
        <div class="empty-state">
          <div class="empty-icon">🛏</div>
          <p>No room allocated yet.<br>Once the hostel admin assigns you a room, it'll show up here.</p>
        </div>`;
      return;
    }

    el.innerHTML = `
      <div class="alloc-card">
        <div class="alloc-icon">🛏</div>
        <div>
          <div class="alloc-detail-label">Room Number</div>
          <div class="alloc-room-number">${active.roomNumber}</div>
          <div class="alloc-meta">Allocated on ${formatDate(active.allocatedDate)}</div>
          <div style="margin-top:10px">${statusBadge(active.status)}</div>
        </div>
      </div>
      <div style="margin-top:16px">
        <a href="complaints.html" class="quick-link">📝 Report an issue →</a>
      </div>
    `;
  } catch (err) {
    renderStats(null);
    el.innerHTML = `<div class="empty-state"><div class="empty-icon">⚠</div><p>Couldn't load allocation:<br>${err.message}</p></div>`;
  }
}

// ─── Load Available Rooms ──────────────────────────────────────────
async function loadAvailableRooms() {
  const el = document.getElementById('rooms-content');
  const countEl = document.getElementById('rooms-count');
  try {
    const rooms = await api.getAvailableRooms();
    countEl.textContent = `${rooms.length} room${rooms.length !== 1 ? 's' : ''} available`;

    if (rooms.length === 0) {
      el.innerHTML = `<div class="empty-state"><div class="empty-icon">🏢</div><p>No rooms currently available.</p></div>`;
      return;
    }

    el.innerHTML = `<div class="room-grid">${rooms.map(roomCardHtml).join('')}</div>`;
  } catch (err) {
    countEl.textContent = '';
    el.innerHTML = `<div class="empty-state"><div class="empty-icon">⚠</div><p>Couldn't load rooms:<br>${err.message}</p></div>`;
  }
}

function roomCardHtml(room) {
  const spotsLeft = room.capacity - room.occupiedCount;
  const pct = Math.round((room.occupiedCount / room.capacity) * 100);
  const fillClass = pct < 50 ? 'fill-low' : pct < 85 ? 'fill-mid' : 'fill-high';

  return `
    <div class="room-card">
      <div class="room-number">Room ${room.roomNumber}</div>
      <div class="room-meta">${room.hostelName}</div>
      <div class="room-meta">Floor ${room.floor}</div>
      <div class="room-bar-wrap">
        <div class="room-bar-label">
          <span>${spotsLeft} spot${spotsLeft !== 1 ? 's' : ''} open</span>
          <span>${room.occupiedCount}/${room.capacity}</span>
        </div>
        <div class="room-bar">
          <div class="room-bar-fill ${fillClass}" style="width:${pct}%"></div>
        </div>
      </div>
    </div>
  `;
}

// ─── Bootstrap ────────────────────────────────────────────────────
loadAllocation();
loadAvailableRooms();
