<%@ include file="/WEB-INF/views/common/header.jsp" %>
<section class="container section">
  <p><a href="${pageContext.request.contextPath}/category">Continue shopping</a></p>
  <div class="product-detail">
    <div class="detail-image">
      <c:choose>
        <c:when test="${fn:startsWith(product.mainImageUrl, 'http')}">
          <img src="<c:out value='${product.mainImageUrl}'/>" alt="<c:out value='${product.name}'/>"
               onerror="this.onerror=null;this.src='${pageContext.request.contextPath}/images/product-placeholder.svg'">
        </c:when>
        <c:otherwise>
          <img src="<c:out value='${pageContext.request.contextPath}${product.mainImageUrl}'/>" alt="<c:out value='${product.name}'/>"
               onerror="this.onerror=null;this.src='${pageContext.request.contextPath}/images/product-placeholder.svg'">
        </c:otherwise>
      </c:choose>
      <c:if test="${not empty product.images}">
        <div class="detail-gallery" aria-label="More product images">
          <c:forEach var="image" items="${product.images}">
            <c:choose>
              <c:when test="${fn:startsWith(image.imageUrl, 'http')}">
                <img src="<c:out value='${image.imageUrl}'/>" alt="<c:out value='${product.name}'/>" loading="lazy"
                     onerror="this.style.display='none'">
              </c:when>
              <c:otherwise>
                <img src="<c:out value='${pageContext.request.contextPath}${image.imageUrl}'/>" alt="<c:out value='${product.name}'/>" loading="lazy"
                     onerror="this.style.display='none'">
              </c:otherwise>
            </c:choose>
          </c:forEach>
        </div>
      </c:if>
    </div>
    <div class="detail-copy">
      <p class="eyebrow"><c:out value="${product.categoryName}"/></p>
      <h1><c:out value="${product.name}"/></h1>
      <p class="muted"><c:out value="${product.brand}"/> <c:if test="${not empty product.color}">| <c:out value="${product.color}"/></c:if></p>
      <p class="product-price">${shop:amount(product.discountedPrice)}
        <c:if test="${product.discountPercent gt 0}"><del>${shop:amount(product.price)}</del> <span class="discount-label">${shop:amount(product.discountPercent)}% off</span></c:if>
      </p>
      <p><c:out value="${product.description}"/></p>
      <p class="${product.stockQuantity gt 0 ? 'stock-ok' : 'stock-out'}">
        <c:choose><c:when test="${product.stockQuantity gt 0}">In stock</c:when><c:otherwise>Currently unavailable</c:otherwise></c:choose>
      </p>
      <c:if test="${product.stockQuantity gt 0}">
        <form action="${pageContext.request.contextPath}/cart" method="post" class="stack-form ajax-cart-form">
          <input type="hidden" name="action" value="add">
          <input type="hidden" name="productId" value="${product.id}">
          <c:if test="${product.sizeAvailable}">
            <label>Size<select name="sizeId" required>
              <option value="">Choose a size</option>
              <c:forEach var="size" items="${product.sizes}">
                <option value="${size.id}" <c:if test="${size.stockQuantity le 0}">disabled</c:if>>
                  <c:out value="${size.sizeLabel}"/> <c:if test="${size.stockQuantity le 0}">(unavailable)</c:if>
                </option>
              </c:forEach>
            </select></label>
          </c:if>
          <label>Quantity<input type="number" name="quantity" value="1" min="1" max="${product.stockQuantity}" required></label>
          <button type="submit" class="btn btn-primary">Add to cart</button>
        </form>
      </c:if>
      <div id="cart-feedback" class="alert" role="status" aria-live="polite"></div>
    </div>
  </div>
</section>
<%@ include file="/WEB-INF/views/common/footer.jsp" %>
