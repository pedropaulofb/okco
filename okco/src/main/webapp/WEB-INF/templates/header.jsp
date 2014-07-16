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

<style type="text/css">

	#maskforloading 
	{
		position:absolute;
		z-index:9990;  
		background-color:#000; 
		display:none;
		width: 100%;	
		
		 filter:alpha(opacity=50);
     	opacity: 0.5;
     	-moz-opacity:0.5;
     	-webkit-opacity:0.5;	
	}
	
	#maskforloading img 
	{
		position:absolute;
		top:50%;
		left:50%;
	}

</style>

</head>

<script type="text/javascript">

$(document).ready(function() {

	$("#maskforloading").hide();

	  // Function loading
	  function loading()
	  {
		  	var maskHeight = $(document).height();
			var maskWidth = "100%";//$(document).width();
	
			//Define largura e altura do div#maskforloading iguais ás dimensões da tela
			$('#maskforloading').css({'width':maskWidth,'height':maskHeight});
	
			//efeito de transição
			$('#maskforloading').show();
	  }

	$(".btnload").click(function(){

		loading();
	});	
	
}); // End - document ready;

</script>

<body>

	<!-- Não remova o div#mask, pois ele é necessário para preencher toda a janela -->
	<div id="mask"></div>
	
	<!-- Não remova o div#maskforloading, pois ele é necessário para preencher toda a janela -->
	<div id="maskforloading">
		<img src="Assets/img/loading.gif" height="100px">
	</div>

	<!-- start: Header -->
	<div class="navbar">
		<div class="navbar-inner">
			<div class="container">
				<button class="navbar-toggle" type="button" data-toggle="collapse"
					data-target=".sidebar-nav.nav-collapse">
					<span class="icon-bar"></span> <span class="icon-bar"></span> <span
						class="icon-bar"></span>
				</button>
				<a id="main-menu-toggle" class="hidden-xs open"><i
					class="icon-reorder"></i></a>
				<div class="row">
					<a class="navbar-brand col-lg-2 col-sm-1 col-xs-12"
						href="/okco/welcome">
						<img src="Assets/img/WOKCo.png" alt="W-OKCo" width="92">
					</a>
				</div>
				<!-- start: Header Menu -->
				<div class="nav-no-collapse header-nav"></div>
				<!-- end: Header Menu -->

			</div>
		</div>
	</div>
	<!-- end: Header -->

	<div class="container">
		<div class="row">

			<!-- start: Main Menu -->
			<div id="sidebar-left" class="col-lg-2 col-sm-1">
				<br />
				<div class="nav-collapse sidebar-nav collapse navbar-collapse bs-navbar-collapse">
					<ul class="nav nav-tabs nav-stacked main-menu">
						<li><a href="/okco/welcome"><i class="icon-bar-chart"></i><span
								class="hidden-sm"> Start Page</span></a></li>
						<li><a href="/okco/list"><i class="icon-ok-circle"></i><span
								class="hidden-sm"> Instances</span></a></li>
						<li><a href="/okco/getModel" class=""><i class="icon-save"></i><span
								class="hidden-sm"> Save OWL File</span></a></li>
						<li><a href="/okco/faq" class=""><i class="icon-eye-open"></i><span
								class="hidden-sm"> FAQ</span></a></li>
					</ul>
				</div>
			</div>
			<!-- end: Main Menu -->

			<!-- start: Content -->
			<div id="content" class="col-lg-10 col-sm-11">