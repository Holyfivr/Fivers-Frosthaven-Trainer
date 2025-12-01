# <center> Strategy for Editing the Base.ruleset File</center>

This document outlines the plan for building an application that can safely edit the `Base.ruleset` file. 

## Core Plan

The goal is to modify specific data objects (characters, items, etc.) within the `Base.ruleset` while maintaining the exact total file size. This is achieved by parsing the file into Java objects, allowing the user to edit them via a UI, and then re-serializing these objects back into their YAML-like text representation before saving the file with an adjusted "filler" section.
<hr>

## Detailed Technical Strategy

The primary challenge with editing `Base.ruleset` is that it is not a simple text file. The game engine likely relies on the file having a specific, constant size, and may perform checks to ensure its integrity. A simple text edit that changes the file's length by even a single byte, will effectively corrupt the file and prevent the game from loading.

The strategy is designed to mitigate this risk entirely by treating the file as a structured binary file and preserving its total size. Here’s how it works in detail:

1.  **Read as Raw Bytes:** The entire file will be read into a `byte[]` array. It ensures that every single byte is preserved perfectly without any interpretation or modification by the operating system or runtime environment.
<br>
2.  **Programmatic Segmentation:** Once in memory, the byte array is programmatically divided into three or four distinct segments:
    *   **Header:** A small, binary section at the beginning of the file.
    *   **Content:** The large, human-readable section containing the YAML-like game data definitions. This is the only section we intend to modify.
    *   **Filler:** A large block of whitespace characters (like spaces or nulls) that separates the Content from the Footer. **This section is the key to our entire strategy.**
    *   **Footer:** A small, binary section at the very end of the file.
<br>
  The **content** and **filler** sections may be combined into one segment, depending on what method we use to handle the fillerbanks.
<br>
1.  **The "Filler Bank" Principle:** The "Filler" section acts as a flexible buffer or a "bank" of bytes. When the user saves changes to the game data, the length of the "Content" section will invariably change.
    *   If the new content is **longer** than the original, we "withdraw" bytes from the Filler bank, shrinking it by the exact number of bytes that were added to the Content.
    *   If the new content is **shorter** than the original, we add bytes into the Filler bank, expanding it by the exact number of bytes that were removed from the Content.
<br>
1.  **Safe Reassembly and Writing:** After the user's changes are applied to the Content and the Filler has been adjusted, the application reassembles the four segments (`Header`, the new `Content`, the adjusted `Filler`, and the `Footer`) back into a single `byte[]` array.

Because the filler section perfectly compensates for any change in the content's length, the final reassembled byte array is **guaranteed to have the exact same size as the original file**. This allows us to modify the game data with maximum confidence that the file will remain valid and readable by the game.

<hr>

## Initial Development Roadmap *(subject to change)*

### 1. Object Classes (Create POJOs)

*   **Goal:** Define Java classes that represent the various objects found in the ruleset file.
*   **Example Classes:**
    *   `Character.java`: With fields for `id`, `name`, `maxHealth`, `perks`, etc.
    *   `Item.java`: With fields for `id`, `name`, `cost`, `slot`, `description`, etc.
    *   `AbilityCard.java`: With fields for `id`, `name`, `level`, `initiative`, etc.

### 2. The `RulesetManager` Class

*   **Responsibility:** This class will be the brain of the application.
*   **Functionality:**
    *   **`loadRuleset()`:** Reads the entire `Base.ruleset` into a raw `byte[]` array. Then calls `parseRuleset()`.
    *   **`parseRuleset()`:** The most advanced method.
        1.  Isolates the Header, Content, Filler, and Footer from the `byte[]` array.
        2.  Takes the "Content" section as a large string, and splits it into smaller blocks, using `"Parser: ..."` to categorize them.
        3.  For each block:
            *   Identifies the type (Character, Item, etc.).
            *   Parses the text and maps the values to the fields in the corresponding Java object (`Character`, `Item`, etc.).
        4.  Stores the created objects in lists.
    *   Create **`getters`**, **`setters`**, and other needed methods to access the lists and edit their content.
    *   **`saveRuleset()`:** Reconstructs the complete file.
        1.  Creates a new, empty "Content" string.
        2.  Iterates through all lists (`characters`, `items`, etc.) and converts each Java object back into its YAML-like text representation.
        3.  Rebuilds the complete "Content" string.
        4.  Converts the new content string to `byte[]`, adjusts the "Filler" size to compensate, concatenates Header, Content, Filler, and Footer, and writes to a new file.

### 3. JavaFX User Interface

*   **Structure:**
    *   **Main Window:** A menu or a list on the left with categories: "Characters", "Items", "Ability Cards", "Perks".
    *   **List View:** When a category is selected (e.g., "Characters"), a list of all objects of that type (e.g., all character names) is displayed.
    *   **Detail View:** When an object is selected in the list, a new view opens on the right with editable fields that are bound to the properties of the selected Java object.
        - When leaving the view and selecting another object, changes are automatically saved by sending the new data to backend via a `Controller`.
    *   A global "Save Ruleset" button calls `RulesetManager.saveRuleset()`, which puts all the objects together and write over the original file.
<hr>

## <center>Project Structure</center>
This structure will, of course, change as the project evolves.

<pre>
┐<u><b>src</b></u>
├─┐<u><b>main</b></u>
│ └─┐<u><b>java</b></u>
│   └─┐<u><b>se</b></u>
│     └─┐<u><b>holyfivr</b></u>
│       └─┐<u><b>trainer</b></u>
│         ├ TrainerApplication.java
│         ├─┐<u><b>controller</b></u>
│         │ └ all controllers for spring
│         ├─┐<u><b>model</b></u>
│         │ ├ RulesetObject.java (Abstract base/interface for all parsed objects)
│         │ ├ Character.java (POJO for character data)
│         │ ├ Item.java (POJO for item data)
│         │ ├ AbilityCard.java (POJO for ability card data)
│         │ └ and other model classes...
│         ├─┐<u><b>service</b></u>
│         │ └ RulesetManager.java</b> (File I/O, parsing, and object management)
│         └─┐<u><b>utils</b></u>
│           └ helper classes
└─┐<u><b>resources</b></u>
  ├─┐<u><b>static</b></u> (thymeleaf/spring boot standard)
  | └ js/css files
  └─┐<u><b>templates</b></u> (thymeleaf/spring boot standard)
    └ html files
</pre>
