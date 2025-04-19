package org.example;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Utils {
    /**
     * Reads the specified resource and returns a ByteBuffer containing its data.
     *
     * @param resource   the resource path (e.g., "textures/texture_atlas.png")
     * @param bufferSize an initial buffer size (e.g., 8 * 1024)
     * @return a ByteBuffer with the contents of the resource
     * @throws IOException if the resource cannot be read
     */
    public static ByteBuffer ioResourceToByteBuffer(String resource, int bufferSize) throws IOException {
        URL url = Utils.class.getClassLoader().getResource(resource);
        if (url == null) {
            throw new FileNotFoundException("Resource not found: " + resource);
        }
        Path path;
        try {
            path = Paths.get(url.toURI());
        } catch (URISyntaxException e) {
            throw new IOException(e);
        }
        ByteBuffer buffer = ByteBuffer.allocateDirect((int) Files.size(path) + 1);
        try (SeekableByteChannel channel = Files.newByteChannel(path)) {
            while (channel.read(buffer) != -1) {
                // Continue until end of file.
            }
        }
        buffer.flip();
        return buffer;
    }
}
