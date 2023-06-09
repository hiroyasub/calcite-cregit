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
name|mutable
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
name|rel
operator|.
name|core
operator|.
name|Project
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
name|rex
operator|.
name|RexNode
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
name|rex
operator|.
name|RexUtil
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
name|validate
operator|.
name|SqlValidatorUtil
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
name|Litmus
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
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|util
operator|.
name|mapping
operator|.
name|Mappings
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
comment|/** Mutable equivalent of {@link org.apache.calcite.rel.core.Project}. */
end_comment

begin_class
specifier|public
class|class
name|MutableProject
extends|extends
name|MutableSingleRel
block|{
specifier|public
specifier|final
name|List
argument_list|<
name|RexNode
argument_list|>
name|projects
decl_stmt|;
specifier|private
name|MutableProject
parameter_list|(
name|RelDataType
name|rowType
parameter_list|,
name|MutableRel
name|input
parameter_list|,
name|List
argument_list|<
name|RexNode
argument_list|>
name|projects
parameter_list|)
block|{
name|super
argument_list|(
name|MutableRelType
operator|.
name|PROJECT
argument_list|,
name|rowType
argument_list|,
name|input
argument_list|)
expr_stmt|;
name|this
operator|.
name|projects
operator|=
name|projects
expr_stmt|;
assert|assert
name|RexUtil
operator|.
name|compatibleTypes
argument_list|(
name|projects
argument_list|,
name|rowType
argument_list|,
name|Litmus
operator|.
name|THROW
argument_list|)
assert|;
block|}
comment|/**    * Creates a MutableProject.    *    * @param rowType   Row type    * @param input     Input relational expression    * @param projects  List of expressions for the input columns    */
specifier|public
specifier|static
name|MutableProject
name|of
parameter_list|(
name|RelDataType
name|rowType
parameter_list|,
name|MutableRel
name|input
parameter_list|,
name|List
argument_list|<
name|RexNode
argument_list|>
name|projects
parameter_list|)
block|{
return|return
operator|new
name|MutableProject
argument_list|(
name|rowType
argument_list|,
name|input
argument_list|,
name|projects
argument_list|)
return|;
block|}
comment|/**    * Creates a MutableProject.    *    * @param input         Input relational expression    * @param exprList      List of expressions for the input columns    * @param fieldNameList Aliases of the expressions, or null to generate    */
specifier|public
specifier|static
name|MutableRel
name|of
parameter_list|(
name|MutableRel
name|input
parameter_list|,
name|List
argument_list|<
name|RexNode
argument_list|>
name|exprList
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|fieldNameList
parameter_list|)
block|{
specifier|final
name|RelDataType
name|rowType
init|=
name|RexUtil
operator|.
name|createStructType
argument_list|(
name|input
operator|.
name|cluster
operator|.
name|getTypeFactory
argument_list|()
argument_list|,
name|exprList
argument_list|,
name|fieldNameList
argument_list|,
name|SqlValidatorUtil
operator|.
name|F_SUGGESTER
argument_list|)
decl_stmt|;
return|return
name|of
argument_list|(
name|rowType
argument_list|,
name|input
argument_list|,
name|exprList
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
annotation|@
name|Nullable
name|Object
name|obj
parameter_list|)
block|{
return|return
name|obj
operator|==
name|this
operator|||
name|obj
operator|instanceof
name|MutableProject
operator|&&
name|MutableRel
operator|.
name|PAIRWISE_STRING_EQUIVALENCE
operator|.
name|equivalent
argument_list|(
name|projects
argument_list|,
operator|(
operator|(
name|MutableProject
operator|)
name|obj
operator|)
operator|.
name|projects
argument_list|)
operator|&&
name|input
operator|.
name|equals
argument_list|(
operator|(
operator|(
name|MutableProject
operator|)
name|obj
operator|)
operator|.
name|input
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|Objects
operator|.
name|hash
argument_list|(
name|input
argument_list|,
name|MutableRel
operator|.
name|PAIRWISE_STRING_EQUIVALENCE
operator|.
name|hash
argument_list|(
name|projects
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|StringBuilder
name|digest
parameter_list|(
name|StringBuilder
name|buf
parameter_list|)
block|{
return|return
name|buf
operator|.
name|append
argument_list|(
literal|"Project(projects: "
argument_list|)
operator|.
name|append
argument_list|(
name|projects
argument_list|)
operator|.
name|append
argument_list|(
literal|")"
argument_list|)
return|;
block|}
comment|/** Returns a list of (expression, name) pairs. */
specifier|public
specifier|final
name|List
argument_list|<
name|Pair
argument_list|<
name|RexNode
argument_list|,
name|String
argument_list|>
argument_list|>
name|getNamedProjects
parameter_list|()
block|{
return|return
name|Pair
operator|.
name|zip
argument_list|(
name|projects
argument_list|,
name|rowType
operator|.
name|getFieldNames
argument_list|()
argument_list|)
return|;
block|}
specifier|public
name|Mappings
operator|.
expr|@
name|Nullable
name|TargetMapping
name|getMapping
argument_list|()
block|{
return|return
name|Project
operator|.
name|getMapping
argument_list|(
name|input
operator|.
name|rowType
operator|.
name|getFieldCount
argument_list|()
argument_list|,
name|projects
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|MutableRel
name|clone
parameter_list|()
block|{
return|return
name|MutableProject
operator|.
name|of
argument_list|(
name|rowType
argument_list|,
name|input
operator|.
name|clone
argument_list|()
argument_list|,
name|projects
argument_list|)
return|;
block|}
block|}
end_class

end_unit

