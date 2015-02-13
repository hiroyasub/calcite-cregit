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
name|logical
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
name|Convention
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
name|RelCollation
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
name|RelCollationTraitDef
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
name|RelCollations
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
name|RelShuttle
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
name|metadata
operator|.
name|RelMdCollation
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
name|util
operator|.
name|Util
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
name|Supplier
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
comment|/**  * Sub-class of {@link org.apache.calcite.rel.core.Project} not  * targeted at any particular engine or calling convention.  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|LogicalProject
extends|extends
name|Project
block|{
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Creates a LogicalProject.    *    *<p>Use {@link #create} unless you know what you're doing.    *    * @param cluster  Cluster this relational expression belongs to    * @param traitSet Traits of this relational expression    * @param input    Input relational expression    * @param projects List of expressions for the input columns    * @param rowType  Output row type    */
specifier|public
name|LogicalProject
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|RelTraitSet
name|traitSet
parameter_list|,
name|RelNode
name|input
parameter_list|,
name|List
argument_list|<
name|?
extends|extends
name|RexNode
argument_list|>
name|projects
parameter_list|,
name|RelDataType
name|rowType
parameter_list|)
block|{
name|super
argument_list|(
name|cluster
argument_list|,
name|traitSet
argument_list|,
name|input
argument_list|,
name|projects
argument_list|,
name|rowType
argument_list|)
expr_stmt|;
assert|assert
name|traitSet
operator|.
name|containsIfApplicable
argument_list|(
name|Convention
operator|.
name|NONE
argument_list|)
assert|;
block|}
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|public
name|LogicalProject
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|RelTraitSet
name|traitSet
parameter_list|,
name|RelNode
name|input
parameter_list|,
name|List
argument_list|<
name|?
extends|extends
name|RexNode
argument_list|>
name|projects
parameter_list|,
name|RelDataType
name|rowType
parameter_list|,
name|int
name|flags
parameter_list|)
block|{
name|this
argument_list|(
name|cluster
argument_list|,
name|traitSet
argument_list|,
name|input
argument_list|,
name|projects
argument_list|,
name|rowType
argument_list|)
expr_stmt|;
name|Util
operator|.
name|discard
argument_list|(
name|flags
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|public
name|LogicalProject
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|RelNode
name|input
parameter_list|,
name|List
argument_list|<
name|RexNode
argument_list|>
name|projects
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|fieldNames
parameter_list|,
name|int
name|flags
parameter_list|)
block|{
name|this
argument_list|(
name|cluster
argument_list|,
name|cluster
operator|.
name|traitSetOf
argument_list|(
name|RelCollations
operator|.
name|EMPTY
argument_list|)
argument_list|,
name|input
argument_list|,
name|projects
argument_list|,
name|RexUtil
operator|.
name|createStructType
argument_list|(
name|cluster
operator|.
name|getTypeFactory
argument_list|()
argument_list|,
name|projects
argument_list|,
name|fieldNames
argument_list|)
argument_list|)
expr_stmt|;
name|Util
operator|.
name|discard
argument_list|(
name|flags
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a LogicalProject by parsing serialized output.    */
specifier|public
name|LogicalProject
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
comment|//~ Methods ----------------------------------------------------------------
comment|/** Creates a LogicalProject. */
specifier|public
specifier|static
name|LogicalProject
name|create
parameter_list|(
specifier|final
name|RelNode
name|input
parameter_list|,
specifier|final
name|List
argument_list|<
name|?
extends|extends
name|RexNode
argument_list|>
name|projects
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|fieldNames
parameter_list|)
block|{
specifier|final
name|RelOptCluster
name|cluster
init|=
name|input
operator|.
name|getCluster
argument_list|()
decl_stmt|;
specifier|final
name|RelDataType
name|rowType
init|=
name|RexUtil
operator|.
name|createStructType
argument_list|(
name|cluster
operator|.
name|getTypeFactory
argument_list|()
argument_list|,
name|projects
argument_list|,
name|fieldNames
argument_list|)
decl_stmt|;
return|return
name|create
argument_list|(
name|input
argument_list|,
name|projects
argument_list|,
name|rowType
argument_list|)
return|;
block|}
comment|/** Creates a LogicalProject, specifying row type rather than field names. */
specifier|public
specifier|static
name|LogicalProject
name|create
parameter_list|(
specifier|final
name|RelNode
name|input
parameter_list|,
specifier|final
name|List
argument_list|<
name|?
extends|extends
name|RexNode
argument_list|>
name|projects
parameter_list|,
name|RelDataType
name|rowType
parameter_list|)
block|{
specifier|final
name|RelOptCluster
name|cluster
init|=
name|input
operator|.
name|getCluster
argument_list|()
decl_stmt|;
specifier|final
name|RelTraitSet
name|traitSet
init|=
name|cluster
operator|.
name|traitSet
argument_list|()
operator|.
name|replace
argument_list|(
name|Convention
operator|.
name|NONE
argument_list|)
operator|.
name|replaceIfs
argument_list|(
name|RelCollationTraitDef
operator|.
name|INSTANCE
argument_list|,
operator|new
name|Supplier
argument_list|<
name|List
argument_list|<
name|RelCollation
argument_list|>
argument_list|>
argument_list|()
block|{
specifier|public
name|List
argument_list|<
name|RelCollation
argument_list|>
name|get
parameter_list|()
block|{
return|return
name|RelMdCollation
operator|.
name|project
argument_list|(
name|input
argument_list|,
name|projects
argument_list|)
return|;
block|}
block|}
argument_list|)
decl_stmt|;
return|return
operator|new
name|LogicalProject
argument_list|(
name|cluster
argument_list|,
name|traitSet
argument_list|,
name|input
argument_list|,
name|projects
argument_list|,
name|rowType
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|LogicalProject
name|copy
parameter_list|(
name|RelTraitSet
name|traitSet
parameter_list|,
name|RelNode
name|input
parameter_list|,
name|List
argument_list|<
name|RexNode
argument_list|>
name|projects
parameter_list|,
name|RelDataType
name|rowType
parameter_list|)
block|{
return|return
operator|new
name|LogicalProject
argument_list|(
name|getCluster
argument_list|()
argument_list|,
name|traitSet
argument_list|,
name|input
argument_list|,
name|projects
argument_list|,
name|rowType
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|RelNode
name|accept
parameter_list|(
name|RelShuttle
name|shuttle
parameter_list|)
block|{
return|return
name|shuttle
operator|.
name|visit
argument_list|(
name|this
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|// End LogicalProject.java
end_comment

end_unit

