const API_BASE_URL = "https://pathwayed-uglily-tenley.ngrok-free.dev/api/";
const LOCAL_API_URL = "http://localhost:8000/api/";

// Persistent API URL Selection
let activeApiUrl = localStorage.getItem("SAFE_ROADS_API_URL") || API_BASE_URL;

const saveApiUrl = (url) => {
    activeApiUrl = url;
    localStorage.setItem("SAFE_ROADS_API_URL", url);
};

// Session Management
const Session = {
    save: (authData) => {
        localStorage.setItem("SAFE_ROADS_SESSION", JSON.stringify({
            ...authData,
            mobile: authData.mobile || "",
            timestamp: new Date().getTime()
        }));
    },
    get: () => {
        const session = localStorage.getItem("SAFE_ROADS_SESSION");
        return session ? JSON.parse(session) : null;
    },
    clear: () => {
        localStorage.removeItem("SAFE_ROADS_SESSION");
        window.location.href = "index.html";
    }
};

// API Client
const Api = {
    post: async (endpoint, data, isMultipart = false) => {
        try {
            return await Api._fetch(endpoint, {
                method: "POST",
                body: isMultipart ? data : JSON.stringify(data),
                isMultipart
            });
        } catch (error) {
            console.error(`API Error (${endpoint}):`, error);
            throw error;
        }
    },
    get: async (endpoint, params = {}) => {
        try {
            const query = new URLSearchParams(params).toString();
            return await Api._fetch(`${endpoint}${query ? '?' + query : ''}`, { method: "GET" });
        } catch (error) {
            console.error(`API Error (${endpoint}):`, error);
            throw error;
        }
    },
    _fetch: async (endpoint, options) => {
        const headers = { "ngrok-skip-browser-warning": "true" };
        if (!options.isMultipart && options.method !== "GET") {
            headers["Content-Type"] = "application/json";
        }

        const controller = new AbortController();
        const timeoutId = setTimeout(() => controller.abort(), 15000); // 15s timeout to allow SMTP

        try {
            // Try primary (ngrok) first
            const response = await fetch(`${activeApiUrl}${endpoint}`, {
                ...options,
                headers,
                signal: controller.signal
            });
            clearTimeout(timeoutId);
            const result = await response.json();
            if (!response.ok) throw new Error(result.error || "Request failed");
            return result;
        } catch (error) {
            clearTimeout(timeoutId);
            // Fallback to local if primary fails/times out and we aren't already on local
            if (activeApiUrl !== LOCAL_API_URL) {
                console.warn("Primary API failed or timed out, trying local fallback...");
                try {
                    const response = await fetch(`${LOCAL_API_URL}${endpoint}`, {
                        ...options,
                        headers
                    });
                    const result = await response.json();
                    if (!response.ok) throw new Error(result.error || "Request failed");
                    saveApiUrl(LOCAL_API_URL); // Persist local for session
                    return result;
                } catch (localError) {
                    throw new Error("API Connection Failed: Ensure backend is running.");
                }
            }
            throw error;
        }
    },
    delete: async (endpoint) => {
        try {
            const response = await fetch(`${activeApiUrl}${endpoint}`, {
                method: "DELETE",
                headers: { "ngrok-skip-browser-warning": "true" }
            });
            if (!response.ok) throw new Error("Delete failed");
            return true;
        } catch (error) {
            console.error(`API Error (${endpoint}):`, error);
            throw error;
        }
    },
    patch: async (endpoint, data, isMultipart = false) => {
        try {
            return await Api._fetch(endpoint, {
                method: "PATCH",
                body: isMultipart ? data : JSON.stringify(data),
                isMultipart
            });
        } catch (error) {
            console.error(`API Error (${endpoint}):`, error);
            throw error;
        }
    }
};

