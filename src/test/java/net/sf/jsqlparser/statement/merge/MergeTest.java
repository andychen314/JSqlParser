/*-
 * #%L
 * JSQLParser library
 * %%
 * Copyright (C) 2004 - 2019 JSQLParser
 * %%
 * Dual licensed under GNU LGPL 2.1 or Apache License 2.0
 * #L%
 */
package net.sf.jsqlparser.statement.merge;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static net.sf.jsqlparser.test.TestUtils.assertOracleHintExists;
import static net.sf.jsqlparser.test.TestUtils.assertSqlCanBeParsedAndDeparsed;
import static org.junit.jupiter.api.Assertions.fail;

/**
 *
 * @author toben
 */
public class MergeTest {

    @Test
    public void testOracleMergeIntoStatement() throws JSQLParserException {
        String sql = "MERGE INTO bonuses B\n" + "USING (\n" + "  SELECT employee_id, salary\n"
                + "  FROM employee\n" + "  WHERE dept_no =20) E\n"
                + "ON (B.employee_id = E.employee_id)\n" + "WHEN MATCHED THEN\n"
                + "  UPDATE SET B.bonus = E.salary * 0.1\n" + "WHEN NOT MATCHED THEN\n"
                + "  INSERT (B.employee_id, B.bonus)\n"
                + "  VALUES (E.employee_id, E.salary * 0.05)  ";

        Statement statement = CCJSqlParserUtil.parse(sql);

        System.out.println(statement.toString());

        assertSqlCanBeParsedAndDeparsed(sql, true);
    }

    @Test
    public void testMergeIssue232() throws JSQLParserException {
        String sql = "MERGE INTO xyz using dual " + "ON ( custom_id = ? ) " + "WHEN matched THEN "
                + "UPDATE SET abc = sysdate " + "WHEN NOT matched THEN "
                + "INSERT (custom_id) VALUES (?)";

        assertSqlCanBeParsedAndDeparsed(sql, true);
    }

    @Test
    public void testMergeIssue676() throws JSQLParserException {
        String sql = "merge INTO M_KC21 USING\n"
                + "(SELECT AAA, BBB FROM I_KC21 WHERE I_KC21.aaa = 'li_kun'\n"
                + ") TEMP ON (TEMP.AAA = M_KC21.AAA)\n" + "WHEN MATCHED THEN\n"
                + "UPDATE SET M_KC21.BBB = 6 WHERE enterprise_id IN (0, 1)\n"
                + "WHEN NOT MATCHED THEN\n" + "INSERT VALUES\n" + "(TEMP.AAA,TEMP.BBB\n" + ")";

        assertSqlCanBeParsedAndDeparsed(sql, true);
    }

