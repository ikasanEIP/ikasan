package org.ikasan.filetransfer.component;

import org.ikasan.filetransfer.Payload;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

/**
 * File implementation of the Payload interface. Backed by a java Path.
 *
 * @author Ikasan Development Team
 */
public class FilePayload implements Payload {

    /** Serialise ID */
    private static final long serialVersionUID = 1L;

    /** id for payload **/
    private final String id;

    /** optional attributes **/
    private final Map<String, String> attributes;

    /** File to be delivered */
    private final Path file;

    public FilePayload(String id, Map<String, String> attributes, Path file) {
        this.id = id;
        this.attributes = attributes;
        this.file = file;
    }

    /* (non-Javadoc)
     * @see org.ikasan.common.Payload#getId()
     */
    @Override
    public String getId() {
        return id;
    }

    /* (non-Javadoc)
     * @see org.ikasan.common.Payload#getAttribute()
     */
    @Override
    public String getAttribute(String attributeName) {
        return attributes.get(attributeName);
    }

    /* (non-Javadoc)
     * @see org.ikasan.common.Payload#getAttributeNames()
     */
    @Override
    public List<String> getAttributeNames() {
        return attributes.keySet()
            .stream()
            .sorted()
            .collect(Collectors.toList());
    }

    /* (non-Javadoc)
     * @see org.ikasan.common.Payload#getAttributeMap()
     */
    @Override
    public Map<String, String> getAttributeMap() {
        return Collections.unmodifiableMap(attributes);
    }

    /**
     * Reads the content of the file into a byte array
     *
     * @return content of the FilePayload
     * @throws UncheckedIOException when IOException encountered
     */
    @Override
    public byte[] getContent() {
        try {
            return Files.readAllBytes(file);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /* (non-Javadoc)
     * @see org.ikasan.common.Payload#getInputStream()
     */
    @Override
    public InputStream getInputStream() throws IOException {
        return Files.newInputStream(file);
    }

    /**
     * Return the OS-specified size of payload file in bytes
     *
     * @return Approximate file size in bytes
     * @throws UncheckedIOException when IOException encountered
     */
    @Override
    public long getSize() {
        try {
            return Files.size(file);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /* (non-Javadoc)
     * @see org.ikasan.common.Payload#setAttribute()
     */
    @Override
    public void setAttribute(String attributeName, String attributeValue) {
        this.attributes.put(attributeName, attributeValue);
    }

    /**
     *
     * Get the FilePayload file
     *
     * @return Path
     */
    public Path getFile() {
        return file;
    }

    @Override
    public String toString() {
        return "FilePayload{" +
            "id='" + id + '\'' +
            ", attributes=" + attributes +
            ", file=" + file +
            ", fileSize=" + getSize() + " bytes " +
            '}';
    }

    /* (non-Javadoc)
     * @see org.ikasan.common.Payload#clone()
     */
    @Override
    @Deprecated
    public Payload clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException("FilePayload does not support cloning.");
    }
}
