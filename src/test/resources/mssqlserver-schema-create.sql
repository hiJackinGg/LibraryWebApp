
use testLibrary;


IF EXISTS (SELECT *
  FROM sys.foreign_keys
   WHERE object_id = OBJECT_ID('dbo.FK_LB')
   AND parent_object_id = OBJECT_ID('dbo.Lib_log')
)
ALTER TABLE Lib_log DROP CONSTRAINT FK_LB;

IF EXISTS (SELECT *
  FROM sys.foreign_keys
   WHERE object_id = OBJECT_ID('dbo.FK_AUTHOR_BOOK')
   AND parent_object_id = OBJECT_ID('dbo.Books_Authors')
)
ALTER TABLE Books_Authors DROP CONSTRAINT FK_AUTHOR_BOOK;

IF EXISTS (SELECT *
  FROM sys.foreign_keys
   WHERE object_id = OBJECT_ID('dbo.FK_BOOK_AUTHOR')
   AND parent_object_id = OBJECT_ID('dbo.Books_Authors')
)
ALTER TABLE Books_Authors DROP CONSTRAINT FK_BOOK_AUTHOR;


IF object_id('dbo.Books', 'U') IS NOT NULL
DROP TABLE dbo.Books;
create table dbo.Books (
   id          int			        not null identity(1,1),
   title       varchar(100)         null,
   quantity    int				          null check(quantity >= 0),
   constraint PK_BOOKS primary key (id)
)


IF object_id('dbo.Authors', 'U') IS NOT NULL
DROP TABLE dbo.Authors;
create table dbo.Authors (
   id            int			        not null identity(1,1),
   name          varchar(100)         null
   constraint PK_AUTHORS primary key (id)
)


IF object_id('dbo.Books_Authors', 'U') IS NOT NULL
DROP TABLE dbo.Books_Authors;
create table dbo.Books_Authors (
	id			    int	      		       not null	identity(1,1),
	bookID		  int                  not null,
	authorID	  int                  not null,
	constraint PK_BA primary key (id),
	constraint FK_BOOK_AUTHOR foreign key (BookID) references Books (id),
	constraint FK_AUTHOR_BOOK foreign key (AuthorID) references  Authors (id)

)


IF object_id('dbo.Lib_log', 'U') IS NOT NULL
DROP TABLE dbo.Lib_log;
create table dbo.Lib_log (
   id            int							    not null identity(1,1),
   email         varchar(100)					not null,
   bookID        int							    not null,
   [date]		     date	   default convert(date, getdate())					      not null
   constraint PK_LL primary key (id),
   constraint FK_LB foreign key (bookID) references Books (id)
)


IF EXISTS(SELECT 1
          FROM   INFORMATION_SCHEMA.ROUTINES
          WHERE  ROUTINE_NAME = 'sp_attachAuthorToBook'
          )
  BEGIN
      DROP PROCEDURE sp_attachAuthorToBook
  END


  INSERT INTO Books(title, quantity)
  VALUES('The Golden Calf', 0);
  INSERT INTO Books(title, quantity)
  VALUES('The Old Man and the Sea', 0);
  INSERT INTO Books(title, quantity)
  VALUES('The Metamorphosis', 2);
  INSERT INTO Books(title, quantity)
  VALUES('For Whom the Bell Tolls', 3);


  INSERT INTO Authors(name)
  VALUES('I.Ilf');
  INSERT INTO Authors(name)
  VALUES('Y.Petrov');
    INSERT INTO Authors(name)
  VALUES('E.Hemingway');
    INSERT INTO Authors(name)
  VALUES('F.Kafka');


    INSERT INTO Books_Authors(bookID, authorID)
  VALUES(1, 1);
      INSERT INTO Books_Authors(bookID, authorID)
  VALUES(1, 2);
    INSERT INTO Books_Authors(bookID, authorID)
  VALUES(2, 3);
      INSERT INTO Books_Authors(bookID, authorID)
  VALUES(3, 4);
      INSERT INTO Books_Authors(bookID, authorID)
  VALUES(4, 3);

    INSERT INTO Lib_log(email, bookID, date)
  VALUES('address1@gmail.com', 1, '2017-01-25');
    INSERT INTO Lib_log(email, bookID, date)
  VALUES('address2@gmail.com', 4, '2017-01-25');
    INSERT INTO Lib_log(email, bookID, date)
  VALUES('address2@gmail.com', 2, '2017-01-25');
    INSERT INTO Lib_log(email, bookID, date)
  VALUES('address3@yandex.com', 1, '2017-01-25');


