# Java
Java projects:

Balancer and COD - web-services (based on AXIS2). Balancer checks available url from list and return to client, 

CKPIT, CRM, ESB, SBOL, FSB  - JMS applications (WebSphere MQ provider implementation): 

ESB - enterprise system bus , all systems (exclude CKPIT) interacting with target system through it.

CKPIT send 1100 messages every 15 minutes.

CRM, SBOL , FSB - send messages with defined intensity. 

All applications have two mode supply load - stable and step. All settings set in xml- config file which validated at startup



LoadApllication - load application , use for load testing. Load one web-service (SOAP protocol) and system interacting through queues (JMS - implementation Websphere MQ). Application has two mode supply laod - stable and step and support multithreading mode for every system. All statistics saved to database. All settings set in xml-config file which validated at startup.

GetResultLoadApp - application for getting result after load test. Export information to excel file.

ClearMqQueues - application for cleaning queues after load test through pcf agents.

JSP - support tools. (under tomcat7):

balancerUrls - web interface for generating url for balancer under definded format

Config_balancer - web interface for configurating balancer (check/uncheck urls, edit , etc)

showBlackList - show urls from balancer's blacklist

configurator - web interface for configurating web services like COD



