<%@ include file="/WEB-INF/views/common/header.jsp" %>
<section class="container">
  <div class="hero-banner">
    <div><p class="eyebrow">The everyday edit</p><h1>Good finds. Better prices.</h1>
      <p>Browse useful picks across tech, style, home, books and more.</p>
      <a class="btn btn-accent" href="${pageContext.request.contextPath}/category">Explore all products</a>
    </div>
  </div>
  <c:if test="${not empty categories}">
    <section class="section">
      <h2 class="section-title">Shop by category</h2>
      <div class="category-grid">
        <c:forEach var="category" items="${categories}">
          <a class="category-tile" href="${pageContext.request.contextPath}/products?categoryId=${category.id}">
            <strong><c:out value="${category.name}"/></strong>
            <span><c:out value="${category.description}"/></span>
          </a>
        </c:forEach>
      </div>
    </section>
  </c:if>
  <section class="section">
    <h2 class="section-title">Featured deals</h2>
    <c:choose>
      <c:when test="${not empty featuredProducts}">
        <div class="product-grid">
          <c:forEach var="product" items="${featuredProducts}">
            <%@ include file="/WEB-INF/views/common/product-card.jsp" %>
          </c:forEach>
        </div>
      </c:when>
      <c:otherwise><div class="empty-state">No featured products yet. <a href="${pageContext.request.contextPath}/category">Browse the catalog</a>.</div></c:otherwise>
    </c:choose>
  </section>
</section>
<%@ include file="/WEB-INF/views/common/footer.jsp" %>
