package org.example;

import com.google.gson.JsonSyntaxException;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class JsonParserTest {
    private final JsonParser parser = new JsonParser();

    @Test
    void parseLaunchWithValidJson() throws Exception{
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("launch_single.json")) {
            String json = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            var launch = parser.parseLaunch(json);

            assertEquals("5e9e4501f509094ba4566f84", launch.getId());
            assertEquals("CRS-20", launch.getName());
            assertEquals(91, launch.getFlightNumber());
            assertEquals("2020-03-07T04:50:31.000Z", launch.getDateUtc());
            assertFalse(launch.getSuccess());
            assertFalse(launch.isUpcoming());
            assertEquals("Failure during first stage separation.", launch.getDetails());
        }
    }

    @Test
    void parseLaunchesWithValidJson() throws Exception {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("launches_list.json")) {
            String json = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            var launches = parser.parseLaunches(json);

            assertNotNull(launches);
            assertEquals(2, launches.size());

            Launch first = launches.get(0);
            assertEquals(1, first.getFlightNumber());
            assertEquals("FalconSat", first.getName());
            assertFalse(first.getSuccess());

            Launch second = launches.get(1);
            assertEquals(2, second.getFlightNumber());
            assertEquals("DemoSat", second.getName());
            assertTrue(second.getSuccess());
        }
    }

    @Test
    void parseLaunchWithNullDetails() throws Exception {
        String json = "{ \"id\": \"1\", \"name\": \"Test\", \"flight_number\": 1, \"date_utc\": \"2020-01-01T00:00:00.000Z\", \"success\": true, \"upcoming\": false }";
        Launch launch = parser.parseLaunch(json);
        assertNull(launch.getDetails());
    }

    @Test
    void parseLaunchThrowExceptionWhenInvalidJson() {
        assertThrows(JsonSyntaxException.class, () -> parser.parseLaunch("{ invalid json }"));
    }

    @Test
    void parseLaunchDeserializeFailuresAndCores() throws Exception {
        String json = "{ \"failures\": [{\"time\": 100, \"altitude\": 200, \"reason\": \"engine failure\"}], \"cores\": [{\"core\": \"core123\", \"flight\": 1, \"landing_attempt\": true, \"landing_success\": false}] }";
        Launch launch = parser.parseLaunch(json);
        assertNotNull(launch.getFailures());
        assertEquals(1, launch.getFailures().size());
        assertEquals("engine failure", launch.getFailures().get(0).getReason());
        assertNotNull(launch.getCores());
        assertEquals(1, launch.getCores().size());
        assertEquals("core123", launch.getCores().get(0).getCore());
    }
}
