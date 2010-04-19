package com.googlecode.loggingfilter;

import java.util.Enumeration;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * This request wrapper class extends the support class HttpServletRequestWrapper,
 * which implements all the methods in the HttpServletRequest interface, as
 * delegations to the wrapped request.
 * You only need to override the methods that you need to change.
 * You can get access to the wrapped request using the method getRequest()
 */
class RequestWrapper extends HttpServletRequestWrapper {

  private boolean debug = false;

  public RequestWrapper(HttpServletRequest request, boolean debug) {
    super(request);
    this.debug = debug;
  }

  @Override
  public void setAttribute(java.lang.String name, java.lang.Object o) {
    if (debug) {
      System.out.println("LogFilter::setAttribute(" + name + "=" + o + ")");
    }
  }

  @Override
  public String getParameter(String name) {
    if (debug) {
      System.out.println("LogFilter::getParameter(" + name + ")");
    }
    return getRequest().getParameter(name);
  }

  @Override
  public String[] getParameterValues(String name) {
    if (debug) {
      System.out.println("LogFilter::getParameterValues(" + name + ")");
    }
    return getRequest().getParameterValues(name);
  }

  @Override
  public Enumeration getParameterNames() {
    if (debug) {
      System.out.println("LogFilter::getParameterNames()");
    }
    return getRequest().getParameterNames();
  }

  @Override
  public Map getParameterMap() {
    if (debug) {
      System.out.println("LogFilter::getParameterMap()");
    }

    return getRequest().getParameterMap();
  }
}
