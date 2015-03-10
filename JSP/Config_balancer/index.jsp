<%@page import="java.util.*"%>
<%@page import="java.io.*"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<link href="favicon.ico" rel="icon" type="image/x-icon" />
        <link href="favicon.ico" rel="shortcut icon" type="image/x-icon" />
        <link href="../style.css" rel="stylesheet" type="text/css" />
		<link href="../bootstrap.min.css" rel="stylesheet" type="text/css" />
		<link href="../bootstrap-theme.min.css" rel="stylesheet" type="text/css" />
		<script src="../jquery-1.11.0.min.js"></script>
        <title>Stubs settings</title>
    </head>
    <body >
	
	
	<div id="wrapper">
		<h2>Configurator for stubs!</h2>
            <div id="asf-box">
                <h1>${pageContext.servletContext.serverInfo}</h1>
            </div>
            <div id="upper" class="curved container">
                <div id="notice">
                    <img src="../tomcat.png" alt="[tomcat logo]" />
                </div>
				
                <div id="actions">
                    <div class="button">
                        <a class="container shadow" href="/manager/status"><span>Server Status</span></a>
                    </div>
                    <div class="button">
                        <a class="container shadow" href="/manager/html"><span>Manager App</span></a>
                    </div>
                    <div class="button">
                        <a id="checker" style="cursor:pointer;" class="container shadow" ><span id="name_checker">Checker</span></a>
                    </div>
                   </div>
                <!--
                <br class="separator" />
                -->
                <br class="separator" />
            </div>
          
	
	</div>
     <% 


		if ((request.getMethod()).equalsIgnoreCase("post")) {
	
			Enumeration paramNames = request.getParameterNames();
			ArrayList<String> d = new ArrayList<String>();
			ArrayList<String> dd = new ArrayList<String>();
			int i = 0;
			while( paramNames.hasMoreElements()) {
			
				d.add((String)paramNames.nextElement());
				dd.add(i + "\r\n");

				i++;
			}

			// for gc
			i = 0;
			d = null;

			String jspPath = session.getServletContext().getRealPath("../../conf");
			String txtFilePath = jspPath+ "/Balancer.properties";	
			
			PrintWriter out2 = new PrintWriter(txtFilePath); 
			
			for (int j = 0; j < dd.size(); j++)
			{
			String value = request.getParameter(dd.get(j));
			
			out2.println(value);
				
			}
			out2.close();
			
	
         }


		
        %> 

	
	<form id="form" method="POST" action="/Config_balancer/index.jsp">
        <%

            String jspPath = session.getServletContext().getRealPath("../../conf");
            String txtFilePath = jspPath+ "/Balancer.properties";
            BufferedReader reader = new BufferedReader(new FileReader(txtFilePath));
            ArrayList<String> data = new ArrayList<String>();
            String line;

            while((line = reader.readLine())!= null){

				if (line.length() > 0 )	data.add(line);
            }
	
        %>
		
		<%	for (int i = 0; i < data.size(); i++) { %>
				<% if ((data.get(i)).matches("@(.)*@")) { %>
				
				<input style="display:none;" class="input-lg"   type= "text" name = "<%out.println(i);%>" value="<% out.println(data.get(i)); %>" size=75  />
				
				  
				
				<% }  else if ((data.get(i)).matches("(.)*Balancer(.)*")) { %>
				
				<div class="input-group">
					<span class = "input-group-addon">
				<% if ((data.get(i)).matches("#Balancer(.)*")) { %>
				
					<input class = "check" type="checkbox">
				
				<% } else { %>
				
					<input class = "check" type="checkbox" checked="checked">
				
				<% } %>
					</span>
					<input class="input-lg" class="form-control"  type= "text" name = "<%out.println(i);%>" value="<% out.println(data.get(i)); %>" size=70  />
				</div>
				
				<% } else { %>
				
				<input class="input-lg" type= "text" name = "<%out.println(i);%>" value="<% out.println(data.get(i)); %>" size=75  /> <br />
				
			<%}%>
			<%}%>
		
		<div style="width:675px;">
		<button class = "btn btn-lg btn-primary btn-block" type = "submit"  >Save</button>
		</div>
		</form>

		<script>
		$(".check").on("click", function() {
		
			var flag_check = $(this).attr("checked");
			
			if (flag_check == "checked") {

				 $(this).removeAttr("checked");

				 var cur_val = $(this).parent().next().attr("value");

				 var new_val = "#" + cur_val;

				 $(this).parent().next().attr("value", new_val);

			} else {

				$(this).attr("checked", "checked");

				var cur_val = $(this).parent().next().attr("value");

				var new_val = cur_val.substr(1, cur_val.length);

				$(this).parent().next().attr("value", new_val);
			}
			
			
		});



		$("#checker").on("click", function() {
			
			var count = 0;

			var count_urls = 0;

	
			$(".input-lg").each(function() {
			
			   var cur_value = $(this).attr("value");
			
			   if (cur_value.search('Balancer') != -1) {
				
				count_urls = count_urls + 1;
				
				if (cur_value.search('#') != -1) {
					
				    count = count + 1;
	
				}

			   }	

			
			});
			

			if (count != count_urls) {	

			$(".check").removeAttr("checked");
			
			$(".input-lg").each(function() {
				
				var cur_value =	$(this).attr("value");
				
				if (cur_value.search('#') != -1) {

					cur_value = cur_value.substring(1, cur_value.length);
					

				}
				
				var new_value = "#" + cur_value;


				if (new_value.search('Balancer') != -1) {

					$(this).attr("value", new_value);

				}
			});
				
			  $("#name_checker").html('Check');

			
			} else {

				$(".check").attr("checked", "checked");

				$(".input-lg").each(function() {

					var cur_value =	$(this).attr("value");
				

					cur_value = cur_value.substring(1, cur_value.length);
					
					if (cur_value.search('Balancer') != -1) {

						$(this).attr("value", cur_value);

					}					

				});

				  $("#name_checker").html('Uncheck');

					
			}
			
			
		})
		</script>

    </body>
</html>
