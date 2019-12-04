package dev.codescreen.cancelling;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class TradeTest {
  @Test
  public void constructorDefaultTest() {
    Trade trade = new Trade();
    assertEquals("Trade class default constructor error.", TradeType.UNKNOWN, trade.getTradeType());
    assertEquals("Trade class default constructor error.", 0l, trade.getTimestamp());
  }

  @Test
  public void constructorDataLineTest() throws Exception {
    Trade trade = new Trade("2015-05-22 08:24:06,Greedy bankers ltd.,D,77");
    assertEquals("Trade class data constructor error - TradeType detection.", TradeType.ORDER, trade.getTradeType());
    assertEquals("Trade class data constructor error - quantity parsing.", 77, trade.getQuantity());
    assertEquals("Trade class data constructor error - name determination.", "Greedy bankers ltd.", trade.getCompanyName());
  }

  @Test(expected = IndexOutOfBoundsException.class)
  public void constructorInvalidDataLineTest() throws Exception {
    Trade trade = new Trade("2015-05-22 08:24:06");
    fail("Incomplete line cannot lead to valid Trade item.");
  }

  @Test(expected = NumberFormatException.class)
  public void constructorInvalidQuantityDataLineTest() throws Exception {
    Trade trade = new Trade("2015-05-22 08:24:06,Greedy bankers ltd.,D,abct");
    fail("Incorrect quantity value cannot lead to valid Trade item.");
  }

  @Test(expected = AssertionError.class)
  public void constructorInvalidTimestampDataLineTest() throws Exception {
    Trade trade = new Trade("2015-05-72 08:24:06,Greedy bankers ltd.,D,88");
    fail("Incorrect day value cannot lead to valid Trade item.");
  }
}
