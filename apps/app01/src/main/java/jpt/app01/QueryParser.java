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

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author avm
 */
public class QueryParser {
  
  private QueryParser() {} // Pure static
  
  private static String urlDecode(String urlPart) {
    try {
      return URLDecoder.decode(urlPart, "UTF-8");
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException("Unexpected encoding exeption", e);
    }
  }
  
  private static Stream<String> getParameterValuesAsStream(String query, final String parameterName) {
    return Arrays.stream(query.split("&")).
            filter((String s) -> s.startsWith(parameterName+"=")).
            map(s -> s.replaceAll("^\\Q" + parameterName + "\\E=", "")).
            map(s -> urlDecode(s));
  }
  
  public static List<String> getParameterValues(String query, final String parameterName) {
    return getParameterValuesAsStream(query, parameterName).collect(Collectors.toList());
  }
  
  public static Optional<String> getFirstParameterValue(String query, final String parameterName) {
    return getParameterValuesAsStream(query, parameterName).reduce((a, b) -> a);
  }
  
}
