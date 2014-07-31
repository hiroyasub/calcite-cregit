begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to the Apache Software Foundation (ASF) under one or more // contributor license agreements.  See the NOTICE file distributed with // this work for additional information regarding copyright ownership. // The ASF licenses this file to you under the Apache License, Version 2.0 // (the "License"); you may not use this file except in compliance with // the License.  You may obtain a copy of the License at // // http://www.apache.org/licenses/LICENSE-2.0 // // Unless required by applicable law or agreed to in writing, software // distributed under the License is distributed on an "AS IS" BASIS, // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. // See the License for the specific language governing permissions and // limitations under the License. */
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
comment|/**  * Abstract implementation of {@link Schema}.  *  *<p>Behavior is as follows:</p>  *<ul>  *<li>The schema has no tables unless you override  *       {@link #getTableMap()}.</li>  *<li>The schema has no functions unless you override  *       {@link #getFunctionMultimap()}.</li>  *<li>The schema has no sub-schemas unless you override  *       {@link #getSubSchemaMap()}.</li>  *<li>The schema is mutable unless you override  *       {@link #isMutable()}.</li>  *<li>The name and parent schema are as specified in the constructor  *       arguments.</li>  *</ul>  *  *<p>For constructing custom maps and multi-maps, we recommend  * {@link com.google.common.base.Suppliers} and  * {@link com.google.common.collect.Maps}.</p>  */
end_comment

begin_class
specifier|public
class|class
name|AbstractSchema
implements|implements
name|Schema
block|{
specifier|public
name|AbstractSchema
parameter_list|()
block|{
block|}
specifier|public
name|boolean
name|isMutable
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
specifier|public
name|boolean
name|contentsHaveChangedSince
parameter_list|(
name|long
name|lastCheck
parameter_list|,
name|long
name|now
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
specifier|public
name|Expression
name|getExpression
parameter_list|(
name|SchemaPlus
name|parentSchema
parameter_list|,
name|String
name|name
parameter_list|)
block|{
return|return
name|Schemas
operator|.
name|subSchemaExpression
argument_list|(
name|parentSchema
argument_list|,
name|name
argument_list|,
name|getClass
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Returns a map of tables in this schema by name.    *    *<p>The implementations of {@link #getTableNames()}    * and {@link #getTable(String)} depend on this map.    * The default implementation of this method returns the empty map.    * Override this method to change their behavior.</p>    */
specifier|protected
name|Map
argument_list|<
name|String
argument_list|,
name|Table
argument_list|>
name|getTableMap
parameter_list|()
block|{
return|return
name|ImmutableMap
operator|.
name|of
argument_list|()
return|;
block|}
specifier|public
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|getTableNames
parameter_list|()
block|{
return|return
name|getTableMap
argument_list|()
operator|.
name|keySet
argument_list|()
return|;
block|}
specifier|public
specifier|final
name|Table
name|getTable
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|getTableMap
argument_list|()
operator|.
name|get
argument_list|(
name|name
argument_list|)
return|;
block|}
comment|/**    * Returns a multi-map of functions in this schema by name.    * It is a multi-map because functions are overloaded; there may be more than    * one function in a schema with a given name (as long as they have different    * parameter lists).    *    *<p>The implementations of {@link #getFunctionNames()}    * and {@link Schema#getFunctions(String)} depend on this map.    * The default implementation of this method returns the empty multi-map.    * Override this method to change their behavior.</p>    */
specifier|protected
name|Multimap
argument_list|<
name|String
argument_list|,
name|Function
argument_list|>
name|getFunctionMultimap
parameter_list|()
block|{
return|return
name|ImmutableMultimap
operator|.
name|of
argument_list|()
return|;
block|}
specifier|public
specifier|final
name|Collection
argument_list|<
name|Function
argument_list|>
name|getFunctions
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|getFunctionMultimap
argument_list|()
operator|.
name|get
argument_list|(
name|name
argument_list|)
return|;
comment|// never null
block|}
specifier|public
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|getFunctionNames
parameter_list|()
block|{
return|return
name|getFunctionMultimap
argument_list|()
operator|.
name|keySet
argument_list|()
return|;
block|}
comment|/**    * Returns a map of tables in this schema by name.    *    *<p>The implementations of {@link #getTableNames()}    * and {@link #getTable(String)} depend on this map.    * The default implementation of this method returns the empty map.    * Override this method to change their behavior.</p>    */
specifier|protected
name|Map
argument_list|<
name|String
argument_list|,
name|Schema
argument_list|>
name|getSubSchemaMap
parameter_list|()
block|{
return|return
name|ImmutableMap
operator|.
name|of
argument_list|()
return|;
block|}
specifier|public
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|getSubSchemaNames
parameter_list|()
block|{
return|return
name|getSubSchemaMap
argument_list|()
operator|.
name|keySet
argument_list|()
return|;
block|}
specifier|public
specifier|final
name|Schema
name|getSubSchema
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|getSubSchemaMap
argument_list|()
operator|.
name|get
argument_list|(
name|name
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|// End AbstractSchema.java
end_comment

end_unit

