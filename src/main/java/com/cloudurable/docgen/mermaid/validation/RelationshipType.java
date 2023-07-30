package com.cloudurable.docgen.mermaid.validation;


public enum RelationshipType {
    INHERITS_LEFT("<|--"),
    IMPLEMENTS_LEFT("<|.."),
    ASSOCIATION_LEFT("<--"),
    DEPENDENCY_LEFT("<.."),
    COMPOSITION("*--"),
    AGGREGATION("o--"),
    SOLID_LINK("--"),
    DEPENDENCY_RIGHT("..>"),
    ASSOCIATION_RIGHT("-->"),
    INHERITS_RIGHT("--|>"),
    IMPLEMENTS_RIGHT("..|>"),
    DASHED_LINK(".."),
    NOT_FOUND("NOT FOUND");

    private String symbol;

    RelationshipType(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return this.symbol;
    }

    public static RelationshipType parseType(String str) {
        for (RelationshipType type : RelationshipType.values()) {
            if (type.getSymbol().equals(str)) {
                return type;
            }
        }
        return NOT_FOUND;
    }

}
