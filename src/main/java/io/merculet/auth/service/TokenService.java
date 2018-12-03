package io.merculet.auth.service;

import cn.magicwindow.opentracing.annotation.Opentracing;
import cn.magicwindow.score.common.entity.User;
import cn.magicwindow.score.common.util.Preconditions;
import com.google.protobuf.Int64Value;
import com.google.protobuf.StringValue;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.merculet.auth.repository.UserRepository;
import io.merculet.auth.util.TokenUtil;
import io.merculet.proto.token.*;
import lombok.extern.slf4j.Slf4j;
import org.lognet.springboot.grpc.GRpcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @author zhou liming
 * @package io.merculet.auth.service
 * @date 2018/11/16 11:15
 * @description
 */

@Slf4j
@Service
@GRpcService
public class TokenService extends TokenServiceGrpc.TokenServiceImplBase{

    @Value("${mvp.sign.key}")
    private String signKey;

    @Autowired
    private UserRepository userRepository;

    @Override
    public void signToken(SignTokenRequest request, StreamObserver<SignTokenResponse> responseObserver) {
        if(Preconditions.isBlank(request.getOpenPlatformId())){
            responseObserver.onError(Status.INVALID_ARGUMENT.asRuntimeException());
        }

        if(Preconditions.isBlank(request.getChannel())){
            responseObserver.onError(Status.INVALID_ARGUMENT.asRuntimeException());
        }

        String signToken = signToken(request.getOpenPlatformId(),request.getChannelValue());

        responseObserver.onNext(SignTokenResponse.newBuilder().setToken(StringValue.of(signToken)).build());
        responseObserver.onCompleted();
    }

    public String signToken(String openPlatformId, Integer channelValue){
        String signToken = TokenUtil.signToken(openPlatformId,channelValue,signKey);
        return signToken;
    }

    @Opentracing
    @Override
    public void verifyToken(VerifyTokenRequest request, StreamObserver<VerifyTokenResponse> responseObserver) {
        if(Preconditions.isBlank(request.getToken())){
            responseObserver.onError(Status.INVALID_ARGUMENT.asRuntimeException());
        }

        try{

            Claims claims = TokenUtil.verifyAndExtractPayload(request.getToken(),signKey);
            VerifyTokenResponse response = formatTokenInfo(claims);
            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (ExpiredJwtException e){

            //token失效
            responseObserver.onError(Status.UNAUTHENTICATED.withDescription(e.getMessage()).asRuntimeException());
        } catch (RuntimeException e){

            //token无效
            responseObserver.onError(Status.PERMISSION_DENIED.withDescription(e.getMessage()).asRuntimeException());
        } catch (Exception e){

            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }

    }

    public String verifyToken(String token, String key){
        String keyValue = TokenUtil.verifyAndExtractKey(token,signKey,key);
        return keyValue;
    }

    private VerifyTokenResponse formatTokenInfo(Claims claims) throws Exception{
        VerifyTokenResponse.Builder builder = VerifyTokenResponse.newBuilder();

        String openPlatformId = claims.getSubject();
        int channel = (int) claims.get(TokenUtil.SIGN_TYPE);

        User user = userRepository.findOneByOpenPlatformIdAndDeletedIsFalse(openPlatformId);

        if(Objects.equals(TokenChannel.APP_USER_VALUE,channel)){
            builder.setAppKey(StringValue.of(user.getApp().getAppKey()));
            builder.setAccountKey(StringValue.of(user.getApp().getAccount().getAccountKey()));
            builder.setExternalUserId(StringValue.of(user.getExternalUserId()));
        }

        if(Objects.equals(TokenChannel.SIMPLET_APP_VALUE,channel)){
            if(Preconditions.isNotBlank(user.getWalletUserId())){
                builder.setWalletUserId(Int64Value.of(user.getWalletUserId()));
            }

            if(Preconditions.isNotBlank(user.getPhoneNumber())){
                builder.setPhoneNumber(StringValue.of(user.getPhoneNumber()));
            }

            builder.setUserId(Int64Value.of(user.getId()));
        }

        if(Objects.equals(TokenChannel.SIMPLET_WX_VALUE,channel)){
            if(Preconditions.isNotBlank(user.getWalletUserId())){
                builder.setWalletUserId(Int64Value.of(user.getWalletUserId()));
            }
            builder.setExternalUserId(StringValue.of(user.getExternalUserId()));
        }

        return builder.build();
    }
}
