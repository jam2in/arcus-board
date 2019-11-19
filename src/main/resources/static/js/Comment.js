$(document).ready(function() {
    load();
});
function load() {
    var date = new Date();

    $.ajax({
        url:"/cmt/list?id=325",
        type:"GET",
        dataType : "json",
        data: $("#post_id"),
        success: function(result){
            var htmls = "";
            $(result).each(function() {
                console.log('success');
                htmls += '<div class="col-6 border-bottom horder-gray">' + this.content + '</div>';
                htmls += '<div class="col-1 border-bottom horder-gray">' +this.user_name + '</div>';
                htmls += '<div class="col-3 border-bottom horder-gray">' +"(날짜)" + '</div>';
                htmls += '<a class="btn btn-light btn-sm" href="/cmt/delete?id=' +this.id+'">삭제</a>';
                htmls += '<a class="btn btn-light btn-sm" href="/cmt/edit?id=' +this.id+'">수정</a>';
            });
            $("#replyList").html(htmls);
        },
        error: function (result) {
            console.log('error');
        }
    });
}

/*
<div class="my-3 p-3 bg-white rounded shadow-sm row border" th:each="comment : ${comments}">
    <div class="col-6" th:text="${comment.content}"></div>
    <div class="col-1" th:text="${comment.user_name}"></div>
    <div class="col-3" th:text="${#dates.format(comment.updated_date, 'yyyy-MM-dd hh:mm')}"></div>
    <a class="btn btn-light btn-sm" th:href="@{'/cmt/delete?id=' + ${comment.id}}">삭제</a>
    <a class="btn btn-light btn-sm" th:href="@{'/cmt/edit?id=' + ${comment.id}}">수정</a>
</div>
*/