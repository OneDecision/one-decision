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
var EASING_DURATION = 500;
fadeOutMessages = true;

var ractive = new OneDecisionApp({
  el: 'container',
  lazy: true,
  template: '#template',
  data: {
    age: function(timeString) {
      return i18n.getAgeString(new Date(timeString))
    },
    decisions: [],
    entities: [],
    filter: undefined,
    formatDate: function(timeString) {
      return new Date(timeString).toLocaleDateString(navigator.languages);
    },
    formatDateTime: function(timeString) {
      if (timeString==undefined) return 'n/a';
      return new Date(timeString).toLocaleString(navigator.languages);
    },
    matchFilter: function(obj) {
      if (ractive.get('filter')==undefined) return true;
      else return ractive.get('filter').value.toLowerCase()==obj[ractive.get('filter').field].toLowerCase();
    },
    //saveObserver:false,
    stdPartials: [
      { "name": "dmnCurrentSect", "url": "/partials/dmn-current-sect.html"},
      { "name": "dmnListSect", "url": "/partials/dmn-list-sect.html"},
      { "name": "poweredBy", "url": "/partials/powered-by.html"},
      { "name": "profileArea", "url": "/partials/profile-area.html"},
      { "name": "sidebar", "url": "/partials/sidebar.html"},
      { "name": "titleArea", "url": "/partials/title-area.html"}
    ],
    tenant: { id: 'onedecision' },
    title: "Decision Model Repository",
    username: localStorage['username']
  },
  add: function () {
    console.log('add...');
    $('#upload').slideDown();
  },
  addDeploymentResource: function () {
    console.log('add...');
    $("#file").click();
  },
  collapseAdd: function () {
    console.log('collapseAdd...');
    $('#upload').slideUp();
  },
  edit: function(type,i,j,obj) {
    console.log('edit '+type+' at position '+i+','+j+': '+obj.name+'...');
  },
  delete: function (decision) {
    console.log('delete '+decision+'...');
    $.ajax({
        url: '/'+ractive.get('tenant.id')+'/decision-models/'+decision.id,
        type: 'DELETE',
        success: completeHandler = function(data) {
          ractive.set('current',undefined);
          ractive.fetch();
          ractive.showResults();
        }
    });
  },
  fetch: function () {
    $.ajax({
      dataType: "json",
      url: '/'+ractive.get('tenant.id')+'/decision-models/',
      crossDomain: true,
      success: function( data ) {
        if (data['_embedded'] == undefined) {
          ractive.merge('decisions', data);
          ractive.set('saveObserver',true);
        }else{
          ractive.merge('decisions', data['_embedded'].decisions);
          ractive.set('saveObserver', true);
        }
        if (ractive.fetchCallbacks!=null) ractive.fetchCallbacks.fire();
        ractive.set('searchMatched',$('#decisionsTable tbody tr:visible').length);
      }
    });
  },
  oninit: function() {
    console.info('oninit');
    this.ajaxSetup();
    this.loadStandardPartials(this.get('stdPartials'));
  },
  select: function(decision) {
    console.log('select...'+decision);
    ractive.set('saveObserver', false);
    ractive.set('current', decision);
    ractive.showDecision();
    $.getJSON('/'+ractive.get('tenant.id')+'/decision-models/'+decision.id,  function( data ) {
      console.log('loaded decision...');
      ractive.set('current', data);
      ractive.set('saveObserver',true);
      ractive.showDecision();
    });
  },
  showDecision: function() {
    console.log('showDecision');
    try {
      $('#currentHighlightedDmn').html(hljs.highlight('xml',formatXml(ractive.get('current.definitionXml'))).value);
    } catch (e) { 
      console.warn('unable to load DMN syntax highlighter'); 
      $('#currentHighlightedDmn').html(formatXml(ractive.get('current.definitionXml')).replace(/</g,'&lt;'));
    }
    $('#decisionsTable').slideUp();
    $('#decisionsTableToggle').removeClass('glyphicon-triangle-bottom').addClass('glyphicon-triangle-right');
    $('#currentSect').slideDown();
  },
  showResults: function() {
    console.log('showResults');
    $('#currentSect').slideUp();
    $('#decisionsTable').slideDown();
    $('#decisionsTableToggle').removeClass('glyphicon-triangle-right').addClass('glyphicon-triangle-bottom');
  },
  toggleResults: function() {
    console.log('toggleResults');
    $('#decisionsTableToggle').toggleClass('glyphicon-triangle-bottom').toggleClass('glyphicon-triangle-right');
    $('#decisionsTable').slideToggle();
    $('#currentSect').slideToggle();
    if ($('#decisionsTable:visible').length>0) {
      ractive.fetch();
    }
  },
  upload: function (formId) {
    console.log('upload:'+formId);
    ractive.showMessage('Uploading ...');

    var formElement = document.getElementById(formId);
    var formData = new FormData(formElement);
    return $.ajax({
        type: 'POST',
        url: '/'+ractive.get('tenant.id')+'/decision-models/upload',
        data: formData,
        cache: false,
        contentType: false,
        processData: false,
        success: function(response) {
          ractive.collapseAdd();
          ractive.fetch();
          ractive.showMessage('Successfully uploaded data');
        },
        error: ractive.handleError
      });
  }
});

// Thanks to https://gist.github.com/sente/1083506
function formatXml(xml) {
  var formatted = '';
  var reg = /(>)(<)(\/*)/g;
  xml = xml.replace(reg, '$1\r\n$2$3');
  var pad = 0;
  jQuery.each(xml.split('\r\n'), function(index, node) {
      var indent = 0;
      if (node.match( /.+<\/\w[^>]*>$/ )) {
          indent = 0;
      } else if (node.match( /^<\/\w/ )) {
          if (pad != 0) {
              pad -= 1;
          }
      } else if (node.match( /^<\w[^>]*[^\/]>.*$/ )) {
          indent = 1;
      } else {
          indent = 0;
      }

      var padding = '';
      for (var i = 0; i < pad; i++) {
          padding += '  ';
      }

      formatted += padding + node + '\r\n';
      pad += indent;
  });

  return formatted;
}
