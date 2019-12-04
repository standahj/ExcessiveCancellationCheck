package dev.codescreen.cancelling;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Item of the Trades data set.
 */
public class Trade {

    /**
     * Timestamp parser.
     */
    private static final SimpleDateFormat TIMESTAMP_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * Timestamp in milliseconds, for fast comparison.
     */
    private long        timestamp   = 0l;

    /**
     * Company name.
     */
    private String      companyName = null;

    /**
     * Decoded trade type.
     */
    private TradeType   tradeType   = TradeType.UNKNOWN;

    /**
     * Trades quantity.
     */
    private long        quantity    = 0l;

    /**
     * Default constructor. Creates an UNKNOWN Trade item.
     */
    public Trade() {
    }

    /**
     * Create a trade record from the line of the dataset.
     * @param dataSetLine Data set line.
     * @throws Exception When data input is malformed. See README.md for details.
     */
    public Trade(final String dataSetLine) throws Exception {
        if (dataSetLine != null && dataSetLine.length() > 0) {
            final String[] tokens = dataSetLine.split(" *, *"); // assuming simple data set, or else need to apply proper CSV parsing
            this.timestamp   = TIMESTAMP_FORMAT.parse(tokens[0]).getTime();
            this.companyName = tokens[1];
            this.tradeType   = TradeType.of(tokens[2]);
            this.quantity    = Long.parseLong(tokens[3]);
        }
    }

    /**
     * Getter for the timestamp value.
     * @return Timestamp of the trade.
     */
    public final long getTimestamp() {
        return timestamp;
    }

    /**
     * Getter for the company name  value.
     * @return Company name.
     */
    public final String getCompanyName() {
        return companyName;
    }

    /**
     * Getter for the trade type value.
     * @return Trade type.
     */
    public final TradeType getTradeType() {
        return tradeType;
    }

    /**
     * Getter for the quantity value.
     * @return Trade quantity.
     */
    public final long getQuantity() {
        return quantity;
    }

    /**
     * Convenience method to make IDE display the object in user-friendly format.
     * @return String representation of the object.
     */
    @Override
    public String toString() {
        return String.format("%s %s (%s) %d",companyName, TIMESTAMP_FORMAT.format(new Date(timestamp)), tradeType, quantity);
    }
}
