import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject

import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.checkpoint.Checkpoint
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.testcase.TestCase
import com.kms.katalon.core.testdata.TestData
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import com.kms.katalon.core.testobject.SelectorMethod
import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.configuration.RunConfiguration
import com.kms.katalon.core.webui.driver.DriverFactory
import com.kms.katalon.core.webui.common.WebUiCommonHelper

import org.openqa.selenium.WebElement
import org.openqa.selenium.WebDriver
import org.openqa.selenium.By

import internal.GlobalVariable
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.nio.file.Paths
import javax.swing.JFrame
import javax.swing.JOptionPane
import javax.swing.JPanel
import javax.swing.JLabel
import javax.swing.JPasswordField
import org.json.JSONObject
import org.json.JSONArray
import java.util.regex.Pattern

/**
 * Kaixa Version [VERSION]
 * Background: Kaixa is pronounced "Kaisha" and is taken from the Catalan word for "box" as in "toolbox"
 * @author gabeabrams
 */
public class Kaixa {
	// Date constants
	static Date start = Calendar.getInstance().getTime();
	static int msSinceEpoch = start.getTime();

	// Default URL info
	static String defaultHost = (
		GlobalVariable.metaClass.hasProperty(GlobalVariable, 'defaultHost')
			? GlobalVariable.defaultHost
			: 'https://'
	);
	static String defaultProtocol = (
		(GlobalVariable.metaClass.hasProperty(GlobalVariable, 'dontUseHTTPS') && GlobalVariable.dontUseHTTPS)
			? 'http://'
			: 'https://'
	);
	static int defaultCourseId = (
		(GlobalVariable.metaClass.hasProperty(GlobalVariable, 'courseId'))
			? GlobalVariable.courseId
			: null
	);
	static String defaultAppName = (
		(GlobalVariable.metaClass.hasProperty(GlobalVariable, 'appName'))
			? GlobalVariable.appName
			: null
	);

	/* -------------------- Variables, Names, URLs -------------------- */

	// Track number of instances for each name
	static HashMap<String, Integer> nameToNumInstances = new HashMap<String, Integer>();

	/**
	 * Add a unique tag to an object name
	 * @author Gabe Abrams
	 * @param {String} name - the name of the item
	 * @return {String} the new name of the item with the unique tag
	 */
	public static String uniquify(String name) {
		// Start tracking name if not already in map
		if (!nameToNumInstances.containsKey(name)) {
			nameToNumInstances.put(name, 0);
		}
		
		// Increment the number of instances
		int numInstances = nameToNumInstances.get(name) + 1;
		nameToNumInstances.put(name, numInstances);
		
		// Create a unique tag
		DateFormat dateFormat = new SimpleDateFormat('yyyy-mm-dd hh:mm:ss.SSS');
		String strDate = dateFormat.format(start);
		String tag = ' [#' + numInstances + ' ' + strDate + ']';
		
		// Concatenate
		return name + tag;
	}


	/**
	 * Set the default host
	 * @author Gabe Abrams
	 * @param {String} host name
	 */
	public static void setDefaultHost(String host) {
		defaultHost = host.replace('/', '');
	}

	/**
	 * Set whether or not to use HTTPS by default
	 * @author Gabe Abrams
	 * @param {boolean} dontUseHTTPS - true if not using HTTPS by default
	 */
	public static void setDontUseHTTPS(boolean dontUseHTTPS) {
		if (dontUseHTTPS) {
			defaultProtocol = 'http://';
		} else {
			defaultProtocol = 'https://';
		}
	}

	/**
	 * Turn a location into a URL
	 * @author Gabe Abrams
	 * @param {String} location - the location to translate (may be a path or a full URL)
	 * @return {String} URL
	 */
	public static String locationToURL(String location) {
		if (location.startsWith('http://') || location.startsWith('https://')) {
			// Already a URL
			return location;
		}

		// URL is assumed to be a path

		// Create a slash separator if necessary
		String slashSeparator = (location.startsWith('/') ? '' : '/');

		// Turn into a URL
		return defaultProtocol + defaultHost + slashSeparator + location;
	}

