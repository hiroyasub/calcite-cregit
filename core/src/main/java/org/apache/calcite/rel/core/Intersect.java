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
name|RelInput
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
name|sql
operator|.
name|SqlKind
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|BitSet
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
comment|/**  * Relational expression that returns the intersection of the rows of its  * inputs.  *  *<p>If "all" is true, performs then multiset intersection; otherwise,  * performs set set intersection (implying no duplicates in the results).  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|Intersect
extends|extends
name|SetOp
block|{
comment|/**    * Creates an Intersect.    */
specifier|public
name|Intersect
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|RelTraitSet
name|traits
parameter_list|,
name|List
argument_list|<
name|RelNode
argument_list|>
name|inputs
parameter_list|,
name|boolean
name|all
parameter_list|)
block|{
name|super
argument_list|(
name|cluster
argument_list|,
name|traits
argument_list|,
name|inputs
argument_list|,
name|SqlKind
operator|.
name|INTERSECT
argument_list|,
name|all
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates an Intersect by parsing serialized output.    */
specifier|protected
name|Intersect
parameter_list|(
name|RelInput
name|input
parameter_list|)
block|{
name|super
argument_list|(
name|input
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|double
name|getRows
parameter_list|()
block|{
comment|// REVIEW jvs 30-May-2005:  I just pulled this out of a hat.
name|double
name|dRows
init|=
name|Double
operator|.
name|MAX_VALUE
decl_stmt|;
for|for
control|(
name|RelNode
name|input
range|:
name|inputs
control|)
block|{
name|dRows
operator|=
name|Math
operator|.
name|min
argument_list|(
name|dRows
argument_list|,
name|RelMetadataQuery
operator|.
name|getRowCount
argument_list|(
name|input
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|dRows
operator|*=
literal|0.25
expr_stmt|;
return|return
name|dRows
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isKey
parameter_list|(
name|BitSet
name|columns
parameter_list|)
block|{
for|for
control|(
name|RelNode
name|input
range|:
name|inputs
control|)
block|{
if|if
condition|(
name|input
operator|.
name|isKey
argument_list|(
name|columns
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
name|super
operator|.
name|isKey
argument_list|(
name|columns
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|// End Intersect.java
end_comment

end_unit

