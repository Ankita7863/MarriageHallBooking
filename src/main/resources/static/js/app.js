'use strict';

const API = '/api/v1/hall-bookings';

let rows = [];            // rows currently displayed
let pendingDeleteId = null;

const el = id => document.getElementById(id);
const bookingModal = new bootstrap.Modal(el('bookingModal'));
const deleteModal = new bootstrap.Modal(el('deleteModal'));

/* ---------- helpers ---------- */

function escapeHtml(value) {
  return String(value ?? '').replace(/[&<>"']/g, c =>
    ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' }[c]));
}

function isoDate(d) {
  const m = String(d.getMonth() + 1).padStart(2, '0');
  const day = String(d.getDate()).padStart(2, '0');
  return `${d.getFullYear()}-${m}-${day}`;
}

function tomorrowIso() {
  const d = new Date();
  d.setDate(d.getDate() + 1);
  return isoDate(d);
}

function formatMoney(n) {
  return Number(n).toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
}

function showAlert(message, type = 'success', target = 'alertBox') {
  const box = el(target);
  box.innerHTML = `<div class="alert alert-${type} alert-dismissible fade show" role="alert">
      ${escapeHtml(message)}
      <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    </div>`;
  if (type === 'success') {
    setTimeout(() => { if (box.firstElementChild) box.innerHTML = ''; }, 4000);
  }
}

/** fetch wrapper: handles 204 (no body) and turns JSON error bodies into Error objects. */
async function request(url, options = {}) {
  const res = await fetch(url, options);
  if (res.status === 204) return null;

  let body = null;
  try { body = await res.json(); } catch (e) { /* empty or non-JSON body */ }

  if (!res.ok) {
    let message = (body && body.message) || `Request failed (${res.status})`;
    if (body && body.errors) {
      message += ': ' + Object.values(body.errors).join('; ');
    }
    throw new Error(message);
  }
  return body;
}

/* ---------- rendering ---------- */

function badge(status) {
  const s = String(status).toUpperCase();
  const cls = { CONFIRMED: 'badge-confirmed', PENDING: 'badge-pending', CANCELLED: 'badge-cancelled' }[s] || 'badge-pending';
  const label = s.charAt(0) + s.slice(1).toLowerCase();
  return `<span class="badge badge-status ${cls}">${escapeHtml(label)}</span>`;
}

function render() {
  const body = el('bookingBody');
  body.innerHTML = rows.map(b => `
    <tr>
      <td>${escapeHtml(b.bookingId)}</td>
      <td>${escapeHtml(b.customerName)}</td>
      <td>${escapeHtml(b.hallName)}</td>
      <td>${escapeHtml(b.eventDate)}</td>
      <td class="text-end">${escapeHtml(b.hallCapacity)}</td>
      <td class="text-end">${formatMoney(b.rentAmount)}</td>
      <td>${badge(b.bookingStatus)}</td>
      <td class="text-end text-nowrap">
        <button class="btn btn-sm btn-outline-secondary" data-action="edit" data-id="${b.bookingId}">Edit</button>
        <button class="btn btn-sm btn-outline-danger" data-action="delete" data-id="${b.bookingId}">Cancel</button>
      </td>
    </tr>`).join('');

  el('emptyNote').classList.toggle('d-none', rows.length > 0);

  const active = rows.filter(b => b.bookingStatus.toUpperCase() !== 'CANCELLED');
  el('statTotal').textContent = rows.length;
  el('statConfirmed').textContent = rows.filter(b => b.bookingStatus.toUpperCase() === 'CONFIRMED').length;
  el('statPending').textContent = rows.filter(b => b.bookingStatus.toUpperCase() === 'PENDING').length;
  el('statRent').textContent = '₹' + Number(active.reduce((sum, b) => sum + Number(b.rentAmount), 0))
      .toLocaleString('en-IN', { maximumFractionDigits: 0 });
}

function setRows(data, label) {
  rows = data;
  el('viewLabel').textContent = label;
  render();
}

/* ---------- data loading ---------- */

async function loadAll() {
  try {
    setRows(await request(API), 'Showing all bookings');
  } catch (err) {
    showAlert(err.message, 'danger');
  }
}

async function runReport(path, params, label) {
  try {
    const qs = new URLSearchParams(params).toString();
    setRows(await request(`${API}/reports/${path}?${qs}`), label);
  } catch (err) {
    showAlert(err.message, 'danger');
  }
}

/* ---------- report forms ---------- */

el('formDates').addEventListener('submit', e => {
  e.preventDefault();
  const start = el('repStart').value;
  const end = el('repEnd').value;
  if (!start || !end) { showAlert('Choose both a start and an end date.', 'warning'); return; }
  if (start > end) { showAlert('Start date must not be after end date.', 'warning'); return; }
  runReport('dates', { start, end }, `Events from ${start} to ${end}`);
});

el('formCapacity').addEventListener('submit', e => {
  e.preventDefault();
  const minCapacity = el('repCapacity').value;
  if (minCapacity === '' || Number(minCapacity) < 0) { showAlert('Enter a minimum capacity of 0 or more.', 'warning'); return; }
  runReport('capacity', { minCapacity }, `Halls with capacity of ${minCapacity} or more`);
});

el('formStatus').addEventListener('submit', e => {
  e.preventDefault();
  const status = el('repStatus').value;
  runReport('status', { status }, `Bookings with status ${status.charAt(0) + status.slice(1).toLowerCase()}`);
});

el('btnAll').addEventListener('click', loadAll);

/* ---------- create / edit ---------- */

function openForm(booking) {
  const form = el('bookingForm');
  form.classList.remove('was-validated');
  el('formAlert').innerHTML = '';
  el('eventDate').min = tomorrowIso();

  if (booking) {
    el('modalTitle').textContent = `Edit booking #${booking.bookingId}`;
    el('bookingId').value = booking.bookingId;
    el('customerName').value = booking.customerName;
    el('hallName').value = booking.hallName;
    el('eventDate').value = booking.eventDate;
    el('hallCapacity').value = booking.hallCapacity;
    el('rentAmount').value = booking.rentAmount;
    el('bookingStatus').value = booking.bookingStatus.toUpperCase();
  } else {
    el('modalTitle').textContent = 'New booking';
    form.reset();
    el('bookingId').value = '';
    el('bookingStatus').value = 'CONFIRMED';
  }
  bookingModal.show();
}

el('btnNew').addEventListener('click', () => openForm(null));

function clientValid() {
  let ok = true;
  const check = (id, valid) => {
    el(id).classList.toggle('is-invalid', !valid);
    if (!valid) ok = false;
  };
  check('customerName', el('customerName').value.trim().length > 0);
  check('hallName', el('hallName').value.trim().length > 0);
  check('eventDate', el('eventDate').value >= tomorrowIso());
  check('hallCapacity', Number(el('hallCapacity').value) > 50);
  check('rentAmount', Number(el('rentAmount').value) > 0);
  return ok;
}

el('bookingForm').addEventListener('submit', async e => {
  e.preventDefault();
  if (!clientValid()) return;

  const id = el('bookingId').value;
  const payload = {
    customerName: el('customerName').value.trim(),
    hallName: el('hallName').value.trim(),
    eventDate: el('eventDate').value,
    hallCapacity: Number(el('hallCapacity').value),
    rentAmount: Number(el('rentAmount').value),
    bookingStatus: el('bookingStatus').value
  };

  const btn = el('btnSave');
  btn.disabled = true;
  try {
    await request(id ? `${API}/${id}` : API, {
      method: id ? 'PUT' : 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload)
    });
    bookingModal.hide();
    showAlert(id ? 'Booking updated.' : 'Booking saved.');
    await loadAll();
  } catch (err) {
    showAlert(err.message, 'danger', 'formAlert');
  } finally {
    btn.disabled = false;
  }
});

/* ---------- row actions (edit / cancel) ---------- */

el('bookingBody').addEventListener('click', e => {
  const btn = e.target.closest('button[data-action]');
  if (!btn) return;
  const booking = rows.find(b => String(b.bookingId) === btn.dataset.id);
  if (!booking) return;

  if (btn.dataset.action === 'edit') {
    openForm(booking);
  } else {
    pendingDeleteId = booking.bookingId;
    el('deleteText').textContent =
      `Booking #${booking.bookingId} for ${booking.customerName} at ${booking.hallName} on ${booking.eventDate} will be removed.`;
    deleteModal.show();
  }
});

el('btnConfirmDelete').addEventListener('click', async () => {
  if (pendingDeleteId === null) return;
  try {
    await request(`${API}/${pendingDeleteId}`, { method: 'DELETE' });
    deleteModal.hide();
    showAlert('Booking cancelled.');
    await loadAll();
  } catch (err) {
    deleteModal.hide();
    showAlert(err.message, 'danger');
  } finally {
    pendingDeleteId = null;
  }
});

/* ---------- start ---------- */
loadAll();
