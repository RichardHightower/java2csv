# com.bouqs.offerservice.entity.dto.request
## Class: CartRequest

**com.bouqs.offerservice.entity.dto.request.CartRequest**

```java
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartRequest 
```
## CartRequest Class

The `CartRequest` class is a data representation of a shopping cart request. It is used to store information related to a cart, including the items, order number, current step in the ordering process, channel of the order, and a flag indicating whether the final check has been completed.

This class provides getter and setter methods for accessing and modifying the fields. It also provides constructors for creating instances of the class with different combinations of fields. An additional builder pattern is available for creating instances with a more readable and concise syntax.

The `CartRequest` class has the following fields:

- `items`: A list of `CartItemRequest` objects representing the items in the cart.
- `orderNumber`: A string representing the unique identifier of the order.
- `step`: A string indicating the current step in the ordering process.
- `channel`: A string representing the channel or platform through which the order was made.
- `finalCheck`: A boolean flag indicating whether the final check has been completed.

Overall, the `CartRequest` class is a convenient and efficient way to handle and manipulate shopping cart requests within a software system.
## Class: UsageUpdateRequest

**com.bouqs.offerservice.entity.dto.request.UsageUpdateRequest**

```java
public class UsageUpdateRequest implements UpdateRequest 
```
UsageUpdateRequest is a class that represents a request for updating usage information. This class implements the UpdateRequest interface. 

The UsageUpdateRequest class has several methods and fields. One of the methods is handleUpdate, which takes in an OfferService object and returns a SyncUpdaterResponse object. This method is responsible for handling the update request and performing the necessary operations using the OfferService.

The fields in the UsageUpdateRequest class are used to store various information related to the update request. These include facilityId, shipDate, skuId, carrierMethodId, shipMethodId, regionId, ofbizId, timestamp, facilityQuantity, carrierMethodQuantity, and materials. These fields are used to provide the necessary data for the update operation.

Overall, the UsageUpdateRequest class encapsulates the functionality and data required for updating usage information.
### Method: handleUpdate
```java
@Override
public SyncUpdaterResponse handleUpdate(OfferService offerService) {
    Map<String, MaterialEntity> newMaterialEntitiesMap = getInsertingMaterialEntityIdMap();
    Map<String, OfferEntity> newOfferEntitiesMap = getInsertingOfferEntityIdMap();
    Map<String, OfferEntity> oldOfferEntitiesMap = offerService.getOfferEntitiesByIds(Collections.singleton(Utils.buildOfferEntityId(facilityId, shipDate, skuId, carrierMethodId, shipMethodId, regionId)));
    Map<String, MaterialOfferEntity> oldMaterialOfferEntitiesMap = offerService.getMaterialOfferEntitiesByOfferEntityIds(oldOfferEntitiesMap.keySet());
    Map<String, MaterialEntity> oldMaterialEntitiesMap = offerService.getMaterialEntitiesByIds(oldMaterialOfferEntitiesMap.values().stream().map(MaterialOfferEntity::getMaterialOfferId).collect(Collectors.toSet()));
    Set<MaterialEntity> updateMaterials = offerService.updateMaterial(newMaterialEntitiesMap, oldMaterialEntitiesMap, this);
    Set<OfferEntity> updateOffers = offerService.updateOffers(newOfferEntitiesMap, oldOfferEntitiesMap, this);
    return offerService.handleUpdateOffers(Collections.emptySet(), Collections.emptySet(), Collections.emptySet(), updateMaterials, Collections.emptySet(), updateOffers, Collections.emptySet(), Collections.emptySet(), Collections.emptySet());
}
```

### handleUpdate Overview 

The `handleUpdate` method in the `UsageUpdateRequest` class is responsible for updating offers and materials in the offer service. 

Based on the provided body, the method performs the following steps:
1. It retrieves a map of new material entities and a map of new offer entities.
2. It retrieves a map of old offer entities based on a specific set of identifiers.
3. It retrieves a map of old material offer entities based on the keys of the old offer entities map.
4. It retrieves a map of old material entities based on the values of the old material offer entities map.
5. It calls the `updateMaterial` method of the offer service, passing in the new material entities map, old material entities map, and `this`.
6. It calls the `updateOffers` method of the offer service, passing in the new offer entities map, old offer entities map, and `this`.
7. Finally, it calls the `handleUpdateOffers` method of the offer service, passing in various empty sets and the updated materials and offers.

