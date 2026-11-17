<%@ include file="/WEB-INF/views/common/header.jsp" %>
<section class="container section narrow-content">
  <p class="eyebrow">Checkout · Step 1</p>
  <h1>Select delivery address</h1>
  <div class="summary-panel">
    <h2><c:out value="${user.firstName}"/> <c:out value="${user.lastName}"/></h2>
    <p><c:out value="${user.addressLine1}"/>
      <c:if test="${not empty user.addressLine2}"><br><c:out value="${user.addressLine2}"/></c:if>
      <br><c:out value="${user.city}"/>, <c:out value="${user.state}"/> <c:out value="${user.pincode}"/>
      <br><c:out value="${user.country}"/></p>
    <c:if test="${not empty user.phone}"><p>Phone: <c:out value="${user.phone}"/></p></c:if>
    <div class="inline-form">
      <a class="btn btn-outline" href="${pageContext.request.contextPath}/address?flow=checkout">Edit saved address</a>
      <a class="btn btn-primary" href="${pageContext.request.contextPath}/checkout">Continue to order summary</a>
    </div>
  </div>
</section>
<%@ include file="/WEB-INF/views/common/footer.jsp" %>
