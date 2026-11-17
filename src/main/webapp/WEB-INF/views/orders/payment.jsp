<%@ include file="/WEB-INF/views/common/header.jsp" %>
<section class="container section narrow-content">
  <p class="eyebrow">Order #<c:out value="${order.id}"/></p>
  <h1>Payment</h1>
  <c:if test="${not empty errorMessage}"><div class="alert alert-error"><c:out value="${errorMessage}"/></div></c:if>
  <div class="summary-panel">
    <p>Order total <strong>${shop:amount(order.totalAmount)}</strong></p>
    <p>Choose cash on delivery to confirm your order. No card details are collected by this application.</p>
    <form action="${pageContext.request.contextPath}/payment" method="post" class="stack-form">
      <input type="hidden" name="orderId" value="${order.id}">
      <input type="hidden" name="paymentMethod" value="COD">
      <button class="btn btn-primary btn-block" type="submit">Confirm cash on delivery</button>
    </form>
  </div>
</section>
<%@ include file="/WEB-INF/views/common/footer.jsp" %>
