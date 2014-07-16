<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ page import="br.ufes.inf.nemo.okco.model.Instance"%>
<%@ page import="br.ufes.inf.nemo.okco.model.DtoDefinitionClass"%>
<%@ page import="java.util.ArrayList"%>

<%	
	ArrayList<Instance> ListAllInstances = (ArrayList<Instance>)request.getSession().getAttribute("listInstances");
	ArrayList<Instance> ListInstancesInRelation = (ArrayList<Instance>)request.getSession().getAttribute("listInstancesInRelation");
	ArrayList<Instance> ListInstancesSameDifferent = (ArrayList<Instance>)request.getSession().getAttribute("listInstancesSameDifferent");
	DtoDefinitionClass dtoDefinition = (DtoDefinitionClass)request.getSession().getAttribute("definitionSelected");
	String propType = request.getSession().getAttribute("propType").toString();
	Instance instanceSelected = (Instance)request.getSession().getAttribute("instanceSelected");
%>

<%@include file="../templates/header.jsp"%>

<script type="text/javascript">

	var instanceIdSelect = 1;		//Used to load instance
	var commitReasoner = false;		//Used to select commit and reasoner or only commit

	$(document).ready(function() {

		/* ---- MODAL - inicio ---- */
		
		//seleciona os elementos a com atributo name="modal"
		$('a[name=modal-create]').click(function(e) {

			$('#selectInstanceForm').hide();
			$('#newInstanceForm').show();
			
			//cancela o comportamento padrão do link
			e.preventDefault();
	
			//armazena o atributo href do link
			var id = $(this).attr('href');
	
			//armazena a largura e a altura da tela
			//var maskHeight = $(document).height();
			//var maskWidth = $(window).width();
			
			var maskHeight = $(document).height();
			var maskWidth = "100%";//$(document).width();
	
			//Define largura e altura do div#mask iguais ás dimensões da tela
			$('#mask').css({'width':maskWidth,'height':maskHeight});
	
			//efeito de transição
			$('#mask').fadeIn(1000);
			$('#mask').fadeTo("slow",0.8);
	
			//armazena a largura e a altura da janela
			var winH = $(window).height();
			var winW = $(window).width();
			
			//centraliza na tela a janela popup
			$(id).css('top',  100);
			$(id).css('left', winW/2-$(id).width());
			
			//efeito de transição
			$(id).fadeIn(2000);
		});

		//seleciona os elementos a com atributo name="modal"
		$('a[name=modal-select]').click(function(e) {

			$('#selectInstanceForm').show();
			$('#newInstanceForm').hide();
			
			//cancela o comportamento padrão do link
			e.preventDefault();
	
			//armazena o atributo href do link
			var id = $(this).attr('href');
	
			//armazena a largura e a altura da tela
			//var maskHeight = $(document).height();
			//var maskWidth = $(window).width();
			
			var maskHeight = $(document).height();
			var maskWidth = "100%";//$(document).width();
	
			//Define largura e altura do div#mask iguais ás dimensões da tela
			$('#mask').css({'width':maskWidth,'height':maskHeight});
	
			//efeito de transição
			$('#mask').fadeIn(1000);
			$('#mask').fadeTo("slow",0.8);
	
			//armazena a largura e a altura da janela
			var winH = $(window).height();
			var winW = $(window).width();
			
			//centraliza na tela a janela popup
			$(id).css('top',  100);
			$(id).css('left', winW/2-$(id).width());
			
			//efeito de transição
			$(id).fadeIn(2000);
		});

		//se o botão fechar for clicado
		$('.window .close').click(function (e) {
			
			//cancela o comportamento padrão do link
			e.preventDefault();
			$('#mask, .window').hide();

			//limpa os campos select
			//instanceIdSelect = 1;

			//Clean ul's select
			//$('#selectInstanceForm .same li').remove();
			//$('#selectInstanceForm .different li').remove();

			//limpa os campos create
			//$('#name').val("");
			
			
		});

		//se div#mask for clicado
		$('#mask').click(function () {
			
			$(this).hide();
			$('.window').hide();
			
		});

		/* ---- MODAL - FIM ---- */		

		//http://api.jquery.com/find/
		
		$(".todo-actions > a").click(function(){

			var type = $(this).attr("id");
			
			if(type == "dif")
			{				
				//get name instance clicked
				var nameSelected = $(this).parent().parent().children(".desc").attr("title");

				if(! ($(this).parent().find('i').attr("class") == "disable"))
				{
					// cheking
					if ($(this).find('i').attr('class') == 'icon-check') {
				        
				        $(this).find('i').removeClass('icon-check-empty').addClass('icon-check');		//check-box
				        $(this).find('i').removeClass('disable');										//check-box-able-click
						$(this).parent().parent().find('span').css({ opacity: 1 });						//text
						$(this).parent().parent().find('span').css("text-decoration", "none");			//text		
	
						//Find the respective inside the ul:same
						
						var respective = $(this).parent().parent().parent().parent().parent().parent().parent().parent().find(".same").find("[title=\""+ nameSelected + "\"]");			

						respective.parent().find('i').addClass("disable");										//check-box-disable-click
				        respective.parent().find('.icon-check').removeClass('icon-check').addClass('icon-check-empty');		//check-box
				        respective.css("text-decoration", "line-through");													//text
				        respective.css({ opacity: 0.25 });																	//text
						
					} else {	// unchecking
						
				        $(this).find('i').removeClass('icon-check').addClass('icon-check-empty');		//check-box
				        $(this).parent().parent().find('span').css({ opacity: 1 });						//text
						$(this).parent().parent().find('span').css("text-decoration", "none");			//text		
	
						//Find the respective inside the ul:same
						
						var respective = $(this).parent().parent().parent().parent().parent().parent().parent().parent().find(".same").find("[title=\""+ nameSelected + "\"]");			
				        
				        respective.parent().find('i').removeClass("disable");				//check-box-disable-click
				        respective.css("text-decoration", "none");							//text
				        respective.css({ opacity: 1 });										//text
						
					}
				}
				
			} else	{

				//get name instance clicked
				var nameSelected = $(this).parent().parent().children(".desc").attr("title");

				if(! ($(this).parent().find('i').attr("class") == "disable"))
				{
					// if checked
					if ($(this).find('i').attr('class') == 'icon-check') {
				        
				        $(this).find('i').removeClass('icon-check-empty').addClass('icon-check');		//check-box
				        $(this).find('i').removeClass('disable');										//check-box-able-click
						$(this).parent().parent().find('span').css({ opacity: 1 });						//text
						$(this).parent().parent().find('span').css("text-decoration", "none");			//text		
	
						//Find the respective inside the ul:same
						
						var respective = $(this).parent().parent().parent().parent().parent().parent().parent().parent().find(".different").find("[title=\""+ nameSelected + "\"]");			

						respective.parent().find('i').addClass("disable");										//check-box-disable-click
				        respective.parent().find('.icon-check').removeClass('icon-check').addClass('icon-check-empty');		//check-box
				        respective.css("text-decoration", "line-through");													//text
				        respective.css({ opacity: 0.25 });																	//text
						
					} else {	//if unchecked
						
				        $(this).find('i').removeClass('icon-check').addClass('icon-check-empty');		//check-box
				        $(this).parent().parent().find('span').css({ opacity: 1 });						//text
						$(this).parent().parent().find('span').css("text-decoration", "none");			//text		
	
						//Find the respective inside the ul:same
						
						var respective = $(this).parent().parent().parent().parent().parent().parent().parent().parent().find(".different").find("[title=\""+ nameSelected + "\"]");			
				        
				        respective.parent().find('i').removeClass("disable");						//check-box-disable-click
				        respective.css("text-decoration", "none");									//text
				        respective.css({ opacity: 1 });												//text
						
					}
				}
			}
		});


		// CHECK BOXES
		
		$('.checkAll-create-same').live('click', function() {	
			
			var listSame = $("#newInstanceForm .todo-list.same li");
			var listDif = $("#newInstanceForm .todo-list.different li");

			if(this.checked == true) //select all
			{				
				//selecting
				listSame.each(function() {
					
					var node = $( this ).children( ".todo-actions" );
					node.find('i').removeClass('icon-check-empty').addClass('icon-check');		//check-box
      				node.find('i').removeClass('disable');										//check-box-able-click
					node.parent().parent().find('span').css({ opacity: 1 });					//text
					node.parent().parent().find('span').css("text-decoration", "none");			//text
				});

				//unselect another check all
				$( '.checkAll-create-different' ).prop('checked', false);

				//unselecting
				listDif.each(function() {
					
					var node = $( this ).children( ".todo-actions" );
					node.find('i').removeClass('icon-check').addClass('icon-check-empty');		//check-box
			        node.parent().parent().find('span').css({ opacity: 1 });					//text
					node.parent().parent().find('span').css("text-decoration", "none");			//text
				});

			} else { //unselect all

				//unselecting
				listSame.each(function() {
					
					var node = $( this ).children( ".todo-actions" );
					node.find('i').removeClass('icon-check').addClass('icon-check-empty');		//check-box
			        node.parent().parent().find('span').css({ opacity: 1 });					//text
					node.parent().parent().find('span').css("text-decoration", "none");			//text
				});
				//unselecting
				listDif.each(function() {
					
					var node = $( this ).children( ".todo-actions" );
					node.find('i').removeClass('icon-check').addClass('icon-check-empty');		//check-box
			        node.parent().parent().find('span').css({ opacity: 1 });					//text
					node.parent().parent().find('span').css("text-decoration", "none");			//text
				});
				
			}
	 
		});

		$('.checkAll-create-different').live('click', function() {
			
			var listSame = $("#newInstanceForm .todo-list.same li");
			var listDif = $("#newInstanceForm .todo-list.different li");

			if(this.checked == true) //select all
			{				
				//selecting
				listDif.each(function() {
					
					var node = $( this ).children( ".todo-actions" );
					node.find('i').removeClass('icon-check-empty').addClass('icon-check');		//check-box
      				node.find('i').removeClass('disable');										//check-box-able-click
					node.parent().parent().find('span').css({ opacity: 1 });					//text
					node.parent().parent().find('span').css("text-decoration", "none");			//text
				});

				//unselecting
				listSame.each(function() {
					
					var node = $( this ).children( ".todo-actions" );
					node.find('i').removeClass('icon-check').addClass('icon-check-empty');		//check-box
			        node.parent().parent().find('span').css({ opacity: 1 });					//text
					node.parent().parent().find('span').css("text-decoration", "none");			//text
				});

			} else { //unselect all

				//unselecting
				listDif.each(function() {
					
					var node = $( this ).children( ".todo-actions" );
					node.find('i').removeClass('icon-check').addClass('icon-check-empty');		//check-box
			        node.parent().parent().find('span').css({ opacity: 1 });					//text
					node.parent().parent().find('span').css("text-decoration", "none");			//text
				});
				//unselecting
				listSame.each(function() {
					
					var node = $( this ).children( ".todo-actions" );
					node.find('i').removeClass('icon-check').addClass('icon-check-empty');		//check-box
			        node.parent().parent().find('span').css({ opacity: 1 });					//text
					node.parent().parent().find('span').css("text-decoration", "none");			//text
				});
				
			}
			 
		});

		$('#selectError0').live('change', function() {		
			
			  instanceIdSelect = $(this).find('option:selected').attr("title");
			  if(instanceIdSelect > 0)
			  {
				  selectInstance();
			  }		 
		});

		//SELECT INSTANCE
		
		function selectInstance()
		{
			var id = instanceIdSelect;
			
			$.ajax({
				url : "selectInstance" + "?id=" + id,
				//data : JSON.stringify(json),
				type : "GET",

				beforeSend : function(xhr) {
					xhr.setRequestHeader("Accept", "application/json");
					xhr.setRequestHeader("Content-Type", "application/json");
				},
				success : function(dto) {

					//Clean ul's
					$('#selectInstanceForm .same li').remove();
					$('#selectInstanceForm .different li').remove();

					var result = "";
					for (var i=0; i < dto.listDifferentShow.length;i++)
					{ 
						if(dto.listDifferentShow[i].exist == true)
						{
							result = result + " <li>" +
							"<span class=\"todo-actions todo-actions-same\">" +
								"<a href=\"#\" id=\"dif\"> <i	class=\"icon-check\"></i></a>" +
							"</span>" +
						    "<span title=\"" + dto.listDifferentShow[i].ns + dto.listDifferentShow[i].name + "\" class=\"desc\">" + 
						   		dto.listDifferentShow[i].name + 
						    "</span>" + 
							"</li>";	
						}
						else
						{
							result = result + " <li>" +
							"<span class=\"todo-actions todo-actions-same\">" +  
								"<a href=\"#\" id=\"dif\"> <i	class=\"icon-check-empty\"></i></a>" +
							"</span>" +
						    "<span title=\"" + dto.listDifferentShow[i].ns + dto.listDifferentShow[i].name + "\" class=\"desc\">" + 
						   		dto.listDifferentShow[i].name + 
						    "</span>" + 
							"</li>";	
						}	
					}

					$('#selectInstanceForm .different').append(result);

					var result = "";
					for (var i=0; i < dto.listSameShow.length;i++)
					{ 
						if(dto.listSameShow[i].exist == true)
						{
							result = result + " <li>" +
							"<span class=\"todo-actions todo-actions-same\">" +  
								"<a href=\"#\" id=\"same\"> <i	class=\"icon-check\"></i></a>" +
							"</span>" +
						    "<span title=\"" + dto.listSameShow[i].ns + dto.listSameShow[i].name + "\" class=\"desc\">" + 
						   		dto.listSameShow[i].name + 
						    "</span>" + 
							"</li>";	
						}
						else
						{
							result = result + " <li>" +
							"<span class=\"todo-actions todo-actions-same\">" +  
								"<a href=\"#\" id=\"same\"> <i	class=\"icon-check-empty\"></i></a>" +
							"</span>" +
						    "<span title=\"" + dto.listSameShow[i].ns + dto.listSameShow[i].name + "\" class=\"desc\">" + 
						   		dto.listSameShow[i].name + 
						    "</span>" + 
							"</li>";	
						}		
					}

					$('#selectInstanceForm .same').append(result); 

				}
			});
		}

		$('.btn-add').live('click', function() {		
			$('#mask, .window').hide();
		});

		// ADD SELECTED INSTANCE

		$('.btn-select-add').live('click', function() {		

			$('#mask, .window').hide();

			var id = instanceIdSelect;

			$.ajax({
				url : "selectInstanceAdd" + "?id=" + id,
				//data : JSON.stringify(json),
				type : "GET",

				beforeSend : function(xhr) {
					xhr.setRequestHeader("Accept", "application/json");
					xhr.setRequestHeader("Content-Type", "application/json");
				},
				success : function(data) {

					var respContent = "<tr>" + 
						  "<td title=\"" + data.ns + data.name + "\">" + data.name + "</td>" +
						  "<td></td>" +
						  "<td></td>" +
						  "<td class=\"center\">" + 
		  						"<a class=\"btn btn-info\" href=\"#\"> <i class=\"icon-edit\"></i> </a>" +
		  						"<a class=\"btn btn-danger btn-exclude\" name=\""+ data.id + "\" style=\"margin-left: 5px;\" href=\"#\"> <i class=\"icon-trash \"></i> </a>" +	
		  			 	  "</td>" +
					  "</tr>";
					
					$('#table-instances tr:last').after(respContent); 

				}
			});
		});

		// Commit
		$('#commitInstanceForm').submit(function(event) {

			var rows = $("#table-instances tr").length;
			
			if(rows > 2)
			{
				var json = {
						"commitReasoner" : commitReasoner
					};
				
				$.ajax({
					url : $("#commitInstanceForm").attr("action"),
					data : JSON.stringify(json),
					type : "POST",

					beforeSend : function(xhr) {
						xhr.setRequestHeader("Accept", "application/json");
						xhr.setRequestHeader("Content-Type", "application/json");
					},
					success : function(data) {

						if(data.result == "ok")
						{
							//Redirect to instance page
							window.location.href = "list";
							
						} else if(data.result == "nothing") {

							alert("Not happens");
							
						} else {

							//Huston we have a problem
							var html = "<div class=\"alert alert-danger\">" +
											"<button type=\"button\" class=\"close\" data-dismiss=\"alert\">×</button>" + 
											"<strong>" + "Erro! " + "</strong>"+ data.result + 
										"</div>";

							$("#content").prepend(html);
						}
					}
				});
			}

			event.preventDefault();			
		});

		//Commit
		$('#commitButton').click(function () {
			
			commitReasoner = false;
			
		});

		//Commit and reasoner
		$('#commitAndReasonerButton').click(function () {
			
			commitReasoner = true;			
		});

		// EXCLUDE INSTANCE
		
		$('.btn-exclude').live('click', function() {

			$(this).addClass("exclude-process");
			var id = $(this).attr("name");
			
			$.ajax({
				url : "removeInstance" + "?id=" + id,
				//data : JSON.stringify(json),
				type : "GET",

				beforeSend : function(xhr) {
					xhr.setRequestHeader("Accept", "application/json");
					xhr.setRequestHeader("Content-Type", "application/json");
				},
				success : function(data) {

					$(".exclude-process").parent().parent().remove();

				}
			});
			
		});

		// CREATE INSTANCE

		$('#newInstanceForm').submit(function(event) {

			var separatorValues = "%&&%";
			
			var name = $('#name').val();
			
			var arraySame = "";
			$(this).find(".same").children("li").each(function( index ) 
			{
				if ( $(this).find("i").attr("class") == "icon-check")
				{
					arraySame = arraySame + separatorValues + $(this).children(".desc").attr("title");
				}				  
			});

			var arrayDif = "";			
			$(this).find(".different").children("li").each(function( index ) 
			{
				if ( $(this).find("i").attr("class") == "icon-check")
				{
					arrayDif = arrayDif + separatorValues + $(this).children(".desc").attr("title");
				}				  
			});	

			//var values = name + separatorValues + arraySame + separatorValues + arrayDif; 
			
			var json = {
				"name" : name,
				"arraySame" : arraySame,
				"arrayDif" : arrayDif
			};

			$.ajax({
				url : $("#newInstanceForm").attr("action"),
				data : JSON.stringify(json),
				type : "POST",

				beforeSend : function(xhr) {
					xhr.setRequestHeader("Accept", "application/json");
					xhr.setRequestHeader("Content-Type", "application/json");
				},
				success : function(data) {

					var respContent = "<tr>" + 
						  "<td title=\"" + data.ns + data.name + "\">" + data.name + "</td>" +
						  "<td></td>" +
						  "<td></td>" +
						  "<td class=\"center\">" + 
		  						"<a class=\"btn btn-info\" href=\"#\"> <i class=\"icon-edit\"></i> </a>" +
		  						"<a class=\"btn btn-danger btn-exclude\" name=\""+ data.id + "\" style=\"margin-left: 5px;\" href=\"#\"> <i class=\"icon-trash \"></i> </a>" +	
		  			 	  "</td>" +
					  "</tr>";
					
					$('#table-instances tr:last').after(respContent); 

				}
			 });

			event.preventDefault();
		});


		/* Ajust css */
		
		$(".chosen-container").css("width", "100%");


	}); // End - document ready
	
