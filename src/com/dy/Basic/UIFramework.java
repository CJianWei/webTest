package com.dy.Basic;

import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.opera.OperaDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

public class UIFramework {

	public static final String FireFox = "firefox";
	public static final String Chrome = "chrome";
	public static final String IE = "IE";
	public static final String Edge = "edge";
	public static final String Opera = "opera";

	public static final String ID = "id";
	public static final String CLASS = "class";
	public static final String LINKTEXT = "linkText";
	public static final String NAME = "name";
	public static final String XPATH = "xpath";
	public static final String TAGNAME = "tagName";
	public static final String PARTIALLINKTEXT = "partialLinkText";
	public static final String CSS = "css";

	public static WebDriver driver;
	protected String main_window;

	public static boolean recordInitLog = false;
	public static boolean confInit = false;

	/**
	 * 初始化浏览器
	 * 
	 * @throws Exception
	 */
	public void initLogAndLaunchBrowser(String conf_addr) throws Exception {
		if (confInit == false) {
			CommonTool.initConf(conf_addr);
			confInit = true;
		}
		if (recordInitLog == false) {
			Log.initLog();
			recordInitLog = true;
		}

		String browser = CommonTool.readConf("browser");
		String Ip = CommonTool.readConf("ip");
		if (getDriver() == null) {
			if (Ip.trim().equals("")) {
				startBrowser(browser);
			} else {
				startRemoteBrowser(browser, Ip);
			}
		}

	}

	/**
	 * 启动浏览器，并窗口最大化
	 * 
	 * @throws Exception
	 */
	public boolean startBrowser(String browser) {

		switch (browser) {
		case FireFox:
			System.setProperty("webdriver.gecko.driver", CommonTool.readConf(FireFox));
			driver = new FirefoxDriver();
			break;
		case Chrome:
			System.setProperty("webdriver.chrome.driver", CommonTool.readConf(Chrome));
			driver = new ChromeDriver();
			break;
		case IE:
			System.setProperty("webdriver.ie.driver", CommonTool.readConf(IE));
			driver = new InternetExplorerDriver();
			break;
		case Edge:
			System.setProperty("webdriver.edge.driver", CommonTool.readConf(Edge));
			driver = new EdgeDriver();
			break;
		case Opera:
			System.setProperty("webdriver.opera.driver", CommonTool.readConf(Opera));
			driver = new OperaDriver();
			break;
		default:
			return false;
		}
		maxSize(1);
		return true;
	}

	/**
	 * 启动浏览器，并窗口最大化
	 * 
	 * @throws Exception
	 */
	public void maxSize(int run_max) {
		if (run_max > 0) {
			this.getDriver().manage().window().maximize();
		}
		this.getDriver().manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
	}

	/**
	 * 启动远程浏览器，并窗口最大化
	 * 
	 * @throws Exception
	 */
	public boolean startRemoteBrowser(String browser, String ip) throws MalformedURLException {
		if (ip.contains(":") == false) {
			ip = ip + ":4444";
		}
		if (ip.startsWith("http") == false) {
			ip = "http://" + ip;
		}
		String url = ip + "/wd/hub";

		switch (browser) {
		case FireFox:
			driver = new RemoteWebDriver(new URL(url), DesiredCapabilities.firefox());
			break;
		case Chrome:
			driver = new RemoteWebDriver(new URL(url), DesiredCapabilities.chrome());
			break;
		case IE:
			driver = new RemoteWebDriver(new URL(url), DesiredCapabilities.internetExplorer());
			break;
		case Edge:
			driver = new RemoteWebDriver(new URL(url), DesiredCapabilities.edge());
			break;
		default:
			return false;
		}
		maxSize(0);
		return true;
	}

	/**
	 * 获取驱动
	 * 
	 */
	public WebDriver getDriver() {
		return driver;
	}

	public void OpenUrl(String url) {
		this.getDriver().get(url);
	}

