#!/usr/bin/env node

/**
 * This code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

'use strict';

var nodeVersion = process.versions.node;
var parts = nodeVersion.split('.');
var majorVersion = parts[0];

if (majorVersion < 10) {
  console.error('Oops! You are running node version ' + majorVersion + '.');
  console.log('Kaixa requires Node 10 or higher. Please update your version of Node.');
} else {
  require('./index.js')();
}
