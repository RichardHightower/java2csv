
# Guidelines for Mermaid Sequence Diagram Generation 

* Produce documentation with Mermaid sequence diagrams for code functionality.
* Target audience: non-tech savvy. Ensure diagrams are easily understood.
* Include relevant business rules/domain knowledge from code comments or logs in diagrams.
* Diagrams must clearly represent critical concepts. Use domain-specific language when applicable.
* No method calls in descriptions so `Foo -> Bar : getFooBar()` not ok, but `Foo -> Bar : Getting some foo from bar` is ok
* Participants should not be name Exception or Throwable and should not end in the word Execption
* Avoid participant aliases in diagrams. Use original class/object names from code.
* Exclude notes from diagrams. Encapsulate all necessary information within interaction sequence.
* Avoid 'activate'/'deactivate' commands in diagrams. Focus on participant interaction and action flow.
* Do not shorten/abbreviate names in diagrams. Use authentic names of classes/objects.
* Participants should be classes/objects with substantial actions/significant interactions.
* Exclude data classes/objects used only as containers from participants.
* Avoid data classes as participants: File, ChatRequest, EmbeddingRequest, AudioResponse, Article, Queue, String, StringNode, ObjectNode, etc.
* Do not use primitives as participants. Entities using/generating data are actual participants.
* Participants include action-oriented classes like NewsService, ArticleSorter, etc.
* Replace Throwable/Exception with "Handled error", "Reported error".
* Avoid System.out or primitives byte[], float, int as participants.
* No angle brackets in participant: FAIL=`participant Optional<OfferEntity>`, PASS=`participant Optional~OfferEntity~`
* No angle brackets in message interaction: FAIL=`offerRepository-->>Optional<OfferEntity>: return Optional<OfferEntity>`, PASS=`offerRepository-->>Optional~OfferEntity~: might return an offer`
* No angle brackets in message interaction: FAIL=`offerRepository-->>Optional<OfferEntity>: return Optional<OfferEntity>`, PASS=`offerRepository-->>Optional~OfferEntity~: might return an offer`
* No dots in participant FAIL=`participant FacilityConfigProto.FacilityConfig`, PASS=`FacilityConfig`


# Mermaid
```mermaid

{{MERMAID}}

```

# Java method 

```java

{{JAVA_METHOD}}

```


# Instruction
Regenerate a mermaid sequence diagram based on the above guidelines and the validation results titled {{TITLE}}


# Validation JSON

```javascript 

{{JSON}}

```
