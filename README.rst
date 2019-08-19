Simple SOAP client
##################

Simple, lightweight SOAP client implemented in Java.

|travis_ci| |codecov|

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


-----


.. |travis_ci| image:: https://travis-ci.org/yevgenykuz/simple-soap-client.svg?branch=master
    :target: https://travis-ci.org/yevgenykuz/simple-soap-client
    :alt: Travis CI

.. |codecov| image:: https://codecov.io/gh/yevgenykuz/simple-soap-client/branch/master/graph/badge.svg
    :target: https://codecov.io/gh/yevgenykuz/simple-soap-client/branch/master
    :alt: Test coverage
