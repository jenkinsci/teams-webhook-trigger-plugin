package org.jenkinsci.plugins.teamstrigger.whitelist;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.jenkinsci.plugins.teamstrigger.global.WhitelistAlgorithm;

public class HMACVerifier {

  public static void hmacVerify(
      final Map<String, List<String>> headers,
      final String postContent,
      final String hmacHeader,
      final String hmacSecret)
      throws WhitelistException {

    final String headerValue = getHeaderValue(hmacHeader, headers);
    final byte[] calculateHmacBytes = getCalculatedHmac(postContent, hmacSecret);
    final String calculateHmacAsHex = bytesToHex(calculateHmacBytes);
    final String calculateHmacAsString = Base64.getEncoder().encodeToString(calculateHmacBytes);

    if (!headerValue.equalsIgnoreCase(calculateHmacAsHex)
        && !headerValue.equalsIgnoreCase(calculateHmacAsString)) {
      throw new WhitelistException(
          "HMAC verification failed with \"" + hmacHeader + "\" as \"" + headerValue);
    }
  }

  private static byte[] getCalculatedHmac(final String postContent, final String hmacSecret) {
    try {
      final byte[] hash = Base64.getDecoder().decode(hmacSecret);
      final Mac mac = Mac.getInstance(WhitelistAlgorithm.HMAC_SHA256.getFullName());
      final SecretKeySpec keySpec =
          new SecretKeySpec(hash, WhitelistAlgorithm.HMAC_SHA256.getFullName());
      mac.init(keySpec);
      return mac.doFinal(postContent.getBytes(UTF_8));
    } catch (NoSuchAlgorithmException | InvalidKeyException e) {
      throw new RuntimeException(e);
    }
  }

  private static String bytesToHex(final byte[] bytes) {
    final char[] hexArray = "0123456789ABCDEF".toCharArray();
    final char[] hexChars = new char[bytes.length * 2];
    for (int j = 0; j < bytes.length; j++) {
      final int v = bytes[j] & 0xFF;
      hexChars[j * 2] = hexArray[v >>> 4];
      hexChars[j * 2 + 1] = hexArray[v & 0x0F];
    }
    return new String(hexChars);
  }

  private static String getHeaderValue(
      final String hmacHeader, final Map<String, List<String>> headers) throws WhitelistException {
    for (final Entry<String, List<String>> ck : headers.entrySet()) {
      final boolean sameHeader = ck.getKey().equalsIgnoreCase(hmacHeader);
      final boolean oneValue = ck.getValue().size() == 1;
      if (sameHeader && oneValue) {
        final String value = ck.getValue().get(0);
        for (final WhitelistAlgorithm algorithm : WhitelistAlgorithm.values()) {
          final String startStringHmac = "HMAC ";
          if (value.startsWith(startStringHmac)) {
            // To handle teams signature authorization: HMAC
            // w2g2swwmrsvRLZ5W68LfjaLrSR4fN0ErKGyfTPbLrBs=
            return value.substring(startStringHmac.length()).trim();
          }
        }
        return value;
      }
    }
    throw new WhitelistException(
        "Was unable to find header with name \"" + hmacHeader + "\" among " + headers);
  }
}
