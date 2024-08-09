<%@ page import="codesquad.javacafe.post.dto.response.PostResponseDto" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<!DOCTYPE html>
<html lang="kr">

<jsp:include page="/common/header.jsp" />
<body>
<jsp:include page="/common/topbar.jsp" />
<jsp:include page="/common/navbar.jsp" />

<%
    var post = (PostResponseDto)request.getAttribute("post");
    System.out.println(post);
%>
<div class="container" id="main">
   <div class="col-md-12 col-sm-12 col-lg-10 col-lg-offset-1">
      <div class="panel panel-default content-main">
          <form name="question" method="post" action="/api/post">
              <input type="hidden" id = "method" name="method" value = "PUT"/>
              <input type="hidden" id = "postId" name="postId" value = "<%=post.getId()%>"/>
              <input type="hidden" id = "memberId" name="memberId" value = "<%=post.getMemberId()%>"/>
              <div class="form-group">
                  <label for="title">제목</label>
                  <input type="text" class="form-control" id="title" name="title" value="<%=post.getTitle()%>" placeholder="제목"/>
              </div>
              <div class="form-group">
                  <label for="contents">내용</label>
                  <textarea name="contents" id="contents" rows="5" class="form-control"><%=post.getContents()%></textarea>
              </div>
              <button type="submit" class="btn btn-success clearfix pull-right" id="updateButton">수정하기</button>
              <div class="clearfix" />
          </form>
        </div>
    </div>
</div>

<script>
    // document.getElementById("updateButton").addEventListener("click", function(event) {
    //     event.preventDefault(); // 기본 동작 방지
    //
    //     const id = document.getElementById("postId").value;
    //     const title = document.getElementById("title").value;
    //     const contents = document.getElementById("contents").value;
    //     const memberId = document.getElementById("memberId").value;
    //
    //     console.log("title = " , title);
    //     console.log("contents = " , contents);
    //
    //     var data = {
    //         id: 2,
    //         title: title,
    //         contents: contents,
    //         memberId: memberId
    //     }
    //     fetch('/api/post', {
    //         method: 'PUT',
    //         headers: {
    //             'Content-Type': 'application/json'
    //         },
    //         body: JSON.stringify(data)
    //     }).then(response => {
    //         if (response.ok) {
    //             window.location.href = '/api/post?postId='+id;
    //         } else {
    //             console.log(response.status);
    //             console.log(response.body);
    //         }
    //     });
    //
    // });
</script>

<!-- script references -->
<%
    var contextPath = request.getContextPath();
%>
<script src="<%=contextPath%>/js/jquery-2.2.0.min.js"></script>
<script src="<%=contextPath%>/js/bootstrap.min.js"></script>
<script src="<%=contextPath%>/js/scripts.js"></script>
	</body>
</html>