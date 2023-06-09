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
name|linq4j
package|;
end_package

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
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Objects
import|;
end_import

begin_comment
comment|/**  * Implementation of {@link Grouping}.  *  * @param<K> Key type  * @param<V> Value type  */
end_comment

begin_class
annotation|@
name|SuppressWarnings
argument_list|(
literal|"type.argument.type.incompatible"
argument_list|)
class|class
name|GroupingImpl
parameter_list|<
name|K
extends|extends
name|Object
parameter_list|,
name|V
parameter_list|>
extends|extends
name|AbstractEnumerable
argument_list|<
name|V
argument_list|>
implements|implements
name|Grouping
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
implements|,
name|Map
operator|.
name|Entry
argument_list|<
name|K
argument_list|,
name|Enumerable
argument_list|<
name|V
argument_list|>
argument_list|>
block|{
specifier|private
specifier|final
name|K
name|key
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|V
argument_list|>
name|values
decl_stmt|;
name|GroupingImpl
parameter_list|(
name|K
name|key
parameter_list|,
name|List
argument_list|<
name|V
argument_list|>
name|values
parameter_list|)
block|{
name|this
operator|.
name|key
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|key
argument_list|,
literal|"key"
argument_list|)
expr_stmt|;
name|this
operator|.
name|values
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|values
argument_list|,
literal|"values"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|key
operator|+
literal|": "
operator|+
name|values
return|;
block|}
comment|/** {@inheritDoc}    *    *<p>Computes hash code consistent with    * {@link java.util.Map.Entry#hashCode()}. */
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|key
operator|.
name|hashCode
argument_list|()
operator|^
name|values
operator|.
name|hashCode
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
annotation|@
name|Nullable
name|Object
name|obj
parameter_list|)
block|{
return|return
name|obj
operator|instanceof
name|GroupingImpl
operator|&&
name|key
operator|.
name|equals
argument_list|(
operator|(
operator|(
name|GroupingImpl
operator|)
name|obj
operator|)
operator|.
name|key
argument_list|)
operator|&&
name|values
operator|.
name|equals
argument_list|(
operator|(
operator|(
name|GroupingImpl
operator|)
name|obj
operator|)
operator|.
name|values
argument_list|)
return|;
block|}
comment|// implement Map.Entry
annotation|@
name|Override
specifier|public
name|Enumerable
argument_list|<
name|V
argument_list|>
name|getValue
parameter_list|()
block|{
return|return
name|Linq4j
operator|.
name|asEnumerable
argument_list|(
name|values
argument_list|)
return|;
block|}
comment|// implement Map.Entry
annotation|@
name|Override
specifier|public
name|Enumerable
argument_list|<
name|V
argument_list|>
name|setValue
parameter_list|(
name|Enumerable
argument_list|<
name|V
argument_list|>
name|value
parameter_list|)
block|{
comment|// immutable
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
comment|// implement Map.Entry
comment|// implement Grouping
annotation|@
name|Override
specifier|public
name|K
name|getKey
parameter_list|()
block|{
return|return
name|key
return|;
block|}
annotation|@
name|Override
specifier|public
name|Enumerator
argument_list|<
name|V
argument_list|>
name|enumerator
parameter_list|()
block|{
return|return
name|Linq4j
operator|.
name|enumerator
argument_list|(
name|values
argument_list|)
return|;
block|}
block|}
end_class

end_unit

