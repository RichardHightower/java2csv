# com.bouqs.offerservice.util
## Class: PopulateResponseComponent

**com.bouqs.offerservice.util.PopulateResponseComponent**

```java
@Component
public class PopulateResponseComponent 
```
The `PopulateResponseComponent` class is a software component responsible for populating response objects for various components of an application. It is annotated with `@Component`, indicating that it is a Spring Bean and can be managed by the Spring framework.

The class provides several methods for populating response objects:

- `fromOfferEntitiesFilterAndGroupByDate`: This method takes a map of offer entities grouped by date, and filters them based on a specified date range. It returns a list of `MonthResponse` objects.

- `buildNextPage`: This method takes a path and a map of parameters, and builds a URL string for the next page to be displayed. The URL is constructed using the `nextPageHostname` property defined in the application configuration.

- `populateMaterialForOffers`: This method takes a collection of offer entities and populates response objects of type `OfferResponse` for each of them.

The class also includes a `Logger` instance for logging purposes and two properties (`nextPageHostname`, `materialDao`, `materialOfferDao`) that are injected using the `@Value` and `@Autowired` annotations.

Overall, the `PopulateResponseComponent` class plays a crucial role in generating response objects for different components of the application.
### Method: fromOfferEntitiesFilterAndGroupByDate
```java
public List<MonthResponse> fromOfferEntitiesFilterAndGroupByDate(Map<LocalDate, Set<OfferEntity>> offerEntities, LocalDate fromDate, LocalDate toDate) {
    log.trace("Start fromOfferEntitiesFilterAndGroupByDate");
    log.debug("fromDate: {}; toDate: {}", fromDate, toDate);
    List<MonthResponse> monthResponses = new ArrayList<>();
    Map<String, List<DayResponse>> dayResponsesMap = new HashMap<>();
    if (offerEntities != null) {
        // each group offer (by delivery date) create a day response
        offerEntities.forEach((date, entities) -> {
            final List<DeliveryWindowResponse> deliveryWindowResponses = entities.stream().map(offerEntity -> {
                if (offerEntity.getDeliveryWindows() == null) {
                    return null;
                }
                return offerEntity.getDeliveryWindows().stream().map(Utils::convertDeliveryWindow).collect(Collectors.toList());
            }).filter(Objects::nonNull).flatMap(List::stream).collect(Collectors.toList());
            // sort deliveryWindowResponse by cutoff time decrease then create dayResponse
            if (!CollectionUtils.isEmpty(deliveryWindowResponses)) {
                deliveryWindowResponses.sort(Comparator.comparing(DeliveryWindowResponse::getCutoff));
            }
            DayResponse dayResponse = DayResponse.builder().day(date.getDayOfMonth()).deliveryDate(date).timeZone(entities.stream().findFirst().map(OfferEntity::getTimeZone).orElse("")).cutoff(entities.stream().findFirst().map(OfferEntity::getCutoff).orElse("")).deliveryWindows(deliveryWindowResponses).build();
            String deliveryDateYearMonth = Utils.buildYearMonthKey(date.getYear(), date.getMonthValue());
            List<DayResponse> previousDayResponses = dayResponsesMap.getOrDefault(deliveryDateYearMonth, new ArrayList<>());
            previousDayResponses.add(dayResponse);
            dayResponsesMap.put(deliveryDateYearMonth, previousDayResponses);
        });
    }
    // Group day response by year and month
    Map<List<Integer>, List<DayResponse>> responseGroupedByMonth = dayResponsesMap.values().stream().flatMap(List::stream).collect(Collectors.groupingBy(dayResponse -> Arrays.asList(dayResponse.getDeliveryDate().getYear(), dayResponse.getDeliveryDate().getMonthValue())));
    // each group of day response, create a month response object and add to list
    responseGroupedByMonth.forEach((gc, dr) -> {
        String deliveryDateYearMonth = Utils.buildYearMonthKey(gc.get(0), gc.get(1));
        MonthResponse monthResponse = MonthResponse.builder().year(gc.get(0)).month(gc.get(1)).days(dayResponsesMap.getOrDefault(deliveryDateYearMonth, new ArrayList<>())).build();
        monthResponses.add(monthResponse);
    });
    return monthResponses;
}
```

### fromOfferEntitiesFilterAndGroupByDate Overview 

The method `fromOfferEntitiesFilterAndGroupByDate` in the class `PopulateResponseComponent` takes in a map of LocalDate and Set of OfferEntity objects, as well as two LocalDate parameters `fromDate` and `toDate`. 

Inside the method, it creates a list of `MonthResponse` objects. It also initializes a map called `dayResponsesMap` which will hold the day responses for each delivery date. 

Next, it checks if the input `offerEntities` map is not null. If it is not null, it iterates over each entry in the map using a lambda expression. For each entry, it retrieves the associated OfferEntity objects and creates a list of `DeliveryWindowResponse` objects by mapping and filtering the delivery windows of each OfferEntity. The resulting `deliveryWindowResponses` list is then sorted by cutoff time. 

After that, a `DayResponse` object is created using the date from the map entry, the timezone and cutoff values from the first OfferEntity in the associated set of entities, and the sorted `deliveryWindowResponses`. 

This `DayResponse` object is then added to the `dayResponsesMap` using the year and month of the delivery date as the key. If a list of `DayResponse` objects already exists for the same year and month, the new `DayResponse` is added to the existing list. 

After iterating through all the entries in the `offerEntities` map, the `dayResponsesMap` is used to group the `DayResponse` objects by year and month into `responseGroupedByMonth`. 

Finally, for each entry in the `responseGroupedByMonth` map, a `MonthResponse` object is created using the year and month values, as well as the list of `DayResponse` objects associated with that year and month in the `dayResponsesMap`. This `MonthResponse` object is then added to the `monthResponses` list. 

The method returns the list of `MonthResponse` objects.


### fromOfferEntitiesFilterAndGroupByDate Step by Step  

## Method: fromOfferEntitiesFilterAndGroupByDate

This method is defined in the class `com.bouqs.offerservice.util.PopulateResponseComponent`. The purpose of this method is to filter and group a given set of offer entities based on their delivery dates.

### Parameters:

- `offerEntities` (Map<LocalDate, Set<OfferEntity>>): A map containing offer entities grouped by their delivery dates.
- `fromDate` (LocalDate): The start date for filtering the offer entities.
- `toDate` (LocalDate): The end date for filtering the offer entities.

### Return Value:

- `List<MonthResponse>`: A list of `MonthResponse` objects containing the filtered and grouped offer entities.

### Steps:

1. Initialize an empty list (`monthResponses`) and an empty map (`dayResponsesMap`).
2. If `offerEntities` is not null, proceed with the following steps:
   a. Iterate over each entry in `offerEntities`, where the key represents the delivery date and the value represents the corresponding offer entities.
   b. For each group of offer entities, create a list of `DeliveryWindowResponse` objects by converting the delivery windows of each offer entity using the `convertDeliveryWindow` method from `Utils` class. Filter out any null values.
   c. Sort the list of `DeliveryWindowResponse` objects by cutoff time in descending order.
   d. Create a `DayResponse` object using the delivery date, timezone, cutoff time, and the sorted list of delivery windows.
   e. Determine the delivery date year and month as a key for grouping.
   f. Retrieve the existing list of `DayResponse` objects for the delivery date year and month, or create a new empty list if it doesn't exist.
   g. Add the newly created `DayResponse` object to the list.
   h. Update the `dayResponsesMap` with the updated list of `DayResponse` objects.
3. Group the `DayResponse` objects in `dayResponsesMap` by year and month.
4. For each group of `DayResponse` objects, create a `MonthResponse` object using the year, month, and the corresponding list of `DayResponse` objects.
5. Add the `MonthResponse` object to the `monthResponses` list.
6. Return the `monthResponses` list.

---
title: fromOfferEntitiesFilterAndGroupByDate (PopulateResponseComponent)
---

sequenceDiagram
    participant log
    participant offerEntities
    participant monthResponses
    participant dayResponsesMap
    participant entities
    participant deliveryWindowResponses
    participant dayResponse
    participant previousDayResponses
    participant responseGroupedByMonth
    participant gc
    participant dr
    participant monthResponse

    Note over log: Start fromOfferEntitiesFilterAndGroupByDate
    log->>log: Trace: Start fromOfferEntitiesFilterAndGroupByDate
    log->>log: Debug: fromDate: {fromDate}; toDate: {toDate}
    offerEntities-->>dayResponsesMap: Check if offerEntities is not null
    dayResponsesMap-->>entities: Iterate over offerEntities
    entities-->>deliveryWindowResponses: Map offerEntity to deliveryWindowResponses
    deliveryWindowResponses-->>deliveryWindowResponses: Filter out null values
    deliveryWindowResponses-->>deliveryWindowResponses: Sort by cutoff time
    deliveryWindowResponses-->>dayResponse: Create dayResponse
    dayResponse-->>previousDayResponses: Add dayResponse to previousDayResponses
    previousDayResponses-->>dayResponsesMap: Update dayResponsesMap
    dayResponsesMap-->>responseGroupedByMonth: Group day responses by year and month
    responseGroupedByMonth-->>monthResponses: Create month response and add to monthResponses

    Note over log: Return monthResponses
    monthResponses-->>offerEntities: Return monthResponses

### Method: buildNextPage
```java
public String buildNextPage(String path, Map<String, String> params) {
    StringBuilder nextPageBuilder = new StringBuilder(nextPageHostname);
    nextPageBuilder.append("/").append(path).append("?");
    params.forEach((key, value) -> nextPageBuilder.append(key).append("=").append(value).append("&"));
    // Remove last &
    return nextPageBuilder.substring(0, nextPageBuilder.length() - 1);
}
```

### buildNextPage Overview 

The `buildNextPage` method in the `com.bouqs.offerservice.util.PopulateResponseComponent` class is responsible for constructing the URL of the next page based on the provided `path` and `params`. 

The method takes in the `path` as a string and `params` as a `Map` of key-value pairs. It begins by creating a `StringBuilder` object called `nextPageBuilder` and appending the `nextPageHostname` (not defined in the given code snippet) to it. 

Then, for each key-value pair in the `params` map, it appends the key, value, and an ampersand (&) to the `nextPageBuilder`. This is done using the `forEach` method with a lambda expression.

Finally, the code removes the last ampersand (&) character from the `nextPageBuilder` and returns the constructed next page URL as a string.

Overall, the `buildNextPage` method constructs a URL for the next page by combining the `nextPageHostname`, `path`, and `params`, in a query string format.


### buildNextPage Step by Step  

## Method buildNextPage
This method is defined in the class `com.bouqs.offerservice.util.PopulateResponseComponent` and is used to build the next page URL based on the given parameters.

### Input
The method takes two parameters:
1. `path` (type: String): This represents the path of the next page.
2. `params` (type: Map<String, String>): This is a collection of key-value pairs representing the parameters of the next page.

### Output
The method returns a string representing the complete URL of the next page.

### Functionality
1. Create a `StringBuilder` object called `nextPageBuilder` and initialize it with the value of `nextPageHostname`.
2. Append a forward slash (/) and the `path` parameter to the `nextPageBuilder`.
3. Append a question mark (?) to the `nextPageBuilder`.
4. Iterate through each entry in the `params` map and append the key-value pair to the `nextPageBuilder`, separated by an equals sign (=) and an ampersand (&).
5. Remove the last ampersand (&) from the `nextPageBuilder`.
6. Return the substring of the `nextPageBuilder` from index 0 to the length of the `nextPageBuilder` minus 1.

---
title: buildNextPage (PopulateResponseComponent)
---

sequenceDiagram
    participant PopulateResponseComponent
    participant StringBuilder
    participant nextPageHostname
    participant Map
    participant String
    
    PopulateResponseComponent->>StringBuilder: Create nextPageBuilder
    PopulateResponseComponent->>nextPageHostname: Get nextPageHostname
    PopulateResponseComponent->>Map: Get params
    Map->>nextPageBuilder: Iterate over params
    StringBuilder->>nextPageBuilder: Append key-value pairs
    StringBuilder-->>PopulateResponseComponent: Return nextPageBuilder
    PopulateResponseComponent-->>String: Return nextPage

