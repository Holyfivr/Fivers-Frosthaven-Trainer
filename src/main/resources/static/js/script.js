// ==========================================
//  TOAST NOTIFICATIONS
// ==========================================

let toastTimeout;

function showToast(message, isError = false) {
    const toast = document.getElementById("toastNotification");
    if (!toast) return;

    // Clear existing timer if a new toast is shown immediately
    if (toastTimeout) clearTimeout(toastTimeout);

    toast.innerText = message;
    
    // Style adjustments for error vs success
    if (isError) {
        toast.style.backgroundColor = "#d32f2f"; // Red
    } else {
        toast.style.backgroundColor = "#234C6A"; // Default Blue
    }

    // Show
    toast.style.opacity = "1";
    
    // Hide after 3 seconds
    toastTimeout = setTimeout(() => {
        toast.style.opacity = "0";
    }, 3000);
}

function registerPendingAction(actionName) {
    sessionStorage.setItem("pendingAction", actionName);
}

function showLoadSuccessfulMessage() {
    const characterMenu = document.getElementById("characterMenu");
    if (!characterMenu.classList.contains("inactive")) {
        showToast("File Loaded Successfully!");
    }
}

function showSuccessMessage(action) {
    let message = "";
    switch (action) {
        case "fileLoaded":
            const rulesetLoadedCheck = document.getElementById("rulesetLoadedCheck");
            if (rulesetLoadedCheck?.value === "true") {
                showLoadSuccessfulMessage();
                return;
            }
            return;
        case "backupCreated"    : message = "Backup Created Successfully!"       ; break;
        case "backupRestored"   : message = "Original Backup Restored!"          ; break;
        case "backupReplaced"   : message = "New Original Backup Created!"       ; break;
        case "rulesetSaved"     : message = "Ruleset Saved Successfully!"        ; break;
        case "linkCopied"       : message = "Link Copied to Clipboard!"          ; break;
        case "hpMaxed"          : message = "All Characters Max HP Set to 99!"   ; break;
        case "cardsMaxed"       : message = "Maxed out Available Abilitycards!"  ; break;
        case "charactersEnabled": message = "Characters Enabled from Start!"     ; break;
        case "characterSaved"   : message = "Character Saved Successfully!"      ; break;
        case "itemUpdated"      : message = "Items Updated Successfully!"        ; break;
        }

    if (message) {
        showToast(message);
    }
}

// Check on load
const pendingAction = sessionStorage.getItem("pendingAction");
if (pendingAction) {
    showSuccessMessage(pendingAction);
    sessionStorage.removeItem("pendingAction");
}

// ==========================================
//  UNIVERSAL MODAL LOGIC
// ==========================================

const universalModal = document.getElementById("universalModal");
const injectedContent = document.getElementById("injectedContent");

/**
 * Opens a modal by cloning a template.
 * @param {string} templateId - ID of the <template> to use.
 * @param {function} [setupCallback] - Optional function to run after content is injected (for setting values).
 */
function openModal(templateId, setupCallback) {
    const template = document.getElementById(templateId);
    if (!template || !universalModal) return;

    // Clear previous content
    injectedContent.innerHTML = "";
    
    // Clone and inject
    const clone = template.content.cloneNode(true);
    injectedContent.appendChild(clone);

    // Run custom setup logic (e.g. setting input values)
    if (typeof setupCallback === 'function') {
        setupCallback(injectedContent);
    }

    // Show modal
    universalModal.style.display = "flex";
    
    // Use requestAnimationFrame to ensure the browser paints 'display: flex' 
    // before applying the opacity transition.
    // We need two frames: one to apply the display change, and another to apply the opacity change.
    requestAnimationFrame(() => {
        requestAnimationFrame(() => {
            if (universalModal) universalModal.style.opacity = "1";
        });
    });
}

function closeModal() {
    if (!universalModal) return;
    universalModal.style.opacity = "0";
    setTimeout(() => {
        universalModal.style.display = "none";
        injectedContent.innerHTML = ""; // Clean up
    }, 300);
}

// Close when clicking outside the content box
window.onclick = function(event) {
    if (event.target === universalModal) {
        closeModal();
    }
}


/* ================================== */
/*  SPECIFIC MODAL OPENING FUNCTIONS  */
/* ================================== */
function openEditAllItemsModal() {
    openModal("editAllItemsTemplate", (container) => {
        container.querySelectorAll("input").forEach(input => {
            if (input.type !== "hidden") {
                input.value = "";
            }
        });

        const form = container.querySelector("#editAllItemsForm");
        if (form) {
            form.addEventListener("submit", () => {
                closeModal();
                showToast("Items Updated Successfully!");
            });
        }
    });
}

function openEditAllCardsModal() {
    openModal("editAllCardsTemplate", (container) => {
        container.querySelectorAll("input").forEach(input => {
            if (input.type !== "hidden" && input.type !== "checkbox") {
                input.value = "";
            }
        });

        const altToggle = container.querySelector("#altInitiativeToggle");
        const altInput = container.querySelector("#altInitiativeInput");
        const initInput = container.querySelector("#initiativeInput");
        if (altToggle && altInput && initInput) {
            altToggle.checked = false;
            altInput.disabled = true;
            altToggle.addEventListener("change", () => {
                altInput.disabled = !altToggle.checked;
                if (altToggle.checked) {
                    initInput.required = true;
                    altInput.required = true;
                } else {
                    initInput.required = false;
                    altInput.required = false;
                    altInput.value = "";
                }
            });
        }

        const form = container.querySelector("#editAllCardsForm");
        if (form) {
            form.addEventListener("submit", () => {
                closeModal();
                showToast("Cards Updated Successfully!");
            });
        }
    });
}

// ==========================================
//  SIZE MISMATCH WARNING
// ==========================================
// If the opened ruleset file differs in size from the original backup,
// auto-open the warning modal so the user can decide what to do.
// Placed at the end so openModal and the modal elements are already defined.
const sizeMismatchCheck = document.getElementById("sizeMismatchCheck");
if (sizeMismatchCheck?.value === "true") {
    openModal("sizeMismatchTemplate");
}

// ==========================================
//  ACCORDIONS (troubleshooting page)
// ==========================================
// Toggles a single accordion open/closed when its header is clicked.
function toggleAccordion(header) {
    header.classList.toggle("open");
    const panel = header.nextElementSibling;
    if (panel) panel.classList.toggle("open");
}

// Opens a specific accordion by id and scrolls to it.
// Used by cross-reference links between sections.
function openAccordion(id) {
    const accordion = document.getElementById(id);
    if (!accordion) return;
    const header = accordion.querySelector(".accordion-header");
    const panel = accordion.querySelector(".accordion-panel");
    if (header) header.classList.add("open");
    if (panel) panel.classList.add("open");
    accordion.scrollIntoView({ behavior: "smooth", block: "start" });
}

document.querySelectorAll(".accordion-header").forEach(header => {
    header.addEventListener("click", () => toggleAccordion(header));
});