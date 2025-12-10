# <center>Fivers Frosthaven Trainer</center>

Fivers Frosthaven Trainer is a tool designed to modify the `Base.ruleset` file for the digital version of Frosthaven. Since the game engine requires the file to maintain its exact size (byte-for-byte), this tool acts as a safe editor that allows you to modify game data without corrupting the file.

## Who am I?
I am **Holyfivr** (or Fiver), the developer behind this project. I created this tool to enable modding of Frosthaven, and to give myself some practice.
This is a personal project of mine, while also studying full-time to become a software engineer. 
So please be patient if updates are slow, as I am balancing this with my studies.

<hr>

## Features
*   **Character Editing:** Change HP for all levels (1-9) for all characters, as well as available cards on hand.
*   **Unlock Characters:** Unlock starting classes (currently Snowflake, Fist, Meteor, Prism, Trap)
*   **Max Stats:** "Max All Values" button to quickly set HP to 99 for all characters.
*   **Max Cards:** Set hand size to 20 cards for all characters.

## Upcoming Features
*   **Item Editing:** Modify item stats and properties.
*   **Ability Card Editing:** Change ability card effects and values.
*   **Enchantment Editing:** Change enchantment effects and values.
*   **Town Editing**: Adjust things like prosperity/morale/available buildings.

<hr>

## How does it work?
1.  Start the application.
2.  The application will open in its own window (it runs as a desktop application).
3.  Click on **File -> Open Ruleset** in the top menu bar and select your `Base.ruleset` file. 
(Default location: `..\Steam\steamapps\common\Frosthaven\Frosthaven_Data\StreamingAssets\Rulesets\Base.ruleset`)
4.  Select a character from the **Characters** menu, or choose the bottom options that edit all characters at once.
5.  Make your changes.
6.  Click on **Save**.
7.  The application saves the changes directly to the file on your disk.
8.  Open Frosthaven and enjoy your modified game!

 
**<b>IMPORTANT: </b>**
**<b>You can not make changes to an existing campaign.</b>** 
Changes apply only to new campaigns started after the modifications.
**<b>Some features, like adding new starting characters, need more testing to ensure they work correctly.</b>** 
Adding characters work, but may have unexpected side effects in the campaign. Do this at your own risk.

<hr>

## Installation
1.  Download the latest release from the [Releases](https://github.com/Holyfivr/Fivers-Frosthaven-Trainer/releases)
2.  Extract the ZIP file to a folder of your choice.
3.  Run the `FrosthavenTrainer.exe` file to start the application.
   
## Building from Source
1.  Ensure you have Java Development Kit (JDK) 11 or higher installed.
2.  Clone this repository to your local machine.
3.  Navigate to the project directory.
4.  Build the project using your preferred IDE or build tool.

## Troubleshooting
If you encounter any issues while using the Frosthaven Trainer, open a GitHub issue in the repository with a detailed description of the problem.
Include the latest player.log located in `..\AppData\LocalLow\Snapshot Games Inc\Frosthaven\`

## Contributing
If you would like to contribute to this project, please fork the repository and create a pull request with your changes.
I will put out a more detailed guide on how to contribute, and how to safely edit the file, but for now, refer to STRATEGY.md for some insights into the file structure.
