<%@ page import="codesquad.javacafe.common.session.MemberInfo" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<!DOCTYPE html>
<html lang="kr">

<jsp:include page="/common/header.jsp" />
<body>
<jsp:include page="/common/topbar.jsp" />
<jsp:include page="/common/navbar.jsp" />


<div class="container" id="main">
    <div class="col-md-6 col-md-offset-3">
        <div class="panel panel-default content-main">
            <%
				var loginInfo = (MemberInfo)request.getSession().getAttribute("loginInfo");
            %>
            <form name="question" method="post" action="/api/users/info">
                <div class="form-group">
                    <label for="userId">ID</label>
                    <input class="form-control" id="userId" name="userId" value="<%=loginInfo.getUserId()%>" readonly>
                </div>
                <div class="form-group">
                    <label for="password">기존 비밀번호</label>
                    <input type="password" class="form-control" id="oldPassword" name="oldPassword" placeholder="Password">
                </div>
                <div class="form-group">
                    <label for="password">변경할 비밀번호</label>
                    <input type="password" class="form-control" id="password" name="password" placeholder="Password">
                </div>
                <div class="form-group">
                    <label for="name">이름</label>
                    <input class="form-control" id="name" name="name" placeholder="<%=loginInfo.getName()%>">
                </div>
                <button type="submit" class="btn btn-success clearfix pull-right">수정</button>
                <div class="clearfix" />
            </form>
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