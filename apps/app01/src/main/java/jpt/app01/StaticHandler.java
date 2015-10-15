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
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import jpt.app01.session.SessionFilter;

/**
 * Returns a static file from resources' ROOT subdirectory
 * @author avm
 */
class StaticHandler implements HttpHandler {
  private static final Logger LOG = Logger.getLogger(StaticHandler.class.getName());
  
  @Override
  public void handle(HttpExchange exchange) throws IOException {
    final String uriPath = exchange.getRequestURI().getPath();
    if ("/".equals(uriPath)) {
      RedirectResponder.respond(exchange, "/index.html", "Home", Optional.empty());
      return;
    }
    String resourcePath = "/ROOT" + uriPath; // URI already starts with /
    InputStream in = StaticHandler.class.getResourceAsStream(resourcePath);
    if (in==null) {
      ErrorResponder.respond(exchange, ErrorResponder.ERR_NOT_FOUND, "Resource '" + resourcePath + "' not found.");
      return;
    }
    LOG.info(() -> "Found resource '" + resourcePath + "'");
    try {
      //Headers responseHeaders = exchange.getResponseHeaders();
      //responseHeaders.set("Content-Type", "text/plain");
      exchange.sendResponseHeaders(200, 0);
      if ("HEAD".equals(exchange.getRequestMethod())) return;
      try (OutputStream responseBody = exchange.getResponseBody()) {
        HeaderResponse.send(new PrintStream(responseBody), SessionFilter.getSession(exchange), "Search for language");
        byte[] buffer = new byte[2<<12];
        int n;
        while (0 <= (n = in.read(buffer))) {
          responseBody.write(buffer, 0, n);
        }
      }
    } catch (Exception e) {
      LOG.log(Level.SEVERE, "Failed processing '" + resourcePath + "': ", e);
      throw(e);
    } finally {
      in.close();
    }
  }

}