    @Test
    public void testComplexOracleMergeIntoStatement() throws JSQLParserException {
        String sql = "MERGE INTO DestinationValue Dest USING\n" + "(SELECT TheMonth ,\n"
                + "  IdentifyingKey ,\n" + "  SUM(NetPrice) NetPrice ,\n"
                + "  SUM(NetDeductionPrice) NetDeductionPrice ,\n"
                + "  MAX(CASE RowNumberMain WHEN 1 THEN QualityIndicator ELSE NULL END) QualityIndicatorMain ,\n"
                + "  MAX(CASE RowNumberDeduction WHEN 1 THEN QualityIndicator ELSE NULL END) QualityIndicatorDeduction \n"
                + "FROM\n" + "  (SELECT pd.TheMonth ,\n"
                + "    COALESCE(pd.IdentifyingKey, 0) IdentifyingKey ,\n"
                + "    COALESCE(CASE pd.IsDeduction WHEN 1 THEN NULL ELSE ConvertedCalculatedValue END, 0) NetPrice ,\n"
                + "    COALESCE(CASE pd.IsDeduction WHEN 1 THEN ConvertedCalculatedValue ELSE NULL END, 0) NetDeductionPrice ,\n"
                + "    pd.QualityIndicator ,\n"
                + "    row_number() OVER (PARTITION BY pd.TheMonth , pd.IdentifyingKey ORDER BY COALESCE(pd.QualityMonth, to_date('18991230', 'yyyymmdd')) DESC ) RowNumberMain ,\n"
                + "    NULL RowNumberDeduction\n" + "  FROM PricingData pd\n"
                + "  WHERE pd.ThingsKey      IN (:ThingsKeys)\n"
                + "  AND pd.TheMonth       >= :startdate\n"
                + "  AND pd.TheMonth       <= :enddate\n" + "  AND pd.IsDeduction = 0\n"
                + "  UNION ALL\n" + "  SELECT pd.TheMonth ,\n"
                + "    COALESCE(pd.IdentifyingKey, 0) IdentifyingKey ,\n"
                + "    COALESCE(CASE pd.IsDeduction WHEN 1 THEN NULL ELSE ConvertedCalculatedValue END, 0) NetPrice ,\n"
                + "    COALESCE(CASE pd.IsDeduction WHEN 1 THEN ConvertedCalculatedValue ELSE NULL END, 0) NetDeductionPrice ,\n"
                + "    pd.QualityIndicator ,\n" + "    NULL RowNumberMain ,\n"
                + "    row_number() OVER (PARTITION BY pd.TheMonth , pd.IdentifyingKey ORDER BY COALESCE(pd.QualityMonth, to_date('18991230', 'yyyymmdd')) DESC ) RowNumberDeduction \n"
                + "  FROM PricingData pd\n" + "  WHERE pd.ThingsKey       IN (:ThingsKeys)\n"
                + "  AND pd.TheMonth        >= :startdate\n"
                + "  AND pd.TheMonth        <= :enddate\n" + "  AND pd.IsDeduction <> 0\n" + "  )\n"
                + "GROUP BY TheMonth ,\n" + "  IdentifyingKey\n"
                + ") Data ON ( Dest.TheMonth = Data.TheMonth \n"
                + "            AND COALESCE(Dest.IdentifyingKey,0) = Data.IdentifyingKey )\n"
                + "WHEN MATCHED THEN\n" + "  UPDATE\n"
                + "  SET NetPrice        = ROUND(Data.NetPrice, PriceDecimalScale) ,\n"
                + "    DeductionPrice    = ROUND(Data.NetDeductionPrice, PriceDecimalScale) ,\n"
                + "    SubTotalPrice     = ROUND(Data.NetPrice + (Data.NetDeductionPrice * Dest.HasDeductions), PriceDecimalScale) ,\n"
                + "    QualityIndicator  =\n" + "    CASE Dest.HasDeductions\n" + "      WHEN 0\n"
                + "      THEN Data.QualityIndicatorMain\n" + "      ELSE\n" + "        CASE\n"
                + "          WHEN COALESCE(Data.CheckMonth1, to_date('18991230', 'yyyymmdd'))> COALESCE(Data.CheckMonth2,to_date('18991230', 'yyyymmdd'))\n"
                + "          THEN Data.QualityIndicatorMain\n"
                + "          ELSE Data.QualityIndicatorDeduction\n" + "        END\n"
                + "    END ,\n" + "    RecUser = :recuser ,\n" + "    RecDate = :recdate\n"
                + "  WHERE 1 =1\n" + "  AND IsImportant = 1\n"
                + "  AND COALESCE(Data.SomeFlag,-1) <> COALESCE(ROUND(Something, 1),-1)\n"
                + "  DELETE WHERE\n" + "  IsImportant = 0\n"
                + "  OR COALESCE(Data.SomeFlag,-1) = COALESCE(ROUND(Something, 1),-1)\n"
                + " WHEN NOT MATCHED THEN \n" + "  INSERT\n" + "    (\n" + "      TheMonth ,\n"
                + "      ThingsKey ,\n" + "      IsDeduction ,\n" + "      CreatedAt \n" + "    )\n"
                + "    VALUES\n" + "    (\n" + "      Data.TheMonth ,\n"
                + "      Data.ThingsKey ,\n" + "      Data.IsDeduction ,\n" + "      SYSDATE\n"
                + "    )\n";

        Statement statement = CCJSqlParserUtil.parse(sql);
        assertSqlCanBeParsedAndDeparsed(sql, true);
    }

