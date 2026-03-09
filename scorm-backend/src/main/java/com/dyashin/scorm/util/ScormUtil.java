package com.dyashin.scorm.util;

import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.Comparator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public final class ScormUtil {

    private ScormUtil() {}

    /**
     * Validate ZIP file signature
     */
    public static void validateZipSignature(InputStream inputStream)
            throws IOException {

        byte[] header = new byte[4];

        if (inputStream.read(header) != 4) {
            throw new RuntimeException("Unable to read file header");
        }

        if (header[0] != 'P' || header[1] != 'K') {
            throw new RuntimeException("Invalid ZIP file signature");
        }
    }

    /**
     * Secure unzip with Zip-Slip protection
     */
    public static void unzipSecure(InputStream inputStream,
                                   Path targetDir) throws Exception {

        try (ZipInputStream zis = new ZipInputStream(inputStream)) {//This is sub class of InputStream to work with zip files

            ZipEntry entry;//Each files need to iterate

            while ((entry = zis.getNextEntry()) != null) {

                Path newFilePath =
                        targetDir.resolve(entry.getName()).normalize();//normalize will resolve the path ../../

                // Zip Slip Protection
                if (!newFilePath.startsWith(targetDir)) {//checking the targetdirectory is base
                    throw new RuntimeException("Invalid ZIP entry: " + entry.getName());
                }

                if (entry.isDirectory()) {

                    Files.createDirectories(newFilePath);

                } else {

                    Files.createDirectories(newFilePath.getParent());

                    Files.copy(zis, newFilePath,
                            StandardCopyOption.REPLACE_EXISTING);
                }

                zis.closeEntry();
            }
        }
    }

    /**
     * Secure XML parsing (prevents XXE attacks) :  It is the rules for parsing
     */
    public static Document parseManifestSecure(Path manifestPath)
            throws Exception {

        DocumentBuilderFactory factory =
                DocumentBuilderFactory.newInstance();

        factory.setFeature(
                "http://apache.org/xml/features/disallow-doctype-decl", true);

        factory.setFeature(
                "http://xml.org/sax/features/external-general-entities", false);//entities

        factory.setFeature(
                "http://xml.org/sax/features/external-parameter-entities", false);

        factory.setXIncludeAware(false);//Do not process XInclude tags to avoid the loading other files

        factory.setExpandEntityReferences(false);//to overcome from billion laugh attack &a 'replacing the value' 

        DocumentBuilder builder = factory.newDocumentBuilder();

        return builder.parse(manifestPath.toFile());
    }

    /**
     * Delete directory recursively
     */
    public static void deleteDirectory(Path path) throws IOException {

        if (path == null || !Files.exists(path)) {
            return;
        }

        Files.walk(path)
                .sorted(Comparator.reverseOrder())
                .forEach(p -> {

                    try {
                        Files.delete(p);

                    } catch (IOException ignored) {
                    }
                });
    }
}