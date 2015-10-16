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
package jpt.app01.user;

import java.util.Optional;

import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;

/**
 *
 * @author avm
 */
public class UserDirectoryTest {

  /**
   * Test of getUser method, of class UserDirectory.
   */
  @Test
  public void testGetUserExisting() {
    System.out.println("getUser");
    String userid = "scott";
    Optional<UserAndPassword> result = UserDirectory.getUser(userid);
    assertTrue(result.isPresent());
    assertNotNull(result.get().getUser());
    assertThat(result.get().getUser().getUserid(), equalTo("scott"));
    assertThat(result.get().getUser().getFirstname(), equalTo("Scott"));
    assertThat(result.get().getUser().getSurname(), equalTo("Tiger"));
    assertThat(result.get().getUser().getEmail(), equalTo("scott.tiger@acme.com"));
    assertTrue(result.get().checkPassword("tiger"));
  }
  
  @Test
  public void testGetUserNotExisting() {
    System.out.println("getUser");
    String userid = "xxx";
    Optional<UserAndPassword> result = UserDirectory.getUser(userid);
    assertFalse(result.isPresent());
  }

}
