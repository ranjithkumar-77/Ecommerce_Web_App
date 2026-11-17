<%@ include file="/WEB-INF/views/common/header.jsp" %>
<section class="container section">
  <p class="eyebrow">Browse by department</p>
  <h1>Shop categories</h1>
  <c:choose>
    <c:when test="${not empty categories}">
      <div class="categories-grid">
        <c:forEach var="category" items="${categories}">
          <a class="category-card" href="${pageContext.request.contextPath}/products?categoryId=${category.id}">
            <c:if test="${not empty category.imageUrl}">
              <img src="<c:out value='${category.imageUrl}'/>" alt="" loading="lazy"
                   onerror="this.style.display='none'">
            </c:if>
            <div class="category-card-copy">
              <h2><c:out value="${category.name}"/></h2>
              <p><c:out value="${category.description}"/></p>
              <span class="btn btn-outline btn-sm">Browse products</span>
            </div>
          </a>
        </c:forEach>
      </div>
    </c:when>
    <c:otherwise>
      <div class="empty-state">Categories are not available right now.</div>
    </c:otherwise>
  </c:choose>
</section>
<%@ include file="/WEB-INF/views/common/footer.jsp" %>
