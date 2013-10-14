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
name|materialize
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
name|Expression
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
name|clone
operator|.
name|CloneSchema
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
name|OptiqPrepare
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
name|Prepare
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
name|sql
operator|.
name|Connection
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
comment|/**  * Manages the collection of materialized tables known to the system,  * and the process by which they become valid and invalid.  */
end_comment

begin_class
specifier|public
class|class
name|MaterializationService
block|{
specifier|public
specifier|static
specifier|final
name|MaterializationService
name|INSTANCE
init|=
operator|new
name|MaterializationService
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|MaterializationActor
name|actor
init|=
operator|new
name|MaterializationActor
argument_list|()
decl_stmt|;
specifier|private
name|MaterializationService
parameter_list|()
block|{
block|}
comment|/** Defines a new materialization. Returns its key. */
specifier|public
name|MaterializationKey
name|defineMaterialization
parameter_list|(
specifier|final
name|Schema
name|schema
parameter_list|,
name|String
name|viewSql
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|viewSchemaPath
parameter_list|,
name|String
name|tableName
parameter_list|)
block|{
specifier|final
name|MaterializationKey
name|key
init|=
operator|new
name|MaterializationKey
argument_list|()
decl_stmt|;
name|Schema
operator|.
name|TableInSchema
name|materializedTable
decl_stmt|;
name|RelDataType
name|rowType
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|tableName
operator|!=
literal|null
condition|)
block|{
name|materializedTable
operator|=
name|schema
operator|.
name|getTables
argument_list|()
operator|.
name|get
argument_list|(
name|tableName
argument_list|)
expr_stmt|;
if|if
condition|(
name|materializedTable
operator|==
literal|null
condition|)
block|{
specifier|final
name|OptiqPrepare
operator|.
name|PrepareResult
argument_list|<
name|Object
argument_list|>
name|prepareResult
init|=
name|Schemas
operator|.
name|prepare
argument_list|(
name|schema
argument_list|,
name|viewSchemaPath
argument_list|,
name|viewSql
argument_list|)
decl_stmt|;
name|materializedTable
operator|=
name|CloneSchema
operator|.
name|createCloneTable
argument_list|(
operator|(
name|MutableSchema
operator|)
name|schema
argument_list|,
name|tableName
argument_list|,
name|prepareResult
operator|.
name|rowType
argument_list|,
operator|new
name|AbstractQueryable
argument_list|<
name|Object
argument_list|>
argument_list|()
block|{
specifier|public
name|Type
name|getElementType
parameter_list|()
block|{
return|return
name|prepareResult
operator|.
name|resultClazz
return|;
block|}
specifier|public
name|Expression
name|getExpression
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|QueryProvider
name|getProvider
parameter_list|()
block|{
return|return
name|schema
operator|.
name|getQueryProvider
argument_list|()
return|;
block|}
specifier|public
name|Iterator
argument_list|<
name|Object
argument_list|>
name|iterator
parameter_list|()
block|{
specifier|final
name|DataContext
name|dataContext
init|=
name|Schemas
operator|.
name|createDataContext
argument_list|(
operator|(
name|Connection
operator|)
name|schema
operator|.
name|getQueryProvider
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|prepareResult
operator|.
name|iterator
argument_list|(
name|dataContext
argument_list|)
return|;
block|}
specifier|public
name|Enumerator
argument_list|<
name|Object
argument_list|>
name|enumerator
parameter_list|()
block|{
specifier|final
name|DataContext
name|dataContext
init|=
name|Schemas
operator|.
name|createDataContext
argument_list|(
operator|(
name|Connection
operator|)
name|schema
operator|.
name|getQueryProvider
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|prepareResult
operator|.
name|enumerator
argument_list|(
name|dataContext
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
operator|(
operator|(
name|MutableSchema
operator|)
name|schema
operator|)
operator|.
name|addTable
argument_list|(
name|materializedTable
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|materializedTable
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|rowType
operator|==
literal|null
condition|)
block|{
comment|// If we didn't validate the SQL by populating a table, validate it now.
specifier|final
name|OptiqPrepare
operator|.
name|ParseResult
name|parse
init|=
name|Schemas
operator|.
name|parse
argument_list|(
name|schema
argument_list|,
name|viewSchemaPath
argument_list|,
name|viewSql
argument_list|)
decl_stmt|;
name|rowType
operator|=
name|parse
operator|.
name|rowType
expr_stmt|;
block|}
specifier|final
name|MaterializationActor
operator|.
name|Materialization
name|materialization
init|=
operator|new
name|MaterializationActor
operator|.
name|Materialization
argument_list|(
name|key
argument_list|,
name|Schemas
operator|.
name|root
argument_list|(
name|schema
argument_list|)
argument_list|,
name|materializedTable
argument_list|,
name|viewSql
argument_list|,
name|rowType
argument_list|)
decl_stmt|;
name|actor
operator|.
name|keyMap
operator|.
name|put
argument_list|(
name|materialization
operator|.
name|key
argument_list|,
name|materialization
argument_list|)
expr_stmt|;
return|return
name|key
return|;
block|}
comment|/** Checks whether a materialization is valid, and if so, returns the table    * where the data are stored. */
specifier|public
name|Schema
operator|.
name|TableInSchema
name|checkValid
parameter_list|(
name|MaterializationKey
name|key
parameter_list|)
block|{
specifier|final
name|MaterializationActor
operator|.
name|Materialization
name|materialization
init|=
name|actor
operator|.
name|keyMap
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|materialization
operator|!=
literal|null
condition|)
block|{
return|return
name|materialization
operator|.
name|materializedTable
return|;
block|}
return|return
literal|null
return|;
block|}
comment|/** Gathers a list of all materialized tables known within a given root    * schema. (Each root schema defines a disconnected namespace, with no overlap    * with the current schema. Especially in a test run, the contents of two    * root schemas may look similar.) */
specifier|public
name|List
argument_list|<
name|Prepare
operator|.
name|Materialization
argument_list|>
name|query
parameter_list|(
name|Schema
name|rootSchema
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|Prepare
operator|.
name|Materialization
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<
name|Prepare
operator|.
name|Materialization
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|MaterializationActor
operator|.
name|Materialization
name|materialization
range|:
name|actor
operator|.
name|keyMap
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|materialization
operator|.
name|rootSchema
operator|==
name|rootSchema
operator|&&
name|materialization
operator|.
name|materializedTable
operator|!=
literal|null
condition|)
block|{
name|list
operator|.
name|add
argument_list|(
operator|new
name|Prepare
operator|.
name|Materialization
argument_list|(
name|materialization
operator|.
name|materializedTable
argument_list|,
name|materialization
operator|.
name|sql
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|list
return|;
block|}
comment|/** De-registers all materialized tables in the system. */
specifier|public
name|void
name|clear
parameter_list|()
block|{
name|actor
operator|.
name|keyMap
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End MaterializationService.java
end_comment

end_unit

