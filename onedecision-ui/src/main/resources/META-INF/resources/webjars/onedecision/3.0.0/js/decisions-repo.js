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

var ractive = new BaseRactive({
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
    server: window['$env'] == undefined ? '//localhost:8090' : $env.server,
    featureEnabled: function(feature) {
      console.log('featureEnabled: '+feature);
      if (feature==undefined || feature.length==0) return true;
      else return ractive.get('tenant.show.'+feature);
    },
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
    matchRole: function(role) {
      console.info('matchRole: '+role)
      if (role==undefined || ractive.hasRole(role)) {
        $('.'+role).show();
        return true;
      } else {
        return false;
      }
    },
    //saveObserver:false,
    stdPartials: [
      { "name": "dmnCurrentSect", "url": "/webjars/onedecision/3.0.0/partials/dmn-current-sect.html"},
      { "name": "dmnListSect", "url": "/webjars/onedecision/3.0.0/partials/dmn-list-sect.html"},
      { "name": "loginSect", "url": "/webjars/auth/1.1.0/partials/login-sect.html"},
      { "name": "navbar", "url": "/webjars/onedecision/3.0.0/partials/dmn-navbar.html"},
      { "name": "profileArea", "url": "/partials/profile-area.html"},
      { "name": "sidebar", "url": "/partials/sidebar.html"},
      { "name": "titleArea", "url": "/partials/title-area.html"}
    ],
    tenant: { id: 'onedecision' },
    title: "Decision Models",
  },
  partials: {
    dmnCurrentSect: '',
    dmnListTable: '',
    dmnListSect: '',
    helpModal: '',
    loginSect: '',
    navbar: '',
    poweredBy: '',
    profileArea: '',
    sidebar: '',
    titleArea: '',
    supportBar: ''
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
    console.info('add...');
    $("#file").click();
  },
  /**
   * Hide the new model upload UI.
   */
  collapseAdd: function () {
    console.info('collapseAdd...');
    $('#upload').slideUp();
  },
  /**
   * Request a new blank decision model from the server.
   */
  create: function() {
    console.info('create');
    var jqXHR = $.post(ractive.getServer()+'/'+ractive.get('tenant.id')+'/decision-models/', function( data ) {
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
  delete: function (definition) {
    console.info('delete '+definition+'...');
    $.ajax({
        url: ractive.tenantUri(definition),
        type: 'DELETE',
        success: completeHandler = function(data) {
          ractive.set('current',undefined);
          ractive.fetch();
          ractive.showResults();
        }
    });
  },
  download: function() {
    console.info('download');
    $.ajax({
      headers: {
        "Accept": "application/xml"
      },
      url: ractive.tenantUri(ractive.get('current'))+'.dmn',
      crossDomain: true,
      success: function( data ) {
        console.warn('response;'+data);
        var serializer = new XMLSerializer();
        var dmn = serializer.serializeToString(data);
        ractive.downloadUri("data:application/xml," + encodeURIComponent(dmn),ractive.get('current.definitionId')+".dmn");
      }
    });
  },
  view: function(id) {
    console.info('view');
    $.ajax({
      headers: {
        "Accept": "text/html"
      },
      url: ractive.tenantUri(ractive.get('current'))+'/'+id+'.html',
      crossDomain: true,
      success: function( data ) {
        console.warn('response;'+data);
        var serializer = new XMLSerializer();
        var dmn = serializer.serializeToString(data);
        ractive.downloadUri("data:text/html," + encodeURIComponent(dmn),ractive.get('current.definitionId')+'/'+id+'.html');
      }
    });
  },
  /**
   * Fetch list of model summaries for tenant.
   */
  fetch: function () {
    $.ajax({
      dataType: "json",
      url: ractive.getServer()+'/'+ractive.get('tenant.id')+'/decision-models/',
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
  },
  /**
   * Select a single decision model in UI and fetch all associated details.
   */
  select: function(definition) {
    console.info('select...'+definition);
    ractive.set('saveObserver', false);
    ractive.set('current', definition);
    ractive.set('entityPath','/decision-models');
    var url = ractive.tenantUri(definition)+'/';
    console.log('querying URL: '+url);
    $.getJSON(url, function( data ) {
      console.log('loaded definition...');
      ractive.set('saveObserver', false);
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
    console.info('showDefinition');
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
    console.info('showResults');
    $('#currentSect').slideUp();
    $('#decisionsTable').slideDown();
    $('#decisionsTableToggle').removeClass('glyphicon-triangle-right').addClass('glyphicon-triangle-bottom');
  },
  showSearchMatched: function() {
    ractive.set('searchMatched',$('#decisionsTable tbody tr').length);
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
    console.info('toggleResults');
    $('#decisionsTableToggle').toggleClass('glyphicon-triangle-bottom').toggleClass('glyphicon-triangle-right');
    $('#decisionsTable').slideToggle();
    $('#currentSect').slideToggle();
    if ($('#decisionsTable:visible').length>0) {
      ractive.fetch();
    }
  },
  /**
   * Display UI to receive a new model upload.
   */
  toggleUpload: function () {
    console.info('showUpload...');
    $('#upload').slideToggle();
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
    console.info('upload:'+formId);
    if ($('#'+formId+' [type="file"]:invalid').length>0) {
      $('#'+formId+' .btn-file').addClass('btn-danger');
      ractive.showError('No files selected');
      return;
    }
    ractive.showMessage('Uploading ...');

    var formElement = document.getElementById(formId);
    var formData = new FormData(formElement);
    return $.ajax({
        type: 'POST',
        url: ractive.getServer()+'/'+ractive.get('tenant.id')+'/decision-models/upload',
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
  console.info('current prop change: '+newValue +','+oldValue+' '+keypath);  
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
