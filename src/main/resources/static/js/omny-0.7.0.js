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

ractive.observe('username', function(newValue, oldValue, keypath) {
  ractive.getProfile();
});

$(document).ready(function() {
  ractive.set('saveObserver',false);
  
  if (ractive.tenantCallbacks==undefined) ractive.tenantCallbacks = $.Callbacks();
  ractive.tenantCallbacks.add(function() {
    ractive.fetch();
  });
  if (ractive.brandingCallbacks==undefined) ractive.brandingCallbacks = $.Callbacks();
  ractive.brandingCallbacks.add(function() {
    ractive.initControls();
  });
  
  var s = getSearchParameters()['s'];
  if (s!=undefined) ractive.set('searchTerm',s);

  var id = getSearchParameters()['id'];
  if (id!=undefined) {
    ractive.set('searchId',id);
    if (ractive.fetchCallbacks==undefined) ractive.fetchCallbacks = $.Callbacks();
    ractive.fetchCallbacks.add(function() {
      ractive.edit(ractive.find(ractive.get('searchId')));
    });
  }
  
  ractive.set('saveObserver', true);
});
