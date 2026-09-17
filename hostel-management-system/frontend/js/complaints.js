// ─── Auth guard & nav setup ──────────────────────────────────────
requireAuth();
setupNav();

const student = getStudent();
let currentRoomId = null;

function logout() {
  clearSession();
  window.location.href = 'login.html';
}

// ─── Error display helpers ────────────────────────────────────────
const errorBox = document.getElementById('error-box');
function showError(msg) { errorBox.textContent = msg; errorBox.classList.add('show'); }
function clearError()   { errorBox.classList.remove('show'); errorBox.textContent = ''; }

// ─── Status Badge & Category Helpers ─────────────────────────────
const STATUS_CONFIG = {
  OPEN:        { cls: 'badge-warning',  label: 'Open',        line: 'open'     },
  IN_PROGRESS: { cls: 'badge-info',     label: 'In Progress', line: 'progress' },
  RESOLVED:    { cls: 'badge-success',  label: 'Resolved',    line: 'resolved' },
};

function statusBadge(status) {
  const cfg = STATUS_CONFIG[status] || { cls: 'badge-neutral', label: status };
  return `<span class="badge ${cfg.cls}">${cfg.label}</span>`;
}

function statusLineClass(status) {
  return (STATUS_CONFIG[status] || {}).line || 'open';
}

const CATEGORY_ICONS = {
  ELECTRICAL: '⚡',
  PLUMBING:   '🚿',
  CLEANING:   '🧹',
  FURNITURE:  '🪑',
  OTHER:      '📋',
};

function categoryLabel(cat) {
  return `${CATEGORY_ICONS[cat] || '📋'} ${cat.charAt(0) + cat.slice(1).toLowerCase()}`;
}

// ─── Setup Form (check if student has a room) ─────────────────────
async function setupForm() {
  try {
    const allocations = await api.getAllocationsForStudent(student.id);
    const active = allocations.find(a => a.status === 'ACTIVE');

    if (!active) {
      document.getElementById('no-room-msg').style.display = 'flex';
      return;
    }

    // Use roomId directly from AllocationResponse (backend fix applied)
    currentRoomId = active.roomId;
    document.getElementById('complaint-form').style.display = 'block';
  } catch (err) {
    showError('Could not check your room allocation: ' + err.message);
  }
}

// ─── Complaint Form Submit ────────────────────────────────────────
document.getElementById('complaint-form').addEventListener('submit', async (e) => {
  e.preventDefault();
  clearError();

  if (!currentRoomId) {
    showError('Could not determine your room. Try refreshing the page.');
    return;
  }

  const submitBtn = document.getElementById('submit-btn');
  setButtonLoading(submitBtn, true);

  try {
    await api.raiseComplaint({
      studentId:   student.id,
      roomId:      currentRoomId,
      category:    document.getElementById('category').value,
      description: document.getElementById('description').value.trim(),
    });

    document.getElementById('description').value = '';
    showToast('Complaint submitted successfully!', 'success');
    await loadComplaints();
  } catch (err) {
    showError(err.message);
  } finally {
    setButtonLoading(submitBtn, false);
  }
});

// ─── Load Complaints ──────────────────────────────────────────────
async function loadComplaints() {
  const el = document.getElementById('complaints-list');
  const countEl = document.getElementById('complaints-count');
  try {
    const complaints = await api.getComplaintsForStudent(student.id);

    countEl.textContent = complaints.length ? `${complaints.length} total` : '';

    if (complaints.length === 0) {
      el.innerHTML = `
        <div class="empty-state">
          <div class="empty-icon">📋</div>
          <p>No complaints raised yet.<br>Use the form above to report any issue.</p>
        </div>`;
      return;
    }

    const sorted = [...complaints].reverse();
    el.innerHTML = `<div class="complaint-timeline">${sorted.map(complaintHtml).join('')}</div>`;
  } catch (err) {
    el.innerHTML = `<div class="empty-state"><div class="empty-icon">⚠</div><p>Couldn't load complaints:<br>${err.message}</p></div>`;
  }
}

function complaintHtml(c) {
  const lineClass = statusLineClass(c.status);
  const resolved = c.resolvedDate
    ? `<span>· Resolved ${timeAgo(c.resolvedDate)}</span>`
    : '';
  return `
    <div class="complaint-item">
      <div class="complaint-line ${lineClass}"></div>
      <div class="complaint-body">
        <div class="complaint-top">
          <span class="complaint-category">${categoryLabel(c.category)} · Room ${c.roomNumber}</span>
          ${statusBadge(c.status)}
        </div>
        <div class="complaint-desc">${c.description}</div>
        <div class="complaint-footer">
          <span>Raised ${timeAgo(c.raisedDate)}</span>
          ${resolved}
        </div>
      </div>
    </div>
  `;
}

// ─── Bootstrap ────────────────────────────────────────────────────
setupForm();
loadComplaints();