	/* -------------------- Logging -------------------- */

	/**
	 * Log a message to the console
	 * @author Gabe Abrams
	 * @param {Object} message - the message to log
	 */
	public static log(Object o) {
		Date now = Calendar.getInstance().getTime();
		DateFormat dateFormat = new SimpleDateFormat('yyyy-mm-dd hh:mm:ss.SSS');
		String strDate = dateFormat.format(now);
		System.out.println(strDate + ' LOG   ' + o);
	}

	/* -------------------- Navigation -------------------- */

	/**
	 * Visit a location in the browser
	 * @author Gabe Abrams
	 * @param {String} location - path or URL to visit
	 */
	public static visit(String location) {
		String url = Kaixa.locationToURL(location);

		try {
			WebUI.navigateToUrl(url)
		} catch (BrowserNotOpenedException) {
			// Browser not opened. Open the browser first
			WebUI.openBrowser(url)
		}
	}

	/* -------------------- Selenium Conversions -------------------- */

	/**
	 * Convert a TestObject into a Selenium WebElement
	 * @author Gabe Abrams
	 * @param {TestObject} obj - the object to convert
	 * @return {WebElement} the corresponding element
	 */
	public static WebElement convertToWebElement(TestObject obj) {
		return WebUiCommonHelper.findWebElement(obj, 10);
	}

	/**
	 * Convert a Selenium WebElement into a TestObject
	 * @author Gabe Abrams
	 * @param {WebElement} obj - the element to convert
	 * @return {TestObject} the corresponding object
	 */
	public static TestObject convertToTestObject(WebElement element) {
		// Get xpath of element
		String elementDescription = element.toString();
		String xpath = elementDescription.substring(elementDescription.lastIndexOf('-> xpath: ') + 10, elementDescription.lastIndexOf(']'));

		// Look up based on xpath
		TestObject testObject = new TestObject();
		testObject.addProperty('xpath', ConditionType.EQUALS, xpath);
		return testObject;
	}

	/* -------------------- Elements -------------------- */

	/**
	 * Given a CSS selector, find the TestObject
	 * @author Gabe Abrams
	 * @param {String} selector - the css selector to search for
	 * @return {TestObject} the item
	 */
	public static TestObject find(String selector) {
		TestObject to = new TestObject('DynamicObjectWithSelector: ' + selector);
		to.setSelectorValue(SelectorMethod.CSS, selector);
		to.setSelectorMethod(SelectorMethod.CSS);

		return to;
	}

	/**
	 * Given the contents and a CSS selector for an element, find the TestObject
	 * Supported css selectors:
	 * All Elements: null or "*"
	 * All P Elements: "p"
	 * All Child Elements of p: "p > *"
	 * Element By ID: "#foo"
	 * Element By Class: ".foo"
	 * Element With Attribute: "*[title]"
	 * First Child of P: "p > *:first-child"
	 * Next Element after P: "p + *"
	 * @author Gabe Abrams
	 */
	public static TestObject findByContents(String contents, String selector) {
		// Generate the xpath
		String start = '*';
		if (selector.startsWith('#')) {
			// id
			start = '*[@id=\'' + selector.substring(1) + '\']';
		} else if (selector.startsWith('.')) {
			// class
			start = '*[contains(@class,\'' + selector.substring(1) + '\')]';
		} else if (selector.startsWith('*[')) {
			// element with attribute
			start = '*[@' + selector.substring(2) +']';
		} else if (selector == '*') {
			// Wild card
			start = '*';
		} else {
			// just a tag names
			String tagName = selector.split(Pattern.quote('>'))[0].split(Pattern.quote('+'))[0];
			if (selector.indexOf('+') >= 0) {
				// assume looking for next sibling
				start = tagName + '/following-sibling::*[0]';
			} else if (selector.indexOf('>') >= 0) {
				if (selector.indexOf('first-child') >= 0) {
					// assume looking for first child of tagName
					start = tagName + '/*[0]';
				} else {
					// assume looking for all children
					start = tagName + '/*';
				}
			} else {
				// assume just a tag name
				start = tagName;
			}
		}

		// Build the xpath
		String contentsEscaped;
		if (contents.indexOf('\'') < 0) {
			contentsEscaped = '\'' + contents + '\'';
		} else if (contents.indexOf('"') < 0) {
			contentsEscaped = '"' + contents + '"';
		} else {
			contentsEscaped = 'concat(\'' + contents.replace('\'', '\',"\'", \'') + '\')';
		}
		String xpath = '//' + start + '[text()[contains(.,' + contentsEscaped + ')]]';

		// Find the element now that we have its xpath
		TestObject to = new TestObject('DynamicContentsTestObjectWithContents:' + contents + '_AndSelector:' + selector);
		to.addProperty('xpath', ConditionType.EQUALS, xpath);

		return to;
	}

