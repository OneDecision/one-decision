<!DOCTYPE html>
<html lang="en">
<head>
  <link href="css/bootstrap.min.css" rel="stylesheet">
  <link href="css/omny-0.7.0.css" rel="stylesheet">
  <link rel="icon" type="image/png" href="images/icon/omny-icon-16x16.png" />
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
    <h1>Omny Link Decisions Management</h1>
    <img src="images/omny-logo.png"/>
    <p>For support and hosted solutions see <a href="//omny.link">http://omny.link</a></p>
  </section>

  <!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
  <script src="js/jquery-1.11.0.min.js"></script>
  <!-- Include all compiled plugins (below), or include individual files as needed -->
  <script src="js/bootstrap.min.js"></script>
  <!--<script src="js/bootstrap3-typeahead.js"></script>
  <script src="js/ractive.min.js"></script>
  <script src="js/login-0.7.0.js"></script>
  <script src="js/login-ui-0.7.0.js"></script>-->
</body>
</html>