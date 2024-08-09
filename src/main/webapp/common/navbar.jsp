<%@ page import="codesquad.javacafe.common.session.MemberInfo" %><%--
  Created by IntelliJ IDEA.
  User: woowatech28
  Date: 2024. 7. 25.
  Time: 오전 10:52
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%
    var contextPath = request.getContextPath();
    var userInfo = (MemberInfo)session.getAttribute("loginInfo");

	if(userInfo != null){
%>
<div class="navbar navbar-default" id="subnav">
    <div class="col-md-12">
        <div class="navbar-header">
            <a href="#" style="margin-left:15px;" class="navbar-btn btn btn-default btn-plus dropdown-toggle" data-toggle="dropdown"><i class="glyphicon glyphicon-home" style="color:#dd1111;"></i> Home <small><i class="glyphicon glyphicon-chevron-down"></i></small></a>
            <ul class="nav dropdown-menu">
                <li><a href="../user/profile.html"><i class="glyphicon glyphicon-user" style="color:#1111dd;"></i> Profile</a></li>
                <li class="nav-divider"></li>
                <li><a href="#"><i class="glyphicon glyphicon-cog" style="color:#dd1111;"></i> Settings</a></li>
            </ul>
            <button type="button" class="navbar-toggle" data-toggle="collapse" data-target="#navbar-collapse2">
                <span class="sr-only">Toggle navigation</span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
        </div>
        <div class="collapse navbar-collapse" id="navbar-collapse2">
            <ul class="nav navbar-nav navbar-right">
                <li class="active"><a href="/">Posts</a></li>
                <li><a href="<%=contextPath%>/api/post/page">글쓰기</a></li>
                <li><a href="/api/users">회원목록</a></li>
                <li><a href="/api/users/info" role="button">회원정보수정</a></li>
                <li><a href="#" role="button" id="logoutButton">로그아웃</a></li>
            </ul>
        </div>
    </div>
</div>
<script>
    document.getElementById("logoutButton").addEventListener("click", function(event) {
        event.preventDefault(); // 기본 동작 방지
        if (confirm("로그아웃 하시겠습니까?")) {
            fetch('/api/auth', {
                method: 'DELETE' // 로그아웃 요청을 POST로 설정
            }).then(response => {
                if (response.ok) {
                    // 로그아웃이 성공하면 로그인 페이지로 리디렉션
                    window.location.href = '/';
                } else {
                    // 오류 처리
                    console.error('Logout failed');
                }
            });
        }
    });

</script>
<%
    }else{
%>
<div class="navbar navbar-default" id="subnav">
    <div class="col-md-12">
        <div class="navbar-header">
            <a href="#" style="margin-left:15px;" class="navbar-btn btn btn-default btn-plus dropdown-toggle" data-toggle="dropdown"><i class="glyphicon glyphicon-home" style="color:#dd1111;"></i> Home <small><i class="glyphicon glyphicon-chevron-down"></i></small></a>
            <ul class="nav dropdown-menu">
                <li><a href="../user/profile.html"><i class="glyphicon glyphicon-user" style="color:#1111dd;"></i> Profile</a></li>
                <li class="nav-divider"></li>
                <li><a href="#"><i class="glyphicon glyphicon-cog" style="color:#dd1111;"></i> Settings</a></li>
            </ul>
            <button type="button" class="navbar-toggle" data-toggle="collapse" data-target="#navbar-collapse2">
                <span class="sr-only">Toggle navigation</span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
        </div>
        <div class="collapse navbar-collapse" id="navbar-collapse2">
            <ul class="nav navbar-nav navbar-right">
                <li class="active"><a href="/">Posts</a></li>
                <li><a href="<%=contextPath%>/api/post/page">글쓰기</a></li>
                <li><a href="/api/users">회원목록</a></li>
                <li><a href="/api/auth" role="button">로그인</a></li>
                <li><a href="/user/form.jsp" role="button">회원가입</a></li>
            </ul>
        </div>
    </div>
</div>
<%
    }
%>