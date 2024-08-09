package codesquad.javacafe.util;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpSession;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class CustomHttpSession implements HttpSession {
    private Map<String, Object> attributes = new HashMap<>();

    @Override
    public long getCreationTime() {
        return 0;
    }

    @Override
    public String getId() {
        return "customSessionId";
    }

    @Override
    public long getLastAccessedTime() {
        return 0;
    }

    @Override
    public ServletContext getServletContext() {
        return null;
    }

    @Override
    public void setMaxInactiveInterval(int i) {

    }

    @Override
    public int getMaxInactiveInterval() {
        return 0;
    }

    @Override
    public Object getAttribute(String s) {
        return attributes.get(s);
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        return new Vector<>(attributes.keySet()).elements();
    }

    @Override
    public void setAttribute(String s, Object o) {
        attributes.put(s, o);
    }

    @Override
    public void removeAttribute(String s) {
        attributes.remove(s);
    }

    @Override
    public void invalidate() {
        attributes.clear();
    }

    @Override
    public boolean isNew() {
        return false;
    }
}
