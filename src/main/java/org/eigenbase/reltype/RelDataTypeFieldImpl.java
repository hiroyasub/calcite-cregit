begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to DynamoBI Corporation (DynamoBI) under one // or more contributor license agreements.  See the NOTICE file // distributed with this work for additional information // regarding copyright ownership.  DynamoBI licenses this file // to you under the Apache License, Version 2.0 (the // "License"); you may not use this file except in compliance // with the License.  You may obtain a copy of the License at  //   http://www.apache.org/licenses/LICENSE-2.0  // Unless required by applicable law or agreed to in writing, // software distributed under the License is distributed on an // "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY // KIND, either express or implied.  See the License for the // specific language governing permissions and limitations // under the License. */
end_comment

begin_package
package|package
name|org
operator|.
name|eigenbase
operator|.
name|reltype
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

begin_comment
comment|/**  * Default implementation of {@link RelDataTypeField}.  *  * @author jhyde  * @version $Id$  */
end_comment

begin_class
specifier|public
class|class
name|RelDataTypeFieldImpl
implements|implements
name|RelDataTypeField
implements|,
name|Serializable
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|private
specifier|final
name|RelDataType
name|type
decl_stmt|;
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
specifier|private
specifier|final
name|int
name|index
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**      * @pre name != null      * @pre type != null      */
specifier|public
name|RelDataTypeFieldImpl
parameter_list|(
name|String
name|name
parameter_list|,
name|int
name|index
parameter_list|,
name|RelDataType
name|type
parameter_list|)
block|{
assert|assert
operator|(
name|name
operator|!=
literal|null
operator|)
assert|;
assert|assert
operator|(
name|type
operator|!=
literal|null
operator|)
assert|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|index
operator|=
name|index
expr_stmt|;
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
comment|// implement RelDataTypeField
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
comment|// implement RelDataTypeField
specifier|public
name|int
name|getIndex
parameter_list|()
block|{
return|return
name|index
return|;
block|}
comment|// implement RelDataTypeField
specifier|public
name|RelDataType
name|getType
parameter_list|()
block|{
return|return
name|type
return|;
block|}
comment|// for debugging
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"#"
operator|+
name|index
operator|+
literal|": "
operator|+
name|name
operator|+
literal|" "
operator|+
name|type
return|;
block|}
block|}
end_class

begin_comment
comment|// End RelDataTypeFieldImpl.java
end_comment

end_unit

