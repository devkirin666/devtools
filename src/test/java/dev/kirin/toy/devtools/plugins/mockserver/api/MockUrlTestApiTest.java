package dev.kirin.toy.devtools.plugins.mockserver.api;

import dev.kirin.common.core.utils.StringUtil;
import dev.kirin.toy.devtools.plugins.mockserver.api.vo.MockResponseVoV1;
import dev.kirin.toy.devtools.plugins.mockserver.api.vo.MockUrlTestVoV1;
import dev.kirin.toy.devtools.plugins.mockserver.api.vo.MockUrlVoV1;
import dev.kirin.toy.devtools.plugins.mockserver.domain.mockerror.code.ErrorType;
import dev.kirin.toy.devtools.plugins.mockserver.domain.mockresponse.dto.MockResponseSearchParamDTO;
import dev.kirin.toy.devtools.plugins.mockserver.domain.mockresponse.entity.MockResponseEntity;
import dev.kirin.toy.devtools.plugins.mockserver.domain.mockresponse.repository.MockResponseRepository;
import dev.kirin.toy.devtools.plugins.mockserver.domain.mockurl.repository.MockUrlRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
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

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@AutoConfigureMockMvc
@SpringBootTest(properties = {"spring.profiles.active=test"})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@WithMockUser(username = "test", roles = {"ADMIN"})
@DisplayName("Mock URL-TEST API Test")
class MockUrlTestApiTest implements ApiControllerTestSupport {
    private static final String BASE_URL_URI = "/api/v1/mock-urls";
    private static final String BASE_RESPONSE_URI = "/api/v1/mock-responses";

    private MockUrlVoV1 requestVo;

    private final String TEST_URI = "/api/v1/test";
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MockUrlRepository urlRepository;
    @Autowired
    private MockResponseRepository responseRepository;

    @BeforeEach
    void beforeEach() throws Exception {
        MockUrlVoV1 requestVo = new MockUrlVoV1();
        requestVo.setName("Test Mock - URL");
        requestVo.setUri(TEST_URI);
        requestVo.setDescription("This is test description for mock-url.");
        requestVo.setEnabled(true);

        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders.post(BASE_URL_URI)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(asBody(requestVo))
                                .with(SecurityMockMvcRequestPostProcessors.csrf().asHeader())
                )
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        Assertions.assertNotNull(response);

        String responseBody = response.getContentAsString();
        Assertions.assertTrue(StringUtils.hasText(responseBody));

        MockUrlVoV1 responseVo = DESERIALIZE_OBJECT_MAPPER.readValue(responseBody, MockUrlVoV1.class);
        Assertions.assertTrue(StringUtils.hasText(responseVo.getId()));
        Assertions.assertEquals(32, responseVo.getId().length());

        Assertions.assertEquals(requestVo.getName(), responseVo.getName());
        Assertions.assertEquals(requestVo.getDescription(), responseVo.getDescription());
        Assertions.assertEquals(requestVo.getEnabled(), responseVo.getEnabled());

        Assertions.assertNotNull(responseVo.getLastUpdated());

