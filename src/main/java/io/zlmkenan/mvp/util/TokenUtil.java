package io.zlmkenan.mvp.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.merculet.proto.token.TokenChannel;

import java.util.Date;

/**
 * @author zhou liming
 * @package io.zlmkenan.mvp
 * @date 2018/12/3 16:54
 * @description
 */
public class TokenUtil {

    public static final long EXPIRE_IN_MILLIS_BY_APP_USER = 2L * 24 * 60 * 60 * 1000;
    public static final long EXPIRE_IN_MILLIS_BY_SIMPLET_USER = 30L * 24 * 60 * 60 * 1000;

    public static String SIGN_TYPE = "channel";

    public static String signToken(String openplatFormId,  int channel, String signKey) {
        long expiration;
        long issueAt = System.currentTimeMillis();

        if(channel == TokenChannel.APP_USER_VALUE){
            expiration = issueAt + EXPIRE_IN_MILLIS_BY_APP_USER;
        }else{
            expiration = issueAt + EXPIRE_IN_MILLIS_BY_SIMPLET_USER;
        }

        return Jwts.builder()
                .setSubject(openplatFormId)
                .setIssuedAt(new Date(issueAt))
                .setExpiration(new Date(expiration))
                .claim(SIGN_TYPE, channel)
                .signWith(SignatureAlgorithm.HS256, signKey)
                .compact();
    }

    public static Claims verifyAndExtractPayload(String token, String signKey) {
        return Jwts.parser()
                .setSigningKey(signKey)
                .parseClaimsJws(token)
                .getBody();
    }

    public static String verifyAndExtractKey(String token, String signWith, String key) {
        return (String)Jwts.parser()
                .setSigningKey(signWith)
                .parseClaimsJws(token)
                .getBody()
                .get(key);
    }

    public static void main(String[] args){
        String token = signToken("test",1,"test");
        System.out.println(token);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Claims claims = verifyAndExtractPayload(token,"test");
        System.out.println(claims.getSubject());
        System.out.println(claims.get(TokenUtil.SIGN_TYPE));
    }

}
