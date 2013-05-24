optiq-csv
============

Optiq adapter that reads <a href="http://en.wikipedia.org/wiki/Comma-separated_values">CSV</a> files.

Optiq-csv is a nice simple example of how to connect Optiq to your own
data source and quickly get a full SQL/JDBC interface.

Download and build
==================

You need Java (1.5 or higher; 1.7 preferred) and maven (2 or higher).

    $ git clone git://github.com/julianhyde/optiq-csv.git
    $ cd optiq-csv
    $ mvn compile

Run sqlline
===========

    $ ./sqlline
    sqlline> !connect jdbc:optiq:model=target/test-classes/model.json admin admin
    sqlline> select * from emp;
    sqlline> !tables
    sqlline> !quit


Advanced use
============

You can also register a CsvSchema as a schema within an Optiq instance.
Then you can combine with other data sources.

You can write a "vanity JDBC driver" with a different name.

You can add optimizer rules and new implementations of relational
operators to execute queries more efficiently.

More information
================

* License: Apache License, Version 2.0.
* Author: Julian Hyde
* Blog: http://julianhyde.blogspot.com
* Project page: http://www.hydromatic.net/optiq-csv
* Source code: http://github.com/julianhyde/optiq-csv
* Developers list: http://groups.google.com/group/optiq-dev

