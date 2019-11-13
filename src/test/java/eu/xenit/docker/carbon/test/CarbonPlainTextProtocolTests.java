package eu.xenit.docker.carbon.test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.text.IsEmptyString.isEmptyString;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
class CarbonPlainTextProtocolTests {

    private static final String TEST_METRIC_KEY = "eu.xenit.amazing.test";

    private static final int PORT_LISTEN_PLAINTEXT = 2003;
    private static final String PATH_WHISPER_STORAGE = "/opt/graphite/storage/whisper/";

    @TempDir
    @SuppressWarnings("WeakerAccess")
    Path temporaryDirectory;

    @Container
    private GenericContainer carbonContainer = new GenericContainer<>(getDockerImage())
            .withExposedPorts(PORT_LISTEN_PLAINTEXT)
            .waitingFor(Wait.forListeningPort());

    private CarbonPlainTextClient carbonClient;

    private static String getDockerImage() {
        final String id = System.getenv("DOCKER_IMAGE");
        assertThat(id, not(isEmptyString()));
        return id;
    }

    @BeforeEach
    void beforeEach() {
        assertThat(carbonContainer.isRunning(), is(true));

        carbonClient = new CarbonPlainTextClient(carbonContainer.getContainerIpAddress(),
                carbonContainer.getMappedPort(PORT_LISTEN_PLAINTEXT));
    }

    @Test
    void uploadMetrics_plainTextProtocol() throws IOException, InterruptedException {
        carbonClient.sendMetric(TEST_METRIC_KEY, 15);

        // Give carbon some time to create the whisperdb file
        Thread.sleep(1000);

        // Validate that Carbon created the whisperdb file associated to our test metric
        Path whisperFile = temporaryDirectory.resolve("test.wsp");
        carbonContainer.copyFileFromContainer(PATH_WHISPER_STORAGE + "eu/xenit/amazing/test.wsp",
                whisperFile.toString());

        assertThat(Files.exists(whisperFile), is(true));
    }

    /**
     * I might have been a little bit inspired by: https://github.com/awin/simplegraphiteclient/blob/master/src/main/java/com/zanox/lib/simplegraphiteclient/SimpleGraphiteClient.java
     */
    private static class CarbonPlainTextClient {

        private final String graphiteHost;
        private final int graphitePort;

        CarbonPlainTextClient(String graphiteHost, int graphitePort) {
            this.graphiteHost = graphiteHost;
            this.graphitePort = graphitePort;
        }

        void sendMetrics(Map<String, Number> metrics, long timeStamp) throws IOException {
            try (Socket socket = createSocket();
                    OutputStream s = socket.getOutputStream();
                    PrintWriter out = new PrintWriter(s, true)) {
                for (Map.Entry<String, Number> metric : metrics.entrySet()) {
                    out.printf("%s %s %d%n", metric.getKey(), metric.getValue(), timeStamp);
                }
            }
        }

        void sendMetric(String key, Number value) throws IOException {
            sendMetric(key, value, getCurrentTimestamp());
        }

        @SuppressWarnings("serial")
        void sendMetric(final String key, final Number value, long timeStamp) throws IOException {
            sendMetrics(new HashMap<String, Number>() {{
                put(key, value);
            }}, timeStamp);
        }

        Socket createSocket() throws IOException {
            return new Socket(graphiteHost, graphitePort);
        }

        long getCurrentTimestamp() {
            return System.currentTimeMillis() / 1000;
        }
    }
}
