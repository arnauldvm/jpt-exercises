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

import com.sun.net.httpserver.Headers;
import java.io.IOException;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.PrintStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import static jpt.app01.QueryParser.getFirstParameterValue;
import jpt.app01.data.LanguageProfile;
import jpt.app01.data.LanguagesDatabase;

/**
 * Search a language profile from the database
 * @author avm
 */
class LanguageHandler implements HttpHandler {
  private static final Logger LOG = Logger.getLogger(LanguageHandler.class.getName());

  private final LanguagesDatabase database;
  public LanguageHandler() {
    database = new LanguagesDatabase();
  }
  
  @Override
  public void handle(HttpExchange exchange) throws IOException {
    String query = exchange.getRequestURI().getQuery();
    String languageName;
    try {
      languageName = getFirstParameterValue(query, "name").
              orElseThrow(() -> new IllegalArgumentException("Missing parameter 'name'"));
    } catch (IllegalArgumentException e) {
      ErrorResponder.respond(exchange, ErrorResponder.ERR_BAD_REQUEST, e.getMessage());
      return;
    }
    
    synchronized (database) {
      LanguageProfile language = database.getLanguage(languageName);
      if (null==language) {
      ErrorResponder.respond(exchange, ErrorResponder.ERR_NOT_FOUND, "Language '" + languageName + "' not found in database.");
      return;
      }
      Headers responseHeaders = exchange.getResponseHeaders();
      responseHeaders.set("Content-Type", "text/html");
      exchange.sendResponseHeaders(200, 0);
      try (PrintStream out = new PrintStream(exchange.getResponseBody())) {
        out.printf("<TITLE>App 01: %s</TITLE>\n", languageName);
        out.println("<DIV>");
        out.printf("<B>%s</B>", languageName);
        out.println("<UL>");
        for (String attributeName: language.getAttributeNames()) {
          out.printf("<LI><U>%s</U>: %s</LI>\n", attributeName, language.getAttributeValue(attributeName));
        }
        out.println("</UL>");
        out.println("</DIV>");
      } catch (Exception e) {
        LOG.log(Level.SEVERE, "Failed returning HTML page for language '" + languageName + "': ", e);
        throw(e);
      }
    }
  }

}
