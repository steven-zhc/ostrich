# Ostrich

A big and strong bird, but it could be running faster.

A simple cassandra util using DAO and Template pattern.

## Quickstart
     git clone git@dev-git.fuhu.org:um-dev/ostrich.git
     cd ostrich
     gradle clean build

The build requires a Java 8 JDK as JAVA_HOME, but will ensure Java 8 compatibility.

## Example

```Java
String cql = "select * from employee where id = ? and dept = ?";

List<Event> list = template.queryForList(cql, new EmployeeRowMapper(), new Long(1), "IT");

```

Implement an EmployeeRowMapper

```Java
class EmployeeRowMapper implements RowMapper<Empolyee> {

    @Override
    public Event mapRow(Row row, int rowNum) {
        Employee e = new Employee();
        e.setId(row.getLong("id"));
        e.setName(row.getString("name"));
        e.setDepartment(row.getString("dept"));
        return e;
    }
}
```

 
## Resources
Ostrich uses Gradle as its build tool. See the Gradle Primer section below if you are new to Gradle.


## Gradle primer
This section describes some of the basics developers and contributors new to Gradle might 
need to know to get productive quickly.  The Gradle documentation is very well done; 2 in 
particular that are indispensable:

* [Gradle User Guide](http://gradle.org/docs/current/userguide/userguide_single.html) is a typical user guide in that
it follows a topical approach to describing all of the capabilities of Gradle.
* [Gradle DSL Guide](http://gradle.org/docs/current/dsl/index.html) is quite unique and excellent in quickyl
getting up to speed on certain aspects of Gradle.

Using the Gradle Wrapper
------------------------

For contributors who do not otherwise use Gradle and do not want to install it, Gradle offers a very cool
features called the wrapper.  It lets you run Gradle builds without a previously installed Gradle distro in 
a zero-conf manner.  Ostrich configures the Gradle wrapper for you.  If you would rather use the wrapper and 
not install Gradle (or to make sure you use the version of Gradle intended for older builds) you would just use
the command `gradlew` (or `gradlew.bat`) rather than `gradle` (or `gradle.bat`) in the following discussions.  
Note that `gradlew` is only available in the project's root dir, so depending on your `pwd` you may need to adjust 
the path to `gradlew` as well.

Executing Tasks
---------------

Gradle uses the concept of build tasks (equivalent to Ant targets or Maven phases/goals). You can get a list of
available tasks via 

    gradle tasks

To execute a task across all modules, simply perform that task from the root directory.  Gradle will visit each
sub-project and execute that task if the sub-project defines it.  To execute a task in a specific module you can 
either:

1. `cd` into that module directory and execute the task
2. name the "task path".  For example, in order to run the tests for the _sub_ module from the 
root directory you could say `gradle sub-module:test`
