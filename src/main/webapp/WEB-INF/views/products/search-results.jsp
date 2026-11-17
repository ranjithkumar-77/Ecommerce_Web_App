<%@ include file="/WEB-INF/views/common/header.jsp" %>
<section class="container section">
  <p class="eyebrow">Search results</p>
  <h1>Results for “<c:out value="${keyword}"/>”</h1>
  <c:if test="${not empty errorMessage}"><div class="alert alert-error"><c:out value="${errorMessage}"/></div></c:if>
  <form action="${pageContext.request.contextPath}/search" method="get" class="filter-sidebar">
    <input type="hidden" name="q" value="<c:out value='${keyword}'/>">
    <h2>Refine results</h2>
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
    <c:when test="${not empty searchResults}">
      <div class="product-grid">
        <c:forEach var="product" items="${searchResults}">
          <%@ include file="/WEB-INF/views/common/product-card.jsp" %>
        </c:forEach>
      </div>
    </c:when>
    <c:otherwise><div class="empty-state">No matching products. Try a different search.</div></c:otherwise>
  </c:choose>
</section>
<%@ include file="/WEB-INF/views/common/footer.jsp" %>
