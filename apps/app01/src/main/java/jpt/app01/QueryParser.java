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
  
  private static Stream<String> getParameterValuesAsStream(String rawQuery, final String parameterName) {
    if (null==rawQuery) return Stream.empty();
    return Arrays.stream(rawQuery.split("&")).
            map((String s) -> Arrays.stream(s.split("=", -1)).map(u -> urlDecode(u)).collect(Collectors.toList())).
            filter(ls -> (ls.size()>1) && ls.get(0).equals(parameterName)).
            map(ls -> ls.get(1));
  }
  
  public static List<String> getParameterValues(String rawQuery, final String parameterName) {
    return getParameterValuesAsStream(rawQuery, parameterName).collect(Collectors.toList());
  }
  
  public static Optional<String> getFirstParameterValue(String rawQuery, final String parameterName) {
    return getParameterValuesAsStream(rawQuery, parameterName).findFirst();
  }
  
}
