// CodeGen template enum
package foundation.pEp.jniadapter;

import java.util.HashMap;

// CodeGen template enum, mode=inner
public enum EncryptionFormat {
    PlanckEncNone (0) {
        public String toString() {
            return "PlanckEncNone";
        }
    }, 
    PlanckEncInline (1) {
        public String toString() {
            return "PlanckEncInline";
        }
    }, 
    PlanckEncSMIME (2) {
        public String toString() {
            return "PlanckEncSMIME";
        }
    }, 
    PlanckEncPGPMIME (3) {
        public String toString() {
            return "PlanckEncPGPMIME";
        }
    }, 
    PlanckEncPEP (4) {
        public String toString() {
            return "PlanckEncPEP";
        }
    }, 
    PlanckEncPGPMIMEOutlook1 (5) {
        public String toString() {
            return "PlanckEncPGPMIMEOutlook1";
        }
    }, 
    PlanckEncInlineEA (6) {
        public String toString() {
            return "PlanckEncInlineEA";
        }
    }, 
    PlanckEncAuto (255) {
        public String toString() {
            return "PlanckEncAuto";
        }
    }
    ;

    public final int value;

    private static HashMap<Integer, EncryptionFormat> intMap;

    private EncryptionFormat(int value) {
        this.value = value;
    }

    public static EncryptionFormat getByInt(int value){
        if (intMap == null) {
            intMap = new HashMap<Integer, EncryptionFormat>();
            for (EncryptionFormat s : EncryptionFormat.values()) {
                intMap.put(s.value, s);
            }
        }
        if (intMap.containsKey(value)) {
            return intMap.get(value);
        }
        return null;
    }

}
