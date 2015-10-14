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

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author avm
 */
public class QueryParser {
  
  private QueryParser() {} // Pure static
  
  public static List<String> getParameterValues(String query, final String parameterName) {
    return Arrays.stream(query.split("&")).
            filter((String s) -> s.startsWith(parameterName+"=")).
            map((String s) -> s.replaceAll("^\\Q" + parameterName + "\\E=", "")).
            collect(Collectors.toList());
  }
  
  public static String getFirstParameterValue(String query, final String parameterName) throws IllegalArgumentException {
    final List<String> values = getParameterValues(query, parameterName);
    if (1!=values.size()) {
      throw new IllegalArgumentException("Invalid " + parameterName + " count: actual " + values.size() + " vs. 1 expected");
    }
    return values.get(0);
  }
  
}