In summary, the `handleUpdate` method handles the update of materials and offers in the offer service based on the provided data.


### handleUpdate Step by Step  

The `handleUpdate` method in the `com.bouqs.offerservice.entity.dto.request.UsageUpdateRequest` class performs the following steps based on the provided body:

1. It retrieves a map of new material entities using the `getInsertingMaterialEntityIdMap` method.
2. It retrieves a map of new offer entities using the `getInsertingOfferEntityIdMap` method.
3. It retrieves a map of old offer entities using the `offerService.getOfferEntitiesByIds` method, which takes a set of offer entity IDs as input. The set of offer entity IDs is generated using the `Utils.buildOfferEntityId` method with specific parameters.
4. It retrieves a map of old material offer entities using the `offerService.getMaterialOfferEntitiesByOfferEntityIds` method, which takes the key set of the old offer entities map as input.
5. It retrieves a map of old material entities using the `offerService.getMaterialEntitiesByIds` method, which takes a set of material offer IDs as input. The set of material offer IDs is generated by extracting the material offer ID from each value of the old material offer entities map.
6. It calls the `offerService.updateMaterial` method, passing the new material entities map, old material entities map, and the current instance of `this` as arguments. This method returns a set of updated material entities.
7. It calls the `offerService.updateOffers` method, passing the new offer entities map, old offer entities map, and the current instance of `this` as arguments. This method returns a set of updated offer entities.
8. Finally, it returns the result of the `offerService.handleUpdateOffers` method, passing various sets (including empty sets) and the sets of updated materials and offers obtained from the previous steps as arguments.

This method essentially performs an update operation on the offer service by updating material and offer entities based on the provided input.

sequenceDiagram
    participant SyncUpdater
    participant OfferService
    participant MaterialEntity
    participant OfferEntity
    participant MaterialOfferEntity
    
    SyncUpdater->>OfferService: getInsertingMaterialEntityIdMap()
    SyncUpdater->>OfferService: getInsertingOfferEntityIdMap()
    SyncUpdater->>OfferService: getOfferEntitiesByIds()
    OfferService-->>SyncUpdater: Return oldOfferEntitiesMap
    SyncUpdater->>OfferService: getMaterialOfferEntitiesByOfferEntityIds()
    OfferService-->>SyncUpdater: Return oldMaterialOfferEntitiesMap
    SyncUpdater->>OfferService: getMaterialEntitiesByIds()
    OfferService-->>SyncUpdater: Return oldMaterialEntitiesMap
    SyncUpdater->>OfferService: updateMaterial()
    SyncUpdater->>OfferService: updateOffers()
    SyncUpdater->>OfferService: handleUpdateOffers()
    OfferService-->>SyncUpdater: Return SyncUpdaterResponse

## Class: DestinationRequest

**com.bouqs.offerservice.entity.dto.request.DestinationRequest**

```java
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class DestinationRequest extends BaseDestination 
```
The `DestinationRequest` class is a subclass of `BaseDestination` and is designed for handling requests related to destinations. This class is annotated with `@Getter`, `@Setter`, `@SuperBuilder`, and `@NoArgsConstructor` which provide convenient getters, setters, and builders for its fields. The purpose of this class is to encapsulate the information and functionality needed for handling destination requests. It inherits all the fields and methods from its superclass `BaseDestination` and may have additional fields and methods specific to destination requests.
## Class: OfferUpdate

**com.bouqs.offerservice.entity.dto.request.OfferUpdate**

```java
@JsonIgnoreProperties(ignoreUnknown = true)
public class OfferUpdate 
```
# OfferUpdate Class

The OfferUpdate class represents a data structure used for the "/offer/updater/offer" API endpoint. It is part of the OfferUpdateRequest and is used in the OfferController.syncUpdaterOffer method.

