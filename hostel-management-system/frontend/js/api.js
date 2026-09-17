// ─── Configuration ──────────────────────────────────────────────
// Change this if your backend runs on a different host/port.
const API_BASE = 'http://localhost:8080/api';

// ─── Session Helpers ────────────────────────────────────────────
function getToken() { return localStorage.getItem('hostel_token'); }

function getStudent() {
  const raw = localStorage.getItem('hostel_student');
  return raw ? JSON.parse(raw) : null;
}

function saveSession(authResponse) {
  localStorage.setItem('hostel_token', authResponse.token);
  localStorage.setItem('hostel_student', JSON.stringify({
    id:   authResponse.studentId,
    name: authResponse.name,
    role: authResponse.role,
  }));
}

function clearSession() {
  localStorage.removeItem('hostel_token');
  localStorage.removeItem('hostel_student');
}

function requireAuth() {
  if (!getToken()) window.location.href = 'login.html';
}

// ─── Core Request Helper ─────────────────────────────────────────
async function apiRequest(path, options = {}) {
  const headers = { 'Content-Type': 'application/json', ...(options.headers || {}) };
  const token = getToken();
  if (token) headers['Authorization'] = `Bearer ${token}`;

  let response;
  try {
    response = await fetch(`${API_BASE}${path}`, { ...options, headers });
  } catch {
    throw new Error('Could not reach the server. Is the backend running on localhost:8080?');
  }

  if (response.status === 401 || response.status === 403) {
    clearSession();
    window.location.href = 'login.html';
    return;
  }

  const isJson = response.headers.get('content-type')?.includes('application/json');
  const body = isJson ? await response.json() : null;

  if (!response.ok) {
    throw new Error(body?.message || `Request failed (${response.status})`);
  }
  return body;
}

// ─── API Methods ─────────────────────────────────────────────────
const api = {
  // Auth
  register:  (data) => apiRequest('/auth/register', { method: 'POST', body: JSON.stringify(data) }),
  login:     (data) => apiRequest('/auth/login',    { method: 'POST', body: JSON.stringify(data) }),

  // Rooms
  getAvailableRooms: ()       => apiRequest('/rooms/available'),
  getAllRooms:        ()       => apiRequest('/rooms'),
  getRoomById:       (id)     => apiRequest(`/rooms/${id}`),
  createRoom:        (data)   => apiRequest('/rooms', { method: 'POST', body: JSON.stringify(data) }),

  // Allocations
  getAllocationsForStudent: (studentId) => apiRequest(`/allocations/student/${studentId}`),
  allocateRoom:  (data) => apiRequest('/allocations', { method: 'POST', body: JSON.stringify(data) }),
  vacateRoom:    (id)   => apiRequest(`/allocations/${id}/vacate`, { method: 'PUT' }),

  // Complaints
  raiseComplaint:           (data) => apiRequest('/complaints', { method: 'POST', body: JSON.stringify(data) }),
  getComplaintsForStudent:  (studentId) => apiRequest(`/complaints/student/${studentId}`),
  getAllComplaintsForRoom:   (roomId)    => apiRequest(`/complaints/room/${roomId}`),
  updateComplaintStatus:    (id, data)  => apiRequest(`/complaints/${id}/status`, { method: 'PUT', body: JSON.stringify(data) }),
};

// ─── Toast Notification System ───────────────────────────────────
function showToast(message, type = 'info', duration = 4000) {
  const container = document.getElementById('toast-container');
  if (!container) return;

  const toast = document.createElement('div');
  toast.className = `toast ${type}`;
  toast.innerHTML = `<span class="toast-icon"></span><span>${message}</span>`;
  container.appendChild(toast);

  setTimeout(() => {
    toast.classList.add('out');
    toast.addEventListener('animationend', () => toast.remove());
  }, duration);
}

// ─── Button Loading State ─────────────────────────────────────────
function setButtonLoading(btn, loading) {
  btn.disabled = loading;
  btn.classList.toggle('loading', loading);
}

// ─── Relative Time ────────────────────────────────────────────────
function timeAgo(dateStr) {
  if (!dateStr) return '';
  const date = new Date(dateStr);
  if (isNaN(date)) return dateStr;
  const diff = Date.now() - date.getTime();
  const mins  = Math.floor(diff / 60000);
  const hours = Math.floor(diff / 3600000);
  const days  = Math.floor(diff / 86400000);
  if (mins < 1)   return 'just now';
  if (mins < 60)  return `${mins}m ago`;
  if (hours < 24) return `${hours}h ago`;
  if (days < 30)  return `${days}d ago`;
  return date.toLocaleDateString('en-IN', { day: 'numeric', month: 'short', year: 'numeric' });
}

// ─── Shared Nav Setup ─────────────────────────────────────────────
function setupNav() {
  const student = getStudent();
  if (!student) return;

  // Student badge
  const nameEl = document.getElementById('student-name-label');
  if (nameEl) nameEl.textContent = student.name || '';

  const avatarEl = document.getElementById('student-avatar');
  if (avatarEl) avatarEl.textContent = (student.name || '?')[0].toUpperCase();

  // Show admin link if ADMIN role
  const adminLink = document.getElementById('admin-nav-link');
  if (adminLink && student.role === 'ADMIN') adminLink.style.display = 'inline';
}
