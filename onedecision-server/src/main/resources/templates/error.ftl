<!DOCTYPE html>
<html lang="en">
<head>
  <link href="/webjars/bootstrap/3.3.5/css/bootstrap.min.css" rel="stylesheet">
  <link href="/css/one-decision-1.2.0.css" rel="stylesheet">
  <link rel="icon" type="image/png" href="/images/one-decision-16x16.png" />
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
      left: auto;
      right: auto;
      top: 20px;
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

  <section id="container">
    <h2>Oops! Something went wrong!</h2>
    <p>The server response was: ${error}</p>
  </section>

  <section id="main">
    <img src="images/one-decision-logo.png"/>
    <!--h1>open decision modeling</h1-->
    <p>For more information see <a href="//onedecision.io">http://onedecision.io</a>. Or for support and hosted solutions see <a href="//omny.link">http://omny.link</a>.</p></p>
  </section>

  <script src="/webjars/jquery/1.11.1/jquery.min.js"></script>
  <script src="/webjars/bootstrap/3.3.5/js/bootstrap.min.js"></script>
</body>
</html>