package com.cloudurable.docgen.mermaid.validation;


import com.cloudurable.jai.util.JsonSerializer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RuleRunner {
    private final List<LineRule> lineRules;
    private final List<ContentRule> contentRules;

    private RuleRunner(Builder builder) {

        this.lineRules = builder.rules;
        this.contentRules = builder.contentRules;
    }

    public static Builder builder() {
        return new Builder();
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

    public List<RuleResult> checkContent(String content) {
        List<String> lines = List.of(content.split("\n"));
        List<RuleResult> ruleResults = new ArrayList<>();
        ruleResults.addAll(checkLines(lines));
        ruleResults.addAll(runContentRule(content));
        return ruleResults;
    }

    public List<RuleResult> checkLines(List<String> lines) {

        List<RuleResult> results = new ArrayList<>();
        for (int i = 0; i < lines.size(); i++) {
            final var line = lines.get(i);
            List<RuleResult> lineResults = runLineRule(i, line);
            results.addAll(lineResults);
        }
        return results;
    }

    private List<RuleResult> runLineRule(int i, String line) {
        List<RuleResult> results = new ArrayList<>();
        for (LineRule rule : lineRules) {
            RuleResult result = rule.check(line, i);

            if (result != RuleResult.SUCCESS) {
                results.add(result);
                //System.out.printf("%s %s %s %d %s\n", rule.getClass().getSimpleName(), result.getRuleName(), line, lineNumber, result == RuleResult.SUCCESS ? "success" : results);
            }

        }
        return results;
    }

    private List<RuleResult> runContentRule(String content) {
        List<RuleResult> results = new ArrayList<>();
        for (ContentRule rule : contentRules) {
            RuleResult result = rule.check(content);

            if (result != RuleResult.SUCCESS) {
                results.add(result);
                //System.out.printf("%s %s %s %d %s\n", rule.getClass().getSimpleName(), result.getRuleName(), line, lineNumber, result == RuleResult.SUCCESS ? "success" : results);
            }

        }
        return results;
    }

    public String checksAndReturnJson(List<String> lines) {
        return serializeRuleResults(checkLines(lines));
    }

    public static class Builder {
        private List<LineRule> rules = Collections.emptyList();
        private List<ContentRule> contentRules = Collections.emptyList();

        public Builder rules(List<LineRule> rules) {
            this.rules = rules;
            return this;
        }

        public RuleRunner build() {
            return new RuleRunner(this);
        }

        public Builder contentRules(List<ContentRule> contentRules) {
            this.contentRules = contentRules;
            return this;
        }
    }

}