### Method: populateMaterialForOffers
```java
public List<OfferResponse> populateMaterialForOffers(Collection<OfferEntity> offerEntities) {
    List<CompletableFuture<OfferResponse>> completableOffers = offerEntities.stream().map(offerEntity -> CompletableFuture.supplyAsync(() -> {
        OfferResponse.OfferResponseBuilder<?, ?> offerResponseBuilder = OfferResponse.builder().id(offerEntity.getId()).facilityId(offerEntity.getFacilityId()).shipDate(offerEntity.getShipDate()).skuId(offerEntity.getSkuId()).carrierMethodId(offerEntity.getCarrierMethodId()).shipMethodId(offerEntity.getShipMethodId()).regionId(offerEntity.getRegionId()).productId(offerEntity.getProductId()).deliveryDate(offerEntity.getDeliveryDate()).ofbizId(offerEntity.getOfbizId()).ecomId(offerEntity.getEcomId()).ofbizFacilityId(offerEntity.getOfbizFacilityId()).facilityName(offerEntity.getFacilityName()).shipMethodName(offerEntity.getShipMethodName()).timestamp(offerEntity.getTimestamp()).priority(offerEntity.getPriority()).cutoff(offerEntity.getCutoff()).airSectors(offerEntity.getAirSectors()).zipCodeExemptions(offerEntity.getZipCodeExemptions()).timeZone(offerEntity.getTimeZone()).deliveryWindows(offerEntity.getDeliveryWindows()).facilityRanking(offerEntity.getFacilityRanking()).facilityCapacity(offerEntity.getFacilityCapacity()).facilityCapacityUsage(offerEntity.getFacilityCapacityUsage()).productionLeadTime(offerEntity.getProductionLeadTime()).carrierMethodCapacity(offerEntity.getCarrierMethodCapacity()).carrierMethodCapacityUsage(offerEntity.getCarrierMethodCapacityUsage()).version(offerEntity.getVersion()).expiration(offerEntity.getExpiration());
        // Find materialOffers by offerId -> materialIds
        List<MaterialOfferEntity> materialOfferEntities = materialOfferDao.findBySkuEntityId(offerEntity.getId());
        if (materialOfferEntities.isEmpty()) {
            offerResponseBuilder.materials(Collections.emptyMap());
            offerResponseBuilder.availableUnits(0);
            return offerResponseBuilder.build();
        }
        Map<String, List<MaterialOfOfferResponse>> materialOfOfferResponseMap = materialOfferEntities.stream().flatMap(materialOffer -> materialDao.findById(materialOffer.getMaterialOfferId()).stream().map(materialEntity -> MaterialOfOfferResponse.builder().id(materialEntity.getId()).materialId(materialEntity.getMaterialId()).inventoryLotId(materialEntity.getInventoryLotId()).baseAPD(materialEntity.getBaseAPD()).inventoryLotStart(materialEntity.getInventoryLotStart()).inventoryLotEnd(materialEntity.getInventoryLotEnd()).inventoryLotCapacity(materialEntity.getInventoryLotCapacity()).capacityUsage(materialEntity.getCapacityUsage()).materialUnit(materialOffer.getMaterialUnit()).timestamp(materialEntity.getTimestamp()).build())).collect(Collectors.groupingBy(MaterialOfOfferResponse::getMaterialId, Collectors.toList()));
        offerResponseBuilder.materials(offerEntity.getEssentialMaterialIds().stream().map(materialId -> Map.entry(materialId, materialOfOfferResponseMap.getOrDefault(materialId, Collections.emptyList()))).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
        offerResponseBuilder.availableUnits(offerEntity.getAvailableUnits());
        return offerResponseBuilder.build();
    })).collect(Collectors.toList());
    return completableOffers.stream().map(CompletableFuture::join).collect(Collectors.toList());
}
```

### populateMaterialForOffers Overview 

The `populateMaterialForOffers` method in the `com.bouqs.offerservice.util.PopulateResponseComponent` class is responsible for populating the material information for a list of offer entities. 

It takes a collection of `OfferEntity` objects as input and returns a list of `OfferResponse` objects. 

The method first creates a list of `CompletableFuture` objects for each offer entity. Within each CompletableFuture, it asynchronously populates the material information for the offer entity. 

The material information is obtained from the `MaterialOfferEntity` objects associated with each offer entity, and the resulting data is stored in the `MaterialOfOfferResponse` objects. 

Finally, the offer response is constructed using the obtained material information and other attributes of the offer entity. The completed CompletableFuture objects are then collected and returned as a list of `OfferResponse` objects.


### populateMaterialForOffers Step by Step  

The `populateMaterialForOffers` method, defined in the `com.bouqs.offerservice.util.PopulateResponseComponent` class, takes in a collection of `OfferEntity` objects and returns a list of `OfferResponse` objects.

Here is a step-by-step breakdown of what this method does based on its body:

1. It creates a list of `CompletableFuture<OfferResponse>` objects called `completableOffers`. Each `OfferEntity` in the input collection is processed asynchronously using `CompletableFuture.supplyAsync()`.
2. For each `OfferEntity`, it creates an `OfferResponseBuilder` object and sets various properties using the corresponding getters of the `OfferEntity`. These properties include `id`, `facilityId`, `shipDate`, `skuId`, `carrierMethodId`, `shipMethodId`, `regionId`, `productId`, `deliveryDate`, `ofbizId`, `ecomId`, `ofbizFacilityId`, `facilityName`, `shipMethodName`, `timestamp`, `priority`, `cutoff`, `airSectors`, `zipCodeExemptions`, `timeZone`, `deliveryWindows`, `facilityRanking`, `facilityCapacity`, `facilityCapacityUsage`, `productionLeadTime`, `carrierMethodCapacity`, `carrierMethodCapacityUsage`, `version`, and `expiration`.
3. It queries the database for `MaterialOfferEntity` objects related to the current `OfferEntity` using the `materialOfferDao.findBySkuEntityId()` method. These objects represent the relationship between materials and offers.
4. If there are no `MaterialOfferEntity` objects found, the `materials` property of the `OfferResponseBuilder` is set to an empty map and the `availableUnits` property is set to 0. The `OfferResponse` is then built and returned.
5. If there are `MaterialOfferEntity` objects found, it queries the database for the corresponding `MaterialEntity` objects using the `materialDao.findById()` method. These objects represent the materials associated with the offers.
6. It transforms the list of `MaterialOfferEntity` and `MaterialEntity` objects into a map called `materialOfOfferResponseMap`. The map has `materialId` as the key and a list of `MaterialOfOfferResponse` objects as the value. These objects are built using the corresponding getters of the `MaterialEntity` and `MaterialOfferEntity`.
7. The `materials` property of the `OfferResponseBuilder` is set to a mapping of essential material IDs to their corresponding `MaterialOfOfferResponse` objects from the `materialOfOfferResponseMap`. The `availableUnits` property is set to the `availableUnits` value from the current `OfferEntity`.
8. The `OfferResponse` is built and returned.
9. The list of `CompletableFuture<OfferResponse>` objects is collected into a final list of `OfferResponse` objects by joining each `CompletableFuture`.
10. The final list of `OfferResponse` objects is returned.

This method efficiently populates the necessary information for creating offer responses by querying the database for related entities and mapping the data into the appropriate response objects.

---
title: populateMaterialForOffers (PopulateResponseComponent)
---

sequenceDiagram
    participant OfferEntity
    participant CompletableFuture
    participant OfferResponse
    participant OfferResponseBuilder
    participant MaterialOfferEntity
    participant MaterialOfOfferResponse
    participant materialOfferDao
    participant materialDao
    participant Collections
    participant Map
    participant Collectors

    OfferEntity->>CompletableFuture: Stream offerEntities
    CompletableFuture->>OfferResponse: Supply async
    OfferResponseBuilder->>OfferResponse: Build offerResponse
    MaterialOfferEntity->>materialOfferDao: Find by skuEntityId
    materialOfferDao-->>MaterialOfferEntity: Return materialOfferEntities
    materialDao->>materialOfferEntity: Find by materialOfferId
    materialDao-->>MaterialOfferEntity: Return materialEntities
    MaterialOfOfferResponse->>MaterialOfOfferResponse: Build materialOfOfferResponse
    MaterialOfOfferResponse->>MaterialOfferEntity: Map materialOfOfferResponse to materialOfferEntity
    MaterialOfOfferResponse->>MaterialOfOfferResponse: Group materialOfOfferResponse by materialId
    OfferResponseBuilder->>OfferResponse: Set materials and availableUnits
    CompletableFuture-->>OfferResponse: Return offerResponse
    CompletableFuture->>CompletableFuture: Join
    CompletableFuture-->>OfferResponse: Return offerResponse
    CompletableFuture->>List: Stream completableOffers
    List->>Collectors: Map completableOffers to list
    Collectors-->>List: Return list
    List-->>OfferResponse: Return list

## Class: ErrorMessages

**com.bouqs.offerservice.util.ErrorMessages**

```java
public class ErrorMessages 
```
The ErrorMessages class is a utility class that provides static final String constants for various error messages related to data validation. These error messages are used to indicate specific constraints or requirements that must be met when working with the corresponding fields or parameters. By using these constants, developers can ensure consistent and meaningful error messages are displayed when an invalid value is encountered. This class does not have any methods or fields of its own and is intended to be used as a reference for error message strings. The error messages cover a range of fields including FacilityId, ShipDate, SkuId, ProductId, OfbizId, EcomId, CarrierMethodId, ShipMethodId, RegionId, MaterialId, InventoryLotId, and Timestamp.
## Class: Utils

**com.bouqs.offerservice.util.Utils**

```java
@Component
public class Utils 
```
The `Utils` class is a utility class that provides various static methods for performing common tasks in software engineering. It contains methods for calculating expiration times, populating string values, building entity IDs, validating dates, and creating keys for various purposes. The class also includes methods for populating data objects and manipulating lists of offer IDs. It serves as a helper class that can be used across different modules or components of a software system. The class uses a private logger instance from the `org.apache.logging.log4j` package to handle logging.
### Method: calculateExpiration
```java
public static Long calculateExpiration(String cutoff) {
    if (StringUtils.isEmpty(cutoff)) {
        log.warn("Unable to calculate expiration: cutoff is null");
        return -1L;
    }
    ZonedDateTime zonedcutoff;
    try {
        zonedcutoff = ZonedDateTime.parse(cutoff);
    } catch (DateTimeParseException a) {
        log.warn("Unable to calculate expiration: cutoff " + cutoff + " is not in ZonedDateTime format, " + a.getMessage());
        return -1L;
    }
    ZonedDateTime today = ZonedDateTime.now();
    Duration duration = Duration.between(today, zonedcutoff);
    long seconds = duration.plusDays(Constants.MAXIMUM_DAYS).getSeconds();
    // negative seconds means no expiration, return 1 for the minimum seconds
    return (seconds < 0) ? 1L : seconds;
}
```

### calculateExpiration Overview 

The `calculateExpiration` method is a static method defined in the `Utils` class in the `com.bouqs.offerservice.util` package. 

This method takes a string parameter called `cutoff`, which represents a date and time in a specific format. The method calculates the expiration time in seconds based on the difference between the current date and time and the provided `cutoff` value.

First, the method checks if the `cutoff` parameter is empty. If it is empty, a warning message is logged and `-1L` is returned to indicate that the expiration cannot be calculated.

If the `cutoff` parameter is not empty, the method attempts to parse it into a `ZonedDateTime` object. If the parsing fails, a warning message is logged and `-1L` is returned.

If the `cutoff` parameter is successfully parsed, the method retrieves the current date and time as a `ZonedDateTime` object.

The method then calculates the duration between the current date and time and the `cutoff` date and time. It adds the maximum number of days defined in `Constants.MAXIMUM_DAYS` to the duration and retrieves the total number of seconds.

If the calculated number of seconds is negative, it means that there is no expiration, so the method returns `1L` as the minimum seconds. Otherwise, it returns the calculated number of seconds as the expiration time.

