<%@ page import="br.ufes.inf.nemo.okco.controller.HomeController"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ page import="br.ufes.inf.nemo.okco.model.Instance"%>
<%@ page import="br.ufes.inf.nemo.okco.model.DtoCompleteClass"%>
<%@ page import="br.ufes.inf.nemo.okco.model.EnumPropertyType"%>
<%@ page import="br.ufes.inf.nemo.okco.model.DtoPropertyAndSubProperties"%>
<%@ page import="br.ufes.inf.nemo.okco.model.DtoDefinitionClass"%>
<%@ page import="br.ufes.inf.nemo.okco.model.DtoInstanceRelation"%>
<%@ page import="java.util.ArrayList"%>


<%
	// Get the parameters from controller
	
	Instance instance = (Instance)request.getSession().getAttribute("instanceSelected");
	ArrayList<Instance> ListAllInstances = (ArrayList<Instance>)request.getSession().getAttribute("listInstances");
	ArrayList<DtoInstanceRelation> InstanceListRelations = (ArrayList<DtoInstanceRelation>)request.getSession().getAttribute("instanceListRelations");	
	
	ArrayList<DtoPropertyAndSubProperties> ListSpecializationProperties = (ArrayList<DtoPropertyAndSubProperties>)request.getSession().getAttribute("ListSpecializationProperties");	
	//ArrayList<String> listClassesMembersTmp = (ArrayList<String>)request.getSession().getAttribute("listClassesMembersTmp");
	
	ArrayList<DtoDefinitionClass> listSomeClassDefinition = (ArrayList<DtoDefinitionClass>)request.getSession().getAttribute("listSomeClassDefinition");
	ArrayList<DtoDefinitionClass> listMinClassDefinition = (ArrayList<DtoDefinitionClass>)request.getSession().getAttribute("listMinClassDefinition");
	ArrayList<DtoDefinitionClass> listMaxClassDefinition = (ArrayList<DtoDefinitionClass>)request.getSession().getAttribute("listMaxClassDefinition");
	ArrayList<DtoDefinitionClass> listExactlyClassDefinition = (ArrayList<DtoDefinitionClass>)request.getSession().getAttribute("listExactlyClassDefinition");
	
%>

<%@include file="../templates/header.jsp"%>

