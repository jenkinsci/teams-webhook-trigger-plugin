package org.jenkinsci.plugins.teamstrigger.whitelist;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.jenkinsci.plugins.teamstrigger.whitelist.HMACVerifier.hmacVerify;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;

public class HMACVerifierTest {

  @Test
  public void testThatHmacCanBeVerifiedAndValid() throws Exception {
    final String postContent = "hello";
    final String hmacSecret = "YT9tnapmcJ3aRMcfBS8Iz6ya226G1U8aoUwbjuWIsMs=";
    final String hmacHeader = "HMAC";
    final String headerValue = "HMAC IwDGeYv/eWO0QZEVehEnd2NrAcYJVFnDg2zKKiHn8ew=";
    final Map<String, List<String>> headers = this.getHeaders(hmacHeader, headerValue);

    final boolean actual =
        this.testTeamsHMACVerification(headers, postContent, hmacHeader, hmacSecret);
    assertThat(actual).isTrue();
  }

  private Map<String, List<String>> getHeaders(final String hmacHeader, final String value) {
    final Map<String, List<String>> headers = new HashMap<>();
    headers.put(hmacHeader, Arrays.asList(value));
    return headers;
  }

  private String getPostContent() throws IOException, URISyntaxException {
    final String postContent =
        new String(
            Files.readAllBytes(
                Paths.get(
                    this.getClass()
                        .getResource("/hmac/hmac-bitbucket-server-payload.json")
                        .toURI())),
            UTF_8);
    return postContent;
  }

  private boolean testTeamsHMACVerification(
      final Map<String, List<String>> headers,
      final String postContent,
      final String hmacHeader,
      final String hmacSecret)
      throws WhitelistException {
    try {
      hmacVerify(headers, postContent, hmacHeader, hmacSecret);
      return true;
    } catch (final WhitelistException e) {
      return false;
    }
  }
}
