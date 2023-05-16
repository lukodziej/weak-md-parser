package com.github.arena.challenges.weakmdparser;

public class MarkdownParser {

    public String parse(String markdown) {
        String[] lines = markdown.split("\n");
        StringBuilder result = new StringBuilder();
        boolean activeList = false;

        for (String line:lines) {

            String theLine = parseHeader(line);

            if (theLine == null) {
                theLine = parseList(line);
            }

            if (theLine == null) {
                theLine = parseParagraph(line);
            }

            if (theLine.matches("(<li>).*") && !theLine.matches("(<h).*") && !theLine.matches("(<p>).*") && !activeList) {
                activeList = true;
                result.append("<ul>").append(theLine);

            } else if (!theLine.matches("(<li>).*") && activeList) {
                activeList = false;
                result.append("</ul>").append(theLine);
            } else {
                result.append(theLine);
            }
        }

        if (activeList) {
            result.append("</ul>");
        }

        return result.toString();
    }

    private String parseHeader(String markdown) {
        int count = 0;

        for (int i = 0; i < markdown.length() && markdown.charAt(i) == '#'; i++) {
            count++;
        }

        if (count == 0) {
            return null;
        }

        return "<h" + count + ">" + markdown.substring(count + 1) + "</h" + count + ">";
    }

    private String parseList(String markdown) {
        if (markdown.startsWith("*")) {
            String skipAsterisk = markdown.substring(2);
            String listItemString = parseToBoldAndItalic(skipAsterisk);
            return "<li>" + listItemString + "</li>";
        }

        return null;
    }

    private String parseParagraph(String markdown) {
        return "<p>" + parseToBoldAndItalic(markdown) + "</p>";
    }

    private String parseToBoldAndItalic(String markdown) {

        String lookingFor = "__(.+)__";
        String update = "<strong>$1</strong>";
        String workingOn = markdown.replaceAll(lookingFor, update);

        lookingFor = "_(.+)_";
        update = "<em>$1</em>";
        return workingOn.replaceAll(lookingFor, update);
    }
}
