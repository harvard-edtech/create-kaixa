const fs = require('fs');
const path = require('path');
const promptSync = require('prompt-sync')();
const { execSync } = require('child_process');

// Local imports
const copyTo = require('./helpers/copyTo');
const packageJSON = require('./package.json');

/* eslint-disable no-console */

// Prep command executor
const exec = (command) => {
  return execSync(command, { stdio: 'inherit' });
};

// Import helpers
const print = require('./helpers/print');

const prompt = (title, notRequired) => {
  const val = promptSync(title);
  if (val === null || (!notRequired && !val)) {
    process.exit(0);
  }
  return val;
};
print.savePrompt(prompt);

// Initializer script
module.exports = () => {
  const targetFilename = path.join(
    process.env.PWD,
    'Keywords/Kaixa.groovy'
  );
  const updating = fs.existsSync(targetFilename);

  // Make sure we're in a Katalon project
  if (
    !fs.existsSync(path.join(process.env.PWD, 'Keywords'))
    || !fs.existsSync(path.join(process.env.PWD, 'Profiles'))
    || !fs.existsSync(path.join(process.env.PWD, 'Drivers'))
    || !fs.existsSync(path.join(process.env.PWD, 'Test Cases'))
  ) {
    // Not in a Katalon project folder
    console.log('');
    print.title('Error | Kaixa Setup');
    console.log('');
    console.log('Oops! Looks like you\'re not in the right folder.');
    console.log('Navigate to the top-level folder of a Katalon project then try this again.')
    return;
  }

  // Welcome
  print.title(`${updating ? 'Updating' : 'Adding'} Kaixa`);
  console.log('');
  console.log(`We are about to ${updating ? 'update your Kaixa version to' : 'install Kaixa version'} ${packageJSON.version}.`);
  console.log('');
  print.enterToContinue();

  // Copy Kaixa
  copyTo(
    path.join(__dirname, 'src/Kaixa.groovy'),
    targetFilename
  );

  // Confirm
  console.log('');
  print.title('Done!');
  console.log('');
  if (updating) {
    console.log(`We updated to Kaixa version ${packageJSON.version} for this project.`);
    console.log('NOTE: if Katalon\'s open, open & close Keywords/default package/Kaixa.groovy');
    console.log('(Katalon is silly and doesn\'t reload resources unless you open and close them)');
  } else {
    console.log(`We added Kaixa version ${packageJSON.version} to this project.`);
  }
};
