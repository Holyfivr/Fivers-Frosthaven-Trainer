# Troubleshooting

This guide covers the most common problems and how to recover from them.
The same information is available inside the program under **More → Troubleshooting**.

> If your issue isn't covered here, please open a [GitHub Issue](https://github.com/Holyfivr/Fivers-Frosthaven-Trainer/issues)
> and include your latest `player.log` (found in `...\AppData\LocalLow\Snapshot Games Inc\Frosthaven\`).

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
