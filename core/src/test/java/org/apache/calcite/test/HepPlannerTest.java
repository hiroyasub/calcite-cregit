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
name|test
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
name|plan
operator|.
name|RelOptListener
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
name|hep
operator|.
name|HepMatchOrder
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
name|hep
operator|.
name|HepPlanner
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
name|hep
operator|.
name|HepProgram
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
name|hep
operator|.
name|HepProgramBuilder
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
name|RelFactories
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
name|logical
operator|.
name|LogicalIntersect
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
name|logical
operator|.
name|LogicalUnion
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
name|rules
operator|.
name|CalcMergeRule
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
name|rules
operator|.
name|CoerceInputsRule
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
name|rules
operator|.
name|FilterToCalcRule
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
name|rules
operator|.
name|ProjectRemoveRule
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
name|rules
operator|.
name|ProjectToCalcRule
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
name|rules
operator|.
name|ReduceExpressionsRule
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
name|rules
operator|.
name|UnionToDistinctRule
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
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|CoreMatchers
operator|.
name|is
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertThat
import|;
end_import

begin_comment
comment|/**  * HepPlannerTest is a unit test for {@link HepPlanner}. See  * {@link RelOptRulesTest} for an explanation of how to add tests; the tests in  * this class are targeted at exercising the planner, and use specific rules for  * convenience only, whereas the tests in that class are targeted at exercising  * specific rules, and use the planner for convenience only. Hence the split.  */
end_comment

