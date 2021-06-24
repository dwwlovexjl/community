/*
提交回复
 */
function post(){
    var questionId = $("#question_id").val();
    var content = $("#comment_content").val();
    comment2targetId(questionId,1,content);
}

function comment2targetId(targetId,type,content){
    if (!content) {
        alert("不能回复空内容")
        return;
    }
    $.ajax({
        type: "POST",
        url: "/comment",
        contentType:'application/json',
        data: JSON.stringify({
            "parentId":targetId,
            "content":content,
            "type":type
        }),
        success: function (response){
            if (response.code==200){
                $("#comment_section").hide();
                window.location.reload();//自动刷新
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

function comment(e){
    var commentId = e.getAttribute('data-id');
    var content = $("#input-"+commentId).val();
    comment2targetId(commentId,2,content);
}
/*
展开二级评论
 */
// function collapseComments(e){
//     var id = e.getAttribute('data-id');
//     var comments = $("#comment-"+id);
//     // 获取二级评论展开状态
//     // var collapse = e.getAttribute("data-collapse");
//     // collapse
//
//     // 展开二级评论
//     comments.toggleClass("in");
//     //标记二级评论展开状态
//     // e.setAttribute("data-collapse","in")
// }
function collapseComments(e) {
    var id = e.getAttribute("data-id");
    var comments = $("#comment-" + id);

    // 获取一下二级评论的展开状态
    var collapse = e.getAttribute("data-collapse");
    if (collapse) {
        // 折叠二级评论
        comments.removeClass("in");
        e.removeAttribute("data-collapse");
        e.classList.remove("active");
    } else {
        var subCommentContainer = $("#comment-" + id);
        if (subCommentContainer.children().length != 1) {
            //展开二级评论
            comments.addClass("in");
            // 标记二级评论展开状态
            e.setAttribute("data-collapse", "in");
            e.classList.add("active");
        } else {
            $.getJSON("/comment/" + id, function (data) {
                $.each(data.data.reverse(), function (index, comment) {
                    var mediaLeftElement = $("<div/>", {
                        "class": "media-left"
                    }).append($("<img/>", {
                        "class": "media-object img-rounded",
                        "src": comment.user.avatarUrl
                    }));

                    var mediaBodyElement = $("<div/>", {
                        "class": "media-body"
                    }).append($("<h5/>", {
                        "class": "media-heading",
                        "html": comment.user.name
                    })).append($("<div/>", {
                        "html": comment.content
                    })).append($("<div/>", {
                        "class": "menu"
                    }).append($("<span/>", {
                        "class": "pull-right",
                        "html": moment(comment.gmtCreate).format('YYYY-MM-DD')
                    })));

                    var mediaElement = $("<div/>", {
                        "class": "media"
                    }).append(mediaLeftElement).append(mediaBodyElement);

                    var commentElement = $("<div/>", {
                        "class": "col-lg-12 col-md-12 col-sm-12 col-xs-12 comments"
                    }).append(mediaElement);

                    subCommentContainer.prepend(commentElement);
                });
                //展开二级评论
                comments.addClass("in");
                // 标记二级评论展开状态
                e.setAttribute("data-collapse", "in");
                e.classList.add("active");
            });
        }
    }
}