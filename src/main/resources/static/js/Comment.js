$(document).ready(function() {
    showCmtList(1,1);
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

function showCmtList(groupIndex, pageIndex){
    var post_id = $("#post_id").val();

    $.ajax({
        url:"/cmt",
        type: "GET",
        data: {post_id: post_id, groupIndex: groupIndex, pageIndex: pageIndex},
        dataType: "json",
        success: function(result) {
            var htmls = "";
            $(result).each(function() {
                var date = new Date(this.updated_date);
                var hours = date.getHours();
                var minutes = "0" + date.getMinutes();
                var seconds = "0" + date.getSeconds();
                var updated_date = new Date(this.updated_date).toISOString().replace(/\D/g,"-").substr(0,10);
                updated_date += ' '+hours+':'+minutes.substr(-2)+':'+seconds.substr(-2)+'';
                console.log('success');
                htmls += '<div class="my-3 p-3 bg-white rounded shadow-sm row" id="cid'+this.id+'">';
                htmls += '<div class="col-6 border-bottom horder-gray">' + this.content + '</div>';
                htmls += '<div class="col-1 border-bottom horder-gray">' +this.user_name + '</div>';
                htmls += '<div class="col-3 border-bottom horder-gray">' +updated_date + '</div>';
                htmls += '<a class="btn btn-light btn-sm" href="#" onclick="cmt_delete('+this.id+',\''+this.post_id+'\')">삭제</a>';
                htmls += '<a class="btn btn-light btn-sm" href="#" onclick="cmt_edit(' +this.id+',\'' +this.content+'\',\''+this.post_id+'\')">수정</a>';
                htmls += '</div>';
            });
            $("#commentList").html(htmls);
        }
    })
}

function prevCmtPaging() {
    var post_id = $("#post_id").val();
    var pageIndex = $("#prev_pageIndex").val();
    var groupIndex = $("#prev_groupIndex").val();
    var htmls = "";
    console.log('prev pagination : '+groupIndex + ' ' +pageIndex);
    $.ajax({
        url:"/cmt/pagination",
        data: {post_id: post_id, pageIndex:pageIndex, groupIndex:groupIndex},
        dataType: "json",
        success:function(result) {
            var prev_groupIndex = groupIndex-1;
            var prev_pageIndex = (groupIndex-2)*result.groupSize+1;
            var next_groupIndex = groupIndex*1+1;
            var next_pageIndex = groupIndex*result.groupSize+1;
            console.log("****" + prev_groupIndex + " " + prev_pageIndex);
            console.log("****" + next_groupIndex + " " + next_pageIndex);
            htmls += '<div id="cmtPagination">';
            htmls += '<ul class="pagination">';
            if(result.prev==false) {
                htmls += '<li class="page-item disabled">';
                console.log(' prev disabled');
            }
            else { htmls += '<li>'; console.log('prev able');}
            htmls += '<input type="hidden" name="prev_groupIndex" id="prev_groupIndex" value="'+prev_groupIndex+'"/>';
            htmls += '<input type="hidden" name="prev_pageIndex" id="prev_pageIndex" value="'+prev_pageIndex+'"/>';
            htmls += '<button type="button" class="page-link" onclick="prevCmtPaging()">&laquo</button></li>';
            for (var i=result.startRow; i<=result.endRow; i++) {
                console.log(result.startRow + " " + i + " " +result.endRow);
                if (result.startRow == result.endRow) htmls += '<li class="page-item disabled">';
                else htmls += '<li>'
                htmls += '<button class="page-link" onclick="showCmtList('+groupIndex+',\''+i+'\')">'+i+'</button>';
            }
            if(result.next==false) {
                htmls += '<li class="page-item disabled">';
            }
            else { htmls += '<li>';}
            htmls += '<input type="hidden" id="next_groupIndex" value="'+next_groupIndex+'"/>';
            htmls += '<input type="hidden" id="next_pageIndex" value="'+next_pageIndex+'"/>';
            htmls += '<button type="button" class="page-link" onclick="nextCmtPaging()">&raquo</button></li>';
            htmls += '</ul></div>';
            showCmtList(result.groupIndex, result.pageIndex);
            $('#cmtPagination').replaceWith(htmls);
        }
    });
}

function nextCmtPaging() {
    var post_id = $("#post_id").val();
    var groupIndex = $("#next_groupIndex").val();
    var pageIndex = $("#next_pageIndex").val();
    var htmls = "";
    console.log("next pagination : " + groupIndex +" " + pageIndex);

    $.ajax({
        url:"/cmt/pagination",
        data: {post_id: post_id, groupIndex: groupIndex, pageIndex: pageIndex},
        dataType: "json",
        success:function(result) {
            var prev_groupIndex = groupIndex-1;
            var prev_pageIndex = (groupIndex-2)*result.groupSize+1;
            //console.log("****" + prev_groupIndex + " " + prev_pageIndex);
            var next_groupIndex = groupIndex*1+1;
            var next_pageIndex = groupIndex*result.groupSize+1;
            htmls += '<div id="cmtPagination">';
            htmls += '<ul class="pagination">';
            if(result.prev==false) {
                htmls += '<li class="page-item disabled">';
            }
            else { htmls += '<li>';}
            htmls += '<input type="hidden" id="prev_groupIndex" value="'+prev_groupIndex+'"/>';
            htmls += '<input type="hidden" id="prev_pageIndex" value="'+prev_pageIndex+'"/>';
            htmls += '<button type="button" class="page-link" onclick="prevCmtPaging()">&laquo</button></li>';
            for (var i=result.startRow; i<=result.endRow; i++) {
                console.log(result.startRow + " " + i + " " +result.endRow);
                if (result.startRow == result.endRow) htmls += '<li class="page-item disabled">';
                else htmls += '<li>'
                htmls += '<button class="page-link" onclick="showCmtList('+groupIndex+',\''+i+'\')">'+i+'</button>';
            }
            if(result.next==false) {
                htmls += '<li class="page-item disabled">';
            }
            else { htmls += '<li>';}
            htmls += '<input type="hidden" id="next_groupIndex" value="'+next_groupIndex+'"/>';
            htmls += '<input type="hidden" id="next_pageIndex" value="'+next_pageIndex+'"/>';
            htmls += '<button type="button" class="page-link" onclick="nextCmtPaging()">&raquo</button></li>';
            htmls += '</ul></div>';
            showCmtList(result.groupIndex, result.pageIndex);
            $('#cmtPagination').replaceWith(htmls);
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
    htmls += '</form>';
    htmls += '</div>';

    $('#cid'+id).replaceWith(htmls);
    $('#cid'+id+'#content').focus();
}