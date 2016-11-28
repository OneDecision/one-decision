<!DOCTYPE html>

<html lang="en">
<head>
  <link href='//fonts.googleapis.com/css?family=Roboto:400italic,400,700' rel='stylesheet' type='text/css'>
  <link href="/webjars/bootstrap/3.3.5/css/bootstrap.min.css" rel="stylesheet">
  <link href="/webjars/bootstrap/3.3.5/css/bootstrap-theme.min.css" rel="stylesheet">
  <link href="css/omny-1.0.0.css" rel="stylesheet">
  <link rel="icon" type="image/png" href="images/icon/omny-icon-16x16.png" />
    <style>
    html,body,section#main {
      height:100%;
      text-align: center;
      width: 100%;
    }
    a,a:visited {
      color: #0e9acd;
    }
    a:hover,a:active {
      color: #ff6c06;
    }
    input {
      border-style: solid;
      border-width: 1px;
      line-height: 2.5em;
    }
    section#main {
      margin: auto;
    }
    #container {
      position: fixed;
      top: 5px;
      right: 20px;
    }
    #container div {
      position: relative;
      margin-top: 5px;
      }
      #container form {
      text-align: right;
    }
    #container #messages {
      margin: 0 auto;
      width: 100%;
      /*width: 531px;*/
      top: -5px;
    }
    .btn { 
      height: 45px;
    }
    
    @media (max-width: 480px) {
      section#main {
        padding-top: 45%;
      }
    }
    @media (min-width: 481px) {
      section#main {
        padding-top: 30%;
      }
    }
    @media (min-width: 769px) {
      section#main {
        padding-top: 10%;
      }
    }
  </style>
<head>
<body>

  <section id="container" style="width:100%"></section>

  <section id="main">
    <h1>CRM | Workflow | Decisions</h1>
    <img src="images/omny-logo.png"/>
    <p>For more information see <a href="//omny.link">http://omny.link</a></p>
  </section>

  <script id='template' type='text/ractive'>
    <div class="col-md-offset-1 col-md-10">
      <div id="messages" style="display:none;width:100%"></div>
    </div>
    <div class="col-md-offset-6 col-md-6 col-sm-12" id="loginSect">
      <form class="form-inline" id="loginForm" name="loginForm" action="/login" method="POST">
        <fieldset>
          <input class="form-control input-lg" type="text" id="username" name="username" placeholder="Username" required/>
          <input class="form-control input-lg" type="password" id="password" name="password" placeholder="Password" required/>
          <input type="hidden" id="_csrf" name="_csrf" value="{{csrfToken}}" />
          <input type="hidden" id="redirect" name="redirect" value="index.html" />
          <input class="btn btn-primary" type="button" id="login" onclick="ractive.login()" value="Login"/>
          <br/>
<label for="rememberMe">Remember me</label>
<input class="form-control" type="checkbox" id="rememberMe" name="rememberMe"/>
        </fieldset>
      </form>
    </div>
    <div id="resetSect" class="col-md-offset-6 col-md-6 col-sm-12" style="display:none">
      <form id="resetForm" class="form-inline" name="resetForm" action="/reset" method="POST">
        <fieldset>
          <input class="form-control input-lg" id="email" name="email" placeholder="Email address" required/>
          <input class="form-control input-lg" id="tenantId" name="tenantId" placeholder="Tenant id" required/>
          <input type="hidden" id="_csrf" name="_csrf" value="{{csrfToken}}" />
          <input type="hidden" id="redirect" name="redirect" value="index.html" />
          <input class="btn" type="button" id="reset" on-click="reset()" value="Reset Password"/>
        </fieldset>
      </form>
    </div>
    <div class="pull-right" style="margin-right:20px">
      <a href="#" on-click="showReset()">Reset your password</a>
      <a href="http://omny.link/contact-us/">Or click here to sign-up for a trial</a>
    </div>
  </script>

  <script src="/webjars/jquery/1.11.1/jquery.min.js"></script>
  <script src="/webjars/bootstrap/3.3.5/js/bootstrap.min.js"></script>
  <script src="/webjars/Bootstrap-3-Typeahead/3.1.1/bootstrap3-typeahead.js"></script>
  <script src="/webjars/ractive/0.7.3/ractive.min.js"></script>

  <script src="js/activity-1.0.0.js"></script>
  <script src="js/login-1.0.0.js"></script>
  <script src="js/login-ui-1.0.0.js"></script>
  <script src="js/omny-1.0.0.js"></script>
</body>
</html>
