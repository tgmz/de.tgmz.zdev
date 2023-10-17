/*!
 * jQuery JavaScript Library v1.11.1
 * http://jquery.com/
 *
 * Includes Sizzle.js
 * http://sizzlejs.com/
 *
 * Copyright 2005, 2014 jQuery Foundation, Inc. and other contributors
 * Released under the MIT license
 * http://jquery.org/license
 *
 * Date: 2014-05-01T17:42Z
 */
/* Function to setup the elements and event listener */
$(document).ready(function() {
  // Create all the elements
  $("#SubmitButton").click(function() {
	$("#OutputArea").val("Basic " + btoa($("#UserIdInputArea").val().toUpperCase() + ":" + $("#PasswordInputArea").val()));
  });
});