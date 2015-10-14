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
package jpt.app01.data;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 *
 * @author avm
 */
public class LanguageProfile {
  private static final String COL_NAME = "Language";
  private static final Pattern SEP_PATTERN = Pattern.compile("(?<!\\\\);");
  
  static List<String> splitLine(String line) {
    return Arrays.stream(SEP_PATTERN.split(line, -1)).
            map(a -> a.replaceAll("\\\\(?!\\\\)", "").replaceAll("\\\\{2}", "\\\\")).
            collect(Collectors.toList());
  }
  
  private final String name;
  private final Map<String, String> attributesMap;

  private LanguageProfile(String name, Map<String, String> attributesMap) {
      this.name = name;
      this.attributesMap = attributesMap;
  }
  
  public static LanguageProfile parse(List<String> columnNames, String line) {
    final List<String> attributes = splitLine(line);
    String languageName = null;
    final int namesCount = columnNames.size();
    final int parsedAttributesCount = attributes.size();
    if (namesCount != parsedAttributesCount)
      throw new IllegalArgumentException("Inconsistent columns count: actual " + parsedAttributesCount + " vs. expected " + namesCount + " in: <<" + line + ">>");
    Map<String, String> attributesMap = new HashMap<>(namesCount-1);
    for (int idx = 0; idx<namesCount; idx++) {
      final String columnValue = attributes.get(idx);
      final String columnName = columnNames.get(idx);
      if (COL_NAME.equals(columnName)) {
        languageName = columnValue;
      } else if (!columnValue.isEmpty()) {
        attributesMap.put(columnName, columnValue);
      }
    }
    return new LanguageProfile(languageName, Collections.unmodifiableMap(attributesMap));
  }

  public String getName() {
    return name;
  }

  public String[] getAttributeNames() {
    return attributesMap.keySet().toArray(new String[0]);
  }

  public String getAttributeValue(String attributeName) {
    return attributesMap.get(attributeName);
  }

  public Map<String, String> getAttributesMap() {
    return attributesMap;
  }

}
