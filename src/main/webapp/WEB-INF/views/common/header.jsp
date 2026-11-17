<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="shop" uri="/WEB-INF/tlds/shopnow.tld" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>ShopNow | Everyday shopping made easy</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body class="${requestScope.authPage ? 'auth-page' : ''}">
<header class="site-header">
  <div class="top-nav">
    <div class="container">
      <a class="logo" href="${pageContext.request.contextPath}/home">Shop<span>Now</span></a>
      <form class="search-bar" action="${pageContext.request.contextPath}/search" method="get">
        <input type="search" name="q" placeholder="Search products and brands" aria-label="Search products">
        <button type="submit">Search</button>
      </form>
      <nav class="nav-links" aria-label="Main navigation">
        <a href="${pageContext.request.contextPath}/category"><span class="nav-value">Shop</span></a>
        <a class="cart-link" href="${pageContext.request.contextPath}/cart"><span class="nav-value">Cart</span></a>
        <c:choose>
          <c:when test="${not empty sessionScope.loggedUser}">
            <a href="${pageContext.request.contextPath}/profile"><span class="nav-value">Account</span></a>
            <a href="${pageContext.request.contextPath}/orders"><span class="nav-value">Orders</span></a>
            <a href="${pageContext.request.contextPath}/logout"><span class="nav-value">Sign out</span></a>
          </c:when>
          <c:otherwise>
            <a href="${pageContext.request.contextPath}/login"><span class="nav-value">Sign in</span></a>
            <a href="${pageContext.request.contextPath}/register"><span class="nav-value">Sign up</span></a>
          </c:otherwise>
        </c:choose>
      </nav>
    </div>
  </div>
</header>
<main class="main-content">
