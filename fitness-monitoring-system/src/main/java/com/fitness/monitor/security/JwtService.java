package com.fitness.monitor.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class JwtService {

    private static final String HEADER = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";
    private static final Pattern SUBJECT = Pattern.compile("\"sub\":\"(\\d+)\"");
    private static final Pattern EXPIRY = Pattern.compile("\"exp\":(\\d+)");

    private final byte[] secret;
    private final long lifetimeSeconds;

    public JwtService(@Value("${app.jwt.secret}") String secret,
                      @Value("${app.jwt.expiry-hours:24}") long expiryHours) {
        if (secret == null || secret.length() < 32) {
            throw new IllegalStateException("app.jwt.secret must be at least 32 characters long");
        }
        this.secret = secret.getBytes(StandardCharsets.UTF_8);
        this.lifetimeSeconds = expiryHours * 3600;
    }

    public String issue(Long userId) {
        long expiresAt = Instant.now().getEpochSecond() + lifetimeSeconds;
        String payload = "{\"sub\":\"" + userId + "\",\"exp\":" + expiresAt + "}";
        String unsigned = encode(HEADER.getBytes(StandardCharsets.UTF_8)) + "."
                + encode(payload.getBytes(StandardCharsets.UTF_8));
        return unsigned + "." + encode(sign(unsigned));
    }

    public Long verify(String token) {
        if (token == null) {
            return null;
        }
        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            return null;
        }
        byte[] expected = sign(parts[0] + "." + parts[1]);
        byte[] given;
        try {
            given = Base64.getUrlDecoder().decode(parts[2]);
        } catch (IllegalArgumentException e) {
            return null;
        }
        if (!MessageDigest.isEqual(expected, given)) {
            return null;
        }
        String payload;
        try {
            payload = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            return null;
        }
        Matcher exp = EXPIRY.matcher(payload);
        Matcher sub = SUBJECT.matcher(payload);
        if (!exp.find() || !sub.find()) {
            return null;
        }
        if (Long.parseLong(exp.group(1)) <= Instant.now().getEpochSecond()) {
            return null;
        }
        return Long.parseLong(sub.group(1));
    }

    private byte[] sign(String data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret, "HmacSHA256"));
            return mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new IllegalStateException("Could not sign token", e);
        }
    }

    private static String encode(byte[] bytes) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
