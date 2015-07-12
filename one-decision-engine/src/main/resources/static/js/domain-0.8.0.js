var ractive = new OneDecisionApp({
  el: 'container',
  lazy: true,
  template: '#template',
  // partial templates
  // partials: { },
  data: {
    csrfToken: getCookie(CSRF_COOKIE),
    //saveObserver:false,
    entityIdx:0,
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
    });
  },
  fetch: function () {
    console.log('fetch...');
    ractive.set('saveObserver', false);
    $.getJSON(ractive.getServer()+'/'+ractive.get('tenant.id')+'/domain-model/?projection=complete',  function( data ) {
      console.log('loaded domain model...');
      ractive.set('domain', data);
//      ractive.merge('entities', data.entities);
      ractive.set('saveObserver',true);
      if ($('.entity.active').length==0) $($('.entity')[0]).addClass('active');
      $('.entity.active').fadeIn();
    });
  },
  nextEntity: function() {
    console.log('nextEntity');
    $('.entity.active').fadeOut().removeClass('active');
    ractive.set('entityIdx', ractive.get('entityIdx')+1);
    $('#entity'+ractive.get('entityIdx')+'Sect').fadeIn().addClass('active');
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
      url: ractive.getServer()+'/'+ractive.get('tenant.id')+'/domain-model/',
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

$(document).ready(function() {
  console.info('Running ready handler');
  ractive.set('saveObserver',false);
  
  if (ractive.initCallbacks==undefined) ractive.initCallbacks = $.Callbacks();
  ractive.initCallbacks.add(function() {
    console.info('initialization handler');
    ractive.applyBranding();
    ractive.fetch();
    ractive.initControls();
  });
  
  var s = getSearchParameters()['s'];
  if (s!=undefined) ractive.set('searchTerm',s);

  var id = getSearchParameters()['id'];
  if (id!=undefined) {
    ractive.set('searchId',id);
    if (ractive.fetchCallbacks==undefined) ractive.fetchCallbacks = $.Callbacks();
    ractive.fetchCallbacks.add(function() {
      console.info('fetch handler');
      ractive.edit(ractive.find(ractive.get('searchId')));
    });
  }
  
  if (ractive.initCallbacks!=undefined) ractive.initCallbacks.fire();
  ractive.set('saveObserver', true);
});
