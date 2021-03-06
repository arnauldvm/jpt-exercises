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
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import jpt.app01.session.Session;
import jpt.app01.session.SessionFilter;
import jpt.app01.session.SessionRegistry;
import jpt.app01.user.UserAndPassword;
import jpt.app01.user.UserDirectory;

/**
 *
 * @author avm
 */
class LoginHandler implements HttpHandler {
  private static final int BUF_SIZE = 2<<25;

  private static final Logger LOG = Logger.getLogger(HttpHandler.class.getName());
  
  private static final String USERID_ATTRNAME = "userid";
  private static final String PASSWORD_ATTRNAME = "password";

  private final String redirectUrl;
  private final String redirectName;
  private final SessionRegistry sessionRegistry;

  public LoginHandler(SessionRegistry sessionRegistry, String redirectUrl, String redirectName) {
    this.redirectUrl = redirectUrl;
    this.redirectName = redirectName;
    this.sessionRegistry = sessionRegistry;
  }

  @Override
  public void handle(HttpExchange exchange) throws IOException {
    if (!"POST".equals(exchange.getRequestMethod())) {
      Headers responseHeaders = exchange.getResponseHeaders();
      responseHeaders.set("Content-Type", "text/html");
      exchange.sendResponseHeaders(200, 0);
      if ("HEAD".equals(exchange.getRequestMethod())) return;
      try (PrintStream out = new PrintStream(exchange.getResponseBody())) {
        HeaderResponse.send(out, Optional.empty(), "Login form");
        out.println("<DIV>");
        out.println("Please log in:<BR>");
        out.println("<form action='login' method='post'>");
        out.printf ("  User name: <input type='text' name='%s'><br>\n", USERID_ATTRNAME);
        out.printf ("  Password: <input type='password' name='%s'><br>\n", PASSWORD_ATTRNAME);
        out.println("  <input type='submit' value='Submit'>");
        out.println("</form>");
        out.println("</DIV>");
      } catch (Exception e) {
        LOG.log(Level.SEVERE, "Failed login form: ", e);
        throw(e);
      }
    } else {
      final String body;
      try (final InputStream in = exchange.getRequestBody()) {
        StringBuilder bodyBuilder = new StringBuilder();
        int count;
        byte[] buf = new byte[BUF_SIZE];
        Arrays.fill(buf, (byte)0);
        while (0 <= (count = in.read(buf))) {
          bodyBuilder.append(new String(buf, 0, count));
        }
        body = bodyBuilder.toString();
      } catch (Exception e) {
        LOG.log(Level.SEVERE, "Failed reading login query from body", e);
        throw(e);
      }
      final Optional<String> userid = QueryParser.getFirstParameterValue(body, USERID_ATTRNAME);
      if (!userid.isPresent()) {
        ErrorResponder.respond(exchange, ErrorResponder.ERR_BAD_REQUEST, "Missing userid");
        return;
      }
      final Optional<String> password = QueryParser.getFirstParameterValue(body, PASSWORD_ATTRNAME);
      if (!password.isPresent()) {
        ErrorResponder.respond(exchange, ErrorResponder.ERR_BAD_REQUEST, "Missing password");
        return;
      }
      final Optional<UserAndPassword> user = UserDirectory.getUser(userid.get());
      if (!user.map(u -> u.checkPassword(password.get())).orElse(false)) {
        ErrorResponder.respond(exchange, ErrorResponder.ERR_BAD_REQUEST, "Invalid userid or password");
        return;
      }
      LOG.finer(() -> String.format("User '%s' authenticated", userid.get()));
      final Session session = sessionRegistry.create(user.get().getUser());

      SessionFilter.setCookie(exchange.getResponseHeaders(), session);
      RedirectResponder.respond(exchange, redirectUrl, redirectName, Optional.of("Session " + session.getId() + " created"));
    }
  }
  
}
