package io.github.mportilho.resources;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Optional;

public class ResourceLoader {

    public static Optional<InputStream> fromClasspath(String resourceName) {
        return getClasspathResource(resourceName).filter(File::isFile).map(file -> {
            try {
                return new FileInputStream(file);
            } catch (FileNotFoundException e) {
                return null;
            }
        });
    }

    public static Optional<InputStream> fromJarFileLocation(String resourceName) {
        Optional<File> jarOpt = getCurrentJarFile();
        if (jarOpt.isPresent()) {
            File externalResourcePath = new File(jarOpt.get().getParentFile().getAbsolutePath() + File.separator + resourceName);
            try {
                return Optional.of(new FileInputStream(externalResourcePath));
            } catch (FileNotFoundException e) {
                return Optional.empty();
            }
        }
        return Optional.empty();
    }

    public static Optional<File> getClasspathResource(String resourceName) {
        URL resourceURL = getClassLoader().getResource(resourceName);
        if (resourceURL == null) {
            return Optional.empty();
        }
        try {
            return Optional.of(new File(new URI(resourceURL.toString().replace(" ", "%20"))));
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Problem occurred while loading resource", e);
        }
    }

    public static Optional<File> getCurrentJarFile() {
        String jarPath = getJarPath(ResourceLoader.class.getProtectionDomain().getCodeSource().getLocation().getPath());
        File jarFile;
        try {
            jarFile = new File(new URL(jarPath).toURI());
        } catch (MalformedURLException | URISyntaxException e) {
            throw new IllegalStateException("Could not find current application .jar file", e);
        }
        if (jarFile.exists() && jarFile.isFile()) {
            return Optional.of(jarFile);
        }
        return Optional.empty();
    }

    private static String getJarPath(String path) {
        if (path.contains(".jar!")) {
            path = path.substring(0, path.indexOf(".jar!")) + ".jar";
        } else if (path.contains(".jar!/")) {
            path = path.substring(0, path.indexOf(".jar!/")) + ".jar";
        } else if (path.contains(".jar/")) {
            path = path.substring(0, path.indexOf(".jar/")) + ".jar";
        } else if (path.lastIndexOf("/") == path.length() - 1) {
            path = path.substring(0, path.lastIndexOf("/")) + ".jar";
        }
        return path.startsWith("file:") ? path : ("file:" + path);
    }

    private static ClassLoader getClassLoader() {
        ClassLoader cl = null;
        try {
            cl = Thread.currentThread().getContextClassLoader();
        } catch (Throwable ex) {
            // Cannot access thread context ClassLoader - falling back...
        }
        if (cl == null) {
            // No thread context class loader -> use class loader of this class.
            cl = ResourceLoader.class.getClassLoader();
            if (cl == null) {
                // getClassLoader() returning null indicates the bootstrap ClassLoader
                try {
                    cl = ClassLoader.getSystemClassLoader();
                } catch (Throwable ex) {
                    // Cannot access system ClassLoader - oh well, maybe the caller can live with null...
                }
            }
        }
        return cl;
    }

}
