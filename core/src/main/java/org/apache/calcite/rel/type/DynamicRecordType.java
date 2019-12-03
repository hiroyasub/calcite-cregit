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
name|rel
operator|.
name|type
package|;
end_package

begin_comment
comment|/**  * Specific type of RelRecordType that corresponds to a dynamic table,  * where columns are created as they are requested.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|DynamicRecordType
extends|extends
name|RelDataTypeImpl
block|{
comment|// The prefix string for dynamic star column name
specifier|public
specifier|static
specifier|final
name|String
name|DYNAMIC_STAR_PREFIX
init|=
literal|"**"
decl_stmt|;
specifier|public
name|boolean
name|isDynamicStruct
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
comment|/**    * Returns true if the column name starts with DYNAMIC_STAR_PREFIX.    */
specifier|public
specifier|static
name|boolean
name|isDynamicStarColName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|name
operator|.
name|startsWith
argument_list|(
name|DYNAMIC_STAR_PREFIX
argument_list|)
return|;
block|}
block|}
end_class

end_unit

