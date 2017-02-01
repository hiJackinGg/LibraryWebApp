// example of json with data for books table
var tableData = '{'+
    '"totalRows": 9,'+
    '"pageNumber": 2,'+
    '"pageSize": 3,'+
    '"totalPages": 3,'+
    '"isAuthorized": false,'+
    '"books": [{'+

    '"id": 1,'+
    '"title": "Java programming",'+
    '"quantity": 3,'+
    '"authors": ["Hortsman", "Cornel"]'+
    '},'+
    '{'+

    '"id": 2,'+
    ' "title": "For whom the bell tolls",'+
    ' "quantity": 3,'+
    ' "authors": ["Hemigway"]'+
    '}'+
    ']'+
    '}';

// example of json with data for book details table
var detailsData = '{'+
    '"details": [{'+
    '"date": "2014-02-03",'+
    '"email": "adddddsd@yandex.ru"'+
    '}, {'+
    '"date": "2010-08-03",'+
    '"email": "212asd@yandex.ru"'+
    '}, {'+
    '"date": "2000-01-22",'+
    '"email": "a11sd@yandex.ru"'+
    '}]'+
    '}';


var grid = {
    pageNumber: 0,  
    pageSize: 3,   
    fieldSortBy: 'title',
    asc: true
}

var inputs = [];        //author input fields


