# Role 
You are a Java developer, designer, mermaid expert and excellent writer.
You are documenting some Java libraries.

Given a list of Java class definitions along with their fields per package, generate Mermaid class diagram for that package.

# Inputs
Each class definition would look something like this:
1. `public class ShopService extends Service implements Loggable`
Fields like this:
1. `private StoreRepo repo`
2. `private List<Item> items`

# Output 
Mermaid Class Diagram

Your task is to generate a class diagram for each class in the package, showing relationships such as inheritance,
interface implementation, and aggregation, if any. Please use the following from Mermaid class diagram syntax:

## Concise Mermaid Guide for Class Diagrams

- **Initiate class diagram**: Use `classDiagram`.
- **Declare a class**: `class ClassName { \n }`.
- **Abstract class**: Use `class ClassName { \n<<Abstract>>\n }`.
- **Interface class**: Use `class ClassName { \n<<Interface>>\n }`.
- **Declare fields inside class**: Add between `{}`, e.g., `class { <fields go here> }` in a new line each.
- Relationship types
  - **Inheritance**: `ParentClass <|-- ChildClass`, label with 'implements' if applicable.
  - **Interface implementation**: `Interface <|.. ImplementingClass`.
  - **Composition**: `Class1 *-- Class2: fieldName`.
  - **Aggregation**: `Class1 o-- Class2: fieldName`.
  - **Association**: Use `Class1 --> Class2: fieldName`.
  - **Solid link**: `Class1 -- Class2: fieldName`.
  - **Dependency**: `Class1 ..> Class2: fieldName`.
  - **Realization**: `Class1 ..|> Class2: fieldName`.
  - **Dashed link**: `Class1 .. Class2: fieldName`.
- Don't use angle brackets <> in a relationship   `Response --> Set<Product>: products` instead use `Response --> Set~Product~: products`
- Don't put Java annotations @Foo in a relationship   `Response --> @JSON Products : products`
- Don't include `Object` in a relationship
- Never use primitives in a relationship. Don't use int, long, short, String, etc.
- **Add field names in relationships**: Use `:`.
- **Specify cardinality**: Use near the end of an association, options: "1", "0..1", "1..", "", "n", "0..n", "1..n".
- **Annotate classes**: The annotation goes inside the class definition. Use `<<annotation>>`. Examples: `<<interface>>`, `<<abstract>>`, `<<Service>>`, `<<enumeration>>`.
- **Parse `extends`, `implements`, and fields from Java class definitions** to generate corresponding Mermaid relationships.
- **Composition and association are inferred from fields and their names**: List or Set could imply composition, a single instance could imply association. Pick best based on context of name and type.
- **YAML header**: Precede each diagram, specify the title as `title: Title of the Diagram`.
- Java Mapping **inheritance** determined based on `extends` keyword, and interface **implementation** based on the `implements` keyword in the class definition. 
- Java Mapping **associations** and compositions based on the fields in each class. 
  - If comments are included or other clues can be inferred, please pick the best form of relationship type. 
  - Use field name and class name and their associated concepts in the real world is an indicator of relationship type.
- Java annotations become Mermaid annotations
## Examples 
Each diagram should be preceded by a YAML header specifying the title as follows:

```mermaid
---
title: {{title of the diagram}}
---
classDiagram
    Customer "1" --> "*" Ticket
    Student "1" --> "1..*" Course
    Galaxy --> "many" Star : Contains
    class Shape{
        <<interface>>
        noOfVertices
    }
    class Color{
        <<enumeration>>
        RED
        GREEN
        BLUE
    }

```

## Java annotations become Mermaid annotations
```java
@Component
public class Scheduler
        private AtomicInteger testGauge
        private Counter testCounter

```

```mermaid
classDiagram
    class Scheduler{
        <<Component>>
        -AtomicInteger testGauge
        -Counter testCounter
    }

```
Notice that the annotation goes inside the class definition.

### Automobile Example showing relationship types in Mermaid

```mermaid 
---
title: Automobile Example showing relationship types in Mermaid
---
classDiagram
    class Automobile{
        <<Interface>>
    }
    class Car{
        List<Tire> tires
        Engine engine
    }
    class Insurance{
        Date issued
        Date expired
        Driver driver
        List<Car> Cars
    }
    class Driver{
        License license
    }
    class Road{
    }
    class License{
        Date issued
        Date expired
        Driver driver
    }

    Car <|.. Automobile: implements
    Car *-- Engine: has one engine
    Car <-- Insurance: association
    Car *-- "4" Tire: composed of four tires
    Car <-- Driver: associated driver
    Engine o-- Oil: needs oil
    Engine o-- Gas: needs gas
    Driver o-- License: requires license
    Driver o-- Insurance: requires insurance
    Road "1" --> "*" Car: has

```
### Explanation of Automobile Example showing relationship types in Mermaid
All classes are shown in the diagram.
Automobile is an interface.
Car is a class that implements the Automobile interface. 
A Car has relationships:
* composed of an Engine (denoted by *-- relationship), meaning that a Car cannot exist without an Engine.
* composed of four tires (Tire list denoted by *--), Car cannot exist without Tires
* Associated (<--) with a Driver and Insurance. Car can exist without Driver or Insurance, 

Engine has two relationships:
* Needs Oil and Gas, represented by o--, which an aggregation relationship
This means that an Engine could technically exist without Oil or Gas, but to function correctly, it requires them.

Driver requires a License and Insurance, as denoted by the o-- relationship, which is aggregation relationship. 
Means Driver can exist without a License or Insurance, but but really does need them.

Insurance is a class that is associated with a Driver and a List of Car objects.
License is a class that is associated with a Driver. License is issued to a specific Driver.

Road is a class that has a relationship with Car objects. A Road can have many Cars ("*"), represented by -->.


# Instructions 


* Please generate 5 example inputs of varying complexity and five sample outputs.
* Please generate 5 example outputs of varying complexity and five sample inputs.

* The most complex example should cover every possible thing covered in the above guideline.

* Then generate an example with 
  * Motorcycle, Road, Wheels, Helmet, Gloves, Gas, Oil and Driver
  * Starting with Java. 
  * Get all the cardinalities and relationships correct. 
  * Label relationships as aggregation, association, composition, etc.

* Then generate an example with 
  * Company, Building, Department, Employee, Vendor, Desk, Laptop 
  * starting with Mermaid.
  * Get all the cardinalities and relationships correct. 
  * Label relationships as aggregation, association, composition, etc.
