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
name|RelDistributionTraitDef
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
name|core
operator|.
name|Spool
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
name|TableSpool
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

begin_comment
comment|/**  * Sub-class of {@link TableSpool} not targeted at any particular engine or calling convention.  *  *<p>NOTE: The current API is experimental and subject to change without notice.</p>  */
end_comment

begin_class
annotation|@
name|Experimental
specifier|public
class|class
name|LogicalTableSpool
extends|extends
name|TableSpool
block|{
comment|//~ Constructors -----------------------------------------------------------
specifier|public
name|LogicalTableSpool
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
name|Type
name|readType
parameter_list|,
name|Type
name|writeType
parameter_list|,
name|String
name|tableName
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
name|readType
argument_list|,
name|writeType
argument_list|,
name|tableName
argument_list|)
expr_stmt|;
block|}
comment|/** Creates a LogicalTableSpool. */
specifier|public
specifier|static
name|LogicalTableSpool
name|create
parameter_list|(
name|RelNode
name|input
parameter_list|,
name|Type
name|readType
parameter_list|,
name|Type
name|writeType
parameter_list|,
name|String
name|tableName
parameter_list|)
block|{
name|RelOptCluster
name|cluster
init|=
name|input
operator|.
name|getCluster
argument_list|()
decl_stmt|;
name|RelMetadataQuery
name|mq
init|=
name|cluster
operator|.
name|getMetadataQuery
argument_list|()
decl_stmt|;
name|RelTraitSet
name|traitSet
init|=
name|cluster
operator|.
name|traitSetOf
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
parameter_list|()
lambda|->
name|mq
operator|.
name|collations
argument_list|(
name|input
argument_list|)
argument_list|)
operator|.
name|replaceIf
argument_list|(
name|RelDistributionTraitDef
operator|.
name|INSTANCE
argument_list|,
parameter_list|()
lambda|->
name|mq
operator|.
name|distribution
argument_list|(
name|input
argument_list|)
argument_list|)
decl_stmt|;
return|return
operator|new
name|LogicalTableSpool
argument_list|(
name|cluster
argument_list|,
name|traitSet
argument_list|,
name|input
argument_list|,
name|readType
argument_list|,
name|writeType
argument_list|,
name|tableName
argument_list|)
return|;
block|}
comment|//~ Methods ----------------------------------------------------------------
annotation|@
name|Override
specifier|protected
name|Spool
name|copy
parameter_list|(
name|RelTraitSet
name|traitSet
parameter_list|,
name|RelNode
name|input
parameter_list|,
name|Type
name|readType
parameter_list|,
name|Type
name|writeType
parameter_list|)
block|{
return|return
operator|new
name|LogicalTableSpool
argument_list|(
name|input
operator|.
name|getCluster
argument_list|()
argument_list|,
name|traitSet
argument_list|,
name|input
argument_list|,
name|readType
argument_list|,
name|writeType
argument_list|,
name|tableName
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|// End LogicalTableSpool.java
end_comment

end_unit

