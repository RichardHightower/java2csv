# com.bouqs.offerservice.entity.dto.response
## Class: SyncUpdaterResponse

**com.bouqs.offerservice.entity.dto.response.SyncUpdaterResponse**
### SyncUpdaterResponse

The `SyncUpdaterResponse` class is a data class that represents the response received from a sync updater operation. It is annotated with `@Setter`, `@Getter`, `@Builder`, `@NoArgsConstructor`, and `@AllArgsConstructor`.

#### Javadoc for Class:
None available.

#### Fields:
- `createMaterialResponses` (private List<CRUDResponse>): A list of responses for create material operations.
- `createMaterialOfferResponses` (private List<CRUDResponse>): A list of responses for create material offer operations.
- `createOfferResponses` (private List<CRUDResponse>): A list of responses for create offer operations.
- `updateMaterialResponses` (private List<CRUDResponse>): A list of responses for update material operations.
- `updateMaterialOfferResponses` (private List<CRUDResponse>): A list of responses for update material offer operations.
- `updateOfferResponses` (private List<CRUDResponse>): A list of responses for update offer operations.
- `deleteMaterialResponses` (private List<CRUDResponse>): A list of responses for delete material operations.
- `deleteMaterialOfferResponses` (private List<CRUDResponse>): A list of responses for delete material offer operations.
- `deleteOfferResponses` (private List<CRUDResponse>): A list of responses for delete offer operations.## Class: ProductsAvailabilityResponse

**com.bouqs.offerservice.entity.dto.response.ProductsAvailabilityResponse**
The ProductsAvailabilityResponse class is a Java class that represents a response for checking the availability of products. This class is annotated with various annotations like @Setter, @Getter, @Builder, @AllArgsConstructor, and @NoArgsConstructor to provide convenient setter and getter methods, a builder pattern for creating objects, and constructors with and without arguments. 

The class has the following methods:

None

The class has the following fields:

- deliverDate: A LocalDate object representing the delivery date. This field is annotated with @JsonInclude(JsonInclude.Include.NON_NULL) to exclude it from the response if it is null.
- regionId: A String representing the region ID. This field is annotated with @JsonInclude(JsonInclude.Include.NON_NULL) to exclude it from the response if it is null.
- zipCode: A String representing the zip code. This field is annotated with @JsonInclude(JsonInclude.Include.NON_NULL) to exclude it from the response if it is null.
- products: A Set of Strings representing the available products. 
- nextPage: A String representing the URL of the next page in the response.

This class can be used to deserialize a JSON response into a Java object and vice versa. It provides a convenient way to access the availability information of products.## Class: AvailableOffersResponse

**com.bouqs.offerservice.entity.dto.response.AvailableOffersResponse**
The AvailableOffersResponse class is a data model representing the response data for available offers. It is annotated with various Lombok annotations, such as @Getter, @Setter, @Builder, @AllArgsConstructor, and @NoArgsConstructor to automatically generate getter and setter methods, builder methods, and constructors.

This class has a number of fields including deliveryDate, regionId, zipCode, skuId, ofbizId, and offers. The deliveryDate field stores the date of delivery for the offer, while the regionId represents the region for which the offer is available. The zipCode field stores the zip code related to the offer, and the skuId and ofbizId fields represent the unique identifiers for the offer in the system. Additionally, the offers field is a list of OfferResponse objects, representing the available offers associated with this response.

Overall, the AvailableOffersResponse class serves as a container for the response data related to available offers and provides the necessary methods and fields to access and manipulate this data.## Class: FacilityDeliveryWindowsForShipMethodResponse

**com.bouqs.offerservice.entity.dto.response.FacilityDeliveryWindowsForShipMethodResponse**
# FacilityDeliveryWindowsForShipMethodResponse

The `FacilityDeliveryWindowsForShipMethodResponse` class represents the response for the facility delivery windows associated with a specific ship method. 

This class includes the following features:
- It uses the Lombok annotations `@Setter`, `@Getter`, `@NoArgsConstructor`, `@AllArgsConstructor`, and `@Builder` to automatically generate getters, setters, constructors, and a builder method.
- It has a private field `facilityId` of type `String` which represents the facility ID.
- It has a private field `shipMethodId` of type `String` which represents the ship method ID.
- It has a private field `deliveryWindows` of type `List<DeliveryWindowResponse>` which represents a list of delivery windows associated with the facility and ship method.

Note: For more details about each field and method, please refer to the class documentation.## Class: CartItemResponse

**com.bouqs.offerservice.entity.dto.response.CartItemResponse**
# CartItemResponse

The `CartItemResponse` class represents a response object for a cart item. It is annotated with `@Setter`, `@Getter`, `@NoArgsConstructor`, `@AllArgsConstructor`, `@Builder`, and `@ToString` to provide convenient getters/setters, constructors, builder methods, and string representation. 

