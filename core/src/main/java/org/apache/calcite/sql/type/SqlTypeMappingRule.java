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
name|sql
operator|.
name|type
package|;
end_package

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
name|ImmutableSet
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
comment|/**  * Interface that defines rules within type mappings.  *  *<p>Each instance should define a type mapping matrix which actually defines  * the rules that indicate whether one type can apply the rule to another.  *  *<p>Typically, the "rule" can be type assignment or type coercion.  */
end_comment

begin_interface
specifier|public
interface|interface
name|SqlTypeMappingRule
block|{
comment|/** Returns the type mappings of this rule instance.    *    *<p>The mappings should be immutable.    */
name|Map
argument_list|<
name|SqlTypeName
argument_list|,
name|ImmutableSet
argument_list|<
name|SqlTypeName
argument_list|>
argument_list|>
name|getTypeMapping
parameter_list|()
function_decl|;
comment|/** Returns whether it is valid to apply the defined rules from type {@code from} to    * type {@code to}. */
specifier|default
name|boolean
name|canApplyFrom
parameter_list|(
name|SqlTypeName
name|to
parameter_list|,
name|SqlTypeName
name|from
parameter_list|)
block|{
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|to
argument_list|)
expr_stmt|;
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|from
argument_list|)
expr_stmt|;
if|if
condition|(
name|to
operator|==
name|SqlTypeName
operator|.
name|NULL
condition|)
block|{
return|return
literal|false
return|;
block|}
if|else if
condition|(
name|from
operator|==
name|SqlTypeName
operator|.
name|NULL
condition|)
block|{
return|return
literal|true
return|;
block|}
specifier|final
name|Set
argument_list|<
name|SqlTypeName
argument_list|>
name|rule
init|=
name|getTypeMapping
argument_list|()
operator|.
name|get
argument_list|(
name|to
argument_list|)
decl_stmt|;
if|if
condition|(
name|rule
operator|==
literal|null
condition|)
block|{
comment|// If you hit this assert, see the constructor of this class on how
comment|// to add new rule
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"No assign rules for "
operator|+
name|to
operator|+
literal|" defined"
argument_list|)
throw|;
block|}
return|return
name|rule
operator|.
name|contains
argument_list|(
name|from
argument_list|)
return|;
block|}
block|}
end_interface

end_unit

