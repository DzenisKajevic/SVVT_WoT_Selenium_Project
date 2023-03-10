package SeleniumTests;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

class TestsWithLogin {

	private static WebDriver webDriver;
	private static String baseUrl;
	private static WebDriverWait wait;
	
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		System.setProperty("webdriver.chrome.driver", "../selenium chrome driver/chromedriver.exe");

		ChromeOptions options = new ChromeOptions();
		options.addArguments("--start-maximized");
		
		webDriver = new ChromeDriver(options);
		baseUrl = "https://worldoftanks.eu/";
		wait = new WebDriverWait(webDriver, Duration.ofSeconds(60));
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
		webDriver.quit();
	}

	@BeforeEach
	void setUp() throws Exception {
		///////////////////////////
		// login
		webDriver.get(baseUrl);
		// Close the promo div
		webDriver.findElement(By.xpath("/html/body/div[1]/div/div[4]/div/section[1]/div[3]/button[2]")).click();
		
		// go to login page
		WebElement loginPage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div[1]/div/div[3]/div[1]/div/div[1]/div[1]/a[1]")));
		
		loginPage.click();
		
		WebElement email = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("id_login")));
		WebElement password = webDriver.findElement(By.id("id_password"));
		email.sendKeys("kanes248751@dentaltz.com");
		password.sendKeys("burnerPassword_123");
		
		// submit form
		webDriver.findElement(By.xpath("/html/body/div[1]/div/div[3]/div/div/div/div[1]/span/form/div/fieldset[2]/span[1]/button/span")).click();
		
		// I'll keep this line here just in case. I don't mind waiting another few ms
		// and I don't want to test whether it's going to break any tests if I remove it
		WebElement usernameTopRight = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div[1]/div/div[3]/div[1]/div/div[1]/div[1]/a[2]/span[3]")));
		////////////////////////////
	}
	
	// DoB can be changed once per day, so this test most likely won't be able to run again
	// Succeeded first try
	@Test
	@Disabled
	void removeBirthDate() throws InterruptedException {
		WebElement usernameTopRight = webDriver.findElement(By.xpath("/html/body/div[1]/div/div[3]/div[1]/div/div[1]/div[1]/a[2]/span[3]"));
		// change date of birth
		usernameTopRight.click();
		webDriver.findElement(By.xpath("/html/body/div[1]/div/div[3]/div[1]/div/div[1]/div[1]/div[2]/div/div[3]/ul/li[1]/a/span")).click();
		
		WebElement changeDateButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div[1]/div/div/div[2]/div/div[2]/div[1]/div[1]/div/div/div[2]/div/div[3]/div/a")));
		changeDateButton.click();
		
		WebElement removeDateButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div[1]/div/div/div[2]/div/div/fieldset[1]/div[2]/a")));
		removeDateButton.click();
		
		// save
		webDriver.findElement(By.xpath("/html/body/div[1]/div/div/div[2]/div/div/fieldset[2]/button")).click();
		
		WebElement DoB = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div[1]/div/div/div[2]/div/div[2]/div[1]/div[1]/div/div/div[2]/div/div[2]/div/span")));
		
		System.out.println(DoB.getText());
		assertEquals("", DoB.getText());
		
		Thread.sleep(2000);
	}

	@Test
	@Disabled
	void redeemIncorrectBonusCode() throws InterruptedException {
		// go to "Redeem Bonus Codes"
		webDriver.findElement(By.xpath("/html/body/div[1]/div/div[2]/div/ul/li[2]/a/span")).click();
		webDriver.findElement(By.xpath("/html/body/div[1]/div/div[2]/div/ul/li[2]/div/ul/li[2]/a")).click();

		WebElement inputCode = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div[2]/div/div/div[2]/div/input")));
		inputCode.sendKeys("RANDOMCODE");
		
		webDriver.findElement(By.xpath("/html/body/div[2]/div/div/div[2]/button")).click();
		
		// sleep for 1 second, otherwise the js code from below:
		// "className = ..." will not work
		Thread.sleep(1000);
		// It's not possible to fetch the errorMessage paragraph using Selenium for some reason, 
		//so I'll use js to change the value of an H3 element below to match the description
		// of the error message, fetch that H3 element using Selenium and check if it matches
		JavascriptExecutor js = (JavascriptExecutor) webDriver;
		js.executeScript("className = document.getElementsByClassName(\"redeem-page_error\");");
		js.executeScript("h3 = document.getElementsByClassName(\"heading-h3\");");
		//js.executeScript("console.log(h3[0], className[0])");
		js.executeScript("h3[0].outerText = className[0].childNodes[0].textContent;");
	
		WebElement h3ErrorMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div[2]/div/div/div[3]")));
		assertTrue(h3ErrorMessage.getText().contains("Invalid code. Please make sure you entered the correct code"));
		Thread.sleep(2000);
	}

	// succeeds if the number of played battles is less than the required amount for becoming a referrer (600 atm)
	@Test
	@Disabled
	void accountTooYoungForReferringTest() throws InterruptedException {
		webDriver.findElement(By.xpath("/html/body/div[1]/div/div[2]/div/ul/li[6]/a/span")).click();
		webDriver.findElement(By.xpath("/html/body/div[1]/div/div[2]/div/ul/li[6]/div/ul/li[3]/a")).click();
		WebElement unavailableParagraph = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div[1]/div/div[4]/div[1]/div/div/div[1]/article/section/div[3]/div[2]/p[1]")));
		
		assertEquals("The referral link is unavailable", unavailableParagraph.getText());
		if(unavailableParagraph.getText().equals("The referral link is unavailable")) {
			WebElement minimumMatchesParagraph = webDriver.findElement(By.xpath("/html/body/div[1]/div/div[4]/div[1]/div/div/div[1]/article/section/div[3]/div[2]/p[3]/span"));
			int minimumMatchesPlayed = Integer.parseInt(minimumMatchesParagraph.getText());
			
			WebElement community = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div[1]/div/div[2]/div/ul/li[6]/a/span")));
			community.click();
			
			webDriver.findElement(By.xpath("/html/body/div[1]/div/div[2]/div/ul/li[6]/div/ul/li[1]/a")).click();
			int matchesPlayed = Integer.parseInt(webDriver.findElement(
					By.xpath("/html/body/div[1]/div/div[4]/div[1]/section[3]/div[2]/div/div[2]/div[2]/span[1]")).getText());
			
			assertTrue(matchesPlayed < minimumMatchesPlayed);
		}
		Thread.sleep(2000);
	}
	
	// Clan applications can't be cancelled, so in order to run this test, a new clan name will have to be entered
	@Test
	void applyForClanAndVerifyTheApplication() throws InterruptedException {
		webDriver.findElement(By.xpath("/html/body/div[1]/div/div[2]/div/ul/li[4]/a")).click();
		WebElement inputClanName = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div[1]/div/div[4]/div[2]/article/div/header/form/div/div/div/input[1]")));
		inputClanName.sendKeys("zZzZz");
		String handle1 = webDriver.getWindowHandle();

		webDriver.findElement(By.xpath("/html/body/div[1]/div/div[4]/div[2]/article/div/header/form/div/div/input")).click();
		
		for (String handle : webDriver.getWindowHandles()) {
			if (!handle.equals(handle1)) {
				webDriver.switchTo().window(handle);
				break;
			}
		}
		
		////////////////////
		// login on the second page
		webDriver.findElement(By.xpath("/html/body/div[1]/div[1]/div/div[1]/div[1]/a[1]")).click();
		////////////////////
		
		WebElement clanNameInput = webDriver.findElement(By.xpath("/html/body/div[1]/div[3]/form/div[2]/div/div[2]/input"));
		clanNameInput.sendKeys("zZzZz");
		webDriver.findElement(By.xpath("/html/body/div[1]/div[3]/form/button/span[2]")).click();
		
		
		WebElement clan = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div[1]/div[3]/div[2]/div[1]/div/div[2]/div[3]/div/div[2]/div/div[2]/div[2]/div[2]/div/span[2]/span[1]/span/span[2]")));
		clan.click();
		
		WebElement textArea = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div[1]/div[3]/div[2]/div[1]/div/div[2]/div[3]/div/div[2]/div/div[2]/div[3]/div/div[2]/div[1]/div/div[3]/div/div[1]/textarea")));
		textArea.sendKeys("Automated testing. Do not accept");
		
		webDriver.findElement(By.xpath("/html/body/div[1]/div[3]/div[2]/div[1]/div/div[2]/div[3]/div/div[2]/div/div[2]/div[3]/div/div[2]/div[1]/div/div[3]/div/div[3]/button/span")).click();
		
		Thread.sleep(2000);
		
		webDriver.get("https://eu.wargaming.net/clans/wot/find_clan/");
		webDriver.findElement(By.xpath("/html/body/div[1]/div[3]/div[2]/div[1]/div/ul/li[2]/label")).click();
		
		WebElement clanApplication = webDriver.findElement(By.xpath("/html/body/div[1]/div[3]/div[2]/div[1]/div/div[2]/div[2]/div[4]/div/div[3]/div[2]/div[1]/div[2]/div[1]/div[2]/div/div[2]/span[1]/span/span[2]"));
		assertEquals("zZzZz", clanApplication.getText());
		
		Thread.sleep(2000);
	}
}
