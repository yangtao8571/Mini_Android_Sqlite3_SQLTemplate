package dao.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Token {

    public String dumpSql(Map<String, Object> param) {
        return toString();
    }

    static interface KeywordToken {

    }

    static class SpaceToken extends Token {

        @Override
        public String toString() {
            return "_";
        }
        @Override
        public String dumpSql(Map<String, Object> param)  {
            return " ";
        }
    }

    static class SelectToken extends Token implements KeywordToken{
        @Override
        public String toString() {
            return "SELECT";
        }
    }

    static class StarToken extends Token {

        @Override
        public String toString() {
            return "*";
        }
    }

    static class FromToken extends Token  implements KeywordToken{

        @Override
        public String toString() {
            return "FROM";
        }
    }

    static class WhereToken extends Token  implements KeywordToken{

        @Override
        public String toString() {
            return "WHERE";
        }
    }

    static class InsertToken extends Token  implements KeywordToken{

        @Override
        public String toString() {
            return "INSERT";
        }
    }

    static class IntoToken extends Token  implements KeywordToken{

        @Override
        public String toString() {
            return "INTO";
        }
    }

    static class UpdateToken extends Token  implements KeywordToken{

        @Override
        public String toString() {
            return "UPDATE";
        }
    }

    static class SetToken extends Token  implements KeywordToken{

        @Override
        public String toString() {
            return "SET";
        }
    }

    static class ValuesToken extends Token  implements KeywordToken{

        @Override
        public String toString() {
            return "VALUES";
        }
    }

    static class VarToken extends Token {
        String _var;
        VarToken(String var) {
            _var = var;
        }
        @Override
        public String toString() {
            return _var;
        }
    }

    static class ByToken extends Token  implements KeywordToken{

        @Override
        public String toString() {
            return "BY";
        }
    }

    static class DescToken extends Token  implements KeywordToken{

        @Override
        public String toString() {
            return "DESC";
        }
    }

    static class AscToken extends Token  implements KeywordToken{

        @Override
        public String toString() {
            return "ASC";
        }
    }

    static class GroupToken extends Token  implements KeywordToken {

        @Override
        public String toString() {
            return "GROUP";
        }
    }

    static class OrderToken extends Token implements KeywordToken {

        @Override
        public String toString() {
            return "ORDER";
        }
    }

    static class AsToken extends Token implements KeywordToken {

        @Override
        public String toString() {
            return "AS";
        }
    }

    static class IsToken extends Token implements KeywordToken {

        @Override
        public String toString() {
            return "IS";
        }
    }

    static class NotToken extends Token implements KeywordToken {

        @Override
        public String toString() {
            return "NOT";
        }
    }

    static class NullToken extends Token implements KeywordToken {

        @Override
        public String toString() {
            return "NULL";
        }
    }

    static class LogicToken extends Token {

    }

    static class UniLogicToken extends LogicToken {

    }

    static class BiLogicToken extends LogicToken {

    }

    static class AndToken extends BiLogicToken  implements KeywordToken {

        @Override
        public String toString() {
            return "AND";
        }
    }

    static class OrToken extends BiLogicToken implements KeywordToken {

        @Override
        public String toString() {
            return "OR";
        }
    }

    // '
    static class SqingleQuotation extends Token {

        @Override
        public String toString() {
            return "'";
        }
    }

    // \
    static class SlashToken extends Token {

        @Override
        public String toString() {
            return "\\";
        }
    }

    // /
    static class BackslashToken extends Token {

        @Override
        public String toString() {
            return "/";
        }
    }

    static class EscapeComposeToken extends Token {
        Token token;

        @Override
        public String toString() {
            return "\\" + token;
        }

        @Override
        public String dumpSql(Map<String, Object> param)  {
            return token.dumpSql(param);
        }
    }

    // 小括号
    static class LeftParathToken extends Token {

        @Override
        public String toString() {
            return "(";
        }
    }

    // 小括号
    static class RightParathToken extends Token {

        @Override
        public String toString() {
            return ")";
        }
    }

    // 大括号
    static class LeftBraceToken extends Token {

        @Override
        public String toString() {
            return "{";
        }
    }

    // 大括号
    static class RightBraceToken extends Token {

        @Override
        public String toString() {
            return "}";
        }
    }

    static class CommaToken extends Token {

        @Override
        public String toString() {
            return ",";
        }
    }

    // 井号
    static class SharpToken extends Token {

        @Override
        public String toString() {
            return "#";
        }
    }

    static class ExclamationToken extends Token {

        @Override
        public String toString() {
            return "!";
        }
    }

    static class CompareToken extends Token {

    }

    static class LessToken extends CompareToken {

        @Override
        public String toString() {
            return "<";
        }
    }

    static class LessEqualToken extends CompareToken {

        @Override
        public String toString() {
            return "<=";
        }
    }

    static class NotEqualToken extends CompareToken {

        @Override
        public String toString() {
            return "!=";
        }
    }

    static class EqualToken extends CompareToken {

        @Override
        public String toString() {
            return "=";
        }
    }

    static class BigToken extends CompareToken {

        @Override
        public String toString() {
            return ">";
        }
    }

    static class BigEqualToken extends CompareToken {

        @Override
        public String toString() {
            return ">=";
        }
    }

    // 分号
    static class SemicolonToken extends Token {

        @Override
        public String toString() {
            return ";";
        }
    }

    static class DotToken extends Token {

        @Override
        public String toString() {
            return ".";
        }
    }

    static class BiOprandToken extends Token {
        Token left;
        Token right;
    }

    static class DotComposeToken extends BiOprandToken {
        @Override
        public String toString() {
            return left + "." + right;
        }
    }

    static class AliasComposeToken extends BiOprandToken {

        @Override
        public String toString() {
            return "[" + left + " " + right + "]";
        }

        @Override
        public String dumpSql(Map<String, Object> param)  {
            return left.dumpSql(param) + " " + right.dumpSql(param);
        }
    }

    static class ParatheseCompseToken extends Token {
        List<Token> tokens = new ArrayList<Token>();

        @Override
        public String toString() {
            return "(..)";
        }

        @Override
        public String dumpSql(Map<String, Object> param)  {
            StringBuilder sb = new StringBuilder();
            sb.append("(");
            for (Token t : tokens) {
//                if (t instanceof SelectClauseComposeToken) {
//                    sb.append(((SelectClauseComposeToken)t).dumpSql(param)).append(" ");
//                } else {
                    sb.append(t.dumpSql(param)).append(" ");
//                }
            }
            sb.append(")");
            return sb.toString();
        }
    }

    static class PlaceholderComposeToken extends Token {
        VarToken var;

        @Override
        public String toString() {
            return "#{_}";
        }

        @Override
        public String dumpSql(Map<String, Object> param)  {
            if (param != null && param.containsKey(var._var)) {
                Object v = param.get(var._var);
                if (v != null) {
                    return v.toString();
                }
            }

            return null;
        }
    }

    static class CompareCompseToken extends BiOprandToken {
        CompareToken compareToken;

        @Override
        public String toString() {
            return compareToken.toString();
        }

        @Override
        public String dumpSql(Map<String, Object> param)  {
            String l = left.dumpSql(param);
            String r = right.dumpSql(param);
            if (l == null || r == null)
                return null;
            else
                return l + compareToken.dumpSql(param) + r;
        }
    }

    static class BiLogicComposeToken extends BiOprandToken {
        LogicToken logicToken;

        @Override
        public String toString() {
            return logicToken.toString();
        }

        @Override
        public String dumpSql(Map<String, Object> param)  {
            String l = left.dumpSql(param);
            String r = right.dumpSql(param);
            if (l == null && r == null)
                return null;
            else if (l == null)
                return r;
            else if (r == null)
                return l;
            else
                return l + " " + logicToken.dumpSql(param) + " " + r;
        }
    }

    static class GroupByComposeToken extends Token {

        @Override
        public String toString() {
            return "GROUP BY";
        }
    }

    static class InsertIntoComposeToken extends Token {

        @Override
        public String toString() {
            return "INSERT INTO";
        }
    }

    static class OrderByComposeToken extends Token {

        @Override
        public String toString() {
            return "ORDER BY";
        }
    }


    static class IsNullComposeToken extends Token {

        Token field;
        boolean hasNot;

        @Override
        public String toString() {
            return "IS";
        }

        @Override
        public String dumpSql(Map<String, Object> param)  {
            return field.dumpSql(param) + (hasNot ? " IS NULL" : " IS NOT NULL");
        }
    }

    // TODO:改名字，不应叫函数，应该叫VarParathese，表示 <Name>(...)的形式，包括函数、插入语句的table(col...)。
    static class FunctionComposeToken extends Token {
        Token fn;
        ParatheseCompseToken p;

        @Override
        public String toString() {
            return "FN:" + fn.toString() + "()";
        }

        @Override
        public String dumpSql(Map<String, Object> param)  {
            return fn.dumpSql(param) + p.dumpSql(param);
        }
    }

    static class TextComposeToken extends Token {
        List<Token> tokens;
//        Map<Integer, Token> holdersInText = new HashMap<Integer, Token>();

        @Override
        public String toString()  {
            StringBuilder sb = new StringBuilder("");
            for (Token t : tokens) {
                sb.append(t);
            }
            return sb.toString();
        }

        @Override
        public String dumpSql(Map<String, Object> param)  {
            StringBuilder sb = new StringBuilder("");
            boolean escaping = false;
            for (int i = 0; i < tokens.size(); i++) {
                Token t = tokens.get(i);
                if (escaping) {
                    sb.append(t.dumpSql(param));
                    escaping = false;
                } else {
                    if (t instanceof SlashToken) {
                        // skip the meta \
                        escaping = true;
                    } else {
                        sb.append(t.dumpSql(param));
                    }
                }
            }
            return sb.toString();
        }

    }

    static class CommaComposeToken extends Token {
        List<Token> tokens = new ArrayList<Token>();

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("Comma[");
            for (Token t : tokens) {
                sb.append(t).append(",");
            }
            if (sb.length() > 1)
                sb.deleteCharAt(sb.length() - 1);
            sb.append("]");
            return sb.toString();
        }

        @Override
        public String dumpSql(Map<String, Object> param) {
            StringBuilder sb = new StringBuilder();
            for (Token t : tokens) {
                sb.append(t.dumpSql(param)).append(",");
            }
            if (sb.length() > 1)
                sb.deleteCharAt(sb.length() - 1);
            return sb.toString();
        }
    }

    static class SelectClauseComposeToken extends Token {
        Token selectPart;
        Token fromPart;
        Token wherePart;
        Token groupPart;
        Token orderPart;

        @Override
        public String toString() {
            return "[Select]";
        }

        @Override
        public String dumpSql(Map<String, Object> param) {
            StringBuilder sb = new StringBuilder();
            sb.append("SELECT ");
            sb.append(selectPart.dumpSql(param));
            sb.append(" FROM ");
            sb.append(fromPart.dumpSql(param));
            if (wherePart != null) {
                String wheresql = wherePart.dumpSql(param);
                if (wheresql != null)
                    sb.append(" WHERE ").append(wheresql);
            }
            if (groupPart != null) {
                String s = groupPart.dumpSql(param);
                if (s != null)
                    sb.append(" GROUP BY ").append(s);
            }
            if (orderPart != null) {
                String s = orderPart.dumpSql(param);
                if (s != null)
                    sb.append(" ORDER BY ").append(s);
            }
            return sb.toString();
        }
    }

    static class InsertClauseComposeToken extends Token {
        Token tablePart;
        Token valuePart;

        @Override
        public String toString() {
            return "[Select]";
        }

        @Override
        public String dumpSql(Map<String, Object> param) {
            StringBuilder sb = new StringBuilder();
            sb.append("INSERT INTO ");
            sb.append(tablePart.dumpSql(param));
//            sb.append(" VALUES ");
            sb.append(" ");
            sb.append(valuePart.dumpSql(param));
            return sb.toString();
        }
    }

    static class UpdateClauseComposeToken extends Token {
        Token tablePart;
        Token setPart;
        Token wherePart;

        @Override
        public String toString() {
            return "[Select]";
        }

        @Override
        public String dumpSql(Map<String, Object> param) {
            StringBuilder sb = new StringBuilder();
            sb.append("UPDATE ");
            sb.append(tablePart.dumpSql(param));
            sb.append(" SET ");
            sb.append(setPart.dumpSql(param));
            if (wherePart != null) {
                String wheresql = wherePart.dumpSql(param);
                if (wheresql != null)
                    sb.append(" WHERE ").append(wheresql);
            }
            return sb.toString();
        }
    }


}
