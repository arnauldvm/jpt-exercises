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
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

/**
 *
 * @author avm
 */
public class LanguageProfileTest {

  private static List<String> columnNames;
  
  @BeforeClass public static void setupClass() {
    columnNames = Arrays.asList("Language;Intended use;Imperative;Object-oriented;Functional;Procedural;Generic;Reflective;Event-driven;Other paradigm(s);Standardized?;Type safety;Type expression;Type compatibility and equivalence;Type checking".split(";"));
  }

  /**
   * Test of parse method, of class LanguageProfile.
   */
  @Test
  public void testParse() {
    System.out.println("parse");
    String line = "ActionScript 3.0;Application, client-side, Web;Yes;Yes;;;;;Yes;;1996, ECMA;safe;implicit with optional explicit typing;;static";
    LanguageProfile result = LanguageProfile.parse(columnNames, line);
    assertEquals("ActionScript 3.0", result.getName());
    assertEquals("static", result.getAttributeValue("Type checking"));
    assertNull(result.getAttributeValue("Functional"));
  }

  /**
   * Test of parse method, of class LanguageProfile.
   */
  @Test
  public void testParseWithSemicolon() {
    System.out.println("parse with semicolon");
    String line = "COBOL;Application, business;Yes;Yes;;Yes;;;;;ANSI X3.23 1968, 1974, 1985\\; ISO/IEC 1989:1985, 2002, 2014;safe;explicit;nominal;static";
    LanguageProfile result = LanguageProfile.parse(columnNames, line);
    assertEquals("COBOL", result.getName());
    assertEquals("ANSI X3.23 1968, 1974, 1985; ISO/IEC 1989:1985, 2002, 2014", result.getAttributeValue("Standardized?"));
  }
  
  /**
   * Test of parse method, of class LanguageProfile.
   */
  @Test
  public void testParseWithTrailingEmptyFields() {
    System.out.println("parse with trailing empty fields");
    String line = "Assembly language;General;Yes;;;;;;;any, syntax is usually highly specific, related to the target processor;No;;;;";
    LanguageProfile result = LanguageProfile.parse(columnNames, line);
    assertEquals("Assembly language", result.getName());
    assertEquals("any, syntax is usually highly specific, related to the target processor", result.getAttributeValue("Other paradigm(s)"));
    assertNull(result.getAttributeValue("Type checking"));
    assertEquals(4, result.getAttributeNames().length);
  }
  
  

  @Test
  public void testGetColumnNames() {
    System.out.println("getColumnNames");
    String line = "ActionScript 3.0;Application, client-side, Web;Yes;Yes;;;;;Yes;;1996, ECMA;safe;implicit with optional explicit typing;;static";
    LanguageProfile result = LanguageProfile.parse(columnNames, line);
    assertEquals("ActionScript 3.0", result.getName());
    final String[] attributeNames = result.getAttributeNames();
    assertThat(attributeNames, arrayWithSize(8));
    assertThat(attributeNames, arrayContainingInAnyOrder(new String[] { "Intended use", "Imperative", "Object-oriented", "Event-driven", "Standardized?", "Type safety", "Type expression", "Type checking" }));
  }
  
}
