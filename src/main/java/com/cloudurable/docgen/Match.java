package com.cloudurable.docgen;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Match {

    public static final String INHERITANCE_PATTERN_LEFT = "<\\|";
    public static final String COMPOSITION_PATTERN = "\\*";
    public static final String AGGREGATION_PATTERN = "o";
    public static final String ASSOCIATION_PATTERN_LEFT = "<";

    public static final String ASSOCIATION_PATTERN_RIGHT = ">";
    public static final String INHERITANCE_PATTERN_RIGHT = "\\|>";

    // Link Patterns
    public static final String LINK_SOLID_PATTERN = "--";
    public static final String LINK_DASHED_PATTERN = "\\.\\.";

    public static final String LEFT = "(" + INHERITANCE_PATTERN_LEFT + "|"
            + COMPOSITION_PATTERN + "|" + AGGREGATION_PATTERN + "|" +
            ASSOCIATION_PATTERN_LEFT + "){0,1}";

    public static final String MIDDLE =
            "(" + LINK_SOLID_PATTERN + "|" + LINK_DASHED_PATTERN + "){1}";
    public static final String RIGHT = "(" + COMPOSITION_PATTERN + "|" + AGGREGATION_PATTERN + "|" +
            ASSOCIATION_PATTERN_RIGHT + "|" + INHERITANCE_PATTERN_RIGHT + "){0,1}";

    public static final String RELATIONSHIP_PATTERN =
            LEFT + MIDDLE + RIGHT;

    public static void main(String[] args) {

        String input = "<|--, *--, o--, -->, <--, ..>, and ..|>";


        Pattern pattern = Pattern.compile(RELATIONSHIP_PATTERN);
        Matcher matcher = pattern.matcher(input);

        while (matcher.find()) {
            System.out.println("Found: " + matcher.group());
        }
    }
}




