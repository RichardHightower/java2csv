package com.cloudurable.docgen.mermaid.validation.sequence;


import com.cloudurable.docgen.mermaid.validation.LineRule;
import com.cloudurable.docgen.mermaid.validation.RuleResult;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class AvoidActivateDeactivateRule implements LineRule {
    //private final Pattern PATTERN_COLON = Pattern.compile("\\b(activate|deactivate)\\b(?=.*:)");
    private final Pattern PATTERN = Pattern.compile("^\\s*(activate|deactivate)\\b");

    private final String RULE_NAME = "Activate/Deactivate Rule";
    private final String RULE_DESCRIPTION = "Avoid 'activate'/'deactivate' commands in diagrams. Focus on participant interaction and action flow.";

    @Override
    public RuleResult check(String line, int lineNumber) {


        Matcher matcher =  PATTERN.matcher(line);

        if(matcher.find()) {
            return RuleResult.builder()
                    .lineNumber(lineNumber)
                    .violatedLine(line)
                    .ruleName(RULE_NAME)
                    .description(RULE_DESCRIPTION)
                    .build();
        } else {
            return RuleResult.SUCCESS;
        }
    }
}
