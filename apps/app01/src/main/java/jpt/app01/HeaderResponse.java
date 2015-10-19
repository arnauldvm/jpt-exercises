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

import java.io.PrintStream;
import java.util.Optional;

import jpt.app01.session.Session;

/**
 *
 * @author avm
 */
public class HeaderResponse {
  
  private HeaderResponse() {} // Pure static
  
  public static void send(PrintStream out, Optional<Session> session, String title) {
    out.printf("<TITLE>App01: %s</TITLE>\n", title);
    out.println("<META charset='UTF-8'>");
    out.println("<TABLE width=100% border=1 cellspacing=0><TR>");
    out.println("<TD align=left nowrap><A href='/'>Home</A></TD>");
    out.println("<TD align=left width=100%><BIG><B>App 01: A programming languages DB</B></BIG></TD>");
    out.printf("<TD align=right nowrap>%s</TD>\n", session.map(s -> s.getUser().toString().replaceAll(" ", "&nbsp;")).orElse("&nbsp;"));
    out.println("</TABLE><BR>");
  }
  
}
