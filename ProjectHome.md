The title of this project (urp) stands for UML Representation Pipeline.

We define and implement transformation rules that translate data models expressed in a restricted subset of UML2 to physical representations such as XML schemas & documents, relational database schema and Java codes.

The generated Java code uses JPA and JAXB annotations to translate between the XML and relational database representations to define a simple CRUD interface (get, upload, validate) for a complete graph of objects.

Required libraries :
- JAXB 2.1.x
- JPA 1.0 provider = EclipseLink 1.1.x

This project is a spin-off of the SimDB project in the volute project vvolute.googlecode.com).

Job Runner is a sub project that allows a web application to launch external jobs (executable) and manages both a dedicated thread pool and a job queue to monitor the jobs.