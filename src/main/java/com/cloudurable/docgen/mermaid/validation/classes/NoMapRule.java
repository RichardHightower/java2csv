package com.cloudurable.docgen.mermaid.validation.classes;


import com.cloudurable.docgen.mermaid.validation.LineRule;
import com.cloudurable.docgen.mermaid.validation.Relationship;
import com.cloudurable.docgen.mermaid.validation.RuleResult;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NoMapRule implements LineRule {

    private final Pattern PATTERN = Pattern.compile("(Map|HashMap|TreeMap)");


    @Override
    public RuleResult check(String line, int lineNumber) {

        if (Relationship.hasRelationship(line)) {

            Relationship relationship = Relationship.parseRelationship(line);
            if (relationship == Relationship.NOT_FOUND) {
                System.out.println("NOT VALID " + line);
                return RuleResult.builder()
                        .ruleName("ValidRelationshipRule")
                        .lineNumber(lineNumber).violatedLine(line)
                        .description("Not a valid relationship")
                        .build();
            }

            final RuleResult leftCheck = checkMap("Left", relationship.getLeftClass(), line, lineNumber);
            if (leftCheck == RuleResult.SUCCESS) {
                return checkMap("Right", relationship.getRightClass(), line, lineNumber);
            } else {
                return leftCheck;
            }

        } else {
            return RuleResult.SUCCESS;
        }
    }

    private RuleResult checkMap(String side, String input, String line, int lineNumber) {
        Matcher matcher = PATTERN.matcher(input);

        if (matcher.find()) {

            return RuleResult.builder()
                    .ruleName("NoMap"+side)
                    .lineNumber(lineNumber).violatedLine(line)
                    .description("No Maps allowed, remove relationship from diagram or use \"*\" " +
                            "and describe key association in description of relationship")
                    .build();
        }
        return RuleResult.SUCCESS;
    }
}
