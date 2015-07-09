<!DOCTYPE html>
<html lang="en">
<head>
  <link href="css/bootstrap.min.css" rel="stylesheet">
  <link href="css/one-decision-0.8.0.css" rel="stylesheet">
  <link rel="icon" type="image/png" href="images/one-decision-icon-16x16.png" />
    <style>
    html,body,section#main {
      height:100%;
      text-align: center;
      width: 100%;
    }
    section#main {
      margin: auto;
      padding-top: 10%;
    }
    section#login {
      padding-top: 0;
      padding-right: 20px;
      position: absolute;
      top: 20px;
      right: 20px;
      width: 30%;
    }
    #container {
      position: fixed;
      top: 20px;
      right: 20px;
    }
    #container a {
      float: right;
    }
    .btn { 
      height: 40px;
    }
  </style>
<head>
<body>

  <section id="container"></section>

  <section id="main">
    <img src="images/one-decision-logo.png"/>
    <!--h1>open decision modeling</h1-->
    <p>For more information see <a href="//onedecision.io">http://onedecision.io</a>. Or for support and hosted solutions see <a href="//omny.link">http://omny.link</a>.</p></p>
  </section>


  <script id='template' type='text/ractive'>
      <form name="loginForm" action="/login" method="POST">
        <fieldset>
          <input type="text" id="username" name="username" placeholder="Username" required/>
          <input type="password" id="password" name="password" placeholder="Password" required/>
          <input type="hidden" id="_csrf" name="_csrf" value="{{csrfToken}}" />
          <input type="hidden" id="redirect" name="redirect" value="index.html" />
          <input type="button" id="login" onclick="ractive.login()" value="Login" class="btn btn-primary" />
        </fieldset>
        <p>If you are running the quick-start application you may login as <em>admin/onedecision</em></p> 
      </form>
  </script>

  <!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
  <script src="js/jquery-1.11.0.min.js"></script>
  <!-- Include all compiled plugins (below), or include individual files as needed -->
  <script src="js/bootstrap.min.js"></script>
  <script src="js/bootstrap3-typeahead.js"></script>
  <script src="js/ractive.min.js"></script>
  <script src="js/one-decision-0.8.0.js"></script>
  <script src="js/login-ui-0.8.0.js"></script>
</body>

</html>