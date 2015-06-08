var CSRF_COOKIE = 'XSRF-TOKEN';
var EASING_DURATION = 500;
fadeOutMessages = true;
var newLineRegEx = /\n/g;


/**
 * Extends Ractive to handle authentication for RESTful clients connecting
 * to Spring servers.
 * 
 * Also offers some standard controls like Typeahead and a re-branding mechanism.
 *
 * Form names expected:
 *   loginForm
 *   logoutForm
 */
var AuthenticatedRactive = Ractive.extend({
  CSRF_TOKEN: 'XSRF-TOKEN',
  ajaxSetup: function() {
    console.log('ajaxSetup: '+this);
    $.ajaxSetup({
      username: localStorage['username'],
      password: localStorage['password'],
      headers: { 'X-CSRF-TOKEN': this.getCookie(CSRF_COOKIE) },
      error: this.handleError
    });
  },
  applyBranding: function() {
    if (ractive.get('profile')==undefined) return ;
    var tenant = ractive.get('profile').tenant;
    if (tenant != undefined) {
      $('link[rel="icon"]').attr('href',$('link[rel="icon"]').attr('href').replace('omny',tenant));
      //$('link[rel="stylesheet"]').attr('href',$('link[rel="stylesheet"]').attr('href').replace('omny',tenant));
      $('head').append('<link href="css/'+tenant+'-0.8.0.css" rel="stylesheet">');
      $('.navbar-brand').empty().append('<img src="/images/'+tenant+'-logo.png" alt="logo"/>');
      // ajax loader 
      $('body').append('<div id="ajax-loader"><img class="ajax-loader" src="images/'+tenant+'-ajax-loader.gif" alt="Loading..."/></div>');
      $( document ).ajaxStart(function() {
        $( "#ajax-loader" ).show();
      });
      $( document ).ajaxStop(function() {
        $( "#ajax-loader" ).hide();
      });
      // powered by 
      if (ractive.get('tenant.id')!='omny' && ractive.get('tenant.showPoweredBy')!=false) {
        $('body').append('<div class="powered-by"><h1><span class="powered-by-text">powered by</span><img src="images/omny-greyscale-inline-logo.png" alt="powered by Omny Link"/></h1></div><p class="beta bg-warning pull-right">Beta!</p>');
      }
      if (ractive.get('tenant.omny-bar')!=undefined) {
        // 'Omny Bar' 
        $('body').append('<div class="omny-bar"><ul></ul></div>');
        $.each(ractive.get('tenant.omny-bar'), function(i,d) {
          $('.omny-bar ul').append('<li><a href="'+d.url+'"><span class="'+d.classes+'" title="'+d.title+'"></span></a></li>');
        });
      }
      ractive.initContentEditable();// required here for the tennt switcher
      // tenant partial templates
      $.each(ractive.get('tenant').partials, function(i,d) {
        $.get(d.url, function(response){
          //console.log('response: '+response)
          ractive.resetPartial(d.name,response);
        });
      });
      if (ractive.brandingCallbacks!=undefined) ractive.brandingCallbacks.fire();
    }
  },
  getCookie: function(name) {
    //console.log('getCookie: '+name)
    var value = "; " + document.cookie;
    var parts = value.split("; " + name + "=");
    if (parts.length == 2) return parts.pop().split(";").shift();
  },
  getProfile: function() {
    console.log('getProfile: '+this.get('username'));
    var ractive = this;
    if (this && this.get('username')) $.getJSON('/users/'+ractive.get('username'), function(profile) {
      ractive.set('profile',profile);
      $('.profile-img').empty().append('<img class="img-rounded" src="http://www.gravatar.com/avatar/'+ractive.hash(ractive.get('profile.email'))+'?s=34"/>');
      if (ractive.hasRole('super_admin')) $('.super-admin').show();
      ractive.loadTenantConfig(ractive.get('profile.tenant'));
    })
    .error(function(){
      console.warn('Failed to get profile, will rely on Omny default');
      ractive.set('profile',{tenant:'omny'});
      ractive.loadTenantConfig(ractive.get('tenant.id'));
    });
    else this.showError('You are not logged in, some functionality will be unavailable.');
  },
  handleError: function(jqXHR, textStatus, errorThrown) {
    switch (jqXHR.status) { 
    case 401:
    case 403: 
    case 405: /* Could also be a bug but in production we'll assume a timeout */ 
      this.showError("Session expired, please login again");
      window.location.href='/login';
      break; 
    default: 
      var msg = "Bother! Something has gone wrong (code "+jqXHR.status+"): "+textStatus+':'+errorThrown;
      console.error('msg:'+msg);
      ractive.showError(msg);
    }
  },
  getServer: function() {
    return ractive.get('server')==undefined ? '' : ractive.get('server');
  },
  hash: function(email) {
    return hex_md5(email.trim().toLowerCase());
  },
  hasRole: function(role) {
    var ractive = this;
    if (this && this.get('profile')) {
      var hasRole = ractive.get('profile').groups.filter(function(g) {return g.id==role})
      return hasRole!=undefined && hasRole.length>0;
    }
    return false;
  },
  hideMessage: function() {
    $('#messages').hide();
  },
  initAutoComplete: function() {
    console.log('initAutoComplete');
    if (ractive.get('tenant.typeaheadControls')!=undefined && ractive.get('tenant.typeaheadControls').length>0) {
      $.each(ractive.get('tenant.typeaheadControls'), function(i,d) {
        console.log('binding ' +d.url+' to typeahead control: '+d.selector);
        $.get(ractive.getServer()+d.url, function(data){
          if (d.name!=undefined) ractive.set(d.name,data); 
          $(d.selector).typeahead({ minLength:0,source:data });
          $(d.selector).on("click", function (ev) {
            newEv = $.Event("keydown");
            newEv.keyCode = newEv.which = 40;
            $(ev.target).trigger(newEv);
            return true;
         });
        },'json');
      });
    }
  },
  initAutoNumeric: function() { 
    if ($('.autoNumeric')!=undefined && $('.autoNumeric').length>0) {
      $('.autoNumeric').autoNumeric('init', {});
    }
  },
  initContentEditable: function() {
    console.log('initContentEditable');
    $("[contenteditable]").focus(function() { 
      console.log('click '+this.id);
      selectElementContents(this);
    });
  },
  initControls: function() { 
    console.log('initControls');
    ractive.initAutoComplete();
    ractive.initAutoNumeric();
    ractive.initDatepicker();
    ractive.initContentEditable();
  },
  initDatepicker: function() {
    console.log('initDatepicker');
    if ($('.datepicker')!=undefined && $('.datepicker').length>0) {
      $('.datepicker').datepicker({
        format: "dd/mm/yyyy",
        autoclose: true,
        todayHighlight: true
      });
    }
  },
  loadStandardPartials: function(stdPartials) {
    $.each(stdPartials, function(i,d) {
      console.log('loading...: '+d.name)
      $.get(d.url, function(response){
        console.log('... loaded: '+d.name)
        //console.log('response: '+response)
        if (ractive != undefined) ractive.resetPartial(d.name,response);
      });
    });
  },
  loadTenantConfig: function(tenant) {
    console.log('loadTenantConfig:'+tenant);
    $.getJSON(ractive.getServer()+'/tenants/'+tenant+'.json', function(response) {
      console.log('... response: '+response);
      ractive.set('tenant', response);
      ractive.applyBranding();
      if (ractive.tenantCallbacks!=undefined) ractive.tenantCallbacks.fire(); 
    });
  },
  login: function() {
    console.log('login');
    if (!document.forms['loginForm'].checkValidity()) {
      // TODO message
      return false;
    }
    localStorage['username'] = $('#username').val();
    localStorage['password'] = $('#password').val();
    document.forms['loginForm'].submit();
  },
  logout: function() {
    localStorage['username'] = null;
    localStorage['password'] = null;
    document.cookie = this.CSRF_COOKIE+'=;expires=Thu, 01 Jan 1970 00:00:01 GMT;';
    document.forms['logoutForm'].submit();
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
    console.log('showMessage: '+msg);
    if (additionalClass == undefined) additionalClass = 'bg-info text-info';
    if (msg === undefined) msg = 'Working...';
    $('#messages').empty().append(msg).removeClass().addClass(additionalClass).show();
//    document.getElementById('messages').scrollIntoView();
    if (fadeOutMessages && additionalClass!='bg-danger text-danger') setTimeout(function() {
      $('#messages').fadeOut();
    }, EASING_DURATION*10);
    else $('#messages').append('<span class="text-danger pull-right glyphicon glyphicon-remove" onclick="ractive.hideMessage()"></span>');
  },
  switchToTenant: function(tenant) {
    if (tenant==undefined || typeof tenant != 'string') {
      return false;
    }
    console.log('switchToTenant: '+tenant);
    $.ajax({
      method: 'PUT',
      url: "/admin/tenant/"+ractive.get('username')+'/'+tenant,
      success: function() {
        window.location.reload();
      }
    })
  }
});

// TODO remove the redundancy of having this in AuthenticatedRactive and here
function getCookie(name) {
  //console.log('getCookie: '+name)
  var value = "; " + document.cookie;
  var parts = value.split("; " + name + "=");
  if (parts.length == 2) return parts.pop().split(";").shift();
}

function selectElementContents(el) {
  var range = document.createRange();
  range.selectNodeContents(el);
  var sel = window.getSelection();
  sel.removeAllRanges();
  sel.addRange(range);
}