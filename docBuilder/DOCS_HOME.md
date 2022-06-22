# Kaixa, a simple and customizable way to write Katalon tests

Kaixa is pronounced "Kaisha" and is taken from the Catalan word for "box" as in "toolbox"

<h1 style="display: flex; align-items: center;">
  <div style="flex-grow: 1; height: 1px; background: #ccc; margin-right: 10px;">
  </div>
  <div>
    Quickstart
  </div>
  <div style="flex-grow: 1; height: 1px; background: #ccc; margin-left: 10px;">
  </div>
</h1>

You need `node` and `npm` installed. Visit [nodejs.org](https://nodejs.org) to install those.

## 1. Navigate to your Katalon project in terminal

```bash
cd ~/Katalon\ Studio/MyProject
```

## 2. Add Kaixa using the `npm init kaixa` command

```bash
npm init kaixa
```

<h1 style="display: flex; align-items: center;">
  <div style="flex-grow: 1; height: 1px; background: #ccc; margin-right: 10px;">
  </div>
  <div>
    Updating Kaixa
  </div>
  <div style="flex-grow: 1; height: 1px; background: #ccc; margin-left: 10px;">
  </div>
</h1>

To update Kaixa, just repeat steps 1 and 2 whenever you want to update your version of Kaixa.

```bash
cd ~/Katalon\ Studio/MyProject
npm init kaixa
```

<h1 style="display: flex; align-items: center;">
  <div style="flex-grow: 1; height: 1px; background: #ccc; margin-right: 10px;">
  </div>
  <div>
    Writing Tests
  </div>
  <div style="flex-grow: 1; height: 1px; background: #ccc; margin-left: 10px;">
  </div>
</h1>

## 1. Create a test and switch to "script" view

## 2. Use Kaixa functions to write your test

That's it! Here's an example:

```js
// Log in
Kaixa.typeInto('#username', 'TestUser');
Kaixa.typeInto('#password', '149t8q23y');
Kaixa.click('#login-button');

// Do something interesting
Kaixa.click('.btn-interesting');

// Close the tool
Kaixa.click('#close-button');

// Clean everything up
Kaixa.done();
```

<h1 style="display: flex; align-items: center;">
  <div style="flex-grow: 1; height: 1px; background: #ccc; margin-right: 10px;">
  </div>
  <div>
    Functions by Category:
  </div>
  <div style="flex-grow: 1; height: 1px; background: #ccc; margin-left: 10px;">
  </div>
</h1>

## **Interactions** – interact with stuff on the page

- [chooseFile](https://harvard-edtech.github.io/create-kaixa/Kaixa.html#chooseFile) - set the file for a file chooser
- [chooseSelectByLabel](https://harvard-edtech.github.io/create-kaixa/Kaixa.html#chooseSelectByLabel) - choose select item by label
- [chooseSelectByValue](https://harvard-edtech.github.io/create-kaixa/Kaixa.html#chooseSelectByValue) - choose select item by value
- [click](https://harvard-edtech.github.io/create-kaixa/Kaixa.html#click) - click something
- [clickByContents](https://harvard-edtech.github.io/create-kaixa/Kaixa.html#clickByContents) - click something (find it by its contents)
- [openAnchorInSameTab](https://harvard-edtech.github.io/create-kaixa/Kaixa.html#openAnchorInSameTab) - click an anchor but force it to open in the same tab
- [pause](https://harvard-edtech.github.io/create-kaixa/Kaixa.html#pause) - wait until the user clicks to continue
- [runScript](https://harvard-edtech.github.io/create-kaixa/Kaixa.html#runScript) - run javascript on the page
- [scrollTo](https://harvard-edtech.github.io/create-kaixa/Kaixa.html#scrollTo) - scroll to an element
- [typeInto](https://harvard-edtech.github.io/create-kaixa/Kaixa.html#typeInto) - type text into an element
- [visit](https://harvard-edtech.github.io/create-kaixa/Kaixa.html#visit) - visit a page

## **Browser Actions** – interact with the browser

- [done](https://harvard-edtech.github.io/create-kaixa/Kaixa.html#done) - close the browser and clean up
- [closeWindow](https://harvard-edtech.github.io/create-kaixa/Kaixa.html#closeWindow) - just close the current window

## **Harvard Functions** – special functions for interacting with Harvard services

- [handleHarvardKey](https://harvard-edtech.github.io/create-kaixa/Kaixa.html#handleHarvardKey) - handle a HarvardKey login page

## **Canvas & LTI Functions** – special functions for Canvas apps

- [launchAs](https://harvard-edtech.github.io/create-kaixa/Kaixa.html#launchAs) - launch an LTI as a user
- [visitCanvasEndpoint](https://harvard-edtech.github.io/create-kaixa/Kaixa.html#visitCanvasEndpoint) - visit a Canvas API GET endpoint

## **Intelligent Waiting** – wait better

- [waitFor](https://harvard-edtech.github.io/create-kaixa/Kaixa.html#waitFor) - wait for a set amount of time
- [waitForElementAbsent](https://harvard-edtech.github.io/create-kaixa/Kaixa.html#waitForElementAbsent) - wait until an element is absent
- [waitForElementPresent](https://harvard-edtech.github.io/create-kaixa/Kaixa.html#waitForElementPresent) - wait until an element is present (on the page even if offscreen)
- [waitForElementVisible](https://harvard-edtech.github.io/create-kaixa/Kaixa.html#waitForElementVisible) - wait until an element is visible (on the page and visible)
- [waitForElementWithContentsPresent](https://harvard-edtech.github.io/create-kaixa/Kaixa.html#waitForElementWithContentsPresent) - wait for ane element with specific contents to be present (on the page even if offscreen)
- [waitForElementWithContentsVisible](https://harvard-edtech.github.io/create-kaixa/Kaixa.html#waitForElementWithContentsVisible) - wait for an element with specific contents to be visible (on the page and visible)

## **Assertions** – make sure your app behaves properly

- [assertAbsent](https://harvard-edtech.github.io/create-kaixa/Kaixa.html#assertAbsent) - make sure an element is absent
- [assertAbsentWithContents](https://harvard-edtech.github.io/create-kaixa/Kaixa.html#assertAbsentWithContents) - make sure an element is absent (find it by its contents)
- [assertDoesNotHaveClass](https://harvard-edtech.github.io/create-kaixa/Kaixa.html#assertDoesNotHaveClass) - make sure an element does not have a certain css class
- [assertExists](https://harvard-edtech.github.io/create-kaixa/Kaixa.html#assertExists) - make sure an element exists
- [assertExistsWithContents](https://harvard-edtech.github.io/create-kaixa/Kaixa.html#assertExistsWithContents) - make sure an element exists (find it by its contents)
- [assertHasClass](https://harvard-edtech.github.io/create-kaixa/Kaixa.html#assertHasClass) - make sure an element has a certain css class
- [assertNumElements](https://harvard-edtech.github.io/create-kaixa/Kaixa.html#assertNumElements) - make sure an element shows up a certain number of times

## **Handy Functions** – useful utilities

- [uniquify](https://harvard-edtech.github.io/create-kaixa/Kaixa.html#uniquify) - add a unique, random tag to a string
- [uniquifySimple](https://harvard-edtech.github.io/create-kaixa/Kaixa.html#uniquifySimple) - add a simple unique, random tag to a string
- [prompt](https://harvard-edtech.github.io/create-kaixa/Kaixa.html#prompt) - ask the user to provide text
- [promptPassword](https://harvard-edtech.github.io/create-kaixa/Kaixa.html#promptPassword) - ask the user to provide a password
- [getProfileValue](https://harvard-edtech.github.io/create-kaixa/Kaixa.html#getProfileValue) - get a value from the profile

## **Elements** – deal with elements

- [find](https://harvard-edtech.github.io/create-kaixa/Kaixa.html#find) - find an element by its css selector
- [findByContents](https://harvard-edtech.github.io/create-kaixa/Kaixa.html#findByContents) - find an element by its contents and selector
- [findChildOfAncestor](https://harvard-edtech.github.io/create-kaixa/Kaixa.html#findChildOfAncestor) - find an element by traversing the element tree up to an ancestor and back down to a child
- [elementAbsent](https://harvard-edtech.github.io/create-kaixa/Kaixa.html#elementAbsent) - check if an element is absent
- [elementExists](https://harvard-edtech.github.io/create-kaixa/Kaixa.html#elementExists) - check if an element exists
- [elementWithContentsAbsent](https://harvard-edtech.github.io/create-kaixa/Kaixa.html#elementWithContentsAbsent) - check if an element is absent (find it by its contents)
- [elementWithContentsExists](https://harvard-edtech.github.io/create-kaixa/Kaixa.html#elementWithContentsExists) - check if an element exists (find it by its contents)
- [descendantOf](https://harvard-edtech.github.io/create-kaixa/Kaixa.html#descendantOf) - get the descendant of an element
- [parentOf](https://harvard-edtech.github.io/create-kaixa/Kaixa.html#parentOf) - get the parent of an element
- [getNumElements](https://harvard-edtech.github.io/create-kaixa/Kaixa.html#getNumElements) - get the number of elements on a page that match a selector

## **Data** – get data from the page or elements

- [extractDataFromClass](https://harvard-edtech.github.io/create-kaixa/Kaixa.html#extractDataFromClass) - extract data from a css class
- [extractDataFromClassByContents](https://harvard-edtech.github.io/create-kaixa/Kaixa.html#extractDataFromClassByContents) - extract data from a css class (find the element by its contents)
- [getAttribute](https://harvard-edtech.github.io/create-kaixa/Kaixa.html#getAttribute) - get the value of an element attribute
- [getJSON](https://harvard-edtech.github.io/create-kaixa/Kaixa.html#getJSON) - get the JSON on the page
- [getQuery](https://harvard-edtech.github.io/create-kaixa/Kaixa.html#getQuery) - get the query parameters
- [getSource](https://harvard-edtech.github.io/create-kaixa/Kaixa.html#getSource) - get the page source
- [getText](https://harvard-edtech.github.io/create-kaixa/Kaixa.html#getText) - get the text inside an element
- [getTitle](https://harvard-edtech.github.io/create-kaixa/Kaixa.html#getTitle) - get the title of the page
- [getURL](https://harvard-edtech.github.io/create-kaixa/Kaixa.html#getURL) - get the URL of the current tab
- [getCurrentMonth](https://harvard-edtech.github.io/create-kaixa/Kaixa.html#getCurrentMonth) - get the current month
- [getCurrentDay](https://harvard-edtech.github.io/create-kaixa/Kaixa.html#getCurrentDay) - get the current day
- [getCurrentYear](https://harvard-edtech.github.io/create-kaixa/Kaixa.html#getCurrentYear) - get the current year
- [getDateString](https://harvard-edtech.github.io/create-kaixa/Kaixa.html#getDateString) - get the current date in mm/dd/yyyy format
- [getMonthAfterDays](https://harvard-edtech.github.io/create-kaixa/Kaixa.html#getMonthAfterDays) - get the month that it will be after a certain number of days
- [getDayAfterDays](https://harvard-edtech.github.io/create-kaixa/Kaixa.html#getDayAfterDays) - get the day that it will be after a certain number of days
- [getYearAfterDays](https://harvard-edtech.github.io/create-kaixa/Kaixa.html#getYearAfterDays) - get the year that it will be after a certain number of days
- [getDateStringAfterDays](https://harvard-edtech.github.io/create-kaixa/Kaixa.html#getDateStringAfterDays) - get the date in mm/dd/yyyy format as it will be after a certain number of days
- [padNumber](https://harvard-edtech.github.io/create-kaixa/Kaixa.html#padNumber) - pad a number so that it will be at least a certain number of digits

## **Logging** – write to the log

- [log](https://harvard-edtech.github.io/create-kaixa/Kaixa.html#log) - add something to the log

## **Defaults** – set behavior defaults

- [setDefaultHost](https://harvard-edtech.github.io/create-kaixa/Kaixa.html#setDefaultHost) - set the default hostname
- [setDontUseHTTPS](https://harvard-edtech.github.io/create-kaixa/Kaixa.html#setDontUseHTTPS) - set whether to use HTTPS by default
