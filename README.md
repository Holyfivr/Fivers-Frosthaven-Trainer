
<p align="center">
  <img src="/preview/header.png" alt="Fivers Frosthaven Trainer" width="700"/>
</p>

---

<div style="max-width: 1000px; margin: auto; font-size: 1.1em; line-height: 1.6em; font-weight: bold; ">
Fivers Frosthaven Trainer is a tool for modifying the digital version of Frosthaven. It edits two kinds of files: the `Base.ruleset` file (game rules: characters, items, ability cards) and campaign savefiles (per-character gold and resources). Since the game engine requires the ruleset to maintain its exact size (byte-for-byte), this tool acts as a safe editor that allows you to modify game data without corrupting the file.
</div>
<div style="max-width: 1000px; margin: auto; text-align: center;">
<h2> Who am I?</h2>
</div>
<div style="max-width: 1000px; margin: auto; font-size: 1.1em; line-height: 1.6em;">
I am Holyfivr (or Fiver), the developer behind this project. I created this tool to enable modding of Frosthaven, and to give myself some practice.
This is a personal project of mine, while also studying full-time to become a software engineer. 
So please be patient if updates are slow, as I am balancing this with my studies.
</div>

<hr>

<h2 style="max-width: 1000px; margin: auto; text-align: center;"> Features
</h2>
<div style="max-width: 1000px; margin: auto; font-size: 1.1em; line-height: 1.6em;">

*   ✅**File Safety:** Automatically creates a backup of the original file when first opened. Ability to restore from backup if needed.
*   ✅**Character Editing:** Change HP for all levels (1-9) for all characters, as well as available cards on hand.
*   ✅**Mass Character Editing** All values can be maxed to their allowed value by single-click menu options.
*   ✅**Unlock Characters:** Unlock starting classes (currently Snowflake, Fist, Meteor, Prism, Trap)
*   ✅**Item Editing:** Modify item stats and properties.
*   ✅**Mass Item Editing** All item values can be set to desired values by a popup window with all the fields needed. Leave fields empty to skip changing that value.
*   ✅**Ability Card Editing:** Change ability card effects and values.
*   ✅**Mass Ability Card Editing** All ability card values can be edited to desired values in a popup window with all needed fields. Leave fields empty to skip changing that value.
*   ✅**Savefile Editing:** Open a campaign savefile (`.dat`) and edit gold and materials (lumber, metal, hide) for each character, plus the town's herbs and stats (morale, prosperity, soldiers, defense, guard perk points). A backup of the save is created automatically the first time it's opened.
*   ✅**Reset card choices (experimental):** Choose a character and a target level to reopen completed card choices above it.

## Upcoming Features
*   **Summon Cards:** Fix so summon cards work. Currently summons are unaffected by any changes in the program.
</div>

<hr>

<h2 style="max-width: 1000px; margin: auto; text-align: center;"> Getting Started</h2>
<div style="max-width: 1000px; margin: auto; font-size: 1.1em; line-height: 1.6em;">

**Editing the ruleset (game rules):**

1.  Start the application.
2.  Click on **File → Open Ruleset** in the top menu bar and select your `Base.ruleset` file. 
(Default location: `..\Steam\steamapps\common\Frosthaven\Frosthaven_Data\StreamingAssets\Rulebase\Base.ruleset`)
3.  The first time you open the file, a backup will be created of the original.
4.  Select an option from the menu that you want to edit.
5.  Make your changes.
6.  Click on **File → Save Ruleset**. Saving also closes the ruleset.
7.  Open Frosthaven and enjoy your modified game!

**Editing a savefile (gold and resources):**

1.  Make sure Frosthaven is fully closed.
2.  Click on **File → Open Savefile** in the top menu bar and select your latest autosave. 
(Default location: `..\AppData\LocalLow\Snapshot Games Inc\Frosthaven\Steam\<steam_id>\Campaign\<party_name>\AutoSave_<number>.dat`. The game loads the most recent one)
3.  The first time you open a save, a backup copy (`.bak`) is created next to it.
4.  Edit the values in the town and character tabs, then click **Save & Close**.
    To redo supported card choices, open **Reset card choices**, choose the character and
    target level, click **Reset** twice to confirm, then **Save & Close**. The game presents
    the choices in level order the next time you load the edited save. This does not reduce
    the character's actual level.
5.  Start the game and load your campaign.

Only one file can be open at a time. Save or close the current ruleset/savefile before opening the other.
</div>

<h2 style="max-width: 1000px; margin: auto; text-align: center; color: red; font-weight: bold;"> Important!</h2>
<div style="max-width: 1000px; margin: auto; font-size: 1.1em; line-height: 1.6em; font-weight: bold;">

**<b>Ruleset changes affect all existing campaigns.</b>** 
Making changes in the ruleset file will effectively change the game rules, this means all existing campaigns will be affected by these changes.
Savefile changes only affect the save you open.