The class has the following fields:
- facilityId: the ID of the facility.
- shipDate: the date of shipment.
- skuId: the ID of the SKU.
- carrierMethodId: the ID of the carrier method.
- shipMethodId: the ID of the shipping method.
- regionId: the ID of the region.
- ofbizId: the ID in the OFBIZ system.
- materialId: the ID of the material.
- inventoryLotId: the ID of the inventory lot.
- timestamp: a timestamp.
- productId: the ID of the product.
- ecomId: the ID in the E-commerce system.
- ofbizFacilityId: the ID of the OFBIZ facility.
- facilityName: the name of the facility.
- shipMethodName: the name of the shipping method.
- facilityCapacity: the capacity of the facility.
- carrierMethodCapacity: the capacity of the carrier method.
- deliveryDate: the date of delivery.
- deliveryWindows: the windows for delivery.
- cutoff: the cutoff time.
- airSectors: the air sectors.
- zipCodeExemptions: the exemptions for zip codes.
- baseAPD: the base APD (Advanced Planning and Scheduling) value.
- priority: the priority.
- facilityRanking: the ranking of the facility.
- productionLeadTime: the lead time for production.
- inventoryLotStart: the start date of the inventory lot.
- inventoryLotEnd: the end date of the inventory lot.
- inventoryLotCapacity: the capacity of the inventory lot.
- materialUnit: the unit of the material.
- materialUsage: the usage of the material.
- facilityTimeZone: the time zone of the facility.
- essentialMaterialIds: a list of IDs for essential materials.

The OfferUpdate class also includes a buildOfferEntity method and a private static final Logger named log.

Note: This class uses the @JsonIgnoreProperties(ignoreUnknown = true) annotation to ignore unknown properties when deserializing JSON.
### Method: buildOfferEntity
```java
public OfferEntity buildOfferEntity() {
    String id = Utils.buildOfferEntityId(getFacilityId(), getShipDate(), getSkuId(), getCarrierMethodId(), getShipMethodId(), getRegionId());
    List<DeliveryWindow> deliveryWindows;
    try {
        Json json = JsonFactory.getJson();
        deliveryWindows = Arrays.asList(json.toObject(getDeliveryWindows(), DeliveryWindow[].class));
    } catch (JsonException e) {
        log.warn("unable to parse delivery windows json to object: " + getDeliveryWindows(), e);
        deliveryWindows = new ArrayList<>();
    }
    return OfferEntity.builder().id(id).facilityId(getFacilityId()).shipDate(LocalDate.parse(getShipDate())).skuId(getSkuId()).carrierMethodId(getCarrierMethodId()).shipMethodId(getShipMethodId()).regionId(getRegionId()).ofbizId(getOfbizId()).productId(getProductId()).ecomId(getEcomId()).ofbizFacilityId(getOfbizFacilityId()).facilityName(getFacilityName()).shipMethodName(getShipMethodName()).deliveryDate(LocalDate.parse(getDeliveryDate())).cutoff(getCutoff()).airSectors(getAirSectors()).zipCodeExemptions(getZipCodeExemptions()).timeZone(getFacilityTimeZone()).deliveryWindows(deliveryWindows).facilityCapacity(getFacilityCapacity()).carrierMethodCapacity(getCarrierMethodCapacity()).priority(getPriority()).facilityRanking(getFacilityRanking()).productionLeadTime(getProductionLeadTime()).timestamp(LocalDateTime.parse(getTimestamp())).essentialMaterialIds(getEssentialMaterialIds()).version(Constants.INITIAL_VERSION).expiration(Utils.calculateExpiration(getCutoff())).build();
}
```

### buildOfferEntity Overview 

The `buildOfferEntity` method in the `OfferUpdate` class is responsible for creating an `OfferEntity` object based on the provided data. 

Here's a breakdown of what the method does:

1. It generates an `id` for the `OfferEntity` using various parameters.
2. It tries to parse the `deliveryWindows` JSON string into a list of `DeliveryWindow` objects. If it fails, it logs a warning and initializes an empty list.
3. It constructs and returns an `OfferEntity` object using the builder pattern. The object is populated with values from the parameters and other methods called within the constructor.

Note that the class and package names mentioned in the code snippet are not provided, so they are assumed to be part of the `com.bouqs.offerservice.entity.dto.request.OfferUpdate` class.


### buildOfferEntity Step by Step  

## Method: `buildOfferEntity` - Build an OfferEntity

The method `buildOfferEntity` is defined in the class `com.bouqs.offerservice.entity.dto.request.OfferUpdate`. It is responsible for creating and returning an instance of the `OfferEntity` class.

### Parameters:

The method does not accept any parameters. It uses the instance variables of the `OfferUpdate` class to construct the `OfferEntity` object.

### Return Value:

