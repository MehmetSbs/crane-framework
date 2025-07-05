package com.crane.mail.model;

import java.io.InputStream;
import java.nio.file.Path;

/**
 * Represents an email attachment
 */
public class Attachment {
    private final String filename;
    private final String contentType;
    private final AttachmentSource source;
    private final long size;

    private Attachment(String filename, String contentType, AttachmentSource source, long size) {
        this.filename = filename;
        this.contentType = contentType;
        this.source = source;
        this.size = size;
    }

    public static Attachment fromBytes(String filename, String contentType, byte[] data) {
        return new Attachment(filename, contentType, new ByteArraySource(data), data.length);
    }

    public static Attachment fromInputStream(String filename, String contentType, InputStream inputStream) {
        return new Attachment(filename, contentType, new InputStreamSource(inputStream), -1);
    }

    public static Attachment fromInputStream(String filename, String contentType, InputStream inputStream, long size) {
        return new Attachment(filename, contentType, new InputStreamSource(inputStream), size);
    }

    public static Attachment fromFile(Path filePath) {
        return fromFile(filePath, null);
    }

    public static Attachment fromFile(Path filePath, String contentType) {
        String filename = filePath.getFileName().toString();
        if (contentType == null) {
            contentType = determineContentType(filename);
        }
        return new Attachment(filename, contentType, new FileSource(filePath), -1);
    }

    public static Attachment fromClasspath(String resourcePath) {
        return fromClasspath(resourcePath, null);
    }

    public static Attachment fromClasspath(String resourcePath, String contentType) {
        String filename = resourcePath.substring(resourcePath.lastIndexOf('/') + 1);
        if (contentType == null) {
            contentType = determineContentType(filename);
        }
        return new Attachment(filename, contentType, new ClasspathSource(resourcePath), -1);
    }

    private static String determineContentType(String filename) {
        String extension = filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
        return switch (extension) {
            case "pdf" -> "application/pdf";
            case "doc" -> "application/msword";
            case "docx" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            case "xls" -> "application/vnd.ms-excel";
            case "xlsx" -> "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            case "png" -> "image/png";
            case "jpg", "jpeg" -> "image/jpeg";
            case "gif" -> "image/gif";
            case "txt" -> "text/plain";
            case "csv" -> "text/csv";
            case "xml" -> "application/xml";
            case "json" -> "application/json";
            case "zip" -> "application/zip";
            default -> "application/octet-stream";
        };
    }

    public String getFilename() {
        return filename;
    }

    public String getContentType() {
        return contentType;
    }

    public AttachmentSource getSource() {
        return source;
    }

    public long getSize() {
        return size;
    }

    @Override
    public String toString() {
        return "Attachment{" +
                "filename='" + filename + '\'' +
                ", contentType='" + contentType + '\'' +
                ", size=" + size +
                '}';
    }

    // Source abstraction for different attachment types
    public interface AttachmentSource {
        InputStream getInputStream() throws Exception;
    }

    public static class ByteArraySource implements AttachmentSource {
        private final byte[] data;

        public ByteArraySource(byte[] data) {
            this.data = data;
        }

        @Override
        public InputStream getInputStream() {
            return new java.io.ByteArrayInputStream(data);
        }

        public byte[] getData() {
            return data;
        }
    }

    public static class InputStreamSource implements AttachmentSource {
        private final InputStream inputStream;

        public InputStreamSource(InputStream inputStream) {
            this.inputStream = inputStream;
        }

        @Override
        public InputStream getInputStream() {
            return inputStream;
        }
    }

    public static class FileSource implements AttachmentSource {
        private final Path filePath;

        public FileSource(Path filePath) {
            this.filePath = filePath;
        }

        @Override
        public InputStream getInputStream() throws Exception {
            return java.nio.file.Files.newInputStream(filePath);
        }

        public Path getFilePath() {
            return filePath;
        }
    }

    public static class ClasspathSource implements AttachmentSource {
        private final String resourcePath;

        public ClasspathSource(String resourcePath) {
            this.resourcePath = resourcePath;
        }

        @Override
        public InputStream getInputStream() throws Exception {
            InputStream stream = getClass().getClassLoader().getResourceAsStream(resourcePath);
            if (stream == null) {
                throw new IllegalArgumentException("Resource not found: " + resourcePath);
            }
            return stream;
        }

        public String getResourcePath() {
            return resourcePath;
        }
    }
}
