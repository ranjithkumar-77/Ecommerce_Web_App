<%@ include file="/WEB-INF/views/common/header.jsp" %>
<section class="container section">
  <p class="eyebrow">Order #<c:out value="${order.id}"/></p>
  <h1>Track your order</h1>
  <div class="checkout-layout">
    <div class="summary-panel">
      <h2>Delivery progress</h2>
      <p>Current status: <span class="status-pill"><c:out value="${order.status}"/></span></p>
      <c:choose>
        <c:when test="${not empty history}">
          <ol class="stack-list">
            <c:forEach var="update" items="${history}">
              <li class="order-row">
                <div><strong><c:out value="${update.status}"/></strong>
                  <c:if test="${not empty update.remarks}"><p><c:out value="${update.remarks}"/></p></c:if>
                </div>
                <time><c:out value="${update.changedAt}"/></time>
              </li>
            </c:forEach>
          </ol>
        </c:when>
        <c:otherwise><p>Tracking updates will appear here as your order progresses.</p></c:otherwise>
      </c:choose>
    </div>
    <aside class="summary-panel">
      <h2>Delivery address</h2>
      <p><c:out value="${order.shippingName}"/><br>
        <c:out value="${order.shippingAddressLine1}"/>
        <c:if test="${not empty order.shippingAddressLine2}"><br><c:out value="${order.shippingAddressLine2}"/></c:if>
        <br><c:out value="${order.shippingCity}"/>, <c:out value="${order.shippingState}"/> <c:out value="${order.shippingPincode}"/>
        <br><c:out value="${order.shippingCountry}"/></p>
      <p>Payment: <c:out value="${order.paymentStatus}"/></p>
      <p>Total: <strong>${shop:amount(order.totalAmount)}</strong></p>
    </aside>
  </div>
  <p><a href="${pageContext.request.contextPath}/orders">Back to my orders</a></p>
</section>
<%@ include file="/WEB-INF/views/common/footer.jsp" %>
