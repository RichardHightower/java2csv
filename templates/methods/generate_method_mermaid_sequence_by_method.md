
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

# Instruction 
Generate a mermaid sequence diagram based on the above guidelines titled {{TITLE}} using the Java code below by following the instructions below.
1. Create a list of participants
2. Take out of that list any exceptions or throwables, and any primitives or value classes.
3. Ensure there are no angle brackets in the list of participants.
4. Show the list of participants.
5. Now show the relationships between the participants.
6. Finally, create the mermaid code for the sequence diagram.

# Java method 

```java

{{JAVA_METHOD}}

```
