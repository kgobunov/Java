<%@page import="java.net.URL"%>
<%@page import="java.net.HttpURLConnection"%>
<%@page import="java.io.IOException"%>
<%@page import="java.io.InputStreamReader"%>
<%@page import="java.io.DataOutputStream"%>
<%@page import="java.io.BufferedReader"%>
<%@page import="javax.xml.stream.XMLStreamException"%>
<%@page import="org.apache.axiom.om.OMElement"%>
<%@page import="org.apache.axiom.om.util.AXIOMUtil"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.ArrayList"%>
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
        <title>BlackList</title>
    </head>
    <body >
	
	
	<div id="wrapper">
		<h2>Black list</h2>
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
                   </div>
                <!--
                <br class="separator" />
                -->
                <br class="separator" />
            </div>
          
	
	</div>
	
	
	
     <% 

		ArrayList<String> data = new ArrayList<String>();
	
		URL url = new URL("http://10.78.214.225:8080/TSM_Balancer_2.0/services/TSMBalancer");

		String soapMessage = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"><soapenv:Header/>   <soapenv:Body/></soapenv:Envelope>";

		HttpURLConnection connection = (HttpURLConnection) url.openConnection();

		connection.setRequestMethod("POST");
		connection.setDoOutput(true);
		connection.setRequestProperty("Content-Type", "text/xml;charset=UTF-8");
		connection.setRequestProperty("SOAPAction",
				"http://www.example.org/TSMBalancer/showBlackList");

		DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
		
		wr.write(soapMessage.getBytes("UTF-8"));
		
		wr.flush();

		wr.close();

		BufferedReader bf = new BufferedReader(new InputStreamReader(
				connection.getInputStream()));

		String inputLine;

		StringBuffer resp = new StringBuffer();

		while ((inputLine = bf.readLine()) != null) {

			resp.append(inputLine);

		}

		String respStr = resp.toString();
		
		OMElement responseOME = null;
		
		try {
			
			responseOME = AXIOMUtil.stringToOM(respStr);
			
		} catch (XMLStreamException e) {
		
			
			e.printStackTrace();
		}

		@SuppressWarnings("rawtypes")
		Iterator  iter = responseOME.getChildrenWithLocalName("Body");

		if (iter.hasNext()) {

			OMElement body = (OMElement) iter.next();

			iter = body.getChildrenWithLocalName("Servers");

			if (iter.hasNext()) {

				OMElement servers = (OMElement) iter.next();

				iter = servers.getChildrenWithLocalName("server");
				
				if (iter.hasNext()) {
				
					
				
					while (iter.hasNext()) {

						OMElement serverTag = (OMElement) iter.next();

						data.add(((OMElement) serverTag.getChildrenWithLocalName("name").next()).getText());

						data.add(((OMElement) serverTag.getChildrenWithLocalName("errorCode").next()).getText());

					} 
				
				} else {
				
					out.println(((OMElement) servers.getChildrenWithLocalName("notFound").next()).getText());
					
				}

			} 

		} 

		
		
        %> 
		
	<div id = "container">
	
	<% if (data.size() > 0) { %>
	
		<table id ="badurls" border = "1" >
		<thead>
		<th>Server</th>
		<th>Reason</th>
		<thead>
		<tbody>
		<% for(int i = 0; i < data.size(); i+=2) { %>
		
			<tr>
				<td><% out.println(data.get(i)); %></td>
				<td><% out.println(data.get(i+1)); %></td>
			</tr>
			
		<% } %>
		</tbody>
		<table>
	
	<% } %>
		
	</div>
	
	
    </body>
</html>
