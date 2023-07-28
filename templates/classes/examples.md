# Input 1
```
public class Car extends Vehicle implements Automobile
Fields:
- private Engine engine
- private Set<Tire> tires
- private Driver driver
- private List<Insurance> insurance

public interface Vehicle
Fields:
- No fields

public interface Automobile
Fields:
- No fields

public abstract class Engine
Fields:
- private String manufacturer
- private int horsepower

public class Tire
Fields:
- private String manufacturer
- private String model

public class Driver
Fields:
- private License license

public class Insurance
Fields:
- private String provider
- private Date issued
- private Date expires
```

# Output 1
```mermaid
---
title: Car System Class Diagram
---
classDiagram

    class Car{
         Engine engine
         Set<Tire> tires
         Driver driver
         List<Insurance> insurance
    }
    class Engine{
        <<abstract>>
         String manufacturer
         int horsepower
    }
    class Vehicle {
        <<Interface>>
    }
    class Automobile {
        <<Interface>>
    }
    class Driver{
         License license
    }
    class Insurance{
         String provider
         Date issued
         Date expires
    }
    class Tire{
         String manufacturer
         String model
    }
    Vehicle <|-- Car: extends
    Automobile <|.. Car: implements
    Car*-- Engine: engine
    Car*-- "4" Tire: tires
    Car -- Driver: driver
    Car o-- "*" Insurance: insurance
```

# Input 2
```
public class Company
Fields:
- private List<Department> departments
- private Set<Building> buildings
- private Set<Vendor> vendors


public class Department
Fields:
- private List<Employee> employees
- private Company company

public class Building
Fields:
- private String buildingName
- private String location

public class Employee
Fields:
- private String name
- private Department department

public class Vendor
Fields:
- private String vendorName
- private Company company
```

# Output 2
```mermaid
---
title: Company System Class Diagram
---
classDiagram
    class Company{
         List<Department> departments
         Set<Building> buildings
         Set<Vendor> vendors
    }
    class Department{
         List<Employee> employees
         Company company
    }
    class Building{
         String buildingName
         String location
    }
    class Employee{
         String name
         Department department
    }
    class Vendor{
         String vendorName
         Company company
    }

    Company o-- "*" Department: departments
    Company o-- "*" Building: buildings
    Company o-- "*" Vendor: vendors
    Department -- Company: company
    Vendor -- Company: company
    Department o-- "*" Employee: employees
    Employee -- Department: department
```

# Input 3
```
public class Motorcycle extends Vehicle
Fields:
- private Engine engine
- private Set<Wheel> wheels
- private Driver driver
- private Helmet helmet
- private Gloves gloves
- private Gas gas
- private Oil oil

public abstract class Vehicle
Fields:
- No fields

public interface Driving
Fields:
- No fields

public class Engine
Fields:
- private String manufacturer
- private int horsepower

public class Wheel
Fields:
- private String manufacturer
- private float radius

public class Driver implements Driving
Fields:
- private License license

public class Helmet
Fields:
- private String brand

public class Gloves
Fields:
- private String brand

public class Gas
Fields:
- private float volume

public class Oil
Fields:
- private float volume
```

# Output 3
```mermaid
---
title: Motorcycle System Class Diagram
---
classDiagram

    class Motorcycle{
         Engine engine
         Set<Wheel> wheels
         Driver driver
         Helmet helmnet
         Gloves gloves
         Gas gas
         Oil oil
    }
    class Vehicle {
        <<Abstract>>
    }
    class Driving {
        <<Interface>>
    }
    class Engine{
         String manufacturer
         int horsepower
    }
    class Wheel{
         String manufacturer
         float radius
    }
    class Driver{
         License license
    }
    class Helmet {
         String brand
    }
    class Gloves {
         String brand
    }
    class Gas{
         float volume
    }
    class Oil{
         float volume
    }
    Vehicle <|-- Motorcycle: extends
    Driving <|.. Driver: implements
    Motorcycle*-- Engine: engine
    Motorcycle*-- "2" Wheel: wheels
    Motorcycle -- Driver: driver
    Motorcycle o-- Helmet: helmet
    Motorcycle o-- Gloves: gloves
    Motorcycle o-- Gas: gas
    Motorcycle o-- Oil: oil
``` 

