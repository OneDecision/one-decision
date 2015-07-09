var ractive = new OneDecisionApp({
  el: 'container',
  
  // If two-way data binding is enabled, whether to only update data based on 
  // text inputs on change and blur events, rather than any event (such as key
  // events) that may result in new data
  lazy: true,
  
  template: '#template',

  // Initialize some data
  data: {
    csrfToken: getCookie(CSRF_COOKIE),
    contacts: [],
    //saveObserver:false,
    username: localStorage['username'],
    age: function(timeString) {
      return i18n.getAgeString(new Date(timeString))
    },
    formatDate: function(timeString) {
      return new Date(timeString).toLocaleDateString(navigator.languages);
    }
  }
});
