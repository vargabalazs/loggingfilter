package com.googlecode.loggingfilter;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Vector;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * This response wrapper class extends the support class HttpServletResponseWrapper,
 * which implements all the methods in the HttpServletResponse interface, as
 * delegations to the wrapped response.
 * You only need to override the methods that you need to change.
 * You can get access to the wrapped response using the method getResponse()
 */
class ResponseWrapper extends HttpServletResponseWrapper {

  // You might, for example, wish to know what cookies were set on the response
  // as it went throught the filter chain. Since HttpServletRequest doesn't
  // have a get cookies method, we will need to store them locally as they
  // are being set.
  protected PrintWriter writer = null;
  protected ServletOutputStream stream = null;
  protected HttpServletResponse response;

  public ResponseWrapper(HttpServletResponse response) {
    super(response);
    this.response = response;
  }

  @Override
  public ServletOutputStream getOutputStream()
          throws IOException {
    if (this.writer != null) {
      throw new IllegalStateException("getWriter() has already been called!");
    }
    if (this.stream == null) {
      this.stream = createOutputStream();
    }
    return this.stream;
  }

  @Override
  public PrintWriter getWriter()
          throws IOException {
    if (this.writer != null) {
      return this.writer;
    }
    if (this.stream != null) {
      throw new IllegalStateException("getOutputStream() has already been called!");
    }
    this.stream = createOutputStream();
    this.writer = new PrintWriter(new OutputStreamWriter(this.stream));
    return this.writer;
  }

  public void finishResponse() throws IOException {
    if (this.writer != null) {
      this.writer.close();
    } else if (this.stream != null) {
      this.stream.close();
    }
  }

  @Override
  public void flushBuffer()
          throws IOException {
    this.stream.flush();
  }

  private ServletOutputStream createOutputStream() throws IOException {
    return new LogResponseStream(this.response.getOutputStream());
  }
}
