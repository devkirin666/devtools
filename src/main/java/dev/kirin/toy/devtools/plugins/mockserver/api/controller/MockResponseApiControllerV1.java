package dev.kirin.toy.devtools.plugins.mockserver.api.controller;

import dev.kirin.common.spring.component.AbstractCrudApiController;
import dev.kirin.toy.devtools.plugins.mockserver.api.facade.MockResponseApiFacadeV1;
import dev.kirin.toy.devtools.plugins.mockserver.api.vo.MockResponseSearchParamVoV1;
import dev.kirin.toy.devtools.plugins.mockserver.api.vo.MockResponseVoV1;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v1/mock-responses")
@Tag(name = "Mock Response Administrator API")
public class MockResponseApiControllerV1 extends AbstractCrudApiController<String, MockResponseVoV1, MockResponseSearchParamVoV1, MockResponseApiFacadeV1> {

}
