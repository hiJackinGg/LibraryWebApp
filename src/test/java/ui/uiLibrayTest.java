package ui;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.*;
import java.util.*;
import static java.lang.Thread.sleep;
import static org.junit.Assert.*;


public class uiLibrayTest {

    private static final String USER_EMAIL = "unname@gmail.com";

    static{
        System.setProperty("webdriver.chrome.driver", "D:\\soft\\chromedriver_win32\\chromedriver.exe");
    }

    private WebDriver driver;
    private Wait<WebDriver> wait;
    private static final String BASE_URL = "http://localhost:8080/";

    @Before
    public void setUp() throws Exception {

        driver = new ChromeDriver();
        driver.navigate().to(BASE_URL);
        wait = new WebDriverWait(driver, 10);

    }

    @After
    public void endTest() {
        driver.quit();
    }


    @Test
    public void testInitialDataLoaded(){

        By prevBtn = By.id("prev");
        By nextBtn = By.id("next");
        By pageCount = By.id("pageCount");
        By filter = By.cssSelector("#filter option[value=\"1\"]");

        assertNotNull(driver.findElement(prevBtn));
        assertNotNull(driver.findElement(nextBtn));
        assertFalse(driver.findElement(prevBtn).isEnabled());
        assertEquals("0/0", driver.findElement(pageCount).getText());
        assertTrue(driver.findElement(filter).isSelected());
    }

    @Test
    public void testAuthorisation(){

        String loginSuccessTxt = "Your email is: unname@gmail.com";

        login(USER_EMAIL);
        By div = By.id("loginSuccess");
        By takeBookBtn = By.className("button-take");
        By takenBooksOpt = By.cssSelector("#filter option[value=\"3\"]");

        wait.until(ExpectedConditions
                .presenceOfElementLocated(takeBookBtn));

        assertTrue(driver.findElement(div).getText().contains(loginSuccessTxt));
        assertTrue(driver.findElement(takeBookBtn).isDisplayed());
        assertTrue(driver.findElement(takenBooksOpt).isDisplayed());
    }

    @Test
    public void testAddBook(){
        loadTableDataAjax();

        List<BookRow> tableRowsBefore = getAllTableRows(true);
        BookRow newRow = addBook("newTestBook");
        List<BookRow> tableRowsAfter = getAllTableRows(true);

        assertEquals(tableRowsBefore.size() + 1, tableRowsAfter.size());
        assertTrue(tableRowsAfter.contains(newRow));
    }

    @Test
    public void testRemoveBook(){
        loadTableDataAjax();

        BookRow newRow = addBook("newTestBookToRemove");
        List<BookRow> tableRowsBefore = getAllTableRows(true);
        actionWithRow(newRow, BookRow.ActionWithRow.REMOVE, true);
        List<BookRow> tableRowsAfter = getAllTableRows(true);

        assertEquals(tableRowsBefore.size() - 1, tableRowsAfter.size());
        assertFalse(tableRowsAfter.contains(newRow));
    }

    @Test
    public void testSortByTitle(){
        loadTableDataAjax();

        List<BookRow> tableRowsBefore = getAllTableRows(false);
        sortTableByTitle();
        List<BookRow> tableRowsAfter = getAllTableRows(false);

        Collections.reverse(tableRowsAfter);

        assertEquals(tableRowsBefore, tableRowsAfter);

    }

    @Test
    public void testChangeQuantity(){

        final int book_quanity = 1;

        loadTableDataAjax();

        BookRow row = addBook("newTestBookToChangeQty");

        actionWithRow(row, BookRow.ActionWithRow.CHANGE_QUANTITY, false);
        changeQuantityCell(row, book_quanity, false);
        actionWithRow(row, BookRow.ActionWithRow.SAVE_CHANGES, false);

        List<BookRow> tableRows = getAllTableRows(false);
        row.setQuantity(book_quanity);

        assertTrue(tableRows.contains(row));

    }

    @Test
    public void testTakeBook(){

        loadTableDataAjax();

        BookRow row = addBook("newTestBookToTake");

        login(USER_EMAIL);

        actionWithRow(row, BookRow.ActionWithRow.TAKE_BOOK, false);
        acceptAlert();
        filterBooks(3);

        List<BookRow> tableRows = getAllTableRows(false);

        row.reduceQuantityByOne();

        assertTrue(tableRows.contains(row));

    }

    /**
     * Reloads page and displays first page of table.
     */
    private void reloadPage(){
        int pageSize = getPageSize();
        String pageCountAfter = 1 + "/" + pageSize;

        driver.get(BASE_URL);
        wait.until(ExpectedConditions.textToBe(By.id("pageCount"), pageCountAfter));
    }

    /**
     * Makes authorisation to take books.
     * @param email
     */
    private void login(String email){
        driver.findElement(By.id("email")).sendKeys(email);
        driver.findElement(By.id("login")).click();

        pause();
    }

    /**
     * Waits until initial data will be loaded.
     */
    private void loadTableDataAjax(){
        wait.until(ExpectedConditions
                .presenceOfElementLocated(By.cssSelector("#booksGrid tbody")));
    }

    /**
     * Navigate to next page of the table.
     */
    private void nextPageUpdate(){

        int pageNumber = getPageNumber();
        int pageSize = getPageSize();
        String pageCountAfter = ++pageNumber + "/" + pageSize;

        driver.findElement(By.id("next")).click();
        wait.until(ExpectedConditions.textToBe(By.id("pageCount"), pageCountAfter));
    }

    private int getPageNumber(){
        String rowCountValue = driver.findElement(By.id("pageCount")).getText();
        return Character.getNumericValue(rowCountValue.charAt(0));
    }

