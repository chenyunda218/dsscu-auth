package mo.gov.dsscu.auth.utils;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.Random;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class PasswordUtils {
  private static final int ITERATIONS = 10000;
  private static final int KEY_LENGTH = 256;
  private static final int SALT_LENGTH = 16;

  public static byte[] generateSalt() {
    SecureRandom random = new SecureRandom();
    byte[] salt = new byte[SALT_LENGTH];
    random.nextBytes(salt);
    return salt;
  }

  public static String hashPassword(String password, byte[] salt)
      throws NoSuchAlgorithmException, InvalidKeySpecException {

    // 創建PBEKeySpec
    PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);

    // 使用PBKDF2WithHmacSHA256算法
    SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
    byte[] hash = factory.generateSecret(spec).getEncoded();

    // 組合鹽和哈希值
    byte[] hashWithSalt = new byte[salt.length + hash.length];
    System.arraycopy(salt, 0, hashWithSalt, 0, salt.length);
    System.arraycopy(hash, 0, hashWithSalt, salt.length, hash.length);

    return Base64.getEncoder().encodeToString(hashWithSalt);
  }

  public static boolean verifyPassword(String password, String storedHash, byte[] salt)
      throws NoSuchAlgorithmException, InvalidKeySpecException {
    return hashPassword(password, salt).equals(storedHash);
  }

  public static String generateRandomString(int length) {
    String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    StringBuilder sb = new StringBuilder(length);
    Random random = new Random();

    for (int i = 0; i < length; i++) {
      int index = random.nextInt(characters.length());
      sb.append(characters.charAt(index));
    }
    return sb.toString();
  }

}
