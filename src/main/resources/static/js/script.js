const maxCharacterValuesModal = document.getElementById("maxCharacterValuesModal");
const maxCards = document.getElementById("maxCards");
const maxHp = document.getElementById("maxHp");

// Function to toggle the main modal visibility
// Used by "Nevermind" and "I am sure" buttons to close the modal
function maxAllCharacters() {
    // We simply close it here, as the opening is handled by specific buttons
    maxCharacterValuesModal.style.opacity = "0";
    maxCharacterValuesModal.style.pointerEvents = "none";
    
    // Reset children visibility
    maxCards.style.display = "none";
    maxHp.style.display = "none";
}

const openMaxCards = document.getElementById("openMaxCards");
if (openMaxCards) {
    openMaxCards.addEventListener("click", function() {
        // Check if this specific modal is already open
        if (maxCharacterValuesModal.style.opacity === "1" && maxCards.style.display === "block") {
            // It's open, so close it
            maxAllCharacters();
        } else {
            // It's closed (or the other one is open), so open this one
            maxCharacterValuesModal.style.opacity = "1";
            maxCharacterValuesModal.style.pointerEvents = "auto";

            maxCards.style.display = "block";
            maxHp.style.display = "none";
        }
    });
}

const openMaxHp = document.getElementById("openMaxHp");
if (openMaxHp) {
    openMaxHp.addEventListener("click", function() {
        // Check if this specific modal is already open
        if (maxCharacterValuesModal.style.opacity === "1" && maxHp.style.display === "block") {
            // It's open, so close it
            maxAllCharacters();
        } else {
            // It's closed (or the other one is open), so open this one
            maxCharacterValuesModal.style.opacity = "1";
            maxCharacterValuesModal.style.pointerEvents = "auto";

            maxHp.style.display = "block";
            maxCards.style.display = "none";
        }
    });
}