$(function () {
    //primary loading of books from server (loads first page)
    $.ajax({
        method: "GET",
        url: "/getBooks",
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        success: function (data) {
            buildLibraryTableFromJSON(data);
        }
    });

    //append new author field to input new author for book
    $("#appendAuthor").click(function () {
        
        input = document.createElement('input');
        $(input).attr('id', 'authors')
                .attr('name', 'authors')
                .attr('list', 'authorList')
                .attr('type', 'text')
                .attr('placeholder', 'Author name');
        inputs.push(input);

        var i = inputs.indexOf(input);
        
        removeInput = document.createElement('button'); //add button to remove input field
        $(removeInput).attr('type', 'button')
                      .attr('id', i)
                      .append('-')
                      .append('<br/>');
        inputs.push(removeInput);

        $(removeInput).click(function () {
            var index = $(this).attr('id');     //button id is equal to array index with element to be removed 
            $(inputs[index]).remove();
            $(this).remove();
            delete inputs[index];
            //inputs.splice(index, 1);
        });

        $("#authorsDiv").prepend(input);     
        $(input).after(removeInput);

    });

    
    //displays form for book adding
    $("#add").click(function (e) {
        e.preventDefault();
        $("#bookDiv").show();
    });
    //hides book form
    $("#book #cancel").click(function () {
        $("#bookDiv").hide();
    });

    $("#book").submit(function (e) {
        $.post("/addBook",
            $('#book').serialize(),
            function (data) {
                buildLibraryTableFromJSON(data);
                grid.pageNumber = 0;
                grid.pageSiz = 3;
                grid.fieldSortBy = 'title';
                grid.asc = true;
            });

        e.preventDefault();
        $("#bookDiv").hide();

        //clear suplementary fields
        while (inputs.length > 0) {
            var inputField = inputs.pop();
            $(inputField).remove();
        }

        this.reset();

    });


    //navigate to next table page
    $("#next").click(function () {

        grid.pageNumber++;

        updateGrid();
    });

    //navigate to previous table page
    $("#prev").click(function () {
        if (grid.pageNumber <= 0) {
            return;
        }
        else {
            grid.pageNumber--;
        }

        updateGrid();
    });

    //table sorting
    window.sort = function (field) {
        grid.pageNumber = 0;
        grid.fieldSortBy = field;
        grid.asc = !grid.asc;
        updateGrid();

    }

    //table filtering
    $('#filter').change(function (e) {
        grid.pageNumber = 0;
        updateGrid();

    });

    //ajax update of books in table with new required parameters
    window.updateGrid = function () {      
        $.ajax({
            type: "GET",
            url: "/getBooks",
            data: {
                'filter': $('#filter').val(),
                'pageNumber': grid.pageNumber,
                'fieldSortBy': grid.fieldSortBy,
                'sortASC': grid.asc
            },
            dataType: "json",
            success: function (data) {
                buildLibraryTableFromJSON(data);
            },
            error: errorFunc
        });
    }


    //login
    $('#login').click(function () {

        var email = $('#email').val();

        var regex = /\S+@\S+\.\S+/;
        if (regex.test(email) == false) {
            alert("The email you've entered is wrong.");
            $('#email').val('');
            return;
        }

        $.ajax({
            type: "GET",
            url: "/rememberUser",
            data: { 
                'email': email
            },
            success: function (data, status) {
                location.reload(true);
            },
            error: errorFunc
        });
    });


    //function to handle errors from the server
    function errorFunc(xhr, ajaxOptions, throwError) {
        switch (xhr.status) {
            case 400: alert("Client error." + xhr.responseText); break;
            case 403: alert("Log in, please. Input your email."); location.reload(true); break;
            case 500: alert("Internal server error. Try again later."); break;
        }
    }

    //Searches and returns the element with attribute name = 'attrName' and value 'attrVal' in the row which contains 'e' element.
    function findElement(e, attrName, attrVal) {

        var tableRow = e;

        //searches parent <tr> tag of 'e' element
        while (tableRow.nodeName !== "TR") {
            tableRow = tableRow.parentNode;
        }

        var elems = tableRow.getElementsByTagName("*");
        var elemToFind;

        for (var i = 0; i < elems.length; i++) {
            var el = elems[i];

            if (el.nodeType == 1 && el.getAttribute(attrName) == attrVal) {
                elemToFind = el;
                break;
            }
        }

        return elemToFind;
    }


    window.takeBook = function (e, bookId) {        //'e' - element which intended event (button)

        var qtyElem = findElement(e, "class", 'quantity');
        var qtyElemOldVal = +qtyElem.innerHTML;
        var qtyElemNewVal = qtyElemOldVal > 0 ? qtyElemOldVal - 1 : 0;

        $.ajax({
            type: "POST",
            url: "/takeBook",
            data: {
                'bookId': bookId
            },
            success: function () {
                if (qtyElemNewVal <= 0) {
                    e.setAttribute("disabled", "true");
                }
                qtyElem.innerHTML = qtyElemNewVal;      //changing book quantity after been taken
                alert("You have taken the book. Notification was sent to your email.");
            },
            error: errorFunc
            
        });

    }

    //allow to edit new quantity value in appropriate cell of the row and save changes.
    window.changeQuantity = function (e, bookId) {

        var qtyElem = findElement(e, "class", 'quantity');
        var qtyElemOldVal = qtyElem.innerHTML;

        qtyElem.setAttribute('contenteditable', true);

        var saveButton = document.createElement("Button");
        var node = document.createTextNode("Save changes");
        saveButton.appendChild(node);
        saveButton.setAttribute('class', 'button-saveChanges');
        saveButton.addEventListener("click", function () {
            saveButton.remove();
            e.style.display = "inline-block";
            qtyElem.setAttribute('contenteditable', false);

            var qtyElemNum = +qtyElem.innerHTML;

            if (isNaN(qtyElemNum) || qtyElemNum !== parseInt(qtyElemNum, 10) || qtyElemNum < 0) {
                alert("Incorrect book quantity value: " + qtyElem.innerHTML);
                qtyElem.innerHTML = qtyElemOldVal;
                return;
            }

            $.ajax({
                type: "POST",
                url: "/changeQuantity",
                data: {
                    'quantity': qtyElem.innerHTML,
                    'bookId': bookId
                },
                success: function (){
                    var qtyElemVal = +qtyElem.innerHTML;
                    var takeBookElem = findElement(e, "class", 'button-take'); 

                    if (takeBookElem != null) {

                        if (qtyElemVal > 0) {
                            takeBookElem.removeAttribute('disabled');
                        }
                        else {
                            takeBookElem.setAttribute('disabled', true);
                        }

                    }
                },
                error: function (xhr, ajaxOptions, throwError) {
                    qtyElem.innerHTML = qtyElemOldVal;
                    switch (xhr.status) {
                        case 400: alert("Client error." + xhr.responseText); break;
                        case 500: alert("Internal server error. Try again later."); break;
                    }
                }
            });

        })

        e.after(saveButton);
        e.style.display = "none";
        qtyElem.focus();

    }

    //get book details
    window.bookDetails = function (bookId) {

        $.ajax({
            type: "GET",
            url: "/bookDetails",
            data: {
                'bookId': bookId
            },
            success: function (data) {
                buildDetailsTableFromJSON(data);
            },
            error: errorFunc
        });
    }

    //removes the book with specified id
    window.deleteBook = function (bookId) {

        $.ajax({
            type: "POST",
            url: "/deleteBook",
            data: {
 //               'rowsInPage': rowsInPage.value,
                'bookId': bookId
            },
            success: function (data) {
                buildLibraryTableFromJSON(data);
                grid.pageNumber = 0;
                grid.pageSiz = 3;
                grid.fieldSortBy = 'title';
                grid.asc = true;
            },
            error: errorFunc
        });
    }
    
});


