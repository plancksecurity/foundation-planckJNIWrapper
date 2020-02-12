package foundation.pEp.jniadapter;

import java.util.HashMap;


public class Message extends AbstractMessage {

    // Explicit Super Constructor call
    public Message() {
        super();
    }

    public Message(String mime_text) {
        super(mime_text);
    }

    private Message(long h) {
        super(h);
    }


    public enum TextFormat {
        Plain (0),
        Html (1),
        Other (255)
        ;

        public final int value;

        private static HashMap<Integer, TextFormat> intMap;

        private TextFormat(int value) {
            this.value = value;
        }

        public static TextFormat getByInt(int value){
            if (intMap == null) {
                intMap = new HashMap<Integer, TextFormat>();
                for (TextFormat s : TextFormat.values()) {
                    intMap.put(s.value, s);
                }
            }
            if (intMap.containsKey(value)) {
                return intMap.get(value);
            }
            return null;
        }
    }
    public enum Direction {
        Incoming (0),
        Outgoing (1)
        ;

        public final int value;

        private static HashMap<Integer, Direction> intMap;

        private Direction(int value) {
            this.value = value;
        }

        public static Direction getByInt(int value){
            if (intMap == null) {
                intMap = new HashMap<Integer, Direction>();
                for (Direction s : Direction.values()) {
                    intMap.put(s.value, s);
                }
            }
            if (intMap.containsKey(value)) {
                return intMap.get(value);
            }
            return null;
        }
    }
    public enum EncFormat {
        None (0),
        Inline (1),
        SMIME (2),
        PGPMIME (3),
        PEP (4)
        ;

        public final int value;

        private static HashMap<Integer, EncFormat> intMap;

        private EncFormat(int value) {
            this.value = value;
        }

        public static EncFormat getByInt(int value){
            if (intMap == null) {
                intMap = new HashMap<Integer, EncFormat>();
                for (EncFormat s : EncFormat.values()) {
                    intMap.put(s.value, s);
                }
            }
            if (intMap.containsKey(value)) {
                return intMap.get(value);
            }
            return null;
        }
    }

}