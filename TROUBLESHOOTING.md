# Troubleshooting

This guide covers the most common problems and how to recover from them.
The same information is available inside the program under **More → Troubleshooting**.

> If your issue isn't covered here, please open a [GitHub Issue](https://github.com/Holyfivr/Fivers-Frosthaven-Trainer/issues)
> and include your latest `player.log` (found in `...\AppData\LocalLow\Snapshot Games Inc\Frosthaven\`).

---

## File couldn't be saved

If the program reports that the file couldn't be saved, the write to `Base.ruleset` (or your
savefile) was refused or aborted. Common reasons:

- **The game is running.** Frosthaven locks the ruleset file while it's open, so it can't be
  overwritten. Fully close the game, then save again.
- **Another program is using the file.** Antivirus scans, cloud sync (OneDrive, Dropbox, Steam
  Cloud), or the file being open in another editor can all lock it.
- **The changes didn't fit.** Every edit has to be squeezed back into the file at its exact original
  size. If a value was increased too much, there wasn't enough spare space to reclaim, and the save
  is aborted to avoid corrupting the file. Try smaller values.
- **Size mismatch.** If the file on disk was changed after you opened it (for example a game patch),
  the save can no longer line up with it. Re-open the file and try again — see
  [Ruleset size mismatch](#ruleset-size-mismatch).
- **Permissions.** The file is read-only, or you don't have permission to write to the folder. Make
  sure the game folder isn't marked read-only.

---

## Game doesn't load

If Frosthaven won't start after you've used this tool, the ruleset file is most likely corrupted.
This can happen if a value was pushed too far and the file could no longer be kept byte-perfect.

**Fix:** Reload the clean copy the program made for you. In the menu bar, choose
**File → Restore Original**. This copies your backup back over `Base.ruleset`, undoing the bad changes.

If your backup is missing, was overwritten, or is also unusable, restore the game's own files
instead. See [Restore game files](#restore-game-files).

---

## Ruleset size mismatch

When you open a ruleset file, the program compares its size to your original backup. If they differ,
the file was changed outside this tool. This happens for one of two reasons:

- **The game was patched.** A game update rewrites `Base.ruleset`, so the opened file is new and
  valid, but your old backup is now outdated.
- **The file is corrupted.** The opened file is broken and your backup is still good.

To tell which one it is, close the program and launch Frosthaven:

| Result | Action |
|---|---|
| Game starts normally *(game was patched)* | Select **"create a new backup from this file"**. |
| Game can't load *(file is corrupted)* | Select **"restore from the old backup"**. |


If both the file **and** your backup are unusable, see [Restore game files](#restore-game-files).

---

## Savefile edits aren't showing up in-game

If you edited a savefile but the values are unchanged in-game, the game never read your edited
file. Common reasons:

- **The game was running.** Frosthaven keeps the campaign in memory and autosaves constantly, so
  anything you edit while it runs gets overwritten. Fully close the game before editing and saving.
- **Steam Cloud restored the old save.** If cloud sync thinks its copy is newer, it overwrites your
  edited file when the game launches. To turn it off: in Steam, right-click
  **Frosthaven → Properties → General** and uncheck **"Keep games saves in the Steam Cloud"**.
  You can re-enable it later.
- **The game loaded a different file.** The campaign folder contains many `AutoSave_<number>.dat`
  files and the game loads the most recent one. Make sure you edited the newest save.
- **A stray backup copy in the campaign folder.** Any extra `.dat` file in the campaign folder can
  get picked up by the game instead of your edited save. Keep manual backup copies outside the
  folder, or use a different file extension. (This program's automatic backups use `.bak` for
  exactly this reason.)

**Restoring a savefile:** the automatic backup is created next to the save the first time you open
it, named `<savename>.dat.bak`. To restore it, close the game, delete the broken `.dat` file, and
remove the `.bak` ending from the backup's filename.

Your savefiles are stored in:

```text
...\AppData\LocalLow\Snapshot Games Inc\Frosthaven\Steam\<steam_id>\Campaign\<party_name>
```

---

## Restore game files

If both the ruleset file **and** your backup are broken or mismatched, you can let Steam restore a
clean original copy of the ruleset.

> **Note:** Verifying game files restores the original ruleset, which also removes any changes you
> have made with this tool.

1. Close this program and the game.
2. Delete the old backup so the program does not reuse it. It is located in the **"original ruleset"**
   folder next to your ruleset file, named `ORIGINAL_BACKUP.ruleset`.
3. In Steam, right-click **Frosthaven → Properties → Installed Files → Verify integrity of game files**.
4. Launch the game once so a fresh `Base.ruleset` is in place.
5. Open the program again. It will create a new clean backup from the restored file.

Your ruleset files are stored in:

```text
...\Steam\steamapps\common\Frosthaven\Frosthaven_Data\StreamingAssets\Rulebase
```
