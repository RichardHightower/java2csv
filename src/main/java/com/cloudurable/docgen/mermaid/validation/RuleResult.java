package com.cloudurable.docgen.mermaid.validation;

import com.cloudurable.jai.util.JsonSerializer;

/**
 * The RuleResult class encapsulates the result of a validation rule being applied to a line of text.
 * This class is typically used to provide feedback about a rule violation,
 * including the line number, the violated line itself, the name of the rule, and a description of the rule.
 * It uses the Builder pattern for object creation.
 */
public class RuleResult {

    public static RuleResult SUCCESS = RuleResult.builder().ruleName("pass")
            .description("success").violatedLine("n/a").build();

    private final int lineNumber;
    private final String violatedLine;
    private final String ruleName;
    private final String description;

    /**
     * Private constructor.
     * @param builder The Builder class used to build the RuleResult object.
     */
    private RuleResult(final Builder builder) {
        this.lineNumber = builder.lineNumber;
        this.violatedLine = builder.violatedLine;
        this.ruleName = builder.ruleName;
        this.description = builder.description;
    }

    /**
     * Builder class for RuleResult.
     * This class follows the Builder Pattern, and is used to construct the RuleResult object.
     */
    public static class Builder {
        private int lineNumber;
        private String violatedLine;
        private String ruleName;
        private String description;

        /**
         * Set line number.
         * @param lineNumber The line number where the rule was violated.
         * @return This Builder object.
         */
        public Builder lineNumber(int lineNumber) {
            this.lineNumber = lineNumber;
            return this;
        }

        /**
         * Set violated line.
         * @param violatedLine The actual line of text where the rule was violated.
         * @return This Builder object.
         */
        public Builder violatedLine(String violatedLine) {
            this.violatedLine = violatedLine;
            return this;
        }

        /**
         * Set rule name.
         * @param ruleName The name of the violated rule.
         * @return This Builder object.
         */
        public Builder ruleName(String ruleName) {
            this.ruleName = ruleName;
            return this;
        }

        /**
         * Set violation description.
         * @param violationDescription The description of the violated rule.
         * @return This Builder object.
         */
        public Builder description(String violationDescription) {
            this.description = violationDescription;
            return this;
        }

        /**
         * Construct the RuleResult object using the current state of the Builder.
         * @return A newly-constructed RuleResult object.
         */
        public RuleResult build() {
            return new RuleResult(this);
        }
    }

    /**
     * Returns a new instance of Builder class.
     * @return A new Builder object.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Serializes this RuleResult into a JSON string.
     * @return A JSON string representing this RuleResult.
     */
    public String serialize() {
        final JsonSerializer jsonSerializer = new JsonSerializer();
        jsonSerializer.startObject();
        jsonSerializer.addAttribute("lineNumber", this.lineNumber);
        jsonSerializer.addAttribute("violatedLine", this.violatedLine);
        jsonSerializer.addAttribute("ruleName", this.ruleName);
        jsonSerializer.addAttribute("description", this.description);
        jsonSerializer.endObject();
        return jsonSerializer.toString();
    }

    /**
     * Retrieves the line number of the violated rule.
     * @return The line number of the violated rule.
     */
    public int getLineNumber() {
        return lineNumber;
    }

    /**
     * Retrieves the line that violated the rule.
     * @return The line that violated the rule.
     */
    public String getViolatedLine() {
        return violatedLine;
    }

    /**
     * Retrieves the name of the violated rule.
     * @return The name of the violated rule.
     */
    public String getRuleName() {
        return ruleName;
    }

    /**
     * Retrieves the description of the rule result.
     * @return The description of the rule result.
     */
    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "RuleResult{" +
                "lineNumber=" + lineNumber +
                ", violatedLine='" + violatedLine + '\'' +
                ", ruleName='" + ruleName + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