This class has the following fields:

- `orderItemNumber` (type: `String`): The order item number for the cart item.
- `primaryProduct` (type: `ItemResponse`): The primary product associated with the cart item.
- `addOnProducts` (type: `List<ItemResponse>`): A list of additional products added to the cart item.
- `addOnAvailability` (type: `Map<String, List<String>>`): A map specifying the availability of add-on products.
- `deliveryDate` (type: `LocalDate`): The delivery date for the cart item.
- `deliveryWindows` (type: `DeliveryWindowResponse`): The delivery window options for the cart item.
- `destination` (type: `DestinationResponse`): The destination for the cart item.
- `reservationToken` (type: `String`): The reservation token for the cart item.
- `errorMessage` (type: `String`): An error message associated with the cart item, if any.
- `itemValid` (type: `boolean`): A flag indicating whether the cart item is valid.

This class is used to encapsulate the response information for a cart item, providing a convenient way to access and manipulate the data associated with it.## Class: MonthResponse

**com.bouqs.offerservice.entity.dto.response.MonthResponse**
# MonthResponse Class

The `MonthResponse` class is a representation of a month, typically used in software applications for managing date and time. This class is annotated with various annotations such as `@Builder`, `@AllArgsConstructor`, `@NoArgsConstructor`, `@Getter`, `@Setter`, and `@Generated`. These annotations provide convenient features like automatic generation of builder methods, getters and setters, and default constructors.

## Fields

- `year`: An integer field that stores the year associated with the month.
- `month`: An integer field that stores the numerical representation of the month.
- `days`: A list of `DayResponse` objects that represents the days within the month.

This class encapsulates the necessary information related to a specific month, allowing for easy manipulation and retrieval of month-related data within a software application.## Class: CartAvailabilityResponse

**com.bouqs.offerservice.entity.dto.response.CartAvailabilityResponse**
# Class Description: CartAvailabilityResponse

The `CartAvailabilityResponse` class is a representation of the response received for checking the availability of a cart. It is designed to store the response data related to the validity of a cart.

The class is annotated with various annotations like `@Getter`, `@Setter`, `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor`, and `@Generated`, which provide convenient accessor and mutator methods, enable builder pattern for object creation, and generate constructors for the class.

## Fields

- `isCartValid`: This private field of type `CartResponse` represents the validity of the cart. It holds information related to whether the cart is valid or not.

No additional methods or fields are mentioned in the given context.

This class serves as a data container to store the availability response of a cart, allowing for easy access and manipulation of the response data.## Class: MaterialOfferResponse

**com.bouqs.offerservice.entity.dto.response.MaterialOfferResponse**
# MaterialOfferResponse Class

The MaterialOfferResponse class is a subclass of the BaseMaterialOffer class. It represents a response object for a material offer. This class is annotated with the @Generated, @Getter, @Setter, @SuperBuilder, and @NoArgsConstructor annotations.

The MaterialOfferResponse class contains various methods and fields that provide functionality and data related to material offers. These methods and fields are not specified in the given context. 

This class serves as a response model for the material offer functionality, providing a structured representation of a material offer response. It may include features such as getters and setters for accessing and modifying the fields of the material offer, as well as additional methods for performing operations specific to material offers.

Note: The Javadoc for the class is not provided in the given context.## Class: DeliveryWindowResponse

**com.bouqs.offerservice.entity.dto.response.DeliveryWindowResponse**
# DeliveryWindowResponse

The **DeliveryWindowResponse** class is a representation of a delivery window response. It is annotated with various lombok annotations such as `@Setter`, `@Getter`, `@Builder`, `@AllArgsConstructor`, `@NoArgsConstructor`, and `@Generated`. 

This class has three fields: 
1. **startTime** - a private string field representing the start time of the delivery window.
2. **endTime** - a private string field representing the end time of the delivery window.
3. **cutoff** - a private string field representing the cutoff time of the delivery window.

This class provides a convenient way to represent and manipulate delivery window response data.## Class: OfferResponse

**com.bouqs.offerservice.entity.dto.response.OfferResponse**
## Class Description: OfferResponse

The `OfferResponse` class represents an offer response in a software system. It is annotated with `@Setter`, `@Getter`, `@SuperBuilder`, and `@NoArgsConstructor` to generate the necessary getters, setters, and constructors automatically.

The class has several fields that hold information related to the offer response. These fields include identifiers such as `id`, `facilityId`, `skuId`, `carrierMethodId`, `shipMethodId`, and `regionId`. There are also fields for `productId`, `deliveryDate`, `ofbizId`, `ecomId`, `ofbizFacilityId`, `facilityName`, `shipMethodName`, `timestamp`, `priority`, `cutoff`, `availableUnits`, `airSectors`, `zipCodeExemptions`, `timeZone`, `deliveryWindows`, `facilityRanking`, `facilityCapacity`, `facilityCapacityUsage`, `productionLeadTime`, `carrierMethodCapacity`, `carrierMethodCapacityUsage`, `version`, `expiration`, and `materials`.

