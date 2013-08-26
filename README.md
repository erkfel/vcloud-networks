vcloud-networks
===============

vCloud Networks Search

This project is the sample of using the VMWare SDK for search available vCloud network in the specified datacanter. 
The project represent the working application with web UI based on Bootstrap.

The intention of this project is to share the example of real usage of VMWare SDK for specific needs in Scala project, 
you can take it, improve and use for your specific needs. 

_Setup the project_

You need maven with version > 2.1 and donwload the VMWare SDK v5.1.2 from https://my.vmware.com/web/vmware/details?downloadGroup=VCL-VCD512&productId=289&rPId=3722.
Install the folllwing dependencier to youl local maven repository (or Nexus):

            <dependency>
                <groupId>com.vmware.vcloud.sdk</groupId>
                <artifactId>vcloud-java-sdk</artifactId>
                <version>5.1.0</version>
            </dependency>
            <dependency>
                <groupId>com.vmware.vcloud.api.rest</groupId>
                <artifactId>rest-api-schemas</artifactId>
                <version>5.1.0</version>
            </dependency>

_Configure the credentials to vCloud organizations_ 

	core/src/main/resources/application.conf

_Build the project_

	mvn package

_Run the application_ 

	cd web
	mvn jetty:run 
