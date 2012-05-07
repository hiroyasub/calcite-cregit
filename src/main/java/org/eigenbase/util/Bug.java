begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to Julian Hyde under one or more contributor license // agreements. See the NOTICE file distributed with this work for // additional information regarding copyright ownership. // // Julian Hyde licenses this file to you under the Apache License, // Version 2.0 (the "License"); you may not use this file except in // compliance with the License. You may obtain a copy of the License at: // // http://www.apache.org/licenses/LICENSE-2.0 // // Unless required by applicable law or agreed to in writing, software // distributed under the License is distributed on an "AS IS" BASIS, // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. // See the License for the specific language governing permissions and // limitations under the License. */
end_comment

begin_package
package|package
name|org
operator|.
name|eigenbase
operator|.
name|util
package|;
end_package

begin_comment
comment|/**  * Holder for a list of constants describing which bugs which have not been  * fixed.  *  *<p>You can use these constants to control the flow of your code. For example,  * suppose that bug FNL-123 causes the "INSERT" statement to return an incorrect  * row-count, and you want to disable unit tests. You might use the constant in  * your code as follows:  *  *<blockquote>  *<pre>Statement stmt = connection.createStatement();  * int rowCount = stmt.execute(  *     "INSERT INTO FemaleEmps SELECT * FROM Emps WHERE gender = 'F'");  * if (Bug.Fnl123Fixed) {  *    assertEquals(rowCount, 5);  * }</pre>  *</blockquote>  *  *<p>The usage of the constant is a convenient way to identify the impact of  * the bug. When someone fixes the bug, they will remove the constant and all  * usages of it. Also, the constant helps track the propagation of the fix: as  * the fix is integrated into other branches, the constant will be removed from  * those branches.</p>  *  * @author jhyde  * @version $Id$  * @since 2006/3/2  */
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
name|Dt239Fixed
init|=
literal|false
decl_stmt|;
comment|/**      * Window Rank functions are supported through the validator but not      * implenmented by calculator. Disable tests and modified SqlRankFunction to      * return "Unknown Function".      */
specifier|public
specifier|static
specifier|final
name|boolean
name|Dt561Fixed
init|=
literal|false
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|boolean
name|Dt591Fixed
init|=
literal|false
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|boolean
name|Dt785Fixed
init|=
literal|false
decl_stmt|;
comment|// angel
comment|/**      * Whether dtbug1446 "Window Rank Functions not fully implemented" is fixed.      */
specifier|public
specifier|static
specifier|final
name|boolean
name|Dt1446Fixed
init|=
literal|false
decl_stmt|;
comment|// jhyde
comment|/**      * Whether<a href="http://issues.eigenbase.org/browse/FNL-3">issue      * Fnl-3</a> is fixed.      */
specifier|public
specifier|static
specifier|final
name|boolean
name|Fnl3Fixed
init|=
literal|false
decl_stmt|;
comment|/**      * Whether<a href="http://issues.eigenbase.org/browse/FNL-77">issue FNL-77:      * Fennel calc returns CURRENT_TIMESTAMP in UTC, should be local time</a> is      * fixed.      */
specifier|public
specifier|static
specifier|final
name|boolean
name|Fnl77Fixed
init|=
literal|false
decl_stmt|;
comment|/**      * Whether<a href="http://issues.eigenbase.org/browse/FRG-327">issue      * FRG-327: AssertionError while translating IN list that contains null</a>      * is fixed.      */
specifier|public
specifier|static
specifier|final
name|boolean
name|Frg327Fixed
init|=
literal|false
decl_stmt|;
comment|/**      * Whether<a href="http://issues.eigenbase.org/browse/FRG-377">issue      * FRG-377: Regular character set identifiers defined in SQL:2008 spec like      * :ALPHA:, * :UPPER:, :LOWER:, ... etc. are not yet implemented in      * SIMILAR TO expressions.</a> is fixed.      */
specifier|public
specifier|static
specifier|final
name|boolean
name|Frg377Fixed
init|=
literal|false
decl_stmt|;
comment|/**      * Whether dtbug1684 "CURRENT_DATE not implemented in fennel calc" is fixed.      */
specifier|public
specifier|static
specifier|final
name|boolean
name|Dt1684Fixed
init|=
literal|false
decl_stmt|;
comment|/**      * Whether dtbug1684 "Integration issues" is fixed.      */
specifier|public
specifier|static
specifier|final
name|boolean
name|Dt1847Fixed
init|=
literal|false
decl_stmt|;
comment|// kkrueger
comment|// mberkowitz
comment|// murali
comment|// rchen
comment|// schoi
comment|// stephan
comment|// tleung
comment|// xluo
comment|// zfong
comment|/**      * Whether<a href="http://issues.eigenbase.org/browse/FNL-25">issue      * FNL-25</a> is fixed. (also filed as dtbug 153)      */
specifier|public
specifier|static
specifier|final
name|boolean
name|Fnl25Fixed
init|=
literal|false
decl_stmt|;
comment|/**      * Whether<a href="http://issues.eigenbase.org/browse/FNL-54">issue FNL-54:      * cast time to timestamp should initialize date to current_date</a> is      * fixed.      */
specifier|public
specifier|static
specifier|final
name|boolean
name|Fnl54Fixed
init|=
literal|false
decl_stmt|;
comment|// johnk
comment|// jouellette
comment|// jpham
comment|// jvs
comment|/**      * Whether<a href="http://issues.eigenbase.org/browse/FRG-26">issue      * FRG-26</a> is fixed.      */
specifier|public
specifier|static
specifier|final
name|boolean
name|Frg26Fixed
init|=
literal|false
decl_stmt|;
comment|/**      * Whether<a href="http://issues.eigenbase.org/browse/FRG-73">issue FRG-73:      * miscellaneous bugs with nested comments</a> is fixed.      */
specifier|public
specifier|static
specifier|final
name|boolean
name|Frg73Fixed
init|=
literal|false
decl_stmt|;
comment|/**      * Whether<a href="http://issues.eigenbase.org/browse/FRG-78">issue FRG-78:      * collation clause should be on expression instead of identifier</a> is      * fixed.      */
specifier|public
specifier|static
specifier|final
name|boolean
name|Frg78Fixed
init|=
literal|false
decl_stmt|;
comment|/**      * Whether<a href="http://issues.eigenbase.org/browse/FRG-103">issue      * FRG-103: validator allows duplicate target columns in insert</a> is      * fixed.      */
specifier|public
specifier|static
specifier|final
name|boolean
name|Frg103Fixed
init|=
literal|false
decl_stmt|;
comment|/**      * Whether<a href="http://issues.eigenbase.org/browse/FRG-140">issue      * FRG-140: validator does not accept column qualified by schema name</a> is      * fixed.      */
specifier|public
specifier|static
specifier|final
name|boolean
name|Frg140Fixed
init|=
literal|false
decl_stmt|;
comment|/**      * Whether<a href="http://issues.eigenbase.org/browse/FRG-187">issue      * FRG-187: FarragoAutoVmOperatorTest.testOverlapsOperator fails</a> is      * fixed.      */
specifier|public
specifier|static
specifier|final
name|boolean
name|Frg187Fixed
init|=
literal|false
decl_stmt|;
comment|/**      * Whether<a href="http://issues.eigenbase.org/browse/FRG-189">issue      * FRG-189: FarragoAutoVmOperatorTest.testSelect fails</a> is fixed.      */
specifier|public
specifier|static
specifier|final
name|boolean
name|Frg189Fixed
init|=
literal|false
decl_stmt|;
comment|// elin
comment|// fliang
comment|// fzhang
comment|// hersker
comment|// jack
comment|/**      * Whether<a href="http://issues.eigenbase.org/browse/FRG-254">issue      * FRG-254: environment-dependent failure for      * SqlOperatorTest.testPrefixPlusOperator</a> is fixed.      */
specifier|public
specifier|static
specifier|final
name|boolean
name|Frg254Fixed
init|=
literal|false
decl_stmt|;
comment|/**      * Whether<a href="http://issues.eigenbase.org/browse/FRG-282">issue      * FRG-282: Support precision in TIME and TIMESTAMP data types</a> is fixed.      */
specifier|public
specifier|static
specifier|final
name|boolean
name|Frg282Fixed
init|=
literal|false
decl_stmt|;
comment|/**      * Whether<a href="http://issues.eigenbase.org/browse/FRG-283">issue      * FRG-283: Calc cannot cast VARBINARY values</a> is fixed.      */
specifier|public
specifier|static
specifier|final
name|boolean
name|Frg283Fixed
init|=
literal|false
decl_stmt|;
comment|/**      * Whether<a href="http://issues.eigenbase.org/browse/FRG-296">issue      * FRG-296: SUBSTRING(string FROM regexp FOR regexp)</a> is fixed.      */
specifier|public
specifier|static
specifier|final
name|boolean
name|Frg296Fixed
init|=
literal|false
decl_stmt|;
comment|/**      * Whether<a href="http://issues.eigenbase.org/browse/FRG-375">issue      * FRG-375: The expression VALUES ('cd' SIMILAR TO '[a-e^c]d') returns TRUE.      * It should return FALSE.</a> is fixed.      */
specifier|public
specifier|static
specifier|final
name|boolean
name|Frg375Fixed
init|=
literal|false
decl_stmt|;
comment|/**      * Whether<a href="http://issues.eigenbase.org/browse/FRG-378">issue      * FRG-378: Regular expressions in SIMILAR TO predicates      * potentially dont match SQL:2008 spec in a few cases.</a> is fixed.      */
specifier|public
specifier|static
specifier|final
name|boolean
name|Frg378Fixed
init|=
literal|false
decl_stmt|;
block|}
end_class

begin_comment
comment|// End Bug.java
end_comment

end_unit

