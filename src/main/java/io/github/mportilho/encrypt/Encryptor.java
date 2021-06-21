package io.github.mportilho.encrypt;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Base64;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

public class Encryptor {

    private final SecretKey secretKey;

    public Encryptor(char[] password) {
        try {
            byte[] saltBytes = generateSaltBytesFromSeed(password);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
            PBEKeySpec spec = new PBEKeySpec(password, saltBytes, 262144, 256);
            SecretKey partialSecretKey = factory.generateSecret(spec);

            Arrays.fill(saltBytes, (byte) 0); // overwrite the content of array with zeros
            this.secretKey = new SecretKeySpec(partialSecretKey.getEncoded(), "AES");
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new IllegalStateException("Could not initialize encryptor", e);
        }
    }

    private byte[] generateSaltBytesFromSeed(char[] seed) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-512");
        digest.update(new String(seed).getBytes(StandardCharsets.ISO_8859_1));
        byte[] saltBytes = new byte[64];

        ByteBuffer finalDigest = ByteBuffer.allocate(64);
        byte[] initialDigest = digest.digest();
        finalDigest.put(initialDigest, 0, Math.min(64, initialDigest.length));
        while (finalDigest.hasRemaining()) {
            byte[] partialDigest = digest.digest();
            finalDigest.put(digest.digest(), 0, Math.min(partialDigest.length, finalDigest.remaining()));
        }
        System.arraycopy(finalDigest.array(), 0, saltBytes, 0, 64);
        return saltBytes;
    }

    public String encrypt(String message) {
        return encrypt(message, null);
    }

    public String encrypt(String message, String additionalAuth) {
        try {
            SecureRandom secureRandom = new SecureRandom();
            byte[] iv = new byte[12]; // NEVER REUSE THIS IV WITH SAME KEY
            secureRandom.nextBytes(iv);

            final Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding", "SunJCE");
            GCMParameterSpec parameterSpec = new GCMParameterSpec(128, iv); // 128 bit auth tag length
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec);

            byte[] cipherText;
            if (additionalAuth != null && !additionalAuth.isBlank()) {
                cipher.updateAAD(generateSaltBytesFromSeed(additionalAuth.toCharArray()));
                cipherText = cipher.doFinal(cipher.update(message.getBytes()));
            } else {
                cipherText = cipher.doFinal(message.getBytes());
            }

            ByteBuffer byteBuffer = ByteBuffer.allocate(4 + iv.length + cipherText.length + 8);
            byteBuffer.putInt(iv.length);
            byteBuffer.put(iv);
            byteBuffer.put(cipherText);
            byteBuffer.putLong(calculateChecksum(byteBuffer.array()));

            Arrays.fill(iv, (byte) 0); // overwrite the content of array with zeros
            Arrays.fill(cipherText, (byte) 0); // overwrite the content of array with zeros
            return Base64.getEncoder().encodeToString(byteBuffer.array());
        } catch (Exception e) {
            throw new IllegalStateException("Could not encrypt", e);
        }
    }

    public String decrypt(String encryptedMessage) {
        return decrypt(encryptedMessage, null);
    }

    public String decrypt(String encryptedMessage, String additionalAuth) {
        try {
            ByteBuffer byteBuffer = ByteBuffer.wrap(Base64.getDecoder().decode(encryptedMessage));
            int ivLength = byteBuffer.getInt();
            if (ivLength < 12 || ivLength >= 16) { // check input parameter
                throw new IllegalArgumentException("invalid iv length");
            }
            byte[] iv = new byte[ivLength];
            byteBuffer.get(iv);
            byte[] cipherText = new byte[byteBuffer.remaining() - 8];
            byteBuffer.get(cipherText);

            ByteBuffer messageCheckBuffer = ByteBuffer.allocate(4 + iv.length + cipherText.length + 8);
            messageCheckBuffer.putInt(ivLength).put(iv).put(cipherText);

            if (calculateChecksum(messageCheckBuffer.array()) != byteBuffer.getLong()) {
                throw new IllegalArgumentException("invalid iv length");
            }

            final Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, new GCMParameterSpec(128, iv));

            byte[] plainText;
            if (additionalAuth != null && !additionalAuth.isBlank()) {
                cipher.updateAAD(generateSaltBytesFromSeed(additionalAuth.toCharArray()));
                plainText = cipher.doFinal(cipher.update(cipherText));
            } else {
                plainText = cipher.doFinal(cipherText);
            }

            try {
                return new String(plainText);
            } finally {
                Arrays.fill(plainText, (byte) 0); // overwrite the content of array with zeros
                Arrays.fill(iv, (byte) 0); // overwrite the content of array with zeros
                Arrays.fill(cipherText, (byte) 0); // overwrite the content of array with zeros
            }
        } catch (Exception e) {
            throw new IllegalStateException("Could not decrypt", e);
        }
    }

    static long calculateChecksum(byte[] data) {
        Checksum checksum = new CRC32();
        checksum.update(data, 0, data.length);
        return checksum.getValue();
    }

}