In summary, the `calculateExpiration` method takes a date and time string, calculates the expiration time in seconds, and returns the result.


### calculateExpiration Step by Step  

The `calculateExpiration` method defined in the `Utils` class in the `com.bouqs.offerservice.util` package is used to calculate the expiration time for a given cutoff date.

Here are the steps performed by the `calculateExpiration` method:

1. Check if the `cutoff` parameter is empty or null. If it is, a warning message is logged and -1 is returned to indicate an error.
2. Parse the `cutoff` string into a `ZonedDateTime` object. If the parsing fails, a warning message is logged with the specific error message, and -1 is returned to indicate an error.
3. Get the current date and time as a `ZonedDateTime` object.
4. Calculate the duration between the current date and time and the `zonedcutoff` (cutoff date and time).
5. Add the maximum number of days defined in the `Constants` class to the duration.
6. Retrieve the total number of seconds from the duration.
7. If the number of seconds is negative, it means there is no expiration, so 1 is returned (the minimum value for expiration).
8. Otherwise, the number of seconds is returned as the expiration time.

This method allows you to determine the expiration time based on a given cutoff date.

sequenceDiagram
    participant Utils
    participant StringUtils
    participant log
    participant ZonedDateTime
    participant DateTimeParseException
    participant Constants
    
    Note over Utils: calculateExpiration(cutoff)
    alt Cutoff is empty
        Utils->>StringUtils: isEmpty(cutoff)
        StringUtils-->>Utils: true
        Utils->>log: log.warn("Unable to calculate expiration: cutoff is null")
        log-->>Utils: 
        Utils-->>Utils: return -1L
    else
        Utils->>StringUtils: isEmpty(cutoff)
        StringUtils-->>Utils: false
        Utils->>ZonedDateTime: ZonedDateTime.parse(cutoff)
        alt Cutoff is not in ZonedDateTime format
            ZonedDateTime-->>Utils: throw DateTimeParseException
            Utils->>log: log.warn("Unable to calculate expiration: cutoff " + cutoff + " is not in ZonedDateTime format, " + a.getMessage())
            log-->>Utils: 
            Utils-->>Utils: return -1L
        else
            ZonedDateTime-->>Utils: zonedcutoff
            Utils->>ZonedDateTime: ZonedDateTime.now()
            ZonedDateTime-->>Utils: today
            Utils->>Duration: Duration.between(today, zonedcutoff)
            Duration-->>Utils: duration
            Utils->>Constants: Constants.MAXIMUM_DAYS
            Constants-->>Utils: maximumDays
            Utils->>duration: duration.plusDays(maximumDays)
            duration-->>Utils: updatedDuration
            Utils->>updatedDuration: getSeconds()
            updatedDuration-->>Utils: seconds
            alt Seconds is negative
                Utils-->>Utils: return 1L
            else
                Utils-->>Utils: return seconds
            end
        end
    end

### Method: populateDBCutoffString
```java
public static String populateDBCutoffString(String cutoff, LocalDate date, String timezone) {
    if (StringUtils.isEmpty(cutoff) || date == null || StringUtils.isEmpty(timezone)) {
        log.error("Unable to create cutoff datetime string, one or more parameter is null: cutoff=" + cutoff + " date=" + date + " timezone=" + timezone);
        return cutoff;
    }
    LocalTime cutoffTime;
    try {
        cutoffTime = LocalTime.parse(cutoff);
    } catch (DateTimeParseException d) {
        log.warn("Unable to create cutoff datetime string, cutoff " + cutoff + " not in time format: " + d.getMessage());
        return cutoff;
    }
    ZonedDateTime cutoffDateTime = ZonedDateTime.of(date, cutoffTime, ZoneId.of(timezone));
    String cutoffResult = cutoffDateTime.toString();
    log.trace("produced cutoff {}", cutoffResult);
    return cutoffResult;
}
```

### populateDBCutoffString Overview 

The `populateDBCutoffString` method in the `Utils` class is used to create a cutoff datetime string based on the given cutoff time, date, and timezone. 

Here is a breakdown of what the method does:

1. It first checks if any of the input parameters (cutoff, date, or timezone) are null or empty. If any of them are null or empty, an error message is logged and the original cutoff value is returned.
2. If all the input parameters are valid, the cutoff time is parsed into a `LocalTime` object using the `LocalTime.parse()` method.
3. Next, a `ZonedDateTime` object is created using the provided date, parsed cutoff time, and timezone information.
4. The cutoff datetime value is converted to a string using the `toString()` method.
5. Finally, the produced cutoff datetime value is logged at trace level and returned.

Please note that the exact behavior may be influenced by the `log` object used for logging, as well as any potential usage of this method within a larger codebase.


### populateDBCutoffString Step by Step  

### Method: populateDBCutoffString

This method is defined in the `com.bouqs.offerservice.util.Utils` class and is used to populate a datetime string for a cutoff time.

#### Parameters
- `cutoff` (String): The cutoff time in the format "HH:mm:ss".
- `date` (LocalDate): The date for which the cutoff time is to be determined.
- `timezone` (String): The timezone in which the cutoff time is to be computed.

#### Return Value
- This method returns a string representation of the resulting cutoff datetime.

#### Functionality
1. Check if any of the parameters (`cutoff`, `date`, or `timezone`) are null or empty. If any of them are, then log an error message and return the `cutoff` parameter as is.
2. Parse the `cutoff` time string into a `LocalTime` object. If the parsing fails, then log a warning message and return the `cutoff` parameter as is.
3. Create a `ZonedDateTime` object by combining the `date`, `cutoffTime`, and `timezone`.
4. Convert the `ZonedDateTime` object to a string representation.
5. Log a trace message with the produced cutoff datetime.
6. Return the resulting cutoff datetime string.

sequenceDiagram
    participant StringUtils
    participant LocalDate
    participant log
    participant LocalTime
    participant DateTimeParseException
    participant ZonedDateTime
    participant ZoneId

    Note over StringUtils, LocalDate, log, LocalTime, DateTimeParseException, ZonedDateTime, ZoneId: populateDBCutoffString(cutoff, date, timezone)

    alt Parameters validation
        StringUtils->>log: Error - One or more parameter is null
        log-->>StringUtils: Return cutoff
    else Cutoff time parsing
        StringUtils->>log: Warn - Cutoff not in time format
        log-->>StringUtils: Return cutoff
    else Cutoff datetime creation
        LocalDate->>LocalTime: Parse cutoff
        LocalTime-->>LocalDate: Return cutoffTime
        LocalDate->>ZoneId: Get ZoneId from timezone
        ZoneId-->>LocalDate: Return ZoneId
        LocalDate->>ZonedDateTime: Create ZonedDateTime with date, cutoffTime, and ZoneId
        ZonedDateTime-->>LocalDate: Return cutoffDateTime
        ZonedDateTime->>log: Trace - Produced cutoff
        log-->>ZonedDateTime: Return cutoffResult
        LocalDate-->>StringUtils: Return cutoffResult
    end

### Method: buildMaterialEntityId
```java
public static String buildMaterialEntityId(String materialId, String inventoryLotId) {
    StringJoiner joiner = new StringJoiner(Constants.DELIMITER_REDIS);
    joiner.add(materialId);
    if (!StringUtils.isEmpty(inventoryLotId)) {
        joiner.add(inventoryLotId);
    }
    return joiner.toString();
}
```

### buildMaterialEntityId Overview 

The method `buildMaterialEntityId` in the class `Utils` takes two arguments, `materialId` and `inventoryLotId`, and returns a string that represents the concatenation of these two values. 

Inside the method, a `StringJoiner` object is created using the constant value `Constants.DELIMITER_REDIS` as the delimiter. The `materialId` is added to the joiner using the `add` method. 

If the `inventoryLotId` is not empty or null, it is also added to the joiner using the `add` method. 

Finally, the `toString` method is called on the joiner object, which returns the concatenated string.


### buildMaterialEntityId Step by Step  

## Method: buildMaterialEntityId

The `buildMaterialEntityId` method is a utility method defined in the `Utils` class of the `com.bouqs.offerservice.util` package. It provides functionality to build a material entity ID based on the provided `materialId` and `inventoryLotId`. 

### Signature
```java
public static String buildMaterialEntityId(String materialId, String inventoryLotId)
```

### Parameters
- `materialId`: A String representing the material ID.
- `inventoryLotId`: A String representing the inventory lot ID.

### Return Value
- A String representing the formatted material entity ID.

### Algorithm
1. Create a `StringJoiner` object `joiner` with the delimiter specified by `Constants.DELIMITER_REDIS`.
2. Add the `materialId` to the `joiner`.
3. If the `inventoryLotId` is not empty, add it to the `joiner`.
4. Return the string representation of the `joiner`.

### Example
```java
String materialEntityId = Utils.buildMaterialEntityId("M123", "IL001");
System.out.println(materialEntityId);
```
Output:
```
M123:IL001
```

Please note that `Constants.DELIMITER_REDIS` is a constant defined in the code and represents the delimiter used to join the `materialId` and `inventoryLotId` in the material entity ID.

---
title: buildMaterialEntityId (Utils)
---

sequenceDiagram
    participant Utils
    participant StringJoiner
    participant Constants
    participant StringUtils

    Utils->>StringJoiner: Create StringJoiner
    Utils->>StringUtils: Check if inventoryLotId is empty
    StringUtils-->>Utils: Return boolean value
    alt inventoryLotId is not empty
        Utils->>joiner: Add materialId
        Utils->>joiner: Add inventoryLotId
    else inventoryLotId is empty
        Utils->>joiner: Add materialId
    end
    Utils-->>joiner: Return StringJoiner
    joiner-->>Utils: Return joined string
    Utils-->>Utils: Return material entity ID

### Method: buildMaterialOfferEntityId
```java
public static String buildMaterialOfferEntityId(String skuOfferId, String materialOfferId) {
    StringJoiner joiner = new StringJoiner(Constants.DELIMITER_REDIS);
    joiner.add(skuOfferId);
    joiner.add(normalizedDataField(materialOfferId));
    return joiner.toString();
}
```

### buildMaterialOfferEntityId Overview 

The method `buildMaterialOfferEntityId` in the class `com.bouqs.offerservice.util.Utils` is used to build a unique identifier for a material offer entity. 

It takes two parameters: `skuOfferId` and `materialOfferId`. 

Inside the method, a `StringJoiner` is created with a delimiter defined in the `Constants` class. The `skuOfferId` and the normalized form of `materialOfferId` are added to the `StringJoiner`. 

Finally, the `toString()` method is called on the `StringJoiner` to return the built identifier as a string.


### buildMaterialOfferEntityId Step by Step  

The `buildMaterialOfferEntityId` method, defined in the `Utils` class within `com.bouqs.offerservice.util`, helps generate a unique identifier for a material offer entity based on the provided inputs.

To use this method, you need to provide two parameters: `skuOfferId` and `materialOfferId`. 

Here are the steps that the `buildMaterialOfferEntityId` method follows:

1. Create a new object called `joiner` of type `StringJoiner`.
2. Add the value of `skuOfferId` to the `joiner` object, ensuring it is properly formatted.
3. Call the `normalizedDataField` method with `materialOfferId` as the input parameter, and add the returned value to the `joiner` object as well.
4. Convert the `joiner` object to a string by using the `toString` method.
5. Return the generated string, which represents the unique identifier for the material offer entity.

It is important to note that the `normalizedDataField` method referenced in step 3 is not defined within the provided code snippet, so its exact behavior or implementation is not known from this context.

---
title: buildMaterialOfferEntityId (Utils)
---

sequenceDiagram
    participant Utils
    participant StringJoiner
    participant Constants
    
    Utils->>StringJoiner: Create StringJoiner object
    Utils->>Constants: Get DELIMITER_REDIS constant
    Utils->>StringJoiner: Add skuOfferId to joiner
    Utils->>StringJoiner: Add normalized materialOfferId to joiner
    Utils->>StringJoiner: Convert joiner to string
    StringJoiner-->>Utils: Return the concatenated string

