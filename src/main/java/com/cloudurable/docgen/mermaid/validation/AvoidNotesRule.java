package com.cloudurable.docgen.mermaid.validation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AvoidNotesRule implements Rule {

    private final Pattern PATTERN = Pattern.compile("^\\sNote\\b");

    private final String RULE_NAME = "Avoid Notes Rule";
    private final String RULE_DESCRIPTION = "Do not include notes in the diagram. Encapsulate all necessary information within interaction sequence.";

    @Override
    public RuleResult check(String line, int lineNumber) {
        final  Matcher matcher = PATTERN.matcher(line);

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
