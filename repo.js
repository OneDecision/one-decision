<!doctype html>
<html lang='en'>
<head>
  <meta http-equiv="X-UA-Compatible" content="IE=edge">
  <meta charset='utf-8'>
  <title>Omny Link</title>
  <link href="/webjars/bootstrap/3.3.5/css/bootstrap.min.css" rel="stylesheet">
  <link href="css/omny-1.0.0.css" rel="stylesheet">
  <link rel="icon" type="image/png" href="images/icon/omny-icon-16x16.png" />
</head>
<body>
  <div class="clearfix" id="messages"></div>
  <div class="container" id="container"></div>
  <script id='template' type='text/ractive'>
 
    {{>profileArea}}
    {{>poweredBy}}
    {{>sidebar { active: 'index.html' }}}
    {{>titleArea}}

    <section>
      <p>&nbsp;</p>
      <h2>{{title2}}</h2>
      <p>{{{intro}}}</p>
      <ul style="list-style:none;">
        {{#tenant.toolbar}}
          {{#if matchRole(role) }}
            <li>
              <a href="{{context}}{{url}}">
                <span class="glyphicon {{role}} {{icon}}" style="display:inline-block" title="{{title}}"></span>
                <span>{{title}}</span>
              </a>
              <span>{{description}}</span>
            </li>
          {{/if}}
        {{/}}
      </ul>
    </section>

  </script>

  <script src="/webjars/jquery/1.11.1/jquery.min.js"></script>
  <script src="/webjars/bootstrap/3.3.5/js/bootstrap.min.js"></script>
  <script src="/webjars/Bootstrap-3-Typeahead/3.1.1/bootstrap3-typeahead.js"></script>
  <script src="/webjars/ractive/0.7.3/ractive.min.js"></script>

  <script src="js/autoNumeric.js"></script>
  <script src="js/md5.min.js"></script>
  <script src="js/activity-1.0.0.js"></script>
  <script src="js/login-1.0.0.js"></script>
  <script src="js/i18n.js"></script>
  <script src="js/index-1.0.0.js"></script>
  <script src="js/omny-1.0.0.js"></script>
  
</body>
</html>
