<%@ page import="codesquad.javacafe.post.dto.response.PostResponseDto" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@page buffer="8kb" autoFlush="true" %>

<!DOCTYPE html>
<html lang="kr">
<jsp:include page="/common/header.jsp"/>
<body>
<jsp:include page="/common/topbar.jsp"/>
<jsp:include page="/common/navbar.jsp"/>

<div class="container" id="main">
    <div class="col-md-12 col-sm-12 col-lg-12">
        <div class="panel panel-default">
            <%
                var post = (PostResponseDto) request.getAttribute("post");
            %>
            <header class="qna-header">
                <h2 class="qna-title"><%=post.getTitle()%>
                </h2>
            </header>
            <div class="content-main">
                <article class="article">
                    <div class="article-header">
                        <div class="article-header-thumb">
                            <img src="https://graph.facebook.com/v2.3/100000059371774/picture"
                                 class="article-author-thumb" alt="">
                        </div>

                        <div class="article-header-text">
                            <a href="<%= request.getContextPath() %>/api/users/profile?userId=<%= post.getMemberId()%>" class="article-author-name"><%=post.getWriter()%>
                            </a>
                            <a href="#" class="article-header-time" title="퍼머링크">
                                <%=post.getCreatedAt()%>
                                <i class="icon-link"></i>
                            </a>
                        </div>
                    </div>
                    <div class="article-doc">
                        <%
                            var contents = post.getContents().split("\n");
                            for (var content : contents) {

                        %><p><%=content%>
                    </p>
                        <%
                            }
                        %>
                    </div>
                    <div class="article-util">
                        <ul class="article-util-list">
                            <li>
                                <a class="link-modify-article" id="updateButton"
                                   href="/api/post/update?postId=<%=post.getId()%>">수정</a>
                            </li>
                            <li>
                                <form class="form-delete" action="/api/post" method="POST">
                                    <input type="hidden" name="method" value="DELETE">
                                    <input type="hidden" name="postId" id="postId" value="<%=post.getId()%>">
                                    <input type="hidden" name="memberId" id="memberId" value="<%=post.getMemberId()%>">
                                    <input type="hidden" name="memberId" id="lastCreated" value="<%=post.getCreatedAt()%>">
                                    <button class="link-delete-article" type="submit">삭제</button>
                                </form>
                            </li>
                            <li>
                                <a class="link-modify-article" href="/">목록</a>
                            </li>
                        </ul>
                    </div>
                </article>

                <div class="qna-comment">
                    <div class="qna-comment-slipp">
                        <p class="qna-comment-count"><strong></strong>댓글</p>
                        <div class="qna-comment-slipp-articles">



                        </div>
                        <div id = "more">

                        </div>
                        <form class="submit-write">
                            <div class="form-group" style="padding:14px;">
                                    <textarea class="form-control" id="comment"
                                              placeholder="Update your status"></textarea>
                            </div>
                            <button class="btn btn-success pull-right" id="commentButton" type="button"
                                    onclick="postComment()">답변하기
                            </button>
                            <div class="clearfix"></div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>


