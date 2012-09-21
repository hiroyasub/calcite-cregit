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
name|org
operator|.
name|eigenbase
operator|.
name|reltype
operator|.
name|RelDataTypeField
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
name|RelRecordType
import|;
end_import

begin_comment
comment|/** * Record type based on a Java class. The fields of the type are the fields  * of the class.  *  *<p><strong>NOTE: This class is experimental and subject to  * change/removal without notice</strong>.</p> */
end_comment

begin_class
specifier|public
class|class
name|JavaRecordType
extends|extends
name|RelRecordType
block|{
specifier|final
name|Class
name|clazz
decl_stmt|;
specifier|public
name|JavaRecordType
parameter_list|(
name|RelDataTypeField
index|[]
name|fields
parameter_list|,
name|Class
name|clazz
parameter_list|)
block|{
name|super
argument_list|(
name|fields
argument_list|)
expr_stmt|;
name|this
operator|.
name|clazz
operator|=
name|clazz
expr_stmt|;
assert|assert
name|clazz
operator|!=
literal|null
assert|;
block|}
block|}
end_class

begin_comment
comment|// End JavaRecordType.java
end_comment

end_unit

