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

function showSuccessMessage(action) {
    let message = "";
    switch (action) {
        case "fileLoaded":
            const rulesetLoadedCheck = document.getElementById("rulesetLoadedCheck");
            if (rulesetLoadedCheck?.value === "true") message = "Ruleset Loaded Successfully!";
            break;
        case "backupCreated": message = "Backup Created Successfully!"; break;
        case "backupRestored": message = "Original Backup Restored!"; break;
        case "rulesetSaved": message = "Ruleset Saved Successfully!"; break;
        case "linkCopied": message = "Link Copied to Clipboard!"; break;
        case "hpMaxed": message = "All Characters Max HP Set to 99!"; break;
        case "cardsMaxed": message = "All Characters Max Cards Set to 20!"; break;
        case "charactersEnabled": message = "Characters Enabled from Start!"; break;
        case "characterSaved": message = "Character Saved Successfully!"; break;
        case "itemUpdated": message = "Items Updated Successfully!"; break;
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

// ==========================================
//  ITEM MODAL HELPER
// ==========================================

/**
 * Opens the item form with specific configuration.
 * @param {string} action - The action string (e.g. 'setGoldCost').
 * @param {string} labelText - Text to display in the header (e.g. 'Gold Cost').
 */
function openItemModal(action, labelText) {
    openModal("itemFormTemplate", (container) => {
        // Set Title
        const modalTitle = container.querySelector("#itemModalTitle");
        if (modalTitle) modalTitle.innerText = `Set ${labelText} for all items`;

        // Set Hidden Action Input (required for controller)
        const actionInput = container.querySelector("#actionInput");
        if (actionInput) actionInput.value = action;
        
        // Configure Value Input based on action
        const valInput = container.querySelector("#itemModalInput");
        if (valInput) {
            valInput.value = ""; // Reset
            
            // Logic for specific attributes
            if (action === "setProsperityReq") {
                valInput.placeholder = "0-9";
                valInput.max = "9";
                valInput.min = "0";
            } 
            else if (action === "setTotalInGame") {
                valInput.placeholder = "1-9";
                valInput.max = "9";
                valInput.min = "1";
            }
            else if (action === "setGoldCost") {
                valInput.placeholder = "1-200";
                valInput.max = "200";
                valInput.min = "1";
            } 
            else if (action === "setUsage") {
                // Special case: Usage is text, and we might default to "Unrestricted"
                valInput.type = "text"; 
                valInput.value = "Unrestricted";
                // If we want to hide it completely (since it's just setting to 'Unrestricted'):
                valInput.style.display = "none";
            }
            else {
                // Default numbers
                valInput.placeholder = "1-99";
                valInput.max = "99";
                valInput.min = "1";
            }

            // Prevent invalid chars for number inputs
            if (valInput.type === "number") {
                valInput.onkeydown = function(e) {
                    if (e.key === '-' || e.key === 'e') e.preventDefault();
                }
            }
        }

        //  Attach submit listener to close modal automatically
        const form = container.querySelector("form");
        if (form) {
            form.addEventListener("submit", () => {
                closeModal();
                showSuccessMessage('itemUpdated');
            });
        }
    });
}