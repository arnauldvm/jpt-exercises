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
package jpt.app00;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

/**
 *
 * @author avm
 */
class Handler implements HttpHandler {
  private static final Logger LOG = Logger.getLogger(Handler.class.getName());

  @Override
  public void handle(HttpExchange exchange) throws IOException {
  final String requestMethod = exchange.getRequestMethod();
  final URI requestUri = exchange.getRequestURI();
  try {
    if (!requestMethod.equalsIgnoreCase("HEAD")) {
      Headers responseHeaders = exchange.getResponseHeaders();
      responseHeaders.set("Content-Type", "text/plain");
      exchange.sendResponseHeaders(200, 0);

      try (OutputStream responseBody = exchange.getResponseBody()) {
        responseBody.write(
          String.format("%s %s %s\r\n\r\n", requestMethod, exchange.getProtocol(), requestUri).getBytes()
        );

        Headers requestHeaders = exchange.getRequestHeaders();
        responseBody.write(
          requestHeaders.entrySet().stream().map(
                  entry -> entry.getKey() + "=" + entry.getValue()
          ).collect(Collectors.joining("\n")).getBytes()
        );

      }
    }
  } catch (Exception e) {
    LOG.log(Level.WARNING, "Failed processing " + requestMethod + " " + requestUri, e);
    exchange.sendResponseHeaders(500, 0);
  }
  }

}
