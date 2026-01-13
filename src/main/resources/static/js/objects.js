
/* ============================================================================================ */
/*                                 DYNAMIC CONDITION FIELDS                                     */
/* This is disabled for now, as this cannot be safely used without corrupting the file on many  */
/* of the items. This will be dealt with in the future, when I update the filler-bank code to   */
/* use space from other blocks. That might solve the problem, but for now we leave it disabled. */
/* ============================================================================================ */

/* document.addEventListener('DOMContentLoaded', function() {
    const addConditionBtn = document.getElementById('addCondition');
    
    if (addConditionBtn) {
        addConditionBtn.addEventListener('click', function() {
            addConditionField();
        });
    }
});

function addConditionField() {

    // find the row that contain conditions
    const conditionRow = document.getElementById('addCondition').closest('tr');
    const tableBody = conditionRow.parentElement;
    
    // counts how many condition input fields are already present (to enforce max limit)
    const currentConditions = tableBody.querySelectorAll('.condition-row').length;
    if (currentConditions >= 5) {
        alert("Maximum of 5 conditions allowed.");
        return;
    }

    // create new row
    const newRow = document.createElement('tr');
    newRow.className = 'condition-row';
    
    // create label and input cells
    const labelCell = document.createElement('td');
    labelCell.className = 'object-attribute-td';
    labelCell.textContent = ''; 
    
    const inputCell = document.createElement('td');
    
    // Create select element instead of input
    const select = document.createElement('select');
    select.name = 'conditions';
    
    // Define the options
    const conditions = [
        "Muddle", "Stun", "Poison", "Immobilize", "Disarm", 
        "Curse", "Bless", "Strengthen", "Invisible", 
        "Brittle", "Bane", "Impair"
    ];
    
    // Create and append options
    conditions.forEach(condition => {
        const option = document.createElement('option');
        option.value = condition;
        option.textContent = condition;
        select.appendChild(option);
    });
    
    // Add a "Remove" button for the new row
    const removeBtn = document.createElement('span');
    removeBtn.innerHTML = '&nbsp;<b>-</b>';
    removeBtn.style.cursor = 'pointer';
    removeBtn.style.color = 'red';
    removeBtn.onclick = function() {
        removeConditionRow(this);
    };

    inputCell.appendChild(select);
    inputCell.appendChild(removeBtn);
    
    newRow.appendChild(labelCell);
    newRow.appendChild(inputCell);
    
    // insert the new row before the button row
    conditionRow.parentNode.insertBefore(newRow, conditionRow);
}

function removeConditionRow(element) {
    const row = element.closest('tr');
    const parent = row.parentNode;
    // Count only the select elements named 'conditions' to avoid counting the 'add' row or others
    const conditionSelects = parent.querySelectorAll('select[name="conditions"]');
    
    if (conditionSelects.length <= 1) {
        alert("At least one condition is required.");
        return;
    }
    
    row.remove();
}
 */
/* ============================================================================================ */
/*                                      SEARCH FUNCTIONALITY                                    */
/* ============================================================================================ */

document.addEventListener('DOMContentLoaded', function() {
    const searchInput = document.getElementById('searchInput');
    
    if (searchInput) {
        searchInput.addEventListener('input', function() {
            const filter = searchInput.value.toLowerCase();
            const container = document.getElementById('objectListContainer');
            const rows = container.getElementsByTagName('tr');

            for (let i = 0; i < rows.length; i++) {
                const div = rows[i].getElementsByTagName('div')[0];
                if (div) {
                    const txtValue = div.textContent || div.innerText;
                    if (txtValue.toLowerCase().indexOf(filter) > -1) {
                        rows[i].style.display = "";
                    } else {
                        rows[i].style.display = "none";
                    }
                }       
            }
        });
    }

    /* Makes sure clicked items stay selected */
    const objectListContainer = document.getElementById('objectListContainer');
    if (objectListContainer) {
        objectListContainer.addEventListener('click', function(e) {
            const td = e.target.closest('td');
            if (td) {
                // Clear previous selection
                objectListContainer.querySelectorAll('.selected').forEach(element => element.classList.remove('selected'));
                // Set new selection
                td.classList.add('selected');
            }
        });
    }
});

// Disable infusion and consume selectors if set to 'Any'
// If you change from 'Any' to a specific element, the item stops working
// Also, why would you want to do that?
const infusionSelector = document.getElementById('infusion');
if (infusionSelector) {
    
        const infuseValue = infusionSelector.value;
        if (infuseValue === 'Any') {
            infusionSelector.disabled = true;
        }

    }
const consumeSelector = document.getElementById('consume');
if (consumeSelector) {
    
        const consumeValue = consumeSelector.value;
        if (consumeValue === 'Any') {
            consumeSelector.disabled = true;
        }

    }

