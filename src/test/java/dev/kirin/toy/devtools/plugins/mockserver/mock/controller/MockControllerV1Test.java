package dev.kirin.toy.devtools.plugins.mockserver.mock.controller;

import dev.kirin.common.junit.extension.depedent.DependentTestExtension;
import dev.kirin.toy.devtools.plugins.mockserver.config.MockServerConfigProperties;
import lombok.AccessLevel;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.Mergeable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.ConfigurableSmartRequestBuilder;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import support.ApiControllerTestSupport;

import javax.servlet.ServletContext;

@Slf4j
@AutoConfigureMockMvc
@SpringBootTest(properties = {"spring.profiles.active=test"})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith({DependentTestExtension.class})
@DisplayName("Mock Controller v1 Test")
class MockControllerV1Test implements ApiControllerTestSupport {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private MockServerConfigProperties mockServerConfigProperties;

    private final String BASE_URL_URI = "/api/v1/unknown";

    @Test
    @DisplayName("Access none security test")
    void testSecurity() throws Exception {
        mockMvc.perform(
                    PortRequestBuilders.portBuilder(
                            MockMvcRequestBuilders.post(BASE_URL_URI)
                                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                                    .with(SecurityMockMvcRequestPostProcessors.csrf().asHeader())
                    )
                    .port(mockServerConfigProperties.getPort())
                )
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Setter(AccessLevel.PRIVATE)
    static class PortRequestBuilders implements RequestBuilder, ConfigurableSmartRequestBuilder<MockHttpServletRequestBuilder>, Mergeable {
        private MockHttpServletRequestBuilder builder;
        private int port;

        PortRequestBuilders(MockHttpServletRequestBuilder builder) {
            this.builder = builder;
        }

        static PortRequestBuilders portBuilder(MockHttpServletRequestBuilder builder) {
            return new PortRequestBuilders(builder);
        }

        PortRequestBuilders port(int port) {
            this.setPort(port);
            return this;
        }

        @Override
        public MockHttpServletRequest buildRequest(ServletContext servletContext) {
            MockHttpServletRequest request = builder.buildRequest(servletContext);
            request.setLocalPort(port);
            return request;
        }

        @Override
        public boolean isMergeEnabled() {
            return builder.isMergeEnabled();
        }

        @Override
        public Object merge(Object parent) {
            return builder.merge(parent);
        }

        @Override
        public MockHttpServletRequestBuilder with(RequestPostProcessor requestPostProcessor) {
            return builder.with(requestPostProcessor);
        }

        @Override
        public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
            return builder.postProcessRequest(request);
        }
    }
}
