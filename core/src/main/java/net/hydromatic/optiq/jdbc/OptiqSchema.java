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
name|jdbc
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
name|Linq4j
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
name|Table
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
name|MaterializedViewTable
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
name|util
operator|.
name|CompositeMap
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
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
name|Function
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
name|Predicate
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
name|*
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
comment|/**  * Schema.  *  *<p>Wrapper around user-defined schema used internally.</p>  */
end_comment

begin_class
specifier|public
class|class
name|OptiqSchema
block|{
specifier|private
specifier|final
name|OptiqSchema
name|parent
decl_stmt|;
specifier|public
specifier|final
name|Schema
name|schema
decl_stmt|;
comment|/** Tables explicitly defined in this schema. Does not include tables in    *  {@link #schema}. */
specifier|public
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|TableEntry
argument_list|>
name|tableMap
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|TableEntry
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|Multimap
argument_list|<
name|String
argument_list|,
name|TableFunctionEntry
argument_list|>
name|tableFunctionMap
init|=
name|LinkedListMultimap
operator|.
name|create
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|OptiqSchema
argument_list|>
name|subSchemaMap
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|OptiqSchema
argument_list|>
argument_list|()
decl_stmt|;
specifier|public
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Table
argument_list|>
name|compositeTableMap
decl_stmt|;
specifier|public
specifier|final
name|Multimap
argument_list|<
name|String
argument_list|,
name|TableFunction
argument_list|>
name|compositeTableFunctionMap
decl_stmt|;
specifier|public
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|OptiqSchema
argument_list|>
name|compositeSubSchemaMap
decl_stmt|;
specifier|public
name|OptiqSchema
parameter_list|(
name|OptiqSchema
name|parent
parameter_list|,
specifier|final
name|Schema
name|schema
parameter_list|)
block|{
name|this
operator|.
name|parent
operator|=
name|parent
expr_stmt|;
name|this
operator|.
name|schema
operator|=
name|schema
expr_stmt|;
assert|assert
operator|(
name|parent
operator|==
literal|null
operator|)
operator|==
operator|(
name|this
operator|instanceof
name|OptiqRootSchema
operator|)
assert|;
name|this
operator|.
name|compositeTableMap
operator|=
name|CompositeMap
operator|.
name|of
argument_list|(
name|Maps
operator|.
name|transformValues
argument_list|(
name|tableMap
argument_list|,
operator|new
name|Function
argument_list|<
name|TableEntry
argument_list|,
name|Table
argument_list|>
argument_list|()
block|{
specifier|public
name|Table
name|apply
parameter_list|(
name|TableEntry
name|input
parameter_list|)
block|{
return|return
name|input
operator|.
name|getTable
argument_list|()
return|;
block|}
block|}
argument_list|)
argument_list|,
name|Maps
operator|.
name|transformValues
argument_list|(
name|Multimaps
operator|.
name|filterEntries
argument_list|(
name|tableFunctionMap
argument_list|,
operator|new
name|Predicate
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|TableFunctionEntry
argument_list|>
argument_list|>
argument_list|()
block|{
specifier|public
name|boolean
name|apply
parameter_list|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|TableFunctionEntry
argument_list|>
name|input
parameter_list|)
block|{
return|return
name|input
operator|.
name|getValue
argument_list|()
operator|.
name|getTableFunction
argument_list|()
operator|.
name|getParameters
argument_list|()
operator|.
name|isEmpty
argument_list|()
return|;
block|}
block|}
argument_list|)
operator|.
name|asMap
argument_list|()
argument_list|,
operator|new
name|Function
argument_list|<
name|Collection
argument_list|<
name|TableFunctionEntry
argument_list|>
argument_list|,
name|Table
argument_list|>
argument_list|()
block|{
specifier|public
name|Table
name|apply
parameter_list|(
name|Collection
argument_list|<
name|TableFunctionEntry
argument_list|>
name|input
parameter_list|)
block|{
comment|// At most one function with zero parameters.
name|TableFunctionEntry
name|entry
init|=
name|input
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
decl_stmt|;
return|return
name|entry
operator|.
name|getTableFunction
argument_list|()
operator|.
name|apply
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|()
argument_list|)
return|;
block|}
block|}
argument_list|)
argument_list|,
name|Maps
operator|.
name|asMap
argument_list|(
name|schema
operator|.
name|getTableNames
argument_list|()
argument_list|,
operator|new
name|Function
argument_list|<
name|String
argument_list|,
name|Table
argument_list|>
argument_list|()
block|{
specifier|public
name|Table
name|apply
parameter_list|(
name|String
name|input
parameter_list|)
block|{
return|return
name|schema
operator|.
name|getTable
argument_list|(
name|input
argument_list|)
return|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
comment|// TODO: include schema's table functions in this map.
name|this
operator|.
name|compositeTableFunctionMap
operator|=
name|Multimaps
operator|.
name|transformValues
argument_list|(
name|tableFunctionMap
argument_list|,
operator|new
name|Function
argument_list|<
name|TableFunctionEntry
argument_list|,
name|TableFunction
argument_list|>
argument_list|()
block|{
specifier|public
name|TableFunction
name|apply
parameter_list|(
name|TableFunctionEntry
name|input
parameter_list|)
block|{
return|return
name|input
operator|.
name|getTableFunction
argument_list|()
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|this
operator|.
name|compositeSubSchemaMap
operator|=
name|CompositeMap
operator|.
name|of
argument_list|(
name|subSchemaMap
argument_list|,
name|Maps
operator|.
expr|<
name|String
argument_list|,
name|OptiqSchema
operator|>
name|asMap
argument_list|(
name|schema
operator|.
name|getSubSchemaNames
argument_list|()
argument_list|,
operator|new
name|Function
argument_list|<
name|String
argument_list|,
name|OptiqSchema
argument_list|>
argument_list|()
block|{
specifier|public
name|OptiqSchema
name|apply
parameter_list|(
name|String
name|input
parameter_list|)
block|{
return|return
name|addSchema
argument_list|(
name|schema
operator|.
name|getSubSchema
argument_list|(
name|input
argument_list|)
argument_list|)
return|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Defines a table within this schema. */
specifier|public
name|TableEntry
name|add
parameter_list|(
name|String
name|tableName
parameter_list|,
name|Table
name|table
parameter_list|)
block|{
specifier|final
name|TableEntryImpl
name|entry
init|=
operator|new
name|TableEntryImpl
argument_list|(
name|this
argument_list|,
name|tableName
argument_list|,
name|table
argument_list|)
decl_stmt|;
name|tableMap
operator|.
name|put
argument_list|(
name|tableName
argument_list|,
name|entry
argument_list|)
expr_stmt|;
return|return
name|entry
return|;
block|}
specifier|private
name|TableFunctionEntry
name|add
parameter_list|(
name|String
name|name
parameter_list|,
name|TableFunction
name|tableFunction
parameter_list|)
block|{
specifier|final
name|TableFunctionEntryImpl
name|entry
init|=
operator|new
name|TableFunctionEntryImpl
argument_list|(
name|this
argument_list|,
name|name
argument_list|,
name|tableFunction
argument_list|)
decl_stmt|;
name|tableFunctionMap
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|entry
argument_list|)
expr_stmt|;
return|return
name|entry
return|;
block|}
specifier|public
name|OptiqRootSchema
name|root
parameter_list|()
block|{
for|for
control|(
name|OptiqSchema
name|schema
init|=
name|this
init|;
condition|;
control|)
block|{
if|if
condition|(
name|schema
operator|.
name|parent
operator|==
literal|null
condition|)
block|{
return|return
operator|(
name|OptiqRootSchema
operator|)
name|schema
return|;
block|}
name|schema
operator|=
name|schema
operator|.
name|parent
expr_stmt|;
block|}
block|}
specifier|public
specifier|final
name|OptiqSchema
name|getSubSchema
parameter_list|(
name|String
name|schemaName
parameter_list|)
block|{
return|return
name|subSchemaMap
operator|.
name|get
argument_list|(
name|schemaName
argument_list|)
return|;
block|}
comment|/** Adds a child schema of this schema. */
specifier|public
name|OptiqSchema
name|addSchema
parameter_list|(
name|Schema
name|schema
parameter_list|)
block|{
specifier|final
name|OptiqSchema
name|optiqSchema
init|=
operator|new
name|OptiqSchema
argument_list|(
name|this
argument_list|,
name|schema
argument_list|)
decl_stmt|;
name|subSchemaMap
operator|.
name|put
argument_list|(
name|schema
operator|.
name|getName
argument_list|()
argument_list|,
name|optiqSchema
argument_list|)
expr_stmt|;
return|return
name|optiqSchema
return|;
block|}
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|schema
operator|.
name|getName
argument_list|()
return|;
block|}
specifier|public
name|void
name|addTableFunction
parameter_list|(
name|TableFunctionEntry
name|tableFunctionEntry
parameter_list|)
block|{
throw|throw
name|Util
operator|.
name|needToImplement
argument_list|(
name|tableFunctionEntry
argument_list|)
throw|;
block|}
specifier|public
name|SchemaPlus
name|plus
parameter_list|()
block|{
return|return
operator|new
name|SchemaPlusImpl
argument_list|()
return|;
block|}
specifier|public
specifier|static
name|OptiqSchema
name|from
parameter_list|(
name|SchemaPlus
name|plus
parameter_list|)
block|{
return|return
operator|(
operator|(
name|SchemaPlusImpl
operator|)
name|plus
operator|)
operator|.
name|optiqSchema
argument_list|()
return|;
block|}
specifier|public
specifier|abstract
specifier|static
class|class
name|Entry
block|{
specifier|public
specifier|final
name|OptiqSchema
name|schema
decl_stmt|;
specifier|public
specifier|final
name|String
name|name
decl_stmt|;
specifier|public
name|Entry
parameter_list|(
name|OptiqSchema
name|schema
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|Linq4j
operator|.
name|requireNonNull
argument_list|(
name|schema
argument_list|)
expr_stmt|;
name|Linq4j
operator|.
name|requireNonNull
argument_list|(
name|name
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
name|name
operator|=
name|name
expr_stmt|;
block|}
comment|/** Returns this object's path. For example ["hr", "emps"]. */
specifier|public
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|path
parameter_list|()
block|{
return|return
name|Schemas
operator|.
name|path
argument_list|(
name|schema
operator|.
name|schema
argument_list|,
name|name
argument_list|)
return|;
block|}
block|}
specifier|public
specifier|abstract
specifier|static
class|class
name|TableEntry
extends|extends
name|Entry
block|{
specifier|public
name|TableEntry
parameter_list|(
name|OptiqSchema
name|schema
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|schema
argument_list|,
name|name
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|abstract
name|Table
name|getTable
parameter_list|()
function_decl|;
block|}
specifier|public
specifier|abstract
specifier|static
class|class
name|TableFunctionEntry
extends|extends
name|Entry
block|{
specifier|public
name|TableFunctionEntry
parameter_list|(
name|OptiqSchema
name|schema
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|schema
argument_list|,
name|name
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|abstract
name|TableFunction
name|getTableFunction
parameter_list|()
function_decl|;
comment|/** Whether this represents a materialized view. (At a given point in time,      * it may or may not be materialized as a table.) */
specifier|public
specifier|abstract
name|boolean
name|isMaterialization
parameter_list|()
function_decl|;
block|}
specifier|private
class|class
name|SchemaPlusImpl
implements|implements
name|SchemaPlus
block|{
specifier|public
name|OptiqSchema
name|optiqSchema
parameter_list|()
block|{
return|return
name|OptiqSchema
operator|.
name|this
return|;
block|}
specifier|public
name|SchemaPlus
name|getParentSchema
parameter_list|()
block|{
return|return
name|parent
operator|==
literal|null
condition|?
literal|null
else|:
name|parent
operator|.
name|plus
argument_list|()
return|;
block|}
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|OptiqSchema
operator|.
name|this
operator|.
name|getName
argument_list|()
return|;
block|}
specifier|public
name|boolean
name|isMutable
parameter_list|()
block|{
return|return
name|schema
operator|.
name|isMutable
argument_list|()
return|;
block|}
specifier|public
name|Expression
name|getExpression
parameter_list|()
block|{
return|return
name|schema
operator|.
name|getExpression
argument_list|()
return|;
block|}
specifier|public
name|Table
name|getTable
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|compositeTableMap
operator|.
name|get
argument_list|(
name|name
argument_list|)
return|;
block|}
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getTableNames
parameter_list|()
block|{
return|return
name|compositeTableMap
operator|.
name|keySet
argument_list|()
return|;
block|}
specifier|public
name|Collection
argument_list|<
name|TableFunction
argument_list|>
name|getTableFunctions
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|compositeTableFunctionMap
operator|.
name|get
argument_list|(
name|name
argument_list|)
return|;
block|}
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getTableFunctionNames
parameter_list|()
block|{
return|return
name|compositeTableFunctionMap
operator|.
name|keySet
argument_list|()
return|;
block|}
specifier|public
name|SchemaPlus
name|getSubSchema
parameter_list|(
name|String
name|name
parameter_list|)
block|{
specifier|final
name|OptiqSchema
name|subSchema
init|=
name|OptiqSchema
operator|.
name|this
operator|.
name|getSubSchema
argument_list|(
name|name
argument_list|)
decl_stmt|;
return|return
name|subSchema
operator|==
literal|null
condition|?
literal|null
else|:
name|subSchema
operator|.
name|plus
argument_list|()
return|;
block|}
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getSubSchemaNames
parameter_list|()
block|{
return|return
name|subSchemaMap
operator|.
name|keySet
argument_list|()
return|;
block|}
specifier|public
name|SchemaPlus
name|add
parameter_list|(
name|Schema
name|schema
parameter_list|)
block|{
specifier|final
name|OptiqSchema
name|optiqSchema
init|=
name|OptiqSchema
operator|.
name|this
operator|.
name|addSchema
argument_list|(
name|schema
argument_list|)
decl_stmt|;
return|return
name|optiqSchema
operator|.
name|plus
argument_list|()
return|;
block|}
specifier|public
name|SchemaPlus
name|addRecursive
parameter_list|(
name|Schema
name|schema
parameter_list|)
block|{
return|return
name|schema
operator|.
name|getParentSchema
argument_list|()
operator|.
name|add
argument_list|(
name|schema
argument_list|)
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
if|if
condition|(
name|clazz
operator|.
name|isInstance
argument_list|(
name|this
argument_list|)
condition|)
block|{
return|return
name|clazz
operator|.
name|cast
argument_list|(
name|this
argument_list|)
return|;
block|}
if|if
condition|(
name|clazz
operator|.
name|isInstance
argument_list|(
name|OptiqSchema
operator|.
name|this
argument_list|)
condition|)
block|{
return|return
name|clazz
operator|.
name|cast
argument_list|(
name|OptiqSchema
operator|.
name|this
argument_list|)
return|;
block|}
if|if
condition|(
name|clazz
operator|.
name|isInstance
argument_list|(
name|OptiqSchema
operator|.
name|this
operator|.
name|schema
argument_list|)
condition|)
block|{
return|return
name|clazz
operator|.
name|cast
argument_list|(
name|OptiqSchema
operator|.
name|this
operator|.
name|schema
argument_list|)
return|;
block|}
throw|throw
operator|new
name|ClassCastException
argument_list|(
literal|"not a "
operator|+
name|clazz
argument_list|)
throw|;
block|}
specifier|public
name|void
name|add
parameter_list|(
name|String
name|name
parameter_list|,
name|Table
name|table
parameter_list|)
block|{
name|OptiqSchema
operator|.
name|this
operator|.
name|add
argument_list|(
name|name
argument_list|,
name|table
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|add
parameter_list|(
name|String
name|name
parameter_list|,
name|TableFunction
name|tableFunction
parameter_list|)
block|{
name|OptiqSchema
operator|.
name|this
operator|.
name|add
argument_list|(
name|name
argument_list|,
name|tableFunction
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Implementation of {@link net.hydromatic.optiq.jdbc.OptiqSchema.TableEntry} where all properties are    * held in fields.    */
specifier|public
specifier|static
class|class
name|TableEntryImpl
extends|extends
name|TableEntry
block|{
specifier|private
specifier|final
name|Table
name|table
decl_stmt|;
comment|/** Creates a TableEntryImpl. */
specifier|public
name|TableEntryImpl
parameter_list|(
name|OptiqSchema
name|schema
parameter_list|,
name|String
name|name
parameter_list|,
name|Table
name|table
parameter_list|)
block|{
name|super
argument_list|(
name|schema
argument_list|,
name|name
argument_list|)
expr_stmt|;
assert|assert
name|table
operator|!=
literal|null
assert|;
name|this
operator|.
name|table
operator|=
name|table
expr_stmt|;
block|}
specifier|public
name|Table
name|getTable
parameter_list|()
block|{
return|return
name|table
return|;
block|}
block|}
comment|/**    * Implementation of {@link net.hydromatic.optiq.jdbc.OptiqSchema.TableFunctionEntry}    * where all properties are held in fields.    */
specifier|public
specifier|static
class|class
name|TableFunctionEntryImpl
extends|extends
name|TableFunctionEntry
block|{
specifier|private
specifier|final
name|TableFunction
name|tableFunction
decl_stmt|;
comment|/** Creates a TableFunctionEntryImpl. */
specifier|public
name|TableFunctionEntryImpl
parameter_list|(
name|OptiqSchema
name|schema
parameter_list|,
name|String
name|name
parameter_list|,
name|TableFunction
name|tableFunction
parameter_list|)
block|{
name|super
argument_list|(
name|schema
argument_list|,
name|name
argument_list|)
expr_stmt|;
name|this
operator|.
name|tableFunction
operator|=
name|tableFunction
expr_stmt|;
block|}
specifier|public
name|TableFunction
name|getTableFunction
parameter_list|()
block|{
return|return
name|tableFunction
return|;
block|}
specifier|public
name|boolean
name|isMaterialization
parameter_list|()
block|{
return|return
name|tableFunction
operator|instanceof
name|MaterializedViewTable
operator|.
name|MaterializedViewTableFunction
return|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End OptiqSchema.java
end_comment

end_unit

