package dao.core;

import android.util.Log;

import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class TokenParser {

    private static final String TAG = "TokenParser";

    static class Reader {
        String _source;
        int i;
        Reader (String source) {
            _source = source.trim();
            i = 0;
        }

         String popNext() {
            if (i == _source.length())
                return null;
            String c = _source.substring(i, i + 1);
            i++;
            return c;
        }

         void pushBack(){
            i--;
        }
    }

    public static List<Token> parse(String source){
        List<Token> tokens = new ArrayList<Token>();
        Reader reader = new Reader(source);
        StringBuilder buf = new StringBuilder();
        String c;
        while ((c = reader.popNext()) != null) {

            if (" \t\r\n,()*;<>=.#{}'\\!".indexOf(c) >= 0) { // 遇到分隔符，生成之前的buf token
                if (buf.length() > 0) {
                    Token token = TokenFactory.createToken(buf.toString());
                    tokens.add(token);
                    buf = new StringBuilder();
                }
            }

//            if (" \t".indexOf(c) >= 0) {
//                continue;
//            } else
            if (" \t\r\n,()*;.=#{}'\\".indexOf(c) >= 0) { // 生成分隔符token
                Token token = TokenFactory.createToken(c);
                tokens.add(token);
            } else if ("<>!".indexOf(c) >= 0) {
                buf.append(c);
                String c2 = reader.popNext();
                if (c2.equals("=")) {
                    buf.append(c2);
                } else {
                    reader.pushBack();
                }
                Token token = TokenFactory.createToken(buf.toString());
                tokens.add(token);
                buf = new StringBuilder();
            } else {
                buf.append(c);
            }
        }
        if (buf.length() > 0) {
            Token token = TokenFactory.createToken(buf.toString());
            tokens.add(token);
            buf = new StringBuilder();
        }
        return tokens;
    }

    static class TokenFactory {
        static Token createToken(String tokenString) {
            String tokenStringUppercase = tokenString.toUpperCase();
            if (tokenStringUppercase.equals(" ")) return new Token.SpaceToken();
            if (tokenStringUppercase.equals("\t")) return new Token.SpaceToken();
            if (tokenStringUppercase.equals("\n")) return new Token.SpaceToken();
            if (tokenStringUppercase.equals("\r")) return new Token.SpaceToken();
            if (tokenStringUppercase.equals("SELECT")) return new Token.SelectToken();
            if (tokenStringUppercase.equals("FROM")) return new Token.FromToken();
            if (tokenStringUppercase.equals("WHERE")) return new Token.WhereToken();
            if (tokenStringUppercase.equals("AS")) return new Token.AsToken();
            if (tokenStringUppercase.equals("AND")) return new Token.AndToken();
            if (tokenStringUppercase.equals("OR")) return new Token.OrToken();
            if (tokenStringUppercase.equals("GROUP")) return new Token.GroupToken();
            if (tokenStringUppercase.equals("ORDER")) return new Token.OrderToken();
            if (tokenStringUppercase.equals("BY")) return new Token.ByToken();
            if (tokenStringUppercase.equals("DESC")) return new Token.DescToken();
            if (tokenStringUppercase.equals("INSERT")) return new Token.InsertToken();
            if (tokenStringUppercase.equals("INTO")) return new Token.IntoToken();
            if (tokenStringUppercase.equals("UPDATE")) return new Token.UpdateToken();
            if (tokenStringUppercase.equals("SET")) return new Token.SetToken();
            if (tokenStringUppercase.equals("VALUES")) return new Token.ValuesToken();
            if (tokenStringUppercase.equals("IS")) return new Token.IsToken();
            if (tokenStringUppercase.equals("NOT")) return new Token.NotToken();
            if (tokenStringUppercase.equals("NULL")) return new Token.NullToken();
//            if (tokenStringUppercase.equals("COUNT")) return new core.Token.CountToken();
            if (tokenStringUppercase.equals("#")) return new Token.SharpToken();
            if (tokenStringUppercase.equals(".")) return new Token.DotToken();
            if (tokenStringUppercase.equals("*")) return new Token.StarToken();
            if (tokenStringUppercase.equals("'")) return new Token.SqingleQuotation();
            if (tokenStringUppercase.equals(",")) return new Token.CommaToken();
            if (tokenStringUppercase.equals(";")) return new Token.SemicolonToken();
            if (tokenStringUppercase.equals("=")) return new Token.EqualToken();
            if (tokenStringUppercase.equals(">")) return new Token.BigToken();
            if (tokenStringUppercase.equals("<")) return new Token.LessToken();
            if (tokenStringUppercase.equals(">=")) return new Token.BigEqualToken();
            if (tokenStringUppercase.equals("<=")) return new Token.LessEqualToken();
            if (tokenStringUppercase.equals("!")) return new Token.ExclamationToken();
            if (tokenStringUppercase.equals("!=")) return new Token.NotEqualToken();
            if (tokenStringUppercase.equals("(")) return new Token.LeftParathToken();
            if (tokenStringUppercase.equals(")")) return new Token.RightParathToken();
            if (tokenStringUppercase.equals("{")) return new Token.LeftBraceToken();
            if (tokenStringUppercase.equals("}")) return new Token.RightBraceToken();
            if (tokenStringUppercase.equals("\\")) return new Token.SlashToken();
            else return new Token.VarToken(tokenString);
        }
    }


    static class TokenReader {
        List<Token> _tokens;
        int _begin;
        int _end; // non-include
        int i; // point to ready to pop token
        int mark;
        TokenReader(List<Token> tokens, int begin, int end) {
            _tokens = tokens;
            i = begin;
            _begin = begin;
            _end = end;
            mark = -1;
        }

        void mark(){
            mark = i;
        }

        void gotoMark() {
            if (mark == -1)
                throw new RuntimeException("goto 错误的 mark from i: " + i);
            i = mark;
            mark = -1;
        }

        int getPos(){
            return i;
        }

        Token next() {
            Token t;
            if (i >= _end || i < _begin)
                t = null;
            else
                t = _tokens.get(i);
            i++;
            return t;
        }

        void back(){
            i--;
        }
    }

    static List<Token> collectTextTokens(TokenReader reader) {
        List<Token> tokens = new ArrayList<Token>();
        Token t;
        int count = 0;
        while((t = reader.next()) != null) {
            tokens.add(t);
            if (t instanceof Token.SqingleQuotation) {
                count++;
                if (count == 2) {
                    break;
                }
            }
        }

        if (count != 2)
            throw new RuntimeException("单引号匹配错误");

        return tokens;
    }

    static List<Token> collectParathiesTokens(TokenReader reader) {
        List<Token> tokens = new ArrayList<Token>();
        Token t;
        int count = 0;
        while((t = reader.next()) != null) {
            tokens.add(t);
            if (t instanceof Token.LeftParathToken) {
                count++;
            } else if (t instanceof Token.RightParathToken) {
                count--;
                if (count == 0) {
                    break;
                }
            }
        }
        return tokens;
    }

    static List<Token> collectCommaTokens(TokenReader reader) {
        List<Token> tokens = new ArrayList<Token>();
        Token t;
        while((t = reader.next()) != null) {
            if (t instanceof Token.ParatheseCompseToken
                    || t instanceof Token.VarToken
                    || t instanceof Token.AliasComposeToken
                    || t instanceof Token.DotComposeToken
                    || t instanceof Token.FunctionComposeToken
                    || t instanceof Token.StarToken
                    || t instanceof Token.PlaceholderComposeToken
                    || t instanceof Token.TextComposeToken
                    || t instanceof Token.CompareCompseToken) {

                tokens.add(t);
                Token next = reader.next();
                if (next instanceof Token.CommaToken == false) {
                    reader.back();
                    break;
                }
            } else {
                reader.back();
                break;
            }
        }
        return tokens;
    }

    static List<Token> collectPlaceholderTokens(TokenReader reader) {
        List<Token> tokens = new ArrayList<Token>();
        Token t;
        int count = 0;
        while((t = reader.next()) != null) {
            tokens.add(t);
            if (t instanceof Token.LeftBraceToken) {
                count++;
            } else if (t instanceof Token.RightBraceToken) {
                count--;
                if (count == 0) {
                    break;
                }
            }
        }
        return tokens;
    }

    static List<Token> escapeClean(List<Token> tokens, int begin, int end) {
        List<Token> newTokens = new ArrayList<Token>();
        TokenReader reader = new TokenReader(tokens, begin, end);
        Token t;
        while((t = reader.next()) != null) {
            if (t instanceof Token.SlashToken) {
                Token.EscapeComposeToken ect = new Token.EscapeComposeToken();
                ect.token = reader.next();
                newTokens.add(ect);
            } else {
                newTokens.add(t);
            }
        }
        return newTokens;
    }

    static List<Token> textClean(List<Token> tokens, int begin, int end) {
        List<Token> newTokens = new ArrayList<Token>();
        TokenReader reader = new TokenReader(tokens, begin, end);
        Token t;
        while((t = reader.next()) != null) {
            if (t instanceof Token.SqingleQuotation) {
                reader.back();
                List<Token> textTokens = collectTextTokens(reader);
                if (textTokens.size() >= 2) {
                    Token.TextComposeToken tct = new Token.TextComposeToken();
                    tct.tokens = textTokens;
                    newTokens.add(tct);
                } else {
                    throw new RuntimeException("单引号数量小于2个，无法匹配");
                }
            } else {
                newTokens.add(t);
            }
        }
        return newTokens;
    }

    static List<Token> spaceClean(List<Token> tokens, int begin, int end) {
        List<Token> newTokens = new ArrayList<Token>();
        TokenReader reader = new TokenReader(tokens, begin, end);
        Token t;
        while((t = reader.next()) != null) {
            if (t instanceof Token.SpaceToken == false) {
                newTokens.add(t);
            }
        }
        return newTokens;
    }

    static List<Token> paratheseClean(List<Token> tokens, int begin, int end) {
        List<Token> newTokens = new ArrayList<Token>();
        TokenReader reader = new TokenReader(tokens, begin, end);
        Token t;
        while((t = reader.next()) != null) {
            if (t instanceof Token.LeftParathToken) {
                reader.back();
                List<Token> parathiesTokens = collectParathiesTokens(reader);
                if (parathiesTokens.size() >= 2) {
                    Token.ParatheseCompseToken pct = new Token.ParatheseCompseToken();
//                    pct.tokens = parathiesTokens;
                    pct.tokens = advParse(parathiesTokens, 1, parathiesTokens.size() - 1);
                    newTokens.add(pct);
                    // 递归parse
                }
            } else {
                newTokens.add(t);
            }
        }
        return newTokens;
    }

    static List<Token> dotClean(List<Token> tokens, int begin, int end) {
        List<Token> newTokens = new ArrayList<Token>();
        TokenReader reader = new TokenReader(tokens, begin, end);
        Token t;
        while((t = reader.next()) != null) {
            if (t instanceof Token.DotToken) {
                reader.back();
                reader.back();
                Token.DotComposeToken dotCT = new Token.DotComposeToken();
                dotCT.left = reader.next();
                reader.next();
                dotCT.right = reader.next();
                newTokens.remove(newTokens.size() - 1);
                newTokens.add(dotCT);
            } else {
                newTokens.add(t);
            }
        }
        return newTokens;
    }

    static List<Token> keywordClean(List<Token> tokens, int begin, int end) {
        List<Token> newTokens = new ArrayList<Token>();
        TokenReader reader = new TokenReader(tokens, begin, end);
        Token t;
        while((t = reader.next()) != null) {
            if (t instanceof Token.GroupToken) {
                Token next = reader.next();
                if (next instanceof Token.ByToken) {
                    Token.GroupByComposeToken gb = new Token.GroupByComposeToken();
                    newTokens.add(gb);
                }
            } else if (t instanceof Token.OrderToken) {
                Token next = reader.next();
                if (next instanceof Token.ByToken) {
                    Token.OrderByComposeToken ob = new Token.OrderByComposeToken();
                    newTokens.add(ob);
                }
            } else if (t instanceof Token.InsertToken) {
                Token next = reader.next();
                if (next instanceof Token.IntoToken) {
                    Token.InsertIntoComposeToken ob = new Token.InsertIntoComposeToken();
                    newTokens.add(ob);
                }
            } else {
                newTokens.add(t);
            }
        }
        return newTokens;
    }

    static List<Token> functionClean(List<Token> tokens, int begin, int end) {
        List<Token> newTokens = new ArrayList<Token>();
        TokenReader reader = new TokenReader(tokens, begin, end);
        Token t;
        while((t = reader.next()) != null) {
            if (t instanceof Token.ParatheseCompseToken) {
                reader.mark();
                reader.back();
                reader.back();
                Token prev = reader.next();
                if (prev instanceof Token.VarToken && prev instanceof Token.KeywordToken == false) {
                    Token.FunctionComposeToken fnct = new Token.FunctionComposeToken();
                    fnct.fn = prev;
                    fnct.p = (Token.ParatheseCompseToken) t;
                    newTokens.remove(newTokens.size() - 1);
                    newTokens.add(fnct);
                } else {
                    newTokens.add(t);
                }
                reader.gotoMark();
            } else {
                newTokens.add(t);
            }
        }
        return newTokens;
    }

    static List<Token> aliasClean(List<Token> tokens, int begin, int end) {
        List<Token> newTokens = new ArrayList<Token>();
        TokenReader reader = new TokenReader(tokens, begin, end);
        Token t;
        while((t = reader.next()) != null) {
            if (t instanceof Token.VarToken) { // 别名
                reader.mark();
                reader.back();
                reader.back();
                Token prev = reader.next();
                if (prev instanceof Token.AsToken) {
                    reader.back();
                    reader.back();
                    prev = reader.next();
                    Token.AliasComposeToken a = new Token.AliasComposeToken();
                    a.left = prev;
                    a.right = t;
                    newTokens.remove(newTokens.size() - 1);
                    newTokens.remove(newTokens.size() - 1);
                    newTokens.add(a);
                } else if (prev instanceof Token.VarToken
                        || prev instanceof Token.ParatheseCompseToken
                        || prev instanceof Token.DotComposeToken
                        || prev instanceof Token.FunctionComposeToken) { // 原名
                    Token.AliasComposeToken a = new Token.AliasComposeToken();
                    a.left = prev;
                    a.right = t;
                    newTokens.remove(newTokens.size() - 1);
                    newTokens.add(a);
                } else {
                    newTokens.add(t);
                }
                reader.gotoMark();
            } else {
                newTokens.add(t);
            }
        }
        return newTokens;
    }

    static List<Token> compareClean(List<Token> tokens, int begin, int end) {
        List<Token> newTokens = new ArrayList<Token>();
        TokenReader reader = new TokenReader(tokens, begin, end);
        Token t;
        while((t = reader.next()) != null) {
            if (t instanceof Token.CompareToken) {
                reader.back();
                reader.back();
                Token prev = reader.next();
                reader.next();
                Token next = reader.next();
                Token.CompareCompseToken cct = new Token.CompareCompseToken();
                cct.compareToken = (Token.CompareToken) t;
                cct.left = prev;
                cct.right = next;
                newTokens.remove(newTokens.size() - 1);
                newTokens.add(cct);
            } else {
                newTokens.add(t);
            }
        }
        return newTokens;
    }

    static List<Token> unilogicClean(List<Token> tokens, int begin, int end) {
        List<Token> newTokens = new ArrayList<Token>();
        TokenReader reader = new TokenReader(tokens, begin, end);
        Token t;
        while((t = reader.next()) != null) {
            if (t instanceof Token.IsToken) {
                reader.back();
                reader.back();
                Token prev = reader.next();
                Token.IsNullComposeToken inc = new Token.IsNullComposeToken();
                inc.field = prev;
                reader.next();
                Token next = reader.next();
                if (next instanceof Token.NotToken) {
                    inc.hasNot = true;
                    next = reader.next();
                    if (next instanceof Token.NullToken == false) {
                        throw new RuntimeException("Is Not Null匹配错误");
                    }
                } else {
                    if (next instanceof Token.NullToken == false) {
                        throw new RuntimeException("Is Null匹配错误");
                    }
                }
                newTokens.remove(newTokens.size() - 1);
                newTokens.add(inc);
            } else {
                newTokens.add(t);
            }
        }
        return newTokens;
    }

    static List<Token> logicClean(List<Token> tokens, int begin, int end) {
        List<Token> newTokens = new ArrayList<Token>();
        TokenReader reader = new TokenReader(tokens, begin, end);
        Token t;
        while((t = reader.next()) != null) {
            if (t instanceof Token.BiLogicToken) {
                reader.back();
                reader.back();
                Token prev = reader.next();
                reader.next();
                Token next = reader.next();
                Token.BiLogicComposeToken bct = new Token.BiLogicComposeToken();
                bct.logicToken = (Token.LogicToken) t;
                bct.left = prev;
                bct.right = next;
                newTokens.remove(newTokens.size() - 1);
                newTokens.add(bct);
            } else {
                newTokens.add(t);
            }
        }
        return newTokens;
    }

    static List<Token> commaClean(List<Token> tokens, int begin, int end) {
        List<Token> newTokens = new ArrayList<Token>();
        TokenReader reader = new TokenReader(tokens, begin, end);
        Token t;
        while((t = reader.next()) != null) {
            if (t instanceof Token.CommaToken) {
                reader.back();
                reader.back();
                List<Token> commaCompseTokens = collectCommaTokens(reader);
                Token.CommaComposeToken cct = new Token.CommaComposeToken();
                cct.tokens = commaCompseTokens;
                newTokens.remove(newTokens.size() - 1);
                newTokens.add(cct);
            } else {
                newTokens.add(t);
            }
        }
        return newTokens;
    }

    static List<Token> placeholderClean(List<Token> tokens, int begin, int end) {
        List<Token> newTokens = new ArrayList<Token>();
        TokenReader reader = new TokenReader(tokens, begin, end);
        Token t;
        while((t = reader.next()) != null) {
            if (t instanceof Token.SharpToken) {
                Token next = reader.next();
                if (next instanceof Token.LeftBraceToken) {
                    reader.back();
                    reader.back();

                    List<Token> placeholderTokens = collectPlaceholderTokens(reader);
                    if (placeholderTokens.size() >= 3){
                        Token.PlaceholderComposeToken pct = new Token.PlaceholderComposeToken();
                        pct.var = (Token.VarToken) placeholderTokens.get(2);
                        newTokens.add(pct);
                    }

                } else {
                    newTokens.add(t);
                }
            } else {
                newTokens.add(t);
            }
        }
        return newTokens;
    }

    static List<Token> selectClauseClean(List<Token> tokens, int begin, int end) {
        List<Token> newTokens = new ArrayList<Token>();
        TokenReader reader = new TokenReader(tokens, begin, end);
        Token t;
        while((t = reader.next()) != null) {
            if (t instanceof Token.SelectToken) {
                Token.SelectClauseComposeToken scct = new Token.SelectClauseComposeToken();
                Token next = reader.next();
                scct.selectPart = next;
                next = reader.next();
                if (next instanceof Token.FromToken == false)
                    throw new RuntimeException("Select语句语法错误: ");
                next = reader.next();
                scct.fromPart = next;
                // where
                next = reader.next();
                if (next instanceof Token.WhereToken) {
                    next = reader.next();
                    scct.wherePart = next;

                    // group
                    next = reader.next();
                    if (next instanceof Token.GroupByComposeToken) {
                        next = reader.next();
                        scct.groupPart = next;

                        // order
                        next = reader.next();
                        if (next instanceof Token.OrderByComposeToken) {
                            next = reader.next();
                            scct.orderPart = next;
                        } else {
                            reader.back();
                        }
                    } else {
                        reader.back();
                    }
                } else {
                    reader.back();
                }

                newTokens.add(scct);
            } else {
                newTokens.add(t);
            }
        }
        return newTokens;
    }

    static List<Token> insertClauseClean(List<Token> tokens, int begin, int end) {
        List<Token> newTokens = new ArrayList<Token>();
        TokenReader reader = new TokenReader(tokens, begin, end);
        Token t;
        while((t = reader.next()) != null) {
            if (t instanceof Token.InsertIntoComposeToken) {
                Token.InsertClauseComposeToken scct = new Token.InsertClauseComposeToken();
                Token next = reader.next();
                scct.tablePart = next;
                next = reader.next();
                scct.valuePart = next;
                newTokens.add(scct);
            } else {
                newTokens.add(t);
            }
        }
        return newTokens;
    }

    static List<Token> updateClauseClean(List<Token> tokens, int begin, int end) {
        List<Token> newTokens = new ArrayList<Token>();
        TokenReader reader = new TokenReader(tokens, begin, end);
        Token t;
        while((t = reader.next()) != null) {
            if (t instanceof Token.UpdateToken) {
                Token.UpdateClauseComposeToken scct = new Token.UpdateClauseComposeToken();
                Token next = reader.next();
                scct.tablePart = next;
                next = reader.next();
                if (next instanceof Token.SetToken == false)
                    throw new RuntimeException("Update语句语法错误: ");
                next = reader.next();
                scct.setPart = next;
                // where
                next = reader.next();
                if (next instanceof Token.WhereToken) {
                    next = reader.next();
                    scct.wherePart = next;
                } else {
                    reader.back();
                }
                newTokens.add(scct);
            } else {
                newTokens.add(t);
            }
        }
        return newTokens;
    }

    public static List<Token> advParse(List<Token> tokens, int begin, int end) {
        // 优先级：
        // 0.转移\
        // 0.变量#{}
        // 0.字符''
        // 0.remove space token
        // 1.括号()
        // 2.点号.
        // 3.keywords compose: group by / order by / insert into
        // 3.函数
        // 4.别名
        // 5.算数比较符
        // 6.逻辑
        // 7.逗号
        // 8.Select语句
        // 9.insert语句
        // 10.update语句
        Log.d(TAG,"======= begin advParsing input: ======" + begin + "," + end);
        printTokens(tokens);

        Log.d(TAG,"======= escapeClean ======");
        List<Token> escapeCleanedTokens = escapeClean(tokens, begin, end);
        printTokens(escapeCleanedTokens);

        Log.d(TAG,"======= placeholderClean ======");
        List<Token> placeholderCleanedTokens = placeholderClean(escapeCleanedTokens, 0, escapeCleanedTokens.size());
        printTokens(placeholderCleanedTokens);

        Log.d(TAG,"======= textCleanedTokens ======");
        List<Token> textCleanedTokens = textClean(placeholderCleanedTokens, 0, placeholderCleanedTokens.size());
        printTokens(textCleanedTokens);

        Log.d(TAG,"======= spaceCleanedTokens ======");
        List<Token> spaceCleanedTokens = spaceClean(textCleanedTokens, 0, textCleanedTokens.size());
        printTokens(spaceCleanedTokens);

        Log.d(TAG,"======= paratheseCleanedTokens ======");
        List<Token> pcleanedTokens = paratheseClean(spaceCleanedTokens, 0, spaceCleanedTokens.size());
        printTokens(pcleanedTokens);

        Log.d(TAG,"======= dotCleanedTokens ======");
        List<Token> dotCleanedTokens = dotClean(pcleanedTokens, 0, pcleanedTokens.size());
        printTokens(dotCleanedTokens);

        Log.d(TAG,"======= keywordCleanedTokens ======");
        List<Token> keyCleanedTokens = keywordClean(dotCleanedTokens, 0, dotCleanedTokens.size());
        printTokens(keyCleanedTokens);

        Log.d(TAG,"======= functionCleanedTokens ======");
        List<Token> functionCleanedTokens = functionClean(keyCleanedTokens, 0, keyCleanedTokens.size());
        printTokens(functionCleanedTokens);

        Log.d(TAG,"======= aliasCleanedTokens ======");
        List<Token> aliasCleanedTokens = aliasClean(functionCleanedTokens, 0, functionCleanedTokens.size());
        printTokens(aliasCleanedTokens);

        Log.d(TAG,"======= compareCleanedTokens ======");
        List<Token> compareCleanedTokens = compareClean(aliasCleanedTokens, 0, aliasCleanedTokens.size());
        printTokens(compareCleanedTokens);

        Log.d(TAG,"======= unilogicCleanedTokens ======");
        List<Token> unilogicCleanedTokens = unilogicClean(compareCleanedTokens, 0, compareCleanedTokens.size());
        printTokens(unilogicCleanedTokens);

        Log.d(TAG,"======= logicCleanedTokens ======");
        List<Token> logicCleanedTokens = logicClean(unilogicCleanedTokens, 0, unilogicCleanedTokens.size());
        printTokens(logicCleanedTokens);

        Log.d(TAG,"======= commaCleanedTokens ======");
        List<Token> commaCleanedTokens = commaClean(logicCleanedTokens, 0, logicCleanedTokens.size());
        printTokens(commaCleanedTokens);

        Log.d(TAG,"======= selectClauseCleanedTokens ======");
        List<Token> selectClauseCleanedTokens = selectClauseClean(commaCleanedTokens, 0, commaCleanedTokens.size());
        printTokens(selectClauseCleanedTokens);

        Log.d(TAG,"======= insertClauseCleanedTokens ======");
        List<Token> insertClauseCleanedTokens = insertClauseClean(selectClauseCleanedTokens, 0, selectClauseCleanedTokens.size());
        printTokens(insertClauseCleanedTokens);

        Log.d(TAG,"======= updateClauseCleanedTokens ======");
        List<Token> updateClauseCleanedTokens = updateClauseClean(insertClauseCleanedTokens, 0, insertClauseCleanedTokens.size());
        printTokens(updateClauseCleanedTokens);

        Log.d(TAG,"======= end advParse ======");

        return updateClauseCleanedTokens;
    }

    public static String dumpSql(List<Token> tokens, Map<String, Object> param) {
        StringBuilder sb = new StringBuilder();
        for (Token t : tokens) {
            String s = t.dumpSql(param);
            if (s != null)
                sb.append(s).append(" ");
        }
        return sb.toString();
    }

    public static void printTokens(List<Token> tokens){
        StringBuilder sb = new StringBuilder();
        for(Token t : tokens)
            sb.append(t).append("~");
        Log.d(TAG,sb.toString());
    }

}
