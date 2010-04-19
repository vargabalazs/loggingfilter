/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.googlecode.loggingfilter;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Enumeration;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author farnulfo
 */
public class LogFilter implements Filter {

  private static final boolean debug = true;
  // The filter configuration object we are associated with.  If
  // this value is null, this filter instance is not currently
  // configured.
  private FilterConfig filterConfig = null;

  public LogFilter() {
  }

  private void doBeforeProcessing(RequestWrapper request, ResponseWrapper response)
          throws IOException, ServletException {
    if (debug) {
      log("LogFilter:DoBeforeProcessing");
    }

    // Write code here to process the request and/or response before
    // the rest of the filter chain is invoked.

    // For example, a filter that implements setParameter() on a request
    // wrapper could set parameters on the request before passing it on
    // to the filter chain.
	/*
    String [] valsOne = {"val1a", "val1b"};
    String [] valsTwo = {"val2a", "val2b", "val2c"};
    request.setParameter("name1", valsOne);
    request.setParameter("nameTwo", valsTwo);
     */

    log("LogFilter: query: '" + request.getQueryString() + "'");

    for (Enumeration en = request.getHeaderNames(); en.hasMoreElements();) {
      String name = (String) en.nextElement();
      Object value = request.getHeader(name);
      log("LogFilter: header: '" + name + "' = '" + value.toString() + "'");

    }

    // For example, a logging filter might log items on the request object,
    // such as the parameters.
    for (Enumeration en = request.getParameterNames(); en.hasMoreElements();) {
      String name = (String) en.nextElement();
      String values[] = request.getParameterValues(name);
      int n = values.length;
      StringBuilder buf = new StringBuilder();
      buf.append(name);
      buf.append("=");
      for (int i = 0; i < n; i++) {
        buf.append(values[i]);
        if (i < n - 1) {
          buf.append(",");
        }
      }
      log("LogFilter: parameters: " + buf.toString());
    }
  }

  private void doAfterProcessing(RequestWrapper request, ResponseWrapper response)
          throws IOException, ServletException {
    if (debug) {
      log("LogFilter:DoAfterProcessing");
    }

    // Write code here to process the request and/or response after
    // the rest of the filter chain is invoked.

    // For example, a logging filter might log the attributes on the
    // request object after the request has been processed.
    for (Enumeration en = request.getAttributeNames(); en.hasMoreElements();) {
      String name = (String) en.nextElement();
      Object value = request.getAttribute(name);
      log("attribute: " + name + "=" + value.toString());

    }
  }

  /**
   *
   * @param request The servlet request we are processing
   * @param response The servlet response we are creating
   * @param chain The filter chain we are processing
   *
   * @exception IOException if an input/output error occurs
   * @exception ServletException if a servlet error occurs
   */
  public void doFilter(ServletRequest request, ServletResponse response,
          FilterChain chain)
          throws IOException, ServletException {

    if (debug) {
      log("LogFilter:doFilter()");
    }

    // Create wrappers for the request and response objects.
    // Using these, you can extend the capabilities of the
    // request and response, for example, allow setting parameters
    // on the request before sending the request to the rest of the filter chain,
    // or keep track of the cookies that are set on the response.
    //
    // Caveat: some servers do not handle wrappers very well for forward or
    // include requests.
    RequestWrapper wrappedRequest = new RequestWrapper((HttpServletRequest) request, debug);
    ResponseWrapper wrappedResponse = new ResponseWrapper((HttpServletResponse) response);

    doBeforeProcessing(wrappedRequest, wrappedResponse);

    Throwable problem = null;

    try {
      chain.doFilter(wrappedRequest, wrappedResponse);
      wrappedResponse.finishResponse();
    } catch (Throwable t) {
      // If an exception is thrown somewhere down the filter chain,
      // we still want to execute our after processing, and then
      // rethrow the problem after that.
      problem = t;
      t.printStackTrace();
    }

    doAfterProcessing(wrappedRequest, wrappedResponse);

    // If there was a problem, we want to rethrow it if it is
    // a known type, otherwise log it.
    if (problem != null) {
      if (problem instanceof ServletException) {
        throw (ServletException) problem;
      }
      if (problem instanceof IOException) {
        throw (IOException) problem;
      }
      sendProcessingError(problem, response);
    }
  }

  /**
   * Return the filter configuration object for this filter.
   */
  public FilterConfig getFilterConfig() {
    return (this.filterConfig);
  }

  /**
   * Set the filter configuration object for this filter.
   *
   * @param filterConfig The filter configuration object
   */
  public void setFilterConfig(FilterConfig filterConfig) {
    this.filterConfig = filterConfig;
  }

  /**
   * Destroy method for this filter
   */
  public void destroy() {
  }

  /**
   * Init method for this filter
   */
  public void init(FilterConfig filterConfig) {
    this.filterConfig = filterConfig;
    if (filterConfig != null) {
      if (debug) {
        log("LogFilter: Initializing filter");
      }
    }
  }

  /**
   * Return a String representation of this object.
   */
  @Override
  public String toString() {
    if (filterConfig == null) {
      return ("LogFilter()");
    }
    StringBuffer sb = new StringBuffer("LogFilter(");
    sb.append(filterConfig);
    sb.append(")");
    return (sb.toString());

  }

  private void sendProcessingError(Throwable t, ServletResponse response) {
    String stackTrace = getStackTrace(t);

    if (stackTrace != null && !stackTrace.equals("")) {
      try {
        response.setContentType("text/html");
        PrintStream ps = new PrintStream(response.getOutputStream());
        PrintWriter pw = new PrintWriter(ps);
        pw.print("<html>\n<head>\n<title>Error</title>\n</head>\n<body>\n"); //NOI18N

        // PENDING! Localize this for next official release
        pw.print("<h1>The resource did not process correctly</h1>\n<pre>\n");
        pw.print(stackTrace);
        pw.print("</pre></body>\n</html>"); //NOI18N
        pw.close();
        ps.close();
        response.getOutputStream().close();
      } catch (Exception ex) {
      }
    } else {
      try {
        PrintStream ps = new PrintStream(response.getOutputStream());
        t.printStackTrace(ps);
        ps.close();
        response.getOutputStream().close();
      } catch (Exception ex) {
      }
    }
  }

  public static String getStackTrace(Throwable t) {
    String stackTrace = null;
    try {
      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw);
      t.printStackTrace(pw);
      pw.close();
      sw.close();
      stackTrace = sw.getBuffer().toString();
    } catch (Exception ex) {
    }
    return stackTrace;
  }

  public void log(String msg) {
    filterConfig.getServletContext().log(msg);
  }
}
