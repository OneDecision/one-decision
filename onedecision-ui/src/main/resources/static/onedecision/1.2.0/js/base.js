/*******************************************************************************
 * Copyright 2015, 2017 Tim Stephenson and contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
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
var BaseRactive = Ractive.extend({
  _autolinker: undefined,
  addDataList: function(d, data) {
    $('datalist#'+d.name).remove();
    $('body').append('<datalist id="'+d.name+'">');
    if (data == null) {
      console.error('No data for datalist: '+d.name+', please fix configuration');
    } else {
      $.each(data, function (i,e) {
        $('datalist#'+d.name).append('<option value="'+e.name+'">'+e.name+'</option>');
      });
    }
  },
  addDoc: function() {
    console.log('addDoc');
    if (ractive.uri(ractive.get('current'))==undefined) {
      var entityName = ractive.entityName(ractive.get('current')).singular();
      ractive.showMessage('You must have created your '+entityName+' before adding documents');
      return;
    }
    ractive.set('saveObserver', false);
    if (ractive.get('current.documents') == undefined) ractive.set('current.documents', []);
    var entityName = ractive.entityName(ractive.get('current')).singular();
    ractive.splice('current.documents', 0, 0, {
      author:$auth.getClaim('sub'), entityName: ractive.uri(ractive.get('current')), content: '', favorite: true
    });
    ractive.set('saveObserver', true);
    if ($('#docsTable:visible').length==0) ractive.toggleSection($('#docsTable').closest('section'));
    $('#docsTable tr:nth-child(1)').slideDown();
  },
  addNote: function() {
    console.log('addNote');
    ractive.set('saveObserver', false);
    if (ractive.uri(ractive.get('current'))==undefined) {
      var entityName = ractive.entityName(ractive.get('current')).singular();
      ractive.showMessage('You must have created your '+entityName+' before adding notes');
      return;
    }
    if (ractive.get('current.notes') == undefined) ractive.set('current.notes', []);
    var entityName = ractive.entityName(ractive.get('current')).singular();
    ractive.splice('current.notes', 0, 0, {
      author:$auth.getClaim('sub'), entityName: ractive.uri(ractive.get('current')), content: '', favorite: true
    });
    ractive.set('saveObserver', true);
    if ($('#notesTable:visible').length==0) ractive.toggleSection($('#notesTable').closest('section'));
    $('#notesTable tr:nth-child(1)').slideDown();
  },
  analyzeEmailActivity: function(activities) {
    if (activities.length > 0) {
      var count = 0;
      for (idx in activities) {
        if (activities[idx].type == 'email') {
          count++;
          if (idx == 0) ractive.set('current.timeSinceEmail',(new Date().getTime() - Date.parse(activities[idx].occurred)));
        }
      }
      ractive.set('current.emailsSent',count);
    }
  },
  applyAccessControl: function() {
    console.info('applyAccessControl');
    if (location.href.indexOf('public')==-1 && ractive.get('tenant.access.readonly')) {
      fadeOutMessages=false;
      ractive.showMessage('System is in read-only mode', 'alert-warning');
      $('input,select,textarea').attr('readonly','readonly').attr('disabled','disabled');
    }
  },
  applyBranding: function() {
    if (ractive.get('tenant')==undefined) return ;
    var tenant = ractive.get('tenant.id');
    if (tenant != undefined) {
      $('head').append('<link href="'+ractive.getServer()+'/css/'+tenant+'-1.0.0.css" rel="stylesheet">');
      if (ractive.get('tenant.theme.logoUrl')!=undefined) {
        $('.navbar-brand').empty().append('<img src="'+ractive.get('tenant.theme.logoUrl')+'" alt="logo"/>');
      }
      if (ractive.get('tenant.theme.iconUrl')!=undefined) {
          $('link[rel="icon"]').attr('href',ractive.get('tenant.theme.iconUrl'));
      }
      // ajax loader
      if ($('#ajax-loader').length==0) $('body').append('<div id="ajax-loader"><img class="ajax-loader" src="'+ractive.getServer()+'/images/one-decision-ajax-loader.gif" style="width:10%" alt="Loading..."/></div>');
      $( document ).ajaxStart(function() {
        $( "#ajax-loader" ).show();
      });
      $( document ).ajaxStop(function() {
        $( "#ajax-loader" ).hide();
      });
      ractive.initContentEditable();// required here for the tenant switcher
      // tenant partial templates
      $.each(ractive.get('tenant').partials, function(i,d) {
        $.get(d.url, function(response){
//          console.log('partial '+d.url+' response: '+response);
          try {
          ractive.resetPartial(d.name,response);
          } catch (e) {
            console.error('Unable to reset partial '+d.name+': '+e);
          }
        });
      });
      ractive.applyAccessControl();
      if (ractive.brandingCallbacks!=undefined) ractive.brandingCallbacks.fire();
    }
  },
  autolinker: function() {
    if (ractive._autolinker==undefined) ractive._autolinker = new Autolinker({
          email: true,
          hashtag: 'twitter',
          mention: 'twitter',
          newWindow : true,
          stripPrefix: {
            scheme : true,
            www    : true
          },
          truncate  : 30
      });
    return ractive._autolinker;
  },
  cancelDoc: function() {
    console.info('cancelDoc');
    ractive.get('current.documents').splice(0, 1);
  },
  cancelNote: function() {
    console.info('cancelNote');
    ractive.get('current.notes').splice(0, 1);
  },
  entityName: function(entity) {
    console.info('entityName');
    var id = ractive.uri(entity);
    var lastSlash = id.lastIndexOf('/');
    return id.substring(id.lastIndexOf('/', lastSlash-1)+1, lastSlash);
  },
  fetchConfig: function() {
    console.info('fetchConfig');
    $.getJSON('/configuration', function(data) {
      ractive.set('server',data.clientContext);
    });
  },
  fetchDocs: function() {
    $.getJSON(ractive.uri(ractive.get('current'))+'/documents',  function( data ) {
      if (data['_embedded'] != undefined) {
        console.log('found docs '+data);
        ractive.merge('current.documents', data['_embedded'].documents);
        // sort most recent first
        ractive.get('current.documents').sort(function(a,b) { return new Date(b.created)-new Date(a.created); });
      }
      ractive.set('saveObserver',true);
    });
  },
//  fetchNotes: function() {
//    $.getJSON(ractive.uri(ractive.get('current'))+'/notes',  function( data ) {
//      if (data['_embedded'] != undefined) {
//        console.log('found notes '+data);
//        ractive.merge('current.notes', data['_embedded'].notes);
////      } else if (data['content'] != undefined) {
////        console.log('found notes '+data);
////        ractive.merge('current.notes', data['content']);
//      }
//      // sort most recent first
//      if (ractive.get('current.notes') != undefined) {
//        ractive.get('current.notes').sort(function(a,b) { return new Date(b.created)-new Date(a.created); });
//      }
//    });
//  },
  fetchStockCategories: function() {
    if (ractive.get('tenant.features.stockCategory')!=true) return;
    console.info('fetchCategories...');
    ractive.set('saveObserver', false);
    $.ajax({
      dataType : "json",
      url : ractive.getServer() + '/' + ractive.get('tenant.id') + '/stock-categories/',
      crossDomain : true,
      success : function(data) {
        if (data['_embedded'] != undefined) {
          data = data['_embedded'].stockCategories;
        }
        ractive.set('stockCategories', data);
        console.log('fetched ' + data.length + ' stock categories');
        // HTML5 style only
        $('datalist#stockCategories').remove();
        $('body').append('<datalist id="stockCategories">');
        $.each(ractive.get('stockCategories'), function (i,d) {
          $('datalist#stockCategories').append('<option value="'+d.name+'">'+d.name+'</option>');
        });

        ractive.set('saveObserver', true);
      }
    });
  },
  fetchStockItems: function() {
    console.info('fetchStockItems...');
    ractive.set('saveObserver', false);
    $.ajax({
      dataType : "json",
      url : ractive.getServer() + '/' + ractive.get('tenant.id') + '/stock-items/',
      crossDomain : true,
      success : function(data) {
        if (data['_embedded'] != undefined) {
          data = data['_embedded'].stockItems;
        }
        ractive.set('stockItems', data);
        console.log('fetched ' + data.length + ' stock items');
        var stockItemData = jQuery.map(data, function(n, i) {
          return ({
            "id": ractive.id(n),
            "name": n.name
          });
        });
        ractive.set('stockItemsTypeahead', stockItemData);
        if (ractive['initStockItemTypeahead']!= undefined) ractive.initStockItemTypeahead();

        // HTML5 style
        $('datalist#stockItems').remove();
        $('body').append('<datalist id="stockItems">');
        $.each(ractive.get('stockItems'), function (i,d) {
          $('datalist#stockItems').append('<option value="'+d.name+'">'+d.name+'</option>');
        });

        ractive.set('saveObserver', true);
      }
    });
  },
  getCookie: function(name) {
    //console.log('getCookie: '+name)
    var value = "; " + document.cookie;
    var parts = value.split("; " + name + "=");
    if (parts.length == 2) return parts.pop().split(";").shift();
  },
  /*getProfile: function() {
    console.log('getProfile: '+this.get('username'));
    $auth.getProfile(this.get('username'));
  },*/
  /*handleError: function(jqXHR, textStatus, errorThrown) {
    switch (jqXHR.status) {
    case 400:
      var msg = jqXHR.responseJSON == null ? textStatus+': '+errorThrown : errorThrown+': '+jqXHR.responseJSON.message;
      ractive.showError(msg);
      break;
    case 401:
    case 403:
    case 405: / * Could also be a bug but in production we'll assume a timeout * /
      ractive.showError("Session expired, please login again");
      ractive.logout();
      break;
    case 404:
      var path ='';
      if (jqXHR.responseJSON != undefined) {
        path = " '"+jqXHR.responseJSON.path+"'";
      }
      var msg = "That's odd, we can't find the page"+path+". Please let us know about this message";
      console.error('msg:'+msg);
      ractive.showError(msg);
      break;
    default:
      var msg = "Bother! Something has gone wrong (code "+jqXHR.status+"): "+textStatus+':'+errorThrown;
      console.error('msg:'+msg);
      $( "#ajax-loader" ).hide();
      ractive.showError(msg);
    }
  },*/
  getServer: function() {
    return ractive.get('server')==undefined ? '' : ractive.get('server');
  },
  hash: function(email) {
    if (email==undefined) return email;
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
  hideLogin: function() {
    $('#loginSect').slideUp();
  },
  hideMessage: function() {
    $('#messages, .messages').hide();
  },
  hideUpload: function () {
    console.log('hideUpload...');
    $('#upload').slideUp();
  },
  id: function(entity) {
    console.log('id: '+entity);
    var id = ractive.uri(entity);
    return id.substring(id.lastIndexOf('/')+1);
  },
  initAutoComplete: function() {
    console.log('initAutoComplete');
    if (ractive.get('tenant.typeaheadControls')!=undefined && ractive.get('tenant.typeaheadControls').length>0) {
      $.each(ractive.get('tenant.typeaheadControls'), function(i,d) {
        //console.log('binding ' +d.url+' to typeahead control: '+d.selector);
        if (d.url==undefined) {
          if ($(d.selector+'.typeahead').length>0 && typeof $(d.selector+'.typeahead').typeahead == 'function') ractive.initAutoCompletePart2(d,d.values);
          ractive.addDataList(d,d.values);
        } else {
          $.getJSON(ractive.getServer()+d.url, function(data){
            if ($(d.selector+'.typeahead').length>0 && typeof $(d.selector+'.typeahead').typeahead == 'function') ractive.initAutoCompletePart2(d,data);
            if (data == null || !Array.isArray(data)) {
              console.info('No values for datalist: '+d.name+', probably refreshing token');
            } else {
              d.values = data;
              ractive.addDataList(d,d.values);
            }
          })
          .fail(function(jqXHR, textStatus, errorThrown) {
            if (jqXHR.status == 401) console.info('No values for datalist: '+d.name+', need to refresh token or login again');
            else console.error('No values for datalist: '+d.name+', please check configuration');
          });
        }
      });
    }
  },
  initAutoCompletePart2: function(d, data) {
    if (d.name!=undefined) ractive.set(d.name,data);
    $(d.selector).typeahead({ items:'all',minLength:0,source:data });
    $(d.selector).on("click", function (ev) {
      newEv = $.Event("keydown");
      newEv.keyCode = newEv.which = 40;
      $(ev.target).trigger(newEv);
      return true;
    });
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
    ractive.initHelp();
  },
  initDatepicker: function() {
    console.log('initDatepicker');
    if ($('.datepicker')!=undefined && $('.datepicker').length>0) {
      $('.datepicker').datepicker({
        format: "dd/mm/yyyy",
        autoclose: trinitialAccountStage: function(idx) {
          console.log('initialAccountStage: '+idx);
          var rtn = '';
          $.each(ractive.get('accountStages'), function(i,d) {
            if (parseInt(d['idx'])==idx) rtn = d.name;
          });
          return rtn;
        },
        initialContactStage: function(idx) {
          console.log('initialContactStage: '+idx);
          var rtn = '';
          $.each(ractive.get('contactStages'), function(i,d) {
            if (parseInt(d['idx'])==idx) rtn = d.name;
          });
          return rtn;
        },
        initialOrderStage: function() {
          console.log('initialOrderStage: '+idx);
          var rtn = '';
          $.each(ractive.get('orderStages'), function(i,d) {
            if (parseInt(d['idx'])==idx) rtn = d.name;
          });
          return rtn;
        },ue,
        todayHighlight: true
      });
    }
  },
  initHelp: function() {
    $( "body" ).keypress(function( event ) {
      if (event.target.tagName.toLowerCase() == 'input' || event.target.tagName.toLowerCase() == 'textarea') return;
      switch (event.which) { // ref http://keycode.info/
      case 47: // forward slash on both Mac and Linux
      case 191: // forward slash (allegedly)
          $('.search').focus();
          event.preventDefault();
          break;
      case 63: // ?
         $('#helpModal').modal({});
         event.preventDefault();
         break;
      default:
        //console.log("No Handler for .keypress() called with: "+event.which);
      }
    });
  },
  initTags: function() {
    console.info('initTags');
    $('[data-bind]').each(function(i,d) {
      $(d).val(ractive.get($(d).data('bind'))).css('display','none');
    });

    if ($(".tag-ctrl").is(":ui-tagit")) $(".tag-ctrl").tagit('destroy');
    $(".tag-ctrl").tagit({
      placeholderText: "Comma separated tags",
      afterTagAdded: function(event, ui) {
        ractive.set($(event.target).data('bind'),$(event.target).val());
      },
      afterTagRemoved: function(event, ui) {
        ractive.set($(event.target).data('bind'),$(event.target).val());
      }
    });
  },
  initialAccountStage: function(idx) {
    console.log('initialAccountStage: '+idx);
    var rtn = '';
    $.each(ractive.get('accountStages'), function(i,d) {
      if (parseInt(d['idx'])==idx) rtn = d.name;
    });
    return rtn;
  },
  initialContactStage: function(idx) {
    console.log('initialContactStage: '+idx);
    var rtn = '';
    $.each(ractive.get('contactStages'), function(i,d) {
      if (parseInt(d['idx'])==idx) rtn = d.name;
    });
    return rtn;
  },
  initialOrderStage: function() {
    console.log('initialOrderStage: '+idx);
    var rtn = '';
    $.each(ractive.get('orderStages'), function(i,d) {
      if (parseInt(d['idx'])==idx) rtn = d.name;
    });
    return rtn;
  },
  loadStandardPartial: function(name,url) {
    //console.log('loading...: '+d.name)
      $.get(url, function(response) {
        //console.log('... loaded: '+d.name)
        //console.log('response: '+response)
        if (ractive != undefined) {
          try {
            ractive.resetPartial(name,response);
          } catch (e) {
            console.warn('Unable to reset partial '+name+': '+e);
          }
        }
      });
    },
  loadStandardPartials: function(stdPartials) {
    console.info('loadStandardPartials');
    if (ractive != undefined) {
      $.each(stdPartials, function(i,d) {
        ractive.loadStandardPartial(d.name, ractive.getServer()+'/'+d.url);
      });
    }
  },
  parseDate: function(timeString) {
    var d = new Date(timeString);
    // IE strikes again
    if (d == 'Invalid Date') d = parseDateIEPolyFill(timeString);
    return d;
  },
  rewrite: function(id) {
    console.info('rewrite:'+id);
    if (ractive.get('server')!=undefined && ractive.get('server')!='' && id.indexOf(ractive.get('server'))==-1) {
      //console.error('  rewrite is necessary');
      if (id.indexOf('://')==-1) {
        return ractive.getServer()+id;
      } else {
        return ractive.getServer()+id.substring(id.indexOf('/', id.indexOf('://')+4));
      }
    } else {
      return id;
    }
  },
  saveDoc: function() {
    console.log('saveDoc');
    if (ractive.get('current.documents')==undefined || ractive.get('current.documents').length==0) return;
    var n = ractive.get('current.documents.0');
//    n.name = $('#docName').val();
//    n.url = $('#doc').val();
    var url = ractive.uri(ractive.get('current'))+'/documents';
    url = url.replace(ractive.entityName(ractive.get('current')),ractive.get('tenant.id')+'/'+ractive.entityName(ractive.get('current')));
    if (n.url != undefined && n.url.trim().length > 0) {
      $.ajax({
        url: url,
        type: 'POST',
        data: n,
        success: completeHandler = function(data) {
          console.log('response: '+ data);
          ractive.showMessage('Document link saved successfully');
          ractive.set('saveObserver',false);
          ractive.set('current.documents.0.created',data.created);
          ractive.set('saveObserver',true);
          $('#doc,#docName').val(undefined);
        }
      });
    }
  },
  saveNote: function(n) {
    console.info('saveNote '+JSON.stringify(n)+' ...');
    /// TODO this is temporary for backwards compatibility with older workflow forms
    if (n == undefined) {
      n = ractive.get('current.notes.0');
      n.content = $('#note').val();
    }
    n.contact = ractive.uri(ractive.get('current'));
    var url = ractive.uri(ractive.get('current'))+'/notes';
    url = url.replace(ractive.entityName(ractive.get('current')),ractive.get('tenant.id')+'/'+ractive.entityName(ractive.get('current')));
    console.log('  url:'+url);
    if (n.content != undefined && n.content.trim().length > 0) {
      $.ajax({
        url: url,
        type: 'POST',
        data: n,
        success: completeHandler = function(data) {
          console.log('response: '+ data);
          ractive.showMessage('Note saved successfully');
          ractive.set('saveObserver',false);
          ractive.set('current.notes.0.created',data.created);
          ractive.set('saveObserver',true);
          $('#note').val(undefined);
        }
      });
    }
  },
  shortId: function(uri) {
    return uri.substring(uri.lastIndexOf('/')+1);
  },
  showDisconnected: function(msg) {
    console.log('showDisconnected: '+msg);
    if ($('#connectivityMessages.alert-info').length>0) {
      ; // Due to ordering of methods, actually reconnected now
    } else {
      $('#connectivityMessages').remove();
      $('body').append('<div id="connectivityMessages" class="alert-warning">'+msg+'</div>').show();
    }
  },
  showError: function(msg) {
    this.showMessage(msg, 'alert-danger');
  },
  showFormError: function(formId, msg) {
    this.showError(msg);
    var selector = formId==undefined || formId=='' ? ':invalid' : '#'+formId+' :invalid';
    $(selector).addClass('field-error');
    $(selector)[0].focus();
  },
  showHelp: function() {
    console.info('showHelp');
    $('iframe.helpContent')
        .attr('src',ractive.get('helpUrl'))
        .prop('height', window.innerHeight*0.8);
    $('#helpModal').modal({});
  },
  showLogin: function() {
    console.info('showLogin');
    $('#loginSect').slideDown();
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
    else $('#messages, .messages').append('<span class="text-danger pull-right glyphicon glyphicon-remove" onclick="ractive.hideMessage()"></span>');
  },
  showReconnected: function() {
    console.log('showReconnected');
    $( "#ajax-loader" ).hide();
    if ($('#connectivityMessages:visible').length>0) {
      $('#connectivityMessages').remove();
      $('body').append('<div id="connectivityMessages" class="alert-info">Reconnected</div>').show();
      setTimeout(function() {
        $('#connectivityMessages').fadeOut();
      }, EASING_DURATION*10);
    }
  },
  showUpload: function () {
    console.log('showUpload...');
    $('#upload').slideDown();
  },
  showSocial: function(networkName, keypath) {
    console.log('showSocial: '+networkName);
    ractive.set('network', { name: networkName, keypath: keypath, value: ractive.get(keypath) });
    $('#socialModalSect').modal('show');
  },
  submitSocial: function(network) {
    console.log('submitSocial: '+network);
    ractive.set(network.keypath,ractive.get('network.value'));
    $('#socialModalSect').modal('hide');
  },
  sortChildren: function(childArray, sortBy, asc) {
    console.info('sortChildren');
    if (ractive.get('current.'+childArray)==undefined) return 0;
    ractive.get('current.'+childArray).sort(function(a,b) {
      if (a[sortBy] > b[sortBy]) {
        return asc ? 1 : -1;
      }
      if (a[sortBy] < b[sortBy]) {
        return asc ? -1 : 1;
      }
      // a must be equal to b
      return 0;
    });
  },
  startCustomAction: function(key, label, object, form, businessKey) {
    console.log('startCustomAction: '+key+(object == undefined ? '' : ' for '+object.id));
    var instanceToStart = ractive.get('instanceToStart');
    if (instanceToStart==undefined) instanceToStart = {processVariables:{}};
    instanceToStart.processDefinitionKey = key;
    instanceToStart.businessKey = businessKey == undefined ? label : businessKey;
    instanceToStart.label = label;
    instanceToStart.processVariables.initiator = $auth.getClaim('sub');
    instanceToStart.processVariables.tenantId = ractive.get('tenant.id');

    if (object != undefined) {
      var singularEntityName = ractive.entityName(object).toCamelCase().singular();
      instanceToStart.processVariables[singularEntityName+'Id'] = ractive.uri(object);
      instanceToStart.processVariables[singularEntityName+'ShortId'] = ractive.shortId(ractive.uri(object));
    }
    console.log(JSON.stringify(instanceToStart));
    // save what we know so far...
    ractive.set('instanceToStart',instanceToStart);
    ractive.initAutoComplete();
    if (form == undefined || !form) {
      // ... and submit
      ractive.submitCustomAction();
    } else {
      // ... or display form, override submit handler with $('#submitCustomActionForm').off('click').on('click',function)
      $('#submitCustomActionForm').on('click', ractive.submitCustomAction);
      $('#customActionModalSect').modal('show');
    }
  },
  submitCustomAction: function() {
    console.info('submitCustomAction');
    if (document.getElementById('customActionForm').checkValidity()) {
      $.ajax({
        url: ractive.getServer()+'/'+ractive.get('tenant.id')+'/process-instances/',
        type: 'POST',
        contentType: 'application/json',
        data: JSON.stringify(ractive.get('instanceToStart')),
        success: function(data, textStatus, jqXHR) {
          console.log('response: '+ jqXHR.status+", Location: "+jqXHR.getResponseHeader('Location'));
          ractive.showMessage('Started workflow "'+ractive.get('instanceToStart.label')+'" for '+ractive.get('instanceToStart.businessKey'));
          $('#customActionModalSect').modal('hide');
          if (document.location.href.endsWith('contacts.html')) {
            ractive.select(ractive.get('current'));// refresh individual record
          } else {
            ractive.fetch(); // refresh list
          }
          if (ractive.customActionCallbacks!=undefined) ractive.customActionCallbacks.fire(jqXHR.getResponseHeader('Location'));
          // cleanup ready for next time
          $('#submitCustomActionForm').off('click');
        },
      });
    } else {
      ractive.showFormError('customActionForm','Please correct the highlighted fields');
    }
  },
  stripProjection: function(link) {
    if (link==undefined) return;
    var idx = link.indexOf('{projection');
    if (idx==-1) {
      idx = link.indexOf('{?projection');
      if (idx==-1) {
        return link;
      } else {
        return link.substring(0,idx);
      }
    } else {
      return link.substring(0,idx);
    }
  },
  switchToTenant: function(tenant) {
    if (tenant==undefined || typeof tenant != 'string') {
      return false;
    }
    console.log('switchToTenant: '+tenant);
    $.ajax({
      method: 'PUT',
      url: ractive.getServer()+"/admin/tenant/"+$auth.getClaim('sub')+'/'+tenant,
      success: function() {
        window.location.reload();
      }
    })
  },
  tenantUri: function(entity, entityPath) {
    //console.log('tenantUri: '+entity);
    var uri = ractive.uri(entity);
    if (entityPath===undefined) entityPath = ractive.get('entityPath');
    if (uri != undefined && uri.indexOf(ractive.get('tenant.id')+'/')==-1) {
      uri = uri.replace(entityPath,'/'+ractive.get('tenant.id')+entityPath);
    }
    return uri;
  },
  toggleSection: function(sect) {
    console.info('toggleSection: '+$(sect).attr('id'));
    $('#'+$(sect).attr('id')+'>div').toggle();
    $('#'+$(sect).attr('id')+' .ol-collapse').toggleClass('glyphicon-triangle-right').toggleClass('glyphicon-triangle-bottom');
  },
  toggleSidebar: function() {
    console.info('toggleSidebar');
    $('.omny-bar-left').toggle(EASING_DURATION);
  },
  upload: function(formId) {
    console.log('upload, id: '+formId);
    var formElement = document.getElementById(formId);
    var formData = new FormData(formElement);
    return $.ajax({
        type: 'POST',
        url: formElement.action,
        data: formData,
        cache: false,
        contentType: false,
        processData: false,
        success: function(response) {
          console.log('successfully uploaded resource');
          ractive.fetch();
          ractive.hideUpload();
        }
    });
  },
  uri: function(entity) {
    // TODO switch to use modularized version
    //console.log('uri: '+entity);
    var saveObserver = ractive.get('saveObserver');
    ractive.set('saveObserver', false);
    var uri;
    if (entity['links']!=undefined) {
      $.each(entity.links, function(i,d) {
        if (d.rel == 'self') {
          uri = d.href;
        }
      });
    } else if (entity['_links']!=undefined) {
      uri = ractive.stripProjection(entity._links.self.href);
    } else if (entity['id']!=undefined) {
      uri = ractive.get('entityPath')+'/'+entity.id;
    }
    // work around for sub-dir running
    if (uri != undefined && uri.indexOf(ractive.getServer())==-1 && uri.indexOf('//')!=-1) {
      uri = ractive.getServer() + uri.substring(uri.indexOf('/', uri.indexOf('//')+2));
    } else if (uri != undefined && uri.indexOf('//')==-1) {
      uri = ractive.getServer()+uri;
    }

    ractive.set('saveObserver', saveObserver);
    return uri;
  }
});

