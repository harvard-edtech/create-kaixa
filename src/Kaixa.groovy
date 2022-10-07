/**
 * Kaixa commands
 * @namespace Kaixa
 * @author Gabe Abrams
 */

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
import java.nio.file.Paths
import javax.swing.JFrame
import javax.swing.JOptionPane
import javax.swing.JPanel
import javax.swing.JLabel
import javax.swing.JPasswordField
import javax.swing.JTextField
import org.json.JSONObject
import org.json.JSONArray
import java.util.regex.Pattern
import java.text.DateFormat
import java.text.SimpleDateFormat

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
			: 12345
	);
	static String defaultAppName = (
		(GlobalVariable.metaClass.hasProperty(GlobalVariable, 'appName'))
			? GlobalVariable.appName
			: 'No App Name'
	);

	// Cache usernames
	static HashMap<String,String> cachedUsernames = new HashMap<String,String>();
	// Cache passwords
	static HashMap<String,String> cachedPasswords = new HashMap<String,String>();
	// Cache access tokens
	static HashMap<String,String> cachedAccessTokens = new HashMap<String,String>();

	/* -------------------- Variables, Names, URLs -------------------- */

	// Track number of instances for each name
	static HashMap<String, Integer> nameToNumInstances = new HashMap<String, Integer>();

	/**
	 * Converts a number to an ascii representation (same string length)
	 * @author Gabe Abrams
	 * @param {int} num - the number to convert to an ascii representation
	 * @return {String} alpha ascii representation
	 */
	private static String _numToAscii(long num) {
		String[] letters = 'abcdefghij'.split('');
		String[] digits = (new Long(num)).toString().split('');

		String output = '';
		for (int i = 0; i < digits.length; i++) {
			output = output + letters[Integer.parseInt(digits[i])];
		}

    return output;
	}

	/**
	 * Add a unique tag to an object name. Tag may add up to 20 chars.
	 * @author Gabe Abrams
	 * @instance
	 * @memberof Kaixa
	 * @method uniquify
	 * @param {String} name - the name of the item
	 * @return {String} the new name of the item with the unique tag
	 */
	public static String uniquify(Object name) {
		// Start tracking name if not already in map
		if (!nameToNumInstances.containsKey(name.toString())) {
			nameToNumInstances.put(name.toString(), 0);
		}
		
		// Increment the number of instances
		int numInstances = nameToNumInstances.get(name.toString()) + 1;
		nameToNumInstances.put(name.toString(), numInstances);
		
		// Create a unique tag
		String tag = ' [' + numInstances + '-' + (new Date()).getTime() + ']';
		
		// Concatenate
		return name.toString() + tag;
	}

	/**
	 * Add a simple unique tag to an object name, where the simple tag is just
	 *   numbers. Tag will be at most 16 chars
	 * @author Gabe Abrams
	 * @instance
	 * @memberof Kaixa
	 * @method uniquifySimple
	 * @param {String} name - the name of the item
	 * @return {String} the new name of the item with the unique tag
	 */
	public static String uniquifySimple(Object name) {
		// Start tracking name if not already in map
		if (!nameToNumInstances.containsKey(name.toString())) {
			nameToNumInstances.put(name.toString(), 0);
		}
		
		// Increment the number of instances
		int numInstances = nameToNumInstances.get(name.toString()) + 1;
		nameToNumInstances.put(name.toString(), numInstances);
		
		// Create a unique tag
		String tag = _numToAscii(numInstances % 1000) + _numToAscii((new Date()).getTime());
		
		// Concatenate
		return name.toString() + tag;
	}

	// Prompt helper
	public static String _prompt(String label, String title, boolean isPass) {
		// Ask user for text
		JPanel panel = new JPanel();
		// > Label
		JLabel jLabel = new JLabel(label);
		// > text field
		JTextField input;
		if (isPass) {
			input = new JPasswordField(30);
		} else {
			input = new JTextField(30);
		}
		// > Add the label and input field to the panel
		panel.add(jLabel);
		panel.add(input);
		
		// Create the continue button
		String[] options = new String[1];
		options[0] = 'Continue';

		// Prompt user
		JOptionPane.showOptionDialog(
			null,
			panel,
			title,
			JOptionPane.NO_OPTION,
			JOptionPane.PLAIN_MESSAGE,
			null,
			options,
			options[0]
		);

		// Get the value
		String value = input.getText();

		// Make sure there is a password
		if (value == '') {
			throw new Exception('Typed text cannot be empty.');
		}

		return value;
	}

	/**
	 * Get text from a test runner user. Throws an error if the text is empty
	 * @author Gabe Abrams
	 * @instance
	 * @memberof Kaixa
	 * @method prompt
	 * @param {String} label - the label to put in front of the text field
	 * @param {String} title - the title of the prompt window
	 * @return {String} the trimmed text that the user entered
	 */
	public static String prompt(String label, String title) {
		return _prompt(label, title, false);
	}

	/**
	 * Get a password from a test runner user. Throws an error if the text is
	 *  empty
	 * @author Gabe Abrams
	 * @instance
	 * @memberof Kaixa
	 * @method promptPassword
	 * @param {String} label - the label to put in front of the text field
	 * @param {String} title - the title of the prompt window
	 * @return {String} the trimmed text that the user entered
	 */
	public static String promptPassword(String label, String title) {
		return _prompt(label, title, true);
	}

	/**
	 * Set the default host
	 * @author Gabe Abrams
	 * @instance
	 * @memberof Kaixa
	 * @method setDefaultHost
	 * @param {String} host name
	 */
	public static void setDefaultHost(String host) {
		defaultHost = host.replace('/', '');
	}

	/**
	 * Set whether or not to use HTTPS by default
	 * @author Gabe Abrams
	 * @instance
	 * @memberof Kaixa
	 * @method setDontUseHTTPS
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
	 * @instance
	 * @memberof Kaixa
	 * @method locationToURL
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

	/**
	 * Get a value from the profile
	 * @author Gabe Abrams
	 * @instance
	 * @memberof Kaixa
	 * @method getProfileValue
	 * @param {String} name - the name of the profile variable
	 * @return {String} the value
	 */
	public static String getProfileValue(String name) {
		String value = (
			(GlobalVariable.metaClass.hasProperty(GlobalVariable, name))
				? GlobalVariable[name]
				: null
		);

		return value;
	}

	/**
	 * Get the current year
	 * @author Gabe Abrams
	 * @instance
	 * @memberof Kaixa
	 * @method getCurrentYear
	 * @return {int} year
	 */
	public static int getCurrentYear() {
		Date now = new Date();
		int year = now.getYear() + 1900;
		return year;
	}

	/**
	 * Get the current month
	 * @author Gabe Abrams
	 * @instance
	 * @memberof Kaixa
	 * @method getCurrentMonth
	 * @return {int} month
	 */
	public static int getCurrentMonth() {
		Date now = new Date();
		int month = now.getMonth() + 1;
		return month;
	}

	/**
	 * Get the current day
	 * @author Gabe Abrams
	 * @instance
	 * @memberof Kaixa
	 * @method getCurrentDay
	 * @return {int} day
	 */
	public static int getCurrentDay() {
		Date now = new Date();
		int date = now.getDate();
		return date;
	}

	/**
	 * Get the year after a certain number of days have passed
	 * @author Gabe Abrams
	 * @instance
	 * @memberof Kaixa
	 * @method getYearAfterDays
	 * @param {int} days - number of days that will have passed (can be negative)
	 * @return {int} year
	 */
	public static int getYearAfterDays(int days) {
		Date now = (new Date()).plus(days);
		int year = now.getYear() + 1900;
		return year;
	}

	/**
	 * Get the month after a certain number of days have passed
	 * @author Gabe Abrams
	 * @instance
	 * @memberof Kaixa
	 * @method getMonthAfterDays
	 * @param {int} days - number of days that will have passed (can be negative)
	 * @return {int} month
	 */
	public static int getMonthAfterDays(int days) {
		Date now = (new Date()).plus(days);
		int month = now.getMonth() + 1;
		return month;
	}

	/**
	 * Get the day after a certain number of days have passed
	 * @author Gabe Abrams
	 * @instance
	 * @memberof Kaixa
	 * @method getDayAfterDays
	 * @param {int} days - number of days that will have passed (can be negative)
	 * @return {int} day
	 */
	public static int getDayAfterDays(int days) {
		Date now = (new Date()).plus(days);
		int date = now.getDate();
		return date;
	}

	/**
	 * Pad a number with zeros
	 * @author Gabe Abrams
	 * @instance
	 * @memberof Kaixa
	 * @method padWithZeros
	 * @param {int} number - the number to pad
	 * @param {int} digits - number of digits to require (must be at least this
	 *   long
	 * @return {String} padded number
	 */
	public static String padNumber(int number, int digits) {
		String out = number.toString();
		while (out.length() < digits) {
			out = '0' + out;
		}
		return out;
	}

	/**
	 * Get the current date as a fully-formed string
	 * @author Gabe Abrams
	 * @instance
	 * @memberof Kaixa
	 * @method getDateString
	 * @return {String} date in form mm/dd/yyyy
	 */
	public static String getDateString() {
		return Kaixa.padNumber(Kaixa.getCurrentMonth(), 2) + '/' + Kaixa.padNumber(Kaixa.getCurrentDay(), 2) + '/' + Kaixa.getCurrentYear();
	}

	/**
	 * Get the current date as a fully-formed string
	 * @author Gabe Abrams
	 * @instance
	 * @memberof Kaixa
	 * @method getDateString
	 * @param {int} days - number of days that will have passed (can be negative)
	 * @return {String} date in form mm/dd/yyyy
	 */
	 public static String getDateStringAfterDays(int days) {
		 return Kaixa.padNumber(Kaixa.getMonthAfterDays(days), 2) + '/' + Kaixa.padNumber(Kaixa.getDayAfterDays(days), 2) + '/' + Kaixa.getYearAfterDays(days);
	 }

	 /**
		* Check if the currently-opened window is a Safari browser window
		* @author Gabe Abrams
		* @instance
		* @memberof Kaixa
		* @method isSafari
		* @return {boolean} true if the browser is Safari
		*/
		public static boolean isSafari() {
			return Kaixa.runScript('return /^((?!chrome|android).)*safari/i.test(navigator.userAgent)');
		}

	/* -------------------- Logging -------------------- */

	/**
	 * Log a message to the console
	 * @author Gabe Abrams
	 * @instance
	 * @memberof Kaixa
	 * @method log
	 * @param {Object} message - the message to log
	 */
	public static void log(Object o) {
		String msg = o.toString();
		msg = msg.replace('TestObject - \'DynamicObjectWithSelector: ', '\'');
		// Replace dynamic 
		WebUI.comment(msg);
	}

	/* -------------------- Navigation -------------------- */

	/**
	 * Visit a location in the browser
	 * @author Gabe Abrams
	 * @instance
	 * @memberof Kaixa
	 * @method visit
	 * @param {String} location - path or URL to visit
	 */
	public static void visit(String location) {
		String url = Kaixa.locationToURL(location);
		Kaixa.log('🌐 Visit ' + location);

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
	 * @instance
	 * @memberof Kaixa
	 * @method convertToWebElement
	 * @param {TestObject} obj - the object to convert
	 * @return {WebElement} the corresponding element
	 */
	public static WebElement convertToWebElement(TestObject obj) {
		return WebUiCommonHelper.findWebElement(obj, 10);
	}

	/**
	 * Convert a Selenium WebElement into a TestObject
	 * @author Gabe Abrams
	 * @instance
	 * @memberof Kaixa
	 * @method convertToTestObject
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
	 * @instance
	 * @memberof Kaixa
	 * @method find
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
	 * Given an XPATH selector, find the TestObject
	 * @author Rute Santos
	 * @instance
	 * @memberof Kaixa
	 * @method findByXPath
	 * @param {String} xPath - the xpath selector to search for
	 * @return {TestObject} the item
	 */
	public static TestObject findByXPath(String xPath) {
		TestObject to = new TestObject('DynamicObjectWithXPath: ' + xPath);
		to.setSelectorValue(SelectorMethod.XPATH, xPath);
		to.setSelectorMethod(SelectorMethod.XPATH);

		return to;
	}

	/**
	 * Get an xpath for finding an element based on a css selector and contents
	 * @author Gabe Abrams
	 * @instance
	 * @memberof Kaixa
	 * @method getContentsXPath
	 * @param {String} contents - the contents of the object
	 * @param {String} selector - a css selector for the element
	 * Supported css selectors:
	 * All Elements: null or "*"
	 * All P Elements: "p"
	 * All Child Elements of p: "p > *"
	 * Element By ID: "#foo"
	 * Element By Class: ".foo"
	 * Element With Attribute: "*[title]"
	 * First Child of P: "p > *:first-child"
	 * Next Element after P: "p + *"
	 * @return {String} xpath to use to find the element
	 */
	public static String getContentsXPath(Object contents, String selector) {
		// Generate the start of the xpath
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
		if (contents.toString().indexOf('\'') < 0) {
			contentsEscaped = '\'' + contents + '\'';
		} else if (contents.toString().indexOf('"') < 0) {
			contentsEscaped = '"' + contents + '"';
		} else {
			contentsEscaped = 'concat(\'' + contents.toString().replace('\'', '\',"\'", \'') + '\')';
		}
		String xpath = '//' + start + '[text()[contains(.,' + contentsEscaped + ')]]';

		return xpath;
	}

	/**
	 * Given the contents and a CSS selector for an element, find the TestObject
	 * @author Gabe Abrams
	 * @instance
	 * @memberof Kaixa
	 * @method findByContents
	 * @param {String} contents - the contents of the object.
	 *   NOTE: must be the contents of the element,
	 *   not contents of a child element!
	 * @param {String} selector - a css selector for the element
	 * Supported css selectors:
	 * All Elements: null or "*"
	 * All P Elements: "p"
	 * All Child Elements of p: "p > *"
	 * Element By ID: "#foo"
	 * Element By Class: ".foo"
	 * Element With Attribute: "*[title]"
	 * First Child of P: "p > *:first-child"
	 * Next Element after P: "p + *"
	 * @return {TestObject} the matching object
	 */
	public static TestObject findByContents(Object contents, String selector) {
		// Get the xpath
		String xpath = Kaixa.getContentsXPath(contents, selector);

		// Find the element now that we have its xpath
		TestObject to = new TestObject('DynamicContentsTestObjectWithContents:' + contents + '_AndSelector:' + selector);
		to.addProperty('xpath', ConditionType.EQUALS, xpath);

		return to;
	}

	/**
	 * Find an element by traversing the element tree: from the start element,
	 *   traverse up the tree to the specified ancestor and then back down
	 *   any number of levels to the first matching child
	 * @author Gabe Abrams
	 * @instance
	 * @memberof Kaixa
	 * @method findChildOfAncestor
	 * @param {Map} args - all arguments in one map
	 * @param {String} args.startSelector - css selector for the start
	 *   element
	 * @param {String} [args.startContents] - text contents to use to narrow the
	 *   search for the start element. NOTE: must be the contents of the element,
	 *   not contents of a child element!
	 * @param {String} [args.ancestor=parent] - the ancestor to traverse
	 *   up to. Allowed options: "parent" or "grandparent" or "greatgrandparent"
	 *   or "greatgreatgrandparent"
	 * @param {int} [args.ancestorLevel=1] - alternative method for identifying
	 *   the ancestor (1 = parent, 2 = grandparent, 3 = greatgrandparent, ...)
	 * @param {String} args.childSelector - css selector for the child
	 *   element
	 * @param {String} [args.childContents] - text contents to use to narrow the
	 *   search for the child element. NOTE: must be the contents of the element,
	 *   not contents of a child element!
	 * @return {TestObject} element
	 */
	public static TestObject findChildOfAncestor(Map<String,Object> args) {
		String xpath = '//';

		// Add search for start element
		// > Add selector
		String startSelector = args.get('startSelector');
		if (startSelector.startsWith('#')) {
			// id
			xpath += '*[@id=\'' + startSelector.substring(1) + '\']';
		} else if (startSelector.startsWith('.')) {
			// class
			xpath += '*[contains(@class,\'' + startSelector.substring(1) + '\')]';
		} else {
			// tag name
			xpath += startSelector;
		}
		// > Optionally add text contents
		if (args.containsKey('startContents')) {
			String startContents = args.get('startContents');
			String contentsEscaped;
			if (startContents.toString().indexOf('\'') < 0) {
				contentsEscaped = '\'' + startContents + '\'';
			} else if (startContents.toString().indexOf('"') < 0) {
				contentsEscaped = '"' + startContents + '"';
			} else {
				contentsEscaped = 'concat(\'' + startContents.toString().replace('\'', '\',"\'", \'') + '\')';
			}
			xpath += '[text()[contains(.,' + contentsEscaped + ')]]';
		}

		// Traverse up to ancestor
		int ancestorLevel = 1;
		if (args.containsKey('ancestor') || args.containsKey('ancestorLevel')) {
			if (args.containsKey('ancestor')) {
				// String
				if (args.get('ancestor').toLowerCase() == 'grandparent') {
					ancestorLevel = 2;
				} else if (args.get('ancestor').toLowerCase() == 'greatgrandparent') {
					ancestorLevel = 3;
				} else if (
					args.get('ancestor').toLowerCase()
					== 'greatgreatgrandparent'
				) {
					ancestorLevel = 4;
				} else {
					throw new Exception('Ancestor level was invalid.');
				}
			} else {
				// Number
				ancestorLevel = (args.get('ancestorLevel') + '').toInteger();
			}
		}
		for (int i = 0; i < ancestorLevel; i++) {
			xpath += '/parent::*';
		}

		// Find nearest child
		xpath += '//';
		// > Add selector
		String childSelector = args.get('childSelector');
		if (childSelector.startsWith('#')) {
			// id
			xpath += '*[@id=\'' + childSelector.substring(1) + '\']';
		} else if (childSelector.startsWith('.')) {
			// class
			xpath += '*[contains(@class,\'' + childSelector.substring(1) + '\')]';
		} else {
			// tag name
			xpath += childSelector;
		}
		// > Optionally add text contents
		if (args.containsKey('childContents')) {
			String childContents = args.get('childContents');
			String contentsEscaped;
			if (childContents.toString().indexOf('\'') < 0) {
				contentsEscaped = '\'' + childContents + '\'';
			} else if (childContents.toString().indexOf('"') < 0) {
				contentsEscaped = '"' + childContents + '"';
			} else {
				contentsEscaped = 'concat(\'' + childContents.toString().replace('\'', '\',"\'", \'') + '\')';
			}
			xpath += '[text()[contains(.,' + contentsEscaped + ')]]';
		}

		// Find the element
		return Kaixa.findByXPath(xpath);
	}

	/**
	 * Make sure an item is a TestObject. If it is a CSS selector, turn it into a TestObject
	 * @author Gabe Abrams
	 * @instance
	 * @memberof Kaixa
	 * @method ensureTestObject
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
	 * @instance
	 * @memberof Kaixa
	 * @method getAttribute
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
	 * @instance
	 * @memberof Kaixa
	 * @method elementExists
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
	 * @instance
	 * @memberof Kaixa
	 * @method elementAbsent
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
	 * @instance
	 * @memberof Kaixa
	 * @method elementWithContentsExists
	 * @param {String} contents - the contents to search for
	 * @param {String} selector - a CSS selector corresponding to the item
	 * @return {boolean} true if the element exists on the page
	 */
	public static boolean elementWithContentsExists(Object contents, String selector) {
		TestObject obj = Kaixa.findByContents(contents, selector);
		return WebUI.verifyElementPresent(obj, 1, FailureHandling.OPTIONAL);
	}

	/**
	 * Check if an element with specific contents does not exist
	 * @author Gabe Abrams
	 * @instance
	 * @memberof Kaixa
	 * @method elementWithContentsAbsent
	 * @param {String} contents - the contents to search for
	 * @param {String} selector - a CSS selector corresponding to the item
	 * @return {boolean} true if the element does not exist on the page
	 */
	public static boolean elementWithContentsAbsent(Object contents, String selector) {
		TestObject obj = Kaixa.findByContents(contents, selector);
		return !WebUI.verifyElementPresent(obj, 1, FailureHandling.OPTIONAL);
	}

	/**
	 * Get number of elements
	 * @author Gabe Abrams
	 * @instance
	 * @memberof Kaixa
	 * @method assertNumElements
	 * @param {String} selector - a CSS selector corresponding to the item
	 * @return {Integer} the number of elements on the page
	 */
	public static int getNumElements(String selector) {
		return WebUI.executeJavaScript('return document.querySelectorAll(\'' + selector + '\').length', null);
	}

	/**
	 * Assert a certain number of elements
	 * @author Gabe Abrams
	 * @instance
	 * @memberof Kaixa
	 * @method assertNumElements
	 * @param {String} selector - a CSS selector corresponding to the item
	 * @param {Integer} num - the precise number of elements expected
	 */
	public static void assertNumElements(String selector, int num) {
		int actualNum = Kaixa.getNumElements(selector);

		if (actualNum != num) {
			throw new Exception('Expected ' + num + ' element(s) matching selector "' + selector + '" but got ' + actualNum + ' instead');
		}
	}

	/**
	 * Get the parent of an element
	 * @author Gabe Abrams
	 * @instance
	 * @memberof Kaixa
	 * @method parentOf
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
	 * @instance
	 * @memberof Kaixa
	 * @method descendantOf
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
	 * @instance
	 * @memberof Kaixa
	 * @method pause
	 */
	public static void pause() {
		Kaixa.log('⏸ Pause Started');
		JFrame frame = new JFrame('Kaixa');
		frame.requestFocus();
		JOptionPane.showMessageDialog(null, 'Test case paused. Click "OK" to continue...');
		Kaixa.log('▶️ Pause Ended');
	}

	/**
	 * Wait for a specific number of milliseconds
	 * @author Gabe Abrams
	 * @instance
	 * @memberof Kaixa
	 * @method waitFor
	 * @param {int} ms - the number of ms to wait
	 */
	public static void waitFor(int ms = 0) {
		Kaixa.log('⏱ Wait for ' + ms + 'ms');
		Thread.sleep(ms);
	}


	/**
	 * Wait for an element to be visible
	 * @author Gabe Abrams
	 * @instance
	 * @memberof Kaixa
	 * @method waitForElementVisible
	 * @param {TestObject|String} item - the TestObject or CSS selector of interest
	 * @param {int} [timeoutSec=10] - the number of seconds to wait before timing out
	 */
	public static void waitForElementVisible(Object item, int timeoutSec = 10) {
		Kaixa.log('⏱👁 Wait for ' + item + ' to be visible');
		try {
			assert WebUI.waitForElementVisible(Kaixa.ensureTestObject(item), timeoutSec);
		} catch (AssertionError e) {
			throw new Exception('Element "' + item + '" not become visible within ' + timeoutSec + ' second(s)');
		}
	}

	/**
	 * Wait for an element with specific contents to be visible
	 * @author Gabe Abrams
	 * @instance
	 * @memberof Kaixa
	 * @method waitForElementWithContentsVisible
	 * @param {String} contents - the contents to search for
	 * @param {String} selector - a CSS selector corresponding to the item
	 * @param {int} [timeoutSec=10] - the number of seconds to wait before timing out
	 */
	public static void waitForElementWithContentsVisible(Object contents, String selector, int timeoutSec) {
		Kaixa.log('⏱👁 Wait for ' + selector + ' with contents "' + contents + '" to be visible');
		try {
			assert WebUI.waitForElementVisible(Kaixa.findByContents(contents, selector), timeoutSec);
		} catch (AssertionError e) {
			throw new Exception('Element "' + selector + '" with contents "' + contents + '" did not become visible within ' + timeoutSec + ' second(s)');
		}
	}

	/**
	 * Wait for an element to be present (on the page, it does not have to be visible)
	 * @author Gabe Abrams
	 * @instance
	 * @memberof Kaixa
	 * @method waitForElementPresent
	 * @param {TestObject|String} item - the TestObject or CSS selector of interest
	 * @param {int} [timeoutSec=10] - the number of seconds to wait before timing out
	 */
	public static void waitForElementPresent(Object item, int timeoutSec = 10) {
		Kaixa.log('⏱📍 Wait for ' + item + ' to be present');
		try {
			assert WebUI.waitForElementPresent(Kaixa.ensureTestObject(item), timeoutSec, FailureHandling.OPTIONAL);
		} catch (AssertionError e) {
			throw new Exception('Element "' + item + '" not become visible within ' + timeoutSec + ' second(s)');
		}
	}

	/**
	 * Wait for an element with specific contents to be present (on the page, it does not have to be visible)
	 * @author Gabe Abrams
	 * @instance
	 * @memberof Kaixa
	 * @method waitForElementWithContentsPresent
	 * @param {String} contents - the contents to search for
	 * @param {String} selector - a CSS selector corresponding to the item
	 * @param {int} [timeoutSec=10] - the number of seconds to wait before timing out
	 */
	public static void waitForElementWithContentsPresent(Object contents, String selector, int timeoutSec) {
		Kaixa.log('⏱📍 Wait for ' + selector + ' with contents "' + contents + '" to be present');
		try {
			assert WebUI.waitForElementPresent(Kaixa.findByContents(contents, selector), timeoutSec, FailureHandling.OPTIONAL);
		} catch (AssertionError e) {
			throw new Exception('Element "' + selector + '" with contents "' + contents + '" did not become visible within ' + timeoutSec + ' second(s)');
		}
	}

	/**
	 * Wait for an element to not be present (on the page, it does not have to be visible)
	 * @author Gabe Abrams
	 * @instance
	 * @memberof Kaixa
	 * @method waitForElementAbsent
	 * @param {TestObject|String} item - the TestObject or CSS selector of interest
	 * @param {int} [timeoutSec=10] - the number of seconds to wait before timing out
	 */
	public static void waitForElementAbsent(Object item, int timeoutSec = 10) {
		Kaixa.log('⏱✖️ Wait for element ' + item + ' to be absent');
		try {
			assert WebUI.waitForElementNotPresent(Kaixa.ensureTestObject(item), timeoutSec, FailureHandling.OPTIONAL);
		} catch (AssertionError e) {
			throw new Exception('Element "' + item + '" was present for the whole ' + timeoutSec + ' second(s)');
		}
	}

	/**
	 * Wait for any element in a list to be present (check every tenth of a
	 *   second for status)
	 * @author Gabe Abrams
	 * @instance
	 * @memberof Kaixa
	 * @method waitForAtLeastOneElementPresent
	 * @param {ArrayList<Object|String>} items - list of TestObject or CSS
	 *   selectors of interest
	 * @param {int} [timeoutSec=10] - the number of seconds to wait before
	 *   timing out
	 * @return {Object} the item that was found first, ties broken by which item
	 *   shows up in the list first
	 */
	public static Object waitForAtLeastOneElementPresent(ArrayList items, int timeoutSec = 10) {
		Kaixa.log('⏱👁 Wait for at least one of the following to be present: ' + items);

		// Attempt to check many times
		int numChecks = (timeoutSec * 10);
		for (int i = 0; i < numChecks; i++) {
			// Loop through and look for one of the items
			for (item in items) {
				if (Kaixa.elementExists(item)) {
					// Found an element that exists! Finish
					return item;
				}
			}
		}
		
		// No element shown
		throw new Exception('Checked every 1/10th second but none of the elements were present within ' + timeoutSec + ' second(s)');
	}

	/* -------------------- Assertions -------------------- */

	/**
	 * Make sure an element exists
	 * @author Gabe Abrams
	 * @instance
	 * @memberof Kaixa
	 * @method assertExists
	 * @param {TestObject|String} item - the TestObject or CSS selector
	 * @param {String} [message=generated message] - a human-readable message to
	 *   display if the test fails
	 * @param {int} [gracePeriodSecs=10] - the number of seconds to wait before
	 *   throwing an error
	 */
	public static void assertExists(Object item, String message = '', int gracePeriodSecs = 10) {
		Kaixa.log('🔎📍 Assert ' + item + ' exists');
		try {
			Kaixa.waitForElementPresent(item, gracePeriodSecs);
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
	 * @instance
	 * @memberof Kaixa
	 * @method assertAbsent
	 * @param {TestObject|String} item - the TestObject or CSS selector
	 * @param {String} [message=generated message] - a human-readable message to
	 *   display if the test fails
	 */
	public static void assertAbsent(Object item, String message = '') {
		Kaixa.log('🔎✖️ Assert ' + item + ' absent');
		TestObject obj = Kaixa.ensureTestObject(item);
		boolean absent = WebUI.verifyElementNotPresent(obj, 1, FailureHandling.OPTIONAL);

		if (!absent) {
			// Found but shouldn't have
			throw new Exception(
				message == ''
					? 'Element "' + item + '" exists, but it should have been absent.'
					: message
			);
		}
	}
	
	/**
	 * Make sure an element with specific contents exists
	 * @author Gabe Abrams
	 * @instance
	 * @memberof Kaixa
	 * @method assertExistsWithContents
	 * @param {String} contents - the contents to search for
	 * @param {String} selector - a CSS selector corresponding to the item
	 * @param {String} [message=generated message] - a human-readable message to
	 *   display if the test fails
	 * @param {int} [gracePeriodSecs=10] - the number of seconds to wait before
	 *   throwing an error
	 */
	public static void assertExistsWithContents(Object contents, String selector, String message = '', int gracePeriodSecs = 10) {
		Kaixa.log('🔎📍 Assert ' + selector + ' with contents "' + contents + '" exists');
		try {
			Kaixa.waitForElementWithContentsPresent(contents, selector, gracePeriodSecs);
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
	 * @author Gabe Abrams
	 * @instance
	 * @memberof Kaixa
	 * @method assertAbsentWithContents
	 * @param {String} contents - the contents to search for
	 * @param {String} selector - a CSS selector corresponding to the item
	 * @param {String} [message=generated message] - a human-readable message to
	 *   display if the test fails
	 */
	public static void assertAbsentWithContents(Object contents, String selector, String message = '') {
		Kaixa.log('🔎✖️ Assert ' + selector + ' with contents "' + contents + '" absent');
		TestObject obj = Kaixa.findByContents(contents, selector);
		boolean absent = WebUI.verifyElementNotPresent(obj, 1, FailureHandling.OPTIONAL);

		if (!absent) {
			// Found but shouldn't have
			throw new Exception(
				message == ''
					? 'Element "' + selector + '" with contents "' + contents + '" exist, but it should have been absent.'
					: message
			);
		}
	}

	/**
	 * Make sure an element has a specific class name
	 * @author Gabe Abrams
	 * @instance
	 * @memberof Kaixa
	 * @method assertHasClass
	 * @param {TestObject|String} item - the TestObject or CSS selector of interest
	 * @param {String} className - the name of the class to expect
	 * @param {String} [message=generated message] - a human-readable message to
	 *   display if the test fails
	 */
	public static void assertHasClass(Object item, String className, String message = '') {
		Kaixa.log('🔎👕 Assert ' + item + ' has class "' + className + '"');
		String classStr = Kaixa.getAttribute(item, 'className');
		String[] classes = classStr.split(' ');
		boolean hasClass = Arrays.asList(classes).contains(className);

		if (!hasClass) {
			throw new Exception(
				message == ''
					? 'Element "' + item + '" did not have class "' + className + '" but it should have had it.'
					: message
			);
		}
	}

	/**
	 * Make sure an element does not have a specific class name
	 * @author Gabe Abrams
	 * @instance
	 * @memberof Kaixa
	 * @method assertDoesNotHaveClass
	 * @param {TestObject|String} item - the TestObject or CSS selector of interest
	 * @param {String} className - the name of the class to expect
	 * @param {String} [message=generated message] - a human-readable message to
	 *   display if the test fails
	 */
	public static void assertDoesNotHaveClass(Object item, String className, String message = '') {
		Kaixa.log('🔎✖️👕 Assert ' + item + ' does not have class "' + className + '"');
		String classStr = Kaixa.getAttribute(item, 'className');
		String[] classes = classStr.split(' ');
		boolean hasClass = Arrays.asList(classes).contains(className);

		if (hasClass) {
			throw new Exception(
				message == ''
					? 'Element "' + item + '" had class "' + className + '" but it shouldn\'t have had it.'
					: message
			);
		}
	}

	/**
	 * Make sure an expression evaluates to true
	 * @author Gabe Abrams
	 * @instance
	 * @memberof Kaixa
	 * @method assertTrue
	 * @param {boolean} value - the value that should be true
	 * @param {String} [message=unknown] - a human-readable message to
	 *   display if the test fails
	 */
	public static void assertTrue(boolean value, String message = 'Something was not true when it should have been') {
		Kaixa.log('🔎 Assert true');
		if (!value) {
			throw new Exception(message);
		}
	}

	/**
	 * Make sure an expression evaluates to false
	 * @author Gabe Abrams
	 * @instance
	 * @memberof Kaixa
	 * @method assertFalse
	 * @param {boolean} value - the value that should be false
	 * @param {String} [message=unknown] - a human-readable message to
	 *   display if the test fails
	 */
	public static void assertFalse(boolean value, String message = 'Something was not false when it should have been') {
		Kaixa.log('🔎 Assert false');
		if (value) {
			throw new Exception(message);
		}
	}

	/**
	 * Exit with an error
	 * @author Gabe Abrams
	 * @instance
	 * @memberof Kaixa
	 * @method exitWithError
	 * @param {String} message - a human-readable message to display
	 */
	public static void exitWithError(String message) {
		throw new Exception(message);
	}

	/* -------------------- Interactions-------------------- */

	/**
	 * Click an element
	 * @author Gabe Abrams
	 * @instance
	 * @memberof Kaixa
	 * @method click
	 * @param {TestObject|String} item - the TestObject or CSS selector of interest
	 * @param {int} [timeoutSec=10] - the number of seconds to wait before timing out
	 * @param {boolean} [dontScrollTo] - if true, do not scroll to the element
	 */
	public static void click(Object item, int timeoutSec = 10, boolean dontScrollTo = false) {
		Kaixa.log('🖱 Click ' + item);
		TestObject obj = Kaixa.ensureTestObject(item);
		try {
			Kaixa.waitForElementVisible(obj, dontScrollTo ? timeoutSec : 1);
		} catch (Exception e) {
			// Try to scroll then wait again
			if (!dontScrollTo) {
				Kaixa.scrollTo(item);
				Kaixa.waitForElementVisible(obj, timeoutSec);
			} else {
				throw e;
			}
		}
		// TODO: remove this workaround for Safari clicks
		if (Kaixa.isSafari() && item instanceof String) {
		  // Click using JavaScript instead of possible because Safari for some reason can't click things
		  Kaixa.runScript('document.querySelector("' + item.toString() + '").click()');
		} else {
		  WebUI.click(obj);
		}
	}

	/**
	 * Open an anchor link in the same tab
	 * @author Gabe Abrams
	 * @instance
	 * @memberof Kaixa
	 * @method openAnchorInSameTab
	 * @param {TestObject|String} item - the TestObject or CSS selector of interest 
	 */
	public static void openAnchorInSameTab(Object item) {
		Kaixa.log('🖱 Open Anchor in Same Tab ' + item);
		TestObject obj = Kaixa.ensureTestObject(item);
		String href = Kaixa.getAttribute(obj, 'href');
		Kaixa.visit(href);
	}

	/**
	 * Click an item by defining its contents
	 * @author Gabe Abrams
	 * @instance
	 * @memberof Kaixa
	 * @method clickByContents
	 * @param {String} contents - the contents to search for
	 * @param {String} selector - a CSS selector corresponding to the item
	 */
	public static void clickByContents(Object contents, String selector) {
		TestObject obj = Kaixa.findByContents(contents, selector);
		Kaixa.click(obj);
	}

	/**
	 * Type text into an element. This function first removes the previous text in the element
	 * @author Gabe Abrams
	 * @instance
	 * @memberof Kaixa
	 * @method typeInto
	 * @param {TestObject|String} item - the TestObject or CSS selector of interest
	 * @param {String} text - the text to type
	 */
	public static void typeInto(Object item, Object text) {
		Kaixa.log('⌨️ Type "' + text + '" into ' + item);
		TestObject obj = Kaixa.ensureTestObject(item);
		Kaixa.waitForElementVisible(obj);
		WebUI.setText(obj, text.toString());
	}

	/**
	 * Scroll the page to the element
	 * @author Gabe Abrams
	 * @instance
	 * @memberof Kaixa
	 * @method scrollTo
	 * @param {TestObject|String} item - the TestObject or CSS selector of interest
	 */
	public static void scrollTo(Object item) {
		Kaixa.log('↕️ Scroll to ' + item);
		TestObject obj = Kaixa.ensureTestObject(item);
		WebUI.scrollToElement(obj, 10);
	}

	/**
	 * Choose an item in a select element based on its label
	 * @author Gabe Abrams
	 * @instance
	 * @memberof Kaixa
	 * @method chooseSelectByLabel
	 * @param {TestObject|String} item - the TestObject or CSS selector of the select element
	 * @param {String} label - the label to select in the dropdown
	 */
	public static void chooseSelectByLabel(Object item, Object label) {
		Kaixa.log('▤ Choose Select Item "' + label + '" in dropdown ' + item);
		TestObject obj = Kaixa.ensureTestObject(item);

		// Select the option
		WebUI.selectOptionByLabel(obj, label.toString(), false);

		// Verify the selection
		WebUI.verifyOptionSelectedByLabel(obj, label.toString(), false, 60);
	}

	/**
	 * Choose an item in a select element based on its value
	 * @author Gabe Abrams
	 * @instance
	 * @memberof Kaixa
	 * @method chooseSelectByValue
	 * @param {TestObject|String} item - the TestObject or CSS selector of the select element
	 * @param {String} value - the value of the item to select in the dropdown
	 */
	public static void chooseSelectByValue(Object item, Object value) {
		Kaixa.log('▤ Choose Select Item with value "' + value+ '" in dropdown ' + item);
		TestObject obj = Kaixa.ensureTestObject(item);

		// Select the option
		WebUI.selectOptionByValue(obj, value.toString(), false);

		// Verify the selection
		WebUI.verifyOptionSelectedByValue(obj, value.toString(), false, 10);
	}

	/**
	 * Get the list of labels in a select
	 * @author Gabe Abrams
	 * @instance
	 * @memberof Kaixa
	 * @method listSelectLabels
	 * @param {String} selector - the css selector for the select element
	 * @return {String[]} list of labels
	 */
	public static String[] listSelectLabels(String selector) {
		try {
			String[] labels = Kaixa.runScript('try { return Array.from(document.querySelectorAll(\'' + selector + ' > option\')).map((option) => { return option.innerHTML; }).filter((x, i) => { return i > 0; }); } catch (err) { alert(err.message);}');
			return labels;
		} catch (err) {
			throw new Exception('Could not get the list of labels inside of the select dropdown with selector "' + selector + '". Check the browser console for more information on the issue.');
		}
	}

	/**
	 * Choose a file for a file chooser
	 * @author Gabe Abrams
	 * @instance
	 * @memberof Kaixa
	 * @method chooseFile
	 * @param {TestObject|String} item - the TestObject or CSS selector of the select element
	 * @param {String} filePath - the path of the file
	 */
	public static void chooseFile(Object item, String relativePath) {
		Kaixa.log('📁 Choose file ' + relativePath + ' for file chooser ' + item);
		TestObject obj = Kaixa.ensureTestObject(item);

		// Get the absolute path of the file
		String absolutePath = Paths.get(RunConfiguration.getProjectDir(), relativePath).toAbsolutePath();

		// Select the file
		WebUI.uploadFile(obj, absolutePath);
	}

	/**
	 * Run a script on the page
	 * @author Gabe Abrams
	 * @instance
	 * @memberof Kaixa
	 * @method runScript
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
	 * @instance
	 * @memberof Kaixa
	 * @method closeWindow
	 */
	public static void closeWindow() {
		Kaixa.log('✖️ Close Window');
		// Get the index of the current window
		int index = WebUI.getWindowIndex();

		// Close the window
		WebUI.closeWindowIndex(index);
	}

	/**
	 * Refresh the page
	 * @author Gabe Abrams
	 * @instance
	 * @memberof Kaixa
	 * @method refresh
	 */
	public static void refresh() {
		Kaixa.log('⟳ Refresh Page');

		// Refresh the page
		WebUI.refresh();
	}

	/**
	 * Close the browser
	 * @author Gabe Abrams
	 * @instance
	 * @memberof Kaixa
	 * @method done
	 */
	public static void done() {
		Kaixa.log('✔ Done');
		WebUI.closeBrowser();
	}

	/* -------------------- Data -------------------- */

	/**
	 * Get the title of the current window
	 * @author Gabe Abrams
	 * @instance
	 * @memberof Kaixa
	 * @method getTitle
	 * @return {String} the title of the window
	 */
	public static String getTitle() {
		return WebUI.getWindowTitle();
	}

	/**
	 * Get the text in an element
	 * @author Gabe Abrams
	 * @instance
	 * @memberof Kaixa
	 * @method getText
	 * @param {TestObject|String} item - the TestObject or CSS selector
	 * @return {String} the contents of the element as text
	 */
	public static String getText(Object item) {
		return WebUI.getText(Kaixa.ensureTestObject(item));
	}

	/**
	 * Get the source of the page
	 * @author Gabe Abrams
	 * @instance
	 * @memberof Kaixa
	 * @method getSource
	 * @return {String} the source of the current page
	 */
	public static String getSource() {
		WebDriver driver = DriverFactory.getWebDriver();
		return driver.getPageSource();
	}

	/**
	 * Get the current URL of the page
	 * @author Gabe Abrams
	 * @instance
	 * @memberof Kaixa
	 * @method getURL
	 * @return {String} the source of the current page
	 */
	public static String getURL() {
		return WebUI.getUrl();
	}

	/**
	 * Get the current URL of the page
	 * @author Gabe Abrams
	 * @instance
	 * @memberof Kaixa
	 * @method getQuery
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
	 * @instance
	 * @memberof Kaixa
	 * @method getJSONString
	 * @return {String} the JSON string
	 */
	public static String getJSONString() {
		// Switch to the raw data tab if on Firefox
		boolean onFirefox = Kaixa.elementExists('#rawdata-tab');
		if (onFirefox) {
			Kaixa.click('#rawdata-tab');
		}

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
	 * @instance
	 * @memberof Kaixa
	 * @method getJSONObject
	 * @return {JSONObject} the JSON object on the page
	 */
	public static JSONObject getJSONObject() {
		return new JSONObject(Kaixa.getJSONString());
	}

	/**
	 * Get the JSON array on the page
	 * @author Gabe Abrams
	 * @instance
	 * @memberof Kaixa
	 * @method getJSONArray
	 * @return {JSONArray} the JSON array on the page
	 */
	public static JSONArray getJSONArray() {
		return new JSONArray(Kaixa.getJSONString());
	}

	/**
	 * Get the JSON object or array on the page
	 * @author Gabe Abrams
	 * @instance
	 * @memberof Kaixa
	 * @method getJSON
	 * @return {JSONObject|JSONArray} the JSON info on the page
	 */
	public static Object getJSON() {
		// Get the text
		String jsonString = Kaixa.getJSONString();

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
	 * Send a GET request
	 * @author Gabe Abrams
	 * @instance
	 * @memberof Kaixa
	 * @method sendGETRequest
	 * @param {String} location - path or URL to visit
	 * @return {JSONArray|JSONObject} response
	 */
	public static Object sendGETRequest(String location, boolean silent = false) {
		if (silent != true) {
			Kaixa.log('🖥 GET Request: ' + location);
		}

		// Get full URL
		String url = Kaixa.locationToURL(location);

		// Send GET request
		URLConnection get = (new URL(url)).openConnection();
		String content = get.getInputStream().getText();
		
		// Process as JSON
		if (content.charAt(0) == '[') {
			// This is an array
			return new JSONArray(content);
		} else {
			// This is an object
			return new JSONObject(content);
		}
	}

	/**
	 * Send a POST request to a Canvas API endpoint
	 * @author Gabe Abrams
	 * @instance
	 * @memberof Kaixa
	 * @method visitCanvasPOSTEndpoint
	 * @param {Map}
	 * @param {String} path the path of the API, excluding https://canvas.harvard.edu, example: "/api/v1/users"
	 * @param {String} accessToken a Canvas access token
	 * @param {String} payload the body of the post request
	 * @returns {JSONArray|JSONObject} Canvas response
	 */
	public static Object visitCanvasPOSTEndpoint(String path, String accessToken, String payload) {
		Kaixa.log('🖥 POST Request: ' + path);

		URL post = new URL('https://canvas.harvard.edu' + path).openConnection();
		post.setRequestMethod('POST')
		post.setDoOutput(true);
		post.setRequestProperty('Content-Type', 'application/json');
		post.setRequestProperty('Authorization', 'Bearer ' + accessToken);
		post.getOutputStream().write(payload.getBytes('UTF-8'));

		// Get the response
		String content = post.getInputStream().getText();
		
		// Process as JSON
		if (content.charAt(0) == '[') {
			// This is an array
			return new JSONArray(content);
		} else {
			// This is an object
			return new JSONObject(content);
		}
	}

	/**
	 * Send a DELETE request to a Canvas API endpoint
	 * @author Gabe Abrams
	 * @instance
	 * @memberof Kaixa
	 * @method visitCanvasDELETEEndpoint
	 * @param {Map}
	 * @param {String} path the path of the API, excluding https://canvas.harvard.edu, example: "/api/v1/users"
	 * @param {String} accessToken a Canvas access token
	 * @param {String} payload the body of the post request
	 * @returns {JSONArray|JSONObject} Canvas response
	 */
	public static Object visitCanvasDELETEEndpoint(String path, String accessToken, String payload) {
		Kaixa.log('🖥 POST Request: ' + path);

		URL post = new URL('https://canvas.harvard.edu' + path).openConnection();
		post.setRequestMethod('DELETE')
		post.setDoOutput(true);
		post.setRequestProperty('Content-Type', 'application/json');
		post.setRequestProperty('Authorization', 'Bearer ' + accessToken);
		post.getOutputStream().write(payload.getBytes('UTF-8'));

		// Get the response
		String content = post.getInputStream().getText();
		
		// Process as JSON
		if (content.charAt(0) == '[') {
			// This is an array
			return new JSONArray(content);
		} else {
			// This is an object
			return new JSONObject(content);
		}
	}

	/**
	 * Extract info from a class
	 * @author Gabe Abrams
	 * @instance
	 * @memberof Kaixa
	 * @method extractDataFromClass
	 * @param {TestObject|String} item - the TestObject or CSS selector of the element
	 * @param {String} classPrefix - the prefix of the class. Example: if there is
	 *   a class "Event-12345" then with classPrefix "Event-" the return of this
	 *   function would be "12345"
	 * @return {String} value following the prefix
	 */
	public static String extractDataFromClass(Object item, String classPrefix) {
		TestObject obj = Kaixa.ensureTestObject(item);

		// Get classes
		String classString = Kaixa.getAttribute(obj, 'class');
		String[] classes = classString.split(' ');

		// Search for the class
		for (String cn : classes) {
			if (cn.startsWith(classPrefix) && cn.length() > classPrefix.length()) {
				return cn.substring(classPrefix.length());
			}
		}

		// No class found
		throw new Exception('Could not get metadata because class prefix "' + classPrefix + '" could not be found.');
	}

	/**
	 * Extract info from a class
	 * @author Gabe Abrams
	 * @instance
	 * @memberof Kaixa
	 * @method extractDataFromClassByContents
	 * @param {String} contents - the contents to search for
	 * @param {String} selector - a CSS selector corresponding to the item
	 * @param {String} classPrefix - the prefix of the class. Example: if there is
	 *   a class "Event-12345" then with classPrefix "Event-" the return of this
	 *   function would be "12345"
	 * @return {String} value following the prefix
	 */
	public static String extractDataFromClassByContents(Object contents, String selector, String classPrefix) {
		TestObject obj = Kaixa.findByContents(contents.toString(), selector);
		return Kaixa.extractDataFromClass(obj, classPrefix);
	}

	/* -------------------- Harvard-specific Commands -------------------- */

	/**
	 * Log into Canvas using an access token
	 * @author Gabe Abrams
	 * @memberof Kaixa
	 * @method launchLTIUsingToken
	 * @param {String} accessToken - the user's access token
	 * @param {int} [courseId=courseId from profile] - the Canvas ID of the course to launch from
	 * @param {String} [appName=appName from profile] - the name of the app as it appears in the course's left-hand nav
	 */
	public static void launchLTIUsingToken(String accessToken, int courseId = defaultCourseId, String appName = defaultAppName) {
		// Try to quit the previous session
		try {
			WebUI.closeBrowser();
		} catch (Exception e) {
			// Ignore
		}

		// Get the external tool URL
		JSONArray externalTools = Kaixa.visitCanvasEndpoint('/courses/' + courseId + '/external_tools', accessToken);
		
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

		// Get a sessionless launch URL
		JSONObject sessionlessLaunchInfo = Kaixa.visitCanvasEndpoint('/courses/' + courseId + '/external_tools/sessionless_launch?id=' + toolId, accessToken);
		String launchURL = sessionlessLaunchInfo.getString('url');

		// Launch the tool
		Kaixa.visit(launchURL);
	}

	/**
	 * Log into Canvas and launch an LTI app as a specific user from the profile variables.
	 *   The value should be a JSON string with the following properties: { [accessToken], [username], [password], [isXID] }
	 *   If the accessToken is excluded, we will attempt to launch using the username.
	 *   If logging in with a username and the password is excluded, the test runner is prompted to run a password.
	 *   If isXID is true, the user will be logged in using the XID login panel.
	 *   If both accessToken and username are excluded, the test runner will be prompted to enter an accessToken.
	 * @author Gabe Abrams
	 * @instance
	 * @memberof Kaixa
	 * @method launchAs
	 * @param {String} name - the name of the variable containing the credentials for the user
	 * @param {int} [courseId=courseId from profile] - the Canvas ID of the course to launch from
	 * @param {String} [appName=appName from profile] - the name of the app as it appears in the course's left-hand nav
	 */
	public static void launchAs(String name, int courseId = defaultCourseId, String appName = defaultAppName) {
		// Make sure the user exists
		if (!GlobalVariable.metaClass.hasProperty(GlobalVariable, name)) {
			throw new Exception('Could not launch as "' + name + '" because that user is not listed in the profile variables.');
		}
		
		// Log action
		Kaixa.log('🚀 Launch as ' + name);
		
		// Check if this is a local launch
		boolean isLocal = (Kaixa.getProfileValue('local') == 'true');

		// Get the user info
		JSONObject obj = new JSONObject(GlobalVariable[name]);
		
		// Handle local launch
		if (isLocal) {
			// Get user type
			String userType = 'teacher';
			if (obj.has('type') && obj.getString('type') == 'student') {
				userType = 'student';
			}
			if (obj.has('type') && obj.getString('type') == 'ta') {
				userType = 'ta';
			}
			
			// Get simulator index
			String simIndex = '0';
			if (obj.has('simIndex')) {
				simIndex = obj.getString('simIndex');
			}
			
			// Create a simulator button id
			String simLaunchButtonId = userType + '_' + simIndex + '-launch-button';
			
			// Visit the simulator
			Kaixa.visit('https://localhost:8088/simulator');
			
			// Bypass certificate issue
			String foundItem = Kaixa.waitForAtLeastOneElementPresent([
				'.ssl',
				'#teacher_0-launch-button',
			]);
			if (foundItem == '.ssl') {
				// Chrome: handle ssl issue
				Kaixa.click('#details-button');
				Kaixa.click('#proceed-link');
			}
			
			// Launch the app
			Kaixa.openAnchorInSameTab('#' + simLaunchButtonId);

			// Check for authorization screen
			Kaixa.waitFor(1000);
			if (Kaixa.elementExists('.authorize-button')) {
				Kaixa.click('.authorize-button');
			}
			
			// Finish
			return;
		}

		// Get access token
		String accessToken = null;
		if (obj.has('accessToken')) {
			accessToken = obj.getString('accessToken');
		} else if (cachedAccessTokens.containsKey(name)) {
			accessToken = cachedAccessTokens.get(name);
		}

		// Handle accessToken-based launch
		if (accessToken) {
			Kaixa.launchLTIUsingToken(accessToken, courseId, appName);
		} else {
			// Ask user for access token
			JPanel panel = new JPanel();
			// > Label
			JLabel label = new JLabel('Token:');
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
				'Access Token for "' + name + '"',
				JOptionPane.NO_OPTION,
				JOptionPane.PLAIN_MESSAGE,
				null,
				options,
				options[0]
			);

			// Get the password
			accessToken = new String(pass.getPassword());

			// Make sure there is a password
			if (accessToken == '') {
				throw new Exception('Token cannot be empty.');
			}

			// Cache
			cachedAccessTokens.put(name, accessToken);

			// Launch
			Kaixa.launchLTIUsingToken(accessToken, courseId, appName);
		}
	}

	/**
	 * Handle a HarvardKey login page for a user
	 * @author Gabe Abrams
	 * @instance
	 * @memberof Kaixa
	 * @method handleHarvardKey
	 * @param {String} name - the name of the variable containing the credentials for the user
	 */
	public static void handleHarvardKey(name) {
		// Get the user info
		JSONObject obj = new JSONObject(GlobalVariable[name]);
		String username;
		String password;
		if (obj.has('username')) {
			username = obj.getString('username');
		} else if (cachedUsernames.containsKey(name)) {
			username = cachedUsernames.get(name);
		} else {
			username = Kaixa.prompt('HarvardKey Email:', 'HarvardKey Email for "' + name + '"');
			cachedUsernames.put(name, username);
		}
		if (obj.has('password')) {
			password = obj.getString('password');
		} else if (cachedPasswords.containsKey(name)) {
			password = cachedPasswords.get(name);
		} else {
			password = Kaixa.promptPassword('Password:', 'Password for "' + name + '"');
			cachedPasswords.put(name, password);
		}

		// Wait for the page to load
		Kaixa.waitForElementVisible('#username');
		Kaixa.waitForElementVisible('#password');

		// Add credentials
		Kaixa.typeInto('#username', username);
		Kaixa.typeInto('#password', password);

		// Click "submit"
		Kaixa.click('button[type=submit]');

		// Wait for URL to not be HarvardKey
		for (int i = 0; i <= 20; i++) {
			// Wait half a second
			Kaixa.waitFor(500);

			// Get current URL
			String url = Kaixa.getURL();

			// Check if URL changed
			boolean changed = !url.contains('harvard.edu/cas/login');
			
			// Check if URL never changed
			if (i == 20 && !changed) {
				throw new Error('HarvardKey page was never resolved');
			}

			// Check if URL changed
			if (changed) {
				// Continue execution
				break;
			}
		}

		// Wait for another moment
		Kaixa.waitFor(1000);

		// Check if two factor showed up
		boolean handlingTwoFactor = (
			Kaixa.elementWithContentsExists('Check your phone for a Duo Push', '.instruction-text p')
			|| Kaixa.getURL().contains('duosecurity.com')
		);

		// Wait for two factor (max 30s to resolve)
		if (handlingTwoFactor) {
			// Wait for continue button
			Kaixa.waitForElementWithContentsVisible('Yes, trust browser', '#trust-browser-button', 30);

			// Click the continue button
			Kaixa.click('#trust-browser-button');
		}
	}
}
