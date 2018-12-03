package io.merculet.auth.controller;

import cn.magicwindow.score.common.bean.ResponseContent;
import cn.magicwindow.score.common.bean.wechat.OAuthInfo;
import cn.magicwindow.score.common.constants.BusinessConstant;
import cn.magicwindow.score.common.entity.WechatPlatformInfo;
import cn.magicwindow.score.common.util.Preconditions;
import com.alibaba.fastjson.JSON;
import io.jsonwebtoken.ExpiredJwtException;
import io.merculet.auth.bean.WechatAuthUserBean;
import io.merculet.auth.repository.WechatPlatformInfoRepository;
import io.merculet.auth.service.CookieService;
import io.merculet.auth.service.TokenService;
import io.merculet.auth.service.UserService;
import io.merculet.auth.service.WechatService;
import io.merculet.proto.user.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Edmund.Wang
 * @package io.merculet.auth.controller
 * @class WechatController
 * @email edmund.wang@magicwindow.cn
 * @date 2018/11/25 上午11:41
 * @description
 */
@Slf4j
@RestController
@RequestMapping(value = "/api/v1/auth")
public class WechatController {

    @Autowired
    private WechatService wechatService;

    @Value("${wx.redirect_domain}")
    private String redirectDomain;

    @Value("${wx.default_fail_url}")
    private String defaultFailUrl;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private CookieService cookieService;

    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private WechatPlatformInfoRepository wechatPlatformInfoRepository;

    private static final Integer WECHAT_AUTH_URL_CODE = 5001;
    private static final Integer AUTH_FAIL_CODE = 5002;

    @RequestMapping(value = "/wechat/authorize", method = RequestMethod.GET)
    public ModelAndView getAuthorizationCode(HttpServletRequest request, HttpServletResponse response,
                                                @RequestParam(name = "scope",defaultValue = "snsapi_userinfo") String scope,
                                                @RequestParam(name = "sl") String serviceUrl,
                                                @RequestParam(name = "wid") String wid,
                                                @RequestParam(name = "sfl", required = false) String serviceFailUrl){
        if(Preconditions.isBlank(serviceFailUrl)){
            serviceFailUrl = defaultFailUrl;
        }

        WechatPlatformInfo wechatPlatformInfo = wechatPlatformInfoRepository.findByWxAppIdAndDeletedIsFalse(wid);
        Long wxAppId = wechatPlatformInfo.getAppId();

        try {
            //读取cookie 如果有mvp-token的cookie，并且在有效期则不进入微信授权
            Cookie cookie = cookieService.getCookieByKey(request, "mw-token");
            Map<String, String> paramMap = new HashMap<>();
            paramMap.put("wid", wid);
            try{
                if(Preconditions.isNotBlank(cookie)){
                    String openPlatformId = tokenService.verifyToken(cookie.getValue(), "open_platform_id");
                    QueryUserResponse queryUserResponse = userService.queryUserByOpenPlatformId(openPlatformId);

                    //如果是非本系统用户，则需要重新跳转授权
                    if(!wechatPlatformInfo.getAppId().toString().equals(String.valueOf(queryUserResponse.getAppId().getValue()))){
                        return new ModelAndView(new RedirectView(wechatService.getAuthorizeUrl(request, scope, wxAppId, serviceUrl, serviceFailUrl)));
                    }

                    if(Preconditions.isNotBlank(queryUserResponse)){
                        cookie.setDomain(redirectDomain);
                        cookie.setPath("/");
                        response.addCookie(cookie);
                        return new ModelAndView(new RedirectView(serviceUrl));
                    }
                }
                String authUrl = wechatService.getAuthorizeUrl(request, scope, wxAppId, serviceUrl, serviceFailUrl);
                return new ModelAndView(new RedirectView(authUrl));
            } catch(ExpiredJwtException e){
                return new ModelAndView(new RedirectView(wechatService.getAuthorizeUrl(request, scope, wxAppId, serviceUrl, serviceFailUrl)));
            }
        } catch (Exception e) {
            log.error("getAuthorizationCode error ", e);
            return new ModelAndView(serviceFailUrl);
        }

    }

    @RequestMapping(value = "/wechat/authorizeCallback", method = RequestMethod.GET)
    public ModelAndView wechatAuthorizeCallback(HttpServletRequest request, HttpServletResponse response, String code) {
        String state = request.getParameter("state");
        String serviceFailUrl = null;
        log.info("callback code : {}, state : {}", code, state);
        try {
            Object object = redisTemplate.opsForValue().get(state);
            log.info("state object: " + object);
            WechatAuthUserBean wechatAuthUserBean = JSON.parseObject(JSON.toJSONString(object), WechatAuthUserBean.class);
            serviceFailUrl = wechatAuthUserBean.getSfl();
            OAuthInfo oAuthInfo = wechatService.getOAuthInfoAllByCode(code);
            String serviceRedirectUrl = wechatService.serviceRedirectUrl(wechatAuthUserBean.getWid(), oAuthInfo, wechatAuthUserBean.getSl());
            return new ModelAndView(new RedirectView(serviceRedirectUrl));
        } catch (Exception e) {
            log.error("wechatAuthorizationCallback error" , e);
            if(Preconditions.isBlank(serviceFailUrl)){
                serviceFailUrl = defaultFailUrl;
            }
            return new ModelAndView(new RedirectView(serviceFailUrl));
        }
    }
}