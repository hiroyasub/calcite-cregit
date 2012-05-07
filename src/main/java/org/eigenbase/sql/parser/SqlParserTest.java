begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to DynamoBI Corporation (DynamoBI) under one // or more contributor license agreements.  See the NOTICE file // distributed with this work for additional information // regarding copyright ownership.  DynamoBI licenses this file // to you under the Apache License, Version 2.0 (the // "License"); you may not use this file except in compliance // with the License.  You may obtain a copy of the License at  //   http://www.apache.org/licenses/LICENSE-2.0  // Unless required by applicable law or agreed to in writing, // software distributed under the License is distributed on an // "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY // KIND, either express or implied.  See the License for the // specific language governing permissions and limitations // under the License. */
end_comment

begin_package
package|package
name|org
operator|.
name|eigenbase
operator|.
name|sql
operator|.
name|parser
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|sql
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|sql
operator|.
name|parser
operator|.
name|impl
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|test
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|util14
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * A<code>SqlParserTest</code> is a unit-test for {@link SqlParser the SQL  * parser}.  *  * @author jhyde  * @version $Id$  * @since Mar 19, 2003  */
end_comment

begin_class
specifier|public
class|class
name|SqlParserTest
extends|extends
name|TestCase
block|{
comment|//~ Static fields/initializers ---------------------------------------------
specifier|protected
specifier|static
specifier|final
name|String
name|NL
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"line.separator"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|ANY
init|=
literal|"(?s).*"
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
specifier|public
name|SqlParserTest
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
comment|// Helper functions -------------------------------------------------------
specifier|protected
name|Tester
name|getTester
parameter_list|()
block|{
return|return
operator|new
name|TesterImpl
argument_list|()
return|;
block|}
specifier|protected
name|void
name|check
parameter_list|(
name|String
name|sql
parameter_list|,
name|String
name|expected
parameter_list|)
block|{
name|getTester
argument_list|()
operator|.
name|check
argument_list|(
name|sql
argument_list|,
name|expected
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|SqlNode
name|parseStmt
parameter_list|(
name|String
name|sql
parameter_list|)
throws|throws
name|SqlParseException
block|{
return|return
operator|new
name|SqlParser
argument_list|(
name|sql
argument_list|)
operator|.
name|parseStmt
argument_list|()
return|;
block|}
specifier|protected
name|void
name|checkExp
parameter_list|(
name|String
name|sql
parameter_list|,
name|String
name|expected
parameter_list|)
block|{
name|getTester
argument_list|()
operator|.
name|checkExp
argument_list|(
name|sql
argument_list|,
name|expected
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|SqlNode
name|parseExpression
parameter_list|(
name|String
name|sql
parameter_list|)
throws|throws
name|SqlParseException
block|{
return|return
operator|new
name|SqlParser
argument_list|(
name|sql
argument_list|)
operator|.
name|parseExpression
argument_list|()
return|;
block|}
specifier|protected
name|SqlParserImpl
name|getParserImpl
parameter_list|()
block|{
return|return
operator|new
name|SqlParser
argument_list|(
literal|""
argument_list|)
operator|.
name|getParserImpl
argument_list|()
return|;
block|}
specifier|protected
name|void
name|checkExpSame
parameter_list|(
name|String
name|sql
parameter_list|)
block|{
name|checkExp
argument_list|(
name|sql
argument_list|,
name|sql
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|checkFails
parameter_list|(
name|String
name|sql
parameter_list|,
name|String
name|expectedMsgPattern
parameter_list|)
block|{
name|getTester
argument_list|()
operator|.
name|checkFails
argument_list|(
name|sql
argument_list|,
name|expectedMsgPattern
argument_list|)
expr_stmt|;
block|}
comment|/**      * Tests that an expression throws an exception which matches the given      * pattern.      */
specifier|protected
name|void
name|checkExpFails
parameter_list|(
name|String
name|sql
parameter_list|,
name|String
name|expectedMsgPattern
parameter_list|)
block|{
name|getTester
argument_list|()
operator|.
name|checkExpFails
argument_list|(
name|sql
argument_list|,
name|expectedMsgPattern
argument_list|)
expr_stmt|;
block|}
comment|/**      * Tests that when there is an error, non-reserved keywords such as "A",      * "ABSOLUTE" (which naturally arise whenver a production uses      * "&lt;IDENTIFIER&gt;") are removed, but reserved words such as "AND"      * remain.      */
specifier|public
name|void
name|testExceptionCleanup
parameter_list|()
block|{
name|checkFails
argument_list|(
literal|"select 0.5e1^.1^ from sales.emps"
argument_list|,
literal|"(?s).*Encountered \".1\" at line 1, column 13."
operator|+
name|NL
operator|+
literal|"Was expecting one of:"
operator|+
name|NL
operator|+
literal|"    \"AND\" ..."
operator|+
name|NL
operator|+
literal|"    \"AS\" ..."
operator|+
name|NL
operator|+
literal|"    \"BETWEEN\" ..."
operator|+
name|NL
operator|+
literal|"    \"FROM\" ..."
operator|+
name|NL
operator|+
literal|"    \"IN\" ..."
operator|+
name|NL
operator|+
literal|"    \"IS\" ..."
operator|+
name|NL
operator|+
literal|"    \"LIKE\" ..."
operator|+
name|NL
operator|+
literal|"    \"MEMBER\" ..."
operator|+
name|NL
operator|+
literal|"    \"MULTISET\" ..."
operator|+
name|NL
operator|+
literal|"    \"NOT\" ..."
operator|+
name|NL
operator|+
literal|"    \"OR\" ..."
operator|+
name|NL
operator|+
literal|"    \"SIMILAR\" ..."
operator|+
name|NL
operator|+
literal|"    \"SUBMULTISET\" ..."
operator|+
name|NL
operator|+
literal|"    \",\" ..."
operator|+
name|NL
operator|+
literal|"    \"=\" ..."
operator|+
name|NL
operator|+
literal|"    \">\" ..."
operator|+
name|NL
operator|+
literal|"    \"<\" ..."
operator|+
name|NL
operator|+
literal|"    \"<=\" ..."
operator|+
name|NL
operator|+
literal|"    \">=\" ..."
operator|+
name|NL
operator|+
literal|"    \"<>\" ..."
operator|+
name|NL
operator|+
literal|"    \"\\+\" ..."
operator|+
name|NL
operator|+
literal|"    \"-\" ..."
operator|+
name|NL
operator|+
literal|"    \"\\*\" ..."
operator|+
name|NL
operator|+
literal|"    \"/\" ..."
operator|+
name|NL
operator|+
literal|"    \"\\|\\|\" ..."
operator|+
name|NL
operator|+
literal|"<IDENTIFIER> ..."
operator|+
name|NL
operator|+
literal|"<QUOTED_IDENTIFIER> ..."
operator|+
name|NL
operator|+
literal|".*"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testInvalidToken
parameter_list|()
block|{
comment|// Causes problems to the test infrastructure because the token mgr
comment|// throws a java.lang.Error. The usual case is that the parser throws
comment|// an exception.
name|checkFails
argument_list|(
literal|"values (a^#^b)"
argument_list|,
literal|"Lexical error at line 1, column 10\\.  Encountered: \"#\" \\(35\\), after : \"\""
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|_testDerivedColumnList
parameter_list|()
block|{
name|check
argument_list|(
literal|"select * from emp (empno, gender) where true"
argument_list|,
literal|"foo"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|_testDerivedColumnListInJoin
parameter_list|()
block|{
name|check
argument_list|(
literal|"select * from emp as e (empno, gender) join dept (deptno, dname) on emp.deptno = dept.deptno"
argument_list|,
literal|"foo"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|_testDerivedColumnListNoAs
parameter_list|()
block|{
name|check
argument_list|(
literal|"select * from emp e (empno, gender) where true"
argument_list|,
literal|"foo"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|_testDerivedColumnListWithAlias
parameter_list|()
block|{
name|check
argument_list|(
literal|"select * from emp as e (empno, gender) where true"
argument_list|,
literal|"foo"
argument_list|)
expr_stmt|;
block|}
comment|// jdbc syntax
specifier|public
name|void
name|_testEmbeddedCall
parameter_list|()
block|{
name|checkExp
argument_list|(
literal|"{call foo(?, ?)}"
argument_list|,
literal|"foo"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|_testEmbeddedFunction
parameter_list|()
block|{
name|checkExp
argument_list|(
literal|"{? = call bar (?, ?)}"
argument_list|,
literal|"foo"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testColumnAliasWithAs
parameter_list|()
block|{
name|check
argument_list|(
literal|"select 1 as foo from emp"
argument_list|,
literal|"SELECT 1 AS `FOO`"
operator|+
name|NL
operator|+
literal|"FROM `EMP`"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testColumnAliasWithoutAs
parameter_list|()
block|{
name|check
argument_list|(
literal|"select 1 foo from emp"
argument_list|,
literal|"SELECT 1 AS `FOO`"
operator|+
name|NL
operator|+
literal|"FROM `EMP`"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testEmbeddedDate
parameter_list|()
block|{
name|checkExp
argument_list|(
literal|"{d '1998-10-22'}"
argument_list|,
literal|"DATE '1998-10-22'"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testEmbeddedTime
parameter_list|()
block|{
name|checkExp
argument_list|(
literal|"{t '16:22:34'}"
argument_list|,
literal|"TIME '16:22:34'"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testEmbeddedTimestamp
parameter_list|()
block|{
name|checkExp
argument_list|(
literal|"{ts '1998-10-22 16:22:34'}"
argument_list|,
literal|"TIMESTAMP '1998-10-22 16:22:34'"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testNot
parameter_list|()
block|{
name|check
argument_list|(
literal|"select not true, not false, not null, not unknown from t"
argument_list|,
literal|"SELECT (NOT TRUE), (NOT FALSE), (NOT NULL), (NOT UNKNOWN)"
operator|+
name|NL
operator|+
literal|"FROM `T`"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testBooleanPrecedenceAndAssociativity
parameter_list|()
block|{
name|check
argument_list|(
literal|"select * from t where true and false"
argument_list|,
literal|"SELECT *"
operator|+
name|NL
operator|+
literal|"FROM `T`"
operator|+
name|NL
operator|+
literal|"WHERE (TRUE AND FALSE)"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"select * from t where null or unknown and unknown"
argument_list|,
literal|"SELECT *"
operator|+
name|NL
operator|+
literal|"FROM `T`"
operator|+
name|NL
operator|+
literal|"WHERE (NULL OR (UNKNOWN AND UNKNOWN))"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"select * from t where true and (true or true) or false"
argument_list|,
literal|"SELECT *"
operator|+
name|NL
operator|+
literal|"FROM `T`"
operator|+
name|NL
operator|+
literal|"WHERE ((TRUE AND (TRUE OR TRUE)) OR FALSE)"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"select * from t where 1 and true"
argument_list|,
literal|"SELECT *"
operator|+
name|NL
operator|+
literal|"FROM `T`"
operator|+
name|NL
operator|+
literal|"WHERE (1 AND TRUE)"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testIsBooleans
parameter_list|()
block|{
name|String
index|[]
name|inOut
init|=
block|{
literal|"NULL"
block|,
literal|"TRUE"
block|,
literal|"FALSE"
block|,
literal|"UNKNOWN"
block|}
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|inOut
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|check
argument_list|(
literal|"select * from t where nOt fAlSe Is "
operator|+
name|inOut
index|[
name|i
index|]
argument_list|,
literal|"SELECT *"
operator|+
name|NL
operator|+
literal|"FROM `T`"
operator|+
name|NL
operator|+
literal|"WHERE ((NOT FALSE) IS "
operator|+
name|inOut
index|[
name|i
index|]
operator|+
literal|")"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"select * from t where c1=1.1 IS NOT "
operator|+
name|inOut
index|[
name|i
index|]
argument_list|,
literal|"SELECT *"
operator|+
name|NL
operator|+
literal|"FROM `T`"
operator|+
name|NL
operator|+
literal|"WHERE ((`C1` = 1.1) IS NOT "
operator|+
name|inOut
index|[
name|i
index|]
operator|+
literal|")"
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|testIsBooleanPrecedenceAndAssociativity
parameter_list|()
block|{
name|check
argument_list|(
literal|"select * from t where x is unknown is not unknown"
argument_list|,
literal|"SELECT *"
operator|+
name|NL
operator|+
literal|"FROM `T`"
operator|+
name|NL
operator|+
literal|"WHERE ((`X` IS UNKNOWN) IS NOT UNKNOWN)"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"select 1 from t where not true is unknown"
argument_list|,
literal|"SELECT 1"
operator|+
name|NL
operator|+
literal|"FROM `T`"
operator|+
name|NL
operator|+
literal|"WHERE ((NOT TRUE) IS UNKNOWN)"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"select * from t where x is unknown is not unknown is false is not false"
operator|+
literal|" is true is not true is null is not null"
argument_list|,
literal|"SELECT *"
operator|+
name|NL
operator|+
literal|"FROM `T`"
operator|+
name|NL
operator|+
literal|"WHERE ((((((((`X` IS UNKNOWN) IS NOT UNKNOWN) IS FALSE) IS NOT FALSE) IS TRUE) IS NOT TRUE) IS NULL) IS NOT NULL)"
argument_list|)
expr_stmt|;
comment|// combine IS postfix operators with infix (AND) and prefix (NOT) ops
name|check
argument_list|(
literal|"select * from t where x is unknown is false and x is unknown is true or not y is unknown is not null"
argument_list|,
literal|"SELECT *"
operator|+
name|NL
operator|+
literal|"FROM `T`"
operator|+
name|NL
operator|+
literal|"WHERE ((((`X` IS UNKNOWN) IS FALSE) AND ((`X` IS UNKNOWN) IS TRUE)) OR (((NOT `Y`) IS UNKNOWN) IS NOT NULL))"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testEqualNotEqual
parameter_list|()
block|{
name|checkExp
argument_list|(
literal|"'abc'=123"
argument_list|,
literal|"('abc' = 123)"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"'abc'<>123"
argument_list|,
literal|"('abc'<> 123)"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"'abc'<>123='def'<>456"
argument_list|,
literal|"((('abc'<> 123) = 'def')<> 456)"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"'abc'<>123=('def'<>456)"
argument_list|,
literal|"(('abc'<> 123) = ('def'<> 456))"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testBangEqualIsBad
parameter_list|()
block|{
comment|// Quoth www.ocelot.ca:
comment|//   "Other relators besides '=' are what you'd expect if
comment|//   you've used any programming language:> and>= and< and<=. The
comment|//   only potential point of confusion is that the operator for 'not
comment|//   equals' is<> as in BASIC. There are many texts which will tell
comment|//   you that != is SQL's not-equals operator; those texts are false;
comment|//   it's one of those unstampoutable urban myths."
name|checkFails
argument_list|(
literal|"'abc'^!^=123"
argument_list|,
literal|"Lexical error at line 1, column 6\\.  Encountered: \"!\" \\(33\\), after : \"\""
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testBetween
parameter_list|()
block|{
name|check
argument_list|(
literal|"select * from t where price between 1 and 2"
argument_list|,
literal|"SELECT *"
operator|+
name|NL
operator|+
literal|"FROM `T`"
operator|+
name|NL
operator|+
literal|"WHERE (`PRICE` BETWEEN ASYMMETRIC 1 AND 2)"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"select * from t where price between symmetric 1 and 2"
argument_list|,
literal|"SELECT *"
operator|+
name|NL
operator|+
literal|"FROM `T`"
operator|+
name|NL
operator|+
literal|"WHERE (`PRICE` BETWEEN SYMMETRIC 1 AND 2)"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"select * from t where price not between symmetric 1 and 2"
argument_list|,
literal|"SELECT *"
operator|+
name|NL
operator|+
literal|"FROM `T`"
operator|+
name|NL
operator|+
literal|"WHERE (`PRICE` NOT BETWEEN SYMMETRIC 1 AND 2)"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"select * from t where price between ASYMMETRIC 1 and 2+2*2"
argument_list|,
literal|"SELECT *"
operator|+
name|NL
operator|+
literal|"FROM `T`"
operator|+
name|NL
operator|+
literal|"WHERE (`PRICE` BETWEEN ASYMMETRIC 1 AND (2 + (2 * 2)))"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"select * from t where price> 5 and price not between 1 + 2 and 3 * 4 AnD price is null"
argument_list|,
literal|"SELECT *"
operator|+
name|NL
operator|+
literal|"FROM `T`"
operator|+
name|NL
operator|+
literal|"WHERE (((`PRICE`> 5) AND (`PRICE` NOT BETWEEN ASYMMETRIC (1 + 2) AND (3 * 4))) AND (`PRICE` IS NULL))"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"select * from t where price> 5 and price between 1 + 2 and 3 * 4 + price is null"
argument_list|,
literal|"SELECT *"
operator|+
name|NL
operator|+
literal|"FROM `T`"
operator|+
name|NL
operator|+
literal|"WHERE ((`PRICE`> 5) AND ((`PRICE` BETWEEN ASYMMETRIC (1 + 2) AND ((3 * 4) + `PRICE`)) IS NULL))"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"select * from t where price> 5 and price between 1 + 2 and 3 * 4 or price is null"
argument_list|,
literal|"SELECT *"
operator|+
name|NL
operator|+
literal|"FROM `T`"
operator|+
name|NL
operator|+
literal|"WHERE (((`PRICE`> 5) AND (`PRICE` BETWEEN ASYMMETRIC (1 + 2) AND (3 * 4))) OR (`PRICE` IS NULL))"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"values a between c and d and e and f between g and h"
argument_list|,
literal|"(VALUES (ROW((((`A` BETWEEN ASYMMETRIC `C` AND `D`) AND `E`) AND (`F` BETWEEN ASYMMETRIC `G` AND `H`)))))"
argument_list|)
expr_stmt|;
name|checkFails
argument_list|(
literal|"values a between b or c^"
argument_list|,
literal|".*BETWEEN operator has no terminating AND"
argument_list|)
expr_stmt|;
name|checkFails
argument_list|(
literal|"values a ^between^"
argument_list|,
literal|"(?s).*Encountered \"between<EOF>\" at line 1, column 10.*"
argument_list|)
expr_stmt|;
name|checkFails
argument_list|(
literal|"values a between symmetric 1^"
argument_list|,
literal|".*BETWEEN operator has no terminating AND"
argument_list|)
expr_stmt|;
comment|// precedence of BETWEEN is higher than AND and OR, but lower than '+'
name|check
argument_list|(
literal|"values a between b and c + 2 or d and e"
argument_list|,
literal|"(VALUES (ROW(((`A` BETWEEN ASYMMETRIC `B` AND (`C` + 2)) OR (`D` AND `E`)))))"
argument_list|)
expr_stmt|;
comment|// '=' and BETWEEN have same precedence, and are left-assoc
name|check
argument_list|(
literal|"values x = a between b and c = d = e"
argument_list|,
literal|"(VALUES (ROW(((((`X` = `A`) BETWEEN ASYMMETRIC `B` AND `C`) = `D`) = `E`))))"
argument_list|)
expr_stmt|;
comment|// AND doesn't match BETWEEN if it's between parentheses!
name|check
argument_list|(
literal|"values a between b or (c and d) or e and f"
argument_list|,
literal|"(VALUES (ROW((`A` BETWEEN ASYMMETRIC ((`B` OR (`C` AND `D`)) OR `E`) AND `F`))))"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testOperateOnColumn
parameter_list|()
block|{
name|check
argument_list|(
literal|"select c1*1,c2  + 2,c3/3,c4-4,c5*c4  from t"
argument_list|,
literal|"SELECT (`C1` * 1), (`C2` + 2), (`C3` / 3), (`C4` - 4), (`C5` * `C4`)"
operator|+
name|NL
operator|+
literal|"FROM `T`"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testRow
parameter_list|()
block|{
name|check
argument_list|(
literal|"select t.r.\"EXPR$1\", t.r.\"EXPR$0\" from (select (1,2) r from sales.depts) t"
argument_list|,
literal|"SELECT `T`.`R`.`EXPR$1`, `T`.`R`.`EXPR$0`"
operator|+
name|NL
operator|+
literal|"FROM (SELECT (ROW(1, 2)) AS `R`"
operator|+
name|NL
operator|+
literal|"FROM `SALES`.`DEPTS`) AS `T`"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"select t.r.\"EXPR$1\".\"EXPR$2\" "
operator|+
literal|"from (select ((1,2),(3,4,5)) r from sales.depts) t"
argument_list|,
literal|"SELECT `T`.`R`.`EXPR$1`.`EXPR$2`"
operator|+
name|NL
operator|+
literal|"FROM (SELECT (ROW((ROW(1, 2)), (ROW(3, 4, 5)))) AS `R`"
operator|+
name|NL
operator|+
literal|"FROM `SALES`.`DEPTS`) AS `T`"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"select t.r.\"EXPR$1\".\"EXPR$2\" "
operator|+
literal|"from (select ((1,2),(3,4,5,6)) r from sales.depts) t"
argument_list|,
literal|"SELECT `T`.`R`.`EXPR$1`.`EXPR$2`"
operator|+
name|NL
operator|+
literal|"FROM (SELECT (ROW((ROW(1, 2)), (ROW(3, 4, 5, 6)))) AS `R`"
operator|+
name|NL
operator|+
literal|"FROM `SALES`.`DEPTS`) AS `T`"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testOverlaps
parameter_list|()
block|{
name|checkExp
argument_list|(
literal|"(x,xx) overlaps (y,yy)"
argument_list|,
literal|"((`X`, `XX`) OVERLAPS (`Y`, `YY`))"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"(x,xx) overlaps (y,yy) or false"
argument_list|,
literal|"(((`X`, `XX`) OVERLAPS (`Y`, `YY`)) OR FALSE)"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"true and not (x,xx) overlaps (y,yy) or false"
argument_list|,
literal|"((TRUE AND (NOT ((`X`, `XX`) OVERLAPS (`Y`, `YY`)))) OR FALSE)"
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"^(x,xx,xxx) overlaps (y,yy)^ or false"
argument_list|,
literal|"(?s).*Illegal overlaps expression.*"
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"true or ^(x,xx,xxx) overlaps (y,yy,yyy)^ or false"
argument_list|,
literal|"(?s).*Illegal overlaps expression.*"
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"^(x,xx) overlaps (y,yy,yyy)^ or false"
argument_list|,
literal|"(?s).*Illegal overlaps expression.*"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testIsDistinctFrom
parameter_list|()
block|{
name|check
argument_list|(
literal|"select x is distinct from y from t"
argument_list|,
literal|"SELECT (`X` IS DISTINCT FROM `Y`)"
operator|+
name|NL
operator|+
literal|"FROM `T`"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"select * from t where x is distinct from y"
argument_list|,
literal|"SELECT *"
operator|+
name|NL
operator|+
literal|"FROM `T`"
operator|+
name|NL
operator|+
literal|"WHERE (`X` IS DISTINCT FROM `Y`)"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"select * from t where x is distinct from (4,5,6)"
argument_list|,
literal|"SELECT *"
operator|+
name|NL
operator|+
literal|"FROM `T`"
operator|+
name|NL
operator|+
literal|"WHERE (`X` IS DISTINCT FROM (ROW(4, 5, 6)))"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"select * from t where true is distinct from true"
argument_list|,
literal|"SELECT *"
operator|+
name|NL
operator|+
literal|"FROM `T`"
operator|+
name|NL
operator|+
literal|"WHERE (TRUE IS DISTINCT FROM TRUE)"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"select * from t where true is distinct from true is true"
argument_list|,
literal|"SELECT *"
operator|+
name|NL
operator|+
literal|"FROM `T`"
operator|+
name|NL
operator|+
literal|"WHERE ((TRUE IS DISTINCT FROM TRUE) IS TRUE)"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testIsNotDistinct
parameter_list|()
block|{
name|check
argument_list|(
literal|"select x is not distinct from y from t"
argument_list|,
literal|"SELECT (`X` IS NOT DISTINCT FROM `Y`)"
operator|+
name|NL
operator|+
literal|"FROM `T`"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"select * from t where true is not distinct from true"
argument_list|,
literal|"SELECT *"
operator|+
name|NL
operator|+
literal|"FROM `T`"
operator|+
name|NL
operator|+
literal|"WHERE (TRUE IS NOT DISTINCT FROM TRUE)"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testCast
parameter_list|()
block|{
name|checkExp
argument_list|(
literal|"cast(x as boolean)"
argument_list|,
literal|"CAST(`X` AS BOOLEAN)"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"cast(x as integer)"
argument_list|,
literal|"CAST(`X` AS INTEGER)"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"cast(x as varchar(1))"
argument_list|,
literal|"CAST(`X` AS VARCHAR(1))"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"cast(x as date)"
argument_list|,
literal|"CAST(`X` AS DATE)"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"cast(x as time)"
argument_list|,
literal|"CAST(`X` AS TIME)"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"cast(x as timestamp)"
argument_list|,
literal|"CAST(`X` AS TIMESTAMP)"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"cast(x as time(0))"
argument_list|,
literal|"CAST(`X` AS TIME(0))"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"cast(x as timestamp(0))"
argument_list|,
literal|"CAST(`X` AS TIMESTAMP(0))"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"cast(x as decimal(1,1))"
argument_list|,
literal|"CAST(`X` AS DECIMAL(1, 1))"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"cast(x as char(1))"
argument_list|,
literal|"CAST(`X` AS CHAR(1))"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"cast(x as binary(1))"
argument_list|,
literal|"CAST(`X` AS BINARY(1))"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"cast(x as varbinary(1))"
argument_list|,
literal|"CAST(`X` AS VARBINARY(1))"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"cast(x as tinyint)"
argument_list|,
literal|"CAST(`X` AS TINYINT)"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"cast(x as smallint)"
argument_list|,
literal|"CAST(`X` AS SMALLINT)"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"cast(x as bigint)"
argument_list|,
literal|"CAST(`X` AS BIGINT)"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"cast(x as real)"
argument_list|,
literal|"CAST(`X` AS REAL)"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"cast(x as double)"
argument_list|,
literal|"CAST(`X` AS DOUBLE)"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"cast(x as decimal)"
argument_list|,
literal|"CAST(`X` AS DECIMAL)"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"cast(x as decimal(0))"
argument_list|,
literal|"CAST(`X` AS DECIMAL(0))"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"cast(x as decimal(1,2))"
argument_list|,
literal|"CAST(`X` AS DECIMAL(1, 2))"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"cast('foo' as bar)"
argument_list|,
literal|"CAST('foo' AS `BAR`)"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testCastFails
parameter_list|()
block|{
block|}
specifier|public
name|void
name|testLikeAndSimilar
parameter_list|()
block|{
name|check
argument_list|(
literal|"select * from t where x like '%abc%'"
argument_list|,
literal|"SELECT *"
operator|+
name|NL
operator|+
literal|"FROM `T`"
operator|+
name|NL
operator|+
literal|"WHERE (`X` LIKE '%abc%')"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"select * from t where x+1 not siMilaR to '%abc%' ESCAPE 'e'"
argument_list|,
literal|"SELECT *"
operator|+
name|NL
operator|+
literal|"FROM `T`"
operator|+
name|NL
operator|+
literal|"WHERE ((`X` + 1) NOT SIMILAR TO '%abc%' ESCAPE 'e')"
argument_list|)
expr_stmt|;
comment|// LIKE has higher precedence than AND
name|check
argument_list|(
literal|"select * from t where price> 5 and x+2*2 like y*3+2 escape (select*from t)"
argument_list|,
literal|"SELECT *"
operator|+
name|NL
operator|+
literal|"FROM `T`"
operator|+
name|NL
operator|+
literal|"WHERE ((`PRICE`> 5) AND ((`X` + (2 * 2)) LIKE ((`Y` * 3) + 2) ESCAPE (SELECT *"
operator|+
name|NL
operator|+
literal|"FROM `T`)))"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"values a and b like c"
argument_list|,
literal|"(VALUES (ROW((`A` AND (`B` LIKE `C`)))))"
argument_list|)
expr_stmt|;
comment|// LIKE has higher precedence than AND
name|check
argument_list|(
literal|"values a and b like c escape d and e"
argument_list|,
literal|"(VALUES (ROW(((`A` AND (`B` LIKE `C` ESCAPE `D`)) AND `E`))))"
argument_list|)
expr_stmt|;
comment|// LIKE has same precedence as '='; LIKE is right-assoc, '=' is left
name|check
argument_list|(
literal|"values a = b like c = d"
argument_list|,
literal|"(VALUES (ROW(((`A` = `B`) LIKE (`C` = `D`)))))"
argument_list|)
expr_stmt|;
comment|// Nested LIKE
name|check
argument_list|(
literal|"values a like b like c escape d"
argument_list|,
literal|"(VALUES (ROW((`A` LIKE (`B` LIKE `C` ESCAPE `D`)))))"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"values a like b like c escape d and false"
argument_list|,
literal|"(VALUES (ROW(((`A` LIKE (`B` LIKE `C` ESCAPE `D`)) AND FALSE))))"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"values a like b like c like d escape e escape f"
argument_list|,
literal|"(VALUES (ROW((`A` LIKE (`B` LIKE (`C` LIKE `D` ESCAPE `E`) ESCAPE `F`)))))"
argument_list|)
expr_stmt|;
comment|// Mixed LIKE and SIMILAR TO
name|check
argument_list|(
literal|"values a similar to b like c similar to d escape e escape f"
argument_list|,
literal|"(VALUES (ROW((`A` SIMILAR TO (`B` LIKE (`C` SIMILAR TO `D` ESCAPE `E`) ESCAPE `F`)))))"
argument_list|)
expr_stmt|;
name|checkFails
argument_list|(
literal|"select * from t where ^escape^ 'e'"
argument_list|,
literal|"(?s).*Encountered \"escape\" at line 1, column 23.*"
argument_list|)
expr_stmt|;
comment|// LIKE with +
name|check
argument_list|(
literal|"values a like b + c escape d"
argument_list|,
literal|"(VALUES (ROW((`A` LIKE (`B` + `C`) ESCAPE `D`))))"
argument_list|)
expr_stmt|;
comment|// LIKE with ||
name|check
argument_list|(
literal|"values a like b || c escape d"
argument_list|,
literal|"(VALUES (ROW((`A` LIKE (`B` || `C`) ESCAPE `D`))))"
argument_list|)
expr_stmt|;
comment|// ESCAPE with no expression
name|checkFails
argument_list|(
literal|"values a like ^escape^ d"
argument_list|,
literal|"(?s).*Encountered \"escape\" at line 1, column 15.*"
argument_list|)
expr_stmt|;
comment|// ESCAPE with no expression
name|checkFails
argument_list|(
literal|"values a like b || c ^escape^ and false"
argument_list|,
literal|"(?s).*Encountered \"escape and\" at line 1, column 22.*"
argument_list|)
expr_stmt|;
comment|// basic SIMILAR TO
name|check
argument_list|(
literal|"select * from t where x similar to '%abc%'"
argument_list|,
literal|"SELECT *"
operator|+
name|NL
operator|+
literal|"FROM `T`"
operator|+
name|NL
operator|+
literal|"WHERE (`X` SIMILAR TO '%abc%')"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"select * from t where x+1 not siMilaR to '%abc%' ESCAPE 'e'"
argument_list|,
literal|"SELECT *"
operator|+
name|NL
operator|+
literal|"FROM `T`"
operator|+
name|NL
operator|+
literal|"WHERE ((`X` + 1) NOT SIMILAR TO '%abc%' ESCAPE 'e')"
argument_list|)
expr_stmt|;
comment|// SIMILAR TO has higher precedence than AND
name|check
argument_list|(
literal|"select * from t where price> 5 and x+2*2 SIMILAR TO y*3+2 escape (select*from t)"
argument_list|,
literal|"SELECT *"
operator|+
name|NL
operator|+
literal|"FROM `T`"
operator|+
name|NL
operator|+
literal|"WHERE ((`PRICE`> 5) AND ((`X` + (2 * 2)) SIMILAR TO ((`Y` * 3) + 2) ESCAPE (SELECT *"
operator|+
name|NL
operator|+
literal|"FROM `T`)))"
argument_list|)
expr_stmt|;
comment|// Mixed LIKE and SIMILAR TO
name|check
argument_list|(
literal|"values a similar to b like c similar to d escape e escape f"
argument_list|,
literal|"(VALUES (ROW((`A` SIMILAR TO (`B` LIKE (`C` SIMILAR TO `D` ESCAPE `E`) ESCAPE `F`)))))"
argument_list|)
expr_stmt|;
comment|// SIMILAR TO with subquery
name|check
argument_list|(
literal|"values a similar to (select * from t where a like b escape c) escape d"
argument_list|,
literal|"(VALUES (ROW((`A` SIMILAR TO (SELECT *"
operator|+
name|NL
operator|+
literal|"FROM `T`"
operator|+
name|NL
operator|+
literal|"WHERE (`A` LIKE `B` ESCAPE `C`)) ESCAPE `D`))))"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testFoo
parameter_list|()
block|{
block|}
specifier|public
name|void
name|testArthimeticOperators
parameter_list|()
block|{
name|checkExp
argument_list|(
literal|"1-2+3*4/5/6-7"
argument_list|,
literal|"(((1 - 2) + (((3 * 4) / 5) / 6)) - 7)"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"power(2,3)"
argument_list|,
literal|"POWER(2, 3)"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"aBs(-2.3e-2)"
argument_list|,
literal|"ABS(-2.3E-2)"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"MOD(5             ,\t\f\r\n2)"
argument_list|,
literal|"MOD(5, 2)"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"ln(5.43  )"
argument_list|,
literal|"LN(5.43)"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"log10(- -.2  )"
argument_list|,
literal|"LOG10((- -0.2))"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testExists
parameter_list|()
block|{
name|check
argument_list|(
literal|"select * from dept where exists (select 1 from emp where emp.deptno = dept.deptno)"
argument_list|,
literal|"SELECT *"
operator|+
name|NL
operator|+
literal|"FROM `DEPT`"
operator|+
name|NL
operator|+
literal|"WHERE (EXISTS (SELECT 1"
operator|+
name|NL
operator|+
literal|"FROM `EMP`"
operator|+
name|NL
operator|+
literal|"WHERE (`EMP`.`DEPTNO` = `DEPT`.`DEPTNO`)))"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testExistsInWhere
parameter_list|()
block|{
name|check
argument_list|(
literal|"select * from emp where 1 = 2 and exists (select 1 from dept) and 3 = 4"
argument_list|,
literal|"SELECT *"
operator|+
name|NL
operator|+
literal|"FROM `EMP`"
operator|+
name|NL
operator|+
literal|"WHERE (((1 = 2) AND (EXISTS (SELECT 1"
operator|+
name|NL
operator|+
literal|"FROM `DEPT`))) AND (3 = 4))"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testFromWithAs
parameter_list|()
block|{
name|check
argument_list|(
literal|"select 1 from emp as e where 1"
argument_list|,
literal|"SELECT 1"
operator|+
name|NL
operator|+
literal|"FROM `EMP` AS `E`"
operator|+
name|NL
operator|+
literal|"WHERE 1"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testConcat
parameter_list|()
block|{
name|checkExp
argument_list|(
literal|"'a' || 'b'"
argument_list|,
literal|"('a' || 'b')"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testReverseSolidus
parameter_list|()
block|{
name|checkExp
argument_list|(
literal|"'\\'"
argument_list|,
literal|"'\\'"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testSubstring
parameter_list|()
block|{
name|checkExp
argument_list|(
literal|"substring('a' \n  FROM \t  1)"
argument_list|,
literal|"SUBSTRING('a' FROM 1)"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"substring('a' FROM 1 FOR 3)"
argument_list|,
literal|"SUBSTRING('a' FROM 1 FOR 3)"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"substring('a' FROM 'reg' FOR '\\')"
argument_list|,
literal|"SUBSTRING('a' FROM 'reg' FOR '\\')"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"substring('a', 'reg', '\\')"
argument_list|,
literal|"SUBSTRING('a' FROM 'reg' FOR '\\')"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"substring('a', 1, 2)"
argument_list|,
literal|"SUBSTRING('a' FROM 1 FOR 2)"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"substring('a' , 1)"
argument_list|,
literal|"SUBSTRING('a' FROM 1)"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testFunction
parameter_list|()
block|{
name|check
argument_list|(
literal|"select substring('Eggs and ham', 1, 3 + 2) || ' benedict' from emp"
argument_list|,
literal|"SELECT (SUBSTRING('Eggs and ham' FROM 1 FOR (3 + 2)) || ' benedict')"
operator|+
name|NL
operator|+
literal|"FROM `EMP`"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"log10(1)\r\n+power(2, mod(\r\n3\n\t\t\f\n,ln(4))*log10(5)-6*log10(7/abs(8)+9))*power(10,11)"
argument_list|,
literal|"(LOG10(1) + (POWER(2, ((MOD(3, LN(4)) * LOG10(5)) - (6 * LOG10(((7 / ABS(8)) + 9))))) * POWER(10, 11)))"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testFunctionWithDistinct
parameter_list|()
block|{
name|checkExp
argument_list|(
literal|"count(DISTINCT 1)"
argument_list|,
literal|"COUNT(DISTINCT 1)"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"count(ALL 1)"
argument_list|,
literal|"COUNT(ALL 1)"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"count(1)"
argument_list|,
literal|"COUNT(1)"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"select count(1), count(distinct 2) from emp"
argument_list|,
literal|"SELECT COUNT(1), COUNT(DISTINCT 2)"
operator|+
name|NL
operator|+
literal|"FROM `EMP`"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testFunctionInFunction
parameter_list|()
block|{
name|checkExp
argument_list|(
literal|"ln(power(2,2))"
argument_list|,
literal|"LN(POWER(2, 2))"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testGroup
parameter_list|()
block|{
name|check
argument_list|(
literal|"select deptno, min(foo) as x from emp group by deptno, gender"
argument_list|,
literal|"SELECT `DEPTNO`, MIN(`FOO`) AS `X`"
operator|+
name|NL
operator|+
literal|"FROM `EMP`"
operator|+
name|NL
operator|+
literal|"GROUP BY `DEPTNO`, `GENDER`"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testGroupEmpty
parameter_list|()
block|{
name|check
argument_list|(
literal|"select count(*) from emp group by ()"
argument_list|,
name|TestUtil
operator|.
name|fold
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"SELECT COUNT(*)"
block|,
literal|"FROM `EMP`"
block|,
literal|"GROUP BY ()"
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"select count(*) from emp group by () having 1 = 2 order by 3"
argument_list|,
name|TestUtil
operator|.
name|fold
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"SELECT COUNT(*)"
block|,
literal|"FROM `EMP`"
block|,
literal|"GROUP BY ()"
block|,
literal|"HAVING (1 = 2)"
block|,
literal|"ORDER BY 3"
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|checkFails
argument_list|(
literal|"select 1 from emp group by ()^,^ x"
argument_list|,
literal|"(?s)Encountered \\\",\\\" at .*"
argument_list|)
expr_stmt|;
name|checkFails
argument_list|(
literal|"select 1 from emp group by x, (^)^"
argument_list|,
literal|"(?s)Encountered \"\\)\" at .*"
argument_list|)
expr_stmt|;
comment|// parentheses do not an empty GROUP BY make
name|check
argument_list|(
literal|"select 1 from emp group by (empno + deptno)"
argument_list|,
name|TestUtil
operator|.
name|fold
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"SELECT 1"
block|,
literal|"FROM `EMP`"
block|,
literal|"GROUP BY (`EMPNO` + `DEPTNO`)"
block|}
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testHavingAfterGroup
parameter_list|()
block|{
name|check
argument_list|(
literal|"select deptno from emp group by deptno, emp having count(*)> 5 and 1 = 2 order by 5, 2"
argument_list|,
literal|"SELECT `DEPTNO`"
operator|+
name|NL
operator|+
literal|"FROM `EMP`"
operator|+
name|NL
operator|+
literal|"GROUP BY `DEPTNO`, `EMP`"
operator|+
name|NL
operator|+
literal|"HAVING ((COUNT(*)> 5) AND (1 = 2))"
operator|+
name|NL
operator|+
literal|"ORDER BY 5, 2"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testHavingBeforeGroupFails
parameter_list|()
block|{
name|checkFails
argument_list|(
literal|"select deptno from emp having count(*)> 5 and deptno< 4 ^group^ by deptno, emp"
argument_list|,
literal|"(?s).*Encountered \"group\" at .*"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testHavingNoGroup
parameter_list|()
block|{
name|check
argument_list|(
literal|"select deptno from emp having count(*)> 5"
argument_list|,
literal|"SELECT `DEPTNO`"
operator|+
name|NL
operator|+
literal|"FROM `EMP`"
operator|+
name|NL
operator|+
literal|"HAVING (COUNT(*)> 5)"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testIdentifier
parameter_list|()
block|{
name|checkExp
argument_list|(
literal|"ab"
argument_list|,
literal|"`AB`"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"     \"a  \"\" b!c\""
argument_list|,
literal|"`a  \" b!c`"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"\"x`y`z\""
argument_list|,
literal|"`x``y``z`"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testInList
parameter_list|()
block|{
name|check
argument_list|(
literal|"select * from emp where deptno in (10, 20) and gender = 'F'"
argument_list|,
literal|"SELECT *"
operator|+
name|NL
operator|+
literal|"FROM `EMP`"
operator|+
name|NL
operator|+
literal|"WHERE ((`DEPTNO` IN (10, 20)) AND (`GENDER` = 'F'))"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testInListEmptyFails
parameter_list|()
block|{
name|checkFails
argument_list|(
literal|"select * from emp where deptno in (^)^ and gender = 'F'"
argument_list|,
literal|"(?s).*Encountered \"\\)\" at line 1, column 36\\..*"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testInQuery
parameter_list|()
block|{
name|check
argument_list|(
literal|"select * from emp where deptno in (select deptno from dept)"
argument_list|,
literal|"SELECT *"
operator|+
name|NL
operator|+
literal|"FROM `EMP`"
operator|+
name|NL
operator|+
literal|"WHERE (`DEPTNO` IN (SELECT `DEPTNO`"
operator|+
name|NL
operator|+
literal|"FROM `DEPT`))"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testInSetop
parameter_list|()
block|{
name|check
argument_list|(
literal|"select * from emp where deptno in ((select deptno from dept union select * from dept)"
operator|+
literal|"except select * from dept) and false"
argument_list|,
name|TestUtil
operator|.
name|fold
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"SELECT *"
block|,
literal|"FROM `EMP`"
block|,
literal|"WHERE ((`DEPTNO` IN ((SELECT `DEPTNO`"
block|,
literal|"FROM `DEPT`"
block|,
literal|"UNION"
block|,
literal|"SELECT *"
block|,
literal|"FROM `DEPT`)"
block|,
literal|"EXCEPT"
block|,
literal|"SELECT *"
block|,
literal|"FROM `DEPT`)) AND FALSE)"
block|}
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testUnion
parameter_list|()
block|{
name|check
argument_list|(
literal|"select * from a union select * from a"
argument_list|,
name|TestUtil
operator|.
name|fold
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"(SELECT *"
block|,
literal|"FROM `A`"
block|,
literal|"UNION"
block|,
literal|"SELECT *"
block|,
literal|"FROM `A`)"
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"select * from a union all select * from a"
argument_list|,
name|TestUtil
operator|.
name|fold
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"(SELECT *"
block|,
literal|"FROM `A`"
block|,
literal|"UNION ALL"
block|,
literal|"SELECT *"
block|,
literal|"FROM `A`)"
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"select * from a union distinct select * from a"
argument_list|,
name|TestUtil
operator|.
name|fold
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"(SELECT *"
block|,
literal|"FROM `A`"
block|,
literal|"UNION"
block|,
literal|"SELECT *"
block|,
literal|"FROM `A`)"
block|}
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testUnionOrder
parameter_list|()
block|{
name|check
argument_list|(
literal|"select a, b from t "
operator|+
literal|"union all "
operator|+
literal|"select x, y from u "
operator|+
literal|"order by 1 asc, 2 desc"
argument_list|,
name|TestUtil
operator|.
name|fold
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"(SELECT `A`, `B`"
block|,
literal|"FROM `T`"
block|,
literal|"UNION ALL"
block|,
literal|"SELECT `X`, `Y`"
block|,
literal|"FROM `U`)"
block|,
literal|"ORDER BY 1, 2 DESC"
block|}
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testUnionOfNonQueryFails
parameter_list|()
block|{
name|checkFails
argument_list|(
literal|"select 1 from emp union ^2^ + 5"
argument_list|,
literal|"Non-query expression encountered in illegal context"
argument_list|)
expr_stmt|;
block|}
comment|/**      * In modern SQL, a query can occur almost everywhere that an expression      * can. This test tests the few exceptions.      */
specifier|public
name|void
name|testQueryInIllegalContext
parameter_list|()
block|{
name|checkFails
argument_list|(
literal|"select 0, multiset[^(^select * from emp), 2] from dept"
argument_list|,
literal|"Query expression encountered in illegal context"
argument_list|)
expr_stmt|;
name|checkFails
argument_list|(
literal|"select 0, multiset[1, ^(^select * from emp), 2, 3] from dept"
argument_list|,
literal|"Query expression encountered in illegal context"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testExcept
parameter_list|()
block|{
name|check
argument_list|(
literal|"select * from a except select * from a"
argument_list|,
name|TestUtil
operator|.
name|fold
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"(SELECT *"
block|,
literal|"FROM `A`"
block|,
literal|"EXCEPT"
block|,
literal|"SELECT *"
block|,
literal|"FROM `A`)"
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"select * from a except all select * from a"
argument_list|,
name|TestUtil
operator|.
name|fold
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"(SELECT *"
block|,
literal|"FROM `A`"
block|,
literal|"EXCEPT ALL"
block|,
literal|"SELECT *"
block|,
literal|"FROM `A`)"
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"select * from a except distinct select * from a"
argument_list|,
name|TestUtil
operator|.
name|fold
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"(SELECT *"
block|,
literal|"FROM `A`"
block|,
literal|"EXCEPT"
block|,
literal|"SELECT *"
block|,
literal|"FROM `A`)"
block|}
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testIntersect
parameter_list|()
block|{
name|check
argument_list|(
literal|"select * from a intersect select * from a"
argument_list|,
name|TestUtil
operator|.
name|fold
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"(SELECT *"
block|,
literal|"FROM `A`"
block|,
literal|"INTERSECT"
block|,
literal|"SELECT *"
block|,
literal|"FROM `A`)"
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"select * from a intersect all select * from a"
argument_list|,
name|TestUtil
operator|.
name|fold
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"(SELECT *"
block|,
literal|"FROM `A`"
block|,
literal|"INTERSECT ALL"
block|,
literal|"SELECT *"
block|,
literal|"FROM `A`)"
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"select * from a intersect distinct select * from a"
argument_list|,
name|TestUtil
operator|.
name|fold
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"(SELECT *"
block|,
literal|"FROM `A`"
block|,
literal|"INTERSECT"
block|,
literal|"SELECT *"
block|,
literal|"FROM `A`)"
block|}
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testJoinCross
parameter_list|()
block|{
name|check
argument_list|(
literal|"select * from a as a2 cross join b"
argument_list|,
name|TestUtil
operator|.
name|fold
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"SELECT *"
block|,
literal|"FROM `A` AS `A2`"
block|,
literal|"CROSS JOIN `B`"
block|}
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testJoinOn
parameter_list|()
block|{
name|check
argument_list|(
literal|"select * from a left join b on 1 = 1 and 2 = 2 where 3 = 3"
argument_list|,
name|TestUtil
operator|.
name|fold
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"SELECT *"
block|,
literal|"FROM `A`"
block|,
literal|"LEFT JOIN `B` ON ((1 = 1) AND (2 = 2))"
block|,
literal|"WHERE (3 = 3)"
block|}
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testOuterJoinNoiseword
parameter_list|()
block|{
name|check
argument_list|(
literal|"select * from a left outer join b on 1 = 1 and 2 = 2 where 3 = 3"
argument_list|,
literal|"SELECT *"
operator|+
name|NL
operator|+
literal|"FROM `A`"
operator|+
name|NL
operator|+
literal|"LEFT JOIN `B` ON ((1 = 1) AND (2 = 2))"
operator|+
name|NL
operator|+
literal|"WHERE (3 = 3)"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testJoinQuery
parameter_list|()
block|{
name|check
argument_list|(
literal|"select * from a join (select * from b) as b2 on true"
argument_list|,
literal|"SELECT *"
operator|+
name|NL
operator|+
literal|"FROM `A`"
operator|+
name|NL
operator|+
literal|"INNER JOIN (SELECT *"
operator|+
name|NL
operator|+
literal|"FROM `B`) AS `B2` ON TRUE"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testFullInnerJoinFails
parameter_list|()
block|{
comment|// cannot have more than one of INNER, FULL, LEFT, RIGHT, CROSS
name|checkFails
argument_list|(
literal|"select * from a full ^inner^ join b"
argument_list|,
literal|"(\\s|.)*Encountered \"inner\" at line 1, column 22(\\s|.)*"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testFullOuterJoin
parameter_list|()
block|{
comment|// OUTER is an optional extra to LEFT, RIGHT, or FULL
name|check
argument_list|(
literal|"select * from a full outer join b"
argument_list|,
name|TestUtil
operator|.
name|fold
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"SELECT *"
block|,
literal|"FROM `A`"
block|,
literal|"FULL JOIN `B`"
block|}
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testInnerOuterJoinFails
parameter_list|()
block|{
name|checkFails
argument_list|(
literal|"select * from a inner ^outer^ join b"
argument_list|,
literal|"(\\s|.)*Encountered \"outer\" at line 1, column 23(\\s|.)*"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|_testJoinAssociativity
parameter_list|()
block|{
comment|// joins are left-associative
comment|// 1. no parens needed
name|check
argument_list|(
literal|"select * from (a natural left join b) left join c on b.c1 = c.c1"
argument_list|,
literal|"SELECT *"
operator|+
name|NL
operator|+
literal|"FROM (`A` NATURAL LEFT JOIN `B`) LEFT JOIN `C` ON (`B`.`C1` = `C`.`C1`)"
operator|+
name|NL
argument_list|)
expr_stmt|;
comment|// 2. parens needed
name|check
argument_list|(
literal|"select * from a natural left join (b left join c on b.c1 = c.c1)"
argument_list|,
literal|"SELECT *"
operator|+
name|NL
operator|+
literal|"FROM (`A` NATURAL LEFT JOIN `B`) LEFT JOIN `C` ON (`B`.`C1` = `C`.`C1`)"
operator|+
name|NL
argument_list|)
expr_stmt|;
comment|// 3. same as 1
name|check
argument_list|(
literal|"select * from a natural left join b left join c on b.c1 = c.c1"
argument_list|,
literal|"SELECT *"
operator|+
name|NL
operator|+
literal|"FROM (`A` NATURAL LEFT JOIN `B`) LEFT JOIN `C` ON (`B`.`C1` = `C`.`C1`)"
operator|+
name|NL
argument_list|)
expr_stmt|;
block|}
comment|// Note: "select * from a natural cross join b" is actually illegal SQL
comment|// ("cross" is the only join type which cannot be modified with the
comment|// "natural") but the parser allows it; we and catch it at validate time
specifier|public
name|void
name|testNaturalCrossJoin
parameter_list|()
block|{
name|check
argument_list|(
literal|"select * from a natural cross join b"
argument_list|,
name|TestUtil
operator|.
name|fold
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"SELECT *"
block|,
literal|"FROM `A`"
block|,
literal|"NATURAL CROSS JOIN `B`"
block|}
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testJoinUsing
parameter_list|()
block|{
name|check
argument_list|(
literal|"select * from a join b using (x)"
argument_list|,
name|TestUtil
operator|.
name|fold
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"SELECT *"
block|,
literal|"FROM `A`"
block|,
literal|"INNER JOIN `B` USING (`X`)"
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|checkFails
argument_list|(
literal|"select * from a join b using (^)^ where c = d"
argument_list|,
literal|"(?s).*Encountered \"[)]\" at line 1, column 31.*"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testTableSample
parameter_list|()
block|{
name|check
argument_list|(
literal|"select * from ("
operator|+
literal|"  select * "
operator|+
literal|"  from emp "
operator|+
literal|"  join dept on emp.deptno = dept.deptno"
operator|+
literal|"  where gender = 'F'"
operator|+
literal|"  order by sal) tablesample substitute('medium')"
argument_list|,
name|TestUtil
operator|.
name|fold
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"SELECT *"
block|,
literal|"FROM (SELECT *"
block|,
literal|"FROM `EMP`"
block|,
literal|"INNER JOIN `DEPT` ON (`EMP`.`DEPTNO` = `DEPT`.`DEPTNO`)"
block|,
literal|"WHERE (`GENDER` = 'F')"
block|,
literal|"ORDER BY `SAL`) TABLESAMPLE SUBSTITUTE('MEDIUM')"
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"select * "
operator|+
literal|"from emp as x tablesample substitute('medium') "
operator|+
literal|"join dept tablesample substitute('lar' /* split */ 'ge') on x.deptno = dept.deptno"
argument_list|,
name|TestUtil
operator|.
name|fold
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"SELECT *"
block|,
literal|"FROM `EMP` AS `X` TABLESAMPLE SUBSTITUTE('MEDIUM')"
block|,
literal|"INNER JOIN `DEPT` TABLESAMPLE SUBSTITUTE('LARGE') ON (`X`.`DEPTNO` = `DEPT`.`DEPTNO`)"
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"select * "
operator|+
literal|"from emp as x tablesample bernoulli(50)"
argument_list|,
name|TestUtil
operator|.
name|fold
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"SELECT *"
block|,
literal|"FROM `EMP` AS `X` TABLESAMPLE BERNOULLI(50.0)"
block|}
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testLiteral
parameter_list|()
block|{
name|checkExpSame
argument_list|(
literal|"'foo'"
argument_list|)
expr_stmt|;
name|checkExpSame
argument_list|(
literal|"100"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"select 1 as one, 'x' as x, null as n from emp"
argument_list|,
literal|"SELECT 1 AS `ONE`, 'x' AS `X`, NULL AS `N`"
operator|+
name|NL
operator|+
literal|"FROM `EMP`"
argument_list|)
expr_stmt|;
comment|// Even though it looks like a date, it's just a string.
name|checkExp
argument_list|(
literal|"'2004-06-01'"
argument_list|,
literal|"'2004-06-01'"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"-.25"
argument_list|,
literal|"-0.25"
argument_list|)
expr_stmt|;
name|checkExpSame
argument_list|(
literal|"TIMESTAMP '2004-06-01 15:55:55'"
argument_list|)
expr_stmt|;
name|checkExpSame
argument_list|(
literal|"TIMESTAMP '2004-06-01 15:55:55.900'"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"TIMESTAMP '2004-06-01 15:55:55.1234'"
argument_list|,
literal|"TIMESTAMP '2004-06-01 15:55:55.123'"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"TIMESTAMP '2004-06-01 15:55:55.1236'"
argument_list|,
literal|"TIMESTAMP '2004-06-01 15:55:55.124'"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"TIMESTAMP '2004-06-01 15:55:55.9999'"
argument_list|,
literal|"TIMESTAMP '2004-06-01 15:55:56.000'"
argument_list|)
expr_stmt|;
name|checkExpSame
argument_list|(
literal|"NULL"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testContinuedLiteral
parameter_list|()
block|{
name|checkExp
argument_list|(
literal|"'abba'\n'abba'"
argument_list|,
name|TestUtil
operator|.
name|fold
argument_list|(
literal|"'abba'\n'abba'"
argument_list|)
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"'abba'\n'0001'"
argument_list|,
name|TestUtil
operator|.
name|fold
argument_list|(
literal|"'abba'\n'0001'"
argument_list|)
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"N'yabba'\n'dabba'\n'doo'"
argument_list|,
name|TestUtil
operator|.
name|fold
argument_list|(
literal|"_ISO-8859-1'yabba'\n'dabba'\n'doo'"
argument_list|)
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"_iso-8859-1'yabba'\n'dabba'\n'don''t'"
argument_list|,
name|TestUtil
operator|.
name|fold
argument_list|(
literal|"_ISO-8859-1'yabba'\n'dabba'\n'don''t'"
argument_list|)
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"x'01aa'\n'03ff'"
argument_list|,
name|TestUtil
operator|.
name|fold
argument_list|(
literal|"X'01AA'\n'03FF'"
argument_list|)
argument_list|)
expr_stmt|;
comment|// a bad hexstring
name|checkFails
argument_list|(
literal|"x'01aa'\n^'vvvv'^"
argument_list|,
literal|"Binary literal string must contain only characters '0' - '9', 'A' - 'F'"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testMixedFrom
parameter_list|()
block|{
comment|// REVIEW: Is this syntax even valid?
name|check
argument_list|(
literal|"select * from a join b using (x), c join d using (y)"
argument_list|,
name|TestUtil
operator|.
name|fold
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"SELECT *"
block|,
literal|"FROM `A`"
block|,
literal|"INNER JOIN `B` USING (`X`),"
block|,
literal|"`C`"
block|,
literal|"INNER JOIN `D` USING (`Y`)"
block|}
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testMixedStar
parameter_list|()
block|{
name|check
argument_list|(
literal|"select emp.*, 1 as foo from emp, dept"
argument_list|,
name|TestUtil
operator|.
name|fold
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"SELECT `EMP`.*, 1 AS `FOO`"
block|,
literal|"FROM `EMP`,"
block|,
literal|"`DEPT`"
block|}
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testNotExists
parameter_list|()
block|{
name|check
argument_list|(
literal|"select * from dept where not not exists (select * from emp) and true"
argument_list|,
literal|"SELECT *"
operator|+
name|NL
operator|+
literal|"FROM `DEPT`"
operator|+
name|NL
operator|+
literal|"WHERE ((NOT (NOT (EXISTS (SELECT *"
operator|+
name|NL
operator|+
literal|"FROM `EMP`)))) AND TRUE)"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testOrder
parameter_list|()
block|{
name|check
argument_list|(
literal|"select * from emp order by empno, gender desc, deptno asc, empno asc, name desc"
argument_list|,
name|TestUtil
operator|.
name|fold
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"SELECT *"
block|,
literal|"FROM `EMP`"
block|,
literal|"ORDER BY `EMPNO`, `GENDER` DESC, `DEPTNO`, `EMPNO`, `NAME` DESC"
block|}
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testOrderInternal
parameter_list|()
block|{
name|check
argument_list|(
literal|"(select * from emp order by empno) union select * from emp"
argument_list|,
name|TestUtil
operator|.
name|fold
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"((SELECT *"
block|,
literal|"FROM `EMP`"
block|,
literal|"ORDER BY `EMPNO`)"
block|,
literal|"UNION"
block|,
literal|"SELECT *"
block|,
literal|"FROM `EMP`)"
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"select * from (select * from t order by x, y) where a = b"
argument_list|,
name|TestUtil
operator|.
name|fold
argument_list|(
literal|"SELECT *\n"
operator|+
literal|"FROM (SELECT *\n"
operator|+
literal|"FROM `T`\n"
operator|+
literal|"ORDER BY `X`, `Y`)\n"
operator|+
literal|"WHERE (`A` = `B`)"
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testOrderIllegalInExpression
parameter_list|()
block|{
name|check
argument_list|(
literal|"select (select 1 from foo order by x,y) from t where a = b"
argument_list|,
name|TestUtil
operator|.
name|fold
argument_list|(
literal|"SELECT (SELECT 1\n"
operator|+
literal|"FROM `FOO`\n"
operator|+
literal|"ORDER BY `X`, `Y`)\n"
operator|+
literal|"FROM `T`\n"
operator|+
literal|"WHERE (`A` = `B`)"
argument_list|)
argument_list|)
expr_stmt|;
name|checkFails
argument_list|(
literal|"select (1 ^order^ by x, y) from t where a = b"
argument_list|,
literal|"ORDER BY unexpected"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testSqlInlineComment
parameter_list|()
block|{
name|check
argument_list|(
literal|"select 1 from t --this is a comment"
operator|+
name|NL
argument_list|,
literal|"SELECT 1"
operator|+
name|NL
operator|+
literal|"FROM `T`"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"select 1 from t--"
operator|+
name|NL
argument_list|,
literal|"SELECT 1"
operator|+
name|NL
operator|+
literal|"FROM `T`"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"select 1 from t--this is a comment"
operator|+
name|NL
operator|+
literal|"where a>b-- this is comment"
operator|+
name|NL
argument_list|,
literal|"SELECT 1"
operator|+
name|NL
operator|+
literal|"FROM `T`"
operator|+
name|NL
operator|+
literal|"WHERE (`A`> `B`)"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testMultilineComment
parameter_list|()
block|{
comment|// on single line
name|check
argument_list|(
literal|"select 1 /* , 2 */, 3 from t"
argument_list|,
literal|"SELECT 1, 3"
operator|+
name|NL
operator|+
literal|"FROM `T`"
argument_list|)
expr_stmt|;
comment|// on several lines
name|check
argument_list|(
literal|"select /* 1,"
operator|+
name|NL
operator|+
literal|" 2, "
operator|+
name|NL
operator|+
literal|" */ 3 from t"
argument_list|,
literal|"SELECT 3"
operator|+
name|NL
operator|+
literal|"FROM `T`"
argument_list|)
expr_stmt|;
comment|// stuff inside comment
name|check
argument_list|(
literal|"values ( /** 1, 2 + ** */ 3)"
argument_list|,
literal|"(VALUES (ROW(3)))"
argument_list|)
expr_stmt|;
comment|// comment in string is preserved
name|check
argument_list|(
literal|"values ('a string with /* a comment */ in it')"
argument_list|,
literal|"(VALUES (ROW('a string with /* a comment */ in it')))"
argument_list|)
expr_stmt|;
comment|// SQL:2003, 5.2, syntax rule # 8 "There shall be no<separator>
comment|// separating the<minus sign>s of a<simple comment introducer>".
name|check
argument_list|(
literal|"values (- -1"
operator|+
name|NL
operator|+
literal|")"
argument_list|,
literal|"(VALUES (ROW((- -1))))"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"values (--1+"
operator|+
name|NL
operator|+
literal|"2)"
argument_list|,
literal|"(VALUES (ROW(2)))"
argument_list|)
expr_stmt|;
comment|// end of multiline commment without start
if|if
condition|(
name|Bug
operator|.
name|Frg73Fixed
condition|)
block|{
name|checkFails
argument_list|(
literal|"values (1 */ 2)"
argument_list|,
literal|"xx"
argument_list|)
expr_stmt|;
block|}
comment|// SQL:2003, 5.2, syntax rule #10 "Within a<bracket comment context>,
comment|// any<solidus> immediately followed by an<asterisk> without any
comment|// intervening<separator> shall be considered to be the<bracketed
comment|// comment introducer> for a<separator> that is a<bracketed
comment|// comment>".
comment|// comment inside a comment
comment|// Spec is unclear what should happen, but currently it crashes the
comment|// parser, and that's bad
if|if
condition|(
name|Bug
operator|.
name|Frg73Fixed
condition|)
block|{
name|check
argument_list|(
literal|"values (1 + /* comment /* inner comment */ */ 2)"
argument_list|,
literal|"xx"
argument_list|)
expr_stmt|;
block|}
comment|// single-line comment inside multiline comment is illegal
comment|//
comment|// SQL-2003, 5.2: "Note 63 - Conforming programs should not place
comment|//<simple comment> within a<bracketed comment> because if such a
comment|//<simple comment> contains the sequence of characeters "*/" without
comment|// a preceding "/*" in the same<simple comment>, it will prematurely
comment|// terminate the containing<bracketed comment>.
if|if
condition|(
name|Bug
operator|.
name|Frg73Fixed
condition|)
block|{
name|checkFails
argument_list|(
literal|"values /* multiline contains -- singline */ "
operator|+
name|NL
operator|+
literal|" (1)"
argument_list|,
literal|"xxx"
argument_list|)
expr_stmt|;
block|}
comment|// non-terminated multiline comment inside singleline comment
if|if
condition|(
name|Bug
operator|.
name|Frg73Fixed
condition|)
block|{
comment|// Test should fail, and it does, but it should give "*/" as the
comment|// erroneous token.
name|checkFails
argument_list|(
literal|"values ( -- rest of line /* a comment  "
operator|+
name|NL
operator|+
literal|" 1, ^*/^ 2)"
argument_list|,
literal|"Encountered \"/\\*\" at"
argument_list|)
expr_stmt|;
block|}
name|check
argument_list|(
literal|"values (1 + /* comment -- rest of line"
operator|+
name|NL
operator|+
literal|" rest of comment */ 2)"
argument_list|,
literal|"(VALUES (ROW((1 + 2))))"
argument_list|)
expr_stmt|;
comment|// multiline comment inside singleline comment
name|check
argument_list|(
literal|"values -- rest of line /* a comment */ "
operator|+
name|NL
operator|+
literal|"(1)"
argument_list|,
literal|"(VALUES (ROW(1)))"
argument_list|)
expr_stmt|;
comment|// non-terminated multiline comment inside singleline comment
name|check
argument_list|(
literal|"values -- rest of line /* a comment  "
operator|+
name|NL
operator|+
literal|"(1)"
argument_list|,
literal|"(VALUES (ROW(1)))"
argument_list|)
expr_stmt|;
comment|// even if comment abuts the tokens at either end, it becomes a space
name|check
argument_list|(
literal|"values ('abc'/* a comment*/'def')"
argument_list|,
name|TestUtil
operator|.
name|fold
argument_list|(
literal|"(VALUES (ROW('abc'\n'def')))"
argument_list|)
argument_list|)
expr_stmt|;
comment|// comment which starts as soon as it has begun
name|check
argument_list|(
literal|"values /**/ (1)"
argument_list|,
literal|"(VALUES (ROW(1)))"
argument_list|)
expr_stmt|;
block|}
comment|// expressions
specifier|public
name|void
name|testParseNumber
parameter_list|()
block|{
comment|//Exacts
name|checkExp
argument_list|(
literal|"1"
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"+1."
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"-1"
argument_list|,
literal|"-1"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"- -1"
argument_list|,
literal|"(- -1)"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"1.0"
argument_list|,
literal|"1.0"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"-3.2"
argument_list|,
literal|"-3.2"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"1."
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|".1"
argument_list|,
literal|"0.1"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"2500000000"
argument_list|,
literal|"2500000000"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"5000000000"
argument_list|,
literal|"5000000000"
argument_list|)
expr_stmt|;
comment|//Approxs
name|checkExp
argument_list|(
literal|"1e1"
argument_list|,
literal|"1E1"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"+1e1"
argument_list|,
literal|"1E1"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"1.1e1"
argument_list|,
literal|"1.1E1"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"1.1e+1"
argument_list|,
literal|"1.1E1"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"1.1e-1"
argument_list|,
literal|"1.1E-1"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"+1.1e-1"
argument_list|,
literal|"1.1E-1"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"1.E3"
argument_list|,
literal|"1E3"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"1.e-3"
argument_list|,
literal|"1E-3"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"1.e+3"
argument_list|,
literal|"1E3"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|".5E3"
argument_list|,
literal|"5E2"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"+.5e3"
argument_list|,
literal|"5E2"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"-.5E3"
argument_list|,
literal|"-5E2"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|".5e-32"
argument_list|,
literal|"5E-33"
argument_list|)
expr_stmt|;
comment|//Mix integer/decimals/approx
name|checkExp
argument_list|(
literal|"3. + 2"
argument_list|,
literal|"(3 + 2)"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"1++2+3"
argument_list|,
literal|"((1 + 2) + 3)"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"1- -2"
argument_list|,
literal|"(1 - -2)"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"1++2.3e-4++.5e-6++.7++8"
argument_list|,
literal|"((((1 + 2.3E-4) + 5E-7) + 0.7) + 8)"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"1- -2.3e-4 - -.5e-6  -"
operator|+
name|NL
operator|+
literal|"-.7++8"
argument_list|,
literal|"((((1 - -2.3E-4) - -5E-7) - -0.7) + 8)"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"1+-2.*-3.e-1/-4"
argument_list|,
literal|"(1 + ((-2 * -3E-1) / -4))"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testParseNumberFails
parameter_list|()
block|{
name|checkFails
argument_list|(
literal|"SELECT 0.5e1^.1^ from t"
argument_list|,
literal|"(?s).*Encountered .*\\.1.* at line 1.*"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testMinusPrefixInExpression
parameter_list|()
block|{
name|checkExp
argument_list|(
literal|"-(1+2)"
argument_list|,
literal|"(- (1 + 2))"
argument_list|)
expr_stmt|;
block|}
comment|// operator precedence
specifier|public
name|void
name|testPrecedence0
parameter_list|()
block|{
name|checkExp
argument_list|(
literal|"1 + 2 * 3 * 4 + 5"
argument_list|,
literal|"((1 + ((2 * 3) * 4)) + 5)"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testPrecedence1
parameter_list|()
block|{
name|checkExp
argument_list|(
literal|"1 + 2 * (3 * (4 + 5))"
argument_list|,
literal|"(1 + (2 * (3 * (4 + 5))))"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testPrecedence2
parameter_list|()
block|{
name|checkExp
argument_list|(
literal|"- - 1"
argument_list|,
literal|"(- -1)"
argument_list|)
expr_stmt|;
comment|// two prefices
block|}
specifier|public
name|void
name|testPrecedence3
parameter_list|()
block|{
name|checkExp
argument_list|(
literal|"- 1 is null"
argument_list|,
literal|"(-1 IS NULL)"
argument_list|)
expr_stmt|;
comment|// prefix vs. postfix
block|}
specifier|public
name|void
name|testPrecedence4
parameter_list|()
block|{
name|checkExp
argument_list|(
literal|"1 - -2"
argument_list|,
literal|"(1 - -2)"
argument_list|)
expr_stmt|;
comment|// infix, prefix '-'
block|}
specifier|public
name|void
name|testPrecedence5
parameter_list|()
block|{
name|checkExp
argument_list|(
literal|"1++2"
argument_list|,
literal|"(1 + 2)"
argument_list|)
expr_stmt|;
comment|// infix, prefix '+'
name|checkExp
argument_list|(
literal|"1+ +2"
argument_list|,
literal|"(1 + 2)"
argument_list|)
expr_stmt|;
comment|// infix, prefix '+'
block|}
specifier|public
name|void
name|testPrecedenceSetOps
parameter_list|()
block|{
name|check
argument_list|(
literal|"select * from a union "
operator|+
literal|"select * from b intersect "
operator|+
literal|"select * from c intersect "
operator|+
literal|"select * from d except "
operator|+
literal|"select * from e except "
operator|+
literal|"select * from f union "
operator|+
literal|"select * from g"
argument_list|,
name|TestUtil
operator|.
name|fold
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"((((SELECT *"
block|,
literal|"FROM `A`"
block|,
literal|"UNION"
block|,
literal|"((SELECT *"
block|,
literal|"FROM `B`"
block|,
literal|"INTERSECT"
block|,
literal|"SELECT *"
block|,
literal|"FROM `C`)"
block|,
literal|"INTERSECT"
block|,
literal|"SELECT *"
block|,
literal|"FROM `D`))"
block|,
literal|"EXCEPT"
block|,
literal|"SELECT *"
block|,
literal|"FROM `E`)"
block|,
literal|"EXCEPT"
block|,
literal|"SELECT *"
block|,
literal|"FROM `F`)"
block|,
literal|"UNION"
block|,
literal|"SELECT *"
block|,
literal|"FROM `G`)"
block|}
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testQueryInFrom
parameter_list|()
block|{
comment|// one query with 'as', the other without
name|check
argument_list|(
literal|"select * from (select * from emp) as e join (select * from dept) d"
argument_list|,
name|TestUtil
operator|.
name|fold
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"SELECT *"
block|,
literal|"FROM (SELECT *"
block|,
literal|"FROM `EMP`) AS `E`"
block|,
literal|"INNER JOIN (SELECT *"
block|,
literal|"FROM `DEPT`) AS `D`"
block|}
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testQuotesInString
parameter_list|()
block|{
name|checkExp
argument_list|(
literal|"'a''b'"
argument_list|,
literal|"'a''b'"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"'''x'"
argument_list|,
literal|"'''x'"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"''"
argument_list|,
literal|"''"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"'Quoted strings aren''t \"hard\"'"
argument_list|,
literal|"'Quoted strings aren''t \"hard\"'"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testScalarQueryInWhere
parameter_list|()
block|{
name|check
argument_list|(
literal|"select * from emp where 3 = (select count(*) from dept where dept.deptno = emp.deptno)"
argument_list|,
literal|"SELECT *"
operator|+
name|NL
operator|+
literal|"FROM `EMP`"
operator|+
name|NL
operator|+
literal|"WHERE (3 = (SELECT COUNT(*)"
operator|+
name|NL
operator|+
literal|"FROM `DEPT`"
operator|+
name|NL
operator|+
literal|"WHERE (`DEPT`.`DEPTNO` = `EMP`.`DEPTNO`)))"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testScalarQueryInSelect
parameter_list|()
block|{
name|check
argument_list|(
literal|"select x, (select count(*) from dept where dept.deptno = emp.deptno) from emp"
argument_list|,
name|TestUtil
operator|.
name|fold
argument_list|(
literal|"SELECT `X`, (SELECT COUNT(*)\n"
operator|+
literal|"FROM `DEPT`\n"
operator|+
literal|"WHERE (`DEPT`.`DEPTNO` = `EMP`.`DEPTNO`))\n"
operator|+
literal|"FROM `EMP`"
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testSelectList
parameter_list|()
block|{
name|check
argument_list|(
literal|"select * from emp, dept"
argument_list|,
name|TestUtil
operator|.
name|fold
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"SELECT *"
block|,
literal|"FROM `EMP`,"
block|,
literal|"`DEPT`"
block|}
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testSelectList3
parameter_list|()
block|{
name|check
argument_list|(
literal|"select 1, emp.*, 2 from emp"
argument_list|,
literal|"SELECT 1, `EMP`.*, 2"
operator|+
name|NL
operator|+
literal|"FROM `EMP`"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testSelectList4
parameter_list|()
block|{
name|checkFails
argument_list|(
literal|"select ^from^ emp"
argument_list|,
literal|"(?s).*Encountered \"from\" at line .*"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testStar
parameter_list|()
block|{
name|check
argument_list|(
literal|"select * from emp"
argument_list|,
literal|"SELECT *"
operator|+
name|NL
operator|+
literal|"FROM `EMP`"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testSelectDistinct
parameter_list|()
block|{
name|check
argument_list|(
literal|"select distinct foo from bar"
argument_list|,
literal|"SELECT DISTINCT `FOO`"
operator|+
name|NL
operator|+
literal|"FROM `BAR`"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testSelectAll
parameter_list|()
block|{
comment|// "unique" is the default -- so drop the keyword
name|check
argument_list|(
literal|"select * from (select all foo from bar) as xyz"
argument_list|,
literal|"SELECT *"
operator|+
name|NL
operator|+
literal|"FROM (SELECT ALL `FOO`"
operator|+
name|NL
operator|+
literal|"FROM `BAR`) AS `XYZ`"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testWhere
parameter_list|()
block|{
name|check
argument_list|(
literal|"select * from emp where empno> 5 and gender = 'F'"
argument_list|,
literal|"SELECT *"
operator|+
name|NL
operator|+
literal|"FROM `EMP`"
operator|+
name|NL
operator|+
literal|"WHERE ((`EMPNO`> 5) AND (`GENDER` = 'F'))"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testNestedSelect
parameter_list|()
block|{
name|check
argument_list|(
literal|"select * from (select * from emp)"
argument_list|,
literal|"SELECT *"
operator|+
name|NL
operator|+
literal|"FROM (SELECT *"
operator|+
name|NL
operator|+
literal|"FROM `EMP`)"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testValues
parameter_list|()
block|{
name|check
argument_list|(
literal|"values(1,'two')"
argument_list|,
literal|"(VALUES (ROW(1, 'two')))"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testValuesExplicitRow
parameter_list|()
block|{
name|check
argument_list|(
literal|"values row(1,'two')"
argument_list|,
literal|"(VALUES (ROW(1, 'two')))"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testFromValues
parameter_list|()
block|{
name|check
argument_list|(
literal|"select * from (values(1,'two'), 3, (4, 'five'))"
argument_list|,
literal|"SELECT *"
operator|+
name|NL
operator|+
literal|"FROM (VALUES (ROW(1, 'two')), (ROW(3)), (ROW(4, 'five')))"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testFromValuesWithoutParens
parameter_list|()
block|{
name|checkFails
argument_list|(
literal|"select 1 from ^values^('x')"
argument_list|,
literal|"Encountered \"values\" at line 1, column 15\\."
operator|+
name|NL
operator|+
literal|"Was expecting one of:"
operator|+
name|NL
operator|+
literal|"    \"LATERAL\" \\.\\.\\."
operator|+
name|NL
operator|+
literal|"    \"TABLE\" \\.\\.\\."
operator|+
name|NL
operator|+
literal|"    \"UNNEST\" \\.\\.\\."
operator|+
name|NL
operator|+
literal|"    \"\\(\" \\.\\.\\."
operator|+
name|NL
operator|+
literal|"<IDENTIFIER> \\.\\.\\."
operator|+
name|NL
operator|+
literal|"<QUOTED_IDENTIFIER> \\.\\.\\."
operator|+
name|NL
operator|+
literal|"<UNICODE_QUOTED_IDENTIFIER> \\.\\.\\."
operator|+
name|NL
operator|+
literal|"    "
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testEmptyValues
parameter_list|()
block|{
name|checkFails
argument_list|(
literal|"select * from (values(^)^)"
argument_list|,
literal|"(?s).*Encountered \"\\)\" at line .*"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testExplicitTable
parameter_list|()
block|{
name|check
argument_list|(
literal|"table emp"
argument_list|,
literal|"(TABLE `EMP`)"
argument_list|)
expr_stmt|;
name|checkFails
argument_list|(
literal|"table ^123^"
argument_list|,
literal|"Encountered \"123\" at line 1, column 7\\."
operator|+
name|NL
operator|+
literal|"Was expecting one of:"
operator|+
name|NL
operator|+
literal|"<IDENTIFIER> \\.\\.\\."
operator|+
name|NL
operator|+
literal|"<QUOTED_IDENTIFIER> \\.\\.\\."
operator|+
name|NL
operator|+
literal|"<UNICODE_QUOTED_IDENTIFIER> \\.\\.\\."
operator|+
name|NL
operator|+
literal|"    "
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testExplicitTableOrdered
parameter_list|()
block|{
name|check
argument_list|(
literal|"table emp order by name"
argument_list|,
name|TestUtil
operator|.
name|fold
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"(TABLE `EMP`)"
block|,
literal|"ORDER BY `NAME`"
block|}
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testSelectFromExplicitTable
parameter_list|()
block|{
name|check
argument_list|(
literal|"select * from (table emp)"
argument_list|,
literal|"SELECT *"
operator|+
name|NL
operator|+
literal|"FROM (TABLE `EMP`)"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testSelectFromBareExplicitTableFails
parameter_list|()
block|{
name|checkFails
argument_list|(
literal|"select * from table ^emp^"
argument_list|,
literal|"(?s).*Encountered \"emp\" at line 1, column 21.*"
argument_list|)
expr_stmt|;
name|checkFails
argument_list|(
literal|"select * from (table ^(^select empno from emp))"
argument_list|,
literal|"(?s)Encountered \"\\(\".*"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testCollectionTable
parameter_list|()
block|{
name|check
argument_list|(
literal|"select * from table(ramp(3, 4))"
argument_list|,
name|TestUtil
operator|.
name|fold
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"SELECT *"
block|,
literal|"FROM TABLE(`RAMP`(3, 4))"
block|}
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testCollectionTableWithCursorParam
parameter_list|()
block|{
name|check
argument_list|(
literal|"select * from table(dedup(cursor(select * from emps),'name'))"
argument_list|,
name|TestUtil
operator|.
name|fold
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"SELECT *"
block|,
literal|"FROM TABLE(`DEDUP`((CURSOR ((SELECT *"
block|,
literal|"FROM `EMPS`))), 'name'))"
block|}
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testCollectionTableWithColumnListParam
parameter_list|()
block|{
name|check
argument_list|(
literal|"select * from table(dedup(cursor(select * from emps),"
operator|+
literal|"row(empno, name)))"
argument_list|,
name|TestUtil
operator|.
name|fold
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"SELECT *"
block|,
literal|"FROM TABLE(`DEDUP`((CURSOR ((SELECT *"
block|,
literal|"FROM `EMPS`))), (ROW(`EMPNO`, `NAME`))))"
block|}
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testIllegalCursors
parameter_list|()
block|{
name|checkFails
argument_list|(
literal|"select ^cursor^(select * from emps) from emps"
argument_list|,
literal|"CURSOR expression encountered in illegal context"
argument_list|)
expr_stmt|;
name|checkFails
argument_list|(
literal|"call p(^cursor^(select * from emps))"
argument_list|,
literal|"CURSOR expression encountered in illegal context"
argument_list|)
expr_stmt|;
name|checkFails
argument_list|(
literal|"select f(^cursor^(select * from emps)) from emps"
argument_list|,
literal|"CURSOR expression encountered in illegal context"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testExplain
parameter_list|()
block|{
name|check
argument_list|(
literal|"explain plan for select * from emps"
argument_list|,
name|TestUtil
operator|.
name|fold
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"EXPLAIN PLAN INCLUDING ATTRIBUTES WITH IMPLEMENTATION FOR"
block|,
literal|"SELECT *"
block|,
literal|"FROM `EMPS`"
block|}
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testExplainWithImpl
parameter_list|()
block|{
name|check
argument_list|(
literal|"explain plan with implementation for select * from emps"
argument_list|,
name|TestUtil
operator|.
name|fold
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"EXPLAIN PLAN INCLUDING ATTRIBUTES WITH IMPLEMENTATION FOR"
block|,
literal|"SELECT *"
block|,
literal|"FROM `EMPS`"
block|}
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testExplainWithoutImpl
parameter_list|()
block|{
name|check
argument_list|(
literal|"explain plan without implementation for select * from emps"
argument_list|,
name|TestUtil
operator|.
name|fold
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"EXPLAIN PLAN INCLUDING ATTRIBUTES WITHOUT IMPLEMENTATION FOR"
block|,
literal|"SELECT *"
block|,
literal|"FROM `EMPS`"
block|}
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testExplainWithType
parameter_list|()
block|{
name|check
argument_list|(
literal|"explain plan with type for (values (true))"
argument_list|,
name|TestUtil
operator|.
name|fold
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"EXPLAIN PLAN INCLUDING ATTRIBUTES WITH TYPE FOR"
block|,
literal|"(VALUES (ROW(TRUE)))"
block|}
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testInsertSelect
parameter_list|()
block|{
name|check
argument_list|(
literal|"insert into emps select * from emps"
argument_list|,
name|TestUtil
operator|.
name|fold
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"INSERT INTO `EMPS`"
block|,
literal|"(SELECT *"
block|,
literal|"FROM `EMPS`)"
block|}
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testInsertUnion
parameter_list|()
block|{
name|check
argument_list|(
literal|"insert into emps select * from emps1 union select * from emps2"
argument_list|,
name|TestUtil
operator|.
name|fold
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"INSERT INTO `EMPS`"
block|,
literal|"(SELECT *"
block|,
literal|"FROM `EMPS1`"
block|,
literal|"UNION"
block|,
literal|"SELECT *"
block|,
literal|"FROM `EMPS2`)"
block|}
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testInsertValues
parameter_list|()
block|{
name|check
argument_list|(
literal|"insert into emps values (1,'Fredkin')"
argument_list|,
literal|"INSERT INTO `EMPS`"
operator|+
name|NL
operator|+
literal|"(VALUES (ROW(1, 'Fredkin')))"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testInsertColumnList
parameter_list|()
block|{
name|check
argument_list|(
literal|"insert into emps(x,y) select * from emps"
argument_list|,
name|TestUtil
operator|.
name|fold
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"INSERT INTO `EMPS` (`X`, `Y`)"
block|,
literal|"(SELECT *"
block|,
literal|"FROM `EMPS`)"
block|}
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testExplainInsert
parameter_list|()
block|{
name|check
argument_list|(
literal|"explain plan for insert into emps1 select * from emps2"
argument_list|,
literal|"EXPLAIN PLAN INCLUDING ATTRIBUTES WITH IMPLEMENTATION FOR"
operator|+
name|NL
operator|+
literal|"INSERT INTO `EMPS1`"
operator|+
name|NL
operator|+
literal|"(SELECT *"
operator|+
name|NL
operator|+
literal|"FROM `EMPS2`)"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testDelete
parameter_list|()
block|{
name|check
argument_list|(
literal|"delete from emps"
argument_list|,
literal|"DELETE FROM `EMPS`"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testDeleteWhere
parameter_list|()
block|{
name|check
argument_list|(
literal|"delete from emps where empno=12"
argument_list|,
literal|"DELETE FROM `EMPS`"
operator|+
name|NL
operator|+
literal|"WHERE (`EMPNO` = 12)"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testMergeSelectSource
parameter_list|()
block|{
name|check
argument_list|(
literal|"merge into emps e "
operator|+
literal|"using (select * from tempemps where deptno is null) t "
operator|+
literal|"on e.empno = t.empno "
operator|+
literal|"when matched then update "
operator|+
literal|"set name = t.name, deptno = t.deptno, salary = t.salary * .1 "
operator|+
literal|"when not matched then insert (name, dept, salary) "
operator|+
literal|"values(t.name, 10, t.salary * .15)"
argument_list|,
name|TestUtil
operator|.
name|fold
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"MERGE INTO `EMPS` AS `E`"
block|,
literal|"USING (SELECT *"
block|,
literal|"FROM `TEMPEMPS`"
block|,
literal|"WHERE (`DEPTNO` IS NULL)) AS `T`"
block|,
literal|"ON (`E`.`EMPNO` = `T`.`EMPNO`)"
block|,
literal|"WHEN MATCHED THEN UPDATE SET `NAME` = `T`.`NAME`"
block|,
literal|", `DEPTNO` = `T`.`DEPTNO`"
block|,
literal|", `SALARY` = (`T`.`SALARY` * 0.1)"
block|,
literal|"WHEN NOT MATCHED THEN INSERT (`NAME`, `DEPT`, `SALARY`) "
operator|+
literal|"(VALUES (ROW(`T`.`NAME`, 10, (`T`.`SALARY` * 0.15))))"
block|}
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testMergeTableRefSource
parameter_list|()
block|{
name|check
argument_list|(
literal|"merge into emps e "
operator|+
literal|"using tempemps as t "
operator|+
literal|"on e.empno = t.empno "
operator|+
literal|"when matched then update "
operator|+
literal|"set name = t.name, deptno = t.deptno, salary = t.salary * .1 "
operator|+
literal|"when not matched then insert (name, dept, salary) "
operator|+
literal|"values(t.name, 10, t.salary * .15)"
argument_list|,
name|TestUtil
operator|.
name|fold
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"MERGE INTO `EMPS` AS `E`"
block|,
literal|"USING `TEMPEMPS` AS `T`"
block|,
literal|"ON (`E`.`EMPNO` = `T`.`EMPNO`)"
block|,
literal|"WHEN MATCHED THEN UPDATE SET `NAME` = `T`.`NAME`"
block|,
literal|", `DEPTNO` = `T`.`DEPTNO`"
block|,
literal|", `SALARY` = (`T`.`SALARY` * 0.1)"
block|,
literal|"WHEN NOT MATCHED THEN INSERT (`NAME`, `DEPT`, `SALARY`) "
operator|+
literal|"(VALUES (ROW(`T`.`NAME`, 10, (`T`.`SALARY` * 0.15))))"
block|}
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testBitStringNotImplemented
parameter_list|()
block|{
comment|// Bit-string is longer part of the SQL standard. We do not support it.
name|checkFails
argument_list|(
literal|"select B^'1011'^ || 'foobar' from (values (true))"
argument_list|,
literal|"(?s).*Encountered \"\\\\'1011\\\\'\" at line 1, column 9.*"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testHexAndBinaryString
parameter_list|()
block|{
name|checkExp
argument_list|(
literal|"x''=X'2'"
argument_list|,
literal|"(X'' = X'2')"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"x'fffff'=X''"
argument_list|,
literal|"(X'FFFFF' = X'')"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"x'1' \t\t\f\r "
operator|+
name|NL
operator|+
literal|"'2'--hi this is a comment'FF'\r\r\t\f "
operator|+
name|NL
operator|+
literal|"'34'"
argument_list|,
name|TestUtil
operator|.
name|fold
argument_list|(
literal|"X'1'\n'2'\n'34'"
argument_list|)
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"x'1' \t\t\f\r "
operator|+
name|NL
operator|+
literal|"'000'--"
operator|+
name|NL
operator|+
literal|"'01'"
argument_list|,
name|TestUtil
operator|.
name|fold
argument_list|(
literal|"X'1'\n'000'\n'01'"
argument_list|)
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"x'1234567890abcdef'=X'fFeEdDcCbBaA'"
argument_list|,
literal|"(X'1234567890ABCDEF' = X'FFEEDDCCBBAA')"
argument_list|)
expr_stmt|;
comment|// Check the inital zeroes don't get trimmed somehow
name|checkExp
argument_list|(
literal|"x'001'=X'000102'"
argument_list|,
literal|"(X'001' = X'000102')"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testHexAndBinaryStringFails
parameter_list|()
block|{
name|checkFails
argument_list|(
literal|"select ^x'FeedGoats'^ from t"
argument_list|,
literal|"Binary literal string must contain only characters '0' - '9', 'A' - 'F'"
argument_list|)
expr_stmt|;
name|checkFails
argument_list|(
literal|"select ^x'abcdefG'^ from t"
argument_list|,
literal|"Binary literal string must contain only characters '0' - '9', 'A' - 'F'"
argument_list|)
expr_stmt|;
name|checkFails
argument_list|(
literal|"select x'1' ^x'2'^ from t"
argument_list|,
literal|"(?s).*Encountered .x.*2.* at line 1, column 13.*"
argument_list|)
expr_stmt|;
comment|// valid syntax, but should fail in the validator
name|check
argument_list|(
literal|"select x'1' '2' from t"
argument_list|,
name|TestUtil
operator|.
name|fold
argument_list|(
literal|"SELECT X'1'\n'2'\nFROM `T`"
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testStringLiteral
parameter_list|()
block|{
name|checkExp
argument_list|(
literal|"_latin1'hi'"
argument_list|,
literal|"_LATIN1'hi'"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"N'is it a plane? no it''s superman!'"
argument_list|,
literal|"_ISO-8859-1'is it a plane? no it''s superman!'"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"n'lowercase n'"
argument_list|,
literal|"_ISO-8859-1'lowercase n'"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"'boring string'"
argument_list|,
literal|"'boring string'"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"_iSo-8859-1'bye'"
argument_list|,
literal|"_ISO-8859-1'bye'"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"'three' \n ' blind'\n' mice'"
argument_list|,
name|TestUtil
operator|.
name|fold
argument_list|(
literal|"'three'\n' blind'\n' mice'"
argument_list|)
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"'three' -- comment \n ' blind'\n' mice'"
argument_list|,
name|TestUtil
operator|.
name|fold
argument_list|(
literal|"'three'\n' blind'\n' mice'"
argument_list|)
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"N'bye' \t\r\f\f\n' bye'"
argument_list|,
name|TestUtil
operator|.
name|fold
argument_list|(
literal|"_ISO-8859-1'bye'\n' bye'"
argument_list|)
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"_iso-8859-1'bye' \n\n--\n-- this is a comment\n' bye'"
argument_list|,
name|TestUtil
operator|.
name|fold
argument_list|(
literal|"_ISO-8859-1'bye'\n' bye'"
argument_list|)
argument_list|)
expr_stmt|;
comment|// newline in string literal
name|checkExp
argument_list|(
literal|"'foo\rbar'"
argument_list|,
literal|"'foo\rbar'"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"'foo\nbar'"
argument_list|,
literal|"'foo\nbar'"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"'foo\r\nbar'"
argument_list|,
literal|"'foo\r\nbar'"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testStringLiteralFails
parameter_list|()
block|{
name|checkFails
argument_list|(
literal|"select N ^'space'^"
argument_list|,
literal|"(?s).*Encountered .*space.* at line 1, column ...*"
argument_list|)
expr_stmt|;
name|checkFails
argument_list|(
literal|"select _latin1 \n^'newline'^"
argument_list|,
literal|"(?s).*Encountered.*newline.* at line 2, column ...*"
argument_list|)
expr_stmt|;
name|checkFails
argument_list|(
literal|"select ^_unknown-charset''^ from (values(true))"
argument_list|,
literal|"Unknown character set 'unknown-charset'"
argument_list|)
expr_stmt|;
comment|// valid syntax, but should give a validator error
name|check
argument_list|(
literal|"select N'1' '2' from t"
argument_list|,
name|TestUtil
operator|.
name|fold
argument_list|(
literal|"SELECT _ISO-8859-1'1'\n'2'\n"
operator|+
literal|"FROM `T`"
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testStringLiteralChain
parameter_list|()
block|{
specifier|final
name|String
name|fooBar
init|=
name|TestUtil
operator|.
name|fold
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"'foo'"
block|,
literal|"'bar'"
block|}
argument_list|)
decl_stmt|;
specifier|final
name|String
name|fooBarBaz
init|=
name|TestUtil
operator|.
name|fold
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"'foo'"
block|,
literal|"'bar'"
block|,
literal|"'baz'"
block|}
argument_list|)
decl_stmt|;
name|checkExp
argument_list|(
literal|"   'foo'\r'bar'"
argument_list|,
name|fooBar
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"   'foo'\r\n'bar'"
argument_list|,
name|fooBar
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"   'foo'\r\n\r\n'bar'  \n   'baz'"
argument_list|,
name|fooBarBaz
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"   'foo' /* a comment */ 'bar'"
argument_list|,
name|fooBar
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"   'foo' -- a comment\r\n 'bar'"
argument_list|,
name|fooBar
argument_list|)
expr_stmt|;
comment|// String literals not separated by comment or newline are OK in
comment|// parser, should fail in validator.
name|checkExp
argument_list|(
literal|"   'foo' 'bar'"
argument_list|,
name|fooBar
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testCaseExpression
parameter_list|()
block|{
comment|//implicit simple else null case
name|checkExp
argument_list|(
literal|"case \t col1 when 1 then 'one' end"
argument_list|,
literal|"(CASE WHEN (`COL1` = 1) THEN 'one' ELSE NULL END)"
argument_list|)
expr_stmt|;
comment|//implicit searched elee null case
name|checkExp
argument_list|(
literal|"case when nbr is false then 'one' end"
argument_list|,
literal|"(CASE WHEN (`NBR` IS FALSE) THEN 'one' ELSE NULL END)"
argument_list|)
expr_stmt|;
comment|//multiple whens
name|checkExp
argument_list|(
literal|"case col1 when \n1.2 then 'one' when 2 then 'two' else 'three' end"
argument_list|,
literal|"(CASE WHEN (`COL1` = 1.2) THEN 'one' WHEN (`COL1` = 2) THEN 'two' ELSE 'three' END)"
argument_list|)
expr_stmt|;
comment|// subqueries as case expression operands
name|checkExp
argument_list|(
literal|"case (select * from emp) when 1 then 2 end"
argument_list|,
name|TestUtil
operator|.
name|fold
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"(CASE WHEN ((SELECT *"
block|,
literal|"FROM `EMP`) = 1) THEN 2 ELSE NULL END)"
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"case 1 when (select * from emp) then 2 end"
argument_list|,
name|TestUtil
operator|.
name|fold
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"(CASE WHEN (1 = (SELECT *"
block|,
literal|"FROM `EMP`)) THEN 2 ELSE NULL END)"
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"case 1 when 2 then (select * from emp) end"
argument_list|,
name|TestUtil
operator|.
name|fold
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"(CASE WHEN (1 = 2) THEN (SELECT *"
block|,
literal|"FROM `EMP`) ELSE NULL END)"
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"case 1 when 2 then 3 else (select * from emp) end"
argument_list|,
name|TestUtil
operator|.
name|fold
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"(CASE WHEN (1 = 2) THEN 3 ELSE (SELECT *"
block|,
literal|"FROM `EMP`) END)"
block|}
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testCaseExpressionFails
parameter_list|()
block|{
comment|// Missing 'END'
name|checkFails
argument_list|(
literal|"select case col1 when 1 then 'one' ^from^ t"
argument_list|,
literal|"(?s).*from.*"
argument_list|)
expr_stmt|;
comment|// Wrong 'WHEN'
name|checkFails
argument_list|(
literal|"select case col1 ^when1^ then 'one' end from t"
argument_list|,
literal|"(?s).*when1.*"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testNullIf
parameter_list|()
block|{
name|checkExp
argument_list|(
literal|"nullif(v1,v2)"
argument_list|,
literal|"NULLIF(`V1`, `V2`)"
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"1 ^+^ nullif + 3"
argument_list|,
literal|"(?s)Encountered \"\\+ nullif \\+\" at line 1, column 3.*"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testCoalesce
parameter_list|()
block|{
name|checkExp
argument_list|(
literal|"coalesce(v1)"
argument_list|,
literal|"COALESCE(`V1`)"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"coalesce(v1,v2)"
argument_list|,
literal|"COALESCE(`V1`, `V2`)"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"coalesce(v1,v2,v3)"
argument_list|,
literal|"COALESCE(`V1`, `V2`, `V3`)"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testLiteralCollate
parameter_list|()
block|{
if|if
condition|(
operator|!
name|Bug
operator|.
name|Frg78Fixed
condition|)
block|{
return|return;
block|}
name|checkExp
argument_list|(
literal|"'string' collate latin1$sv_SE$mega_strength"
argument_list|,
literal|"'string' COLLATE ISO-8859-1$sv_SE$mega_strength"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"'a long '\n'string' collate latin1$sv_SE$mega_strength"
argument_list|,
literal|"'a long ' 'string' COLLATE ISO-8859-1$sv_SE$mega_strength"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"x collate iso-8859-6$ar_LB$1"
argument_list|,
literal|"`X` COLLATE ISO-8859-6$ar_LB$1"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"x.y.z collate shift_jis$ja_JP$2"
argument_list|,
literal|"`X`.`Y`.`Z` COLLATE SHIFT_JIS$ja_JP$2"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"'str1'='str2' collate latin1$sv_SE"
argument_list|,
literal|"('str1' = 'str2' COLLATE ISO-8859-1$sv_SE$primary)"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"'str1' collate latin1$sv_SE>'str2'"
argument_list|,
literal|"('str1' COLLATE ISO-8859-1$sv_SE$primary> 'str2')"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"'str1' collate latin1$sv_SE<='str2' collate latin1$sv_FI"
argument_list|,
literal|"('str1' COLLATE ISO-8859-1$sv_SE$primary<= 'str2' COLLATE ISO-8859-1$sv_FI$primary)"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testCharLength
parameter_list|()
block|{
name|checkExp
argument_list|(
literal|"char_length('string')"
argument_list|,
literal|"CHAR_LENGTH('string')"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"character_length('string')"
argument_list|,
literal|"CHARACTER_LENGTH('string')"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testPosition
parameter_list|()
block|{
name|checkExp
argument_list|(
literal|"posiTion('mouse' in 'house')"
argument_list|,
literal|"POSITION('mouse' IN 'house')"
argument_list|)
expr_stmt|;
block|}
comment|// check date/time functions.
specifier|public
name|void
name|testTimeDate
parameter_list|()
block|{
comment|// CURRENT_TIME - returns time w/ timezone
name|checkExp
argument_list|(
literal|"CURRENT_TIME(3)"
argument_list|,
literal|"CURRENT_TIME(3)"
argument_list|)
expr_stmt|;
comment|// checkFails("SELECT CURRENT_TIME() FROM foo", "SELECT CURRENT_TIME()
comment|// FROM `FOO`");
name|checkExp
argument_list|(
literal|"CURRENT_TIME"
argument_list|,
literal|"`CURRENT_TIME`"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"CURRENT_TIME(x+y)"
argument_list|,
literal|"CURRENT_TIME((`X` + `Y`))"
argument_list|)
expr_stmt|;
comment|// LOCALTIME returns time w/o TZ
name|checkExp
argument_list|(
literal|"LOCALTIME(3)"
argument_list|,
literal|"LOCALTIME(3)"
argument_list|)
expr_stmt|;
comment|// checkFails("SELECT LOCALTIME() FROM foo", "SELECT LOCALTIME() FROM
comment|// `FOO`");
name|checkExp
argument_list|(
literal|"LOCALTIME"
argument_list|,
literal|"`LOCALTIME`"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"LOCALTIME(x+y)"
argument_list|,
literal|"LOCALTIME((`X` + `Y`))"
argument_list|)
expr_stmt|;
comment|// LOCALTIMESTAMP - returns timestamp w/o TZ
name|checkExp
argument_list|(
literal|"LOCALTIMESTAMP(3)"
argument_list|,
literal|"LOCALTIMESTAMP(3)"
argument_list|)
expr_stmt|;
comment|// checkFails("SELECT LOCALTIMESTAMP() FROM foo", "SELECT
comment|// LOCALTIMESTAMP() FROM `FOO`");
name|checkExp
argument_list|(
literal|"LOCALTIMESTAMP"
argument_list|,
literal|"`LOCALTIMESTAMP`"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"LOCALTIMESTAMP(x+y)"
argument_list|,
literal|"LOCALTIMESTAMP((`X` + `Y`))"
argument_list|)
expr_stmt|;
comment|// CURRENT_DATE - returns DATE
name|checkExp
argument_list|(
literal|"CURRENT_DATE(3)"
argument_list|,
literal|"CURRENT_DATE(3)"
argument_list|)
expr_stmt|;
comment|// checkFails("SELECT CURRENT_DATE() FROM foo", "SELECT CURRENT_DATE()
comment|// FROM `FOO`");
name|checkExp
argument_list|(
literal|"CURRENT_DATE"
argument_list|,
literal|"`CURRENT_DATE`"
argument_list|)
expr_stmt|;
comment|// checkFails("SELECT CURRENT_DATE(x+y) FROM foo", "CURRENT_DATE((`X` +
comment|// `Y`))"); CURRENT_TIMESTAMP - returns timestamp w/ TZ
name|checkExp
argument_list|(
literal|"CURRENT_TIMESTAMP(3)"
argument_list|,
literal|"CURRENT_TIMESTAMP(3)"
argument_list|)
expr_stmt|;
comment|// checkFails("SELECT CURRENT_TIMESTAMP() FROM foo", "SELECT
comment|// CURRENT_TIMESTAMP() FROM `FOO`");
name|checkExp
argument_list|(
literal|"CURRENT_TIMESTAMP"
argument_list|,
literal|"`CURRENT_TIMESTAMP`"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"CURRENT_TIMESTAMP(x+y)"
argument_list|,
literal|"CURRENT_TIMESTAMP((`X` + `Y`))"
argument_list|)
expr_stmt|;
comment|// Date literals
name|checkExp
argument_list|(
literal|"DATE '2004-12-01'"
argument_list|,
literal|"DATE '2004-12-01'"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"TIME '12:01:01'"
argument_list|,
literal|"TIME '12:01:01'"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"TIME '12:01:01.'"
argument_list|,
literal|"TIME '12:01:01'"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"TIME '12:01:01.000'"
argument_list|,
literal|"TIME '12:01:01.000'"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"TIME '12:01:01.001'"
argument_list|,
literal|"TIME '12:01:01.001'"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"TIMESTAMP '2004-12-01 12:01:01'"
argument_list|,
literal|"TIMESTAMP '2004-12-01 12:01:01'"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"TIMESTAMP '2004-12-01 12:01:01.1'"
argument_list|,
literal|"TIMESTAMP '2004-12-01 12:01:01.1'"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"TIMESTAMP '2004-12-01 12:01:01.'"
argument_list|,
literal|"TIMESTAMP '2004-12-01 12:01:01'"
argument_list|)
expr_stmt|;
name|checkExpSame
argument_list|(
literal|"TIMESTAMP '2004-12-01 12:01:01.1'"
argument_list|)
expr_stmt|;
comment|// Failures.
name|checkFails
argument_list|(
literal|"^DATE '12/21/99'^"
argument_list|,
literal|"(?s).*Illegal DATE literal.*"
argument_list|)
expr_stmt|;
name|checkFails
argument_list|(
literal|"^TIME '1230:33'^"
argument_list|,
literal|"(?s).*Illegal TIME literal.*"
argument_list|)
expr_stmt|;
name|checkFails
argument_list|(
literal|"^TIME '12:00:00 PM'^"
argument_list|,
literal|"(?s).*Illegal TIME literal.*"
argument_list|)
expr_stmt|;
name|checkFails
argument_list|(
literal|"^TIMESTAMP '12-21-99, 12:30:00'^"
argument_list|,
literal|"(?s).*Illegal TIMESTAMP literal.*"
argument_list|)
expr_stmt|;
block|}
comment|/**      * Tests for casting to/from date/time types.      */
specifier|public
name|void
name|testDateTimeCast
parameter_list|()
block|{
comment|//   checkExp("CAST(DATE '2001-12-21' AS CHARACTER VARYING)",
comment|// "CAST(2001-12-21)");
name|checkExp
argument_list|(
literal|"CAST('2001-12-21' AS DATE)"
argument_list|,
literal|"CAST('2001-12-21' AS DATE)"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"CAST(12 AS DATE)"
argument_list|,
literal|"CAST(12 AS DATE)"
argument_list|)
expr_stmt|;
name|checkFails
argument_list|(
literal|"CAST('2000-12-21' AS DATE ^NOT^ NULL)"
argument_list|,
literal|"(?s).*Encountered \"NOT\" at line 1, column 27.*"
argument_list|)
expr_stmt|;
name|checkFails
argument_list|(
literal|"CAST('foo' as ^1^)"
argument_list|,
literal|"(?s).*Encountered \"1\" at line 1, column 15.*"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"Cast(DATE '2004-12-21' AS VARCHAR(10))"
argument_list|,
literal|"CAST(DATE '2004-12-21' AS VARCHAR(10))"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testTrim
parameter_list|()
block|{
name|checkExp
argument_list|(
literal|"trim('mustache' FROM 'beard')"
argument_list|,
literal|"TRIM(BOTH 'mustache' FROM 'beard')"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"trim('mustache')"
argument_list|,
literal|"TRIM(BOTH ' ' FROM 'mustache')"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"trim(TRAILING FROM 'mustache')"
argument_list|,
literal|"TRIM(TRAILING ' ' FROM 'mustache')"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"trim(bOth 'mustache' FROM 'beard')"
argument_list|,
literal|"TRIM(BOTH 'mustache' FROM 'beard')"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"trim( lEaDing       'mustache' FROM 'beard')"
argument_list|,
literal|"TRIM(LEADING 'mustache' FROM 'beard')"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"trim(\r\n\ttrailing\n  'mustache' FROM 'beard')"
argument_list|,
literal|"TRIM(TRAILING 'mustache' FROM 'beard')"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"trim (coalesce(cast(null as varchar(2)))||"
operator|+
literal|"' '||coalesce('junk ',''))"
argument_list|,
literal|"TRIM(BOTH ' ' FROM ((COALESCE(CAST(NULL AS VARCHAR(2))) || "
operator|+
literal|"' ') || COALESCE('junk ', '')))"
argument_list|)
expr_stmt|;
name|checkFails
argument_list|(
literal|"trim(^from^ 'beard')"
argument_list|,
literal|"(?s).*'FROM' without operands preceding it is illegal.*"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testConvertAndTranslate
parameter_list|()
block|{
name|checkExp
argument_list|(
literal|"convert('abc' using conversion)"
argument_list|,
literal|"CONVERT('abc' USING `CONVERSION`)"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"translate('abc' using lazy_translation)"
argument_list|,
literal|"TRANSLATE('abc' USING `LAZY_TRANSLATION`)"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testOverlay
parameter_list|()
block|{
name|checkExp
argument_list|(
literal|"overlay('ABCdef' placing 'abc' from 1)"
argument_list|,
literal|"OVERLAY('ABCdef' PLACING 'abc' FROM 1)"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"overlay('ABCdef' placing 'abc' from 1 for 3)"
argument_list|,
literal|"OVERLAY('ABCdef' PLACING 'abc' FROM 1 FOR 3)"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testJdbcFunctionCall
parameter_list|()
block|{
name|checkExp
argument_list|(
literal|"{fn apa(1,'1')}"
argument_list|,
literal|"{fn APA(1, '1') }"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"{ Fn apa(log10(ln(1))+2)}"
argument_list|,
literal|"{fn APA((LOG10(LN(1)) + 2)) }"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"{fN apa(*)}"
argument_list|,
literal|"{fn APA(*) }"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"{   FN\t\r\n apa()}"
argument_list|,
literal|"{fn APA() }"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"{fn insert()}"
argument_list|,
literal|"{fn INSERT() }"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testWindowReference
parameter_list|()
block|{
name|checkExp
argument_list|(
literal|"sum(sal) over (w)"
argument_list|,
literal|"(SUM(`SAL`) OVER (`W`))"
argument_list|)
expr_stmt|;
comment|// Only 1 window reference allowed
name|checkExpFails
argument_list|(
literal|"sum(sal) over (w ^w1^ partition by deptno)"
argument_list|,
literal|"(?s)Encountered \"w1\" at.*"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testWindowInSubquery
parameter_list|()
block|{
name|check
argument_list|(
literal|"select * from ( select sum(x) over w, sum(y) over w from s window w as (range interval '1' minute preceding))"
argument_list|,
name|TestUtil
operator|.
name|fold
argument_list|(
literal|"SELECT *\n"
operator|+
literal|"FROM (SELECT (SUM(`X`) OVER `W`), (SUM(`Y`) OVER `W`)\n"
operator|+
literal|"FROM `S`\n"
operator|+
literal|"WINDOW `W` AS (RANGE INTERVAL '1' MINUTE PRECEDING))"
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testWindowSpec
parameter_list|()
block|{
comment|// Correct syntax
name|check
argument_list|(
literal|"select count(z) over w as foo from Bids window w as (partition by y + yy, yyy order by x rows between 2 preceding and 2 following)"
argument_list|,
name|TestUtil
operator|.
name|fold
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"SELECT (COUNT(`Z`) OVER `W`) AS `FOO`"
block|,
literal|"FROM `BIDS`"
block|,
literal|"WINDOW `W` AS (PARTITION BY (`Y` + `YY`), `YYY` ORDER BY `X` ROWS BETWEEN 2 PRECEDING AND 2 FOLLOWING)"
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"select count(*) over w from emp window w as (rows 2 preceding)"
argument_list|,
literal|"SELECT (COUNT(*) OVER `W`)"
operator|+
name|NL
operator|+
literal|"FROM `EMP`"
operator|+
name|NL
operator|+
literal|"WINDOW `W` AS (ROWS 2 PRECEDING)"
argument_list|)
expr_stmt|;
comment|// Chained string literals are valid syntax. They are unlikely to be
comment|// semantically valid, because intervals are usually numeric or
comment|// datetime.
name|check
argument_list|(
literal|"select count(*) over w from emp window w as (\n"
operator|+
literal|"  rows 'foo' 'bar'\n"
operator|+
literal|"       'baz' preceding)"
argument_list|,
literal|"SELECT (COUNT(*) OVER `W`)\n"
operator|+
literal|"FROM `EMP`\n"
operator|+
literal|"WINDOW `W` AS (ROWS 'foobarbaz' PRECEDING)"
argument_list|)
expr_stmt|;
comment|// Partition clause out of place. Found after ORDER BY
name|checkFails
argument_list|(
literal|"select count(z) over w as foo "
operator|+
name|NL
operator|+
literal|"from Bids window w as (partition by y order by x ^partition^ by y)"
argument_list|,
literal|"(?s).*Encountered \"partition\".*"
argument_list|)
expr_stmt|;
name|checkFails
argument_list|(
literal|"select count(z) over w as foo from Bids window w as (order by x ^partition^ by y)"
argument_list|,
literal|"(?s).*Encountered \"partition\".*"
argument_list|)
expr_stmt|;
comment|// Cannot partition by subquery
name|checkFails
argument_list|(
literal|"select sum(a) over (partition by ^(^select 1 from t), x) from t2"
argument_list|,
literal|"Query expression encountered in illegal context"
argument_list|)
expr_stmt|;
comment|// AND is required in BETWEEN clause of window frame
name|checkFails
argument_list|(
literal|"select sum(x) over (order by x range between unbounded preceding ^unbounded^ following)"
argument_list|,
literal|"(?s).*Encountered \"unbounded\".*"
argument_list|)
expr_stmt|;
comment|// WINDOW keyword is not permissible.
name|checkFails
argument_list|(
literal|"select sum(x) over ^window^ (order by x) from bids"
argument_list|,
literal|"(?s).*Encountered \"window\".*"
argument_list|)
expr_stmt|;
comment|// ORDER BY must be before Frame spec
name|checkFails
argument_list|(
literal|"select sum(x) over (rows 2 preceding ^order^ by x) from emp"
argument_list|,
literal|"(?s).*Encountered \"order\".*"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testWindowSpecPartial
parameter_list|()
block|{
comment|// ALLOW PARTIAL is the default, and is omitted when the statement is
comment|// unparsed.
name|check
argument_list|(
literal|"select sum(x) over (order by x allow partial) from bids"
argument_list|,
name|TestUtil
operator|.
name|fold
argument_list|(
literal|"SELECT (SUM(`X`) OVER (ORDER BY `X`))\n"
operator|+
literal|"FROM `BIDS`"
argument_list|)
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"select sum(x) over (order by x) from bids"
argument_list|,
name|TestUtil
operator|.
name|fold
argument_list|(
literal|"SELECT (SUM(`X`) OVER (ORDER BY `X`))\n"
operator|+
literal|"FROM `BIDS`"
argument_list|)
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"select sum(x) over (order by x disallow partial) from bids"
argument_list|,
name|TestUtil
operator|.
name|fold
argument_list|(
literal|"SELECT (SUM(`X`) OVER (ORDER BY `X` DISALLOW PARTIAL))\n"
operator|+
literal|"FROM `BIDS`"
argument_list|)
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"select sum(x) over (order by x) from bids"
argument_list|,
name|TestUtil
operator|.
name|fold
argument_list|(
literal|"SELECT (SUM(`X`) OVER (ORDER BY `X`))\n"
operator|+
literal|"FROM `BIDS`"
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testAs
parameter_list|()
block|{
comment|// AS is optional for column aliases
name|check
argument_list|(
literal|"select x y from t"
argument_list|,
name|TestUtil
operator|.
name|fold
argument_list|(
literal|"SELECT `X` AS `Y`\n"
operator|+
literal|"FROM `T`"
argument_list|)
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"select x AS y from t"
argument_list|,
name|TestUtil
operator|.
name|fold
argument_list|(
literal|"SELECT `X` AS `Y`\n"
operator|+
literal|"FROM `T`"
argument_list|)
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"select sum(x) y from t group by z"
argument_list|,
name|TestUtil
operator|.
name|fold
argument_list|(
literal|"SELECT SUM(`X`) AS `Y`\n"
operator|+
literal|"FROM `T`\n"
operator|+
literal|"GROUP BY `Z`"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Even after OVER
name|check
argument_list|(
literal|"select count(z) over w foo from Bids window w as (order by x)"
argument_list|,
name|TestUtil
operator|.
name|fold
argument_list|(
literal|"SELECT (COUNT(`Z`) OVER `W`) AS `FOO`\n"
operator|+
literal|"FROM `BIDS`\n"
operator|+
literal|"WINDOW `W` AS (ORDER BY `X`)"
argument_list|)
argument_list|)
expr_stmt|;
comment|// AS is optional for table correlation names
specifier|final
name|String
name|expected
init|=
name|TestUtil
operator|.
name|fold
argument_list|(
literal|"SELECT `X`\n"
operator|+
literal|"FROM `T` AS `T1`"
argument_list|)
decl_stmt|;
name|check
argument_list|(
literal|"select x from t as t1"
argument_list|,
name|expected
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"select x from t t1"
argument_list|,
name|expected
argument_list|)
expr_stmt|;
comment|// AS is required in WINDOW declaration
name|checkFails
argument_list|(
literal|"select sum(x) over w from bids window w ^(order by x)"
argument_list|,
literal|"(?s).*Encountered \"\\(\".*"
argument_list|)
expr_stmt|;
comment|// Error if OVER and AS are in wrong order
name|checkFails
argument_list|(
literal|"select count(*) as foo ^over^ w from Bids window w (order by x)"
argument_list|,
literal|"(?s).*Encountered \"over\".*"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testAsAliases
parameter_list|()
block|{
name|check
argument_list|(
literal|"select x from t as t1 (a, b) where foo"
argument_list|,
name|TestUtil
operator|.
name|fold
argument_list|(
literal|"SELECT `X`\n"
operator|+
literal|"FROM `T` AS `T1` (`A`, `B`)\n"
operator|+
literal|"WHERE `FOO`"
argument_list|)
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"select x from (values (1, 2), (3, 4)) as t1 (\"a\", b) where \"a\"> b"
argument_list|,
name|TestUtil
operator|.
name|fold
argument_list|(
literal|"SELECT `X`\n"
operator|+
literal|"FROM (VALUES (ROW(1, 2)), (ROW(3, 4))) AS `T1` (`a`, `B`)\n"
operator|+
literal|"WHERE (`a`> `B`)"
argument_list|)
argument_list|)
expr_stmt|;
comment|// must have at least one column
name|checkFails
argument_list|(
literal|"select x from (values (1, 2), (3, 4)) as t1 (^)"
argument_list|,
name|TestUtil
operator|.
name|fold
argument_list|(
literal|"(?s).*Was expecting one of:\n"
operator|+
literal|"<IDENTIFIER> ...\n"
operator|+
literal|"<QUOTED_IDENTIFIER>.*"
argument_list|)
argument_list|)
expr_stmt|;
comment|// cannot have expressions
name|checkFails
argument_list|(
literal|"select x from t as t1 (x ^+^ y)"
argument_list|,
name|TestUtil
operator|.
name|fold
argument_list|(
literal|"(?s).*Was expecting one of:\n"
operator|+
literal|"    \"\\)\" \\.\\.\\.\n"
operator|+
literal|"    \",\" \\.\\.\\..*"
argument_list|)
argument_list|)
expr_stmt|;
comment|// cannot have compound identifiers
name|checkFails
argument_list|(
literal|"select x from t as t1 (x^.^y)"
argument_list|,
name|TestUtil
operator|.
name|fold
argument_list|(
literal|"(?s).*Was expecting one of:\n"
operator|+
literal|"    \"\\)\" \\.\\.\\.\n"
operator|+
literal|"    \",\" \\.\\.\\..*"
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testOver
parameter_list|()
block|{
name|checkExp
argument_list|(
literal|"sum(sal) over ()"
argument_list|,
literal|"(SUM(`SAL`) OVER ())"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"sum(sal) over (partition by x, y)"
argument_list|,
literal|"(SUM(`SAL`) OVER (PARTITION BY `X`, `Y`))"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"sum(sal) over (order by x desc, y asc)"
argument_list|,
literal|"(SUM(`SAL`) OVER (ORDER BY `X` DESC, `Y`))"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"sum(sal) over (rows 5 preceding)"
argument_list|,
literal|"(SUM(`SAL`) OVER (ROWS 5 PRECEDING))"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"sum(sal) over (range between interval '1' second preceding and interval '1' second following)"
argument_list|,
literal|"(SUM(`SAL`) OVER (RANGE BETWEEN INTERVAL '1' SECOND PRECEDING AND INTERVAL '1' SECOND FOLLOWING))"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"sum(sal) over (range between interval '1:03' hour preceding and interval '2' minute following)"
argument_list|,
literal|"(SUM(`SAL`) OVER (RANGE BETWEEN INTERVAL '1:03' HOUR PRECEDING AND INTERVAL '2' MINUTE FOLLOWING))"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"sum(sal) over (range between interval '5' day preceding and current row)"
argument_list|,
literal|"(SUM(`SAL`) OVER (RANGE BETWEEN INTERVAL '5' DAY PRECEDING AND CURRENT ROW))"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"sum(sal) over (range interval '5' day preceding)"
argument_list|,
literal|"(SUM(`SAL`) OVER (RANGE INTERVAL '5' DAY PRECEDING))"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"sum(sal) over (range between unbounded preceding and current row)"
argument_list|,
literal|"(SUM(`SAL`) OVER (RANGE BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW))"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"sum(sal) over (range unbounded preceding)"
argument_list|,
literal|"(SUM(`SAL`) OVER (RANGE UNBOUNDED PRECEDING))"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"sum(sal) over (range between current row and unbounded preceding)"
argument_list|,
literal|"(SUM(`SAL`) OVER (RANGE BETWEEN CURRENT ROW AND UNBOUNDED PRECEDING))"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"sum(sal) over (range between current row and unbounded following)"
argument_list|,
literal|"(SUM(`SAL`) OVER (RANGE BETWEEN CURRENT ROW AND UNBOUNDED FOLLOWING))"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"sum(sal) over (range between 6 preceding and interval '1:03' hour preceding)"
argument_list|,
literal|"(SUM(`SAL`) OVER (RANGE BETWEEN 6 PRECEDING AND INTERVAL '1:03' HOUR PRECEDING))"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"sum(sal) over (range between interval '1' second following and interval '5' day following)"
argument_list|,
literal|"(SUM(`SAL`) OVER (RANGE BETWEEN INTERVAL '1' SECOND FOLLOWING AND INTERVAL '5' DAY FOLLOWING))"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testElementFunc
parameter_list|()
block|{
name|checkExp
argument_list|(
literal|"element(a)"
argument_list|,
literal|"ELEMENT(`A`)"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testCardinalityFunc
parameter_list|()
block|{
name|checkExp
argument_list|(
literal|"cardinality(a)"
argument_list|,
literal|"CARDINALITY(`A`)"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testMemberOf
parameter_list|()
block|{
name|checkExp
argument_list|(
literal|"a member of b"
argument_list|,
literal|"(`A` MEMBER OF `B`)"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"a member of multiset[b]"
argument_list|,
literal|"(`A` MEMBER OF (MULTISET [`B`]))"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testSubMultisetrOf
parameter_list|()
block|{
name|checkExp
argument_list|(
literal|"a submultiset of b"
argument_list|,
literal|"(`A` SUBMULTISET OF `B`)"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testIsASet
parameter_list|()
block|{
name|checkExp
argument_list|(
literal|"b is a set"
argument_list|,
literal|"(`B` IS A SET)"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"a is a set"
argument_list|,
literal|"(`A` IS A SET)"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testMultiset
parameter_list|()
block|{
name|checkExp
argument_list|(
literal|"multiset[1]"
argument_list|,
literal|"(MULTISET [1])"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"multiset[1,2.3]"
argument_list|,
literal|"(MULTISET [1, 2.3])"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"multiset[1,    '2']"
argument_list|,
literal|"(MULTISET [1, '2'])"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"multiset[ROW(1,2)]"
argument_list|,
literal|"(MULTISET [(ROW(1, 2))])"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"multiset[ROW(1,2),ROW(3,4)]"
argument_list|,
literal|"(MULTISET [(ROW(1, 2)), (ROW(3, 4))])"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"multiset(select*from T)"
argument_list|,
name|TestUtil
operator|.
name|fold
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"(MULTISET ((SELECT *"
block|,
literal|"FROM `T`)))"
block|}
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testMultisetUnion
parameter_list|()
block|{
name|checkExp
argument_list|(
literal|"a multiset union b"
argument_list|,
literal|"(`A` MULTISET UNION `B`)"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"a multiset union all b"
argument_list|,
literal|"(`A` MULTISET UNION ALL `B`)"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"a multiset union distinct b"
argument_list|,
literal|"(`A` MULTISET UNION `B`)"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testMultisetExcept
parameter_list|()
block|{
name|checkExp
argument_list|(
literal|"a multiset EXCEPT b"
argument_list|,
literal|"(`A` MULTISET EXCEPT `B`)"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"a multiset EXCEPT all b"
argument_list|,
literal|"(`A` MULTISET EXCEPT ALL `B`)"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"a multiset EXCEPT distinct b"
argument_list|,
literal|"(`A` MULTISET EXCEPT `B`)"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testMultisetIntersect
parameter_list|()
block|{
name|checkExp
argument_list|(
literal|"a multiset INTERSECT b"
argument_list|,
literal|"(`A` MULTISET INTERSECT `B`)"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"a multiset INTERSECT all b"
argument_list|,
literal|"(`A` MULTISET INTERSECT ALL `B`)"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"a multiset INTERSECT distinct b"
argument_list|,
literal|"(`A` MULTISET INTERSECT `B`)"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testMultisetMixed
parameter_list|()
block|{
name|checkExp
argument_list|(
literal|"multiset[1] MULTISET union b"
argument_list|,
literal|"((MULTISET [1]) MULTISET UNION `B`)"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"a MULTISET union b multiset intersect c multiset except d multiset union e"
argument_list|,
literal|"(((`A` MULTISET UNION (`B` MULTISET INTERSECT `C`)) MULTISET EXCEPT `D`) MULTISET UNION `E`)"
argument_list|)
expr_stmt|;
block|}
comment|/**      * Runs tests for INTERVAL... YEAR that should pass both parser and      * validator. A substantially identical set of tests exists in      * SqlValidatorTest, and any changes here should be synchronized there.      * Similarly, any changes to tests here should be echoed appropriately to      * each of the other 12 subTestIntervalXXXPositive() tests.      */
specifier|public
name|void
name|subTestIntervalYearPositive
parameter_list|()
block|{
comment|//default precision
name|checkExp
argument_list|(
literal|"interval '1' year"
argument_list|,
literal|"INTERVAL '1' YEAR"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval '99' year"
argument_list|,
literal|"INTERVAL '99' YEAR"
argument_list|)
expr_stmt|;
comment|//explicit precision equal to default
name|checkExp
argument_list|(
literal|"interval '1' year(2)"
argument_list|,
literal|"INTERVAL '1' YEAR(2)"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval '99' year(2)"
argument_list|,
literal|"INTERVAL '99' YEAR(2)"
argument_list|)
expr_stmt|;
comment|//max precision
name|checkExp
argument_list|(
literal|"interval '2147483647' year(10)"
argument_list|,
literal|"INTERVAL '2147483647' YEAR(10)"
argument_list|)
expr_stmt|;
comment|//min precision
name|checkExp
argument_list|(
literal|"interval '0' year(1)"
argument_list|,
literal|"INTERVAL '0' YEAR(1)"
argument_list|)
expr_stmt|;
comment|//alternate precision
name|checkExp
argument_list|(
literal|"interval '1234' year(4)"
argument_list|,
literal|"INTERVAL '1234' YEAR(4)"
argument_list|)
expr_stmt|;
comment|//sign
name|checkExp
argument_list|(
literal|"interval '+1' year"
argument_list|,
literal|"INTERVAL '+1' YEAR"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval '-1' year"
argument_list|,
literal|"INTERVAL '-1' YEAR"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval +'1' year"
argument_list|,
literal|"INTERVAL '1' YEAR"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval +'+1' year"
argument_list|,
literal|"INTERVAL '+1' YEAR"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval +'-1' year"
argument_list|,
literal|"INTERVAL '-1' YEAR"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval -'1' year"
argument_list|,
literal|"INTERVAL -'1' YEAR"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval -'+1' year"
argument_list|,
literal|"INTERVAL -'+1' YEAR"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval -'-1' year"
argument_list|,
literal|"INTERVAL -'-1' YEAR"
argument_list|)
expr_stmt|;
block|}
comment|/**      * Runs tests for INTERVAL... YEAR TO MONTH that should pass both parser and      * validator. A substantially identical set of tests exists in      * SqlValidatorTest, and any changes here should be synchronized there.      * Similarly, any changes to tests here should be echoed appropriately to      * each of the other 12 subTestIntervalXXXPositive() tests.      */
specifier|public
name|void
name|subTestIntervalYearToMonthPositive
parameter_list|()
block|{
comment|//default precision
name|checkExp
argument_list|(
literal|"interval '1-2' year to month"
argument_list|,
literal|"INTERVAL '1-2' YEAR TO MONTH"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval '99-11' year to month"
argument_list|,
literal|"INTERVAL '99-11' YEAR TO MONTH"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval '99-0' year to month"
argument_list|,
literal|"INTERVAL '99-0' YEAR TO MONTH"
argument_list|)
expr_stmt|;
comment|//explicit precision equal to default
name|checkExp
argument_list|(
literal|"interval '1-2' year(2) to month"
argument_list|,
literal|"INTERVAL '1-2' YEAR(2) TO MONTH"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval '99-11' year(2) to month"
argument_list|,
literal|"INTERVAL '99-11' YEAR(2) TO MONTH"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval '99-0' year(2) to month"
argument_list|,
literal|"INTERVAL '99-0' YEAR(2) TO MONTH"
argument_list|)
expr_stmt|;
comment|//max precision
name|checkExp
argument_list|(
literal|"interval '2147483647-11' year(10) to month"
argument_list|,
literal|"INTERVAL '2147483647-11' YEAR(10) TO MONTH"
argument_list|)
expr_stmt|;
comment|//min precision
name|checkExp
argument_list|(
literal|"interval '0-0' year(1) to month"
argument_list|,
literal|"INTERVAL '0-0' YEAR(1) TO MONTH"
argument_list|)
expr_stmt|;
comment|//alternate precision
name|checkExp
argument_list|(
literal|"interval '2006-2' year(4) to month"
argument_list|,
literal|"INTERVAL '2006-2' YEAR(4) TO MONTH"
argument_list|)
expr_stmt|;
comment|//sign
name|checkExp
argument_list|(
literal|"interval '-1-2' year to month"
argument_list|,
literal|"INTERVAL '-1-2' YEAR TO MONTH"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval '+1-2' year to month"
argument_list|,
literal|"INTERVAL '+1-2' YEAR TO MONTH"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval +'1-2' year to month"
argument_list|,
literal|"INTERVAL '1-2' YEAR TO MONTH"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval +'-1-2' year to month"
argument_list|,
literal|"INTERVAL '-1-2' YEAR TO MONTH"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval +'+1-2' year to month"
argument_list|,
literal|"INTERVAL '+1-2' YEAR TO MONTH"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval -'1-2' year to month"
argument_list|,
literal|"INTERVAL -'1-2' YEAR TO MONTH"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval -'-1-2' year to month"
argument_list|,
literal|"INTERVAL -'-1-2' YEAR TO MONTH"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval -'+1-2' year to month"
argument_list|,
literal|"INTERVAL -'+1-2' YEAR TO MONTH"
argument_list|)
expr_stmt|;
block|}
comment|/**      * Runs tests for INTERVAL... MONTH that should pass both parser and      * validator. A substantially identical set of tests exists in      * SqlValidatorTest, and any changes here should be synchronized there.      * Similarly, any changes to tests here should be echoed appropriately to      * each of the other 12 subTestIntervalXXXPositive() tests.      */
specifier|public
name|void
name|subTestIntervalMonthPositive
parameter_list|()
block|{
comment|//default precision
name|checkExp
argument_list|(
literal|"interval '1' month"
argument_list|,
literal|"INTERVAL '1' MONTH"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval '99' month"
argument_list|,
literal|"INTERVAL '99' MONTH"
argument_list|)
expr_stmt|;
comment|//explicit precision equal to default
name|checkExp
argument_list|(
literal|"interval '1' month(2)"
argument_list|,
literal|"INTERVAL '1' MONTH(2)"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval '99' month(2)"
argument_list|,
literal|"INTERVAL '99' MONTH(2)"
argument_list|)
expr_stmt|;
comment|//max precision
name|checkExp
argument_list|(
literal|"interval '2147483647' month(10)"
argument_list|,
literal|"INTERVAL '2147483647' MONTH(10)"
argument_list|)
expr_stmt|;
comment|//min precision
name|checkExp
argument_list|(
literal|"interval '0' month(1)"
argument_list|,
literal|"INTERVAL '0' MONTH(1)"
argument_list|)
expr_stmt|;
comment|//alternate precision
name|checkExp
argument_list|(
literal|"interval '1234' month(4)"
argument_list|,
literal|"INTERVAL '1234' MONTH(4)"
argument_list|)
expr_stmt|;
comment|//sign
name|checkExp
argument_list|(
literal|"interval '+1' month"
argument_list|,
literal|"INTERVAL '+1' MONTH"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval '-1' month"
argument_list|,
literal|"INTERVAL '-1' MONTH"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval +'1' month"
argument_list|,
literal|"INTERVAL '1' MONTH"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval +'+1' month"
argument_list|,
literal|"INTERVAL '+1' MONTH"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval +'-1' month"
argument_list|,
literal|"INTERVAL '-1' MONTH"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval -'1' month"
argument_list|,
literal|"INTERVAL -'1' MONTH"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval -'+1' month"
argument_list|,
literal|"INTERVAL -'+1' MONTH"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval -'-1' month"
argument_list|,
literal|"INTERVAL -'-1' MONTH"
argument_list|)
expr_stmt|;
block|}
comment|/**      * Runs tests for INTERVAL... DAY that should pass both parser and      * validator. A substantially identical set of tests exists in      * SqlValidatorTest, and any changes here should be synchronized there.      * Similarly, any changes to tests here should be echoed appropriately to      * each of the other 12 subTestIntervalXXXPositive() tests.      */
specifier|public
name|void
name|subTestIntervalDayPositive
parameter_list|()
block|{
comment|//default precision
name|checkExp
argument_list|(
literal|"interval '1' day"
argument_list|,
literal|"INTERVAL '1' DAY"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval '99' day"
argument_list|,
literal|"INTERVAL '99' DAY"
argument_list|)
expr_stmt|;
comment|//explicit precision equal to default
name|checkExp
argument_list|(
literal|"interval '1' day(2)"
argument_list|,
literal|"INTERVAL '1' DAY(2)"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval '99' day(2)"
argument_list|,
literal|"INTERVAL '99' DAY(2)"
argument_list|)
expr_stmt|;
comment|//max precision
name|checkExp
argument_list|(
literal|"interval '2147483647' day(10)"
argument_list|,
literal|"INTERVAL '2147483647' DAY(10)"
argument_list|)
expr_stmt|;
comment|//min precision
name|checkExp
argument_list|(
literal|"interval '0' day(1)"
argument_list|,
literal|"INTERVAL '0' DAY(1)"
argument_list|)
expr_stmt|;
comment|//alternate precision
name|checkExp
argument_list|(
literal|"interval '1234' day(4)"
argument_list|,
literal|"INTERVAL '1234' DAY(4)"
argument_list|)
expr_stmt|;
comment|//sign
name|checkExp
argument_list|(
literal|"interval '+1' day"
argument_list|,
literal|"INTERVAL '+1' DAY"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval '-1' day"
argument_list|,
literal|"INTERVAL '-1' DAY"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval +'1' day"
argument_list|,
literal|"INTERVAL '1' DAY"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval +'+1' day"
argument_list|,
literal|"INTERVAL '+1' DAY"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval +'-1' day"
argument_list|,
literal|"INTERVAL '-1' DAY"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval -'1' day"
argument_list|,
literal|"INTERVAL -'1' DAY"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval -'+1' day"
argument_list|,
literal|"INTERVAL -'+1' DAY"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval -'-1' day"
argument_list|,
literal|"INTERVAL -'-1' DAY"
argument_list|)
expr_stmt|;
block|}
comment|/**      * Runs tests for INTERVAL... DAY TO HOUR that should pass both parser and      * validator. A substantially identical set of tests exists in      * SqlValidatorTest, and any changes here should be synchronized there.      * Similarly, any changes to tests here should be echoed appropriately to      * each of the other 12 subTestIntervalXXXPositive() tests.      */
specifier|public
name|void
name|subTestIntervalDayToHourPositive
parameter_list|()
block|{
comment|//default precision
name|checkExp
argument_list|(
literal|"interval '1 2' day to hour"
argument_list|,
literal|"INTERVAL '1 2' DAY TO HOUR"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval '99 23' day to hour"
argument_list|,
literal|"INTERVAL '99 23' DAY TO HOUR"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval '99 0' day to hour"
argument_list|,
literal|"INTERVAL '99 0' DAY TO HOUR"
argument_list|)
expr_stmt|;
comment|//explicit precision equal to default
name|checkExp
argument_list|(
literal|"interval '1 2' day(2) to hour"
argument_list|,
literal|"INTERVAL '1 2' DAY(2) TO HOUR"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval '99 23' day(2) to hour"
argument_list|,
literal|"INTERVAL '99 23' DAY(2) TO HOUR"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval '99 0' day(2) to hour"
argument_list|,
literal|"INTERVAL '99 0' DAY(2) TO HOUR"
argument_list|)
expr_stmt|;
comment|//max precision
name|checkExp
argument_list|(
literal|"interval '2147483647 23' day(10) to hour"
argument_list|,
literal|"INTERVAL '2147483647 23' DAY(10) TO HOUR"
argument_list|)
expr_stmt|;
comment|//min precision
name|checkExp
argument_list|(
literal|"interval '0 0' day(1) to hour"
argument_list|,
literal|"INTERVAL '0 0' DAY(1) TO HOUR"
argument_list|)
expr_stmt|;
comment|//alternate precision
name|checkExp
argument_list|(
literal|"interval '2345 2' day(4) to hour"
argument_list|,
literal|"INTERVAL '2345 2' DAY(4) TO HOUR"
argument_list|)
expr_stmt|;
comment|//sign
name|checkExp
argument_list|(
literal|"interval '-1 2' day to hour"
argument_list|,
literal|"INTERVAL '-1 2' DAY TO HOUR"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval '+1 2' day to hour"
argument_list|,
literal|"INTERVAL '+1 2' DAY TO HOUR"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval +'1 2' day to hour"
argument_list|,
literal|"INTERVAL '1 2' DAY TO HOUR"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval +'-1 2' day to hour"
argument_list|,
literal|"INTERVAL '-1 2' DAY TO HOUR"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval +'+1 2' day to hour"
argument_list|,
literal|"INTERVAL '+1 2' DAY TO HOUR"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval -'1 2' day to hour"
argument_list|,
literal|"INTERVAL -'1 2' DAY TO HOUR"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval -'-1 2' day to hour"
argument_list|,
literal|"INTERVAL -'-1 2' DAY TO HOUR"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval -'+1 2' day to hour"
argument_list|,
literal|"INTERVAL -'+1 2' DAY TO HOUR"
argument_list|)
expr_stmt|;
block|}
comment|/**      * Runs tests for INTERVAL... DAY TO MINUTE that should pass both parser and      * validator. A substantially identical set of tests exists in      * SqlValidatorTest, and any changes here should be synchronized there.      * Similarly, any changes to tests here should be echoed appropriately to      * each of the other 12 subTestIntervalXXXPositive() tests.      */
specifier|public
name|void
name|subTestIntervalDayToMinutePositive
parameter_list|()
block|{
comment|//default precision
name|checkExp
argument_list|(
literal|"interval '1 2:3' day to minute"
argument_list|,
literal|"INTERVAL '1 2:3' DAY TO MINUTE"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval '99 23:59' day to minute"
argument_list|,
literal|"INTERVAL '99 23:59' DAY TO MINUTE"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval '99 0:0' day to minute"
argument_list|,
literal|"INTERVAL '99 0:0' DAY TO MINUTE"
argument_list|)
expr_stmt|;
comment|//explicit precision equal to default
name|checkExp
argument_list|(
literal|"interval '1 2:3' day(2) to minute"
argument_list|,
literal|"INTERVAL '1 2:3' DAY(2) TO MINUTE"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval '99 23:59' day(2) to minute"
argument_list|,
literal|"INTERVAL '99 23:59' DAY(2) TO MINUTE"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval '99 0:0' day(2) to minute"
argument_list|,
literal|"INTERVAL '99 0:0' DAY(2) TO MINUTE"
argument_list|)
expr_stmt|;
comment|//max precision
name|checkExp
argument_list|(
literal|"interval '2147483647 23:59' day(10) to minute"
argument_list|,
literal|"INTERVAL '2147483647 23:59' DAY(10) TO MINUTE"
argument_list|)
expr_stmt|;
comment|//min precision
name|checkExp
argument_list|(
literal|"interval '0 0:0' day(1) to minute"
argument_list|,
literal|"INTERVAL '0 0:0' DAY(1) TO MINUTE"
argument_list|)
expr_stmt|;
comment|//alternate precision
name|checkExp
argument_list|(
literal|"interval '2345 6:7' day(4) to minute"
argument_list|,
literal|"INTERVAL '2345 6:7' DAY(4) TO MINUTE"
argument_list|)
expr_stmt|;
comment|//sign
name|checkExp
argument_list|(
literal|"interval '-1 2:3' day to minute"
argument_list|,
literal|"INTERVAL '-1 2:3' DAY TO MINUTE"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval '+1 2:3' day to minute"
argument_list|,
literal|"INTERVAL '+1 2:3' DAY TO MINUTE"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval +'1 2:3' day to minute"
argument_list|,
literal|"INTERVAL '1 2:3' DAY TO MINUTE"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval +'-1 2:3' day to minute"
argument_list|,
literal|"INTERVAL '-1 2:3' DAY TO MINUTE"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval +'+1 2:3' day to minute"
argument_list|,
literal|"INTERVAL '+1 2:3' DAY TO MINUTE"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval -'1 2:3' day to minute"
argument_list|,
literal|"INTERVAL -'1 2:3' DAY TO MINUTE"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval -'-1 2:3' day to minute"
argument_list|,
literal|"INTERVAL -'-1 2:3' DAY TO MINUTE"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval -'+1 2:3' day to minute"
argument_list|,
literal|"INTERVAL -'+1 2:3' DAY TO MINUTE"
argument_list|)
expr_stmt|;
block|}
comment|/**      * Runs tests for INTERVAL... DAY TO SECOND that should pass both parser and      * validator. A substantially identical set of tests exists in      * SqlValidatorTest, and any changes here should be synchronized there.      * Similarly, any changes to tests here should be echoed appropriately to      * each of the other 12 subTestIntervalXXXPositive() tests.      */
specifier|public
name|void
name|subTestIntervalDayToSecondPositive
parameter_list|()
block|{
comment|//default precision
name|checkExp
argument_list|(
literal|"interval '1 2:3:4' day to second"
argument_list|,
literal|"INTERVAL '1 2:3:4' DAY TO SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval '99 23:59:59' day to second"
argument_list|,
literal|"INTERVAL '99 23:59:59' DAY TO SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval '99 0:0:0' day to second"
argument_list|,
literal|"INTERVAL '99 0:0:0' DAY TO SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval '99 23:59:59.999999' day to second"
argument_list|,
literal|"INTERVAL '99 23:59:59.999999' DAY TO SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval '99 0:0:0.0' day to second"
argument_list|,
literal|"INTERVAL '99 0:0:0.0' DAY TO SECOND"
argument_list|)
expr_stmt|;
comment|//explicit precision equal to default
name|checkExp
argument_list|(
literal|"interval '1 2:3:4' day(2) to second"
argument_list|,
literal|"INTERVAL '1 2:3:4' DAY(2) TO SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval '99 23:59:59' day(2) to second"
argument_list|,
literal|"INTERVAL '99 23:59:59' DAY(2) TO SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval '99 0:0:0' day(2) to second"
argument_list|,
literal|"INTERVAL '99 0:0:0' DAY(2) TO SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval '99 23:59:59.999999' day to second(6)"
argument_list|,
literal|"INTERVAL '99 23:59:59.999999' DAY TO SECOND(6)"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval '99 0:0:0.0' day to second(6)"
argument_list|,
literal|"INTERVAL '99 0:0:0.0' DAY TO SECOND(6)"
argument_list|)
expr_stmt|;
comment|//max precision
name|checkExp
argument_list|(
literal|"interval '2147483647 23:59:59' day(10) to second"
argument_list|,
literal|"INTERVAL '2147483647 23:59:59' DAY(10) TO SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval '2147483647 23:59:59.999999999' day(10) to second(9)"
argument_list|,
literal|"INTERVAL '2147483647 23:59:59.999999999' DAY(10) TO SECOND(9)"
argument_list|)
expr_stmt|;
comment|//min precision
name|checkExp
argument_list|(
literal|"interval '0 0:0:0' day(1) to second"
argument_list|,
literal|"INTERVAL '0 0:0:0' DAY(1) TO SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval '0 0:0:0.0' day(1) to second(1)"
argument_list|,
literal|"INTERVAL '0 0:0:0.0' DAY(1) TO SECOND(1)"
argument_list|)
expr_stmt|;
comment|//alternate precision
name|checkExp
argument_list|(
literal|"interval '2345 6:7:8' day(4) to second"
argument_list|,
literal|"INTERVAL '2345 6:7:8' DAY(4) TO SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval '2345 6:7:8.9012' day(4) to second(4)"
argument_list|,
literal|"INTERVAL '2345 6:7:8.9012' DAY(4) TO SECOND(4)"
argument_list|)
expr_stmt|;
comment|//sign
name|checkExp
argument_list|(
literal|"interval '-1 2:3:4' day to second"
argument_list|,
literal|"INTERVAL '-1 2:3:4' DAY TO SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval '+1 2:3:4' day to second"
argument_list|,
literal|"INTERVAL '+1 2:3:4' DAY TO SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval +'1 2:3:4' day to second"
argument_list|,
literal|"INTERVAL '1 2:3:4' DAY TO SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval +'-1 2:3:4' day to second"
argument_list|,
literal|"INTERVAL '-1 2:3:4' DAY TO SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval +'+1 2:3:4' day to second"
argument_list|,
literal|"INTERVAL '+1 2:3:4' DAY TO SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval -'1 2:3:4' day to second"
argument_list|,
literal|"INTERVAL -'1 2:3:4' DAY TO SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval -'-1 2:3:4' day to second"
argument_list|,
literal|"INTERVAL -'-1 2:3:4' DAY TO SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval -'+1 2:3:4' day to second"
argument_list|,
literal|"INTERVAL -'+1 2:3:4' DAY TO SECOND"
argument_list|)
expr_stmt|;
block|}
comment|/**      * Runs tests for INTERVAL... HOUR that should pass both parser and      * validator. A substantially identical set of tests exists in      * SqlValidatorTest, and any changes here should be synchronized there.      * Similarly, any changes to tests here should be echoed appropriately to      * each of the other 12 subTestIntervalXXXPositive() tests.      */
specifier|public
name|void
name|subTestIntervalHourPositive
parameter_list|()
block|{
comment|//default precision
name|checkExp
argument_list|(
literal|"interval '1' hour"
argument_list|,
literal|"INTERVAL '1' HOUR"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval '99' hour"
argument_list|,
literal|"INTERVAL '99' HOUR"
argument_list|)
expr_stmt|;
comment|//explicit precision equal to default
name|checkExp
argument_list|(
literal|"interval '1' hour(2)"
argument_list|,
literal|"INTERVAL '1' HOUR(2)"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval '99' hour(2)"
argument_list|,
literal|"INTERVAL '99' HOUR(2)"
argument_list|)
expr_stmt|;
comment|//max precision
name|checkExp
argument_list|(
literal|"interval '2147483647' hour(10)"
argument_list|,
literal|"INTERVAL '2147483647' HOUR(10)"
argument_list|)
expr_stmt|;
comment|//min precision
name|checkExp
argument_list|(
literal|"interval '0' hour(1)"
argument_list|,
literal|"INTERVAL '0' HOUR(1)"
argument_list|)
expr_stmt|;
comment|//alternate precision
name|checkExp
argument_list|(
literal|"interval '1234' hour(4)"
argument_list|,
literal|"INTERVAL '1234' HOUR(4)"
argument_list|)
expr_stmt|;
comment|//sign
name|checkExp
argument_list|(
literal|"interval '+1' hour"
argument_list|,
literal|"INTERVAL '+1' HOUR"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval '-1' hour"
argument_list|,
literal|"INTERVAL '-1' HOUR"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval +'1' hour"
argument_list|,
literal|"INTERVAL '1' HOUR"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval +'+1' hour"
argument_list|,
literal|"INTERVAL '+1' HOUR"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval +'-1' hour"
argument_list|,
literal|"INTERVAL '-1' HOUR"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval -'1' hour"
argument_list|,
literal|"INTERVAL -'1' HOUR"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval -'+1' hour"
argument_list|,
literal|"INTERVAL -'+1' HOUR"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval -'-1' hour"
argument_list|,
literal|"INTERVAL -'-1' HOUR"
argument_list|)
expr_stmt|;
block|}
comment|/**      * Runs tests for INTERVAL... HOUR TO MINUTE that should pass both parser      * and validator. A substantially identical set of tests exists in      * SqlValidatorTest, and any changes here should be synchronized there.      * Similarly, any changes to tests here should be echoed appropriately to      * each of the other 12 subTestIntervalXXXPositive() tests.      */
specifier|public
name|void
name|subTestIntervalHourToMinutePositive
parameter_list|()
block|{
comment|//default precision
name|checkExp
argument_list|(
literal|"interval '2:3' hour to minute"
argument_list|,
literal|"INTERVAL '2:3' HOUR TO MINUTE"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval '23:59' hour to minute"
argument_list|,
literal|"INTERVAL '23:59' HOUR TO MINUTE"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval '99:0' hour to minute"
argument_list|,
literal|"INTERVAL '99:0' HOUR TO MINUTE"
argument_list|)
expr_stmt|;
comment|//explicit precision equal to default
name|checkExp
argument_list|(
literal|"interval '2:3' hour(2) to minute"
argument_list|,
literal|"INTERVAL '2:3' HOUR(2) TO MINUTE"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval '23:59' hour(2) to minute"
argument_list|,
literal|"INTERVAL '23:59' HOUR(2) TO MINUTE"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval '99:0' hour(2) to minute"
argument_list|,
literal|"INTERVAL '99:0' HOUR(2) TO MINUTE"
argument_list|)
expr_stmt|;
comment|//max precision
name|checkExp
argument_list|(
literal|"interval '2147483647:59' hour(10) to minute"
argument_list|,
literal|"INTERVAL '2147483647:59' HOUR(10) TO MINUTE"
argument_list|)
expr_stmt|;
comment|//min precision
name|checkExp
argument_list|(
literal|"interval '0:0' hour(1) to minute"
argument_list|,
literal|"INTERVAL '0:0' HOUR(1) TO MINUTE"
argument_list|)
expr_stmt|;
comment|//alternate precision
name|checkExp
argument_list|(
literal|"interval '2345:7' hour(4) to minute"
argument_list|,
literal|"INTERVAL '2345:7' HOUR(4) TO MINUTE"
argument_list|)
expr_stmt|;
comment|//sign
name|checkExp
argument_list|(
literal|"interval '-1:3' hour to minute"
argument_list|,
literal|"INTERVAL '-1:3' HOUR TO MINUTE"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval '+1:3' hour to minute"
argument_list|,
literal|"INTERVAL '+1:3' HOUR TO MINUTE"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval +'2:3' hour to minute"
argument_list|,
literal|"INTERVAL '2:3' HOUR TO MINUTE"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval +'-2:3' hour to minute"
argument_list|,
literal|"INTERVAL '-2:3' HOUR TO MINUTE"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval +'+2:3' hour to minute"
argument_list|,
literal|"INTERVAL '+2:3' HOUR TO MINUTE"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval -'2:3' hour to minute"
argument_list|,
literal|"INTERVAL -'2:3' HOUR TO MINUTE"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval -'-2:3' hour to minute"
argument_list|,
literal|"INTERVAL -'-2:3' HOUR TO MINUTE"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval -'+2:3' hour to minute"
argument_list|,
literal|"INTERVAL -'+2:3' HOUR TO MINUTE"
argument_list|)
expr_stmt|;
block|}
comment|/**      * Runs tests for INTERVAL... HOUR TO SECOND that should pass both parser      * and validator. A substantially identical set of tests exists in      * SqlValidatorTest, and any changes here should be synchronized there.      * Similarly, any changes to tests here should be echoed appropriately to      * each of the other 12 subTestIntervalXXXPositive() tests.      */
specifier|public
name|void
name|subTestIntervalHourToSecondPositive
parameter_list|()
block|{
comment|//default precision
name|checkExp
argument_list|(
literal|"interval '2:3:4' hour to second"
argument_list|,
literal|"INTERVAL '2:3:4' HOUR TO SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval '23:59:59' hour to second"
argument_list|,
literal|"INTERVAL '23:59:59' HOUR TO SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval '99:0:0' hour to second"
argument_list|,
literal|"INTERVAL '99:0:0' HOUR TO SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval '23:59:59.999999' hour to second"
argument_list|,
literal|"INTERVAL '23:59:59.999999' HOUR TO SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval '99:0:0.0' hour to second"
argument_list|,
literal|"INTERVAL '99:0:0.0' HOUR TO SECOND"
argument_list|)
expr_stmt|;
comment|//explicit precision equal to default
name|checkExp
argument_list|(
literal|"interval '2:3:4' hour(2) to second"
argument_list|,
literal|"INTERVAL '2:3:4' HOUR(2) TO SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval '99:59:59' hour(2) to second"
argument_list|,
literal|"INTERVAL '99:59:59' HOUR(2) TO SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval '99:0:0' hour(2) to second"
argument_list|,
literal|"INTERVAL '99:0:0' HOUR(2) TO SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval '23:59:59.999999' hour to second(6)"
argument_list|,
literal|"INTERVAL '23:59:59.999999' HOUR TO SECOND(6)"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval '99:0:0.0' hour to second(6)"
argument_list|,
literal|"INTERVAL '99:0:0.0' HOUR TO SECOND(6)"
argument_list|)
expr_stmt|;
comment|//max precision
name|checkExp
argument_list|(
literal|"interval '2147483647:59:59' hour(10) to second"
argument_list|,
literal|"INTERVAL '2147483647:59:59' HOUR(10) TO SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval '2147483647:59:59.999999999' hour(10) to second(9)"
argument_list|,
literal|"INTERVAL '2147483647:59:59.999999999' HOUR(10) TO SECOND(9)"
argument_list|)
expr_stmt|;
comment|//min precision
name|checkExp
argument_list|(
literal|"interval '0:0:0' hour(1) to second"
argument_list|,
literal|"INTERVAL '0:0:0' HOUR(1) TO SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval '0:0:0.0' hour(1) to second(1)"
argument_list|,
literal|"INTERVAL '0:0:0.0' HOUR(1) TO SECOND(1)"
argument_list|)
expr_stmt|;
comment|//alternate precision
name|checkExp
argument_list|(
literal|"interval '2345:7:8' hour(4) to second"
argument_list|,
literal|"INTERVAL '2345:7:8' HOUR(4) TO SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval '2345:7:8.9012' hour(4) to second(4)"
argument_list|,
literal|"INTERVAL '2345:7:8.9012' HOUR(4) TO SECOND(4)"
argument_list|)
expr_stmt|;
comment|//sign
name|checkExp
argument_list|(
literal|"interval '-2:3:4' hour to second"
argument_list|,
literal|"INTERVAL '-2:3:4' HOUR TO SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval '+2:3:4' hour to second"
argument_list|,
literal|"INTERVAL '+2:3:4' HOUR TO SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval +'2:3:4' hour to second"
argument_list|,
literal|"INTERVAL '2:3:4' HOUR TO SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval +'-2:3:4' hour to second"
argument_list|,
literal|"INTERVAL '-2:3:4' HOUR TO SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval +'+2:3:4' hour to second"
argument_list|,
literal|"INTERVAL '+2:3:4' HOUR TO SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval -'2:3:4' hour to second"
argument_list|,
literal|"INTERVAL -'2:3:4' HOUR TO SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval -'-2:3:4' hour to second"
argument_list|,
literal|"INTERVAL -'-2:3:4' HOUR TO SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval -'+2:3:4' hour to second"
argument_list|,
literal|"INTERVAL -'+2:3:4' HOUR TO SECOND"
argument_list|)
expr_stmt|;
block|}
comment|/**      * Runs tests for INTERVAL... MINUTE that should pass both parser and      * validator. A substantially identical set of tests exists in      * SqlValidatorTest, and any changes here should be synchronized there.      * Similarly, any changes to tests here should be echoed appropriately to      * each of the other 12 subTestIntervalXXXPositive() tests.      */
specifier|public
name|void
name|subTestIntervalMinutePositive
parameter_list|()
block|{
comment|//default precision
name|checkExp
argument_list|(
literal|"interval '1' minute"
argument_list|,
literal|"INTERVAL '1' MINUTE"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval '99' minute"
argument_list|,
literal|"INTERVAL '99' MINUTE"
argument_list|)
expr_stmt|;
comment|//explicit precision equal to default
name|checkExp
argument_list|(
literal|"interval '1' minute(2)"
argument_list|,
literal|"INTERVAL '1' MINUTE(2)"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval '99' minute(2)"
argument_list|,
literal|"INTERVAL '99' MINUTE(2)"
argument_list|)
expr_stmt|;
comment|//max precision
name|checkExp
argument_list|(
literal|"interval '2147483647' minute(10)"
argument_list|,
literal|"INTERVAL '2147483647' MINUTE(10)"
argument_list|)
expr_stmt|;
comment|//min precision
name|checkExp
argument_list|(
literal|"interval '0' minute(1)"
argument_list|,
literal|"INTERVAL '0' MINUTE(1)"
argument_list|)
expr_stmt|;
comment|//alternate precision
name|checkExp
argument_list|(
literal|"interval '1234' minute(4)"
argument_list|,
literal|"INTERVAL '1234' MINUTE(4)"
argument_list|)
expr_stmt|;
comment|//sign
name|checkExp
argument_list|(
literal|"interval '+1' minute"
argument_list|,
literal|"INTERVAL '+1' MINUTE"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval '-1' minute"
argument_list|,
literal|"INTERVAL '-1' MINUTE"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval +'1' minute"
argument_list|,
literal|"INTERVAL '1' MINUTE"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval +'+1' minute"
argument_list|,
literal|"INTERVAL '+1' MINUTE"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval +'+1' minute"
argument_list|,
literal|"INTERVAL '+1' MINUTE"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval -'1' minute"
argument_list|,
literal|"INTERVAL -'1' MINUTE"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval -'+1' minute"
argument_list|,
literal|"INTERVAL -'+1' MINUTE"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval -'-1' minute"
argument_list|,
literal|"INTERVAL -'-1' MINUTE"
argument_list|)
expr_stmt|;
block|}
comment|/**      * Runs tests for INTERVAL... MINUTE TO SECOND that should pass both parser      * and validator. A substantially identical set of tests exists in      * SqlValidatorTest, and any changes here should be synchronized there.      * Similarly, any changes to tests here should be echoed appropriately to      * each of the other 12 subTestIntervalXXXPositive() tests.      */
specifier|public
name|void
name|subTestIntervalMinuteToSecondPositive
parameter_list|()
block|{
comment|//default precision
name|checkExp
argument_list|(
literal|"interval '2:4' minute to second"
argument_list|,
literal|"INTERVAL '2:4' MINUTE TO SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval '59:59' minute to second"
argument_list|,
literal|"INTERVAL '59:59' MINUTE TO SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval '99:0' minute to second"
argument_list|,
literal|"INTERVAL '99:0' MINUTE TO SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval '59:59.999999' minute to second"
argument_list|,
literal|"INTERVAL '59:59.999999' MINUTE TO SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval '99:0.0' minute to second"
argument_list|,
literal|"INTERVAL '99:0.0' MINUTE TO SECOND"
argument_list|)
expr_stmt|;
comment|//explicit precision equal to default
name|checkExp
argument_list|(
literal|"interval '2:4' minute(2) to second"
argument_list|,
literal|"INTERVAL '2:4' MINUTE(2) TO SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval '59:59' minute(2) to second"
argument_list|,
literal|"INTERVAL '59:59' MINUTE(2) TO SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval '99:0' minute(2) to second"
argument_list|,
literal|"INTERVAL '99:0' MINUTE(2) TO SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval '99:59.999999' minute to second(6)"
argument_list|,
literal|"INTERVAL '99:59.999999' MINUTE TO SECOND(6)"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval '99:0.0' minute to second(6)"
argument_list|,
literal|"INTERVAL '99:0.0' MINUTE TO SECOND(6)"
argument_list|)
expr_stmt|;
comment|//max precision
name|checkExp
argument_list|(
literal|"interval '2147483647:59' minute(10) to second"
argument_list|,
literal|"INTERVAL '2147483647:59' MINUTE(10) TO SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval '2147483647:59.999999999' minute(10) to second(9)"
argument_list|,
literal|"INTERVAL '2147483647:59.999999999' MINUTE(10) TO SECOND(9)"
argument_list|)
expr_stmt|;
comment|//min precision
name|checkExp
argument_list|(
literal|"interval '0:0' minute(1) to second"
argument_list|,
literal|"INTERVAL '0:0' MINUTE(1) TO SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval '0:0.0' minute(1) to second(1)"
argument_list|,
literal|"INTERVAL '0:0.0' MINUTE(1) TO SECOND(1)"
argument_list|)
expr_stmt|;
comment|//alternate precision
name|checkExp
argument_list|(
literal|"interval '2345:8' minute(4) to second"
argument_list|,
literal|"INTERVAL '2345:8' MINUTE(4) TO SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval '2345:7.8901' minute(4) to second(4)"
argument_list|,
literal|"INTERVAL '2345:7.8901' MINUTE(4) TO SECOND(4)"
argument_list|)
expr_stmt|;
comment|//sign
name|checkExp
argument_list|(
literal|"interval '-3:4' minute to second"
argument_list|,
literal|"INTERVAL '-3:4' MINUTE TO SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval '+3:4' minute to second"
argument_list|,
literal|"INTERVAL '+3:4' MINUTE TO SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval +'3:4' minute to second"
argument_list|,
literal|"INTERVAL '3:4' MINUTE TO SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval +'-3:4' minute to second"
argument_list|,
literal|"INTERVAL '-3:4' MINUTE TO SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval +'+3:4' minute to second"
argument_list|,
literal|"INTERVAL '+3:4' MINUTE TO SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval -'3:4' minute to second"
argument_list|,
literal|"INTERVAL -'3:4' MINUTE TO SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval -'-3:4' minute to second"
argument_list|,
literal|"INTERVAL -'-3:4' MINUTE TO SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval -'+3:4' minute to second"
argument_list|,
literal|"INTERVAL -'+3:4' MINUTE TO SECOND"
argument_list|)
expr_stmt|;
block|}
comment|/**      * Runs tests for INTERVAL... SECOND that should pass both parser and      * validator. A substantially identical set of tests exists in      * SqlValidatorTest, and any changes here should be synchronized there.      * Similarly, any changes to tests here should be echoed appropriately to      * each of the other 12 subTestIntervalXXXPositive() tests.      */
specifier|public
name|void
name|subTestIntervalSecondPositive
parameter_list|()
block|{
comment|//default precision
name|checkExp
argument_list|(
literal|"interval '1' second"
argument_list|,
literal|"INTERVAL '1' SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval '99' second"
argument_list|,
literal|"INTERVAL '99' SECOND"
argument_list|)
expr_stmt|;
comment|//explicit precision equal to default
name|checkExp
argument_list|(
literal|"interval '1' second(2)"
argument_list|,
literal|"INTERVAL '1' SECOND(2)"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval '99' second(2)"
argument_list|,
literal|"INTERVAL '99' SECOND(2)"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval '1' second(2,6)"
argument_list|,
literal|"INTERVAL '1' SECOND(2, 6)"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval '99' second(2,6)"
argument_list|,
literal|"INTERVAL '99' SECOND(2, 6)"
argument_list|)
expr_stmt|;
comment|//max precision
name|checkExp
argument_list|(
literal|"interval '2147483647' second(10)"
argument_list|,
literal|"INTERVAL '2147483647' SECOND(10)"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval '2147483647.999999999' second(9,9)"
argument_list|,
literal|"INTERVAL '2147483647.999999999' SECOND(9, 9)"
argument_list|)
expr_stmt|;
comment|//min precision
name|checkExp
argument_list|(
literal|"interval '0' second(1)"
argument_list|,
literal|"INTERVAL '0' SECOND(1)"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval '0.0' second(1,1)"
argument_list|,
literal|"INTERVAL '0.0' SECOND(1, 1)"
argument_list|)
expr_stmt|;
comment|//alternate precision
name|checkExp
argument_list|(
literal|"interval '1234' second(4)"
argument_list|,
literal|"INTERVAL '1234' SECOND(4)"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval '1234.56789' second(4,5)"
argument_list|,
literal|"INTERVAL '1234.56789' SECOND(4, 5)"
argument_list|)
expr_stmt|;
comment|//sign
name|checkExp
argument_list|(
literal|"interval '+1' second"
argument_list|,
literal|"INTERVAL '+1' SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval '-1' second"
argument_list|,
literal|"INTERVAL '-1' SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval +'1' second"
argument_list|,
literal|"INTERVAL '1' SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval +'+1' second"
argument_list|,
literal|"INTERVAL '+1' SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval +'-1' second"
argument_list|,
literal|"INTERVAL '-1' SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval -'1' second"
argument_list|,
literal|"INTERVAL -'1' SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval -'+1' second"
argument_list|,
literal|"INTERVAL -'+1' SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval -'-1' second"
argument_list|,
literal|"INTERVAL -'-1' SECOND"
argument_list|)
expr_stmt|;
block|}
comment|/**      * Runs tests for INTERVAL... YEAR that should pass parser but fail      * validator. A substantially identical set of tests exists in      * SqlValidatorTest, and any changes here should be synchronized there.      * Similarly, any changes to tests here should be echoed appropriately to      * each of the other 12 subTestIntervalXXXFailsValidation() tests.      */
specifier|public
name|void
name|subTestIntervalYearFailsValidation
parameter_list|()
block|{
comment|// Qualifier - field mismatches
name|checkExp
argument_list|(
literal|"INTERVAL '-' YEAR"
argument_list|,
literal|"INTERVAL '-' YEAR"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '1-2' YEAR"
argument_list|,
literal|"INTERVAL '1-2' YEAR"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '1.2' YEAR"
argument_list|,
literal|"INTERVAL '1.2' YEAR"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '1 2' YEAR"
argument_list|,
literal|"INTERVAL '1 2' YEAR"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '1-2' YEAR(2)"
argument_list|,
literal|"INTERVAL '1-2' YEAR(2)"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL 'bogus text' YEAR"
argument_list|,
literal|"INTERVAL 'bogus text' YEAR"
argument_list|)
expr_stmt|;
comment|// negative field values
name|checkExp
argument_list|(
literal|"INTERVAL '--1' YEAR"
argument_list|,
literal|"INTERVAL '--1' YEAR"
argument_list|)
expr_stmt|;
comment|// Field value out of range
comment|//  (default, explicit default, alt, neg alt, max, neg max)
name|checkExp
argument_list|(
literal|"INTERVAL '100' YEAR"
argument_list|,
literal|"INTERVAL '100' YEAR"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '100' YEAR(2)"
argument_list|,
literal|"INTERVAL '100' YEAR(2)"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '1000' YEAR(3)"
argument_list|,
literal|"INTERVAL '1000' YEAR(3)"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '-1000' YEAR(3)"
argument_list|,
literal|"INTERVAL '-1000' YEAR(3)"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '2147483648' YEAR(10)"
argument_list|,
literal|"INTERVAL '2147483648' YEAR(10)"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '-2147483648' YEAR(10)"
argument_list|,
literal|"INTERVAL '-2147483648' YEAR(10)"
argument_list|)
expr_stmt|;
comment|// precision> maximum
name|checkExp
argument_list|(
literal|"INTERVAL '1' YEAR(11)"
argument_list|,
literal|"INTERVAL '1' YEAR(11)"
argument_list|)
expr_stmt|;
comment|// precision< minimum allowed)
comment|// note: parser will catch negative values, here we
comment|// just need to check for 0
name|checkExp
argument_list|(
literal|"INTERVAL '0' YEAR(0)"
argument_list|,
literal|"INTERVAL '0' YEAR(0)"
argument_list|)
expr_stmt|;
block|}
comment|/**      * Runs tests for INTERVAL... YEAR TO MONTH that should pass parser but fail      * validator. A substantially identical set of tests exists in      * SqlValidatorTest, and any changes here should be synchronized there.      * Similarly, any changes to tests here should be echoed appropriately to      * each of the other 12 subTestIntervalXXXFailsValidation() tests.      */
specifier|public
name|void
name|subTestIntervalYearToMonthFailsValidation
parameter_list|()
block|{
comment|// Qualifier - field mismatches
name|checkExp
argument_list|(
literal|"INTERVAL '-' YEAR TO MONTH"
argument_list|,
literal|"INTERVAL '-' YEAR TO MONTH"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '1' YEAR TO MONTH"
argument_list|,
literal|"INTERVAL '1' YEAR TO MONTH"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '1:2' YEAR TO MONTH"
argument_list|,
literal|"INTERVAL '1:2' YEAR TO MONTH"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '1.2' YEAR TO MONTH"
argument_list|,
literal|"INTERVAL '1.2' YEAR TO MONTH"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '1 2' YEAR TO MONTH"
argument_list|,
literal|"INTERVAL '1 2' YEAR TO MONTH"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '1:2' YEAR(2) TO MONTH"
argument_list|,
literal|"INTERVAL '1:2' YEAR(2) TO MONTH"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL 'bogus text' YEAR TO MONTH"
argument_list|,
literal|"INTERVAL 'bogus text' YEAR TO MONTH"
argument_list|)
expr_stmt|;
comment|// negative field values
name|checkExp
argument_list|(
literal|"INTERVAL '--1-2' YEAR TO MONTH"
argument_list|,
literal|"INTERVAL '--1-2' YEAR TO MONTH"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '1--2' YEAR TO MONTH"
argument_list|,
literal|"INTERVAL '1--2' YEAR TO MONTH"
argument_list|)
expr_stmt|;
comment|// Field value out of range
comment|//  (default, explicit default, alt, neg alt, max, neg max)
comment|//  plus>max value for mid/end fields
name|checkExp
argument_list|(
literal|"INTERVAL '100-0' YEAR TO MONTH"
argument_list|,
literal|"INTERVAL '100-0' YEAR TO MONTH"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '100-0' YEAR(2) TO MONTH"
argument_list|,
literal|"INTERVAL '100-0' YEAR(2) TO MONTH"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '1000-0' YEAR(3) TO MONTH"
argument_list|,
literal|"INTERVAL '1000-0' YEAR(3) TO MONTH"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '-1000-0' YEAR(3) TO MONTH"
argument_list|,
literal|"INTERVAL '-1000-0' YEAR(3) TO MONTH"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '2147483648-0' YEAR(10) TO MONTH"
argument_list|,
literal|"INTERVAL '2147483648-0' YEAR(10) TO MONTH"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '-2147483648-0' YEAR(10) TO MONTH"
argument_list|,
literal|"INTERVAL '-2147483648-0' YEAR(10) TO MONTH"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '1-12' YEAR TO MONTH"
argument_list|,
literal|"INTERVAL '1-12' YEAR TO MONTH"
argument_list|)
expr_stmt|;
comment|// precision> maximum
name|checkExp
argument_list|(
literal|"INTERVAL '1-1' YEAR(11) TO MONTH"
argument_list|,
literal|"INTERVAL '1-1' YEAR(11) TO MONTH"
argument_list|)
expr_stmt|;
comment|// precision< minimum allowed)
comment|// note: parser will catch negative values, here we
comment|// just need to check for 0
name|checkExp
argument_list|(
literal|"INTERVAL '0-0' YEAR(0) TO MONTH"
argument_list|,
literal|"INTERVAL '0-0' YEAR(0) TO MONTH"
argument_list|)
expr_stmt|;
block|}
comment|/**      * Runs tests for INTERVAL... MONTH that should pass parser but fail      * validator. A substantially identical set of tests exists in      * SqlValidatorTest, and any changes here should be synchronized there.      * Similarly, any changes to tests here should be echoed appropriately to      * each of the other 12 subTestIntervalXXXFailsValidation() tests.      */
specifier|public
name|void
name|subTestIntervalMonthFailsValidation
parameter_list|()
block|{
comment|// Qualifier - field mismatches
name|checkExp
argument_list|(
literal|"INTERVAL '-' MONTH"
argument_list|,
literal|"INTERVAL '-' MONTH"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '1-2' MONTH"
argument_list|,
literal|"INTERVAL '1-2' MONTH"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '1.2' MONTH"
argument_list|,
literal|"INTERVAL '1.2' MONTH"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '1 2' MONTH"
argument_list|,
literal|"INTERVAL '1 2' MONTH"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '1-2' MONTH(2)"
argument_list|,
literal|"INTERVAL '1-2' MONTH(2)"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL 'bogus text' MONTH"
argument_list|,
literal|"INTERVAL 'bogus text' MONTH"
argument_list|)
expr_stmt|;
comment|// negative field values
name|checkExp
argument_list|(
literal|"INTERVAL '--1' MONTH"
argument_list|,
literal|"INTERVAL '--1' MONTH"
argument_list|)
expr_stmt|;
comment|// Field value out of range
comment|//  (default, explicit default, alt, neg alt, max, neg max)
name|checkExp
argument_list|(
literal|"INTERVAL '100' MONTH"
argument_list|,
literal|"INTERVAL '100' MONTH"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '100' MONTH(2)"
argument_list|,
literal|"INTERVAL '100' MONTH(2)"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '1000' MONTH(3)"
argument_list|,
literal|"INTERVAL '1000' MONTH(3)"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '-1000' MONTH(3)"
argument_list|,
literal|"INTERVAL '-1000' MONTH(3)"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '2147483648' MONTH(10)"
argument_list|,
literal|"INTERVAL '2147483648' MONTH(10)"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '-2147483648' MONTH(10)"
argument_list|,
literal|"INTERVAL '-2147483648' MONTH(10)"
argument_list|)
expr_stmt|;
comment|// precision> maximum
name|checkExp
argument_list|(
literal|"INTERVAL '1' MONTH(11)"
argument_list|,
literal|"INTERVAL '1' MONTH(11)"
argument_list|)
expr_stmt|;
comment|// precision< minimum allowed)
comment|// note: parser will catch negative values, here we
comment|// just need to check for 0
name|checkExp
argument_list|(
literal|"INTERVAL '0' MONTH(0)"
argument_list|,
literal|"INTERVAL '0' MONTH(0)"
argument_list|)
expr_stmt|;
block|}
comment|/**      * Runs tests for INTERVAL... DAY that should pass parser but fail      * validator. A substantially identical set of tests exists in      * SqlValidatorTest, and any changes here should be synchronized there.      * Similarly, any changes to tests here should be echoed appropriately to      * each of the other 12 subTestIntervalXXXFailsValidation() tests.      */
specifier|public
name|void
name|subTestIntervalDayFailsValidation
parameter_list|()
block|{
comment|// Qualifier - field mismatches
name|checkExp
argument_list|(
literal|"INTERVAL '-' DAY"
argument_list|,
literal|"INTERVAL '-' DAY"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '1-2' DAY"
argument_list|,
literal|"INTERVAL '1-2' DAY"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '1.2' DAY"
argument_list|,
literal|"INTERVAL '1.2' DAY"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '1 2' DAY"
argument_list|,
literal|"INTERVAL '1 2' DAY"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '1:2' DAY"
argument_list|,
literal|"INTERVAL '1:2' DAY"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '1-2' DAY(2)"
argument_list|,
literal|"INTERVAL '1-2' DAY(2)"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL 'bogus text' DAY"
argument_list|,
literal|"INTERVAL 'bogus text' DAY"
argument_list|)
expr_stmt|;
comment|// negative field values
name|checkExp
argument_list|(
literal|"INTERVAL '--1' DAY"
argument_list|,
literal|"INTERVAL '--1' DAY"
argument_list|)
expr_stmt|;
comment|// Field value out of range
comment|//  (default, explicit default, alt, neg alt, max, neg max)
name|checkExp
argument_list|(
literal|"INTERVAL '100' DAY"
argument_list|,
literal|"INTERVAL '100' DAY"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '100' DAY(2)"
argument_list|,
literal|"INTERVAL '100' DAY(2)"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '1000' DAY(3)"
argument_list|,
literal|"INTERVAL '1000' DAY(3)"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '-1000' DAY(3)"
argument_list|,
literal|"INTERVAL '-1000' DAY(3)"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '2147483648' DAY(10)"
argument_list|,
literal|"INTERVAL '2147483648' DAY(10)"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '-2147483648' DAY(10)"
argument_list|,
literal|"INTERVAL '-2147483648' DAY(10)"
argument_list|)
expr_stmt|;
comment|// precision> maximum
name|checkExp
argument_list|(
literal|"INTERVAL '1' DAY(11)"
argument_list|,
literal|"INTERVAL '1' DAY(11)"
argument_list|)
expr_stmt|;
comment|// precision< minimum allowed)
comment|// note: parser will catch negative values, here we
comment|// just need to check for 0
name|checkExp
argument_list|(
literal|"INTERVAL '0' DAY(0)"
argument_list|,
literal|"INTERVAL '0' DAY(0)"
argument_list|)
expr_stmt|;
block|}
comment|/**      * Runs tests for INTERVAL... DAY TO HOUR that should pass parser but fail      * validator. A substantially identical set of tests exists in      * SqlValidatorTest, and any changes here should be synchronized there.      * Similarly, any changes to tests here should be echoed appropriately to      * each of the other 12 subTestIntervalXXXFailsValidation() tests.      */
specifier|public
name|void
name|subTestIntervalDayToHourFailsValidation
parameter_list|()
block|{
comment|// Qualifier - field mismatches
name|checkExp
argument_list|(
literal|"INTERVAL '-' DAY TO HOUR"
argument_list|,
literal|"INTERVAL '-' DAY TO HOUR"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '1' DAY TO HOUR"
argument_list|,
literal|"INTERVAL '1' DAY TO HOUR"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '1:2' DAY TO HOUR"
argument_list|,
literal|"INTERVAL '1:2' DAY TO HOUR"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '1.2' DAY TO HOUR"
argument_list|,
literal|"INTERVAL '1.2' DAY TO HOUR"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '1 x' DAY TO HOUR"
argument_list|,
literal|"INTERVAL '1 x' DAY TO HOUR"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL ' ' DAY TO HOUR"
argument_list|,
literal|"INTERVAL ' ' DAY TO HOUR"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '1:2' DAY(2) TO HOUR"
argument_list|,
literal|"INTERVAL '1:2' DAY(2) TO HOUR"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL 'bogus text' DAY TO HOUR"
argument_list|,
literal|"INTERVAL 'bogus text' DAY TO HOUR"
argument_list|)
expr_stmt|;
comment|// negative field values
name|checkExp
argument_list|(
literal|"INTERVAL '--1 1' DAY TO HOUR"
argument_list|,
literal|"INTERVAL '--1 1' DAY TO HOUR"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '1 -1' DAY TO HOUR"
argument_list|,
literal|"INTERVAL '1 -1' DAY TO HOUR"
argument_list|)
expr_stmt|;
comment|// Field value out of range
comment|//  (default, explicit default, alt, neg alt, max, neg max)
comment|//  plus>max value for mid/end fields
name|checkExp
argument_list|(
literal|"INTERVAL '100 0' DAY TO HOUR"
argument_list|,
literal|"INTERVAL '100 0' DAY TO HOUR"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '100 0' DAY(2) TO HOUR"
argument_list|,
literal|"INTERVAL '100 0' DAY(2) TO HOUR"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '1000 0' DAY(3) TO HOUR"
argument_list|,
literal|"INTERVAL '1000 0' DAY(3) TO HOUR"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '-1000 0' DAY(3) TO HOUR"
argument_list|,
literal|"INTERVAL '-1000 0' DAY(3) TO HOUR"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '2147483648 0' DAY(10) TO HOUR"
argument_list|,
literal|"INTERVAL '2147483648 0' DAY(10) TO HOUR"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '-2147483648 0' DAY(10) TO HOUR"
argument_list|,
literal|"INTERVAL '-2147483648 0' DAY(10) TO HOUR"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '1 24' DAY TO HOUR"
argument_list|,
literal|"INTERVAL '1 24' DAY TO HOUR"
argument_list|)
expr_stmt|;
comment|// precision> maximum
name|checkExp
argument_list|(
literal|"INTERVAL '1 1' DAY(11) TO HOUR"
argument_list|,
literal|"INTERVAL '1 1' DAY(11) TO HOUR"
argument_list|)
expr_stmt|;
comment|// precision< minimum allowed)
comment|// note: parser will catch negative values, here we
comment|// just need to check for 0
name|checkExp
argument_list|(
literal|"INTERVAL '0 0' DAY(0) TO HOUR"
argument_list|,
literal|"INTERVAL '0 0' DAY(0) TO HOUR"
argument_list|)
expr_stmt|;
block|}
comment|/**      * Runs tests for INTERVAL... DAY TO MINUTE that should pass parser but fail      * validator. A substantially identical set of tests exists in      * SqlValidatorTest, and any changes here should be synchronized there.      * Similarly, any changes to tests here should be echoed appropriately to      * each of the other 12 subTestIntervalXXXFailsValidation() tests.      */
specifier|public
name|void
name|subTestIntervalDayToMinuteFailsValidation
parameter_list|()
block|{
comment|// Qualifier - field mismatches
name|checkExp
argument_list|(
literal|"INTERVAL ' :' DAY TO MINUTE"
argument_list|,
literal|"INTERVAL ' :' DAY TO MINUTE"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '1' DAY TO MINUTE"
argument_list|,
literal|"INTERVAL '1' DAY TO MINUTE"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '1 2' DAY TO MINUTE"
argument_list|,
literal|"INTERVAL '1 2' DAY TO MINUTE"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '1:2' DAY TO MINUTE"
argument_list|,
literal|"INTERVAL '1:2' DAY TO MINUTE"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '1.2' DAY TO MINUTE"
argument_list|,
literal|"INTERVAL '1.2' DAY TO MINUTE"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL 'x 1:1' DAY TO MINUTE"
argument_list|,
literal|"INTERVAL 'x 1:1' DAY TO MINUTE"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '1 x:1' DAY TO MINUTE"
argument_list|,
literal|"INTERVAL '1 x:1' DAY TO MINUTE"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '1 1:x' DAY TO MINUTE"
argument_list|,
literal|"INTERVAL '1 1:x' DAY TO MINUTE"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '1 1:2:3' DAY TO MINUTE"
argument_list|,
literal|"INTERVAL '1 1:2:3' DAY TO MINUTE"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '1 1:1:1.2' DAY TO MINUTE"
argument_list|,
literal|"INTERVAL '1 1:1:1.2' DAY TO MINUTE"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '1 1:2:3' DAY(2) TO MINUTE"
argument_list|,
literal|"INTERVAL '1 1:2:3' DAY(2) TO MINUTE"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '1 1' DAY(2) TO MINUTE"
argument_list|,
literal|"INTERVAL '1 1' DAY(2) TO MINUTE"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL 'bogus text' DAY TO MINUTE"
argument_list|,
literal|"INTERVAL 'bogus text' DAY TO MINUTE"
argument_list|)
expr_stmt|;
comment|// negative field values
name|checkExp
argument_list|(
literal|"INTERVAL '--1 1:1' DAY TO MINUTE"
argument_list|,
literal|"INTERVAL '--1 1:1' DAY TO MINUTE"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '1 -1:1' DAY TO MINUTE"
argument_list|,
literal|"INTERVAL '1 -1:1' DAY TO MINUTE"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '1 1:-1' DAY TO MINUTE"
argument_list|,
literal|"INTERVAL '1 1:-1' DAY TO MINUTE"
argument_list|)
expr_stmt|;
comment|// Field value out of range
comment|//  (default, explicit default, alt, neg alt, max, neg max)
comment|//  plus>max value for mid/end fields
name|checkExp
argument_list|(
literal|"INTERVAL '100 0' DAY TO MINUTE"
argument_list|,
literal|"INTERVAL '100 0' DAY TO MINUTE"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '100 0' DAY(2) TO MINUTE"
argument_list|,
literal|"INTERVAL '100 0' DAY(2) TO MINUTE"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '1000 0' DAY(3) TO MINUTE"
argument_list|,
literal|"INTERVAL '1000 0' DAY(3) TO MINUTE"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '-1000 0' DAY(3) TO MINUTE"
argument_list|,
literal|"INTERVAL '-1000 0' DAY(3) TO MINUTE"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '2147483648 0' DAY(10) TO MINUTE"
argument_list|,
literal|"INTERVAL '2147483648 0' DAY(10) TO MINUTE"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '-2147483648 0' DAY(10) TO MINUTE"
argument_list|,
literal|"INTERVAL '-2147483648 0' DAY(10) TO MINUTE"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '1 24:1' DAY TO MINUTE"
argument_list|,
literal|"INTERVAL '1 24:1' DAY TO MINUTE"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '1 1:60' DAY TO MINUTE"
argument_list|,
literal|"INTERVAL '1 1:60' DAY TO MINUTE"
argument_list|)
expr_stmt|;
comment|// precision> maximum
name|checkExp
argument_list|(
literal|"INTERVAL '1 1' DAY(11) TO MINUTE"
argument_list|,
literal|"INTERVAL '1 1' DAY(11) TO MINUTE"
argument_list|)
expr_stmt|;
comment|// precision< minimum allowed)
comment|// note: parser will catch negative values, here we
comment|// just need to check for 0
name|checkExp
argument_list|(
literal|"INTERVAL '0 0' DAY(0) TO MINUTE"
argument_list|,
literal|"INTERVAL '0 0' DAY(0) TO MINUTE"
argument_list|)
expr_stmt|;
block|}
comment|/**      * Runs tests for INTERVAL... DAY TO SECOND that should pass parser but fail      * validator. A substantially identical set of tests exists in      * SqlValidatorTest, and any changes here should be synchronized there.      * Similarly, any changes to tests here should be echoed appropriately to      * each of the other 12 subTestIntervalXXXFailsValidation() tests.      */
specifier|public
name|void
name|subTestIntervalDayToSecondFailsValidation
parameter_list|()
block|{
comment|// Qualifier - field mismatches
name|checkExp
argument_list|(
literal|"INTERVAL ' ::' DAY TO SECOND"
argument_list|,
literal|"INTERVAL ' ::' DAY TO SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL ' ::.' DAY TO SECOND"
argument_list|,
literal|"INTERVAL ' ::.' DAY TO SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '1' DAY TO SECOND"
argument_list|,
literal|"INTERVAL '1' DAY TO SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '1 2' DAY TO SECOND"
argument_list|,
literal|"INTERVAL '1 2' DAY TO SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '1:2' DAY TO SECOND"
argument_list|,
literal|"INTERVAL '1:2' DAY TO SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '1.2' DAY TO SECOND"
argument_list|,
literal|"INTERVAL '1.2' DAY TO SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '1 1:2' DAY TO SECOND"
argument_list|,
literal|"INTERVAL '1 1:2' DAY TO SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '1 1:2:x' DAY TO SECOND"
argument_list|,
literal|"INTERVAL '1 1:2:x' DAY TO SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '1:2:3' DAY TO SECOND"
argument_list|,
literal|"INTERVAL '1:2:3' DAY TO SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '1:1:1.2' DAY TO SECOND"
argument_list|,
literal|"INTERVAL '1:1:1.2' DAY TO SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '1 1:2' DAY(2) TO SECOND"
argument_list|,
literal|"INTERVAL '1 1:2' DAY(2) TO SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '1 1' DAY(2) TO SECOND"
argument_list|,
literal|"INTERVAL '1 1' DAY(2) TO SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL 'bogus text' DAY TO SECOND"
argument_list|,
literal|"INTERVAL 'bogus text' DAY TO SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '2345 6:7:8901' DAY TO SECOND(4)"
argument_list|,
literal|"INTERVAL '2345 6:7:8901' DAY TO SECOND(4)"
argument_list|)
expr_stmt|;
comment|// negative field values
name|checkExp
argument_list|(
literal|"INTERVAL '--1 1:1:1' DAY TO SECOND"
argument_list|,
literal|"INTERVAL '--1 1:1:1' DAY TO SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '1 -1:1:1' DAY TO SECOND"
argument_list|,
literal|"INTERVAL '1 -1:1:1' DAY TO SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '1 1:-1:1' DAY TO SECOND"
argument_list|,
literal|"INTERVAL '1 1:-1:1' DAY TO SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '1 1:1:-1' DAY TO SECOND"
argument_list|,
literal|"INTERVAL '1 1:1:-1' DAY TO SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '1 1:1:1.-1' DAY TO SECOND"
argument_list|,
literal|"INTERVAL '1 1:1:1.-1' DAY TO SECOND"
argument_list|)
expr_stmt|;
comment|// Field value out of range
comment|//  (default, explicit default, alt, neg alt, max, neg max)
comment|//  plus>max value for mid/end fields
name|checkExp
argument_list|(
literal|"INTERVAL '100 0' DAY TO SECOND"
argument_list|,
literal|"INTERVAL '100 0' DAY TO SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '100 0' DAY(2) TO SECOND"
argument_list|,
literal|"INTERVAL '100 0' DAY(2) TO SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '1000 0' DAY(3) TO SECOND"
argument_list|,
literal|"INTERVAL '1000 0' DAY(3) TO SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '-1000 0' DAY(3) TO SECOND"
argument_list|,
literal|"INTERVAL '-1000 0' DAY(3) TO SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '2147483648 0' DAY(10) TO SECOND"
argument_list|,
literal|"INTERVAL '2147483648 0' DAY(10) TO SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '-2147483648 0' DAY(10) TO SECOND"
argument_list|,
literal|"INTERVAL '-2147483648 0' DAY(10) TO SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '1 24:1:1' DAY TO SECOND"
argument_list|,
literal|"INTERVAL '1 24:1:1' DAY TO SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '1 1:60:1' DAY TO SECOND"
argument_list|,
literal|"INTERVAL '1 1:60:1' DAY TO SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '1 1:1:60' DAY TO SECOND"
argument_list|,
literal|"INTERVAL '1 1:1:60' DAY TO SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '1 1:1:1.0000001' DAY TO SECOND"
argument_list|,
literal|"INTERVAL '1 1:1:1.0000001' DAY TO SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '1 1:1:1.0001' DAY TO SECOND(3)"
argument_list|,
literal|"INTERVAL '1 1:1:1.0001' DAY TO SECOND(3)"
argument_list|)
expr_stmt|;
comment|// precision> maximum
name|checkExp
argument_list|(
literal|"INTERVAL '1 1' DAY(11) TO SECOND"
argument_list|,
literal|"INTERVAL '1 1' DAY(11) TO SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '1 1' DAY TO SECOND(10)"
argument_list|,
literal|"INTERVAL '1 1' DAY TO SECOND(10)"
argument_list|)
expr_stmt|;
comment|// precision< minimum allowed)
comment|// note: parser will catch negative values, here we
comment|// just need to check for 0
name|checkExp
argument_list|(
literal|"INTERVAL '0 0:0:0' DAY(0) TO SECOND"
argument_list|,
literal|"INTERVAL '0 0:0:0' DAY(0) TO SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '0 0:0:0' DAY TO SECOND(0)"
argument_list|,
literal|"INTERVAL '0 0:0:0' DAY TO SECOND(0)"
argument_list|)
expr_stmt|;
block|}
comment|/**      * Runs tests for INTERVAL... HOUR that should pass parser but fail      * validator. A substantially identical set of tests exists in      * SqlValidatorTest, and any changes here should be synchronized there.      * Similarly, any changes to tests here should be echoed appropriately to      * each of the other 12 subTestIntervalXXXFailsValidation() tests.      */
specifier|public
name|void
name|subTestIntervalHourFailsValidation
parameter_list|()
block|{
comment|// Qualifier - field mismatches
name|checkExp
argument_list|(
literal|"INTERVAL '-' HOUR"
argument_list|,
literal|"INTERVAL '-' HOUR"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '1-2' HOUR"
argument_list|,
literal|"INTERVAL '1-2' HOUR"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '1.2' HOUR"
argument_list|,
literal|"INTERVAL '1.2' HOUR"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '1 2' HOUR"
argument_list|,
literal|"INTERVAL '1 2' HOUR"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '1:2' HOUR"
argument_list|,
literal|"INTERVAL '1:2' HOUR"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '1-2' HOUR(2)"
argument_list|,
literal|"INTERVAL '1-2' HOUR(2)"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL 'bogus text' HOUR"
argument_list|,
literal|"INTERVAL 'bogus text' HOUR"
argument_list|)
expr_stmt|;
comment|// negative field values
name|checkExp
argument_list|(
literal|"INTERVAL '--1' HOUR"
argument_list|,
literal|"INTERVAL '--1' HOUR"
argument_list|)
expr_stmt|;
comment|// Field value out of range
comment|//  (default, explicit default, alt, neg alt, max, neg max)
name|checkExp
argument_list|(
literal|"INTERVAL '100' HOUR"
argument_list|,
literal|"INTERVAL '100' HOUR"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '100' HOUR(2)"
argument_list|,
literal|"INTERVAL '100' HOUR(2)"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '1000' HOUR(3)"
argument_list|,
literal|"INTERVAL '1000' HOUR(3)"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '-1000' HOUR(3)"
argument_list|,
literal|"INTERVAL '-1000' HOUR(3)"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '2147483648' HOUR(10)"
argument_list|,
literal|"INTERVAL '2147483648' HOUR(10)"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '-2147483648' HOUR(10)"
argument_list|,
literal|"INTERVAL '-2147483648' HOUR(10)"
argument_list|)
expr_stmt|;
comment|// negative field values
name|checkExp
argument_list|(
literal|"INTERVAL '--1' HOUR"
argument_list|,
literal|"INTERVAL '--1' HOUR"
argument_list|)
expr_stmt|;
comment|// precision> maximum
name|checkExp
argument_list|(
literal|"INTERVAL '1' HOUR(11)"
argument_list|,
literal|"INTERVAL '1' HOUR(11)"
argument_list|)
expr_stmt|;
comment|// precision< minimum allowed)
comment|// note: parser will catch negative values, here we
comment|// just need to check for 0
name|checkExp
argument_list|(
literal|"INTERVAL '0' HOUR(0)"
argument_list|,
literal|"INTERVAL '0' HOUR(0)"
argument_list|)
expr_stmt|;
block|}
comment|/**      * Runs tests for INTERVAL... HOUR TO MINUTE that should pass parser but      * fail validator. A substantially identical set of tests exists in      * SqlValidatorTest, and any changes here should be synchronized there.      * Similarly, any changes to tests here should be echoed appropriately to      * each of the other 12 subTestIntervalXXXFailsValidation() tests.      */
specifier|public
name|void
name|subTestIntervalHourToMinuteFailsValidation
parameter_list|()
block|{
comment|// Qualifier - field mismatches
name|checkExp
argument_list|(
literal|"INTERVAL ':' HOUR TO MINUTE"
argument_list|,
literal|"INTERVAL ':' HOUR TO MINUTE"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '1' HOUR TO MINUTE"
argument_list|,
literal|"INTERVAL '1' HOUR TO MINUTE"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '1:x' HOUR TO MINUTE"
argument_list|,
literal|"INTERVAL '1:x' HOUR TO MINUTE"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '1.2' HOUR TO MINUTE"
argument_list|,
literal|"INTERVAL '1.2' HOUR TO MINUTE"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '1 2' HOUR TO MINUTE"
argument_list|,
literal|"INTERVAL '1 2' HOUR TO MINUTE"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '1:2:3' HOUR TO MINUTE"
argument_list|,
literal|"INTERVAL '1:2:3' HOUR TO MINUTE"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '1 2' HOUR(2) TO MINUTE"
argument_list|,
literal|"INTERVAL '1 2' HOUR(2) TO MINUTE"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL 'bogus text' HOUR TO MINUTE"
argument_list|,
literal|"INTERVAL 'bogus text' HOUR TO MINUTE"
argument_list|)
expr_stmt|;
comment|// negative field values
name|checkExp
argument_list|(
literal|"INTERVAL '--1:1' HOUR TO MINUTE"
argument_list|,
literal|"INTERVAL '--1:1' HOUR TO MINUTE"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '1:-1' HOUR TO MINUTE"
argument_list|,
literal|"INTERVAL '1:-1' HOUR TO MINUTE"
argument_list|)
expr_stmt|;
comment|// Field value out of range
comment|//  (default, explicit default, alt, neg alt, max, neg max)
comment|//  plus>max value for mid/end fields
name|checkExp
argument_list|(
literal|"INTERVAL '100:0' HOUR TO MINUTE"
argument_list|,
literal|"INTERVAL '100:0' HOUR TO MINUTE"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '100:0' HOUR(2) TO MINUTE"
argument_list|,
literal|"INTERVAL '100:0' HOUR(2) TO MINUTE"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '1000:0' HOUR(3) TO MINUTE"
argument_list|,
literal|"INTERVAL '1000:0' HOUR(3) TO MINUTE"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '-1000:0' HOUR(3) TO MINUTE"
argument_list|,
literal|"INTERVAL '-1000:0' HOUR(3) TO MINUTE"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '2147483648:0' HOUR(10) TO MINUTE"
argument_list|,
literal|"INTERVAL '2147483648:0' HOUR(10) TO MINUTE"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '-2147483648:0' HOUR(10) TO MINUTE"
argument_list|,
literal|"INTERVAL '-2147483648:0' HOUR(10) TO MINUTE"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '1:24' HOUR TO MINUTE"
argument_list|,
literal|"INTERVAL '1:24' HOUR TO MINUTE"
argument_list|)
expr_stmt|;
comment|// precision> maximum
name|checkExp
argument_list|(
literal|"INTERVAL '1:1' HOUR(11) TO MINUTE"
argument_list|,
literal|"INTERVAL '1:1' HOUR(11) TO MINUTE"
argument_list|)
expr_stmt|;
comment|// precision< minimum allowed)
comment|// note: parser will catch negative values, here we
comment|// just need to check for 0
name|checkExp
argument_list|(
literal|"INTERVAL '0:0' HOUR(0) TO MINUTE"
argument_list|,
literal|"INTERVAL '0:0' HOUR(0) TO MINUTE"
argument_list|)
expr_stmt|;
block|}
comment|/**      * Runs tests for INTERVAL... HOUR TO SECOND that should pass parser but      * fail validator. A substantially identical set of tests exists in      * SqlValidatorTest, and any changes here should be synchronized there.      * Similarly, any changes to tests here should be echoed appropriately to      * each of the other 12 subTestIntervalXXXFailsValidation() tests.      */
specifier|public
name|void
name|subTestIntervalHourToSecondFailsValidation
parameter_list|()
block|{
comment|// Qualifier - field mismatches
name|checkExp
argument_list|(
literal|"INTERVAL '::' HOUR TO SECOND"
argument_list|,
literal|"INTERVAL '::' HOUR TO SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '::.' HOUR TO SECOND"
argument_list|,
literal|"INTERVAL '::.' HOUR TO SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '1' HOUR TO SECOND"
argument_list|,
literal|"INTERVAL '1' HOUR TO SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '1 2' HOUR TO SECOND"
argument_list|,
literal|"INTERVAL '1 2' HOUR TO SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '1:2' HOUR TO SECOND"
argument_list|,
literal|"INTERVAL '1:2' HOUR TO SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '1.2' HOUR TO SECOND"
argument_list|,
literal|"INTERVAL '1.2' HOUR TO SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '1 1:2' HOUR TO SECOND"
argument_list|,
literal|"INTERVAL '1 1:2' HOUR TO SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '1:2:x' HOUR TO SECOND"
argument_list|,
literal|"INTERVAL '1:2:x' HOUR TO SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '1:x:3' HOUR TO SECOND"
argument_list|,
literal|"INTERVAL '1:x:3' HOUR TO SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '1:1:1.x' HOUR TO SECOND"
argument_list|,
literal|"INTERVAL '1:1:1.x' HOUR TO SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '1 1:2' HOUR(2) TO SECOND"
argument_list|,
literal|"INTERVAL '1 1:2' HOUR(2) TO SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '1 1' HOUR(2) TO SECOND"
argument_list|,
literal|"INTERVAL '1 1' HOUR(2) TO SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL 'bogus text' HOUR TO SECOND"
argument_list|,
literal|"INTERVAL 'bogus text' HOUR TO SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '6:7:8901' HOUR TO SECOND(4)"
argument_list|,
literal|"INTERVAL '6:7:8901' HOUR TO SECOND(4)"
argument_list|)
expr_stmt|;
comment|// negative field values
name|checkExp
argument_list|(
literal|"INTERVAL '--1:1:1' HOUR TO SECOND"
argument_list|,
literal|"INTERVAL '--1:1:1' HOUR TO SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '1:-1:1' HOUR TO SECOND"
argument_list|,
literal|"INTERVAL '1:-1:1' HOUR TO SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '1:1:-1' HOUR TO SECOND"
argument_list|,
literal|"INTERVAL '1:1:-1' HOUR TO SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '1:1:1.-1' HOUR TO SECOND"
argument_list|,
literal|"INTERVAL '1:1:1.-1' HOUR TO SECOND"
argument_list|)
expr_stmt|;
comment|// Field value out of range
comment|//  (default, explicit default, alt, neg alt, max, neg max)
comment|//  plus>max value for mid/end fields
name|checkExp
argument_list|(
literal|"INTERVAL '100:0:0' HOUR TO SECOND"
argument_list|,
literal|"INTERVAL '100:0:0' HOUR TO SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '100:0:0' HOUR(2) TO SECOND"
argument_list|,
literal|"INTERVAL '100:0:0' HOUR(2) TO SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '1000:0:0' HOUR(3) TO SECOND"
argument_list|,
literal|"INTERVAL '1000:0:0' HOUR(3) TO SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '-1000:0:0' HOUR(3) TO SECOND"
argument_list|,
literal|"INTERVAL '-1000:0:0' HOUR(3) TO SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '2147483648:0:0' HOUR(10) TO SECOND"
argument_list|,
literal|"INTERVAL '2147483648:0:0' HOUR(10) TO SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '-2147483648:0:0' HOUR(10) TO SECOND"
argument_list|,
literal|"INTERVAL '-2147483648:0:0' HOUR(10) TO SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '1:60:1' HOUR TO SECOND"
argument_list|,
literal|"INTERVAL '1:60:1' HOUR TO SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '1:1:60' HOUR TO SECOND"
argument_list|,
literal|"INTERVAL '1:1:60' HOUR TO SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '1:1:1.0000001' HOUR TO SECOND"
argument_list|,
literal|"INTERVAL '1:1:1.0000001' HOUR TO SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '1:1:1.0001' HOUR TO SECOND(3)"
argument_list|,
literal|"INTERVAL '1:1:1.0001' HOUR TO SECOND(3)"
argument_list|)
expr_stmt|;
comment|// precision> maximum
name|checkExp
argument_list|(
literal|"INTERVAL '1:1:1' HOUR(11) TO SECOND"
argument_list|,
literal|"INTERVAL '1:1:1' HOUR(11) TO SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '1:1:1' HOUR TO SECOND(10)"
argument_list|,
literal|"INTERVAL '1:1:1' HOUR TO SECOND(10)"
argument_list|)
expr_stmt|;
comment|// precision< minimum allowed)
comment|// note: parser will catch negative values, here we
comment|// just need to check for 0
name|checkExp
argument_list|(
literal|"INTERVAL '0:0:0' HOUR(0) TO SECOND"
argument_list|,
literal|"INTERVAL '0:0:0' HOUR(0) TO SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '0:0:0' HOUR TO SECOND(0)"
argument_list|,
literal|"INTERVAL '0:0:0' HOUR TO SECOND(0)"
argument_list|)
expr_stmt|;
block|}
comment|/**      * Runs tests for INTERVAL... MINUTE that should pass parser but fail      * validator. A substantially identical set of tests exists in      * SqlValidatorTest, and any changes here should be synchronized there.      * Similarly, any changes to tests here should be echoed appropriately to      * each of the other 12 subTestIntervalXXXFailsValidation() tests.      */
specifier|public
name|void
name|subTestIntervalMinuteFailsValidation
parameter_list|()
block|{
comment|// Qualifier - field mismatches
name|checkExp
argument_list|(
literal|"INTERVAL '-' MINUTE"
argument_list|,
literal|"INTERVAL '-' MINUTE"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '1-2' MINUTE"
argument_list|,
literal|"INTERVAL '1-2' MINUTE"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '1.2' MINUTE"
argument_list|,
literal|"INTERVAL '1.2' MINUTE"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '1 2' MINUTE"
argument_list|,
literal|"INTERVAL '1 2' MINUTE"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '1:2' MINUTE"
argument_list|,
literal|"INTERVAL '1:2' MINUTE"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '1-2' MINUTE(2)"
argument_list|,
literal|"INTERVAL '1-2' MINUTE(2)"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL 'bogus text' MINUTE"
argument_list|,
literal|"INTERVAL 'bogus text' MINUTE"
argument_list|)
expr_stmt|;
comment|// negative field values
name|checkExp
argument_list|(
literal|"INTERVAL '--1' MINUTE"
argument_list|,
literal|"INTERVAL '--1' MINUTE"
argument_list|)
expr_stmt|;
comment|// Field value out of range
comment|//  (default, explicit default, alt, neg alt, max, neg max)
name|checkExp
argument_list|(
literal|"INTERVAL '100' MINUTE"
argument_list|,
literal|"INTERVAL '100' MINUTE"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '100' MINUTE(2)"
argument_list|,
literal|"INTERVAL '100' MINUTE(2)"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '1000' MINUTE(3)"
argument_list|,
literal|"INTERVAL '1000' MINUTE(3)"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '-1000' MINUTE(3)"
argument_list|,
literal|"INTERVAL '-1000' MINUTE(3)"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '2147483648' MINUTE(10)"
argument_list|,
literal|"INTERVAL '2147483648' MINUTE(10)"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '-2147483648' MINUTE(10)"
argument_list|,
literal|"INTERVAL '-2147483648' MINUTE(10)"
argument_list|)
expr_stmt|;
comment|// precision> maximum
name|checkExp
argument_list|(
literal|"INTERVAL '1' MINUTE(11)"
argument_list|,
literal|"INTERVAL '1' MINUTE(11)"
argument_list|)
expr_stmt|;
comment|// precision< minimum allowed)
comment|// note: parser will catch negative values, here we
comment|// just need to check for 0
name|checkExp
argument_list|(
literal|"INTERVAL '0' MINUTE(0)"
argument_list|,
literal|"INTERVAL '0' MINUTE(0)"
argument_list|)
expr_stmt|;
block|}
comment|/**      * Runs tests for INTERVAL... MINUTE TO SECOND that should pass parser but      * fail validator. A substantially identical set of tests exists in      * SqlValidatorTest, and any changes here should be synchronized there.      * Similarly, any changes to tests here should be echoed appropriately to      * each of the other 12 subTestIntervalXXXFailsValidation() tests.      */
specifier|public
name|void
name|subTestIntervalMinuteToSecondFailsValidation
parameter_list|()
block|{
comment|// Qualifier - field mismatches
name|checkExp
argument_list|(
literal|"INTERVAL ':' MINUTE TO SECOND"
argument_list|,
literal|"INTERVAL ':' MINUTE TO SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL ':.' MINUTE TO SECOND"
argument_list|,
literal|"INTERVAL ':.' MINUTE TO SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '1' MINUTE TO SECOND"
argument_list|,
literal|"INTERVAL '1' MINUTE TO SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '1 2' MINUTE TO SECOND"
argument_list|,
literal|"INTERVAL '1 2' MINUTE TO SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '1.2' MINUTE TO SECOND"
argument_list|,
literal|"INTERVAL '1.2' MINUTE TO SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '1 1:2' MINUTE TO SECOND"
argument_list|,
literal|"INTERVAL '1 1:2' MINUTE TO SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '1:x' MINUTE TO SECOND"
argument_list|,
literal|"INTERVAL '1:x' MINUTE TO SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL 'x:3' MINUTE TO SECOND"
argument_list|,
literal|"INTERVAL 'x:3' MINUTE TO SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '1:1.x' MINUTE TO SECOND"
argument_list|,
literal|"INTERVAL '1:1.x' MINUTE TO SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '1 1:2' MINUTE(2) TO SECOND"
argument_list|,
literal|"INTERVAL '1 1:2' MINUTE(2) TO SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '1 1' MINUTE(2) TO SECOND"
argument_list|,
literal|"INTERVAL '1 1' MINUTE(2) TO SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL 'bogus text' MINUTE TO SECOND"
argument_list|,
literal|"INTERVAL 'bogus text' MINUTE TO SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '7:8901' MINUTE TO SECOND(4)"
argument_list|,
literal|"INTERVAL '7:8901' MINUTE TO SECOND(4)"
argument_list|)
expr_stmt|;
comment|// negative field values
name|checkExp
argument_list|(
literal|"INTERVAL '--1:1' MINUTE TO SECOND"
argument_list|,
literal|"INTERVAL '--1:1' MINUTE TO SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '1:-1' MINUTE TO SECOND"
argument_list|,
literal|"INTERVAL '1:-1' MINUTE TO SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '1:1.-1' MINUTE TO SECOND"
argument_list|,
literal|"INTERVAL '1:1.-1' MINUTE TO SECOND"
argument_list|)
expr_stmt|;
comment|// Field value out of range
comment|//  (default, explicit default, alt, neg alt, max, neg max)
comment|//  plus>max value for mid/end fields
name|checkExp
argument_list|(
literal|"INTERVAL '100:0' MINUTE TO SECOND"
argument_list|,
literal|"INTERVAL '100:0' MINUTE TO SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '100:0' MINUTE(2) TO SECOND"
argument_list|,
literal|"INTERVAL '100:0' MINUTE(2) TO SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '1000:0' MINUTE(3) TO SECOND"
argument_list|,
literal|"INTERVAL '1000:0' MINUTE(3) TO SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '-1000:0' MINUTE(3) TO SECOND"
argument_list|,
literal|"INTERVAL '-1000:0' MINUTE(3) TO SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '2147483648:0' MINUTE(10) TO SECOND"
argument_list|,
literal|"INTERVAL '2147483648:0' MINUTE(10) TO SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '-2147483648:0' MINUTE(10) TO SECOND"
argument_list|,
literal|"INTERVAL '-2147483648:0' MINUTE(10) TO SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '1:60' MINUTE TO SECOND"
argument_list|,
literal|"INTERVAL '1:60' MINUTE TO SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '1:1.0000001' MINUTE TO SECOND"
argument_list|,
literal|"INTERVAL '1:1.0000001' MINUTE TO SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '1:1:1.0001' MINUTE TO SECOND(3)"
argument_list|,
literal|"INTERVAL '1:1:1.0001' MINUTE TO SECOND(3)"
argument_list|)
expr_stmt|;
comment|// precision> maximum
name|checkExp
argument_list|(
literal|"INTERVAL '1:1' MINUTE(11) TO SECOND"
argument_list|,
literal|"INTERVAL '1:1' MINUTE(11) TO SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '1:1' MINUTE TO SECOND(10)"
argument_list|,
literal|"INTERVAL '1:1' MINUTE TO SECOND(10)"
argument_list|)
expr_stmt|;
comment|// precision< minimum allowed)
comment|// note: parser will catch negative values, here we
comment|// just need to check for 0
name|checkExp
argument_list|(
literal|"INTERVAL '0:0' MINUTE(0) TO SECOND"
argument_list|,
literal|"INTERVAL '0:0' MINUTE(0) TO SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '0:0' MINUTE TO SECOND(0)"
argument_list|,
literal|"INTERVAL '0:0' MINUTE TO SECOND(0)"
argument_list|)
expr_stmt|;
block|}
comment|/**      * Runs tests for INTERVAL... SECOND that should pass parser but fail      * validator. A substantially identical set of tests exists in      * SqlValidatorTest, and any changes here should be synchronized there.      * Similarly, any changes to tests here should be echoed appropriately to      * each of the other 12 subTestIntervalXXXFailsValidation() tests.      */
specifier|public
name|void
name|subTestIntervalSecondFailsValidation
parameter_list|()
block|{
comment|// Qualifier - field mismatches
name|checkExp
argument_list|(
literal|"INTERVAL ':' SECOND"
argument_list|,
literal|"INTERVAL ':' SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '.' SECOND"
argument_list|,
literal|"INTERVAL '.' SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '1-2' SECOND"
argument_list|,
literal|"INTERVAL '1-2' SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '1.x' SECOND"
argument_list|,
literal|"INTERVAL '1.x' SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL 'x.1' SECOND"
argument_list|,
literal|"INTERVAL 'x.1' SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '1 2' SECOND"
argument_list|,
literal|"INTERVAL '1 2' SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '1:2' SECOND"
argument_list|,
literal|"INTERVAL '1:2' SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '1-2' SECOND(2)"
argument_list|,
literal|"INTERVAL '1-2' SECOND(2)"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL 'bogus text' SECOND"
argument_list|,
literal|"INTERVAL 'bogus text' SECOND"
argument_list|)
expr_stmt|;
comment|// negative field values
name|checkExp
argument_list|(
literal|"INTERVAL '--1' SECOND"
argument_list|,
literal|"INTERVAL '--1' SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '1.-1' SECOND"
argument_list|,
literal|"INTERVAL '1.-1' SECOND"
argument_list|)
expr_stmt|;
comment|// Field value out of range
comment|//  (default, explicit default, alt, neg alt, max, neg max)
name|checkExp
argument_list|(
literal|"INTERVAL '100' SECOND"
argument_list|,
literal|"INTERVAL '100' SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '100' SECOND(2)"
argument_list|,
literal|"INTERVAL '100' SECOND(2)"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '1000' SECOND(3)"
argument_list|,
literal|"INTERVAL '1000' SECOND(3)"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '-1000' SECOND(3)"
argument_list|,
literal|"INTERVAL '-1000' SECOND(3)"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '2147483648' SECOND(10)"
argument_list|,
literal|"INTERVAL '2147483648' SECOND(10)"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '-2147483648' SECOND(10)"
argument_list|,
literal|"INTERVAL '-2147483648' SECOND(10)"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '1.0000001' SECOND"
argument_list|,
literal|"INTERVAL '1.0000001' SECOND"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '1.0000001' SECOND(2)"
argument_list|,
literal|"INTERVAL '1.0000001' SECOND(2)"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '1.0001' SECOND(2, 3)"
argument_list|,
literal|"INTERVAL '1.0001' SECOND(2, 3)"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '1.000000001' SECOND(2, 9)"
argument_list|,
literal|"INTERVAL '1.000000001' SECOND(2, 9)"
argument_list|)
expr_stmt|;
comment|// precision> maximum
name|checkExp
argument_list|(
literal|"INTERVAL '1' SECOND(11)"
argument_list|,
literal|"INTERVAL '1' SECOND(11)"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '1.1' SECOND(1, 10)"
argument_list|,
literal|"INTERVAL '1.1' SECOND(1, 10)"
argument_list|)
expr_stmt|;
comment|// precision< minimum allowed)
comment|// note: parser will catch negative values, here we
comment|// just need to check for 0
name|checkExp
argument_list|(
literal|"INTERVAL '0' SECOND(0)"
argument_list|,
literal|"INTERVAL '0' SECOND(0)"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"INTERVAL '0' SECOND(1, 0)"
argument_list|,
literal|"INTERVAL '0' SECOND(1, 0)"
argument_list|)
expr_stmt|;
block|}
comment|/**      * Runs tests for each of the thirteen different main types of INTERVAL      * qualifiers (YEAR, YEAR TO MONTH, etc.) Tests in this section fall into      * two categories:      *      *<ul>      *<li>xxxPositive: tests that should pass parser and validator</li>      *<li>xxxFailsValidation: tests that should pass parser but fail validator      *</li>      *</ul>      *      * A substantially identical set of tests exists in SqlValidatorTest, and      * any changes here should be synchronized there.      */
specifier|public
name|void
name|testIntervalLiterals
parameter_list|()
block|{
name|subTestIntervalYearPositive
argument_list|()
expr_stmt|;
name|subTestIntervalYearToMonthPositive
argument_list|()
expr_stmt|;
name|subTestIntervalMonthPositive
argument_list|()
expr_stmt|;
name|subTestIntervalDayPositive
argument_list|()
expr_stmt|;
name|subTestIntervalDayToHourPositive
argument_list|()
expr_stmt|;
name|subTestIntervalDayToMinutePositive
argument_list|()
expr_stmt|;
name|subTestIntervalDayToSecondPositive
argument_list|()
expr_stmt|;
name|subTestIntervalHourPositive
argument_list|()
expr_stmt|;
name|subTestIntervalHourToMinutePositive
argument_list|()
expr_stmt|;
name|subTestIntervalHourToSecondPositive
argument_list|()
expr_stmt|;
name|subTestIntervalMinutePositive
argument_list|()
expr_stmt|;
name|subTestIntervalMinuteToSecondPositive
argument_list|()
expr_stmt|;
name|subTestIntervalSecondPositive
argument_list|()
expr_stmt|;
name|subTestIntervalYearFailsValidation
argument_list|()
expr_stmt|;
name|subTestIntervalYearToMonthFailsValidation
argument_list|()
expr_stmt|;
name|subTestIntervalMonthFailsValidation
argument_list|()
expr_stmt|;
name|subTestIntervalDayFailsValidation
argument_list|()
expr_stmt|;
name|subTestIntervalDayToHourFailsValidation
argument_list|()
expr_stmt|;
name|subTestIntervalDayToMinuteFailsValidation
argument_list|()
expr_stmt|;
name|subTestIntervalDayToSecondFailsValidation
argument_list|()
expr_stmt|;
name|subTestIntervalHourFailsValidation
argument_list|()
expr_stmt|;
name|subTestIntervalHourToMinuteFailsValidation
argument_list|()
expr_stmt|;
name|subTestIntervalHourToSecondFailsValidation
argument_list|()
expr_stmt|;
name|subTestIntervalMinuteFailsValidation
argument_list|()
expr_stmt|;
name|subTestIntervalMinuteToSecondFailsValidation
argument_list|()
expr_stmt|;
name|subTestIntervalSecondFailsValidation
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|testUnparseableIntervalQualifiers
parameter_list|()
block|{
comment|// No qualifier
name|checkExpFails
argument_list|(
literal|"interval '1^'^"
argument_list|,
name|TestUtil
operator|.
name|fold
argument_list|(
literal|"Encountered \"<EOF>\" at line 1, column 12\\.\n"
operator|+
literal|"Was expecting one of:\n"
operator|+
literal|"    \"DAY\" \\.\\.\\.\n"
operator|+
literal|"    \"HOUR\" \\.\\.\\.\n"
operator|+
literal|"    \"MINUTE\" \\.\\.\\.\n"
operator|+
literal|"    \"MONTH\" \\.\\.\\.\n"
operator|+
literal|"    \"SECOND\" \\.\\.\\.\n"
operator|+
literal|"    \"YEAR\" \\.\\.\\.\n"
operator|+
literal|"    "
argument_list|)
argument_list|)
expr_stmt|;
comment|// illegal qualfiers, no precision in either field
name|checkExpFails
argument_list|(
literal|"interval '1' year ^to^ year"
argument_list|,
name|TestUtil
operator|.
name|fold
argument_list|(
literal|"(?s)Encountered \"to year\" at line 1, column 19.\n"
operator|+
literal|"Was expecting one of:\n"
operator|+
literal|"<EOF> \n"
operator|+
literal|"    \"AND\" \\.\\.\\..*"
argument_list|)
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"interval '1-2' year ^to^ day"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"interval '1-2' year ^to^ hour"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"interval '1-2' year ^to^ minute"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"interval '1-2' year ^to^ second"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"interval '1-2' month ^to^ year"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"interval '1-2' month ^to^ month"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"interval '1-2' month ^to^ day"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"interval '1-2' month ^to^ hour"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"interval '1-2' month ^to^ minute"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"interval '1-2' month ^to^ second"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"interval '1-2' day ^to^ year"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"interval '1-2' day ^to^ month"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"interval '1-2' day ^to^ day"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"interval '1-2' hour ^to^ year"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"interval '1-2' hour ^to^ month"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"interval '1-2' hour ^to^ day"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"interval '1-2' hour ^to^ hour"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"interval '1-2' minute ^to^ year"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"interval '1-2' minute ^to^ month"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"interval '1-2' minute ^to^ day"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"interval '1-2' minute ^to^ hour"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"interval '1-2' minute ^to^ minute"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"interval '1-2' second ^to^ year"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"interval '1-2' second ^to^ month"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"interval '1-2' second ^to^ day"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"interval '1-2' second ^to^ hour"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"interval '1-2' second ^to^ minute"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"interval '1-2' second ^to^ second"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
comment|// illegal qualfiers, including precision in start field
name|checkExpFails
argument_list|(
literal|"interval '1' year(3) ^to^ year"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"interval '1-2' year(3) ^to^ day"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"interval '1-2' year(3) ^to^ hour"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"interval '1-2' year(3) ^to^ minute"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"interval '1-2' year(3) ^to^ second"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"interval '1-2' month(3) ^to^ year"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"interval '1-2' month(3) ^to^ month"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"interval '1-2' month(3) ^to^ day"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"interval '1-2' month(3) ^to^ hour"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"interval '1-2' month(3) ^to^ minute"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"interval '1-2' month(3) ^to^ second"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"interval '1-2' day(3) ^to^ year"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"interval '1-2' day(3) ^to^ month"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"interval '1-2' hour(3) ^to^ year"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"interval '1-2' hour(3) ^to^ month"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"interval '1-2' hour(3) ^to^ day"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"interval '1-2' minute(3) ^to^ year"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"interval '1-2' minute(3) ^to^ month"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"interval '1-2' minute(3) ^to^ day"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"interval '1-2' minute(3) ^to^ hour"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"interval '1-2' second(3) ^to^ year"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"interval '1-2' second(3) ^to^ month"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"interval '1-2' second(3) ^to^ day"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"interval '1-2' second(3) ^to^ hour"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"interval '1-2' second(3) ^to^ minute"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
comment|// illegal qualfiers, including precision in end field
name|checkExpFails
argument_list|(
literal|"interval '1' year ^to^ year(2)"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"interval '1-2' year to month^(^2)"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"interval '1-2' year ^to^ day(2)"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"interval '1-2' year ^to^ hour(2)"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"interval '1-2' year ^to^ minute(2)"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"interval '1-2' year ^to^ second(2)"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"interval '1-2' year ^to^ second(2,6)"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"interval '1-2' month ^to^ year(2)"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"interval '1-2' month ^to^ month(2)"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"interval '1-2' month ^to^ day(2)"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"interval '1-2' month ^to^ hour(2)"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"interval '1-2' month ^to^ minute(2)"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"interval '1-2' month ^to^ second(2)"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"interval '1-2' month ^to^ second(2,6)"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"interval '1-2' day ^to^ year(2)"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"interval '1-2' day ^to^ month(2)"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"interval '1-2' day ^to^ day(2)"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"interval '1-2' day to hour^(^2)"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"interval '1-2' day to minute^(^2)"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"interval '1-2' day to second(2^,^6)"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"interval '1-2' hour ^to^ year(2)"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"interval '1-2' hour ^to^ month(2)"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"interval '1-2' hour ^to^ day(2)"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"interval '1-2' hour ^to^ hour(2)"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"interval '1-2' hour to minute^(^2)"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"interval '1-2' hour to second(2^,^6)"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"interval '1-2' minute ^to^ year(2)"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"interval '1-2' minute ^to^ month(2)"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"interval '1-2' minute ^to^ day(2)"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"interval '1-2' minute ^to^ hour(2)"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"interval '1-2' minute ^to^ minute(2)"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"interval '1-2' minute to second(2^,^6)"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"interval '1-2' second ^to^ year(2)"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"interval '1-2' second ^to^ month(2)"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"interval '1-2' second ^to^ day(2)"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"interval '1-2' second ^to^ hour(2)"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"interval '1-2' second ^to^ minute(2)"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"interval '1-2' second ^to^ second(2)"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"interval '1-2' second ^to^ second(2,6)"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
comment|// illegal qualfiers, including precision in start and end field
name|checkExpFails
argument_list|(
literal|"interval '1' year(3) ^to^ year(2)"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"interval '1-2' year(3) to month^(^2)"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"interval '1-2' year(3) ^to^ day(2)"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"interval '1-2' year(3) ^to^ hour(2)"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"interval '1-2' year(3) ^to^ minute(2)"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"interval '1-2' year(3) ^to^ second(2)"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"interval '1-2' year(3) ^to^ second(2,6)"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"interval '1-2' month(3) ^to^ year(2)"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"interval '1-2' month(3) ^to^ month(2)"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"interval '1-2' month(3) ^to^ day(2)"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"interval '1-2' month(3) ^to^ hour(2)"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"interval '1-2' month(3) ^to^ minute(2)"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"interval '1-2' month(3) ^to^ second(2)"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"interval '1-2' month(3) ^to^ second(2,6)"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"interval '1-2' day(3) ^to^ year(2)"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"interval '1-2' day(3) ^to^ month(2)"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"interval '1-2' day(3) ^to^ day(2)"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"interval '1-2' day(3) to hour^(^2)"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"interval '1-2' day(3) to minute^(^2)"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"interval '1-2' day(3) to second(2^,^6)"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"interval '1-2' hour(3) ^to^ year(2)"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"interval '1-2' hour(3) ^to^ month(2)"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"interval '1-2' hour(3) ^to^ day(2)"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"interval '1-2' hour(3) ^to^ hour(2)"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"interval '1-2' hour(3) to minute^(^2)"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"interval '1-2' hour(3) to second(2^,^6)"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"interval '1-2' minute(3) ^to^ year(2)"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"interval '1-2' minute(3) ^to^ month(2)"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"interval '1-2' minute(3) ^to^ day(2)"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"interval '1-2' minute(3) ^to^ hour(2)"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"interval '1-2' minute(3) ^to^ minute(2)"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"interval '1-2' minute(3) to second(2^,^6)"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"interval '1-2' second(3) ^to^ year(2)"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"interval '1-2' second(3) ^to^ month(2)"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"interval '1-2' second(3) ^to^ day(2)"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"interval '1-2' second(3) ^to^ hour(2)"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"interval '1-2' second(3) ^to^ minute(2)"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"interval '1-2' second(3) ^to^ second(2)"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"interval '1-2' second(3) ^to^ second(2,6)"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
comment|// precision of -1 (< minimum allowed)
name|checkExpFails
argument_list|(
literal|"INTERVAL '0' YEAR(^-^1)"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"INTERVAL '0-0' YEAR(^-^1) TO MONTH"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"INTERVAL '0' MONTH(^-^1)"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"INTERVAL '0' DAY(^-^1)"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"INTERVAL '0 0' DAY(^-^1) TO HOUR"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"INTERVAL '0 0' DAY(^-^1) TO MINUTE"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"INTERVAL '0 0:0:0' DAY(^-^1) TO SECOND"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"INTERVAL '0 0:0:0' DAY TO SECOND(^-^1)"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"INTERVAL '0' HOUR(^-^1)"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"INTERVAL '0:0' HOUR(^-^1) TO MINUTE"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"INTERVAL '0:0:0' HOUR(^-^1) TO SECOND"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"INTERVAL '0:0:0' HOUR TO SECOND(^-^1)"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"INTERVAL '0' MINUTE(^-^1)"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"INTERVAL '0:0' MINUTE(^-^1) TO SECOND"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"INTERVAL '0:0' MINUTE TO SECOND(^-^1)"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"INTERVAL '0' SECOND(^-^1)"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"INTERVAL '0' SECOND(1, ^-^1)"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
comment|// These may actually be legal per SQL2003, as the first field is
comment|// "more significant" than the last, but we do not support them
name|checkExpFails
argument_list|(
literal|"interval '1' day(3) ^to^ day"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"interval '1' hour(3) ^to^ hour"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"interval '1' minute(3) ^to^ minute"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"interval '1' second(3) ^to^ second"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"interval '1' second(3,1) ^to^ second"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"interval '1' second(2,3) ^to^ second"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"interval '1' second(2,2) ^to^ second(3)"
argument_list|,
name|ANY
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testMiscIntervalQualifier
parameter_list|()
block|{
name|checkExp
argument_list|(
literal|"interval '-' day"
argument_list|,
literal|"INTERVAL '-' DAY"
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"interval '1 2:3:4.567' day to hour ^to^ second"
argument_list|,
literal|"(?s)Encountered \"to\" at.*"
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"interval '1:2' minute to second(2^,^ 2)"
argument_list|,
literal|"(?s)Encountered \",\" at.*"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval '1:x' hour to minute"
argument_list|,
literal|"INTERVAL '1:x' HOUR TO MINUTE"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval '1:x:2' hour to second"
argument_list|,
literal|"INTERVAL '1:x:2' HOUR TO SECOND"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testIntervalOperators
parameter_list|()
block|{
name|checkExp
argument_list|(
literal|"-interval '1' day"
argument_list|,
literal|"(- INTERVAL '1' DAY)"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval '1' day + interval '1' day"
argument_list|,
literal|"(INTERVAL '1' DAY + INTERVAL '1' DAY)"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval '1' day - interval '1:2:3' hour to second"
argument_list|,
literal|"(INTERVAL '1' DAY - INTERVAL '1:2:3' HOUR TO SECOND)"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval -'1' day"
argument_list|,
literal|"INTERVAL -'1' DAY"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval '-1' day"
argument_list|,
literal|"INTERVAL '-1' DAY"
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"interval 'wael was here^'^"
argument_list|,
literal|"(?s)Encountered \"<EOF>\".*"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval 'wael was here' HOUR"
argument_list|,
literal|"INTERVAL 'wael was here' HOUR"
argument_list|)
expr_stmt|;
comment|// ok in parser, not in validator
block|}
specifier|public
name|void
name|testDateMinusDate
parameter_list|()
block|{
name|checkExp
argument_list|(
literal|"(date1 - date2) HOUR"
argument_list|,
literal|"((`DATE1` - `DATE2`) HOUR)"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"(date1 - date2) YEAR TO MONTH"
argument_list|,
literal|"((`DATE1` - `DATE2`) YEAR TO MONTH)"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"(date1 - date2) HOUR> interval '1' HOUR"
argument_list|,
literal|"(((`DATE1` - `DATE2`) HOUR)> INTERVAL '1' HOUR)"
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"^(date1 + date2) second^"
argument_list|,
literal|"(?s).*Illegal expression. Was expecting ..DATETIME - DATETIME. INTERVALQUALIFIER.*"
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"^(date1,date2,date2) second^"
argument_list|,
literal|"(?s).*Illegal expression. Was expecting ..DATETIME - DATETIME. INTERVALQUALIFIER.*"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testExtract
parameter_list|()
block|{
name|checkExp
argument_list|(
literal|"extract(year from x)"
argument_list|,
literal|"EXTRACT(YEAR FROM `X`)"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"extract(month from x)"
argument_list|,
literal|"EXTRACT(MONTH FROM `X`)"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"extract(day from x)"
argument_list|,
literal|"EXTRACT(DAY FROM `X`)"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"extract(hour from x)"
argument_list|,
literal|"EXTRACT(HOUR FROM `X`)"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"extract(minute from x)"
argument_list|,
literal|"EXTRACT(MINUTE FROM `X`)"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"extract(second from x)"
argument_list|,
literal|"EXTRACT(SECOND FROM `X`)"
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"extract(day ^to^ second from x)"
argument_list|,
literal|"(?s)Encountered \"to\".*"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testIntervalArithmetics
parameter_list|()
block|{
name|checkExp
argument_list|(
literal|"TIME '23:59:59' - interval '1' hour "
argument_list|,
literal|"(TIME '23:59:59' - INTERVAL '1' HOUR)"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"TIMESTAMP '2000-01-01 23:59:59.1' - interval '1' hour "
argument_list|,
literal|"(TIMESTAMP '2000-01-01 23:59:59.1' - INTERVAL '1' HOUR)"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"DATE '2000-01-01' - interval '1' hour "
argument_list|,
literal|"(DATE '2000-01-01' - INTERVAL '1' HOUR)"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"TIME '23:59:59' + interval '1' hour "
argument_list|,
literal|"(TIME '23:59:59' + INTERVAL '1' HOUR)"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"TIMESTAMP '2000-01-01 23:59:59.1' + interval '1' hour "
argument_list|,
literal|"(TIMESTAMP '2000-01-01 23:59:59.1' + INTERVAL '1' HOUR)"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"DATE '2000-01-01' + interval '1' hour "
argument_list|,
literal|"(DATE '2000-01-01' + INTERVAL '1' HOUR)"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval '1' hour + TIME '23:59:59' "
argument_list|,
literal|"(INTERVAL '1' HOUR + TIME '23:59:59')"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval '1' hour * 8"
argument_list|,
literal|"(INTERVAL '1' HOUR * 8)"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"1 * interval '1' hour"
argument_list|,
literal|"(1 * INTERVAL '1' HOUR)"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval '1' hour / 8"
argument_list|,
literal|"(INTERVAL '1' HOUR / 8)"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testIntervalCompare
parameter_list|()
block|{
name|checkExp
argument_list|(
literal|"interval '1' hour = interval '1' second"
argument_list|,
literal|"(INTERVAL '1' HOUR = INTERVAL '1' SECOND)"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval '1' hour<> interval '1' second"
argument_list|,
literal|"(INTERVAL '1' HOUR<> INTERVAL '1' SECOND)"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval '1' hour< interval '1' second"
argument_list|,
literal|"(INTERVAL '1' HOUR< INTERVAL '1' SECOND)"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval '1' hour<= interval '1' second"
argument_list|,
literal|"(INTERVAL '1' HOUR<= INTERVAL '1' SECOND)"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval '1' hour> interval '1' second"
argument_list|,
literal|"(INTERVAL '1' HOUR> INTERVAL '1' SECOND)"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"interval '1' hour>= interval '1' second"
argument_list|,
literal|"(INTERVAL '1' HOUR>= INTERVAL '1' SECOND)"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testCastToInterval
parameter_list|()
block|{
name|checkExp
argument_list|(
literal|"cast(x as interval year)"
argument_list|,
literal|"CAST(`X` AS INTERVAL YEAR)"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"cast(x as interval month)"
argument_list|,
literal|"CAST(`X` AS INTERVAL MONTH)"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"cast(x as interval year to month)"
argument_list|,
literal|"CAST(`X` AS INTERVAL YEAR TO MONTH)"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"cast(x as interval day)"
argument_list|,
literal|"CAST(`X` AS INTERVAL DAY)"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"cast(x as interval hour)"
argument_list|,
literal|"CAST(`X` AS INTERVAL HOUR)"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"cast(x as interval minute)"
argument_list|,
literal|"CAST(`X` AS INTERVAL MINUTE)"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"cast(x as interval second)"
argument_list|,
literal|"CAST(`X` AS INTERVAL SECOND)"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"cast(x as interval day to hour)"
argument_list|,
literal|"CAST(`X` AS INTERVAL DAY TO HOUR)"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"cast(x as interval day to minute)"
argument_list|,
literal|"CAST(`X` AS INTERVAL DAY TO MINUTE)"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"cast(x as interval day to second)"
argument_list|,
literal|"CAST(`X` AS INTERVAL DAY TO SECOND)"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"cast(x as interval hour to minute)"
argument_list|,
literal|"CAST(`X` AS INTERVAL HOUR TO MINUTE)"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"cast(x as interval hour to second)"
argument_list|,
literal|"CAST(`X` AS INTERVAL HOUR TO SECOND)"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"cast(x as interval minute to second)"
argument_list|,
literal|"CAST(`X` AS INTERVAL MINUTE TO SECOND)"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testUnnest
parameter_list|()
block|{
name|check
argument_list|(
literal|"select*from unnest(x)"
argument_list|,
literal|"SELECT *\n"
operator|+
literal|"FROM (UNNEST(`X`))"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"select*from unnest(x) AS T"
argument_list|,
literal|"SELECT *\n"
operator|+
literal|"FROM (UNNEST(`X`)) AS `T`"
argument_list|)
expr_stmt|;
comment|// UNNEST cannot be first word in query
name|checkFails
argument_list|(
literal|"^unnest^(x)"
argument_list|,
literal|"(?s)Encountered \"unnest\" at.*"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testParensInFrom
parameter_list|()
block|{
comment|// UNNEST may not occur within parentheses.
name|checkFails
argument_list|(
literal|"select *from (^unnest^(x))"
argument_list|,
literal|"(?s)Encountered \"unnest\" at .*"
argument_list|)
expr_stmt|;
comment|//<table-name> may not occur within parentheses.
name|checkFails
argument_list|(
literal|"select * from (^emp^)"
argument_list|,
literal|"(?s)Non-query expression encountered in illegal context.*"
argument_list|)
expr_stmt|;
comment|//<table-name> may not occur within parentheses.
name|checkFails
argument_list|(
literal|"select * from (^emp^ as x)"
argument_list|,
literal|"(?s)Non-query expression encountered in illegal context.*"
argument_list|)
expr_stmt|;
comment|//<table-name> may not occur within parentheses.
name|checkFails
argument_list|(
literal|"select * from (^emp^) as x"
argument_list|,
literal|"(?s)Non-query expression encountered in illegal context.*"
argument_list|)
expr_stmt|;
comment|// Parentheses around JOINs are OK, and sometimes necessary.
if|if
condition|(
literal|false
condition|)
block|{
comment|// todo:
name|check
argument_list|(
literal|"select * from (emp join dept using (deptno))"
argument_list|,
literal|"xx"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"select * from (emp join dept using (deptno)) join foo using (x)"
argument_list|,
literal|"xx"
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|testProcedureCall
parameter_list|()
block|{
name|check
argument_list|(
literal|"call blubber(5)"
argument_list|,
literal|"(CALL `BLUBBER`(5))"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"call \"blubber\"(5)"
argument_list|,
literal|"(CALL `blubber`(5))"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"call whale.blubber(5)"
argument_list|,
literal|"(CALL `WHALE`.`BLUBBER`(5))"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testNewSpecification
parameter_list|()
block|{
name|checkExp
argument_list|(
literal|"new udt()"
argument_list|,
literal|"(NEW `UDT`())"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"new my.udt(1, 'hey')"
argument_list|,
literal|"(NEW `MY`.`UDT`(1, 'hey'))"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"new udt() is not null"
argument_list|,
literal|"((NEW `UDT`()) IS NOT NULL)"
argument_list|)
expr_stmt|;
name|checkExp
argument_list|(
literal|"1 + new udt()"
argument_list|,
literal|"(1 + (NEW `UDT`()))"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testMultisetCast
parameter_list|()
block|{
name|checkExp
argument_list|(
literal|"cast(multiset[1] as double multiset)"
argument_list|,
literal|"CAST((MULTISET [1]) AS DOUBLE MULTISET)"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testAddCarets
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|"values (^foo^)"
argument_list|,
name|SqlParserUtil
operator|.
name|addCarets
argument_list|(
literal|"values (foo)"
argument_list|,
literal|1
argument_list|,
literal|9
argument_list|,
literal|1
argument_list|,
literal|12
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"abc^def"
argument_list|,
name|SqlParserUtil
operator|.
name|addCarets
argument_list|(
literal|"abcdef"
argument_list|,
literal|1
argument_list|,
literal|4
argument_list|,
literal|1
argument_list|,
literal|4
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"abcdef^"
argument_list|,
name|SqlParserUtil
operator|.
name|addCarets
argument_list|(
literal|"abcdef"
argument_list|,
literal|1
argument_list|,
literal|7
argument_list|,
literal|1
argument_list|,
literal|7
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testMetadata
parameter_list|()
block|{
name|SqlAbstractParserImpl
operator|.
name|Metadata
name|metadata
init|=
name|getParserImpl
argument_list|()
operator|.
name|getMetadata
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|metadata
operator|.
name|isReservedFunctionName
argument_list|(
literal|"ABS"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|metadata
operator|.
name|isReservedFunctionName
argument_list|(
literal|"FOO"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|metadata
operator|.
name|isContextVariableName
argument_list|(
literal|"CURRENT_USER"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|metadata
operator|.
name|isContextVariableName
argument_list|(
literal|"CURRENT_CATALOG"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|metadata
operator|.
name|isContextVariableName
argument_list|(
literal|"CURRENT_SCHEMA"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|metadata
operator|.
name|isContextVariableName
argument_list|(
literal|"ABS"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|metadata
operator|.
name|isContextVariableName
argument_list|(
literal|"FOO"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|metadata
operator|.
name|isNonReservedKeyword
argument_list|(
literal|"A"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|metadata
operator|.
name|isNonReservedKeyword
argument_list|(
literal|"KEY"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|metadata
operator|.
name|isNonReservedKeyword
argument_list|(
literal|"SELECT"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|metadata
operator|.
name|isNonReservedKeyword
argument_list|(
literal|"FOO"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|metadata
operator|.
name|isNonReservedKeyword
argument_list|(
literal|"ABS"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|metadata
operator|.
name|isKeyword
argument_list|(
literal|"ABS"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|metadata
operator|.
name|isKeyword
argument_list|(
literal|"CURRENT_USER"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|metadata
operator|.
name|isKeyword
argument_list|(
literal|"CURRENT_CATALOG"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|metadata
operator|.
name|isKeyword
argument_list|(
literal|"CURRENT_SCHEMA"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|metadata
operator|.
name|isKeyword
argument_list|(
literal|"KEY"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|metadata
operator|.
name|isKeyword
argument_list|(
literal|"SELECT"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|metadata
operator|.
name|isKeyword
argument_list|(
literal|"HAVING"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|metadata
operator|.
name|isKeyword
argument_list|(
literal|"A"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|metadata
operator|.
name|isKeyword
argument_list|(
literal|"BAR"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|metadata
operator|.
name|isReservedWord
argument_list|(
literal|"SELECT"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|metadata
operator|.
name|isReservedWord
argument_list|(
literal|"CURRENT_CATALOG"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|metadata
operator|.
name|isReservedWord
argument_list|(
literal|"CURRENT_SCHEMA"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|metadata
operator|.
name|isReservedWord
argument_list|(
literal|"KEY"
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|jdbcKeywords
init|=
name|metadata
operator|.
name|getJdbcKeywords
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|jdbcKeywords
operator|.
name|indexOf
argument_list|(
literal|",COLLECT,"
argument_list|)
operator|>=
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|jdbcKeywords
operator|.
name|indexOf
argument_list|(
literal|",SELECT,"
argument_list|)
operator|<
literal|0
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testTabStop
parameter_list|()
block|{
name|check
argument_list|(
literal|"SELECT *\n\tFROM mytable"
argument_list|,
name|TestUtil
operator|.
name|fold
argument_list|(
literal|"SELECT *\n"
operator|+
literal|"FROM `MYTABLE`"
argument_list|)
argument_list|)
expr_stmt|;
comment|// make sure that the tab stops do not affect the placement of the
comment|// error tokens
name|checkFails
argument_list|(
literal|"SELECT *\tFROM mytable\t\tWHERE x ^=^ = y AND b = 1"
argument_list|,
literal|"(?s).*Encountered \"= =\" at line 1, column 32\\..*"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testLongIdentifiers
parameter_list|()
block|{
name|StringBuilder
name|ident128Builder
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|128
condition|;
name|i
operator|++
control|)
block|{
name|ident128Builder
operator|.
name|append
argument_list|(
operator|(
name|char
operator|)
operator|(
literal|'a'
operator|+
operator|(
name|i
operator|%
literal|26
operator|)
operator|)
argument_list|)
expr_stmt|;
block|}
name|String
name|ident128
init|=
name|ident128Builder
operator|.
name|toString
argument_list|()
decl_stmt|;
name|String
name|ident128Upper
init|=
name|ident128
operator|.
name|toUpperCase
argument_list|(
name|Locale
operator|.
name|US
argument_list|)
decl_stmt|;
name|String
name|ident129
init|=
literal|"x"
operator|+
name|ident128
decl_stmt|;
name|String
name|ident129Upper
init|=
name|ident129
operator|.
name|toUpperCase
argument_list|(
name|Locale
operator|.
name|US
argument_list|)
decl_stmt|;
name|check
argument_list|(
literal|"select * from "
operator|+
name|ident128
argument_list|,
literal|"SELECT *"
operator|+
name|NL
operator|+
literal|"FROM `"
operator|+
name|ident128Upper
operator|+
literal|"`"
argument_list|)
expr_stmt|;
name|checkFails
argument_list|(
literal|"select * from ^"
operator|+
name|ident129
operator|+
literal|"^"
argument_list|,
literal|"Length of identifier '"
operator|+
name|ident129Upper
operator|+
literal|"' must be less than or equal to 128 characters"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"select "
operator|+
name|ident128
operator|+
literal|" from mytable"
argument_list|,
literal|"SELECT `"
operator|+
name|ident128Upper
operator|+
literal|"`"
operator|+
name|NL
operator|+
literal|"FROM `MYTABLE`"
argument_list|)
expr_stmt|;
name|checkFails
argument_list|(
literal|"select ^"
operator|+
name|ident129
operator|+
literal|"^ from mytable"
argument_list|,
literal|"Length of identifier '"
operator|+
name|ident129Upper
operator|+
literal|"' must be less than or equal to 128 characters"
argument_list|)
expr_stmt|;
block|}
comment|/**      * Tests that you can't quote the names of builtin functions.      *      * @see org.eigenbase.test.SqlValidatorTest#testQuotedFunction()      */
specifier|public
name|void
name|testQuotedFunction
parameter_list|()
block|{
name|checkExpFails
argument_list|(
literal|"\"CAST\"(1 ^as^ double)"
argument_list|,
literal|"(?s).*Encountered \"as\" at .*"
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"\"POSITION\"('b' ^in^ 'alphabet')"
argument_list|,
literal|"(?s).*Encountered \"in \\\\'alphabet\\\\'\" at .*"
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"\"OVERLAY\"('a' ^PLAcing^ 'b' from 1)"
argument_list|,
literal|"(?s).*Encountered \"PLAcing\" at.*"
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"\"SUBSTRING\"('a' ^from^ 1)"
argument_list|,
literal|"(?s).*Encountered \"from\" at .*"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testUnicodeLiteral
parameter_list|()
block|{
comment|// Note that here we are constructing a SQL statement which directly
comment|// contains Unicode characters (not SQL Unicode escape sequences).  The
comment|// escaping here is Java-only, so by the time it gets to the SQL
comment|// parser, the literal already contains Unicode characters.
name|String
name|in1
init|=
literal|"values _UTF16'"
operator|+
name|ConversionUtil
operator|.
name|TEST_UNICODE_STRING
operator|+
literal|"'"
decl_stmt|;
name|String
name|out1
init|=
literal|"(VALUES (ROW(_UTF16'"
operator|+
name|ConversionUtil
operator|.
name|TEST_UNICODE_STRING
operator|+
literal|"')))"
decl_stmt|;
name|check
argument_list|(
name|in1
argument_list|,
name|out1
argument_list|)
expr_stmt|;
comment|// Without the U& prefix, escapes are left unprocessed
name|String
name|in2
init|=
literal|"values '"
operator|+
name|ConversionUtil
operator|.
name|TEST_UNICODE_SQL_ESCAPED_LITERAL
operator|+
literal|"'"
decl_stmt|;
name|String
name|out2
init|=
literal|"(VALUES (ROW('"
operator|+
name|ConversionUtil
operator|.
name|TEST_UNICODE_SQL_ESCAPED_LITERAL
operator|+
literal|"')))"
decl_stmt|;
name|check
argument_list|(
name|in2
argument_list|,
name|out2
argument_list|)
expr_stmt|;
comment|// Likewise, even with the U& prefix, if some other escape
comment|// character is specified, then the backslash-escape
comment|// sequences are not interpreted
name|String
name|in3
init|=
literal|"values U&'"
operator|+
name|ConversionUtil
operator|.
name|TEST_UNICODE_SQL_ESCAPED_LITERAL
operator|+
literal|"' UESCAPE '!'"
decl_stmt|;
name|String
name|out3
init|=
literal|"(VALUES (ROW(_UTF16'"
operator|+
name|ConversionUtil
operator|.
name|TEST_UNICODE_SQL_ESCAPED_LITERAL
operator|+
literal|"')))"
decl_stmt|;
name|check
argument_list|(
name|in3
argument_list|,
name|out3
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testUnicodeEscapedLiteral
parameter_list|()
block|{
comment|// Note that here we are constructing a SQL statement which
comment|// contains SQL-escaped Unicode characters to be handled
comment|// by the SQL parser.
name|String
name|in
init|=
literal|"values U&'"
operator|+
name|ConversionUtil
operator|.
name|TEST_UNICODE_SQL_ESCAPED_LITERAL
operator|+
literal|"'"
decl_stmt|;
name|String
name|out
init|=
literal|"(VALUES (ROW(_UTF16'"
operator|+
name|ConversionUtil
operator|.
name|TEST_UNICODE_STRING
operator|+
literal|"')))"
decl_stmt|;
name|check
argument_list|(
name|in
argument_list|,
name|out
argument_list|)
expr_stmt|;
comment|// Verify that we can override with an explicit escape character
name|check
argument_list|(
name|in
operator|.
name|replaceAll
argument_list|(
literal|"\\\\"
argument_list|,
literal|"!"
argument_list|)
operator|+
literal|"UESCAPE '!'"
argument_list|,
name|out
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testIllegalUnicodeEscape
parameter_list|()
block|{
name|checkExpFails
argument_list|(
literal|"U&'abc' UESCAPE '!!'"
argument_list|,
literal|".*must be exactly one character.*"
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"U&'abc' UESCAPE ''"
argument_list|,
literal|".*must be exactly one character.*"
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"U&'abc' UESCAPE '0'"
argument_list|,
literal|".*hex digit.*"
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"U&'abc' UESCAPE 'a'"
argument_list|,
literal|".*hex digit.*"
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"U&'abc' UESCAPE 'F'"
argument_list|,
literal|".*hex digit.*"
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"U&'abc' UESCAPE ' '"
argument_list|,
literal|".*whitespace.*"
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"U&'abc' UESCAPE '+'"
argument_list|,
literal|".*plus sign.*"
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"U&'abc' UESCAPE '\"'"
argument_list|,
literal|".*double quote.*"
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"'abc' UESCAPE ^'!'^"
argument_list|,
literal|".*without Unicode literal introducer.*"
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"^U&'\\0A'^"
argument_list|,
literal|".*is not exactly four hex digits.*"
argument_list|)
expr_stmt|;
name|checkExpFails
argument_list|(
literal|"^U&'\\wxyz'^"
argument_list|,
literal|".*is not exactly four hex digits.*"
argument_list|)
expr_stmt|;
block|}
comment|//~ Inner Interfaces -------------------------------------------------------
comment|/**      * Callback to control how test actions are performed.      */
specifier|protected
interface|interface
name|Tester
block|{
name|void
name|check
parameter_list|(
name|String
name|sql
parameter_list|,
name|String
name|expected
parameter_list|)
function_decl|;
name|void
name|checkExp
parameter_list|(
name|String
name|sql
parameter_list|,
name|String
name|expected
parameter_list|)
function_decl|;
name|void
name|checkFails
parameter_list|(
name|String
name|sql
parameter_list|,
name|String
name|expectedMsgPattern
parameter_list|)
function_decl|;
name|void
name|checkExpFails
parameter_list|(
name|String
name|sql
parameter_list|,
name|String
name|expectedMsgPattern
parameter_list|)
function_decl|;
block|}
comment|//~ Inner Classes ----------------------------------------------------------
comment|/**      * Default implementation of {@link Tester}.      */
specifier|protected
class|class
name|TesterImpl
implements|implements
name|Tester
block|{
specifier|public
name|void
name|check
parameter_list|(
name|String
name|sql
parameter_list|,
name|String
name|expected
parameter_list|)
block|{
specifier|final
name|SqlNode
name|sqlNode
init|=
name|parseStmtAndHandleEx
argument_list|(
name|sql
argument_list|)
decl_stmt|;
comment|// no dialect, always parenthesize
specifier|final
name|String
name|actual
init|=
name|sqlNode
operator|.
name|toSqlString
argument_list|(
literal|null
argument_list|,
literal|true
argument_list|)
operator|.
name|getSql
argument_list|()
decl_stmt|;
name|TestUtil
operator|.
name|assertEqualsVerbose
argument_list|(
name|expected
argument_list|,
name|actual
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|SqlNode
name|parseStmtAndHandleEx
parameter_list|(
name|String
name|sql
parameter_list|)
block|{
specifier|final
name|SqlNode
name|sqlNode
decl_stmt|;
try|try
block|{
name|sqlNode
operator|=
name|parseStmt
argument_list|(
name|sql
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SqlParseException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|String
name|message
init|=
literal|"Received error while parsing SQL '"
operator|+
name|sql
operator|+
literal|"'; error is:"
operator|+
name|NL
operator|+
name|e
operator|.
name|toString
argument_list|()
decl_stmt|;
throw|throw
operator|new
name|AssertionFailedError
argument_list|(
name|message
argument_list|)
throw|;
block|}
return|return
name|sqlNode
return|;
block|}
specifier|public
name|void
name|checkExp
parameter_list|(
name|String
name|sql
parameter_list|,
name|String
name|expected
parameter_list|)
block|{
specifier|final
name|SqlNode
name|sqlNode
init|=
name|parseExpressionAndHandleEx
argument_list|(
name|sql
argument_list|)
decl_stmt|;
specifier|final
name|String
name|actual
init|=
name|sqlNode
operator|.
name|toSqlString
argument_list|(
literal|null
argument_list|,
literal|true
argument_list|)
operator|.
name|getSql
argument_list|()
decl_stmt|;
name|TestUtil
operator|.
name|assertEqualsVerbose
argument_list|(
name|expected
argument_list|,
name|actual
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|SqlNode
name|parseExpressionAndHandleEx
parameter_list|(
name|String
name|sql
parameter_list|)
block|{
specifier|final
name|SqlNode
name|sqlNode
decl_stmt|;
try|try
block|{
name|sqlNode
operator|=
name|parseExpression
argument_list|(
name|sql
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SqlParseException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|String
name|message
init|=
literal|"Received error while parsing SQL '"
operator|+
name|sql
operator|+
literal|"'; error is:"
operator|+
name|NL
operator|+
name|e
operator|.
name|toString
argument_list|()
decl_stmt|;
throw|throw
operator|new
name|AssertionFailedError
argument_list|(
name|message
argument_list|)
throw|;
block|}
return|return
name|sqlNode
return|;
block|}
specifier|public
name|void
name|checkFails
parameter_list|(
name|String
name|sql
parameter_list|,
name|String
name|expectedMsgPattern
parameter_list|)
block|{
name|SqlParserUtil
operator|.
name|StringAndPos
name|sap
init|=
name|SqlParserUtil
operator|.
name|findPos
argument_list|(
name|sql
argument_list|)
decl_stmt|;
name|Throwable
name|thrown
init|=
literal|null
decl_stmt|;
try|try
block|{
specifier|final
name|SqlNode
name|sqlNode
init|=
name|parseStmt
argument_list|(
name|sap
operator|.
name|sql
argument_list|)
decl_stmt|;
name|Util
operator|.
name|discard
argument_list|(
name|sqlNode
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|ex
parameter_list|)
block|{
name|thrown
operator|=
name|ex
expr_stmt|;
block|}
name|SqlValidatorTestCase
operator|.
name|checkEx
argument_list|(
name|thrown
argument_list|,
name|expectedMsgPattern
argument_list|,
name|sap
argument_list|)
expr_stmt|;
block|}
comment|/**          * Tests that an expression throws an exception which matches the given          * pattern.          */
specifier|public
name|void
name|checkExpFails
parameter_list|(
name|String
name|sql
parameter_list|,
name|String
name|expectedMsgPattern
parameter_list|)
block|{
name|SqlParserUtil
operator|.
name|StringAndPos
name|sap
init|=
name|SqlParserUtil
operator|.
name|findPos
argument_list|(
name|sql
argument_list|)
decl_stmt|;
name|Throwable
name|thrown
init|=
literal|null
decl_stmt|;
try|try
block|{
specifier|final
name|SqlNode
name|sqlNode
init|=
name|parseExpression
argument_list|(
name|sap
operator|.
name|sql
argument_list|)
decl_stmt|;
name|Util
operator|.
name|discard
argument_list|(
name|sqlNode
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|ex
parameter_list|)
block|{
name|thrown
operator|=
name|ex
expr_stmt|;
block|}
name|SqlValidatorTestCase
operator|.
name|checkEx
argument_list|(
name|thrown
argument_list|,
name|expectedMsgPattern
argument_list|,
name|sap
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Implementation of {@link Tester} which makes sure that the results of      * unparsing a query are consistent with the original query.      */
specifier|public
class|class
name|UnparsingTesterImpl
extends|extends
name|TesterImpl
block|{
specifier|public
name|void
name|check
parameter_list|(
name|String
name|sql
parameter_list|,
name|String
name|expected
parameter_list|)
block|{
name|SqlNode
name|sqlNode
init|=
name|parseStmtAndHandleEx
argument_list|(
name|sql
argument_list|)
decl_stmt|;
comment|// Unparse with no dialect, always parenthesize.
specifier|final
name|String
name|actual
init|=
name|sqlNode
operator|.
name|toSqlString
argument_list|(
literal|null
argument_list|,
literal|true
argument_list|)
operator|.
name|getSql
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|actual
argument_list|)
expr_stmt|;
comment|// Unparse again in Eigenbase dialect (which we can parse), and
comment|// minimal parentheses.
specifier|final
name|String
name|sql1
init|=
name|sqlNode
operator|.
name|toSqlString
argument_list|(
name|SqlDialect
operator|.
name|EIGENBASE
argument_list|,
literal|false
argument_list|)
operator|.
name|getSql
argument_list|()
decl_stmt|;
comment|// Parse and unparse again.
name|SqlNode
name|sqlNode2
init|=
name|parseStmtAndHandleEx
argument_list|(
name|sql1
argument_list|)
decl_stmt|;
specifier|final
name|String
name|sql2
init|=
name|sqlNode2
operator|.
name|toSqlString
argument_list|(
name|SqlDialect
operator|.
name|EIGENBASE
argument_list|,
literal|false
argument_list|)
operator|.
name|getSql
argument_list|()
decl_stmt|;
comment|// Should be the same as we started with.
name|assertEquals
argument_list|(
name|sql1
argument_list|,
name|sql2
argument_list|)
expr_stmt|;
comment|// Now unparse again in the null dialect.
comment|// If the unparser is not including sufficient parens to override
comment|// precedence, the problem will show up here.
specifier|final
name|String
name|actual2
init|=
name|sqlNode2
operator|.
name|toSqlString
argument_list|(
literal|null
argument_list|,
literal|true
argument_list|)
operator|.
name|getSql
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|actual2
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|checkExp
parameter_list|(
name|String
name|sql
parameter_list|,
name|String
name|expected
parameter_list|)
block|{
name|SqlNode
name|sqlNode
init|=
name|parseExpressionAndHandleEx
argument_list|(
name|sql
argument_list|)
decl_stmt|;
comment|// Unparse with no dialect, always parenthesize.
specifier|final
name|String
name|actual
init|=
name|sqlNode
operator|.
name|toSqlString
argument_list|(
literal|null
argument_list|,
literal|true
argument_list|)
operator|.
name|getSql
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|actual
argument_list|)
expr_stmt|;
comment|// Unparse again in Eigenbase dialect (which we can parse), and
comment|// minimal parentheses.
specifier|final
name|String
name|sql1
init|=
name|sqlNode
operator|.
name|toSqlString
argument_list|(
name|SqlDialect
operator|.
name|EIGENBASE
argument_list|,
literal|false
argument_list|)
operator|.
name|getSql
argument_list|()
decl_stmt|;
comment|// Parse and unparse again.
name|SqlNode
name|sqlNode2
init|=
name|parseExpressionAndHandleEx
argument_list|(
name|sql1
argument_list|)
decl_stmt|;
specifier|final
name|String
name|sql2
init|=
name|sqlNode2
operator|.
name|toSqlString
argument_list|(
name|SqlDialect
operator|.
name|EIGENBASE
argument_list|,
literal|false
argument_list|)
operator|.
name|getSql
argument_list|()
decl_stmt|;
comment|// Should be the same as we started with.
name|assertEquals
argument_list|(
name|sql1
argument_list|,
name|sql2
argument_list|)
expr_stmt|;
comment|// Now unparse again in the null dialect.
comment|// If the unparser is not including sufficient parens to override
comment|// precedence, the problem will show up here.
specifier|final
name|String
name|actual2
init|=
name|sqlNode2
operator|.
name|toSqlString
argument_list|(
literal|null
argument_list|,
literal|true
argument_list|)
operator|.
name|getSql
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|actual2
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|checkFails
parameter_list|(
name|String
name|sql
parameter_list|,
name|String
name|expectedMsgPattern
parameter_list|)
block|{
comment|// Do nothing. We're not interested in unparsing invalid SQL
block|}
specifier|public
name|void
name|checkExpFails
parameter_list|(
name|String
name|sql
parameter_list|,
name|String
name|expectedMsgPattern
parameter_list|)
block|{
comment|// Do nothing. We're not interested in unparsing invalid SQL
block|}
block|}
block|}
end_class

begin_comment
comment|// End SqlParserTest.java
end_comment

end_unit