$(document).ready(function() {
  ractive.loadStandardPartials(ractive.get('stdPartials'));
  
  $( document ).ajaxComplete(function( event, jqXHR, ajaxOptions ) {
    if (jqXHR.status > 0) ractive.showReconnected();
  });

  ractive.observe('tenant', function(newValue, oldValue, keypath) {
    console.log('tenant changed');
    if ((oldValue == undefined || oldValue.id == '') && newValue != undefined && newValue.id != '' && ractive['fetch'] != undefined) {
      ractive.fetch();
    }
  });
  
  ractive.on( 'sort', function ( event, column ) {
    console.info('sort on '+column);
    // if already sorted by this column reverse order
    if (this.get('sortColumn')==column) this.set('sortAsc', !this.get('sortAsc'));
    this.set( 'sortColumn', column );
  });

  ractive.observe('title', function(newValue, oldValue, keypath) {
    console.log('title changing from '+oldValue+' to '+newValue);
    if (newValue!=undefined && newValue!='') {
      $('title').empty().append(newValue);
    }
  });

  ractive.observe('searchTerm', function(newValue, oldValue, keypath) {
    console.log('searchTerm changed');
    if (typeof ractive['showResults'] == 'function') ractive.showResults();
    setTimeout(ractive.showSearchMatched, 1000);
  });

  var params = getSearchParameters();
  if (params['searchTerm']!=undefined) {
    ractive.set('searchTerm',decodeURIComponent(params['searchTerm']));
  } else if (params['q']!=undefined) {
    ractive.set('searchTerm',decodeURIComponent(params['q']));
  }
  window.i18n = new I18nController($env.server+'/onedecision/1.2.0');
});

