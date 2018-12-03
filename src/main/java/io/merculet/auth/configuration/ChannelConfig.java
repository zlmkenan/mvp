package io.merculet.auth.configuration;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhou liming
 * @package io.merculet.wallet.config
 * @date 2018-11-12 14:24
 * @description
 */
@Configuration
public class ChannelConfig {

    @Value("${rpc.user}")
    private String userRpcHost;

    @Bean(name = "userChannel")
    public ManagedChannel userChannel(){
        return ManagedChannelBuilder.forTarget(userRpcHost).usePlaintext().build();
    }


}
