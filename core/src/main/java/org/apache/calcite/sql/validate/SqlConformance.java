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
name|sql
operator|.
name|validate
package|;
end_package

begin_comment
comment|/**  * Enumeration of valid SQL compatibility modes.  *  *<p>For most purposes, one of the built-in compatibility modes in enum  * {@link SqlConformanceEnum} will suffice.  *  *<p>If you wish to implement this interface to build your own conformance,  * we strongly recommend that you extend {@link SqlAbstractConformance},  * or use a {@link SqlDelegatingConformance},  * so that you won't be broken by future changes.  *  * @see SqlConformanceEnum  * @see SqlAbstractConformance  * @see SqlDelegatingConformance  */
end_comment

begin_interface
specifier|public
interface|interface
name|SqlConformance
block|{
comment|/** Short-cut for {@link SqlConformanceEnum#DEFAULT}. */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
annotation|@
name|Deprecated
comment|// to be removed before 2.0
name|SqlConformanceEnum
name|DEFAULT
init|=
name|SqlConformanceEnum
operator|.
name|DEFAULT
decl_stmt|;
comment|/** Short-cut for {@link SqlConformanceEnum#STRICT_92}. */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
annotation|@
name|Deprecated
comment|// to be removed before 2.0
name|SqlConformanceEnum
name|STRICT_92
init|=
name|SqlConformanceEnum
operator|.
name|STRICT_92
decl_stmt|;
comment|/** Short-cut for {@link SqlConformanceEnum#STRICT_99}. */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
annotation|@
name|Deprecated
comment|// to be removed before 2.0
name|SqlConformanceEnum
name|STRICT_99
init|=
name|SqlConformanceEnum
operator|.
name|STRICT_99
decl_stmt|;
comment|/** Short-cut for {@link SqlConformanceEnum#PRAGMATIC_99}. */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
annotation|@
name|Deprecated
comment|// to be removed before 2.0
name|SqlConformanceEnum
name|PRAGMATIC_99
init|=
name|SqlConformanceEnum
operator|.
name|PRAGMATIC_99
decl_stmt|;
comment|/** Short-cut for {@link SqlConformanceEnum#ORACLE_10}. */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
annotation|@
name|Deprecated
comment|// to be removed before 2.0
name|SqlConformanceEnum
name|ORACLE_10
init|=
name|SqlConformanceEnum
operator|.
name|ORACLE_10
decl_stmt|;
comment|/** Short-cut for {@link SqlConformanceEnum#STRICT_2003}. */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
annotation|@
name|Deprecated
comment|// to be removed before 2.0
name|SqlConformanceEnum
name|STRICT_2003
init|=
name|SqlConformanceEnum
operator|.
name|STRICT_2003
decl_stmt|;
comment|/** Short-cut for {@link SqlConformanceEnum#PRAGMATIC_2003}. */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
annotation|@
name|Deprecated
comment|// to be removed before 2.0
name|SqlConformanceEnum
name|PRAGMATIC_2003
init|=
name|SqlConformanceEnum
operator|.
name|PRAGMATIC_2003
decl_stmt|;
comment|/**    * Whether 'order by 2' is interpreted to mean 'sort by the 2nd column in    * the select list'.    *    *<p>Among the built-in conformance levels, true in    * {@link SqlConformanceEnum#DEFAULT},    * {@link SqlConformanceEnum#ORACLE_10},    * {@link SqlConformanceEnum#STRICT_92},    * {@link SqlConformanceEnum#PRAGMATIC_99},    * {@link SqlConformanceEnum#PRAGMATIC_2003};    * false otherwise.    */
name|boolean
name|isSortByOrdinal
parameter_list|()
function_decl|;
comment|/**    * Whether 'order by x' is interpreted to mean 'sort by the select list item    * whose alias is x' even if there is a column called x.    *    *<p>Among the built-in conformance levels, true in    * {@link SqlConformanceEnum#DEFAULT},    * {@link SqlConformanceEnum#ORACLE_10},    * {@link SqlConformanceEnum#STRICT_92};    * false otherwise.    */
name|boolean
name|isSortByAlias
parameter_list|()
function_decl|;
comment|/**    * Whether "empno" is invalid in "select empno as x from emp order by empno"    * because the alias "x" obscures it.    *    *<p>Among the built-in conformance levels, true in    * {@link SqlConformanceEnum#STRICT_92};    * false otherwise.    */
name|boolean
name|isSortByAliasObscures
parameter_list|()
function_decl|;
comment|/**    * Whether FROM clause is required in a SELECT statement.    *    *<p>Among the built-in conformance levels, true in    * {@link SqlConformanceEnum#ORACLE_10},    * {@link SqlConformanceEnum#STRICT_92},    * {@link SqlConformanceEnum#STRICT_99},    * {@link SqlConformanceEnum#STRICT_2003};    * false otherwise.    */
name|boolean
name|isFromRequired
parameter_list|()
function_decl|;
comment|/**    * Whether the bang-equal token != is allowed as an alternative to&lt;&gt; in    * the parser.    *    *<p>Among the built-in conformance levels, true in    * {@link SqlConformanceEnum#ORACLE_10};    * false otherwise.    */
name|boolean
name|isBangEqualAllowed
parameter_list|()
function_decl|;
block|}
end_interface

begin_comment
comment|// End SqlConformance.java
end_comment

end_unit

