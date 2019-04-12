begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to you under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|adapter
operator|.
name|tpcds
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|config
operator|.
name|CalciteSystemProperty
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|plan
operator|.
name|RelOptUtil
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|plan
operator|.
name|RelTraitDef
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|rel
operator|.
name|RelNode
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|rel
operator|.
name|core
operator|.
name|JoinRelType
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|runtime
operator|.
name|Hook
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|schema
operator|.
name|SchemaPlus
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|sql
operator|.
name|fun
operator|.
name|SqlStdOperatorTable
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|sql
operator|.
name|parser
operator|.
name|SqlParser
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|test
operator|.
name|CalciteAssert
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|tools
operator|.
name|Frameworks
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|tools
operator|.
name|Program
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|tools
operator|.
name|Programs
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|tools
operator|.
name|RelBuilder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|util
operator|.
name|Bug
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|util
operator|.
name|Holder
import|;
end_import

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|tpcds
operator|.
name|query
operator|.
name|Query
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Ignore
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Consumer
import|;
end_import

begin_comment
comment|/** Unit test for {@link org.apache.calcite.adapter.tpcds.TpcdsSchema}.  *  *<p>Only runs if {@link org.apache.calcite.config.CalciteSystemProperty#TEST_SLOW} is set.</p>  */
end_comment