    @Test
    public void testMergeUpdateInsertOrderIssue401() throws JSQLParserException {
        assertSqlCanBeParsedAndDeparsed(
                "MERGE INTO a USING dual ON (col3 = ? AND col1 = ? AND col2 = ?) WHEN NOT MATCHED THEN INSERT (col1, col2, col3, col4) VALUES (?, ?, ?, ?) WHEN MATCHED THEN UPDATE SET col4 = col4 + ?");
    }

    @Test
    public void testMergeUpdateInsertOrderIssue401_2() throws JSQLParserException {
        assertSqlCanBeParsedAndDeparsed(
                "MERGE INTO a USING dual ON (col3 = ? AND col1 = ? AND col2 = ?) WHEN MATCHED THEN UPDATE SET col4 = col4 + ? WHEN NOT MATCHED THEN INSERT (col1, col2, col3, col4) VALUES (?, ?, ?, ?)");
    }

    @Test
    public void testMergeUpdateInsertOrderIssue401_3() throws JSQLParserException {
        try {
            assertSqlCanBeParsedAndDeparsed(
                    "MERGE INTO a USING dual ON (col3 = ? AND col1 = ? AND col2 = ?) WHEN MATCHED THEN UPDATE SET col4 = col4 + ? WHEN NOT MATCHED THEN INSERT (col1, col2, col3, col4) VALUES (?, ?, ?, ?) WHEN MATCHED THEN UPDATE SET col4 = col4 + ?");
            fail("syntaxerror parsed");
        } catch (JSQLParserException ex) {
            // expected to fail
        }
    }

    @Test
    public void testOracleHint() throws JSQLParserException {
        String sql = "MERGE /*+ SOMEHINT */ INTO bonuses B\n" + "USING (\n"
                + "  SELECT employee_id, salary\n" + "  FROM employee\n"
                + "  WHERE dept_no =20) E\n" + "ON (B.employee_id = E.employee_id)\n"
                + "WHEN MATCHED THEN\n" + "  UPDATE SET B.bonus = E.salary * 0.1\n"
                + "WHEN NOT MATCHED THEN\n" + "  INSERT (B.employee_id, B.bonus)\n"
                + "  VALUES (E.employee_id, E.salary * 0.05)  ";

        assertOracleHintExists(sql, true, "SOMEHINT");

        // @todo: add a testcase supposed to not finding a misplaced hint
    }

    @Test
    public void testInsertMergeWhere() throws JSQLParserException {
        String sql = "-- Both clauses present.\n" + "MERGE INTO test1 a\n"
                + "  USING all_objects b\n" + "    ON (a.object_id = b.object_id)\n"
                + "  WHEN MATCHED THEN\n" + "    UPDATE SET a.status = b.status\n"
                + "    WHERE  b.status != 'VALID'\n" + "  WHEN NOT MATCHED THEN\n"
                + "    INSERT (object_id, status)\n" + "    VALUES (b.object_id, b.status)\n" + "\n"
                + "    WHERE  b.status != 'VALID'\n";

        Statement statement = CCJSqlParserUtil.parse(sql);
        assertSqlCanBeParsedAndDeparsed(sql, true);

        Merge merge = (Merge) statement;
        MergeInsert mergeInsert = merge.getMergeInsert();
        Assertions.assertThat(mergeInsert.getWhereCondition());

        MergeUpdate mergeUpdate = merge.getMergeUpdate();
        Assertions.assertThat(mergeUpdate.getWhereCondition());
    }

    @Test
    public void testWith() throws JSQLParserException {
        String statement = "" + "WITH a\n" + "     AS (SELECT 1 id_instrument_ref)\n" + "     , b\n"
                + "       AS (SELECT 1 id_instrument_ref)\n" + "MERGE INTO cfe.instrument_ref b\n"
                + "using a\n" + "ON ( b.id_instrument_ref = a.id_instrument_ref )\n"
                + "WHEN matched THEN\n" + "  UPDATE SET b.id_instrument = 'a' ";
        statement = "" + "WITH a\n" + "     AS (SELECT 1 id_instrument_ref)\n" + "select * from a ";
        assertSqlCanBeParsedAndDeparsed(statement, true);
    }
}
