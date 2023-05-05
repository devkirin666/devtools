(function() {
    const URL_ID = $("meta[name='url-id']").attr("content");

    const URL_BASE_URI = BASE_URL + "/api/v1/mock-urls";

    let URL_DETAIL_DATA = null;

    const setNoContent = function() {
        $(".content").html("<h1 class='text-black text-center align-middle'>No Content</h1>");
        $("#li-url-detail-id").text("");
    }

    const reloadUrlDetail = function() {
        if(!URL_ID) {
            notify.alert("Illegal Access");
            setNoContent();
            return;
        }
        api.get(URL_BASE_URI + "/" + URL_ID, null, function(body) {
            URL_DETAIL_DATA = body;
            mockServerUtil.updateFormData($("div.content-wrapper"), 'url', body);
            mockServerUtil.updateFormData($(".modal"), 'url', body);
        },function (e) {
            console.log("call error")
            setNoContent();
        });
    }

    $(document).ready(function() {
        const $DOCUMENT = $(document);
        const $URL_ENABLE_TOGGLE = $("#toggle-url-enable");
        const $MODAL_URI_EDIT = $("#modal-url-edit");

        $DOCUMENT.customEvent("mock.url.active", {id: URL_ID});

        $URL_ENABLE_TOGGLE.change(function(e) {
            const $this = $(this);
            const isEnabled = $this.val();
            api.patch(URL_BASE_URI + "/" + URL_ID + "/enable/" + isEnabled, null
                , function(body) {
                    $("#txt-url-uri").cancel(!asBoolean(isEnabled));
                    $this.customEvent("mock.url.enable", body);
                });
        });

        $("#btn-url-delete").click(function(e) {
            e.preventDefault();
            const $this = $(this);
            if(confirm("Delete ?")) {
                api.delete(URL_BASE_URI + "/" + URL_ID, function() {
                    notify.info("Deleted url. name = " + $this.data("name"))
                    $this.customEvent("mock.url.removed", {id: URL_ID});
                    setNoContent();
                });
            }
        });

        $("#btn-url-edit").click(function(e) {
            e.preventDefault();
            $MODAL_URI_EDIT.show();
        });

        $("#form-url-edit").submit(function(e) {
            e.preventDefault();
            forms.upsert($(this), function(e) {
                reloadUrlDetail();
                $MODAL_URI_EDIT.hide();
            });
        });

        $("#btn-url-test").click(function(e) {
            e.preventDefault();
            $(this).customEvent("mock.url.test.execute", URL_DETAIL_DATA);
        });

        reloadUrlDetail();
    });
})();