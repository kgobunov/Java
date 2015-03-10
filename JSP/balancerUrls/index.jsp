<%@page import="java.util.*"%>
<%@page import="java.io.*"%>
<%@page import="java.net.InetAddress"%>
<%@page import="java.net.UnknownHostException"%>
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
<body>


	<div id="wrapper">
		<h2>Configurator for balancer urls!</h2>
		<div id="asf-box">
			<h1>${pageContext.servletContext.serverInfo}</h1>
		</div>
		<div id="upper" class="curved container">
			<div id="notice">
				<img src="../tomcat.png" alt="[tomcat logo]" />
			</div>

			<div id="actions">
				<div class="button">
					<a class="container shadow" href="/manager/status"><span>Server
							Status</span></a>
				</div>
				<div class="button">
					<a class="container shadow" href="/manager/html"><span>Manager
							App</span></a>
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
			
			String ki = "UrlKI";
			
        	String under = "UrlUND";
        
        	ArrayList<Integer> portsKI = new ArrayList<Integer>();
        	
        	ArrayList<Integer> portsUnder = new ArrayList<Integer>();
        	
        	for (int i = 443; i < 456; i++) {
        		
        		if (i == 445) {
        			
        			continue;
        			
        		} 
        		
        		if (i <= 454) {
        			
        			portsKI.add(i);
        			
        		}
        		
        		portsUnder.add(i);

        		
        	}
        	
        	
			int i = 0;
			

			String jspPath = session.getServletContext().getRealPath("../../conf");
			String txtFilePath = jspPath+ "/BalancerUrls.properties";	
			
			PrintWriter out2 = new PrintWriter(txtFilePath); 
			
			while( paramNames.hasMoreElements()) {
				
				d.add((String)paramNames.nextElement());
				
				dd.add(i + "\r\n");
				
				i++;
			}

			// for gc
			i = 0;
			d = null;

			
			
			for (int j = 0; j < dd.size(); j = j + 2)
			{
			
			String value = request.getParameter(dd.get(j));
			
			String type = request.getParameter(dd.get(j+1));
			
			
			String ip = "";
			
			if (value != null) {
				
				
				try {
					
					InetAddress address = InetAddress.getByName(value);
					
					ip = address.getHostAddress();
					
				} catch (UnknownHostException e) {
				
					e.printStackTrace();
				}
			
			}
			

			if (type != null) {

				if ((type.equalsIgnoreCase("roleKI"))) {
					
					type = ki;
					
					for (int k = 0; k < portsKI.size(); k++) {
					
						String url = "Balancer." +type+"."+value+"-"+portsKI.get(k)+"=https://"+ip+":"+portsKI.get(k)+"";
						
						out2.println(url);
					}
					
				} else if (type.equalsIgnoreCase("roleUnder")) {
					
					type = under;
					for (int g = 0; g < portsUnder.size(); g++) {
						
						String url = "Balancer." +type+"."+value+"-"+portsUnder.get(g)+"=https://"+ip+":"+portsUnder.get(g)+"";
						
						out2.println(url);
					}
					
				}
			
			}
			
				
			}
			
			out2.close();
			
	
         }


		
        %>

	<form id="formUrls" method="POST" action="/balancerUrls/index.jsp">
		<%

            String jspPath = session.getServletContext().getRealPath("../../conf");
            String txtFilePath = jspPath+ "/servers.properties";
            BufferedReader reader = new BufferedReader(new FileReader(txtFilePath));
            ArrayList<String> data = new ArrayList<String>();
            String line;

            while((line = reader.readLine())!= null){

				if (line.length() > 0 )	data.add(line);
            }
	
        %>

		<% int j = 0; %>
		<%	for (int i = 0; i < data.size(); i++) { %>


		<div>

			<input id="server" class="input-sm" type="text" name="<%out.println(j);%>"
				value="<% out.println(data.get(i)); %>" size=10 />

			<% j++; %>

			<input class="role input-sm" type="text" name="<%out.println(j);%>"
				value="" size=10 />

			<% j++; %>

			<span class="labels">KI</span><input id="roleKI" class="check" type="checkbox" />
			<span class="labels">Under</span><input	id="roleUnder" class="check" type="checkbox" />
			

		</div>
		<br />

		<%}%>

		<div style="width: 385px;">
			<button class="btn btn-lg btn-primary btn-block" type="submit">Generating
				Urls</button>
		</div>
	</form>

	<script>
	
	
		$(".check").on("click", function() {

			var flag_check = $(this).attr("checked");
			
			var role = $(this).parent().find(".role");
			
			if (flag_check == "checked") {
				
				$(this).removeAttr("checked");
				$(role).attr("value", "");
				
			} else {
				
				var id = $(this).attr("id");

				$(this).attr("checked", "checked");
				
				if (id == "roleKI") {
					
					$(this).parent().find("#roleUnder").removeAttr("checked");
					
					/*if (($(this).parent().find("#remove").attr("checked")=="checked")&& id != "remove") {
						
						$(this).parent().find("#remove").removeAttr("checked");
					}*/
					
				} else {
					
					$(this).parent().find("#roleKI").removeAttr("checked");
					
					
					/*if (($(this).parent().find("#remove").attr("checked")=="checked") && id != "remove" ) {
						
						$(this).parent().find("#remove").removeAttr("checked");
						
					}
					
					if (id == "remove") {
						
						$(this).parent().find("#roleUnder").removeAttr("checked");
						
					}*/
					
					
				}
				
				$(role).attr("value", "");
				
				$(this).parent().find(".role").attr("value",$(this).attr("id"));
				
				
				
				
			}

		});
	</script>

</body>
</html>
