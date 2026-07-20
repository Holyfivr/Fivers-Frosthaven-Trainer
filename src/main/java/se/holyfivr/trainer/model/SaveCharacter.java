package se.holyfivr.trainer.model;

import java.util.ArrayList;
import java.util.List;

/* ======================================================================== */
/* Represents one character record found in a save file (.dat).             */
/* Holds the character's name and the list of editable fields (gold and     */
/* resources) that were located inside that character's record.             */
/* ======================================================================== */
public class SaveCharacter {

    private final String name;
    private final List<SaveField> fields = new ArrayList<>();

    public SaveCharacter(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public List<SaveField> getFields() {
        return fields;
    }

    public void addField(SaveField field) {
        fields.add(field);
    }
}
