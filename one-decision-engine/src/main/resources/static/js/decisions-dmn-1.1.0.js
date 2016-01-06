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
      { "name": "dmnDecisionColumnSect", "url": "/partials/dmn-decision-column-sect.html"},
      { "name": "dmnDecisionSect", "url": "/partials/dmn-decision-row-sect.html"},
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
  /**
   * Add a decision to the currently selected model.
   */
  addDecision: function() {
    console.info('addDecision');
    ractive.get('current.definitions.decisions').push({
      id:'decision'+(ractive.get('current.definitions.decisions').length+1),
      name:'Decision '+(ractive.get('current.definitions.decisions').length+1),
      decisionTable:{
        id:"dt1",
        hitPolicy:"UNIQUE",
        preferredOrientation:"RULE_AS_ROW",
        inputs:[{inputExpression:{text:"TODO"}}],
        outputs:[{name:"Strategy"}],
        rules:[]
      }
    });
  },
  /**
   * 
   */
  addDeploymentResource: function () {
    console.log('add...');
    $("#file").click();
  },
  /**
   * Hide the new model upload UI.
   */
  collapseAdd: function () {
    console.log('collapseAdd...');
    $('#upload').slideUp();
  },
  /**
   * Request a new blank decision model from the server.
   */
  create: function() {
    console.log('create');
    var jqXHR = $.post('/'+ractive.get('tenant.id')+'/decision-models/', function( data ) {
      console.log('created definition...');
      var location = jqXHR.getResponseHeader('Location');
//      data._links = {self:{href:location}};
      ractive.merge('decisions', data);
      ractive.select(data);
    });
  },
  /**
   * Delete a DMN model.
   */
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
  /**
   * Fetch list of model summaries for tenant.
   */
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
  getLink: function(definition) {
    console.info('getLink');
    return ractive.getServer()+'/'+ractive.get('tenant.id')+'/decision-models/'+definition.shortId;
  },
  oninit: function() {
    console.info('oninit');
    this.ajaxSetup();
    this.loadStandardPartials(this.get('stdPartials'));
  },

  /**
   * Select a single decision model in UI and fetch all associated details.
   */
  select: function(definition) {
    console.log('select...'+definition);
    ractive.set('saveObserver', false);
    ractive.set('current', definition);
    $.getJSON(ractive.getLink(definition), function( data ) {
      console.log('loaded definition...');
      data.definitions.decisions = data.definitions.decisions.sort(sortByName);
      data.definitions.businessKnowledgeModels = data.definitions.businessKnowledgeModels.sort(sortByName);
      ractive.set('current', data);
      ractive.set('saveObserver',true);
      ractive.showDefinition();
    });
  },
  /**
   * Show the 'raw' DMN for the current model.
   */
  showDefinition: function() {
    console.log('showDefinition');
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
  /**
   * Show the model list UI.
   */
  showResults: function() {
    console.log('showResults');
    $('#currentSect').slideUp();
    $('#decisionsTable').slideDown();
    $('#decisionsTableToggle').removeClass('glyphicon-triangle-right').addClass('glyphicon-triangle-bottom');
  },
  /**
   * Display UI to receive a new model upload.
   */
  showUpload: function () {
    console.log('showUpload...');
    $('#upload').slideDown();
  },
  /** 
   * Toggle editable property of decision tables.
   */
  toggleEdit: function() {
    console.info('toggleEdit');
    $('.onoffswitch-checkbox').checked = !$('.onoffswitch-checkbox').checked;
  },
  /**
   * Toggle UI between model list and single model focus.
   */
  toggleResults: function() {
    console.log('toggleResults');
    $('#decisionsTableToggle').toggleClass('glyphicon-triangle-bottom').toggleClass('glyphicon-triangle-right');
    $('#decisionsTable').slideToggle();
    $('#currentSect').slideToggle();
    if ($('#decisionsTable:visible').length>0) {
      ractive.fetch();
    }
  },
  update: function () {
    console.info('update');
    
    // remove drgElements (this is denormalisation) 
    ractive.set('current.definitions.drgElements',[]);
    ractive.set('current.tenantId',ractive.get('tenant.id'));

    $.ajax({
      url: ractive.getServer()+'/'+ractive.get('tenant.id')+'/decision-models/'+ractive.get('current.definitions.id'),
      type: 'PUT',
      contentType: 'application/json',
      data: JSON.stringify(ractive.get('current')),
      success: completeHandler = function(data, textStatus, jqXHR) {
        console.log('data: '+ data);
        ractive.splice('decisions',ractive.get('currentIdx'),1,data);
        ractive.showMessage('Saved');
      }
    });
  },
  /**
   * Upload one or more DMN files and associated meta data.
   */
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

// sync model and definitions 
ractive.observe('current.definitionId', function(newValue, oldValue, keypath) {
  ractive.set('current.definitions.id',newValue);
});
ractive.observe('current.name', function(newValue, oldValue, keypath) {
  ractive.set('current.definitions.name',newValue);
});

//Save on model change
ractive.observe('current.*', function(newValue, oldValue, keypath) {
  console.log('current prop change: '+newValue +','+oldValue+' '+keypath);  
  var ignored=[];
  if (ractive.get('saveObserver') && ignored.indexOf(keypath)==-1) {
    console.log('current prop change: '+newValue +','+oldValue+' '+keypath);
    ractive.update();
  } else { 
    console.warn  ('Skipped save of '+keypath);
    //console.log('current prop change: '+newValue +','+oldValue+' '+keypath);
    //console.log('  saveObserver: '+ractive.get('saveObserver'));
  }
});

function sortByName(a, b) {
  if (a.name < b.name) return -1;
  else if (a.name > b.name) return 1;
  else return 0;
}

$(document).ready(function() {
  console.info('Running ready handler');
  
  if (ractive.initCallbacks==undefined) ractive.initCallbacks = $.Callbacks();
  ractive.initCallbacks.add(function() {
//    ractive.applyBranding();
    ractive.fetch();
//    ractive.initControls();
  });

  if (ractive.initCallbacks!=undefined) ractive.initCallbacks.fire();
});