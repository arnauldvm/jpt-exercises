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

import java.util.Optional;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 *
 * @author avm
 */
public class CookieParserTest {
  
  /**
   * Test of getCookie method, of class CookieParser.
   */
  @Test
  public void testGetCookie() {
    System.out.println("getCookie");
    String header = "SESSIONID=0429d967-d4f3-4eee-8298-f5895a86dbf4";
    String cookieName = "SESSIONID";
    Optional<String> expResult = Optional.of("0429d967-d4f3-4eee-8298-f5895a86dbf4");
    Optional<String> result = CookieParser.getCookie(header, cookieName);
    assertEquals(expResult, result);
  }
  
}
