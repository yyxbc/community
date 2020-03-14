function post() {
    var questionId = $("#question_id").val();
    var content = $("#comment_content").val();
    comment2target(questionId, 1, content);
    console.log(questionId);
    console.log(content);
}

function comment2target(targetId, type, content) {
    if (!content) {
        alert("不能回复空内容···");
        return;
    }
    $.ajax({
        type: "POST",
        url: "/comment",
        contentType: "application/json",
        data: JSON.stringify({
            "parentId": targetId,
            "content": content,
            "type": type
        }),
        success: function (response) {
            console.log(response)
            if (response.code == 200) {
                window.location.reload();
            } else if (response.code == 2003) {
                var isAccepted =confirm(response.message);
                if(isAccepted){
                    window.open("https://github.com/login/oauth/authorize?client_id=8dbc6e07750093b1bc32&redirect_uri=http://47.94.23.170:8888/callback&scope=user&state=1");
                    window.localStorage.setItem("closable",true)
                }
            } else {
                alert(response.message);
            }
        },
        datatype: "json"
    });
}

function comment(e) {
    var commentId = e.getAttribute("data-id");
    var content = $("#input-" + commentId).val();
    var test = document.getElementById("comment-"+commentId);
    console.log(test);
    console.log(111);
    window.localStorage.setItem("sss996",test.outerHTML);
    console.log(test);
    window.localStorage.setItem("id",commentId);
    //添加一个全局标志位，说明添加成功了
    window.localStorage.setItem("commentSuccess","true");
    comment2target(commentId, 2, content);
}

/*
* 展开二级评论
* */
function collapseComments(e) {
    var id = e.getAttribute("data-id");
    if(id==null){
        id = window.localStorage.getItem("id");
    }
    var comments = $("#comment-" + id);
    var collapse = e.getAttribute("data-collapse");
    if (collapse) {
        comments.removeClass("in");
        e.removeAttribute("data-collapse");
        e.classList.remove("active");
    } else {
        var subCommentContainer = $("#comment-" + id);
        console.log(subCommentContainer);
        console.log(11);
        if (subCommentContainer.children().length != 1) {
            //展开二级评论
            comments.addClass("in");
            e.setAttribute("data-collapse", "in");
            e.classList.add("active");
        } else {
            $.getJSON("/comment/" + id, function (data) {
                $.each(data, function (index, comment) {

                    var mediaLeftElement = $("<div/>", {
                        "class": "media-left"
                    }).append($("<img/>", {
                        "class": "media-object img-circle",
                        "src": comment.user.avatarUrl
                    }));

                    var mediaBodyElement = $("<div/>", {
                        "class": "media-body"
                    }).append($("<h5/>", {
                        "class": "media-heading",
                        "html": comment.user.username
                    })).append($("<div/>", {
                        "class": "media-heading",
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
                    });
                    commentElement.append(mediaElement);
                    subCommentContainer.prepend(commentElement);
                });
                //展开二级评论
                comments.addClass("in");
                e.setAttribute("data-collapse", "in");
                e.classList.add("active");
            });
        }
    }
}

function showSelectTag() {
    $("#select-tag").show()
}

function selectTag(e) {
    var value =e.getAttribute("data-tag");
    var previous = $("#tag").val();
    if(previous.indexOf(value)==-1){
        if(previous){
            $("#tag").val(previous+','+value);
        }else{
            $("#tag").val(value);
        }
    }
}

$(function(){
    var cs = window.localStorage.getItem("commentSuccess");
    var s9 = window.localStorage.getItem("sss996");
    var judgespan = parseToDOM(s9);
    console.log(judgespan)
    if(cs == "true"){
        collapseComments(judgespan[0]);
        //成功后清空localstorage
        window.localStorage.removeItem("id");
        window.localStorage.removeItem("sss996");
        window.localStorage.removeItem("commentSuccess");
    }
})

function parseToDOM(str) {
    var div = document.createElement("div");
    if(typeof  str == "string")
        div.innerHTML = str;
    return div.childNodes;
}

function delete_question(id1) {
    if(confirm("确认删除吗？")){
        var id = $("#question-id").val();
        if(id1!=null){
            id=id1;
        }
        console.log(id);
        $.ajax({
            type: "post",
            url: "/question/"+id,
            contentType: "application/json",
            data: {
                "id": id
            },
            success: function (response) {
                console.log(response)
                if (response.code == 200) {
                    window.location.href="http://localhost:8888/";
                } else {
                    alert(response.message);
                }
            },
            datatype: "json"
        });
    }

}


function update(){
    var categoryName = $("#Select").find("option:selected").text();
    var tag = $("#tag").val();
   $.ajax({
       type: "post",
       url: "/categories",
       contentType : 'application/json',
       data:JSON.stringify({
           'categoryName': categoryName,
           'tag':tag
    }),
       success: function (response) {
           console.log(response);
           if (response.code == 200) {
               alert(response.message);
               window.location.reload();
               reset();
           } else {
               alert(response.message);
           }
       },
       dateType:"json"
   });

}

function reset(){
    document.getElementById('inputbox').style.display = 'none';
}
function add_tag() {
    var categoryName =$("#categoryName").text();
    document.getElementById('inputbox').style.display='block';
    var options =document.getElementById('Select').options;
    if(options){
        var len = options.length;
        if(categoryName=="all"){
            options[0].defaultSelected = true;
            options[0].selected = true;
        }else{
            for(var i=0;i<len;i++){
                if(options[i].text == categoryName){
                    options[i].defaultSelected = true;
                    options[i].selected = true;
                    return true;
                }
            }
        }

    }
}

function del_tag(e) {
   if(confirm('确定')){
       var categoryName =$("#categoryName").text();
       var tag =e.getAttribute("data-tag");
       $.ajax({
           type: "Delete",
           url: "/categories",
           contentType : 'application/json',
           data:JSON.stringify({
               'categoryName': categoryName,
               'tag':tag
           }),
           success: function (response) {
               console.log(response);
               if (response.code == 200) {
                   alert(response.message);
                   window.location.reload();
                   alert(response.message);
                   reset();
               } else {
                   alert(response.message);
               }
           },
           dateType:"json"
       });
   }else{
       return false;
   }
}