// UI Helpers
const UI = {
    fixImageUrl: (url) => {
        if (!url) return null;
        if (url.startsWith('http')) {
            if (url.includes('localhost:8000') && activeApiUrl.includes('ngrok')) {
                return url.replace('http://localhost:8000/', activeApiUrl.replace('api/', ''));
            }
            return url;
        }
        // Prepend current active API origin if relative
        return `${activeApiUrl.replace(/\/api\/$/, '')}${url}`;
    },
    loadImage: async (url, imgElement) => {
        if (!url || !imgElement) return;
        const fixedUrl = UI.fixImageUrl(url);
        if (fixedUrl.includes('ngrok')) {
            try {
                const response = await fetch(fixedUrl, {
                    headers: { 'ngrok-skip-browser-warning': 'true' }
                });
                if (response.ok) {
                    const blob = await response.blob();
                    imgElement.src = URL.createObjectURL(blob);
                } else {
                    imgElement.src = fixedUrl;
                }
            } catch (e) {
                imgElement.src = fixedUrl;
            }
        } else {
            imgElement.src = fixedUrl;
        }
    },
    showLoading: (btn, text = "Processing...") => {
        btn.disabled = true;
        btn.dataset.oldText = btn.textContent;
        btn.textContent = text;
    },
    hideLoading: (btn) => {
        btn.disabled = false;
        btn.textContent = btn.dataset.oldText;
    },
    showAlert: (msg, type = "error") => {
        alert(msg); // Can be replaced with a toast notification later
    },
    showImageModal: (url) => {
        console.log("DEBUG: showImageModal called with url:", url);
        if (!url) return;
        
        let modal = document.getElementById('globalImageModal');
        if (!modal) {
            console.log("DEBUG: Creating globalImageModal...");
            modal = document.createElement('div');
            modal.id = 'globalImageModal';
            modal.style = `
                display: none; position: fixed; z-index: 9999; left: 0; top: 0; 
                width: 100%; height: 100%; background: rgba(0,0,0,0.9); 
                backdrop-filter: blur(8px); flex-direction: column; align-items: center; justify-content: center; cursor: zoom-out;
            `;
            modal.innerHTML = `
                <span style="position: absolute; right: 25px; top: 20px; color: white; font-size: 35px; cursor: pointer;">&times;</span>
                <img id="modalImg" style="max-width: 90%; max-height: 85%; object-fit: contain; border-radius: 8px; box-shadow: 0 0 30px rgba(0,0,0,0.5);">
            `;
            document.body.appendChild(modal);
            modal.onclick = (e) => {
                console.log("DEBUG: Closing modal");
                modal.style.display = 'none';
            };
        }
        
        const modalImg = document.getElementById('modalImg');
        console.log("DEBUG: Loading image into modal...");
        UI.loadImage(url, modalImg); // Reuse blob logic for ngrok
        modal.style.display = 'flex';
    },
    injectBackground: () => {
        if (document.querySelector('.background-bubbles')) return;
        const container = document.createElement('div');
        container.className = 'background-bubbles';
        container.innerHTML = `
            <div class="bubble bubble-1"></div>
            <div class="bubble bubble-2"></div>
            <div class="bubble bubble-3"></div>
        `;
        document.body.prepend(container);
    },
    formatId: (id) => {
        if (!id) return "N/A";
        let clean = id.toString();
        const low = clean.toLowerCase();
        if (low.startsWith("src - ")) clean = clean.substring(6);
        else if (low.startsWith("src-")) clean = clean.substring(4);
        else if (low.startsWith("src ")) clean = clean.substring(4);
        else if (low.startsWith("src")) clean = clean.substring(3);
        return `src - ${clean}`;
    }
};