    private int getPageSize(){
        String rowCountValue = driver.findElement(By.id("pageCount")).getText();
        return Character.getNumericValue(rowCountValue.charAt(2));
    }

    private String getPageCount(){
        By rowCountSpan= By.id("pageCount");
        return driver.findElement(rowCountSpan).getText();
    }

    /**
     * Navigates the table pages to collect all book rows.
     * @param toReloadPage true if it's needed to reload page and navigate from first page table.
     * @return List of book rows contained by table (in all pages).
     */
    private List<BookRow> getAllTableRows(boolean toReloadPage){

        By nextBtn = By.id("next");

        List<BookRow> rows = new LinkedList<>();

        if (toReloadPage)
            reloadPage();

        while(true){
            rows.addAll( getTableRowsInPage() ) ;

            if (!driver.findElement(nextBtn).isEnabled()){
                break;
            }

            nextPageUpdate();
        }

        return rows;
    }

    /**
     * Collects all table rows in one page.
     * @return List of book rows contained by table (in current page).
     */
    private List<BookRow> getTableRowsInPage(){
        List<BookRow> rows = new ArrayList<>(3);

        List<WebElement> tableRows = driver.findElements(By.cssSelector("#booksGrid tbody tr"));

        BookRow row = null;

        for (WebElement tr : tableRows){
            List<WebElement> tableCols = tr.findElements(By.cssSelector("td"));
            if (tableCols.size() > 1) {
                row = new BookRow();
                row.setTitle(tableCols.get(1).getText());
                row.setQuantity(Integer.parseInt(tableCols.get(2).getText()));
                row.getAuthors().add(tableCols.get(3).getText());
                rows.add(row);
            }
            else {
                row.getAuthors().add(tableCols.get(0).getText());
            }
        }

        return rows;
    }

    /**
     * Adds new book in table.
     * @param bookTitle book title
     * @return ui.BookRow object of inserted book.
     */
    private BookRow addBook(String bookTitle){

        String title = bookTitle;
        Integer quantity = 3;
        Queue<String> authors = new LinkedList<>(Arrays.asList("author1", "author2"));

        BookRow newRow = new BookRow();
        newRow.setTitle(title);
        newRow.setQuantity(quantity);
        newRow.setAuthors(new ArrayList<>(authors));

        driver.findElement(By.id("add")).click();
        driver.findElement(By.id("appendAuthor")).click();
        driver.findElement(By.id("Title")).sendKeys(title);
        driver.findElement(By.id("Quantity")).clear();
        driver.findElement(By.id("Quantity")).sendKeys(quantity.toString());

        List<WebElement> elemets = driver.findElements(By.id("authors"));
        elemets.forEach(elem -> elem.sendKeys(authors.poll()));
        driver.findElement(By.cssSelector("input[type=\"submit\"]")).click();

        pause();

        return newRow;
    }

    /**
     * Performs needed action with table rows.
     * Action types are described in ui.BookRow.ActionWithRow object.
     * @param row the action to be performed with.
     * @param action to be done with the row (book). For ex.: Take book, remove, change.
     * @param toReloadPage
     */
    private void actionWithRow(BookRow row, BookRow.ActionWithRow action, boolean toReloadPage){//[contains(.,'Heart of a Dog')]. id('booksGrid')/tbody/tr/td" .//td[text() = 'Heart of a Dog']  /td[last()]/button[@class='button-remove']

        String xpathTRtoFind = ".//table[@id='booksGrid']/tbody/tr[contains(.,'%s')]";
        String xpathActionButtonToFind = ".//td[last()]/button[@class='%s']";

        if (toReloadPage)
            reloadPage();

        WebElement actionButton = null;

        while(true){

            try {
                WebElement tr = driver.findElement(By.xpath(String.format(xpathTRtoFind, row.getTitle())));
                actionButton = tr.findElement(By.xpath(String.format(xpathActionButtonToFind, action)));
            }
            catch (NoSuchElementException e){
                nextPageUpdate();
            }
            if (!driver.findElement(By.id("next")).isEnabled() || actionButton != null){
                break;
            }

        }

        actionButton.click();

        pause();
    }

    /**
     * Changes value of the cell quantity in table.
     * @param row
     * @param quantity
     * @param toReloadPage
     */
    private void changeQuantityCell(BookRow row, int quantity, boolean toReloadPage){
        String xpathTRtoFind = ".//table[@id='booksGrid']/tbody/tr[contains(.,'%s')]";
        String xpathTDquantityFind = ".//td[@class = 'quantity']";

        if (toReloadPage)
            reloadPage();

        WebElement cellQuantity = null;

        while(true){

            try {
                WebElement tr = driver.findElement(By.xpath(String.format(xpathTRtoFind, row.getTitle())));
                cellQuantity = tr.findElement(By.xpath(xpathTDquantityFind));
            }
            catch (NoSuchElementException e){
                nextPageUpdate();
            }
            if (!driver.findElement(By.id("next")).isEnabled() || cellQuantity != null){
                break;
            }

        }

        cellQuantity.clear();
        cellQuantity.sendKeys(String.valueOf(quantity));

        pause();

    }

    private void sortTableByTitle(){
        driver.findElement(By.id("sortByTitle")).click();
        pause();

    }

    /**
     *
     * @param optionVal value of <option> tag (<option value="optionVal">)
     */
    private void filterBooks(int optionVal){
        By filter = By.cssSelector(String.format("#filter option[value=\"%d\"]", optionVal));
        driver.findElement(filter).click();
        pause();

    }

    /**
     * Make pause to ensure that previous action is completed.
     */
    private void pause(){
        try {
            sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void acceptAlert(){
        driver.switchTo().alert().accept();
        pause();
    }
}

