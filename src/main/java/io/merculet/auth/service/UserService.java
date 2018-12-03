package io.merculet.auth.service;

import cn.magicwindow.common.exception.MwException;
import cn.magicwindow.score.common.bean.wechat.OAuthInfo;
import cn.magicwindow.score.common.enumeration.UserChannelEnum;
import cn.magicwindow.score.common.util.Preconditions;
import com.alibaba.fastjson.JSON;
import com.google.protobuf.Int64Value;
import com.google.protobuf.StringValue;
import io.grpc.ManagedChannel;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.merculet.auth.configuration.ChannelConfig;
import io.merculet.proto.user.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Edmund.Wang
 * @package io.merculet.auth.service
 * @class UserService
 * @email edmund.wang@magicwindow.cn
 * @date 2018/11/25 下午1:51
 * @description
 */
@Service
@Slf4j
public class UserService {

    @Autowired
    private ChannelConfig channelConfig;

    public CreateUserResponse createUser(OAuthInfo oAuthInfo, Long appId) throws MwException {
        log.info("create user: oauth info = {}, app id = {}", JSON.toJSONString(oAuthInfo), appId);
        ManagedChannel channel = channelConfig.userChannel();
        CreateUserRequest.Builder builder = CreateUserRequest.newBuilder();
        if(Preconditions.isNotBlank(oAuthInfo.getUnionid())){
            builder.setUnionId(oAuthInfo.getUnionid());
        }

        builder.setAppId(Int64Value.of(appId));
        builder.setChannel(UserChannelEnum.WECHAT.getChannel());
        builder.setExternalUserAvatar(oAuthInfo.getHeadimgurl());
        builder.setExternalUserNickName(oAuthInfo.getNickname());
        builder.setOpenId(StringValue.of(oAuthInfo.getOpenid()));

        UserBaseServiceGrpc.UserBaseServiceBlockingStub stub = UserBaseServiceGrpc.newBlockingStub(channel);
        try {
            CreateUserResponse createUserResponse = stub.createUser(builder.build());
            log.info("create user success.");
            return createUserResponse;
        } catch (StatusRuntimeException e){
            log.error("create user failed: {}", e.getMessage());
            throw new MwException(String.format("score user createUser error, response : %s", e.getMessage()));
        }
    }

    public QueryUserResponse queryUserByOpenPlatformId(String openPlatformId) throws MwException {
        UserQueryServiceGrpc.UserQueryServiceBlockingStub stub = UserQueryServiceGrpc.newBlockingStub(channelConfig.userChannel());
        QueryUserRequestByOpenPlatformId.Builder builder = QueryUserRequestByOpenPlatformId.newBuilder();
        builder.setOpenPlatformId(StringValue.of(openPlatformId));
        try{
            QueryUserResponse queryUserResponse = stub.getUserByOpenPlatformId(builder.build());
            return queryUserResponse;
        } catch (StatusRuntimeException e){
            if(e.getStatus().equals(Status.NOT_FOUND)){
                return null;
            } else {
                log.error("get user failed by app id and external user id: {}", e.getMessage());
                throw new MwException(String.format("score user createUser error, response : %s", e.getMessage()));
            }
        }
    }

    public QueryUserResponse queryUserByAppIdAndExternalUserId(Long appId, String externalUserId) throws MwException {
        UserQueryServiceGrpc.UserQueryServiceBlockingStub stub = UserQueryServiceGrpc.newBlockingStub(channelConfig.userChannel());
        QueryUserRequestByAppIdAndExternalUserId.Builder builder = QueryUserRequestByAppIdAndExternalUserId.newBuilder();
        builder.setAppId(Int64Value.of(appId));
        builder.setExternalUserId(StringValue.of(externalUserId));
        try{
            QueryUserResponse queryUserResponse = stub.getUserByAppIdAndExternalUserId(builder.build());
            return queryUserResponse;
        } catch (StatusRuntimeException e){
            if(e.getStatus().equals(Status.NOT_FOUND)){
                log.warn("cannot find the external user id: {}, app id: {}", externalUserId, appId);
                return null;
            } else {
                log.error("get user failed by app id and external user id: {}", e.getMessage());
                throw new MwException(String.format("score user createUser error, response : %s", e.getMessage()));
            }
        }
    }
}