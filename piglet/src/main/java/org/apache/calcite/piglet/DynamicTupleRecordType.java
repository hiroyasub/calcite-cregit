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
name|piglet
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
name|rel
operator|.
name|type
operator|.
name|DynamicRecordTypeImpl
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|rel
operator|.
name|type
operator|.
name|RelDataTypeFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|rel
operator|.
name|type
operator|.
name|RelDataTypeField
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Matcher
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
import|;
end_import

begin_comment
comment|/**  * Represents Pig Tuples with unknown fields. The tuple field  * can only be accessed via name '$index', like ('$0', '$1').  * The tuple is then resized to match the index.  */
end_comment

begin_class
specifier|public
class|class
name|DynamicTupleRecordType
extends|extends
name|DynamicRecordTypeImpl
block|{
specifier|private
specifier|static
specifier|final
name|Pattern
name|INDEX_PATTERN
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"^\\$(\\d+)$"
argument_list|)
decl_stmt|;
name|DynamicTupleRecordType
parameter_list|(
name|RelDataTypeFactory
name|typeFactory
parameter_list|)
block|{
name|super
argument_list|(
name|typeFactory
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|RelDataTypeField
name|getField
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|boolean
name|caseSensitive
parameter_list|,
name|boolean
name|elideRecord
parameter_list|)
block|{
specifier|final
name|int
name|index
init|=
name|nameToIndex
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
if|if
condition|(
name|index
operator|>=
literal|0
condition|)
block|{
name|resize
argument_list|(
name|index
operator|+
literal|1
argument_list|)
expr_stmt|;
return|return
name|super
operator|.
name|getField
argument_list|(
name|fieldName
argument_list|,
name|caseSensitive
argument_list|,
name|elideRecord
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
comment|/**    * Resizes the record if the new size greater than the current size.    *    * @param size New size    */
name|void
name|resize
parameter_list|(
name|int
name|size
parameter_list|)
block|{
name|int
name|currentSize
init|=
name|getFieldCount
argument_list|()
decl_stmt|;
if|if
condition|(
name|size
operator|>
name|currentSize
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
name|currentSize
init|;
name|i
operator|<
name|size
condition|;
name|i
operator|++
control|)
block|{
name|super
operator|.
name|getField
argument_list|(
literal|"$"
operator|+
name|i
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
name|computeDigest
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Gets index number from field name.    * @param fieldName Field name, format example '$1'    */
specifier|private
specifier|static
name|int
name|nameToIndex
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
name|Matcher
name|matcher
init|=
name|INDEX_PATTERN
operator|.
name|matcher
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
if|if
condition|(
name|matcher
operator|.
name|find
argument_list|()
condition|)
block|{
return|return
name|Integer
operator|.
name|parseInt
argument_list|(
name|matcher
operator|.
name|group
argument_list|(
literal|1
argument_list|)
argument_list|)
return|;
block|}
return|return
operator|-
literal|1
return|;
block|}
block|}
end_class

begin_comment
comment|// End DynamicTupleRecordType.java
end_comment

end_unit