</script>

<style>

	/* O z-index do div#mask deve ser menor que do div#boxes e do div.window */

	#mask {
	position:absolute;
	z-index:9998;  
	background-color:#000; 
	display:none;
	width: 100%;
	}
	
	#boxes .window {
	position:absolute;
	width:440px;
	height:220px;
	display:none;
	z-index:9999;
	padding:20px;
	}
	
	/* Personalize a janela modal aqui. Você pode adicionar uma imagem de fundo. */
	#boxes #dialog {
	min-width:775px;
	min-height:503px;
	background-color: white;
	border-radius: 10px; 
	
	}
	/* posiciona o link para fechar a janela */
	.close {
	display:block; 
	text-align:right;
	}
	
	.checkAll{
		margin-left:4px;
	}
}

</style>

<div id="boxes">
	
	<div id="dialog" class="window">
		
		<!-- Botão para fechar a janela tem class="close" -->
		<a href="#" class="close">Close [X]</a>
		<br />
		<br />
		
		<!-- SELECT DIALOG -->
		<div id="selectInstanceForm">
		
			<div class="row">
			
				<div class="col-lg-12">
					<div class="box">
						<div class="box-header">
							<h2>
								<i class="icon-tasks"></i>Instance form
							</h2>
						</div>
						<div class="box-content">
						
								<div class="form-group">
										<label class="control-label" for="selectError">Select instance</label>
										<div class="controls">
										  	<select id="selectError0" class="form-control" data-rel="chosen">
										  		<option title="0">(Choose)</option>		
												<%
												for (Instance i : ListAllInstances) 
												{
													//Unchecked
													
													out.println("<option title=\"" + i.id + "\">" + i.name + "</option>");									
												}
												%>
										  	</select>
										</div>
								</div>		
								<!-- <a class="btn btn-success" title="View instance" href="#"> <i class="icon-zoom-in"></i> </a>
								<a class="btn btn-info btn-select-instance" title="Load instance" href="#"> <i class="icon-edit"></i> </a>  -->	
							
							
						</div>
					</div>
					<!--/col-->
				</div>
				<!--/col-->
			
				<div class="col-lg-6 col-md-6">
			
					<div class="box">
						<div class="box-header">
							<h2>
								<i class=""><input type="checkbox" disabled="disabled" class="checkAll-select-same" name="checkAll" title="Select All"></i>Same instance 
							</h2>
							<div class="box-icon">
									<a	href="#" class="btn-minimize"><i class="icon-chevron-up"></i></a>
							</div>
						</div>
						<div class="box-content">
							<div class="todo">
								<ul class="todo-list same" style="overflow: auto; height: 190px;">
									
									<!-- Load from jquery -->
									
								</ul>
							</div>
						</div>
					</div>
			
				</div>
				<!--/col-->
			
				<div class="col-lg-6 col-md-6">
			
					<div class="box">
						<div class="box-header">
							<h2>
								<i class=""><input type="checkbox" disabled="disabled" class="checkAll-select-different" name="checkAll" title="Select All"></i>Different instance
							</h2>
							<div class="box-icon">
									<a href="#" class="btn-minimize"><i class="icon-chevron-up"></i></a>
							</div>
						</div>
						<div class="box-content">
							<div class="todo">
								<ul class="todo-list different" style="overflow: auto; height: 190px;">
								
									<!-- Load from jquery -->
									
								</ul>
							</div>
						</div>
					</div>
			
				</div>
				<!--/col-->
				
				<div style="margin-left:12px; margin-top: -20px;">
				<input type="submit" class="btn-select-add btn btn-pre" value="Add To Relation"  />
				</div>
	
			</div>
			<!--/row-->
			
		</div>
		<!-- /SELECT DIALOG -->
		
		<!-- CREATE DIALOG -->
		<form id="newInstanceForm" action="createInstance" method="POST">
		
			<div class="row">
			
				<div class="col-lg-12">
					<div class="box">
						<div class="box-header">
							<h2>
								<i class="icon-tasks"></i>Instance form
							</h2>
						</div>
						<div class="box-content">
							
								<table class="table">
									<tr>
										<td>New Instance name</td>
										<td><input class="form-control" type="text" name="name" id="name"></td>
									</tr>
			
								</table>
							
						</div>
					</div>
					<!--/col-->
				</div>
				<!--/col-->
			 
				<div class="col-lg-6 col-md-6">
			
					<div class="box">
						<div class="box-header">
							<h2>
								<i class=""><input type="checkbox" class="checkAll-create-same" name="checkAll" title="Select All"></i>Same instance
							</h2>
							<div class="box-icon">
									<a	href="#" class="btn-minimize"><i class="icon-chevron-up"></i></a>
							</div>
						</div>
						<div class="box-content">
							<div class="todo">
								<ul class="todo-list same" style="overflow: auto; height: 190px;">
									<%
									for (Instance i : ListInstancesSameDifferent) 
									{
										//Unchecked
										
										out.println("<li>" +
												"<span class=\"todo-actions todo-actions-same\">" +  
														"<a id=\"same\" href=\"#\"> <i	class=\"icon-check-empty\"></i></a>" +
												"</span>" +
												"<span title=\"" + i.ns + i.name + "\" class=\"desc\">" + i.name + "</span>" + 
											"</li>");									
									}
									%>
								</ul>
							</div>
						</div>
					</div>
			
				</div>
				<!--/col-->
			
				<div class="col-lg-6 col-md-6">
			
					<div class="box">
						<div class="box-header">
							<h2>
								<i class=""><input type="checkbox" class="checkAll-create-different" name="checkAll" title="Select All"></i>Different instance
							</h2>
							<div class="box-icon">
									<a href="#" class="btn-minimize"><i class="icon-chevron-up"></i></a>
							</div>
						</div>
						<div class="box-content">
							<div class="todo">
								<ul class="todo-list different" style="overflow: auto; height: 190px;">
									<%
									for (Instance i : ListInstancesSameDifferent) 
									{
										//Unchecked
										
										out.println("<li>" +
												"<span class=\"todo-actions todo-actions-same\">" +  
														"<a id=\"dif\" href=\"#\"> <i	class=\"icon-check-empty\"></i></a>" +
												"</span>" +
												"<span title=\"" + i.ns + i.name + "\" class=\"desc\">" + i.name + "</span>" + 
											"</li>");									
									}
									%>
									
								</ul>
							</div>
						</div>
					</div>
			
				</div>
				<!--/col-->
				
				<div style="clear:both"></div>
				
				<div style="margin-left:12px; margin-top: -20px;" >
					<input type="submit" class="btn btn-pre btn-add" value="Create" />	
				</div>
				
	
			</div>
			<!--/row-->
			
		</form>
		<!-- /CREATE DIALOG -->
			
		<div style="clear:both"></div>
	
	</div>

