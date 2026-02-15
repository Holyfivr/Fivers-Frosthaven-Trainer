# <center> Strategy for Editing the Base.ruleset File</center>

## Core Plan

The goal is to modify specific data objects (characters, items, etc.) within the `Base.ruleset` while maintaining the exact total file size. This is achieved by parsing the file into Java objects, allowing the user to edit them via a UI, and then re-serializing these objects back into their YAML-like text representation. We use a **"Byte Compensation"** strategy to ensure every single block preserves its original byte size.
<hr>

## Detailed Technical Strategy

The primary challenge with editing `Base.ruleset` is that it is not a simple text file. The game engine relies on the file having a specific, constant size. A simple text edit that changes the file's length by even a single byte will effectively corrupt the file and prevent the game from loading.

The strategy is designed to mitigate this risk entirely by treating the file as a structured binary file and preserving its total size. Here’s how it works in detail:

1.  **Read as Raw Bytes:** The entire file is read into a `byte[]` array. It ensures that every single byte is preserved perfectly.
<br>
2.  **Segmentation:** Once in memory, the byte array is divided into three distinct segments:
    *   **Header:** A small, binary section at the beginning of the file.
    *   **Content:** A large, human-readable section containing the YAML-like game data definitions. This is the only section we intend to modify. It is approximately about 100,000 lines long.
    *   **Footer:** A massive, binary section at the very end of the file. Roughly 150,000 lines long.
    <br>
3.  **Convert to the right format:** Once everything is loaded and the sections are split up, we convert the content part into a string in ISO_8859_1 format, to maintain a 1:1 byte ratio.
<br>
4.  **Parse Blocks:** We now divide each object in the file into it's own block, by using the keyword "Parser" as a delimiter. Every object starts with this phrase, and shows what type of object something is (e.g. "Parser: ItemCard", "Parser: AbilityCard").
<br>
5.  **The "Byte Compensation Strategy":** In order to maintain the bytesize of each parsed block, we use a byte compensating strategy to account for each lost/added byte.
    *   **Padding:** If the user's edits make a block *shorter* than the original, we add spaces to the end of the block until it matches the original length.
    *   **Trimming:** If the user's edits make a block *longer* than the original (e.g., increasing HP from 9 to 10), we must reclaim space. We do this by removing redundant characters in a prioritized order:
        1.  **Stripping Comments:** The file has plenty of comments. Anything behind a '#' can be safely removed and regarded as free bytes.
        2.  **Trailing Whitespace:** There's plenty of lines in the file that end with multiple whitespaces. Removing invisible spaces at the end of lines will give us some bytes.
        3.  **Carriage Returns:** Converting Windows line endings (`\r\n`) to Unix line endings (`\n`). This is invisible and saves 1 byte per line.
        4.  **Remove Double Spaces behind colons:** Many lines will have unnecessary padding in the "value" part of the key/value pairs. These can be safely trimmed to one.
        5.  **Double Spaces:** Removing extra spaces in indentation (last resort). If we reach this stage, the likelyhood of the file getting corrupted are very high, but with proper limitations to values, this shouldn't happen.
    <br>
6.  **Safe Reassembly and Writing:** After the user's changes are applied and every block has been adjusted to match its original size, the application reassembles the segments (`Header`, `New Content`, `Footer`) back into a single `byte[]` array. During the save-process we first write all the bytes to a .tmp file, and verify it's size, before using atomic move to replace the original.

<hr>

## Architecture & Services

### 1. Object Classes (POJOs)
*   **Classes:** `PlayerCharacter.java`, `Item.java`, `AbilityCard.java`, etc.

### 2. Layers
The application logic is split into specialized layers, in order to keep it maintainable and readable. The classes are divided into the following packages:
* Controllers - Contains spring boot controllers. These handle the flow between frontend and backend. No logic should be in this layer.
* Services - Contains all the business logic related to handling dataflow between core classes and frontend.
* Core classes - Handle all the advanced *core logic*, such as parsing, saving, loading, as well as the in-memory data storage.
* Model - Contains the POJO's and enums.

### 3. The Core

   

*   **`RulesetLoader`**: Loads the file, splitting it into Header/Content/Footer, and passes it on to the `RulesetParser` and Saver.
*   **`RulesetParser`**: Responsible for parsing the text content. It uses Regex to extract data into Java objects (`PlayerCharacter`, etc.) and stores them in the session.
*   **`RulesetSaver`**: The most critical component. It reconstructs the file string from the Java objects. It implements the **byte compensation** logic (padding/trimming) to ensure binary safety.
*   **`ActiveSessionData`**: A component that holds the in-memory state of the application (loaded objects, object changes, file path, etc.).

### 4. Spring Boot & Thymeleaf UI

*   **Spring Boot** The application uses Spring Boot as the underlying framework to manage dependencies, controllers, and services.
*   **JavaFX:** With the help of JavaFX 'webview' component, we render the application's UI using HTML/CSS/Javascript, which allows for a more flexible and visually appealing interface.
*   **Thymeleaf:** For smooth integration and dynamic rendering of HTML, we use Thymeleaf templates.

<hr>

## <center>Project Structure</center>
This structure reflects the current state of the project. (obviously subject to change)

<pre>
┐<u><b>src</b></u>
├─┐<u><b>main/java/se/holyfivr/trainer/</b></u>
│ ├ <i>TrainerApplication.java</i>
| |
│ ├─┐<u><b>controller</b></u>
│ │ └ <i>Spring MVC Controllers</i>
| |
│ ├─┐<u><b>Core</b></u>
│ │ ├ <i>File Handling Logic</i>
│ │ │
│ │ │
│ │ ├─┐<u><b>Parser</b></u>
│ │ | └  <i>Parsing Logic</i>
│ │ |
│ │ └─┐<u><b>utils</b></u>
│ │   └ <i>Helper classes for the core components</i>
| |
│ ├─┐<u><b>model</b></u>
│ │ ├ <i>POJO Classes</i>
│ │ │
│ │ └─┐<u><b>Enums</b></u> 
│ │   └ <i>Enum Classes</i>
│ │  
│ └─┐<u><b>service</b></u>
│   └ <i>Business Logic Services</i>
│
└─┐<u><b>resources</b></u>
  ├─┐<u><b>static</b></u>
  | └ js/css files
  └─┐<u><b>templates</b></u>
    └ start.html
</pre>
