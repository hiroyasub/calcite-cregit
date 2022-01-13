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
name|rel
operator|.
name|core
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
name|linq4j
operator|.
name|function
operator|.
name|Experimental
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
name|RelOptCluster
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
name|RelOptTable
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
name|RelTraitSet
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
name|BiRel
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
name|RelWriter
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
name|metadata
operator|.
name|RelMetadataQuery
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
name|type
operator|.
name|RelDataType
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
name|TransientTable
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
name|Util
import|;
end_import

begin_import
import|import
name|org
operator|.
name|checkerframework
operator|.
name|checker
operator|.
name|nullness
operator|.
name|qual
operator|.
name|Nullable
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
name|Objects
import|;
end_import

begin_comment
comment|/**  * Relational expression that computes a repeat union (recursive union in SQL  * terminology).  *  *<p>This operation is executed as follows:  *  *<ul>  *<li>Evaluate the left input (i.e., seed relational expression) once.  For  *   UNION (but not UNION ALL), discard duplicated rows.  *  *<li>Evaluate the right input (i.e., iterative relational expression) over and  *   over until it produces no more results (or until an optional maximum number  *   of iterations is reached). For UNION (but not UNION ALL), discard  *   duplicated results.  *</ul>  *  *<p>NOTE: The current API is experimental and subject to change without  * notice.  */
end_comment

begin_class
annotation|@
name|Experimental
specifier|public
specifier|abstract
class|class
name|RepeatUnion
extends|extends
name|BiRel
block|{
comment|/**    * Whether duplicates are considered.    */
specifier|public
specifier|final
name|boolean
name|all
decl_stmt|;
comment|/**    * Maximum number of times to repeat the iterative relational expression;    * negative value means no limit, 0 means only seed will be evaluated.    */
specifier|public
specifier|final
name|int
name|iterationLimit
decl_stmt|;
comment|/**    * Transient table where repeat union's intermediate results will be stored (optional).    */
specifier|protected
specifier|final
annotation|@
name|Nullable
name|RelOptTable
name|transientTable
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
specifier|protected
name|RepeatUnion
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|RelTraitSet
name|traitSet
parameter_list|,
name|RelNode
name|seed
parameter_list|,
name|RelNode
name|iterative
parameter_list|,
name|boolean
name|all
parameter_list|,
name|int
name|iterationLimit
parameter_list|,
annotation|@
name|Nullable
name|RelOptTable
name|transientTable
parameter_list|)
block|{
name|super
argument_list|(
name|cluster
argument_list|,
name|traitSet
argument_list|,
name|seed
argument_list|,
name|iterative
argument_list|)
expr_stmt|;
name|this
operator|.
name|iterationLimit
operator|=
name|iterationLimit
expr_stmt|;
name|this
operator|.
name|all
operator|=
name|all
expr_stmt|;
name|this
operator|.
name|transientTable
operator|=
name|transientTable
expr_stmt|;
if|if
condition|(
name|transientTable
operator|!=
literal|null
condition|)
block|{
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|transientTable
operator|.
name|unwrap
argument_list|(
name|TransientTable
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|double
name|estimateRowCount
parameter_list|(
name|RelMetadataQuery
name|mq
parameter_list|)
block|{
comment|// TODO implement a more accurate row count?
name|double
name|seedRowCount
init|=
name|mq
operator|.
name|getRowCount
argument_list|(
name|getSeedRel
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|iterationLimit
operator|==
literal|0
condition|)
block|{
return|return
name|seedRowCount
return|;
block|}
return|return
name|seedRowCount
operator|+
name|mq
operator|.
name|getRowCount
argument_list|(
name|getIterativeRel
argument_list|()
argument_list|)
operator|*
operator|(
name|iterationLimit
operator|<
literal|0
condition|?
literal|10
else|:
name|iterationLimit
operator|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|RelWriter
name|explainTerms
parameter_list|(
name|RelWriter
name|pw
parameter_list|)
block|{
name|super
operator|.
name|explainTerms
argument_list|(
name|pw
argument_list|)
expr_stmt|;
if|if
condition|(
name|iterationLimit
operator|>=
literal|0
condition|)
block|{
name|pw
operator|.
name|item
argument_list|(
literal|"iterationLimit"
argument_list|,
name|iterationLimit
argument_list|)
expr_stmt|;
block|}
return|return
name|pw
operator|.
name|item
argument_list|(
literal|"all"
argument_list|,
name|all
argument_list|)
return|;
block|}
specifier|public
name|RelNode
name|getSeedRel
parameter_list|()
block|{
return|return
name|left
return|;
block|}
specifier|public
name|RelNode
name|getIterativeRel
parameter_list|()
block|{
return|return
name|right
return|;
block|}
specifier|public
annotation|@
name|Nullable
name|RelOptTable
name|getTransientTable
parameter_list|()
block|{
return|return
name|transientTable
return|;
block|}
annotation|@
name|Override
specifier|protected
name|RelDataType
name|deriveRowType
parameter_list|()
block|{
specifier|final
name|List
argument_list|<
name|RelDataType
argument_list|>
name|inputRowTypes
init|=
name|Util
operator|.
name|transform
argument_list|(
name|getInputs
argument_list|()
argument_list|,
name|RelNode
operator|::
name|getRowType
argument_list|)
decl_stmt|;
specifier|final
name|RelDataType
name|rowType
init|=
name|getCluster
argument_list|()
operator|.
name|getTypeFactory
argument_list|()
operator|.
name|leastRestrictive
argument_list|(
name|inputRowTypes
argument_list|)
decl_stmt|;
if|if
condition|(
name|rowType
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Cannot compute compatible row type "
operator|+
literal|"for arguments: "
operator|+
name|Util
operator|.
name|sepList
argument_list|(
name|inputRowTypes
argument_list|,
literal|", "
argument_list|)
argument_list|)
throw|;
block|}
return|return
name|rowType
return|;
block|}
block|}
end_class

end_unit