</div>


<!-- ------------------------------------------------------------------------------------------------------------------------------------------------------------- -->

<h1 style="font-style: italic;">Complete Object Property</h1>
<div class="row">
	<div class="col-lg-12">
		<div class="box">
			<div class="box-header" data-original-title>
				<h2>
					<i class="icon-user"></i><span class="break"></span>Object Property
				</h2>
				<div class="box-icon">
					<a href="#" class="btn-minimize"><i class="icon-chevron-up"></i></a>
				</div>
			</div>
			<div class="box-content">
				<div class="span12">
					
					<div style="span6">
						<h2>
							<%
								if(propType.equals("SOME")){
									out.println("<b>Relation: </b>" + instanceSelected.name + "<small> -> </small>" + 
																	dtoDefinition.Relation.split("#")[1] + "<small> -> </small>" + propType + " " + " [?] " + "(" + dtoDefinition.Target.split("#")[1] + ")");
								} else {
									out.println("<b>Relation: </b>" + instanceSelected.name + "<small> -> </small>" + 
											dtoDefinition.Relation.split("#")[1] + "<small> -> </small>" + propType + " " + dtoDefinition.Cardinality + " [?] " + "(" + dtoDefinition.Target.split("#")[1] + ")");
								}
							%>
						</h2>
					</div>
					<div style="span6">
						<div style="margin-left: 10px; padding: 5px; width: 100px">
							<a href="#dialog" name="modal-create" style="padding: 15px 10px 5px 10px;"
								class="quick-button-small"> <i class="icon-group"></i>
								<p>Create instance</p>
							</a>
						</div>
						<div style="margin-left: 10px; padding: 5px; width: 100px">
							<a href="#dialog" name="modal-select" style="padding: 15px 10px 5px 10px;"
								class="quick-button-small"> <i class="icon-group"></i>
								<p>Select instance</p>
							</a>
						</div>
					</div>

				</div>
			</div>
			<div class="box-content">

				<table id="table-instances" class="table table-bordered">
					<thead>
						<tr>
							<th>Instance name</th>
							<th>Same instances</th>
							<th>Different instances</th>
							<th>Actions</th>
						</tr>
					</thead>
					<tbody>

						<%
							if(ListInstancesInRelation.size() == 0)
					  		{
					  			out.println("<tr>");
					  			out.println("<td></td>");
					  			out.println("<td></td>");
					  			out.println("<td></td>");
					  			out.println("<td></td>");
					  			out.println("</tr>");
					  		}
						  	for (Instance i : ListInstancesInRelation) {
						  		
						  		out.println("<tr>");
						  		
							  		out.println("<td title=\"" + i.ns + i.name + "\">" + i.name + "</td>");
							  		out.println("<td class=\"center\">");
							  			out.println("<ul>");
								  		for(String c : i.ListSameInstances)
								  		{
								  			out.println("<li title=\"" + c + "\">" + c.split("#")[1] + "</li>");
								  		}
							  			out.println("</ul>");
							  		out.println("</td>");
							  		out.println("<td class=\"center\">");
							  			out.println("<ul>");
								  		for(String c : i.ListDiferentInstances)
								  		{
								  			out.println("<li title=\"" + c + "\">" + c.split("#")[1] + "</li>");
								  		}
							  			out.println("</ul>");
						  			out.println("</td>");
							  		if(i.existInModel == true)
							  		{
							  			out.println("<td class=\"center\">	<i>No actions</i> </td>");
	
							  		} else {
							  			
							  			out.println("<td class=\"center\">" + 
							  					//"<a class=\"btn btn-success\" href=\"#\"> <i class=\"icon-zoom-in\"></i> </a> " + 
							  					"<a class=\"btn btn-info\" href=\"#\"> <i class=\"icon-edit\"></i> </a>" +
							  					"<a class=\"btn btn-danger\" href=\"#\"> <i class=\"icon-trash \"></i> </a>" +
							  					"</td>");
							  		}
						  									  		
						  		out.println("</tr>");
						  		
								
							}
						%>

					</tbody>
				</table>
				
				

			</div>
		</div>
	</div>
	<!--/col-->



