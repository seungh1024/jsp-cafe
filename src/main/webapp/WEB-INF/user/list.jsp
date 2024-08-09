<%@ page import="codesquad.javacafe.member.dto.response.MemberResponseDto" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Objects" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html lang="kr">
<jsp:include page="/common/header.jsp" />
<body>
<jsp:include page="/common/topbar.jsp" />
<jsp:include page="/common/navbar.jsp" />

<div class="container" id="main">
   <div class="col-md-10 col-md-offset-1">
      <div class="panel panel-default">
          <table class="table table-hover">
              <thead>
                <tr>
                    <th>#</th> <th>사용자 아이디</th> <th>이름</th> </th>
                </tr>
              </thead>
              <tbody>
                <%
                    var members = (List<MemberResponseDto>) request.getAttribute("memberList");
                    if (Objects.nonNull(members) && members.size() > 0) {
                        int row = 1;
                        for(MemberResponseDto memberResponseDto : members) {
                %>
                    <tr>
                        <th scope="row"><%= row++%></th> <td><a href = "/api/users/profile?userId=<%=memberResponseDto.getUserId()%>"><%=memberResponseDto.getUserId()%></a></td><td><%=memberResponseDto.getName()%></td><td><a href="/api/users/info?userId=<%=memberResponseDto.getUserId()%>" class="btn btn-success" role="button">수정</a></td>
                    </tr>
              <%
                    }
                  }
              %>
              </tbody>
          </table>
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