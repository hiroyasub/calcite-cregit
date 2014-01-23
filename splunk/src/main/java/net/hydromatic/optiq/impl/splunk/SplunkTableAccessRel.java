begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to Julian Hyde under one or more contributor license // agreements. See the NOTICE file distributed with this work for // additional information regarding copyright ownership. // // Julian Hyde licenses this file to you under the Apache License, // Version 2.0 (the "License"); you may not use this file except in // compliance with the License. You may obtain a copy of the License at: // // http://www.apache.org/licenses/LICENSE-2.0 // // Unless required by applicable law or agreed to in writing, software // distributed under the License is distributed on an "AS IS" BASIS, // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. // See the License for the specific language governing permissions and // limitations under the License. */
end_comment

begin_package
package|package
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|impl
operator|.
name|splunk
package|;
end_package

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|linq4j
operator|.
name|expressions
operator|.
name|*
import|;
end_import

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|impl
operator|.
name|splunk
operator|.
name|search
operator|.
name|SplunkConnection
import|;
end_import

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|prepare
operator|.
name|OptiqPrepareImpl
import|;
end_import

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|rules
operator|.
name|java
operator|.
name|*
import|;
end_import

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|runtime
operator|.
name|Hook
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
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
name|eigenbase
operator|.
name|rel
operator|.
name|TableAccessRelBase
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|relopt
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|reltype
operator|.
name|RelDataType
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|reltype
operator|.
name|RelDataTypeFactory
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
name|ImmutableMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Constructor
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Relational expression representing a scan of Splunk.  *  *<p>Splunk does not have tables, but it's easiest to imagine that a Splunk  * instance is one large table. This "table" does not have a fixed set of  * columns (Splunk calls them "fields") but each query specifies the fields that  * it wants. It also specifies a search expression, and optionally earliest&  * latest dates.</p>  */
end_comment

