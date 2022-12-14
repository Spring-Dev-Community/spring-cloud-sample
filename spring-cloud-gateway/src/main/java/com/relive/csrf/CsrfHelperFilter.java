package com.relive.csrf;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.security.web.server.csrf.CsrfToken;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * @author: ReLive
 * @date: 2022/3/11 12:46 下午
 */
@Slf4j
@Component
public class CsrfHelperFilter implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String key = CsrfToken.class.getName();
        Mono<CsrfToken> csrfToken = null != exchange.getAttribute(key) ? exchange.getAttribute(key) : Mono.empty();
        return csrfToken.doOnSuccess(token -> {
            ResponseCookie cookie = ResponseCookie.from("XSRF-TOKEN", token.getToken()).maxAge(Duration.ofHours(1))
                    .httpOnly(false).path("/").build();
            log.debug("Cookie: {}", cookie);
            exchange.getResponse().getCookies().add("XSRF-TOKEN", cookie);
        }).then(chain.filter(exchange));
    }
}
