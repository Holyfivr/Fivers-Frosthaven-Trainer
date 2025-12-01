package se.holyfivr.trainer.service;

import java.nio.file.Path;

import org.springframework.stereotype.Component;

@Component
public class State {

    private final Path hardCodedPath = Path.of("src/main/resources/ruleset/Base.ruleset"); // ONLY DURING DEV PHASE
    public Path getHardcodedPath(){
        return hardCodedPath; // CHANGE TO RULESETPATH WHEN PROPER GUI IS IMPLEMENTED
    }

    private Path rulesetPath;
    public Path getRulesetPath() {
        return rulesetPath;
    }

    public void setRulesetPath(Path rulesetPath){
        this.rulesetPath = rulesetPath;
    }
}
