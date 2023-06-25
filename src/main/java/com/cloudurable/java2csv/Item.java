package com.cloudurable.java2csv;

public class Item {
    private final String importBody;
    private final String body;
    private final String javadoc;
    private final String name;

    private final String simpleName;
    private final String definition;
    private final Item parent;

    private final JavaItemType type;

    public Item(String importBody, String body, String javadoc, String name, String simpleName, String definition, Item parent, JavaItemType type) {
        this.importBody = importBody;
        this.body = body;
        this.javadoc = javadoc;
        this.name = name;
        this.simpleName = simpleName;
        this.definition = definition;
        this.parent = parent;
        this.type = type;
    }

    public String getImportBody() {
        return importBody;
    }

    public String getBody() {
        return body;
    }

    public String getJavadoc() {
        return javadoc;
    }

    public String getName() {
        return name;
    }

    public String getDefinition() {
        return definition;
    }

    public Item getParent() {
        return parent;
    }

    public JavaItemType getType() {
        return type;
    }

    @Override
    public String toString() {
        return "Item{" +
                "simpleName='" + simpleName + '\'' +
                ", type=" + type +
                ", name='" + name + '\'' +
                ", definition='" + definition + '\'' +
                ", parent=" + parent +
                ", importBody='" + importBody + '\'' +
                ", javadoc='" + javadoc + '\'' +
                ", body='" + body + '\'' +
                '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Builder () {
        }
        private String body;
        private String javadoc;
        private String name;
        private String definition;
        private Item parent;
        private  String importBody;


        private  String simpleName;

        public Builder simpleName(String simpleName) {
            this.simpleName = simpleName;
            return this;
        }

        private JavaItemType type;

        public Builder type(JavaItemType type) {
            this.type = type;
            return this;
        }

        public Builder importBody(String importBody) {
            this.importBody = importBody;
            return this;
        }

        public Builder body(String body) {
            this.body = body;
            return this;
        }

        public Builder javadoc(String javadoc) {
            this.javadoc = javadoc;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder definition(String definition) {
            this.definition = definition;
            return this;
        }

        public Builder parent(Item parent) {
            this.parent = parent;
            return this;
        }

        public Item build() {
            return new Item(importBody, body, javadoc, name, simpleName, definition, parent, type);
        }
    }

}
