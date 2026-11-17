<%@ include file="/WEB-INF/views/common/header.jsp" %>
<section class="container section auth-layout">
  <div class="form-panel">
    <h1>Choose a new password</h1>
    <c:if test="${not empty errorMessage}"><div class="alert alert-error"><c:out value="${errorMessage}"/></div></c:if>
    <form action="${pageContext.request.contextPath}/reset-password" method="post" class="stack-form">
      <input type="hidden" name="token" value="<c:out value='${token}'/>">
      <label>New password (at least 8 characters)<input type="password" name="password" autocomplete="new-password" minlength="8" required></label>
      <label>Confirm new password<input type="password" name="confirmPassword" autocomplete="new-password" minlength="8" required></label>
      <button class="btn btn-primary btn-block" type="submit">Save new password</button>
    </form>
    <p><a href="${pageContext.request.contextPath}/login">Return to sign in</a></p>
  </div>
</section>
<%@ include file="/WEB-INF/views/common/footer.jsp" %>
