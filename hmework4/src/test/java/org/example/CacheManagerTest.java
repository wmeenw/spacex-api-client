package org.example;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import static org.junit.jupiter.api.Assertions.*;

public class CacheManagerTest {
    private CacheManager cacheManager;
    private final String testDir = "cache_test";

    @BeforeEach
    void setUp() {
        deleteDirectory(new File(testDir));
        cacheManager = new CacheManager(testDir);
    }

    @AfterEach
    void tearDown() {
        deleteDirectory(new File(testDir));
    }

    private void deleteDirectory(File dir) {
        if (dir.exists()) {
            try {
                Files.walk(dir.toPath())
                        .sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(File::delete);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    void saveAndLoad() {
        String key = "test_key";
        String data = "test_data";
        cacheManager.save(key, data);
        String loaded = cacheManager.load(key);
        assertEquals(data, loaded);
    }

    @Test
    void loadWithKeyNotFoundReturnNull() {
        assertNull(cacheManager.load("non_existent_key"));
    }

    @Test
    void clearDeleteAllCacheFiles() {
        cacheManager.save("key1", "data1");
        cacheManager.save("key2", "data2");
        cacheManager.clear();
        assertNull(cacheManager.load("key1"));
        assertNull(cacheManager.load("key2"));
    }
}