# Android Sqlite3 SQL Template
This is a TINY SIMPLE sql template framework for android sqlite3 like Mybatis.

## Features:
1. All sqls are written in sql.xml files.
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

// query the "selectByCond" sql
```
                SurveyDAO surveyDAO = new SurveyDAO(context);
                Survey query = new Survey();
                query.setId(1);
                query.setLineno("10");
                surveyDAO.withOpenDb(()->{
                    List<Survey> surveys = surveyDAO.selectByCond(query);
                    // do somethine meaningful here...
                    
                });
```
In this example, #{pointno} is null, and then the and condition will be dropped, so the final sql executed by sqlite3 will be:
'''
select * from record
where id = 1
and line_no = '10'
'''

## Usage:
### STEP 1: 
Copy sql.xml to android project's assets/ directory.
### STEP 2: 
Define XxxDao extends AbstractDAO class like SurveyDAO.
### STEP 3: 
Instaniate XxxDao and use .selectByCond() method or other methods to excute sql.
And cursor data will be auto converted to List of bean by java reflection.
