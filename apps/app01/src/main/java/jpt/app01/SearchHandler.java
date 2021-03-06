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
import java.net.URLEncoder;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import jpt.app01.data.LanguagesDatabase;
import jpt.app01.session.SessionFilter;

import static jpt.app01.QueryParser.getFirstParameterValue;

/**
 * Search a language profile from the database
 * @author avm
 */
class SearchHandler implements HttpHandler {
  private static final Logger LOG = Logger.getLogger(SearchHandler.class.getName());

  private final LanguagesDatabase database;
  public SearchHandler() {
    database = new LanguagesDatabase();
  }
  
  @Override
  public void handle(HttpExchange exchange) throws IOException {
    String rawQuery = exchange.getRequestURI().getRawQuery();
    String keyValue;
    try {
      keyValue = getFirstParameterValue(rawQuery, "key").
              orElseThrow(() -> new IllegalArgumentException("Missing parameter 'key'")).
              toLowerCase();
    } catch (IllegalArgumentException e) {
      ErrorResponder.respond(exchange, ErrorResponder.ERR_BAD_REQUEST, e.getMessage());
      return;
    }
    
    synchronized (database) {
      List<String> foundLanguageNames = database.findAllMatchingLanguages(keyValue);
      if (foundLanguageNames.isEmpty()) {
        ErrorResponder.respond(exchange, ErrorResponder.ERR_NOT_FOUND, "No language matching '" + keyValue + "' found.");
        return;
      }
      if (1 == foundLanguageNames.size()) {
        final String languageName = foundLanguageNames.get(0);
        final String url = "/language?name=" + URLEncoder.encode(languageName, "UTF-8");
        RedirectResponder.respond(exchange, url, "language page for '" + languageName + "'", Optional.empty());
      } else {
        Headers responseHeaders = exchange.getResponseHeaders();
        responseHeaders.set("Content-Type", "text/html");
        exchange.sendResponseHeaders(200, 0);
        if ("HEAD".equals(exchange.getRequestMethod())) return;
        try (PrintStream out = new PrintStream(exchange.getResponseBody())) {
          HeaderResponse.send(out, SessionFilter.getSession(exchange), String.format("List of languages matching '%s'", keyValue));
          out.println("<DIV>");
          out.printf("List of languages matching <B>%s</B>", keyValue);
          out.println("<UL>");
          for (String languageName: foundLanguageNames) {
            final String url = "/language?name=" + URLEncoder.encode(languageName, "UTF-8");
            out.printf("<LI><A href=\"%s\"><B>%s</B></A></LI>\n", url, languageName);
          }
          out.println("</UL>");
          out.println("</DIV>");
        } catch (Exception e) {
          LOG.log(Level.SEVERE, e, () -> "Failed generating list of languages matches for '" + keyValue + "': ");
          throw(e);
        }
      }
    }
  }

}
