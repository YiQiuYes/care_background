package com.linping.care;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.junit.jupiter.api.Test;

import java.util.Calendar;
import java.util.HashMap;

public class AuthTests {
    /**
     * 生成token
     */
    @Test
    void contextLoads() {
        HashMap<String, Object> map = new HashMap<>();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, 20);
        String token = JWT.create()
                .withHeader(map)
                .withClaim("userId", 1)
                .withClaim("username", "linping")
                .withExpiresAt(calendar.getTime())
                .sign(Algorithm.HMAC256("linping"));
        System.out.println(token);
    }
}
