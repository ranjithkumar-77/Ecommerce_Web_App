<%@ include file="/WEB-INF/views/common/header.jsp" %>
<section class="container section">
  <h1>Your shopping cart</h1>
  <c:if test="${not empty errorMessage}"><div class="alert alert-error"><c:out value="${errorMessage}"/></div></c:if>
  <div id="cart-feedback" class="alert" role="status" aria-live="polite"></div>
  <c:choose>
    <c:when test="${not empty cart.items}">
      <div class="checkout-layout">
        <div class="stack-list">
          <c:forEach var="item" items="${cart.items}">
            <article class="cart-row">
              <c:choose>
                <c:when test="${fn:startsWith(item.productImage, 'http')}">
                  <img src="<c:out value='${item.productImage}'/>" alt=""
                       onerror="this.onerror=null;this.src='${pageContext.request.contextPath}/images/product-placeholder.svg'">
                </c:when>
                <c:otherwise>
                  <img src="<c:out value='${pageContext.request.contextPath}${item.productImage}'/>" alt=""
                       onerror="this.onerror=null;this.src='${pageContext.request.contextPath}/images/product-placeholder.svg'">
                </c:otherwise>
              </c:choose>
              <div class="cart-item-copy">
                <h2><c:out value="${item.productName}"/></h2>
                <p class="muted"><c:out value="${item.productColor}"/>
                  <c:if test="${not empty item.sizeLabel}"> · Size <c:out value="${item.sizeLabel}"/></c:if>
                </p>
                <p>${shop:amount(item.discountedPrice)} each</p>
              </div>
              <form action="${pageContext.request.contextPath}/cart" method="post" class="inline-form ajax-cart-form">
                <input type="hidden" name="action" value="update">
                <input type="hidden" name="cartItemId" value="${item.id}">
                <label class="sr-only" for="qty-${item.id}">Quantity</label>
                <input id="qty-${item.id}" type="number" name="quantity" min="1" value="${item.quantity}" required>
                <button class="btn btn-secondary btn-sm" type="submit">Update</button>
              </form>
              <strong>${shop:amount(item.itemTotal)}</strong>
              <form action="${pageContext.request.contextPath}/cart" method="post" class="ajax-cart-form">
                <input type="hidden" name="action" value="remove">
                <input type="hidden" name="cartItemId" value="${item.id}">
                <button class="btn btn-outline btn-sm" type="submit">Remove</button>
              </form>
            </article>
          </c:forEach>
        </div>
        <aside class="summary-panel">
          <h2>Order summary</h2>
          <p>Subtotal <strong>${shop:amount(subtotal)}</strong></p>
          <p class="muted">Shipping is calculated at checkout.</p>
          <a class="btn btn-primary btn-block" href="${pageContext.request.contextPath}/checkout">Continue to checkout</a>
          <a class="text-link" href="${pageContext.request.contextPath}/category">Continue shopping</a>
        </aside>
      </div>
    </c:when>
    <c:otherwise>
      <div class="empty-state"><h2>Your cart is empty</h2><p>Find something you like and it will appear here.</p>
        <a class="btn btn-primary" href="${pageContext.request.contextPath}/category">Browse products</a></div>
    </c:otherwise>
  </c:choose>
</section>
<%@ include file="/WEB-INF/views/common/footer.jsp" %>
