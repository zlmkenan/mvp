package io.merculet.auth.service;

import cn.magicwindow.common.exception.HttpServiceException;
import cn.magicwindow.common.exception.MwException;
import cn.magicwindow.common.util.AsyncHttpUtils;
import cn.magicwindow.common.util.Preconditions;
import cn.magicwindow.score.common.bean.wechat.OAuthInfo;
import cn.magicwindow.score.common.entity.WechatPlatformInfo;
import cn.magicwindow.score.common.exception.WxGrantCodeUnavailableException;
import com.alibaba.fastjson.JSON;
import io.merculet.auth.bean.WechatAuthUserBean;
import org.lognet.springboot.grpc.GRpcService;
import io.merculet.auth.repository.WechatPlatformInfoRepository;
import io.merculet.proto.token.TokenChannel;
import io.merculet.proto.user.CreateUserResponse;
import io.merculet.proto.user.QueryUserResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

/**
 * @author Edmund.Wang
 * @package io.merculet.auth.service
 * @class WechatService
 * @email edmund.wang@magicwindow.cn
 * @date 2018/11/25 上午11:16
 * @description
 */
@GRpcService
@Service
@Slf4j
public class WechatService {

    @Autowired
    private WechatPlatformInfoRepository wechatPlatformInfoRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private TokenService tokenService;

    @Value("${wx.authorization_url}")
    private String authorizationUrl;

    @Value("${wx.default_authorization_callback_url}")
    private String defaultAuthorizationCallbackUrl;

    @Value("${wx.app_id}")
    private String wxAppId;

    @Value("${wx.app_secret}")
    private String wxAppSecret;

    @Autowired
    private RedisTemplate redisTemplate;

    private static final String WECHAT_AUTH_PREFIX = "WE_AUTH_";

    /**
     * 获取微信用户相信信息，非静默授权
     * @param code
     * @return
     * @throws UnsupportedEncodingException
     * @throws WxGrantCodeUnavailableException
     * @throws HttpServiceException
     */
    public OAuthInfo getOAuthInfoAllByCode(String code) throws UnsupportedEncodingException, WxGrantCodeUnavailableException, HttpServiceException {
        OAuthInfo snsapiBase = this.getOAuthInfoByCode(code);
        String url = "https://api.weixin.qq.com/sns/userinfo";
        Map<String,String> param = new HashMap<>(3);
        param.put("access_token", snsapiBase.getAccessToken());
        param.put("openid", snsapiBase.getOpenid());
        param.put("lang","zh_CN");
        String response = AsyncHttpUtils.syncGet(url,param);
        log.info("getOAuthInfoAllByCode code:{}, response:{}",code ,response);

        OAuthInfo oAuthInfo = JSON.parseObject(response,OAuthInfo.class);
        if(Preconditions.isBlank(oAuthInfo.getOpenid())){
            throw new WxGrantCodeUnavailableException(oAuthInfo.getErrMsg());
        }
        String nickName = new String(oAuthInfo.getNickname().getBytes("iso-8859-1"), "utf-8");
        oAuthInfo.setNickname(nickName);
        return oAuthInfo;
    }

    /**
     * 静默授权获取openid
     * @param code
     * @return
     * @throws HttpServiceException
     */
    private OAuthInfo getOAuthInfoByCode(String code) throws HttpServiceException, WxGrantCodeUnavailableException {
        String url = "https://api.weixin.qq.com/sns/oauth2/access_token";
        Map<String,String> param = new HashMap<>(4);
        param.put("appid",wxAppId);
        param.put("secret",wxAppSecret);
        param.put("code",code);
        param.put("grant_type","authorization_code");
        String response = AsyncHttpUtils.syncGet(url,param);
        log.info("getOAuthInfoByCode code:{}, response:{}",code ,response);
        OAuthInfo oAuthInfo = JSON.parseObject(response,OAuthInfo.class);
        if(Preconditions.isBlank(oAuthInfo.getOpenid())){
            throw new WxGrantCodeUnavailableException(oAuthInfo.getErrMsg());
        }
        return oAuthInfo;
    }

