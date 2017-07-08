/*
 * Console support
 */
var console = { log: print };

/*
 * One Decision helper functions
 */
initObj = function(expr) {
  console.log('initObj '+expr);
  var parts = expr.split('.');
  var ref = '';
  for (idx in parts) {
    if (ref.length >0) ref+='.';
    ref+=(parts[idx]);
    console.log('Needs initialising? '+ref);
    try {
      console.log('... 10');
      var obj = eval(ref);
      console.log('... 20');
      if (obj == undefined && idx<(parts.length-1)) {
        console.log('Initialising '+ref);
        eval(ref+' = new Object();');
        console.log('... '+ref);
      } else if (obj == undefined){
        console.log('... 30');
        // How to init leaf? string? number?
      }
    } catch (e) {
      console.log('... exception...'+e);
      console.log('Initialising '+ref);
      eval(ref+' = new Object();');
      console.log('... '+ref);
    }
  }
};

/*
 *
 */
Number.prototype.inRange = function(expr) { 
  expr=expr.trim();
  var lowValue = expr.substring(1,expr.indexOf('..'));
  var highValue = expr.substring(expr.indexOf('..')+2, expr.length-1);

  if (expr[0]=='[' && expr[expr.length-1]==']') {
    return this >= lowValue && this <= highValue;
  } else if (expr[0]=='(' && expr[expr.length-1]==']') {
    return this > lowValue && this <= highValue;
  } else if (expr[0]=='[' && expr[expr.length-1]==')') {
    return this >= lowValue && this < highValue;
  } else if (expr[0]=='(' && expr[expr.length-1]==')') {
    return this > lowValue && this < highValue;
  }
}