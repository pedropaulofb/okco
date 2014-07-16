<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<%@include file="../templates/header.jsp"%>

<style>

	.list-questions li
	{
		margin-top:10px;
	}
	.answer
	{
	margin-top:5px;
	}

</style>

<script type="text/javascript">

	//Variables to control specialization properties
	
	var ablePrev = false; //begnning
	var ableNext = true;  //begnning

	$(document).ready(function() {

		$(".answer").hide();

		//Previous bottom click
		$('.question').live('click', function() {		

			//$(this).parent().children(".answer").slideDown();
			if ( $(this).parent().children(".answer").is( ":hidden" ) ) {
				$(this).parent().children(".answer").slideDown();
			  } else {
				  $(this).parent().children(".answer").hide();
			  }
			
			
		}); // End - btn-prev
	});


</script>

	<h1>FAQ</h1>

	<div class="row">
		<div class="col-lg-12">
			<div class="box">
				<div class="box-header">
					<h2>
						<i class="icon-edit"></i>Questions
					</h2>
					<div class="box-icon">
						<a href="#" class="btn-setting"><i class="icon-wrench"></i></a> <a
							href="#" class="btn-minimize"><i class="icon-chevron-up"></i></a>
					</div>
				</div>
				<div class="box-content">
					
					<ol class="list-questions">
						  <li><a class="question" href="#">Lorem ipsum dolor sit amet?</a> <div class="answer">resposta.....</div> </li>
						  <li><a class="question" href="#">Consectetur adipiscing elit?</a> <div class="answer">resposta.....</div> </li>
						  <li><a class="question" href="#">Integer molestie lorem at massa?</a> <div class="answer">resposta.....</div> </li>
						  <li><a class="question" href="#">Facilisis in pretium nisl aliquet?</a> <div class="answer">resposta.....</div> </li>
						  <li><a class="question" href="#">Nulla volutpat aliquam velit?</a> <div class="answer">resposta.....</div> </li>
						  <li><a class="question" href="#">Faucibus porta lacus fringilla vel?</a> <div class="answer">resposta.....</div> </li>
						  <li><a class="question" href="#">Aenean sit amet erat nunc?</a> <div class="answer">resposta.....</div> </li>
						  <li><a class="question" href="#">Eget porttitor lorem?</a> <div class="answer">resposta.....</div> </li>
					</ol>
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
			  	<p class="muted" style="margin-bottom: 0;">Tight pants next level keffiyeh <a href="#" data-rel="tooltip" data-original-title="first tooltip">you probably</a> haven't heard of them. Photo booth beard raw denim letterpress vegan messenger bag stumptown. Farm-to-table seitan, mcsweeney's fixie sustainable quinoa 8-bit american appadata-rel <a href="#" data-rel="tooltip" data-original-title="Another tooltip">have a</a> terry richardson vinyl chambray. Beard stumptown, cardigans banh mi lomo thundercats. Tofu biodiesel williamsburg marfa, four loko mcsweeney's cleanse vegan chambray. A <a href="#" data-rel="tooltip" data-original-title="Another one here too">really ironic</a> artisan whatever keytar, scenester farm-to-table banksy Austin <a href="#" data-rel="tooltip" data-original-title="The last tip!">twitter handle</a> freegan cred raw denim single-origin coffee viral.
			  	</p>
			</div>                                  
		 </div>
  	</div>	
  	<!-- /row -->	

<%@include file="../templates/footer.jsp"%>