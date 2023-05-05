package dev.kirin.toy.devtools.plugins.mockserver.api;

import dev.kirin.toy.devtools.plugins.mockserver.api.vo.MockErrorVoV1;
import dev.kirin.toy.devtools.plugins.mockserver.domain.mockerror.code.ErrorType;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.StringUtils;
import support.ApiControllerTestSupport;

@Slf4j
@AutoConfigureMockMvc
@SpringBootTest(properties = {"spring.profiles.active=test"})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@WithMockUser(username = "test", roles = {"ADMIN"})
@DisplayName("Mock Error CRUD API Test")
class MockErrorApiControllerTest implements ApiControllerTestSupport {
    private static final String BASE_URI = "/api/v1/mock-errors";

    private static MockErrorVoV1 requestVo;
    @Autowired
    private MockMvc mockMvc;

    @BeforeAll
    static void beforeAll() {
        requestVo = new MockErrorVoV1();
        requestVo.setType(ErrorType.UNKNOWN);
        requestVo.setStatus(HttpStatus.CREATED.value());
        requestVo.setBody("{\"result\": \"ok\"}");
        requestVo.setHeaders(HttpHeaders.EMPTY);
    }

    @Test
    @DisplayName("[Fail] Create New Error")
    @Order(0)
    void test_FailCreate() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.post(BASE_URI)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(asBody(requestVo))
                                .with(SecurityMockMvcRequestPostProcessors.csrf().asHeader())
                )
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @DisplayName("[OK] Update Error")
    @Order(150)
    void testUpdate() throws Exception {
        requestVo.setStatus(HttpStatus.OK.value());

        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders.put(BASE_URI + "/" + requestVo.getType())
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(asBody(requestVo))
                                .with(SecurityMockMvcRequestPostProcessors.csrf().asHeader())
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        Assertions.assertNotNull(response);

        String responseBody = response.getContentAsString();
        Assertions.assertTrue(StringUtils.hasText(responseBody));

        log.debug("response = {}", responseBody);

        MockErrorVoV1 responseVo = DESERIALIZE_OBJECT_MAPPER.readValue(responseBody, MockErrorVoV1.class);
        Assertions.assertEquals(ErrorType.UNKNOWN, responseVo.getType());

        Assertions.assertEquals(requestVo.getStatus(), responseVo.getStatus());
        Assertions.assertEquals(requestVo.getBody(), responseVo.getBody());
        Assertions.assertEquals(requestVo.getHeaders(), responseVo.getHeaders());

        Assertions.assertNotNull(responseVo.getLastUpdated());

        requestVo = responseVo;
    }

    @Test
    @DisplayName("[OK] Get Error Detail")
    @Order(200)
    void testGet() throws Exception {
        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders.get(BASE_URI + "/" + requestVo.getType())
                                .with(SecurityMockMvcRequestPostProcessors.csrf().asHeader())
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        Assertions.assertNotNull(response);

        String responseBody = response.getContentAsString();
        Assertions.assertTrue(StringUtils.hasText(responseBody));

        MockErrorVoV1 responseVo = DESERIALIZE_OBJECT_MAPPER.readValue(responseBody, MockErrorVoV1.class);
        Assertions.assertEquals(ErrorType.UNKNOWN, responseVo.getType());

        Assertions.assertEquals(requestVo.getStatus(), responseVo.getStatus());
        Assertions.assertEquals(requestVo.getBody(), responseVo.getBody());
        Assertions.assertEquals(requestVo.getHeaders(), responseVo.getHeaders());

        Assertions.assertNotNull(responseVo.getLastUpdated());
    }

    @Test
    @DisplayName("[Fail] Delete Error")
    @Order(1000000)
    void testDelete() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.delete(BASE_URI + "/" + requestVo.getType())
                                .with(SecurityMockMvcRequestPostProcessors.csrf().asHeader())
                )
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }
}
