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
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import com.sun.net.httpserver.HttpServer;

public class Main {

  private static final int DEFAULT_QUEUE_SIZE = 0;
  private static final int DEFAULT_PORT_NUMBER = 6666;

  public static void main(String[] args) throws IOException {
    int portNumber = DEFAULT_PORT_NUMBER;
    int queueSize = DEFAULT_QUEUE_SIZE;
    for (String arg : args) {
      if (arg.startsWith("-p")) {
        portNumber = Integer.parseInt(arg.replaceFirst("\\-p", ""));
      } else if (arg.startsWith("-q")) {
        queueSize = Integer.parseInt(arg.replaceFirst("\\-q", ""));
      } else {
        System.err.println("Usage: java " + Main.class.getName() + " [option...]");
        System.err.println("Options: ");
        System.err.println("    -p<i>  change the listen port number (" + DEFAULT_PORT_NUMBER +")");
        System.err.println("    -q<i>  change the queue size (" + DEFAULT_QUEUE_SIZE + ")");
        System.exit(1);
      }
    }
    HttpServer server = HttpServer.create(new InetSocketAddress(portNumber), queueSize);

    server.createContext("/", new Handler());
    server.setExecutor(Executors.newCachedThreadPool());
    server.start();
    System.out.println("Server is listening on port " + portNumber);
  }

}
