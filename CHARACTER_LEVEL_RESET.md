# Reset character card progression

This feature reopens completed level-up **card choices** in a campaign save.
It does not reduce the character's displayed level or change XP, items, perks,
or other progression. For a level-4 character, “reset to level 1” banks three
choices (levels 2–4); “reset to level 2” banks two (levels 3–4). Existing
pending choices remain pending. The game offers the choices in level order
when the edited save is loaded.

## Use and safety

Close Frosthaven, copy the exact save to a safe location outside the campaign
folder, and open that `.dat` in the trainer. On **Reset Character Levels**,
choose a character and a target level, confirm **Reset**, then **Save & Close**.
Load the edited save, make the offered choices, save in-game, and reload once
to confirm. The trainer's adjacent `.bak` is created only the first time the
file is opened; it may be stale. Additional `.dat` files in the campaign
folder can cause the game to load the wrong one. Steam Cloud may overwrite
edited saves.

The editor shows only characters and target levels it can infer from the save
and its bundled card catalog. If a level-4 character chose a spare level-2
card at level 3, for example, the save may not reveal *which* level awarded
which level-2 card. “Reset to level 2” is then hidden, while “reset to level 1”
can still be safe because both cards are removed. If no option appears, do
not edit the binary by guessing.

## Mechanism and evidence

The save stores owned cards in `SelectedCardIDs` (hand) and `UnusedCardIDs`,
plus a count of unspent choices in `CardUnlocks`. Completing a choice adds a
card and decreases `CardUnlocks`. The game-tested Bannerspear trials showed
that removing the chosen card, restoring an unused starter to the hand if
needed, increasing `CardUnlocks`, and adjusting five enclosing lengths
reopens the choice. Reversing levels 2–4 together caused the game to offer
three choices in order; the resulting game-written save reloaded successfully.
Reducing `Level` alone did not trigger a choice.

The generalized editor uses [card-level.properties](src/main/resources/card-level.properties),
generated from the installed game's `Base.ruleset`, to identify starter cards
and possible level-up awards by class. It enumerates legal assignments of
owned non-starter cards to completed levels; a target is offered only if every
legal assignment removes the same set. It also verifies all starter cards,
owned-card counts, unique IDs, and a starter available for each hand slot to
replace. It reindexes `UnusedCardIDs`, updates `CardUnlocks`, then adjusts the
unused-list length, character length, `AllCharacters` length, and two outer
lengths. The transformed save is parsed again before it can be saved.

The bundled catalog can be regenerated after a game update with
`python3 scripts/generate-card-levels.py /path/to/Base.ruleset`. A catalog
from a different game version may cause valid saves to be rejected; do not
force edits around that validation. The Bannerspear level-2–4 operation has
been tested in-game; behavior for other classes and higher levels is inferred
from the same save structure and still needs in-game verification. Always
retain a separate pre-edit backup for recovery.
