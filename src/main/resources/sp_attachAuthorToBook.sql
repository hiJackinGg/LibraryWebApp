
use Library;

create PROCEDURE sp_attachAuthorToBook
  @bookId int,
  @authorName varchar(20)
AS
declare @authorId int
select @authorId = id from Authors where name = @authorName

IF (@authorId is null)
BEGIN
INSERT INTO Authors (name)
VALUES (@authorName)
SELECT @authorId = SCOPE_IDENTITY()
  END

insert into Books_Authors (bookID, authorID)
values (@bookId, @authorId)

--select @authorId
