(function() {
    const token = localStorage.getItem('token');
    const userRole = (localStorage.getItem('userRole') || '').toUpperCase();

    const menuByRole = {
        ADMIN: [
            { href: 'admin-dashboard.html', label: 'Dashboard', icon: 'fas fa-home' },
            { href: 'cases.html', label: 'Cases', icon: 'fas fa-folder-open' },
            { href: 'advocates.html', label: 'Advocates', icon: 'fas fa-user-tie' },
            { href: 'clients.html', label: 'Clients', icon: 'fas fa-users' },
            { href: 'hearings.html', label: 'Hearings', icon: 'fas fa-gavel' },
            { href: 'documents.html', label: 'Documents', icon: 'fas fa-file-alt' },
            { href: 'invoices.html', label: 'Invoices', icon: 'fas fa-file-invoice-dollar' },
            { href: 'payments.html', label: 'Payments', icon: 'fas fa-credit-card' },
            { href: 'legal-notices.html', label: 'Legal Notices', icon: 'fas fa-envelope-open-text' },
            { href: 'reports.html', label: 'Reports', icon: 'fas fa-chart-bar' },
            { href: 'profile.html', label: 'Profile', icon: 'fas fa-user-circle' },
            { href: 'settings.html', label: 'Settings', icon: 'fas fa-cog' }
        ],
        ADVOCATE: [
            { href: 'advocate-dashboard.html', label: 'Dashboard', icon: 'fas fa-home' },
            { href: 'cases.html', label: 'Cases', icon: 'fas fa-folder-open' },
            { href: 'hearings.html', label: 'Hearings', icon: 'fas fa-gavel' },
            { href: 'documents.html', label: 'Documents', icon: 'fas fa-file-alt' },
            { href: 'invoices.html', label: 'Invoices', icon: 'fas fa-file-invoice-dollar' },
            { href: 'clients.html', label: 'Clients', icon: 'fas fa-users' },
            { href: 'profile.html', label: 'Profile', icon: 'fas fa-user-circle' }
        ],
        CLIENT: [
            { href: 'client-dashboard.html', label: 'Dashboard', icon: 'fas fa-home' },
            { href: 'cases.html', label: 'Cases', icon: 'fas fa-folder-open' },
            { href: 'documents.html', label: 'Documents', icon: 'fas fa-file-alt' },
            { href: 'invoices.html', label: 'Invoices', icon: 'fas fa-file-invoice-dollar' },
            { href: 'payments.html', label: 'Payments', icon: 'fas fa-credit-card' },
            { href: 'profile.html', label: 'Profile', icon: 'fas fa-user-circle' }
        ]
    };

    function titleCaseRole(role) {
        if (!role) return 'User';
        return role.charAt(0) + role.slice(1).toLowerCase();
    }

    function buildSidebar() {
        const sidebar = document.querySelector('.sidebar-menu');
        if (!sidebar) return;

        const page = window.location.pathname.split('/').pop() || 'index.html';
        const menu = menuByRole[userRole] || menuByRole.ADMIN;

        sidebar.innerHTML = menu.map(item => {
            const isActive = page === item.href;
            return `<li><a href="${item.href}" class="${isActive ? 'active' : ''}"><i class="${item.icon}"></i> ${item.label}</a></li>`;
        }).join('');
    }

    function applyUserHeader() {
        const name = localStorage.getItem('userName');
        const roleLabel = titleCaseRole(userRole || '');
        const nameEl = document.getElementById('userName') || document.querySelector('.user-info strong');
        const roleEl = document.querySelector('.user-info small');
        const avatarEl = document.querySelector('.user-avatar');

        if (name && nameEl) nameEl.textContent = name;
        if (roleEl) roleEl.textContent = roleLabel;
        if (avatarEl && name) avatarEl.textContent = name.charAt(0).toUpperCase();
    }

    function init() {
        // Skip nav setup on auth pages without token; still allow view.
        if (!token) return;
        buildSidebar();
        applyUserHeader();
        window.appUserRole = userRole; // expose for other scripts if needed
    }

    document.addEventListener('DOMContentLoaded', init);
})();
