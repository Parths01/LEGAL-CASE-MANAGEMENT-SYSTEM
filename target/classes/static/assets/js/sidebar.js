/**
 * Smart Legal CMS – Shared Sidebar Component
 * Auto-injects a role-aware sidebar into any authenticated page.
 *
 * Usage (add to any authenticated page):
 *   <link rel="stylesheet" href="/assets/css/sidebar.css">
 *   <script src="/assets/js/sidebar.js"></script>
 *   <!-- Wrap page body content in <div class="page-inner"> -->
 */

(function () {
    'use strict';

    /* ── Role-based navigation menus ─────────────────────── */
    const NAV_MENUS = {
        ADMIN: [
            { label: 'Dashboard', icon: 'fas fa-tachometer-alt', href: '/admin-dashboard.html' },
            { label: 'Cases', icon: 'fas fa-gavel', href: '/cases.html' },
            { label: 'Hearings', icon: 'fas fa-calendar-alt', href: '/hearings.html' },
            { label: 'Tasks', icon: 'fas fa-tasks', href: '/tasks.html' },
            { label: 'Invoices', icon: 'fas fa-file-invoice-dollar', href: '/invoices.html' },
            { label: 'Legal Notices', icon: 'fas fa-envelope-open-text', href: '/notices.html' },
            { label: 'Messages', icon: 'fas fa-comments', href: '/messages.html' },
            { label: 'Reports', icon: 'fas fa-chart-bar', href: '/reports.html' },
            { label: 'User Management', icon: 'fas fa-users', href: '/user-management.html' },
            { label: 'My Profile', icon: 'fas fa-id-badge', href: '/profile.html' },
        ],
        ADVOCATE: [
            { label: 'Dashboard', icon: 'fas fa-tachometer-alt', href: '/advocate-dashboard.html' },
            { label: 'My Cases', icon: 'fas fa-gavel', href: '/advocate-cases.html' },
            { label: 'Hearings', icon: 'fas fa-calendar-alt', href: '/hearings.html' },
            { label: 'Tasks', icon: 'fas fa-tasks', href: '/tasks.html' },
            { label: 'Messages', icon: 'fas fa-comments', href: '/messages.html' },
            { label: 'My Profile', icon: 'fas fa-id-badge', href: '/profile.html' },
        ],
        CLERK: [
            { label: 'Dashboard', icon: 'fas fa-tachometer-alt', href: '/clerk-dashboard.html' },
            { label: 'Cases', icon: 'fas fa-gavel', href: '/clerk-cases.html' },
            { label: 'Hearings', icon: 'fas fa-calendar-alt', href: '/hearings.html' },
            { label: 'Tasks', icon: 'fas fa-tasks', href: '/tasks.html' },
            { label: 'Legal Notices', icon: 'fas fa-envelope-open-text', href: '/notices.html' },
            { label: 'Messages', icon: 'fas fa-comments', href: '/messages.html' },
            { label: 'My Profile', icon: 'fas fa-id-badge', href: '/profile.html' },
        ],
        CLIENT: [
            { label: 'Dashboard', icon: 'fas fa-tachometer-alt', href: '/client-dashboard.html' },
            { label: 'My Cases', icon: 'fas fa-gavel', href: '/cases.html' },
            { label: 'Invoices', icon: 'fas fa-file-invoice-dollar', href: '/invoices.html' },
            { label: 'Messages', icon: 'fas fa-comments', href: '/messages.html' },
            { label: 'My Profile', icon: 'fas fa-id-badge', href: '/profile.html' },
        ],
    };

    /* ── Helpers ─────────────────────────────────────────── */
    function getRole() { return (localStorage.getItem('userRole') || '').toUpperCase(); }
    function getName() { return localStorage.getItem('userName') || 'User'; }
    function getEmail() { return localStorage.getItem('userEmail') || ''; }

    function currentPagePath() {
        const p = window.location.pathname;
        // normalise – strip trailing slash
        return p === '/' ? '/index.html' : p;
    }

    function initials(name) {
        return name.split(' ').slice(0, 2).map(w => w[0]).join('').toUpperCase() || '?';
    }

    function roleLabel(role) {
        return { ADMIN: 'Administrator', ADVOCATE: 'Advocate', CLERK: 'Clerk', CLIENT: 'Client' }[role] || role;
    }

    function logout() {
        localStorage.clear();
        window.location.href = '/login.html';
    }

    /* ── Build HTML ─────────────────────────────────────── */
    function buildSidebar(role, name) {
        const menuItems = NAV_MENUS[role] || [];
        const curPath = currentPagePath();

        const navHTML = menuItems.map(item => {
            const isActive = curPath === item.href || curPath.endsWith(item.href.replace(/^\//, ''));
            return `<a href="${item.href}" class="${isActive ? 'active' : ''}">
        <i class="${item.icon}"></i> ${item.label}
      </a>`;
        }).join('');

        return `
      <div class="sidebar-brand" style="text-decoration:none;">
        <div class="sidebar-brand-icon"><i class="fas fa-balance-scale"></i></div>
        <div class="sidebar-brand-text">
          <h2>Smart Legal</h2>
          <p>Case Management</p>
        </div>
      </div>

      <div class="sidebar-role-badge">
        <div class="avatar">${initials(name)}</div>
        <div class="info">
          <div class="name">${name}</div>
          <div class="role">${roleLabel(role)}</div>
        </div>
      </div>

      <div class="sidebar-section-label">Navigation</div>
      <nav class="sidebar-nav">${navHTML}</nav>

      <div class="sidebar-footer">
        <button onclick="window._sidebarLogout()">
          <i class="fas fa-sign-out-alt"></i> Sign Out
        </button>
      </div>
    `;
    }

    /* ── Wrap existing <body> content ────────────────────── */
    function wrapBodyContent() {
        // Move all existing body children into #app-content > .page-inner
        const bodyChildren = Array.from(document.body.childNodes);

        // Create sidebar element
        const sidebar = document.createElement('div');
        sidebar.id = 'app-sidebar';

        // Create overlay for mobile
        const overlay = document.createElement('div');
        overlay.id = 'sidebar-overlay';

        // Create content wrapper
        const contentWrapper = document.createElement('div');
        contentWrapper.id = 'app-content';

        // Create topbar
        const topbar = document.createElement('div');
        topbar.className = 'content-topbar';
        topbar.innerHTML = `
      <button class="topbar-menu-toggle" id="sidebar-toggle" onclick="window._sidebarToggle()">
        <i class="fas fa-bars"></i>
      </button>
      <span class="topbar-page-title" id="topbar-page-title">Smart Legal CMS</span>
    `;

        // Create page inner (existing content goes here)
        const pageInner = document.createElement('div');
        pageInner.className = 'page-inner';

        // Move existing body children into pageInner
        bodyChildren.forEach(node => {
            // Skip script tags (move them back to body later)
            if (node.nodeType === 1 && node.tagName === 'SCRIPT') return;
            pageInner.appendChild(node);
        });

        contentWrapper.appendChild(topbar);
        contentWrapper.appendChild(pageInner);

        document.body.innerHTML = '';
        document.body.appendChild(overlay);
        document.body.appendChild(sidebar);
        document.body.appendChild(contentWrapper);

        return { sidebar, overlay, topbar };
    }

    /* ── Mobile toggle ───────────────────────────────────── */
    window._sidebarToggle = function () {
        const sb = document.getElementById('app-sidebar');
        const ov = document.getElementById('sidebar-overlay');
        if (sb) sb.classList.toggle('open');
        if (ov) ov.classList.toggle('open');
    };

    window._sidebarLogout = function () { logout(); };

    /* ── Main init ──────────────────────────────────────── */
    function init() {
        const role = getRole();
        const name = getName();

        if (!role) {
            // Not logged in – redirect to login (except on public pages)
            const pubPages = ['/login.html', '/index.html', '/'];
            const cur = currentPagePath();
            if (!pubPages.some(p => cur === p || cur.endsWith(p.replace(/^\//, '')))) {
                window.location.href = '/login.html';
            }
            return;
        }

        // Inject sidebar CSS font if not present
        if (!document.getElementById('fa-cdn')) {
            const fa = document.createElement('link');
            fa.id = 'fa-cdn';
            fa.rel = 'stylesheet';
            fa.href = 'https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css';
            document.head.appendChild(fa);
        }

        const { sidebar, overlay } = wrapBodyContent();

        // Inject sidebar HTML
        sidebar.innerHTML = buildSidebar(role, name);

        // Overlay click closes sidebar
        overlay.addEventListener('click', () => {
            sidebar.classList.remove('open');
            overlay.classList.remove('open');
        });

        // Set topbar title from the active nav item
        setTimeout(() => {
            const activeLink = sidebar.querySelector('.sidebar-nav a.active');
            const titleEl = document.getElementById('topbar-page-title');
            if (activeLink && titleEl) {
                titleEl.textContent = activeLink.textContent.trim();
            }
        }, 50);
    }

    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', init);
    } else {
        init();
    }
})();
