const fs = require('fs');
const packageJSON = require('../package.json');

module.exports = (path, dest) => {
  let body = fs.readFileSync(path, 'utf-8');

  // Update version placeholders
  body = body.replace(/\[VERSION\]/g, packageJSON.version);

  fs.writeFileSync(dest, body, 'utf-8');
};
