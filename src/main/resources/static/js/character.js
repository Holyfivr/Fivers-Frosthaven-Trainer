
const hpFields = document.querySelectorAll(".hp-field");
const abilityCardAmount = document.getElementById("maxAbilityCard");
function maxAllValues(event){
    event.preventDefault();
    for (const field of hpFields){
        field.value = "99";
    }
    abilityCardAmount.value = "20";
}

