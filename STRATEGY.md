# <center> Strategy for Editing the Base.ruleset File</center>

## Core Plan

The goal is to modify specific data objects (characters, items, etc.) within the `Base.ruleset` while maintaining the exact total file size. This is achieved by parsing the file into Java objects, allowing the user to edit them via a UI, and then re-serializing these objects back into their YAML-like text representation. We use a **"Filler Bank"** strategy to ensure every single block preserves its original byte size.
<hr>

## Detailed Technical Strategy

The primary challenge with editing `Base.ruleset` is that it is not a simple text file. The game engine relies on the file having a specific, constant size. A simple text edit that changes the file's length by even a single byte will effectively corrupt the file and prevent the game from loading.

The strategy is designed to mitigate this risk entirely by treating the file as a structured binary file and preserving its total size. Here’s how it works in detail:

1.  **Read as Raw Bytes:** The entire file is read into a `byte[]` array. It ensures that every single byte is preserved perfectly.
<br>
1.  **Segmentation:** Once in memory, the byte array is divided into three distinct segments:
    *   **Header:** A small, binary section at the beginning of the file.
    *   **Content:** A large, human-readable section containing the YAML-like game data definitions. This is the only section we intend to modify. It is approximately about 100,000 lines long.
    *   **Footer:** A massive, binary section at the very end of the file. Roughly 150,000 lines long.
<br>
1.  **The "Filler Bank" Principle:** We treat every single parsed block (e.g., a Character definition) as its own fixed-size bank.
    *   **Padding:** If the user's edits make a block *shorter* than the original, we add spaces to the end of the block until it matches the original length.
    *   **Trimming:** If the user's edits make a block *longer* than the original (e.g., increasing HP from 9 to 10), we must reclaim space. We do this by removing redundant characters in a prioritized order:
        1.  **Trailing Whitespace:** Removing invisible spaces at the end of lines.
        2.  **Carriage Returns:** Converting Windows line endings (`\r\n`) to Unix line endings (`\n`). This is invisible and saves 1 byte per line.
        3.  **Double Spaces:** Removing extra spaces in indentation (last resort).
<br>
1.  **Safe Reassembly and Writing:** After the user's changes are applied and every block has been adjusted to match its original size, the application reassembles the segments (`Header`, `New Content`, `Footer`) back into a single `byte[]` array.

Because every block perfectly compensates for any change in its content's length, the final reassembled byte array is **guaranteed to have the exact same size as the original file**. This allows us to modify the game data with maximum confidence.

<hr>

## Architecture & Services

### 1. Object Classes (POJOs)
*   **Goal:** Define Java classes that represent the various objects found in the ruleset file.
*   **Classes:** `PlayerCharacter.java`, `Item.java`, `AbilityCard.java`, etc.

### 2. Service Layer
The application logic is split into specialized services:

*   **`RulesetLoader`**: Loads the file, and splitting it into Header/Content/Footer, and finally passes it on to the `RulesetParser` and Saver.
*   **`RulesetParser`**: Responsible for parsing the text content. It uses Regex to extract data into Java objects (`PlayerCharacter`, etc.) and stores them in the session.
*   **`RulesetSaver`**: The most critical component. It reconstructs the file string from the Java objects. It implements the **Filler Bank** logic (padding/trimming) to ensure binary safety.
*   **`ActiveSessionData`**: A component that holds the in-memory state of the application (loaded characters, file path, etc.).

### 3. Spring Boot & Thymeleaf UI
*   **Structure:**
    *   **Web Interface:** The application runs as a local web server.
    *   **Thymeleaf Templates:** HTML files in `src/main/resources/templates` render the UI.
    *   **Controllers:** Spring MVC controllers handle user interactions (saving, editing) and communicate with the Service layer.

<hr>

## <center>Project Structure</center>
This structure reflects the current state of the project.

<pre>
┐<u><b>src</b></u>
├─┐<u><b>main</b></u>
│ └─┐<u><b>java</b></u>
│   └─┐<u><b>se</b></u>
│     └─┐<u><b>holyfivr</b></u>
│       └─┐<u><b>trainer</b></u>
│         ├ TrainerApplication.java
│         ├─┐<u><b>controller</b></u>
│         │ └ StartController.java
│         ├─┐<u><b>model</b></u>
│         │ ├ PlayerCharacter.java
│         │ ├ Item.java
│         │ ├ AbilityCard.java
│         │ └ GameObject.java
│         ├─┐<u><b>service</b></u>
│         │ ├ <b>RulesetLoader.java</b> (Orchestrator)
│         │ ├ <b>RulesetParser.java</b> (Parsing Logic)
│         │ ├ <b>RulesetSaver.java</b> (Saving & Filler Bank Logic)
│         │ └ <b>ActiveSessionData.java</b> (In-memory State)
│         └─┐<u><b>utils</b></u>
│           └ helper classes
└─┐<u><b>resources</b></u>
  ├─┐<u><b>static</b></u>
  | └ js/css files
  └─┐<u><b>templates</b></u>
    └ start.html
</pre>
