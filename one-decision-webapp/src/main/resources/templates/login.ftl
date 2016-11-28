<!DOCTYPE html>
<html lang="en">
<head>
  <link href="/webjars/bootstrap/3.3.5/css/bootstrap.min.css" rel="stylesheet">
  <link href="/css/one-decision-1.2.0.css" rel="stylesheet">
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
		      {{! If populated (eg by JS) on successful login user will be shown this page }}
          <input type="hidden" id="redirect" name="redirect"/>
          <input type="button" id="login" onclick="ractive.login()" value="Login" class="btn btn-primary" />
        </fieldset>
        <p style="text-align:right">If you are running the quick-start application you may login as <em>user, super-user</em> or <em>author</em>.
        <br/>The password for all users is <em>onedecision</em>.</p> 
      </form>
  </script>

  <script src="/webjars/jquery/1.11.1/jquery.min.js"></script>
  <script src="/webjars/bootstrap/3.3.5/js/bootstrap.min.js"></script>
  <script src="/webjars/Bootstrap-3-Typeahead/3.1.1/bootstrap3-typeahead.js"></script>
  <script src="/webjars/ractive/0.7.3/ractive.min.js"></script>
  <script src="/js/one-decision-1.2.0.js"></script>
  <script src="/js/one-decision-login-1.2.0.js"></script>
</body>

</html>
