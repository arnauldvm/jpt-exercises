/*
 * The MIT License
 *
 * Copyright 2015 avm.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package jpt.app01;

import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

/**
 *
 * @author avm
 */
public class QueryParserTest {

  /**
   * Test of getParameterValues method, of class QueryParser.
   */
  @Test
  public void testGetParameterValues() {
    System.out.println("getParameterValues");
    String query = "a=b&c=d&e=f";
    List<String> expResult = null;
    assertThat(QueryParser.getParameterValues(query, "a"), hasItems("b"));
    assertThat(QueryParser.getParameterValues(query, "c"), hasItems("d"));
    assertThat(QueryParser.getParameterValues(query, "e"), hasItems("f"));
  }

  /**
   * Test of getParameterValues method, of class QueryParser.
   */
  @Test
  public void testGetParameterValuesMultiple() {
    System.out.println("getParameterValues multiple");
    String query = "a=b&c=d&a=f";
    List<String> expResult = null;
    assertThat(QueryParser.getParameterValues(query, "a"), hasItems("b", "f"));
    assertThat(QueryParser.getParameterValues(query, "c"), hasItems("d"));
  }

  /**
   * Test of getParameterValue method, of class QueryParser.
   */
  @Test
  public void testGetParameterValue() {
    System.out.println("getParameterValue");
    String query = "a=b&c=d&a=f";
    assertThat(QueryParser.getParameterValue(query, "c"), equalTo("d"));
  }
  
  /**
   * Test of getParameterValue method, of class QueryParser.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testGetParameterValueMissing() {
    System.out.println("getParameterValue");
    String query = "a=b&c=d&a=f";
    QueryParser.getParameterValue(query, "x");
  }
  
  /**
   * Test of getParameterValue method, of class QueryParser.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testGetParameterValueMultiple() {
    System.out.println("getParameterValue");
    String query = "a=b&c=d&a=f";
    QueryParser.getParameterValue(query, "a");
  }
  
}
