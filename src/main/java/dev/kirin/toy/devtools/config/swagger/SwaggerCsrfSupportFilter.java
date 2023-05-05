package dev.kirin.toy.devtools.config.swagger;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.Constants;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor(access = AccessLevel.MODULE)
public class SwaggerCsrfSupportFilter extends OncePerRequestFilter {
    private final String PATTERN = "/**" + Constants.SWAGGER_UI_PREFIX + "/**" + Constants.INDEX_PAGE;
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    private final String csrfCookieName;
    private final CsrfTokenRepository csrfTokenRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.debug("(doFilterInternal) support URI = {}", request.getRequestURI());
        Cookie cookie = new Cookie(this.csrfCookieName, csrfTokenRepository.loadToken(request).getToken());
        response.addCookie(cookie);
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return !antPathMatcher.match(PATTERN, request.getRequestURI());
    }
}