<%--게시글 생성 스크립트--%>
<script>

    function postComment() {
        console.log("create comment called");
        $.ajax({
            url: '/ajax/comment',
            type: 'POST',
            dataType: 'json',
            contentType: 'application/json',
            data: JSON.stringify({
                postId: $('#postId').val(),
                memberId: $('#memberId').val(),
                comment: $('#comment').val()
            }),
            timeout: 1500,
            success: function (data) {
                console.log(data);

                var moreDiv = document.querySelector('#more');
                moreDiv.innerHTML = '';
                var button = document.createElement('button');
                button.innerText = '더보기';
                button.className = 'btn btn-primary pull-right';
                button.addEventListener('click', function(){
                    loadCommentList(lastCreated, lastCommentId);
                })
                moreDiv.appendChild(button);
            },
            error: function (request, status, error) {

                console.log(error);
            }
        });

        document.getElementById('comment').value = '';
    }

    function addCommentToPage(commentData) {
        const container = document.querySelector('.qna-comment-slipp-articles');
        // container.innerHTML = '';

        var moreDiv = document.querySelector('#more');
        moreDiv.innerHTML = '';
        var template = '';
        if (commentData != null) {

            commentData.forEach(comment => {
                console.log(comment);
                var addTemplate = $('#answerTemplate').html();
                var formattedComment = addTemplate
                    .replace('{authorName}', comment.member.name) // Assuming memberId is used as authorName
                    .replace('{createdAt}', comment.createdAt)
                    .replace('{comment}', comment.comment.replaceAll(/\n/g, '<br>'))
                    .replaceAll('{commentId}', comment.id);

                template += formattedComment;
            });

            $('.qna-comment-slipp-articles').append(template);

            const lastComment = commentData[commentData.length - 1];
            console.log(commentData.length);
            console.log(commentData[0]);
            console.log(commentData[commentData.length - 1]);
            console.log(lastComment);
            lastCreated = lastComment.createdAt;
            lastCommentId = lastComment.id;

            var button = document.createElement('button');
            button.innerText = '더보기';
            button.className = 'btn btn-primary pull-right';
            button.addEventListener('click', function(){
                loadCommentList(lastCreated, lastCommentId);
            })
            moreDiv.appendChild(button);
        }
    }

</script>

<%-- 댓글 요청 스크립트--%>
<script>
    // var lastCreated= $('#lastCreated').val();
    // var lastCommentId = 0;
    var lastCreated = "1000-07-19T01:46:09"
    var lastCommentId = 0;

    window.onload = function () {
        loadCommentList(lastCreated, lastCommentId);
    }

    function loadCommentList(lastCreated, lastCommentId){
        $.ajax({
            url: '/ajax/comment?postId='+$('#postId').val()+'&lastCreated='+lastCreated + '&lastCommentId='+lastCommentId,
            type: 'GET',
            dataType: 'json',
            contentType: 'application/json',
            timeout: 1500,
            success: function (data) {
                console.log(data);
                addCommentToPage(data);
            },
            error: function (request, status, error) {
                console.log(error);
                alert(error);
            }
        });
    }
</script>

<%-- 게시글 삭제 --%>
<script>

    function deleteComment(event,commentId){
        console.log(commentId);
        $.ajax({
            url: '/ajax/comment',
            type: 'DELETE',
            dataType: 'json',
            contentType: 'application/json',
            timeout: 1500,
            data: JSON.stringify({
                commentId: commentId
            }),
            success: function () {
                var commentDiv = event.target.closest('.article');
                if (commentDiv) {
                    commentDiv.remove();
                }
                loadCommentList(lastCreated, lastCommentId);
            },
            error: function (request, status, error) {
                console.log(error);
                alert(error);
            }
        });
    }
</script>

<script type="text/template" id="answerTemplate">
    <article class="article">
        <div class="article-header">
            <div class="article-header-thumb">
                <img src="https://graph.facebook.com/v2.3/1324855987/picture" class="article-author-thumb" alt="">
            </div>
            <div class="article-header-text">
                <a href="#" class="article-author-name">{authorName}</a>
                <div class="article-header-time">{createdAt}</div>
            </div>
        </div>
        <div class="article-doc comment-doc">
            {comment}
        </div>
        <div class="article-util">
            <ul class="article-util-list">
                <li>
                    <form class="delete-answer-form">
                        <input type="hidden" name="_method" value="DELETE">
                    </form>
                    <button type="submit" class="delete-answer-button" id="commentDelete{commentId}" onclick="deleteComment(event,{commentId})">삭제</button>
                </li>
            </ul>
        </div>
    </article>
</script>

<!-- script references -->
<script src="<%= request.getContextPath() %>/js/jquery-2.2.0.min.js"></script>
<script src="<%= request.getContextPath() %>/js/bootstrap.min.js"></script>
<script src="<%= request.getContextPath() %>/js/scripts.js"></script>
</body>
</html>
