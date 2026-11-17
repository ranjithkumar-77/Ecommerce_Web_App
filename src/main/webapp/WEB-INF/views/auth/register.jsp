<%@ include file="/WEB-INF/views/common/header.jsp" %>
<section class="container section auth-layout">
  <div class="form-panel auth-card">
    <a class="auth-brand" href="${pageContext.request.contextPath}/home" aria-label="ShopNow home">Shop<span>Now</span></a>
    <h1>Create your account</h1>
    <p>Sign up to shop, check out, and track your orders.</p>
    <c:if test="${not empty errorMessage}"><div class="alert alert-error"><c:out value="${errorMessage}"/></div></c:if>
    <form action="${pageContext.request.contextPath}/register" method="post" class="stack-form">
      <div class="form-row">
        <label>First name<input name="firstName" autocomplete="given-name" required maxlength="100"></label>
        <label>Last name<input name="lastName" autocomplete="family-name" required maxlength="100"></label>
      </div>
      <label>Email address<input type="email" name="email" autocomplete="email" required maxlength="255"></label>
      <label>Phone (optional)<input type="tel" name="phone" autocomplete="tel" maxlength="20"></label>
      <label>Password (at least 8 characters)<input type="password" name="password" autocomplete="new-password" minlength="8" required></label>
      <button class="btn btn-primary btn-block" type="submit">Sign up</button>
    </form>
    <p>Already have an account? <a href="${pageContext.request.contextPath}/login">Sign in</a></p>
  </div>
</section>
<%@ include file="/WEB-INF/views/common/footer.jsp" %>
