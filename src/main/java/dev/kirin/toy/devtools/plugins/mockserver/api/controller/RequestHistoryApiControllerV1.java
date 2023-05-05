package dev.kirin.toy.devtools.plugins.mockserver.api.controller;

import dev.kirin.common.spring.component.AbstractSearchApiController;
import dev.kirin.toy.devtools.plugins.mockserver.api.facade.MockRequestHistoryApiFacadeV1;
import dev.kirin.toy.devtools.plugins.mockserver.api.vo.RequestHistorySearchParamVoV1;
import dev.kirin.toy.devtools.plugins.mockserver.api.vo.RequestHistoryVoV1;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v1/request-histories")
@Tag(name = "Mock Requested History API")
public class RequestHistoryApiControllerV1 extends AbstractSearchApiController<RequestHistoryVoV1, RequestHistorySearchParamVoV1, MockRequestHistoryApiFacadeV1> {
}
