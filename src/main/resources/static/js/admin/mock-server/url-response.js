(function() {
    let isEdit = false;

    hljs.configure({
        ignoreUnescapedHTML: true,
    })

    const URL_ID = $("meta[name='url-id']").attr("content");

    const RESPONSE_BASE_URI = BASE_URL + "/api/v1/mock-responses";

    const emptyResponse = function(lastNo) {
        return {
            status: 200,
            method: "GET",
            no: lastNo,
            mockUrlId: URL_ID,
            body: "{\r\n}",
        }
    }

    $(document).ready(function() {
        let responseTotalCount = 0;

        const $DIV_URL_RESPONSE_DETAIL = $("#div-url-response-detail");
        const $FORM_RESPONSE_LIST = $("#form-response-list");
        const $TBL_RESPONSE_DETAIL_HEADER = $("#tbl-response-detail-headers");

        const $BTN_RESPONSE_EDIT_SAVE = $("#btn-response-edit-save");
        const $BTN_RESPONSE_LIST_PREVIOUS = $("#btn-response-list-previous");
        const $BTN_RESPONSE_LIST_NEXT = $("#btn-response-list-next");

        let isNew = false;

        $("input[name='mockUrlId']").val(URL_ID);

        const addResponseHeaderRow = function(name, value) {
            $TBL_RESPONSE_DETAIL_HEADER.append("<tr>\n" +
                "   <td class='align-middle' data-form-toggle='view' style='display: none;'>" + name + "</td>\n" +
                "   <td data-form-toggle='view' style='display: none;'>" + value + "</td>\n" +
                "   <td class='align-middle' data-form-toggle='edit'>\n" +
                "       <input type='text' class='form-control' name='headerName' value='" + name + "'>\n" +
                "   </td>\n" +
                "   <td class='align-middle' data-form-toggle='edit'>\n" +
                "       <textarea class='form-control' name='headerValue'>" + value + "</textarea>\n" +
                "   </td>\n" +
                "   <td class='align-middle text-sm-center' data-form-toggle='edit'><i class='fas fa-minus-circle text-danger header-remove'></i></td>\n" +
                "</tr>");

            $TBL_RESPONSE_DETAIL_HEADER.find("input[name='headerName']")
                .autocomplete({
                    source: RESPONSE_HEADER_NAMES,
                });

            $TBL_RESPONSE_DETAIL_HEADER.find(".header-remove")
                .unbind("click").click(function(e) {
                    $(this).closest("tr").remove();
            })
        }

        const changeResponseListPage = function(bound) {
            const $pageField = $("#input-response-search-page");
            $pageField.val(Number($pageField.val()) + bound);
            $FORM_RESPONSE_LIST.trigger("submit");
        }

        const showDetail = function(isView, body) {
            if(isView) {
                isEdit = false;
            }
            mockServerUtil.updateFormData($("section.content"), 'response', body);

            $TBL_RESPONSE_DETAIL_HEADER.html("");
            if(body.headers) {
                $.each(body.headers, function(key, value) {
                    addResponseHeaderRow(key, value.join("\n"));
                });
            } else {
                $TBL_RESPONSE_DETAIL_HEADER.append("<tr data-form-toggle='view'><td colspan='2' class='text-center'>No Headers</td></tr>");
            }

            $DIV_URL_RESPONSE_DETAIL.show();

            $BTN_RESPONSE_EDIT_SAVE.removeClass(isNew ? "btn-info" : "btn-primary")
                .addClass(isNew ? "btn-primary" : "btn-info")
                .text(isNew ? "Create" : "Save");

            mockServerUtil.formToggleView(isView ? 'view' : 'edit');
        }

        const hideDetail = function() {
            isNew = false;

            $DIV_URL_RESPONSE_DETAIL.hide();
            mockServerUtil.resetData($DIV_URL_RESPONSE_DETAIL.find("form"));
        }

        const reloadResponseList = function($form) {
            const $tbl = $("#tbl-response-list").html("");

            $BTN_RESPONSE_LIST_PREVIOUS.removeClass("btn-primary").addClass("btn-default").attr("disabled", true);
            $BTN_RESPONSE_LIST_NEXT.removeClass("btn-primary").addClass("btn-default").attr("disabled", true);

            forms.get($form, function(body) {
                responseTotalCount = body.total;
                if(!body || !body.hasContents) {
                    $tbl.append("<tr class='text-center'><td colspan='4'>No Content</td></tr>")
                }
                $.each(body.contents, function(idx, item) {
                    const statusDetail = mockServerUtil.getStatusDetail(item.status);
                    $tbl.append("<tr data-id='" + item.id + "' class='url-response-detail-row'>\n" +
                        "   <td>" + item.no + "</td>\n" +
                        "   <td><span class='badge bg-" + statusDetail.style + "'>" + item.status + "(" + statusDetail.comment + ")" + "</span></td>\n" +
                        "   <td><span class='badge bg-" + METHOD_CLASS[item.method] + "'>" + item.method + "</span></td>\n" +
                        "   <td>" + item.condition + "</td>\n" +
                        "</tr>");
                });
                if(body.pageable.pageNumber > 0) {
                    $BTN_RESPONSE_LIST_PREVIOUS.removeClass("btn-default").addClass("btn-primary").attr("disabled", false);
                }
                if(body.pageable.pageNumber < body.pageable.totalPage - 1) {
                    $BTN_RESPONSE_LIST_NEXT.removeClass("btn-default").addClass("btn-primary").attr("disabled", false);
                }
                $(".url-response-detail-row").unbind("click")
                    .click(function(e) {
                        if(isEdit || isNew) {
                            alert("Now Editing");
                            return false;
                        }
                        const $this = $(this);
                        api.get(RESPONSE_BASE_URI + "/" + $this.data("id"), null, function(body) {
                            showDetail(true, body);
                            $(".url-response-detail-row").removeClass("active");
                            $this.addClass("active");
                        });
                    });
            });
        }

        $FORM_RESPONSE_LIST.submit(function (e) {
            e.preventDefault();
            reloadResponseList($(this));
        });

        $("#btn-response-add").click(function(e) {
            e.preventDefault();
            isNew = true;
            showDetail(false, emptyResponse(responseTotalCount));
            $("#btn-response-edit").trigger("click");
        });

        $("#btn-response-edit").click(function(e) {
            e.preventDefault();
            mockServerUtil.formToggleView("edit");
            isEdit = true;
        });

        $("#btn-response-edit-cancel").click(function(e) {
            e.preventDefault();
            mockServerUtil.formToggleView("view");
            isEdit = false;
            if(isNew) {
                hideDetail();
            }
        });

        $(".response-detail-tab").click(function(e) {
            if(isEdit && !isNew) {
                alert("Now Editing");
                return false;
            }
        });

        $("#btn-response-delete").click(function(e) {
            const $this = $(this);
            const id = $this.parent().find("input[name='id']").val();
            if(!!id) {
                api.delete(RESPONSE_BASE_URI + "/" + id, function() {
                    hideDetail();
                    $FORM_RESPONSE_LIST.trigger("submit");
                });
            } else {
                hideDetail();
            }
            isEdit = false;
        });

        $("button[name='btn-response-order-pre-value']").click(function(e) {
            e.preventDefault();
            const $this = $(this);
            const $target = $("#input-response-no");
            const value = $this.data("value");
            switch (value) {
                case "first":
                    $target.val(-1);
                    break;
                case "last":
                    $target.val(responseTotalCount);
                    break;
                default:
                    console.warn("Unknown value.", value);
                    break;
            }
        });

        $BTN_RESPONSE_LIST_PREVIOUS.click(function(e) {
            e.preventDefault();
            changeResponseListPage(-1)
        });

        $BTN_RESPONSE_LIST_NEXT.click(function(e) {
            e.preventDefault();
            changeResponseListPage(1)
        });

        $BTN_RESPONSE_EDIT_SAVE.click(function(e) {
            if(isNew) {
                let baseData = {}
                $DIV_URL_RESPONSE_DETAIL.find(".tab-pane").find("form")
                    .each(function(idx, item) {
                        Object.assign(baseData, $(item).serializeJSON({checkboxUncheckedValue: "false"}));
                    });

                baseData["headers"] = utils.http.plainHeaderAsMultiValue(
                    $TBL_RESPONSE_DETAIL_HEADER.tableAsMultiValueMap("input[name='headerName']", "textarea[name='headerValue']")
                );

                api.post(RESPONSE_BASE_URI, baseData, function(body) {
                    showDetail(true, body);
                    $FORM_RESPONSE_LIST.trigger("submit");
                    isNew = false;
                });
            } else {
                $(".tab-pane.active").find("form").trigger("submit");
            }
        });

        $("#btn-response-hide").click(function(e) {
            e.preventDefault();
            hideDetail();
        })

        $("form.partial-form").submit(function(e) {
            e.preventDefault();
            forms.upsert($(this), function(body) {
                $FORM_RESPONSE_LIST.trigger("submit");
                showDetail(true, body);
            })
        });

        $("#btn-response-new-header").click(function(e) {
            e.preventDefault();
            addResponseHeaderRow("", "");
        });

        $("#form-response-header").submit(function(e) {
            e.preventDefault();
            const headerData = utils.http.plainHeaderAsMultiValue(
                $TBL_RESPONSE_DETAIL_HEADER.tableAsMultiValueMap("input[name='headerName']", "textarea[name='headerValue']")
            );
            api.put(RESPONSE_BASE_URI + "/" + $("#input-response-id").val(), {mockUrlId: URL_ID, headers: headerData}, function(data) {
                showDetail(true, data);
            })
        });

        $FORM_RESPONSE_LIST.trigger("submit");
    });
})();