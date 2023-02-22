package ru.itmo.park.config;

import org.apache.commons.io.output.TeeOutputStream;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.itmo.park.security.jwt.JwtProvider;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class HttpLoggingFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(HttpLoggingFilter.class);

    @Value("${spring.application.name}") String applicationName;
    @Value("${spring.datasource.url:NO_DATABASE}") String database;

    private final JwtProvider jwtService;

    public HttpLoggingFilter(JwtProvider jwtService) {
        this.jwtService = jwtService;
    }


    @Override
    public void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                         FilterChain chain) throws IOException, ServletException {

        JSONObject obj = new JSONObject();

        BufferedRequestWrapper bufferedRequest = new BufferedRequestWrapper(
                httpServletRequest);
        BufferedResponseWrapper bufferedResponse = new BufferedResponseWrapper(
                httpServletResponse);

        try {
            String requestContentType = httpServletRequest.getHeader("Content-Type");
            SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
            dateFormatGmt.setTimeZone(TimeZone.getTimeZone("Europe/Moscow"));
            try {
                obj.put("database", database.split("=")[1]);
            } catch (Exception e){
                obj.put("database", database);
            }
            obj.put("applicationName", applicationName);
            obj.put("timestamp", dateFormatGmt.format(new Date()));
            obj.put("path", httpServletRequest.getServletPath());
            obj.put("method", httpServletRequest.getMethod());
            try {
                Integer userId = jwtService.getCurrentUser(httpServletRequest.getHeader("Authorization"));
                obj.put("userId", userId != null ? userId.toString() : "Token no valid");
            }catch (Exception ignored){
                obj.put("userId", "No token");
            }
            String query = httpServletRequest.getQueryString();
            obj.put("query", query == null ? "" : query);
            try {
                obj.put("requestContentType", requestContentType.split(";")[0]);
            }catch (Exception e){
                obj.put("requestContentType", requestContentType == null
                        ? JSONObject.NULL
                        : requestContentType);
            }
            obj.put("clientIp", httpServletRequest.getHeader("X-FORWARDED-FOR") != null
                    ? httpServletRequest.getHeader("X-FORWARDED-FOR").split(",",2)[0]
                    : httpServletRequest.getRemoteAddr());
            try{
                if(!requestContentType.contains("multipart/form-data")){
                    obj.put("requestBody", bufferedRequest.getRequestBody());
                } else {
                    obj.put("requestBody", JSONObject.NULL);
                }
            } catch (Exception e){
                obj.put("requestBody", JSONObject.NULL);
            }
        } catch (Exception e) {
            obj.append("exception", e.toString());
        }

        try {
            chain.doFilter(bufferedRequest, bufferedResponse);
        } catch (Exception ex){
            JSONObject js = new JSONObject();
            js.put("message", ex.getMessage());
            js.put("httpStatus", HttpStatus.INTERNAL_SERVER_ERROR.value());
            httpServletResponse.setStatus(500);
            httpServletResponse.getWriter().write(js.toString());
            obj.append("exception",ex.toString());
        }


        try{
            String responseContentType = bufferedResponse.getContentType();
            obj.put("responseContentType", responseContentType == null
                    ? JSONObject.NULL
                    : responseContentType.split(";")[0]);
            obj.put("httpStatus", bufferedResponse.getStatus());
            try {
                if(!responseContentType.contains("multipart/form-data")) {
                    obj.put("responseBody", bufferedResponse.getContent());
                } else {
                    obj.put("responseBody", JSONObject.NULL);
                }
            } catch (Exception e) {
                obj.put("responseBody", JSONObject.NULL);
            }
        } catch (Exception e){
            obj.append("exception",e.toString());
        }
        if(!httpServletRequest.getServletPath().equals("/alarm")) System.out.println(obj);
    }



    private Map<String, String> getTypesafeRequestMap(HttpServletRequest request) {
        Map<String, String> typesafeRequestMap = new HashMap<String, String>();
        Enumeration<?> requestParamNames = request.getParameterNames();
        while (requestParamNames.hasMoreElements()) {
            String requestParamName = (String) requestParamNames.nextElement();
            String requestParamValue;
            if (requestParamName.equalsIgnoreCase("password")) {
                requestParamValue = "********";
            } else {
                requestParamValue = request.getParameter(requestParamName);
            }
            typesafeRequestMap.put(requestParamName, requestParamValue);
        }
        return typesafeRequestMap;
    }

    @Override
    public void destroy() {
    }

    private static final class BufferedRequestWrapper extends
            HttpServletRequestWrapper {

        private ByteArrayInputStream bais = null;
        private ByteArrayOutputStream baos = null;
        private BufferedServletInputStream bsis = null;
        private byte[] buffer = null;

        public BufferedRequestWrapper(HttpServletRequest req)
                throws IOException {
            super(req);
            // Read InputStream and store its content in a buffer.
            InputStream is = req.getInputStream();
            this.baos = new ByteArrayOutputStream();
            byte buf[] = new byte[1024];
            int read;
            while ((read = is.read(buf)) > 0) {
                this.baos.write(buf, 0, read);
            }
            this.buffer = this.baos.toByteArray();
        }

        @Override
        public ServletInputStream getInputStream() {
            this.bais = new ByteArrayInputStream(this.buffer);
            this.bsis = new BufferedServletInputStream(this.bais);
            return this.bsis;
        }

        String getRequestBody() throws IOException {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    this.getInputStream()));
            String line = null;
            StringBuilder inputBuffer = new StringBuilder();
            do {
                line = reader.readLine();
                if (null != line) {
                    inputBuffer.append(line.trim());
                }
            } while (line != null);
            reader.close();
            return inputBuffer.toString().trim();
        }

    }

    private static final class BufferedServletInputStream extends
            ServletInputStream {

        private ByteArrayInputStream bais;

        public BufferedServletInputStream(ByteArrayInputStream bais) {
            this.bais = bais;
        }

        @Override
        public int available() {
            return this.bais.available();
        }

        @Override
        public int read() {
            return this.bais.read();
        }

        @Override
        public int read(byte[] buf, int off, int len) {
            return this.bais.read(buf, off, len);
        }

        @Override
        public boolean isFinished() {
            return false;
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setReadListener(ReadListener readListener) {

        }
    }

    public class TeeServletOutputStream extends ServletOutputStream {

        private final TeeOutputStream targetStream;

        public TeeServletOutputStream(OutputStream one, OutputStream two) {
            targetStream = new TeeOutputStream(one, two);
        }

        @Override
        public void write(int arg0) throws IOException {
            this.targetStream.write(arg0);
        }

        public void flush() throws IOException {
            super.flush();
            this.targetStream.flush();
        }

        public void close() throws IOException {
            super.close();
            this.targetStream.close();
        }

        @Override
        public boolean isReady() {
            return false;
        }

        @Override
        public void setWriteListener(WriteListener writeListener) {

        }
    }

    public class BufferedResponseWrapper implements HttpServletResponse {

        HttpServletResponse original;
        TeeServletOutputStream tee;
        ByteArrayOutputStream bos;

        public BufferedResponseWrapper(HttpServletResponse response) {
            original = response;
        }

        public String getContent() {
            return bos.toString();
        }

        public PrintWriter getWriter() throws IOException {
            return original.getWriter();
        }

        public ServletOutputStream getOutputStream() throws IOException {
            if (tee == null) {
                bos = new ByteArrayOutputStream();
                tee = new TeeServletOutputStream(original.getOutputStream(),
                        bos);
            }
            return tee;

        }

        @Override
        public String getCharacterEncoding() {
            return original.getCharacterEncoding();
        }

        @Override
        public String getContentType() {
            return original.getContentType();
        }

        @Override
        public void setCharacterEncoding(String charset) {
            original.setCharacterEncoding(charset);
        }

        @Override
        public void setContentLength(int len) {
            original.setContentLength(len);
        }

        @Override
        public void setContentLengthLong(long l) {
            original.setContentLengthLong(l);
        }

        @Override
        public void setContentType(String type) {
            original.setContentType(type);
        }

        @Override
        public void setBufferSize(int size) {
            original.setBufferSize(size);
        }

        @Override
        public int getBufferSize() {
            return original.getBufferSize();
        }

        @Override
        public void flushBuffer() throws IOException {
            tee.flush();
        }

        @Override
        public void resetBuffer() {
            original.resetBuffer();
        }

        @Override
        public boolean isCommitted() {
            return original.isCommitted();
        }

        @Override
        public void reset() {
            original.reset();
        }

        @Override
        public void setLocale(Locale loc) {
            original.setLocale(loc);
        }

        @Override
        public Locale getLocale() {
            return original.getLocale();
        }

        @Override
        public void addCookie(Cookie cookie) {
            original.addCookie(cookie);
        }

        @Override
        public boolean containsHeader(String name) {
            return original.containsHeader(name);
        }

        @Override
        public String encodeURL(String url) {
            return original.encodeURL(url);
        }

        @Override
        public String encodeRedirectURL(String url) {
            return original.encodeRedirectURL(url);
        }

        @SuppressWarnings("deprecation")
        @Override
        public String encodeUrl(String url) {
            return original.encodeUrl(url);
        }

        @SuppressWarnings("deprecation")
        @Override
        public String encodeRedirectUrl(String url) {
            return original.encodeRedirectUrl(url);
        }

        @Override
        public void sendError(int sc, String msg) throws IOException {
            original.sendError(sc, msg);
        }

        @Override
        public void sendError(int sc) throws IOException {
            original.sendError(sc);
        }

        @Override
        public void sendRedirect(String location) throws IOException {
            original.sendRedirect(location);
        }

        @Override
        public void setDateHeader(String name, long date) {
            original.setDateHeader(name, date);
        }

        @Override
        public void addDateHeader(String name, long date) {
            original.addDateHeader(name, date);
        }

        @Override
        public void setHeader(String name, String value) {
            original.setHeader(name, value);
        }

        @Override
        public void addHeader(String name, String value) {
            original.addHeader(name, value);
        }

        @Override
        public void setIntHeader(String name, int value) {
            original.setIntHeader(name, value);
        }

        @Override
        public void addIntHeader(String name, int value) {
            original.addIntHeader(name, value);
        }

        @Override
        public void setStatus(int sc) {
            original.setStatus(sc);
        }

        @SuppressWarnings("deprecation")
        @Override
        public void setStatus(int sc, String sm) {
            original.setStatus(sc, sm);
        }

        @Override
        public String getHeader(String arg0) {
            return original.getHeader(arg0);
        }

        @Override
        public Collection<String> getHeaderNames() {
            return original.getHeaderNames();
        }

        @Override
        public Collection<String> getHeaders(String arg0) {
            return original.getHeaders(arg0);
        }

        @Override
        public int getStatus() {
            return original.getStatus();
        }

    }
}