The method returns an instance of the `OfferEntity` class.

### Steps:

1. Generate a unique `id` for the `OfferEntity` using the `Utils.buildOfferEntityId` method. This method combines several values such as `facilityId`, `shipDate`, `skuId`, `carrierMethodId`, `shipMethodId`, and `regionId`.
2. Convert the `deliveryWindows` JSON string into a list of `DeliveryWindow` objects. This is done by using the `JsonFactory.getJson()` method and then calling the `toObject` method, specifying the target class as `DeliveryWindow[]`.
   - If there is an error during the conversion, a warning message is logged and an empty list is used instead.
3. Create an instance of `OfferEntity` using the `OfferEntity.builder()` method.
4. Set the properties of the `OfferEntity` object using the corresponding values from the `OfferUpdate` instance, such as `facilityId`, `shipDate`, `skuId`, `carrierMethodId`, `shipMethodId`, `regionId`, `ofbizId`, `productId`, `ecomId`, `ofbizFacilityId`, `facilityName`, `shipMethodName`, `deliveryDate`, `cutoff`, `airSectors`, `zipCodeExemptions`, `timeZone`, `deliveryWindows`, `facilityCapacity`, `carrierMethodCapacity`, `priority`, `facilityRanking`, `productionLeadTime`, `timestamp`, `essentialMaterialIds`, and `version`.
5. Calculate the expiration of the offer using the `Utils.calculateExpiration` method and set it as the value for the `expiration` property.
6. Finally, call the `build()` method on the `OfferEntity.builder()` object to construct and return the final `OfferEntity` object.

sequenceDiagram
    participant Utils
    participant JsonFactory
    participant Json
    participant log
    participant OfferEntity
    participant DeliveryWindow
    participant LocalDate
    participant Arrays
    participant ArrayList

    Note over Utils: buildOfferEntityId()
    Note over JsonFactory: getJson()
    Note over Json: toObject()
    Note over log: warn()
    Note over LocalDate: parse()
    Note over Arrays: asList()
    Note over ArrayList: ArrayList<>()
    Note over OfferEntity: builder()

    activate Utils
    Utils->>Utils: buildOfferEntityId(facilityId, shipDate, skuId, carrierMethodId, shipMethodId, regionId)
    deactivate Utils

    activate JsonFactory
    JsonFactory->>JsonFactory: getJson()
    deactivate JsonFactory

    activate Json
    Json->>Json: toObject(deliveryWindows, DeliveryWindow[].class)
    deactivate Json

    alt JsonException
        activate log
        log->>log: warn("unable to parse delivery windows json to object: " + deliveryWindows, e)
        deactivate log

        activate ArrayList
        ArrayList-->>Json: Return new ArrayList<>()
        deactivate ArrayList
    else
        activate Arrays
        Arrays-->>Json: Return Arrays.asList(deliveryWindows)
        deactivate Arrays
    end

    activate OfferEntity
    OfferEntity-->>OfferEntity: builder()
    OfferEntity->>OfferEntity: id(id)
    OfferEntity->>OfferEntity: facilityId(facilityId)
    OfferEntity->>LocalDate: parse(shipDate)
    OfferEntity->>OfferEntity: skuId(skuId)
    OfferEntity->>OfferEntity: carrierMethodId(carrierMethodId)
    OfferEntity->>OfferEntity: shipMethodId(shipMethodId)
    OfferEntity->>OfferEntity: regionId(regionId)
    OfferEntity->>OfferEntity: ofbizId(ofbizId)
    OfferEntity->>OfferEntity: productId(productId)
    OfferEntity->>OfferEntity: ecomId(ecomId)
    OfferEntity->>OfferEntity: ofbizFacilityId(ofbizFacilityId)
    OfferEntity->>OfferEntity: facilityName(facilityName)
    OfferEntity->>OfferEntity: shipMethodName(shipMethodName)
    OfferEntity->>LocalDate: parse(deliveryDate)
    OfferEntity->>OfferEntity: cutoff(cutoff)
    OfferEntity->>OfferEntity: airSectors(airSectors)
    OfferEntity->>OfferEntity: zipCodeExemptions(zipCodeExemptions)
    OfferEntity->>OfferEntity: timeZone(facilityTimeZone)
    OfferEntity->>OfferEntity: deliveryWindows(deliveryWindows)
    OfferEntity->>OfferEntity: facilityCapacity(facilityCapacity)
    OfferEntity->>OfferEntity: carrierMethodCapacity(carrierMethodCapacity)
    OfferEntity->>OfferEntity: priority(priority)
    OfferEntity->>OfferEntity: facilityRanking(facilityRanking)
    OfferEntity->>OfferEntity: productionLeadTime(productionLeadTime)
    OfferEntity->>LocalDateTime: parse(timestamp)
    OfferEntity->>OfferEntity: essentialMaterialIds(essentialMaterialIds)
    OfferEntity->>Constants: INITIAL_VERSION
    OfferEntity->>Utils: calculateExpiration(cutoff)
    OfferEntity-->>OfferEntity: build()
    deactivate OfferEntity

    Note over OfferEntity: Return OfferEntity

