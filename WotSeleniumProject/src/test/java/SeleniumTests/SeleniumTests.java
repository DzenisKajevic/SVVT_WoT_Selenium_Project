package SeleniumTests;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import HelperClasses.FileUtil;

class SeleniumTests {

	private static WebDriver webDriver;
	private static String baseUrl;
	private static WebDriverWait wait;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		System.setProperty("webdriver.chrome.driver", "../selenium chrome driver/chromedriver.exe");

		ChromeOptions options = new ChromeOptions();
		options.addArguments("--start-maximized");
		// options.addArguments("--window-size=768,1024");
		// options.addArguments("--headless");

		// options.addArguments("--user-data-dir=C:\\Users\\dzeni\\Desktop");

		webDriver = new ChromeDriver(options);
		baseUrl = "https://worldoftanks.eu/";
		wait = new WebDriverWait(webDriver, Duration.ofSeconds(30));
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
		webDriver.quit();
	}

	@Test
	@Disabled
	// Interactive test (download might need to be accepted)
	// Test passed
	void downloadTest() throws InterruptedException, IOException {
		webDriver.get(baseUrl);
		// Close the promo div
		webDriver.findElement(By.xpath("/html/body/div[1]/div/div[4]/div/section[1]/div[3]/button[2]")).click();

		// Go to the download page
		webDriver.findElement(By.xpath("/html/body/div[1]/div/div[2]/div/ul/li[2]/a/span")).click();
		webDriver.findElement(By.linkText("Download Game")).click();

		// Download the game
		webDriver.findElement(By.xpath("/html/body/div[1]/div/div[4]/div[1]/div/div[3]/div[2]/a[1]/span")).click();
		// Allow the tester to accept the "risky file"
		Thread.sleep(3000);

		// Check if the file was downloaded
		// The file will be deleted in .checkFileDownload()
		// Since the filename is randomised often, the function calls .contains()
		boolean fileFound = FileUtil.checkFileDownload("C:\\Users\\dzeni\\Downloads", "world_of_tanks_install_eu",
				".exe", 10000, 3000);

		// True
		assertTrue(fileFound);

		Thread.sleep(2000);
	}

	@Test
	@Disabled
	void checkMapLeaderboardIfFrozenRedirectTest() {
		webDriver.get(baseUrl);
		// Close the promo div
		webDriver.findElement(By.xpath("/html/body/div[1]/div/div[4]/div/section[1]/div[3]/button[2]")).click();

		// Save the handle of the first tab to enable switching to the second
		String handle1 = webDriver.getWindowHandle();

		// Go to the download page
		webDriver.findElement(By.xpath("/html/body/div[1]/div/div[2]/div/ul/li[4]/a/span")).click();
		webDriver.findElement(By.linkText("Global Map")).click();

		for (String handle : webDriver.getWindowHandles()) {
			if (!handle.equals(handle1)) {
				webDriver.switchTo().window(handle);
				break;
			}
		}

		// if the map is currently frozen, check if the leaderboards are paused
		WebElement frozenMapParagraph = wait.until(
				ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div[8]/div/div/div/div/div/p")));

		boolean mapFrozen = frozenMapParagraph.getText().contains("The Global Map is temporarily frozen.");
		webDriver.findElement(By.xpath("/html/body/div[8]/div/div/div/button")).click();

		// if map is frozen, go to the leaderboard & check if the event is finished
		if (mapFrozen) {
			WebElement acceptCookies = webDriver.findElement(By.xpath("/html/body/article/div/button"));
			acceptCookies.click();

			webDriver.findElement(By.xpath("/html/body/div[17]/div/div[1]/button")).click();
			WebElement subtitle = webDriver.findElement(By.xpath("/html/body/div[7]/div/div[1]/div/div[2]/div/h3"));
			assertEquals("event finished", subtitle.getText().toLowerCase());
		}
		// if the map is unfrozen test automatically passed
		// (the map won't unfreeze for a long time so I can't set other conditions)
	}

	@Test
	@Disabled
	// For this test to work, the map probably has to be frozen so that the "Go to
	// the map" button is visible
	void languageOnFrozenGlobalMapChange() throws InterruptedException, IOException {
		webDriver.get("https://eu.wargaming.net/globalmap/");

		// "Go to the map" button (if map is not frozen, it might not be present
		WebElement exitNotification = wait
				.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div[8]/div/div/div/button")));
		exitNotification.click();
		WebElement acceptCookies = webDriver.findElement(By.xpath("/html/body/article/div/button"));
		acceptCookies.click();

		WebElement settingsButton = webDriver.findElement(By.xpath("/html/body/div[16]/button"));
		settingsButton.click();
		WebElement languageDivDropdown = webDriver.findElement(
				By.xpath("/html/body/div[8]/div/div/div/div/div/div/table/tbody[1]/tr/td[2]/div/div[2]/button"));
		languageDivDropdown.click();
		WebElement germanLanguage = webDriver.findElement(By.xpath(
				"/html/body/div[8]/div/div/div/div/div/div/table/tbody[1]/tr/td[2]/div/div[3]/div/div[1]/div[2]/div/span/span"));
		germanLanguage.click();
		WebElement closeButton = webDriver.findElement(By.xpath("/html/body/div[8]/div/div/div/div/footer/div/button"));
		// closing the div reloads the page with the new language
		closeButton.click();

		exitNotification = wait
				.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div[8]/div/div/div/button")));
		exitNotification.click();

		// check if the language was changed successfully
		WebElement playerSupportSpan = webDriver.findElement(By.xpath("/html/body/div[1]/div/div[1]/div[4]/a[2]/span"));
		assertEquals("Spieler Support", playerSupportSpan.getText());
		// repeat the process to change the language back to English

		settingsButton = webDriver.findElement(By.xpath("/html/body/div[16]/button"));
		settingsButton.click();
		languageDivDropdown = webDriver.findElement(
				By.xpath("/html/body/div[8]/div/div/div/div/div/div/table/tbody[1]/tr/td[2]/div/div[2]/button"));
		languageDivDropdown.click();
		WebElement englishLanguage = webDriver.findElement(By.xpath(
				"/html/body/div[8]/div/div/div/div/div/div/table/tbody[1]/tr/td[2]/div/div[3]/div/div[1]/div[1]/div/span/span"));
		englishLanguage.click();
		closeButton = webDriver.findElement(By.xpath("/html/body/div[8]/div/div/div/div/footer/div/button"));
		// closing the div reloads the page with the new language
		closeButton.click();
		exitNotification = wait
				.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div[8]/div/div/div/button")));
		exitNotification.click();
		playerSupportSpan = webDriver.findElement(By.xpath("/html/body/div[1]/div/div[1]/div[4]/a[2]/span"));
		assertEquals("Player Support", playerSupportSpan.getText());

		Thread.sleep(2000);
	}
	
	@Test
	@Disabled
	void register() throws InterruptedException {
		webDriver.get(baseUrl);
		webDriver.findElement(By.xpath("/html/body/div[1]/div/div[4]/div/section[1]/div[3]/div[1]/div/div/a[1]")).click();
		WebElement email = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("id_login")));
		WebElement name = webDriver.findElement(By.id("id_name"));
		WebElement password = webDriver.findElement(By.id("id_password"));
		WebElement confirmPassword = webDriver.findElement(By.id("id_re_password"));
		
		email.sendKeys("kanes2487511@dentaltz.com");
		name.sendKeys("burnerName120949111");
		password.sendKeys("burnerPassword_123");
		confirmPassword.sendKeys("burnerPassword_123");
		
		// Accept privacy policy
		webDriver.findElement(By.id("onetrust-accept-btn-handler")).click();
		
		// Accept EULA
		WebElement EULA = webDriver.findElement(By.xpath("/html/body/div[1]/div/div/div[3]/div[6]/form/fieldset[7]/ul/li[1]/label"));
		WebElement submitButton = webDriver.findElement(By.xpath("/html/body/div[1]/div/div/div[3]/div[6]/form/fieldset[8]/button/span"));
		
		Actions builder = new Actions(webDriver);
		Action scrollToSubmit = 
				builder
					.moveToElement(submitButton)
					.build();
		
		scrollToSubmit.perform();
		
		EULA.click();
		
		// wait until the EULA is accepted (otherwise it will try to submit the form 
		// before it gets accepted
		// wait.until(Expec... .elementSelectionStateToBe() does not work, swapping to sleep()
		Thread.sleep(1000);
		
		// Submit form
		submitButton.click();
		
		WebElement successHeading = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div[1]/div/div/div[3]/div[3]/h1")));
		assertEquals("account created", successHeading.getText().toLowerCase());
		
		Thread.sleep(2000);
	}
	
	@Test
	@Disabled
	void login() throws InterruptedException {
		webDriver.get(baseUrl);
		// Close the promo div
		webDriver.findElement(By.xpath("/html/body/div[1]/div/div[4]/div/section[1]/div[3]/button[2]")).click();
		
		// go to login page
		webDriver.findElement(By.xpath("/html/body/div[1]/div/div[3]/div[1]/div/div[1]/div[1]/a[1]")).click();
		
		WebElement email = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("id_login")));
		WebElement password = webDriver.findElement(By.id("id_password"));
		email.sendKeys("kanes248751@dentaltz.com");
		password.sendKeys("burnerPassword_123");
		
		// submit form
		webDriver.findElement(By.xpath("/html/body/div[1]/div/div[3]/div/div/div/div[1]/span/form/div/fieldset[2]/span[1]/button/span")).click();
		
		WebElement usernameTopRight = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div[1]/div/div[3]/div[1]/div/div[1]/div[1]/a[2]/span[3]")));
		
		assertEquals("burnerName12094911", usernameTopRight.getText());
		
		Thread.sleep(2000);
	}
	
	@Test
	@Disabled
	void redirectToYoutubeChannel() throws InterruptedException {
		webDriver.get(baseUrl);
		
		// Close the promo div
		webDriver.findElement(By.xpath("/html/body/div[1]/div/div[4]/div/section[1]/div[3]/button[2]")).click();

		WebElement youtubeLink = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div[1]/div/div[5]/div[1]/div/a[2]")));
		
		Actions builder = new Actions(webDriver);
		Action scrollToYTLink = 
				builder
					.moveToElement(youtubeLink)
					.build();
		
		scrollToYTLink.perform();
		
		youtubeLink.click();
		
		// Save the handle of the first tab to enable switching to the second
		String handle1 = webDriver.getWindowHandle();

		for (String handle : webDriver.getWindowHandles()) {
			if (!handle.equals(handle1)) {
				webDriver.switchTo().window(handle);
				break;
			}
		}
			
		String currentUrl = webDriver.getCurrentUrl();
		assertEquals("https://www.youtube.com/@WorldOfTanksOfficialChannel", currentUrl);
		
		Thread.sleep(2000);
	}
}
