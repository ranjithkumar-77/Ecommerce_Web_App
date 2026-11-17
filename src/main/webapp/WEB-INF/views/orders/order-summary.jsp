<%@ include file="/WEB-INF/views/common/header.jsp" %>
<section class="container section">
  <p class="eyebrow">Checkout · Step 2</p>
  <h1>Order summary</h1>
  <c:if test="${not empty errorMessage}"><div class="alert alert-error"><c:out value="${errorMessage}"/></div></c:if>
  <div class="checkout-layout">
    <div class="stack-list">
      <div class="summary-panel">
        <h2>Delivering to</h2>
        <p><c:out value="${user.firstName}"/> <c:out value="${user.lastName}"/><br>
          <c:out value="${user.addressLine1}"/>
          <c:if test="${not empty user.addressLine2}"><br><c:out value="${user.addressLine2}"/></c:if>
          <br><c:out value="${user.city}"/>, <c:out value="${user.state}"/> <c:out value="${user.pincode}"/>
          <br><c:out value="${user.country}"/></p>
        <a href="${pageContext.request.contextPath}/address-selection">Change address</a>
      </div>
      <div class="summary-panel">
        <h2>Items</h2>
        <c:forEach var="item" items="${cart.items}">
          <p><c:out value="${item.productName}"/> × <c:out value="${item.quantity}"/>
            <strong>${shop:amount(item.itemTotal)}</strong></p>
        </c:forEach>
      </div>
    </div>
    <aside class="summary-panel">
      <h2>Price details</h2>
      <p>Subtotal <strong>${shop:amount(subtotal)}</strong></p>
      <p>Discount <strong>-${shop:amount(discountAmount)}</strong></p>
      <p>Delivery <strong>${shop:amount(shippingCharge)}</strong></p>
      <p class="summary-total">Total <strong>${shop:amount(totalAmount)}</strong></p>
      <form action="${pageContext.request.contextPath}/checkout" method="get" class="stack-form">
        <label for="couponCode">Coupon code</label>
        <div class="inline-form">
          <input id="couponCode" name="couponCode" value="<c:out value='${couponCode}'/>" maxlength="50" autocomplete="off">
          <button type="submit" class="btn btn-outline">Apply</button>
        </div>
      </form>
      <form action="${pageContext.request.contextPath}/checkout" method="post" class="stack-form">
        <input type="hidden" name="couponCode" value="<c:out value='${couponCode}'/>">
        <button type="submit" class="btn btn-primary btn-block">Continue to payment</button>
      </form>
      <p class="muted">Cash on delivery is available at the next step.</p>
    </aside>
  </div>
</section>
<%@ include file="/WEB-INF/views/common/footer.jsp" %>