    /**
     * 构造各服务回调地址
     * @param oAuthInfo
     * @return
     * @throws MwException
     */
    public String serviceRedirectUrl(String wid, OAuthInfo oAuthInfo, String serviceFailUrl) throws Exception {
        WechatPlatformInfo wechatPlatformInfo = wechatPlatformInfoRepository.findByWxAppIdAndDeletedIsFalse(wid);
        QueryUserResponse queryUserResponse = userService.queryUserByAppIdAndExternalUserId(wechatPlatformInfo.getAppId(), oAuthInfo.getOpenid());
        Map<String, String> paramMap = new HashMap<>();
        if(Preconditions.isNotBlank(queryUserResponse)){
            paramMap.put("oid", queryUserResponse.getOpenPlatformId().getValue());
        } else {
            CreateUserResponse createUserResponse = userService.createUser(oAuthInfo, wechatPlatformInfo.getAppId());
            //返回open platform id
            paramMap.put("oid", createUserResponse.getOpenPlatformId().getValue());
        }
        paramMap.put("token", tokenService.signToken(paramMap.get("oid").toString(), TokenChannel.SIMPLET_WX_VALUE));
        String redirectUrl = addRedirectParams(serviceFailUrl, paramMap);
        log.info("redirect url: {}", redirectUrl);
        return redirectUrl;
    }

    public String addRedirectParams(String redirectH5Url, Map<String, String> paramMap){
        if(Preconditions.isNotBlank(paramMap)){
            List<String> params = new ArrayList<>();
            for(Map.Entry<String, String> entry : paramMap.entrySet()){
                params.add(entry.getKey() + "=" + entry.getValue());
            }
            if(Preconditions.isNotBlank(params)){
                return redirectH5Url + "?" + String.join("&", params);
            }
        }
        return redirectH5Url;
    }

    public String getAuthorizeUrl(HttpServletRequest request, String scope, Long wxAppId, String serviceUrl, String serviceFailUrl) throws UnsupportedEncodingException, MwException {
        WechatAuthUserBean bean = constructWechatAuthBean(request);
        String state = WECHAT_AUTH_PREFIX + UUID.randomUUID().toString().replaceAll("-","");
        redisTemplate.opsForValue().set(state, bean);

        String authorizeUrl = String.format(authorizationUrl,
                wxAppId, URLEncoder.encode(defaultAuthorizationCallbackUrl, "UTF-8"),
                scope, state, URLEncoder.encode(serviceUrl,"UTF-8"), URLEncoder.encode(serviceFailUrl,"UTF-8"));
        log.info("authorizeUrl : {}", authorizeUrl);
        return authorizeUrl;
    }

    private WechatAuthUserBean constructWechatAuthBean(HttpServletRequest request){
        WechatAuthUserBean bean = new WechatAuthUserBean();
        bean.setSfl(request.getParameter("sfl"));
        bean.setSl(request.getParameter("sl"));
        bean.setWid(request.getParameter("wid"));
        return bean;
    }

    private String getAllRequestParameters(HttpServletRequest request) throws UnsupportedEncodingException {
        Enumeration<String> allParameterNames = request.getParameterNames();
        List<String> params = new ArrayList<>();
        if(Preconditions.isNotBlank(allParameterNames)){
            while(allParameterNames.hasMoreElements()){
                String paraName = allParameterNames.nextElement();
                String paraValue = request.getParameter(paraName);
                if(paraName.equals("sl") || paraName.equals("sfl")){
                    paraValue = URLEncoder.encode(request.getParameter(paraName), "UTF-8");
                }
                params.add(paraName + "--" + paraValue);
            }
        }
        return String.join(",", params);
    }
}