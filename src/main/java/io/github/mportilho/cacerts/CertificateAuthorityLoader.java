package io.github.mportilho.cacerts;

import io.github.mportilho.assertions.Asserts;
import io.github.mportilho.resources.ResourceLoader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

final class CertificateAuthorityLoader {

    private static final int FILE_SYSTEM_PRIORITY = 5;
    private static final int JAR_PRIORITY = 10;
    private static final int CLASSPATH_PRIORITY = 15;

    public enum CertificateLocation {
        FILE_SYSTEM, JAR_FILE, CLASSPATH
    }

    public static Map<String, CertificateData> loadFromPaths(String[] paths) {
        Asserts.assertNotEmpty(paths, "Paths must be provided");
        Map<String, CertificateData> certificateMap = new HashMap<>();
        for (String path : paths) {
            Map<String, CertificateData> loadedMap = CertificateAuthorityLoader.loadFromFileSystem(path);
            if (loadedMap.isEmpty()) {
                loadedMap = CertificateAuthorityLoader.loadFromApplicationJar(path);
            }
            if (loadedMap.isEmpty()) {
                loadedMap = CertificateAuthorityLoader.loadFromClasspath(path);
            }
            loadedMap.forEach((key, value) -> mergeByPriority(certificateMap, key, value));
        }
        return certificateMap;
    }

    public static Map<String, CertificateData> loadFrom(String path, CertificateLocation certificateLocation) {
        Objects.requireNonNull(certificateLocation, "Certification location type must be informed");
        Asserts.assertNotEmpty(path, "Path to certificate must be informed");
        switch (certificateLocation) {
            case FILE_SYSTEM:
            case JAR_FILE:
                return CertificateAuthorityLoader.loadFromFileSystem(path);
            case CLASSPATH:
                return CertificateAuthorityLoader.loadFromClasspath(path);
            default:
                throw new IllegalStateException("Unexpected value: " + certificateLocation);
        }
    }

    private static Map<String, CertificateData> loadFromFileSystem(String certificatePath) {
        File file = new File(certificatePath);
        if (!file.exists()) {
            throw new IllegalArgumentException("Path provided does not exist");
        }
        return loadFiles(file, FILE_SYSTEM_PRIORITY);
    }

    private static Map<String, CertificateData> loadFromApplicationJar(String path) {
        Optional<File> optionalFile = ResourceLoader.getCurrentJarFile();
        if (optionalFile.isEmpty()) {
            return Collections.emptyMap();
        }
        File file = optionalFile.get();
        try (JarFile jar = new JarFile(file)) {
            return jar.stream()
                    .filter(entry -> entry.getName().contains(path + "/") && isExtensionPermitted(entry.getName()))
                    .collect(Collectors.toMap(JarEntry::getName, e -> {
                        try {
                            byte[] bytes = jar.getInputStream(jar.getJarEntry(e.getName())).readAllBytes();
                            return new CertificateData(e.getName(), bytes, JAR_PRIORITY);
                        } catch (IOException ioException) {
                            throw new IllegalStateException("Invalid jar entry: " + e.getName(), ioException);
                        }
                    }));
        } catch (IOException e) {
            throw new IllegalStateException("Problem found while loading resource", e);
        }
    }

    private static Map<String, CertificateData> loadFromClasspath(String path) {
        return ResourceLoader.getClasspathResource(path).map(file -> loadFiles(file, CLASSPATH_PRIORITY))
                .orElse(Collections.emptyMap());
    }

    private static Map<String, CertificateData> loadFiles(File file, int priority) {
        Map<String, CertificateData> certMap = new HashMap<>();
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File currFile : (files != null ? files : new File[0])) {
                certMap.putAll(loadFiles(currFile, priority));
            }
        } else if (file.isFile()) {
            if (isExtensionPermitted(file.getName())) {
                try (FileInputStream fis = new FileInputStream(file)) {
                    certMap = Collections.singletonMap(file.getName(), new CertificateData(file.getName(), fis.readAllBytes(), priority));
                } catch (IOException e) {
                    throw new IllegalStateException("Problems found while loading resource", e);
                }
            }
        } else {
            throw new IllegalArgumentException("Unknown resource type to load: " + file.getAbsolutePath());
        }
        return certMap;
    }

    private static void mergeByPriority(Map<String, CertificateData> certsContentRepo, String key, CertificateData value) {
        certsContentRepo.merge(key, value, (old, current) -> {
            if (old.getLoadingPriority() <= current.getLoadingPriority()) {
                return old;
            }
            return current;
        });
    }

    private static boolean isExtensionPermitted(String extension) {
        return false;
    }


}