	/**
	 * Make sure an item is a TestObject. If it is a CSS selector, turn it into a TestObject
	 * @author Gabe Abrams
	 * @param {TestObject|String} item - the TestObject or CSS selector
	 * @return {TestObject} a TestObject
	 */
	public static TestObject ensureTestObject(Object item) {
		if (item instanceof TestObject) {
			return item;
		}
		return Kaixa.find((String)item);
	}

	/**
	 * Get an attribute of an element
	 * @author Gabe Abrams
	 * @param {TestObject|String} item - the TestObject or CSS selector
	 * @param {String} attribute - the name of the attribute to get
	 * @return {String} value of the attribute
	 */
	public static String getAttribute(Object item, String attribute) {
		TestObject obj = Kaixa.ensureTestObject(item);
		return WebUI.getAttribute(obj, attribute);
	}

	/**
	 * Check if an element exists
	 * @author Gabe Abrams
	 * @param {TestObject|String} item - the TestObject or CSS selector
	 * @return {boolean} true if the element exists on the page
	 */
	public static boolean elementExists(Object item) {
		TestObject obj = Kaixa.ensureTestObject(item);
		return WebUI.verifyElementPresent(obj, 1, FailureHandling.OPTIONAL);
	}

	/**
	 * Check if an element does not exist
	 * @author Gabe Abrams
	 * @param {TestObject|String} item - the TestObject or CSS selector
	 * @return {boolean} true if the element does not exist on the page
	 */
	public static boolean elementAbsent(Object item) {
		TestObject obj = Kaixa.ensureTestObject(item);
		return !WebUI.verifyElementPresent(obj, 1, FailureHandling.OPTIONAL);
	}
	
	/**
	 * Check if an element with specific contents exists
	 * @author Gabe Abrams
	 * @param {String} contents - the contents to search for
	 * @param {String} selector - a CSS selector corresponding to the item
	 * @return {boolean} true if the element exists on the page
	 */
	public static boolean elementWithContentsExists(String contents, String selector) {
		TestObject obj = Kaixa.findByContents(contents, selector);
		return WebUI.verifyElementPresent(obj, 1, FailureHandling.OPTIONAL);
	}

	/**
	 * Check if an element with specific contents does not exist
	 * @param {String} contents - the contents to search for
	 * @param {String} selector - a CSS selector corresponding to the item
	 * @return {boolean} true if the element does not exist on the page
	 */
	public static boolean elementWithContentsAbsent(String contents, String selector) {
		TestObject obj = Kaixa.findByContents(contents, selector);
		return !WebUI.verifyElementPresent(obj, 1, FailureHandling.OPTIONAL);
	}

	/**
	 * Get the parent of an element
	 * @author Gabe Abrams
	 * @param {TestObject|String} item - the TestObject or CSS selector
	 * @return {TestObject} parent element
	 */
	public static TestObject parentOf(Object item) {
		TestObject obj = Kaixa.ensureTestObject(item);

		// Convert to WebElement
		WebElement el = Kaixa.convertToWebElement(obj);

		// Find parent
		WebElement parent = el.findElement(By.xpath('./..'));

		// Convert back to TestObject
		return Kaixa.convertToTestObject(parent);
	}

