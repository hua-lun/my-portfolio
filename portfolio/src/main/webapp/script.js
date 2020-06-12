// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

/**
 * Adds a random fact to the page.
 */
function addRandomFact() {
  const facts =
      ['I performed in Chingay 2011.', 'My favourite game is between Tetris and Pokemon.', 
        'I own too many piplup plushies.', 'I went to Korea for Summer school 2019.'];

  // Pick a random fact.
  const fact = facts[Math.floor(Math.random() * facts.length)];

  // Add it to the page.
  const factContainer = document.getElementById('fact-container');
  factContainer.innerText = fact;
}

/**
 * Fetches a random message from the server and adds it to the DOM.
 */
function getRandomMessage() {
  console.log('Fetching a random message.');

  // The fetch() function returns a Promise because the request is asynchronous.
  const responsePromise = fetch('/data');

  // When the request is complete, pass the response into handleResponse().
  responsePromise.then(handleResponse);
}

/**
 * Handles response by converting it to text and passing the result to
 * addMessageToDom().
 */
function handleResponse(response) {
  console.log('Handling the response.');

  // response.text() returns a Promise, because the response is a stream of
  // content and not a simple variable.
  const textPromise = response.text();

  // When the response is converted to text, pass the result into the
  // addMessageToDom() function.
  textPromise.then(addMessageToDom);
}

/** Adds a random message to the DOM. */
function addMessageToDom(message) {
  console.log('Adding message to dom: ' + message);

  const messageContainer = document.getElementById('message-container');
  messageContainer.innerText = message;
}

/**
 * The above code is organized to show each individual step, but we can use an
 * ES6 feature called arrow functions to shorten the code. This function
 * combines all of the above code into a single Promise chain. You can use
 * whichever syntax makes the most sense to you.
 */
function getRandomMessageUsingArrowFunctions() {
  fetch('/data').then(response => response.text()).then((message) => {
    document.getElementById('message-container').innerText = message;
  });
}

/**
 * Another way to use fetch is by using the async and await keywords. This
 * allows you to use the return values directly instead of going through
 * Promises.
 */
async function getRandomMessageUsingAsyncAwait() {
  const response = await fetch('/data');
  const message = await response.text();
  document.getElementById('message-container').innerText = message;
}