## Class: UsageMaterialRequest

**com.bouqs.offerservice.entity.dto.request.UsageMaterialRequest**

```java
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsageMaterialRequest 
```
The `UsageMaterialRequest` class is a Java class that represents a request for usage of a material. It is annotated with `@Setter`, `@Getter`, `@NoArgsConstructor`, `@AllArgsConstructor`, and `@Builder` to provide convenient getters, setters, constructors, and a builder for the class.

The class has three fields:
- `materialId`: a private string field that represents the ID of the material being requested for usage.
- `inventoryLotId`: a private string field that represents the ID of the inventory lot associated with the material being requested for usage.
- `materialUsage`: a private integer field that represents the amount of material to be used.

This class can be used in software systems where there is a need to track and process requests for material usage. It provides a convenient and efficient way to represent and manage such requests.
## Class: MaterialRequest

**com.bouqs.offerservice.entity.dto.request.MaterialRequest**

```java
@Generated
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class MaterialRequest extends BaseMaterial 
```
MaterialRequest is a class that represents a material request in a software system. It is a subclass of the BaseMaterial class. This class is annotated with various annotations such as @Generated, @Getter, @Setter, @SuperBuilder, and @NoArgsConstructor. The MaterialRequest class inherits all the properties and behaviors from the BaseMaterial class.

The MaterialRequest class has a single field called facilityId, which represents the identifier of the facility associated with the material request.

This class provides a way to create and manage material requests within the software system. It encapsulates the data and logic related to material requests, allowing software engineers to easily handle and process various operations related to requesting materials.
## Class: CartItemRequest

**com.bouqs.offerservice.entity.dto.request.CartItemRequest**

```java
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartItemRequest 
```
The "CartItemRequest" class is a data class that represents a request for a cart item. It is annotated with various annotations like "@Builder", "@Getter", "@Setter", "@NoArgsConstructor", and "@AllArgsConstructor" to provide convenient constructor, getter, setter methods, and default constructor options. 

This class has several methods and fields which provide a flexible and organized structure for handling cart item requests. The "orderItemNumber" field represents the unique identifier for the cart item request. The "primaryProduct" field holds the details of the primary product associated with the cart item. The "addOnProducts" field is a list of additional products that can be added to the cart item.

The "deliveryDate" field represents the desired delivery date for the cart item. The "destination" field holds the details of the destination where the cart item is to be delivered.

Overall, the "CartItemRequest" class provides a comprehensive and well-structured representation of a cart item request, facilitating easy manipulation and handling of cart items in a software system.
## Class: ItemRequest

**com.bouqs.offerservice.entity.dto.request.ItemRequest**

```java
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class ItemRequest extends BaseItem 
```
The ItemRequest class is a software engineering class that extends the BaseItem class. It is annotated with the Lombok annotations @Getter, @Setter, @SuperBuilder, and @NoArgsConstructor, which provide convenient methods for retrieving and setting field values, enabling fluent builder pattern, and generating a default constructor respectively. This class represents a request for an item in a system, and it is designed to encapsulate necessary information related to an item request. The specific methods and fields defined within the class are not described in this context.
## Class: MaterialOfferRequest

**com.bouqs.offerservice.entity.dto.request.MaterialOfferRequest**

```java
@Generated
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class MaterialOfferRequest extends BaseMaterialOffer 
```
The MaterialOfferRequest class is a subclass of the BaseMaterialOffer class and represents a request for a material offer in a software system. This class includes several annotations such as @Generated, @Getter, @Setter, @SuperBuilder, and @NoArgsConstructor. These annotations enhance the functionality and ease of use of the class.

