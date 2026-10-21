package com.fitness.monitor.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private static final String SECRET = "a-test-secret-that-is-long-enough-for-hmac";

    @Test
    void issuedTokenVerifiesToTheSameUser() {
        JwtService jwt = new JwtService(SECRET, 1);
        assertEquals(42L, jwt.verify(jwt.issue(42L)));
    }

    @Test
    void tamperedTokenIsRejected() {
        JwtService jwt = new JwtService(SECRET, 1);
        String[] parts = jwt.issue(1L).split("\\.");
        String forged = parts[0] + "." + java.util.Base64.getUrlEncoder().withoutPadding()
                .encodeToString("{\"sub\":\"2\",\"exp\":9999999999}".getBytes()) + "." + parts[2];
        assertNull(jwt.verify(forged));
    }

    @Test
    void tokenFromAnotherSecretIsRejected() {
        String token = new JwtService(SECRET, 1).issue(7L);
        assertNull(new JwtService("another-secret-that-is-also-long-enough-ok", 1).verify(token));
    }

    @Test
    void expiredTokenIsRejected() {
        JwtService jwt = new JwtService(SECRET, 0);
        assertNull(jwt.verify(jwt.issue(5L)));
    }

    @Test
    void garbageIsRejected() {
        JwtService jwt = new JwtService(SECRET, 1);
        assertNull(jwt.verify(null));
        assertNull(jwt.verify("not-a-token"));
        assertNull(jwt.verify("a.b.c"));
    }

    @Test
    void shortSecretIsNotAccepted() {
        assertThrows(IllegalStateException.class, () -> new JwtService("short", 1));
    }
}