        this.requestVo = responseVo;
    }

    @AfterEach
    void afterEach() {
        MockResponseSearchParamDTO responseSearchParamDTO = MockResponseSearchParamDTO.builder()
                .mockUrlIds(Collections.singleton(requestVo.getId()))
                .build();
        Set<String> responseIds = responseRepository.findAll(responseSearchParamDTO.asSpecification())
                .stream()
                .map(MockResponseEntity::getId)
                .collect(Collectors.toSet());
        responseRepository.deleteAllById(responseIds);
        urlRepository.deleteById(requestVo.getId());
    }

    @Test
    @DisplayName("No response test")
    void testNoResponse() throws Exception {
        String requestTraceId = UUID.randomUUID().toString();
        MockUrlTestVoV1.Request testRequestVo = new MockUrlTestVoV1.Request();
        testRequestVo.setUri(requestVo.getUri());
        testRequestVo.setMethod(HttpMethod.POST);
        testRequestVo.setHeaders(asHeaders("X-Request-Trace-Id", requestTraceId));
        testRequestVo.setBody("{\n" +
                "\t\"num\": 1\n" +
                "}");

        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders.put(BASE_URL_URI + "/" + requestVo.getId() + "/test")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(asBody(testRequestVo))
                                .with(SecurityMockMvcRequestPostProcessors.csrf().asHeader())
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        Assertions.assertNotNull(response);

        String responseBody = response.getContentAsString();
        Assertions.assertTrue(StringUtils.hasText(responseBody));

        log.debug("response = {}", responseBody);

        MockUrlTestVoV1.Response responseVo = DESERIALIZE_OBJECT_MAPPER.readValue(responseBody, MockUrlTestVoV1.Response.class);
        Assertions.assertNotNull(responseVo);
        Assertions.assertFalse(responseVo.isSuccess());
        Assertions.assertEquals(requestVo.getId(), responseVo.getId());
        Assertions.assertEquals(requestVo.getUri(), responseVo.getUri());
        Assertions.assertEquals(HttpStatus.NO_CONTENT.value(), responseVo.getStatus());
        Assertions.assertEquals(HttpHeaders.EMPTY, responseVo.getHeaders());
        Assertions.assertNotNull(responseVo.getBody());
        Assertions.assertTrue(responseVo.getResponses().isEmpty());
        Assertions.assertEquals(ErrorType.NO_RESPONSE.getTitle(), responseVo.getErrorType());
    }

    @Test
    @DisplayName("Single match response test")
    void testSingleMatchResponse() throws Exception {
        MockResponseVoV1 responseRequestVo = new MockResponseVoV1();
        responseRequestVo.setMockUrlId(requestVo.getId());
        responseRequestVo.setNo(1);
        responseRequestVo.setMethod(HttpMethod.POST);
        responseRequestVo.setCondition("request.body.num eq 1");
        responseRequestVo.setDescription("This is test description for mock-response.");
        responseRequestVo.setStatus(HttpStatus.CREATED.value());
        responseRequestVo.setBody("{\r\n" +
                "    \"timestamp\": #{now()},\n\n" +
                "    \"num\": @{request.body.num},\n\n" +
                "}");
        responseRequestVo.setHeaders(
                asHeaders("X-Response-Trace-Id", "#{uuid()}"
                    , "X-Request-Trace-Id", "@{request.headers.X-Request-Trace-Id[0]}")
        );
        responseRequestVo.setEnabled(true);

        addResponse(responseRequestVo);

        String requestTraceId = UUID.randomUUID().toString();
        MockUrlTestVoV1.Request testRequestVo = new MockUrlTestVoV1.Request();
        testRequestVo.setUri(requestVo.getUri());
        testRequestVo.setMethod(responseRequestVo.getMethod());
        testRequestVo.setHeaders(asHeaders("X-Request-Trace-Id", requestTraceId));
        testRequestVo.setBody("{\n" +
                "\t\"num\": 1\n" +
                "}");

        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders.put(BASE_URL_URI + "/" + requestVo.getId() + "/test")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(asBody(testRequestVo))
                                .with(SecurityMockMvcRequestPostProcessors.csrf().asHeader())
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        Assertions.assertNotNull(response);

        String responseBody = response.getContentAsString();
        Assertions.assertTrue(StringUtils.hasText(responseBody));

        log.debug("response = {}", responseBody);

        MockUrlTestVoV1.Response responseVo = DESERIALIZE_OBJECT_MAPPER.readValue(responseBody, MockUrlTestVoV1.Response.class);
        Assertions.assertNotNull(responseVo);
        Assertions.assertTrue(responseVo.isSuccess());
        Assertions.assertEquals(requestVo.getId(), responseVo.getId());
        Assertions.assertEquals(requestVo.getUri(), responseVo.getUri());
        Assertions.assertEquals(responseRequestVo.getStatus(), responseVo.getStatus());

        HttpHeaders headers = responseVo.getHeaders();
        Assertions.assertNotNull(headers);
        Assertions.assertEquals(2, headers.size());

        String responseTraceId = headers.getFirst("X-Response-Trace-Id");
        Assertions.assertNotNull(responseTraceId);
        Assertions.assertNotEquals(StringUtil.BLANK, responseTraceId);
        Assertions.assertEquals(36, responseTraceId.length());  // uuid with '-'

        String repliedRequestTraceId = headers.getFirst("X-Request-Trace-Id");
        Assertions.assertEquals(requestTraceId, repliedRequestTraceId);

        String body = responseVo.getBody();
        Assertions.assertNotNull(body);
        Assertions.assertNotEquals(StringUtil.BLANK, body);

        Assertions.assertNull(responseVo.getErrorType());

        List<MockUrlTestVoV1.MockResponseDetail> responses = responseVo.getResponses();
        Assertions.assertNotNull(responses);
        Assertions.assertEquals(1, responses.size());

        MockUrlTestVoV1.MockResponseDetail selectedResponse = responses.get(0);
        Assertions.assertTrue(selectedResponse.isSelected());
        Assertions.assertEquals(responseRequestVo.getId(), selectedResponse.getId());
        Assertions.assertEquals(responseRequestVo.getCondition(), selectedResponse.getCondition());
        Assertions.assertEquals(responseRequestVo.getNo(), selectedResponse.getNo());
    }

    @Test
    @DisplayName("[Fail] Invalid access format header")
    void testFailInvalidAccessFormatHeaderValue() throws Exception {
        MockResponseVoV1 responseRequestVo = new MockResponseVoV1();
        responseRequestVo.setMockUrlId(requestVo.getId());
        responseRequestVo.setNo(1);
        responseRequestVo.setMethod(HttpMethod.POST);
        responseRequestVo.setCondition("request.body.num eq 1");
        responseRequestVo.setDescription("This is test description for mock-response.");
        responseRequestVo.setStatus(HttpStatus.CREATED.value());
        responseRequestVo.setBody("{\n" +
                "    \"timestamp\": \"#{now()}\",\n" +
                "    \"num\": @{request.body.num},\n" +
                "}");
        responseRequestVo.setHeaders(
                asHeaders("X-Response-Trace-Id", "#{uuid()}"
                        , "X-Request-Trace-Id", "@{request.headers.X-Request-Trace-Id}")
        );
        responseRequestVo.setEnabled(true);

        addResponse(responseRequestVo);

        String requestTraceId = UUID.randomUUID().toString();
        MockUrlTestVoV1.Request testRequestVo = new MockUrlTestVoV1.Request();
        testRequestVo.setUri(requestVo.getUri());
        testRequestVo.setMethod(responseRequestVo.getMethod());
        testRequestVo.setHeaders(asHeaders("X-Request-Trace-Id", requestTraceId));
        testRequestVo.setBody("{\n" +
                "\t\"num\": 1\n" +
                "}");

        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders.put(BASE_URL_URI + "/" + requestVo.getId() + "/test")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(asBody(testRequestVo))
                                .with(SecurityMockMvcRequestPostProcessors.csrf().asHeader())
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        Assertions.assertNotNull(response);

        String responseBody = response.getContentAsString();
        Assertions.assertTrue(StringUtils.hasText(responseBody));

        log.debug("response = {}", responseBody);

        MockUrlTestVoV1.Response responseVo = DESERIALIZE_OBJECT_MAPPER.readValue(responseBody, MockUrlTestVoV1.Response.class);
        Assertions.assertNotNull(responseVo);
        Assertions.assertFalse(responseVo.isSuccess());
        Assertions.assertEquals(requestVo.getId(), responseVo.getId());
        Assertions.assertEquals(requestVo.getUri(), responseVo.getUri());
        Assertions.assertEquals(ErrorType.UNKNOWN.getTitle(), responseVo.getErrorType());
    }

    @Test
    @DisplayName("Multiple responses, but single match test")
    void testSingleMatchTestWithMultipleResponses() throws Exception {
        MockResponseVoV1 responseRequestVo = new MockResponseVoV1();
        responseRequestVo.setMockUrlId(requestVo.getId());
        responseRequestVo.setNo(1);
        responseRequestVo.setMethod(HttpMethod.POST);
        responseRequestVo.setCondition("request.body.num eq 1");
        responseRequestVo.setDescription("This is test description for mock-response.");
        responseRequestVo.setStatus(HttpStatus.CREATED.value());
        responseRequestVo.setBody("{\n" +
                "    \"timestamp\": \"#{now()}\",\n" +
                "    \"num\": @{request.body.num},\n" +
                "}");
        responseRequestVo.setHeaders(
                asHeaders("X-Response-Trace-Id", "#{uuid()}"
                        , "X-Request-Trace-Id", "@{request.headers.X-Request-Trace-Id[0]}")
        );
        responseRequestVo.setEnabled(true);

        addResponse(responseRequestVo);

        MockResponseVoV1 notMatchedResponse = new MockResponseVoV1();
        notMatchedResponse.setMockUrlId(requestVo.getId());
        notMatchedResponse.setNo(1);
        notMatchedResponse.setMethod(HttpMethod.POST);
        notMatchedResponse.setCondition("request.body.num eq 2");
        notMatchedResponse.setDescription("This is test description for mock-response.");
        notMatchedResponse.setStatus(HttpStatus.CREATED.value());
        notMatchedResponse.setBody("{\n" +
                "    \"timestamp\": \"#{now()}\",\n" +
                "    \"num\": @{request.body.num},\n" +
                "}");
        notMatchedResponse.setHeaders(
                asHeaders("X-Response-Trace-Id", "#{uuid()}"
                        , "X-Request-Trace-Id", "@{request.headers.X-Request-Trace-Id[0]}")
        );
        notMatchedResponse.setEnabled(true);

        addResponse(notMatchedResponse);

        String requestTraceId = UUID.randomUUID().toString();
        MockUrlTestVoV1.Request testRequestVo = new MockUrlTestVoV1.Request();
        testRequestVo.setUri(requestVo.getUri());
        testRequestVo.setMethod(responseRequestVo.getMethod());
        testRequestVo.setHeaders(asHeaders("X-Request-Trace-Id", requestTraceId));
        testRequestVo.setBody("{\n" +
                "\t\"num\": 1\n" +
                "}");

        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders.put(BASE_URL_URI + "/" + requestVo.getId() + "/test")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(asBody(testRequestVo))
                                .with(SecurityMockMvcRequestPostProcessors.csrf().asHeader())
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        Assertions.assertNotNull(response);

        String responseBody = response.getContentAsString();
        Assertions.assertTrue(StringUtils.hasText(responseBody));

        log.debug("response = {}", responseBody);

        MockUrlTestVoV1.Response responseVo = DESERIALIZE_OBJECT_MAPPER.readValue(responseBody, MockUrlTestVoV1.Response.class);
        Assertions.assertNotNull(responseVo);
        Assertions.assertTrue(responseVo.isSuccess());
        Assertions.assertEquals(requestVo.getId(), responseVo.getId());
        Assertions.assertEquals(requestVo.getUri(), responseVo.getUri());
        Assertions.assertEquals(responseRequestVo.getStatus(), responseVo.getStatus());

        HttpHeaders headers = responseVo.getHeaders();
        Assertions.assertNotNull(headers);
        Assertions.assertEquals(2, headers.size());

        String responseTraceId = headers.getFirst("X-Response-Trace-Id");
        Assertions.assertNotNull(responseTraceId);
        Assertions.assertNotEquals(StringUtil.BLANK, responseTraceId);
        Assertions.assertEquals(36, responseTraceId.length());  // uuid with '-'

        String repliedRequestTraceId = headers.getFirst("X-Request-Trace-Id");
        Assertions.assertEquals(requestTraceId, repliedRequestTraceId);

        String body = responseVo.getBody();
        Assertions.assertNotNull(body);
        Assertions.assertNotEquals(StringUtil.BLANK, body);

        Assertions.assertNull(responseVo.getErrorType());

        List<MockUrlTestVoV1.MockResponseDetail> responses = responseVo.getResponses();
        Assertions.assertNotNull(responses);
        Assertions.assertEquals(2, responses.size());

        MockUrlTestVoV1.MockResponseDetail selectedResponse = responses.get(0);
        Assertions.assertTrue(selectedResponse.isSelected());
        Assertions.assertEquals(responseRequestVo.getId(), selectedResponse.getId());
        Assertions.assertEquals(responseRequestVo.getCondition(), selectedResponse.getCondition());
        Assertions.assertEquals(responseRequestVo.getNo(), selectedResponse.getNo());

        MockUrlTestVoV1.MockResponseDetail response1 = responses.get(1);
        Assertions.assertFalse(response1.isSelected());
        Assertions.assertEquals(notMatchedResponse.getId(), response1.getId());
        Assertions.assertEquals(notMatchedResponse.getCondition(), response1.getCondition());
        Assertions.assertEquals(notMatchedResponse.getNo(), response1.getNo());
    }

    @Test
    @DisplayName("Multiple match test")
    void testMultipleMatch() throws Exception {
        MockResponseVoV1 responseRequestVo = new MockResponseVoV1();
        responseRequestVo.setMockUrlId(requestVo.getId());
        responseRequestVo.setNo(1);
        responseRequestVo.setMethod(HttpMethod.POST);
        responseRequestVo.setCondition("request.body.num eq 1");
        responseRequestVo.setDescription("This is test description for mock-response.");
        responseRequestVo.setStatus(HttpStatus.CREATED.value());
        responseRequestVo.setBody("{\n" +
                "    \"timestamp\": \"#{now()}\",\n" +
                "    \"num\": @{request.body.num},\n" +
                "}");
        responseRequestVo.setHeaders(
                asHeaders("X-Response-Trace-Id", "#{uuid()}"
                        , "X-Request-Trace-Id", "@{request.headers.X-Request-Trace-Id[0]}")
        );
        responseRequestVo.setEnabled(true);

        addResponse(responseRequestVo);

        MockResponseVoV1 secondaryResponse = new MockResponseVoV1();
        secondaryResponse.setMockUrlId(requestVo.getId());
        secondaryResponse.setNo(2);
        secondaryResponse.setMethod(HttpMethod.POST);
        secondaryResponse.setCondition("request.body.num eq 1");
        secondaryResponse.setDescription("This is test description for mock-response.");
        secondaryResponse.setStatus(HttpStatus.CREATED.value());
        secondaryResponse.setBody("{\n" +
                "    \"timestamp\": \"#{now()}\",\n" +
                "    \"num\": @{request.body.num},\n" +
                "}");
        secondaryResponse.setHeaders(
                asHeaders("X-Response-Trace-Id", "#{uuid()}"
                        , "X-Request-Trace-Id", "@{request.headers.X-Request-Trace-Id[0]}")
        );
        secondaryResponse.setEnabled(true);

        addResponse(secondaryResponse);

        String requestTraceId = UUID.randomUUID().toString();
        MockUrlTestVoV1.Request testRequestVo = new MockUrlTestVoV1.Request();
        testRequestVo.setUri(requestVo.getUri());
        testRequestVo.setMethod(responseRequestVo.getMethod());
        testRequestVo.setHeaders(asHeaders("X-Request-Trace-Id", requestTraceId));
        testRequestVo.setBody("{\n" +
                "\t\"num\": 1\n" +
                "}");

        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders.put(BASE_URL_URI + "/" + requestVo.getId() + "/test")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(asBody(testRequestVo))
                                .with(SecurityMockMvcRequestPostProcessors.csrf().asHeader())
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        Assertions.assertNotNull(response);

        String responseBody = response.getContentAsString();
        Assertions.assertTrue(StringUtils.hasText(responseBody));

        log.debug("response = {}", responseBody);

        MockUrlTestVoV1.Response responseVo = DESERIALIZE_OBJECT_MAPPER.readValue(responseBody, MockUrlTestVoV1.Response.class);
        Assertions.assertNotNull(responseVo);
        Assertions.assertTrue(responseVo.isSuccess());
        Assertions.assertEquals(requestVo.getId(), responseVo.getId());
        Assertions.assertEquals(requestVo.getUri(), responseVo.getUri());
        Assertions.assertEquals(responseRequestVo.getStatus(), responseVo.getStatus());

        HttpHeaders headers = responseVo.getHeaders();
        Assertions.assertNotNull(headers);
        Assertions.assertEquals(2, headers.size());

        String responseTraceId = headers.getFirst("X-Response-Trace-Id");
        Assertions.assertNotNull(responseTraceId);
        Assertions.assertNotEquals(StringUtil.BLANK, responseTraceId);
        Assertions.assertEquals(36, responseTraceId.length());  // uuid with '-'

        String repliedRequestTraceId = headers.getFirst("X-Request-Trace-Id");
        Assertions.assertEquals(requestTraceId, repliedRequestTraceId);

        String body = responseVo.getBody();
        Assertions.assertNotNull(body);
        Assertions.assertNotEquals(StringUtil.BLANK, body);

        Assertions.assertNull(responseVo.getErrorType());

        List<MockUrlTestVoV1.MockResponseDetail> responses = responseVo.getResponses();
        Assertions.assertNotNull(responses);
        Assertions.assertEquals(2, responses.size());

        MockUrlTestVoV1.MockResponseDetail selectedResponse = responses.get(0);
        Assertions.assertTrue(selectedResponse.isSelected());
        Assertions.assertEquals(responseRequestVo.getId(), selectedResponse.getId());
        Assertions.assertEquals(responseRequestVo.getCondition(), selectedResponse.getCondition());
        Assertions.assertEquals(responseRequestVo.getNo(), selectedResponse.getNo());

        MockUrlTestVoV1.MockResponseDetail response1 = responses.get(1);
        Assertions.assertFalse(response1.isSelected());
        Assertions.assertEquals(secondaryResponse.getId(), response1.getId());
        Assertions.assertEquals(secondaryResponse.getCondition(), response1.getCondition());
        Assertions.assertEquals(secondaryResponse.getNo(), response1.getNo());
    }

    private HttpHeaders asHeaders(String... items) {
        HttpHeaders headers = new HttpHeaders();
        for(int i = 0; i< items.length; i += 2) {
            headers.add(items[i], items[i+1]);
        }
        return headers;
    }

    private void addResponse(MockResponseVoV1 responseRequestVo) throws Exception {
        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders.post(BASE_RESPONSE_URI)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(asBody(responseRequestVo))
                                .with(SecurityMockMvcRequestPostProcessors.csrf().asHeader())
                )
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        Assertions.assertNotNull(response);

        String responseBody = response.getContentAsString();
        Assertions.assertTrue(StringUtils.hasText(responseBody));

        MockResponseVoV1 responseVo = DESERIALIZE_OBJECT_MAPPER.readValue(responseBody, MockResponseVoV1.class);
        Assertions.assertTrue(StringUtils.hasText(responseVo.getId()));
        Assertions.assertEquals(32, responseVo.getId().length());

        Assertions.assertEquals(responseRequestVo.getMockUrlId(), responseVo.getMockUrlId());
        Assertions.assertEquals(responseRequestVo.getNo(), responseVo.getNo());
        Assertions.assertEquals(responseRequestVo.getMethod(), responseVo.getMethod());
        Assertions.assertEquals(responseRequestVo.getCondition(), responseVo.getCondition());
        Assertions.assertEquals(responseRequestVo.getStatus(), responseVo.getStatus());
        Assertions.assertEquals(responseRequestVo.getBody(), responseVo.getBody());
        Assertions.assertEquals(responseRequestVo.getHeaders(), responseVo.getHeaders());
        Assertions.assertTrue(responseVo.getEnabled());
        Assertions.assertEquals(responseRequestVo.getDescription(), responseVo.getDescription());

        Assertions.assertNotNull(responseVo.getLastUpdated());

        responseRequestVo.setId(responseVo.getId());
    }
}
