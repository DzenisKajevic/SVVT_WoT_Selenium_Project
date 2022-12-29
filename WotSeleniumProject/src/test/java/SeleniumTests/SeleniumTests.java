package SeleniumTests;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import HelperClasses.FileUtil;

class SeleniumTests {

	private static WebDriver webDriver;
	private static String baseUrl;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		System.setProperty("webdriver.chrome.driver", "../selenium chrome driver/chromedriver.exe");
		
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--start-maximized");
		//options.addArguments("--window-size=768,1024");
		//options.addArguments("--headless");
		
		// options.addArguments("--user-data-dir=C:\\Users\\dzeni\\Desktop");		
		
		webDriver = new ChromeDriver(options);
		baseUrl = "https://worldoftanks.eu/";
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
		webDriver.quit();
	}
	
	@Test
	@Disabled
	// Interactive test (download might need to be accepted) 
	// Test passed
	void downloadTest() throws InterruptedException, IOException
	{
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
		boolean fileFound = FileUtil.checkFileDownload("C:\\Users\\dzeni\\Downloads",
				"world_of_tanks_install_eu", ".exe", 10000, 3000);

		// True
		assertTrue(fileFound); 
		
		Thread.sleep(2000);
	}
}
