JBoss Data Grid Example Storing XML in the Cache (Library Mode)
=========================================

To build this project use

    mvn install

To run the project simply deploy the war to JBoss. You can optional install the JBoss Plugin.

The cache loads sample music files from the data directory. Each entry in the cache represents a single XML files.

The servlet here http://localhost:8080/jdg-xml-cache-store-lm/getComposers loads all the entries in the cache and uses XPATH to print the individual composer (if found).
The servlet here http://localhost:8080/jdg-xml-cache-store-lm/getXML will load an XML entry to the browser.
