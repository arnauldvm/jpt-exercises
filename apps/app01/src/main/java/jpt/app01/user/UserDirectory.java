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
package jpt.app01.user;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author avm
 */
public class UserDirectory {
 
  private static final String USERS_FILE_RESOURCEPATH = "/users.csv";

  public static Optional<UserAndPassword> getUser(String userid) {
    int linesCount = 0;
    try (final BufferedReader in = new BufferedReader(new InputStreamReader(UserDirectory.class.getResourceAsStream(USERS_FILE_RESOURCEPATH)))) {
      String headerLine = in.readLine(); linesCount++;
      if (null==headerLine) throw new IllegalArgumentException("Unexpected empty file");
      List<String> columnNames = UserAndPassword.splitLine(headerLine);
      String line;
      while (null!= (line = in.readLine())) {
        linesCount++;
        if (!line.startsWith(userid+";")) continue;
        return Optional.of(UserAndPassword.parse(columnNames, line));
      }
      return Optional.empty();
    } catch (Exception e) {
      throw new RuntimeException("Failed loading data from " + USERS_FILE_RESOURCEPATH + " @" + linesCount + ": " + e.getMessage(), e);
    }
  }
  
}