This class is used to store and manipulate offer response data within the software system. It provides a convenient way to access and modify the various attributes of an offer response.## Class: ItemResponse

**com.bouqs.offerservice.entity.dto.response.ItemResponse**
# ItemResponse Class

The **ItemResponse** class is a software engineering class that extends the **BaseItem** class. It is annotated with `@Getter`, `@Setter`, `@SuperBuilder`, `@NoArgsConstructor`, and `@AllArgsConstructor`.

This class has a single private boolean attribute named **valid**. The purpose of this attribute is not specified in the class definition.

ItemResponse serves as a response object that can hold information related to an item. It is designed to provide a convenient way to encapsulate data and communicate between different components or systems. The class inherits the properties and behavior defined in the BaseItem class, which enables reusability and promotes a modular code structure.

It is important to note that the methods and additional fields of the class are not provided in the class definition, preventing us from fully understanding its functionality and intended use. Therefore, further information would be required to provide a more comprehensive description of the ItemResponse class.## Class: MaterialOfOfferResponse

**com.bouqs.offerservice.entity.dto.response.MaterialOfOfferResponse**
The MaterialOfOfferResponse class is a Java class that encapsulates information about a material offered in a system. This class utilizes several annotations such as @Setter, @Getter, @NoArgsConstructor, @AllArgsConstructor, and @Builder to generate setter and getter methods, a default constructor, a constructor with all fields, and a builder for creating instances of the class. 

The class has the following fields:

- id: a String representing the unique identifier of the material offer
- materialId: a String representing the identifier of the material
- inventoryLotId: a String representing the identifier of the inventory lot containing the material
- baseAPD: an integer representing the base average daily production of the material
- inventoryLotStart: a String representing the start date of the inventory lot
- inventoryLotEnd: a String representing the end date of the inventory lot
- inventoryLotCapacity: an integer representing the capacity of the inventory lot
- capacityUsage: an integer representing the current usage of the inventory lot's capacity
- materialUnit: an integer representing the unit of measurement for the material
- timestamp: a LocalDateTime object representing the date and time of the material offer response

This class serves as a representation of a material offer response and provides a convenient way to access and manipulate its attributes.## Class: CartResponse

**com.bouqs.offerservice.entity.dto.response.CartResponse**
# Class: CartResponse

The `CartResponse` class is a representation of a response that is returned by the application when interacting with a shopping cart. It is annotated with several annotations such as `@Setter`, `@Getter`, `@NoArgsConstructor`, `@AllArgsConstructor`, and `@Builder` to provide standard utility methods and functionality.

## Fields

- `items` (private `List<CartItemResponse>`): A list of `CartItemResponse` objects representing the items in the shopping cart.
- `allValid` (private `boolean`): A boolean value indicating whether all the items in the cart are valid.

This class acts as a container to hold the response data related to a shopping cart, providing access to the items contained in the cart and information about their validity.

**Note**: This description provides an overview of the class and its purpose. For more detailed information about the class and its methods, please refer to the class's Javadoc documentation.## Class: DayResponse

**com.bouqs.offerservice.entity.dto.response.DayResponse**
# DayResponse

The `DayResponse` class is a representation of a day's response in a software system. It is annotated with various Lombok annotations including `@Builder`, `@AllArgsConstructor`, `@NoArgsConstructor`, `@Getter`, and `@Setter` to provide convenient constructors, getters, and setters for its fields.

## Fields
- `day`: An integer representing the day.
- `timeZone`: A string representing the timezone.
- `cutoff`: A string representing the cutoff time.
- `deliveryDate`: An instance of `LocalDate` representing the delivery date.
- `deliveryWindows`: A list of `DeliveryWindowResponse` objects representing the delivery windows for the day.

This class is used to retrieve and manipulate information related to a day's response in a software system.## Class: RegionResponseList

**com.bouqs.offerservice.entity.dto.response.RegionResponseList**
## RegionResponseList Class

The `RegionResponseList` class is a Java class that represents a list of region responses. This class is annotated with various annotations like `@Builder`, `@Getter`, `@Setter`, `@NoArgsConstructor`, `@AllArgsConstructor`, `@Generated`, and `@JsonIgnoreProperties(ignoreUnknown = true)`.

### Methods

The `RegionResponseList` class does not have any specific methods defined.

### Fields

The `RegionResponseList` class has the following fields:

- `nextPage`: a private field of type `String` that represents the next page of the region response list.
- `regionResponses`: a private field of type `List<RegionResponse>` that holds a list of region responses.

