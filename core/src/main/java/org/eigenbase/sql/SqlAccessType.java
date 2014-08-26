begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to you under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|eigenbase
operator|.
name|sql
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
comment|/**  * SqlAccessType is represented by a set of allowed access types  */
end_comment

begin_class
specifier|public
class|class
name|SqlAccessType
block|{
comment|//~ Static fields/initializers ---------------------------------------------
specifier|public
specifier|static
specifier|final
name|SqlAccessType
name|ALL
init|=
operator|new
name|SqlAccessType
argument_list|(
name|EnumSet
operator|.
name|allOf
argument_list|(
name|SqlAccessEnum
operator|.
name|class
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|SqlAccessType
name|READ_ONLY
init|=
operator|new
name|SqlAccessType
argument_list|(
name|EnumSet
operator|.
name|of
argument_list|(
name|SqlAccessEnum
operator|.
name|SELECT
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|SqlAccessType
name|WRITE_ONLY
init|=
operator|new
name|SqlAccessType
argument_list|(
name|EnumSet
operator|.
name|of
argument_list|(
name|SqlAccessEnum
operator|.
name|INSERT
argument_list|)
argument_list|)
decl_stmt|;
comment|//~ Instance fields --------------------------------------------------------
specifier|private
specifier|final
name|EnumSet
argument_list|<
name|SqlAccessEnum
argument_list|>
name|accessEnums
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
specifier|public
name|SqlAccessType
parameter_list|(
name|EnumSet
argument_list|<
name|SqlAccessEnum
argument_list|>
name|accessEnums
parameter_list|)
block|{
name|this
operator|.
name|accessEnums
operator|=
name|accessEnums
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
specifier|public
name|boolean
name|allowsAccess
parameter_list|(
name|SqlAccessEnum
name|access
parameter_list|)
block|{
return|return
name|accessEnums
operator|.
name|contains
argument_list|(
name|access
argument_list|)
return|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|accessEnums
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|public
specifier|static
name|SqlAccessType
name|create
parameter_list|(
name|String
index|[]
name|accessNames
parameter_list|)
block|{
assert|assert
name|accessNames
operator|!=
literal|null
assert|;
name|EnumSet
argument_list|<
name|SqlAccessEnum
argument_list|>
name|enumSet
init|=
name|EnumSet
operator|.
name|noneOf
argument_list|(
name|SqlAccessEnum
operator|.
name|class
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|accessNames
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|enumSet
operator|.
name|add
argument_list|(
name|SqlAccessEnum
operator|.
name|valueOf
argument_list|(
name|accessNames
index|[
name|i
index|]
operator|.
name|trim
argument_list|()
operator|.
name|toUpperCase
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|SqlAccessType
argument_list|(
name|enumSet
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|SqlAccessType
name|create
parameter_list|(
name|String
name|accessString
parameter_list|)
block|{
assert|assert
name|accessString
operator|!=
literal|null
assert|;
name|accessString
operator|=
name|accessString
operator|.
name|replace
argument_list|(
literal|'['
argument_list|,
literal|' '
argument_list|)
expr_stmt|;
name|accessString
operator|=
name|accessString
operator|.
name|replace
argument_list|(
literal|']'
argument_list|,
literal|' '
argument_list|)
expr_stmt|;
name|String
index|[]
name|accessNames
init|=
name|accessString
operator|.
name|split
argument_list|(
literal|","
argument_list|)
decl_stmt|;
return|return
name|create
argument_list|(
name|accessNames
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|// End SqlAccessType.java
end_comment

end_unit

