package com.ems.it;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public final class EmsIntegrationTest {

    private static WebDriver driver;
    private static final String BASE_URL = "http://localhost:8080/login";
    private static final int SLEEP_TIME = 3000;

    @BeforeClass
    public static void setUp() {
        populateDatabase();
        driver = new ChromeDriver();
        driver.get(BASE_URL);
    }

    private static void populateDatabase() {
        final Optional<Connection> optional = ConnectionManager.getConnection();

        final ResourceDatabasePopulator databasePopulator = new ResourceDatabasePopulator();
        databasePopulator.addScript(new ClassPathResource("scripts/setup.sql"));

        optional.ifPresent(connection -> executeScript(databasePopulator, connection));


    }

    private static void executeScript(ResourceDatabasePopulator databasePopulator, Connection connection) {
        databasePopulator.populate(connection);

        try {
            connection.close();
        } catch (final SQLException problemClosing) {
            System.out.println("SQL connection did not properly close");
        }
    }

    @Test
    public void a_shouldDoSuccessfulLogin() {
        final WebElement usernameInput = driver.findElement(By.id("j_username"));
        final WebElement passwordInput = driver.findElement(By.id("j_password"));

        final String emsUsername = "john.smith";
        final String emsPassword = "password";

        usernameInput.sendKeys(emsUsername);
        passwordInput.sendKeys(emsPassword);

        final WebElement loginButton = driver.findElement(By.id("j_idt17"));
        loginButton.click();

        final List<WebElement> images = driver.findElements(By.tagName("img"));

        Optional<String> loggedIn = images.stream()
                .map(element -> element.getAttribute("src"))
                .filter(Objects::nonNull)
                .filter(attribute -> attribute.contains("home.jpg"))
                .findFirst();

        assertTrue("User is not logged in", loggedIn.isPresent());

    }

    @Test
    public void b_shouldHaveBonus() {
        driver.get("http://localhost:8080/pages/my-bonuses.xhtml");

        final WebElement bonusList = driver.findElement(By.id("form:bonusList_list"));

        final List<WebElement> bonusListContent = bonusList.findElement(By.tagName("tbody"))
                .findElement(By.tagName("tbody"))
                .findElements(By.tagName("tr"));


        final List<Tuple2<String>> contents = bonusListContent.stream()
                .map(this::extractTuple)
                .collect(Collectors.toList());

        final String expectedProposedBy = "Smith John";
        final String expectedBonusType = "???Performance???";
        final float expectedBonusValue = 234.56f;

        assertEquals("Not expected proposed by", expectedProposedBy, contents.get(0).getY());
        assertEquals("Not expected bonus type", expectedBonusType, contents.get(1).getY());
        assertEquals("Not expected bonus value", expectedBonusValue, Float.parseFloat(contents.get(2).getY()), 0.0002f);

        sleepForAWhile(SLEEP_TIME);

        driver.navigate().back();

    }

    private Tuple2<String> extractTuple(WebElement webElement) {
        final List<WebElement> tds = webElement.findElements(By.tagName("td"));

        final String first = tds.get(0).getText();
        final String second = tds.get(1).getText();

        return new Tuple2<>(first, second);
    }

    @Test
    public void c_shouldHaveSalary() {
        driver.get("http://localhost:8080/pages/my-paid-salaries.xhtml");

        final WebElement salaryTable = driver.findElement(By.id("form:j_idt68:j_idt70:0:j_idt72_content"));

        final List<WebElement> salaries = salaryTable.findElement(By.tagName("table"))
                .findElement(By.tagName("tbody"))
                .findElements(By.tagName("tr"));

        assertEquals("Not expected number of payed salaries", 1, salaries.size());

        List<String> salariesEntries = salaries.get(0)
                .findElements(By.tagName("td"))
                .subList(1, 3).stream()
                .map(WebElement::getText)
                .map(string -> string.split(": ")[1])
                .collect(Collectors.toList());

        final String expectedPayDay = "24/03/2018";
        final float expectedPayedSalary = 11000.98f;

        assertEquals("Not expected pay day", expectedPayDay, salariesEntries.get(0));
        assertEquals("Not expected payed salary", expectedPayedSalary, Float.parseFloat(salariesEntries.get(1)), 0.0002f);

        sleepForAWhile(SLEEP_TIME);

        driver.navigate().back();
    }

    @Test
    public void d_shouldHaveDocuments() {
        driver.get("http://localhost:8080/pages/my-documents.xhtml");

        final List<WebElement> divs = driver.findElements(By.tagName("div"));

        final List<WebElement> documentsList = divs.stream()
                .filter(webElement -> webElement.getAttribute("class") != null)
                .filter(webElement -> webElement.getAttribute("class").equals("ui-carousel-viewport"))
                .findFirst()
                .map(webElement -> webElement.findElement(By.tagName("ul")))
                .filter(webElement -> webElement.getAttribute("class") != null)
                .filter(webElement -> webElement.getAttribute("class").equals("ui-carousel-items"))
                .map(webElement -> webElement.findElements(By.tagName("li")))
                .orElse(null);

        assertNotNull("Not expected null value", documentsList);
        assertEquals("Not expected number of documents", 1, documentsList.size());

        final String expectedDocumentName = "Previous employment";
        final String actualDocumentName = documentsList.get(0).getText().split("\n")[0];

        assertEquals("Not expected document name", expectedDocumentName, actualDocumentName);

        sleepForAWhile(SLEEP_TIME);

        driver.navigate().back();
    }

    @Test
    public void e_shouldDoSuccessfulLogout() {
        final List<WebElement> spans = driver.findElements(By.tagName("span"));

        spans.stream()
                .filter(webElement -> webElement.getAttribute("class") != null)
                .filter(webElement -> webElement.getAttribute("class").equals("ui-button-text ui-c"))
                .filter(webElement -> webElement.getText().equals("Options"))
                .findFirst()
                .ifPresent(WebElement::click);

        final String logoutOnClickIdentifier
                = "PrimeFaces.addSubmitParam('j_idt11',{'j_idt11:j_idt66':'j_idt11:j_idt66'}).submit('j_idt11');return false;";

        final List<WebElement> as = driver.findElements(By.tagName("a"));

        as.stream()
                .filter(webElement -> webElement.getAttribute("onclick") != null)
                .filter(webElement -> webElement.getAttribute("onclick").equals(logoutOnClickIdentifier))
                .findFirst()
                .ifPresent(WebElement::click);

        try {
            final WebElement loginButton = driver.findElement(By.id("j_idt17"))
                    .findElement(By.tagName("span"));
            assertEquals("No login button found", "Log in", loginButton.getText());
        } catch (final NoSuchElementException noElementFound) {
            fail("Could not properly logout");
        }
    }

    private void sleepForAWhile(int millis) {
        try {
            Thread.sleep(millis);
        } catch (final InterruptedException interrupted) {
            fail("Thread Exception encountered");
        }
    }

    public static final class Tuple2<T> {
        private final T x;
        private final T y;

        Tuple2(T x, T y) {
            this.x = x;
            this.y = y;
        }

        T getX() {
            return x;
        }

        T getY() {
            return y;
        }
    }

    @AfterClass
    public static void tearDown() {
        cleanUpDatabase();
    }

    public static void cleanUpDatabase() {
        final Optional<Connection> optional = ConnectionManager.getConnection();

        final ResourceDatabasePopulator databasePopulator = new ResourceDatabasePopulator();
        databasePopulator.addScript(new ClassPathResource("scripts/cleanup.sql"));

        optional.ifPresent(connection -> executeScript(databasePopulator, connection));
    }

}