**<b>Steam Cloud can overwrite savefile edits.</b>**
If Steam Cloud sync considers its copy of a save newer, it restores it over your edited file when the game launches. If your savefile edits don't stick, disable cloud saves: in Steam, right-click **Frosthaven → Properties → General** and uncheck **"Keep games saves in the Steam Cloud"**. See [TROUBLESHOOTING.md](TROUBLESHOOTING.md) for details.

</div>
<hr>

## <center>Preview</center>

### Ruleset Editor

| Open ruleset | Create a backup |
|:---:|:---:|
| ![Open Ruleset](/preview/ruleset-editor-open-ruleset.png) | ![Create Backup](/preview/ruleset-editor-create-backup.png) |

| Mass-edit characters | Edit a specific character |
|:---:|:---:|
| ![Mass-edit Characters](/preview/ruleset-editor-all-character.png) | ![Edit Character](/preview/ruleset-editor-single-character.png) |

| Mass-edit items | Edit a specific item |
|:---:|:---:|
| ![Mass-edit Items](/preview/ruleset-editor-all-item.png) | ![Edit Item](/preview/ruleset-editor-single-item.png) |

| Mass-edit ability cards | Edit a specific ability card |
|:---:|:---:|
| ![Mass-edit Ability Cards](/preview/ruleset-editor-all-abilitycard.png) | ![Edit Ability Card](/preview/ruleset-editor-single-abilitycard.png) |

| Restore the original | Save changes |
|:---:|:---:|
| ![Restore Original](/preview/ruleset-editor-restore-original.png) | ![Save Changes](/preview/ruleset-editor-save-changes.png) |

### Save Editor

| Open a savefile | Edit town resources and stats |
|:---:|:---:|
| ![Open Savefile](/preview/save-editor-load-file.png) | ![Edit Town](/preview/save-editor-town.png) |

| Edit a character's gold and materials | Save and close |
|:---:|:---:|
| ![Edit Character](/preview/save-editor-character.png) | ![Save and Close](/preview/save-editor-save-and-close.png) |

<hr>

## Tools Used
*   [JDK 21](https://www.oracle.com/java/technologies/downloads/#java21) - Java Development Kit
*   [Maven](https://maven.apache.org/) - Build Automation Tool
*   [Spring Boot](https://spring.io/projects/spring-boot) - Application Framework
*   [Thymeleaf](https://www.thymeleaf.org/) - Templating Engine
*   [JavaFX (Webview)](https://openjfx.io/) - GUI Framework
*   [Visual Studio Code](https://code.visualstudio.com/) - IDE

## Languages
*   Java
*   Css
*   HTML
*   JavaScript

## How to run
1.  Download the latest release from the [Releases](https://github.com/Holyfivr/Fivers-Frosthaven-Trainer/releases)
2.  Extract the ZIP file to a folder of your choice.
3.  Run the `FrosthavenTrainer.exe` file to start the application.

<h4> Note:</h4>
<div style="margin-top: -15px;">
Your computer may alert you when you try to run the application and say "Windows protected your PC". This is because the application is not signed with a verified certificate. To proceed, click on "More info" and then "Run anyway".<br>
Getting a certificate costs money, and since I am a student working on this project when I have spare time (which is not much), I don't intend on getting one.
</div>

## Known Issues
* Values on item cards are not updated in game. It might be impossible for me to fix this without using external software. The values are implemented, just not visible on the cards.
* Enhancement costs can't be edited. The game ignores the enhancement cost tables in the ruleset (the values appear to be hardcoded in the game itself), so an enhancement editor is not planned.
* Savefile edits may be reverted by Steam Cloud sync. See the **Important!** section above.
   
## Building from Source
1.  Ensure you have Java Development Kit (JDK) 21 or higher installed.
2.  Clone this repository to your local machine.
3.  Navigate to the project directory.
4.  Build the project with ```./mvnw clean package```.
    *If you plan on building the project several times after making changes, make sure to delete the target\dist directory first, or JPackage won't be able to delete the previous build.*
5.  After building the project, there will be a directory named `target\dist\FiversFrosthavenTrainer\` placed in the root of the project directory. In this folder you will find the executable `FrosthavenTrainer.exe`.

## Troubleshooting
Potential problems (files that won't save, the game not loading, a ruleset size mismatch, savefile edits not showing up in-game, or recovering when both your file and backup are unusable) are covered in **[TROUBLESHOOTING.md](TROUBLESHOOTING.md)**. The same guide is available inside the program under **More → Troubleshooting**.

If your issue isn't covered there, please open a [GitHub Issue](https://github.com/Holyfivr/Fivers-Frosthaven-Trainer/issues) in the repository with a detailed description of the problem.
Include the latest player.log located in `..\AppData\LocalLow\Snapshot Games Inc\Frosthaven\`

## Contributing
If you would like to contribute to this project, please fork the repository and create a pull request with your changes.
I will put out a more detailed guide on how to contribute, and how to safely edit the file, but for now, refer to [STRATEGY.md](STRATEGY.md) for some insights into the file structure.
