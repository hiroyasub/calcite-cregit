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
name|clone
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
name|jdbc
operator|.
name|OptiqConnection
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
name|Type
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
comment|/**  * Schema that contains in-memory copies of tables from a JDBC schema.  */
end_comment

begin_class
specifier|public
class|class
name|CloneSchema
extends|extends
name|MapSchema
block|{
specifier|private
specifier|final
name|Schema
name|sourceSchema
decl_stmt|;
comment|/**      * Creates a CloneSchema.      *      * @param queryProvider Query provider      * @param typeFactory Type factory      * @param expression Expression for schema      * @param sourceSchema JDBC data source      */
specifier|public
name|CloneSchema
parameter_list|(
name|QueryProvider
name|queryProvider
parameter_list|,
name|JavaTypeFactory
name|typeFactory
parameter_list|,
name|Expression
name|expression
parameter_list|,
name|Schema
name|sourceSchema
parameter_list|)
block|{
name|super
argument_list|(
name|queryProvider
argument_list|,
name|typeFactory
argument_list|,
name|expression
argument_list|)
expr_stmt|;
name|this
operator|.
name|sourceSchema
operator|=
name|sourceSchema
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Table
name|getTable
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|Table
name|table
init|=
name|super
operator|.
name|getTable
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|table
operator|!=
literal|null
condition|)
block|{
return|return
name|table
return|;
block|}
comment|// TODO: make thread safe!
name|Table
name|sourceTable
init|=
name|sourceSchema
operator|.
name|getTable
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|sourceTable
operator|!=
literal|null
condition|)
block|{
name|table
operator|=
name|createCloneTable
argument_list|(
name|sourceTable
argument_list|,
name|name
argument_list|)
expr_stmt|;
name|addTable
argument_list|(
name|name
argument_list|,
name|table
argument_list|)
expr_stmt|;
return|return
name|table
return|;
block|}
return|return
literal|null
return|;
block|}
specifier|private
parameter_list|<
name|T
parameter_list|>
name|Table
argument_list|<
name|T
argument_list|>
name|createCloneTable
parameter_list|(
name|Table
argument_list|<
name|T
argument_list|>
name|sourceTable
parameter_list|,
name|String
name|name
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|T
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<
name|T
argument_list|>
argument_list|()
decl_stmt|;
name|sourceTable
operator|.
name|into
argument_list|(
name|list
argument_list|)
expr_stmt|;
return|return
operator|new
name|ListTable
argument_list|<
name|T
argument_list|>
argument_list|(
name|this
argument_list|,
name|sourceTable
operator|.
name|getElementType
argument_list|()
argument_list|,
name|Expressions
operator|.
name|call
argument_list|(
name|getExpression
argument_list|()
argument_list|,
name|BuiltinMethod
operator|.
name|SCHEMA_GET_TABLE
operator|.
name|method
argument_list|,
name|Expressions
operator|.
name|constant
argument_list|(
name|name
argument_list|)
argument_list|)
argument_list|,
name|list
argument_list|)
return|;
block|}
comment|/**      * Creates a CloneSchema within another schema.      *      *      * @param optiqConnection Connection to Optiq (also a query provider)      * @param parentSchema Parent schema      * @param name Name of new schema      * @param sourceSchema Source schema      * @return New CloneSchema      */
specifier|public
specifier|static
name|CloneSchema
name|create
parameter_list|(
name|OptiqConnection
name|optiqConnection
parameter_list|,
name|MutableSchema
name|parentSchema
parameter_list|,
name|String
name|name
parameter_list|,
name|Schema
name|sourceSchema
parameter_list|)
block|{
name|CloneSchema
name|schema
init|=
operator|new
name|CloneSchema
argument_list|(
name|optiqConnection
argument_list|,
name|optiqConnection
operator|.
name|getTypeFactory
argument_list|()
argument_list|,
name|parentSchema
operator|.
name|getSubSchemaExpression
argument_list|(
name|name
argument_list|,
name|Object
operator|.
name|class
argument_list|)
argument_list|,
name|sourceSchema
argument_list|)
decl_stmt|;
name|parentSchema
operator|.
name|addSchema
argument_list|(
name|name
argument_list|,
name|schema
argument_list|)
expr_stmt|;
return|return
name|schema
return|;
block|}
specifier|private
specifier|static
class|class
name|ListTable
parameter_list|<
name|T
parameter_list|>
extends|extends
name|BaseQueryable
argument_list|<
name|T
argument_list|>
implements|implements
name|Table
argument_list|<
name|T
argument_list|>
block|{
specifier|private
specifier|final
name|Schema
name|schema
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|T
argument_list|>
name|list
decl_stmt|;
specifier|public
name|ListTable
parameter_list|(
name|Schema
name|schema
parameter_list|,
name|Type
name|elementType
parameter_list|,
name|Expression
name|expression
parameter_list|,
name|List
argument_list|<
name|T
argument_list|>
name|list
parameter_list|)
block|{
name|super
argument_list|(
name|schema
operator|.
name|getQueryProvider
argument_list|()
argument_list|,
name|elementType
argument_list|,
name|expression
argument_list|)
expr_stmt|;
name|this
operator|.
name|schema
operator|=
name|schema
expr_stmt|;
name|this
operator|.
name|list
operator|=
name|list
expr_stmt|;
block|}
specifier|public
name|DataContext
name|getDataContext
parameter_list|()
block|{
return|return
name|schema
return|;
block|}
annotation|@
name|Override
specifier|public
name|Enumerator
argument_list|<
name|T
argument_list|>
name|enumerator
parameter_list|()
block|{
return|return
name|Linq4j
operator|.
name|enumerator
argument_list|(
name|list
argument_list|)
return|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End CloneSchema.java
end_comment

end_unit

