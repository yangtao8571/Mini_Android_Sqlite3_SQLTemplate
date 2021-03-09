package lh.com.lib_common.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.camera2.TotalCaptureResult;
import android.util.LruCache;

import com.orhanobut.logger.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import lh.com.lib_common.dao.core.SqlXmlParser;
import lh.com.lib_common.dao.core.Token;
import lh.com.lib_common.dao.core.TokenParser;
import lh.com.lib_common.dao.core.TypeConverter;
import lh.com.lib_common.utils.ReflectionUtil;

public class SqlHelper {

    private Context _context;
    private String _sqlFilename;
    private LruCache<String, String> _sqlCache;
    private LruCache<String, List<Token>> _tokensCache;

    public SqlHelper(Context context, String sqlFilename) {
        _context = context;
        _sqlFilename = sqlFilename;
        _sqlCache = new LruCache(100);
        _tokensCache = new LruCache(100);
    }

    /**
     * 获取sql语句
     *
     * @param sqlid
     * @return
     */
    public String getSql(String sqlid) {
        String cached = _sqlCache.get(sqlid);
        if (cached !=null) {
            return cached;
        }

        InputStream is = null;
        try {
            is = _context.getAssets().open(_sqlFilename);
            Map<String, String> sqlMap = SqlXmlParser.readXmlByDOM(is);
            for (String k : sqlMap.keySet()) {
                _sqlCache.put(k, sqlMap.get(k));
            }
            return sqlMap.get(sqlid);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                }
            }
        }
        return "";
    }

    /**
     * 获取符号，内部使用
     *
     * @param sqlid
     * @return
     */
    public List<Token> getTokens(String sqlid) {
        List<Token> cached = _tokensCache.get(sqlid);
        if (cached != null) {
            return cached;
        }

        String sql = getSql(sqlid);
        List<Token> tokens = TokenParser.parse(sql);
        List<Token> advTokens = TokenParser.advParse(tokens, 0, tokens.size());
        _tokensCache.put(sqlid, advTokens);
        return advTokens;
    }

    /**
     * 获取参数替换后的sql
     *
     * @param sqlid
     * @param param     key 必须为大写
     * @return
     */
    public String getParsedSql(String sqlid, Map<String, Object> param) {
        List<Token> advTokens = getTokens(sqlid);
        String newSql = TokenParser.dumpSql(advTokens, param);
        Logger.d("====== DUMP NEW SQL ======");
        Logger.d(newSql);
        return newSql;
    }

    /**
     * 获取参数替换后的sql
     *
     * @param sqlid
     * @param param
     * @return
     */
    public String getParsedSql(String sqlid, Object param) {
        Map<String, Object> paramMap = ReflectionUtil.object2Map(param);
        return getParsedSql(sqlid, paramMap);
    }

    /**
     *
     * @param sql
     * @param param
     * @return
     */
    public static String dumpSql(String sql, Map<String, Object> param) {
        List<Token> tokens = TokenParser.parse(sql);
        List<Token> adbTokens = TokenParser.advParse(tokens, 0, tokens.size());
        String newSql = TokenParser.dumpSql(adbTokens, param);
        Logger.d("====== DUMP NEW SQL ======");
        Logger.d(newSql);
        return newSql;
    }

    /**
     *
     * @param sql
     * @param param
     * @return
     */
    public static String dumpSql(String sql, Object param) {
        Map<String, Object> paramMap = ReflectionUtil.object2Map(param);
        return dumpSql(sql, paramMap);
    }

//    public static <T> List<T> selectAll(SQLiteDatabase db, String sql) {
//        Cursor cursor = db.rawQuery(sql, null);
//        TypeConverter<T> tc = new TypeConverter<>(cursor);
//        List<T> tasks = tc.toBeans(T.class);
//        return tasks;
//    }
}
