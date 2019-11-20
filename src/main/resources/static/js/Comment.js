$(document).ready(function() {
    showCmtList();
});
$(document).on('click', "#btnCmtAdd", function() {
    var post_id = $("#post_id").val();
    var content = $("#content").val();
    var comment = JSON.stringify({"post_id":post_id, "content":content});
   $.ajax({
        url:"/cmt/add",
        type:"POST",
        dataType: "text",
        data: comment,
        contentType: 'application/json; charset=utf-8',
        success: function(result) {
            showCmtList();
        }
    });
});
$(document).on('click', "#btnCmtUpdate", function() {
    var id = $("#id").val();
    var content = $("#content").val();
    var comment = JSON.stringify({"id":id, "content":content});
    console.log('update : '+comment);
    $.ajax({
        url:"/cmt/update",
        type:"POST",
        dataType: "text",
        data: comment,
        contentType : 'application/json; charset=utf-8',
        success: function(result) {
            showCmtList();
        }
    });
});
function showCmtList() {
    var post_id = $("#post_id");
    $.ajax({
        url:"/cmt/list",
        type:"GET",
        dataType: "json",
        data: post_id,
        success: function(result){
            var htmls = "";
            $(result).each(function() {
                console.log('success');
                htmls += '<div class="my-3 p-3 bg-white rounded shadow-sm row" id="cid'+this.id+'">';
                htmls += '<div class="col-6 border-bottom horder-gray">' + this.content + '</div>';
                htmls += '<div class="col-1 border-bottom horder-gray">' +this.user_name + '</div>';
                htmls += '<div class="col-3 border-bottom horder-gray">' +"(날짜)" + '</div>';
                htmls += '<a class="btn btn-light btn-sm" href="#" onclick="cmt_delete('+this.id+',\''+this.post_id+'\')">삭제</a>';
                htmls += '<a class="btn btn-light btn-sm" href="#" onclick="cmt_edit(' +this.id+',\'' +this.content+'\',\''+this.post_id+'\')">수정</a>';
                htmls += '</div>';
            });
            $("#CommentList").html(htmls);
        }
    });
}

function cmt_delete(id, post_id) {
    console.log('function cmt_delete');

    $.ajax({
        url:"/cmt/delete",
        dataType: "text",
        data: {id},
        success: function(result) {
            showCmtList();
        }
    });
}

function cmt_update(id, content) {
    var id = $("#id").val();
        var content = $("#content").val();
        var comment = JSON.stringify({"id":id, "content":content});
        console.log('update : '+comment);
        $.ajax({
            url:"/cmt/update",
            type:"POST",
            dataType: "text",
            data: comment,
            contentType : 'application/json; charset=utf-8',
            success: function(result) {
                showCmtList();
            }
        });
}

function cmt_edit(id, content, post_id) {
    console.log('function cmt_edit');
    var htmls = "";
    htmls += '<div class="my-3 p-3 bg-white rounded shadow-sm row" id="cid'+this.id+'">';
    htmls += '<form action="/cmt/update" method="post">';
    htmls += '<input type="hidden" id="id" name="id" value="'+id+'"/>';
    htmls += '<input type="hidden" id="post_id" name="post_id" value="'+post_id+'"/>';
    htmls += '<textarea class="form-control col-6" rows="1" id="content" name="content" placeholder="'+content+'"></textarea>';
    htmls += '<a class="btn btn-light btn-sm" href="#" onclick="showCmtList()">취소</a>';
    htmls += '<a class="btn btn-light btn-sm" href="#" onclick="cmt_update('+id+',\''+content+'\')">수정</a>';
    //htmls += '<button class="btn btn-light btn-sm" type="submit" id="update_button">수정</a>';
    htmls += '</form>';
    htmls += '</div>';

    $('#cid'+id).replaceWith(htmls);
    $('#cid'+id+'#content').focus();
}