	/**
	 * Get the first descendant of an element that matches the selector
	 * @author Gabe Abrams
	 * @param {TestObject|String} item - the TestObject or CSS selector of the parent
	 * @param {String} selector - the selector to use to search for the descendant
	 * @return {TestObject} child element 
	 */
	public static TestObject descendantOf(Object item, String selector) {
		TestObject obj = Kaixa.ensureTestObject(item);

		// Convert to WebElement
		WebElement el = Kaixa.convertToWebElement(obj);

		// Get the descendant
		TestObject descendant = el.findElement(By.css(selector));

		// Convert back to TestObject
		return Kaixa.convertToTestObject(descendant);
	}

	/* -------------------- Waiting -------------------- */

	/**
	 * Pause until the user clicks okay
	 * @author Gabe Abrams
	 */
	public static void pause() {
		JFrame frame = new JFrame('Kaixa');
		frame.requestFocus();
		JOptionPane.showMessageDialog(null, 'Test case paused. Click "OK" to continue...');
	}

	/**
	 * Wait for a specific number of milliseconds
	 * @author Gabe Abrams
	 * @param {int} ms - the number of ms to wait
	 */
	public static void waitFor(int ms = 0) {
		Thread.sleep(ms);
	}


	/**
	 * Wait for an element to be visible
	 * @author Gabe Abrams
	 * @param {TestObject|String} item - the TestObject or CSS selector of interest
	 * @param {int} [timeoutSec=10] - the number of seconds to wait before timing out
	 */
	public static void waitForElementVisible(Object item, int timeoutSec = 10) {
		try {
			assert WebUI.waitForElementVisible(Kaixa.ensureTestObject(item), timeoutSec);
		} catch (AssertionError e) {
			throw new Exception('Element "' + item + '" not become visible within ' + timeoutSec + ' second(s)');
		}
	}

	/**
	 * Wait for an element with specific contents to be visible
	 * @author Gabe Abrams
	 * @param {String} contents - the contents to search for
	 * @param {String} selector - a CSS selector corresponding to the item
	 * @param {int} [timeoutSec=10] - the number of seconds to wait before timing out
	 */
	public static void waitForElementWithContentsVisible(String contents, String selector, int timeoutSec) {
		try {
			WebUI.waitForElementVisible(Kaixa.findByContents(contents, selector), timeoutSec);
		} catch (AssertionError e) {
			throw new Exception('Element "' + selector + '" with contents "' + contents + '" did not become visible within ' + timeoutSec + ' second(s)');
		}
	}

	/* -------------------- Assertions -------------------- */

	/**
	 * Make sure an element exists
	 * @author Gabe Abrams
	 * @param {TestObject|String} item - the TestObject or CSS selector
	 * @param {String} [message=generated message] - a human-readable message to
	 *   display if the test fails
	 * @param {int} [gracePeriodSecs=10] - the number of seconds to wait before
	 *   throwing an error
	 */
	public static boolean assertExists(Object item, String message = '', int gracePeriodSecs = 10) {
		try {
			Kaixa.waitForElementVisible(item, gracePeriodSecs);
		} catch (Exception e) {
			// Could not find!
			throw new Exception(
				message == ''
					? 'Element "' + item + '" did not exist, but it should have been there.'
					: message
			);
		}
	}

	/**
	 * Make sure an element does not exist
	 * @author Gabe Abrams
	 * @param {TestObject|String} item - the TestObject or CSS selector
	 * @param {String} [message=generated message] - a human-readable message to
	 *   display if the test fails
	 * @return {boolean} true if the element does not exist on the page
	 */
	public static boolean assertAbsent(Object item, String message = '') {
		TestObject obj = Kaixa.ensureTestObject(item);
		boolean exists = !WebUI.verifyElementPresent(obj, 1, FailureHandling.OPTIONAL);

		if (exists) {
			// Found but shouldn't have
			throw new Exception(
				message == ''
					? 'Element "' + item + '" exist, but it should have been absent.'
					: message
			);
		}
	}
	
