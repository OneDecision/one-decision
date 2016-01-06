<!DOCTYPE html>

<html lang="en">
<head>
  <link href="/webjars/bootstrap/3.3.5/css/bootstrap.min.css" rel="stylesheet">
  <link href="/css/one-decision-1.1.0.css" rel="stylesheet">
  <link href="/css/decisions-1.1.0.css" rel="stylesheet">
  <link rel="icon" type="image/png" href="/images/one-decision-icon-16x16.png" />
<head>
<body>
  <div class="clearfix" id="messages"></div>
  <section class="container" id="container"></section>

  <script id='template' type='text/ractive'>
    {{>profileArea}}
    {{>titleArea}}
    {{>poweredBy}}
    {{>sidebar}}
  </script>

  <section class="container">
    <h2>Decision Model ${dmnModel.name} (id: ${dmnModel.shortId})</h2>
    <p></p>
    <section>${decisionHtml}</section>
  </section>
      
  <script src="/webjars/jquery/1.11.1/jquery.min.js"></script>
  <script src="/webjars/bootstrap/3.3.5/js/bootstrap.min.js"></script>
  <script src="/webjars/Bootstrap-3-Typeahead/3.1.1/bootstrap3-typeahead.js"></script>
  <script src="/webjars/ractive/0.7.3/ractive.min.js"></script>
  <script src="/js/one-decision-1.1.0.js"></script>
  <script src="/js/one-decision-login-1.1.0.js"></script>
</body>
</html>
