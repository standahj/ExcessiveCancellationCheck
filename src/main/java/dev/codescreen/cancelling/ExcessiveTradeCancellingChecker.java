package dev.codescreen.cancelling;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Checks which companies from the Trades.data are involved in excessive cancelling.
 */
final class ExcessiveTradeCancellingChecker {

    private static final long CHECK_WINDOW = 60 * 1000l; // 1 minute interval

    /**
     * Simple logger
     */
    private static final Logger logger = Logger.getLogger("ExcessiveTradeCancellingChecker");

    /**
     * Instance of the Excessive Trade Cancelling Checker.
     */
    private static final ExcessiveTradeCancellingChecker checker = new ExcessiveTradeCancellingChecker();

    /**
     * Parsed data set.
     */
    private List<Trade> tradeList = null;

    /**
     * Companies involved in excessive cancellations.
     */
    private Set<String> excessiveCancellations = new HashSet<>();

    /**
     * Unique Companies present in the data set.
     */
    private Set<String> companySet = new HashSet<>();

    /**
     * Class instantiator.
     */
    private ExcessiveTradeCancellingChecker() {}

    /**
     * Returns the list of companies that are involved in excessive cancelling.
     * @return List of company names.
     */
    public final List<String> getCompaniesInvolvedInExcessiveCancellations() {
        if (this.tradeList == null) {
            this.tradeList = this.parseTradeData();
        }
        this.checkExcessiveCancellations(this.tradeList);
        return this.excessiveCancellations.stream().collect(Collectors.toList());
    }

    /**
     * Returns the total number of companies that are not involved in any excessive cancelling.
     */
    public final int getTotalNumberOfWellBehavedCompanies() {
        if (this.companySet.size() == 0) {
            this.getCompaniesInvolvedInExcessiveCancellations(); // initialize the data first
        }
        return this.companySet.size() - this.excessiveCancellations.size();
    }

    /**
     * See README.md for details. Creates list of company names involved in the excessive cancellation.
     * Uses look-ahead to one minute frame. Would this be a live feed, I'd look in reveres order (1 minute backwards)
     * @param tradeList List of Trade transactions.
     */
    private void checkExcessiveCancellations(final List<Trade> tradeList) {
        int i = 0; // index to make the look-ahead iterations possible
        // seconds bucket allows to consider all company's transactions within the same second only once
        final HashMap<String, Long> secondsBucket = new HashMap<>();
        for (final Trade trade : tradeList) {
            final Long timestamp = secondsBucket.get(trade.getCompanyName());
            // do not do look-ahead if this is transaction within the same second frame as already tested
            if (timestamp == null || trade.getTimestamp() > timestamp) {
                this.companySet.add(trade.getCompanyName()); // at this place I will do the least amount of inserts to companySet
                secondsBucket.put(trade.getCompanyName(), trade.getTimestamp());
                long orders        = 0l;
                long cancellations = 0l;
                long samples       = 0l;
                // for each record look ahead for up to 1 minute to find any record
                final ListIterator<Trade> iterator = tradeList.listIterator(i);
                while (iterator.hasNext()) {
                    final Trade test = iterator.next();
                    if (test.getTimestamp() < trade.getTimestamp() + CHECK_WINDOW) {
                        if (test.getCompanyName().equals(trade.getCompanyName())) {
                            samples++;
                            final TradeType testType = test.getTradeType();
                            if (testType == TradeType.ORDER) {
                                orders += test.getQuantity();
                            } else if (testType == TradeType.CANCEL) {
                                cancellations += test.getQuantity();
                            }
                        }
                    } else {
                        break;
                    }
                }
                // Excesive cancellation logic:
                // if there is more then one sample in given 60 second (single cancel w/o order is perhaps not
                // excessive cancellation) AND cancellation to orders ratio is more then 1 : 3
                // (I used multiplication as it is generally faster then division)
                if (samples > 1l && 3 * cancellations > orders) {
                    excessiveCancellations.add(trade.getCompanyName());
                }
            }
            i++; // keep index up-to-date
        }
        logger.info(String.join(", ",excessiveCancellations));
    }

    /**
     * Parses the included data set.
     * @return list of Trade items.
     */
    private List<Trade> parseTradeData() {
        final List<Trade> result = new LinkedList<>();
        final long start = System.currentTimeMillis();
        try (final InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("Trades.data")) {
            if (is != null) {
                final BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                String dataLine;
                while ((dataLine = reader.readLine()) != null) {
                    try {
                        result.add(new Trade(dataLine));
                    } catch (Exception e) {
                        // malformed entry, ignore (skip) it.
                        logger.log(Level.SEVERE,
                                String.format("Cannot parse Trade entry [%s] due to %s", dataLine, e.getMessage()));
                    }
                }
                logger.info("Dataset parsed in: " + (System.currentTimeMillis() - start) + "ms.");
            } else {
                logger.info("Trading data set not found in its initial location.");
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error reading the trading data set 'Trades.data'.", e);
        }
        return result;
    }

    /**
     * Returns the list of companies that are involved in excessive cancelling.
     */
    static List<String> companiesInvolvedInExcessiveCancellations() {
        return ExcessiveTradeCancellingChecker.checker.getCompaniesInvolvedInExcessiveCancellations();
    }

    /**
     * Returns the total number of companies that are not involved in any excessive cancelling.
     */
    static int totalNumberOfWellBehavedCompanies() {
        return ExcessiveTradeCancellingChecker.checker.getTotalNumberOfWellBehavedCompanies();
    }

}