	/**
	 * Make sure an element with specific contents exists
	 * @author Gabe Abrams
	 * @param {String} contents - the contents to search for
	 * @param {String} selector - a CSS selector corresponding to the item
	 * @param {String} [message=generated message] - a human-readable message to
	 *   display if the test fails
	 * @param {int} [gracePeriodSecs=10] - the number of seconds to wait before
	 *   throwing an error
	 * @return {boolean} true if the element exists on the page
	 */
	public static boolean assertExistsWithContents(String contents, String selector, String message = '', int gracePeriodSecs = 10) {
		try {
			Kaixa.waitForElementWithContentsVisible(contents, selector, gracePeriodSecs);
		} catch (Exception e) {
			// Could not find!
			throw new Exception(
				message == ''
					? 'Element "' + selector + '" with contents + "' + contents + '" did not exist, but it should have been there.'
					: message
			);
		}
	}

	/**
	 * Make sure an element with specific contents does not exist
	 * @param {String} contents - the contents to search for
	 * @param {String} selector - a CSS selector corresponding to the item
	 * @param {String} [message=generated message] - a human-readable message to
	 *   display if the test fails
	 * @return {boolean} true if the element does not exist on the page
	 */
	public static boolean assertAbsentWithContents(String contents, String selector, String message = '') {
		TestObject obj = Kaixa.findByContents(contents, selector);
		boolean exists = WebUI.verifyElementPresent(obj, 1, FailureHandling.OPTIONAL);

		if (exists) {
			// Found but shouldn't have
			throw new Exception(
				message == ''
					? 'Element "' + selector + '" with contents "' + contents + '" exist, but it should have been absent.'
					: message
			);
		}
	}

	/* -------------------- Interactions-------------------- */

	/**
	 * Click an element
	 * @author Gabe Abrams
	 * @param {TestObject|String} item - the TestObject or CSS selector of interest
	 * @param {int} [timeoutSec=10] - the number of seconds to wait before timing out
	 */
	public static void click(Object item, int timeoutSec = 10) {
		TestObject obj = Kaixa.ensureTestObject(item);
		Kaixa.waitForElementVisible(obj, timeoutSec);
		WebUI.click(obj);
	}

	/**
	 * Open an anchor link in the same tab
	 * @author Gabe Abrams
	 * @param {TestObject|String} item - the TestObject or CSS selector of interest 
	 */
	public static void openAnchorInSameTab(Object item) {
		TestObject obj = Kaixa.ensureTestObject(item);
		String href = Kaixa.getAttribute(obj, 'href');
		Kaixa.visit(href);
	}

	/**
	 * Click an item by defining its contents
	 * @author Gabe Abrams
	 * @param {String} contents - the contents to search for
	 * @param {String} selector - a CSS selector corresponding to the item
	 */
	public static void clickByContents(String contents, String selector) {
		TestObject obj = Kaixa.findByContents(contents, selector);
		Kaixa.click(obj);
	}

	/**
	 * Type text into an element. This function first removes the previous text in the element
	 * @author Gabe Abrams
	 * @param {TestObject|String} item - the TestObject or CSS selector of interest
	 * @param {String} text - the text to type
	 */
	public static void typeInto(Object item, String text) {
		TestObject obj = Kaixa.ensureTestObject(item);
		Kaixa.waitForElementVisible(obj);
		WebUI.setText(obj, text);
	}

	/**
	 * Scroll the page to the element
	 * @author Gabe Abrams
	 * @param {TestObject|String} item - the TestObject or CSS selector of interest
	 */
	public static void scrollTo(Object item) {
		TestObject obj = Kaixa.ensureTestObject(item);
		WebUI.scrollToElement(obj, 10);
	}