The purpose of this class is to provide a container for storing and handling a list of region responses. It allows for easy manipulation and access to region response objects.## Class: RegionResponse

**com.bouqs.offerservice.entity.dto.response.RegionResponse**
# RegionResponse Class

The RegionResponse class is a Java class that represents a response object for a region-related operation. The class is annotated with various annotations such as `@Getter`, `@Setter`, `@NoArgsConstructor`, and `@JsonIgnoreProperties(ignoreUnknown = true)`.

It has no specific methods defined, but it may include standard getter and setter methods for accessing and modifying the class fields.

The class has the following fields:

- `shipMethodId` (String): Represents the ID of the shipping method associated with the region. This field is marked with the `@NotNull` annotation, indicating that it cannot be null.
- `region` (String): Represents the name or identifier of the region. This field is marked with the `@NotNull` annotation, indicating that it cannot be null.
- `days` (int): Represents the number of days it takes for shipping to the specified region. This field is marked with the `@NotNull` annotation, indicating that it cannot be null.

Please note that this description only provides an overview of the class and its fields. Further implementation details and usage information may be available in the accompanying code documentation or comments.## Class: MaterialResponse

**com.bouqs.offerservice.entity.dto.response.MaterialResponse**
# MaterialResponse

The `MaterialResponse` class is a subclass of the `BaseMaterial` class. It is annotated with several annotations like `@Setter`, `@Getter`, `@SuperBuilder`, `@NoArgsConstructor`, and `@Generated`, which provide additional functionality and behavior to the class.

This class serves as a response object for material-related operations in the software system. It encapsulates data and functionality related to materials, allowing for convenient retrieval and manipulation of material information. 

The `MaterialResponse` class may contain additional methods specific to material responses, but these are not specified in the given context. It may also have fields that store data pertaining to the material, but these are not described here either.

Overall, the `MaterialResponse` class represents a specialized class within the software system for handling material-related responses, providing a standardized and efficient approach to manage material information.## Class: DestinationResponse

**com.bouqs.offerservice.entity.dto.response.DestinationResponse**
# DestinationResponse Class

The `DestinationResponse` class is a subclass of `BaseDestination` and is used to represent a response for a destination in a software system. It is equipped with the following annotations: `@Getter`, `@Setter`, `@SuperBuilder`, and `@NoArgsConstructor` which provide convenient getter and setter methods, builder pattern support, and a no-args constructor respectively. 

This class encapsulates the information related to a destination response but does not provide any specific methods apart from the inherited methods from its superclass. It serves as a data structure to hold and manipulate the response attributes for a destination.

Please refer to the Javadoc documentation for further details on the class.## Class: DailyProductAvailabilityResponse

**com.bouqs.offerservice.entity.dto.response.DailyProductAvailabilityResponse**
The DailyProductAvailabilityResponse class is a Java class designed to represent the response for the availability of a product on a daily basis. It is annotated with various annotations like @Getter, @Setter, @Builder, @NoArgsConstructor, and @AllArgsConstructor to provide convenient methods and constructors for accessing and manipulating its fields.

The class has the following fields:

- productId: A private String variable representing the unique identifier for the product.

- fromDate: A private LocalDate variable representing the starting date for which the availability response is valid.

- toDate: A private LocalDate variable representing the end date till which the availability response is valid.

- regionId: A private String variable representing the identifier for the region. This field may be included or excluded from the response depending on the situation, and it is marked as @JsonInclude(JsonInclude.Include.NON_NULL) to exclude it when it has a null value.

- months: A private List<MonthResponse> variable representing the availability response for each month within the specified date range. MonthResponse is a separate class that provides information about the availability within a specific month.

Overall, the DailyProductAvailabilityResponse class provides a structured and comprehensive representation of the availability of a product on a daily basis, including the necessary fields and methods to process and handle the availability data.## Class: CRUDResponse

**com.bouqs.offerservice.entity.dto.response.CRUDResponse**
## Class Description: CRUDResponse

The `CRUDResponse` class is a Java class that is annotated with `@Setter`, `@Getter`, `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor`, and `@Generated`. This class is typically used in software engineering projects for handling responses related to CRUD (Create, Read, Update, Delete) operations.

This class has three private fields: `id`, `success`, and `message`. These fields are used to store information related to the outcome of a CRUD operation. The `id` field holds a unique identifier, the `success` field indicates whether the operation was successful or not, and the `message` field stores any additional information or error messages.

The `CRUDResponse` class provides encapsulation for these fields, allowing other classes to access and modify them using getter and setter methods. The class also includes a builder pattern, which provides a convenient way to construct instances of `CRUDResponse` with different combinations of values for the fields.

Overall, the `CRUDResponse` class plays an important role in representing and processing response data for CRUD operations, making it a fundamental component for software developers working on projects involving data manipulation and management.