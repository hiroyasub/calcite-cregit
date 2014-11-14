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
name|SqlCall
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
name|SqlNode
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
name|Pair
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

begin_comment
comment|/**  * The name-resolution scope of a OVER clause. The objects visible are those in  * the parameters found on the left side of the over clause, and objects  * inherited from the parent scope.  *  *<p>This object is both a {@link SqlValidatorScope} only. In the query</p>  *  *<blockquote>  *<pre>SELECT name FROM (  *     SELECT *  *     FROM emp OVER (  *         ORDER BY empno  *         RANGE BETWEEN 2 PRECEDING AND 2 FOLLOWING))  *</pre>  *</blockquote>  *  *<p>We need to use the {@link OverScope} as a {@link SqlValidatorNamespace}  * when resolving names used in the window specification.</p>  */
end_comment

begin_class
specifier|public
class|class
name|OverScope
extends|extends
name|ListScope
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|private
specifier|final
name|SqlCall
name|overCall
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Creates a scope corresponding to a SELECT clause.    *    * @param parent   Parent scope, or null    * @param overCall Call to OVER operator    */
name|OverScope
parameter_list|(
name|SqlValidatorScope
name|parent
parameter_list|,
name|SqlCall
name|overCall
parameter_list|)
block|{
name|super
argument_list|(
name|parent
argument_list|)
expr_stmt|;
name|this
operator|.
name|overCall
operator|=
name|overCall
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
specifier|public
name|SqlNode
name|getNode
parameter_list|()
block|{
return|return
name|overCall
return|;
block|}
specifier|public
name|SqlMonotonicity
name|getMonotonicity
parameter_list|(
name|SqlNode
name|expr
parameter_list|)
block|{
name|SqlMonotonicity
name|monotonicity
init|=
name|expr
operator|.
name|getMonotonicity
argument_list|(
name|this
argument_list|)
decl_stmt|;
if|if
condition|(
name|monotonicity
operator|!=
name|SqlMonotonicity
operator|.
name|NOT_MONOTONIC
condition|)
block|{
return|return
name|monotonicity
return|;
block|}
if|if
condition|(
name|children
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
specifier|final
name|SqlValidatorNamespace
name|child
init|=
name|children
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|right
decl_stmt|;
specifier|final
name|List
argument_list|<
name|Pair
argument_list|<
name|SqlNode
argument_list|,
name|SqlMonotonicity
argument_list|>
argument_list|>
name|monotonicExprs
init|=
name|child
operator|.
name|getMonotonicExprs
argument_list|()
decl_stmt|;
for|for
control|(
name|Pair
argument_list|<
name|SqlNode
argument_list|,
name|SqlMonotonicity
argument_list|>
name|pair
range|:
name|monotonicExprs
control|)
block|{
if|if
condition|(
name|expr
operator|.
name|equalsDeep
argument_list|(
name|pair
operator|.
name|left
argument_list|,
literal|false
argument_list|)
condition|)
block|{
return|return
name|pair
operator|.
name|right
return|;
block|}
block|}
block|}
return|return
name|super
operator|.
name|getMonotonicity
argument_list|(
name|expr
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|// End OverScope.java
end_comment

end_unit

