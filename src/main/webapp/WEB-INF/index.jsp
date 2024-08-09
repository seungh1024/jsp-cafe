<%@ page import="java.util.List" %>
<%@ page import="codesquad.javacafe.post.dto.response.PostResponseDto" %>
<%@ page import="java.util.Objects" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<!DOCTYPE html>
<html lang="kr">
<jsp:include page="/common/header.jsp" />
<body>
<jsp:include page="/common/topbar.jsp" />
<jsp:include page="/common/navbar.jsp" />


<div class="container" id="main">
    <div class="col-md-12 col-sm-12 col-lg-10 col-lg-offset-1">
        <div class="panel panel-default qna-list">
            <ul class="list">
                <%
                    var postList = (List<PostResponseDto>) request.getAttribute("postList");

                    if (Objects.nonNull(postList)) {
                        int row = 1;
                        for (PostResponseDto post : postList) {
                %>
                <li>
                    <div class="wrap">
                        <div class="main">
                            <strong class="subject">
                                <a href="<%= request.getContextPath() %>/api/post?postId=<%= post.getId() %>"><%= post.getTitle() %></a>
                            </strong>
                            <div class="auth-info">
                                <i class="icon-add-comment"></i>
                                <span class="time"><%= post.getCreatedAt() %></span>
                                <a href="<%= request.getContextPath() %>/api/users/profile?userId=<%= post.getMemberId()%>" class="author"><%= post.getWriter()%></a>
                            </div>
                            <div class="reply" title="댓글">
                                <i class="icon-reply"></i>
                                <span class="point"><%=row++%></span>
                            </div>
                        </div>
                    </div>
                </li>
                <%
                        }
                    }
                %>
            </ul>
            <div class="row">
                <div class="col-md-3"></div>
                <div class="col-md-6 text-center">
                    <ul class="pagination center-block" style="display:inline-block;">
                        <%
                            var startPage = (int)request.getAttribute("startPage");
                            var endPage = (int)request.getAttribute("endPage");
                            if(startPage > 5){

                        %>
                        <li><a href="/api/post/paging?page=<%=startPage-1%>">«</a></li>
                        <%
                            }
                            for(int i = startPage; i <= endPage; i++){
                        %>
                        <li><a href="/api/post/paging?page=<%=i%>"><%=i%></a></li>
                        <%
                            }
                            var isEnd = (Boolean) request.getAttribute("isEnd");
                            if(isEnd == null){
                        %>
                        <li><a href="/api/post/paging?page=<%=endPage+1%>">»</a></li>
                        <%
                            }
                        %>
                    </ul>
                </div>
                <div class="col-md-3 qna-write">
                    <a href="<%= request.getContextPath() %>/api/post/page" class="btn btn-primary pull-right" role="button">질문하기</a>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- script references -->
<%
    var contextPath = request.getContextPath();
%>
<script src="<%=contextPath%>/js/jquery-2.2.0.min.js"></script>
<script src="<%=contextPath%>/js/bootstrap.min.js"></script>
<script src="<%=contextPath%>/js/scripts.js"></script>
</body>
</html>
