# com.bouqs.offerservice.entity.dto
![class diagram](./images/com_bouqs_offerservice_entity_dto.png)
## Class: BaseDestination

**com.bouqs.offerservice.entity.dto.BaseDestination**

```java
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class BaseDestination 
```
The `BaseDestination` class is a Java class that represents a destination. It is designed to be used as a base class for other destination classes.

The class includes annotations such as `@Setter`, `@Getter`, `@NoArgsConstructor`, `@AllArgsConstructor`, and `@SuperBuilder`, which provide convenient ways to generate getters, setters, constructors, and builder methods for the fields of the class.

The class contains the following fields:
- `residential` (boolean) - indicates whether the destination is residential or not.
- `address1` (String) - the first line of the address.
- `address2` (String) - the second line of the address.
- `city` (String) - the city of the destination.
- `state` (String) - the state or province of the destination.
- `postalCode` (String) - the postal code or ZIP code of the destination.
- `country` (String) - the country of the destination.

These fields can be used to store the relevant information about a destination, such as its location and address details.

The purpose of the `BaseDestination` class is to provide a common structure and functionality for different types of destinations. Subclasses can extend this class and add additional fields or methods specific to their particular type of destination.
## Class: BaseItem

**com.bouqs.offerservice.entity.dto.BaseItem**

```java
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class BaseItem 
```
The `BaseItem` class represents an item in a software system. It is annotated with various annotations (`@Setter`, `@Getter`, `@NoArgsConstructor`, `@AllArgsConstructor`, `@SuperBuilder`) to provide convenient access to its methods and fields. 

The class has three fields: 
- `id`: This field stores the product ID or e-commerce ID associated with the item.
- `sku`: This field stores the SKU ID or OFBiz ID associated with the item.
- `quantity`: This field stores the quantity of the item.

Overall, the `BaseItem` class serves as a base class for representing items within the software system, providing access to relevant information such as product and SKU IDs, as well as quantity.
## Class: BaseMaterial

**com.bouqs.offerservice.entity.dto.BaseMaterial**

```java
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class BaseMaterial 
```
## Description

The `BaseMaterial` class is a software representation of a base material used in a system. It is a data structure with various fields and methods to facilitate managing and manipulating base material information. The class includes fields such as `id`, `materialId`, `inventoryLotId`, `baseAPD`, `inventoryLotStart`, `inventoryLotEnd`, `inventoryLotCapacity`, `capacityUsage`, `materialUnit`, `timestamp`, and `version`. These fields store relevant data about the base material, such as identification information, inventory lot details, capacity and usage information, material unit, timestamp, and version. The class also includes various annotations, such as `@Setter`, `@Getter`, `@NoArgsConstructor`, `@AllArgsConstructor`, and `@SuperBuilder`, which provide additional functionalities and behaviors to the class.
## Class: BaseMaterialOffer

**com.bouqs.offerservice.entity.dto.BaseMaterialOffer**

```java
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class BaseMaterialOffer 
```
**BaseMaterialOffer** is a class that represents a material offer in a software system. It is designed for use by software engineers and contains various methods and fields for manipulating and storing information related to material offers. This class has annotations such as @Setter, @Getter, @NoArgsConstructor, @AllArgsConstructor, and @SuperBuilder, which provide convenience methods and constructors for working with the class.

The class has the following methods for performing operations on material offers:
- No methods were mentioned in the class definition.

The class has the following fields for storing information about a material offer:
- **id**: A private string field that represents the unique identifier for the material offer.
- **skuOfferId**: A private string field that represents the identifier for the SKU offer associated with the material offer.
- **materialOfferId**: A private string field that represents the identifier for the material offer.
- **materialUnit**: A private integer field that represents the unit of measurement for the material offer.

Overall, the BaseMaterialOffer class provides a foundation for manipulating and storing material offers in a software system, making it a valuable asset for software engineers working on materials management systems.
