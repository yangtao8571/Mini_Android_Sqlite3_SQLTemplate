# Mini_Android_Sqlite3_SQLTemplate
This is TINY SIMPLE yet another Sql template framework for android sqlite3 database accessing.

## Features:
1. All sqls are written in sql.xml template files, no need write hard-code sql strings in java files.
2. Auto inject parameter into sql template.
3. Auto parsing sql, auto remove syntax-error sql clause.

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
Define the sql.xml android project assets/ directory.
### STEP 2: 
Define XxxDao extends AbstractDAO class.
### STEP 3: 
Instaniate XxxDao and use .selectByCond() method or other methods to excute sql.
And cursor data will be auto converted to List of bean by java reflection.

