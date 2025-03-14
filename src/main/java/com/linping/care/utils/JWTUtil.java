package com.linping.care.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


public class JWTUtil {

    // 私钥
    private static final String SING = "linping";

    /**
     * 生成token
     *
     * @param map 传入payload
     * @return token
     */
    public static String generalToken(Map<String, String> map) {
        Calendar instance = Calendar.getInstance();
        // 默认7天过期
        instance.add(Calendar.DATE, 7);
        //创建jwt builder
        JWTCreator.Builder builder = JWT.create();
        // payload
        map.forEach(builder::withClaim);
        //指定令牌过期时间
        return builder.withExpiresAt(instance.getTime()).sign(Algorithm.HMAC256(SING));
    }

    public static String generaRefreshToken(Map<String, String> map) {
        Calendar instance = Calendar.getInstance();
        // 默认30天过期
        instance.add(Calendar.DATE, 30);
        //创建jwt builder
        JWTCreator.Builder builder = JWT.create();
        // payload
        map.forEach(builder::withClaim);
        //指定令牌过期时间
        return builder.withExpiresAt(instance.getTime()).sign(Algorithm.HMAC256(SING));
    }

    /**
     * 验证token
     *
     * @param token 传入token
     */
    public static void verifyToken(String token) throws Exception {
        DecodedJWT tokenInfo = getTokenInfo(token);
        String type = tokenInfo.getClaim("type").asString();
        if ("refreshToken".equals(type)) {
            throw new Exception("请使用正确的token");
        }
        JWT.require(Algorithm.HMAC256(SING)).build().verify(token);
    }

    /**
     * 获取token信息方法
     *
     * @param token 传入token
     * @return DecodedJWT
     */
    public static DecodedJWT getTokenInfo(String token) {
        return JWT.require(Algorithm.HMAC256(SING)).build().verify(token);
    }

    /**
     * 根据旧token生成新token
     *
     * @param token 传入token
     * @return 新token
     */
    public static String reNewToken(String token) {
        DecodedJWT tokenInfo = getTokenInfo(token);
        Map<String, String> payload = new HashMap<>();
        payload.put("id", tokenInfo.getClaim("id").asString());
        payload.put("type", "token");
        return generalToken(payload);
    }

    /**
     * 根据旧refreshToken生成新refreshToken
     *
     * @param refreshToken 传入refreshToken
     * @return 新refreshToken
     */
    public static String reNewRefreshToken(String refreshToken) {
        DecodedJWT tokenInfo = getTokenInfo(refreshToken);
        Map<String, String> payload = new HashMap<>();
        payload.put("id", tokenInfo.getClaim("id").asString());
        payload.put("type", "refreshToken");
        return generaRefreshToken(payload);
    }

    /**
     * 获取id
     * @param token 传入token
     * @return id
     */
    public static String getId(String token) {
        DecodedJWT tokenInfo = JWTUtil.getTokenInfo(token);
        return tokenInfo.getClaim("id").asString();
    }
}

