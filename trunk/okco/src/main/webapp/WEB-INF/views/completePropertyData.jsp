<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ page import="br.ufes.inf.nemo.okco.model.Instance"%>
<%@ page import="br.ufes.inf.nemo.okco.model.DataPropertyValue"%>
<%@ page import="br.ufes.inf.nemo.okco.model.DtoDefinitionClass"%>
<%@ page import="java.util.ArrayList"%>


<%
	// Get the parameters from controller
	
	ArrayList<DataPropertyValue> ListValuesInRelation = (ArrayList<DataPropertyValue>)request.getSession().getAttribute("listValuesInRelation");
	DtoDefinitionClass dtoDefinition = (DtoDefinitionClass)request.getSession().getAttribute("definitionSelected");
	Instance instanceSelected = (Instance)request.getSession().getAttribute("instanceSelected");
	String propType = request.getSession().getAttribute("propType").toString();
%>

<%@include file="../templates/header.jsp"%>

<script type="text/javascript">

	$(document).ready(function() {

		//Load selected instance
		//selectInstance();

		/* ---- MODAL - inicio ---- */
		
		//seleciona os elementos a com atributo name="modal"
		$('a[name=modal-create]').click(function(e) {
			
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
		$('a[name=modal-edit]').click(function(e) {
			
			//cancela o comportamento padrão do link
			e.preventDefault();
	
			//armazena o atributo href do link
			var id = $(this).attr('href');
	
			//armazena a largura e a altura da tela
			//var maskHeight = $(document).height();
			//var maskWidth = $(window).width();
			
			var maskHeight = $(document).height();
			var maskWidth = $("#content").width();
	
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
		
		$('.btn-add').live('click', function() {		
			$('#mask, .window').hide();
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
				url : "removeDataValue" + "?id=" + id,
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

		//CREATE INSTANCE

		$('#newDataValueForm').submit(function(event) {
			
			var value = $('#value').val();
			
			var json = {
				"value" : value
			};

			$.ajax({
				url : $("#newDataValueForm").attr("action"),
				data : JSON.stringify(json),
				type : "POST",

				beforeSend : function(xhr) {
					xhr.setRequestHeader("Accept", "application/json");
					xhr.setRequestHeader("Content-Type", "application/json");
				},
				success : function(data) {

					var respContent = "<tr>" + 
						  "<td title=\"" + data.value + "\">" + data.value + "</td>" +
						  "<td title=\"" + data.classValue + "\">" + data.classValue + "</td>" +
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


	});
</script>

<style>

	/* O z-index do div#mask deve ser menor que do div#boxes e do div.window */

	#mask {
	position:absolute;
	z-index:9998;  
	background-color:#000; 
	display:none;
	}
	
	#boxes .window {
	position:absolute;
	width:440px;
	height:200px;
	display:none;
	z-index:9999;
	padding:20px;
	}
	
	/* Personalize a janela modal aqui. Você pode adicionar uma imagem de fundo. */
	#boxes #dialog-create, #boxes #dialog-select {
	min-width:675px;
	min-height:403px;
	background-color: white;
	border-radius: 10px; 
	
	}
	/* posiciona o link para fechar a janela */
	.close {
	display:block; 
	text-align:right;
	}

</style>

<div id="boxes">

	<!-- CREATE DIALOG -->	
	
	<div id="dialog-create" class="window">
		
		<!-- Botão para fechar a janela tem class="close" -->
		<a href="#" class="close">Fechar [X]</a>
		<br />
		<br />		
		<form id="newDataValueForm" action="createDataValue" method="POST">
		
			<div class="row">
			
				<div class="col-lg-12">
					<div class="box">
						<div class="box-header">
							<h2>
								<i class="icon-tasks"></i>Datatype form
							</h2>
						</div>
						<div class="box-content">
							
								<table class="table">
									<tr>
										<td>New data value</td>
										<td><input class="form-control" type="text" name="value" id="value"></td>
									</tr>
									<tr>
										<td>DataType</td>
										<td><% out.println(dtoDefinition.Target); %></td>
									</tr>
			
								</table>
							
						</div>
					</div>
					<!--/col-->
				</div>
				<!--/col-->					

				<input type="submit" class="btn btn-pre btn-add" value="Add data value" />
	
			</div>
			<!--/row-->
			
		</form>
		
		<div style="clear:both"></div>
	
	</div>
	<!-- /CREATE DIALOG -->

</div>


<!-- ------------------------------------------------------------------------------------------------------------------------------------------------------------- -->

<h1 style="font-style: italic;">Complete Data Property</h1>
<div class="row">
	<div class="col-lg-12">
		<div class="box">
			<div class="box-header" data-original-title>
				<h2>
					<i class="icon-user"></i><span class="break"></span>Data Property
				</h2>
				<div class="box-icon">
					<a href="#" class="btn-minimize"><i class="icon-chevron-up"></i></a>
				</div>
			</div>
			<div class="box-content">
				<div class="">
					<div style="">
						<div style="float: left">
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
						<div style="float: left">
							<div style="margin-left: 10px; padding: 5px;">
								<a href="#dialog-create" name="modal-create" style="padding: 15px 10px 5px 10px;"
									class="quick-button-small"> <i class="icon-group"></i>
									<p>Add data values</p>
								</a>
							</div>
						</div>
						<div style="clear: both;"></div>

					</div>

				</div>
			</div>
			<div class="box-content">

				<table id="table-instances" class="table table-bordered">
					<thead>
						<tr>
							<th>Data value</th>
							<th>DataType</th>
							<th>Actions</th>
						</tr>
					</thead>
					<tbody>

						<%
							if(ListValuesInRelation.size() == 0)
					  		{
					  			out.println("<tr>");
					  			out.println("<td></td>");
					  			out.println("<td></td>");
					  			out.println("<td></td>");
					  			out.println("</tr>");
					  		}
						  	for (DataPropertyValue data : ListValuesInRelation) {
						  		
						  		out.println("<tr>");						  		
							  		out.println("<td title=\"" + data.value + "\">" + data.value + "</td>");
							  		out.println("<td title=\"" + data.classValue + "\">" + data.classValue + "</td>");
							  		out.println("<td class=\"center\">	<i>No actions</i> </td>");						  									  		
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

<div class="row">
	
	<form id="commitInstanceForm" action="commitDataValues" method="POST">
		<%
			out.println("<button onclick=\"window.location = '/okco/details?id=" + instanceSelected.id + "';\" type=\"button\" class=\"btn btn-prev\"> <i class=\"icon-arrow-left\"></i> Back to instance</button>");
		%>
		
		<button id="commitButton" type="submit" class="btn btn-pre btn-commit btnload"> <i class="icon-arrow-right"></i> Commit</button>
		
		<button id="commitAndReasonerButton" type="submit" class="btn btn-pre btn-commit btnload"> <i class="icon-arrow-right"></i> Commit and Reasoner</button>
	</form>

</div>

<br/>

<div class="row">
	 <div class="col-lg-12">
		<p>Description of page:</p>
		<div class="tooltip-demo well">
		  	<p class="muted" style="margin-bottom: 0;">
					This page is used for solving data property incompletions.
					<br>	
					<br>
					For complete the relation, the user have to add some data values according to the type of the completion (minCardinality, someValuesFrom, cardinality).
					<br>
					<br>
					When a modification is done, the user can <i>commit and reason it</i>. With this action, the ontology consistency is verified by the reasoner, which also perform its classification and impact verification over the already existent information. OKCo will then provide a new completion list to the user. Another user option is to <i>commit</i> the modification. With this option, the instance assumes the Modified state.					
				</p>
		</div>                                  
	 </div>
</div>	
<!-- /row -->


<%@include file="../templates/footer.jsp"%>