	/**
	 * Choose an item in a select element based on its label
	 * @author Gabe Abrams
	 * @param {TestObject|String} item - the TestObject or CSS selector of the select element
	 * @param {String} label - the label to select in the dropdown
	 */
	public static void chooseSelectByLabel(Object item, String label) {
		TestObject obj = Kaixa.ensureTestObject(item);

		// Select the option
		WebUI.selectOptionByLabel(obj, label, false);

		// Verify the selection
		WebUI.verifyOptionSelectedByLabel(obj, label, false, 60);
	}

	/**
	 * Choose an item in a select element based on its value
	 * @author Gabe Abrams
	 * @param {TestObject|String} item - the TestObject or CSS selector of the select element
	 * @param {String} value - the value of the item to select in the dropdown
	 */
	public static void chooseSelectByValue(Object item, String value) {
		TestObject obj = Kaixa.ensureTestObject(item);

		// Select the option
		WebUI.selectOptionByValue(obj, value, false);

		// Verify the selection
		WebUI.verifyOptionSelectedByValue(obj, value, false, 60);
	}

	/**
	 * Choose a file for a file chooser
	 * @author Gabe Abrams
	 * @param {TestObject|String} item - the TestObject or CSS selector of the select element
	 * @param {String} filePath - the path of the file
	 */
	public static void chooseFile(Object item, String relativePath) {
		TestObject obj = Kaixa.ensureTestObject(item);

		// Get the absolute path of the file
		String absolutePath = Paths.get(RunConfiguration.getProjectDir(), relativePath).toAbsolutePath();

		// Select the file
		WebUI.uploadFile(obj, absolutePath);
	}

	/**
	 * Run a script on the page
	 * @author Gabe Abrams
	 * @param {String} script - the script to run in an anonymous function on the page.
	 *   If multiple script arguments are included, each argument will be considered a
	 *   new line of the script.
	 * @return {Object} return value
	 */
	public static Object runScript(String... scriptLines) {
		String fullScript = String.join('\n', scriptLines);
		return WebUI.executeJavaScript(fullScript, null);
	}

	/* -------------------- Browser Actions -------------------- */

	/**
	 * Close the current window/tab
	 * @author Gabe Abrams
	 */
	public static void closeWindow() {
		// Get the index of the current window
		int index = WebUI.getWindowIndex();

		// Close the window
		WebUI.closeWindowIndex(index);
	}

	/* -------------------- Data -------------------- */

	/**
	 * Get the title of the current window
	 * @author Gabe Abrams
	 * @return {String} the title of the window
	 */
	public static String getTitle() {
		return WebUI.getWindowTitle();
	}

	/**
	 * Get the source of the page
	 * @author Gabe Abrams
	 * @return {String} the source of the current page
	 */
	public static String getSource() {
		WebDriver driver = DriverFactory.getWebDriver();
		return driver.getPageSource();
	}

	/**
	 * Get the current URL of the page
	 * @author Gabe Abrams
	 * @return {String} the source of the current page
	 */
	public static String getURL() {
		return WebUI.getUrl();
	}

	/**
	 * Get the current URL of the page
	 * @author Gabe Abrams
	 * @return {Map<String, String>} map of query parameters 
	 */
	public static Map<String, String> getQuery() throws UnsupportedEncodingException {
		Map<String, String> queryPairs = new LinkedHashMap<String, String>();

		// Get the URL
		String url = Kaixa.getURL();
		if (url.indexOf('?') < 0) {
			// No query! Just return empty map
			return queryPairs;
		}

		// Get the query string
		String query = url.split(Pattern.quote('?'))[1];

		// Split into pairs of values
		String[] pairs = query.split(Pattern.quote('&'));
		for (String pair : pairs) {
			int index = pair.indexOf('=');
			String key = URLDecoder.decode(pair.substring(0, index), 'UTF-8');
			String value = URLDecoder.decode(pair.substring(index + 1), 'UTF-8');
			queryPairs.put(key, value);
		}

		// Return
		return queryPairs;
	}