begin_class
specifier|public
class|class
name|SplunkTableAccessRel
extends|extends
name|TableAccessRelBase
implements|implements
name|EnumerableRel
block|{
specifier|final
name|SplunkTable
name|splunkTable
decl_stmt|;
specifier|final
name|String
name|search
decl_stmt|;
specifier|final
name|String
name|earliest
decl_stmt|;
specifier|final
name|String
name|latest
decl_stmt|;
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|fieldList
decl_stmt|;
specifier|protected
name|SplunkTableAccessRel
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|RelOptTable
name|table
parameter_list|,
name|SplunkTable
name|splunkTable
parameter_list|,
name|String
name|search
parameter_list|,
name|String
name|earliest
parameter_list|,
name|String
name|latest
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|fieldList
parameter_list|)
block|{
name|super
argument_list|(
name|cluster
argument_list|,
name|cluster
operator|.
name|traitSetOf
argument_list|(
name|EnumerableConvention
operator|.
name|INSTANCE
argument_list|)
argument_list|,
name|table
argument_list|)
expr_stmt|;
name|this
operator|.
name|splunkTable
operator|=
name|splunkTable
expr_stmt|;
name|this
operator|.
name|search
operator|=
name|search
expr_stmt|;
name|this
operator|.
name|earliest
operator|=
name|earliest
expr_stmt|;
name|this
operator|.
name|latest
operator|=
name|latest
expr_stmt|;
name|this
operator|.
name|fieldList
operator|=
name|fieldList
expr_stmt|;
assert|assert
name|splunkTable
operator|!=
literal|null
assert|;
assert|assert
name|search
operator|!=
literal|null
assert|;
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
name|item
argument_list|(
literal|"table"
argument_list|,
name|table
operator|.
name|getQualifiedName
argument_list|()
argument_list|)
operator|.
name|item
argument_list|(
literal|"earliest"
argument_list|,
name|earliest
argument_list|)
operator|.
name|item
argument_list|(
literal|"latest"
argument_list|,
name|latest
argument_list|)
operator|.
name|item
argument_list|(
literal|"fieldList"
argument_list|,
name|fieldList
argument_list|)
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
name|planner
operator|.
name|addRule
argument_list|(
name|SplunkPushDownRule
operator|.
name|FILTER
argument_list|)
expr_stmt|;
name|planner
operator|.
name|addRule
argument_list|(
name|SplunkPushDownRule
operator|.
name|FILTER_ON_PROJECT
argument_list|)
expr_stmt|;
name|planner
operator|.
name|addRule
argument_list|(
name|SplunkPushDownRule
operator|.
name|PROJECT
argument_list|)
expr_stmt|;
name|planner
operator|.
name|addRule
argument_list|(
name|SplunkPushDownRule
operator|.
name|PROJECT_ON_FILTER
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|RelDataType
name|deriveRowType
parameter_list|()
block|{
specifier|final
name|RelDataTypeFactory
operator|.
name|FieldInfoBuilder
name|builder
init|=
name|getCluster
argument_list|()
operator|.
name|getTypeFactory
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|field
range|:
name|fieldList
control|)
block|{
comment|// REVIEW: is case-sensitive match what we want here?
name|builder
operator|.
name|add
argument_list|(
name|table
operator|.
name|getRowType
argument_list|()
operator|.
name|getField
argument_list|(
name|field
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
operator|.
name|build
argument_list|()
return|;
block|}
specifier|private
specifier|static
specifier|final
name|Constructor
name|CONSTRUCTOR
init|=
name|Types
operator|.
name|lookupConstructor
argument_list|(
name|SplunkQuery
operator|.
name|class
argument_list|,
name|SplunkConnection
operator|.
name|class
argument_list|,
name|String
operator|.
name|class
argument_list|,
name|String
operator|.
name|class
argument_list|,
name|String
operator|.
name|class
argument_list|,
name|List
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
name|Result
name|implement
parameter_list|(
name|EnumerableRelImplementor
name|implementor
parameter_list|,
name|Prefer
name|pref
parameter_list|)
block|{
name|Map
name|map
init|=
name|ImmutableMap
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"search"
argument_list|,
name|search
argument_list|)
operator|.
name|put
argument_list|(
literal|"earliest"
argument_list|,
name|earliest
argument_list|)
operator|.
name|put
argument_list|(
literal|"latest"
argument_list|,
name|latest
argument_list|)
operator|.
name|put
argument_list|(
literal|"fieldList"
argument_list|,
name|fieldList
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
if|if
condition|(
name|OptiqPrepareImpl
operator|.
name|DEBUG
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Splunk: "
operator|+
name|map
argument_list|)
expr_stmt|;
block|}
name|Hook
operator|.
name|QUERY_PLAN
operator|.
name|run
argument_list|(
name|map
argument_list|)
expr_stmt|;
specifier|final
name|PhysType
name|physType
init|=
name|PhysTypeImpl
operator|.
name|of
argument_list|(
name|implementor
operator|.
name|getTypeFactory
argument_list|()
argument_list|,
name|getRowType
argument_list|()
argument_list|,
name|pref
operator|.
name|preferCustom
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|BlockBuilder
name|builder
init|=
operator|new
name|BlockBuilder
argument_list|()
decl_stmt|;
return|return
name|implementor
operator|.
name|result
argument_list|(
name|physType
argument_list|,
name|builder
operator|.
name|append
argument_list|(
name|Expressions
operator|.
name|new_
argument_list|(
name|CONSTRUCTOR
argument_list|,
name|Expressions
operator|.
name|field
argument_list|(
name|Types
operator|.
name|castIfNecessary
argument_list|(
name|SplunkSchema
operator|.
name|class
argument_list|,
name|splunkTable
operator|.
name|schema
operator|.
name|getExpression
argument_list|()
argument_list|)
argument_list|,
literal|"splunkConnection"
argument_list|)
argument_list|,
name|Expressions
operator|.
name|constant
argument_list|(
name|search
argument_list|)
argument_list|,
name|Expressions
operator|.
name|constant
argument_list|(
name|earliest
argument_list|)
argument_list|,
name|Expressions
operator|.
name|constant
argument_list|(
name|latest
argument_list|)
argument_list|,
name|fieldList
operator|==
literal|null
condition|?
name|Expressions
operator|.
name|constant
argument_list|(
literal|null
argument_list|)
else|:
name|constantStringList
argument_list|(
name|fieldList
argument_list|)
argument_list|)
argument_list|)
operator|.
name|toBlock
argument_list|()
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|Expression
name|constantStringList
parameter_list|(
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|strings
parameter_list|)
block|{
return|return
name|Expressions
operator|.
name|call
argument_list|(
name|Arrays
operator|.
name|class
argument_list|,
literal|"asList"
argument_list|,
name|Expressions
operator|.
name|newArrayInit
argument_list|(
name|Object
operator|.
name|class
argument_list|,
operator|new
name|AbstractList
argument_list|<
name|Expression
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Expression
name|get
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
name|Expressions
operator|.
name|constant
argument_list|(
name|strings
operator|.
name|get
argument_list|(
name|index
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|strings
operator|.
name|size
argument_list|()
return|;
block|}
block|}
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|// End SplunkTableAccessRel.java
end_comment

end_unit

