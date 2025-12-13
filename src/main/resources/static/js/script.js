const confirmationModal = document.getElementById("confirmationModal");
const itemModalInput = document.getElementById("itemModalInput");

// Function to handle pending actions (save to storage)
// this is used when redirecting after an action to show a success message
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
        case "hpMaxed":
            message = "All Characters Max HP Set to 99!";
            break;
        case "cardsMaxed":
            message = "All Characters Max Cards Set to 20!";
            break;
        case "charactersEnabled":
            message = "Characters Enabled from Start!";
            break;
        case "characterSaved":
            message = "Character Saved Successfully!";
            break;
        case "itemUpdated":
            message = "Items Updated Successfully!";
            break;
    }

    // Display the message in the modal
    if (message) {
        confirmationModal.innerHTML = `<h3>${message}</h3>`;
        confirmationModal.style.opacity = "1";
        setTimeout(() => {
            confirmationModal.style.opacity = "0";
        }, 2000);
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
        itemModal.style.opacity = "0";
        itemModal.style.pointerEvents = "none";
    }
}

// On Load: Check for pending actions and show success messages if there are any
const pendingAction = sessionStorage.getItem("pendingAction");
if (pendingAction) {
    showSuccessMessage(pendingAction);
    sessionStorage.removeItem("pendingAction");
}

const itemModal = document.getElementById("itemModal");
const itemModalTitle = document.getElementById("itemModalTitle");
const actionInput = document.getElementById("actionInput");

function openItemModal(action, type){
    
    itemModal.style.opacity = "1";
    itemModal.style.pointerEvents = "all";
    itemModalTitle.innerText = `Set ${type} for all items`;
    actionInput.value = action;
    itemModalInput.value = "";
}

// Handle item modal submission
const itemModalForm = itemModal.querySelector("form");
if (itemModalForm) {
    itemModalForm.addEventListener('submit', function() {
        // The submit event only fires if the form is valid.
        // We close the modal immediately so the user sees the result in the iframe.

        closeModal();
        showSuccessMessage('itemUpdated')
    });
}