# ShopNow e-commerce application

A Java 21 / Jakarta Servlet 6.0 web application using JSP, JDBC, and MySQL. The application includes account registration and login, product/category browsing and search, a per-user cart, checkout with coupons, cash-on-delivery orders, order history, profile/address management, and one-time password-reset links.

The sample catalog uses Unsplash image URLs with a local placeholder fallback; network access is required to load the remote photos.

## Customer pages

The customer storefront is organized into these 18 pages:

- Account access: `/register`, `/login`, `/forgot-password`, `/reset-password`
- Shopping: `/home`, `/category`, `/products`, `/product?productId=...`, `/search?q=...`
- Cart and orders: `/cart`, `/address-selection`, `/checkout`, `/payment`, `/order-confirmation`, `/orders`, `/order-tracking`
- Customer account: `/profile`, `/address`

Each customer has one saved delivery address. Checkout uses that address and offers cash on delivery only; there are no admin, seller, wishlist, or informational pages.

## Requirements

- JDK 21
- Maven 3.9+
- MySQL 8+
- Tomcat 10.1+

The MySQL Connector/J and jBCrypt JARs are bundled under `src/main/webapp/WEB-INF/lib`.

## Database setup

Run `src/main/resources/database_setup.sql` against your MySQL server. It creates a separate `shopnow_ecommerce` database and its tables, then seeds six categories, twelve sample products, product sizes, online product/category photos, and demo coupons. Using a separate database leaves any older `ecommerce_website_application` database and its data untouched. The catalog seed is safe to rerun.

Set all three connection settings in the environment used to launch Tomcat. The application does not fall back to a blank root password:

```powershell
$env:DB_URL = "jdbc:mysql://localhost:3306/shopnow_ecommerce?useSSL=false&serverTimezone=UTC"
$env:DB_USER = "root"
$env:DB_PASSWORD = "your-mysql-password"
```

Restart Tomcat after changing these variables so the server process inherits them. Do not place database credentials in source code or commit them.

## Build and run

From this directory, build the WAR:

```powershell
mvn clean package
```

Copy `target/Ecommerce_Web_App-0.0.1-SNAPSHOT.war` into Tomcat 10.1's `webapps` folder and start Tomcat. Open `http://localhost:8080/Ecommerce_Web_App/`.

### Run from Eclipse

1. Use Eclipse IDE for Enterprise Java and Web Developers, JDK 21, and a Tomcat 10.1 server. Import this folder with **File > Import > Maven > Existing Maven Projects** and select `pom.xml`.
2. Right-click the project and choose **Maven > Update Project**, then **Project > Clean**. The project includes WTP deployment mappings for its Maven runtime libraries; confirm the JSTL JARs are published under the server's `WEB-INF/lib` if JSP pages report a missing `jakarta.tags.core` tag library.
3. In the **Servers** view, add `Ecommerce_Web_App` to the Tomcat 10.1 server, then choose **Clean** and **Publish** on the server.
4. In Windows **Environment Variables**, add user variables `DB_URL` (`jdbc:mysql://localhost:3306/shopnow_ecommerce?useSSL=false&serverTimezone=UTC`), `DB_USER` (your MySQL username), and `DB_PASSWORD` (your MySQL password). Alternatively, add the same variables under the Tomcat server's **Run Configurations... > Environment** tab. Do not put the password in Java source or commit it. Run `src/main/resources/database_setup.sql` once.
5. Open `http://localhost:8080/Ecommerce_Web_App/`.

If you change Windows user variables, fully restart Eclipse so its Tomcat process inherits them. After running `database_setup.sql` once, MySQL keeps the product catalog in its database across application/Tomcat restarts.

## Development-only integrations

- Password-reset tokens expire after 30 minutes and are stored as SHA-256 digests. Until SMTP is configured, the reset link is printed to the Tomcat console; never expose those logs publicly.
- Checkout currently supports cash on delivery only. No card or UPI details are collected and no online payment is processed. Integrate a payment provider before enabling online payments.
