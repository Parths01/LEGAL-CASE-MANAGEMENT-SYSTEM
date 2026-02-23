#!/usr/bin/env python3
"""
Batch updates all authenticated HTML pages to:
1. Add sidebar.css <link> in <head>
2. Add sidebar.js <script> right before </body>
3. Remove the old <header>…</header> block
4. Remove inline CSS/JS related to old header/navbar

Run from: /home/parth/IdeaProjects/LEGAL CASE MANAGEMENT SYSTEM/src/main/resources/static/
"""
import os, re

STATIC_DIR = "/home/parth/IdeaProjects/LEGAL CASE MANAGEMENT SYSTEM/src/main/resources/static"

# Pages that have a sidebar (authenticated pages, not landing or login)
TARGET_PAGES = [
    "admin-dashboard.html",
    "advocate-cases.html",
    "advocate-dashboard.html",
    "case-details.html",
    "cases.html",
    "clerk-cases.html",
    "clerk-dashboard.html",
    "client-dashboard.html",
    "client-details.html",
    "create-case.html",
    "create-client.html",
    "hearings.html",
    "invoices.html",
    "messages.html",
    "notices.html",
    "reports.html",
    "tasks.html",
    "user-management.html",
]

SIDEBAR_CSS = '<link rel="stylesheet" href="/assets/css/sidebar.css">'
SIDEBAR_JS  = '<script src="/assets/js/sidebar.js"></script>'

def add_sidebar_css(html):
    """Insert sidebar.css before the first existing <link> or before </head>"""
    if '/assets/css/sidebar.css' in html:
        return html
    # Insert after <head> or before first <link rel="stylesheet">
    if '<link' in html:
        return html.replace('<link', SIDEBAR_CSS + '\n    <link', 1)
    return html.replace('</head>', '    ' + SIDEBAR_CSS + '\n</head>')

def add_sidebar_js(html):
    """Insert sidebar.js before </body>"""
    if '/assets/js/sidebar.js' in html:
        return html
    return html.replace('</body>', SIDEBAR_JS + '\n</body>')

def remove_header_block(html):
    """Remove the <header>...</header> block (the old top navbar)"""
    # Remove <header ...>...</header> block
    html = re.sub(r'<header[^>]*>.*?</header>', '', html, flags=re.DOTALL)
    return html

def remove_header_css(html):
    """Remove CSS classes related to old header/navbar from inline <style> blocks"""
    # Remove .header { ... } .nav-menu { ...} .nav-links {...} .logout-btn {...} etc
    # We do a targeted removal of common navbar CSS blocks from <style> tags
    patterns = [
        r'/\*\s*Header\s*Styles?\s*\*/.*?(?=\n\s*[.#]|\Z)',
        r'\.header\s*\{[^}]*\}',
        r'\.header\s+\.container\s*\{[^}]*\}',
        r'\.header\s+\.inner\s*\{[^}]*\}',
        r'\.logo-section\s*\{[^}]*\}',
        r'\.logo-icon\s*\{[^}]*\}',
        r'\.firm-name\s*h1\s*\{[^}]*\}',
        r'\.firm-name\s*p\s*\{[^}]*\}',
        r'\.nav-links\s*\{[^}]*\}',
        r'\.nav-links\s*a\s*\{[^}]*\}',
        r'\.nav-links\s*a:hover\s*\{[^}]*\}',
        r'\.nav-menu\s*\{[^}]*\}',
        r'\.nav-menu\s*a\s*\{[^}]*\}',
        r'\.nav-menu\s*a:hover\s*\{[^}]*\}',
        r'\.logout-btn\s*\{[^}]*\}',
        r'\.logout-btn:hover\s*\{[^}]*\}',
        r'\.brand\s*\{[^}]*\}',
        r'\.brand\s+i\s*\{[^}]*\}',
        r'\.brand\s+h1\s*\{[^}]*\}',
        r'\.brand\s+p\s*\{[^}]*\}',
    ]
    for pat in patterns:
        html = re.sub(pat, '', html, flags=re.DOTALL)
    return html

def fix_existing_layout(html):
    """Remove old dashboard-container wrapper that assumed full width"""
    # Replace class="dashboard-container" style that used flex column
    # We keep the content but just remove the wrapper assumption
    # The page-inner div from sidebar.js wraps content
    return html

def process_file(path):
    with open(path, 'r', encoding='utf-8') as f:
        html = f.read()

    original = html
    html = add_sidebar_css(html)
    html = remove_header_block(html)
    html = remove_header_css(html)
    html = add_sidebar_js(html)

    if html != original:
        with open(path, 'w', encoding='utf-8') as f:
            f.write(html)
        print(f"  ✅ Updated: {os.path.basename(path)}")
    else:
        print(f"  ⚠️  No changes: {os.path.basename(path)}")

def main():
    print("🔄 Starting batch sidebar injection...\n")
    for page in TARGET_PAGES:
        fpath = os.path.join(STATIC_DIR, page)
        if os.path.exists(fpath):
            process_file(fpath)
        else:
            print(f"  ❌ NOT FOUND: {page}")
    print("\n✅ Batch update complete.")

if __name__ == '__main__':
    main()