	/**
	 * Get the JSON string on the current page
	 * @author Gabe Abrams
	 * @return {String} the JSON string
	 */
	public static String getJSONString() {
		// Get the contents of the formatted pre tag
		return Kaixa.runScript(
			'const preElems = document.getElementsByTagName("pre");',
			'if (preElems.length > 0) {',
			'  const contents = preElems[0].innerHTML;',
			'  return (',
			'    contents.startsWith("while(1);")',
			'      ? contents.replace("while(1);", "")',
			'      : contents',
			'  );',
			'}',
			'return "{}"'
		);
	}

	/**
	 * Get the JSON object on the page
	 * @author Gabe Abrams
	 * @return {JSONObject} the JSON object on the page
	 */
	public static JSONObject getJSONObject() {
		return new JSONObject(Kaixa.getJSONString());
	}

	/**
	 * Get the JSON array on the page
	 * @author Gabe Abrams
	 * @return {JSONArray} the JSON array on the page
	 */
	public static JSONArray getJSONArray() {
		return new JSONArray(Kaixa.getJSONString());
	}

	/**
	 * Get the JSON object or array on the page
	 * @author Gabe Abrams
	 * @return {JSONObject|JSONArray} the JSON info on the page
	 */
	public static Object getJSON() {
		// Get the text
		String jsonString = Kaixa.getJSONString();
		System.out.println(jsonString);

		// Detect object type
		if (jsonString.charAt(0) == '[') {
			// This is an array
			return new JSONArray(jsonString);
		} else {
			// This is an object
			return new JSONObject(jsonString);
		}
	}

	/**
	 * Get the value of a Canvas GET API endpoint. Automatically adds a per_page=200 param. A valid Canvas session
	 *   must be active already.
	 * @author Gabe Abrams
	 * @param {String} path - the path of the API, excluding https://canvas.harvard.edu/api/v1, example: "/users"
	 * @return {JSONArray|JSONObject} Canvas response
	 */
	public static Object visitCanvasEndpoint(String path) {
		String id = 'Kaixa-open-new-tab-button';
		String url = 'https://canvas.harvard.edu/api/v1' + path + (path.indexOf('?') >= 0 ? '&per_page=200' : '?per_page=200');

		// Add a button that opens a new tab
		Kaixa.runScript(
			'const elemId = "' + id + '";',
			// Create the element if it doesn't exist yet
			'if (!document.getElementById(elemId)) {',
			'  const a = document.createElement("a");',
			'  a.id = elemId;',
			'  a.href = "' + url + '";',
			'  a.target = "_blank";',
			'  a.innerHTML = "Open in New Tab (for Testing)";',
			'  a.style = "position: fixed; top: 0; left: 0; z-index: 20000000";',
			'  document.body.appendChild(a);',
			'}'
		);

		// Save the current window index
		int currentTab = WebUI.getWindowIndex();

		// Open the new tab
		Kaixa.click('#Kaixa-open-new-tab-button');
		WebUI.switchToWindowIndex(currentTab + 1);

		// Get the JSON
		Object json = Kaixa.getJSON();

		// Close the window
		Kaixa.closeWindow();

		// Navigate back to the previous window
		WebUI.switchToWindowIndex(currentTab);

		// Remove the new tab button
		Kaixa.runScript(
			'const elemId = "' + id + '";',
			'const elem = document.getElementById(elemId);',
			'elem.parentElement.removeChild(elem);'
		);

		// Return the JSON
		return json;
	}

	/* -------------------- Harvard-specific Commands -------------------- */