</div>
<!--/row-->

<div class="row" style="margin-left: 0px">
					
	<form id="commitInstanceForm" action="commitInstance" method="POST">
		<%
			out.println("<button onclick=\"window.location = '/okco/details?id=" + instanceSelected.id + "';\" type=\"button\" class=\"btn btn-prev\"> <i class=\"icon-arrow-left\"></i> Back to instance</button>");
		%>
		
		<button id="commitButton" type="submit" class="btn btn-pre btn-commit btnload"> <i class="icon-arrow-right"></i> Commit</button>
		
		<button id="commitAndReasonerButton" type="submit" class="btn btn-pre btn-commit btnload"> <i class="icon-arrow-right"></i> Commit and Reasoner</button>
		
	</form>


	<!-- <button type="button" class="btn btn-success btn-next" data-last="Finish">Next <i class="icon-arrow-right"></i></button> -->
</div>
				<br>

<div class="row">
	 <div class="col-lg-12">
		<p>Description of page:</p>
		<div class="tooltip-demo well">
		  	<p class="muted" style="margin-bottom: 0;">
					This page is used for solving incompletions of type: object property minimum, some and exactly cardinality.
					<br>	
					<br>
					For complete a relation, the user must have to choose between "Create New Instance" and "Select instance", considering the type of competition (minCardinality, someValuesFrom, cardinality)
					<br>
					<br>
					When a modification is done, the user can <i>commit and reason it</i>. With this action, the ontology consistency is verified by the reasoner, which also perform its classification and impact verification over the already existent information. OKCo will then provide a new completion list to the user. Another user option is to <i>commit</i> the modification. With this option, the instance assumes the Modified state.					
				</p>
		</div>                                  
	 </div>
</div>	
<!-- /row -->

<%@include file="../templates/footer.jsp"%>