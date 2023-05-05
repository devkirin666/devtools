package dev.kirin.toy.devtools.plugins.mockserver.api.controller;

import dev.kirin.common.spring.component.AbstractFixedApiController;
import dev.kirin.toy.devtools.plugins.mockserver.api.facade.MockErrorApiFacadeV11;
import dev.kirin.toy.devtools.plugins.mockserver.api.vo.MockErrorSearchParamVoV1;
import dev.kirin.toy.devtools.plugins.mockserver.api.vo.MockErrorVoV1;
import dev.kirin.toy.devtools.plugins.mockserver.domain.mockerror.code.ErrorType;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v1/mock-errors")
@Tag(name = "Mock Error Administrator API")
public class MockErrorApiControllerV1 extends AbstractFixedApiController<ErrorType, MockErrorVoV1, MockErrorSearchParamVoV1, MockErrorApiFacadeV11> {
}
