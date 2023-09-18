### Softwares ###

* Any latest eclipse or STS
* JDK 1.8 and above
* Maven 3.6.0 and above
* Tomcat 8 and above

### Setup Process ###

* See video ("configure_deploy_pdfprocessor.mp4") inside docs/ folder to setup project inside eclipse or STS 

### Endpoints and Parameters ###

* http://HOSTNAME:PORT/api/process
* See parameter explanation sheet("Parameter_Explanation.xlsx") inside docs/ folder

### Deployment Process ###

* Build project using mvn clean install command, it will generate war file inside target folder
* Put that jar inside tomcat webapps folder and start tomcat