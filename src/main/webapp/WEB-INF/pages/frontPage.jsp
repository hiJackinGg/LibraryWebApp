<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
    <head>
        <title>Library</title>
        <script src="resources/jquery-1.7.1.min.js"></script>
        <script src="resources/index.js"></script>
        <link rel="stylesheet" type="text/css" href="resources/css/style.css" />
    </head>

<body>

    <div id="grid">
        <div id="bookDiv">
            <form  action="/addBook" id="book" method="post">
                <h3>Book Form</h3>
                <hr/><br/>
                <label>Title: <span>*</span></label><br/>
                <input required type="text" id="Title" name="Title" placeholder="Book title"/><br/>
                <br/>
                <label>Author:</label><br/>
                <div id="authorsDiv">
                <input type="text" id="authors" name="authors" placeholder="Author name"/> 
                <button type="button" id="appendAuthor">+</button><br/>
                </div>
                <br/>
                <label>Quantity:</label><br/>
                <input type="number" id="Quantity" name="Quantity" min="0" value="1" /><br/>         
                <br/>
                <input  type="submit" value="Send"/>
                <input type="button" id="cancel" value="Cancel"/>
                <br/>
            </form>
        </div>

        <div>
            <b>Filter by:</b>
            <select id="filter">
                <option selected value="1">all</option>
                <option  value="2">in stock</option>
                    <% if (session.getAttribute("user_email") != null) { %>
                <option  value="3">Taken books</option>
                    <% }%>

            </select>
                <a id="add" href="Home/AddBook">Add new book</a>
        </div><br>


        <div id="container">
            <table id="booksGrid" class="display" cellspacing="0">
                <thead>
                    <tr>
                        <th>№</th>
                        <th><a class="sortLink" href="" id="sortByTitle" onclick="sort('title');return false;">Book title</a></th>
                        <th>Quantity</th>
                        <th><a class="sortLink" href="" id="sortByAuthor" onclick="sort('author');return false;">Authors</a></th>
                    </tr>
                </thead>

                <%--<tbody> ...ajax... </tbody>--%>

                <tfoot>
                    <tr>
                        <td colspan = "4">
                            <button disabled id="prev" type="button" >Back</button>
                            <span id = "pageCount">0/0</span>
                            <button disabled id="next" type="button" >Next</button>
                        </td>
                    </tr>
                </tfoot>
            </table>
        </div><br>


        <div>
            <label>Register yourself:</label><br>
            <input id="email" type="email" required/>
            <button id="login" type="button" >Log in</button>
         </div><br />


        <div id="loginSuccess">
            <% if (session.getAttribute("user_email") != null) { %>
            <b>Your email is: </b> <%= session.getAttribute("user_email")%> <br><span>Now you can take books.</span>
            <% }%>
        </div>


    </div>


    <div id="note">
        <p><b>Details:</b></p>
        <div id="details">
            <table id="detailsGrid" class="display" cellspacing="0">
                <thead>
                <tr>
                    <th>Date</th>
                    <th>Email</th>
                </tr>
                </thead>

            </table>
        </div>
    </div>

    </body>
</html>
