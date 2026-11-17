<article class="product-card">
  <a class="product-card-img" href="${pageContext.request.contextPath}/product?productId=${product.id}">
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
  </a>
  <div class="product-card-body">
    <p class="product-category"><c:out value="${product.categoryName}"/></p>
    <h3><a href="${pageContext.request.contextPath}/product?productId=${product.id}"><c:out value="${product.name}"/></a></h3>
    <p class="product-price">${shop:amount(product.discountedPrice)}
      <c:if test="${product.discountPercent gt 0}">
        <del>${shop:amount(product.price)}</del>
        <span class="discount-label"><c:out value="${product.discountPercent}"/>% off</span>
      </c:if>
    </p>
  </div>
  <div class="product-card-footer">
    <a class="btn btn-primary" href="${pageContext.request.contextPath}/product?productId=${product.id}">View item</a>
  </div>
</article>
