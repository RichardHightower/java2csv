package com.cloudurable.docgen;

import org.junit.jupiter.api.Test;

class Java2CSVTest {

    @Test
    void extractSequenceDiagram() {

        String str = DocGenerator.extractSequenceDiagram("asdafasdf\n" +
                "asdafasdf\n" +
                "\n" +
                "sequenceDiagram\n" +
                "    participant A\n" +
                "    participant B\n" +
                "\n" +
                "    Note right of A: A note\n" +
                "\n" +
                "    A->>B: Synchronous message\n" +
                "    B-->A: Asynchronous message\n" +
                "    B->>A: Response message\n" +
                "    A-->>B: Asynchronous response message\n" +
                "\n" +
                "    Note left of B: Another note\n" +
                "    loop Loop example\n" +
                "    alt Alternative example\n" +
                "        A->>B: Option 1\n" +
                "    else Option 2\n" +
                "        break when the booking process fails\n" +
                "            API-->Consumer: show failure\n" +
                "        end\n" +
                "    end\n" +
                "    A->>B: Loop message\n" +
                "    end\n" +
                "    title Sequence diagram example\n" +
                "    B->A: A very long message that needs to be broken\n" +
                "\n" +
                "For NOte \n");
        System.out.println(str);
    }
}
