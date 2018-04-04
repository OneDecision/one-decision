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
  parser: new DOMParser(),
  serializer: new XMLSerializer(),
  transformer: new XSLTProcessor(),
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
    match: function(obj) {
      if (obj == ractive.get('decisionId')) return true;
      else return false;
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
    stdPartials: [
      { "name": "dmnTableSect", "url": "/webjars/onedecision/3.0.0/partials/decision-table-sect.html"},
      { "name": "loginSect", "url": "/webjars/auth/1.1.0/partials/login-sect.html"},
      { "name": "profileArea", "url": "/partials/profile-area.html"},
      { "name": "sidebar", "url": "/partials/sidebar.html"},
      { "name": "titleArea", "url": "/partials/title-area.html"}
    ],
    tenant: { id: 'onedecision' },
    title: "Decision Table",
  },
  partials: {
    dmnTableSect: '',
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
   * Add a column to a decision table.
   * @param exprType expression type ('decision' or 'bkm').
   * @param idx position of expression in its respective list.
   */
  addColumnCondition: function (exprType, idx) {
    console.log('addColumnCondition to '+exprType+' idx:'+idx);
    var input = { inputExpression: { text: '' }};
    console.log(' logic:'+ractive.get('decisionLogic'));
    switch (exprType) {
    case 'bkm':
      ractive.get('current.definitions.businessKnowledgeModels['+idx+'].encapsulatedLogic.decisionTable.inputs').push(input);
      $.each(ractive.get('current.definitions.businessKnowledgeModels['+idx+'].encapsulatedLogic.decisionTable.rules'), function(i,d) {
        d.inputEntry.push({inputEntry:[],outputEntry:[]});
      });
    default:
      ractive.get('current.definitions.'+exprType+'s['+idx+'].decisionTable.inputs').push(input);
      $.each(ractive.get('current.definitions.'+exprType+'s['+idx+'].decisionTable.rules'), function(i,d) {
        d.inputEntry.push({inputEntry:['-'],outputEntry:[]});
      });
    }
  },
  /**
   * Add rule as row to a decision table.
   * @param exprType expression type ('decision' or 'bkm').
   * @param idx position of expression in its respective list.
   */
  addRowRule: function (exprType, idx) {
    console.log('addRowRule to '+exprType+' idx:'+idx);
    switch (exprType) {
    case 'bkm':
      var rule = { inputEntry:new Array(), outputEntry:new Array() };
      ractive.get('current.definitions.businessKnowledgeModels['+idx+'].encapsulatedLogic.decisionTable.rules').push(rule);
    default:
      var l = ractive.get('current.definitions.'+exprType+'s['+idx+'].decisionTable.inputs').length;
      var rule = { inputEntry:new Array(l), outputEntry:new Array(1) };
      ractive.get('current.definitions.'+exprType+'s['+idx+'].decisionTable.rules').push(rule);
    }
  },
  fetch: function() {
    console.info('fetch:'+window.location.href);
    ractive.set('saveObserver', false);
    ractive.set('definitionId', getSearchParameters()['definitionId']);
    ractive.set('elementId', getSearchParameters()['elementId']);
    $.getJSON(ractive.getServer()+'/'+ractive.get('tenant.id')+'/decision-models/'+ractive.get('definitionId')+'/', function( data ) {
      console.log('loaded model...');
      data.definitions.decisions = data.definitions.decisions.sort(sortByName);
      data.definitions.businessKnowledgeModels = data.definitions.businessKnowledgeModels.sort(sortByName);
      ractive.set('current', data);
      var el = ractive.get('current.definitions.drgElements').find(function(el) { return el.value.id == ractive.get('elementId'); });
      ractive.set('title', el.declaredType.substring(el.declaredType.lastIndexOf('.')+1));
      ractive.renderDmnElement();
      ractive.set('saveObserver',true);
      
      $('#editOnOffSwitch').click(function(ev) {
        ractive.toggleEdit();
      });
      if (Object.keys(getSearchParameters()).indexOf('edit') == -1) {
        ractive.set('editable',false);
      } else {
        ractive.set('editable',true);
      }
    });
  },
  fetchRenderer: function() {
    $.get(ractive.getServer()+'/xslt/dmn2html.xslt', function(data) {
      ractive.transformer.importStylesheet(data);
    });
  },
  oncomplete: function() {
    this.fetchRenderer();
    this.on('tenantConfigLoaded', this.onTenantConfigLoaded);
  },
  onTenantConfigLoaded: function() {
    console.info('onTenantConfigLoaded');
    ractive.fetch();
  },
  renderDmnElement: function() {
    if (ractive.get('current.dmn') == undefined) {
      ractive.set('current.dmn',ractive.parser.parseFromString(ractive.get('current.definitionXml'), "text/xml"));
    }
    ractive.transformer.clearParameters();
    ractive.transformer.setParameter('http://www.omg.org/spec/BPMN/20100524/MODEL', 'drgElementId', ractive.get('elementId'));
    var result = ractive.transformer.transformToDocument(ractive.get('current.dmn'));
    if (result == undefined) {
      ractive.showError("Unable to render image of DMN element "+ractive.get('elementId'));
    } else {
      // debugging...
      ractive.set('current.html',ractive.serializer.serializeToString(result.firstElementChild));
      $('#drgElement').empty().append(result.querySelector('#'+ractive.get('elementId')+'Sect'));
    }
  },
  save: function() { 
    console.warn('save is not implemented on this public demo system');
  },
  toggleEdit: function() {
    console.info('toggleEdit:'+!$('#editOnOffSwitch').prop('checked'));
    ractive.toggle('editable');
  },
});

function sortByName(a, b) {
  if (a.name < b.name) return -1;
  else if (a.name > b.name) return 1;
  else return 0;
}

ractive.observe('editable', function(newValue, oldValue, keypath) {
  console.log('editable: '+newValue +','+oldValue+','+keypath);  
  if (newValue) {
    $('#editOnOffSwitch').prop('checked',newValue);
    if (ractive.hasRole('manage')) $('.manage').prop('readonly',false).prop('disabled',false);
    if (ractive.hasRole('author')) $('.author,.manage').prop('readonly',false).prop('disabled',false);
    $('.editCell').show();
  } else {
    $('#editOnOffSwitch').prop('checked',newValue);
    $('.author,.manage').prop('readonly',true).prop('disabled',true);
    $('.editCell').hide();
  }
});
