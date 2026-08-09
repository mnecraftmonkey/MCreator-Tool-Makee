# MCreator Tool: Makee

Makee is a collection of tools intended to be used alongside MCreator to generate workspace elements and to produce improved variants of a generator using template-driven heuristics.

Important: The included generator scripts create template MCreator workspace files (JSON/text). They are not an autonomous agent; you should review generated code before importing or publishing. Use these tools as scaffolding to speed up repetitive generator tasks.

Files added in this commit:
- addon.json — simple manifest for the addon
- src/main/java/com/makee/MakeeGenerator.java — a small standalone Java generator CLI that creates and can "improve" template workspace files
- tools/makee_improver.py — a helper script that demonstrates a template-driven "self-improvement" step
- templates/sample_workspace.json — a small example workspace template that the generator reads

Quick start (CLI):

1. Java (requires JDK 11+):
   - Compile: javac -d out src/main/java/com/makee/MakeeGenerator.java
   - Run: java -cp out com.makee.MakeeGenerator generate templates/sample_workspace.json out/generated_workspace.json
   - Improve: java -cp out com.makee.MakeeGenerator improve out/generated_workspace.json out/improved_workspace.json

2. Python helper (requires Python 3.8+):
   - Run: python3 tools/makee_improver.py templates/sample_workspace.json out/improved_by_python.json

Installation into MCreator:
- The Java CLI and Python helper are standalone tools. To integrate with MCreator you can wrap the generator invocation in an external tool or script that runs before importing the generated workspace into MCreator.

Limitations and safety:
- This project is a scaffolding and proof-of-concept. Generated workspaces should be manually reviewed.
- "Improvement" rules are heuristic-based template transformations, not full AI-driven code invention.

License: MIT
