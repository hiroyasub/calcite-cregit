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
name|adapter
operator|.
name|innodb
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
name|RelOptPlanner
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
name|RelOptRule
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
name|core
operator|.
name|TableScan
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
name|hint
operator|.
name|HintPredicates
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
name|hint
operator|.
name|HintStrategyTable
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
name|hint
operator|.
name|RelHint
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
name|commons
operator|.
name|collections
operator|.
name|CollectionUtils
import|;
end_import

begin_import
import|import
name|com
operator|.
name|alibaba
operator|.
name|innodb
operator|.
name|java
operator|.
name|reader
operator|.
name|Constants
import|;
end_import

begin_import
import|import
name|com
operator|.
name|alibaba
operator|.
name|innodb
operator|.
name|java
operator|.
name|reader
operator|.
name|schema
operator|.
name|KeyMeta
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
name|Optional
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_comment
comment|/**  * Relational expression representing a scan of an InnoDB data source.  */
end_comment

begin_class
specifier|public
class|class
name|InnodbTableScan
extends|extends
name|TableScan
implements|implements
name|InnodbRel
block|{
specifier|final
name|InnodbTable
name|innodbTable
decl_stmt|;
specifier|final
name|RelDataType
name|projectRowType
decl_stmt|;
comment|/** Force to use one specific index from hint. */
specifier|private
specifier|final
annotation|@
name|Nullable
name|String
name|forceIndexName
decl_stmt|;
comment|/** This contains index to scan table and optional condition. */
specifier|private
specifier|final
name|IndexCondition
name|indexCondition
decl_stmt|;
specifier|protected
name|InnodbTableScan
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|RelTraitSet
name|traitSet
parameter_list|,
name|RelOptTable
name|table
parameter_list|,
name|InnodbTable
name|innodbTable
parameter_list|,
name|RelDataType
name|projectRowType
parameter_list|,
name|List
argument_list|<
name|RelHint
argument_list|>
name|hints
parameter_list|)
block|{
name|super
argument_list|(
name|cluster
argument_list|,
name|traitSet
argument_list|,
name|hints
argument_list|,
name|table
argument_list|)
expr_stmt|;
name|this
operator|.
name|innodbTable
operator|=
name|innodbTable
expr_stmt|;
name|this
operator|.
name|projectRowType
operator|=
name|projectRowType
expr_stmt|;
name|this
operator|.
name|forceIndexName
operator|=
name|getForceIndexName
argument_list|(
name|hints
argument_list|)
operator|.
name|orElse
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|this
operator|.
name|indexCondition
operator|=
name|getIndexCondition
argument_list|()
expr_stmt|;
assert|assert
name|innodbTable
operator|!=
literal|null
assert|;
assert|assert
name|getConvention
argument_list|()
operator|==
name|InnodbRel
operator|.
name|CONVENTION
assert|;
block|}
annotation|@
name|Override
specifier|public
name|RelNode
name|copy
parameter_list|(
name|RelTraitSet
name|traitSet
parameter_list|,
name|List
argument_list|<
name|RelNode
argument_list|>
name|inputs
parameter_list|)
block|{
assert|assert
name|inputs
operator|.
name|isEmpty
argument_list|()
assert|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
specifier|public
name|RelDataType
name|deriveRowType
parameter_list|()
block|{
return|return
name|projectRowType
operator|!=
literal|null
condition|?
name|projectRowType
else|:
name|super
operator|.
name|deriveRowType
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|register
parameter_list|(
name|RelOptPlanner
name|planner
parameter_list|)
block|{
name|HintStrategyTable
name|strategies
init|=
name|HintStrategyTable
operator|.
name|builder
argument_list|()
operator|.
name|hintStrategy
argument_list|(
literal|"index"
argument_list|,
name|HintPredicates
operator|.
name|TABLE_SCAN
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|getCluster
argument_list|()
operator|.
name|setHintStrategies
argument_list|(
name|strategies
argument_list|)
expr_stmt|;
name|planner
operator|.
name|addRule
argument_list|(
name|InnodbRules
operator|.
name|TO_ENUMERABLE
argument_list|)
expr_stmt|;
for|for
control|(
name|RelOptRule
name|rule
range|:
name|InnodbRules
operator|.
name|RULES
control|)
block|{
name|planner
operator|.
name|addRule
argument_list|(
name|rule
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|implement
parameter_list|(
name|Implementor
name|implementor
parameter_list|)
block|{
name|implementor
operator|.
name|innodbTable
operator|=
name|innodbTable
expr_stmt|;
name|implementor
operator|.
name|table
operator|=
name|table
expr_stmt|;
name|implementor
operator|.
name|setIndexCondition
argument_list|(
name|indexCondition
argument_list|)
expr_stmt|;
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
return|return
name|super
operator|.
name|explainTerms
argument_list|(
name|pw
argument_list|)
operator|.
name|itemIf
argument_list|(
literal|"forceIndex"
argument_list|,
name|forceIndexName
argument_list|,
name|forceIndexName
operator|!=
literal|null
argument_list|)
return|;
block|}
comment|/**    * Infer the implicit collation from index.    *    * @return the implicit collation based on the natural ordering of an index    */
specifier|public
name|RelCollation
name|getImplicitCollation
parameter_list|()
block|{
return|return
name|indexCondition
operator|.
name|getImplicitCollation
argument_list|()
return|;
block|}
specifier|private
name|Optional
argument_list|<
name|String
argument_list|>
name|getForceIndexName
parameter_list|(
specifier|final
name|List
argument_list|<
name|RelHint
argument_list|>
name|hints
parameter_list|)
block|{
if|if
condition|(
name|CollectionUtils
operator|.
name|isEmpty
argument_list|(
name|hints
argument_list|)
condition|)
block|{
return|return
name|Optional
operator|.
name|empty
argument_list|()
return|;
block|}
for|for
control|(
name|RelHint
name|hint
range|:
name|hints
control|)
block|{
if|if
condition|(
literal|"index"
operator|.
name|equalsIgnoreCase
argument_list|(
name|hint
operator|.
name|hintName
argument_list|)
condition|)
block|{
if|if
condition|(
name|CollectionUtils
operator|.
name|isNotEmpty
argument_list|(
name|hint
operator|.
name|listOptions
argument_list|)
condition|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|indexesNameSet
init|=
name|innodbTable
operator|.
name|getIndexesNameSet
argument_list|()
decl_stmt|;
name|Optional
argument_list|<
name|String
argument_list|>
name|forceIndexName
init|=
name|hint
operator|.
name|listOptions
operator|.
name|stream
argument_list|()
operator|.
name|findFirst
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|forceIndexName
operator|.
name|isPresent
argument_list|()
condition|)
block|{
return|return
name|Optional
operator|.
name|empty
argument_list|()
return|;
block|}
for|for
control|(
name|String
name|indexName
range|:
name|indexesNameSet
control|)
block|{
if|if
condition|(
name|indexName
operator|!=
literal|null
operator|&&
name|indexName
operator|.
name|equalsIgnoreCase
argument_list|(
name|forceIndexName
operator|.
name|get
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|Optional
operator|.
name|of
argument_list|(
name|indexName
argument_list|)
return|;
block|}
block|}
block|}
block|}
block|}
return|return
name|Optional
operator|.
name|empty
argument_list|()
return|;
block|}
specifier|public
name|String
name|getForceIndexName
parameter_list|()
block|{
return|return
name|forceIndexName
return|;
block|}
specifier|private
name|IndexCondition
name|getIndexCondition
parameter_list|()
block|{
comment|// force to use a secondary index to scan table if present
if|if
condition|(
name|forceIndexName
operator|!=
literal|null
operator|&&
operator|!
name|forceIndexName
operator|.
name|equalsIgnoreCase
argument_list|(
name|Constants
operator|.
name|PRIMARY_KEY_NAME
argument_list|)
condition|)
block|{
name|KeyMeta
name|skMeta
init|=
name|innodbTable
operator|.
name|getTableDef
argument_list|()
operator|.
name|getSecondaryKeyMetaMap
argument_list|()
operator|.
name|get
argument_list|(
name|forceIndexName
argument_list|)
decl_stmt|;
if|if
condition|(
name|skMeta
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"secondary index not found "
operator|+
name|forceIndexName
argument_list|)
throw|;
block|}
return|return
name|IndexCondition
operator|.
name|create
argument_list|(
name|InnodbRules
operator|.
name|innodbFieldNames
argument_list|(
name|getRowType
argument_list|()
argument_list|)
argument_list|,
name|forceIndexName
argument_list|,
name|skMeta
operator|.
name|getKeyColumnNames
argument_list|()
argument_list|,
name|QueryType
operator|.
name|SK_FULL_SCAN
argument_list|)
return|;
block|}
comment|// by default clustering index will be used to scan table
return|return
name|IndexCondition
operator|.
name|create
argument_list|(
name|InnodbRules
operator|.
name|innodbFieldNames
argument_list|(
name|getRowType
argument_list|()
argument_list|)
argument_list|,
name|Constants
operator|.
name|PRIMARY_KEY_NAME
argument_list|,
name|innodbTable
operator|.
name|getTableDef
argument_list|()
operator|.
name|getPrimaryKeyColumnNames
argument_list|()
argument_list|,
name|QueryType
operator|.
name|PK_FULL_SCAN
argument_list|)
return|;
block|}
block|}
end_class

end_unit

