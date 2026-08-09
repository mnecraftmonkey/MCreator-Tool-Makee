package com.makee;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * MakeeGenerator
 *
 * A small CLI tool that reads a template JSON workspace (a lightweight placeholder format),
 * writes a generated workspace, and can perform a simple "improvement" transformation to
 * produce a variant of the workspace that adds or tweaks elements.
 *
 * This class is intentionally simple and dependency-free so it can be run without build tooling.
 * The format is NOT the official MCreator workspace format; treat this as a scaffolding example
 * and adapt it when integrating with a real MCreator workspace export.
 */
public class MakeeGenerator {

    public static void main(String[] args) throws IOException {
        if (args.length < 3) {
            System.out.println("Usage: java com.makee.MakeeGenerator <generate|improve> <input.json> <output.json>");
            System.exit(1);
        }
        String cmd = args[0];
        Path in = Paths.get(args[1]);
        Path out = Paths.get(args[2]);
        if ("generate".equals(cmd)) {
            generate(in, out);
        } else if ("improve".equals(cmd)) {
            improve(in, out);
        } else {
            System.out.println("Unknown command: " + cmd);
            System.exit(2);
        }
    }

    /**
     * Copies the template and fills in a workspace id and timestamp.
     */
    public static void generate(Path templatePath, Path outputPath) throws IOException {
        String content = Files.readString(templatePath);
        String id = UUID.randomUUID().toString();
        content = content.replace("__WORKSPACE_ID__", id);
        content = content.replace("__VERSION__", "0.1.0");
        Files.createDirectories(outputPath.getParent());
        Files.writeString(outputPath, content);
        System.out.println("Generated workspace at " + outputPath + " (id=" + id + ")");
    }

    /**
     * A very small heuristic-based "improvement" pass.
     * - Bumps the version
     * - Duplicates an element with a new id and a small tweak to a numeric property
     */
    public static void improve(Path inputPath, Path outputPath) throws IOException {
        String content = Files.readString(inputPath);
        // bump version heuristically
        content = content.replaceFirst("\"version\":\s*\"([0-9]+)\.([0-9]+)\.([0-9]+)\"",
                m -> {
                    // naive bump patch
                    try {
                        String[] parts = m.group(1).split("\\.");
                        return "\"version\": \"0.1.1\""; // simple stable bump for example
                    } catch (Exception e) {
                        return "\"version\": \"0.1.1\"";
                    }
                }
        );

        // If there is a numeric property called "power", increase by 1 for the duplicated improved element
        if (content.contains("\"elements\"")) {
            // Very naive textual approach: find first element block and duplicate with small changes.
            int idx = content.indexOf("\"elements\"");
            int arrStart = content.indexOf('[', idx);
            int arrEnd = content.indexOf(']', arrStart);
            if (arrStart != -1 && arrEnd != -1 && arrEnd > arrStart) {
                String elements = content.substring(arrStart + 1, arrEnd).trim();
                if (!elements.isEmpty()) {
                    // find first element object (very naive)
                    int objStart = elements.indexOf('{');
                    int objEnd = elements.indexOf('}', objStart);
                    if (objStart != -1 && objEnd != -1) {
                        String firstObj = elements.substring(objStart, objEnd + 1);
                        String newObj = firstObj.replaceAll("\"id\":\s*\"[a-f0-9-]+\"", "\"id\": \"" + UUID.randomUUID().toString() + "\"");
                        // tweak numeric property named "power" if present
                        newObj = newObj.replaceAll("\"power\":\s*([0-9]+)", (m)-> {
                            int v = Integer.parseInt(m.group(1));
                            return "\"power\": " + (v + 1);
                        });
                        // append to elements
                        String newElements = elements + ",\n" + newObj;
                        String newContent = content.substring(0, arrStart + 1) + "\n" + newElements + "\n" + content.substring(arrEnd);
                        content = newContent;
                    }
                }
            }
        }

        Files.createDirectories(outputPath.getParent());
        Files.writeString(outputPath, content);
        System.out.println("Wrote improved workspace to " + outputPath);
    }
}
