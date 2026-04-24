package ru.github.musiccrossing.auth.service;

import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.github.musiccrossing.common.error.exception.AuthException;
import ru.github.musiccrossing.auth.exception.InvalidTelegramDataSignatureException;
import ru.github.musiccrossing.auth.exception.MissingTelegramDataException;
import ru.github.musiccrossing.auth.exception.TelegramAuthExpiredException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;

import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TokenVerifierService {

    private static final String GOOGLE_CERTS_URL = "https://www.googleapis.com/oauth2/v3/certs";

    private final String clientId;
    private final String telegramBotToken;

    public TokenVerifierService(
            @Value("${google.client-id}") String clientId,
            @Value("${telegram.bot-token}") String telegramBotToken) {
        this.clientId = clientId;
        this.telegramBotToken = telegramBotToken;
    }

    public JWTClaimsSet verifyGoogle(String idToken) throws Exception {
        SignedJWT signedJWT = SignedJWT.parse(idToken);

        String kid = signedJWT.getHeader().getKeyID();

        JWKSet jwkSet = JWKSet.load(new URL(GOOGLE_CERTS_URL));

        JWK jwk = jwkSet.getKeyByKeyId(kid);
        if (jwk == null) {
            throw new AuthException("Публичный ключ Google не был найден", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        JWSVerifier verifier = new RSASSAVerifier((RSAKey) jwk);
        if (!signedJWT.verify(verifier)) {
            throw new AuthException("Невалидный Google token", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        List<String> audience = signedJWT.getJWTClaimsSet().getAudience();
        if (!audience.contains(clientId)) {
            throw new AuthException("Ключ предназначен не для этого приложения", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return signedJWT.getJWTClaimsSet();
    }

    public Map<String, String> verifyTelegram(Map<String, String> data) {
        String receivedHash = data.get("hash");
        if (receivedHash == null || receivedHash.isEmpty()) {
            throw new MissingTelegramDataException("hash");
        }

        Map<String, String> filtered = new HashMap<>(data);
        filtered.remove("hash");

        List<String> keyValuePairs = new ArrayList<>();
        filtered.keySet().stream().sorted().forEach(
                key -> keyValuePairs.add(key + "=" + filtered.get(key))
        );

        String dataString = String.join("\n", keyValuePairs);

        try {
            byte[] secretKey = MessageDigest.getInstance("SHA-256")
                    .digest(telegramBotToken.getBytes(StandardCharsets.UTF_8));

            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secretKey, "HmacSHA256"));
            byte[] result = mac.doFinal(dataString.getBytes(StandardCharsets.UTF_8));
            String computedHash = bytesToHex(result);

            if (!computedHash.equals(receivedHash)) {
                throw new InvalidTelegramDataSignatureException();
            }

            String authDateStr = filtered.get("auth_date");
            if (authDateStr == null) {
                throw new MissingTelegramDataException("auth_date");
            }

            long authDate = Long.parseLong(authDateStr);
            long currentTime = System.currentTimeMillis() / 1000;
            long fiveMinutes = 5 * 60;

            if (currentTime - authDate > fiveMinutes) {
                throw new TelegramAuthExpiredException();
            }

            return filtered;

        } catch (NoSuchAlgorithmException e) {
            throw new AuthException("Алгоритм SHA-256 не найден", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (InvalidKeyException e) {
            throw new AuthException("Некорректный ключ для HMAC", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
