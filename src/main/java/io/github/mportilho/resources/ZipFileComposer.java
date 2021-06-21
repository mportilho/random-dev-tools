package io.github.mportilho.resources;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipFileComposer {

    private Map<String, ByteWrapper> files;

    private ZipFileComposer() {
        this.files = new HashMap<>();
    }

    public static ZipFileComposer newBuilder() {
        return new ZipFileComposer();
    }

    public ZipFileComposer addEntry(String fileName, byte[] fileBytes) {
        files.put(fileName, new ByteWrapper(fileBytes));
        return this;
    }

    public byte[] build() throws IOException {
        try (ByteArrayOutputStream finalFileOutputStream = new ByteArrayOutputStream()) {
            try (ZipOutputStream zos = new ZipOutputStream(finalFileOutputStream)) {
                for (Entry<String, ByteWrapper> fileEntry : files.entrySet()) {
                    addEntry(fileEntry.getKey(), fileEntry.getValue().getFileBytes(), zos);
                }
            }
            return finalFileOutputStream.toByteArray();
        }
    }

    private void addEntry(String fileName, byte[] data, ZipOutputStream zos) throws IOException {
        ByteArrayInputStream bis = new ByteArrayInputStream(data);
        ZipEntry zipEntry = new ZipEntry(fileName);
        zos.putNextEntry(zipEntry);
        byte[] bytes = new byte[1024];
        int length;
        while ((length = bis.read(bytes)) >= 0) {
            zos.write(bytes, 0, length);
        }
        zos.closeEntry();
    }

    private static class ByteWrapper {
        private final byte[] fileBytes;

        ByteWrapper(byte[] fileBytes) {
            this.fileBytes = fileBytes;
        }

        public byte[] getFileBytes() {
            return fileBytes;
        }
    }

}
