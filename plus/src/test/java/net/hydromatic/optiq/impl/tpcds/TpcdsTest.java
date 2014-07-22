begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to Julian Hyde under one or more contributor license // agreements. See the NOTICE file distributed with this work for // additional information regarding copyright ownership. // // Julian Hyde licenses this file to you under the Apache License, // Version 2.0 (the "License"); you may not use this file except in // compliance with the License. You may obtain a copy of the License at: // // http://www.apache.org/licenses/LICENSE-2.0 // // Unless required by applicable law or agreed to in writing, software // distributed under the License is distributed on an "AS IS" BASIS, // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. // See the License for the specific language governing permissions and // limitations under the License. */
end_comment

begin_package
package|package
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|impl
operator|.
name|tpcds
package|;
end_package

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|prepare
operator|.
name|Prepare
import|;
end_import

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|runtime
operator|.
name|Hook
import|;
end_import

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|test
operator|.
name|OptiqAssert
import|;
end_import

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|tools
operator|.
name|Program
import|;
end_import

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|optiq
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
name|eigenbase
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
name|eigenbase
operator|.
name|util
operator|.
name|Pair
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Function
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

begin_comment
comment|/** Unit test for {@link net.hydromatic.optiq.impl.tpcds.TpcdsSchema}.  *  *<p>Only runs if {@code -Doptiq.test.slow=true} is specified on the  * command-line.  * (See {@link net.hydromatic.optiq.test.OptiqAssert#ENABLE_SLOW}.)</p> */
end_comment