begin_class
specifier|public
class|class
name|TpcdsTest
block|{
specifier|private
specifier|static
name|Consumer
argument_list|<
name|Holder
argument_list|<
name|Program
argument_list|>
argument_list|>
name|handler
parameter_list|(
specifier|final
name|boolean
name|bushy
parameter_list|,
specifier|final
name|int
name|minJoinCount
parameter_list|)
block|{
return|return
name|holder
lambda|->
name|holder
operator|.
name|set
argument_list|(
name|Programs
operator|.
name|sequence
argument_list|(
name|Programs
operator|.
name|heuristicJoinOrder
argument_list|(
name|Programs
operator|.
name|RULE_SET
argument_list|,
name|bushy
argument_list|,
name|minJoinCount
argument_list|)
argument_list|,
name|Programs
operator|.
name|CALC_PROGRAM
argument_list|)
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|String
name|schema
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|scaleFactor
parameter_list|)
block|{
return|return
literal|"     {\n"
operator|+
literal|"       type: 'custom',\n"
operator|+
literal|"       name: '"
operator|+
name|name
operator|+
literal|"',\n"
operator|+
literal|"       factory: 'org.apache.calcite.adapter.tpcds.TpcdsSchemaFactory',\n"
operator|+
literal|"       operand: {\n"
operator|+
literal|"         columnPrefix: true,\n"
operator|+
literal|"         scale: "
operator|+
name|scaleFactor
operator|+
literal|"\n"
operator|+
literal|"       }\n"
operator|+
literal|"     }"
return|;
block|}
specifier|public
specifier|static
specifier|final
name|String
name|TPCDS_MODEL
init|=
literal|"{\n"
operator|+
literal|"  version: '1.0',\n"
operator|+
literal|"  defaultSchema: 'TPCDS',\n"
operator|+
literal|"   schemas: [\n"
operator|+
name|schema
argument_list|(
literal|"TPCDS"
argument_list|,
literal|"1.0"
argument_list|)
operator|+
literal|",\n"
operator|+
name|schema
argument_list|(
literal|"TPCDS_01"
argument_list|,
literal|"0.01"
argument_list|)
operator|+
literal|",\n"
operator|+
name|schema
argument_list|(
literal|"TPCDS_5"
argument_list|,
literal|"5.0"
argument_list|)
operator|+
literal|"\n"
operator|+
literal|"   ]\n"
operator|+
literal|"}"
decl_stmt|;
specifier|private
name|CalciteAssert
operator|.
name|AssertThat
name|with
parameter_list|()
block|{
return|return
name|CalciteAssert
operator|.
name|model
argument_list|(
name|TPCDS_MODEL
argument_list|)
operator|.
name|enable
argument_list|(
name|CalciteSystemProperty
operator|.
name|TEST_SLOW
operator|.
name|value
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCallCenter
parameter_list|()
block|{
specifier|final
name|String
index|[]
name|strings
init|=
block|{
literal|"CC_CALL_CENTER_SK=1; CC_CALL_CENTER_ID=AAAAAAAABAAAAAAA; CC_REC_START_DATE=1998-01-01;"
operator|+
literal|" CC_REC_END_DATE=null; CC_CLOSED_DATE_SK=null; CC_OPEN_DATE_SK=2450952;"
operator|+
literal|" CC_NAME=NY Metro; CC_CLASS=large; CC_EMPLOYEES=2; CC_SQ_FT=1138;"
operator|+
literal|" CC_HOURS=8AM-4PM             ; CC_MANAGER=Bob Belcher; CC_MKT_ID=6;"
operator|+
literal|" CC_MKT_CLASS=More than other authori                           ;"
operator|+
literal|" CC_MKT_DESC=Shared others could not count fully dollars. New members ca;"
operator|+
literal|" CC_MARKET_MANAGER=Julius Tran; CC_DIVISION=3; CC_DIVISION_NAME=pri;"
operator|+
literal|" CC_COMPANY=6; CC_COMPANY_NAME=cally                                             ;"
operator|+
literal|" CC_STREET_NUMBER=730       ; CC_STREET_NAME=Ash Hill;"
operator|+
literal|" CC_STREET_TYPE=Boulevard      ; CC_SUITE_NUMBER=Suite 0   ; CC_CITY=Midway;"
operator|+
literal|" CC_COUNTY=Williamson County; CC_STATE=TN; CC_ZIP=31904     ;"
operator|+
literal|" CC_COUNTRY=United States; CC_GMT_OFFSET=-5; CC_TAX_PERCENTAGE=0.11"
block|,
literal|"CC_CALL_CENTER_SK=2; CC_CALL_CENTER_ID=AAAAAAAACAAAAAAA; CC_REC_START_DATE=1998-01-01;"
operator|+
literal|" CC_REC_END_DATE=2000-12-31; CC_CLOSED_DATE_SK=null; CC_OPEN_DATE_SK=2450806;"
operator|+
literal|" CC_NAME=Mid Atlantic; CC_CLASS=medium; CC_EMPLOYEES=6; CC_SQ_FT=2268;"
operator|+
literal|" CC_HOURS=8AM-8AM             ; CC_MANAGER=Felipe Perkins; CC_MKT_ID=2;"
operator|+
literal|" CC_MKT_CLASS=A bit narrow forms matter animals. Consist        ;"
operator|+
literal|" CC_MKT_DESC=Largely blank years put substantially deaf, new others. Question;"
operator|+
literal|" CC_MARKET_MANAGER=Julius Durham; CC_DIVISION=5; CC_DIVISION_NAME=anti;"
operator|+
literal|" CC_COMPANY=1; CC_COMPANY_NAME=ought                                             ;"
operator|+
literal|" CC_STREET_NUMBER=984       ; CC_STREET_NAME=Center Hill;"
operator|+
literal|" CC_STREET_TYPE=Way            ; CC_SUITE_NUMBER=Suite 70  ; CC_CITY=Midway;"
operator|+
literal|" CC_COUNTY=Williamson County; CC_STATE=TN; CC_ZIP=31904     ;"
operator|+
literal|" CC_COUNTRY=United States; CC_GMT_OFFSET=-5; CC_TAX_PERCENTAGE=0.12"
block|,
literal|"CC_CALL_CENTER_SK=3; CC_CALL_CENTER_ID=AAAAAAAACAAAAAAA; CC_REC_START_DATE=2001-01-01;"
operator|+
literal|" CC_REC_END_DATE=null; CC_CLOSED_DATE_SK=null; CC_OPEN_DATE_SK=2450806;"
operator|+
literal|" CC_NAME=Mid Atlantic; CC_CLASS=medium; CC_EMPLOYEES=6; CC_SQ_FT=4134;"
operator|+
literal|" CC_HOURS=8AM-4PM             ; CC_MANAGER=Mark Hightower; CC_MKT_ID=2;"
operator|+
literal|" CC_MKT_CLASS=Wrong troops shall work sometimes in a opti       ;"
operator|+
literal|" CC_MKT_DESC=Largely blank years put substantially deaf, new others. Question;"
operator|+
literal|" CC_MARKET_MANAGER=Julius Durham; CC_DIVISION=1; CC_DIVISION_NAME=ought;"
operator|+
literal|" CC_COMPANY=2; CC_COMPANY_NAME=able                                              ;"
operator|+
literal|" CC_STREET_NUMBER=984       ; CC_STREET_NAME=Center Hill;"
operator|+
literal|" CC_STREET_TYPE=Way            ; CC_SUITE_NUMBER=Suite 70  ; CC_CITY=Midway;"
operator|+
literal|" CC_COUNTY=Williamson County; CC_STATE=TN; CC_ZIP=31904     ;"
operator|+
literal|" CC_COUNTRY=United States; CC_GMT_OFFSET=-5; CC_TAX_PERCENTAGE=0.01"
block|,
literal|"CC_CALL_CENTER_SK=4; CC_CALL_CENTER_ID=AAAAAAAAEAAAAAAA; CC_REC_START_DATE=1998-01-01;"
operator|+
literal|" CC_REC_END_DATE=2000-01-01; CC_CLOSED_DATE_SK=null; CC_OPEN_DATE_SK=2451063;"
operator|+
literal|" CC_NAME=North Midwest; CC_CLASS=medium; CC_EMPLOYEES=1; CC_SQ_FT=649;"
operator|+
literal|" CC_HOURS=8AM-4PM             ; CC_MANAGER=Larry Mccray; CC_MKT_ID=2;"
operator|+
literal|" CC_MKT_CLASS=Dealers make most historical, direct students     ;"
operator|+
literal|" CC_MKT_DESC=Rich groups catch longer other fears; future,;"
operator|+
literal|" CC_MARKET_MANAGER=Matthew Clifton; CC_DIVISION=4; CC_DIVISION_NAME=ese;"
operator|+
literal|" CC_COMPANY=3; CC_COMPANY_NAME=pri                                               ;"
operator|+
literal|" CC_STREET_NUMBER=463       ; CC_STREET_NAME=Pine Ridge;"
operator|+
literal|" CC_STREET_TYPE=RD             ; CC_SUITE_NUMBER=Suite U   ; CC_CITY=Midway;"
operator|+
literal|" CC_COUNTY=Williamson County; CC_STATE=TN; CC_ZIP=31904     ;"
operator|+
literal|" CC_COUNTRY=United States; CC_GMT_OFFSET=-5; CC_TAX_PERCENTAGE=0.05"
block|,
literal|"CC_CALL_CENTER_SK=5; CC_CALL_CENTER_ID=AAAAAAAAEAAAAAAA; CC_REC_START_DATE=2000-01-02;"
operator|+
literal|" CC_REC_END_DATE=2001-12-31; CC_CLOSED_DATE_SK=null; CC_OPEN_DATE_SK=2451063;"
operator|+
literal|" CC_NAME=North Midwest; CC_CLASS=small; CC_EMPLOYEES=3; CC_SQ_FT=795;"
operator|+
literal|" CC_HOURS=8AM-8AM             ; CC_MANAGER=Larry Mccray; CC_MKT_ID=2;"
operator|+
literal|" CC_MKT_CLASS=Dealers make most historical, direct students     ;"
operator|+
literal|" CC_MKT_DESC=Blue, due beds come. Politicians would not make far thoughts. "
operator|+
literal|"Specifically new horses partic;"
operator|+
literal|" CC_MARKET_MANAGER=Gary Colburn; CC_DIVISION=4; CC_DIVISION_NAME=ese;"
operator|+
literal|" CC_COMPANY=3; CC_COMPANY_NAME=pri                                               ;"
operator|+
literal|" CC_STREET_NUMBER=463       ; CC_STREET_NAME=Pine Ridge;"
operator|+
literal|" CC_STREET_TYPE=RD             ; CC_SUITE_NUMBER=Suite U   ; CC_CITY=Midway;"
operator|+
literal|" CC_COUNTY=Williamson County; CC_STATE=TN; CC_ZIP=31904     ;"
operator|+
literal|" CC_COUNTRY=United States; CC_GMT_OFFSET=-5; CC_TAX_PERCENTAGE=0.12"
block|,
literal|"CC_CALL_CENTER_SK=6; CC_CALL_CENTER_ID=AAAAAAAAEAAAAAAA; CC_REC_START_DATE=2002-01-01;"
operator|+
literal|" CC_REC_END_DATE=null; CC_CLOSED_DATE_SK=null; CC_OPEN_DATE_SK=2451063;"
operator|+
literal|" CC_NAME=North Midwest; CC_CLASS=medium; CC_EMPLOYEES=7; CC_SQ_FT=3514;"
operator|+
literal|" CC_HOURS=8AM-4PM             ; CC_MANAGER=Larry Mccray; CC_MKT_ID=5;"
operator|+
literal|" CC_MKT_CLASS=Silly particles could pro                         ;"
operator|+
literal|" CC_MKT_DESC=Blue, due beds come. Politicians would not make far thoughts. "
operator|+
literal|"Specifically new horses partic;"
operator|+
literal|" CC_MARKET_MANAGER=Gary Colburn; CC_DIVISION=5; CC_DIVISION_NAME=anti;"
operator|+
literal|" CC_COMPANY=3; CC_COMPANY_NAME=pri                                               ;"
operator|+
literal|" CC_STREET_NUMBER=463       ; CC_STREET_NAME=Pine Ridge;"
operator|+
literal|" CC_STREET_TYPE=RD             ; CC_SUITE_NUMBER=Suite U   ; CC_CITY=Midway;"
operator|+
literal|" CC_COUNTY=Williamson County; CC_STATE=TN; CC_ZIP=31904     ;"
operator|+
literal|" CC_COUNTRY=United States; CC_GMT_OFFSET=-5; CC_TAX_PERCENTAGE=0.11"
block|}
decl_stmt|;
name|with
argument_list|()
operator|.
name|query
argument_list|(
literal|"select * from tpcds.call_center"
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
name|strings
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testTableCount
parameter_list|()
block|{
specifier|final
name|CalciteAssert
operator|.
name|AssertThat
name|with
init|=
name|with
argument_list|()
decl_stmt|;
comment|//    foo(with, "CALL_CENTER", 6);
comment|//    foo(with, "CATALOG_PAGE", 11_718);
comment|//    foo(with, "CATALOG_RETURNS", 144_067);
comment|//    foo(with, "CATALOG_SALES", 1_441_548);
comment|//    foo(with, "CUSTOMER", 100_000);
comment|//    foo(with, "CUSTOMER_ADDRESS", 50_000);
comment|//    foo(with, "CUSTOMER_DEMOGRAPHICS", 1_920_800);
comment|//    foo(with, "DATE_DIM", 73_049);
comment|//    foo(with, "HOUSEHOLD_DEMOGRAPHICS", 7_200);
comment|//    foo(with, "INCOME_BAND", 20);
comment|//    foo(with, "INVENTORY", 11_745_000);
comment|//    foo(with, "ITEM", 18_000);
comment|//    foo(with, "PROMOTION", 300);
comment|//    foo(with, "REASON", 35);
comment|//    foo(with, "SHIP_MODE", 20);
comment|//    foo(with, "STORE", 12);
comment|//    foo(with, "STORE_RETURNS", 287_514);
comment|//    foo(with, "STORE_SALES", 2_880_404);
comment|//    foo(with, "TIME_DIM", 86_400);
comment|//    foo(with, "WAREHOUSE", 5);
comment|//    foo(with, "WEB_PAGE", 60);
comment|//    foo(with, "WEB_RETURNS", 71_763);
comment|//    foo(with, "WEB_SALES", 719_384);
comment|//    foo(with, "WEB_SITE", 30);
name|foo
argument_list|(
name|with
argument_list|,
literal|"DBGEN_VERSION"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|foo
parameter_list|(
name|CalciteAssert
operator|.
name|AssertThat
name|with
parameter_list|,
name|String
name|tableName
parameter_list|,
name|int
name|expectedCount
parameter_list|)
block|{
specifier|final
name|String
name|sql
init|=
literal|"select * from tpcds."
operator|+
name|tableName
decl_stmt|;
name|with
operator|.
name|query
argument_list|(
name|sql
argument_list|)
operator|.
name|returnsCount
argument_list|(
name|expectedCount
argument_list|)
expr_stmt|;
block|}
comment|/** Tests the customer table with scale factor 5. */
annotation|@
name|Ignore
argument_list|(
literal|"add tests like this that count each table"
argument_list|)
annotation|@
name|Test
specifier|public
name|void
name|testCustomer5
parameter_list|()
block|{
name|with
argument_list|()
operator|.
name|query
argument_list|(
literal|"select * from tpcds_5.customer"
argument_list|)
operator|.
name|returnsCount
argument_list|(
literal|750000
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Ignore
argument_list|(
literal|"throws 'RuntimeException: Cannot convert null to long'"
argument_list|)
annotation|@
name|Test
specifier|public
name|void
name|testQuery01
parameter_list|()
block|{
name|checkQuery
argument_list|(
literal|1
argument_list|)
operator|.
name|runs
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testQuery17Plan
parameter_list|()
block|{
comment|//noinspection unchecked
name|checkQuery
argument_list|(
literal|17
argument_list|)
operator|.
name|withHook
argument_list|(
name|Hook
operator|.
name|PROGRAM
argument_list|,
name|handler
argument_list|(
literal|true
argument_list|,
literal|2
argument_list|)
argument_list|)
operator|.
name|explainMatches
argument_list|(
literal|"including all attributes "
argument_list|,
name|CalciteAssert
operator|.
name|checkMaskedResultContains
argument_list|(
literal|""
operator|+
literal|"EnumerableCalc(expr#0..9=[{inputs}], expr#10=[/($t4, $t3)], expr#11=[CAST($t10):INTEGER NOT NULL], expr#12=[*($t4, $t4)], expr#13=[/($t12, $t3)], expr#14=[-($t5, $t13)], expr#15=[1], expr#16=[=($t3, $t15)], expr#17=[null], expr#18=[-($t3, $t15)], expr#19=[CASE($t16, $t17, $t18)], expr#20=[/($t14, $t19)], expr#21=[0.5], expr#22=[POWER($t20, $t21)], expr#23=[CAST($t22):INTEGER NOT NULL], expr#24=[/($t23, $t11)], expr#25=[/($t6, $t3)], expr#26=[CAST($t25):INTEGER NOT NULL], expr#27=[*($t6, $t6)], expr#28=[/($t27, $t3)], expr#29=[-($t7, $t28)], expr#30=[/($t29, $t19)], expr#31=[POWER($t30, $t21)], expr#32=[CAST($t31):INTEGER NOT NULL], expr#33=[/($t32, $t26)], expr#34=[/($t8, $t3)], expr#35=[CAST($t34):INTEGER NOT NULL], expr#36=[*($t8, $t8)], expr#37=[/($t36, $t3)], expr#38=[-($t9, $t37)], expr#39=[/($t38, $t19)], expr#40=[POWER($t39, $t21)], expr#41=[CAST($t40):INTEGER NOT NULL], expr#42=[/($t41, $t35)], proj#0..3=[{exprs}], STORE_SALES_QUANTITYAVE=[$t11], STORE_SALES_QUANTITYSTDEV=[$t23], STORE_SALES_QUANTITYCOV=[$t24], AS_STORE_RETURNS_QUANTITYCOUNT=[$t3], AS_STORE_RETURNS_QUANTITYAVE=[$t26], AS_STORE_RETURNS_QUANTITYSTDEV=[$t32], STORE_RETURNS_QUANTITYCOV=[$t33], CATALOG_SALES_QUANTITYCOUNT=[$t3], CATALOG_SALES_QUANTITYAVE=[$t35], CATALOG_SALES_QUANTITYSTDEV=[$t42], CATALOG_SALES_QUANTITYCOV=[$t42]): rowcount = 100.0, cumulative cost = {1.2435775409784036E28 rows, 2.555295485909236E30 cpu, 0.0 io}\n"
operator|+
literal|"  EnumerableLimit(fetch=[100]): rowcount = 100.0, cumulative cost = {1.2435775409784036E28 rows, 2.555295485909236E30 cpu, 0.0 io}\n"
operator|+
literal|"    EnumerableSort(sort0=[$0], sort1=[$1], sort2=[$2], dir0=[ASC], dir1=[ASC], dir2=[ASC]): rowcount = 5.434029018852197E26, cumulative cost = {1.2435775409784036E28 rows, 2.555295485909236E30 cpu, 0.0 io}\n"
operator|+
literal|"      EnumerableAggregate(group=[{0, 1, 2}], STORE_SALES_QUANTITYCOUNT=[COUNT()], agg#1=[$SUM0($3)], agg#2=[$SUM0($6)], agg#3=[$SUM0($4)], agg#4=[$SUM0($7)], agg#5=[$SUM0($5)], agg#6=[$SUM0($8)]): rowcount = 5.434029018852197E26, cumulative cost = {1.1892372507898816E28 rows, 1.2172225002228922E30 cpu, 0.0 io}\n"
operator|+
literal|"        EnumerableCalc(expr#0..211=[{inputs}], expr#212=[*($t89, $t89)], expr#213=[*($t140, $t140)], expr#214=[*($t196, $t196)], I_ITEM_ID=[$t58], I_ITEM_DESC=[$t61], S_STATE=[$t24], SS_QUANTITY=[$t89], SR_RETURN_QUANTITY=[$t140], CS_QUANTITY=[$t196], $f6=[$t212], $f7=[$t213], $f8=[$t214]): rowcount = 5.434029018852197E27, cumulative cost = {1.0873492066864028E28 rows, 1.2172225002228922E30 cpu, 0.0 io}\n"
operator|+
literal|"          EnumerableHashJoin(condition=[AND(=($82, $133), =($81, $132), =($88, $139))], joinType=[inner]): rowcount = 5.434029018852197E27, cumulative cost = {5.439463048011832E27 rows, 1.8506796E7 cpu, 0.0 io}\n"
operator|+
literal|"            EnumerableHashJoin(condition=[=($0, $86)], joinType=[inner]): rowcount = 2.3008402586892598E13, cumulative cost = {4.8588854672854766E13 rows, 7281360.0 cpu, 0.0 io}\n"
operator|+
literal|"              EnumerableTableScan(table=[[TPCDS, STORE]]): rowcount = 12.0, cumulative cost = {12.0 rows, 13.0 cpu, 0.0 io}\n"
operator|+
literal|"              EnumerableHashJoin(condition=[=($0, $50)], joinType=[inner]): rowcount = 1.2782445881607E13, cumulative cost = {1.279800620431234E13 rows, 7281347.0 cpu, 0.0 io}\n"
operator|+
literal|"                EnumerableCalc(expr#0..27=[{inputs}], expr#28=['1998Q1'], expr#29=[=($t15, $t28)], proj#0..27=[{exprs}], $condition=[$t29]): rowcount = 10957.35, cumulative cost = {84006.35 rows, 4382941.0 cpu, 0.0 io}\n"
operator|+
literal|"                  EnumerableTableScan(table=[[TPCDS, DATE_DIM]]): rowcount = 73049.0, cumulative cost = {73049.0 rows, 73050.0 cpu, 0.0 io}\n"
operator|+
literal|"                EnumerableHashJoin(condition=[=($0, $24)], joinType=[inner]): rowcount = 7.7770908E9, cumulative cost = {7.783045975286664E9 rows, 2898406.0 cpu, 0.0 io}\n"
operator|+
literal|"                  EnumerableTableScan(table=[[TPCDS, ITEM]]): rowcount = 18000.0, cumulative cost = {18000.0 rows, 18001.0 cpu, 0.0 io}\n"
operator|+
literal|"                  EnumerableTableScan(table=[[TPCDS, STORE_SALES]]): rowcount = 2880404.0, cumulative cost = {2880404.0 rows, 2880405.0 cpu, 0.0 io}\n"
operator|+
literal|"            EnumerableHashJoin(condition=[AND(=($31, $79), =($30, $91))], joinType=[inner]): rowcount = 6.9978029381741304E16, cumulative cost = {7.0048032234040472E16 rows, 1.1225436E7 cpu, 0.0 io}\n"
operator|+
literal|"              EnumerableHashJoin(condition=[=($0, $28)], joinType=[inner]): rowcount = 7.87597881975E8, cumulative cost = {7.884434212216867E8 rows, 5035701.0 cpu, 0.0 io}\n"
operator|+
literal|"                EnumerableCalc(expr#0..27=[{inputs}], expr#28=['1998Q1'], expr#29=[=($t15, $t28)], expr#30=['1998Q2'], expr#31=[=($t15, $t30)], expr#32=['1998Q3'], expr#33=[=($t15, $t32)], expr#34=[OR($t29, $t31, $t33)], proj#0..27=[{exprs}], $condition=[$t34]): rowcount = 18262.25, cumulative cost = {91311.25 rows, 4748186.0 cpu, 0.0 io}\n"
operator|+
literal|"                  EnumerableTableScan(table=[[TPCDS, DATE_DIM]]): rowcount = 73049.0, cumulative cost = {73049.0 rows, 73050.0 cpu, 0.0 io}\n"
operator|+
literal|"                EnumerableTableScan(table=[[TPCDS, STORE_RETURNS]]): rowcount = 287514.0, cumulative cost = {287514.0 rows, 287515.0 cpu, 0.0 io}\n"
operator|+
literal|"              EnumerableHashJoin(condition=[=($0, $28)], joinType=[inner]): rowcount = 3.94888649445E9, cumulative cost = {3.9520401026966867E9 rows, 6189735.0 cpu, 0.0 io}\n"
operator|+
literal|"                EnumerableCalc(expr#0..27=[{inputs}], expr#28=['1998Q1'], expr#29=[=($t15, $t28)], expr#30=['1998Q2'], expr#31=[=($t15, $t30)], expr#32=['1998Q3'], expr#33=[=($t15, $t32)], expr#34=[OR($t29, $t31, $t33)], proj#0..27=[{exprs}], $condition=[$t34]): rowcount = 18262.25, cumulative cost = {91311.25 rows, 4748186.0 cpu, 0.0 io}\n"
operator|+
literal|"                  EnumerableTableScan(table=[[TPCDS, DATE_DIM]]): rowcount = 73049.0, cumulative cost = {73049.0 rows, 73050.0 cpu, 0.0 io}\n"
operator|+
literal|"                EnumerableTableScan(table=[[TPCDS, CATALOG_SALES]]): rowcount = 1441548.0, cumulative cost = {1441548.0 rows, 1441549.0 cpu, 0.0 io}\n"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Ignore
argument_list|(
literal|"throws 'RuntimeException: Cannot convert null to long'"
argument_list|)
annotation|@
name|Test
specifier|public
name|void
name|testQuery27
parameter_list|()
block|{
name|checkQuery
argument_list|(
literal|27
argument_list|)
operator|.
name|runs
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Ignore
argument_list|(
literal|"throws 'RuntimeException: Cannot convert null to long'"
argument_list|)
annotation|@
name|Test
specifier|public
name|void
name|testQuery58
parameter_list|()
block|{
name|checkQuery
argument_list|(
literal|58
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN"
argument_list|)
operator|.
name|runs
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Ignore
argument_list|(
literal|"takes too long to optimize"
argument_list|)
annotation|@
name|Test
specifier|public
name|void
name|testQuery72
parameter_list|()
block|{
name|checkQuery
argument_list|(
literal|72
argument_list|)
operator|.
name|runs
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Ignore
argument_list|(
literal|"work in progress"
argument_list|)
annotation|@
name|Test
specifier|public
name|void
name|testQuery72Plan
parameter_list|()
block|{
name|checkQuery
argument_list|(
literal|72
argument_list|)
operator|.
name|withHook
argument_list|(
name|Hook
operator|.
name|PROGRAM
argument_list|,
name|handler
argument_list|(
literal|true
argument_list|,
literal|2
argument_list|)
argument_list|)
operator|.
name|planContains
argument_list|(
literal|"xx"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Ignore
argument_list|(
literal|"throws 'java.lang.AssertionError: type mismatch'"
argument_list|)
annotation|@
name|Test
specifier|public
name|void
name|testQuery95
parameter_list|()
block|{
name|checkQuery
argument_list|(
literal|95
argument_list|)
operator|.
name|withHook
argument_list|(
name|Hook
operator|.
name|PROGRAM
argument_list|,
name|handler
argument_list|(
literal|false
argument_list|,
literal|6
argument_list|)
argument_list|)
operator|.
name|runs
argument_list|()
expr_stmt|;
block|}
specifier|private
name|CalciteAssert
operator|.
name|AssertQuery
name|checkQuery
parameter_list|(
name|int
name|i
parameter_list|)
block|{
specifier|final
name|Query
name|query
init|=
name|Query
operator|.
name|of
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|String
name|sql
init|=
name|query
operator|.
name|sql
argument_list|(
operator|new
name|Random
argument_list|(
literal|0
argument_list|)
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|i
condition|)
block|{
case|case
literal|58
case|:
if|if
condition|(
name|Bug
operator|.
name|upgrade
argument_list|(
literal|"new TPC-DS generator"
argument_list|)
condition|)
block|{
comment|// Work around bug: Support '<DATE>  =<character literal>'.
name|sql
operator|=
name|sql
operator|.
name|replace
argument_list|(
literal|" = '"
argument_list|,
literal|" = DATE '"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// Until TPC-DS generator can handle date(...).
name|sql
operator|=
name|sql
operator|.
name|replace
argument_list|(
literal|"'date([YEAR]+\"-01-01\",[YEAR]+\"-07-24\",sales)'"
argument_list|,
literal|"DATE '1998-08-18'"
argument_list|)
expr_stmt|;
block|}
break|break;
case|case
literal|72
case|:
comment|// Work around CALCITE-304: Support '<DATE> +<INTEGER>'.
name|sql
operator|=
name|sql
operator|.
name|replace
argument_list|(
literal|"+ 5"
argument_list|,
literal|"+ interval '5' day"
argument_list|)
expr_stmt|;
break|break;
case|case
literal|95
case|:
name|sql
operator|=
name|sql
operator|.
name|replace
argument_list|(
literal|"60 days"
argument_list|,
literal|"interval '60' day"
argument_list|)
expr_stmt|;
name|sql
operator|=
name|sql
operator|.
name|replace
argument_list|(
literal|"d_date between '"
argument_list|,
literal|"d_date between date '"
argument_list|)
expr_stmt|;
break|break;
block|}
return|return
name|with
argument_list|()
operator|.
name|query
argument_list|(
name|sql
operator|.
name|replaceAll
argument_list|(
literal|"tpcds\\."
argument_list|,
literal|"tpcds_01."
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|Frameworks
operator|.
name|ConfigBuilder
name|config
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Holder
argument_list|<
name|SchemaPlus
argument_list|>
name|root
init|=
name|Holder
operator|.
name|of
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|CalciteAssert
operator|.
name|model
argument_list|(
name|TPCDS_MODEL
argument_list|)
operator|.
name|doWithConnection
argument_list|(
name|connection
lambda|->
block|{
name|root
operator|.
name|set
argument_list|(
name|connection
operator|.
name|getRootSchema
argument_list|()
operator|.
name|getSubSchema
argument_list|(
literal|"TPCDS"
argument_list|)
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
return|return
name|Frameworks
operator|.
name|newConfigBuilder
argument_list|()
operator|.
name|parserConfig
argument_list|(
name|SqlParser
operator|.
name|Config
operator|.
name|DEFAULT
argument_list|)
operator|.
name|defaultSchema
argument_list|(
name|root
operator|.
name|get
argument_list|()
argument_list|)
operator|.
name|traitDefs
argument_list|(
operator|(
name|List
argument_list|<
name|RelTraitDef
argument_list|>
operator|)
literal|null
argument_list|)
operator|.
name|programs
argument_list|(
name|Programs
operator|.
name|heuristicJoinOrder
argument_list|(
name|Programs
operator|.
name|RULE_SET
argument_list|,
literal|true
argument_list|,
literal|2
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Builder query 27 using {@link RelBuilder}.    *    *<blockquote><pre>    *   select  i_item_id,    *         s_state, grouping(s_state) g_state,    *         avg(ss_quantity) agg1,    *         avg(ss_list_price) agg2,    *         avg(ss_coupon_amt) agg3,    *         avg(ss_sales_price) agg4    * from store_sales, customer_demographics, date_dim, store, item    * where ss_sold_date_sk = d_date_sk and    *        ss_item_sk = i_item_sk and    *        ss_store_sk = s_store_sk and    *        ss_cdemo_sk = cd_demo_sk and    *        cd_gender = 'dist(gender, 1, 1)' and    *        cd_marital_status = 'dist(marital_status, 1, 1)' and    *        cd_education_status = 'dist(education, 1, 1)' and    *        d_year = 1998 and    *        s_state in ('distmember(fips_county,[STATENUMBER.1], 3)',    *              'distmember(fips_county,[STATENUMBER.2], 3)',    *              'distmember(fips_county,[STATENUMBER.3], 3)',    *              'distmember(fips_county,[STATENUMBER.4], 3)',    *              'distmember(fips_county,[STATENUMBER.5], 3)',    *              'distmember(fips_county,[STATENUMBER.6], 3)')    *  group by rollup (i_item_id, s_state)    *  order by i_item_id    *          ,s_state    *  LIMIT 100    *</pre></blockquote>    */
annotation|@
name|Test
specifier|public
name|void
name|testQuery27Builder
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|RelBuilder
name|builder
init|=
name|RelBuilder
operator|.
name|create
argument_list|(
name|config
argument_list|()
operator|.
name|build
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|RelNode
name|root
init|=
name|builder
operator|.
name|scan
argument_list|(
literal|"STORE_SALES"
argument_list|)
operator|.
name|scan
argument_list|(
literal|"CUSTOMER_DEMOGRAPHICS"
argument_list|)
operator|.
name|scan
argument_list|(
literal|"DATE_DIM"
argument_list|)
operator|.
name|scan
argument_list|(
literal|"STORE"
argument_list|)
operator|.
name|scan
argument_list|(
literal|"ITEM"
argument_list|)
operator|.
name|join
argument_list|(
name|JoinRelType
operator|.
name|INNER
argument_list|)
operator|.
name|join
argument_list|(
name|JoinRelType
operator|.
name|INNER
argument_list|)
operator|.
name|join
argument_list|(
name|JoinRelType
operator|.
name|INNER
argument_list|)
operator|.
name|join
argument_list|(
name|JoinRelType
operator|.
name|INNER
argument_list|)
operator|.
name|filter
argument_list|(
name|builder
operator|.
name|equals
argument_list|(
name|builder
operator|.
name|field
argument_list|(
literal|"SS_SOLD_DATE_SK"
argument_list|)
argument_list|,
name|builder
operator|.
name|field
argument_list|(
literal|"D_DATE_SK"
argument_list|)
argument_list|)
argument_list|,
name|builder
operator|.
name|equals
argument_list|(
name|builder
operator|.
name|field
argument_list|(
literal|"SS_ITEM_SK"
argument_list|)
argument_list|,
name|builder
operator|.
name|field
argument_list|(
literal|"I_ITEM_SK"
argument_list|)
argument_list|)
argument_list|,
name|builder
operator|.
name|equals
argument_list|(
name|builder
operator|.
name|field
argument_list|(
literal|"SS_STORE_SK"
argument_list|)
argument_list|,
name|builder
operator|.
name|field
argument_list|(
literal|"S_STORE_SK"
argument_list|)
argument_list|)
argument_list|,
name|builder
operator|.
name|equals
argument_list|(
name|builder
operator|.
name|field
argument_list|(
literal|"SS_CDEMO_SK"
argument_list|)
argument_list|,
name|builder
operator|.
name|field
argument_list|(
literal|"CD_DEMO_SK"
argument_list|)
argument_list|)
argument_list|,
name|builder
operator|.
name|equals
argument_list|(
name|builder
operator|.
name|field
argument_list|(
literal|"CD_GENDER"
argument_list|)
argument_list|,
name|builder
operator|.
name|literal
argument_list|(
literal|"M"
argument_list|)
argument_list|)
argument_list|,
name|builder
operator|.
name|equals
argument_list|(
name|builder
operator|.
name|field
argument_list|(
literal|"CD_MARITAL_STATUS"
argument_list|)
argument_list|,
name|builder
operator|.
name|literal
argument_list|(
literal|"S"
argument_list|)
argument_list|)
argument_list|,
name|builder
operator|.
name|equals
argument_list|(
name|builder
operator|.
name|field
argument_list|(
literal|"CD_EDUCATION_STATUS"
argument_list|)
argument_list|,
name|builder
operator|.
name|literal
argument_list|(
literal|"HIGH SCHOOL"
argument_list|)
argument_list|)
argument_list|,
name|builder
operator|.
name|equals
argument_list|(
name|builder
operator|.
name|field
argument_list|(
literal|"D_YEAR"
argument_list|)
argument_list|,
name|builder
operator|.
name|literal
argument_list|(
literal|1998
argument_list|)
argument_list|)
argument_list|,
name|builder
operator|.
name|call
argument_list|(
name|SqlStdOperatorTable
operator|.
name|IN
argument_list|,
name|builder
operator|.
name|field
argument_list|(
literal|"S_STATE"
argument_list|)
argument_list|,
name|builder
operator|.
name|call
argument_list|(
name|SqlStdOperatorTable
operator|.
name|ARRAY_VALUE_CONSTRUCTOR
argument_list|,
name|builder
operator|.
name|literal
argument_list|(
literal|"CA"
argument_list|)
argument_list|,
name|builder
operator|.
name|literal
argument_list|(
literal|"OR"
argument_list|)
argument_list|,
name|builder
operator|.
name|literal
argument_list|(
literal|"WA"
argument_list|)
argument_list|,
name|builder
operator|.
name|literal
argument_list|(
literal|"TX"
argument_list|)
argument_list|,
name|builder
operator|.
name|literal
argument_list|(
literal|"OK"
argument_list|)
argument_list|,
name|builder
operator|.
name|literal
argument_list|(
literal|"MD"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
operator|.
name|aggregate
argument_list|(
name|builder
operator|.
name|groupKey
argument_list|(
literal|"I_ITEM_ID"
argument_list|,
literal|"S_STATE"
argument_list|)
argument_list|,
name|builder
operator|.
name|avg
argument_list|(
literal|false
argument_list|,
literal|"AGG1"
argument_list|,
name|builder
operator|.
name|field
argument_list|(
literal|"SS_QUANTITY"
argument_list|)
argument_list|)
argument_list|,
name|builder
operator|.
name|avg
argument_list|(
literal|false
argument_list|,
literal|"AGG2"
argument_list|,
name|builder
operator|.
name|field
argument_list|(
literal|"SS_LIST_PRICE"
argument_list|)
argument_list|)
argument_list|,
name|builder
operator|.
name|avg
argument_list|(
literal|false
argument_list|,
literal|"AGG3"
argument_list|,
name|builder
operator|.
name|field
argument_list|(
literal|"SS_COUPON_AMT"
argument_list|)
argument_list|)
argument_list|,
name|builder
operator|.
name|avg
argument_list|(
literal|false
argument_list|,
literal|"AGG4"
argument_list|,
name|builder
operator|.
name|field
argument_list|(
literal|"SS_SALES_PRICE"
argument_list|)
argument_list|)
argument_list|)
operator|.
name|sortLimit
argument_list|(
literal|0
argument_list|,
literal|100
argument_list|,
name|builder
operator|.
name|field
argument_list|(
literal|"I_ITEM_ID"
argument_list|)
argument_list|,
name|builder
operator|.
name|field
argument_list|(
literal|"S_STATE"
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|RelOptUtil
operator|.
name|toString
argument_list|(
name|root
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End TpcdsTest.java
end_comment

end_unit