The class provides various methods and fields, which are not explicitly described here for brevity, that enable the manipulation and retrieval of material offer request data. These methods and fields offer flexibility and encapsulation, ensuring that the class can be easily integrated and used within the larger software system.

The MaterialOfferRequest class is an important component in managing material offers and serves as a blueprint for creating and handling material offer requests. It provides a convenient and efficient way to process and manage the specific requirements and details of material offers within the software system.
## Class: OfferUpdateRequest

**com.bouqs.offerservice.entity.dto.request.OfferUpdateRequest**

```java
public class OfferUpdateRequest implements UpdateRequest 
```
# OfferUpdateRequest

The `OfferUpdateRequest` class is a Java class that implements the `UpdateRequest` interface. This class is used to handle update requests for offers. 

The class has a method called `handleUpdate`, which takes an `OfferService` object as a parameter and returns a `SyncUpdaterResponse` object. This method is responsible for processing the update request and performing the necessary operations on the offers.

The class also has several fields, including a private `Logger` object for logging purposes, a static `Json` object for handling JSON serialization and deserialization, and several String fields (`facilityId`, `shipDate`, and `materialId`) that represent the details of the offer update request. Additionally, there is a `Set` of `OfferUpdate` objects (`offerUpdates`) that represents the updates to be applied to the offers.

Overall, the `OfferUpdateRequest` class provides a convenient and organized way to handle update requests for offers in a software system.
### Method: handleUpdate
```java
@Override
public SyncUpdaterResponse handleUpdate(OfferService offerService) {
    Map<String, MaterialEntity> newMaterialEntitiesMap = getInsertingMaterialEntityIdMap();
    Map<String, OfferEntity> newOfferEntitiesMap = getInsertingOfferEntityIdMap();
    Map<String, MaterialOfferEntity> newMaterialOfferEntitiesMap = getInsertingMaterialOfferEntityIdMap();
    log.debug("newMaterialEntitiesMap: {}", () -> json.toJson(newMaterialEntitiesMap));
    log.debug("newOfferEntitiesMap: {}", () -> json.toJson(newOfferEntitiesMap));
    log.debug("newMaterialOfferEntitiesMap: {}", () -> json.toJson(newMaterialOfferEntitiesMap));
    Map<String, MaterialEntity> affectedMaterialEntities = filterMaterialEntitiesByFacilityShipDate(offerService.getMaterialEntitiesByFacilityIdMaterialId(facilityId, materialId));
    Map<String, MaterialOfferEntity> affectedMaterialOfferEntities = offerService.getMaterialOfferEntitiesByMaterialOfferIds(affectedMaterialEntities.keySet());
    log.debug("affectedMaterialEntities: {}", () -> json.toJson(affectedMaterialEntities));
    log.debug("affectedMaterialOfferEntities: {}", () -> json.toJson(affectedMaterialOfferEntities));
    Map<String, OfferEntity> oldOfferEntitiesMap = filterOfferEntitiesByShipDate(offerService.getOfferEntitiesByIds(getOfferEntityIds(affectedMaterialOfferEntities)));
    Map<String, MaterialOfferEntity> oldMaterialOfferEntitiesMap = filterMaterialOfferEntitiesByOfferEntityIds(oldOfferEntitiesMap.keySet(), affectedMaterialOfferEntities);
    Map<String, MaterialEntity> oldMaterialEntitiesMap = filterMaterialEntitiesByMaterialEntityIds(getMaterialEntityIds(oldMaterialOfferEntitiesMap), affectedMaterialEntities);
    log.debug("oldOfferEntitiesMap: {}", () -> json.toJson(oldOfferEntitiesMap));
    log.debug("oldMaterialOfferEntitiesMap: {}", () -> json.toJson(oldMaterialOfferEntitiesMap));
    log.debug("oldMaterialEntitiesMap: {}", () -> json.toJson(oldMaterialEntitiesMap));
    Set<MaterialEntity> createMaterials = offerService.createMaterial(newMaterialEntitiesMap, oldMaterialEntitiesMap);
    Set<MaterialOfferEntity> createMaterialOffers = offerService.createMaterialOffers(newMaterialOfferEntitiesMap, oldMaterialOfferEntitiesMap);
    Set<OfferEntity> createOffers = offerService.createOffers(newOfferEntitiesMap, oldOfferEntitiesMap);
    Set<MaterialEntity> updateMaterials = offerService.updateMaterial(newMaterialEntitiesMap, oldMaterialEntitiesMap, this);
    Set<MaterialOfferEntity> updateMaterialOffers = offerService.updateMaterialOffers(newMaterialOfferEntitiesMap, oldMaterialOfferEntitiesMap, this);
    Set<OfferEntity> updateOffers = offerService.updateOffers(newOfferEntitiesMap, oldOfferEntitiesMap, this);
    Set<MaterialEntity> deleteMaterials = offerService.deleteMaterials(newMaterialEntitiesMap, oldMaterialEntitiesMap);
    Set<MaterialOfferEntity> deleteMaterialOffers = offerService.deleteMaterialOffers(newMaterialOfferEntitiesMap, oldMaterialOfferEntitiesMap);
    Set<OfferEntity> deleteOffers = offerService.deleteOffers(newOfferEntitiesMap, oldOfferEntitiesMap);
    return offerService.handleUpdateOffers(createMaterials, createMaterialOffers, createOffers, updateMaterials, updateMaterialOffers, updateOffers, deleteMaterials, deleteMaterialOffers, deleteOffers);
}
```

