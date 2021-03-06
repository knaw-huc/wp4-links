# wp4-links

Code for linking all Dutch civil registries. It's purpose, use case, technical features and limitations are described in the <a href="https://github.com/CLARIAH/wp4-links/blob/master/documentation.md">documentation</a>.


## How does it work?
1. Convert all CSV files to RDF according to our simple [Civil Registries schema](assets/LINKS-schema.png), based on Schema.org and BIO vocabularies. You can browse the [RDF file](assets/LINKS-schema.ttl) in any triple store (e.g. Druid) or Ontology editor (e.g. Protégé).
  - We use COW for converting the CSV files to RDF, but any other method would also work as long as the data in RDF are modelled according to the  [Civil Registries schema](assets/LINKS-schema.png).

2. Merge all resulting RDF files into one larger file:
  - ```cat birth-registrations.nq birth-persons.nq marriage-registrations.nq marriage-persons.nq > all-civil-registries.nq```

3. Install JAVA Runtime Environment (JRE), which is almost installed in every computer these days
  - Free [download](https://www.oracle.com/java/technologies/javase-jre8-downloads.html) from Oracle.

4. Convert the large RDF file to HDT (this process might require a lot of memory usage depending on the dataset size).
  - ```nohup rdf2hdt all-civil-registries.nq all-civil-registries.hdt -i -p &``` (-i for also creating the index to speed-up the reading)
  - Or using the function `convertToHDT` in the linking tool

5. Start linking from terminal :)
  - ```java -jar links.jar```

## Need some help regarding the input/output of the tool?
You can open your terminal, and ask our tool for help:

  ```java -jar links.jar --help```

## Docker setup

An _wp4-links_ docker image can be created from a checkout or directly from github:

  ```docker build -t wp4-links https://raw.githubusercontent.com/knaw-huc/wp4-links/master/Dockerfile```
  
The docker image supports the complete pipeline as mentioned above, starting from the CSV files `registrations.csv` and `persons.csv` which it expects in a `CSV` subdirectory of your dataset, e.g. `/path-to/my-dataset/CSV`. To start linking:

  ```docker -m 16GB run -v /data:/path-to wp4-links my-dataset -function between_m_m --maxLev 1```
  
 It will put the result of the RDF and HDT conversions into the `RDF` subdirectory, e.g. `/path-to/my-dataset/RDF`. The result directories and a log file  are created next to the `CSV` and `RDF` subdirectories. Running time and memory consumption depend on the input size, but creating the HDT index takes quite some time and memory (tune the `-m 16g` as needed).
