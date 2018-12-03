package io.zlmkenan.mvp.interceptor;

import com.google.common.base.Throwables;
import io.grpc.*;
import org.lognet.springboot.grpc.GRpcGlobalInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zhou liming
 * @package io.zlmkenan.mvp
 * @date 2018/12/3 16:54
 * @description
 */
@GRpcGlobalInterceptor
public class TokenInterceptor implements ServerInterceptor{

    final static Logger logger = LoggerFactory.getLogger(TokenInterceptor.class);

    /**
     * Intercept {@link ServerCall} dispatch by the {@code next} {@link ServerCallHandler}. General
     * semantics of {@link ServerCallHandler#startCall} apply and the returned
     * {@link ServerCall.Listener} must not be {@code null}.
     * <p>
     * <p>If the implementation throws an exception, {@code call} will be closed with an error.
     * Implementations must not throw an exception if they started processing that may use {@code
     * call} on another thread.
     *
     * @param call    object to receive response messages
     * @param headers which can contain extra call metadata from {@link ClientCall#start},
     *                e.g. authentication credentials.
     * @param next    next processor in the interceptor chain
     * @return listener for processing incoming messages for {@code call}, never {@code null}.
     */
    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> call, Metadata headers, ServerCallHandler<ReqT, RespT> next) {
        ServerCall.Listener<ReqT> delegate = next.startCall(call, headers);

        return new ForwardingServerCallListener.SimpleForwardingServerCallListener<ReqT>(delegate) {
            @Override
            public void onHalfClose() {
                try {
                    super.onHalfClose();
                } catch (Exception e) {
                    String stackTrace  = Throwables.getStackTraceAsString(e);
                    logger.error("grpc server error,{}",stackTrace);
                    call.close(Status.INTERNAL.withCause(e).withDescription(e.getMessage()), new Metadata());
                }
            }
        };
    }
}