// Global Initialization
document.addEventListener("DOMContentLoaded", () => {
    UI.injectBackground();
    const session = Session.get();
    const pathParts = window.location.pathname.split("/");
    const currentPage = pathParts[pathParts.length - 1] || "index.html";

    // Auth Guard
    const publicPages = ["index.html", "login.html", "register.html", "forgot-password.html"];
    if (!session && !publicPages.includes(currentPage)) {
        window.location.href = "login.html";
        return;
    }

    // Role-based Page Protection
    if (session) {
        const citizenPages = ["report.html", "my-reports.html"];
        const authorityPages = ["authority-complaints.html"];
        const supervisorPages = ["supervisor-complaints.html", "supervisor-dashboard.html"];

        if (session.role === 'CITIZEN' && (authorityPages.includes(currentPage) || supervisorPages.includes(currentPage))) {
            window.location.href = "dashboard.html";
            return;
        }
        if (session.role === 'AUTHORITY' && supervisorPages.includes(currentPage)) {
            window.location.href = "dashboard.html";
            return;
        }
        if (session.role === 'SUPERVISOR' && authorityPages.includes(currentPage)) {
            window.location.href = "dashboard.html";
            return;
        }
        if ((session.role === 'AUTHORITY' || session.role === 'SUPERVISOR') && citizenPages.includes(currentPage)) {
            window.location.href = "dashboard.html";
            return;
        }
    }

    if (session) {
        // Universal User Info
        const welcomeName = document.getElementById("welcomeName");
        if (welcomeName) welcomeName.textContent = session.name;

        const roleTag = document.getElementById("roleTag");
        if (roleTag) {
            roleTag.textContent = session.role;
            roleTag.className = `badge ${session.role === 'CITIZEN' ? 'badge-pending' : 'badge-resolved'}`;
        }

        // Dynamic Sidebar Injection
        const nav = document.getElementById("dynamicNav");
        if (nav) {
            let links = [];
            if (session.role === 'AUTHORITY') {
                links = [
                    { href: "dashboard.html", icon: "th-large", text: "Dashboard" },
                    { href: "authority-complaints.html", icon: "list-check", text: "Manage Reports" },
                    { href: "notifications.html", icon: "bell", text: "Notifications" },
                    { href: "profile.html", icon: "user-circle", text: "Profile" },
                    { href: "help.html", icon: "circle-info", text: "Help & Support" }
                ];
            } else if (session.role === 'SUPERVISOR') {
                links = [
                    { href: "dashboard.html", icon: "th-large", text: "Dashboard" },
                    { href: "supervisor-complaints.html", icon: "list-check", text: "Work Queue" },
                    { href: "notifications.html", icon: "bell", text: "Notifications" },
                    { href: "profile.html", icon: "user-circle", text: "Profile" },
                    { href: "help.html", icon: "circle-info", text: "Help & Support" }
                ];
            } else {
                links = [
                    { href: "dashboard.html", icon: "th-large", text: "Dashboard" },
                    { href: "report.html", icon: "plus-circle", text: "New Report" },
                    { href: "my-reports.html", icon: "file-alt", text: "My Reports" },
                    { href: "notifications.html", icon: "bell", text: "Notifications" },
                    { href: "profile.html", icon: "user-circle", text: "Profile" },
                    { href: "help.html", icon: "circle-info", text: "Help & Support" }
                ];
            }

            nav.innerHTML = links.map(link => `
                <a href="${link.href}" class="nav-link ${currentPage === link.href ? 'active' : ''}">
                    <i class="fas fa-${link.icon}"></i> ${link.text}
                </a>
            `).join('') + `
                <a href="#" class="nav-link" id="btnLogout" style="margin-top: 2rem; color: var(--accent-red);">
                    <i class="fas fa-sign-out-alt"></i> Logout
                </a>
            `;

            // Re-bind logout after injection
            document.getElementById("btnLogout").onclick = () => Session.clear();
        }
    }

    // Specific Page Logic
    if (currentPage === "dashboard.html") initDashboard(session);
    if (currentPage === "my-reports.html") initMyReports(session);
    if (currentPage === "authority-complaints.html") initAuthorityView(session);
    if (currentPage === "supervisor-complaints.html") initSupervisorView(session);
    if (currentPage === "profile.html") initProfile(session);
    if (currentPage === "notifications.html") initNotifications(session);
});