	/**
	 * 强制等待
	 * 
	 * @param millis
	 *            强制等待的秒数 单位是毫秒
	 */
	public void waitByTimeOut(int millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 刷新页面
	 * 
	 * @return 页面刷新成功，则返回true
	 */
	public boolean refresh() {
		this.getDriver().navigate().refresh();
		waitByTimeOut(3000);
		return true;
	}

	/**
	 * 关闭
	 */
	public void close() {
		if (driver != null) {
			driver.quit();
			driver = null;
		}
	}

	/**
	 * 截图
	 */
	public static void ScreenShot() {
		File screenShot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
		try {
			FileUtils.copyFile(screenShot, new File(Path.loadPathClient(new StringChain().add("screen")),
					CommonTool.Now("yyyy-MM-dd-HH-mm") + ".jpg"));
			Log.I("截图成功");
		} catch (IOException e) {
			Log.E("截图失败");
		}
	}

	// 页面元素是否在页面上可用和可被单击
	public void waitForElementClick(WebElement ele) {
		new WebDriverWait(driver, 10).until(ExpectedConditions.elementToBeClickable(ele));
	}

	// 页面元素存在
	public void waitForElementShow(String type, String locatorStr) {
		WebDriverWait wait = new WebDriverWait(driver, 10);
		wait.until(ExpectedConditions.presenceOfElementLocated(getByObjectByType(type, locatorStr)));
	}

	// 获取 By 对象
	protected By getByObjectByType(String type, String locatorStr) {
		switch (type.toLowerCase()) {
		case ID:
			return By.id(locatorStr);
		case CLASS:
			return By.className(locatorStr);
		case LINKTEXT:
			return By.linkText(locatorStr);
		case NAME:
			return By.name(locatorStr);
		case XPATH:
			return By.xpath(locatorStr);
		case TAGNAME:
			return By.tagName(locatorStr);
		case PARTIALLINKTEXT:
			return By.partialLinkText(locatorStr);
		case CSS:
			return By.cssSelector(locatorStr);
		default:
			return By.cssSelector(locatorStr);
		}
	}

	// 通过链式 获取元素
	public WebElement loadChainEle(EleChain ec) {
		return loadChainEle(ec, false);
	}

	public WebElement loadChainEle(EleChain ec, boolean showDisAble) {
		ArrayList<Ele> eles = ec.getEles();
		if (eles.isEmpty() || eles.size() == 0) {
			throw new NullPointerException();
		}
		WebElement ele = null;
		for (int i = 0; i < eles.size(); i++) {
			String type = eles.get(i).getType();
			String locatorStr = eles.get(i).getLocatorStr();
			int index = eles.get(i).getIndex();
			List<WebElement> ws = getElements(type, locatorStr, ele, showDisAble);
			ele = ws.get(index);
		}
		return ele;
	}

	// 通过链式 获取元素数组长度
	public int loadChainSize(EleChain ec, boolean showDisAble) throws Exception {
		ArrayList<Ele> eles = ec.getEles();
		if (eles.isEmpty() || eles.size() == 0) {
			throw new NullPointerException();
		}
		WebElement ele = null;
		for (int i = 0; i < eles.size(); i++) {
			String type = eles.get(i).getType();
			String locatorStr = eles.get(i).getLocatorStr();
			int index = eles.get(i).getIndex();
			List<WebElement> ws = getElements(type, locatorStr, ele, showDisAble);
			if (i == eles.size() - 1) {
				return ws.size();
			} else {
				if (ws.size() > index) {
					ele = ws.get(index);
				} else {
					String msg = String.format("type %s locatorStr %s size is %d by eager to be more than %s", type,
							locatorStr, ws.size(), index + 1);
					Log.E(msg);
					throw new Exception(msg);
				}
			}
		}
		return 0;
	}

	public int loadChainSize(EleChain ec) throws Exception {
		return loadChainSize(ec, false);
	}

	// ***********************************
	// ********** select
	// ***********************************

	/**
	 * select 有同名元素的时候css配合下标定位 下拉框
	 * 
	 * @param locatorStr
	 *            通过cssSelector 定位 locatorStr
	 * @param index
	 *            元素的下标
	 */
	protected Select Selector(String type, String locatorStr, int index) {
		WebElement ele = getElement(type, locatorStr, index);
		Select selectstr = new Select(ele);
		return selectstr;
	}

	public Select Selector(String locatorStr) {
		return Selector(CSS, locatorStr, 0);
	}

	public Select Selector(String locatorStr, int index) {
		return Selector(CSS, locatorStr, index);
	}

	// ***********************************
	// ********** eles
	// ***********************************

	/**
	 * 获取元素，默认是通过css
	 * 
	 * @param locatorType
	 *            元素定位的方式——如css、id、xpath、name等
	 * @param locatorStr
	 *            元素定位
	 */
	public List<WebElement> getElements(String type, String locatorStr, WebElement ele, boolean showDisAble) {
		if (type.isEmpty() && locatorStr.isEmpty()) {
			throw new NullPointerException();
		}
		List<WebElement> list;
		if (ele != null) {
			list = ele.findElements(getByObjectByType(type, locatorStr));
		} else {
			list = driver.findElements(getByObjectByType(type, locatorStr));
		}
		if (list.size() <= 0) {
			Log.E("do not exist this kind of type" + type + " when the element is " + locatorStr);
		}
		if (showDisAble == false) {
			List<WebElement> tmp = new ArrayList<WebElement>();
			for (int i = 0; i < list.size(); i++) {
				if (list.get(i).isDisplayed()) {
					tmp.add(list.get(i));
				}
			}
			list = tmp;
		}
		Log.I(String.format("type:(%s) locatorMsg(%s) size(%d)", type, locatorStr, list.size()));
		return list;
	}

	protected List<WebElement> getElements(String type, String locatorStr) {
		return getElements(type, locatorStr, null, false);
	}

	// ***********************************
	// ********** ele
	// ***********************************

	/**
	 * 获取元素，默认是通过css
	 * 
	 * @param locatorType
	 *            元素定位的方式——如css、id、xpath、name等
	 * @param locatorStr
	 *            元素定位
	 */
	protected WebElement getElement(String type, String locatorStr, int index, boolean showDisAble) {
		if (type.isEmpty() || locatorStr.isEmpty()) {
			throw new NullPointerException();
		}
		WebElement ele = null;
		List<WebElement> list = this.getElements(type, locatorStr, null, showDisAble);

		if (list.size() < index + 1) {
			Log.E("out of range");
			ScreenShot();
		} else {
			ele = list.get(index);
		}
		return ele;
	}

	protected WebElement getElement(String type, String locatorStr, int index) {
		return getElement(type, locatorStr, index, false);
	}

	protected WebElement getElement(String locatorStr) {
		return getElement(CSS, locatorStr, 0);
	}

	protected WebElement getElement(String type, String locatorStr) {
		return getElement(type, locatorStr, 0);
	}

	// ***********************************
	// ********** size
	// ***********************************

	/**
	 * 获取定位元素的长度
	 * 
	 * @param locatorStr
	 *            通过css定位的元素
	 */
	public int size(String type, String locatorStr) {
		return getElements(type, locatorStr).size();
	}

	public int size(String locatorStr) {
		return size(CSS, locatorStr);
	}

	public int size(EleChain ec) throws Exception {
		return loadChainSize(ec);
	}

	// ***********************************
	// ********** click
	// ***********************************

	/**
	 * 通过指定类型点击元素
	 * 
	 * @param type
	 *            元素定位的方式——如css、id、xpath、name等
	 * @param elementLocatorStr
	 *            通过css定位的元素
	 */
	public void click(String type, String locatorStr, int index) {
		WebElement ele = getElement(type, locatorStr, index);
		waitForElementClick(ele);
		ele.click();
		waitByTimeOut(1000);

	}

	public void click(String locatorStr) {
		this.click(CSS, locatorStr, 0);

	}

	public void click(String locatorStr, int index) {
		this.click(CSS, locatorStr, index);

	}

	public void click(String type, String locatorStr) {
		this.click(type, locatorStr, 0);

	}

	public void click(EleChain ec) {
		loadChainEle(ec).click();
	}

	// ***********************************
	// ********** clickByJs
	// ***********************************

	/**
	 * 通过JavaScript配合下标点击
	 * 
	 * @param type
	 *            类型
	 * @param locatorStr
	 *            通过css 定位元素
	 * @param index
	 *            元素的下标
	 */
	public void clickByJs(String type, String locatorStr, int index) {
		JavascriptExecutor jse = (JavascriptExecutor) this.getDriver();
		WebElement element = getElement(type, locatorStr, index);
		jse.executeScript("arguments[0].click();", element);
	}

	public void clickByJs(String locatorStr, int index) {
		clickByJs(CSS, locatorStr, index);
	}

	public void clickByJs(String locatorStr) {
		clickByJs(CSS, locatorStr, 0);
	}

	public void clickByJs(EleChain ec) {
		JavascriptExecutor jse = (JavascriptExecutor) this.getDriver();
		jse.executeScript("arguments[0].click();", loadChainEle(ec));
	}

	public void pureJs(String str) {
		JavascriptExecutor jse = (JavascriptExecutor) this.getDriver();
		jse.executeScript(str);
	}

	// ***********************************
	// ********** uploadFile
	// ***********************************

	/**
	 * 上传文件
	 * 
	 * @param type
	 *            元素类型
	 * @param locatorStr
	 *            元素定位
	 * @param index
	 *            元素下标
	 * @param file
	 *            文件路径
	 */
	public void uploadFile(String type, String locatorStr, int index, String file) {

		WebElement ele = getElement(type, locatorStr, index, true);
		ele.sendKeys(file);
		waitByTimeOut(1000);

	}

	public void uploadFile(String locatorStr, String file) {
		this.uploadFile(CSS, locatorStr, 0, file);
	}

	public void uploadFile(String type, String locatorStr, String file) {
		this.uploadFile(type, locatorStr, 0, file);
	}

	public void uploadFile(String locatorStr, int index, String file) {
		this.uploadFile(CSS, locatorStr, index, file);
	}

	// ***********************************
	// ********** frame
	// ***********************************

	public void switchToIframe(String frame) {
		this.getDriver().switchTo().frame(frame);
	}

	public void switchToIframeDefault() {
		this.getDriver().switchTo().defaultContent();
	}

	public void switchToIframe(String type, String locatorStr, int index) {
		this.getDriver().switchTo().frame(getElement(type, locatorStr, index));
	}

	/**
	 * 切换窗口，切换到最新的窗口
	 */
	public void switchNewOpenWindow() {
		String currentWindow = getDriver().getWindowHandle();
		Set<String> handles = getDriver().getWindowHandles();// 获取所有窗口句柄
		for (String name : handles) {
			if (currentWindow.equals(name)) {
				continue;
			}
			getDriver().switchTo().window(name);// 切换到新窗口
		}
	}

	/**
	 * 关闭最新打开的窗口
	 */
	public void closeLastWindow() {
		String currentWindow = getDriver().getWindowHandle();
		Set<String> handles = getDriver().getWindowHandles();
		ArrayList<String> tmp = new ArrayList<String>();
		for (String name : handles) {
			if (currentWindow.equals(name)) {
				System.out.println(currentWindow);
				continue;
			}
			tmp.add(name);
		}
		getDriver().switchTo().window(currentWindow).close();
		if (tmp.size() > 0) {
			getDriver().switchTo().window(tmp.get(tmp.size() - 1));
		}
	}

	public int windowSize() {
		return getDriver().getWindowHandles().size();
	}

	/**
	 * 将焦点切回到主窗口页面上
	 */
	public void returnToMain() {
		String main_window = getDriver().getWindowHandle();
		getDriver().switchTo().window(main_window);
	}

	// ***********************************
	// ********** getAttribute
	// ***********************************

	/**
	 * 通过 ele 获取 元素的值
	 * 
	 * @param type
	 *            元素的type
	 * @param locatorStr
	 *            元素的定位
	 * @param index
	 *            元素的下标
	 * 
	 */
	public String getAttribute(String type, String locatorStr, int index, String v) {
		return getElement(type, locatorStr, index).getAttribute(v);
	}

	public String getAttribute(String locatorStr, String v) {
		return getAttribute(CSS, locatorStr, 0, v);
	}

	public String getAttribute(String type, String locatorStr, String v) {
		return getAttribute(type, locatorStr, 0, v);
	}

	public String getAttribute(String locatorStr, int index, String v) {
		return getAttribute(CSS, locatorStr, index, v);
	}

	public String getAttribute(EleChain ec, String v) {
		return loadChainEle(ec).getAttribute(v);
	}

	// ***********************************
	// ********** get value
	// ***********************************

	public String getValue(String type, String locatorStr, int index) {
		return getElement(type, locatorStr, index).getText();
	}

	public String getValue(String locatorStr) {
		return getValue(CSS, locatorStr, 0);
	}

	public String getValue(String locatorStr, int index) {
		return getValue(CSS, locatorStr, index);
	}

	public String getValue(String type, String locatorStr) {
		return getValue(type, locatorStr, 0);
	}

	public String getValue(EleChain ec) {
		return loadChainEle(ec).getText();
	}

	// ***********************************
	// ********** get value by js
	// ***********************************

	/**
	 * 通过 js 获取 元素的值
	 * 
	 * @param type
	 *            元素的type
	 * @param index
	 *            元素的下标
	 * 
	 */

	public String getValueByJs(String selector, int index, String val) {
		String str;
		if (val.trim().equals("text")) {
			str = String.format("$($('%s')['%d']).text()", selector, index);
		} else {
			str = String.format("$($('%s')['%d']).attr('%s')", selector, index, val);
		}
		JavascriptExecutor js = (JavascriptExecutor) this.getDriver();
		String value = (String) js.executeScript(str);
		return value;
	}

	public String getValueByJs(String selector, String val) {
		return getValueByJs(selector, 0, val);
	}

	public int getValueByJsInt(String selector, int index, String val) {
		String v = getValueByJs(selector, index, val);
		return Integer.parseInt(v);
	}

	public int getValueByJsInt(String selector, String val) {
		return getValueByJsInt(selector, 0, val);
	}

	// ***********************************
	// ********** set value
	// ***********************************

	/**
	 * 通过指定的type找到元素后指定下标对应的input框元素并赋值
	 * 
	 * @param type
	 *            元素的type
	 * @param locatorStr
	 *            元素的定位
	 * @param index
	 *            元素的下标
	 * 
	 */
	public void setValue(String type, String locatorStr, int index, String str) {
		waitForElementShow(type, locatorStr);
		WebElement ele = getElement(type, locatorStr, index);
		ele.clear();
		ele.sendKeys(str);
		waitByTimeOut(1000);
	}

	public void setValue(String locatorStr, String str) {
		setValue(CSS, locatorStr, 0, str);
	}

	public void setValue(String type, String locatorStr, String str) {
		setValue(type, locatorStr, 0, str);
	}

	public void setValue(String locatorStr, int index, String str) {
		setValue(CSS, locatorStr, index, str);
	}

	public void setValue(EleChain ec, String str) {
		WebElement ele = loadChainEle(ec);
		ele.clear();
		ele.sendKeys(str);
		waitByTimeOut(1000);
	}

	// ***********************************
	// ********** set value by js
	// ***********************************

	/**
	 * 直接通过JS配合下标找到input框元素并赋值
	 * 
	 * @param locatorStr
	 *            通过JS定位的元素
	 * @param index
	 *            元素的下标
	 * @param str
	 *            要输入的值
	 */
	public void setValueByJS(String type, String locatorStr, int index, String str) {
		JavascriptExecutor jse = (JavascriptExecutor) getDriver();
		WebElement element = getElement(type, locatorStr, index);
		jse.executeScript("arguments[0].click();", element);
		element.clear();
		element.sendKeys(str);
	}

	public void setValueByJS(String locatorStr, String str) {
		setValueByJS(CSS, locatorStr, 0, str);
	}

	public void setValueByJS(String type, String locatorStr, String str) {
		setValueByJS(type, locatorStr, 0, str);
	}

	public void setValueByJS(String locatorStr, int index, String str) {
		setValueByJS(CSS, locatorStr, index, str);
	}

	public void setValueByJS(EleChain ec, String str) {
		JavascriptExecutor jse = (JavascriptExecutor) getDriver();
		WebElement ele = loadChainEle(ec);
		jse.executeScript("arguments[0].click();", ele);
		ele.clear();
		ele.sendKeys(str);
	}

	// ***********************************
	// ********** set value by js
	// ***********************************

	/**
	 * 移除元素的只读属性后赋值
	 * 
	 * @param locatorStr
	 *            通过id 定位元素
	 * @param str
	 *            要输入的值
	 */
	public void RemoveReadOnly(String locatorStr, String str) {
		JavascriptExecutor removeAttribute = (JavascriptExecutor) getDriver();
		// remove readonly attribute
		removeAttribute.executeScript(
				"var setDate=document.getElementById('" + locatorStr + "');setDate.removeAttribute('readonly');");
		WebElement setDatElement = getDriver().findElement(By.id(locatorStr));
		waitByTimeOut(2000);
		setDatElement.clear();
		waitByTimeOut(2000);
		setDatElement.sendKeys(str);
	}

	// ***********************************
	// ********** check ele appear or not
	// ***********************************

	public boolean waitAppear(String locatorStr) {
		return waitAppear(CSS, locatorStr, 0);
	}

	public boolean waitAppear(String type, String locatorStr) {
		return waitAppear(type, locatorStr, 0);
	}

	public boolean waitAppear(String locatorStr, int index) {
		return waitAppear(CSS, locatorStr, index);
	}

	public boolean waitAppear(String type, String locatorStr, int index) {
		return waitAppearWithTs(type, locatorStr, index, DEFAULT_TS);
	}

	public boolean waitAppearWithTs(String type, String locatorStr, int index, int ts) {
		for (int i = 0; i < ts; i++) {
			List<WebElement> tmp = this.getElements(type, locatorStr);
			if (tmp.size() > index) {
				return true;
			}
			waitByTimeOut(1000);
		}
		return false;
	}

	public boolean waitAppearWithTs(String locatorStr, int ts) {
		return waitAppearWithTs(CSS, locatorStr, 0, ts);
	}

	public boolean waitAppearWithTs(String type, String locatorStr, int ts) {
		return waitAppearWithTs(type, locatorStr, 0, ts);
	}

	public boolean waitAppearWithTs(String locatorStr, int index, int ts) {
		return waitAppearWithTs(CSS, locatorStr, index, ts);
	}

	public boolean waitDisAppear(String type, String locatorStr, int index) {
		return waitDisAppearWithTs(type, locatorStr, index, DEFAULT_TS);
	}

	public boolean waitDisAppear(String locatorStr) {
		return waitDisAppear(CSS, locatorStr, 0);
	}

	public boolean waitDisAppear(String type, String locatorStr) {
		return waitDisAppear(type, locatorStr, 0);
	}

	public boolean waitDisAppear(String locatorStr, int index) {
		return waitDisAppear(CSS, locatorStr, index);
	}

	public boolean waitDisAppearWithTs(String type, String locatorStr, int index, int ts) {
		for (int i = 0; i < ts; i++) {
			List<WebElement> tmp = this.getElements(type, locatorStr);
			if (tmp.size() <= index) {
				return true;
			}
			waitByTimeOut(1000);
		}
		return false;
	}

	public boolean waitDisAppearWithTs(String locatorStr, int ts) {
		return waitDisAppearWithTs(CSS, locatorStr, 0, ts);
	}

	public boolean waitDisAppearWithTs(String type, String locatorStr, int ts) {
		return waitDisAppearWithTs(type, locatorStr, 0, ts);
	}

	public boolean waitDisAppearWithTs(String locatorStr, int index, int ts) {
		return waitDisAppearWithTs(CSS, locatorStr, index, ts);
	}

	// 确认处理的情况
	public int checkSituation(EleChain ec) {
		ArrayList<Ele> ary_ele = ec.getEles();
		for (int i = 0; i < DEFAULT_TS; i++) {
			for (int index = 0; index < ary_ele.size(); index++) {
				List<WebElement> tmp = this.getElements(ary_ele.get(index).getType(),
						ary_ele.get(index).getLocatorStr());
				if (tmp.size() > ary_ele.get(index).getIndex()) {
					return index;
				}
				waitByTimeOut(1000);
			}
		}
		return -1;
	}

	// ***********************************
	// ********** wait for ele
	// ***********************************

	public static final int DEFAULT_TS = 10;

	public UIFramework waitEle(String type, String locatorStr, int index, int ts) throws Exception {
		String msg;
		for (int i = 0; i < ts; i++) {
			List<WebElement> tmp = this.getElements(type, locatorStr);
			if (tmp.size() > index) {
				msg = String.format("wait type(%s) locatorStr(%s) index(%d) times(%d) ", type, locatorStr, index,
						i * 1000);
				Log.I(msg);
				return this;
			}
			waitByTimeOut(1000);
		}
		msg = String.format("wait type(%s) locatorStr(%s) index(%d) times(%d) timeout", type, locatorStr, index,
				ts * 1000);
		Log.E(msg);
		throw new Exception(msg);
	}

	public UIFramework waitEle(String locatorStr, int ts) throws Exception {
		return waitEle(CSS, locatorStr, 0, ts);
	}

	public UIFramework waitEle(String type, String locatorStr, int ts) throws Exception {
		return waitEle(type, locatorStr, 0, ts);
	}

	public UIFramework waitEle(String locatorStr, int index, int ts) throws Exception {
		return waitEle(CSS, locatorStr, index, ts);
	}

	public UIFramework waitEle(EleChain ec, int ts) throws Exception {
		String msg;
		for (int i = 0; i < ts; i++) {
			try {
				int sizes = loadChainSize(ec);
				ArrayList<Ele> eles = ec.getEles();
				if (eles.get(eles.size() - 1).getIndex() < sizes) {
					msg = String.format("waitEle EleChain  times(%d) ", i * 1000);
					Log.I(msg);
					return this;
				}
			} catch (NullPointerException e) {
				throw new Exception(e.getMessage());
			} catch (Exception e) {
				continue;
			}
		}
		msg = String.format("waitEle EleChain  times(%d) timeout", ts * 1000);
		Log.E(msg);
		throw new Exception(msg);
	}

	// ***********************************
	// ********** wait for ele default
	// ***********************************

	public UIFramework waitEleDefault(String type, String locatorStr, int index) throws Exception {
		return waitEle(type, locatorStr, index, DEFAULT_TS);
	}

	public UIFramework waitEleDefault(String locatorStr) throws Exception {
		return waitEle(CSS, locatorStr, 0, DEFAULT_TS);
	}

	public UIFramework waitEleDefault(String type, String locatorStr) throws Exception {
		return waitEle(type, locatorStr, 0, DEFAULT_TS);
	}

	public UIFramework waitEleDefault(String locatorStr, int index) throws Exception {
		return waitEle(CSS, locatorStr, index, DEFAULT_TS);
	}

	public UIFramework waitEleDefault(EleChain ec) throws Exception {
		return waitEle(ec, DEFAULT_TS);
	}

}
