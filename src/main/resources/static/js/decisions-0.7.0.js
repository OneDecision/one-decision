var EASING_DURATION = 500;
fadeOutMessages = true;

// 4. We've got an element in the DOM, we've created a template, and we've
// loaded the library - now it's time to build our Hello World app.
var ractive = new AuthenticatedRactive({
  // The `el` option can be a node, an ID, or a CSS selector.
  el: 'container',

  // If two-way data binding is enabled, whether to only update data based on
  // text inputs on change and blur events, rather than any event (such as key
  // events) that may result in new data
  lazy: true,

  // We could pass in a string, but for the sake of convenience
  // we're passing the ID of the <script> tag above.
  template: '#template',
  // partial templates
  // partials: { question: question },

  // Here, we're passing in some initial data
  data: {
    csrfToken: getCookie(CSRF_COOKIE),
    server: 'http://api.knowprocess.com',
    content: 'test',
    entities: [],
    /* default for development, will be overridden if running in production */
    tenant: { id: 'omny' },
    //saveObserver:false,
    username: localStorage['username'],
    age: function(timeString) {
      return i18n.getAgeString(new Date(timeString))
    }
  },
  addCondition: function() {
    console.log('addCondition...');
    var newCondition = { name: 'Select...', expressions: new Array(parseInt(ractive.get('decision.conditions')[0].expressions.length)) };
    console.log('  '+JSON.stringify(newCondition));
    var idx = ractive.get('decision.conditions').push(newCondition);
    console.log('Adding typeahead to conditions: '+idx);
    $('.condition:nth-child('+idx+') th input').typeahead({ minLength:0,source:ractive.get('entityAttrs')}); 
  },
  addConclusion: function() {
    console.log('addConclusion...');
    var newConclusion = { name: 'Select...', expressions: new Array(parseInt(ractive.get('decision.conditions')[0].expressions.length)) };
    console.log('  '+JSON.stringify(newConclusion));
    var idx = ractive.get('decision.conclusions').push(newConclusion);
    console.log('Adding typeahead to conclusion: '+idx);
    // need row idx not conclusions idx
    idx = idx+ractive.get('decision.conditions').length+1
    $('.conclusion:nth-child('+idx+') th input').typeahead({ minLength:0,source:ractive.get('entityAttrs')}); 
  },
  addConditionExpr: function() {
    console.log('addConditionExpr...');
    $.each(ractive.get('decision.conditions'), function(i,d) {
      d.expressions.push('-');
    })
    $.each(ractive.get('decision.conclusions'), function(i,d) {
      d.expressions.push('-');
    })
  },
//  edit: function(type,i,j,obj) {
//    console.log('edit '+type+' at position '+i+','+j+': '+obj.name+'...');
//  },
//  delete: function (obj) {
//    console.log('delete '+obj+'...');
//    var url = obj.links != undefined
//        ? obj.links.filter(function(d) { console.log('this:'+d);if (d['rel']=='self') return d;})[0].href
//        : obj._links.self.href;
//    $.ajax({
//        url: url,
//        type: 'DELETE',
//        success: completeHandler = function(data) {
//          // ractive.fetch();
//        },
//        error: errorHandler = function(jqXHR, textStatus, errorThrown) {
//            ractive.handleError(jqXHR,textStatus,errorThrown);
//        }
//    });
//  },
  fetch: function () {
    console.log('fetch...');
    ractive.set('saveObserver', false);
    $.getJSON('/'+ractive.get('tenant.id')+'/decision-ui-models/'+ractive.get('decisionName'),  function( data ) {
      console.log('loaded decision...');
      ractive.set('decision', data);
      ractive.set('saveObserver',true);
      $('.entity.active').fadeIn();
      ractive.initAutoComplete();
    });
  },
  fetchDomain: function () {
    console.log('fetchDomain...');
    ractive.set('saveObserver', false);
    $.getJSON('/'+ractive.get('tenant.id')+'/domain/?projection=complete',  function( data ) {
      console.log('loaded entities...');
      ractive.merge('entities', data.entities);
      ractive.set('saveObserver',true);
      $('.entity.active').fadeIn();
      
      ractive.fetch();
      
      var entityAttrs = [];
      $(ractive.get('entities')).each(function(i,d) {
        $(d.fields).each(function(j,e) {
          entityAttrs.push({id: d.name+'.'+e.name, name: d.name+' '+e.label});
        });
      });
      ractive.set('entityAttrs', entityAttrs);
      // This may be too soon, check on the click handler of decision table conditions / conclusions
      
    });
  },
  handleError: function(jqXHR, textStatus, errorThrown) {
    switch (jqXHR.status) {
    case 401:
    case 403:
      ractive.showError("Session expired, please login again");
      window.location.href='/login';
      break;
    default:
      ractive.showError("Bother! Something has gone wrong: "+textStatus+':'+errorThrown);
    }
  },
  initAutoComplete: function() {
    console.log('initAutoComplete');
//    $('.expr-name .typeahead').click(function(ev) {
//      console.log(this);
//      console.log(ev);
//      if (this.typeahead()==undefined) {
//        console.error('not inited');
//      } else {
//        console.log('inited');
//      }
//    });
    $('.expr-name .typeahead').each(function(i,d) {
      console.log('binding entities to typeahead control: '+d.name);
      $(d).typeahead({ minLength:0,source:ractive.get('entityAttrs')}); 
    });
//      $(d.selector).on("click", function (ev) {
//        newEv = $.Event("keydown");
//        newEv.keyCode = newEv.which = 40;
//        $(ev.target).trigger(newEv);
//        return true;
//     });
  },
  oninit: function() {
    console.log('oninit');
    //this.ajaxSetup();
    $( document ).ajaxStart(function() {
      $( "#ajax-loader" ).show();
    });
    $( document ).ajaxStop(function() {
      $( "#ajax-loader" ).hide();
    });
  },
  save: function (decision) {
    console.log('save '+JSON.stringify(decision)+' ...');

//    var id = decision.id;
    var id = decision._links === undefined ? undefined : (
        decision._links.self.href.indexOf('?') == -1 ? decision._links.self.href : decision._links.self.href.substr(0,decision._links.self.href.indexOf('?')-1)
    );
    console.log('saving as id: '+id);
    decision.tenantId = ractive.get('tenant.id');
    $.ajax({
      url: id == undefined ? '/decision-ui-models/' : id,
      type: id == undefined ? 'POST' : 'PUT',
      contentType: 'application/json',
      data: JSON.stringify(decision),
      success: completeHandler = function(data, textStatus, jqXHR) {
        console.log('data: '+ data);
        var location = jqXHR.getResponseHeader('Location');
        if (location != undefined) { 
          ractive.set('decision._links.self.href',location);
//          ractive.set('decision.id',location.substring());
        }
//        if (jqXHR.status == 201) ractive.get('deciosn').push(ractive.get('current'));
//        if (jqXHR.status == 204) ractive.splice('contacts',ractive.get('currentIdx'),1,ractive.get('current'));
        ractive.showMessage('Saved');
      },
      error: errorHandler = function(jqXHR, textStatus, errorThrown) {
        ractive.handleError(jqXHR,textStatus,errorThrown);
      }
    });
  },
  showActivityIndicator: function(msg, addClass) {
    document.body.style.cursor='progress';
    this.showMessage(msg, addClass);
  },
  showError: function(msg) {
    this.showMessage(msg, 'bg-danger text-danger');
  },
  showFormError: function(formId, msg) {
    this.showError(msg);
    var selector = formId==undefined || formId=='' ? ':invalid' : '#'+formId+' :invalid';
    $(selector).addClass('field-error');
    $(selector)[0].focus();
  },
  showMessage: function(msg, additionalClass) {
    if (additionalClass == undefined) additionalClass = 'bg-info text-info';
    if (msg === undefined) msg = 'Working...';
    $('#messages p').empty().append(msg).removeClass().addClass(additionalClass).show();
//    document.getElementById('messages').scrollIntoView();
    if (fadeOutMessages && additionalClass!='bg-danger text-danger') setTimeout(function() {
      $('#messages p').fadeOut();
    }, EASING_DURATION*10);
  },
//  toggleEdit: function(type,i,j,obj) {
//    console.log('toggleEdit '+type+' at position '+i+','+j+': '+obj.name+'...');
//    $($('.'+type+' input')[i]).toggle();
//    $($('.'+type+' .edit')[i]).removeClass('hide').toggle();
//  },
//  toggleResults: function(ctrl) {
//    console.log('toggleResults: '+ctrl);
//    $('#contactsTableToggle').toggleClass('glyphicon-triangle-bottom').toggleClass('glyphicon-triangle-right');
//    $('#contactsTable').slideToggle();
//  },
  /**
   * Inverse of editField.
   */
  updateField: function (selector, path) {
    var tmp = $(selector).text();
    console.log('updateField '+path+' to '+tmp);
    ractive.set(path,tmp);
    $(selector).css('border-width','0px').css('padding','0px');
  },
  upload: function (formId) {
    console.log('upload:'+formId);
    ractive.showMessage('Uploading ...');

    var formElement = document.getElementById(formId);
    var formData = new FormData(formElement);
    var entity = $('#'+formId+' .entity').val();
    return $.ajax({
        type: 'POST',
        url: '/'+ractive.get('tenant.id')+'/'+entity+'/upload',
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