begin_class
specifier|public
class|class
name|TpcdsTest
block|{
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
literal|"       factory: 'net.hydromatic.optiq.impl.tpcds.TpcdsSchemaFactory',\n"
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
name|OptiqAssert
operator|.
name|AssertThat
name|with
parameter_list|()
block|{
return|return
name|OptiqAssert
operator|.
name|that
argument_list|()
operator|.
name|withModel
argument_list|(
name|TPCDS_MODEL
argument_list|)
operator|.
name|enable
argument_list|(
name|OptiqAssert
operator|.
name|ENABLE_SLOW
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
name|with
argument_list|()
operator|.
name|query
argument_list|(
literal|"select * from tpcds.call_center"
argument_list|)
operator|.
name|returnsUnordered
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Ignore
argument_list|(
literal|"add tests like this that count each table"
argument_list|)
annotation|@
name|Test
specifier|public
name|void
name|testLineItem
parameter_list|()
block|{
name|with
argument_list|()
operator|.
name|query
argument_list|(
literal|"select * from tpcds.lineitem"
argument_list|)
operator|.
name|returnsCount
argument_list|(
literal|6001215
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
name|Hook
operator|.
name|Closeable
name|closeable
init|=
name|Hook
operator|.
name|PROGRAM
operator|.
name|addThread
argument_list|(
operator|new
name|Function
argument_list|<
name|Pair
argument_list|<
name|List
argument_list|<
name|Prepare
operator|.
name|Materialization
argument_list|>
argument_list|,
name|Program
index|[]
argument_list|>
argument_list|,
name|Void
argument_list|>
argument_list|()
block|{
specifier|public
name|Void
name|apply
parameter_list|(
name|Pair
argument_list|<
name|List
argument_list|<
name|Prepare
operator|.
name|Materialization
argument_list|>
argument_list|,
name|Program
index|[]
argument_list|>
name|a0
parameter_list|)
block|{
name|a0
operator|.
name|right
index|[
literal|0
index|]
operator|=
name|Programs
operator|.
name|heuristicJoinOrder
argument_list|(
name|Programs
operator|.
name|RULE_SET
argument_list|,
literal|true
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
argument_list|)
decl_stmt|;
try|try
block|{
name|checkQuery
argument_list|(
literal|17
argument_list|)
operator|.
name|explainMatches
argument_list|(
literal|"including all attributes "
argument_list|,
name|OptiqAssert
operator|.
name|checkMaskedResultContains
argument_list|(
literal|""
operator|+
literal|"EnumerableProjectRel(I_ITEM_ID=[$0], I_ITEM_DESC=[$1], S_STATE=[$2], STORE_SALES_QUANTITYCOUNT=[$3], STORE_SALES_QUANTITYAVE=[$4], STORE_SALES_QUANTITYSTDEV=[$5], STORE_SALES_QUANTITYCOV=[/($5, $4)], AS_STORE_RETURNS_QUANTITYCOUNT=[$6], AS_STORE_RETURNS_QUANTITYAVE=[$7], AS_STORE_RETURNS_QUANTITYSTDEV=[$8], STORE_RETURNS_QUANTITYCOV=[/($8, $7)], CATALOG_SALES_QUANTITYCOUNT=[$9], CATALOG_SALES_QUANTITYAVE=[$10], CATALOG_SALES_QUANTITYSTDEV=[/($11, $10)], CATALOG_SALES_QUANTITYCOV=[/($11, $10)]): rowcount = 5.434029018852197E26, cumulative cost = {1.618185849567114E30 rows, 6.412154242245593E28 cpu, 0.0 io}\n"
operator|+
literal|"  EnumerableSortRel(sort0=[$0], sort1=[$1], sort2=[$2], dir0=[ASC], dir1=[ASC], dir2=[ASC]): rowcount = 5.434029018852197E26, cumulative cost = {1.6176424466652288E30 rows, 5.597049889417763E28 cpu, 0.0 io}\n"
operator|+
literal|"    EnumerableProjectRel(I_ITEM_ID=[$0], I_ITEM_DESC=[$1], S_STATE=[$2], STORE_SALES_QUANTITYCOUNT=[$3], STORE_SALES_QUANTITYAVE=[CAST(/($4, $5)):JavaType(class java.lang.Integer)], STORE_SALES_QUANTITYSTDEV=[CAST(POWER(/(-($6, /(*($4, $4), $5)), CASE(=($5, 1), null, -($5, 1))), 0.5)):JavaType(class java.lang.Integer)], AS_STORE_RETURNS_QUANTITYCOUNT=[$7], AS_STORE_RETURNS_QUANTITYAVE=[CAST(/($8, $7)):JavaType(class java.lang.Integer)], AS_STORE_RETURNS_QUANTITYSTDEV=[CAST(POWER(/(-($9, /(*($8, $8), $7)), CASE(=($7, 1), null, -($7, 1))), 0.5)):JavaType(class java.lang.Integer)], CATALOG_SALES_QUANTITYCOUNT=[$10], CATALOG_SALES_QUANTITYAVE=[CAST(/($11, $10)):JavaType(class java.lang.Integer)], $f11=[CAST(POWER(/(-($12, /(*($11, $11), $10)), CASE(=($10, 1), null, -($10, 1))), 0.5)):JavaType(class java.lang.Integer)]): rowcount = 5.434029018852197E26, cumulative cost = {1.1954863841615548E28 rows, 5.5427095992292415E28 cpu, 0.0 io}\n"
operator|+
literal|"      EnumerableAggregateRel(group=[{0, 1, 2}], STORE_SALES_QUANTITYCOUNT=[COUNT()], agg#1=[SUM($3)], agg#2=[COUNT($3)], agg#3=[SUM($6)], AS_STORE_RETURNS_QUANTITYCOUNT=[COUNT($4)], agg#5=[SUM($4)], agg#6=[SUM($7)], CATALOG_SALES_QUANTITYCOUNT=[COUNT($5)], agg#8=[SUM($5)], agg#9=[SUM($8)]): rowcount = 5.434029018852197E26, cumulative cost = {1.1411460939730328E28 rows, 4.890626116966977E28 cpu, 0.0 io}\n"
operator|+
literal|"        EnumerableProjectRel(I_ITEM_ID=[$58], I_ITEM_DESC=[$61], S_STATE=[$24], SS_QUANTITY=[$89], SR_RETURN_QUANTITY=[$140], CS_QUANTITY=[$196], $f6=[*($89, $89)], $f7=[*($140, $140)], $f8=[*($196, $196)]): rowcount = 5.434029018852197E27, cumulative cost = {1.0868058037845108E28 rows, 4.890626116966977E28 cpu, 0.0 io}\n"
operator|+
literal|"          EnumerableJoinRel(condition=[AND(=($82, $133), =($81, $132), =($88, $139))], joinType=[inner]): rowcount = 5.434029018852197E27, cumulative cost = {5.434029018992911E27 rows, 5065780.0 cpu, 0.0 io}\n"
operator|+
literal|"            EnumerableJoinRel(condition=[=($0, $86)], joinType=[inner]): rowcount = 2.3008402586892598E13, cumulative cost = {4.8588854672852766E13 rows, 3044518.0 cpu, 0.0 io}\n"
operator|+
literal|"              EnumerableTableAccessRel(table=[[TPCDS, STORE]]): rowcount = 12.0, cumulative cost = {12.0 rows, 13.0 cpu, 0.0 io}\n"
operator|+
literal|"              EnumerableJoinRel(condition=[=($0, $50)], joinType=[inner]): rowcount = 1.2782445881607E13, cumulative cost = {1.279800620431134E13 rows, 3044505.0 cpu, 0.0 io}\n"
operator|+
literal|"                EnumerableFilterRel(condition=[=(CAST($15):VARCHAR(6) CHARACTER SET \"ISO-8859-1\" COLLATE \"ISO-8859-1$en_US$primary\", '1998Q1')]): rowcount = 10957.35, cumulative cost = {84006.35 rows, 146099.0 cpu, 0.0 io}\n"
operator|+
literal|"                  EnumerableTableAccessRel(table=[[TPCDS, DATE_DIM]]): rowcount = 73049.0, cumulative cost = {73049.0 rows, 73050.0 cpu, 0.0 io}\n"
operator|+
literal|"                EnumerableJoinRel(condition=[=($0, $24)], joinType=[inner]): rowcount = 7.7770908E9, cumulative cost = {7.783045975286664E9 rows, 2898406.0 cpu, 0.0 io}\n"
operator|+
literal|"                  EnumerableTableAccessRel(table=[[TPCDS, ITEM]]): rowcount = 18000.0, cumulative cost = {18000.0 rows, 18001.0 cpu, 0.0 io}\n"
operator|+
literal|"                  EnumerableTableAccessRel(table=[[TPCDS, STORE_SALES]]): rowcount = 2880404.0, cumulative cost = {2880404.0 rows, 2880405.0 cpu, 0.0 io}\n"
operator|+
literal|"            EnumerableJoinRel(condition=[AND(=($31, $79), =($30, $91))], joinType=[inner]): rowcount = 6.9978029381741304E16, cumulative cost = {6.9978054204658736E16 rows, 2021262.0 cpu, 0.0 io}\n"
operator|+
literal|"              EnumerableJoinRel(condition=[=($28, $0)], joinType=[inner]): rowcount = 7.87597881975E8, cumulative cost = {7.884434222216867E8 rows, 433614.0 cpu, 0.0 io}\n"
operator|+
literal|"                EnumerableFilterRel(condition=[OR(=($15, '1998Q1'), =($15, '1998Q2'), =($15, '1998Q3'))]): rowcount = 18262.25, cumulative cost = {91311.25 rows, 146099.0 cpu, 0.0 io}\n"
operator|+
literal|"                  EnumerableTableAccessRel(table=[[TPCDS, DATE_DIM]]): rowcount = 73049.0, cumulative cost = {73049.0 rows, 73050.0 cpu, 0.0 io}\n"
operator|+
literal|"                EnumerableTableAccessRel(table=[[TPCDS, STORE_RETURNS]]): rowcount = 287514.0, cumulative cost = {287514.0 rows, 287515.0 cpu, 0.0 io}\n"
operator|+
literal|"              EnumerableJoinRel(condition=[=($28, $0)], joinType=[inner]): rowcount = 3.94888649445E9, cumulative cost = {3.9520401026966867E9 rows, 1587648.0 cpu, 0.0 io}\n"
operator|+
literal|"                EnumerableFilterRel(condition=[OR(=($15, '1998Q1'), =($15, '1998Q2'), =($15, '1998Q3'))]): rowcount = 18262.25, cumulative cost = {91311.25 rows, 146099.0 cpu, 0.0 io}\n"
operator|+
literal|"                  EnumerableTableAccessRel(table=[[TPCDS, DATE_DIM]]): rowcount = 73049.0, cumulative cost = {73049.0 rows, 73050.0 cpu, 0.0 io}\n"
operator|+
literal|"                EnumerableTableAccessRel(table=[[TPCDS, CATALOG_SALES]]): rowcount = 1441548.0, cumulative cost = {1441548.0 rows, 1441549.0 cpu, 0.0 io}\n"
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|closeable
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
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
name|planContains
argument_list|(
literal|"xx"
argument_list|)
expr_stmt|;
block|}
specifier|private
name|OptiqAssert
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
operator|-
literal|1
argument_list|,
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
comment|// Work around OPTIQ-304: Support '<DATE> +<INTEGER>'.
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
block|}
end_class

begin_comment
comment|// End TpcdsTest.java
end_comment

end_unit

