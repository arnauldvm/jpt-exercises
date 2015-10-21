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
class LogoutHandler implements HttpHandler {
  private static final Logger LOG = Logger.getLogger(HttpHandler.class.getName());
  
  private final String redirectUrl;
  private final String redirectName;
  private final SessionRegistry sessionRegistry;

  public LogoutHandler(SessionRegistry sessionRegistry, String redirectUrl, String redirectName) {
    this.redirectUrl = redirectUrl;
    this.redirectName = redirectName;
    this.sessionRegistry = sessionRegistry;
  }

  @Override
  public void handle(HttpExchange exchange) throws IOException {
    final Optional<Session> optSession = SessionFilter.getSession(exchange);
    if (!optSession.isPresent()) {
      LOG.warning("Logging out, but no session retrieved");
    } else {
      final Session session = optSession.get();
      sessionRegistry.remove(session);
      LOG.info("Forget session " + session);
    }
      
    SessionFilter.removeCookie(exchange.getResponseHeaders());
    RedirectResponder.respond(exchange, redirectUrl, redirectName, Optional.of("Logged out"));
  }
  
}
