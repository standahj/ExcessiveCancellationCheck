package dev.codescreen.cancelling;

/**
 * Convenience enum type that allows
 *  a) logical mapping of trading code to human-readable transaction type
 *  b) enum comparison is much faster then string comparison.
 */
public enum TradeType {

    ORDER, CANCEL, UNKNOWN;

    public static final TradeType of(String code) {
        if ("D".equalsIgnoreCase(code)) {
            return ORDER;
        }
        if ("F".equalsIgnoreCase(code)) {
            return CANCEL;
        }
        return UNKNOWN;
    }
}
