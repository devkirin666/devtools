package dev.kirin.toy.devtools.plugins.mockserver.api;

import com.fasterxml.jackson.core.type.TypeReference;
import dev.kirin.common.junit.extension.depedent.DependentTest;
import dev.kirin.common.junit.extension.depedent.DependentTestExtension;
import dev.kirin.toy.devtools.plugins.mockserver.api.vo.MockUrlVoV1;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import support.ApiControllerTestSupport;
import support.model.TestPageResponseVo;

import java.util.List;
import java.util.UUID;

@Slf4j
@AutoConfigureMockMvc
@SpringBootTest(properties = {"spring.profiles.active=test"})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@WithMockUser(username = "test", roles = {"ADMIN"})
@ExtendWith({DependentTestExtension.class})
@DisplayName("Mock URL CRUD API Test")
class MockUrlApiControllerTest implements ApiControllerTestSupport {
    private static final String BASE_URI = "/api/v1/mock-urls";

    private static MockUrlVoV1 requestVo;
    @Autowired
    private MockMvc mockMvc;

    @BeforeAll
    static void beforeAll() {
        requestVo = new MockUrlVoV1();
        requestVo.setName("CRUD Mock - URL");
        requestVo.setUri("/api/v1/crud/{id}");
        requestVo.setDescription("This is test description for mock-url.");
        requestVo.setEnabled(true);
    }

    @Test
    @DisplayName("[OK] Create New URL")
    @Order(0)
    void testCreate() throws Exception {
        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders.post(BASE_URI)
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

        requestVo = responseVo;
    }

