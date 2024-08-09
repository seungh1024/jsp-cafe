package codesquad.javacafe.util;

import jakarta.servlet.*;
import jakarta.servlet.http.*;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.*;

public class CustomHttpServletRequest implements HttpServletRequest {
    private String method;
    private Map<String, String[]> parameters = new HashMap<>();
    private Map<String, Object> attributes = new HashMap<>();
    private HttpSession session;
    private String uri;
    private byte[] body;

    public void setMethod(String method) {
        this.method = method;
    }

    public void addParameter(String name, String value) {
        parameters.put(name, new String[]{value});
    }

    @Override
    public HttpSession getSession(boolean create) {
        if (session == null && create) {
            session = new CustomHttpSession();
        }
        return session;
    }

    @Override
    public HttpSession getSession() {
        return getSession(true);
    }

    // Other methods remain unchanged
    @Override
    public String getAuthType() { return ""; }
    @Override
    public Cookie[] getCookies() { return new Cookie[0]; }
    @Override
    public long getDateHeader(String s) { return 0; }
    @Override
    public String getHeader(String s) { return ""; }
    @Override
    public Enumeration<String> getHeaders(String s) { return null; }
    @Override
    public Enumeration<String> getHeaderNames() { return null; }
    @Override
    public int getIntHeader(String s) { return 0; }
    @Override
    public String getMethod() { return method; }
    @Override
    public String getPathInfo() { return ""; }
    @Override
    public String getPathTranslated() { return ""; }
    @Override
    public String getContextPath() { return ""; }
    @Override
    public String getQueryString() { return ""; }
    @Override
    public String getRemoteUser() { return ""; }
    @Override
    public boolean isUserInRole(String s) { return false; }
    @Override
    public Principal getUserPrincipal() { return null; }
    @Override
    public String getRequestedSessionId() { return ""; }
    @Override
    public String getRequestURI() { return uri; }
    public void setRequestURI(String requestURI) {
        this.uri = requestURI;}
    @Override
    public StringBuffer getRequestURL() { return null; }
    @Override
    public String getServletPath() { return ""; }
    @Override
    public String changeSessionId() { return ""; }
    @Override
    public boolean isRequestedSessionIdValid() { return false; }
    @Override
    public boolean isRequestedSessionIdFromCookie() { return false; }
    @Override
    public boolean isRequestedSessionIdFromURL() { return false; }
    @Override
    public boolean authenticate(HttpServletResponse httpServletResponse) throws IOException, ServletException { return false; }
    @Override
    public void login(String s, String s1) throws ServletException { }
    @Override
    public void logout() throws ServletException { }
    @Override
    public Collection<Part> getParts() throws IOException, ServletException { return List.of(); }
    @Override
    public Part getPart(String s) throws IOException, ServletException { return null; }
    @Override
    public <T extends HttpUpgradeHandler> T upgrade(Class<T> aClass) throws IOException, ServletException { return null; }
    @Override
    public Map<String, String[]> getParameterMap() { return parameters; }
    @Override
    public String getProtocol() { return ""; }
    @Override
    public String getScheme() { return ""; }
    @Override
    public String getServerName() { return ""; }
    @Override
    public int getServerPort() { return 0; }
    @Override
    public BufferedReader getReader() throws IOException { return null; }
    @Override
    public String getRemoteAddr() { return ""; }
    @Override
    public String getRemoteHost() { return ""; }
    @Override
    public void setAttribute(String name, Object o) { attributes.put(name, o); }
    @Override
    public void removeAttribute(String s) { }
    @Override
    public Locale getLocale() { return null; }
    @Override
    public Enumeration<Locale> getLocales() { return null; }
    @Override
    public boolean isSecure() { return false; }
    @Override
    public RequestDispatcher getRequestDispatcher(String s) { return new MockRequestDispatcher(s); }
    @Override
    public int getRemotePort() { return 0; }
    @Override
    public String getLocalName() { return ""; }
    @Override
    public String getLocalAddr() { return ""; }
    @Override
    public int getLocalPort() { return 0; }
    @Override
    public ServletContext getServletContext() { return null; }
    @Override
    public AsyncContext startAsync() throws IllegalStateException { return null; }
    @Override
    public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse) throws IllegalStateException { return null; }
    @Override
    public boolean isAsyncStarted() { return false; }
    @Override
    public boolean isAsyncSupported() { return false; }
    @Override
    public AsyncContext getAsyncContext() { return null; }
    @Override
    public DispatcherType getDispatcherType() { return null; }
    @Override
    public String getRequestId() { return ""; }
    @Override
    public String getProtocolRequestId() { return ""; }
    @Override
    public ServletConnection getServletConnection() { return null; }
    @Override
    public Object getAttribute(String name) { return attributes.get(name); }
    @Override
    public Enumeration<String> getAttributeNames() { return null; }
    @Override
    public String getCharacterEncoding() { return ""; }
    @Override
    public void setCharacterEncoding(String s) throws UnsupportedEncodingException { }
    @Override
    public int getContentLength() { return 0; }
    @Override
    public long getContentLengthLong() { return 0; }
    @Override
    public String getContentType() { return ""; }
    @Override
    public ServletInputStream getInputStream() throws IOException {
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(body);
        return new ServletInputStream() {
            @Override
            public boolean isFinished() {
                return byteArrayInputStream.available() == 0;
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setReadListener(ReadListener readListener) {
                // Not implemented
            }

            @Override
            public int read() throws IOException {
                return byteArrayInputStream.read();
            }
        };
    }

    @Override
    public String getParameter(String s) { String[] values = parameters.get(s); return values != null && values.length > 0 ? values[0] : null; }
    @Override
    public Enumeration<String> getParameterNames() { return null; }
    @Override
    public String[] getParameterValues(String s) { return new String[0]; }

    public void setBody(String s) {
        this.body = s.getBytes();
    }
}