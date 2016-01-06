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
  lazy: true,
  template: '#template',
  data: {
    decisionTemplate: { 
      "decisionName":"A new decision",
      "hitPolicy":"U",
      "domainModelUri":null,
      "inputs":[{name: "Select..."},{name:"Select..."}],
      "outputs":[{name:"Select..."}],
      "rules":[ 
        { "inputEntries": ["A1", "A2"], "outputEntries": ["A3"] },
        { "inputEntries": ["B1", "B2"], "outputEntries": ["B3"] },
        { "inputEntries": ["C1", "C2"], "outputEntries": ["C3"] }
      ] 
    },      
    decision: $(this.decisionTemplate).clone(),
    decisions: [],
    entities: [],
    filter: undefined,
    formatDate: function(timeString) {
      if (timeString==undefined) return 'n/a';
      return new Date(timeString).toLocaleDateString(navigator.languages);
    },
    hitPolicies: [
      {"id":"U","name":"Unique"},
      {"id":"A","name":"Any"},
      {"id":"P","name":"Priority"},
      {"id":"F","name":"First"},
      {"id":"C","name":"Collect"},
      {"id":"O","name":"Output order"},
      {"id":"R","name":"Rule order"}
    ],
    matchFilter: function(obj) {
      if (ractive.get('filter')==undefined) return true;
      else return ractive.get('filter').value.toLowerCase()==obj[ractive.get('filter').field].toLowerCase();
    },
    stdPartials: [
      { "name": "decisionTableSect", "url": "/partials/decision-table-sect.html"},
      { "name": "poweredBy", "url": "/partials/powered-by.html"},
      { "name": "profileArea", "url": "/partials/profile-area.html"},
      { "name": "sidebar", "url": "/partials/sidebar.html"},
      { "name": "titleArea", "url": "/partials/title-area.html"},
      { "name": "uiListSect", "url": "/partials/ui-list-sect.html"}
    ],
    //saveObserver:false,
    tenant: { id: "onedecision" },
    title: "Decision Table Definer",
    username: localStorage['username'],
    age: function(timeString) {
      return i18n.getAgeString(new Date(timeString))
    }
  },
  add: function () {
    console.log('add...');
    ractive.set('saveObserver',false);
    var template = ractive.get('decisionTemplate');
    //$().clone();
    console.log('  tmplt:'+template);
    template = JSON.parse(JSON.stringify(template));
    console.log('  tmplt:'+template);
    ractive.set('decision',template);
    ractive.set('decision.tenantId',ractive.get('tenant.id'));
//    ractive.addRule();
//    ractive.addConclusion();
    ractive.set('saveObserver',true);
    ractive.showDecision();
  },
  addRule: function() {
    console.log('addRule...');
    var rule0 = ractive.get('decision.rules')[0];
    var newRule = { 
      inputEntries: new Array(parseInt(rule0 == undefined ? '0' : rule0.inputEntries.length)),
      outputEntries: new Array(parseInt(rule0 == undefined ? '0' : rule0.outputEntries.length)) 
    };
    $.each(newRule.inputEntries, function(i,d) { 
      newRule.inputEntries[i] = '-'; 
    });
    $.each(newRule.outputEntries, function(i,d) { 
      newRule.outputEntries[i] = '-'; 
    });
    console.log('  '+JSON.stringify(newRule));
    var idx = ractive.get('decision.rules').push(newRule);
    console.log('Adding typeahead to conditions: '+idx);
    $('.condition:nth-child('+idx+') th input').typeahead({ minLength:0,source:ractive.get('entityAttrs')}); 
  },
  /*addConclusion: function() {
    console.log('addConclusion...');
    var conds = ractive.get('decision.conditions')[0];
    var newConclusion = { name: 'Select...', expressions: new Array(parseInt(conds == undefined ? '0' : conds.expressions.length)) };
    console.log('  '+JSON.stringify(newConclusion));
    var idx = ractive.get('decision.conclusions').push(newConclusion);
    console.log('Adding typeahead to conclusion: '+idx);
    // need row idx not conclusions idx
    idx = idx+ractive.get('decision.conditions').length+1
    $('.conclusion:nth-child('+idx+') th input').typeahead({ minLength:0,source:ractive.get('entityAttrs')}); 
  },*/
  addRuleExpr: function() {
    console.log('addRuleExpr...');
    $.each(ractive.get('decision.rules'), function(i,d) {
      d.inputValues.push('-');
      d.outputValues.push('-');
    })
  },
  edit: function(type,i,j,obj) {
    console.log('edit '+type+' at position '+i+','+j+': '+obj.name+'...');
  },
  decision2Image: function() {
    // first hide controls 
    $('.decision-name').attr('colspan',2);
    $('#decisionTable .glyphicon-plus, #decisionTable .glyphicon-remove').parent().hide();
    html2canvas($("#decisionTable"), {
      onrendered: function(canvas) {
        theCanvas = canvas;
        document.body.appendChild(canvas);
        // Convert and download as image 
        Canvas2Image.saveAsPNG(canvas); 
        // Clean up 
        document.body.removeChild(canvas);
        // restore controls 
        $('.decision-name').attr('colspan',3);
        $('#decisionTable .glyphicon-plus, #decisionTable .glyphicon-remove').parent().show();
      }
    });
  },
  delete: function (decision) {
    console.log('delete '+decision+'...');
    $.ajax({
        url: ractive.getServer()+'/'+ractive.get('tenant.id')+'/decision-ui-models/'+decision.id,
        type: 'DELETE',
        success: completeHandler = function(data) {
           ractive.fetch();
           ractive.toggleResults();
        }
    });
  },
  deleteCondition: function (idx) {
    console.log('deleteCondition: '+idx+'...');
    ractive.get('decision.conditions').splice(idx,1);
  },
  deleteConclusion: function (idx) {
    console.log('deleteConclusion: '+idx+'...');
    ractive.get('decision.conclusions').splice(idx,1);
  },
  fetch: function () {
    $.ajax({
      dataType: "json",
      url: ractive.getServer()+'/'+ractive.get('tenant.id')+'/decision-ui-models/',
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
  fetchDomain: function () {
    console.log('fetchDomain...');
    ractive.set('saveObserver', false);
    $.getJSON(ractive.getServer()+'/'+ractive.get('tenant.id')+'/domain-model/',  function( data ) {
      console.log('loaded entities...');
      ractive.merge('entities', data.entities);
      ractive.set('saveObserver',true);
      $('.entity.active').fadeIn();
      
//      ractive.fetch();
      
      var entityAttrs = [];
      $(ractive.get('entities')).each(function(i,d) {
        $(d.fields).each(function(j,e) {
          entityAttrs.push({id: d.name+'.'+e.name, name: d.name+' '+e.label});
        });
      });
      ractive.set('entityAttrs', entityAttrs);
      // This may be too soon, check on the click handler of decision table conditions / conclusions

      if (ractive.fetchDomainCallbacks!=null) ractive.fetchDomainCallbacks.fire();
    });
  },
  initAutoComplete: function() {
    console.log('initAutoComplete');
    $('.expr-name .typeahead').each(function(i,d) {
      console.log('binding entities to typeahead control: '+d.name);
      $(d).typeahead({ minLength:0,source:ractive.get('entityAttrs') }); 
    });
    $('.hit-policy .typeahead').each(function(i,d) {
      console.log('binding hit policies to typeahead control: '+d.name);
      $(d).typeahead({ minLength:0,showHintOnFocus:true,source:ractive.get('hitPolicies') }); 
    });
  },
  oninit: function() {
    console.info('oninit');
    this.ajaxSetup();
    this.loadStandardPartials(this.get('stdPartials'));
  },
  save: function (decision) {
    console.log('save '+JSON.stringify(decision)+' ...');

    var id = decision._links === undefined ? undefined : (
        decision._links.self.href.indexOf('?') == -1 ? decision._links.self.href : decision._links.self.href.substr(0,decision._links.self.href.indexOf('?')-1)
    );
    console.log('saving as id: '+id);
    decision.tenantId = ractive.get('tenant.id');
    $.ajax({
      url: id == undefined 
          ? ractive.getServer()+'/'+ractive.get('tenant.id')+'/decision-ui-models/' 
          : id.replace(/\/decision-ui-models\//, '/'+ractive.get('tenant.id')+'/decision-ui-models/'),
      type: id == undefined ? 'POST' : 'PUT',
      contentType: 'application/json',
      data: JSON.stringify(decision),
      success: completeHandler = function(data, textStatus, jqXHR) {
        console.log('data: '+ data);
        var location = jqXHR.getResponseHeader('Location');
        if (jqXHR.status == 201) { 
          //console.log('Created '+location);
          location = location.replace(ractive.get('tenant.id')+'/',''),
          data._links={ self: {href:location} };
          ractive.set('saveObserver',false);
          ractive.set('currentIdx',ractive.get('decisions').push(data));
          ractive.set('decision',data);
          ractive.set('saveObserver',true);
        }
        if (jqXHR.status == 200) { 
          //console.log('Updated '+id);
          ractive.splice('decisions',ractive.get('currentIdx'),1,data);
        }
        ractive.showMessage('Saved');
      },
      error: errorHandler = function(jqXHR, textStatus, errorThrown) {
        ractive.handleError(jqXHR,textStatus,errorThrown);
      }
    });
  },
  select: function(idx,decision) {
    console.log('select...'+decision);
    ractive.set('saveObserver', false);
    ractive.set('currentIdx', idx);
    $.getJSON(ractive.getServer()+'/'+ractive.get('tenant.id')+'/decision-ui-models/'+decision.id,  function( data ) {
      console.log('loaded decision...');
      ractive.set('decision', data);
      ractive.set('saveObserver',true);
      ractive.showDecision();
      ractive.initAutoComplete();
    });
  },
  showDecision: function() {
    console.log('showDecision');
    $('#decisionsTable').slideUp();
    $('#decisionsTableToggle').removeClass('glyphicon-triangle-bottom').addClass('glyphicon-triangle-right');
    $('#dtSect').slideDown();
  },
  toggleEdit: function(type,i,j,obj) {
    console.log('toggleEdit '+type+' at position '+i+','+j+': '+obj.name+'...');
    $($('.'+type+' input')[i]).toggle();
    $($('.'+type+' .edit')[i]).removeClass('hide').toggle();
  },
  toggleResults: function(ctrl) {
    console.log('toggleResults: '+ctrl);
    $('#decisionsTableToggle').toggleClass('glyphicon-triangle-bottom').toggleClass('glyphicon-triangle-right');
    $('#decisionsTable').slideToggle();
    $('#dtSect').slideToggle();
    if ($('#decisionsTable:visible').length>0) {
      ractive.fetch();
    }
  },
  upload: function (formId) {
    console.log('upload:'+formId);
    ractive.showMessage('Uploading ...');

    var formElement = document.getElementById(formId);
    var formData = new FormData(formElement);
    var entity = $('#'+formId+' .entity').val();
    return $.ajax({
        type: 'POST',
        url: ractive.getServer()+'/'+ractive.get('tenant.id')+'/'+entity+'/upload',
        data: formData,
        cache: false,
        contentType: false,
        processData: false,
        success: function(response) {
  //        console.log('successfully uploaded data');
          ractive.showMessage('Successfully uploaded data');
        },
        error: function(jqXHR, textStatus, errorThrown) {
          ractive.handleError(jqXHR, textStatus, errorThrown);
        }
      });
  }
});

//Save on model change
ractive.observe('decision.*', function(newValue, oldValue, keypath) {
  console.log('current prop change: '+newValue +','+oldValue+' '+keypath);  
  var ignored=[];
  if (ractive.get('saveObserver') && ignored.indexOf(keypath)==-1) {
    console.log('current prop change: '+newValue +','+oldValue+' '+keypath);
    ractive.save(ractive.get('decision'));
  } else { 
    console.warn  ('Skipped save of '+keypath);
    //console.log('current prop change: '+newValue +','+oldValue+' '+keypath);
    //console.log('  saveObserver: '+ractive.get('saveObserver'));
  }
});


$(document).ready(function() {
  console.info('Running ready handler');
  
  if (ractive.initCallbacks==undefined) ractive.initCallbacks = $.Callbacks();
  ractive.initCallbacks.add(function() {
    ractive.fetchDomain();
//    ractive.applyBranding();
    ractive.fetch();
//    ractive.initControls();
  });

  if (ractive.initCallbacks!=undefined) ractive.initCallbacks.fire();

});