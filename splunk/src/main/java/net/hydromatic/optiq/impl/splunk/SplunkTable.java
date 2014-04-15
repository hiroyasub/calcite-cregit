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
name|AbstractTableQueryable
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
name|java
operator|.
name|AbstractQueryableTable
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
name|java
operator|.
name|JavaTypeFactory
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
name|RelNode
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
name|RelOptTable
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
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  * Table based on Splunk.  */
end_comment

begin_class
class|class
name|SplunkTable
extends|extends
name|AbstractQueryableTable
implements|implements
name|TranslatableTable
block|{
specifier|public
specifier|static
specifier|final
name|SplunkTable
name|INSTANCE
init|=
operator|new
name|SplunkTable
argument_list|()
decl_stmt|;
specifier|private
name|SplunkTable
parameter_list|()
block|{
name|super
argument_list|(
name|Object
index|[]
operator|.
expr|class
argument_list|)
expr_stmt|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"SplunkTable"
return|;
block|}
specifier|public
name|RelDataType
name|getRowType
parameter_list|(
name|RelDataTypeFactory
name|typeFactory
parameter_list|)
block|{
name|RelDataType
name|stringType
init|=
operator|(
operator|(
name|JavaTypeFactory
operator|)
name|typeFactory
operator|)
operator|.
name|createType
argument_list|(
name|String
operator|.
name|class
argument_list|)
decl_stmt|;
return|return
name|typeFactory
operator|.
name|builder
argument_list|()
operator|.
name|add
argument_list|(
literal|"source"
argument_list|,
name|stringType
argument_list|)
operator|.
name|add
argument_list|(
literal|"sourcetype"
argument_list|,
name|stringType
argument_list|)
operator|.
name|add
argument_list|(
literal|"_extra"
argument_list|,
name|stringType
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
specifier|public
parameter_list|<
name|T
parameter_list|>
name|Queryable
argument_list|<
name|T
argument_list|>
name|asQueryable
parameter_list|(
name|QueryProvider
name|queryProvider
parameter_list|,
name|SchemaPlus
name|schema
parameter_list|,
name|String
name|tableName
parameter_list|)
block|{
return|return
operator|new
name|SplunkTableQueryable
argument_list|<
name|T
argument_list|>
argument_list|(
name|queryProvider
argument_list|,
name|schema
argument_list|,
name|this
argument_list|,
name|tableName
argument_list|)
return|;
block|}
specifier|public
name|RelNode
name|toRel
parameter_list|(
name|RelOptTable
operator|.
name|ToRelContext
name|context
parameter_list|,
name|RelOptTable
name|relOptTable
parameter_list|)
block|{
return|return
operator|new
name|SplunkTableAccessRel
argument_list|(
name|context
operator|.
name|getCluster
argument_list|()
argument_list|,
name|relOptTable
argument_list|,
name|this
argument_list|,
literal|"search"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|relOptTable
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldNames
argument_list|()
argument_list|)
return|;
block|}
comment|/** Implementation of {@link Queryable} backed by a {@link SplunkTable}.    * Generated code uses this get a Splunk connection for executing arbitrary    * Splunk queries. */
specifier|public
specifier|static
class|class
name|SplunkTableQueryable
parameter_list|<
name|T
parameter_list|>
extends|extends
name|AbstractTableQueryable
argument_list|<
name|T
argument_list|>
block|{
specifier|public
name|SplunkTableQueryable
parameter_list|(
name|QueryProvider
name|queryProvider
parameter_list|,
name|SchemaPlus
name|schema
parameter_list|,
name|SplunkTable
name|table
parameter_list|,
name|String
name|tableName
parameter_list|)
block|{
name|super
argument_list|(
name|queryProvider
argument_list|,
name|schema
argument_list|,
name|table
argument_list|,
name|tableName
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Enumerator
argument_list|<
name|T
argument_list|>
name|enumerator
parameter_list|()
block|{
specifier|final
name|SplunkQuery
argument_list|<
name|T
argument_list|>
name|query
init|=
name|createQuery
argument_list|(
literal|"search"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
return|return
name|query
operator|.
name|enumerator
argument_list|()
return|;
block|}
specifier|public
name|SplunkQuery
argument_list|<
name|T
argument_list|>
name|createQuery
parameter_list|(
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
specifier|final
name|SplunkSchema
name|splunkSchema
init|=
name|schema
operator|.
name|unwrap
argument_list|(
name|SplunkSchema
operator|.
name|class
argument_list|)
decl_stmt|;
return|return
operator|new
name|SplunkQuery
argument_list|<
name|T
argument_list|>
argument_list|(
name|splunkSchema
operator|.
name|splunkConnection
argument_list|,
name|search
argument_list|,
name|earliest
argument_list|,
name|latest
argument_list|,
name|fieldList
argument_list|)
return|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End SplunkTable.java
end_comment

end_unit

