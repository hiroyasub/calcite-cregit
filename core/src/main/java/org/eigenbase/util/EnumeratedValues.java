begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to Julian Hyde under one or more contributor license // agreements. See the NOTICE file distributed with this work for // additional information regarding copyright ownership. // // Julian Hyde licenses this file to you under the Apache License, // Version 2.0 (the "License"); you may not use this file except in // compliance with the License. You may obtain a copy of the License at: // // http://www.apache.org/licenses/LICENSE-2.0 // // Unless required by applicable law or agreed to in writing, software // distributed under the License is distributed on an "AS IS" BASIS, // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. // See the License for the specific language governing permissions and // limitations under the License. */
end_comment

begin_package
package|package
name|org
operator|.
name|eigenbase
operator|.
name|util
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
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

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|util14
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  *<code>EnumeratedValues</code> is a helper class for declaring a set of  * symbolic constants which have names, ordinals, and possibly descriptions. The  * ordinals do not have to be contiguous.  *  *<p>Typically, for a particular set of constants, you derive a class from this  * interface, and declare the constants as<code>public static final</code>  * members. Give it a private constructor, and a<code>public static final<i>  * ClassName</i> instance</code> member to hold the singleton instance.</p>  */
end_comment

begin_class
specifier|public
class|class
name|EnumeratedValues
extends|extends
name|Enum14
block|{
comment|//~ Constructors -----------------------------------------------------------
comment|/**      * Creates a new empty, mutable enumeration.      */
specifier|public
name|EnumeratedValues
parameter_list|()
block|{
block|}
comment|/**      * Creates an enumeration, with an array of values, and freezes it.      */
specifier|public
name|EnumeratedValues
parameter_list|(
name|Value
index|[]
name|values
parameter_list|)
block|{
name|super
argument_list|(
name|values
argument_list|)
expr_stmt|;
block|}
comment|/**      * Creates an enumeration, initialize it with an array of strings, and      * freezes it.      */
specifier|public
name|EnumeratedValues
parameter_list|(
name|String
index|[]
name|names
parameter_list|)
block|{
name|super
argument_list|(
name|names
argument_list|)
expr_stmt|;
block|}
comment|/**      * Create an enumeration, initializes it with arrays of code/name pairs, and      * freezes it.      */
specifier|public
name|EnumeratedValues
parameter_list|(
name|String
index|[]
name|names
parameter_list|,
name|int
index|[]
name|codes
parameter_list|)
block|{
name|super
argument_list|(
name|names
argument_list|,
name|codes
argument_list|)
expr_stmt|;
block|}
comment|/**      * Create an enumeration, initializes it with arrays of code/name pairs, and      * freezes it.      */
specifier|public
name|EnumeratedValues
parameter_list|(
name|String
index|[]
name|names
parameter_list|,
name|int
index|[]
name|codes
parameter_list|,
name|String
index|[]
name|descriptions
parameter_list|)
block|{
name|super
argument_list|(
name|names
argument_list|,
name|codes
argument_list|,
name|descriptions
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
comment|/**      * Creates a mutable enumeration from an existing enumeration, which may      * already be immutable.      */
specifier|public
name|EnumeratedValues
name|getMutableClone
parameter_list|()
block|{
return|return
operator|(
name|EnumeratedValues
operator|)
name|clone
argument_list|()
return|;
block|}
block|}
end_class

begin_comment
comment|// End EnumeratedValues.java
end_comment

end_unit

