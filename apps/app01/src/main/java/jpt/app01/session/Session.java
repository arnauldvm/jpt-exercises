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
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import jpt.app01.user.User;

/**
 *
 * @author avm
 */
public class Session {
  
  private final User user;
  
  private final String id;
  
  private final Map<String, Object> attributesMap;
  
  public Session(User user) {
    this.id = UUID.randomUUID().toString();
    this.user = user;
    this.attributesMap = new ConcurrentHashMap<>();
  }
  
  public void setAttribute(String name, Object attribute) {
    synchronized(attributesMap) {
      attributesMap.put(name, attribute);
    }
  }
  
  public Object getAttribute(String name) {
    return attributesMap.get(name);
  }
  
  public String getId() {
    return id;
  }
  
  public User getUser() {
    return user;
  }

  public <T> T getOrCreateAttribute(String attributeName, Supplier<? extends T> supplier) {
    T result;
    synchronized (attributesMap) {
      result = (T) attributesMap.get(attributeName);
      if (null == result) {
        result = supplier.get();
        attributesMap.put(attributeName, result);
      }
    }
    return result;
  }

  @Override
  public String toString() {
    return String.format("%s:%s", user.getUserid(), id);
  }
  
}