<script type="text/javascript">

	//Variables to control specialization properties
	
	var ablePrev = false; //begnning
	var ableNext = true;  //begnning

	$(document).ready(function() {

		$(".completePropertyForm").hide();
		$(".completeClassForm").hide();
		$("#completePropertyForm_1").show();
		$("#completeClassForm_1").show();

		// Complete property	
		$('.completePropertyForm').submit(function(event) {

			var separatorValues = "%&&%";
			var id = $("#specValue").attr("value");
			
			var arraySubProp = "";
			$(this).find(".checked").each(function( index ) 
			{
				arraySubProp = arraySubProp + separatorValues + $(this).parent().parent().parent().parent().children("span").attr("title");		  
			});
			
			var json = {
				"arrayCls" : "",
				"arraySubProp" : arraySubProp,
				"id" : id,
			};

			$.ajax({
				url : $(".completePropertyForm").attr("action"),
				data : JSON.stringify(json),
				type : "POST",

				beforeSend : function(xhr) {
					xhr.setRequestHeader("Accept", "application/json");
					xhr.setRequestHeader("Content-Type", "application/json");
				},
				success : function(data) {

					if(data.ok == true)
					{
						//alert("sucess. Refresh the page instance and remember the id");
						location.reload(true);
						
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
			
		}); // End - Complete Property

		// Complete class	
		$('.completeClassForm').submit(function(event) {

			var separatorValues = "%&&%";
			
			var arrayCls = "";
			$(this).find(".checked").each(function( index ) 
			{
				arrayCls = arrayCls + separatorValues + $(this).parent().parent().parent().parent().children("span").attr("title");		  
			});
			
			var json = {
				"arrayCls" : arrayCls,
				"arraySubProp" : "",
				"id" : "",
			};

			$.ajax({
				url : $(".completeClassForm").attr("action"),
				data : JSON.stringify(json),
				type : "POST",

				beforeSend : function(xhr) {
					xhr.setRequestHeader("Accept", "application/json");
					xhr.setRequestHeader("Content-Type", "application/json");
				},
				success : function(data) {

					if(data.ok == true)
					{
						location.reload(true);
						//alert("sucess. Refresh the page instance and remember the id");
						
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
			
		}); // End - Complete Class		
		
	}); // End - document ready
	
	//Previous bottom click
	$(document).on("click", ".btn-prev",function() {
		
		if($(this).hasClass("btn-success"))
		{
			var form = $(this).parent().parent().parent();
			form.hide();
			var id = form.attr('id');
			var name = id.split("_")[0];
			var numForm = id.split("_")[1];
			var numFormPrev = parseInt(numForm) - 1;			
			var prev = name + "_" + numFormPrev.toString();
			$("#" + prev).show();
		}
		
	}); // End - btn-prev
	
	//Next bottom click
	$(document).on("click", ".btn-next",function() {

		if($(this).hasClass("btn-success"))
		{
			var form = $(this).parent().parent().parent();
			form.hide();
			var id = form.attr('id');
			var name = id.split("_")[0];
			var numForm = id.split("_")[1];
			var numFormNext = parseInt(numForm) + 1;			
			var next = name + "_" + numFormNext.toString();
			$("#" + next).show();
		}
		

	});


</script>

<div class="row">

	<div style="padding-left: 15px; margin-bottom:20px;">
	
		<button onclick="window.location = '/okco/list';" style="float:left;" type="button" class="btn btn-prev"> <i class="icon-arrow-left"></i> Back to list</button>

		<div style="clear:both"></div>
		
	</div>
			
	<div class="col-lg-12">
		<div class="box">
			<div class="box-header">
				<h2>
					<i class="icon-edit"></i>Instance informations
				</h2>
				<div class="box-icon">
					 <a	href="#" class="btn-minimize"><i class="icon-chevron-up"></i></a>
				</div>
			</div>
			<div class="box-content">
				<table class="table table-bordered table-striped">
					<tr>
						<td>Name</td>
						<td style="padding-left: 30px;">
							<%
								out.println("<label title=\"" + instance.ns +instance.name + "\">" + instance.name + "</label>");
							%>
						</td>
					</tr>
					<tr>
						<td>Same instances</td>
						<td>
							<ul style="margin: 0">
							<%
								for(String iName: instance.ListSameInstances)
								{
									Instance i = HomeController.ManagerInstances.getInstance(ListAllInstances, iName);
									out.println("<li> <a title=\"" + i.ns + i.name  + "\" href=\"/okco/details?id=" + i.id + "\">" + i.name + "</a> </li>");
								}
							%>
							</ul>
						</td>
						
					</tr>
					<tr>
						<td>Different instances</td>
						<td>
							<ul style="margin: 0">
							<%
								for(String iName: instance.ListDiferentInstances)
								{
									Instance i = HomeController.ManagerInstances.getInstance(ListAllInstances, iName);
									out.println("<li> <a title=\"" + i.ns + i.name  + "\" href=\"/okco/details?id=" + i.id + "\">" + i.name + "</a> </li>");
								}
							%>
							</ul>
						</td>
					</tr>
					<tr>
						<td>Classes</td>
						<td>
							<ul style="margin: 0">
							<%
						  		for(String c : instance.ListClasses)
						  		{
						  			out.println("<li title=\"" + c + "\">" + c.split("#")[1] + "</li>");
						  		}
							%>
							</ul>
						</td>
					</tr>
					<tr>
						<td>Relations</td>
						<td>
							<ul style="margin: 0">
							<%
						  		for(DtoInstanceRelation dto : InstanceListRelations)
						  		{
						  			if(dto.Target.contains("^^"))
						  			{
						  				out.println("<li title=\"" + dto.Property + " -> " + dto.Target + "\">" + dto.Property.split("#")[1] + " -> " + dto.Target.split("\\^\\^")[0] + "</li>");
						  			}else{
						  				out.println("<li title=\"" + dto.Property + " -> " + dto.Target + "\">" + dto.Property.split("#")[1] + " -> " + dto.Target.split("#")[1] + "</li>");
						  			}
						  		}
							%>
							</ul>
						</td>
					</tr>
					<tr>
						<td>Visualizations</td>
						<td>
							<ul style="margin: 0">
								<%
									out.println("<li><a class=\"btn btn-success\" target=\"_blank\" href=\"/okco/graphVisualizer?typeView=IN&id=" + instance.id + "\"> <i class=\"icon-zoom-in\"></i> </a> IN</li>");
									out.println("<li style=\"margin-top:3px;\"><a class=\"btn btn-success\" target=\"_blank\" href=\"/okco/graphVisualizer?typeView=OUT&id=" + instance.id + "\"> <i class=\"icon-zoom-in\"></i> </a> OUT</li>");
								%>
							</ul>
						</td>
					<tr>
				</table>

			</div>
		</div>
	</div>
	<!--/col-->
	
	<div class="col-lg-12">
		<div class="box">
			<div class="box-header">
				<h2>
					<i class="icon-edit"></i>Specializations
				</h2>
				<div class="box-icon">
					 <a	href="#" class="btn-minimize"><i class="icon-chevron-up"></i></a>
				</div>
			</div>
			<div class="box-content">
			
				<ul class="nav tab-menu nav-tabs" style="padding-right: 24px;" id="myTab">
					<li class="propExclamacao"><a href="#properties">Properties
					
					<%
						if(ListSpecializationProperties.size() > 0)
						{
							out.println("<span class=\"notification orange\">!</span>");
							
						} 
						
					%>
					
					</a></li>
					<li class="active clsExclamacao"><a href="#classes">Classes
					
					<%
						if(instance.ListCompleteClasses.size() > 0)
						{
							out.println("<span class=\"notification orange\">!</span>");
							
						} 
						
					%>
					
					</a></li>
				</ul>
				
				<div id="myTabContent" class="tab-content">
					
					<div class="tab-pane" id="classes">
						
						<%
						if(instance.ListCompleteClasses.size() > 0)
						{
							
						} else {
							
							out.println("<h3>* No class specializations.</h3>");
						}
						
						%>
						
						<%	
							int countForm = 1;
							if(instance.ListCompleteClasses.size() > 0)
							{
								for (DtoCompleteClass dto : instance.ListCompleteClasses)
								{
									if(dto.Members.size()> 0)
									{
										out.println("<form class=\"completeClassForm\" id=\"completeClassForm_"+ countForm + "\" action=\"classifyInstanceClasses\" method=\"POST\">");
											out.println("<h3 style=\"margin-bottom: 10px;\">Classify instance <i>" +  instance.name + "</i> from <i>" + dto.CompleteClass.split("#")[1] + "</i> as:</h3>");
											
												out.println("<div class=\"controls\">");
																int count = 0;
																for (String member : dto.Members) 
																{
																	count++;
																	out.println("<label class=\"checkbox inline checkboxMarc\">");
																	out.println("<div class=\"checker\" id=\"uniform-inlineCheckbox" + count  +"\">");
																		out.println("<span class=\"\">");
																				out.println("<input type=\"checkbox\" id=\"inlineCheckbox" + count + "\" value=\"option" + count + "\"></span></div>");
																				out.println("<span title=\"" + member + "\">" + member.split("#")[1]);
																	out.println("</label>");
																}
												out.println("</div>");
											
												out.println("<div id=\"MyWizard\" class=\"wizard\" >");
											 
										 			out.println("<div class=\"actions\">");	
										 			
										 				 //one element
										 				 if(instance.ListCompleteClasses.size() == 1) {	// if only one
															 
										 					out.println("<button type=\"button\" class=\"btn btn-prev\" data-last=\"\"><i class=\"icon-arrow-left\"></i> Prev Generalization Set</button>");
										 					out.println("<button type=\"button\" class=\"btn btn-next\" data-last=\"\">Next Generalization Set<i class=\"icon-arrow-right\"></i></button>");
										 					
														 } else {
												 
														 	// Prev bottom
														 
															  if(instance.ListCompleteClasses.get(0).equals(dto))	//if first of list
															 {
															 	out.println("<button type=\"button\" class=\"btn btn-prev\" data-last=\"\"><i class=\"icon-arrow-left\"></i> Prev Generalization Set</button>");
															 }
															 else{
																 
																//have prev
																out.println("<button type=\"button\" class=\"btn btn-success btn-prev\" data-last=\"\"> <i class=\"icon-arrow-left\"></i> Prev Generalization Set</button>");
															 }												
															 
															 // Next bottom
															 
															 if(instance.ListCompleteClasses.get(instance.ListCompleteClasses.size() - 1).equals(dto))	//if last of list
															 {
															 	out.println("<button type=\"button\" class=\"btn btn-next\" data-last=\"\">Next Generalization Set<i class=\"icon-arrow-right\"></i></button>");
															 }
															 else { //have next
																 
																 out.println("<button type=\"button\" class=\"btn btn-success btn-next\" data-last=\"\">Next Generalization Set<i class=\"icon-arrow-right\"></i></button>"); 
															 }
														 
														 }
													
													out.println("</div>"); //action
												
												out.println("</div>");	// wizard
											
												out.println("<div class=\"form-actions\">" +
																"<button type=\"submit\" class=\"btn btn-primary\">Classify</button>" +
														"</div>");									
											
										out.println("</form>");
										
										countForm++;
									}	
								}
							}
							
						
						%>

					</div>
					<!-- /classes -->
					
					<div class="tab-pane" id="properties">
							
							<%
							if(ListSpecializationProperties.size() > 0)
							{
								int countDtos = 1;
								
								for (DtoPropertyAndSubProperties dto : ListSpecializationProperties) 
								{
									if(dto.SubProperties.size() > 0)
									{
										out.println("<form id=\"completePropertyForm_"+ countDtos + "\" class=\"completePropertyForm\" action=\"classifyInstanceProperty\" method=\"POST\">");
										
										out.println("<div class=\"form-group\" style=\"margin-top: 20px;\">");
										if(dto.iTargetNs.contains("^^"))
							  			{
											out.println("<h3>Classify relation <b>" + instance.name + " -> " + dto.Property.split("#")[1] + " -> " + dto.iTargetNs.split("\\^\\^")[0] + "</b> as:</h3>");	
											
							  			}else{
							  				
							  				out.println("<h3>Classify relation <b>" + instance.name + " -> " + dto.Property.split("#")[1] + " -> " + dto.iTargetName + "</b> as:</h3>");	
							  			}
										
											out.println("<input id=\"specValue\" type=\"hidden\" value=\""+ dto.id + "\">");
											
											 if(dto.SubProperties.size() > 0)
											 {
												 int countSub = 0;
												 for (String subProp: dto.SubProperties) 
												 {
													countSub++;
													
													if(dto.iTargetNs.contains("^^"))
										  			{
														out.println("<label class=\"checkbox inline\">");
														out.println("<div class=\"checker\" id=\"uniform-inlineCheckbox" + countSub  +"\"><span class=\"\"><input type=\"checkbox\" id=\"inlineCheckbox" + countSub + "\" value=\"option" + countSub + "\"></span></div> <span title=\"" + subProp + "\">" + subProp.split("#")[1] + " -> " + dto.iTargetNs.split("\\^\\^")[0]);
														out.println("</label>");						
										  			}else{
										  				out.println("<label class=\"checkbox inline\">");
														out.println("<div class=\"checker\" id=\"uniform-inlineCheckbox" + countSub  +"\"><span class=\"\"><input type=\"checkbox\" id=\"inlineCheckbox" + countSub + "\" value=\"option" + countSub + "\"></span></div> <span title=\"" + subProp + "\">" + subProp.split("#")[1] + " -> " + dto.iTargetName);
														out.println("</label>");	
										  			}													 
												 }
											 }
										 
										 out.println("</div>");
										 
										 out.println("<div class=\"form-actions\" style=\"padding-bottom:5px; margin-bottom:0px; border-bottom:1px solid #ccc\">");
										 out.println("<button type=\"submit\" class=\"btn btn-primary\">Classify</button>");
										 out.println("</div>");
										 
										 out.println("<div id=\"MyWizard\" class=\"wizard\" >");
										 
										 	out.println("<div class=\"actions\">");
										 	
										 		if(ListSpecializationProperties.size() == 1){
										 			
										 			out.println("<button type=\"button\" class=\"btn btn-prev\" data-last=\"\"><i class=\"icon-arrow-left\"></i> Prev relation</button>");
										 			out.println("<button type=\"button\" class=\"btn btn-next\" data-last=\"\">Next relation<i class=\"icon-arrow-right\"></i></button>");
										 			
										 		}else {
										 			
										 			// Prev bottom
													 
													  if(ListSpecializationProperties.get(0).equals(dto))	//if first of list
													 {
													 	out.println("<button type=\"button\" class=\"btn btn-prev\" data-last=\"\"><i class=\"icon-arrow-left\"></i> Prev relation</button>");
													 }
													 else{
														 
														//have prev
														out.println("<button type=\"button\" class=\"btn btn-success btn-prev\" data-last=\"\"> <i class=\"icon-arrow-left\"></i> Prev relation</button>");
													 }												
													 
													 // Next bottom
													 
													 if(ListSpecializationProperties.get(ListSpecializationProperties.size() - 1).equals(dto))	//if last of list
													 {
													 	out.println("<button type=\"button\" class=\"btn btn-next\" data-last=\"\">Next relation<i class=\"icon-arrow-right\"></i></button>");
													 }
													 else{
														 
														 //have next
														 out.println("<button type=\"button\" class=\"btn btn-success btn-next\" data-last=\"\">Next relation<i class=\"icon-arrow-right\"></i></button>"); 
													 }
										 		}
										 			
												 
												 
													
											out.println("</div>");
										 out.println("</div>");
										 
										 countDtos++;
										 
										 out.println("</form>");
									}
									 
								}
									 
							} else {
	
									 out.println("<h3>* No property specializations.</h3>");
							}			
							
										
							%>

					</div>
					<!-- /properties -->
					
				</div>
				<!-- /myTabContent -->
								
			</div>
			<!-- /box content -->
			
		</div>
		<!-- /box -->
		
	</div>
	<!--/col-->
	
</div>
<!--/row-->

<div class="row">
	<div class="col-lg-12">
		<div class="box">
			<div class="box-header" data-original-title>
				<h2>
					<i class="icon-user"></i><span class="break"></span>Object
					properties
				</h2>
				<div class="box-icon">
					<a href="#" class="btn-minimize"><i class="icon-chevron-up"></i></a>
				</div>
			</div>
			<div class="box-content">
				<table
					class="table table-striped table-bordered bootstrap-datatable datatable">
					<thead>
						<tr>
							<th>Source</th>
							<th>Relation</th>
							<th>Type</th>
							<th>Target</th>
							<th>Actions</th>
						</tr>
					</thead>
					<tbody>

						<%
							for (DtoDefinitionClass dto : listSomeClassDefinition) {
															  		
											  		if(dto.PropertyType.equals(EnumPropertyType.OBJECT_PROPERTY))
											  		{
											  		
												  		out.println("<tr>");
												  		
													  		//out.println("<td title=\"" + dto.Source + "\">" + dto.Source.split("#")[1] + "</td>");
													  		out.println("<td title=\"" + instance.ns + instance.name + "\">" + instance.name + "</td>");
													  		out.println("<td title=\"" + dto.Relation + "\">" + dto.Relation.split("#")[1] + "</td>");
													  		out.println("<td>" + "SOME" + "</td>");
													  		out.println("<td title=\"" + dto.Target + "\">" + dto.Target.split("#")[1] + "</td>");									  									  		
													  		out.println("<td class=\"center\">" + 
													  						"<a class=\"btn btn-info\" title=\"Manually Complete\" href=\"/okco/completeProperty?idInstance="+ instance.id + "&idDefinition=" + dto.id + "&type=" + "object" + "&propType=SOME" + "\"> <i class=\"icon-hand-up\"></i> </a>" + "&nbsp;" +
													  								"<a class=\"btn btn-info\" title=\"Auto Complete\" href=\"/okco/completePropertyAuto?idInstance="+ instance.id + "&idDefinition=" + dto.id + "&type=" + "object" + "&propType=SOME" + "\"> <i class=\"icon-cogs\"></i> </a>" +
													  					"</td>");
												  									  		
												  		out.println("</tr>");
											  		}	
												}
											  	
												for (DtoDefinitionClass dto : listMinClassDefinition) {
											  		
													if(dto.PropertyType.equals(EnumPropertyType.OBJECT_PROPERTY))
											  		{
											  		
												  		out.println("<tr>");
													  		//out.println("<td title=\"" + dto.Source + "\">" + dto.Source.split("#")[1] + "</td>");
													  		out.println("<td title=\"" + instance.ns + instance.name + "\">" + instance.name + "</td>");
													  		out.println("<td title=\"" + dto.Relation + "\">" + dto.Relation.split("#")[1] + "</td>");
													  		out.println("<td>" + "MIN " + dto.Cardinality + "</td>");
													  		out.println("<td title=\"" + dto.Target + "\">" + dto.Target.split("#")[1] + "</td>");								  		
													  		out.println("<td class=\"center\">" + 
													  				"<a class=\"btn btn-info\" title=\"Manually Complete\" href=\"/okco/completeProperty?idInstance="+ instance.id + "&idDefinition=" + dto.id + "&type=" + "object" + "&propType=MIN" + "\"> <i class=\"icon-hand-up\"></i> </a>" + "&nbsp;" +
											  								"<a class=\"btn btn-info\" title=\"Auto Complete\" href=\"/okco/completePropertyAuto?idInstance="+ instance.id + "&idDefinition=" + dto.id + "&type=" + "object" + "&propType=MIN" + "\"> <i class=\"icon-cogs\"></i> </a>" +
													  					"</td>");
												  									  		
												  		out.println("</tr>");							  		
											  		}							  		
													
												}
												
												for (DtoDefinitionClass dto : listMaxClassDefinition) {
											  		
													if(dto.PropertyType.equals(EnumPropertyType.OBJECT_PROPERTY))
											  		{
											  		
												  		out.println("<tr>");
												  		
												  		//out.println("<td title=\"" + dto.Source + "\">" + dto.Source.split("#")[1] + "</td>");
												  		out.println("<td title=\"" + instance.ns + instance.name + "\">" + instance.name + "</td>");
												  		out.println("<td title=\"" + dto.Relation + "\">" + dto.Relation.split("#")[1] + "</td>");
												  		out.println("<td>" + "MAX " + dto.Cardinality + "</td>");
												  		out.println("<td title=\"" + dto.Target + "\">" + dto.Target.split("#")[1] + "</td>");							  		
													  	out.println("<td class=\"center\">" + 
													  			"<a class=\"btn btn-info\" title=\"Manually Complete\" href=\"/okco/completeProperty?idInstance="+ instance.id + "&idDefinition=" + dto.id + "&type=" + "objectMax" + "&propType=MAX" + "\"> <i class=\"icon-hand-up\"></i> </a>" + "&nbsp;" +
										  								
													  					"</td>");
												  									  		
												  		out.println("</tr>");							  		
											  		}							  		
													
												}
											  	
												for (DtoDefinitionClass dto : listExactlyClassDefinition) {
											  		
													if(dto.PropertyType.equals(EnumPropertyType.OBJECT_PROPERTY))
											  		{
											  		
												  		out.println("<tr>");
												  		
												  		//out.println("<td title=\"" + dto.Source + "\">" + dto.Source.split("#")[1] + "</td>");
												  		out.println("<td title=\"" + instance.ns + instance.name + "\">" + instance.name + "</td>");
												  		out.println("<td title=\"" + dto.Relation + "\">" + dto.Relation.split("#")[1] + "</td>");
												  		out.println("<td>" + "EXACTLY " + dto.Cardinality + "</td>");
												  		out.println("<td title=\"" + dto.Target + "\">" + dto.Target.split("#")[1] + "</td>");					  		
													  	out.println("<td class=\"center\">" + 
													  			"<a class=\"btn btn-info\" title=\"Manually Complete\" href=\"/okco/completeProperty?idInstance="+ instance.id + "&idDefinition=" + dto.id + "&type=" + "object" + "&propType=EXACTLY" + "\"> <i class=\"icon-hand-up\"></i> </a>" + "&nbsp;" +
										  								"<a class=\"btn btn-info\" title=\"Auto Complete\" href=\"/okco/completePropertyAuto?idInstance="+ instance.id + "&idDefinition=" + dto.id + "&type=" + "object" + "&propType=EXACTLY" + "\"> <i class=\"icon-cogs\"></i> </a>" +
													  					"</td>");
												  									  		
												  		out.println("</tr>");							  		
											  		}							  		
													
												}
						%>

					</tbody>
				</table>
			</div>
		</div>
	</div>
	<!--/col-->

	<div class="col-lg-12">
		<div class="box">
			<div class="box-header" data-original-title>
				<h2>
					<i class="icon-user"></i><span class="break"></span>Data properties
				</h2>
				<div class="box-icon">
					<a href="#" class="btn-minimize"><i class="icon-chevron-up"></i></a>
				</div>
			</div>
			<div class="box-content">
				<table
					class="table table-striped table-bordered bootstrap-datatable datatable">
					<thead>
						<tr>
							<th>Source</th>
							<th>Relation</th>
							<th>Type</th>
							<th>Target</th>
							<th>Actions</th>
						</tr>
					</thead>
					<tbody>

						<%
							for (DtoDefinitionClass dto : listSomeClassDefinition) {
															  		
										  		if(dto.PropertyType.equals(EnumPropertyType.DATA_PROPERTY))
										  		{
										  		
											  		out.println("<tr>");
											  		
											  		//out.println("<td title=\"" + dto.Source + "\">" + dto.Source.split("#")[1] + "</td>");
											  		out.println("<td title=\"" + instance.ns + instance.name + "\">" + instance.name + "</td>");
											  		out.println("<td title=\"" + dto.Relation + "\">" + dto.Relation.split("#")[1] + "</td>");
											  		out.println("<td>" + "SOME " + "</td>");
											  		out.println("<td title=\"" + dto.Target + "\">" + dto.Target.split("#")[1] + "</td>");								  		
											  		out.println("<td class=\"center\">" + 
											  				"<a class=\"btn btn-info\" href=\"/okco/completeProperty?idInstance="+ instance.id + "&idDefinition=" + dto.id + "&type=" + "data" + "&propType=SOME" + "\"> <i class=\"icon-hand-up\"></i> </a>" +
											  					"</td>");
											  									  		
											  		out.println("</tr>");							  		
										  		}							  		
												
											}
										  	
											for (DtoDefinitionClass dto : listMinClassDefinition) {
										  		
												if(dto.PropertyType.equals(EnumPropertyType.DATA_PROPERTY))
										  		{
										  		
											  		out.println("<tr>");
											  		
											  		//out.println("<td title=\"" + dto.Source + "\">" + dto.Source.split("#")[1] + "</td>");
											  		out.println("<td title=\"" + instance.ns + instance.name + "\">" + instance.name + "</td>");
											  		out.println("<td title=\"" + dto.Relation + "\">" + dto.Relation.split("#")[1] + "</td>");
											  		out.println("<td>" + "MIN " + dto.Cardinality + "</td>");
											  		out.println("<td title=\"" + dto.Target + "\">" + dto.Target.split("#")[1] + "</td>");						  		
												  	out.println("<td class=\"center\">" + 
												  			"<a class=\"btn btn-info\" href=\"/okco/completeProperty?idInstance="+ instance.id + "&idDefinition=" + dto.id + "&type=" + "data" + "&propType=MIN" + "\"> <i class=\"icon-hand-up\"></i> </a>" +
												  					"</td>");
											  									  		
											  		out.println("</tr>");							  		
										  		}							  		
												
											}
											
											for (DtoDefinitionClass dto : listMaxClassDefinition) {
										  		
												if(dto.PropertyType.equals(EnumPropertyType.DATA_PROPERTY))
										  		{
										  		
											  		out.println("<tr>");
											  		
											  		//out.println("<td title=\"" + dto.Source + "\">" + dto.Source.split("#")[1] + "</td>");
											  		out.println("<td title=\"" + instance.ns + instance.name + "\">" + instance.name + "</td>");
											  		out.println("<td title=\"" + dto.Relation + "\">" + dto.Relation.split("#")[1] + "</td>");
											  		out.println("<td>" + "MAX " + dto.Cardinality + "</td>");
											  		out.println("<td title=\"" + dto.Target + "\">" + dto.Target.split("#")[1] + "</td>");							  		
											  		out.println("<td class=\"center\">" + 
											  				"<a class=\"btn btn-info\" href=\"/okco/completeProperty?idInstance="+ instance.id + "&idDefinition=" + dto.id + "&type=" + "data" + "&propType=MAX" + "\"> <i class=\"icon-hand-up\"></i> </a>" +
											  					"</td>");
											  									  		
											  		out.println("</tr>");							  		
										  		}							  		
												
											}
										  	
											for (DtoDefinitionClass dto : listExactlyClassDefinition) {
										  		
												if(dto.PropertyType.equals(EnumPropertyType.DATA_PROPERTY))
										  		{
										  		
											  		out.println("<tr>");
											  		
											  		//out.println("<td title=\"" + dto.Source + "\">" + dto.Source.split("#")[1] + "</td>");
											  		out.println("<td title=\"" + instance.ns + instance.name + "\">" + instance.name + "</td>");
											  		out.println("<td title=\"" + dto.Relation + "\">" + dto.Relation.split("#")[1] + "</td>");
											  		out.println("<td>" + "EXACTLY " + dto.Cardinality + "</td>");
											  		out.println("<td title=\"" + dto.Target + "\">" + dto.Target.split("#")[1] + "</td>");
												  	out.println("<td class=\"center\">" + 
												  			"<a class=\"btn btn-info\" href=\"/okco/completeProperty?idInstance="+ instance.id + "&idDefinition=" + dto.id + "&type=" + "data" + "&propType=EXACTLY" + "\"> <i class=\"icon-hand-up\"></i> </a>" +
												  					"</td>");							  									  		
											  		out.println("</tr>");							  		
										  		}							  		
												
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
	 <div class="col-lg-12">
		<p>Description of page:</p>
		<div class="tooltip-demo well">
		  	<p class="muted" style="margin-bottom: 0;">
					This page presents all details of a selected instance.
					<br>	
					<br>
					The Instance Information box presents the following information about a selected instance: name , sameAs and differentFrom relations, belonging classes, and its relations. It also presents two types of visualizations: (a) visualize all relations (object and data properties) that a selected instance has from it to other instances, (b) visualize all relations that other instances have from them to the selected instance.
					<br>
					<br>
					The Specialization box has two tabs: the first one shows the Class specializations and the other one shows the Property specializations.
					<br>
					<br>
					All cases of property competitions are presented in the Object properties and in the Data properties boxes. There are: (i) object and (ii) data properties minimum cardinality restriction (minCardinality), (iii) object and (iv) data properties maximum cardinality restriction (maxCardinality), (v) object and (vi) data properties exactly cardinality restriction (cardinality), (vii) object and (viii) data properties some cardinality restriction (someValuesFrom). The user can use the hand icon button to complete manually its knowledge or the gear icon button to complete automatically.
					
				</p>
		</div>                                  
	 </div>
</div>	
<!-- /row -->	

<div class="actions">
	<button onclick="window.location = '/okco/list';" type="button" class="btn btn-prev"> <i class="icon-arrow-left"></i> Back to list</button>
	<!-- <button type="button" class="btn btn-success btn-next" data-last="Finish">Next <i class="icon-arrow-right"></i></button> -->
</div>


<%@include file="../templates/footer.jsp"%>