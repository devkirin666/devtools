(function() {
    hljs.configure({
        ignoreUnescapedHTML: true,
    });

    let isEdit = false;
    let lastData;

    const ERROR_ID = $("meta[name='error-type']").attr("content");
    const BASE_URI = BASE_URL + "/api/v1/mock-errors";


    const addHeaderRow = function(name, value) {
        const $tbl = $("#tbl-error-headers");
        const style = {
            view: isEdit ? "display: none;" : "",
            edit: isEdit ? "" : "display: none;",
        }
        $tbl.append("<tr>\n" +
            "   <td class='align-middle' data-form-toggle='view' style='" + style.view + "'>" + name + "</td>\n" +
            "   <td data-form-toggle='view' style='" + style.view + "'>" + value.replaceAll("\n", "<br/>") + "</td>\n" +
            "   <td class='align-middle' data-form-toggle='edit' style='" + style.edit + "'>\n" +
            "       <input type='text' class='form-control' data-name='headerName' value='" + name + "'>\n" +
            "   </td>\n" +
            "   <td class='align-middle' data-form-toggle='edit' style='" + style.edit + "'>\n" +
            "       <textarea class='form-control' data-name='headerValue'>" + value + "</textarea>\n" +
            "   </td>\n" +
            "   <td class='align-middle text-sm-center' data-form-toggle='edit' style='" + style.edit + "'><i class='fas fa-minus-circle text-danger header-remove'></i></td>\n" +
            "</tr>");

        $tbl.find("input[data-name='headerName']")
            .autocomplete({
                source: RESPONSE_HEADER_NAMES,
            });

        $tbl.find(".header-remove")
            .unbind("click")
            .click(function(e) {
                $(this).closest("tr").remove();
            })
    }

    const reloadErrorDetail = function(body) {
        mockServerUtil.updateFormData($("div.content-wrapper"), 'error', body);
        const $headerTbl = $("#tbl-error-headers");
        $headerTbl.html("");
        if(body.headers && Object.keys(body.headers).length > 0) {
            $.each(body.headers, function(key, value) {
                addHeaderRow(key, value.join("\n"));
            });
        } else {
            if(!isEdit) {
                $headerTbl.append("<tr data-form-toggle='view'><td colspan='2' class='text-center'>No Headers</td></tr>");
            }
        }
    }

    $(document).ready(function() {
        $("#btn-error-edit").click(function(e) {
            isEdit = true;
            mockServerUtil.formToggleView("edit");
            reloadErrorDetail(lastData);
        });

        $("#btn-error-edit-cancel").click(function(e) {
            isEdit = false;
            mockServerUtil.formToggleView("view");
        });

        $("#btn-error-add-headers").click(function(e) {
            e.preventDefault();
            addHeaderRow("", "");
            return false;
        });

        $("#form-error-detail").submit(function(e) {
            e.preventDefault();
            const $form = $(this);
            const data = $form.serializeJSON({checkboxUncheckedValue: "false"});

            data.headers = utils.http.plainHeaderAsMultiValue(
                $("#tbl-error-headers").tableAsMultiValueMap("input[data-name='headerName']", "textarea[data-name='headerValue']")
            );

            console.log(data.headers);

            api.put(BASE_URI + "/" + ERROR_ID, data, function(body) {
                isEdit = false;
                mockServerUtil.formToggleView("view");
                reloadErrorDetail(body);
                lastData = body;
            });
        })

        api.get(BASE_URI + "/" + ERROR_ID, null, function(body) {
            reloadErrorDetail(body);
            lastData = body;
        });
    });
})();