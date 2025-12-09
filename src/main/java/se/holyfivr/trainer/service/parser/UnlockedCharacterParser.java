package se.holyfivr.trainer.service.parser;

import org.springframework.stereotype.Component;

import se.holyfivr.trainer.service.ActiveSessionData;

@Component
public class UnlockedCharacterParser {

    private final ActiveSessionData activeSessionData;

    
    public UnlockedCharacterParser(ActiveSessionData activeSessionData) {
        this.activeSessionData = activeSessionData;
    }

    public void parseGameModeBlock(String currentBlock) {       
        String[] currentBlockAsLines = currentBlock.split("\n");
        for (String line : currentBlockAsLines) {
            line = line.trim();
            if (line.equalsIgnoreCase("Name: FrostHaven")) {
               parseCharacterArray(currentBlock);
            }
        }
    }

    public void parseCharacterArray(String currentBlock) {
        String[] currentBlockAsLines = currentBlock.split("\n");

        for (String currentLine : currentBlockAsLines) {
            if (currentLine.startsWith("UnlockedClasses:")) {
                int start = currentLine.indexOf('[') + 1;
                int end = currentLine.indexOf(']');

                if (start > 1 && end > start) {
                    currentLine = currentLine.substring(start, end);
                    String[] characterArray = currentLine.split(",");

                    for (String character : characterArray) {
                        activeSessionData.addUnlockedCharacter(character);
                    }
                }
            }
        }
    }


}
