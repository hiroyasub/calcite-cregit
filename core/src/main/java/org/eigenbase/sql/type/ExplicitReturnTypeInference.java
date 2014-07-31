begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to the Apache Software Foundation (ASF) under one or more // contributor license agreements.  See the NOTICE file distributed with // this work for additional information regarding copyright ownership. // The ASF licenses this file to you under the Apache License, Version 2.0 // (the "License"); you may not use this file except in compliance with // the License.  You may obtain a copy of the License at // // http://www.apache.org/licenses/LICENSE-2.0 // // Unless required by applicable law or agreed to in writing, software // distributed under the License is distributed on an "AS IS" BASIS, // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. // See the License for the specific language governing permissions and // limitations under the License. */
end_comment

begin_package
package|package
name|org
operator|.
name|eigenbase
operator|.
name|sql
operator|.
name|type
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
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|sql
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * A {@link SqlReturnTypeInference} which always returns the same SQL type.  */
end_comment

begin_class
specifier|public
class|class
name|ExplicitReturnTypeInference
implements|implements
name|SqlReturnTypeInference
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|protected
specifier|final
name|RelProtoDataType
name|protoType
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Creates an inference rule which always returns the same type object.    *    *<p>If the requesting type factory is different, returns a copy of the    * type object made using {@link RelDataTypeFactory#copyType(RelDataType)}    * within the requesting type factory.    *    *<p>A copy of the type is required because each statement is prepared using    * a different type factory; each type factory maintains its own cache of    * canonical instances of each type.    *    * @param protoType Type object    */
specifier|protected
name|ExplicitReturnTypeInference
parameter_list|(
name|RelProtoDataType
name|protoType
parameter_list|)
block|{
assert|assert
name|protoType
operator|!=
literal|null
assert|;
name|this
operator|.
name|protoType
operator|=
name|protoType
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
specifier|public
name|RelDataType
name|inferReturnType
parameter_list|(
name|SqlOperatorBinding
name|opBinding
parameter_list|)
block|{
return|return
name|protoType
operator|.
name|apply
argument_list|(
name|opBinding
operator|.
name|getTypeFactory
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|// End ExplicitReturnTypeInference.java
end_comment

end_unit

