<%@ include file="/WEB-INF/views/common/header.jsp" %>
<section class="container section auth-layout">
  <div class="form-panel">
    <h1>Delivery address</h1>
    <c:if test="${not empty successMessage}"><div class="alert alert-success"><c:out value="${successMessage}"/></div></c:if>
    <c:if test="${not empty errorMessage}"><div class="alert alert-error"><c:out value="${errorMessage}"/></div></c:if>
    <form action="${pageContext.request.contextPath}/address" method="post" class="stack-form">
      <c:if test="${param.flow == 'checkout'}"><input type="hidden" name="flow" value="checkout"></c:if>
      <label>Address line 1<input name="addressLine1" value="<c:out value='${user.addressLine1}'/>" required maxlength="255"></label>
      <label>Address line 2<input name="addressLine2" value="<c:out value='${user.addressLine2}'/>" maxlength="255"></label>
      <div class="form-row">
        <label>City<input name="city" value="<c:out value='${user.city}'/>" required maxlength="100"></label>
        <label>State<input name="state" value="<c:out value='${user.state}'/>" required maxlength="100"></label>
      </div>
      <div class="form-row">
        <label>Postal code<input name="pincode" value="<c:out value='${user.pincode}'/>" required maxlength="20"></label>
        <label>Country<input name="country" value="<c:out value='${user.country}' default='India'/>" required maxlength="100"></label>
      </div>
      <button type="submit" class="btn btn-primary">Save address</button>
    </form>
    <c:if test="${param.flow == 'checkout'}">
      <p><a href="${pageContext.request.contextPath}/address-selection">Return to address selection</a></p>
    </c:if>
    <p><a href="${pageContext.request.contextPath}/profile">Back to profile</a></p>
  </div>
</section>
<%@ include file="/WEB-INF/views/common/footer.jsp" %>
