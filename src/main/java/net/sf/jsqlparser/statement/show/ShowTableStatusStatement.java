/*-
 * #%L
 * JSQLParser library
 * %%
 * Copyright (C) 2004 - 2020 JSQLParser
 * %%
 * Dual licensed under GNU LGPL 2.1 or Apache License 2.0
 * #L%
 */
package net.sf.jsqlparser.statement.show;

import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.StatementVisitor;

/**
 * A {@code SHOW TABLES} statement
 * 
 * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/show-tables.html">MySQL show tables</a>
 */
public class ShowTableStatusStatement implements Statement {

    @Override
    public String toString() {
        return "SHOW TABLE STATUS";
    }
    @Override
    public void accept(StatementVisitor statementVisitor) {
        statementVisitor.visit(this);
    }

}
