package com.acon.server.global.logging;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

public class CachedBodyHttpServletResponse extends HttpServletResponseWrapper {

    private final ByteArrayOutputStream cachedBody = new ByteArrayOutputStream();
    private ServletOutputStream outputStream;
    private PrintWriter writer;

    public CachedBodyHttpServletResponse(final HttpServletResponse response) {
        super(response);
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        if (writer != null) {
            throw new IllegalStateException("이미 getWriter()가 호출되었습니다.");
        }

        if (outputStream == null) {
            outputStream = new CachedServletOutputStream(cachedBody);
        }

        return outputStream;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        if (outputStream != null) {
            throw new IllegalStateException("이미 getOutputStream()이 호출되었습니다.");
        }

        if (writer == null) {
            writer = new PrintWriter(cachedBody, true, StandardCharsets.UTF_8);
        }

        return writer;
    }

    public String getBody() {
        try {
            if (writer != null) {
                writer.flush();
            } else if (outputStream != null) {
                outputStream.flush();
            }
        } catch (IOException ignored) {
        }

        return cachedBody.toString(StandardCharsets.UTF_8);
    }

    public void copyBodyToResponse() throws IOException {
        getResponse().getOutputStream().write(cachedBody.toByteArray());
        getResponse().getOutputStream().flush();
    }

    private static class CachedServletOutputStream extends ServletOutputStream {

        private final ByteArrayOutputStream cachedBody;

        public CachedServletOutputStream(final ByteArrayOutputStream cachedBody) {
            this.cachedBody = cachedBody;
        }

        @Override
        public void write(int b) throws IOException {
            cachedBody.write(b);
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setWriteListener(final WriteListener writeListener) {
        }
    }
}
