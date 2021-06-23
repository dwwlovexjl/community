function post(){
    var questionId = $("#question_id").val();
    var content = $("#comment_content").val();
    $.ajax({
        type: "POST",
        url: "/comment",
        contentType:'application/json',
        data: JSON.stringify({
            "parentId":questionId,
            "content":content,
            "type":1
        }),
        success: function (response){
            if (response.code==200){
                $("#comment_section").hide();
            }else {
                if(response.code==2003){
                    var isAccepted = confirm(response.message);
                    if (isAccepted) {
                        window.open("https://gitee.com/oauth/authorize?client_id=76e770c006d714f6d6fc263bf882963f11943487663d73d9e0ff0895e6b2cadb&redirect_uri=http://localhost:8887/callback&scope=user_info&response_type=code");
                        window.localStorage.setItem("closable",true);
                    }
                }else {
                    alert(response.message);
                }
            }

            console.log(response);
        },
        dataType: "json"
    });
}