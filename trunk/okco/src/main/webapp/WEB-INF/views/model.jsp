<%@ page contentType="text/rdf-xml" %>
<%
	String model = (String)request.getSession().getAttribute("model");

	if(model != null && model != "")
	{
		out.println(model);
		
	} else {
		
		out.println("* You need to load some model.");
	}
%>