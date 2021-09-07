Simple SOAP client
##################

Simple, lightweight SOAP client implemented in Java.

|mc| |ci| |codecov| |CodeQL|

-----

.. contents::

.. section-numbering::

Features
========

* **Send SOAP requests** - Sends a SOAP request XML and returns the response as string
* **Basic XML utilities** - Basic XML parsing included to parse the response and extract values
* **Simple and lightweight** - No dependencies needed, uses Java's HttpURLConnection to handle HTTP, and org.w3c.dom, org.xml.sax and javax.xml packages to handle XML parsing

Usage
=====

.. code-block:: java

    // Create a client for a specific SOAP operation
    SimpleSoapClient client = new SimpleSoapClientImpl("http://www.dneonline.com/calculator", "http://tempuri.org",
            "Add");

    // Send a request XML file (the service you wish to use should provide a service description where you can get
    // a template XML, and fill the parameters you need), and get the response XML as string:
    String response = client.sendSoapRequest(new File("src/test/resources/requestExample.xml"));

    // Use the provided XmlUtilities to parse the response string and get the text value of a field
    String textContent = XmlUtilities.getTextContentOfXmlElement(XmlUtilities.xmlStringToDocument(response),
            "AddResult")

Meta
====

Authors
-------

`yevgenykuz <https://github.com/yevgenykuz>`_

License
-------

`MIT License <https://github.com/yevgenykuz//simple-soap-client/blob/master/LICENSE>`_

Release configuration
---------------------

To publish artifacts to central maven repository via Sonatype OSSRH, do the following:

* Create a maven master password:

.. code-block:: bash

    mvn --encrypt-master-password
    # Enter password to encrypt when prompted
    # Save encrypted password in /.m2/settings-security.xml:
    <settingsSecurity>
      <master>ENCRYPTED_MASTER_PASSWORD</master>
    </settingsSecurity>

* Configure maven to publish when the ``ossrh-release`` profile is used:

.. code-block:: bash

    # Encrypt passwords for OSSRH website and for your GPG key:
    mvn --encrypt-password
    # Enter password to encrypt when prompted, encrypted password will be printed to stdout
    # Get public GPG key:
    gpg --list-keys --keyid-format LONG
    # Copy the key ID from: "pub   rsa4096/<GPG_KEY_ID>"
    # Add configuration in /.m2/settings.xml:
    <?xml version="1.0" encoding="UTF-8"?>
    <settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
      xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
      xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 https://maven.apache.org/xsd/settings-1.0.0.xsd">
      <servers>
        <server>
          <id>ossrh</id>
          <username>OSSRH_WEBSITE_USER</username>
          <password>OSSRH_WEBSITE_PASS</password>
        </server>
        <server>
          <id>GPG_KEY_ID</id>
          <passphrase>GPG_KEY_PASS</passphrase>
        </server>
      </servers>
      <profiles>
        <profile>
          <id>ossrh-release</id>
          <activation>
            <activeByDefault>false</activeByDefault>
          </activation>
          <properties>
            <gpg.keyname>GPG_KEY_ID</gpg.keyname>
          </properties>
        </profile>
      </profiles>
    </settings>

-----

.. |mc| image:: https://img.shields.io/maven-central/v/com.yevgenyk.simplesoapclient/simple-soap-client.svg?label=Maven%20Central
    :target: https://search.maven.org/search?q=g:%22com.yevgenyk.simplesoapclient%22%20AND%20a:%22simple-soap-client%22
    :alt: Maven Central

.. |ci| image:: https://github.com/yevgenykuz/simple-soap-client/workflows/CI/badge.svg
    :target: https://github.com/yevgenykuz/simple-soap-client/actions?query=workflow%3ACI
    :alt: CI

.. |codecov| image:: https://codecov.io/gh/yevgenykuz/simple-soap-client/branch/master/graph/badge.svg
    :target: https://codecov.io/gh/yevgenykuz/simple-soap-client/branch/master
    :alt: Test coverage

.. |CodeQL| image:: https://github.com/yevgenykuz/simple-soap-client/workflows/CodeQL/badge.svg
    :target: https://github.com/yevgenykuz/simple-soap-client/actions?query=workflow%3ACodeQL
    :alt: CodeQL
