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
operator|.
name|property
package|;
end_package

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
comment|/**  * Definition and accessor for a string property.  *  * @author jhyde  * @version $Id$  * @since May 4, 2004  */
end_comment

begin_class
specifier|public
class|class
name|StringProperty
extends|extends
name|Property
block|{
comment|//~ Constructors -----------------------------------------------------------
comment|/**      * Creates a string property.      *      * @param properties Properties object which holds values for this property.      * @param path Name by which this property is serialized to a properties      * file, for example "com.acme.trace.Verbosity".      * @param defaultValue Default value, null if there is no default.      */
specifier|public
name|StringProperty
parameter_list|(
name|Properties
name|properties
parameter_list|,
name|String
name|path
parameter_list|,
name|String
name|defaultValue
parameter_list|)
block|{
name|super
argument_list|(
name|properties
argument_list|,
name|path
argument_list|,
name|defaultValue
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
comment|/**      * Retrieves the value of this property. Returns the property's default      * value if the property set has no value for this property.      */
specifier|public
name|String
name|get
parameter_list|()
block|{
return|return
name|stringValue
argument_list|()
return|;
block|}
comment|/**      * Retrieves the value of this property, optionally failing if there is no      * value. Returns the property's default value if the property set has no      * value for this property.      */
specifier|public
name|String
name|get
parameter_list|(
name|boolean
name|required
parameter_list|)
block|{
return|return
name|getInternal
argument_list|(
literal|null
argument_list|,
name|required
argument_list|)
return|;
block|}
comment|/**      * Retrieves the value of this property, or the default value if none is      * found.      */
specifier|public
name|String
name|get
parameter_list|(
name|String
name|defaultValue
parameter_list|)
block|{
return|return
name|getInternal
argument_list|(
name|defaultValue
argument_list|,
literal|false
argument_list|)
return|;
block|}
comment|/**      * Sets the value of this property.      *      * @return The previous value, or the default value if not set.      */
specifier|public
name|String
name|set
parameter_list|(
name|String
name|value
parameter_list|)
block|{
name|String
name|prevValue
init|=
name|setString
argument_list|(
name|value
argument_list|)
decl_stmt|;
if|if
condition|(
name|prevValue
operator|==
literal|null
condition|)
block|{
name|prevValue
operator|=
name|getDefaultValue
argument_list|()
expr_stmt|;
block|}
return|return
name|prevValue
return|;
block|}
block|}
end_class

begin_comment
comment|// End StringProperty.java
end_comment

end_unit

