package dev.kirin.toy.devtools.plugins.mockserver.api.controller;

import dev.kirin.common.spring.component.AbstractCrudApiController;
import dev.kirin.toy.devtools.plugins.mockserver.api.facade.MockUrlApiFacadeV1;
import dev.kirin.toy.devtools.plugins.mockserver.api.vo.MockUrlSearchParamVoV1;
import dev.kirin.toy.devtools.plugins.mockserver.api.vo.MockUrlTestVoV1;
import dev.kirin.toy.devtools.plugins.mockserver.api.vo.MockUrlVoV1;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/v1/mock-urls")
@Tag(name = "Mock URL Administrator API")
public class MockUrlApiControllerV1 extends AbstractCrudApiController<String, MockUrlVoV1, MockUrlSearchParamVoV1, MockUrlApiFacadeV1> {
    @PatchMapping(value = "/{id}/enable/{enabled}")
    public MockUrlVoV1 patchEnable(@PathVariable("id") String id, @PathVariable("enabled") Boolean enabled) {
        MockUrlVoV1 body = MockUrlVoV1.builder()
                .enabled(enabled)
                .build();
        return facade.update(id, body);
    }

    @PutMapping(value = "/{id}/test")
    public MockUrlTestVoV1.Response testUrl(@PathVariable("id") String id, @RequestBody @Validated MockUrlTestVoV1.Request body) {
        return facade.testUrl(id, body);
    }
}