//function to parse JSON received from the server and display into books table
//JSON contains information about table parameters (number of rows, pages, currrent page...) and books
function buildLibraryTableFromJSON(data){

    ///////remove tbody container if exists
    var elem = document.querySelector('#booksGrid tbody');
    if (elem != null)
        elem.parentNode.removeChild(elem);
    /////////////////////////////////

    var num = data.pageNumber * data.pageSize;

    var table = document.getElementById( 'booksGrid' );
    var tbody = document.createElement('tbody');

    data.books.forEach(function(book) {
        num++;

        var tr = document.createElement('tr');

        var td1 = document.createElement('td');
        td1.setAttribute('rowspan', book.authors.length);

        var td2 = document.createElement('td');
        td2.setAttribute('rowspan', book.authors.length);

        var td3 = document.createElement('td');
        td3.setAttribute('rowspan', book.authors.length);
        td3.setAttribute('class', 'quantity');

        var td4 = document.createElement('td');

        var button1 = document.createElement('button');
        var bText1 = document.createTextNode("Details");
        button1.setAttribute('onclick', 'bookDetails('+book.id+')');
        button1.setAttribute('class', 'button-details');
        button1.appendChild(bText1);
        var button2 = document.createElement('button');
        var bText2 = document.createTextNode("Remove book");
        button2.setAttribute('onclick', 'deleteBook('+book.id+')');
        button2.setAttribute('class', 'button-remove');
        button2.appendChild(bText2);
        var button3 = document.createElement('button');
        var bText3 = document.createTextNode("Change qty");
        button3.setAttribute('onclick', 'changeQuantity(this, '+book.id+')');
        button3.setAttribute('class', 'button-edit');
        button3.appendChild(bText3);
        var button4 = document.createElement('button');
        var bText4 = document.createTextNode("Take book");
        button4.setAttribute('onclick', 'takeBook(this, '+book.id+')');
        button4.setAttribute('class', 'button-take');
        if (book.quantity <= 0)
            button4.setAttribute('disabled', 'true');
        button4.appendChild(bText4);

        var text1 = document.createTextNode(num);
        var text2 = document.createTextNode(book.title);
        var text3 = document.createTextNode(book.quantity);
        var text4 = document.createTextNode(book.authors[0]);

        td1.appendChild(text1);
        td2.appendChild(text2);
        td3.appendChild(text3);
        td4.appendChild(text4);

        var td5 = document.createElement('td');
        td5.setAttribute('rowspan', book.authors.length);
        td5.setAttribute('class', 'buttons');
        td5.appendChild(button1);
        td5.appendChild(button2);
        td5.appendChild(button3);
        if (data.authorized == true) {
            td5.appendChild(button4);
        }

        tr.appendChild(td1);
        tr.appendChild(td2);
        tr.appendChild(td3);
        tr.appendChild(td4);
        tr.appendChild(td5);

        tbody.appendChild(tr);

        for (var i = 1; i < book.authors.length; i++) {
            var tr0 = document.createElement('tr');
            var td0 = document.createElement('td');
            var tx0 = document.createTextNode(book.authors[i]);
            td0.appendChild(tx0);
            tr0.appendChild(td0);
            tbody.appendChild(tr0);
        }

    });

    table.appendChild(tbody);

    var currentPage = data.totalPages == 0 ? 0 : data.pageNumber + 1;
    var totalPages = data.totalPages;
    var stopPrev = data.pageNumber <= 0;
    var stopNext = currentPage >= totalPages;

    var prevButton = document.querySelector('#booksGrid tfoot #prev');
    if (stopPrev)
        prevButton.setAttribute('disabled', 'true');
    else
        prevButton.removeAttribute('disabled');

    var pageCount = document.querySelector('#booksGrid tfoot #pageCount');
    pageCount.innerHTML = currentPage + '/' + totalPages;

    var nextButton = document.querySelector('#booksGrid tfoot #next');
    if (stopNext)
        nextButton.setAttribute('disabled', 'true');
    else
        nextButton.removeAttribute('disabled');

}

//function to parse JSON received from the server and display into book details table
function buildDetailsTableFromJSON(data){

    ///////remove tbody container if exists
    var elem = document.querySelector('#detailsGrid tbody');
    if (elem != null)
        elem.parentNode.removeChild(elem);
    /////////////////////////////////

    var table = document.getElementById( 'detailsGrid' );
    var tbody = document.createElement('tbody');

    data.details.forEach(function(detail) {

        var tr = document.createElement('tr');

        var td1 = document.createElement('td');

        var td2 = document.createElement('td');

        var text1 = document.createTextNode(detail.key);
        var text2 = document.createTextNode(detail.value);

        td1.appendChild(text1);
        td2.appendChild(text2);

        tr.appendChild(td1);
        tr.appendChild(td2);
        tbody.appendChild(tr);

    });

    table.appendChild(tbody);
}
