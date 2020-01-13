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
name|schema
operator|.
name|Table
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
comment|/**  * A<code>LogicalTableScan</code> reads all the rows from a  * {@link RelOptTable}.  *  *<p>If the table is a<code>net.sf.saffron.ext.JdbcTable</code>, then this is  * literally possible. But for other kinds of tables, there may be many ways to  * read the data from the table. For some kinds of table, it may not even be  * possible to read all of the rows unless some narrowing constraint is applied.  *  *<p>In the example of the<code>net.sf.saffron.ext.ReflectSchema</code>  * schema,</p>  *  *<blockquote>  *<pre>select from fields</pre>  *</blockquote>  *  *<p>cannot be implemented, but</p>  *  *<blockquote>  *<pre>select from fields as f  * where f.getClass().getName().equals("java.lang.String")</pre>  *</blockquote>  *  *<p>can. It is the optimizer's responsibility to find these ways, by applying  * transformation rules.</p>  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|LogicalTableScan
extends|extends
name|TableScan
block|{
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Creates a LogicalTableScan.    *    *<p>Use {@link #create} unless you know what you're doing.    */
specifier|public
name|LogicalTableScan
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|RelTraitSet
name|traitSet
parameter_list|,
name|List
argument_list|<
name|RelHint
argument_list|>
name|hints
parameter_list|,
name|RelOptTable
name|table
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
block|}
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|public
name|LogicalTableScan
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|RelTraitSet
name|traitSet
parameter_list|,
name|RelOptTable
name|table
parameter_list|)
block|{
name|this
argument_list|(
name|cluster
argument_list|,
name|traitSet
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|()
argument_list|,
name|table
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|public
name|LogicalTableScan
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|RelOptTable
name|table
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
name|Convention
operator|.
name|NONE
argument_list|)
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|()
argument_list|,
name|table
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a LogicalTableScan by parsing serialized output.    */
specifier|public
name|LogicalTableScan
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
name|traitSet
operator|.
name|containsIfApplicable
argument_list|(
name|Convention
operator|.
name|NONE
argument_list|)
assert|;
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
comment|/** Creates a LogicalTableScan.    *    * @param cluster     Cluster    * @param relOptTable Table    * @param hints       The hints    */
specifier|public
specifier|static
name|LogicalTableScan
name|create
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
specifier|final
name|RelOptTable
name|relOptTable
parameter_list|,
name|List
argument_list|<
name|RelHint
argument_list|>
name|hints
parameter_list|)
block|{
specifier|final
name|Table
name|table
init|=
name|relOptTable
operator|.
name|unwrap
argument_list|(
name|Table
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
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
block|{
if|if
condition|(
name|table
operator|!=
literal|null
condition|)
block|{
return|return
name|table
operator|.
name|getStatistic
argument_list|()
operator|.
name|getCollations
argument_list|()
return|;
block|}
return|return
name|ImmutableList
operator|.
name|of
argument_list|()
return|;
block|}
argument_list|)
decl_stmt|;
return|return
operator|new
name|LogicalTableScan
argument_list|(
name|cluster
argument_list|,
name|traitSet
argument_list|,
name|hints
argument_list|,
name|relOptTable
argument_list|)
return|;
block|}
comment|/** Creates a LogicalTableScan.    *    * @param cluster Cluster    * @param relOptTable Table    */
specifier|public
specifier|static
name|LogicalTableScan
name|create
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
specifier|final
name|RelOptTable
name|relOptTable
parameter_list|)
block|{
return|return
name|create
argument_list|(
name|cluster
argument_list|,
name|relOptTable
argument_list|,
operator|new
name|ArrayList
argument_list|<>
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|RelNode
name|withHints
parameter_list|(
name|List
argument_list|<
name|RelHint
argument_list|>
name|hintList
parameter_list|)
block|{
return|return
operator|new
name|LogicalTableScan
argument_list|(
name|getCluster
argument_list|()
argument_list|,
name|traitSet
argument_list|,
name|hintList
argument_list|,
name|table
argument_list|)
return|;
block|}
block|}
end_class

end_unit

