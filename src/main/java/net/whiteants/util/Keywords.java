package net.whiteants.util;

import java.util.HashSet;
import java.util.Set;

public class Keywords {

    private Set<String> begin_clauses = new HashSet<String>();
    private Set<String> end_clauses = new HashSet<String>();
    private Set<String> logical = new HashSet<String>();
    private Set<String> quantifiers = new HashSet<String>();
    private Set<String> dml = new HashSet<String>();
    private Set<String> misk = new HashSet<String>();
    private String whitespace = " \n\r\f\t";

    public Keywords() {
        begin_clauses.add("left");
        begin_clauses.add("right");
        begin_clauses.add("inner");
        begin_clauses.add("outer");
        begin_clauses.add("group");
        begin_clauses.add("order");
        begin_clauses.add("begin");
        end_clauses.add("returns");
        end_clauses.add("declare");
        end_clauses.add("then");
        end_clauses.add("into");
        end_clauses.add("where");
        end_clauses.add("set");
        begin_clauses.add("in");
        end_clauses.add("perform");
        end_clauses.add("if");
        end_clauses.add("from");

        end_clauses.add("where");
        end_clauses.add("set");    
        end_clauses.add("having");
        end_clauses.add("join");
        end_clauses.add("from");
        end_clauses.add("by");
        end_clauses.add("join");
        end_clauses.add("into");
        end_clauses.add("union");
        end_clauses.add("table");
        end_clauses.add("replace");
        end_clauses.add("function");
        end_clauses.add("trigger");
        end_clauses.add("as");
        end_clauses.add("raise");
        end_clauses.add("exception");

        logical.add("and");
        logical.add("or");
        logical.add("when");
        logical.add("else");
        logical.add("end");

        quantifiers.add("in");
        quantifiers.add("all");
        quantifiers.add("exists");
        quantifiers.add("some");
        quantifiers.add("any");

        dml.add("insert");
        dml.add("update");
        dml.add("delete");

        misk.add("select");
        misk.add("on");
    }

    public boolean contains(String token) {
        return begin_clauses.contains(token) || dml.contains(token) ||
                end_clauses.contains(token) || logical.contains(token) ||
                misk.contains(token) || quantifiers.contains(token);
    }


    public String getWhitespace() {
        return whitespace;
    }


    public Set getBegin_clauses() {
        return begin_clauses;
    }


    public Set getEnd_clauses() {
        return end_clauses;
    }


    public Set getLogical() {
        return logical;
    }

    public Set getQuantifiers() {
        return quantifiers;
    }


    public Set getDml() {
        return dml;
    }


    public Set getMisk() {
        return misk;
    }

}