### Method: buildPageableOfferIndexKey
```java
public static String buildPageableOfferIndexKey(OfferEntity offerEntity) {
    String deliveryDateValue = Optional.ofNullable(offerEntity.getDeliveryDate()).map(Object::toString).orElse("");
    String offerEntityId = buildOfferEntityId(offerEntity.getFacilityId(), offerEntity.getShipDate().toString(), offerEntity.getSkuId(), offerEntity.getCarrierMethodId(), offerEntity.getShipMethodId(), offerEntity.getRegionId());
    StringJoiner joiner = new StringJoiner(Constants.DELIMITER_REDIS);
    joiner.add(offerEntityId).add(deliveryDateValue).add(normalizedDataField(offerEntity.getProductId())).add(normalizedDataField(offerEntity.getEcomId()));
    return joiner.toString();
}
```

### buildPageableOfferIndexKey Overview 

The `buildPageableOfferIndexKey` method in the `Utils` class is used to construct a unique key for indexing offers in a pageable format. 

The method takes an `OfferEntity` object as input and extracts various attributes from it. These attributes include the delivery date, facility ID, ship date, SKU ID, carrier method ID, ship method ID, region ID, product ID, and e-commerce ID.

These attributes are then concatenated using a predefined delimiter and returned as a string. This string can be used as a key for indexing the offer in a pageable format.


### buildPageableOfferIndexKey Step by Step  

The buildPageableOfferIndexKey method is a utility method that is used to construct a key for indexing offers. The key is created based on the properties of the OfferEntity object provided as input.

Here are the steps involved in constructing the key:

1. First, the method retrieves the delivery date from the offerEntity object. If the delivery date is not null, it is converted to a string. Otherwise, an empty string is used as the value.

2. Next, the method calls a separate utility method called buildOfferEntityId to create an offerEntityId value. This method takes several properties of the offerEntity object (facilityId, shipDate, skuId, carrierMethodId, shipMethodId, and regionId) and combines them into a single string.

3. The method then creates a StringJoiner object, which is a helper class for joining multiple strings together with a delimiter. In this case, the delimiter used is stored in a constant variable called DELIMITER_REDIS.

4. The offerEntityId value, deliveryDateValue, and two additional values (productId and ecomId) are added to the StringJoiner object using the add() method.

5. Finally, the toString() method is called on the StringJoiner object, which returns the final constructed key as a string.

Overall, the buildPageableOfferIndexKey method takes in an OfferEntity object and generates a key for indexing based on its properties. The key is constructed by combining various values using a delimiter and returning the result as a string.

sequenceDiagram
    participant OfferEntity
    participant Optional
    participant Constants
    participant StringJoiner
    
    OfferEntity->>Optional: Get delivery date
    Optional-->>OfferEntity: Return delivery date value
    OfferEntity->>Utils: Build offer entity ID
    Utils->>StringJoiner: Create StringJoiner instance
    StringJoiner->>StringJoiner: Add offer entity ID
    StringJoiner->>StringJoiner: Add delivery date value
    StringJoiner->>StringJoiner: Add normalized product ID
    StringJoiner->>StringJoiner: Add normalized ecom ID
    StringJoiner-->>Utils: Return joined string
    Utils-->>OfferEntity: Return pageable offer index key

### Method: convertPageableIndexToOfferId
```java
public static String convertPageableIndexToOfferId(String pageableIdKey) {
    String[] items = pageableIdKey.split(Constants.DELIMITER_REDIS);
    StringJoiner joiner = new StringJoiner(Constants.DELIMITER_REDIS);
    joiner.add(items[0]).add(items[1]).add(items[2]).add(items[3]).add(items[4]).add(items[5]);
    return joiner.toString();
}
```

### convertPageableIndexToOfferId Overview 

The `convertPageableIndexToOfferId` method defined in the `Utils` class in the `com.bouqs.offerservice.util` package takes a `pageableIdKey` as input and returns a concatenated string. 

The method first splits the `pageableIdKey` using the constant `Constants.DELIMITER_REDIS` as the delimiter, resulting in an array called `items`. 

Then, a `StringJoiner` named `joiner` is created with the same delimiter defined in `Constants.DELIMITER_REDIS`.

The method then adds each element of the `items` array to the `joiner` using the `add` method. This creates a string where each element of the `items` array is concatenated with the delimiter.

Finally, the method returns the concatenated string by calling `toString` on the `joiner` object.


### convertPageableIndexToOfferId Step by Step  

## Method: convertPageableIndexToOfferId

The `convertPageableIndexToOfferId` method, defined in class `com.bouqs.offerservice.util.Utils`, is responsible for converting a pageable index key into an offer ID.

### Parameters:
- `pageableIdKey` : The pageable index key that needs to be converted.

### Return Value:
- A `String` representing the converted offer ID.

### Description:
This method takes in a pageable index key and splits it using the delimiter constant `DELIMITER_REDIS` from the `Constants` class. It then creates a `StringJoiner` object, which is used to concatenate the individual elements of the split array (items) into a single string, using the same delimiter.

The `joiner.add` method is called for each item in the `items` array, and then the `toString` method is called on the `joiner` object to obtain the final converted offer ID.

This method is useful for converting a pageable index key into a structured offer ID that can be easily used in business operations or further processing.

---
title: convertPageableIndexToOfferId (Utils)
---

sequenceDiagram
    participant Utils
    participant String
    participant String[]
    participant StringJoiner

    Utils->>String: split(pageableIdKey)
    String-->>Utils: items[]
    Utils->>StringJoiner: create StringJoiner
    StringJoiner->>StringJoiner: add items[0]
    StringJoiner->>StringJoiner: add items[1]
    StringJoiner->>StringJoiner: add items[2]
    StringJoiner->>StringJoiner: add items[3]
    StringJoiner->>StringJoiner: add items[4]
    StringJoiner->>StringJoiner: add items[5]
    StringJoiner-->>Utils: return joiner.toString()

### Method: validateDate
```java
public static boolean validateDate(String dateStr, LocalDate fromDate, LocalDate endDate) {
    if (StringUtils.isEmpty(dateStr)) {
        return false;
    }
    LocalDate comparingDate;
    try {
        comparingDate = LocalDate.parse(dateStr);
    } catch (Exception e) {
        log.debug(e.getMessage());
        return false;
    }
    return (comparingDate.isAfter(fromDate) || comparingDate.isEqual(fromDate)) && (comparingDate.equals(endDate) || comparingDate.isBefore(endDate));
}
```

### validateDate Overview 

The `validateDate` method is a static method that takes three parameters: `dateStr`, `fromDate`, and `endDate`. It is used to validate if a given date string is within a specified date range.

The method first checks if the `dateStr` is empty or null. If it is, it returns `false`, indicating that the date is not valid.

Next, it attempts to parse the `dateStr` into a `LocalDate` object using the `LocalDate.parse` method. If an exception occurs during parsing, the method logs the exception message and returns `false`.

Finally, the method checks if the `comparingDate` is after or equal to the `fromDate`. It also checks if the `comparingDate` is equal to or before the `endDate`. If both conditions are true, the method returns `true`, indicating that the date is valid within the specified date range. Otherwise, it returns `false`.


### validateDate Step by Step  

## validateDate Method
---

The `validateDate` method, which is part of the `com.bouqs.offerservice.util.Utils` class, is used to determine if a given date is valid within a specified date range.

### Inputs
- `dateStr` (String): The date to be validated in string format.
- `fromDate` (LocalDate): The start date of the date range.
- `endDate` (LocalDate): The end date of the date range.

### Output
- `boolean`: Returns `true` if the `dateStr` falls within the specified date range, otherwise returns `false`.

### Steps
1. Check if the `dateStr` is empty or null. If it is, return `false` as an empty date is considered invalid.

2. Convert the `dateStr` to a `LocalDate` object by parsing it using the `LocalDate.parse()` method. If an exception occurs during parsing, log the error message and return `false`. This ensures that only valid dates are accepted.

3. Compare the `comparingDate` with the `fromDate` and `endDate`. 
   - If the `comparingDate` is after or equal to the `fromDate` AND the `comparingDate` is equal to or before the `endDate`, return `true`, indicating that the date is valid within the specified range.
   - Otherwise, return `false`, indicating that the date is outside the specified range.

That's it! The `validateDate` method provides a convenient way to validate if a date falls within a given range.

---
title: validateDate (Utils)
---

sequenceDiagram
    participant StringUtils
    participant LocalDate
    participant log
    participant Exception

    Note over StringUtils, LocalDate: Participants used for data processing

    alt Check if dateStr is empty
        StringUtils->>StringUtils: isEmpty(dateStr)
        StringUtils-->>Utils: Return result
    else Parse dateStr to LocalDate
        Utils->>LocalDate: parse(dateStr)
        alt Parsing successful
            LocalDate-->>Utils: Return comparingDate
        else Parsing failed
            LocalDate->>log: debug(e.getMessage())
            log-->>Utils: Return false
        end
    end

    alt Compare comparingDate with fromDate and endDate
        Utils->>comparingDate: isAfter(fromDate) || isEqual(fromDate)
        comparingDate->>Utils: Return result
        Utils->>comparingDate: equals(endDate) || isBefore(endDate)
        comparingDate-->>Utils: Return result
    end

    Utils-->>Utils: Return final result

### Method: facilityIdShipDateMaterialIdKey
```java
public static String facilityIdShipDateMaterialIdKey(String facilityId, String shipDate, String materialId) {
    StringJoiner joiner = new StringJoiner(Constants.DELIMITER_REDIS);
    joiner.add(facilityId);
    joiner.add(shipDate);
    joiner.add(materialId);
    String key = joiner.toString();
    return "{" + key + "}";
}
```

### facilityIdShipDateMaterialIdKey Overview 

The `facilityIdShipDateMaterialIdKey` method in the `Utils` class is used to generate a key for a Redis cache based on three input parameters: `facilityId`, `shipDate`, and `materialId`. 

The method takes these three parameters and concatenates them using a delimiter (`Constants.DELIMITER_REDIS`). It then surrounds the concatenated string with curly braces to conform to the Redis cache key format. The resulting key is returned as a string.

This method is useful when you need to generate a unique key for caching purposes based on the given parameters.


### facilityIdShipDateMaterialIdKey Step by Step  

### Method: facilityIdShipDateMaterialIdKey

This method, defined in the `Utils` class under the `com.bouqs.offerservice.util` package, is used to generate a unique key based on three input parameters: `facilityId`, `shipDate`, and `materialId`.

#### Parameters:
- `facilityId` (String): The identifier of the facility associated with the key.
- `shipDate` (String): The date on which the shipment is scheduled.
- `materialId` (String): The identifier of the material being shipped.

#### Return value:
- `String`: The generated key, encapsulated in curly braces.

#### Example usage:
```java
String facilityId = "FAC123";
String shipDate = "2022-05-01";
String materialId = "MAT456";

String key = Utils.facilityIdShipDateMaterialIdKey(facilityId, shipDate, materialId);
```

#### Example output:
The generated key would be `"{FAC123|2022-05-01|MAT456}"`.

#### How it works:
1. A `StringJoiner` object, `joiner`, is initialized with the provided `Constants.DELIMITER_REDIS` delimiter.
2. The `facilityId`, `shipDate`, and `materialId` are added to the `joiner` object using the `add()` method.
3. The `key` variable is assigned the string representation of the `joiner` object, obtained by calling the `toString()` method.
4. The generated `key` is surrounded by curly braces by appending `"{"` at the beginning and `"}"` at the end.
5. The final `key` is returned as the result of the method.

---
title: facilityIdShipDateMaterialIdKey (Utils)
---

sequenceDiagram
    participant Utils
    participant StringJoiner
    participant Constants

    Utils->>StringJoiner: Create StringJoiner instance
    StringJoiner->>Constants: Get DELIMITER_REDIS constant
    StringJoiner->>StringJoiner: Add facilityId
    StringJoiner->>StringJoiner: Add shipDate
    StringJoiner->>StringJoiner: Add materialId
    StringJoiner->>String: Convert to String
    String-->>Utils: Return key

