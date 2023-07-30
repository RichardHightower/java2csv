package com.cloudurable.docgen.mermaid.validation.sequence;

import com.cloudurable.docgen.mermaid.validation.LineRule;
import com.cloudurable.docgen.mermaid.validation.RuleResult;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParticipantAliasRule implements LineRule {
    private final String ALIAS_PATTERN = "^participant\\s+.+\\s+as\\s+.+$";
    private final Pattern PATTERN = Pattern.compile(ALIAS_PATTERN);

    private final String RULE_NAME = "Participant Alias Rule";
    private final String RULE_DESCRIPTION = "Avoid participant aliases in diagrams. Use original class/object names from code.";

    @Override
    public RuleResult check(String line, int lineNumber) {

        final Matcher matcher = PATTERN.matcher(line);

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
