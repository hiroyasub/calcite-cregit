begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to you under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|Holder
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
comment|/** Unit test for {@link net.hydromatic.optiq.impl.tpcds.TpcdsSchema}.  *  *<p>Only runs if {@code -Dcalcite.test.slow=true} is specified on the  * command-line.  * (See {@link net.hydromatic.optiq.test.OptiqAssert#ENABLE_SLOW}.)</p> */
end_comment

begin_class
specifier|public
class|class
name|TpcdsTest
block|{
specifier|private
specifier|static
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
name|Holder
argument_list|<
name|Program
argument_list|>
argument_list|>
argument_list|,
name|Void
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
name|Holder
argument_list|<
name|Program
argument_list|>
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
name|Holder
argument_list|<
name|Program
argument_list|>
argument_list|>
name|pair
parameter_list|)
block|{
name|pair
operator|.
name|right
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
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
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
name|OptiqAssert
operator|.
name|checkMaskedResultContains
argument_list|(
literal|""
operator|+
literal|"EnumerableCalcRel(expr#0..11=[{inputs}], expr#12=[/($t5, $t4)], expr#13=[/($t8, $t7)], expr#14=[/($t11, $t10)], proj#0..5=[{exprs}], STORE_SALES_QUANTITYCOV=[$t12], AS_STORE_RETURNS_QUANTITYCOUNT=[$t6], AS_STORE_RETURNS_QUANTITYAVE=[$t7], AS_STORE_RETURNS_QUANTITYSTDEV=[$t8], STORE_RETURNS_QUANTITYCOV=[$t13], CATALOG_SALES_QUANTITYCOUNT=[$t9], CATALOG_SALES_QUANTITYAVE=[$t10], CATALOG_SALES_QUANTITYSTDEV=[$t14], CATALOG_SALES_QUANTITYCOV=[$t14]): rowcount = 5.434029018852197E26, cumulative cost = {1.618185849567114E30 rows, 1.2672155671963324E30 cpu, 0.0 io}\n"
operator|+
literal|"  EnumerableSortRel(sort0=[$0], sort1=[$1], sort2=[$2], dir0=[ASC], dir1=[ASC], dir2=[ASC]): rowcount = 5.434029018852197E26, cumulative cost = {1.6176424466652288E30 rows, 1.2509134801397759E30 cpu, 0.0 io}\n"
operator|+
literal|"    EnumerableCalcRel(expr#0..12=[{inputs}], expr#13=[/($t4, $t5)], expr#14=[CAST($t13):JavaType(class java.lang.Integer)], expr#15=[*($t4, $t4)], expr#16=[/($t15, $t5)], expr#17=[-($t6, $t16)], expr#18=[1], expr#19=[=($t5, $t18)], expr#20=[null], expr#21=[-($t5, $t18)], expr#22=[CASE($t19, $t20, $t21)], expr#23=[/($t17, $t22)], expr#24=[0.5], expr#25=[POWER($t23, $t24)], expr#26=[CAST($t25):JavaType(class java.lang.Integer)], expr#27=[/($t8, $t7)], expr#28=[CAST($t27):JavaType(class java.lang.Integer)], expr#29=[*($t8, $t8)], expr#30=[/($t29, $t7)], expr#31=[-($t9, $t30)], expr#32=[=($t7, $t18)], expr#33=[-($t7, $t18)], expr#34=[CASE($t32, $t20, $t33)], expr#35=[/($t31, $t34)], expr#36=[POWER($t35, $t24)], expr#37=[CAST($t36):JavaType(class java.lang.Integer)], expr#38=[/($t11, $t10)], expr#39=[CAST($t38):JavaType(class java.lang.Integer)], expr#40=[*($t11, $t11)], expr#41=[/($t40, $t10)], expr#42=[-($t12, $t41)], expr#43=[=($t10, $t18)], expr#44=[-($t10, $t18)], expr#45=[CASE($t43, $t20, $t44)], expr#46=[/($t42, $t45)], expr#47=[POWER($t46, $t24)], expr#48=[CAST($t47):JavaType(class java.lang.Integer)], proj#0..3=[{exprs}], STORE_SALES_QUANTITYAVE=[$t14], STORE_SALES_QUANTITYSTDEV=[$t26], AS_STORE_RETURNS_QUANTITYCOUNT=[$t7], AS_STORE_RETURNS_QUANTITYAVE=[$t28], AS_STORE_RETURNS_QUANTITYSTDEV=[$t37], CATALOG_SALES_QUANTITYCOUNT=[$t10], CATALOG_SALES_QUANTITYAVE=[$t39], $f11=[$t48]): rowcount = 5.434029018852197E26, cumulative cost = {1.1954863841615548E28 rows, 1.2503700772378907E30 cpu, 0.0 io}\n"
operator|+
literal|"      EnumerableAggregateRel(group=[{0, 1, 2}], STORE_SALES_QUANTITYCOUNT=[COUNT()], agg#1=[SUM($3)], agg#2=[COUNT($3)], agg#3=[SUM($6)], AS_STORE_RETURNS_QUANTITYCOUNT=[COUNT($4)], agg#5=[SUM($4)], agg#6=[SUM($7)], CATALOG_SALES_QUANTITYCOUNT=[COUNT($5)], agg#8=[SUM($5)], agg#9=[SUM($8)]): rowcount = 5.434029018852197E26, cumulative cost = {1.1411460939730328E28 rows, 1.2172225002228922E30 cpu, 0.0 io}\n"
operator|+
literal|"        EnumerableCalcRel(expr#0..211=[{inputs}], expr#212=[*($t89, $t89)], expr#213=[*($t140, $t140)], expr#214=[*($t196, $t196)], I_ITEM_ID=[$t58], I_ITEM_DESC=[$t61], S_STATE=[$t24], SS_QUANTITY=[$t89], SR_RETURN_QUANTITY=[$t140], CS_QUANTITY=[$t196], $f6=[$t212], $f7=[$t213], $f8=[$t214]): rowcount = 5.434029018852197E27, cumulative cost = {1.0868058037845108E28 rows, 1.2172225002228922E30 cpu, 0.0 io}\n"
operator|+
literal|"          EnumerableJoinRel(condition=[AND(=($82, $133), =($81, $132), =($88, $139))], joinType=[inner]): rowcount = 5.434029018852197E27, cumulative cost = {5.434029018992911E27 rows, 1.8579845E7 cpu, 0.0 io}\n"
operator|+
literal|"            EnumerableJoinRel(condition=[=($0, $86)], joinType=[inner]): rowcount = 2.3008402586892598E13, cumulative cost = {4.8588854672853766E13 rows, 7354409.0 cpu, 0.0 io}\n"
operator|+
literal|"              EnumerableTableAccessRel(table=[[TPCDS, STORE]]): rowcount = 12.0, cumulative cost = {12.0 rows, 13.0 cpu, 0.0 io}\n"
operator|+
literal|"              EnumerableJoinRel(condition=[=($0, $50)], joinType=[inner]): rowcount = 1.2782445881607E13, cumulative cost = {1.279800620431234E13 rows, 7354396.0 cpu, 0.0 io}\n"
operator|+
literal|"                EnumerableCalcRel(expr#0..27=[{inputs}], expr#28=[CAST($t15):VARCHAR(6) CHARACTER SET \"ISO-8859-1\" COLLATE \"ISO-8859-1$en_US$primary\"], expr#29=['1998Q1'], expr#30=[=($t28, $t29)], proj#0..27=[{exprs}], $condition=[$t30]): rowcount = 10957.35, cumulative cost = {84006.35 rows, 4455990.0 cpu, 0.0 io}\n"
operator|+
literal|"                  EnumerableTableAccessRel(table=[[TPCDS, DATE_DIM]]): rowcount = 73049.0, cumulative cost = {73049.0 rows, 73050.0 cpu, 0.0 io}\n"
operator|+
literal|"                EnumerableJoinRel(condition=[=($0, $24)], joinType=[inner]): rowcount = 7.7770908E9, cumulative cost = {7.783045975286664E9 rows, 2898406.0 cpu, 0.0 io}\n"
operator|+
literal|"                  EnumerableTableAccessRel(table=[[TPCDS, ITEM]]): rowcount = 18000.0, cumulative cost = {18000.0 rows, 18001.0 cpu, 0.0 io}\n"
operator|+
literal|"                  EnumerableTableAccessRel(table=[[TPCDS, STORE_SALES]]): rowcount = 2880404.0, cumulative cost = {2880404.0 rows, 2880405.0 cpu, 0.0 io}\n"
operator|+
literal|"            EnumerableJoinRel(condition=[AND(=($31, $79), =($30, $91))], joinType=[inner]): rowcount = 6.9978029381741304E16, cumulative cost = {6.9978054204658736E16 rows, 1.1225436E7 cpu, 0.0 io}\n"
operator|+
literal|"              EnumerableJoinRel(condition=[=($0, $28)], joinType=[inner]): rowcount = 7.87597881975E8, cumulative cost = {7.884434222216867E8 rows, 5035701.0 cpu, 0.0 io}\n"
operator|+
literal|"                EnumerableCalcRel(expr#0..27=[{inputs}], expr#28=['1998Q1'], expr#29=[=($t15, $t28)], expr#30=['1998Q2'], expr#31=[=($t15, $t30)], expr#32=['1998Q3'], expr#33=[=($t15, $t32)], expr#34=[OR($t29, $t31, $t33)], proj#0..27=[{exprs}], $condition=[$t34]): rowcount = 18262.25, cumulative cost = {91311.25 rows, 4748186.0 cpu, 0.0 io}\n"
operator|+
literal|"                  EnumerableTableAccessRel(table=[[TPCDS, DATE_DIM]]): rowcount = 73049.0, cumulative cost = {73049.0 rows, 73050.0 cpu, 0.0 io}\n"
operator|+
literal|"                EnumerableTableAccessRel(table=[[TPCDS, STORE_RETURNS]]): rowcount = 287514.0, cumulative cost = {287514.0 rows, 287515.0 cpu, 0.0 io}\n"
operator|+
literal|"              EnumerableJoinRel(condition=[=($0, $28)], joinType=[inner]): rowcount = 3.94888649445E9, cumulative cost = {3.9520401026966867E9 rows, 6189735.0 cpu, 0.0 io}\n"
operator|+
literal|"                EnumerableCalcRel(expr#0..27=[{inputs}], expr#28=['1998Q1'], expr#29=[=($t15, $t28)], expr#30=['1998Q2'], expr#31=[=($t15, $t30)], expr#32=['1998Q3'], expr#33=[=($t15, $t32)], expr#34=[OR($t29, $t31, $t33)], proj#0..27=[{exprs}], $condition=[$t34]): rowcount = 18262.25, cumulative cost = {91311.25 rows, 4748186.0 cpu, 0.0 io}\n"
operator|+
literal|"                  EnumerableTableAccessRel(table=[[TPCDS, DATE_DIM]]): rowcount = 73049.0, cumulative cost = {73049.0 rows, 73050.0 cpu, 0.0 io}\n"
operator|+
literal|"                EnumerableTableAccessRel(table=[[TPCDS, CATALOG_SALES]]): rowcount = 1441548.0, cumulative cost = {1441548.0 rows, 1441549.0 cpu, 0.0 io}\n"
argument_list|)
argument_list|)
expr_stmt|;
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
block|}
end_class

begin_comment
comment|// End TpcdsTest.java
end_comment

end_unit

