package com.anihub.gateway.fitter;

import com.anihub.gateway.utils.AppJwtUtil;
import com.anihub.gateway.utils.CollUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
//TODO: Implement this class
/*public class AuthGlobalFitter implements GlobalFilter, Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
  //获取request
        ServerHttpRequest request = exchange.getRequest();
        //判断是否需要拦截
        if (isExclude(request.getPath().toString())){
            return chain.filter(exchange);
        }
        //获取token
        String token = null;
        List<String> headers = request.getHeaders().get("authorization");
        if (!CollUtils.isEmpty(headers)){
            token = headers.get(0);
        }
        Long userId = null;
        try {
            userId = AppJwtUtil.parseToken(token);
        }catch (UnauthorizedException e){
            // 如果无效，拦截
            ServerHttpResponse response = exchange.getResponse();
            response.setRawStatusCode(401);
            return response.setComplete();
        }
        String userInfo = userId.toString();
        ServerWebExchange ex = exchange.mutate().request(builder -> builder.header("user-info", userInfo)).build();
        // 6.放行
        return chain.filter(ex);

    }

    private boolean isExclude(String string) {

    }

    @Override
    public int getOrder() {
        return 0;
    }
}*/
