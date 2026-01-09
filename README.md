
<p align="center">
  <img src="/preview/header.png" alt="Fivers Frosthaven Trainer" width="700"/>
</p>

---

<div style="max-width: 1000px; margin: auto; font-size: 1.1em; line-height: 1.6em; font-weight: bold; ">
Fivers Frosthaven Trainer is a tool designed to modify the `Base.ruleset` file for the digital version of Frosthaven. Since the game engine requires the file to maintain its exact size (byte-for-byte), this tool acts as a safe editor that allows you to modify game data without corrupting the file.
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

*   **Character Editing:** Change HP for all levels (1-9) for all characters, as well as available cards on hand.
*   **Unlock Characters:** Unlock starting classes (currently Snowflake, Fist, Meteor, Prism, Trap)
*   **Max Stats:** "Max All Values" button to quickly set HP to 99 for all characters.
*   **Max Cards:** Set hand size to 20 cards for all characters.
</div>

<h2 style="max-width: 1000px; margin: auto; text-align: center;"> Upcoming Features
</h2>

<div style="max-width: 1000px; margin: auto; font-size: 1.1em; line-height: 1.6em;">

*   **Item Editing:** Modify item stats and properties.
*   **Ability Card Editing:** Change ability card effects and values.
*   **Enchantment Editing:** Change enchantment effects and values.
*   **Town Editing**: Adjust things like prosperity/morale/available buildings.
</div>

<hr>

<h2 style="max-width: 1000px; margin: auto; text-align: center;"> Getting Started</h2>
<div style="max-width: 1000px; margin: auto; font-size: 1.1em; line-height: 1.6em;">

1.  Start the application.
2.  The application will open in its own window (it runs as a desktop application).
3.  Click on **File -> Open Ruleset** in the top menu bar and select your `Base.ruleset` file. 
(Default location: `..\Steam\steamapps\common\Frosthaven\Frosthaven_Data\StreamingAssets\Rulesets\Base.ruleset`)
1.  The first time you open the file, a backup will be created of the original.
2.  Select an option from the menu that you want to edit.
3.  Make your changes.
4.  Click on **Save**.
5.  The application saves the changes directly to the file on your disk.
6.  Open Frosthaven and enjoy your modified game!
</div>

<h2 style="max-width: 1000px; margin: auto; text-align: center; color: red; font-weight: bold;"> Important!</h2>
<div style="max-width: 1000px; margin: auto; font-size: 1.1em; line-height: 1.6em; font-weight: bold;">

**<b>You can not make changes to an existing campaign.</b>** 
Changes apply only to new campaigns started after the modifications.

**<b>Some features, like adding new starting characters, need more testing to ensure they work correctly.</b>** 
Adding characters work, but may have unexpected side effects in the campaign. Do this at your own risk.

</div>
<hr>

## <center>Preview</center>

| Open ruleset | Create backup or rely on the automatic original backup |
|:---:|:---:|
| ![Open Ruleset](/preview/open-ruleset.png) | ![Create Backup](/preview/create-backup.png) |

| Edit characters (don't forget to save) | Use "Max All HP/Cards", or "Enable All" to mass-edit |
|:---:|:---:|
| ![Edit Characters](/preview/edit-character.png) | ![Edit All Characters](/preview/edit-all-character.png) |

| Edit items | Use the "set" options to mass-edit |
|:---:|:---:|
| ![Edit Items](/preview/edit-item.png) | ![Edit All Items](/preview/edit-all-item.png) |

| Save Ruleset  | If file somehow gets corrupted, restore from backup |
|:---:|:---:|
| ![Save Ruleset](/preview/save-ruleset.png) | ![Restore Backup](/preview/restore-ruleset.png) |
<hr>

## Installation
1.  Download the latest release from the [Releases](https://github.com/Holyfivr/Fivers-Frosthaven-Trainer/releases)
2.  Extract the ZIP file to a folder of your choice.
3.  Run the `FrosthavenTrainer.exe` file to start the application.

<h4> Note:</h4>
<div style="margin-top: -15px;">
Your computer may alert you when you try to run the application and say "Windows protected your PC". This is because the application is not signed with a verified certificate. To proceed, click on "More info" and then "Run anyway".<br>
Getting a certificate costs money, and since I am a student working on this project when I have spare time (which is not much), I don't intend on getting one.
</div>
   
## Building from Source
1.  Ensure you have Java Development Kit (JDK) 11 or higher installed.
2.  Clone this repository to your local machine.
3.  Navigate to the project directory.
4.  Build the project using your preferred IDE or build tool.

## Troubleshooting
If you encounter any issues while using the Frosthaven Trainer, please open a [GitHub Issue](https://github.com/Holyfivr/Fivers-Frosthaven-Trainer/issues) in the repository with a detailed description of the problem.
Include the latest player.log located in `..\AppData\LocalLow\Snapshot Games Inc\Frosthaven\`

## Contributing
If you would like to contribute to this project, please fork the repository and create a pull request with your changes.
I will put out a more detailed guide on how to contribute, and how to safely edit the file, but for now, refer to STRATEGY.md for some insights into the file structure.
