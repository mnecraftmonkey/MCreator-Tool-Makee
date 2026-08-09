#!/usr/bin/env python3
"""
A tiny helper script that loads a template workspace JSON, performs a simple "improvement" step
and writes a new JSON. This demonstrates the idea of an algorithmic "improvement" without claiming
full automation.

Usage:
  python3 tools/makee_improver.py templates/sample_workspace.json out/improved.json
"""
import json
import sys
import uuid
from pathlib import Path


def improve_workspace(input_path: Path, output_path: Path):
    data = json.loads(input_path.read_text())
    # bump version (naive semantic-like bump)
    ver = data.get("version", "0.1.0")
    parts = ver.split('.')
    if len(parts) == 3:
        parts[2] = str(int(parts[2]) + 1)
    data['version'] = '.'.join(parts)

    # duplicate first element with a tweak
    elems = data.get('elements', [])
    if elems:
        first = elems[0]
        new_elem = dict(first)
        new_elem['id'] = str(uuid.uuid4())
        if 'power' in new_elem and isinstance(new_elem['power'], int):
            new_elem['power'] = new_elem['power'] + 1
            new_elem['name'] = new_elem.get('name', '') + ' (Improved)'
        elems.append(new_elem)
    data['elements'] = elems

    output_path.parent.mkdir(parents=True, exist_ok=True)
    output_path.write_text(json.dumps(data, indent=2))
    print(f"Wrote improved workspace to {output_path}")


if __name__ == '__main__':
    if len(sys.argv) < 3:
        print('Usage: makee_improver.py <input.json> <output.json>')
        sys.exit(1)
    improve_workspace(Path(sys.argv[1]), Path(sys.argv[2]))