function selectElementContents(el) {
  var range = document.createRange();
  range.selectNodeContents(el);
  var sel = window.getSelection();
  sel.removeAllRanges();
  sel.addRange(range);
}

function getSearchParameters() {
  var prmstr = window.location.search.substr(1);
  return prmstr != null && prmstr != "" ? transformToAssocArray(prmstr) : {};
}

function transformToAssocArray( prmstr ) {
  var params = {};
  var prmarr = prmstr.split("&");
  for ( var i = 0; i < prmarr.length; i++) {
      var tmparr = prmarr[i].split("=");
      params[tmparr[0]] = tmparr[1];
  }
  return params;
}

/* Object extensions */

Array.prototype.clean = function(deleteValue) {
  for (var i = 0; i < this.length; i++) {
    if (this[i] == deleteValue) {
      this.splice(i, 1);
      i--;
    }
  }
  return this;
};

/**
 * @return The first array element whose 'k' field equals 'v'.
 */
Array.findBy = function(k,v,arr) {
  for (idx in arr) {
    if (arr[idx][k]==v) return arr[idx];
    else if ('selfRef'==k && arr[idx][k] != undefined && arr[idx][k].endsWith(v)) return arr[idx];
  }
}
/**
 * @return All  array elements whose 'k' field equals 'v'.
 */
