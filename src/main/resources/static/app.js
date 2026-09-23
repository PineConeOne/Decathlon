const el = (id) => document.getElementById(id);
const err = el('error');
const msg = el('msg');

function setError(text) { err.textContent = text; }
function clearError() { err.textContent = ''; }
function setMsg(text) { msg.textContent = text; }

const DECATHLON_EVENTS = [
  { id: '100m', menuLabel: '100m (s)' },
  { id: '110mHurdles', menuLabel: '110m Hurdles (s)' },
  { id: '400m', menuLabel: '400m (s)' },
  { id: '1500m', menuLabel: '1500m (s)' },
  { id: 'discusThrow', menuLabel: 'Discus Throw (m)' },
  { id: 'highJump', menuLabel: 'High Jump (cm)' },
  { id: 'javelinThrow', menuLabel: 'Javelin Throw (m)' },
  { id: 'longJump', menuLabel: 'Long Jump (cm)' },
  { id: 'poleVault', menuLabel: 'Pole Vault (cm)' },
  { id: 'shotPut', menuLabel: 'Shot Put (m)' }
];

const HEPTATHLON_EVENTS = [
  { id: 'hep100mHurdles', menuLabel: '100m Hurdles (s)' },
  { id: 'hep200m', menuLabel: '200m (s)' },
  { id: 'hep800m', menuLabel: '800m (s)' },
  { id: 'hepHighJump', menuLabel: 'High Jump (cm)' },
  { id: 'hepJavelinThrow', menuLabel: 'Javelin Throw (m)' },
  { id: 'hepLongJump', menuLabel: 'Long Jump (cm)' },
  { id: 'hepShotPut', menuLabel: 'Shot Put (m)' }
];

function eventsForGroup(group) {
  return group === 'Heptathlon' ? HEPTATHLON_EVENTS : DECATHLON_EVENTS;
}

function getSelectedGroup() {
  return document.querySelector('input[name="group"]:checked').value;
}

function populateEventSelect() {
  const select = el('event');
  select.innerHTML = '';
  eventsForGroup(getSelectedGroup()).forEach(ev => {
    const opt = document.createElement('option');
    opt.value = ev.id;
    opt.textContent = ev.menuLabel;
    select.appendChild(opt);
  });
}

function updateGroupVisibility() {
  const group = getSelectedGroup();
  el('decathlonSection').style.display = group === 'Decathlon' ? '' : 'none';
  el('heptathlonSection').style.display = group === 'Heptathlon' ? '' : 'none';
  populateEventSelect();
}

document.querySelectorAll('input[name="group"]').forEach(radio => {
  radio.addEventListener('change', updateGroupVisibility);
});
updateGroupVisibility();

el('add').addEventListener('click', async () => {
  const name = el('name').value;
  try {
    const res = await fetch('/api/competitors', {
      method: 'POST', headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ name })
    });
    if (!res.ok) {
      const t = await res.text();
      setError(t || 'Failed to add competitor');
    } else {
      clearError();
      setMsg('Added');
      el('name').value = '';
    }
    await renderStandings();
  } catch (e) {
    setError('Network error');
  }
});

el('save').addEventListener('click', async () => {
  const body = {
    name: el('name2').value,
    event: el('event').value,
    raw: parseFloat(el('raw').value)
  };
  try {
    const res = await fetch('/api/score', {
      method: 'POST', headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(body)
    });
    if (!res.ok) {
      const t = await res.text();
      setError(t || 'Failed to save score');
      return;
    }
    const json = await res.json();
    clearError();
    setMsg(`Saved: ${json.points} pts`);
    await renderStandings();
  } catch (e) {
    setError('Score failed');
  }
});

function timestamp() {
  const d = new Date();
  const pad = (n) => String(n).padStart(2, '0');
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}_${pad(d.getHours())}-${pad(d.getMinutes())}-${pad(d.getSeconds())}`;
}

function isValidExportCsv(text) {
  if (!text || !text.trim()) {
    return false;
  }
  const lines = text.split(/\r?\n/);
  const hasDecathlonHeader = lines.some(l => l.trim() === 'Decathlon');
  const hasHeptathlonHeader = lines.some(l => l.trim() === 'Heptathlon');
  const hasColumnHeader = lines.some(l => l.startsWith('Name,'));
  return hasDecathlonHeader && hasHeptathlonHeader && hasColumnHeader;
}

el('export').addEventListener('click', async () => {
  try {
    const res = await fetch('/api/export.csv');
    if (!res.ok) {
      setError('Export failed: the file could not be downloaded.');
      return;
    }
    const text = await res.text();
    if (!isValidExportCsv(text)) {
      setError('Export failed: the downloaded file is corrupt.');
      return;
    }
    clearError();
    const blob = new Blob([text], { type: 'text/csv;charset=utf-8' });
    const a = document.createElement('a');
    a.href = URL.createObjectURL(blob);
    a.download = `results-${timestamp()}.csv`;
    a.click();
  } catch (e) {
    setError('Export failed');
  }
});

el('importFile').addEventListener('change', async (event) => {
  const file = event.target.files[0];
  if (!file) {
    return;
  }
  try {
    const text = await file.text();
    const res = await fetch('/api/import', {
      method: 'POST',
      headers: { 'Content-Type': 'text/plain' },
      body: text
    });
    if (!res.ok) {
      setError('Import failed');
      return;
    }
    clearError();
    setMsg('Imported');
    await renderStandings();
  } catch (e) {
    setError('Import failed');
  }
  event.target.value = '';
});

function renderTableBody(tbodySelector, events, group) {
  const tbody = document.querySelector(tbodySelector);
  const rows = (group && group.rows) || [];
  tbody.innerHTML = rows.map(r => {
    const cells = events.map(e => `<td>${r.scores?.[e.id] ?? ''}</td>`).join('');
    return `<tr>
      <td>${r.position}</td>
      <td>${escapeHtml(r.name)}</td>
      ${cells}
      <td>${r.total}</td>
    </tr>`;
  }).join('');
}

async function renderStandings() {
  try {
    const res = await fetch('/api/standings');
    if (!res.ok) {
      throw new Error('Failed to load standings');
    }
    const data = await res.json();
    renderTableBody('[data-testid="decathlonStandingsTable"]', DECATHLON_EVENTS, data.decathlon);
    renderTableBody('[data-testid="heptathlonStandingsTable"]', HEPTATHLON_EVENTS, data.heptathlon);
  } catch (e) {
    setError('Could not load standings');
  }
}

function escapeHtml(s) {
  return String(s).replace(/[&<>"]/g, c => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;' }[c]));
}

renderStandings();