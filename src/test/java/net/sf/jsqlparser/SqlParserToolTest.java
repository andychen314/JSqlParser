/*-
 * #%L
 * JSQLParser library
 * %%
 * Copyright (C) 2004 - 2023 JSQLParser
 * %%
 * Dual licensed under GNU LGPL 2.1 or Apache License 2.0
 * #L%
 */
package net.sf.jsqlparser;

import java.io.StringReader;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;

public class SqlParserToolTest {

    public static void main(String args[]) throws JSQLParserException {
//        Statement statement = CCJSqlParserUtil.parse(new StringReader("SHOW FULL COLUMNS FROM crm_tenant"));
//        Statement statement = CCJSqlParserUtil.parse(new StringReader("show table status"));
        Statement statement = CCJSqlParserUtil.parse(new StringReader("commit"));
        System.out.println(statement.toString());
    }

}
