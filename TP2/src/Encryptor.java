import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class Encryptor {

    public byte[] encryptMessage (byte[] message) throws InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance(Constants.TRANSFORMATION);
        SecretKey secretKey = new SecretKeySpec(Constants.Key, Constants.ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedMessage = cipher.doFinal(message);
        return encryptedMessage;
    }

    public byte[] decryptMessage(byte[] encryptedMessage) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance(Constants.TRANSFORMATION);
        SecretKey secretKey = new SecretKeySpec(Constants.Key, Constants.ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] clearMessage = cipher.doFinal(encryptedMessage);
        return clearMessage;
    }

}
