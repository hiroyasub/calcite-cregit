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
name|plan
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
name|tree
operator|.
name|Expression
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
name|prepare
operator|.
name|RelOptTableImpl
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
name|RelDistribution
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
name|RelDistributions
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
name|RelReferentialConstraint
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
name|LogicalTableScan
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
name|rel
operator|.
name|type
operator|.
name|RelDataTypeField
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
name|ColumnStrategy
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
name|ImmutableBitSet
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
name|collect
operator|.
name|ImmutableList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
comment|/**  * Partial implementation of {@link RelOptTable}.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|RelOptAbstractTable
implements|implements
name|RelOptTable
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|protected
specifier|final
name|RelOptSchema
name|schema
decl_stmt|;
specifier|protected
specifier|final
name|RelDataType
name|rowType
decl_stmt|;
specifier|protected
specifier|final
name|String
name|name
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
specifier|protected
name|RelOptAbstractTable
parameter_list|(
name|RelOptSchema
name|schema
parameter_list|,
name|String
name|name
parameter_list|,
name|RelDataType
name|rowType
parameter_list|)
block|{
name|this
operator|.
name|schema
operator|=
name|schema
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|rowType
operator|=
name|rowType
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getQualifiedName
parameter_list|()
block|{
return|return
name|ImmutableList
operator|.
name|of
argument_list|(
name|name
argument_list|)
return|;
block|}
specifier|public
name|double
name|getRowCount
parameter_list|()
block|{
return|return
literal|100
return|;
block|}
specifier|public
name|RelDataType
name|getRowType
parameter_list|()
block|{
return|return
name|rowType
return|;
block|}
specifier|public
name|RelOptSchema
name|getRelOptSchema
parameter_list|()
block|{
return|return
name|schema
return|;
block|}
comment|// Override to define collations.
specifier|public
name|List
argument_list|<
name|RelCollation
argument_list|>
name|getCollationList
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
specifier|public
name|RelDistribution
name|getDistribution
parameter_list|()
block|{
return|return
name|RelDistributions
operator|.
name|BROADCAST_DISTRIBUTED
return|;
block|}
specifier|public
parameter_list|<
name|T
parameter_list|>
name|T
name|unwrap
parameter_list|(
name|Class
argument_list|<
name|T
argument_list|>
name|clazz
parameter_list|)
block|{
return|return
name|clazz
operator|.
name|isInstance
argument_list|(
name|this
argument_list|)
condition|?
name|clazz
operator|.
name|cast
argument_list|(
name|this
argument_list|)
else|:
literal|null
return|;
block|}
comment|// Override to define keys
specifier|public
name|boolean
name|isKey
parameter_list|(
name|ImmutableBitSet
name|columns
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
comment|// Override to get unique keys
specifier|public
name|List
argument_list|<
name|ImmutableBitSet
argument_list|>
name|getKeys
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
comment|// Override to define foreign keys
specifier|public
name|List
argument_list|<
name|RelReferentialConstraint
argument_list|>
name|getReferentialConstraints
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
specifier|public
name|RelNode
name|toRel
parameter_list|(
name|ToRelContext
name|context
parameter_list|)
block|{
return|return
name|LogicalTableScan
operator|.
name|create
argument_list|(
name|context
operator|.
name|getCluster
argument_list|()
argument_list|,
name|this
argument_list|,
name|context
operator|.
name|getTableHints
argument_list|()
argument_list|)
return|;
block|}
specifier|public
name|Expression
name|getExpression
parameter_list|(
name|Class
name|clazz
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|RelOptTable
name|extend
parameter_list|(
name|List
argument_list|<
name|RelDataTypeField
argument_list|>
name|extendedFields
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
specifier|public
name|List
argument_list|<
name|ColumnStrategy
argument_list|>
name|getColumnStrategies
parameter_list|()
block|{
return|return
name|RelOptTableImpl
operator|.
name|columnStrategies
argument_list|(
name|this
argument_list|)
return|;
block|}
block|}
end_class

end_unit

