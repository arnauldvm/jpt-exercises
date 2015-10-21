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

import java.io.IOException;
import java.io.PrintStream;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sun.net.httpserver.HttpExchange;

import jpt.app01.session.SessionFilter;

/**
 *
 * @author avm
 */
public class RedirectResponder {
  private static final Logger LOG = Logger.getLogger(RedirectResponder.class.getName());

  private RedirectResponder() {} // Pure static

  public static void respond(HttpExchange exchange, String redirectUrl, String name, Optional<String> message) throws IOException {
    LOG.info(() -> "Redirecting to " + name + " [" + redirectUrl + "]" + message.map( m -> ": " + m).orElse(""));
    exchange.getResponseHeaders().set("Location", redirectUrl);
    exchange.sendResponseHeaders(302, 0);
    if ("HEAD".equals(exchange.getRequestMethod())) return;
    try (PrintStream out = new PrintStream(exchange.getResponseBody())) {
      HeaderResponse.send(out, SessionFilter.getSession(exchange), "Redirection to " + name);
      out.println("<DIV>");
      message.ifPresent(m -> out.printf("%s<BR>\n", m));
      out.printf("Go to: <A href=\"%s\"><B>%s</B></A>", redirectUrl, name);
      out.println("</DIV>");
    } catch (Exception e) {
      LOG.log(Level.SEVERE, e, () -> "Failed generating redirection to '" + name + "': ");
      throw(e);
    }
  }
  
}
