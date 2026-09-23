#!/usr/bin/env python3
"""Regenerate card-level.properties from Frosthaven's Base.ruleset.

Usage: python3 scripts/generate-card-levels.py /path/to/Base.ruleset
The output is class-ID qualified because numeric card IDs can be shared.
"""

import hashlib
import re
import sys
from pathlib import Path


def main() -> None:
    if len(sys.argv) != 2:
        raise SystemExit("Usage: generate-card-levels.py /path/to/Base.ruleset")
    source = Path(sys.argv[1])
    raw = source.read_bytes()
    text = raw.decode("utf-8", errors="replace")
    cards: dict[tuple[str, int], str] = {}
    for block in text.split("Parser: AbilityCard")[1:]:
        header = block.split("Parser:", 1)[0]
        fields = {}
        for key in ("Character", "ID", "Level"):
            match = re.search(rf"(?m)^{key}: (.+)$", header)
            if match:
                fields[key] = match.group(1).strip()
        if len(fields) != 3 or fields["Level"] not in ("X", *(str(i) for i in range(1, 10))):
            continue
        card = (fields["Character"], int(fields["ID"]))
        if card in cards and cards[card] != fields["Level"]:
            raise ValueError(f"Conflicting level for {card}")
        cards[card] = fields["Level"]

    if len(cards) < 500:
        raise ValueError("Too few cards; check the input ruleset format")
    output = Path(__file__).resolve().parents[1] / "src/main/resources/card-level.properties"
    lines = [
        "# ClassID.cardID=printed card level, generated from Base.ruleset.",
        f"# Source SHA-256: {hashlib.sha256(raw).hexdigest()}",
        "# Regenerate with scripts/generate-card-levels.py after game updates.",
    ]
    lines += [f"{class_id}.{card_id}={cards[(class_id, card_id)]}"
              for class_id, card_id in sorted(cards)]
    output.write_text("\n".join(lines) + "\n", encoding="utf-8")
    print(f"Wrote {len(cards)} cards to {output}")


if __name__ == "__main__":
    main()
