<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	
	<!-- start: Meta -->
	<meta charset="utf-8">
	<title>OKCo - Ontology Knowlodge Completer</title>
	<meta name="description" content="OKCo - Ontology Knowlodge Completer.">
	<meta name="author" content="Fábio Coradini">
	<!-- end: Meta -->
	
	<!-- start: Mobile Specific -->
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<!-- end: Mobile Specific -->	
	
	<!-- start: CSS -->
	
	<link href="Assets/css/bootstrap.min.css" rel="stylesheet">
	<link href="Assets/css/style.min.css" rel="stylesheet">
	<link href="Assets/css/retina.min.css" rel="stylesheet">
	
	<!--end: CSS -->
	
	
	<!-- The HTML5 shim, for IE6-8 support of HTML5 elements -->
	<!--[if lt IE 9]>
		  	<script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
			<link id="ie-style" href="Assets/ie.css" rel="stylesheet">
		<![endif]-->
	
	<!--[if IE 9]>
			<link id="ie9style" href="Assets/ie9.css" rel="stylesheet">
		<![endif]-->
	
	<!-- start: Favicon and Touch Icons -->
	<link rel="apple-touch-icon-precomposed" sizes="144x144"
		href="Assets/ico/apple-touch-icon-144-precomposed.png">
	<link rel="apple-touch-icon-precomposed" sizes="114x114"
		href="Assets/ico/apple-touch-icon-114-precomposed.png">
	<link rel="apple-touch-icon-precomposed" sizes="72x72"
		href="Assets/ico/apple-touch-icon-72-precomposed.png">
	<link rel="apple-touch-icon-precomposed"
		href="Assets/ico/apple-touch-icon-57-precomposed.png">
	<link rel="shortcut icon"
		href="Assets/ico/favicon.png">
	<!-- end: Favicon and Touch Icons -->
	
	<!-- start: JavaScript-->
	<script
		src="Assets/js/jquery-1.10.2.min.js"></script>
	<script
		src="Assets/js/jquery-migrate-1.2.1.min.js"></script>
	<script
		src="Assets/js/jquery-ui-1.10.3.custom.min.js"></script>
	<script
		src="Assets/js/jquery.ui.touch-punch.js"></script>
	<script src="Assets/js/modernizr.js"></script>
	<script
		src="Assets/js/bootstrap.min.js"></script>
	<script
		src="Assets/js/jquery.cookie.js"></script>
	<script
		src='Assets/js/fullcalendar.min.js'></script>
	<script
		src='Assets/js/jquery.dataTables.min.js'></script>
	<script
		src='Assets/js/dataTables.bootstrap.min.js'></script>
	<script src="Assets/js/excanvas.js"></script>
	<script src="Assets/js/jquery.flot.js"></script>
	<script
		src="Assets/js/jquery.flot.pie.js"></script>
	<script
		src="Assets/js/jquery.flot.stack.js"></script>
	<script
		src="Assets/js/jquery.flot.resize.min.js"></script>
	<script
		src="Assets/js/jquery.flot.time.js"></script>
	
	<script
		src="Assets/js/jquery.chosen.min.js"></script>
	<script
		src="Assets/js/jquery.uniform.min.js"></script>
	<script
		src="Assets/js/jquery.cleditor.min.js"></script>
	<script src="Assets/js/jquery.noty.js"></script>
	<script
		src="Assets/js/jquery.elfinder.min.js"></script>
	<script
		src="Assets/js/jquery.raty.min.js"></script>
	<script
		src="Assets/js/jquery.iphone.toggle.js"></script>
	<script
		src="Assets/js/jquery.uploadify-3.1.min.js"></script>
	<script
		src="Assets/js/jquery.gritter.min.js"></script>
	<script
		src="Assets/js/jquery.imagesloaded.js"></script>
	<script
		src="Assets/js/jquery.masonry.min.js"></script>
	<script
		src="Assets/js/jquery.knob.modified.js"></script>
	<script
		src="Assets/js/jquery.sparkline.min.js"></script>
	<script src="Assets/js/counter.min.js"></script>
	<script
		src="Assets/js/raphael.2.1.0.min.js"></script>
	<script
		src="Assets/js/justgage.1.0.1.min.js"></script>
	<script
		src="Assets/js/jquery.autosize.min.js"></script>
	<script src="Assets/js/retina.js"></script>
	<script
		src="Assets/js/jquery.placeholder.min.js"></script>
	<script src="Assets/js/wizard.min.js"></script>
	<script src="Assets/js/core.min.js"></script>
	<script src="Assets/js/charts.min.js"></script>
	<script src="Assets/js/custom.min.js"></script>
	<script src="Assets/js/jquery.paulund_modal_box.js"></script>
	<!-- end: JavaScript-->

</head>
<%
	String login = (String)request.getSession().getAttribute("login");
	if(login == null)
	{
		login = "";
	}
%>

<body>
		<div class="container">
		<div class="row">
					
			<div class="row">
				<div class="login-box">
					<div style="background: #1e8fc6; padding-top:10px; padding-bottom:10px; padding-left:85px">
						<img src="Assets/img/WOKCo.png" alt="W-OKCo" width="92">
					</div>
					<h2>Login</h2>
					<form class="form-horizontal" action="login" enctype="multipart/form-data" method="post">
						<fieldset>
							
							<input class="input-large col-xs-12" name="username" id="username" type="text" placeholder="type username"/>
							<input class="input-large col-xs-12" name="password" id="password" type="password" placeholder="type password"/>
							
							<div class="clearfix"></div>
							
							<button type="submit" class="btn btn-primary col-xs-12">Login</button>
						</fieldset>
						
						<%
						
						if(login.equals("false"))
						{
							out.println("<hr>");
							out.println("<h3 style=\"color:red\">Invalid Username or Password.</h3>");
						}
						
						%>

					</form>
					<hr>
					
				</div>
			</div><!--/row-->
			
				</div><!--/row-->		
		
	</div><!--/container-->
	
	</body>
</html>


