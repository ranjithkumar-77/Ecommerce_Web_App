<%@ include file="/WEB-INF/views/common/header.jsp" %>
<section class="container section auth-layout">
  <div class="form-panel">
    <h1>Reset your password</h1>
    <p>Enter your account email. If it exists, a time-limited reset link will be issued.</p>
    <c:if test="${not empty infoMessage}"><div class="alert alert-info"><c:out value="${infoMessage}"/></div></c:if>
    <form action="${pageContext.request.contextPath}/forgot-password" method="post" class="stack-form">
      <label>Email address<input type="email" name="email" autocomplete="email" required></label>
      <button class="btn btn-primary btn-block" type="submit">Send reset link</button>
    </form>
    <p><a href="${pageContext.request.contextPath}/login">Return to sign in</a></p>
  </div>
</section>
<%@ include file="/WEB-INF/views/common/footer.jsp" %>
