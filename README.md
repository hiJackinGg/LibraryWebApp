# LibraryWebApp
<b><i>Description:</i></b> simplified Library web application.<br><br>
<b><i>The application functionality:</i></b><br>
<ul>
<li>managing books available in the library: adding, removing, changing quantity. Each book
can have a few authors, an author can write a few books;<br></li>
<li>a book can be taken by different people and at different time;<br></li>
<li>registration is required to take books (email);<br></li>
<li>filter that shows all books / books available / books taken by the user;<br></li>
<li>library tracks its readers (users). Displays a history of a book (when and by whom was taken);<br></li>
<li>implemented sending notifications by mail to people who took a book (“You took the
following books in our library”)<br></li>
<li>on client side, it has a front-end page with grid. Grid allows paging,
sorting by book titles and author names.<br></li>
<li>requests to the server implemented via AJAX (like single-page app)<br></li>
</ul>

<b><i>Main technologies:</i></b> Spring MVC, plain JDBC, MSSQL, JUnit, Selenium Web driver, Maven, javascript and jQuery library (on client side).<br><br>
<b><i>Features:</i></b>
<ul>
<li>intensive usage of JDBC and SQL (crud, transactions, batch update, stored procedures, complex queries with window functions)<br></li>
<li>effective memory usage (pagination on database side, retrieved only what it's needed to client)<br></li>
<li>4 tables with relationships: one-to-many, many-to-many<br></li>
<li>exception handling, HTTP codes status<br></li>
<li>server- and client-side validation with appropriated error message to the user<br></li>
<li>working with http sessions (autorisation)<br></li>
<li>manual realisation of ajax table with pagination, filters, sorting<br></li>
<li>intensive usage of AJAX<br></li>
<li>intensive working with DOM model via javascript<br></li>
<li>modern styling of front-page<br></li>
<li>testing of all application layers (DAO, controllers, user interface)<br></li>
<li>testing UI with Selenium Web driver (in DSL style)<br></li>
</ul><br>

<b><i>Project setup:</i></b>
To start project you need:
<ol>
<li>java8, Maven, MSSQL Server<br></li>
<li>in MSSQL create database "Library" (to run test also "testLibrary")<br></li>
<li>by default it's used windows authentication to MSSQL Server (otherwise you need to specify username and password in configuration file)<br></li>
<li>make .war (without environment: "mvn war:war" command if via maven comman line)<br></li>
<li>next you need to download and inject jdbc MSSQL driver to output .war artifact (WEB-INF/lib). Or you can install it to your local Maven repository and inject it via maven (uncomment last dependancy in pom.xml) and just build<br></li>
<li>finally deploy your .war to needed web server (Tomcat)<br></li>
</ol>
