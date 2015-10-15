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
import java.util.UUID;
import java.util.logging.Logger;

import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.HttpExchange;

/**
 *
 * @author avm
 */
public class AccessLogFilter extends Filter {
  private static final Logger LOG = Logger.getLogger(AccessLogFilter.class.getName());
  
  public AccessLogFilter() {
  }

  @Override
  public void doFilter(HttpExchange exchange, Chain chain) throws IOException {
    String uuid = UUID.randomUUID().toString();
    LOG.info(() -> String.format("[%s]> %s %s %s", uuid, exchange.getProtocol(), exchange.getRequestMethod(), exchange.getRequestURI()));
    long bgnTime_ms = System.currentTimeMillis();
    chain.doFilter(exchange);
    long endTime_ms = System.currentTimeMillis();
    LOG.info(() -> String.format("[%s]< %s %d", uuid, exchange.getResponseCode(), (endTime_ms-bgnTime_ms)));
  }

  @Override
  public String description() {
    return "Log access (request & response).";
  }
  
}