### handleUpdate Overview 

The `handleUpdate` method in the `OfferUpdateRequest` class is responsible for handling updates to offers. 

Here is a summary of what the method does:

1. It retrieves maps of new material entities, offer entities, and material-offer entities from the request body.
2. It logs the new material entities map, new offer entities map, and new material-offer entities map.
3. It filters the material entities and material-offer entities affected by the update based on the facility ID and material ID.
4. It logs the affected material entities map and affected material-offer entities map.
5. It filters the offer entities and material-offer entities that were updated based on the affected material-offer entities.
6. It logs the old offer entities map, old material-offer entities map, and old material entities map.
7. It creates new material entities, material-offer entities, and offer entities.
8. It updates existing material entities, material-offer entities, and offer entities.
9. It deletes unnecessary material entities, material-offer entities, and offer entities.
10. It returns the response from the `handleUpdateOffers` method in the `OfferService`.


### handleUpdate Step by Step  

The handleUpdate method is defined in the `OfferUpdateRequest` class in the `com.bouqs.offerservice.entity.dto.request` package. Here is a step-by-step explanation of what this method does based on its BODY:

1. It retrieves the maps of new material entities, new offer entities, and new material offer entities.
2. It logs the debug information for the new material entities map, new offer entities map, and new material offer entities map using JSON serialization.
3. It filters the material entities based on facility and ship date by calling the `filterMaterialEntitiesByFacilityShipDate` method with the parameters `facilityId` and `materialId` obtained from the `offerService`.
4. It retrieves the affected material offer entities by calling the `getMaterialOfferEntitiesByMaterialOfferIds` method on the `offerService` with the keys of the affected material entities map obtained from the previous step.
5. It logs the debug information for the affected material entities map and the affected material offer entities map using JSON serialization.
6. It filters the offer entities based on ship date by calling the `filterOfferEntitiesByShipDate` method on the `offerService` with the offer entity IDs obtained from the affected material offer entities.
7. It filters the material offer entities based on offer entity IDs and the affected material offer entities by calling the `filterMaterialOfferEntitiesByOfferEntityIds` method with the parameters `oldOfferEntitiesMap.keySet()` and `affectedMaterialOfferEntities`.
8. It filters the material entities based on material entity IDs and the affected material entities by calling the `filterMaterialEntitiesByMaterialEntityIds` method with the parameters obtained from the previous step.
9. It logs the debug information for the old offer entities map, the old material offer entities map, and the old material entities map using JSON serialization.
10. It creates new material entities by calling the `createMaterial` method on the `offerService` with the new material entities map and the old material entities map.
11. It creates new material offer entities by calling the `createMaterialOffers` method on the `offerService` with the new material offer entities map and the old material offer entities map.
12. It creates new offer entities by calling the `createOffers` method on the `offerService` with the new offer entities map and the old offer entities map.
13. It updates material entities by calling the `updateMaterial` method on the `offerService` with the new material entities map, the old material entities map, and the current instance of the class.
14. It updates material offer entities by calling the `updateMaterialOffers` method on the `offerService` with the new material offer entities map, the old material offer entities map, and the current instance of the class.
15. It updates offer entities by calling the `updateOffers` method on the `offerService` with the new offer entities map, the old offer entities map, and the current instance of the class.
16. It deletes material entities by calling the `deleteMaterials` method on the `offerService` with the new material entities map and the old material entities map.
17. It deletes material offer entities by calling the `deleteMaterialOffers` method on the `offerService` with the new material offer entities map and the old material offer entities map.
18. It deletes offer entities by calling the `deleteOffers` method on the `offerService` with the new offer entities map and the old offer entities map.
19. Finally, it returns the result of the `handleUpdateOffers` method on the `offerService` with all the created, updated, and deleted entities as arguments.