### Method: facilityIdShipMethodIdKey
```java
public static String facilityIdShipMethodIdKey(String facilityId, String shipMethodId) {
    StringJoiner joiner = new StringJoiner(Constants.DELIMITER_REDIS);
    joiner.add(facilityId).add(shipMethodId);
    String key = joiner.toString();
    return "{" + key + "}";
}
```

### facilityIdShipMethodIdKey Overview 

The `facilityIdShipMethodIdKey` method takes two parameters, `facilityId` and `shipMethodId`, and concatenates them using a delimiter (`Constants.DELIMITER_REDIS`). The resulting string is enclosed in curly brackets and returned. 

This method is typically used to generate a unique key for storing data in a Redis database, where the `facilityId` and `shipMethodId` values are combined into a single string with the delimiter. This key can then be used to retrieve or manipulate the corresponding data in the Redis database.


### facilityIdShipMethodIdKey Step by Step  

The method `facilityIdShipMethodIdKey` defined in class `com.bouqs.offerservice.util.Utils` is a utility method for generating a unique key based on the provided `facilityId` and `shipMethodId`. 

Here is a step-by-step explanation of how the method works:

1. Define the method `facilityIdShipMethodIdKey` with two parameters: `facilityId` and `shipMethodId`. These parameters are strings that represent the facility and ship method identifiers.

2. Create a `StringJoiner` object named `joiner`. This object is used to concatenate strings together with a delimiter. In this case, the delimiter is defined in the `Constants.DELIMITER_REDIS` variable.

3. Add the `facilityId` and `shipMethodId` to the `joiner` object using the `add` method. This will concatenate the two strings together with the delimiter.

4. Call the `toString` method on the `joiner` object to get the concatenated string. Assign this value to the variable `key`.

5. Add curly braces around the `key` by concatenating it with the "{" and "}" characters.

6. Finally, return the generated key as a string.

Overall, this method takes the `facilityId` and `shipMethodId`, concatenates them with a delimiter, and returns the result surrounded by curly braces. This generated key can be used for various purposes, such as storing data in a database or as a unique identifier for a particular combination of facility and ship method.

---
title: facilityIdShipMethodIdKey (Utils)
---

sequenceDiagram
    participant Utils
    participant StringJoiner
    participant Constants
    
    Note over Utils: facilityIdShipMethodIdKey method
    
    Utils->>StringJoiner: Create StringJoiner
    StringJoiner->>Constants: Get DELIMITER_REDIS
    Constants-->>StringJoiner: Return DELIMITER_REDIS
    StringJoiner->>StringJoiner: Add facilityId
    StringJoiner->>StringJoiner: Add shipMethodId
    StringJoiner->>String: Convert to String
    String-->>Utils: Return key

### Method: productIdRegionIdKey
```java
public static String productIdRegionIdKey(String productId, String regionId) {
    StringJoiner joiner = new StringJoiner(Constants.DELIMITER_REDIS);
    joiner.add(normalizedDataField(productId)).add(regionId);
    String key = joiner.toString();
    return "{" + key + "}";
}
```

### productIdRegionIdKey Overview 

The `productIdRegionIdKey` method, defined in the `Utils` class of the `com.bouqs.offerservice.util` package, is responsible for generating a unique key based on the provided `productId` and `regionId` strings. 

In the method body, a `StringJoiner` object is created, using the `Constants.DELIMITER_REDIS` constant as the delimiter. The `normalizedDataField` function is called with the `productId` as an argument, and the result is added to the joiner, followed by the `regionId`. 

Finally, the `toString` method is called on the joiner, and the resulting string is enclosed within curly braces `{}` before being returned. This curly brace-wrapped string serves as a unique key for the given `productId` and `regionId`, which can be used for various purposes, such as in a Redis database.


### productIdRegionIdKey Step by Step  

## `productIdRegionIdKey` method

The `productIdRegionIdKey` method is a utility method defined in the `com.bouqs.offerservice.util.Utils` class. It is designed to create a unique key for a product and region combination.

### Method Signature

```java
public static String productIdRegionIdKey(String productId, String regionId)
```

### Method Description

The `productIdRegionIdKey` method takes two parameters:

1. `productId` - The unique identifier for a product.
2. `regionId` - The unique identifier for a region.

The method creates a key by concatenating the normalized data field value of the `productId` parameter with the `regionId` parameter, using the `Constants.DELIMITER_REDIS` delimiter.

The resulting key is then enclosed within curly braces `{}` and returned as a string.

### Example Usage

```java
String productId = "12345";
String regionId = "US";
String key = Utils.productIdRegionIdKey(productId, regionId);
System.out.println(key);
```

Output:

```
{12345-US}
```

In this example, the `productId` is set to `"12345"` and the `regionId` is set to `"US"`. The `productIdRegionIdKey` method is called with these parameters which produces the unique key `"{12345-US}"`. The key is then printed to the console.

---
title: productIdRegionIdKey (Utils)
---

sequenceDiagram
    participant Utils
    participant StringJoiner
    participant Constants
    
    Utils->>StringJoiner: Create StringJoiner instance
    Utils->>Constants: Access DELIMITER_REDIS constant
    StringJoiner->>Utils: Add normalizedDataField(productId)
    StringJoiner->>Utils: Add regionId
    Utils->>StringJoiner: Get joined string
    Utils-->>Utils: Enclose string in curly braces
    Utils-->>Utils: Return final key

### Method: ecomIdRegionIdKey
```java
public static String ecomIdRegionIdKey(String ecomId, String regionId) {
    StringJoiner joiner = new StringJoiner(Constants.DELIMITER_REDIS);
    joiner.add(normalizedDataField(ecomId)).add(regionId);
    String key = joiner.toString();
    return "{" + key + "}";
}
```

### ecomIdRegionIdKey Overview 

The method `ecomIdRegionIdKey` in class `com.bouqs.offerservice.util.Utils` is a utility method that creates a key for a Redis cache based on an e-commerce ID and a region ID. 

The method takes in two parameters, `ecomId` and `regionId`, which are strings representing the e-commerce ID and region ID respectively. 

It uses a `StringJoiner` object to concatenate the normalized e-commerce ID and the region ID with a delimiter specified in `Constants.DELIMITER_REDIS`. The normalized e-commerce ID is obtained by applying a function called `normalizedDataField` on the `ecomId`.

Finally, the method wraps the resulting key in curly braces and returns it as a string.


### ecomIdRegionIdKey Step by Step  

# Method: ecomIdRegionIdKey

## Description
The `ecomIdRegionIdKey` method is a utility method defined in the `Utils` class of the `com.bouqs.offerservice.util` package. This method is used to generate a key for storing data in a specific format, based on the provided `ecomId` and `regionId` parameters.

## Parameters
- `ecomId`: The ecommerce ID of the data.
- `regionId`: The region ID of the data.

## Return Value
The method returns a string representing the generated key.

## Algorithm

1. Create a new `StringJoiner` object named `joiner` using the `DELIMITER_REDIS` constant from the `Constants` class.
2. Add the normalized `ecomId` value to the `joiner` object using the `normalizedDataField` method.
3. Add the `regionId` value to the `joiner` object.
4. Convert the `joiner` object to a string using the `toString` method and assign it to the `key` variable.
5. Concatenate the `key` value with a brace `{` at the beginning and a closing brace `}` at the end.
6. Return the resulting string as the generated key.

## Example Usage

```java
String ecomId = "12345";
String regionId = "US";

String key = Utils.ecomIdRegionIdKey(ecomId, regionId);
System.out.println("Generated Key: " + key);
```

Output:
```
Generated Key: {normalized_ecomId_US}
```

Note: The actual generated key may vary depending on the implementation of the `normalizedDataField` method.

---
title: ecomIdRegionIdKey (Utils)
---

sequenceDiagram
    participant Utils
    participant StringJoiner
    participant Constants
    
    Utils->>StringJoiner: Create StringJoiner
    StringJoiner->>Constants: Get DELIMITER_REDIS
    Constants-->>StringJoiner: Return DELIMITER_REDIS
    StringJoiner->>Utils: Add normalizedDataField(ecomId)
    StringJoiner->>Utils: Add regionId
    Utils->>StringJoiner: Convert to String
    StringJoiner-->>Utils: Return key
    Utils->>Utils: Enclose key in curly braces
    Utils-->>Utils: Return ecomIdRegionIdKey

### Method: productIdDeliveryDateKey
```java
public static String productIdDeliveryDateKey(String productId, String deliveryDate) {
    StringJoiner joiner = new StringJoiner(Constants.DELIMITER_REDIS);
    joiner.add(normalizedDataField(productId)).add(deliveryDate);
    String key = joiner.toString();
    return "{" + key + "}";
}
```

### productIdDeliveryDateKey Overview 

The `productIdDeliveryDateKey` method in the `Utils` class of the `offerservice` package in the `com.bouqs` module is responsible for generating a unique key based on the given `productId` and `deliveryDate` parameters. 

The method creates a `StringJoiner` object, using the `Constants.DELIMITER_REDIS` delimiter, to join the normalized `productId` and `deliveryDate` values. The `normalizedDataField` method is called to normalize the `productId`. 

The normalized `productId` and `deliveryDate` are then concatenated using the `joiner.toString()` method. Finally, the result is wrapped within curly braces and returned as the key.


### productIdDeliveryDateKey Step by Step  

The `com.bouqs.offerservice.util.Utils` class includes a method called `productIdDeliveryDateKey`. This method takes two input parameters: `productId` and `deliveryDate`. 

Here is a step-by-step breakdown of what happens in this method:

1. A new `StringJoiner` object is created, which will help us concatenate the strings together. 
2. The `normalizedDataField` method is called, passing the `productId` parameter as an argument. The result of this method call is added as the first part of the concatenation. 
3. The `deliveryDate` parameter is added as the second part of the concatenation. 
4. The `StringJoiner` object is converted to a string by calling the `toString` method, and stored in the `key` variable. 
5. The `key` string is wrapped inside curly braces and returned by the method. 

In summary, the `productIdDeliveryDateKey` method creates a key for a data entry by concatenating the `productId` with the `deliveryDate` using a specified delimiter. The key is then enclosed in curly braces and returned as the result of the method.

sequenceDiagram
    participant Utils
    participant StringJoiner
    participant Constants
    
    Utils->>StringJoiner: Create StringJoiner instance
    Utils->>Constants: Get DELIMITER_REDIS constant
    Utils->>Utils: Normalize productId
    StringJoiner->>Utils: Add normalized productId
    StringJoiner->>Utils: Add deliveryDate
    Utils->>StringJoiner: Convert to String
    Utils-->>Utils: Return key surrounded by curly braces

### Method: ecomIdDeliveryDateKey
```java
public static String ecomIdDeliveryDateKey(String ecomId, String deliveryDate) {
    StringJoiner joiner = new StringJoiner(Constants.DELIMITER_REDIS);
    joiner.add(normalizedDataField(ecomId)).add(deliveryDate);
    String key = joiner.toString();
    return "{" + key + "}";
}
```

### ecomIdDeliveryDateKey Overview 

The `ecomIdDeliveryDateKey` method in the `Utils` class is used to generate a unique key for storing delivery date related data in Redis. 

The method takes two parameters - `ecomId` and `deliveryDate`. It creates a `StringJoiner` object with a Redis delimiter and adds the normalized version of `ecomId` and `deliveryDate` to it. 

It then converts the joined string into a final key format by surrounding it with curly braces and returns the key as a string.

The resulting key can be used to store and retrieve delivery date information in Redis efficiently.


### ecomIdDeliveryDateKey Step by Step  

The `ecomIdDeliveryDateKey` method defined in the `Utils` class within the `com.bouqs.offerservice.util` package is responsible for generating a unique key for a given `ecomId` and `deliveryDate`. This key is used for data storage and retrieval in the system.

Here is a step-by-step breakdown of what this method does:

1. Initialize a `StringJoiner` object called `joiner` using the `DELIMITER_REDIS` constant.
2. Add the normalized `ecomId` to the `joiner` object using the `normalizedDataField` method.
3. Add the `deliveryDate` to the `joiner` object.
4. Convert the `joiner` object to a string representation using the `toString` method and assign it to the `key` variable.
5. Surround the `key` with curly brackets to form a JSON-like structure.
6. Return the final key string.

