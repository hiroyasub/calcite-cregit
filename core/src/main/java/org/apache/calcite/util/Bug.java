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
name|util
package|;
end_package

begin_comment
comment|/**  * Holder for a list of constants describing which bugs which have not been  * fixed.  *  *<p>You can use these constants to control the flow of your code. For example,  * suppose that bug CALCITE-123 causes the "INSERT" statement to return an  * incorrect row-count, and you want to disable unit tests. You might use the  * constant in your code as follows:  *  *<blockquote><pre>{@code  *   Statement stmt = connection.createStatement();  *   int rowCount =  *       stmt.execute("INSERT INTO FemaleEmps\n"  *           + "SELECT * FROM Emps WHERE gender = 'F'");  *   if (Bug.CALCITE_123_FIXED) {  *      assertEquals(rowCount, 5);  *   }  * }</pre></blockquote>  *  *<p>The usage of the constant is a convenient way to identify the impact of  * the bug. When someone fixes the bug, they will remove the constant and all  * usages of it. Also, the constant helps track the propagation of the fix: as  * the fix is integrated into other branches, the constant will be removed from  * those branches.</p>  *  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|Bug
block|{
comment|//~ Static fields/initializers ---------------------------------------------
comment|// -----------------------------------------------------------------------
comment|// Developers should create new fields here, in their own section. This
comment|// will make merge conflicts much less likely than if everyone is
comment|// appending.
specifier|public
specifier|static
specifier|final
name|boolean
name|DT239_FIXED
init|=
literal|false
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|boolean
name|DT785_FIXED
init|=
literal|false
decl_stmt|;
comment|// jhyde
comment|/**    * Whether<a href="http://issues.eigenbase.org/browse/FNL-3">issue    * Fnl-3</a> is fixed.    */
specifier|public
specifier|static
specifier|final
name|boolean
name|FNL3_FIXED
init|=
literal|false
decl_stmt|;
comment|/**    * Whether<a href="http://issues.eigenbase.org/browse/FRG-327">issue    * FRG-327: AssertionError while translating IN list that contains null</a>    * is fixed.    */
specifier|public
specifier|static
specifier|final
name|boolean
name|FRG327_FIXED
init|=
literal|false
decl_stmt|;
comment|/**    * Whether<a href="http://issues.eigenbase.org/browse/FRG-377">issue    * FRG-377: Regular character set identifiers defined in SQL:2008 spec like    * :ALPHA:, * :UPPER:, :LOWER:, ... etc. are not yet implemented in    * SIMILAR TO expressions.</a> is fixed.    */
specifier|public
specifier|static
specifier|final
name|boolean
name|FRG377_FIXED
init|=
literal|false
decl_stmt|;
comment|/**    * Whether dtbug1684 "CURRENT_DATE not implemented in fennel calc" is fixed.    */
specifier|public
specifier|static
specifier|final
name|boolean
name|DT1684_FIXED
init|=
literal|false
decl_stmt|;
comment|/**    * Whether<a href="http://issues.eigenbase.org/browse/FNL-25">issue    * FNL-25</a> is fixed. (also filed as dtbug 153)    */
specifier|public
specifier|static
specifier|final
name|boolean
name|FNL25_FIXED
init|=
literal|false
decl_stmt|;
comment|/**    * Whether<a href="http://issues.eigenbase.org/browse/FRG-73">issue FRG-73:    * miscellaneous bugs with nested comments</a> is fixed.    */
specifier|public
specifier|static
specifier|final
name|boolean
name|FRG73_FIXED
init|=
literal|false
decl_stmt|;
comment|/**    * Whether<a href="http://issues.eigenbase.org/browse/FRG-78">issue FRG-78:    * collation clause should be on expression instead of identifier</a> is    * fixed.    */
specifier|public
specifier|static
specifier|final
name|boolean
name|FRG78_FIXED
init|=
literal|false
decl_stmt|;
comment|/**    * Whether<a href="http://issues.eigenbase.org/browse/FRG-189">issue    * FRG-189: FarragoAutoVmOperatorTest.testSelect fails</a> is fixed.    */
specifier|public
specifier|static
specifier|final
name|boolean
name|FRG189_FIXED
init|=
literal|false
decl_stmt|;
comment|/**    * Whether<a href="http://issues.eigenbase.org/browse/FRG-254">issue    * FRG-254: environment-dependent failure for    * SqlOperatorTest.testPrefixPlusOperator</a> is fixed.    */
specifier|public
specifier|static
specifier|final
name|boolean
name|FRG254_FIXED
init|=
literal|false
decl_stmt|;
comment|/**    * Whether<a href="http://issues.eigenbase.org/browse/FRG-282">issue    * FRG-282: Support precision in TIME and TIMESTAMP data types</a> is fixed.    */
specifier|public
specifier|static
specifier|final
name|boolean
name|FRG282_FIXED
init|=
literal|false
decl_stmt|;
comment|/**    * Whether<a href="http://issues.eigenbase.org/browse/FRG-296">issue    * FRG-296: SUBSTRING(string FROM regexp FOR regexp)</a> is fixed.    */
specifier|public
specifier|static
specifier|final
name|boolean
name|FRG296_FIXED
init|=
literal|false
decl_stmt|;
comment|/**    * Whether<a href="http://issues.eigenbase.org/browse/FRG-375">issue    * FRG-375: The expression VALUES ('cd' SIMILAR TO '[a-e^c]d') returns TRUE.    * It should return FALSE.</a> is fixed.    */
specifier|public
specifier|static
specifier|final
name|boolean
name|FRG375_FIXED
init|=
literal|false
decl_stmt|;
comment|/** Whether    *<a href="https://issues.apache.org/jira/browse/CALCITE-194">[CALCITE-194]    * Array items in MongoDB adapter</a> is fixed. */
specifier|public
specifier|static
specifier|final
name|boolean
name|CALCITE_194_FIXED
init|=
literal|false
decl_stmt|;
comment|/** Whether    *<a href="https://issues.apache.org/jira/browse/CALCITE-673">[CALCITE-673]    * Timeout executing joins against MySQL</a> is fixed. */
specifier|public
specifier|static
specifier|final
name|boolean
name|CALCITE_673_FIXED
init|=
literal|false
decl_stmt|;
comment|/** Whether    *<a href="https://issues.apache.org/jira/browse/CALCITE-1048">[CALCITE-1048]    * Make metadata more robust</a> is fixed. */
specifier|public
specifier|static
specifier|final
name|boolean
name|CALCITE_1048_FIXED
init|=
literal|false
decl_stmt|;
comment|/** Whether    *<a href="https://issues.apache.org/jira/browse/CALCITE-1045">[CALCITE-1045]    * Decorrelate sub-queries in Project and Join</a> is fixed. */
specifier|public
specifier|static
specifier|final
name|boolean
name|CALCITE_1045_FIXED
init|=
literal|false
decl_stmt|;
comment|/** Whether    *<a href="https://issues.apache.org/jira/browse/CALCITE-2400">[CALCITE-2400]    * Allow standards-compliant column ordering for NATURAL JOIN and JOIN USING    * when dynamic tables are used</a> is fixed. */
specifier|public
specifier|static
specifier|final
name|boolean
name|CALCITE_2400_FIXED
init|=
literal|false
decl_stmt|;
comment|/** Whether    *<a href="https://issues.apache.org/jira/browse/CALCITE-2401">[CALCITE-2401]    * Improve RelMdPredicates performance</a> is fixed. */
specifier|public
specifier|static
specifier|final
name|boolean
name|CALCITE_2401_FIXED
init|=
literal|false
decl_stmt|;
comment|/** Whether    *<a href="https://issues.apache.org/jira/browse/CALCITE-2539">[CALCITE-2539]    * Several test case not passed in CalciteSqlOperatorTest.java</a> is fixed. */
specifier|public
specifier|static
specifier|final
name|boolean
name|CALCITE_2539_FIXED
init|=
literal|false
decl_stmt|;
comment|/** Whether    *<a href="https://issues.apache.org/jira/browse/CALCITE-2869">[CALCITE-2869]    * JSON data type support</a> is fixed. */
specifier|public
specifier|static
specifier|final
name|boolean
name|CALCITE_2869_FIXED
init|=
literal|false
decl_stmt|;
comment|/** Whether    *<a href="https://issues.apache.org/jira/browse/CALCITE-3243">[CALCITE-3243]    * Incomplete validation of operands in JSON functions</a> is fixed. */
specifier|public
specifier|static
specifier|final
name|boolean
name|CALCITE_3243_FIXED
init|=
literal|false
decl_stmt|;
comment|/** Whether    *<a href="https://issues.apache.org/jira/browse/CALCITE-4204">[CALCITE-4204]    * Intermittent precision in Druid results when using aggregation functions over columns of type    * DOUBLE</a> is fixed. */
specifier|public
specifier|static
specifier|final
name|boolean
name|CALCITE_4204_FIXED
init|=
literal|false
decl_stmt|;
comment|/** Whether    *<a href="https://issues.apache.org/jira/browse/CALCITE-4205">[CALCITE-4205]    * DruidAdapterIT#testDruidTimeFloorAndTimeParseExpressions2 fails</a> is fixed. */
specifier|public
specifier|static
specifier|final
name|boolean
name|CALCITE_4205_FIXED
init|=
literal|false
decl_stmt|;
comment|/** Whether    *<a href="https://issues.apache.org/jira/browse/CALCITE-4213">[CALCITE-4213]    * Druid plans with small intervals should be chosen over full interval scan plus filter</a> is    * fixed. */
specifier|public
specifier|static
specifier|final
name|boolean
name|CALCITE_4213_FIXED
init|=
literal|false
decl_stmt|;
comment|/** Whether    *<a href="https://issues.apache.org/jira/browse/CALCITE-4645">[CALCITE-4645]    * In Elasticsearch adapter, a range predicate should be translated to a range query</a> is    * fixed. */
specifier|public
specifier|static
specifier|final
name|boolean
name|CALCITE_4645_FIXED
init|=
literal|false
decl_stmt|;
comment|/** Whether    *<a href="https://issues.apache.org/jira/browse/CALCITE-5422">[CALCITE-5422]    * MILLISECOND and MICROSECOND units in INTERVAL literal</a> is fixed. */
specifier|public
specifier|static
specifier|final
name|boolean
name|CALCITE_5422_FIXED
init|=
literal|false
decl_stmt|;
comment|/**    * Use this to flag temporary code.    */
specifier|public
specifier|static
specifier|final
name|boolean
name|TODO_FIXED
init|=
literal|false
decl_stmt|;
comment|/**    * Use this method to flag temporary code.    *    *<p>Example #1:    *<blockquote><pre>    * if (Bug.remark("baz fixed") == null) {    *   baz();    * }</pre></blockquote>    *    *<p>Example #2:    *<blockquote><pre>    * /&#42;&#42;&#64;see Bug#remark Remove before checking in&#42;/    * void uselessMethod() {}    *</pre></blockquote>    */
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|T
name|remark
parameter_list|(
name|T
name|remark
parameter_list|)
block|{
return|return
name|remark
return|;
block|}
comment|/**    * Use this method to flag code that should be re-visited after upgrading    * a component.    *    *<p>If the intended change is that a class or member be removed, flag    * instead using a {@link Deprecated} annotation followed by a comment such as    * "to be removed before 2.0".    */
specifier|public
specifier|static
name|boolean
name|upgrade
parameter_list|(
name|String
name|remark
parameter_list|)
block|{
name|Util
operator|.
name|discard
argument_list|(
name|remark
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

