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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import jpt.app01.user.User;

/**
 *
 * @author avm
 */
public class SessionRegistry {
  
  private final Map <String, Session> sessions;

  public SessionRegistry() {
    this.sessions = new ConcurrentHashMap<>();
  }

  public Session get(String sessionId) {
    return sessions.get(sessionId);
  }

  public Session create(User user) {
    Session session = new Session(user);
    sessions.put(session.getId(), session);
    return session;
  }

  public void remove(Session session) {
    sessions.remove(session.getId());
  }
  
}
