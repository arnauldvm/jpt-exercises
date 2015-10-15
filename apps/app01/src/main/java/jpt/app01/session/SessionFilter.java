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
package jpt.app01.session;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

import jpt.app01.CookieParser;
import jpt.app01.RedirectResponder;

/**
 *
 * @author avm
 */
public class SessionFilter extends Filter {
  private static final Logger LOG = Logger.getLogger(SessionFilter.class.getName());
  
  private static final String SESSIONID_COOKIENAME = "SESSIONID";
  
  private final SessionRegistry sessionRegistry;
  private final String redirectUrl;

  public SessionFilter(SessionRegistry sessionRegistry, String redirectUrl) {
    this.redirectUrl = redirectUrl;
    this.sessionRegistry = sessionRegistry;
  }

  @Override
  public void doFilter(HttpExchange exchange, Chain chain) throws IOException {
    final List<String> cookiesAttribute = exchange.getRequestHeaders().get("Cookie");
    if (null==cookiesAttribute) {
      RedirectResponder.respond(exchange, redirectUrl, "login", Optional.of("Not yet logged in."));
      return; // no doFilter since we abort here
    }
    final Optional<String> sessionId = cookiesAttribute.stream().
            map(h -> CookieParser.getCookie(h, SESSIONID_COOKIENAME)).
            filter(oc -> oc.isPresent()).map(oc -> oc.get()).
            findFirst();
    if (!sessionId.isPresent()) {
      RedirectResponder.respond(exchange, redirectUrl, "login", Optional.of("Session not found."));
      return; // no doFilter since we abort here
    }
    LOG.info(() -> "Retrieved session " + sessionId);
    sessionRegistry.select(sessionId);
    chain.doFilter(exchange);
  }

  @Override
  public String description() {
    return "Retrieve session from Map and store it in ThreadLocal, redirect to login if session not found.";
  }

  public void setCookie(Headers responseHeaders, Session session) {
    responseHeaders.set("Set-Cookie", SESSIONID_COOKIENAME + "=" + session.getId());
  }
  
}