// Explicitly export to window for access from HTML inline scripts
window.Api = Api;
window.Session = Session;
window.UI = UI;

async function initDashboard(user) {
    try {
        await loadComplaints(user, "complaintsList");
        // Additional stats logic
        const complaints = await Api.get("complaints/");
        document.getElementById("statTotal").textContent = complaints.length;
        document.getElementById("statPending").textContent = complaints.filter(c => c.status === 'Pending').length;
        const processingCount = complaints.filter(c => c.status === 'Processing' || c.status === 'In Progress').length;
        const statProcElement = document.getElementById("statProcessing");
        if (statProcElement) statProcElement.textContent = processingCount;
        document.getElementById("statResolved").textContent = complaints.filter(c => c.status === 'Resolved' || c.status === 'Fixed').length;
    } catch (error) {
        console.error("Dashboard init error:", error);
    }
}

async function loadComplaints(user, containerId, filter = {}) {
    const listContainer = document.getElementById(containerId);
    if (!listContainer) return;

    try {
        const complaints = await Api.get("complaints/", filter);
        listContainer.innerHTML = "";

        if (complaints.length === 0) {
            listContainer.innerHTML = '<div class="text-center p-4">No reports found</div>';
            return;
        }

        complaints.forEach(item => {
            const card = document.createElement("div");
            card.className = "card animate-fade-in mb-3";
            let displayStatus = item.status;
            if (displayStatus === 'In Progress') displayStatus = 'Processing';
            const statusClass = displayStatus.toLowerCase().replace(/\s+/g, '-');

            const proofBlock = item.proof ? `
                <div style="margin-top: 15px; padding: 12px; background: #e8f5e9; border-radius: 12px; border: 1px dashed #4caf50; cursor: pointer; transition: background 0.2s;" onclick="event.stopPropagation(); window.UI.showImageModal('${item.proof}')" onmouseover="this.style.background='#f1f8f1'" onmouseout="this.style.background='#e8f5e9'">
                    <div style="margin-bottom: 8px;">
                        <p style="font-size: 0.8rem; font-weight: 700; color: #2e7d32; margin: 0; display: inline-block;">
                            <i class="fas fa-check-double"></i> Resolution Proof
                        </p>
                    </div>
                    <div style="width: 100%; height: 160px; border-radius: 8px; overflow: hidden; background: #000; position: relative;">
                        <img data-src="${item.proof}" style="width: 100%; height: 100%; object-fit: contain; position: relative; z-index: 5;">
                        <div style="position: absolute; bottom: 5px; right: 5px; background: rgba(0,0,0,0.5); color: #fff; padding: 2px 8px; border-radius: 4px; font-size: 0.7rem;"><i class="fas fa-expand"></i> Click to enlarge</div>
                    </div>
                </div>
            ` : '';

            card.innerHTML = `
                <div style="display: flex; gap: 15px;">
                    <div style="width: 80px; height: 80px; border-radius: 12px; overflow: hidden; background: #000; flex-shrink: 0; position: relative;">
                        ${item.image ? `<img data-src="${item.image}" style="width: 100%; height: 100%; object-fit: contain; cursor: pointer; position: relative; z-index: 5;" onclick="event.stopPropagation(); window.UI.showImageModal('${item.image}')">` : '<i class="fas fa-image" style="display: block; text-align: center; margin-top: 30px; color: #ccc;"></i>'}
                    </div>
                    <div style="flex: 1;">
                        <h4 style="margin-bottom: 5px;">${item.title || "Complaint"}</h4>
                        <p style="font-size: 0.75rem; font-weight: 600; color: var(--primary-blue); margin-bottom: 5px;">SRC Number: ${UI.formatId(item.report_id)}</p>
                        <p style="font-size: 0.85rem; color: var(--text-muted); margin-bottom: 8px;">${item.description || "No description provided."}</p>
                        <div style="display: flex; align-items: center; gap: 10px; font-size: 0.8rem; color: var(--text-muted);">
                            <span><i class="fas fa-map-marker-alt"></i> ${item.zone || "Chennai"}</span>
                            <span><i class="fas fa-calendar-alt"></i> ${new Date(item.created_at || Date.now()).toLocaleString([], { year: 'numeric', month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit' })}</span>
                        </div>
                        ${proofBlock}
                        ${item.rating || item.rating_description ? `
                            <div style="margin-top: 10px; padding: 10px; background: #FFFDE7; border-radius: 8px; border: 1px solid #FFF59D; font-size: 0.8rem;">
                                <div style="display: flex; align-items: center; justify-content: space-between; margin-bottom: 4px;">
                                    <span style="font-weight: 700; color: #F57F17;">Citizen Rating</span>
                                    <div style="color: #FBC02D;">
                                        ${item.rating ? '★'.repeat(item.rating) + '☆'.repeat(5-item.rating) : 'Unrated'}
                                    </div>
                                </div>
                                ${item.rating_description ? `<p style="margin: 0; font-style: italic; color: #455A64;">"${item.rating_description}"</p>` : ''}
                            </div>
                        ` : ''}
                        <div style="margin-top: 10px; display: flex; justify-content: space-between; align-items: center;">
                            <div style="display: flex; flex-direction: column; gap: 2px;">
                                <div style="display: flex; align-items: center; gap: 8px;">
                                    <span class="badge badge-${statusClass}">${displayStatus}</span>
                                    ${item.authority_name ? `<span style="font-size: 0.7rem; color: #888;">${item.status.toLowerCase() === 'processing' || item.status.toLowerCase() === 'in progress' ? 'Taken by' : 'Resolved by'} <strong>${item.authority_name}</strong></span>` : ''}
                                </div>
                                ${item.supervisor_name ? `<span style="font-size: 0.65rem; color: #999; margin-left: 4px;">Evidence via: <strong>${item.supervisor_name}</strong></span>` : ''}
                            </div>
                            <span style="font-size: 0.8rem; font-weight: 600; color: ${item.priority === 'High' ? 'var(--accent-red)' : 'var(--primary-blue)'}">${item.priority || 'Normal'}</span>
                        </div>
                    </div>
                </div>
            `;
            listContainer.appendChild(card);
            if (item.image) {
                const img = card.querySelector('img[data-src="' + item.image + '"]');
                if (img) UI.loadImage(item.image, img);
            }
            if (item.proof) {
                const img = card.querySelector('img[data-src="' + item.proof + '"]');
                if (img) UI.loadImage(item.proof, img);
            }
        });
    } catch (error) {
        listContainer.innerHTML = '<div class="text-center p-4 text-danger">Error loading data</div>';
    }
}

// Login & Global Actions
document.addEventListener("click", async (e) => {
    // Login Logic
    if (e.target && e.target.id === "btnLogin") {
        console.log("DEBUG: Login button clicked");
        const identifier = document.getElementById("identifier").value;
        const password = document.getElementById("password").value;
        const btn = e.target;

        if (!identifier || !password) return alert("Please enter credentials");

        try {
            UI.showLoading(btn, "Connecting...");
            console.log("DEBUG: Attempting API POST to login/ for", identifier);
            
            const activeTab = document.querySelector('.tab.active');
            const role = activeTab ? activeTab.dataset.role : 'CITIZEN';

            const authData = await Api.post("login/", {
                email: identifier,
                password: password,
                role: role
            });

            console.log("DEBUG: Login successful, saving session", authData);
            Session.save(authData);

            console.log("DEBUG: Redirecting to dashboard.html");
            window.location.href = "dashboard.html";
        } catch (error) {
            console.error("DEBUG: Login handler error:", error);
            alert(error.message);
            UI.hideLoading(btn);
        }
    }

    // Logout Logic
    if (e.target && e.target.id === "btnLogout") {
        e.preventDefault();
        Session.clear();
    }
});