    @DependentTest(method = "testCreate")
    @DisplayName("[Fail] Create New URL - Duplicate name")
    @Order(1)
    void failTestCreate_DuplicateName() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.post(BASE_URI)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(asBody(requestVo))
                                .with(SecurityMockMvcRequestPostProcessors.csrf().asHeader())
                )
                .andExpect(MockMvcResultMatchers.status().isConflict());
    }

    @Test
    @Order(100)
    @DisplayName("[Fail] Update URL - Id is 'null'")
    void failTestUpdate_IdIsNull() throws Exception {
        mockMvc.perform(
                    MockMvcRequestBuilders.put(BASE_URI + "/null")
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(asBody(requestVo))
                            .with(SecurityMockMvcRequestPostProcessors.csrf().asHeader())
            )
            .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @Order(101)
    @DisplayName("[Fail] Update URL - Id is 'undefined'")
    void failTestUpdate_IdIsUndefined() throws Exception {
        mockMvc.perform(
                    MockMvcRequestBuilders.put(BASE_URI + "/undefined")
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(asBody(requestVo))
                            .with(SecurityMockMvcRequestPostProcessors.csrf().asHeader())
            )
            .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @DisplayName("[OK] Update URL")
    @Order(150)
    @DependentTest(method = "testCreate")
    void testUpdate() throws Exception {
        requestVo.setName(requestVo.getName() + " (changed)");
        requestVo.setDescription("(changed) " + requestVo.getDescription());

        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders.put(BASE_URI + "/" + requestVo.getId())
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

        MockUrlVoV1 responseVo = DESERIALIZE_OBJECT_MAPPER.readValue(responseBody, MockUrlVoV1.class);
        Assertions.assertTrue(StringUtils.hasText(responseVo.getId()));
        Assertions.assertEquals(32, responseVo.getId().length());

        Assertions.assertEquals(requestVo.getName(), responseVo.getName());
        Assertions.assertEquals(requestVo.getDescription(), responseVo.getDescription());
        Assertions.assertEquals(requestVo.getEnabled(), responseVo.getEnabled());

        Assertions.assertNotNull(responseVo.getLastUpdated());

        requestVo = responseVo;
    }

    @Test
    @DisplayName("[Fail] Get URL - Id is 'null'")
    void failTestGet_IdIsNull() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.get(BASE_URI + "/null")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .with(SecurityMockMvcRequestPostProcessors.csrf().asHeader())
                )
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @DisplayName("[Fail] Get URL - Id is 'undefined'")
    void failTestGet_IdIsUndefined() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.get(BASE_URI + "/undefined")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .with(SecurityMockMvcRequestPostProcessors.csrf().asHeader())
                )
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @DisplayName("[OK] Get URL Detail")
    @Order(200)
    @DependentTest(method = "testCreate")
    void testGet() throws Exception {
        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders.get(BASE_URI + "/" + requestVo.getId())
                                .with(SecurityMockMvcRequestPostProcessors.csrf().asHeader())
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
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
    }

    @DisplayName("[OK] Search URL")
    @Order(300)
    @DependentTest(method = "testCreate")
    void testSearch() throws Exception {
        int pageSize = 10;
        int pageNum = 0;

        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders.get(BASE_URI)
                                .param("page", String.valueOf(pageNum))
                                .param("size", String.valueOf(pageSize))
                                .with(SecurityMockMvcRequestPostProcessors.csrf().asHeader())
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        Assertions.assertNotNull(response);

        String responseBody = response.getContentAsString();
        Assertions.assertTrue(StringUtils.hasText(responseBody));

        TestPageResponseVo<MockUrlVoV1> searchResponse = DESERIALIZE_OBJECT_MAPPER.readValue(responseBody, new TypeReference<TestPageResponseVo<MockUrlVoV1>>() {});
        Assertions.assertNotNull(searchResponse);
        Assertions.assertEquals(1L, searchResponse.getTotal());
        Assertions.assertTrue(searchResponse.getHasContents());

        Pageable pageable = searchResponse.getPageable();
        Assertions.assertNotNull(pageable);
        Assertions.assertEquals(pageSize, pageable.getPageSize());
        Assertions.assertEquals(0, pageable.getOffset());
        Assertions.assertEquals(pageNum, pageable.getPageNumber());

        List<MockUrlVoV1> contents = searchResponse.getContents();
        Assertions.assertFalse(CollectionUtils.isEmpty(contents));
        Assertions.assertEquals(1, contents.size());

        MockUrlVoV1 responseVo = contents.get(0);
        Assertions.assertTrue(StringUtils.hasText(responseVo.getId()));
        Assertions.assertEquals(32, responseVo.getId().length());

        Assertions.assertEquals(requestVo.getName(), responseVo.getName());
        Assertions.assertEquals(requestVo.getDescription(), responseVo.getDescription());
        Assertions.assertEquals(requestVo.getEnabled(), responseVo.getEnabled());

        Assertions.assertNotNull(responseVo.getLastUpdated());
    }

    @Test
    @DisplayName("[Fail] Delete URL - Id is 'null'")
    void failTestDelete_IdIsNull() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.delete(BASE_URI + "/null")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .with(SecurityMockMvcRequestPostProcessors.csrf().asHeader())
                )
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @DisplayName("[Fail] Delete URL - Id is 'undefined'")
    void failTestDelete_IdIsUndefined() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.delete(BASE_URI + "/undefined")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .with(SecurityMockMvcRequestPostProcessors.csrf().asHeader())
                )
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @DisplayName("[OK] Delete URL")
    @Order(1000000)
    @DependentTest(method = "testCreate")
    void testDelete() throws Exception {
        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders.delete(BASE_URI + "/" + requestVo.getId())
                                .with(SecurityMockMvcRequestPostProcessors.csrf().asHeader())
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        Assertions.assertNotNull(response);

        String responseBody = response.getContentAsString();
        Assertions.assertFalse(StringUtils.hasText(responseBody));
    }

    @Test
    @DisplayName("[Fail] Delete URL - Already deleted id")
    @Order(1000001)
    void failTestDelete_AlreadyDeletedId() throws Exception {
        String id = requestVo.getId();
        if(!StringUtils.hasText(id)) {
            id = UUID.randomUUID().toString();
        }
        mockMvc.perform(
                        MockMvcRequestBuilders.delete(BASE_URI + "/" + id)
                                .with(SecurityMockMvcRequestPostProcessors.csrf().asHeader())
                )
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }
}
