# Kaixa, a simply and customizable way to write Katalon tests

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

Check out the Kaixa functions below or in the left-hand menu :)

<hr />

<h1 style="display: flex; align-items: center;">
  <div style="flex-grow: 1; height: 1px; background: #ccc; margin-right: 10px;">
  </div>
  <div>
    Functions by Category:
  </div>
  <div style="flex-grow: 1; height: 1px; background: #ccc; margin-left: 10px;">
  </div>
</h1>

## Interactions

- [click](https://google.com) – click something
- [click](https://google.com) – click something
- [click](https://google.com) – click something
- [click](https://google.com) – click something
- [click](https://google.com) – click something
- [click](https://google.com) – click something
- [click](https://google.com) – click something
- [click](https://google.com) – click something
- [click](https://google.com) – click something

## Browser Actions

## Canvas & LTI Functions

## Intelligent Waiting 

## Assertions

## Data and Element
