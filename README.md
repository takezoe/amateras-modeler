# Amateras Modeler

Lightweight UML and ER-diagram editor for Eclipse. Former AmaterasUML and AmaterasERD.

It provides following features:

- UML
  - Class diagram graphical editing
  - Sequence diagram graphical editing
  - Activity diagram graphical editing
  - Import and export java code
- ER
  - ER diagram  graphical editing
  - Import tables from existing database
  - Generate DDL from diagram
  - Export table definition as HTML

Installation
--------
Install from the update site: http://takezoe.github.io/amateras-update-site/

License
--------
[Eclipse Public License 1.0](http://opensource.org/licenses/eclipse-1.0.php)

History
--------
### 2012/06/13 - AmaterasERD 1.0.9

- Displays unique indices on the diagram.
- Switching display mode (logical or physical) by CTRL+D.
- Improvement of PostgreSQLDialect, MySQLDialect and OracleDialect.
- Refreshing imported tables.
-The dictionary which is used for name conversion became customizable.

See details here about new features.

### 2011/02/12 - AmaterasERD 1.0.8

- Sybase support
- In importing and exporting, table and column comments are mapped to logical name (Oracle and Sybase only)
- Copy As Image from the context menu
- Quick Outline ([CTRL]+[O])
- Diagram font configuration
- HTML generation tool from *.erd file which could be used in command-line

See details here about new features.

### 2010/01/03 - AmaterasERD 1.0.7

- H2 Support
- Switching dialect after creating a diagram
- Improved foreign key creation behavior
- Improved HTML report
- Convert table / column name from context menu
- Show NOT NULL constraint in the diagram
- Show and snap to Grid, snap to other figure
- SQL Highlighting

See details here about new features.

### 2009/05/05 - AmaterasERD 1.0.6

- Note is available on the diagram
- Additional SQL (such as inserting initial data) of each table
- Auto diagram reloading when the file is updated such as SVN update

See details here about new features.

### 2008/12/10 - AmaterasERD 1.0.5

- Show selected tables DDL from the context menu
- Table background color has been configurable
- Incremental search in the JDBC importing wizard

See details here about new features.

### 2008/9/3 - AmaterasERD 1.0.4.1

- MySQL reverse engineering issue has been fixed.

### 2008/8/21 - AmaterasERD 1.0.4

- SQL comment generation in DDL
- CREATE TABLE with SCHEMA option
- Diagram Validation
- Linked Table
- Table copy and paste

See details here about new features.

### 2008/6/7 - AmaterasERD 1.0.3

- PostgreSQL and Oracle support
- Table re-importing improved
- Foreign keys decoration in the diagram
- Default column value support
- Outline view
- New DDL generate option to generate constraints as ALTER TABLE
- Index designing support
- Domain (Generic data type definition) support
- HTML report imprived

See details here about new features.

### 2008/2/7 - AmaterasERD 1.0.2

- New actions are available at the ER diagram context menu
  - Save as Image and Print
  - Export table definition as HTML
  - Auto layout
- Table definition dialog improved
  - Table / Column description
  - Auto increment column
- New extension point (net.java.amateras.db.generators)

See details here about new features.

### 2007/7/19 - AmaterasERD 1.0.1

- Apache Derby support
- Logical database design support

### 2006/12/31 - AmaterasERD 1.0.0

- Initial Release
