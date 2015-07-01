# Amateras Modeler

Lightweight UML and ER-diagram editor for Eclipse. Former AmaterasUML and AmaterasERD.

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
### 2012/7/16 - AmaterasUML 1.3.4

- Fixed the issue on Java7.

### 2012/06/13 - AmaterasERD 1.0.9

- Displays unique indices on the diagram.
- Switching display mode (logical or physical) by CTRL+D.
- Improvement of PostgreSQLDialect, MySQLDialect and OracleDialect.
- Refreshing imported tables.
-The dictionary which is used for name conversion became customizable.

See details [here](http://osdn.jp/projects/amateras/wiki/AmaterasERD_1_0_9) about new features.

### 2011/8/13 - AmaterasUML 1.3.3

- Improvement of constructor in the class diagram
- Improvement of Java generics support
- Quick Filter ("Show only Public" and "Show All")
- Refresh the class diagram from Java source
- Copy the diagram as image

### 2011/02/12 - AmaterasERD 1.0.8

- Sybase support
- In importing and exporting, table and column comments are mapped to logical name (Oracle and Sybase only)
- Copy As Image from the context menu
- Quick Outline ([CTRL]+[O])
- Diagram font configuration
- HTML generation tool from *.erd file which could be used in command-line

See details [here](http://osdn.jp/projects/amateras/wiki/AmaterasERD_1_0_8) about new features.

### 2010/01/03 - AmaterasERD 1.0.7

- H2 Support
- Switching dialect after creating a diagram
- Improved foreign key creation behavior
- Improved HTML report
- Convert table / column name from context menu
- Show NOT NULL constraint in the diagram
- Show and snap to Grid, snap to other figure
- SQL Highlighting

See details [here](http://osdn.jp/projects/amateras/wiki/AmaterasERD_1_0_7) about new features.

### 2009/05/05 - AmaterasERD 1.0.6

- Note is available on the diagram
- Additional SQL (such as inserting initial data) of each table
- Auto diagram reloading when the file is updated such as SVN update

See details [here](http://osdn.jp/projects/amateras/wiki/AmaterasERD_1_0_6) about new features.

### 2009/04/19 - AmaterasUML 1.3.2

- New appearance settings of class diagram / sequence diagram
- Auto diagram reloading when the file is updated such as SVN update

### 2008/12/10 - AmaterasERD 1.0.5

- Show selected tables DDL from the context menu
- Table background color has been configurable
- Incremental search in the JDBC importing wizard

See details [here](http://osdn.jp/projects/amateras/wiki/AmaterasERD_1_0_5) about new features.

### 2008/9/3 - AmaterasERD 1.0.4.1

- MySQL reverse engineering issue has been fixed.

### 2008/8/21 - AmaterasERD 1.0.4

- SQL comment generation in DDL
- CREATE TABLE with SCHEMA option
- Diagram Validation
- Linked Table
- Table copy and paste

See details [here](http://osdn.jp/projects/amateras/wiki/AmaterasERD_1_0_4) about new features.

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

See details [here](http://osdn.jp/projects/amateras/wiki/AmaterasERD_1_0_3) about new features.

### 2008/02/14 - AmaterasUML 1.3.1

- Auto layout in the class diagram editor.
- Java model importing supports aggregation.

### 2008/2/7 - AmaterasERD 1.0.2

- New actions are available at the ER diagram context menu
  - Save as Image and Print
  - Export table definition as HTML
  - Auto layout
- Table definition dialog improved
  - Table / Column description
  - Auto increment column
- New extension point (net.java.amateras.db.generators)

See details [here](http://osdn.jp/projects/amateras/wiki/AmaterasERD_1_0_2) about new features.

### 2007/10/07 - AmaterasUML 1.3.0

- Eclipse 3.3 Support (This version can't work with Eclipse 3.2.x)
- Copy & Pase in the class diagram, the usecase diagram and the activity diagram.
- Brand new visual theme for diagrams.

### 2007/7/19 - AmaterasERD 1.0.1

- Apache Derby support
- Logical database design support

### 2007/04/22 - AmaterasUML 1.2.2

- Activity diagram is available.
- Connection routers which exclude BendpoinConnectionRouter are not supported.
- Bendpoint moves according to the entity dragging.
- Note: This version hasn't compatibility with old versions.

### 2006/12/31 - AmaterasERD 1.0.0

- Initial Release

### 2006/8/31 - AmaterasUML 1.2.1

- Class Diagram
  - Enable to drag two or more classes at a time.
  - Enable auto connect to Generalization/Realization connection.
  - Enable to drop the classes in jar entry.
- Extension
  - XMI import/export extension is available.

### 2006/6/10 - AmaterasUML 1.2.0

- Common
  - Enable to switch show/hide diagram grid.
- Class Diagram
  - Enable to hide fields/methods by context menu.
- Usecase Diagram
  - New feature

### 2006/5/13 - AmaterasUML 1.1.2

- Common
  - Enable to hide icons.
  - Enable to change background/foreground color.
- Class Diagram
  - Enable to align diagram element.
  - Add new connection router.
- Sequence Diagram
  - Change presentation of instance.
  - Add Sequence API and sample plugin.
-Java support
  - Enable to open Java editor from class diagram.
- Limitation
  - Sorry, Sequence diagram editor can't edit *.sqd files created by older version. 

### 2006/4/22 - AmaterasUML 1.1.1

- Add drag-and-drop
  - both class and sequence
  - drap Java file in package exploler view.
- Enable to delete and create Return message.
  - Auto return message creation swtich in AmaterasUML preference
- Add code compiletion feature to edit message.

### 2006/4/9 - AmaterasUML 1.1.0

- Add sequence diagram editor.
- Add Note for class diagram editor

### 2005/12/31 - AmaterasUML 1.0.0

- This is the initial public release.
