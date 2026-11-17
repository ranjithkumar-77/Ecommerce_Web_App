<%@ include file="/WEB-INF/views/common/header.jsp" %>
<section class="container section auth-layout">
  <div class="form-panel auth-card">
    <a class="auth-brand" href="${pageContext.request.contextPath}/home" aria-label="ShopNow home">Shop<span>Now</span></a>
    <h1>Welcome back</h1>
    <p>Sign in to manage your cart and orders.</p>
    <c:if test="${param.reset eq 'success'}"><div class="alert alert-success">Your password has been reset. Sign in with your new password.</div></c:if>
    <c:if test="${not empty errorMessage}"><div class="alert alert-error"><c:out value="${errorMessage}"/></div></c:if>
    <form action="${pageContext.request.contextPath}/login" method="post" class="stack-form">
      <label>Email address<input type="email" name="email" autocomplete="email" required></label>
      <label>Password<input type="password" name="password" autocomplete="current-password" required></label>
      <button class="btn btn-primary btn-block" type="submit">Sign in</button>
    </form>
    <p><a href="${pageContext.request.contextPath}/forgot-password">Forgot your password?</a></p>
    <p>New to ShopNow? <a href="${pageContext.request.contextPath}/register">Sign up</a></p>
  </div>
</section>
<%@ include file="/WEB-INF/views/common/footer.jsp" %>
