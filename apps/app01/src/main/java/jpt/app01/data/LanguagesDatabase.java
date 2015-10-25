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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 *
 * @author avm
 */
public class LanguagesDatabase {
  private static final Logger LOG = Logger.getLogger(LanguagesDatabase.class.getName());
  
  private static final String DATA_FILE_RESOURCEPATH = "/languages.csv";
  private final Map<String, LanguageProfile> languagesMap;

  public LanguagesDatabase() {
    int linesCount = 0;
    try (final BufferedReader in = new BufferedReader(new InputStreamReader(LanguageProfile.class.getResourceAsStream(DATA_FILE_RESOURCEPATH)))) {
      String headerLine = in.readLine(); linesCount++;
      if (null==headerLine) throw new IllegalArgumentException("Unexpected empty file");
      List<String> columnNames = LanguageProfile.splitLine(headerLine);
      Map<String, LanguageProfile> languagesMap = new HashMap<>();
      String line;
      while (null!= (line = in.readLine())) {
        linesCount++;
        LanguageProfile language = LanguageProfile.parse(columnNames, line);
        languagesMap.put(language.getName(), language);
      }
      this.languagesMap = languagesMap;
    } catch (Exception e) {
      throw new RuntimeException("Failed loading data from " + DATA_FILE_RESOURCEPATH + " @" + linesCount + ": " + e.getMessage(), e);
    }
  }
  
  public LanguageProfile getLanguage(String name) {
    dbResponseTime(20, 10);
    return languagesMap.get(name);
  }

  public String[] getLanguageNames() {
    dbResponseTime(40, 20);
    return languagesMap.keySet().toArray(new String[0]);
  }
  
  public List<String> findAllMatchingLanguages(String keyValue) {
    return Arrays.stream(this.getLanguageNames()).
            filter(s -> s.toLowerCase().contains(keyValue.toLowerCase())).
            sorted().
            collect(Collectors.toList());
  }  
  
  private Random random = new Random();
  public void dbResponseTime(int millis, int randomPct) { try {
    // Keep this, it simulates DB response time
    Thread.sleep(millis + random.nextInt(millis*randomPct/100) - millis*randomPct/50);
    } catch (InterruptedException ex) {
      LOG.warning("Sleep interrupted, ignoring exception");
    }
  }

}
