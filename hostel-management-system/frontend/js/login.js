// If already signed in, skip to dashboard.
if (getToken()) window.location.href = 'dashboard.html';

// ─── Error Display ───────────────────────────────────────────────
const errorBox = document.getElementById('error-box');
function showError(msg) { errorBox.textContent = msg; errorBox.classList.add('show'); }
function clearError()   { errorBox.classList.remove('show'); errorBox.textContent = ''; }

// ─── Tab Switcher ─────────────────────────────────────────────────
function showTab(tab) {
  clearError();
  const isLogin = tab === 'login';
  document.getElementById('tab-login').classList.toggle('active', isLogin);
  document.getElementById('tab-register').classList.toggle('active', !isLogin);
  document.getElementById('login-form').style.display = isLogin ? 'block' : 'none';
  document.getElementById('register-form').style.display = isLogin ? 'none' : 'block';

  const toggle = document.getElementById('auth-toggle');
  toggle.classList.toggle('on-register', !isLogin);
}

// ─── Password Toggle ──────────────────────────────────────────────
function togglePw(inputId, btn) {
  const input = document.getElementById(inputId);
  const isPassword = input.type === 'password';
  input.type = isPassword ? 'text' : 'password';
  btn.textContent = isPassword ? '🙈' : '👁';
}

// ─── Login ────────────────────────────────────────────────────────
document.getElementById('login-form').addEventListener('submit', async (e) => {
  e.preventDefault();
  clearError();
  const btn = document.getElementById('login-submit');
  setButtonLoading(btn, true);

  try {
    const result = await api.login({
      email:    document.getElementById('login-email').value.trim(),
      password: document.getElementById('login-password').value,
    });
    saveSession(result);
    window.location.href = 'dashboard.html';
  } catch (err) {
    showError(err.message);
    setButtonLoading(btn, false);
  }
});

// ─── Register ─────────────────────────────────────────────────────
document.getElementById('register-form').addEventListener('submit', async (e) => {
  e.preventDefault();
  clearError();
  const btn = document.getElementById('register-submit');
  setButtonLoading(btn, true);

  try {
    const result = await api.register({
      name:     document.getElementById('reg-name').value.trim(),
      email:    document.getElementById('reg-email').value.trim(),
      regNo:    document.getElementById('reg-regno').value.trim(),
      phone:    document.getElementById('reg-phone').value.trim(),
      password: document.getElementById('reg-password').value,
    });
    saveSession(result);
    window.location.href = 'dashboard.html';
  } catch (err) {
    showError(err.message);
    setButtonLoading(btn, false);
  }
});
