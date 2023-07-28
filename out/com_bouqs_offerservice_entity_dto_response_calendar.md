# com.bouqs.offerservice.entity.dto.response.calendar
![class diagram](./images/com_bouqs_offerservice_entity_dto_response_calendar.png)
## Class: ProductCalendarRegionResponse

**com.bouqs.offerservice.entity.dto.response.calendar.ProductCalendarRegionResponse**

```java
public class ProductCalendarRegionResponse 
```
# ProductCalendarRegionResponse Class

The `ProductCalendarRegionResponse` class represents a response object for retrieving product calendar information specific to a region. 

## Fields
- `region` (private String): Represents the name or identifier of the region for which the product calendar information is being retrieved.
- `shipDates` (private Set<ProductCalendarShipDateResponse>): Represents a set of ship dates associated with the region. Each ship date is represented by an instance of `ProductCalendarShipDateResponse` class.

This class encapsulates the data required to provide a comprehensive response to a client's request for product calendar information for a specific region.

Note: The description above assumes that the provided methods and fields are used within the class for internal processing or interaction with other classes.
## Class: ProductCalendarResponse

**com.bouqs.offerservice.entity.dto.response.calendar.ProductCalendarResponse**

```java
public class ProductCalendarResponse 
```
# ProductCalendarResponse Class

The `ProductCalendarResponse` class is a representation of a response object related to product calendar information. It is used to provide data about a specific product's calendar, including its ID, availability from a specific start date to an end date, and a list of calendar month responses that further detail availability on a monthly basis.

The class includes fields to store information such as the `productId` (representing the ID of the product), `fromDate` (representing the start date of availability), `toDate` (representing the end date of availability), and a `months` list (containing `ProductCalendarMonthResponse` objects that provide availability details on a monthly basis).

By using the `ProductCalendarResponse` class, software engineers can retrieve and handle product calendar information efficiently and effectively.
## Class: ProductCalendarDayResponse

**com.bouqs.offerservice.entity.dto.response.calendar.ProductCalendarDayResponse**

```java
public class ProductCalendarDayResponse 
```
The `ProductCalendarDayResponse` class is a representation of a calendar day for a product. It contains information about the day itself and the facilities available on that day. The `day` field stores the numeric value of the day. The `facilities` field is a list of `ProductCalendarFacilityResponse` objects, which represent the facilities available on that day. This class provides a convenient way to retrieve and manipulate information related to a specific day and its associated facilities in a product calendar system.
## Class: ProductCalendarFacilityResponse

**com.bouqs.offerservice.entity.dto.response.calendar.ProductCalendarFacilityResponse**

```java
public class ProductCalendarFacilityResponse 
```
## ProductCalendarFacilityResponse Class

The `ProductCalendarFacilityResponse` class is a Java class designed to represent a response object in a product calendar facility system. This class encapsulates the information related to a facility in the system.

The class has the following fields:

1. `facilityId` - a private string field that stores the unique identifier of the facility.

2. `shipMethods` - a private list of `ProductCalendarShipMethodResponse` objects that stores the available shipping methods for the facility.

The `ProductCalendarFacilityResponse` class is used to access and manipulate the information about a facility in the product calendar facility system.
## Class: ProductCalendarMonthResponse

**com.bouqs.offerservice.entity.dto.response.calendar.ProductCalendarMonthResponse**

```java
public class ProductCalendarMonthResponse 
```
The `ProductCalendarMonthResponse` class represents a response for a specific month in a product calendar. It contains information about the year, month, and a list of `ProductCalendarDayResponse` objects representing the days in that month. This class is primarily used to retrieve and display data related to product calendar months.
## Class: ProductCalendarShipMethodResponse

**com.bouqs.offerservice.entity.dto.response.calendar.ProductCalendarShipMethodResponse**

```java
public class ProductCalendarShipMethodResponse 
```
The `ProductCalendarShipMethodResponse` class is a Java class that represents a response object for shipping methods in a product calendar. This class contains information about the available shipping methods and their corresponding regions.

The class has two fields: 
- `shipMethodId`, which is a private string field representing the unique identifier for a specific shipping method.
- `regions`, which is a private list of `ProductCalendarRegionResponse` objects representing the regions where the shipping method is available.

The `ProductCalendarShipMethodResponse` class is designed to store and provide access to information regarding shipping methods and their corresponding regions in the context of a product calendar. It can be used to handle and manipulate shipping method data in an organized and structured manner.
## Class: ProductCalendarShipDateResponse

**com.bouqs.offerservice.entity.dto.response.calendar.ProductCalendarShipDateResponse**

```java
public class ProductCalendarShipDateResponse 
```
The ProductCalendarShipDateResponse class represents a response that contains ship date information for a product in a calendar format. This class has two fields, "date" and "timeZone", which will hold the date and time zone information respectively. The class does not contain any methods apart from the default constructor. This class is used to encapsulate and store ship date information for a product, making it easier to transmit and process this information within a software system.
