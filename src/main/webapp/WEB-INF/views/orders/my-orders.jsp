<%@ include file="/WEB-INF/views/common/header.jsp" %>
<section class="container section">
  <h1>Your orders</h1>
  <c:choose>
    <c:when test="${not empty orders}">
      <div class="stack-list">
        <c:forEach var="order" items="${orders}">
          <article class="order-row">
            <div><p class="eyebrow">Order #<c:out value="${order.id}"/></p>
              <p>Placed <c:out value="${order.createdAt}"/></p></div>
            <div><span class="status-pill"><c:out value="${order.status}"/></span>
              <p>Payment: <c:out value="${order.paymentStatus}"/></p></div>
            <strong>${shop:amount(order.totalAmount)}</strong>
            <a class="btn btn-outline btn-sm" href="${pageContext.request.contextPath}/order-tracking?orderId=${order.id}">Track order</a>
          </article>
        </c:forEach>
      </div>
    </c:when>
    <c:otherwise><div class="empty-state"><h2>No orders yet</h2><a href="${pageContext.request.contextPath}/category">Start shopping</a></div></c:otherwise>
  </c:choose>
</section>
<%@ include file="/WEB-INF/views/common/footer.jsp" %>
