package dev.kirin.toy.devtools.plugins.mockserver.mock;

import dev.kirin.toy.devtools.plugins.mockserver.annotation.MockController;
import dev.kirin.toy.devtools.plugins.mockserver.config.MockServerConfigProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.data.util.StreamUtils;
import org.springframework.web.servlet.mvc.condition.RequestCondition;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@RequiredArgsConstructor
public class PortBasedRequestMappingHandlerMapping extends RequestMappingHandlerMapping {
    private final MockServerConfigProperties properties;

    @Override
    protected RequestCondition<?> getCustomMethodCondition(Method method) {
        if(AnnotatedElementUtils.hasAnnotation(method.getDeclaringClass(), MockController.class)) {
            return new PortRequestCondition(properties.getPort());
        }
        return super.getCustomMethodCondition(method);
    }


    @Slf4j
    public static class PortRequestCondition implements RequestCondition<PortRequestCondition> {
        private final Set<Integer> ports;

        PortRequestCondition(Integer... ports) {
            this.ports = Arrays.stream(ports).collect(Collectors.toSet());
        }
        PortRequestCondition(Set<Integer> ports) {
            this.ports = ports;
        }


        @Override
        public PortRequestCondition combine(PortRequestCondition other) {
            return new PortRequestCondition(Stream.concat(ports.stream(), other.ports.stream()).collect(StreamUtils.toUnmodifiableSet()));
        }

        @Override
        public PortRequestCondition getMatchingCondition(HttpServletRequest request) {
            return this.ports.contains(request.getLocalPort()) ? this : null;
        }

        @Override
        public int compareTo(PortRequestCondition other, HttpServletRequest request) {
            return 0;
        }
    }
}
