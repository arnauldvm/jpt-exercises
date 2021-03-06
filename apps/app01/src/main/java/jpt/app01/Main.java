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
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.HttpServer;

import jpt.app01.session.SessionFilter;
import jpt.app01.session.SessionRegistry;

public class Main {
  private static final Logger LOG = Logger.getLogger(Main.class.getName());

  private static final int DEFAULT_QUEUE_SIZE = 0;
  private static final int DEFAULT_POOL_SIZE = 5;
  private static final int DEFAULT_PORT_NUMBER = 7666;
  
  private int portNumber = DEFAULT_PORT_NUMBER;
  private int queueSize = DEFAULT_QUEUE_SIZE;
  private int poolSize = DEFAULT_POOL_SIZE;
  
  public Main() { }

  public void readArgs(String[] args) {
    for (String arg : args) {
      if (arg.startsWith("-p")) {
        portNumber = Integer.parseInt(arg.replaceFirst("\\-p", ""));
      } else if (arg.startsWith("-q")) {
        queueSize = Integer.parseInt(arg.replaceFirst("\\-q", ""));
      } else if (arg.startsWith("-t")) {
        poolSize = Integer.parseInt(arg.replaceFirst("\\-t", ""));
      } else {
        System.err.println("Usage: java " + Main.class.getName() + " [option...]");
        System.err.println("Options: ");
        System.err.println("    -p<i>  change the listen port number (" + DEFAULT_PORT_NUMBER +")");
        System.err.println("    -q<i>  change the queue size (" + DEFAULT_QUEUE_SIZE + ")");
        System.err.println("    -t<i>  change the threads pool size (" + DEFAULT_POOL_SIZE + ")");
        System.exit(1);
      }
    }
  }

  private void start() throws IOException {
    HttpServer server = HttpServer.create(new InetSocketAddress(portNumber), queueSize);
    
    SessionRegistry sessionRegistry = new SessionRegistry();
    SessionFilter sessionFilter = new SessionFilter(sessionRegistry, "/login");
    AccessLogFilter accessLogFilter = new AccessLogFilter();
    
    List<Filter> defaultFilters = Arrays.asList(new Filter[] { accessLogFilter, sessionFilter });
    
    server.createContext("/", new StaticHandler()).getFilters().addAll(defaultFilters);
    server.createContext("/login", new LoginHandler(sessionRegistry, "/", "home")).getFilters().add(accessLogFilter); // no session filter or else infinite redirect loop!
    server.createContext("/search", new SearchHandler()).getFilters().addAll(defaultFilters);
    server.createContext("/language", new LanguageHandler()).getFilters().addAll(defaultFilters);
    final ExecutorService threadPool = Executors.newFixedThreadPool(poolSize);
    server.setExecutor(threadPool);
    server.start();
    LOG.info(() -> "Server is listening on port " + portNumber);
  }

  public static void main(String[] args) throws IOException {
    Main main = new Main();
    main.readArgs(args);
    main.start();
  }

}
