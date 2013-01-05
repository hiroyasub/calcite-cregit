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
name|rules
operator|.
name|java
package|;
end_package

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

begin_comment
comment|/**  * How a row is represented as a Java value.  */
end_comment

begin_enum
specifier|public
enum|enum
name|JavaRowFormat
block|{
name|CUSTOM
block|,
name|SCALAR
block|,
name|EMPTY_LIST
block|,
name|ARRAY
block|;
specifier|public
name|JavaRowFormat
name|optimize
parameter_list|(
name|RelDataType
name|rowType
parameter_list|)
block|{
switch|switch
condition|(
name|rowType
operator|.
name|getFieldCount
argument_list|()
condition|)
block|{
case|case
literal|0
case|:
return|return
name|EMPTY_LIST
return|;
case|case
literal|1
case|:
return|return
name|SCALAR
return|;
default|default:
return|return
name|this
return|;
block|}
block|}
block|}
end_enum

begin_comment
comment|// End JavaRowFormat.java
end_comment

end_unit

