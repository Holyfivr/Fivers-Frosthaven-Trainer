package se.holyfivr.trainer.core.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/* Covers the YAML comment rule used when the saver steals bytes from comments: */
/* only a '#' at line start or after a space/tab opens a comment. A glued '#',  */
/* like the colour in "<mark=#6EBE4922>" inside card text, must never be eaten. */
class SaveUtilsTest {

    private final SaveUtils saveUtils = new SaveUtils();

    @Test
    void cutsACommentLineAtItsSecondHash() {
        String block = "Name: x\r\n            #         #UseControllingActorPlayerControl: True\r\nID: 1";
        assertEquals("Name: x\r\n            #         \nID: 1", saveUtils.stripDoubleHashComments(block));
    }

    @Test
    void doesNotTreatAGluedHashAsACommentStart() {
        // The '#' in the colour is content; the comment starts at " #" and is cut at its second '#'
        String line = "- Row: \"<mark=#6EBE4922><sprite name=Range>5</mark>\" # todo # localize";
        assertEquals("- Row: \"<mark=#6EBE4922><sprite name=Range>5</mark>\" # todo ",
                saveUtils.stripDoubleHashComments(line));
    }

    @Test
    void leavesALineWithOnlyGluedHashesUntouched() {
        String line = "- Row: \"<mark=#6EBE4922>a</mark> <mark=#80919922>b</mark>\"";
        assertEquals(line, saveUtils.stripDoubleHashComments(line));
    }

    @Test
    void commentTrimPatternStealsFromACommentAndSkipsCardText() {
        String block = "- Row: \"<mark=#6EBE4922>x\"\r\n# PlayerLevel: [1, 2]\r\n";
        String trimmed = PatternRepository.COMMENT_TRIM_PATTERN.matcher(block).replaceFirst("$1");
        assertEquals("- Row: \"<mark=#6EBE4922>x\"\r\n#PlayerLevel: [1, 2]\r\n", trimmed);
    }

    @Test
    void commentTrimPatternChangesNothingWhenOnlyGluedHashesRemain() {
        String block = "- Row: \"<mark=#6EBE4922>x\"\r\n";
        assertEquals(block, PatternRepository.COMMENT_TRIM_PATTERN.matcher(block).replaceFirst("$1"));
    }
}
