This folder contains a work in progress version of the DMN 1.1 schema.

Java bindings have been generated from this schema using: 
```
    xjc -d src/main/java \
        -p io.onedecision.engine.decisions.model.dmn \
        -b  src/main/resources/schema/bindings.xjb \
        src/main/resources/schema/dmn.xsd
```  


To generate Builder pattern: 
```
    mvn -P jaxb org.jvnet.jaxb2.maven2:maven-jaxb2-plugin:generate
```