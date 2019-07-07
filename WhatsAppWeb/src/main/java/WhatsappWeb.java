import java.io.FileWriter;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

class WhatsappUtil {
  private WhatsappUtil() {
  }

  static WebDriver initialize() {
    System.setProperty("webdriver.chrome.driver", "/Users/rohangupta/IdeaProjects/chromedriver");
    ChromeOptions options = new ChromeOptions();
    options.addArguments("disable-infobars");
    options.addArguments("--start-fullscreen");
    WebDriver driver = new ChromeDriver(options);
    driver.get("https://web.whatsapp.com/");
    return driver;
  }

}

public class WhatsappWeb {

  private static final String SPAN_TITLE = "span[title='";
  private static final String STRING = "']";
  private static final String SELECTOR = "div.copyable-text";
  private static final String ATTRIBUTES = "data-pre-plain-text";
  private static final String CSS_SELECTOR = "div.message-in";
  private static final String TAG_NAME = "span";
  private static final Scanner scanner = new Scanner(System.in);
  static int flag = 0;

  private WhatsappWeb() {
  }

  public static void main(String[] args) throws IOException {
    WebDriver driver = WhatsappUtil.initialize();
    driver.manage().timeouts().implicitlyWait(Long.MAX_VALUE, TimeUnit.SECONDS);
    clickOnGroup(driver);


    FileWriter fileWriter = new FileWriter("src/main/resources/Chat.txt");


    List<WebElement> messageList;
    String newMessage;
    String lastMessage = "";

    while (true) {
      messageList = getElements(driver);
      WebElement lastMessageTag = getLastMessageElement(messageList);
      String message = "";
      try {
        WebElement fromTag = getWebElement(lastMessageTag, By.cssSelector(SELECTOR));
        String messageFrom = fromTag.getAttribute(ATTRIBUTES);
        message = getWebElement(fromTag, By.tagName(TAG_NAME)).getText();
        newMessage = messageFrom + message;
      } catch (NoSuchElementException element) {
        newMessage = "Previous messaege deleted";
      }
      if ("shutdown".equalsIgnoreCase(message) && flag > 2) {
        break;
      }

      if (!lastMessage.equals(newMessage)) {
        System.out.println(newMessage);
        writeInFile(newMessage, fileWriter);
        lastMessage = newMessage;
      }

      messageList.clear();

    }
    fileWriter.close();
  }

  private static void clickOnGroup(WebDriver driver) {
    System.out.print("Enter the Group Name: ");
    String group = joiner(scanner.nextLine());
    driver.findElement(By.cssSelector(group)).click();
  }

  private static List<WebElement> getElements(WebDriver driver) {
    return driver.findElements(By.cssSelector(CSS_SELECTOR));
  }

  private static WebElement getLastMessageElement(List<WebElement> messageList) {
    int sizeOfList = messageList.size();
    return messageList.get(sizeOfList - 1);
  }

  private static WebElement getWebElement(WebElement lastMessageTag, By by) {
    return lastMessageTag.findElement(by);
  }

  private static void writeInFile(String newMessage, FileWriter fileWriter) throws IOException {
    fileWriter.write(newMessage+"\n");
    flag++;
  }

  private static String joiner(String data) {
    return MessageFormat.format("{0}{1}{2}", SPAN_TITLE, data, STRING);
  }

}