# Input 4
```
public class Building extends RealEstate
Fields:
- private Set<Floor> floors
- private Room room
- private Door door
- private Window window

public DriveThru extends Building
Fields:
- private Menu menu

public abstract RealEstate
Fields:
- private String address
- private double price

public interface Structure
Fields:
- No fields


public class Room
Fields:
- private Set<Desk> desks
- private Set<Chair> chairs

public class Door
Fields:
- private String material
- private Dimension dimension

public class Window
Fields:
- private String material
- private Dimension dimension

public class Floor
Fields:
- private String type
- private List<Window> windows

public class Desk
Fields:
- private double height

public class Chair
Fields:
- private double height

public class Menu 
Fields:
- private List<Item> items
```

# Output 4
```mermaid
---
title: Structure System Class Diagram
---

classDiagram

    class Building{
         Set<Floor> floors
         Room room
         Door door
         Window window
    }
    class DriveThru{
         Menu menu
    }
    class RealEstate {
        <<Abstract>>
    }
    class Structure {
        <<Interface>>
    }
    class Floor{
         String type
         List<Window> windows
    }
    class Room {
         Set<Desk> desks
         Set<Chair> chairs
    }
    class Door{
         String material
         Dimension dimension
    }
    class Window {
         String material
         Dimension dimension
    }
    class Desk {
        <<Structure>>
        double height
    }
    class Chair {
        <<Structure>>
        double height
    }
    class Menu{
         List<Item> items
    }
    RealEstate <|-- Building: extends
    Structure <|.. Desk: implements
    Structure <|.. Chair: implements
    Building <|-- DriveThru: extends
    Building*-- "*" Floor: floors
    Building -- Room: room
    Building -- Door: door
    Building -- Window: window
    DriveThru o-- Menu: menu
    Room o-- "*" Desk: desks
    Room o-- "*" Chair: chairs
    Floor o-- "*" Window: windows
```

# Input 5
```
public abstract class Machine
Fields:
- private String model
- private String manufacturer
- private PowerSupply powerSupply

public class Laptop extends Machine
Fields:
- private Processor processor
- private RAM ram
- private HardDisk hardDisk
- private Monitor monitor
- private Keyboard keyboard
- private Mouse mouse

public interface Portable
Fields:
- No fields

public class Processor
Fields:
- private String manufacturer
- private String model
- private int coreCount
- private float clockSpeed

public class RAM
Fields:
- private String manufacturer
- private String model
- private int capacity
- private int speed

public class HardDisk
Fields:
- private String manufacturer
- private String model
- private int capacity
- private float speed

public class Monitor
Fields:
- private String manufacturer
- private String model
- private Dimension resolution

public class Keyboard
Fields:
- private String manufacturer
- private String model

public class PowerSupply
Fields:
- private String manufacturer
- private String model
- private int wattage

public class Mouse
Fields:
- private String manufacturer
- private String model
```

# Output 5
```mermaid
---
title: Laptop System Class Diagram
---
classDiagram

    class Machine{
        <<Abstract>>
         String model
         String manufacturer
         PowerSupply powerSupply
    }
    class Laptop{
         Processor processor
         RAM ram
         HardDisk hardDisk
         Monitor monitor
         Keyboard keyboard
         Mouse mouse
    }
    class Portable {
        <<Interface>>
    }
    class Processor{
         String manufacturer
         String model
         int coreCount
         float clockSpeed
    }
    class RAM{
         String manufacturer
         String model
         int capacity
         int speed
    }
    class HardDisk{
          String manufacturer
          String model
          int capacity
          int speed
    }
    class Monitor{
          String manufacturer
          String model
          Dimension resolution
    }
    class Keyboard{
          String manufacturer
          String model
    }
    class PowerSupply{
          String manufacturer
          String model
          int wattage
    }
    class Mouse{
          String manufacturer
          String model
    }

    Machine <|-- Laptop: extends
    Portable <|.. Laptop: implements
    Machine -- PowerSupply: powerSupply
    Laptop o-- Processor: processor
    Laptop o-- RAM: ram
    Laptop o-- HardDisk: hardDisk
    Laptop o-- Monitor: monitor
    Laptop o-- Keyboard: keyboard
    Laptop o-- Mouse: mouse
```
