# Java


CKPIT, CRM, ESB, SBOL, FSB  - JMS applications (WebSphere MQ provider implementation). 

# Balancer, COD

Web-services (based on AXIS2). 

Balancer checks available url from list and return to client, can adds bad url to blacklist. 
COD example of an external system.



# ESB

Enterprise system bus, all systems (exclude CKPIT) and web-services like COD interacting with target system through it.

#CKPIT

Send 1100 messages every 15 minutes. (time can be changed in config file)

#CRM, SBOL, FSB

Send messages with defined intensity. 

All applications have two mode supply load - stable and step. All settings set in xml- config file which validated at startup



# LoadApllication

Load application, use for load testing. Load one web-service (SOAP protocol) and system interacting through queues (JMS - implementation Websphere MQ). Application has two mode supply laod - stable and step and support multithreading mode for every system. All statistics saved to database. All settings set in xml-config file which validated at startup.

# GetResultLoadApp

Application for getting result after load test. Export information to excel file.

# ClearMqQueues

Application for cleaning queues after load test through pcf agents.

# JSP - support tools. (under tomcat7):

	1) balancerUrls - web interface for generating url for balancer under definded format

	2) Config_balancer - web interface for configurating balancer (check/uncheck urls, edit , etc)

	3) showBlackList - show urls from balancer's blacklist

	4) configurator - web interface for configurating web services like COD



