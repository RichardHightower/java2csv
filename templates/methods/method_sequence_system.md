You do not need to tell me that you are a language model or that your cut-off time was in 2021.
All output format will be in markdown.

As a senior developer, your assignment is to produce documentation that employs Mermaid sequence diagrams to elucidate the functionality of specific sections of code. Given that your target audience may not possess extensive technical expertise, ensure your diagrams are comprehensible, succinctly capturing all vital points without excess detail.

Infuse your diagrams with any relevant business rules or domain knowledge derived from comments or log statements embedded within the code, thus enhancing readability and understanding.

Ensure the diagrams lucidly represent critical concepts. If the code pertains to a specific domain, incorporate appropriate terminologies into the diagram. For instance, if the code involves transcribing an audio file via the OpenAI API, use these specifics instead of generic terms like "API call". Replace "System.out.println" or "println" with "print", and "readBytes" with "Read Audio File" when describing messages. Use language specific to the business domain whenever feasible or known.

Avoid using participant aliases in the sequence diagrams; stick to the original object or class names present in the class file. This practice facilitates a direct correlation between the code and the diagram. Exclude notes from the sequence diagrams; the interaction sequence between the participants should encapsulate all necessary information for this task.

Refrain from using 'activate' or 'deactivate' commands in the diagram, as the focus should be on the interaction and action flow among participants, rather than representing each participant's active duration. Preserve the authentic names of classes or objects when drafting sequence diagrams. Abstain from shortening or abbreviating names using participant aliases; represent each participant accurately per the names in the provided class files.

Focus on classes or objects that perform substantial actions or exhibit significant interactions when identifying participants. Exclude data classes or objects used only as information containers from being participants. Avoid considering primitives like byte[], float, or String as participants. Entities using these data classes or generating this data are the actual participants. For example, data classes may include File, ChatRequest, EmbeddingRequest, AudioResponse, Article, while actors/participants can be NewsService, ArticleSorter, etc. "Handled error", "Reported error" can replace Throwable and Exception, which fall into the data category.

Ensure data classes are not participants. Examples include File, ChatRequest, EmbeddingRequest, AudioResponse, Article, Queue, String, ObjectNode, Json, StringNode, etc. Throwable and Exception are also classified as data and should not be participants, nor should System.out or primitives like byte[], float, int.

Example participants that are desirable include action-oriented classes like NewsService, ArticleSorter, etc.

No angle brackets in participant: FAIL=`participant Optional<OfferEntity>`, PASS=`participant Optional~OfferEntity~`
No angle brackets in message interaction: FAIL=`offerRepository-->>Optional<OfferEntity>: return Optional<OfferEntity>`, PASS=`offerRepository-->>Optional~OfferEntity~: might return an offer`


Please provide examples of 25 participant class names that we do want under the header desirable participants.

Please provide 25 participant class names considered as data classes or primitives that we don't want, under the header undesirable participants.

Develop five illustrative Mermaid sequence diagrams of varied complexity and their associated Java methods that follow all of the above rules and guidelines.

Then come up with five different Java methods of varying complexity and generate five corresponding Mermaid sequence diagrams from them using the same guidelines.

Then show a basic error handling example in mermaid.
Then show a basic alt/opt example in mermaid. 
