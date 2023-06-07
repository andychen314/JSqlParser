/*-
 * #%L
 * JSQLParser library
 * %%
 * Copyright (C) 2004 - 2019 JSQLParser
 * %%
 * Dual licensed under GNU LGPL 2.1 or Apache License 2.0
 * #L%
 */
package net.sf.jsqlparser.statement;

public class ShowFullColumnsStatement implements Statement {

    private String tableName;

    public ShowFullColumnsStatement() {
        // empty constructor
    }

    public ShowFullColumnsStatement(String tableName) {
        this.tableName = tableName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    @Override
    public String toString() {
        return "SHOW FULL COLUMNS FROM " + tableName;
    }

    @Override
    public void accept(StatementVisitor statementVisitor) {
        statementVisitor.visit(this);
    }

    public ShowFullColumnsStatement withTableName(String tableName) {
        this.setTableName(tableName);
        return this;
    }
}
