(function () {
  const headerQuery = window.matchMedia("(min-width: 1024px)");
  const desktopQuery = window.matchMedia("(min-width: 1280px)");

  function initShell() {
    const shell = document.querySelector("[data-app-shell]");
    if (!shell) {
      return;
    }

    const overlay = shell.querySelector("[data-sidebar-overlay]");
    const sidebarOpenIcon = shell.querySelector("[data-sidebar-open-icon]");
    const sidebarCloseIcon = shell.querySelector("[data-sidebar-close-icon]");
    const headerMenu = shell.querySelector("[data-header-menu]");
    const headerMenuToggle = shell.querySelector("[data-header-menu-toggle]");
    const userMenu = shell.querySelector("[data-user-menu]");
    const userMenuToggle = shell.querySelector("[data-user-menu-toggle]");
    const userMenuPanel = shell.querySelector("[data-user-menu-panel]");
    const userMenuArrow = shell.querySelector("[data-user-menu-arrow]");
    const storedCollapsed = localStorage.getItem("smartLaundrySidebarCollapsed") === "true";

    if (storedCollapsed && desktopQuery.matches) {
      shell.classList.add("is-sidebar-collapsed");
    }

    function setSidebarOpen(open) {
      shell.classList.toggle("is-sidebar-open", open);
      document.body.classList.toggle("overflow-hidden", open && !desktopQuery.matches);
      if (overlay) {
        overlay.classList.toggle("hidden", !open);
        overlay.classList.toggle("block", open);
      }
      if (sidebarOpenIcon && sidebarCloseIcon) {
        sidebarOpenIcon.classList.toggle("hidden", open);
        sidebarCloseIcon.classList.toggle("hidden", !open);
      }
    }

    function toggleSidebar() {
      if (desktopQuery.matches) {
        setSidebarOpen(false);
        const collapsed = !shell.classList.contains("is-sidebar-collapsed");
        shell.classList.toggle("is-sidebar-collapsed", collapsed);
        localStorage.setItem("smartLaundrySidebarCollapsed", String(collapsed));
        return;
      }
      setSidebarOpen(!shell.classList.contains("is-sidebar-open"));
    }

    function setHeaderMenuOpen(open) {
      if (!headerMenu) {
        return;
      }
      headerMenu.classList.toggle("hidden", !open);
      headerMenu.classList.toggle("flex", open);
    }

    function setUserMenuOpen(open) {
      if (!userMenuPanel) {
        return;
      }
      userMenuPanel.classList.toggle("hidden", !open);
      userMenuPanel.classList.toggle("flex", open);
      if (userMenuArrow) {
        userMenuArrow.classList.toggle("rotate-180", open);
      }
    }

    shell.querySelectorAll("[data-sidebar-toggle]").forEach((button) => {
      button.addEventListener("click", toggleSidebar);
    });

    if (overlay) {
      overlay.addEventListener("click", () => setSidebarOpen(false));
    }

    shell.querySelectorAll("[data-sidebar] a").forEach((link) => {
      link.addEventListener("click", () => {
        if (!desktopQuery.matches) {
          setSidebarOpen(false);
        }
      });
    });

    if (headerMenuToggle) {
      headerMenuToggle.addEventListener("click", () => {
        const isOpen = headerMenu && headerMenu.classList.contains("flex");
        setHeaderMenuOpen(!isOpen);
      });
    }

    if (userMenuToggle) {
      userMenuToggle.addEventListener("click", (event) => {
        event.stopPropagation();
        const isOpen = userMenuPanel && userMenuPanel.classList.contains("flex");
        setUserMenuOpen(!isOpen);
      });
    }

    document.addEventListener("click", (event) => {
      if (userMenu && !userMenu.contains(event.target)) {
        setUserMenuOpen(false);
      }
    });

    document.addEventListener("keydown", (event) => {
      if (event.key === "Escape") {
        setSidebarOpen(false);
        setUserMenuOpen(false);
        setHeaderMenuOpen(false);
      }
    });

    desktopQuery.addEventListener("change", (event) => {
      setSidebarOpen(false);
      if (event.matches) {
        const collapsed = localStorage.getItem("smartLaundrySidebarCollapsed") === "true";
        shell.classList.toggle("is-sidebar-collapsed", collapsed);
      } else {
        shell.classList.remove("is-sidebar-collapsed");
      }
    });

    headerQuery.addEventListener("change", (event) => {
      setHeaderMenuOpen(event.matches);
    });

    setHeaderMenuOpen(headerQuery.matches);

    if (!desktopQuery.matches) {
      shell.classList.remove("is-sidebar-collapsed");
    }
  }

  if (document.readyState === "loading") {
    document.addEventListener("DOMContentLoaded", initShell);
  } else {
    initShell();
  }
})();
