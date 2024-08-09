package codesquad.javacafe.util;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

public class CustomHttpServletResponse implements HttpServletResponse {
    private String redirectedUrl;
    private String forwardedUrl;
    private int statusCode;
    private final CharArrayWriter charArrayWriter = new CharArrayWriter();


    @Override
    public void addCookie(Cookie cookie) {

    }

    @Override
    public boolean containsHeader(String s) {
        return false;
    }

    @Override
    public String encodeURL(String s) {
        return "";
    }

    @Override
    public String encodeRedirectURL(String s) {
        return "";
    }

    @Override
    public void sendError(int i, String s) throws IOException {

    }

    @Override
    public void sendError(int i) throws IOException {

    }

    @Override
    public void sendRedirect(String location) throws IOException {
        this.redirectedUrl = location;
    }

    @Override
    public void setDateHeader(String s, long l) {

    }

    @Override
    public void addDateHeader(String s, long l) {

    }

    @Override
    public void setHeader(String s, String s1) {

    }

    @Override
    public void addHeader(String s, String s1) {

    }

    @Override
    public void setIntHeader(String s, int i) {

    }

    @Override
    public void addIntHeader(String s, int i) {

    }

    @Override
    public void setStatus(int i) {
        this.statusCode = i;
    }

    @Override
    public int getStatus() {
        return statusCode;
    }

    @Override
    public String getHeader(String s) {
        return "";
    }

    @Override
    public Collection<String> getHeaders(String s) {
        return List.of();
    }

    @Override
    public Collection<String> getHeaderNames() {
        return List.of();
    }

    public String getRedirectedUrl() {
        return redirectedUrl;
    }

    public void setForwardedUrl(String forwardedUrl) {
        this.forwardedUrl = forwardedUrl;
    }

    public String getForwardedUrl() {
        return forwardedUrl;
    }

    @Override
    public String getCharacterEncoding() {
        return "";
    }

    @Override
    public String getContentType() {
        return "";
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        return null;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        return new PrintWriter(charArrayWriter);
    }

    public String getOutput() {
        return charArrayWriter.toString();
    }

    @Override
    public void setCharacterEncoding(String s) {

    }

    @Override
    public void setContentLength(int i) {

    }

    @Override
    public void setContentLengthLong(long l) {

    }

    @Override
    public void setContentType(String s) {

    }

    @Override
    public void setBufferSize(int i) {

    }

    @Override
    public int getBufferSize() {
        return 0;
    }

    @Override
    public void flushBuffer() throws IOException {

    }

    @Override
    public void resetBuffer() {

    }

    @Override
    public boolean isCommitted() {
        return false;
    }

    @Override
    public void reset() {

    }

    @Override
    public void setLocale(Locale locale) {

    }

    @Override
    public Locale getLocale() {
        return null;
    }

    // Implement other methods as needed...
}
