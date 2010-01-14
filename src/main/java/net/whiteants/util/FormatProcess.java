package net.whiteants.util;

import java.util.LinkedList;
import java.util.StringTokenizer;

public class FormatProcess {
    boolean beginLine = true;
    boolean afterBeginBeforeEnd = false;
    boolean afterByOrSetOrFromOrSelect = false;
    boolean afterValues = false;
    boolean afterOn = false;
    boolean afterBetween = false;
    boolean afterInsert = false;
    boolean afterMisc = false;
    boolean beforeMisc = false;
//    boolean afterEndClause = false;
//    boolean afterMisc = false;

    int inFunction = 0;
    int parentsSinceSelect = 0;
    private LinkedList<Integer> parentCounts = new LinkedList<Integer>();
    private LinkedList<Boolean> afterByOrFromOrSelects = new LinkedList<Boolean>();

    int indent = 1;

    StringBuffer result = new StringBuffer();
    StringTokenizer tokens;
    String lastToken;
    String token;
    String lcToken;

    private String initial;
    private String indentString;
    private Keywords keywords = new Keywords();

    public FormatProcess(String sql, String initial, String indentString) {
        this.initial = initial;
        this.indentString = indentString;
        tokens = new StringTokenizer(
                sql,
                "()+*/-=<>'`\"[]," + keywords.getWhitespace(),
                true
        );
    }

    public String perform() {

        result.append(initial);

        while (tokens.hasMoreTokens()) {
            token = tokens.nextToken();
            lcToken = token.toLowerCase();

            if (token.equals("\"") || token.equals("'")) {
                String t = "";
                for (; !t.equals("\"") && !t.equals("'"); t = tokens.nextToken())
                    token += t;
                token += t;
            }

            if (token.equals("-")) {
                String s = tokens.nextToken();
                if (s.equals("-")) {
                    token = "\n--";
                    while (!s.equals("\n")) {
                        s = tokens.nextToken();
                        token += s;
                    }
                }
            }

            if (afterByOrSetOrFromOrSelect && ",".equals(token)) {
                commaAfterByOrFromOrSelect();
            } else if (afterOn && ",".equals(token)) {
                commaAfterOn();
            } else if ("(".equals(token)) {
                openParent();
            } else if (")".equals(token)) {
                closeParent();
            } else if (keywords.getBegin_clauses().contains(lcToken)) {
                beginNewClause();
            } else if (keywords.getEnd_clauses().contains(lcToken)) {
                endNewClause();
            } else if ("select".equals(lcToken)) {
                select();
            } else if (keywords.getDml().contains(lcToken)) {
                updateOrInsertOrDelete();
            } else if ("values".equals(lcToken)) {
                values();
            } else if ("on".equals(lcToken)) {
                on();
            } else if (afterBetween && lcToken.equals("and")) {
                misc();
                afterBetween = false;
            } else if (keywords.getLogical().contains(lcToken)) {
                logical();
            } else if (isWhitespace(token)) {
                white();
            } else {
                misc();
            }

            if (!isWhitespace(token)) {
                lastToken = lcToken;
            }

        }
        return result.toString();
    }

    private void commaAfterOn() {
        out();
        indent--;
        newline();
        afterOn = false;
        afterByOrSetOrFromOrSelect = true;
    }

    private void commaAfterByOrFromOrSelect() {
        out();
        newline();
    }

    private void logical() {
        if ("end".equals(lcToken)) {
            indent--;
        }
        newline();
        out();
        beginLine = false;
    }

    private void on() {
        indent++;
        afterOn = true;
        newline();
        out();
        beginLine = false;
    }

    private void misc() {
        afterMisc = true;
        out();
        if ("between".equals(lcToken)) {
            afterBetween = true;
        }
        if (afterInsert) {
            newline();
            afterInsert = false;
        } else {
            beginLine = false;
            if ("case".equals(lcToken)) {
                indent++;
            }
        }
    }

    private void white() {
        if (!beginLine) {
            result.append(" ");
        }
    }

    private void updateOrInsertOrDelete() {
        out();
        indent++;
        beginLine = false;
        if ("update".equals(lcToken)) {
            newline();
        }
        if ("insert".equals(lcToken)) {
            afterInsert = true;
        }
    }

    private void select() {
        out();
        indent++;
        newline();
        parentCounts.addLast(parentsSinceSelect);
        afterByOrFromOrSelects.addLast(afterByOrSetOrFromOrSelect);
        parentsSinceSelect = 0;
        afterByOrSetOrFromOrSelect = true;
    }

    private void out() {
        result.append(token);
    }

    private void endNewClause() {
        if (!afterBeginBeforeEnd) {
            indent--;
            if (afterOn) {
                indent--;
                afterOn = false;
            }
            newline();
        }
        out();
        if (!"union".equals(lcToken)) {
            indent++;
        }
        newline();
        afterBeginBeforeEnd = false;
        afterByOrSetOrFromOrSelect = "by".equals(lcToken)
                || "set".equals(lcToken)
                || "from".equals(lcToken);
    }

    private void beginNewClause() {
        if (afterMisc)
            newline();
        if (!afterBeginBeforeEnd) {
            if (afterOn) {
                indent--;
                afterOn = false;
            }
            indent--;
            newline();
        }

        out();
        beginLine = false;
        afterBeginBeforeEnd = true;
        afterMisc = false;
    }

    private void values() {
        indent--;
        newline();
        out();
        indent++;
        newline();
        afterValues = true;
    }

    private void closeParent() {
        parentsSinceSelect--;
        if (parentsSinceSelect < 0) {
            indent--;
            parentsSinceSelect = parentCounts.removeLast();
            afterByOrSetOrFromOrSelect = afterByOrFromOrSelects.removeLast();
        }
        if (inFunction > 0) {
            inFunction--;
            out();
        } else {
            if (!afterByOrSetOrFromOrSelect) {
                indent--;
                newline();
            }
            out();
        }
        beginLine = false;
    }

    private void openParent() {
        if (isFunctionName(lastToken) || inFunction > 0) {
            inFunction++;
        }
        beginLine = false;
        if (inFunction > 0) {
            out();
        } else {
            out();
            if (!afterByOrSetOrFromOrSelect) {
                indent++;
                newline();
                beginLine = true;
            }
        }
        parentsSinceSelect++;
    }

    private boolean isFunctionName(String tok) {
        final char begin = tok.charAt(0);
        final boolean isIdentifier = Character.isJavaIdentifierStart(begin) || '"' == begin;
        return isIdentifier &&
                !keywords.getLogical().contains(tok) &&
                !keywords.getEnd_clauses().contains(tok) &&
                !keywords.getQuantifiers().contains(tok) &&
                !keywords.getDml().contains(tok) &&
                !keywords.getMisk().contains(tok);
    }

    private boolean isWhitespace(String token) {
        return keywords.getWhitespace().indexOf(token) >= 0;
    }

    private void newline() {
        result.append("\n");
        for (int i = 0; i < indent; i++) {
            result.append(indentString);
        }
        beginLine = true;
    }
}