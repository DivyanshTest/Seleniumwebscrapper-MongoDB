package webscrapping_mongodb.seleniumWebScrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.bson.Document;
import org.openqa.selenium.By;
//import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import io.github.bonigarcia.wdm.WebDriverManager;




public class WebscrapTest {
	
	WebDriver driver;
	MongoCollection<Document> dbcollection;
//	List<Document> docsList = new ArrayList<Document>();
	
	@BeforeSuite
	public void connectoMongoBD() {
		
//		Logger dblogs = Logger.getLogger("org.momgdb.driver");
		MongoClient dbclient = MongoClients.create("mongodb://127.0.0.1:27017");
		MongoDatabase db =  dbclient.getDatabase("scrapperdb");
		dbcollection = db.getCollection("webs");
		
	}
	
	@BeforeTest
	public void setup(){
	WebDriverManager.chromedriver().setup();
	ChromeOptions op = new ChromeOptions();
	op.addArguments("--headless");
	driver = new ChromeDriver(op);
	}
	@DataProvider
	public  Object[][] getData() {
		return new Object[][] {
			{"https://www.asda.com/"},
			{"https://www.amazon.com/"}
		};
	}
	
	@Test(dataProvider = "getData")
	public void webScrapper(String urlData) {
		driver.get(urlData);
		String url = driver.getCurrentUrl();
		String title = driver.getTitle();
		int linksCount = driver.findElements(By.tagName("a")).size();
		int imagesCount = driver.findElements(By.tagName("img")).size();
		List<WebElement>linksData =  driver.findElements(By.tagName("a"));
		List<WebElement>imgdata =  driver.findElements(By.tagName("img"));
		List<String> images = new ArrayList<String>();
		
		Document d1 = new Document();
		d1.append("url", url);
		d1.append("title", title);
		d1.append("linksCount", linksCount);
		d1.append("imagesCount", imagesCount);
//		
//		for(WebElement e : linksData) {
//			d1.append("links", e.getAttribute("href"));
//		}

		for(WebElement e : imgdata) {
			String imgValue = e.getAttribute("src");
			images.add(imgValue);
//			d1.append("imagesLink", images);
		}
		d1.append("imagesLink", images);
		List<Document> docsList = new ArrayList<Document>();
		docsList.add(d1);
		
		dbcollection.insertMany(docsList);
		
	}
	
	@AfterTest
	public void close() {
		driver.quit();
	}

}
