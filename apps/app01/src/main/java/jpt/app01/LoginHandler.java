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
import java.util.Optional;
import java.util.logging.Logger;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import jpt.app01.session.Session;
import jpt.app01.session.SessionFilter;
import jpt.app01.session.SessionRegistry;

/**
 *
 * @author avm
 */
class LoginHandler implements HttpHandler {
  private static final Logger LOG = Logger.getLogger(HttpHandler.class.getName());
  
  private final String redirectUrl;
  private final SessionRegistry sessionRegistry;

  public LoginHandler(SessionRegistry sessionRegistry, String redirectUrl) {
    this.redirectUrl = redirectUrl;
    this.sessionRegistry = sessionRegistry;
  }

  @Override
  public void handle(HttpExchange exchange) throws IOException {
    final Session session = sessionRegistry.create("JohnDoe");

    SessionFilter.setCookie(exchange.getResponseHeaders(), session);
    RedirectResponder.respond(exchange, redirectUrl, "home", Optional.of("Session " + session.getId() + " created"));
  }
  
}
