var EASING_DURATION = 500;
fadeOutMessages = true;

var ractive = new OneDecisionApp({
  el: 'container',
  lazy: true,
  template: '#template',
  data: {
    csrfToken: getCookie(CSRF_COOKIE),
    content: 'test',
    decisionTemplate: { "name":"A new decision", "hitPolicy":null, "domainModelUri":null, "inputs":null, "outputs":null, "conditions":[{ "name":"a new condition","expressions":["-"],"label":"a new condition"}],"conclusions":[{ "name":"a new conclusion","expressions":["-"],"label":"a new conclusion"}] },
    decision: $(this.decisionTemplate).clone(),
    decisions: [],
    entities: [],
    filter: undefined,
    formatDate: function(timeString) {
      return new Date(timeString).toLocaleDateString(navigator.languages);
    },
    matchFilter: function(obj) {
      if (ractive.get('filter')==undefined) return true;
      else return ractive.get('filter').value.toLowerCase()==obj[ractive.get('filter').field].toLowerCase();
    },
    //saveObserver:false,
    tenant: { id: 'onedecision' },
    username: localStorage['username'],
    age: function(timeString) {
      return i18n.getAgeString(new Date(timeString))
    }
  },
  add: function () {
    console.log('add...');
    $('#upload').slideDown();
  },
  addDeploymentResource: function () {
    console.log('add...');
    //$('#upload fieldset').append($('#resourceControl').html());
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
        }
      });
  }
});
