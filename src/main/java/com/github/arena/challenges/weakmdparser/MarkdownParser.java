package com.github.arena.challenges.weakmdparser;

public class MarkdownParser {

    private static final String NEW_LINE_SYMBOL = "\n";
    private static final String HTML_BOLD = "<strong>$1</strong>";
    private static final String HTML_ITALIC = "<em>$1</em>";
    private static final String MARKDOWN_BOLD = "__(.+)__";
    private static final String MARKDOWN_ITALIC = "_(.+)_";
    private static final String START_LIST = "<li>";
    private static final String END_LIST = "</li>";

    public String parse(String markdown) {
        String[] lines = markdown.split(NEW_LINE_SYMBOL);
        StringBuilder result = new StringBuilder();

        for (String line : lines) {
            result.append(parseLine(line));
        }
        result = parseListHeaderNew(result);
        return result.toString();
    }

    private String parseLine(String line) {
        String theLine = parseHeader(line);
        if (theLine == null) {
            theLine = parseList(line);
        }
        if (theLine == null) {
            theLine = parseParagraph(line);
        }
        return theLine;
    }

    private String parseHeader(String markdown) {
        int count = markdown.length() - markdown.replaceAll("^#+", "").length();
        return count == 0 ? null : "<h" + count + ">" + markdown.substring(count + 1) + "</h" + count + ">";
    }

    private String parseParagraph(String markdown) {
        return wrapInTags(markdown, "p");
    }

    private StringBuilder parseListHeaderNew(StringBuilder line) {
        StringBuilder result = new StringBuilder();
        int firstIndex = line.indexOf(START_LIST);
        int lastIndex = line.lastIndexOf(END_LIST);

        if (firstIndex == -1 || lastIndex == -1) {
            return line;
        }

        result.append(line.substring(0, firstIndex))
                .append("<ul>")
                .append(line.substring(firstIndex, lastIndex + START_LIST.length() + 1)).append("</ul>")
                .append(line.substring(lastIndex + END_LIST.length()));
        return result;
    }

    private String parseList(String markdown) {
        return !markdown.startsWith("*") ? null : wrapInTags(markdown.substring(2), "li");
    }

    private String parseToBoldAndItalic(String markdown) {
        return markdown
                .replaceAll(MARKDOWN_BOLD, HTML_BOLD)
                .replaceAll(MARKDOWN_ITALIC, HTML_ITALIC);
    }

    private String wrapInTags(String markdown, String tag) {
        return String.format("<%s>%s</%s>", tag, parseToBoldAndItalic(markdown), tag);
    }
}