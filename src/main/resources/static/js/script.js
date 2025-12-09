const confirmationModal = document.getElementById("confirmationModal");

// Function to handle pending actions (save to storage)
function registerPendingAction(actionName) {
    sessionStorage.setItem("pendingAction", actionName);
}

// Function to handle what to show in the confirmation modal (Success messages)
function showSuccessMessage(action) {
    let message = "";
    switch(action) {
        case "fileLoaded":
            { const rulesetLoadedCheck = document.getElementById("rulesetLoadedCheck");
            
            // Check rulesetLoadedCheck if needed, or just trust the action
            // Set message based on rulesetLoadedCheck value
            if (rulesetLoadedCheck?.value === "true") message = "Ruleset Loaded Successfully!";
            break; }
        case "backupCreated":
            message = "Backup Created Successfully!";
            break;
        case "backupRestored":
            message = "Original Backup Restored!";
            break;
        case "rulesetSaved":
            message = "Ruleset Saved Successfully!";
            break;
        case "linkCopied":
            message = "Link Copied to Clipboard!";
            break;
    }

    // Display the message in the modal
    if (message) {
        confirmationModal.innerHTML = `<h3>${message}</h3>`;
        confirmationModal.style.opacity = "1";
        setTimeout(() => {
            confirmationModal.style.opacity = "0";
        }, 3000);
    }
}

// Function to handle opening input modals (Backup Form / Save Confirm / etc.)
function openModal(templateId) {
    const template = document.getElementById(templateId);
    if (template && confirmationModal) {
        // If already open with same content, close it
        if (confirmationModal.style.opacity === "1" && confirmationModal.dataset.activeTemplate === templateId) {
            closeModal();
            return;
        }

        confirmationModal.innerHTML = template.innerHTML;
        confirmationModal.style.opacity = "1";
        confirmationModal.style.pointerEvents = "all";
        confirmationModal.dataset.activeTemplate = templateId;
    }
}

function closeModal() {
    if (confirmationModal) {
        confirmationModal.style.opacity = "0";
        confirmationModal.style.pointerEvents = "none";
        confirmationModal.dataset.activeTemplate = "";
    }
}

// On Load: Check for pending actions
const pending = sessionStorage.getItem("pendingAction");
if (pending) {
    showSuccessMessage(pending);
    sessionStorage.removeItem("pendingAction");
}