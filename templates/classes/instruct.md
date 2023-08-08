
# Guidelines for Mermaid Class Diagram Generation
* Produce documentation with Mermaid class diagrams.
* Do not put annotations outside of a class block
## Concise Mermaid Guide for Class Diagrams
- **Declare a class**: `class ClassName { \n }`.
- **Abstract class**: Use `class ClassName { \n<<Abstract>>\n }`.
- **Interface class**: Use `class ClassName { \n<<Interface>>\n }`.
- **Specify cardinality**: Use near the end of an association, options: "1", "0..1", "1..", "", "n", "0..n", "1..n".
- **Composition and association are inferred from fields and their names**: List or Set could imply composition, a single instance could imply association. Pick best based on context of name and type.
- **Inheritance**: `ParentClass <|-- ChildClass`, label with 'implements' if applicable.
- **Interface implementation**: `Interface <|.. ImplementingClass`.
- **Composition**: `Class1 *-- Class2: fieldName`.
- **Aggregation**: `Class1 o-- Class2: fieldName`.
- **Association**: Use `Class1 --> Class2: fieldName`.
- Don't use <> in a relationship  
  - WRONG:`Response --> Set<Product>: products` 
  - CORRECT: `Response "one"--"many"> products : Set of Products`
- Don't put Java annotations @Foo in a relationship   `Response --> @JSON Products : products`
- Don't use angle brackets <> in a relationship   `Response --> Set<Product>: products` instead use `Response --> Set~Product~: products`
- Never use primitives in a relationship. Don't use int, long, short, String, etc.
- Don't include `Object` in a relationship


# Instruction
Generate a mermaid class diagram based on the above guidelines titled {{TITLE}} using the code below with these steps.

1. List the classes in the diagram 
2. List the relationships in the diagram
3. Ensure that there are no angle brackets <> in the relationships use the form `Response "one"--"many"> products : Set of Products` instead.
4. Ensure that there are no primitives in the relationships. Don't use int, long, short, String, etc.
5. After you finish with the list, then generate the diagram based on the code below. 

# Java code

```java

{{JAVA_CODE}}

```


