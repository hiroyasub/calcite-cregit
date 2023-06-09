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
name|type
operator|.
name|SqlTypeName
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|pig
operator|.
name|data
operator|.
name|DataType
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|sql
operator|.
name|type
operator|.
name|SqlTypeName
operator|.
name|VARCHAR
import|;
end_import

begin_comment
comment|/**  * Supported Pig data types and their Calcite counterparts.  */
end_comment

begin_enum
specifier|public
enum|enum
name|PigDataType
block|{
name|CHARARRAY
argument_list|(
name|DataType
operator|.
name|CHARARRAY
argument_list|,
name|VARCHAR
argument_list|)
block|;
specifier|private
specifier|final
name|byte
name|pigType
decl_stmt|;
comment|// Pig defines types using bytes
specifier|private
specifier|final
name|SqlTypeName
name|sqlType
decl_stmt|;
name|PigDataType
parameter_list|(
name|byte
name|pigType
parameter_list|,
name|SqlTypeName
name|sqlType
parameter_list|)
block|{
name|this
operator|.
name|pigType
operator|=
name|pigType
expr_stmt|;
name|this
operator|.
name|sqlType
operator|=
name|sqlType
expr_stmt|;
block|}
specifier|public
name|byte
name|getPigType
parameter_list|()
block|{
return|return
name|pigType
return|;
block|}
specifier|public
name|SqlTypeName
name|getSqlType
parameter_list|()
block|{
return|return
name|sqlType
return|;
block|}
specifier|public
specifier|static
name|PigDataType
name|valueOf
parameter_list|(
name|byte
name|pigType
parameter_list|)
block|{
for|for
control|(
name|PigDataType
name|pigDataType
range|:
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|pigDataType
operator|.
name|pigType
operator|==
name|pigType
condition|)
block|{
return|return
name|pigDataType
return|;
block|}
block|}
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Pig data type "
operator|+
name|DataType
operator|.
name|findTypeName
argument_list|(
name|pigType
argument_list|)
operator|+
literal|" is not supported"
argument_list|)
throw|;
block|}
specifier|public
specifier|static
name|PigDataType
name|valueOf
parameter_list|(
name|SqlTypeName
name|sqlType
parameter_list|)
block|{
for|for
control|(
name|PigDataType
name|pigDataType
range|:
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|pigDataType
operator|.
name|sqlType
operator|==
name|sqlType
condition|)
block|{
return|return
name|pigDataType
return|;
block|}
block|}
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"SQL data type "
operator|+
name|sqlType
operator|+
literal|" is not supported"
argument_list|)
throw|;
block|}
block|}
end_enum

end_unit