	/**
	 * Log into Canvas and launch an LTI app
	 * @author Gabe Abrams
	 * @param {String} username - the username of the user
	 * @param {String} password - the password of the user
	 * @param {int} [courseId=courseId from profile] - the Canvas ID of the course to launch from
	 * @param {String} [appName=appName from profile] - the name of the app as it appears in the course's left-hand nav 
	 * @param {boolean} [isXID] - if true, the user is an XID user
	 */
	public static void launchLTIUsingCreds(String username, String password, int courseId = defaultCourseId, String appName = defaultAppName, boolean isXID = false) {
		// Visit the HarvardKey login service for Canvas
		Kaixa.visit('https://www.pin1.harvard.edu/cas/login?service=https%3A%2F%2Fcanvas.harvard.edu%2Flogin%2Fcas')

		// If isXID, click the "XID" button
		if (isXID) {
			Kaixa.click('#XID');
		}

		// Type the username and password
		Kaixa.typeInto('#username', username);
		Kaixa.typeInto('#password', password);

		// Click submit
		Kaixa.click('#submitLogin');

		// Wait for login to finish (wait up to 30s)
		Kaixa.waitForElementVisible('#global_nav_dashboard_link', 30);

		// Get the external tool URL
		JSONArray externalTools = Kaixa.visitCanvasEndpoint('/courses/' + courseId + '/external_tools');
		
		// Find the external tool of interest
		int toolId = 0;
		for (int i = 0; i < externalTools.length(); i++) {
			JSONObject externalTool = externalTools.getJSONObject(i);

			// Skip non-nav items
			if (
				!externalTool.has('course_navigation')
				|| !(externalTool.get('course_navigation') instanceof JSONObject)
			) {
				continue;
			}
			
			// Get nav info
			System.out.println(externalTool.get('course_navigation'));
			JSONObject courseNavigation = externalTool.getJSONObject('course_navigation');
			
			// Skip non-labeled items
			if (!courseNavigation.has('text')) {
				continue;
			}
			
			// Skip apps that don't match the name
			String thisAppName = courseNavigation.getString('text').trim().toLowerCase();
			if (thisAppName != appName.trim().toLowerCase()) {
				continue;
			}
			
			// Found the app!
			toolId = externalTool.getInt('id');
		}
		
		// Make sure we found the app
		if (toolId == 0) {
			throw new Exception('We could not find any apps named "' + appName + '" in course ' + courseId + '.');
		}

		// Go to the course
		Kaixa.visit('https://canvas.harvard.edu/courses/' + courseId + '/external_tools/' + toolId + '?display=borderless');
	}

	/**
	 * Log into Canvas and launch an LTI app as a specific user from the profile variables.
	 *   The value should be a JSON object with the following properties: { username, [password], [isXID] }
	 * @author Gabe Abrams
	 * @param {String} name - the name of the variable containing the credentials for the user
	 * @param {int} [courseId=courseId from profile] - the Canvas ID of the course to launch from
	 * @param {String} [appName=appName from profile] - the name of the app as it appears in the course's left-hand nav
	 */
	public static void launchAs(String name, int courseId = defaultCourseId, String appName = defaultAppName) {
		// Make sure the user exists
		if (!GlobalVariable.metaClass.hasProperty(GlobalVariable, name)) {
			throw new Exception('Could not launch as "' + name + '" because that user is not listed in the profile variables.');
		}

		// Get the user info
		JSONObject obj = new JSONObject(GlobalVariable[name]);
		String username = obj.getString('username');

		// Prompt user for password if its not included
		String password = '';
		if (obj.has('password')) {
			password = obj.getString('password');
		} else {
			// Create a password input pane
			JPanel panel = new JPanel();
			// > Label
			JLabel label = new JLabel('Password:');
			// > Password field
			JPasswordField pass = new JPasswordField(30);
			// > Add the label and password field to the panel
			panel.add(label);
			panel.add(pass);
			
			// Create the continue button
			String[] options = new String[1];
			options[0] = 'Continue';

			// Prompt user
			JOptionPane.showOptionDialog(
				null,
				panel,
				'Password for "' + name + '"',
				JOptionPane.NO_OPTION,
				JOptionPane.PLAIN_MESSAGE,
				null,
				options,
				options[0]
			);

			// Get the password
			password = new String(pass.getPassword());
		}
		
		// Make sure there is a password
		if (password == '') {
			throw new Exception('Password cannot be empty.');
		}

		boolean isXID = (
			obj.has('isXID')
			&& obj.getBoolean('isXID')
		);

		// Perform launch
		Kaixa.launchLTIUsingCreds(username, password, courseId, appName, isXID);
	}
}
