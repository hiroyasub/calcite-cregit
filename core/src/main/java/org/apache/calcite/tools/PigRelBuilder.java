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
name|tools
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
name|Ord
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
name|Context
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
name|RelOptSchema
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
name|JoinRelType
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
name|sql
operator|.
name|fun
operator|.
name|SqlStdOperatorTable
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
name|ArrayList
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
comment|/**  * Extension to {@link RelBuilder} for Pig relational operators.  */
end_comment

begin_class
specifier|public
class|class
name|PigRelBuilder
extends|extends
name|RelBuilder
block|{
specifier|private
name|String
name|lastAlias
decl_stmt|;
specifier|private
name|PigRelBuilder
parameter_list|(
name|Context
name|context
parameter_list|,
name|RelOptCluster
name|cluster
parameter_list|,
name|RelOptSchema
name|relOptSchema
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|,
name|cluster
argument_list|,
name|relOptSchema
argument_list|)
expr_stmt|;
block|}
comment|/** Creates a PigRelBuilder. */
specifier|public
specifier|static
name|PigRelBuilder
name|create
parameter_list|(
name|FrameworkConfig
name|config
parameter_list|)
block|{
specifier|final
name|RelBuilder
name|relBuilder
init|=
name|RelBuilder
operator|.
name|create
argument_list|(
name|config
argument_list|)
decl_stmt|;
return|return
operator|new
name|PigRelBuilder
argument_list|(
name|config
operator|.
name|getContext
argument_list|()
argument_list|,
name|relBuilder
operator|.
name|cluster
argument_list|,
name|relBuilder
operator|.
name|relOptSchema
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|PigRelBuilder
name|scan
parameter_list|(
name|String
modifier|...
name|tableNames
parameter_list|)
block|{
name|lastAlias
operator|=
literal|null
expr_stmt|;
return|return
operator|(
name|PigRelBuilder
operator|)
name|super
operator|.
name|scan
argument_list|(
name|tableNames
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|PigRelBuilder
name|scan
parameter_list|(
name|Iterable
argument_list|<
name|String
argument_list|>
name|tableNames
parameter_list|)
block|{
name|lastAlias
operator|=
literal|null
expr_stmt|;
return|return
operator|(
name|PigRelBuilder
operator|)
name|super
operator|.
name|scan
argument_list|(
name|tableNames
argument_list|)
return|;
block|}
comment|/** Loads a data set.    *    *<p>Equivalent to Pig Latin:    *<pre>{@code LOAD 'path' USING loadFunction AS rowType}</pre>    *    *<p>{@code loadFunction} and {@code rowType} are optional.    *    * @param path File path    * @param loadFunction Load function    * @param rowType Row type (what Pig calls 'schema')    *    * @return This builder    */
specifier|public
name|PigRelBuilder
name|load
parameter_list|(
name|String
name|path
parameter_list|,
name|RexNode
name|loadFunction
parameter_list|,
name|RelDataType
name|rowType
parameter_list|)
block|{
name|scan
argument_list|(
name|path
operator|.
name|replace
argument_list|(
literal|".csv"
argument_list|,
literal|""
argument_list|)
argument_list|)
expr_stmt|;
comment|// TODO: use a UDT
return|return
name|this
return|;
block|}
comment|/** Removes duplicate tuples in a relation.    *    *<p>Equivalent Pig Latin:    *<blockquote>    *<pre>alias = DISTINCT alias [PARTITION BY partitioner] [PARALLEL n];</pre>    *</blockquote>    *    * @param partitioner Partitioner; null means no partitioner    * @param parallel Degree of parallelism; negative means unspecified    *    * @return This builder    */
specifier|public
name|PigRelBuilder
name|distinct
parameter_list|(
name|Partitioner
name|partitioner
parameter_list|,
name|int
name|parallel
parameter_list|)
block|{
comment|// TODO: Use partitioner and parallel
name|distinct
argument_list|()
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/** Groups the data in one or more relations.    *    *<p>Pig Latin syntax:    *<blockquote>    * alias = GROUP alias { ALL | BY expression }    *   [, alias ALL | BY expression ...]    *   [USING 'collected' | 'merge'] [PARTITION BY partitioner] [PARALLEL n];    *</blockquote>    *    * @param groupKeys One of more group keys; use {@link #groupKey()} for ALL    * @param option Whether to use an optimized method combining the data    *              (COLLECTED for one input or MERGE for two or more inputs)    * @param partitioner Partitioner; null means no partitioner    * @param parallel Degree of parallelism; negative means unspecified    *    * @return This builder    */
specifier|public
name|PigRelBuilder
name|group
parameter_list|(
name|GroupOption
name|option
parameter_list|,
name|Partitioner
name|partitioner
parameter_list|,
name|int
name|parallel
parameter_list|,
name|GroupKey
modifier|...
name|groupKeys
parameter_list|)
block|{
return|return
name|group
argument_list|(
name|option
argument_list|,
name|partitioner
argument_list|,
name|parallel
argument_list|,
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|groupKeys
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|PigRelBuilder
name|group
parameter_list|(
name|GroupOption
name|option
parameter_list|,
name|Partitioner
name|partitioner
parameter_list|,
name|int
name|parallel
parameter_list|,
name|Iterable
argument_list|<
name|?
extends|extends
name|GroupKey
argument_list|>
name|groupKeys
parameter_list|)
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|final
name|List
argument_list|<
name|GroupKeyImpl
argument_list|>
name|groupKeyList
init|=
name|ImmutableList
operator|.
name|copyOf
argument_list|(
operator|(
name|Iterable
operator|)
name|groupKeys
argument_list|)
decl_stmt|;
if|if
condition|(
name|groupKeyList
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"must have at least one group"
argument_list|)
throw|;
block|}
specifier|final
name|int
name|groupCount
init|=
name|groupKeyList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|nodes
operator|.
name|size
argument_list|()
decl_stmt|;
for|for
control|(
name|GroupKeyImpl
name|groupKey
range|:
name|groupKeyList
control|)
block|{
if|if
condition|(
name|groupKey
operator|.
name|nodes
operator|.
name|size
argument_list|()
operator|!=
name|groupCount
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"group key size mismatch"
argument_list|)
throw|;
block|}
block|}
specifier|final
name|int
name|n
init|=
name|groupKeyList
operator|.
name|size
argument_list|()
decl_stmt|;
for|for
control|(
name|Ord
argument_list|<
name|GroupKeyImpl
argument_list|>
name|groupKey
range|:
name|Ord
operator|.
name|reverse
argument_list|(
name|groupKeyList
argument_list|)
control|)
block|{
name|RelNode
name|r
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|groupKey
operator|.
name|i
operator|<
name|n
operator|-
literal|1
condition|)
block|{
name|r
operator|=
name|build
argument_list|()
expr_stmt|;
block|}
comment|// Create a ROW to pass to COLLECT. Interestingly, this is not allowed
comment|// by standard SQL; see [CALCITE-877] Allow ROW as argument to COLLECT.
specifier|final
name|RexNode
name|row
init|=
name|cluster
operator|.
name|getRexBuilder
argument_list|()
operator|.
name|makeCall
argument_list|(
name|peek
argument_list|(
literal|1
argument_list|,
literal|0
argument_list|)
operator|.
name|getRowType
argument_list|()
argument_list|,
name|SqlStdOperatorTable
operator|.
name|ROW
argument_list|,
name|fields
argument_list|()
argument_list|)
decl_stmt|;
name|aggregate
argument_list|(
name|groupKey
operator|.
name|e
argument_list|,
name|aggregateCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|COLLECT
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|,
name|getAlias
argument_list|()
argument_list|,
name|row
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|groupKey
operator|.
name|i
operator|<
name|n
operator|-
literal|1
condition|)
block|{
name|push
argument_list|(
name|r
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|RexNode
argument_list|>
name|predicates
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|key
range|:
name|Util
operator|.
name|range
argument_list|(
name|groupCount
argument_list|)
control|)
block|{
name|predicates
operator|.
name|add
argument_list|(
name|equals
argument_list|(
name|field
argument_list|(
literal|2
argument_list|,
literal|0
argument_list|,
name|key
argument_list|)
argument_list|,
name|field
argument_list|(
literal|2
argument_list|,
literal|1
argument_list|,
name|key
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|join
argument_list|(
name|JoinRelType
operator|.
name|INNER
argument_list|,
name|and
argument_list|(
name|predicates
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|this
return|;
block|}
name|String
name|getAlias
parameter_list|()
block|{
if|if
condition|(
name|lastAlias
operator|!=
literal|null
condition|)
block|{
return|return
name|lastAlias
return|;
block|}
else|else
block|{
name|RelNode
name|top
init|=
name|peek
argument_list|()
decl_stmt|;
if|if
condition|(
name|top
operator|instanceof
name|TableScan
condition|)
block|{
return|return
name|Util
operator|.
name|last
argument_list|(
name|top
operator|.
name|getTable
argument_list|()
operator|.
name|getQualifiedName
argument_list|()
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
block|}
comment|/** As super-class method, but also retains alias for naming of aggregates. */
annotation|@
name|Override
specifier|public
name|RelBuilder
name|as
parameter_list|(
specifier|final
name|String
name|alias
parameter_list|)
block|{
name|lastAlias
operator|=
name|alias
expr_stmt|;
return|return
name|super
operator|.
name|as
argument_list|(
name|alias
argument_list|)
return|;
block|}
comment|/** Partitioner for group and join */
interface|interface
name|Partitioner
block|{   }
comment|/** Option for performing group efficiently if data set is already sorted */
specifier|public
enum|enum
name|GroupOption
block|{
name|MERGE
block|,
name|COLLECTED
block|}
block|}
end_class

begin_comment
comment|// End PigRelBuilder.java
end_comment

end_unit