begin_class
specifier|public
class|class
name|HepPlannerTest
extends|extends
name|RelOptTestBase
block|{
comment|//~ Static fields/initializers ---------------------------------------------
specifier|private
specifier|static
specifier|final
name|String
name|UNION_TREE
init|=
literal|"(select name from dept union select ename from emp)"
operator|+
literal|" union (select ename from bonus)"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|COMPLEX_UNION_TREE
init|=
literal|"select * from (\n"
operator|+
literal|"  select ENAME, 50011895 as cat_id, '1' as cat_name, 1 as require_free_postage, 0 as require_15return, 0 as require_48hour,1 as require_insurance from emp where EMPNO = 20171216 and MGR = 0 and ENAME = 'Y' and SAL = 50011895 union all\n"
operator|+
literal|"  select ENAME, 50013023 as cat_id, '2' as cat_name, 0 as require_free_postage, 0 as require_15return, 0 as require_48hour,0 as require_insurance from emp where EMPNO = 20171216 and MGR = 0 and ENAME = 'Y' and SAL = 50013023 union all\n"
operator|+
literal|"  select ENAME, 50013032 as cat_id, '3' as cat_name, 0 as require_free_postage, 0 as require_15return, 0 as require_48hour,0 as require_insurance from emp where EMPNO = 20171216 and MGR = 0 and ENAME = 'Y' and SAL = 50013032 union all\n"
operator|+
literal|"  select ENAME, 50013024 as cat_id, '4' as cat_name, 0 as require_free_postage, 0 as require_15return, 0 as require_48hour,0 as require_insurance from emp where EMPNO = 20171216 and MGR = 0 and ENAME = 'Y' and SAL = 50013024 union all\n"
operator|+
literal|"  select ENAME, 50004204 as cat_id, '5' as cat_name, 0 as require_free_postage, 0 as require_15return, 0 as require_48hour,0 as require_insurance from emp where EMPNO = 20171216 and MGR = 0 and ENAME = 'Y' and SAL = 50004204 union all\n"
operator|+
literal|"  select ENAME, 50013043 as cat_id, '6' as cat_name, 0 as require_free_postage, 0 as require_15return, 0 as require_48hour,0 as require_insurance from emp where EMPNO = 20171216 and MGR = 0 and ENAME = 'Y' and SAL = 50013043 union all\n"
operator|+
literal|"  select ENAME, 290903 as cat_id, '7' as cat_name, 1 as require_free_postage, 0 as require_15return, 0 as require_48hour,1 as require_insurance from emp where EMPNO = 20171216 and MGR = 0 and ENAME = 'Y' and SAL = 290903 union all\n"
operator|+
literal|"  select ENAME, 50008261 as cat_id, '8' as cat_name, 1 as require_free_postage, 0 as require_15return, 0 as require_48hour,1 as require_insurance from emp where EMPNO = 20171216 and MGR = 0 and ENAME = 'Y' and SAL = 50008261 union all\n"
operator|+
literal|"  select ENAME, 124478013 as cat_id, '9' as cat_name, 0 as require_free_postage, 0 as require_15return, 1 as require_48hour,0 as require_insurance from emp where EMPNO = 20171216 and MGR = 0 and ENAME = 'Y' and SAL = 124478013 union all\n"
operator|+
literal|"  select ENAME, 124472005 as cat_id, '10' as cat_name, 0 as require_free_postage, 0 as require_15return, 1 as require_48hour,0 as require_insurance from emp where EMPNO = 20171216 and MGR = 0 and ENAME = 'Y' and SAL = 124472005 union all\n"
operator|+
literal|"  select ENAME, 50013475 as cat_id, '11' as cat_name, 0 as require_free_postage, 1 as require_15return, 1 as require_48hour,0 as require_insurance from emp where EMPNO = 20171216 and MGR = 0 and ENAME = 'Y' and SAL = 50013475 union all\n"
operator|+
literal|"  select ENAME, 50018263 as cat_id, '12' as cat_name, 0 as require_free_postage, 1 as require_15return, 1 as require_48hour,0 as require_insurance from emp where EMPNO = 20171216 and MGR = 0 and ENAME = 'Y' and SAL = 50018263 union all\n"
operator|+
literal|"  select ENAME, 50013498 as cat_id, '13' as cat_name, 0 as require_free_postage, 1 as require_15return, 1 as require_48hour,0 as require_insurance from emp where EMPNO = 20171216 and MGR = 0 and ENAME = 'Y' and SAL = 50013498 union all\n"
operator|+
literal|"  select ENAME, 350511 as cat_id, '14' as cat_name, 0 as require_free_postage, 1 as require_15return, 1 as require_48hour,0 as require_insurance from emp where EMPNO = 20171216 and MGR = 0 and ENAME = 'Y' and SAL = 350511 union all\n"
operator|+
literal|"  select ENAME, 50019790 as cat_id, '15' as cat_name, 0 as require_free_postage, 1 as require_15return, 1 as require_48hour,0 as require_insurance from emp where EMPNO = 20171216 and MGR = 0 and ENAME = 'Y' and SAL = 50019790 union all\n"
operator|+
literal|"  select ENAME, 50015382 as cat_id, '16' as cat_name, 0 as require_free_postage, 1 as require_15return, 1 as require_48hour,0 as require_insurance from emp where EMPNO = 20171216 and MGR = 0 and ENAME = 'Y' and SAL = 50015382 union all\n"
operator|+
literal|"  select ENAME, 350503 as cat_id, '17' as cat_name, 0 as require_free_postage, 1 as require_15return, 1 as require_48hour,0 as require_insurance from emp where EMPNO = 20171216 and MGR = 0 and ENAME = 'Y' and SAL = 350503 union all\n"
operator|+
literal|"  select ENAME, 350401 as cat_id, '18' as cat_name, 0 as require_free_postage, 1 as require_15return, 1 as require_48hour,0 as require_insurance from emp where EMPNO = 20171216 and MGR = 0 and ENAME = 'Y' and SAL = 350401 union all\n"
operator|+
literal|"  select ENAME, 50015560 as cat_id, '19' as cat_name, 0 as require_free_postage, 0 as require_15return, 0 as require_48hour,0 as require_insurance from emp where EMPNO = 20171216 and MGR = 0 and ENAME = 'Y' and SAL = 50015560 union all\n"
operator|+
literal|"  select ENAME, 122658003 as cat_id, '20' as cat_name, 0 as require_free_postage, 1 as require_15return, 1 as require_48hour,0 as require_insurance from emp where EMPNO = 20171216 and MGR = 0 and ENAME = 'Y' and SAL = 122658003 union all\n"
operator|+
literal|"  select ENAME, 122716008 as cat_id, '21' as cat_name, 0 as require_free_postage, 1 as require_15return, 1 as require_48hour,0 as require_insurance from emp where EMPNO = 20171216 and MGR = 0 and ENAME = 'Y' and SAL = 122716008 union all\n"
operator|+
literal|"  select ENAME, 50018406 as cat_id, '22' as cat_name, 0 as require_free_postage, 1 as require_15return, 1 as require_48hour,0 as require_insurance from emp where EMPNO = 20171216 and MGR = 0 and ENAME = 'Y' and SAL = 50018406 union all\n"
operator|+
literal|"  select ENAME, 50018407 as cat_id, '23' as cat_name, 0 as require_free_postage, 1 as require_15return, 1 as require_48hour,0 as require_insurance from emp where EMPNO = 20171216 and MGR = 0 and ENAME = 'Y' and SAL = 50018407 union all\n"
operator|+
literal|"  select ENAME, 50024678 as cat_id, '24' as cat_name, 0 as require_free_postage, 1 as require_15return, 1 as require_48hour,0 as require_insurance from emp where EMPNO = 20171216 and MGR = 0 and ENAME = 'Y' and SAL = 50024678 union all\n"
operator|+
literal|"  select ENAME, 50022290 as cat_id, '25' as cat_name, 0 as require_free_postage, 1 as require_15return, 1 as require_48hour,0 as require_insurance from emp where EMPNO = 20171216 and MGR = 0 and ENAME = 'Y' and SAL = 50022290 union all\n"
operator|+
literal|"  select ENAME, 50020072 as cat_id, '26' as cat_name, 0 as require_free_postage, 1 as require_15return, 1 as require_48hour,0 as require_insurance from emp where EMPNO = 20171216 and MGR = 0 and ENAME = 'Y' and SAL = 50020072 union all\n"
operator|+
literal|"  select ENAME, 50024679 as cat_id, '27' as cat_name, 1 as require_free_postage, 1 as require_15return, 1 as require_48hour,0 as require_insurance from emp where EMPNO = 20171216 and MGR = 0 and ENAME = 'Y' and SAL = 50024679 union all\n"
operator|+
literal|"  select ENAME, 50013326 as cat_id, '28' as cat_name, 1 as require_free_postage, 1 as require_15return, 1 as require_48hour,0 as require_insurance from emp where EMPNO = 20171216 and MGR = 0 and ENAME = 'Y' and SAL = 50013326 union all\n"
operator|+
literal|"  select ENAME, 50020032 as cat_id, '19' as cat_name, 0 as require_free_postage, 1 as require_15return, 1 as require_48hour,0 as require_insurance from emp where EMPNO = 20171216 and MGR = 0 and ENAME = 'Y' and SAL = 50020032 union all\n"
operator|+
literal|"  select ENAME, 50022273 as cat_id, '30' as cat_name, 0 as require_free_postage, 1 as require_15return, 1 as require_48hour,0 as require_insurance from emp where EMPNO = 20171216 and MGR = 0 and ENAME = 'Y' and SAL = 50022273 union all\n"
operator|+
literal|"  select ENAME, 50013511 as cat_id, '31' as cat_name, 0 as require_free_postage, 1 as require_15return, 1 as require_48hour,0 as require_insurance from emp where EMPNO = 20171216 and MGR = 0 and ENAME = 'Y' and SAL = 50013511 union all\n"
operator|+
literal|"  select ENAME, 122694006 as cat_id, '32' as cat_name, 0 as require_free_postage, 1 as require_15return, 1 as require_48hour,0 as require_insurance from emp where EMPNO = 20171216 and MGR = 0 and ENAME = 'Y' and SAL = 122694006 union all\n"
operator|+
literal|"  select ENAME, 50019940 as cat_id, '33' as cat_name, 0 as require_free_postage, 1 as require_15return, 1 as require_48hour,0 as require_insurance from emp where EMPNO = 20171216 and MGR = 0 and ENAME = 'Y' and SAL = 50019940 union all\n"
operator|+
literal|"  select ENAME, 50022288 as cat_id, '34' as cat_name, 0 as require_free_postage, 1 as require_15return, 1 as require_48hour,0 as require_insurance from emp where EMPNO = 20171216 and MGR = 0 and ENAME = 'Y' and SAL = 50022288 union all\n"
operator|+
literal|"  select ENAME, 50020069 as cat_id, '35' as cat_name, 0 as require_free_postage, 1 as require_15return, 1 as require_48hour,0 as require_insurance from emp where EMPNO = 20171216 and MGR = 0 and ENAME = 'Y' and SAL = 50020069 union all\n"
operator|+
literal|"  select ENAME, 50021800 as cat_id, '36' as cat_name, 0 as require_free_postage, 1 as require_15return, 1 as require_48hour,0 as require_insurance from emp where EMPNO = 20171216 and MGR = 0 and ENAME = 'Y' and SAL = 50021800 union all\n"
operator|+
literal|"  select ENAME, 50024684 as cat_id, '37' as cat_name, 1 as require_free_postage, 1 as require_15return, 1 as require_48hour,0 as require_insurance from emp where EMPNO = 20171216 and MGR = 0 and ENAME = 'Y' and SAL = 50024684 union all\n"
operator|+
literal|"  select ENAME, 50024676 as cat_id, '38' as cat_name, 1 as require_free_postage, 1 as require_15return, 1 as require_48hour,0 as require_insurance from emp where EMPNO = 20171216 and MGR = 0 and ENAME = 'Y' and SAL = 50024676 union all\n"
operator|+
literal|"  select ENAME, 50020070 as cat_id, '39' as cat_name, 0 as require_free_postage, 1 as require_15return, 1 as require_48hour,0 as require_insurance from emp where EMPNO = 20171216 and MGR = 0 and ENAME = 'Y' and SAL = 50020070 union all\n"
operator|+
literal|"  select ENAME, 50020058 as cat_id, '40' as cat_name, 0 as require_free_postage, 1 as require_15return, 1 as require_48hour,0 as require_insurance from emp where EMPNO = 20171216 and MGR = 0 and ENAME = 'Y' and SAL = 50020058 union all\n"
operator|+
literal|"  select ENAME, 50019938 as cat_id, '41' as cat_name, 0 as require_free_postage, 1 as require_15return, 1 as require_48hour,0 as require_insurance from emp where EMPNO = 20171216 and MGR = 0 and ENAME = 'Y' and SAL = 50019938 union all\n"
operator|+
literal|"  select ENAME, 122686009 as cat_id, '42' as cat_name, 0 as require_free_postage, 1 as require_15return, 1 as require_48hour,0 as require_insurance from emp where EMPNO = 20171216 and MGR = 0 and ENAME = 'Y' and SAL = 122686009 union all\n"
operator|+
literal|"  select ENAME, 50022286 as cat_id, '43' as cat_name, 0 as require_free_postage, 1 as require_15return, 1 as require_48hour,0 as require_insurance from emp where EMPNO = 20171216 and MGR = 0 and ENAME = 'Y' and SAL = 50022286 union all\n"
operator|+
literal|"  select ENAME, 122692007 as cat_id, '44' as cat_name, 0 as require_free_postage, 1 as require_15return, 1 as require_48hour,0 as require_insurance from emp where EMPNO = 20171216 and MGR = 0 and ENAME = 'Y' and SAL = 122692007 union all\n"
operator|+
literal|"  select ENAME, 50020059 as cat_id, '45' as cat_name, 0 as require_free_postage, 1 as require_15return, 1 as require_48hour,0 as require_insurance from emp where EMPNO = 20171216 and MGR = 0 and ENAME = 'Y' and SAL = 50020059 union all\n"
operator|+
literal|"  select ENAME, 50006050 as cat_id, '45' as cat_name, 0 as require_free_postage, 1 as require_15return, 1 as require_48hour,0 as require_insurance from emp where EMPNO = 20171216 and MGR = 0 and ENAME = 'Y' and SAL = 50006050 union all\n"
operator|+
literal|"  select ENAME, 122718006 as cat_id, '47' as cat_name, 0 as require_free_postage, 1 as require_15return, 1 as require_48hour,0 as require_insurance from emp where EMPNO = 20171216 and MGR = 0 and ENAME = 'Y' and SAL = 122718006 union all\n"
operator|+
literal|"  select ENAME, 50022652 as cat_id, '48' as cat_name, 1 as require_free_postage, 1 as require_15return, 1 as require_48hour,0 as require_insurance from emp where EMPNO = 20171216 and MGR = 0 and ENAME = 'Y' and SAL = 50022652 union all\n"
operator|+
literal|"  select ENAME, 50024685 as cat_id, '49' as cat_name, 1 as require_free_postage, 1 as require_15return, 1 as require_48hour,0 as require_insurance from emp where EMPNO = 20171216 and MGR = 0 and ENAME = 'Y' and SAL = 50024685 union all\n"
operator|+
literal|"  select ENAME, 50020104 as cat_id, '50' as cat_name, 1 as require_free_postage, 1 as require_15return, 1 as require_48hour,0 as require_insurance from emp where EMPNO = 20171216 and MGR = 0 and ENAME = 'Y' and SAL = 50020104 union all\n"
operator|+
literal|"  select ENAME, 50013500 as cat_id, '51' as cat_name, 1 as require_free_postage, 1 as require_15return, 1 as require_48hour,0 as require_insurance from emp where EMPNO = 20171216 and MGR = 0 and ENAME = 'Y' and SAL = 50013500 union all\n"
operator|+
literal|"  select ENAME, 50003558 as cat_id, '52' as cat_name, 0 as require_free_postage, 1 as require_15return, 1 as require_48hour,0 as require_insurance from emp where EMPNO = 20171216 and MGR = 0 and ENAME = 'Y' and SAL = 50003558 union all\n"
operator|+
literal|"  select ENAME, 50020061 as cat_id, '53' as cat_name, 0 as require_free_postage, 1 as require_15return, 1 as require_48hour,0 as require_insurance from emp where EMPNO = 20171216 and MGR = 0 and ENAME = 'Y' and SAL = 50020061 union all\n"
operator|+
literal|"  select ENAME, 122656012 as cat_id, '54' as cat_name, 0 as require_free_postage, 1 as require_15return, 1 as require_48hour,0 as require_insurance from emp where EMPNO = 20171216 and MGR = 0 and ENAME = 'Y' and SAL = 122656012 union all\n"
operator|+
literal|"  select ENAME, 50024812 as cat_id, '55' as cat_name, 0 as require_free_postage, 1 as require_15return, 1 as require_48hour,0 as require_insurance from emp where EMPNO = 20171216 and MGR = 0 and ENAME = 'Y' and SAL = 50024812 union all\n"
operator|+
literal|"  select ENAME, 50022287 as cat_id, '56' as cat_name, 0 as require_free_postage, 1 as require_15return, 1 as require_48hour,0 as require_insurance from emp where EMPNO = 20171216 and MGR = 0 and ENAME = 'Y' and SAL = 50022287 union all\n"
operator|+
literal|"  select ENAME, 50020107 as cat_id, '57' as cat_name, 0 as require_free_postage, 1 as require_15return, 1 as require_48hour,0 as require_insurance from emp where EMPNO = 20171216 and MGR = 0 and ENAME = 'Y' and SAL = 50020107 union all\n"
operator|+
literal|"  select ENAME, 50019842 as cat_id, '58' as cat_name, 0 as require_free_postage, 1 as require_15return, 1 as require_48hour,0 as require_insurance from emp where EMPNO = 20171216 and MGR = 0 and ENAME = 'Y' and SAL = 50019842 union all\n"
operator|+
literal|"  select ENAME, 50020106 as cat_id, '59' as cat_name, 0 as require_free_postage, 1 as require_15return, 1 as require_48hour,0 as require_insurance from emp where EMPNO = 20171216 and MGR = 0 and ENAME = 'Y' and SAL = 50020106 union all\n"
operator|+
literal|"  select ENAME, 50020071 as cat_id, '60' as cat_name, 0 as require_free_postage, 1 as require_15return, 1 as require_48hour,0 as require_insurance from emp where EMPNO = 20171216 and MGR = 0 and ENAME = 'Y' and SAL = 50020071 union all\n"
operator|+
literal|"  select ENAME, 50019939 as cat_id, '61' as cat_name, 0 as require_free_postage, 1 as require_15return, 1 as require_48hour,0 as require_insurance from emp where EMPNO = 20171216 and MGR = 0 and ENAME = 'Y' and SAL = 50019939 union all\n"
operator|+
literal|"  select ENAME, 50020034 as cat_id, '62' as cat_name, 1 as require_free_postage, 1 as require_15return, 1 as require_48hour,0 as require_insurance from emp where EMPNO = 20171216 and MGR = 0 and ENAME = 'Y' and SAL = 50020034 union all\n"
operator|+
literal|"  select ENAME, 50020025 as cat_id, '63' as cat_name, 1 as require_free_postage, 1 as require_15return, 1 as require_48hour,0 as require_insurance from emp where EMPNO = 20171216 and MGR = 0 and ENAME = 'Y' and SAL = 50020025 union all\n"
operator|+
literal|"  select ENAME, 50022293 as cat_id, '64' as cat_name, 0 as require_free_postage, 1 as require_15return, 1 as require_48hour,0 as require_insurance from emp where EMPNO = 20171216 and MGR = 0 and ENAME = 'Y' and SAL = 50022293 union all\n"
operator|+
literal|"  select ENAME, 50022279 as cat_id, '65' as cat_name, 0 as require_free_postage, 1 as require_15return, 1 as require_48hour,0 as require_insurance from emp where EMPNO = 20171216 and MGR = 0 and ENAME = 'Y' and SAL = 50022279 union all\n"
operator|+
literal|"  select ENAME, 50013818 as cat_id, '66' as cat_name, 0 as require_free_postage, 1 as require_15return, 1 as require_48hour,0 as require_insurance from emp where EMPNO = 20171216 and MGR = 0 and ENAME = 'Y' and SAL = 50013818 union all\n"
operator|+
literal|"  select ENAME, 50020060 as cat_id, '67' as cat_name, 0 as require_free_postage, 1 as require_15return, 1 as require_48hour,0 as require_insurance from emp where EMPNO = 20171216 and MGR = 0 and ENAME = 'Y' and SAL = 50020060 union all\n"
operator|+
literal|"  select ENAME, 50020062 as cat_id, '68' as cat_name, 0 as require_free_postage, 1 as require_15return, 1 as require_48hour,0 as require_insurance from emp where EMPNO = 20171216 and MGR = 0 and ENAME = 'Y' and SAL = 50020062 union all\n"
operator|+
literal|"  select ENAME, 50022276 as cat_id, '69' as cat_name, 0 as require_free_postage, 1 as require_15return, 1 as require_48hour,0 as require_insurance from emp where EMPNO = 20171216 and MGR = 0 and ENAME = 'Y' and SAL = 50022276 union all\n"
operator|+
literal|"  select ENAME, 50022280 as cat_id, '70' as cat_name, 0 as require_free_postage, 1 as require_15return, 1 as require_48hour,0 as require_insurance from emp where EMPNO = 20171216 and MGR = 0 and ENAME = 'Y' and SAL = 50022280 union all\n"
operator|+
literal|"  select ENAME, 50020619 as cat_id, '71' as cat_name, 1 as require_free_postage, 1 as require_15return, 1 as require_48hour,0 as require_insurance from emp where EMPNO = 20171216 and MGR = 0 and ENAME = 'Y' and SAL = 50020619 union all\n"
operator|+
literal|"  select ENAME, 50013347 as cat_id, '72' as cat_name, 1 as require_free_postage, 1 as require_15return, 1 as require_48hour,0 as require_insurance from emp where EMPNO = 20171216 and MGR = 0 and ENAME = 'Y' and SAL = 50013347 union all\n"
operator|+
literal|"  select ENAME, 50008698 as cat_id, '73' as cat_name, 1 as require_free_postage, 1 as require_15return, 1 as require_48hour,0 as require_insurance from emp where EMPNO = 20171216 and MGR = 0 and ENAME = 'Y' and SAL = 50008698 union all\n"
operator|+
literal|"  select ENAME, 50013334 as cat_id, '74' as cat_name, 1 as require_free_postage, 1 as require_15return, 1 as require_48hour,0 as require_insurance from emp where EMPNO = 20171216 and MGR = 0 and ENAME = 'Y' and SAL = 50013334 union all\n"
operator|+
literal|"  select ENAME, 50024810 as cat_id, '75' as cat_name, 0 as require_free_postage, 1 as require_15return, 1 as require_48hour,0 as require_insurance from emp where EMPNO = 20171216 and MGR = 0 and ENAME = 'Y' and SAL = 50024810 union all\n"
operator|+
literal|"  select ENAME, 50019936 as cat_id, '76' as cat_name, 1 as require_free_postage, 1 as require_15return, 1 as require_48hour,0 as require_insurance from emp where EMPNO = 20171216 and MGR = 0 and ENAME = 'Y' and SAL = 50019936 union all\n"
operator|+
literal|"  select ENAME, 50024813 as cat_id, '77' as cat_name, 0 as require_free_postage, 1 as require_15return, 1 as require_48hour,0 as require_insurance from emp where EMPNO = 20171216 and MGR = 0 and ENAME = 'Y' and SAL = 50024813 union all\n"
operator|+
literal|"  select ENAME, 50020959 as cat_id, '78' as cat_name, 0 as require_free_postage, 1 as require_15return, 1 as require_48hour,0 as require_insurance from emp where EMPNO = 20171216 and MGR = 0 and ENAME = 'Y' and SAL = 50020959 union all\n"
operator|+
literal|"  select ENAME, 124474002 as cat_id, '79' as cat_name, 0 as require_free_postage, 1 as require_15return, 1 as require_48hour,0 as require_insurance from emp where EMPNO = 20171216 and MGR = 0 and ENAME = 'Y' and SAL = 124474002 union all\n"
operator|+
literal|"  select ENAME, 50019853 as cat_id, '80' as cat_name, 0 as require_free_postage, 1 as require_15return, 1 as require_48hour,0 as require_insurance from emp where EMPNO = 20171216 and MGR = 0 and ENAME = 'Y' and SAL = 50019853 union all\n"
operator|+
literal|"  select ENAME, 50019837 as cat_id, '81' as cat_name, 0 as require_free_postage, 1 as require_15return, 1 as require_48hour,0 as require_insurance from emp where EMPNO = 20171216 and MGR = 0 and ENAME = 'Y' and SAL = 50019837 union all\n"
operator|+
literal|"  select ENAME, 50022289 as cat_id, '82' as cat_name, 0 as require_free_postage, 1 as require_15return, 1 as require_48hour,0 as require_insurance from emp where EMPNO = 20171216 and MGR = 0 and ENAME = 'Y' and SAL = 50022289 union all\n"
operator|+
literal|"  select ENAME, 50022278 as cat_id, '83' as cat_name, 0 as require_free_postage, 1 as require_15return, 1 as require_48hour,0 as require_insurance from emp where EMPNO = 20171216 and MGR = 0 and ENAME = 'Y' and SAL = 50022278 union all\n"
operator|+
literal|"  select ENAME, 50024690 as cat_id, '84' as cat_name, 0 as require_free_postage, 1 as require_15return, 1 as require_48hour,0 as require_insurance from emp where EMPNO = 20171216 and MGR = 0 and ENAME = 'Y' and SAL = 50024690 union all\n"
operator|+
literal|"  select ENAME, 50592002 as cat_id, '85' as cat_name, 0 as require_free_postage, 1 as require_15return, 1 as require_48hour,0 as require_insurance from emp where EMPNO = 20171216 and MGR = 0 and ENAME = 'Y' and SAL = 50592002 union all\n"
operator|+
literal|"  select ENAME, 50013342 as cat_id, '86' as cat_name, 0 as require_free_postage, 1 as require_15return, 1 as require_48hour,0 as require_insurance from emp where EMPNO = 20171216 and MGR = 0 and ENAME = 'Y' and SAL = 50013342 union all\n"
operator|+
literal|"  select ENAME, 50022296 as cat_id, '87' as cat_name, 0 as require_free_postage, 1 as require_15return, 1 as require_48hour,0 as require_insurance from emp where EMPNO = 20171216 and MGR = 0 and ENAME = 'Y' and SAL = 50022296 union all\n"
operator|+
literal|"  select ENAME, 123456001 as cat_id, '88' as cat_name, 0 as require_free_postage, 1 as require_15return, 1 as require_48hour,0 as require_insurance from emp where EMPNO = 20171216 and MGR = 0 and ENAME = 'Y' and SAL = 123456001 union all\n"
operator|+
literal|"  select ENAME, 50022298 as cat_id, '89' as cat_name, 0 as require_free_postage, 1 as require_15return, 1 as require_48hour,0 as require_insurance from emp where EMPNO = 20171216 and MGR = 0 and ENAME = 'Y' and SAL = 50022298 union all\n"
operator|+
literal|"  select ENAME, 50022274 as cat_id, '90' as cat_name, 0 as require_free_postage, 1 as require_15return, 1 as require_48hour,0 as require_insurance from emp where EMPNO = 20171216 and MGR = 0 and ENAME = 'Y' and SAL = 50022274 union all\n"
operator|+
literal|"  select ENAME, 50006046 as cat_id, '91' as cat_name, 0 as require_free_postage, 1 as require_15return, 1 as require_48hour,0 as require_insurance from emp where EMPNO = 20171216 and MGR = 0 and ENAME = 'Y' and SAL = 50006046 union all\n"
operator|+
literal|"  select ENAME, 50020676 as cat_id, '92' as cat_name, 1 as require_free_postage, 0 as require_15return, 0 as require_48hour,1 as require_insurance from emp where EMPNO = 20171216 and MGR = 0 and ENAME = 'Y' and SAL = 50020676 union all\n"
operator|+
literal|"  select ENAME, 50020678 as cat_id, '93' as cat_name, 1 as require_free_postage, 0 as require_15return, 0 as require_48hour,1 as require_insurance from emp where EMPNO = 20171216 and MGR = 0 and ENAME = 'Y' and SAL = 50020678 union all\n"
operator|+
literal|"  select ENAME, 121398012 as cat_id, '94' as cat_name, 1 as require_free_postage, 0 as require_15return, 0 as require_48hour,1 as require_insurance from emp where EMPNO = 20171216 and MGR = 0 and ENAME = 'Y' and SAL = 121398012 union all\n"
operator|+
literal|"  select ENAME, 50020720 as cat_id, '95' as cat_name, 1 as require_free_postage, 0 as require_15return, 0 as require_48hour,1 as require_insurance from emp where EMPNO = 20171216 and MGR = 0 and ENAME = 'Y' and SAL = 50020720 union all\n"
operator|+
literal|"  select ENAME, 50001714 as cat_id, '96' as cat_name, 0 as require_free_postage, 1 as require_15return, 1 as require_48hour,0 as require_insurance from emp where EMPNO = 20171216 and MGR = 0 and ENAME = 'Y' and SAL = 50001714 union all\n"
operator|+
literal|"  select ENAME, 50008905 as cat_id, '97' as cat_name, 1 as require_free_postage, 0 as require_15return, 1 as require_48hour,1 as require_insurance from emp where EMPNO = 20171216 and MGR = 0 and ENAME = 'Y' and SAL = 50008905 union all\n"
operator|+
literal|"  select ENAME, 50008904 as cat_id, '98' as cat_name, 1 as require_free_postage, 0 as require_15return, 1 as require_48hour,1 as require_insurance from emp where EMPNO = 20171216 and MGR = 0 and ENAME = 'Y' and SAL = 50008904 union all\n"
operator|+
literal|"  select ENAME, 50022358 as cat_id, '99' as cat_name, 0 as require_free_postage, 0 as require_15return, 0 as require_48hour,0 as require_insurance from emp where EMPNO = 20171216 and MGR = 0 and ENAME = 'Y' and SAL = 50022358 union all\n"
operator|+
literal|"  select ENAME, 50022371 as cat_id, '100' as cat_name, 0 as require_free_postage, 0 as require_15return, 0 as require_48hour,0 as require_insurance from emp where EMPNO = 20171216 and MGR = 0 and ENAME = 'Y' and SAL = 50022371\n"
operator|+
literal|") a"
decl_stmt|;
comment|//~ Methods ----------------------------------------------------------------
specifier|protected
name|DiffRepository
name|getDiffRepos
parameter_list|()
block|{
return|return
name|DiffRepository
operator|.
name|lookup
argument_list|(
name|HepPlannerTest
operator|.
name|class
argument_list|)
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRuleClass
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Verify that an entire class of rules can be applied.
name|HepProgramBuilder
name|programBuilder
init|=
name|HepProgram
operator|.
name|builder
argument_list|()
decl_stmt|;
name|programBuilder
operator|.
name|addRuleClass
argument_list|(
name|CoerceInputsRule
operator|.
name|class
argument_list|)
expr_stmt|;
name|HepPlanner
name|planner
init|=
operator|new
name|HepPlanner
argument_list|(
name|programBuilder
operator|.
name|build
argument_list|()
argument_list|)
decl_stmt|;
name|planner
operator|.
name|addRule
argument_list|(
operator|new
name|CoerceInputsRule
argument_list|(
name|LogicalUnion
operator|.
name|class
argument_list|,
literal|false
argument_list|,
name|RelFactories
operator|.
name|LOGICAL_BUILDER
argument_list|)
argument_list|)
expr_stmt|;
name|planner
operator|.
name|addRule
argument_list|(
operator|new
name|CoerceInputsRule
argument_list|(
name|LogicalIntersect
operator|.
name|class
argument_list|,
literal|false
argument_list|,
name|RelFactories
operator|.
name|LOGICAL_BUILDER
argument_list|)
argument_list|)
expr_stmt|;
name|checkPlanning
argument_list|(
name|planner
argument_list|,
literal|"(select name from dept union select ename from emp)"
operator|+
literal|" intersect (select fname from customer.contact)"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRuleDescription
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Verify that a rule can be applied via its description.
name|HepProgramBuilder
name|programBuilder
init|=
name|HepProgram
operator|.
name|builder
argument_list|()
decl_stmt|;
name|programBuilder
operator|.
name|addRuleByDescription
argument_list|(
literal|"FilterToCalcRule"
argument_list|)
expr_stmt|;
name|HepPlanner
name|planner
init|=
operator|new
name|HepPlanner
argument_list|(
name|programBuilder
operator|.
name|build
argument_list|()
argument_list|)
decl_stmt|;
name|planner
operator|.
name|addRule
argument_list|(
name|FilterToCalcRule
operator|.
name|INSTANCE
argument_list|)
expr_stmt|;
name|checkPlanning
argument_list|(
name|planner
argument_list|,
literal|"select name from sales.dept where deptno=12"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMatchLimitOneTopDown
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Verify that only the top union gets rewritten.
name|HepProgramBuilder
name|programBuilder
init|=
name|HepProgram
operator|.
name|builder
argument_list|()
decl_stmt|;
name|programBuilder
operator|.
name|addMatchOrder
argument_list|(
name|HepMatchOrder
operator|.
name|TOP_DOWN
argument_list|)
expr_stmt|;
name|programBuilder
operator|.
name|addMatchLimit
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|programBuilder
operator|.
name|addRuleInstance
argument_list|(
name|UnionToDistinctRule
operator|.
name|INSTANCE
argument_list|)
expr_stmt|;
name|checkPlanning
argument_list|(
name|programBuilder
operator|.
name|build
argument_list|()
argument_list|,
name|UNION_TREE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMatchLimitOneBottomUp
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Verify that only the bottom union gets rewritten.
name|HepProgramBuilder
name|programBuilder
init|=
name|HepProgram
operator|.
name|builder
argument_list|()
decl_stmt|;
name|programBuilder
operator|.
name|addMatchLimit
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|programBuilder
operator|.
name|addMatchOrder
argument_list|(
name|HepMatchOrder
operator|.
name|BOTTOM_UP
argument_list|)
expr_stmt|;
name|programBuilder
operator|.
name|addRuleInstance
argument_list|(
name|UnionToDistinctRule
operator|.
name|INSTANCE
argument_list|)
expr_stmt|;
name|checkPlanning
argument_list|(
name|programBuilder
operator|.
name|build
argument_list|()
argument_list|,
name|UNION_TREE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMatchUntilFixpoint
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Verify that both unions get rewritten.
name|HepProgramBuilder
name|programBuilder
init|=
name|HepProgram
operator|.
name|builder
argument_list|()
decl_stmt|;
name|programBuilder
operator|.
name|addMatchLimit
argument_list|(
name|HepProgram
operator|.
name|MATCH_UNTIL_FIXPOINT
argument_list|)
expr_stmt|;
name|programBuilder
operator|.
name|addRuleInstance
argument_list|(
name|UnionToDistinctRule
operator|.
name|INSTANCE
argument_list|)
expr_stmt|;
name|checkPlanning
argument_list|(
name|programBuilder
operator|.
name|build
argument_list|()
argument_list|,
name|UNION_TREE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testReplaceCommonSubexpression
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Note that here it may look like the rule is firing
comment|// twice, but actually it's only firing once on the
comment|// common sub-expression.  The purpose of this test
comment|// is to make sure the planner can deal with
comment|// rewriting something used as a common sub-expression
comment|// twice by the same parent (the join in this case).
name|checkPlanning
argument_list|(
name|ProjectRemoveRule
operator|.
name|INSTANCE
argument_list|,
literal|"select d1.deptno from (select * from dept) d1,"
operator|+
literal|" (select * from dept) d2"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSubprogram
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Verify that subprogram gets re-executed until fixpoint.
comment|// In this case, the first time through we limit it to generate
comment|// only one calc; the second time through it will generate
comment|// a second calc, and then merge them.
name|HepProgramBuilder
name|subprogramBuilder
init|=
name|HepProgram
operator|.
name|builder
argument_list|()
decl_stmt|;
name|subprogramBuilder
operator|.
name|addMatchOrder
argument_list|(
name|HepMatchOrder
operator|.
name|TOP_DOWN
argument_list|)
expr_stmt|;
name|subprogramBuilder
operator|.
name|addMatchLimit
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|subprogramBuilder
operator|.
name|addRuleInstance
argument_list|(
name|ProjectToCalcRule
operator|.
name|INSTANCE
argument_list|)
expr_stmt|;
name|subprogramBuilder
operator|.
name|addRuleInstance
argument_list|(
name|CalcMergeRule
operator|.
name|INSTANCE
argument_list|)
expr_stmt|;
name|HepProgramBuilder
name|programBuilder
init|=
name|HepProgram
operator|.
name|builder
argument_list|()
decl_stmt|;
name|programBuilder
operator|.
name|addSubprogram
argument_list|(
name|subprogramBuilder
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
name|checkPlanning
argument_list|(
name|programBuilder
operator|.
name|build
argument_list|()
argument_list|,
literal|"select upper(ename) from (select lower(ename) as ename from emp)"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGroup
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Verify simultaneous application of a group of rules.
comment|// Intentionally add them in the wrong order to make sure
comment|// that order doesn't matter within the group.
name|HepProgramBuilder
name|programBuilder
init|=
name|HepProgram
operator|.
name|builder
argument_list|()
decl_stmt|;
name|programBuilder
operator|.
name|addGroupBegin
argument_list|()
expr_stmt|;
name|programBuilder
operator|.
name|addRuleInstance
argument_list|(
name|CalcMergeRule
operator|.
name|INSTANCE
argument_list|)
expr_stmt|;
name|programBuilder
operator|.
name|addRuleInstance
argument_list|(
name|ProjectToCalcRule
operator|.
name|INSTANCE
argument_list|)
expr_stmt|;
name|programBuilder
operator|.
name|addRuleInstance
argument_list|(
name|FilterToCalcRule
operator|.
name|INSTANCE
argument_list|)
expr_stmt|;
name|programBuilder
operator|.
name|addGroupEnd
argument_list|()
expr_stmt|;
name|checkPlanning
argument_list|(
name|programBuilder
operator|.
name|build
argument_list|()
argument_list|,
literal|"select upper(name) from dept where deptno=20"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGC
parameter_list|()
throws|throws
name|Exception
block|{
name|HepProgramBuilder
name|programBuilder
init|=
name|HepProgram
operator|.
name|builder
argument_list|()
decl_stmt|;
name|programBuilder
operator|.
name|addMatchOrder
argument_list|(
name|HepMatchOrder
operator|.
name|TOP_DOWN
argument_list|)
expr_stmt|;
name|programBuilder
operator|.
name|addRuleInstance
argument_list|(
name|CalcMergeRule
operator|.
name|INSTANCE
argument_list|)
expr_stmt|;
name|programBuilder
operator|.
name|addRuleInstance
argument_list|(
name|ProjectToCalcRule
operator|.
name|INSTANCE
argument_list|)
expr_stmt|;
name|programBuilder
operator|.
name|addRuleInstance
argument_list|(
name|FilterToCalcRule
operator|.
name|INSTANCE
argument_list|)
expr_stmt|;
name|HepPlanner
name|planner
init|=
operator|new
name|HepPlanner
argument_list|(
name|programBuilder
operator|.
name|build
argument_list|()
argument_list|)
decl_stmt|;
name|planner
operator|.
name|setRoot
argument_list|(
name|tester
operator|.
name|convertSqlToRel
argument_list|(
literal|"select upper(name) from dept where deptno=20"
argument_list|)
operator|.
name|rel
argument_list|)
expr_stmt|;
name|planner
operator|.
name|findBestExp
argument_list|()
expr_stmt|;
comment|// Reuse of HepPlanner (should trigger GC).
name|planner
operator|.
name|setRoot
argument_list|(
name|tester
operator|.
name|convertSqlToRel
argument_list|(
literal|"select upper(name) from dept where deptno=20"
argument_list|)
operator|.
name|rel
argument_list|)
expr_stmt|;
name|planner
operator|.
name|findBestExp
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRuleApplyCount
parameter_list|()
block|{
specifier|final
name|long
name|applyTimes1
init|=
name|checkRuleApplyCount
argument_list|(
name|HepMatchOrder
operator|.
name|ARBITRARY
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|applyTimes1
argument_list|,
name|is
argument_list|(
literal|5451L
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|long
name|applyTimes2
init|=
name|checkRuleApplyCount
argument_list|(
name|HepMatchOrder
operator|.
name|DEPTH_FIRST
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|applyTimes2
argument_list|,
name|is
argument_list|(
literal|403L
argument_list|)
argument_list|)
expr_stmt|;
comment|// DEPTH_FIRST has 10x fewer matches than ARBITRARY
name|assertThat
argument_list|(
name|applyTimes1
operator|>
name|applyTimes2
operator|*
literal|10
argument_list|,
name|is
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|long
name|checkRuleApplyCount
parameter_list|(
name|HepMatchOrder
name|matchOrder
parameter_list|)
block|{
specifier|final
name|HepProgramBuilder
name|programBuilder
init|=
name|HepProgram
operator|.
name|builder
argument_list|()
decl_stmt|;
name|programBuilder
operator|.
name|addMatchOrder
argument_list|(
name|matchOrder
argument_list|)
expr_stmt|;
name|programBuilder
operator|.
name|addRuleInstance
argument_list|(
name|ReduceExpressionsRule
operator|.
name|FILTER_INSTANCE
argument_list|)
expr_stmt|;
name|programBuilder
operator|.
name|addRuleInstance
argument_list|(
name|ReduceExpressionsRule
operator|.
name|PROJECT_INSTANCE
argument_list|)
expr_stmt|;
specifier|final
name|HepTestListener
name|listener
init|=
operator|new
name|HepTestListener
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|HepPlanner
name|planner
init|=
operator|new
name|HepPlanner
argument_list|(
name|programBuilder
operator|.
name|build
argument_list|()
argument_list|)
decl_stmt|;
name|planner
operator|.
name|addListener
argument_list|(
name|listener
argument_list|)
expr_stmt|;
name|planner
operator|.
name|setRoot
argument_list|(
name|tester
operator|.
name|convertSqlToRel
argument_list|(
name|COMPLEX_UNION_TREE
argument_list|)
operator|.
name|rel
argument_list|)
expr_stmt|;
name|planner
operator|.
name|findBestExp
argument_list|()
expr_stmt|;
return|return
name|listener
operator|.
name|getApplyTimes
argument_list|()
return|;
block|}
comment|/** Listener for HepPlannerTest; counts how many times rules fire. */
specifier|private
class|class
name|HepTestListener
implements|implements
name|RelOptListener
block|{
specifier|private
name|long
name|applyTimes
decl_stmt|;
name|HepTestListener
parameter_list|(
name|long
name|applyTimes
parameter_list|)
block|{
name|this
operator|.
name|applyTimes
operator|=
name|applyTimes
expr_stmt|;
block|}
name|long
name|getApplyTimes
parameter_list|()
block|{
return|return
name|applyTimes
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|relEquivalenceFound
parameter_list|(
name|RelEquivalenceEvent
name|event
parameter_list|)
block|{
block|}
annotation|@
name|Override
specifier|public
name|void
name|ruleAttempted
parameter_list|(
name|RuleAttemptedEvent
name|event
parameter_list|)
block|{
if|if
condition|(
name|event
operator|.
name|isBefore
argument_list|()
condition|)
block|{
operator|++
name|applyTimes
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|ruleProductionSucceeded
parameter_list|(
name|RuleProductionEvent
name|event
parameter_list|)
block|{
block|}
annotation|@
name|Override
specifier|public
name|void
name|relDiscarded
parameter_list|(
name|RelDiscardedEvent
name|event
parameter_list|)
block|{
block|}
annotation|@
name|Override
specifier|public
name|void
name|relChosen
parameter_list|(
name|RelChosenEvent
name|event
parameter_list|)
block|{
block|}
block|}
block|}
end_class

begin_comment
comment|// End HepPlannerTest.java
end_comment

end_unit

