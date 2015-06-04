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
    /* default for development, will be overridden if running in production */
    tenant: { id: 'omny' },
    //saveObserver:false,
    username: localStorage['username'],
    age: function(timeString) {
      return i18n.getAgeString(new Date(timeString))
    }
  },
  addField: function(dataName) {
    console.log('addField...'+dataName);
    var newField = ractive.get(dataName);
    console.log('  '+JSON.stringify(newField));
    ractive.get('domain.entities')[ractive.get('entityIdx')].fields.push(newField);
    $('#fieldModal').modal('hide');
  },
  editField: function (selector, path) {
    console.log('editField '+path+'...');
    $(selector).css('border-width','1px').css('padding','5px 10px 5px 10px');
  },
  delete: function (obj) {
    console.log('delete '+obj+'...');
    var url = obj.links != undefined
        ? obj.links.filter(function(d) { console.log('this:'+d);if (d['rel']=='self') return d;})[0].href
        : obj._links.self.href;
    $.ajax({
        url: url,
        type: 'DELETE',
        success: completeHandler = function(data) {
          ractive.fetch();
        },
        error: errorHandler = function(jqXHR, textStatus, errorThrown) {
            ractive.handleError(jqXHR,textStatus,errorThrown);
        }
    });
  },
  fetch: function () {
    console.log('fetch...');
    ractive.set('saveObserver', false);
    $.getJSON('/'+ractive.get('tenant.id')+'/domain-model/?projection=complete',  function( data ) {
      console.log('loaded domain model...');
      ractive.set('domain', data);
//      ractive.merge('entities', data.entities);
      ractive.set('saveObserver',true);
      $('.entity.active').fadeIn();
    });
  },
  nextEntity: function() {
    console.log('nextEntity');
    $('.entity.active').fadeOut().removeClass('active');
    ractive.set('entityIdx', ractive.get('entityIdx')+1);
    $('#entity'+ractive.get('entityIdx')+'Sect').fadeIn().addClass('active');
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
  previousEntity: function() {
    console.log('previousEntity');
    $('.entity.active').fadeOut().removeClass('active');
    ractive.set('entityIdx', ractive.get('entityIdx')-1);
    $('#entity'+ractive.get('entityIdx')+'Sect').fadeIn().addClass('active');
  },
  save: function () {
    var domain = ractive.get('domain');
    console.log('save...'+JSON.stringify(domain)+' ...');
    $.ajax({
      url: ractive.get('tenant.id')+'/domain-model/',
      type: 'PUT',
      contentType: 'application/json',
      data: JSON.stringify(domain),
      success: completeHandler = function(data, textStatus, jqXHR) {
        console.log('data: '+ data);
        var location = jqXHR.getResponseHeader('Location');
        if (location != undefined) { 
          ractive.set('domain._links.self.href',location);
//            ractive.set('domain.id',location.substring());
        }
//          if (jqXHR.status == 201) ractive.get('deciosn').push(ractive.get('current'));
//          if (jqXHR.status == 204) ractive.splice('contacts',ractive.get('currentIdx'),1,ractive.get('current'));
        ractive.showMessage('Saved');
      },
      error: errorHandler = function(jqXHR, textStatus, errorThrown) {
        ractive.handleError(jqXHR,textStatus,errorThrown);
      }
    });
  },
  /**
   * Inverse of editField.
   */
  updateField: function (selector, path) {
    var tmp = $(selector).text();
    console.log('updateField '+path+' to '+tmp);
    ractive.set(path,tmp);
    $(selector).css('border-width','0px').css('padding','0px');
  }
});

