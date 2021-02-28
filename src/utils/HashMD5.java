package utils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashMD5 {

  private final String elector;

  public HashMD5(String electorName) {
    this.elector = generateMD5(electorName);
  }

  public String getElector() {
    return elector;
  }

  private String generateMD5(String elector) {
    try {
      MessageDigest md = MessageDigest.getInstance("MD5");
      md.update(elector.getBytes());
      byte[] md5 = md.digest();
      BigInteger numMd5 = new BigInteger(1, md5);
      return String.format("%022x", numMd5);
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }
  }
}
