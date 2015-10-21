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
import java.util.Deque;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import jpt.app01.data.LanguageProfile;
import jpt.app01.data.LanguagesDatabase;
import jpt.app01.session.Session;
import jpt.app01.session.SessionFilter;

import static jpt.app01.QueryParser.getFirstParameterValue;

/**
 * Search a language profile from the database
 * @author avm
 */
class LanguageHandler implements HttpHandler {
  private static final Logger LOG = Logger.getLogger(LanguageHandler.class.getName());

  private static final String HISTORY_ATTNM = "HISTORY";
  private static final String LASTREQ_ATTNM = "LASTREQ";

  private final LanguagesDatabase database;
  
  public LanguageHandler() {
    database = new LanguagesDatabase();
  }
  
  static class LastRequest {
    private final String languageName;
    private final byte[] importantInfo;

    public LastRequest(String languageName) {
      this.languageName = languageName;
      this.importantInfo = getImportantInfo(); // Keep this, it simulates actual memory usage
    }

    public String getLanguageName() {
      return languageName;
    }
    
    public int getSize() {
      return importantInfo.length;
    }

    @Override
    public String toString() {
      return String.format("%s (%d)", languageName, importantInfo.length);
    }
    
    private static final Random random = new Random();
    private static byte[] getImportantInfo() {
      int randomSize = random.nextInt(10000);
      return new byte[randomSize];
    }

  }
  
  public static Deque<LastRequest> getOrCreateHistory(HttpExchange exchange) {
    final Optional<Session> optSession = SessionFilter.getSession(exchange);
    if (!optSession.isPresent()) throw new IllegalStateException("Could not retrieve session");
    Session session = optSession.get();
    return session.getOrCreateAttribute(HISTORY_ATTNM,
            () -> new ConcurrentLinkedDeque<>());
  }

  private static boolean setLastRequest(HttpExchange exchange, final LastRequest lastRequest) {
    // return getOrCreateHistory(exchange).add(lastRequest);
    final Optional<Session> optSession = SessionFilter.getSession(exchange);
    if (!optSession.isPresent()) throw new IllegalStateException("Could not retrieve session");
    Session session = optSession.get();
    session.setAttribute(LASTREQ_ATTNM, lastRequest);
    return true;
   }

  public static Optional<LastRequest> getLastRequest(HttpExchange exchange) {
    // return Optional.ofNullable(getOrCreateHistory(exchange).peekLast());
    final Optional<Session> optSession = SessionFilter.getSession(exchange);
    if (!optSession.isPresent()) throw new IllegalStateException("Could not retrieve session");
    Session session = optSession.get();
    return Optional.ofNullable((LastRequest) session.getAttribute(LASTREQ_ATTNM));
  }
  
  @Override
  public void handle(HttpExchange exchange) throws IOException {
    String rawQuery = exchange.getRequestURI().getRawQuery();
    String languageName;
    try {
      languageName = getFirstParameterValue(rawQuery, "name").
              orElseThrow(() -> new IllegalArgumentException("Missing parameter 'name'"));
    } catch (IllegalArgumentException e) {
      ErrorResponder.respond(exchange, ErrorResponder.ERR_BAD_REQUEST, e.getMessage());
      return;
    }
    
    try {
      final LastRequest lastRequest = new LastRequest(languageName);
      setLastRequest(exchange, lastRequest);
    } catch (Exception e) {
      ErrorResponder.respond(exchange, ErrorResponder.SYS_INTERNAL, "Could not retrieve history");
      return;
    }
 
      LanguageProfile language = database.getLanguage(languageName);
      if (null==language) {
      ErrorResponder.respond(exchange, ErrorResponder.ERR_NOT_FOUND, "Language '" + languageName + "' not found in database.");
      return;
      }
      Headers responseHeaders = exchange.getResponseHeaders();
      responseHeaders.set("Content-Type", "text/html");
      exchange.sendResponseHeaders(200, 0);
      if ("HEAD".equals(exchange.getRequestMethod())) return;
      try (PrintStream out = new PrintStream(exchange.getResponseBody())) {
        HeaderResponse.send(out, SessionFilter.getSession(exchange), languageName);
        out.println("<DIV>");
        out.printf("<B>%s</B>", languageName);
        out.println("<UL>");
        for (String attributeName: language.getAttributeNames()) {
          out.printf("<LI><U>%s</U>: %s</LI>\n", attributeName, language.getAttributeValue(attributeName));
        }
        out.println("</UL>");
        out.println("</DIV>");
      } catch (Exception e) {
        LOG.log(Level.SEVERE, e, () -> "Failed returning HTML page for language '" + languageName + "': ");
        throw(e);
      }
 }

}
