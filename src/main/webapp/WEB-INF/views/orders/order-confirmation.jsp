<%@ include file="/WEB-INF/views/common/header.jsp" %>
<section class="container section narrow-content">
  <div class="empty-state">
    <p class="eyebrow">Order #<c:out value="${order.id}"/></p>
    <h1>Thank you. Your order is confirmed.</h1>
    <p>Payment method: cash on delivery</p>
    <p>Order status: <strong><c:out value="${order.status}"/></strong></p>
    <p>Order total: <strong>${shop:amount(order.totalAmount)}</strong></p>
    <div class="inline-form">
      <a class="btn btn-primary" href="${pageContext.request.contextPath}/order-tracking?orderId=${order.id}">Track this order</a>
      <a class="btn btn-outline" href="${pageContext.request.contextPath}/orders">View all orders</a>
    </div>
  </div>
</section>
<%@ include file="/WEB-INF/views/common/footer.jsp" %>
