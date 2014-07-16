<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Ontology Knowledge Complete - Graph Visualizer</title>

<style>
	#currentNode{ 
		border: 1px solid #B8B8B8;
		width: 300px;
		height: 250px;
				
		padding-left: 15px;
		padding-top: 15px;
		top: 20px;
		
		overflow-y: auto;
		max-width:290px;
		max-height:210px;
		
		color: #fff;
		background: #222 linear-gradient(#444, #222);
		font-family:verdana,arial,sans-serif;
		font-size:14px;		
	}	

	#subtitle{
		border: 1px solid #B8B8B8;
		width: 270px;
		height: 240px;
		
		padding-left: 10px;
		padding-top: 10px;
		overflow-y: auto;
		background: #222 linear-gradient(#444, #222);						
	}	
</style>

<script	src="Assets/js/jquery-1.10.2.min.js"></script>
<script src="Assets/js/graph/arbor.js"></script>
<script src="Assets/js/graph/graphics.js"></script>
<script src="Assets/js/graph/main.js"></script>

<script>
	$(document).ready(function(){
		graph = startArbor("#viewport");
		addNodes(graph);			
	});
	
	<%
		String values = (String)request.getSession().getAttribute("valuesGraph");
		out.println(values);
	%>
</script>

</head>
<body>
	<%
		int width  = (Integer)request.getSession().getAttribute("width");
		out.println("<div style=\"width:"+(width+320)+"px\">");
	%>	
		<div style="float:left; border: 1px solid black;">
			<%
				int height = (Integer)request.getSession().getAttribute("height");
				out.println("<canvas id=\"viewport\" width=\""+width+"\" height=\""+height+"\"></canvas>");
				
			%>
		</div>	
		<div style="float:right;">
			<div id="subtitle">
				<%
					String subtitle = (String)request.getSession().getAttribute("subtitle");
					out.println("<img id=\"subtitle_table\" src=\"Assets/img/subtitles/"+subtitle+"\">");
				%>				
			</div>
			<br>
			<div id="currentNode">Select a node to visualize information about it.</div>
		</div>
	</div>

	<script>
	(function($) {
		$.fn.drags = function(opt) {

			opt = $.extend({handle:"",cursor:"move"}, opt);

			if(opt.handle === "") {
				var $el = this;
			} else {
				var $el = this.find(opt.handle);
			}

			return $el.css('cursor', opt.cursor).on("mousedown", function(e) {
				if(opt.handle === "") {
					var $drag = $(this).addClass('draggable');
				} else {
					var $drag = $(this).addClass('active-handle').parent().addClass('draggable');
				}
				var z_idx = $drag.css('z-index'),
					drg_h = $drag.outerHeight(),
					drg_w = $drag.outerWidth(),
					pos_y = $drag.offset().top + drg_h - e.pageY,
					pos_x = $drag.offset().left + drg_w - e.pageX;
				$drag.css('z-index', 1000).parents().on("mousemove", function(e) {
					$('.draggable').offset({
						top:e.pageY + pos_y - drg_h,
						left:e.pageX + pos_x - drg_w
					}).on("mouseup", function() {
						$(this).removeClass('draggable').css('z-index', z_idx);
					});
				});
				e.preventDefault(); // disable selection
			}).on("mouseup", function() {
				if(opt.handle === "") {
					$(this).removeClass('draggable');
				} else {
					$(this).removeClass('active-handle').parent().removeClass('draggable');
				}
			});

		}
	})(jQuery);

	$('#currentNode').drags();
	$('#subtitle').drags();
	</script>
	<!-- Just used in TNOKCO
	<img id="AF_AZUL" src="shapes/AF_AZUL.png" hidden>
	<img id="TTF_AZUL" src="shapes/TTF_AZUL.png" hidden>
	<img id="SN_AZUL" src="shapes/SN_AZUL.png" hidden>
	<img id="M_AZUL" src="shapes/M_AZUL.png" hidden>
	<img id="RP_AZUL" src="shapes/RP_AZUL.png" hidden>
	<img id="TE_AZUL" src="shapes/TE_AZUL.png" hidden>
	<img id="Layer_AZUL" src="shapes/Layer_AZUL.png" hidden>
	<img id="Binding_AZUL" src="shapes/Binding_AZUL.png" hidden>
	<img id="InfTransfer_AZUL" src="shapes/InfTransfer_AZUL.png" hidden>
	<img id="PM_AZUL" src="shapes/PM_AZUL.png" hidden>
	<img id="Process_AZUL" src="shapes/Process_AZUL.png" hidden>
	<img id="Input_AZUL" src="shapes/Input_AZUL.png" hidden>
	<img id="Output_AZUL" src="shapes/Output_AZUL.png" hidden>
	<img id="Datatype_AZUL" src="shapes/Datatype_AZUL.png" hidden>

	<img id="AF_VERDE" src="shapes/AF_VERDE.png" hidden>
	<img id="TTF_VERDE" src="shapes/TTF_VERDE.png" hidden>
	<img id="SN_VERDE" src="shapes/SN_VERDE.png" hidden>
	<img id="M_VERDE" src="shapes/M_VERDE.png" hidden>
	<img id="RP_VERDE" src="shapes/RP_VERDE.png" hidden>
	<img id="TE_VERDE" src="shapes/TE_VERDE.png" hidden>
	<img id="Layer_VERDE" src="shapes/Layer_VERDE.png" hidden>
	<img id="Binding_VERDE" src="shapes/Binding_VERDE.png" hidden>
	<img id="InfTransfer_VERDE" src="shapes/InfTransfer_VERDE.png" hidden>
	<img id="PM_VERDE" src="shapes/PM_VERDE.png" hidden>
	<img id="Process_VERDE" src="shapes/Process_VERDE.png" hidden>
	<img id="Datatype_VERDE" src="shapes/Datatype_VERDE.png" hidden>
	<img id="Input_VERDE" src="shapes/Input_VERDE.png" hidden>
	<img id="Output_VERDE" src="shapes/Output_VERDE.png" hidden>

	<img id="AF_ROXO" src="shapes/AF_ROXO.png" hidden>
	<img id="TTF_ROXO" src="shapes/TTF_ROXO.png" hidden>
	<img id="SN_ROXO" src="shapes/SN_ROXO.png" hidden>
	<img id="M_ROXO" src="shapes/M_ROXO.png" hidden>
	<img id="RP_ROXO" src="shapes/RP_ROXO.png" hidden>
	<img id="TE_ROXO" src="shapes/TE_ROXO.png" hidden>
	<img id="Layer_ROXO" src="shapes/Layer_ROXO.png" hidden>
	<img id="Binding_ROXO" src="shapes/Binding_ROXO.png" hidden>
	<img id="InfTransfer_ROXO" src="shapes/InfTransfer_ROXO.png" hidden>
	<img id="PM_ROXO" src="shapes/PM_ROXO.png" hidden>
	<img id="Process_ROXO" src="shapes/Process_ROXO.png" hidden>
	<img id="Datatype_ROXO" src="shapes/Datatype_ROXO.png" hidden>
	<img id="Input_ROXO" src="shapes/Input_ROXO.png" hidden>
	<img id="Output_ROXO" src="shapes/Output_ROXO.png" hidden>
	-->

</body>
</html>
