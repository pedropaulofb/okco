<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ page import="br.ufes.inf.nemo.okco.model.Instance"%>
<%@ page import="br.ufes.inf.nemo.okco.model.DataPropertyValue"%>
<%@ page import="br.ufes.inf.nemo.okco.model.DtoDefinitionClass"%>
<%@ page import="java.util.ArrayList"%>


<%
	// Get the parameters from controller
	
	ArrayList<Instance> ListInstancesInRelation = (ArrayList<Instance>)request.getSession().getAttribute("listInstancesInRelation");
	DtoDefinitionClass dtoDefinition = (DtoDefinitionClass)request.getSession().getAttribute("definitionSelected");
	Instance instanceSelected = (Instance)request.getSession().getAttribute("instanceSelected");
	String propType = request.getSession().getAttribute("propType").toString();
%>

<%@include file="../templates/header.jsp"%>

<script type="text/javascript">

	$(document).ready(function() {

		var total = 0;
		var totalToSelect = parseInt($(".totalToSelect").text());
		var commitReasoner = "false";		//Used to select commit and reasoner or only commit
		var arraySameDif = "";
		var count = 0;

		//Selecting
		$('.selectSameDif').live('change', function() {

			total = total + 1;
			if(total <= totalToSelect)
			{
				var value = $(this).find('option:selected').attr("value");
				var parentId = $(this).parent().attr("id");
				var parentIdReverse = parentId.split("").reverse().join("");

				//add
				arraySameDif = arraySameDif + "%&&%" + value + "x" + parentId;
				
				//Select respective
				$("#" + parentIdReverse + " option").each(function(){			
					
				    if ($(this).attr("value") == value) 
					{
				        $(this).attr("selected",true);
				        
				    } else {
				        $(this).removeAttr("selected");
				    }
				});
			}

			if(total >= totalToSelect)
			{
				$('.selectSameDif').attr('disabled', true);
			}

		});

		//refresh
		$('#refresh').live('click', function() {

			arraySameDif = new Array();
			count = 0;
			total = 0;
			$('.selectSameDif').attr("disabled", false);
			
			$("select option").each(function(){			
				
			    if ($(this).attr("value") == "none") 
				{
			        $(this).attr("selected", true);
			        
			    } else {
				    
			        $(this).removeAttr("selected");

			    }
			});
		});


		// Commit
		$('#commitInstanceForm').submit(function(event) {

			var json = {
					"ListInstanceDifSameIds" : arraySameDif,
					"runReasoner" : commitReasoner
				};
			
			$.ajax({
				
				url : $("#commitInstanceForm").attr("action"),
				data : JSON.stringify(json),
				type : "POST",

				beforeSend : function(xhr) {
					xhr.setRequestHeader("Accept", "application/json");
					xhr.setRequestHeader("Content-Type", "application/json");
				},
				success : function(dto) {

					window.location.href = "list";
					
					if(dto.result == "ok")
					{
						//Redirect to instance page
						window.location.href = "list";
						
					} else if(dto.result == "nothing") {

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

			event.preventDefault();			
		});

		//Commit
		$('#commitButton').click(function () {
			
			commitReasoner = "false";
			
		});

		//Commit and reasoner
		$('#commitAndReasonerButton').click(function () {
			
			commitReasoner = "true";			
		});

	});
</script>



<!-- ------------------------------------------------------------------------------------------------------------------------------------------------------------- -->

<h1 style="font-style: italic;">Complete Max Cardinality Property</h1>
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

				<div style="">
					<div style="float: left">
						<h2>
							<%
								if (propType.equals("SOME")) {
									out.println("<b>Relation: </b>" + instanceSelected.name + "<small> -> </small>"
											+ dtoDefinition.Relation.split("#")[1]
											+ "<small> -> </small>" + propType + " " + " [?] "
											+ "(" + dtoDefinition.Target.split("#")[1] + ")");
								} else {
									out.println("<b>Relation: </b>" + instanceSelected.name + "<small> -> </small>"
											+ dtoDefinition.Relation.split("#")[1]
											+ "<small> -> </small>" + propType + " "
											+ dtoDefinition.Cardinality + " [?] " + "("
											+ dtoDefinition.Target.split("#")[1] + ")");
								}
							%>
						</h2>
					</div>

				</div>

			</div>

			<div class="box-content">

				<table id="table-instances" class="table table-bordered">
					<thead>
						<tr>
							<th>Source</th>
							<th>Relation</th>
							<th>Target</th>
						</tr>
					</thead>
					<tbody>

						<%
							if (ListInstancesInRelation.size() == 0) {
								out.println("<tr>");
								out.println("<td></td>");
								out.println("<td></td>");
								out.println("<td></td>");
								out.println("</tr>");
							}
							for (Instance i : ListInstancesInRelation) {

								out.println("<tr>");

								out.println("<td title=\"" + instanceSelected.ns
										+ instanceSelected.name + "\">" + instanceSelected.name
										+ "</td>");
								out.println("<td class=\"center\"> "
										+ dtoDefinition.Relation.split("#")[1]);

								out.println("</td>");
								out.println("<td title=\"" + i.ns + i.name + "\">" + i.name
										+ "</td>");

								out.println("</td>");

								out.println("</tr>");

							}
						%>

					</tbody>
				</table>

				<h2>Select  
						<% 
							int value = (ListInstancesInRelation.size() - Integer.parseInt(dtoDefinition.Cardinality));
							out.println("<b> <span class=\"totalToSelect\">" + value + "</span> </b> Same and Different instances: <input id=\"refresh\" type=\"button\" onClick=\"#\" value=\"Refresh Selection\">");
						%>
						</h2>

				<table id="table-instances" class="table table-bordered">
					<thead>
						<tr>
							<td style="text-align: center">x</td>
							<%
								for (Instance i : ListInstancesInRelation) {

									out.println("<td id=\"y-" + i.id + "\">" + i.name + "</td>");
								}
							%>
						</tr>
					</thead>
					<tbody>

						<%
							for (Instance i : ListInstancesInRelation) {
								out.println("<tr>");

								out.println("<td id=\"" + i.id + "\">" + i.name + "</td>");

								for (Instance i2 : ListInstancesInRelation) {
									if (i2.name.equals(i.name)) {
										out.println("<td style=\"text-align:center\">x</td>");

									} else {

										out.println("<td style=\"text-align:center\" id=\""
												+ i.id + "x" + i2.id + "\">");

										out.println("<select class=\"selectSameDif\">");
										out.println("<option value=\"none\">-</option>");
										out.println("<option value=\"same\">Same</option>");
										out.println("<option value=\"dif\">Different</option>");
										out.println("</select>");

										out.println("</td>");
									}
								}

								out.println("</tr>");

							}
						%>

					</tbody>
				</table>

				<div class="actions">

					<form id="commitInstanceForm" action="commitMaxCard" method="POST">
						<%
							out.println("<button onclick=\"window.location = '/okco/details?id="
									+ instanceSelected.id
									+ "';\" type=\"button\" class=\"btn btn-prev\"> <i class=\"icon-arrow-left\"></i> Back to instance</button>");
						%>

						<button id="commitButton" type="submit"
							class="btn btn-pre btn-commit btnload">
							<i class="icon-arrow-right"></i> Commit
						</button>

						<button id="commitAndReasonerButton" type="submit"
							class="btn btn-pre btn-commit btnload">
							<i class="icon-arrow-right"></i> Commit and Reasoner
						</button>
					</form>

				</div>
			</div>
		</div>
	</div>
	<!--/col-->



</div>
<!--/row-->

<div class="row">
	<div class="col-lg-12">
		<p>Description of page:</p>
		<div class="tooltip-demo well">
			<p class="muted" style="margin-bottom: 0;">
					This page is used for solving incompletions of type: object property maximum cardinality.
					<br>	
					<br>
					For complete a relation, the user have to classify the relation's target instances, setting if they are equal or different, according the max cardinality value.
					<br>
					<br>
					When a modification is done, the user can <i>commit and reason it</i>. With this action, the ontology consistency is verified by the reasoner, which also perform its classification and impact verification over the already existent information. OKCo will then provide a new completion list to the user. Another user option is to <i>commit</i> the modification. With this option, the instance assumes the Modified state.				
				</p>
		</div>
	</div>
</div>
<!-- /row -->


<%@include file="../templates/footer.jsp"%>