This method takes the `ecomId` and `deliveryDate` as input parameters and returns a unique key that can be used for storing and retrieving data in the system. The key generated is a combination of the normalized `ecomId` and the `deliveryDate`, separated by a delimiter. The key is then enclosed in curly brackets to follow a JSON-like format.

sequenceDiagram
    participant Utils
    participant StringJoiner
    participant Constants
    
    Utils->>StringJoiner: Create StringJoiner instance
    Utils->>Constants: Access DELIMITER_REDIS constant
    StringJoiner->>Utils: Add normalizedDataField(ecomId)
    StringJoiner->>Utils: Add deliveryDate
    Utils->>StringJoiner: Get joined string
    Utils-->>Utils: Format key with curly braces
    Utils-->>Utils: Return key

### Method: deliveryDateRegionIdKey
```java
public static String deliveryDateRegionIdKey(String deliveryDate, String regionId) {
    StringJoiner joiner = new StringJoiner(Constants.DELIMITER_REDIS);
    joiner.add(deliveryDate).add(regionId);
    String key = joiner.toString();
    return "{" + key + "}";
}
```

### deliveryDateRegionIdKey Overview 

The `deliveryDateRegionIdKey` method in the `Utils` class is a utility method that takes two parameters, `deliveryDate` and `regionId`, both of type `String`. 

Within the method, a `StringJoiner` object called `joiner` is created, with `Constants.DELIMITER_REDIS` as the delimiter (presumably a delimiter used in Redis). The `deliveryDate` and `regionId` parameters are then added to the `joiner` object.

The `joiner.toString()` method is called to convert the `joiner` object to a `String`, and the resulting `key` is enclosed in curly braces using the concatenation operator. The final result is returned as a `String`.

In summary, this method concatenates the `deliveryDate` and `regionId` parameters using a specified delimiter, and encloses the result in curly braces to create a key, typically used for Redis. The resulting key would be of the format `{deliveryDate:regionId}`.


### deliveryDateRegionIdKey Step by Step  

## deliveryDateRegionIdKey Method

The `deliveryDateRegionIdKey` method in the `com.bouqs.offerservice.util.Utils` class is used to generate a unique key based on the delivery date and region ID. This key is used in the Redis database.

### Method Signature

```java
public static String deliveryDateRegionIdKey(String deliveryDate, String regionId)
```

### Method Description

1. Create a new `StringJoiner` object named `joiner`, which uses the Redis delimiter.
2. Add the `deliveryDate` parameter to the `joiner` object.
3. Add the `regionId` parameter to the `joiner` object.
4. Convert the `joiner` object to a string and assign it to the `key` variable.
5. Return the `key` variable, surrounded by curly braces.

### Example

```java
String deliveryDate = "2022-01-01";
String regionId = "12345";
String key = Utils.deliveryDateRegionIdKey(deliveryDate, regionId);
```

The `key` variable will be `"{2022-01-01:12345}"`.

Note: In this example, `Constants.DELIMITER_REDIS` is a constant that represents the delimiter used in the Redis database.

---
title: deliveryDateRegionIdKey (Utils)
---

sequenceDiagram
    participant Utils
    participant StringJoiner

    Utils->>StringJoiner: Create StringJoiner instance
    StringJoiner->>Utils: Return StringJoiner instance
    Utils->>StringJoiner: Add deliveryDate to StringJoiner
    StringJoiner->>Utils: Return updated StringJoiner
    Utils->>StringJoiner: Add regionId to StringJoiner
    StringJoiner->>Utils: Return updated StringJoiner
    Utils->>StringJoiner: Convert StringJoiner to String
    StringJoiner->>Utils: Return String
    Utils->>Utils: Enclose String in curly braces
    Utils-->>Utils: Return formatted key

### Method: productIdDeliveryDateRegionIdKey
```java
public static String productIdDeliveryDateRegionIdKey(String productId, String deliveryDate, String regionId) {
    StringJoiner joiner = new StringJoiner(Constants.DELIMITER_REDIS);
    joiner.add(normalizedDataField(productId)).add(deliveryDate).add(regionId);
    String key = joiner.toString();
    return "{" + key + "}";
}
```

### productIdDeliveryDateRegionIdKey Overview 

The `productIdDeliveryDateRegionIdKey` method in the `Utils` class of the `com.bouqs.offerservice.util` package is used to generate a key for a specific product, delivery date, and region id. 

The method takes three parameters: `productId` (a string representing the product id), `deliveryDate` (a string representing the delivery date), and `regionId` (a string representing the region id). 

Inside the method, a `StringJoiner` object is created with the Redis delimiter as the separator. The product id, delivery date, and region id are added to the joiner using the `normalizedDataField` method (whose definition is not provided in the given code snippet). 

After joining all the values, the resulting string is enclosed in curly braces and returned as the final key.


### productIdDeliveryDateRegionIdKey Step by Step  

## Method: `productIdDeliveryDateRegionIdKey`

This method is defined in the `com.bouqs.offerservice.util.Utils` class and is used to generate a key based on the provided `productId`, `deliveryDate`, and `regionId` values. The generated key is then enclosed in curly braces and returned as a string.

### Parameters
- `productId`: The unique identifier of the product.
- `deliveryDate`: The date on which the product is expected to be delivered.
- `regionId`: The identifier of the region where the product will be delivered.

### Return Value
- `String`: The generated key as a string, enclosed in curly braces.

### Usage Example
```java
String key = Utils.productIdDeliveryDateRegionIdKey(productId, deliveryDate, regionId);
```

### Method Body Explanation
1. A `StringJoiner` object named `joiner` is initialized with the `Constants.DELIMITER_REDIS` delimiter, which is used to concatenate the values together.
2. The `normalizedDataField` method is called with the `productId` parameter and its return value is added to the `joiner`.
3. The `deliveryDate` and `regionId` values are added to the `joiner`.
4. The `joiner` is converted to a string using the `toString` method.
5. The generated key is enclosed in curly braces by concatenating the string with a leading and trailing curly brace.
6. The final key is returned as a string.

---
title: productIdDeliveryDateRegionIdKey (Utils)
---

sequenceDiagram
    participant Utils
    participant StringJoiner
    participant Constants

    Utils->>StringJoiner: Create StringJoiner object
    StringJoiner->>Utils: Return StringJoiner object
    Utils->>Constants: Access DELIMITER_REDIS constant
    Utils->>Utils: Normalize productId
    Utils->>StringJoiner: Add normalized productId
    Utils->>StringJoiner: Add deliveryDate
    Utils->>StringJoiner: Add regionId
    StringJoiner->>Utils: Return joined string
    Utils->>Utils: Enclose joined string in curly braces
    Utils-->>Utils: Return final key

### Method: ecomIdDeliveryDateRegionIdKey
```java
public static String ecomIdDeliveryDateRegionIdKey(String ecomId, String deliveryDate, String regionId) {
    StringJoiner joiner = new StringJoiner(Constants.DELIMITER_REDIS);
    joiner.add(normalizedDataField(ecomId)).add(deliveryDate).add(regionId);
    String key = joiner.toString();
    return "{" + key + "}";
}
```

### ecomIdDeliveryDateRegionIdKey Overview 

This method takes three parameters: ecomId, deliveryDate, and regionId. It uses a StringJoiner to concatenate these three parameters using a delimiter defined as Constants.DELIMITER_REDIS. It then calls the method normalizedDataField to normalize the ecomId parameter before adding it to the joiner. Finally, it returns the resulting key enclosed in braces.


### ecomIdDeliveryDateRegionIdKey Step by Step  

## `ecomIdDeliveryDateRegionIdKey` Method

The `ecomIdDeliveryDateRegionIdKey` method is a utility method which is defined in the `com.bouqs.offerservice.util.Utils` class. It is used to generate a unique key based on the provided parameters `ecomId`, `deliveryDate`, and `regionId`.

### Method Signature
```java
public static String ecomIdDeliveryDateRegionIdKey(String ecomId, String deliveryDate, String regionId)
```

### Method Flow

1. Create a `StringJoiner` instance called `joiner` using the `Constants.DELIMITER_REDIS` delimiter.
2. Add the normalized `ecomId` to the `joiner` using the `normalizedDataField` method.
3. Add the `deliveryDate` to the `joiner`.
4. Add the `regionId` to the `joiner`.
5. Convert the contents of the `joiner` to a string using the `toString` method and assign it to the `key` variable.
6. Surround the `key` with curly brackets and return the result.

### Example Usage
```java
String ecomId = "ABCDE";
String deliveryDate = "2022-01-01";
String regionId = "123";
String key = Utils.ecomIdDeliveryDateRegionIdKey(ecomId, deliveryDate, regionId);
```

### Example Output
```java
key = "{ABCDE-2022-01-01-123}"
```

Please note that the actual formatting of the `key` may vary depending on the actual values of `ecomId`, `deliveryDate`, and `regionId`. The example provided is just for demonstration purposes.

sequenceDiagram
    participant Utils
    participant StringJoiner
    participant Constants
    
    Utils->>StringJoiner: Create StringJoiner instance
    Utils->>Constants: Access DELIMITER_REDIS constant
    StringJoiner->>Utils: Add normalizedDataField(ecomId)
    StringJoiner->>Utils: Add deliveryDate
    StringJoiner->>Utils: Add regionId
    Utils->>StringJoiner: Convert to String
    Utils-->>Utils: Concatenate with curly braces
    Utils-->>Utils: Return final key

### Method: skuIdDeliveryDateRegionIdKey
```java
public static String skuIdDeliveryDateRegionIdKey(String skuId, String deliveryDate, String regionId) {
    StringJoiner joiner = new StringJoiner(Constants.DELIMITER_REDIS);
    joiner.add(skuId).add(deliveryDate).add(regionId);
    String key = joiner.toString();
    return "{" + key + "}";
}
```

### skuIdDeliveryDateRegionIdKey Overview 

The method `skuIdDeliveryDateRegionIdKey` is a utility method defined in the `com.bouqs.offerservice.util.Utils` class. It takes in three parameters: `skuId`, `deliveryDate`, and `regionId`. 

This method is used to generate a unique key by concatenating these three parameters with a delimiter defined in the `Constants` class. The concatenated key is then enclosed within curly braces to form the final key. 

The purpose of this method is to create a consistent and unique identifier for a specific SKU ID, delivery date, and region ID combination, which can be used for various purposes such as caching, database operations, or any other scenario where a unique key is required.


### skuIdDeliveryDateRegionIdKey Step by Step  

# skuIdDeliveryDateRegionIdKey Method

The `skuIdDeliveryDateRegionIdKey` method is a utility method defined in the `com.bouqs.offerservice.util.Utils` class. It allows you to generate a key based on three input parameters: `skuId`, `deliveryDate`, and `regionId`.

## Inputs
The method takes the following inputs:

- `skuId`: The SKU ID, which represents a unique identifier for a product.
- `deliveryDate`: The delivery date, which specifies the date on which a product will be delivered.
- `regionId`: The region ID, which identifies the geographical region where the product will be delivered.

## Output
The method returns a string representation of the generated key. The key is created by concatenating the `skuId`, `deliveryDate`, and `regionId` using a delimiter.

## Steps
The method performs the following steps:

1. Creates a `StringJoiner` object named `joiner` using the `Constants.DELIMITER_REDIS` delimiter. This delimiter is used to separate the different components of the key.
2. Adds the `skuId`, `deliveryDate`, and `regionId` to the `joiner` object.
3. Converts the `joiner` object to a string representation using the `toString()` method.
4. Wraps the string representation of the key with curly braces and assigns it to the `key` variable.
5. Returns the `key`.

## Example
Here's an example usage of the `skuIdDeliveryDateRegionIdKey` method:

```java
String skuId = "123";
String deliveryDate = "2022-12-31";
String regionId = "456";
String key = Utils.skuIdDeliveryDateRegionIdKey(skuId, deliveryDate, regionId);
System.out.println(key);
```

Output:
```
{123|2022-12-31|456}
```

In this example, the method generates a key by concatenating the `skuId`, `deliveryDate`, and `regionId` using the `Constants.DELIMITER_REDIS` delimiter. The resulting key is then wrapped with curly braces.