Array.findAll = function(k,v,arr) {
  var retArr = [];
  for (idx in arr) {
    if (arr[idx][k]==v) retArr.push(arr[idx]);
    else if ('selfRef'==k && arr[idx][k] != undefined && arr[idx][k].endsWith(v)) return retArr.push(arr[idx]);
  }
  return retArr;
}
Array.uniq = function(fieldName, arr) {
  // console.info('uniq');
  list = '';
  for (idx in arr) {
    if (index(arr[idx],fieldName) != undefined
        && list.indexOf(index(arr[idx],fieldName)) == -1) {
      if (list != '')
        list += ','
      list += index(arr[idx],fieldName);
    }
  }
  return list;
}

function index(obj, keypath, value) {
  if (typeof keypath == 'string')
      return index(obj,keypath.split('.'), value);
  else if (keypath.length==1 && value!==undefined)
      return obj[keypath[0]] = value;
  else if (keypath.length==0)
      return obj;
  else
      return index(obj[keypath[0]],keypath.slice(1), value);
}

/******************************** Polyfills **********************************/
// ref https://developer.mozilla.org/en/docs/Web/JavaScript/Reference/Global_Objects/String/endsWith
if (!String.prototype.endsWith) {
  String.prototype.endsWith = function(searchString, position) {
      var subjectString = this.toString();
      if (typeof position !== 'number' || !isFinite(position) || Math.floor(position) !== position || position > subjectString.length) {
        position = subjectString.length;
      }
      position -= searchString.length;
      var lastIndex = subjectString.indexOf(searchString, position);
      return lastIndex !== -1 && lastIndex === position;
  };
}

function parseDateIEPolyFill(timeString) {
  var start = timeString.substring(0,timeString.indexOf('.'));
  var offset;
  if (timeString.indexOf('-',timeString.indexOf('T'))!=-1) {
    offset = timeString.substr(timeString.indexOf('-',timeString.indexOf('T')),3)+':'+timeString.substr(timeString.indexOf('-',timeString.indexOf('T'))+3,2);
  } else if (timeString.indexOf('+')!=-1) {
    offset = timeString.substr(timeString.indexOf('+'),3)+':'+timeString.substr(timeString.indexOf('+')+3,2);
  }
  return new Date(Date.parse(start+offset));
}
