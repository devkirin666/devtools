package dev.kirin.toy.devtools.config.security;

import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.http.HttpServletRequest;

public class PortMatcher implements RequestMatcher {
    private final int port;
    private final boolean primary;

    PortMatcher(int port, boolean primary) {
        this.port = port;
        this.primary = primary;
    }

    public PortMatcher(int port) {
        this.port = port;
        this.primary = false;
    }

    @Override
    public boolean matches(HttpServletRequest request) {
        try {
            return port == request.getLocalPort();
        } catch (UnsupportedOperationException ignored) {
            return primary;
        }
    }
}
