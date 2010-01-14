package net.whiteants.util;

import java.util.StringTokenizer;

public class SQLFormatter {

    private String source;
    private String indentString = "  ";
    private String initial = "  ";
    private Keywords keywords = new Keywords();

    public SQLFormatter(String sql) {
        source = sql;
    }

    public String format() {
        return new FormatProcess(source, initial, indentString).perform();
    }

    public void setIndentString(String indent) {
        indentString = indent;
    }

    public void setInitial(String initial) {
        this.initial = initial;
    }

    public void toLowerCase() {
        StringTokenizer tokens = new StringTokenizer(source,
                "()+*//*-=<>'`\"[]," + keywords.getWhitespace(), true);
        source = "";
        for (String token = ""; tokens.hasMoreTokens(); token = tokens.nextToken())
            if (keywords.contains(token.toLowerCase()))
                source += token.toLowerCase();
            else source += token;
    }

    public void toUpperCase() {
        StringTokenizer tokens = new StringTokenizer(source,
                "()+*//*-=<>'`\"[]," + keywords.getWhitespace(), true);
        source = "";
        for (String token = ""; tokens.hasMoreTokens(); token = tokens.nextToken())
            if (keywords.contains(token.toLowerCase()))
                source += token.toUpperCase();
            else source += token;
    }
}