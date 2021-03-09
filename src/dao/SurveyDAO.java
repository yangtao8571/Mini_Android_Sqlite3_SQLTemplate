package dao;

import android.content.Context;


public class SurveyDAO extends AbstractDAO<Survey> {

    public SurveyDAO(Context context) {
        super(context);
    }

    @Override
    public String getTableName(){ return "record";}
    @Override
    public String getDbFileName(){ return "survey.db";}
    @Override
    public String getSqlFileName(){ return "sql_survey.xml";}
    @Override
    public String getCreateTableSql(){ return getSql("create_table_record");}
    @Override
    public Class getEntityClass() { return Survey.class;}

}
