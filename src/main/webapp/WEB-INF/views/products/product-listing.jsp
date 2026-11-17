<%@ include file="/WEB-INF/views/common/header.jsp" %>
<section class="container section">
  <div class="page-heading">
    <div><p class="eyebrow">Shop the collection</p><h1>Products</h1></div>
    <p><a href="${pageContext.request.contextPath}/category">View all</a></p>
  </div>
  <c:if test="${not empty errorMessage}"><div class="alert alert-error"><c:out value="${errorMessage}"/></div></c:if>
  <c:if test="${not empty allCategories}">
    <nav class="category-chips" aria-label="Product categories">
      <c:forEach var="category" items="${allCategories}">
        <a href="${pageContext.request.contextPath}/products?categoryId=${category.id}"><c:out value="${category.name}"/></a>
      </c:forEach>
    </nav>
  </c:if>
  <form action="${pageContext.request.contextPath}/products" method="get" class="filter-sidebar">
    <h2>Filter and sort</h2>
    <div class="filter-section">
      <label for="categoryId">Category</label>
      <select id="categoryId" name="categoryId">
        <option value="">All categories</option>
        <c:forEach var="category" items="${allCategories}">
          <option value="${category.id}" ${categoryId == category.id ? 'selected' : ''}><c:out value="${category.name}"/></option>
        </c:forEach>
      </select>
    </div>
    <div class="filter-section">
      <h4>Price range</h4>
      <div class="price-range-inputs">
        <input type="number" name="minPrice" min="0" step="0.01" placeholder="Min" value="<c:out value='${minPrice}'/>" aria-label="Minimum price">
        <span>to</span>
        <input type="number" name="maxPrice" min="0" step="0.01" placeholder="Max" value="<c:out value='${maxPrice}'/>" aria-label="Maximum price">
      </div>
    </div>
    <div class="filter-section">
      <label for="sort">Sort by</label>
      <select id="sort" name="sort">
        <option value="" ${empty sort ? 'selected' : ''}>Newest</option>
        <option value="price-asc" ${sort == 'price-asc' ? 'selected' : ''}>Price: low to high</option>
        <option value="price-desc" ${sort == 'price-desc' ? 'selected' : ''}>Price: high to low</option>
        <option value="name" ${sort == 'name' ? 'selected' : ''}>Name</option>
        <option value="discount" ${sort == 'discount' ? 'selected' : ''}>Best discount</option>
      </select>
    </div>
    <button class="btn btn-primary" type="submit">Apply filters</button>
  </form>
  <c:choose>
    <c:when test="${not empty products}">
      <div class="product-grid">
        <c:forEach var="product" items="${products}">
          <%@ include file="/WEB-INF/views/common/product-card.jsp" %>
        </c:forEach>
      </div>
    </c:when>
    <c:otherwise><div class="empty-state">No products found in this category.</div></c:otherwise>
  </c:choose>
</section>
<%@ include file="/WEB-INF/views/common/footer.jsp" %>
