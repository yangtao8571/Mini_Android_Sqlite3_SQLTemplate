# Android Sqlite3 SQL Template
This is a TINY sql template library for android database(e.g. sqlite3) accessing like Mybatis but more simpler.

## Features:
1. All sqls are moved into sql.xml files. Java codes are clean.
2. Auto inject parameter into sql template.
3. Auto correcting sql statment, parsing sql ast-tree instead of using template language.

## Example:
// define sql.xml
```
    <sql id="selectByCond" >
        select * from record
        where id = #{id}
        and line_no = '#{lineno}'
        and station_no = '#{pointno}'
    </sql>
```

// query the "selectByCond" sql in java
```
    SurveyDAO surveyDAO = new SurveyDAO(context);
    Survey query = new Survey(); // Survey is an entity class
    query.setId(1);
    query.setLineno("10");
    surveyDAO.withOpenDb(()->{
        List<Survey> surveys = surveyDAO.selectByCond(query);
        // do something meaningful here...
    });
```
In this example, the query parameter #{pointno} is null, and then the "and station_no = '#{pointno}'" condition will be removed, so the final sql executed by sqlite3 will be:
```
select * from record
where id = 1
and line_no = '10'
```

## Usage:
### STEP 1: 
Copy sql.xml to android project's assets/ directory.
### STEP 2: 
Define XxxDao extends AbstractDAO class like SurveyDAO.
### STEP 3: 
Instaniate XxxDao and use .selectByCond() method or other methods to excute sql.
And cursor data will be auto converted to List of bean by java reflection.

## Note:
1. Complex sql not support yet (e.g. Inner join and outer join).
2. Sql-injecting is not considered.

## Contributions:
All kinds of contributeions are weeelcom.
