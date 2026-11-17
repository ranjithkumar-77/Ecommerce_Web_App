<%@ include file="/WEB-INF/views/common/header.jsp" %>
<section class="container section">
  <p class="eyebrow">Your account</p><h1>Profile</h1>
  <c:if test="${not empty successMessage}"><div class="alert alert-success"><c:out value="${successMessage}"/></div></c:if>
  <c:if test="${not empty errorMessage}"><div class="alert alert-error"><c:out value="${errorMessage}"/></div></c:if>
  <div class="account-grid">
    <form action="${pageContext.request.contextPath}/profile" method="post" class="form-panel stack-form">
      <h2>Personal details</h2><input type="hidden" name="action" value="updateProfile">
      <label>First name<input name="firstName" value="<c:out value='${user.firstName}'/>" required maxlength="100"></label>
      <label>Last name<input name="lastName" value="<c:out value='${user.lastName}'/>" required maxlength="100"></label>
      <label>Email<input type="email" value="<c:out value='${user.email}'/>" readonly></label>
      <label>Phone<input type="tel" name="phone" value="<c:out value='${user.phone}'/>" maxlength="20"></label>
      <button class="btn btn-primary" type="submit">Save profile</button>
    </form>
    <form action="${pageContext.request.contextPath}/profile" method="post" class="form-panel stack-form">
      <h2>Change password</h2><input type="hidden" name="action" value="changePassword">
      <label>Current password<input type="password" name="oldPassword" autocomplete="current-password" required></label>
      <label>New password (at least 8 characters)<input type="password" name="newPassword" autocomplete="new-password" minlength="8" required></label>
      <button class="btn btn-primary" type="submit">Update password</button>
    </form>
    <div class="form-panel">
      <h2>Saved address</h2>
      <p><c:out value="${user.addressLine1}"/><c:if test="${not empty user.addressLine2}"><br><c:out value="${user.addressLine2}"/></c:if>
        <br><c:out value="${user.city}"/> <c:out value="${user.state}"/> <c:out value="${user.pincode}"/></p>
      <a class="btn btn-outline" href="${pageContext.request.contextPath}/address">Manage address</a>
      <p><a href="${pageContext.request.contextPath}/orders">View your orders</a></p>
    </div>
  </div>
</section>
<%@ include file="/WEB-INF/views/common/footer.jsp" %>