---
title: handleUpdate (OfferUpdateRequest)
---

sequenceDiagram
    participant OfferService
    participant MaterialEntity
    participant OfferEntity
    participant MaterialOfferEntity
    participant SyncUpdaterResponse

    Note over OfferService: handleUpdate(OfferService offerService)
    OfferService->>MaterialEntity: getInsertingMaterialEntityIdMap()
    OfferService->>OfferEntity: getInsertingOfferEntityIdMap()
    OfferService->>MaterialOfferEntity: getInsertingMaterialOfferEntityIdMap()
    OfferService->>OfferService: getMaterialEntitiesByFacilityIdMaterialId(facilityId, materialId)
    OfferService->>OfferService: getMaterialOfferEntitiesByMaterialOfferIds(affectedMaterialEntities.keySet())
    OfferService->>OfferService: getOfferEntitiesByIds(getOfferEntityIds(affectedMaterialOfferEntities))
    OfferService->>OfferService: filterMaterialOfferEntitiesByOfferEntityIds(oldOfferEntitiesMap.keySet(), affectedMaterialOfferEntities)
    OfferService->>OfferService: filterMaterialEntitiesByMaterialEntityIds(getMaterialEntityIds(oldMaterialOfferEntitiesMap), affectedMaterialEntities)
    OfferService->>OfferService: createMaterial(newMaterialEntitiesMap, oldMaterialEntitiesMap)
    OfferService->>OfferService: createMaterialOffers(newMaterialOfferEntitiesMap, oldMaterialOfferEntitiesMap)
    OfferService->>OfferService: createOffers(newOfferEntitiesMap, oldOfferEntitiesMap)
    OfferService->>OfferService: updateMaterial(newMaterialEntitiesMap, oldMaterialEntitiesMap, this)
    OfferService->>OfferService: updateMaterialOffers(newMaterialOfferEntitiesMap, oldMaterialOfferEntitiesMap, this)
    OfferService->>OfferService: updateOffers(newOfferEntitiesMap, oldOfferEntitiesMap, this)
    OfferService->>OfferService: deleteMaterials(newMaterialEntitiesMap, oldMaterialEntitiesMap)
    OfferService->>OfferService: deleteMaterialOffers(newMaterialOfferEntitiesMap, oldMaterialOfferEntitiesMap)
    OfferService->>OfferService: deleteOffers(newOfferEntitiesMap, oldOfferEntitiesMap)
    OfferService->>OfferService: handleUpdateOffers(createMaterials, createMaterialOffers, createOffers, updateMaterials, updateMaterialOffers, updateOffers, deleteMaterials, deleteMaterialOffers, deleteOffers)
    OfferService-->>SyncUpdaterResponse: Return SyncUpdaterResponse

## Class: OfferRequest

**com.bouqs.offerservice.entity.dto.request.OfferRequest**

```java
@Generated
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode()
public class OfferRequest 
```
The OfferRequest class is a data structure used for requesting offers in the PUT and POST Offer API. It contains various fields that provide information related to the offer, such as facility ID, ship date, SKU ID, carrier method ID, ship method ID, region ID, product ID, delivery date, ofbiz ID, ecom ID, ofbiz facility ID, facility name, ship method name, timestamp, priority, cutoff, available units, air sectors, zip code exemptions, time zone, delivery windows, facility ranking, facility capacity, facility capacity usage, carrier method capacity, carrier method capacity usage, essential material IDs, version, and expiration. This class is generated and includes annotations such as @Getter, @Setter, @SuperBuilder, @NoArgsConstructor, and @EqualsAndHashCode().
