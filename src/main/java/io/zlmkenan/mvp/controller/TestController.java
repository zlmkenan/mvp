package io.zlmkenan.mvp.controller;

import com.google.protobuf.Int64Value;
import com.google.protobuf.StringValue;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import io.merculet.proto.user.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Iterator;

/**
 * @author zhou liming
 * @package io.zlmkenan.mvp
 * @date 2018/12/3 16:54
 * @description
 */

@Slf4j
@RestController
@RequestMapping(value = "/v1/test")
public class TestController {

    @RequestMapping(value = "/createUser", method = RequestMethod.GET)
    public void createUser(HttpServletRequest request) {
        try{
            ManagedChannel channel = ManagedChannelBuilder.forTarget("localhost:19202").usePlaintext().build();
            UserBaseServiceGrpc.UserBaseServiceBlockingStub stub = UserBaseServiceGrpc.newBlockingStub(channel);
            CreateUserResponse response = stub.createUser(CreateUserRequest.newBuilder().setChannel(3).setExternalUserAvatar("http://thirdwxA/132").setOpenId(StringValue.of("test"))
                    .setExternalUserNickName("chencun").build());
        }catch (StatusRuntimeException e){
            log.error(e.getStatus().getCode().name() + "&&&&&" + e.getMessage());
        }


    }

    @RequestMapping(value = "/getWalletUserDevice", method = RequestMethod.GET)
    public void getWalletUserDevice(HttpServletRequest request) {
        try{
            ManagedChannel channel = ManagedChannelBuilder.forTarget("localhost:19202").usePlaintext().build();

            UserBaseServiceGrpc.UserBaseServiceBlockingStub stub = UserBaseServiceGrpc.newBlockingStub(channel);

            Iterator<GetWalletUserDeviceResponse> responses = stub.getWalletUserDevice(GetWalletUserDeviceRequest.newBuilder().setWalletUserId(Int64Value.of(660)).build());
            while(responses.hasNext()){
                GetWalletUserDeviceResponse response = responses.next();
                log.info(response.getDeviceId() + "========" + response.getWalletUserId());
            }
        }catch (StatusRuntimeException e){
            log.error(e.getStatus().getCode().name() + "&&&&&" + e.getMessage());
        }


    }
}