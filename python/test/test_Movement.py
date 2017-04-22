// Load Unit.js module
exports.test_Movement = function() {

var Movement = require('../lib/Movement.js');
var test = require('unit.js');
    
// just for example of tested value
var direction = Movement.DOWN;

// assert that example variable is a string
test.value( direction == Movement.DOWN ).isTrue();

// or with assert
test.assert(typeof direction == 'Movement');
}
