(function () {
    const URL_BASE_URI = BASE_URL + "/api/v1/mock-urls";

    $(document).ready(function(e) {
        const $DIV_URL_TEST_BODY_CODE_EDITOR = $("#div-url-test-body-code-editor");
        const $MODAL_URL_TEST = $("#modal-url-test");
        const $TBL_URL_TEST_PATH_VARIABLES = $("#tbl-url-test-path-variables");

        const $TBL_URL_TEST_PARAMS = $("#tbl-url-test-params");
        const $TBL_URL_TEST_HEADERS = $("#tbl-url-test-headers");
        const $BTN_URL_TEST_ADD_PARAMS = $("#btn-url-test-add-params");
        const $BTN_URL_TEST_ADD_HEADERS = $("#btn-url-test-add-headers");


        const $TBL_URL_TEST_RESULT_HEADERS = $("#tbl-url-test-result-headers");
        const $TBL_URL_TEST_RESULT_RESPONSES = $("#tbl-url-test-result-responses");

        $(document).on("mock.url.test.execute", function(e) {
            const urlData = e.detail;
            testUrl(urlData);
        })

        const testUrl = function(urlData) {
            mockServerUtil.updateFormData($(".modal"), 'url-test', urlData);
            const pathVariables = utils.url.parsePathVariables(urlData.uri);

            if(pathVariables && pathVariables.length > 0) {
                $.each(pathVariables, function(idx, item) {
                    $TBL_URL_TEST_PATH_VARIABLES.append("<tr>\n" +
                        "   <td class='align-middle'>" + item + "</td>\n" +
                        "   <td><input type='text' class='form-control' name='pathVariable[" + item + "]' value=''></td>\n" +
                        "</tr>");
                });
                $TBL_URL_TEST_PATH_VARIABLES.closest(".card").show();
            } else {
                $TBL_URL_TEST_PATH_VARIABLES.closest(".card").hide();
            }
            if($TBL_URL_TEST_PARAMS.find("td").length < 1) {
                $BTN_URL_TEST_ADD_PARAMS.trigger("click");
            }
            if($TBL_URL_TEST_HEADERS.find("td").length < 1) {
                $BTN_URL_TEST_ADD_HEADERS.trigger("click");
            }
            $DIV_URL_TEST_BODY_CODE_EDITOR.aceEditor().setContent("\n".repeat(5));
            $MODAL_URL_TEST.find("input[name='headerName']").autocomplete({
                source: RESPONSE_HEADER_NAMES,
            });
            $MODAL_URL_TEST.show();
        }

        $("#form-url-test").submit(function(e) {
            e.preventDefault();
            const $form = $(this);
            const id = $($form.data("id-field")).val();
            const data = $form.serializeJSON({checkboxUncheckedValue: "false"});

            data.uri = data.uri.format(data.pathVariable);
            delete data.pathVariable;

            data.params = $TBL_URL_TEST_PARAMS.tableAsMultiValueMap("input[name='paramName']", "input[name='paramValue']");
            data.headers = utils.http.plainHeaderAsMultiValue(
                $TBL_URL_TEST_HEADERS.tableAsMultiValueMap("input[name='headerName']", "textarea[name='headerValue']")
            );
            data.body = $("#textarea-url-test-body").val().trim();

            console.log(data);
            api.put(URL_BASE_URI + "/" + id + "/test", data, function(body) {
                $MODAL_URL_TEST.hide();
                showResult(body);
            });
        });

        const showResult = function(resultData) {
            console.log(resultData);
            resultData.result = !!resultData.success ? "OK" : "FAIL - " + resultData.errorType;
            mockServerUtil.updateFormData($(".modal"), 'url-test-result', resultData);

            $TBL_URL_TEST_RESULT_HEADERS.html("");
            if(resultData.headers && Object.keys(resultData.headers).length > 0) {
                $.each(resultData.headers, function(key, value) {
                    $TBL_URL_TEST_RESULT_HEADERS.append("<tr>\n" +
                        "   <td class='align-middle'>" + key + "</td>\n" +
                        "   <td>" + value.join("\n") + "</td>\n" +
                        "</tr>");
                });
            } else {
                $TBL_URL_TEST_RESULT_HEADERS.append("<tr><td colspan='2' class='text-center'>No Headers</td></tr>");
            }

            $TBL_URL_TEST_RESULT_RESPONSES.html("");

            if(resultData.responses && resultData.responses.length > 0) {
                $.each(resultData.responses, function(idx, response) {
                    let rowColor = "";
                    if(response.selected) {
                        rowColor = "bg-success";
                    } else if(!response.matched) {
                        rowColor = "bg-secondary"
                    }

                    const $tr = $("<tr data-toggle='tooltip' data-placement='bottom'>" +
                        "   <td>" + response.no + "</td>" +
                        "   <td>" + response.id + "</td>" +
                        "   <td>" + response.condition + "</td>" +
                        "   <td>" + response.description + "</td>" +
                        "</tr>")
                        .addClass(rowColor)
                        .attr("title", response.cause);
                    $TBL_URL_TEST_RESULT_RESPONSES.append($tr)
                })
            } else {
                $TBL_URL_TEST_RESULT_RESPONSES.append("<tr><td colspan='4' class='text-center'>No matched responses</td></tr>")
            }

            $("#modal-url-test-result").show();
        }

        $BTN_URL_TEST_ADD_HEADERS.click(function(e) {
            e.preventDefault();
            $TBL_URL_TEST_HEADERS.append("<tr>\n" +
                "   <td class='align-middle'><input type='text' class='form-control' name='headerName'></td>\n" +
                "   <td><textarea type='text' class='form-control' name='headerValue'></textarea></td>\n" +
                "   <td class='align-middle text-sm-center p-0 m-0'><button type='button' name='btn-url-test-remove-header' class='btn btn-sm btn-tool'><i class='fas fa-minus'></i></button></td>\n" +
                "</tr>");
            $TBL_URL_TEST_HEADERS.find("input[name='headerName']")
                .autocomplete({
                    source: RESPONSE_HEADER_NAMES,
                });
            $("button[name='btn-url-test-remove-header']").unbind("click")
                .click(function(e) {
                    e.preventDefault();
                    $(this).closest("tr").remove();
                });
            return false;
        });

        $BTN_URL_TEST_ADD_PARAMS.click(function(e) {
            e.preventDefault();
            $TBL_URL_TEST_PARAMS.append("<tr>\n" +
                "   <td><input type='text' class='form-control' name='paramName' value=''></td>\n" +
                "   <td><input type='text' class='form-control' name='paramValue' value=''></td>\n" +
                "   <td class='align-middle text-sm-center p-0 m-0'><button type='button' name='btn-url-test-remove-parameter' class='btn btn-sm btn-tool'><i class='fas fa-minus'></i></button></td>\n" +
                "</tr>");
            $("button[name='btn-url-test-remove-parameter']").unbind("click")
                .click(function(e) {
                    e.preventDefault();
                    $(this).closest("tr").remove();
                });
            return false;
        });

        $("#btn-url-test-back").click(function(e) {
            e.preventDefault();
            $("#modal-url-test-result").hide();
            $MODAL_URL_TEST.show();
        })
    })
})();