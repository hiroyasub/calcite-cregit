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
name|adapter
operator|.
name|pig
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|sql
operator|.
name|SqlKind
import|;
end_import

begin_comment
comment|/**  * Supported Pig aggregate functions and their Calcite counterparts. The enum's  * name() is the same as the function's name in Pig Latin.  */
end_comment

begin_enum
specifier|public
enum|enum
name|PigAggFunction
block|{
name|COUNT
argument_list|(
name|SqlKind
operator|.
name|COUNT
argument_list|,
literal|false
argument_list|)
block|,
name|COUNT_STAR
argument_list|(
name|SqlKind
operator|.
name|COUNT
argument_list|,
literal|true
argument_list|)
block|;
specifier|private
specifier|final
name|SqlKind
name|calciteFunc
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|star
decl_stmt|;
comment|// as in COUNT(*)
name|PigAggFunction
parameter_list|(
name|SqlKind
name|calciteFunc
parameter_list|)
block|{
name|this
argument_list|(
name|calciteFunc
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
name|PigAggFunction
parameter_list|(
name|SqlKind
name|calciteFunc
parameter_list|,
name|boolean
name|star
parameter_list|)
block|{
name|this
operator|.
name|calciteFunc
operator|=
name|calciteFunc
expr_stmt|;
name|this
operator|.
name|star
operator|=
name|star
expr_stmt|;
block|}
specifier|public
specifier|static
name|PigAggFunction
name|valueOf
parameter_list|(
name|SqlKind
name|calciteFunc
parameter_list|,
name|boolean
name|star
parameter_list|)
block|{
for|for
control|(
name|PigAggFunction
name|pigAggFunction
range|:
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|pigAggFunction
operator|.
name|calciteFunc
operator|==
name|calciteFunc
operator|&&
name|pigAggFunction
operator|.
name|star
operator|==
name|star
condition|)
block|{
return|return
name|pigAggFunction
return|;
block|}
block|}
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Pig agg func for "
operator|+
name|calciteFunc
operator|+
literal|" is not supported"
argument_list|)
throw|;
block|}
block|}
end_enum

end_unit

