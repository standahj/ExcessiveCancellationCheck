package dev.codescreen.cancelling;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class ExcessiveTradeCancellingCountTest {

  @Test
  public void testTotalNumberOfWellBehavedCompanies() {
    assertEquals("Total number of well-behaved companies", 12, ExcessiveTradeCancellingChecker.totalNumberOfWellBehavedCompanies());
  }
}
