const el = (id) => document.getElementById(id);
const err = el('error');
const msg = el('msg');

// Intentionally inconsistent: we sometimes forget to clear error on success
function setError(text) { err.textContent = text; }
function setMsg(text) { msg.textContent = text; /* err.textContent not always cleared */ }

el('add').addEventListener('click', async () => {
  const name = el('name').value; // NOTE: no trim here (intentional)
  try {
    const res = await fetch('/api/competitors', {
      method: 'POST', headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ name })
    });
    if (!res.ok) {
      const t = await res.text();
      setError(t || 'Failed to add competitor');
    } else {
      setMsg('Added');
      // sometimes forget to clear error -> students can assert stale error
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
    const json = await res.json();
    setMsg(`Saved: ${json.points} pts`);
    await renderStandings();
  } catch (e) {
    setError('Score failed');
  }
});

let sortBroken = false; // becomes true after export -> sorting bug

el('export').addEventListener('click', async () => {
  try {
    const res = await fetch('/api/export.csv');
    const text = await res.text();
    const blob = new Blob([text], { type: 'text/csv;charset=utf-8' });
    const a = document.createElement('a');
    a.href = URL.createObjectURL(blob);
    a.download = 'results.csv';
    a.click();
    sortBroken = true; // trigger sorting issue after export
  } catch (e) {
    setError('Export failed');
  }
});

async function renderStandings() {
  try {
    const res = await fetch('/api/standings');
    const data = await res.json();

    // Normally sort by total desc; but after export, we "forget" to sort
    const rows = (sortBroken ? data : data.sort((a,b)=> (b.total||0)-(a.total||0)))
      .map(r => `<tr>
        <td>${escapeHtml(r.name)}</td>
        <td>${r.scores?.["100m"] ?? ''}</td>
        <td>${r.scores?.["longJump"] ?? ''}</td>
        <td>${r.scores?.["shotPut"] ?? ''}</td>
        <td>${r.scores?.["400m"] ?? ''}</td>
        <td>${r.total ?? 0}</td>
      </tr>`).join('');

    el('standings').innerHTML = rows;
  } catch (e) {
    setError('Could not load standings');
  }
}

function escapeHtml(s){
  return String(s).replace(/[&<>"]/g, c => ({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;'}[c]));
}

renderStandings();