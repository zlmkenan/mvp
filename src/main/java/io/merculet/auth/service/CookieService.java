package io.merculet.auth.service;

import cn.magicwindow.score.common.util.Preconditions;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * @author Edmund.Wang
 * @package io.merculet.auth.service
 * @class CookieService
 * @email edmund.wang@magicwindow.cn
 * @date 2018/11/25 下午8:30
 * @description
 */
@Service
public class CookieService {

    /**
     * 通过cookie name，获取指定cookie
     *
     * @param request
     * @param cookieName
     * @return
     */
    public Cookie getCookieByKey(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();
        //
        if (Preconditions.isNotBlank(cookies)) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(cookieName)) {
                    return cookie;
                }
            }
        }
        return null;
    }
}