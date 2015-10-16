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

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


/**
 *
 * @author avm
 */
public class UserAndPassword {
  private static final Pattern SEP_PATTERN = Pattern.compile("(?<!\\\\);");
  
  static List<String> splitLine(String line) {
    return Arrays.stream(SEP_PATTERN.split(line, -1)).
            map(a -> a.replaceAll("\\\\(?!\\\\)", "").replaceAll("\\\\{2}", "\\\\")).
            collect(Collectors.toList());
  }

  public static UserAndPassword parse(List<String> columnNames, String line) {
    final List<String> fields = splitLine(line);
    String languageName = null;
    final int namesCount = columnNames.size();
    final int parsedFieldsCount = fields.size();
    if (namesCount != parsedFieldsCount)
      throw new IllegalArgumentException("Inconsistent columns count: actual " + parsedFieldsCount + " vs. expected " + namesCount + " in: <<" + line + ">>");
    String userid = fields.get(columnNames.indexOf("userid"));
    String firstname = fields.get(columnNames.indexOf("firstname"));
    String surname = fields.get(columnNames.indexOf("surname"));
    String password = fields.get(columnNames.indexOf("password"));
    String email = fields.get(columnNames.indexOf("email"));
    return new UserAndPassword(new User(userid, firstname, surname, email), password);
  }

  private final User user;
  private final String password;

  private UserAndPassword(User user, String password) {
    this.user = user;
    this.password = password;
  }

  public User getUser() {
    return user;
  }

  public String getPassword() {
    return password;
  }
  
}
