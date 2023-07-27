package com.cloudurable.docgen.mermaid.validation;


import com.cloudurable.jai.util.JsonSerializer;

import java.util.ArrayList;
import java.util.List;

public class RuleRunner {
    private List<Rule> rules;

    private RuleRunner(Builder builder) {
        this.rules = builder.rules;
    }

    public static class Builder {
        private List<Rule> rules;

        public Builder rules(List<Rule> rules) {
            this.rules = rules;
            return this;
        }

        public RuleRunner build() {
            return new RuleRunner(this);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public List<RuleResult> checks(List<String> lines) {
        List<RuleResult> results = new ArrayList<>();
        for (int i = 0; i < lines.size(); i++) {
            final var line = lines.get(i);
            List<RuleResult> lineResults = runRuleForLine(i, line);
            results.addAll(lineResults);
        }
        return results;
    }

    private List<RuleResult> runRuleForLine(int i, String line) {
        List<RuleResult> results = new ArrayList<>();
        for (Rule rule : rules) {
            RuleResult result = rule.check(line, i);

            if (result != RuleResult.SUCCESS) {
                results.add(result);
                //System.out.printf("%s %s %s %d %s\n", rule.getClass().getSimpleName(), result.getRuleName(), line, lineNumber, result == RuleResult.SUCCESS ? "success" : results);
            }

        }
        return results;
    }

    public String checksAndReturnJson(List<String> lines) {
        return serializeRuleResults(checks(lines));
    }

    public static String serializeRuleResults(List<RuleResult> ruleResults) {
        JsonSerializer jsonSerializer = new JsonSerializer();

        jsonSerializer.startArray();

        for (RuleResult ruleResult : ruleResults) {
            jsonSerializer.startNestedObjectElement();
            jsonSerializer.addAttribute("lineNumber", ruleResult.getLineNumber());
            jsonSerializer.addAttribute("violatedLine", ruleResult.getViolatedLine());
            jsonSerializer.addAttribute("ruleName", ruleResult.getRuleName());
            jsonSerializer.addAttribute("description", ruleResult.getDescription());
            jsonSerializer.endObject();
        }

        jsonSerializer.endArray();

        return jsonSerializer.toString();
    }

}