---
title: skuIdDeliveryDateRegionIdKey (Utils)
---

sequenceDiagram
    participant Utils
    participant StringJoiner
    participant Constants

    Utils->>StringJoiner: Create StringJoiner
    StringJoiner->>Constants: Get DELIMITER_REDIS
    Constants-->>StringJoiner: Return DELIMITER_REDIS
    StringJoiner->>StringJoiner: Add skuId
    StringJoiner->>StringJoiner: Add deliveryDate
    StringJoiner->>StringJoiner: Add regionId
    StringJoiner->>String: Convert to String
    String-->>Utils: Return key

### Method: ofbizIdDeliveryDateRegionIdKey
```java
public static String ofbizIdDeliveryDateRegionIdKey(String ofbizId, String deliveryDate, String regionId) {
    StringJoiner joiner = new StringJoiner(Constants.DELIMITER_REDIS);
    joiner.add(normalizedDataField(ofbizId)).add(deliveryDate).add(regionId);
    String key = joiner.toString();
    return "{" + key + "}";
}
```

### ofbizIdDeliveryDateRegionIdKey Overview 

The method `ofbizIdDeliveryDateRegionIdKey` in the `Utils` class is used to generate a unique key based on three input parameters: `ofbizId`, `deliveryDate`, and `regionId`. 

The method creates a `StringJoiner` object using a constant delimiter (`Constants.DELIMITER_REDIS`), and appends the normalized value of `ofbizId`, `deliveryDate`, and `regionId` to it. The normalized value of `ofbizId` is obtained by invoking the `normalizedDataField` method. 

Finally, the method returns the generated key surrounded by curly braces `{}`.


### ofbizIdDeliveryDateRegionIdKey Step by Step  

## ofbizIdDeliveryDateRegionIdKey Method

The `ofbizIdDeliveryDateRegionIdKey` method is a utility method used to generate a unique key based on three input parameters: `ofbizId`, `deliveryDate`, and `regionId`.

### Method Signature
```java
public static String ofbizIdDeliveryDateRegionIdKey(String ofbizId, String deliveryDate, String regionId)
```

### Parameters
- `ofbizId` (String): The business identifier.
- `deliveryDate` (String): The delivery date.
- `regionId` (String): The region identifier.

### Return Value
- `String`: The generated key.

### Description

1. Create a new `StringJoiner` object named `joiner` with the redis delimiter as the separator.
2. Add the `normalizedDataField` of the `ofbizId` to the `joiner`.
3. Add the `deliveryDate` to the `joiner`.
4. Add the `regionId` to the `joiner`.
5. Convert the `joiner` to a `String` by calling the `toString` method.
6. Surround the generated key with curly braces.
7. Return the final key.

### Example Usage

```java
String ofbizId = "123";
String deliveryDate = "2022-01-01";
String regionId = "NY";
String key = Utils.ofbizIdDeliveryDateRegionIdKey(ofbizId, deliveryDate, regionId);
System.out.println(key); // Output: "{123|2022-01-01|NY}"
```

In this example, the `ofbizId`, `deliveryDate`, and `regionId` are used to generate a unique key. The output key is "{123|2022-01-01|NY}".

sequenceDiagram
    participant Utils
    participant StringJoiner
    participant Constants
    
    Utils->>StringJoiner: Create StringJoiner instance
    Utils->>Utils: Normalize ofbizId
    Utils->>StringJoiner: Add normalized ofbizId to joiner
    Utils->>StringJoiner: Add deliveryDate to joiner
    Utils->>StringJoiner: Add regionId to joiner
    Utils->>StringJoiner: Convert joiner to string
    Utils->>Utils: Enclose string in curly braces
    Utils-->>Utils: Return formatted key

### Method: populateMaterialOfferData
```java
public static MaterialOfferEntity populateMaterialOfferData(MaterialOfferEntity existedEntity, MaterialOfferEntity insertingEntity, boolean isUpdaterUsage) {
    if (!isUpdaterUsage) {
        existedEntity.setMaterialUnit(insertingEntity.getMaterialUnit());
    }
    return existedEntity;
}
```

### populateMaterialOfferData Overview 

The `populateMaterialOfferData` method in the `com.bouqs.offerservice.util.Utils` class is used to populate the data of a `MaterialOfferEntity` object. The method takes in an existing `MaterialOfferEntity` object (`existedEntity`), a new `MaterialOfferEntity` object (`insertingEntity`), and a boolean flag (`isUpdaterUsage`) that determines if the method is being used for updating the data.

If `isUpdaterUsage` is `false`, the method sets the material unit of the existing entity to the material unit of the inserting entity.

Finally, the method returns the modified `existedEntity` object.

This method is helpful when updating or inserting data for material offers in the offer service.


### populateMaterialOfferData Step by Step  

### Method: populateMaterialOfferData

**Description:**

This method is responsible for populating the data of a MaterialOfferEntity object. It takes two MaterialOfferEntity objects as input - `existedEntity` and `insertingEntity`, along with a boolean flag `isUpdaterUsage`.

If `isUpdaterUsage` is `true`, the method simply returns the `existedEntity` without making any changes.

If `isUpdaterUsage` is `false`, the `materialUnit` property of `existedEntity` is updated to match the `materialUnit` property of `insertingEntity`.

**Parameters:**

- `existedEntity` (MaterialOfferEntity) - The existing MaterialOfferEntity object.
- `insertingEntity` (MaterialOfferEntity) - The MaterialOfferEntity object containing the updated data.
- `isUpdaterUsage` (boolean) - A flag indicating whether the method is being used as an updater.

**Return Value:**

- `existedEntity` (MaterialOfferEntity) - The updated MaterialOfferEntity object.

**Usage Example:**

Here's an example of how to use this method:

```java
MaterialOfferEntity existingOffer = // obtain the existing MaterialOfferEntity
MaterialOfferEntity newOffer = // create a new MaterialOfferEntity with updated data

MaterialOfferEntity updatedOffer = Utils.populateMaterialOfferData(existingOffer, newOffer, false);
```

In this example, the `existingOffer` object is updated with the `materialUnit` property from the `newOffer` object, and the updated offer is stored in the `updatedOffer` variable.

---
title: populateMaterialOfferData (Utils)
---

sequenceDiagram
    participant existedEntity
    participant insertingEntity

    existedEntity->>insertingEntity: getMaterialUnit()
    insertingEntity-->>existedEntity: Return material unit
    existedEntity-->>existedEntity: setMaterialUnit(materialUnit)
    existedEntity-->>existedEntity: Return updated existedEntity

### Method: populateMaterialData
```java
public static MaterialEntity populateMaterialData(MaterialEntity existedEntity, MaterialEntity insertingEntity, boolean isUpdaterUsage) {
    // Update existed data with new data - Partial Update
    if (isUpdaterUsage) {
        existedEntity.setCapacityUsage(insertingEntity.getCapacityUsage());
    } else {
        existedEntity.setBaseAPD(insertingEntity.getBaseAPD());
        existedEntity.setInventoryLotCapacity(insertingEntity.getInventoryLotCapacity());
        existedEntity.setTimestamp(insertingEntity.getTimestamp());
        existedEntity.setInventoryLotStart(insertingEntity.getInventoryLotStart());
        existedEntity.setInventoryLotEnd(insertingEntity.getInventoryLotEnd());
    }
    // TODO: handle version
    // For UpdaterUsageRequest, we don't sync timestamp
    return existedEntity;
}
```

### populateMaterialData Overview 

The method `populateMaterialData` is a utility method in the `Utils` class of the `offerservice` package. 

This method is used to populate the data of a `MaterialEntity` object. It takes three parameters: `existedEntity`, `insertingEntity`, and `isUpdaterUsage`. 

If `isUpdaterUsage` is true, the method performs a partial update by setting the `capacityUsage` of the `existedEntity` object with the value from the `insertingEntity` object. 

If `isUpdaterUsage` is false, the method performs a full update by setting multiple attributes of the `existedEntity` object, including `baseAPD`, `inventoryLotCapacity`, `timestamp`, `inventoryLotStart`, and `inventoryLotEnd`, with the corresponding values from the `insertingEntity` object. 

After updating the data, the method returns the updated `existedEntity` object. 

Note: There is a TODO comment indicating that the method should handle version information, but that part is not currently implemented.


### populateMaterialData Step by Step  

The `populateMaterialData` method in the `Utils` class is used to update the material data. It takes in two parameters: `existedEntity` and `insertingEntity`, both of type `MaterialEntity`, and a boolean variable `isUpdaterUsage`.

The method is used to update existing material data with new data. If `isUpdaterUsage` is true, it indicates a partial update, and the method only updates the `capacityUsage` field of the `existedEntity` parameter with the value from the `insertingEntity` parameter.

If `isUpdaterUsage` is false, it indicates a full update, and the method updates multiple fields of the `existedEntity` parameter with values from the `insertingEntity` parameter. The fields that are updated include `baseAPD`, `inventoryLotCapacity`, `timestamp`, `inventoryLotStart`, and `inventoryLotEnd`.

The method also includes a TODO comment to handle the version, but the implementation for this is not provided in the code. Additionally, for the `UpdaterUsageRequest`, the timestamp is not synchronized during the update process.

Finally, the method returns the updated `existedEntity` parameter.

---
title: populateMaterialData (Utils)
---

sequenceDiagram
    participant existedEntity
    participant insertingEntity

    existedEntity->>existedEntity: setCapacityUsage(insertingEntity.getCapacityUsage())
    existedEntity->>existedEntity: setBaseAPD(insertingEntity.getBaseAPD())
    existedEntity->>existedEntity: setInventoryLotCapacity(insertingEntity.getInventoryLotCapacity())
    existedEntity->>existedEntity: setTimestamp(insertingEntity.getTimestamp())
    existedEntity->>existedEntity: setInventoryLotStart(insertingEntity.getInventoryLotStart())
    existedEntity->>existedEntity: setInventoryLotEnd(insertingEntity.getInventoryLotEnd())
    existedEntity-->>existedEntity: Return updated existedEntity

### Method: populateOfferData
```java
public static OfferEntity populateOfferData(OfferEntity existedEntity, OfferEntity insertingEntity, boolean isUpdaterUsage) {
    if (isUpdaterUsage) {
        existedEntity.setFacilityCapacityUsage(insertingEntity.getFacilityCapacityUsage());
        existedEntity.setCarrierMethodCapacityUsage(insertingEntity.getCarrierMethodCapacityUsage());
    } else {
        // End custom keys
        if (StringUtils.isNotEmpty(insertingEntity.getOfbizId())) {
            existedEntity.setOfbizId(insertingEntity.getOfbizId());
        }
        if (StringUtils.isNotEmpty(insertingEntity.getProductId())) {
            existedEntity.setProductId(insertingEntity.getProductId());
        }
        if (StringUtils.isNotEmpty(insertingEntity.getEcomId())) {
            existedEntity.setEcomId(insertingEntity.getEcomId());
        }
        if (insertingEntity.getDeliveryDate() != null) {
            existedEntity.setDeliveryDate(insertingEntity.getDeliveryDate());
        }
        if (StringUtils.isNotEmpty(insertingEntity.getCutoff())) {
            existedEntity.setCutoff(insertingEntity.getCutoff());
        }
        existedEntity.setFacilityCapacity(insertingEntity.getFacilityCapacity());
        existedEntity.setCarrierMethodCapacity(insertingEntity.getCarrierMethodCapacity());
        existedEntity.setPriority(insertingEntity.getPriority());
        existedEntity.setFacilityRanking(insertingEntity.getFacilityRanking());
        existedEntity.setProductionLeadTime(insertingEntity.getProductionLeadTime());
        existedEntity.setEssentialMaterialIds(insertingEntity.getEssentialMaterialIds());
        existedEntity.setTimestamp(insertingEntity.getTimestamp());
        existedEntity.setAirSectors(insertingEntity.getAirSectors());
        existedEntity.setDeliveryWindows(insertingEntity.getDeliveryWindows());
        existedEntity.setOfbizFacilityId(insertingEntity.getOfbizFacilityId());
        existedEntity.setFacilityName(insertingEntity.getFacilityName());
        existedEntity.setShipMethodName(insertingEntity.getShipMethodName());
    }
    // TODO: handle version
    // For UpdaterUsageRequest, we don't sync timestamp
    return existedEntity;
}
```

