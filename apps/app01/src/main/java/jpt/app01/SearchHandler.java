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
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import jpt.app01.data.LanguageProfile;
import jpt.app01.data.LanguagesDatabase;
import static jpt.app01.QueryParser.getParameterValues;

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
    final URI uri = exchange.getRequestURI();
    LOG.info(uri.toString());
    
    String query = exchange.getRequestURI().getQuery();
    final List<String> keyValues = getParameterValues(query, "key");
    if (1!=keyValues.size()) {
      ErrorHandler.handle(exchange, ErrorHandler.ERR_BAD_REQUEST, "Invalid key count: actual " + keyValues.size() + " vs. 1 expected");
      return;
    }
    String keyValue = keyValues.get(0).toLowerCase();
    
    synchronized (database) {
      final Optional<String> firstFoundLanguageName = Arrays.stream(database.getLanguageNames()).
              filter(s -> s.toLowerCase().contains(keyValue)).
              findFirst();
      if (!firstFoundLanguageName.isPresent()) {
        ErrorHandler.handle(exchange, ErrorHandler.ERR_NOT_FOUND, "No language matching '" + keyValue + "' found.");
        return;
      }
      final String languageName = firstFoundLanguageName.get();
      LanguageProfile language = database.getLanguage(languageName);
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