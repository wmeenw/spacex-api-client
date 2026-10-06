package org.example;

import org.junit.jupiter.api.Test;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import static org.junit.jupiter.api.Assertions.*;

public class SpaceXHttpClientTest {
    private final SpaceXHttpClient client = new SpaceXHttpClient();

    @Test
    void readStreamReturnStringWithNewlineWhenInputStreamHasData() throws IOException {
        String original = "test line";
        InputStream is = new ByteArrayInputStream(original.getBytes(StandardCharsets.UTF_8));
        String result = client.readStream(is);
        assertEquals(original + "\n", result);
    }

    @Test
    void readStreamShouldReturnEmptyStringWhenInputStreamIsNull() throws IOException {
        String result = client.readStream(null);
        assertEquals("", result);
    }
}