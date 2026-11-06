(() => {
  const progress = document.querySelector('.df-progress');
  const article = document.querySelector('article.markdown');
  const updateProgress = () => {
    const h = document.documentElement;
    const max = h.scrollHeight - h.clientHeight;
    const pct = max > 0 ? (h.scrollTop / max) * 100 : 0;
    if (progress) progress.style.width = `${pct}%`;
  };
  document.addEventListener('scroll', updateProgress, { passive: true });
  updateProgress();

  const headingNodes = [...document.querySelectorAll('article.markdown h2, article.markdown h3')];
  const tocLinks = [...document.querySelectorAll('.toc-list a')];
  if (headingNodes.length && tocLinks.length) {
    const byId = new Map(tocLinks.map(link => [link.getAttribute('href').slice(1), link]));
    const observer = new IntersectionObserver((entries) => {
      entries.forEach(entry => {
        if (!entry.isIntersecting) return;
        tocLinks.forEach(l => l.classList.remove('active'));
        const id = entry.target.id;
        const link = byId.get(id);
        if (link) link.classList.add('active');
      });
    }, { rootMargin: '-30% 0px -65% 0px', threshold: [0, 1] });
    headingNodes.forEach(h => observer.observe(h));
  }

  document.querySelectorAll('article.markdown pre').forEach((pre) => {
    const code = pre.querySelector('code');
    if (!code) return;
    const btn = document.createElement('button');
    btn.className = 'copy-btn';
    btn.type = 'button';
    btn.textContent = 'Copy';
    btn.addEventListener('click', async () => {
      try {
        await navigator.clipboard.writeText(code.innerText);
        btn.textContent = 'Copied!';
        setTimeout(() => (btn.textContent = 'Copy'), 1200);
      } catch {
        btn.textContent = 'Failed';
        setTimeout(() => (btn.textContent = 'Copy'), 1200);
      }
    });
    pre.appendChild(btn);
  });

  const toggleBtn = document.querySelector('#toggle-details');
  if (toggleBtn) {
    toggleBtn.addEventListener('click', () => {
      const all = [...document.querySelectorAll('article.markdown details')];
      if (!all.length) return;
      const shouldOpen = all.some((d) => !d.open);
      all.forEach((d) => { d.open = shouldOpen; });
      toggleBtn.textContent = shouldOpen ? 'Collapse hints' : 'Expand hints';
    });
  }

  const filterInput = document.querySelector('#toc-filter');
  if (filterInput) {
    filterInput.addEventListener('input', () => {
      const q = filterInput.value.trim().toLowerCase();
      tocLinks.forEach(link => {
        const text = link.textContent.toLowerCase();
        link.parentElement.style.display = !q || text.includes(q) ? '' : 'none';
      });
    });
  }

  if (article) {
    article.querySelectorAll('a[href^="http"]').forEach(a => {
      a.target = '_blank';
      a.rel = 'noopener noreferrer';
    });
  }


  // Persist task-list checkbox state per page
  const taskBoxes = [...document.querySelectorAll('.task-list-item input[type="checkbox"]')];
  if (taskBoxes.length) {
    const key = `curriculum-checklist:${location.pathname}`;
    let saved = [];
    try {
      saved = JSON.parse(localStorage.getItem(key) || '[]');
      if (!Array.isArray(saved)) saved = [];
    } catch {
      saved = [];
    }

    taskBoxes.forEach((box, idx) => {
      if (typeof saved[idx] === 'boolean') box.checked = saved[idx];
      box.addEventListener('change', () => {
        const state = taskBoxes.map((b) => b.checked);
        localStorage.setItem(key, JSON.stringify(state));
      });
    });
  }

})();
