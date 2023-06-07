/*-
 * #%L
 * JSQLParser library
 * %%
 * Copyright (C) 2004 - 2019 JSQLParser
 * %%
 * Dual licensed under GNU LGPL 2.1 or Apache License 2.0
 * #L%
 */
package net.sf.jsqlparser.util.deparser;

import net.sf.jsqlparser.statement.ShowFullColumnsStatement;

public class ShowFullColumnsStatementDeParser extends AbstractDeParser<ShowFullColumnsStatement> {

    public ShowFullColumnsStatementDeParser(StringBuilder buffer) {
        super(buffer);
    }

    @Override
    public void deParse(ShowFullColumnsStatement show) {
        buffer.append("SHOW FULL COLUMNS FROM ").append(show.getTableName());
    }
}
