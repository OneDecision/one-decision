/*******************************************************************************
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
var ractive = new OneDecisionApp({
  el: 'container',
  
  // If two-way data binding is enabled, whether to only update data based on 
  // text inputs on change and blur events, rather than any event (such as key
  // events) that may result in new data
  lazy: true,
  
  template: '#template',

  // Initialize some data
  data: {
    contacts: [],
    //saveObserver:false,
    username: localStorage['username'],
    age: function(timeString) {
      return i18n.getAgeString(new Date(timeString))
    },
    formatDate: function(timeString) {
      return new Date(timeString).toLocaleDateString(navigator.languages);
    },
    stdPartials: [
    ],
    tenant: { id: 'onedecision' },
    title: "Decision",
    username: localStorage['username']
  },
  oninit: function() {
    console.info('oninit');
    this.ajaxSetup();
    this.loadStandardPartials(this.get('stdPartials'));
  }
});
