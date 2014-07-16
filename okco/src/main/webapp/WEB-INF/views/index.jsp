<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<%
	// Get the parameters from controller
	String error = (String)request.getSession().getAttribute("errorMensage");
	String loadOk = (String)request.getSession().getAttribute("loadOk");
%>

<%@include file="../templates/header.jsp"%>


<script type="text/javascript">

	$(document).ready(function() {

		
		
	}); // End - document ready;

</script>

<%
		if(error != null && !error.equals(""))
		{
			String htmlError = "<div class=\"alert alert-danger\">" +
					"<button type=\"button\" class=\"close\" data-dismiss=\"alert\">x</button>" + 
					"<strong>" + "Error! " + "</strong>"+ error + 
				"</div>";
			out.println(htmlError);
		}
	
		if(loadOk != null && !loadOk.equals("true"))
		{
			String htmlLoad = "<div class=\"alert alert-info\">" +
				"<button type=\"button\" class=\"close\" data-dismiss=\"alert\">x</button>" +
				"<strong>Hey!</strong> You need to load owl first." +
			"</div>";
			out.println(htmlLoad);
		}
	
	%>

<h1 style="font-style: italic;">Welcome to W-OKCo</h1>

<div class="row">
	<div class="col-lg-12">
		<div class="box">
			<div class="box-header">
				<h2>
					<i class="icon-edit"></i>OKCo configuration
				</h2>
				<div class="box-icon">
					<a href="#" class="btn-setting"><i class="icon-wrench"></i></a> <a
						href="#" class="btn-minimize"><i class="icon-chevron-up"></i></a>
				</div>
			</div>
			<div class="box-content">
				<form action="upload" class="form-horizontal"
					enctype="multipart/form-data" method="POST">
					<fieldset class="col-sm-12">

						<div class="form-group">
							<label class="control-label">Select Reasoner:</label>
							<div class="controls">

								<label class="radio"> <span class="checked"><input
										type="radio" name="optionsReasoner" id="optionsRadios1"
										value="pellet" checked="checked"></span> Pellet
								</label>
								<div style="clear: both"></div>
								<label class="radio"> <span class=""><input
										type="radio" name="optionsReasoner" id="optionsRadios2"
										value="hermit" ></span> Hermit
								</label>

							</div>
							<br /> <label class="checkbox inline"> <input
								type="checkbox" name="loadReasonerFirstCheckbox"
								id="loadReasonerFirstCheckbox" checked="checked" /> Use
								reasoner in load
							</label> <br /> <label class="control-label">File Upload:</label>
							<div class="controls">
								<input name="file" type="file"> <input type="submit" class="btnload"
									name="submit" value="Upload" />
							</div>
						</div>

					</fieldset>
				</form>

			</div>
		</div>
	</div>
	<!--/col-->

</div>
<!--/row-->

<div class="row">
	<div class="col-lg-12">
		<p>Description of OKCo:</p>
		<div class="tooltip-demo well">
			
				<p class="muted" style="margin-bottom: 0;">
					In the Start Page two settings must be provided: the reasoner settings and the Owl File.
				</p>
				<br>
				<p class="muted" style="margin-bottom: 0;">
					<b>Reasoner settings:</b>
					<br>
					The Ontology-based Network Advisor uses the OWL reasoners capabilities of classification, consistency checking and inference to 
					support the Advisor's functionalities. Two different reasoner options are provided: <a target="_blank" href="http://clarkparsia.com/pellet/">Pellet (version 2.3.1)</a> and <a target="_blank" href="http://hermit-reasoner.com/">HermiT (version 1.3.8)</a>.
					<br> 
					The existence of two different options is justified by the different characteristics of these reasoners. 
					Pellet implements a tableau-based algorithm, while HermiT implements a faster algorithm, the hypertableau calculus <a target="_blank" href="http://semantic-web-journal.org/sites/default/files/swj120.pdf">[reference]</a>. 
					However, Pellet has an incremental reasoning, i.e., it supports incremental classification and incremental consistency check for additions and removals <a target="_blank" href="http://citeseerx.ist.psu.edu/viewdoc/download?doi=10.1.1.153.8799&rep=rep1&type=pdf">[reference]</a>.
				</p>
				<br>
				<p class="muted" style="margin-bottom: 0;">
					<b>Load OWL File:</b>
					<br>
					The OWL File can be loaded by choosing the File button and then clicking in Upload.					
				</p>

		</div>
	</div>
</div>
<!-- /row -->

<%@include file="../templates/footer.jsp"%>