### populateOfferData Overview 

The method `populateOfferData` in the class `Utils` is used to populate data in an `OfferEntity` object. 

If the `isUpdaterUsage` parameter is true, the method copies the `facilityCapacityUsage` and `carrierMethodCapacityUsage` values from the `insertingEntity` object to the corresponding fields in the `existedEntity` object.

If `isUpdaterUsage` is false, the method populates various fields in the `existedEntity` object based on the values in the `insertingEntity` object. These fields include `ofbizId`, `productId`, `ecomId`, `deliveryDate`, `cutoff`, `facilityCapacity`, `carrierMethodCapacity`, `priority`, `facilityRanking`, `productionLeadTime`, `essentialMaterialIds`, `timestamp`, `airSectors`, `deliveryWindows`, `ofbizFacilityId`, `facilityName`, and `shipMethodName`.

The method then returns the updated `existedEntity` object.


### populateOfferData Step by Step  

## Method: `populateOfferData`

The `populateOfferData` method, defined in the `com.bouqs.offerservice.util.Utils` class, is responsible for populating the data of an offer.

### Parameters

- `existedEntity`: An instance of the `OfferEntity` class representing the existing offer entity.
- `insertingEntity`: An instance of the `OfferEntity` class representing the new offer entity containing the data to be inserted.
- `isUpdaterUsage`: A boolean flag indicating whether the method is being used for updating the offer data.

### Behaviour

1. If `isUpdaterUsage` is `true`:
   - Copy the `facilityCapacityUsage` value from `insertingEntity` to `existedEntity`.
   - Copy the `carrierMethodCapacityUsage` value from `insertingEntity` to `existedEntity`.
2. If `isUpdaterUsage` is `false`:
   - If `ofbizId` in `insertingEntity` is not empty, set `ofbizId` in `existedEntity` to the value in `insertingEntity`.
   - If `productId` in `insertingEntity` is not empty, set `productId` in `existedEntity` to the value in `insertingEntity`.
   - If `ecomId` in `insertingEntity` is not empty, set `ecomId` in `existedEntity` to the value in `insertingEntity`.
   - If `deliveryDate` in `insertingEntity` is not null, set `deliveryDate` in `existedEntity` to the value in `insertingEntity`.
   - If `cutoff` in `insertingEntity` is not empty, set `cutoff` in `existedEntity` to the value in `insertingEntity`.
   - Copy the `facilityCapacity` value from `insertingEntity` to `existedEntity`.
   - Copy the `carrierMethodCapacity` value from `insertingEntity` to `existedEntity`.
   - Copy the `priority` value from `insertingEntity` to `existedEntity`.
   - Copy the `facilityRanking` value from `insertingEntity` to `existedEntity`.
   - Copy the `productionLeadTime` value from `insertingEntity` to `existedEntity`.
   - Copy the `essentialMaterialIds` value from `insertingEntity` to `existedEntity`.
   - Copy the `timestamp` value from `insertingEntity` to `existedEntity`.
   - Copy the `airSectors` value from `insertingEntity` to `existedEntity`.
   - Copy the `deliveryWindows` value from `insertingEntity` to `existedEntity`.
   - Copy the `ofbizFacilityId` value from `insertingEntity` to `existedEntity`.
   - Copy the `facilityName` value from `insertingEntity` to `existedEntity`.
   - Copy the `shipMethodName` value from `insertingEntity` to `existedEntity`.
3. TODO: Handle version (implementation to be added).
4. For `UpdaterUsageRequest`, the `timestamp` is not synchronized.
5. Return the modified `existedEntity` object as the result.

---
title: populateOfferData (Utils)
---

sequenceDiagram
    participant existedEntity
    participant insertingEntity
    participant StringUtils
    participant OfferEntity

    alt isUpdaterUsage is true
        existedEntity->>insertingEntity: getFacilityCapacityUsage()
        insertingEntity->>existedEntity: setFacilityCapacityUsage()
        existedEntity->>insertingEntity: getCarrierMethodCapacityUsage()
        insertingEntity->>existedEntity: setCarrierMethodCapacityUsage()
    else
        existedEntity->>StringUtils: isNotEmpty(insertingEntity.getOfbizId())
        StringUtils-->>existedEntity: return true
        existedEntity->>insertingEntity: getOfbizId()
        insertingEntity->>existedEntity: setOfbizId()
        existedEntity->>StringUtils: isNotEmpty(insertingEntity.getProductId())
        StringUtils-->>existedEntity: return true
        existedEntity->>insertingEntity: getProductId()
        insertingEntity->>existedEntity: setProductId()
        existedEntity->>StringUtils: isNotEmpty(insertingEntity.getEcomId())
        StringUtils-->>existedEntity: return true
        existedEntity->>insertingEntity: getEcomId()
        insertingEntity->>existedEntity: setEcomId()
        existedEntity->>insertingEntity: getDeliveryDate()
        insertingEntity->>existedEntity: setDeliveryDate()
        existedEntity->>StringUtils: isNotEmpty(insertingEntity.getCutoff())
        StringUtils-->>existedEntity: return true
        existedEntity->>insertingEntity: getCutoff()
        insertingEntity->>existedEntity: setCutoff()
        existedEntity->>insertingEntity: getFacilityCapacity()
        insertingEntity->>existedEntity: setFacilityCapacity()
        existedEntity->>insertingEntity: getCarrierMethodCapacity()
        insertingEntity->>existedEntity: setCarrierMethodCapacity()
        existedEntity->>insertingEntity: getPriority()
        insertingEntity->>existedEntity: setPriority()
        existedEntity->>insertingEntity: getFacilityRanking()
        insertingEntity->>existedEntity: setFacilityRanking()
        existedEntity->>insertingEntity: getProductionLeadTime()
        insertingEntity->>existedEntity: setProductionLeadTime()
        existedEntity->>insertingEntity: getEssentialMaterialIds()
        insertingEntity->>existedEntity: setEssentialMaterialIds()
        existedEntity->>insertingEntity: getTimestamp()
        insertingEntity->>existedEntity: setTimestamp()
        existedEntity->>insertingEntity: getAirSectors()
        insertingEntity->>existedEntity: setAirSectors()
        existedEntity->>insertingEntity: getDeliveryWindows()
        insertingEntity->>existedEntity: setDeliveryWindows()
        existedEntity->>insertingEntity: getOfbizFacilityId()
        insertingEntity->>existedEntity: setOfbizFacilityId()
        existedEntity->>insertingEntity: getFacilityName()
        insertingEntity->>existedEntity: setFacilityName()
        existedEntity->>insertingEntity: getShipMethodName()
        insertingEntity->>existedEntity: setShipMethodName()
    end

    existedEntity-->>OfferEntity: return existedEntity

### Method: populateResultOfferIds
```java
public static List<String> populateResultOfferIds(List<String> offerIds, int minNum, int maxNum) {
    List<String> resultOfferIds = new ArrayList<>();
    for (int i = minNum; i < maxNum; i++) {
        if (i < offerIds.size()) {
            resultOfferIds.add(offerIds.get(i));
        }
    }
    return resultOfferIds;
}
```

### populateResultOfferIds Overview 

The `populateResultOfferIds` method defined in the `com.bouqs.offerservice.util.Utils` class is a static method that takes in three parameters: a `List` of `String` `offerIds`, an integer `minNum`, and an integer `maxNum`. 

The method creates a new `ArrayList` called `resultOfferIds` to store the result. It then iterates over a range of values starting from `minNum` and ending before `maxNum`. 

For each iteration, the method checks if the current value is within the bounds of the `offerIds` list. If it is, it retrieves the element at position `i` from the `offerIds` list and adds it to the `resultOfferIds` list. 

Finally, the method returns the `resultOfferIds` list. 

In summary, the `populateResultOfferIds` method extracts a sublist of elements from the `offerIds` list, starting from the `minNum` index and ending at the `maxNum` index (exclusive), and returns this sublist as a new list.


### populateResultOfferIds Step by Step  

The `populateResultOfferIds` method, defined in the `Utils` class of the `com.bouqs.offerservice.util` package, allows you to retrieve a subset of offer IDs from a given list. 

Here is a step-by-step explanation of how this method works:

1. The method takes three parameters: `offerIds`, `minNum`, and `maxNum`. 
   - `offerIds` is a list of strings containing all the offer IDs.
   - `minNum` and `maxNum` define the range of offer IDs to retrieve. 

2. The method initializes an empty list called `resultOfferIds` to store the subset of offer IDs that meet the specified criteria.

3. The method then iterates over a loop from `minNum` to `maxNum-1` (exclusive).
   - Within this loop, the method checks if the current index `i` is within the bounds of `offerIds` (i.e., if `i` is less than the size of the `offerIds` list).
     - If the current index is within the bounds, the method retrieves the offer ID at index `i` from `offerIds` using the `get` method, and adds it to the `resultOfferIds` list using the `add` method.

4. After iterating over the loop, the method returns the `resultOfferIds` list, which contains the subset of offer IDs that fall within the specified range.

By using this method, you can efficiently retrieve a subset of offer IDs based on the provided minimum and maximum range, ensuring that you only work with the relevant offer IDs for further processing or analysis in your business domain.

---
title: populateResultOfferIds (Utils)
---

sequenceDiagram
    participant Utils
    participant List
    participant resultOfferIds
    participant offerIds
    
    Utils->>List: Create new list resultOfferIds
    loop for each index i from minNum to maxNum
        Utils->>offerIds: Check if i is less than offerIds size
        offerIds-->>Utils: Return result
        Utils->>resultOfferIds: Add offerId at index i to resultOfferIds
    end
    Utils-->>List: Return resultOfferIds

## Class: TimeRecorder

**com.bouqs.offerservice.util.TimeRecorder**

```java
public class TimeRecorder 
```
The TimeRecorder class is a utility class that helps in tracking the time. It provides accurate recording of the start time when a specific task or operation begins. The class has a private final field, startTime, which stores the time in milliseconds since the epoch at the start of the task. This timestamp can be used for various purposes such as calculating the duration of the task or measuring performance.
## Class: Constants

**com.bouqs.offerservice.util.Constants**

```java
public class Constants 
```
The Constants class is a utility class that provides a collection of constant values used throughout the software application. These constants represent various configurations, formatting patterns, default values, and keys for logging. 

Some of the key constants in this class include:
- `MAXIMUM_DAYS`: Represents the maximum number of days.
- `INITIAL_VERSION`: Represents the initial version.
- `DELIMITER_REDIS`: Represents the delimiter for the Redis database.
- `DELIMITER_DYNAMODB`: Represents the delimiter for the DynamoDB database.
- `DELIMITER_TIME`: Represents the delimiter for time.
- `DATE_TIME_FORMATTER`: Represents the date and time formatter.
- `DATE_FORMATTER`: Represents the date formatter.
- `DEFAULT_TIMEZONE`: Represents the default timezone.
- `NUMBER_DELIVERY_DATE`: Represents the number of delivery dates.
- `NUMBER_SHIP_DATE`: Represents the number of ship dates.
- `NUMBER_TIMESTAMP`: Represents the number of timestamps.
- `DEFAULT_PAGE_STR`: Represents the default page number as a string.
- `DEFAULT_PAGE_SIZE_STR`: Represents the default page size as a string.
- `OFFER_AVAILABILITY_TYPE_SKU`: Represents the offer availability type as SKU.
- `OFFER_AVAILABILITY_TYPE_OFBIZ`: Represents the offer availability type as OFBIZ.

Additionally, this class also provides constants for various logging attributes to be used with the MDC (Mapped Diagnostic Context) logging framework, such as request ID, e-commerce ID, region ID, facility ID, ship method ID, material ID, SKU ID, carrier method ID, delivery date, product IDs, from date, to date, and zip code.

Overall, the Constants class serves as a central repository for commonly used values and keys within the software application.
