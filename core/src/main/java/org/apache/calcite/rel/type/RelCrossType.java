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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|linq4j
operator|.
name|Ord
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  * Type of the cartesian product of two or more sets of records.  *  *<p>Its fields are those of its constituent records, but unlike a  * {@link RelRecordType}, those fields' names are not necessarily distinct.</p>  */
end_comment

begin_class
specifier|public
class|class
name|RelCrossType
extends|extends
name|RelDataTypeImpl
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|public
specifier|final
name|ImmutableList
argument_list|<
name|RelDataType
argument_list|>
name|types
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Creates a cartesian product type. This should only be called from a    * factory method.    */
specifier|public
name|RelCrossType
parameter_list|(
name|List
argument_list|<
name|RelDataType
argument_list|>
name|types
parameter_list|,
name|List
argument_list|<
name|RelDataTypeField
argument_list|>
name|fields
parameter_list|)
block|{
name|super
argument_list|(
name|fields
argument_list|)
expr_stmt|;
name|this
operator|.
name|types
operator|=
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|types
argument_list|)
expr_stmt|;
assert|assert
name|types
operator|.
name|size
argument_list|()
operator|>=
literal|1
assert|;
for|for
control|(
name|RelDataType
name|type
range|:
name|types
control|)
block|{
assert|assert
operator|!
operator|(
name|type
operator|instanceof
name|RelCrossType
operator|)
assert|;
block|}
name|computeDigest
argument_list|()
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
annotation|@
name|Override
specifier|public
name|boolean
name|isStruct
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|generateTypeString
parameter_list|(
name|StringBuilder
name|sb
parameter_list|,
name|boolean
name|withDetail
parameter_list|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"CrossType("
argument_list|)
expr_stmt|;
for|for
control|(
name|Ord
argument_list|<
name|RelDataType
argument_list|>
name|type
range|:
name|Ord
operator|.
name|zip
argument_list|(
name|types
argument_list|)
control|)
block|{
if|if
condition|(
name|type
operator|.
name|i
operator|>
literal|0
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|withDetail
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|type
operator|.
name|e
operator|.
name|getFullTypeString
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|sb
operator|.
name|append
argument_list|(
name|type
operator|.
name|e
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|sb
operator|.
name|append
argument_list|(
literal|")"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

