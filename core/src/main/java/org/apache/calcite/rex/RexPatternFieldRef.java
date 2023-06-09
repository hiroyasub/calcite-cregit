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
name|rex
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
name|RelDataType
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
name|sql
operator|.
name|SqlKind
import|;
end_import

begin_comment
comment|/**  * Variable that references a field of an input relational expression.  */
end_comment

begin_class
specifier|public
class|class
name|RexPatternFieldRef
extends|extends
name|RexInputRef
block|{
specifier|private
specifier|final
name|String
name|alpha
decl_stmt|;
specifier|public
name|RexPatternFieldRef
parameter_list|(
name|String
name|alpha
parameter_list|,
name|int
name|index
parameter_list|,
name|RelDataType
name|type
parameter_list|)
block|{
name|super
argument_list|(
name|index
argument_list|,
name|type
argument_list|)
expr_stmt|;
name|this
operator|.
name|alpha
operator|=
name|alpha
expr_stmt|;
name|digest
operator|=
name|alpha
operator|+
literal|".$"
operator|+
name|index
expr_stmt|;
block|}
specifier|public
name|String
name|getAlpha
parameter_list|()
block|{
return|return
name|alpha
return|;
block|}
specifier|public
specifier|static
name|RexPatternFieldRef
name|of
parameter_list|(
name|String
name|alpha
parameter_list|,
name|int
name|index
parameter_list|,
name|RelDataType
name|type
parameter_list|)
block|{
return|return
operator|new
name|RexPatternFieldRef
argument_list|(
name|alpha
argument_list|,
name|index
argument_list|,
name|type
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|RexPatternFieldRef
name|of
parameter_list|(
name|String
name|alpha
parameter_list|,
name|RexInputRef
name|ref
parameter_list|)
block|{
return|return
operator|new
name|RexPatternFieldRef
argument_list|(
name|alpha
argument_list|,
name|ref
operator|.
name|getIndex
argument_list|()
argument_list|,
name|ref
operator|.
name|getType
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
parameter_list|<
name|R
parameter_list|>
name|R
name|accept
parameter_list|(
name|RexVisitor
argument_list|<
name|R
argument_list|>
name|visitor
parameter_list|)
block|{
return|return
name|visitor
operator|.
name|visitPatternFieldRef
argument_list|(
name|this
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
parameter_list|<
name|R
parameter_list|,
name|P
parameter_list|>
name|R
name|accept
parameter_list|(
name|RexBiVisitor
argument_list|<
name|R
argument_list|,
name|P
argument_list|>
name|visitor
parameter_list|,
name|P
name|arg
parameter_list|)
block|{
return|return
name|visitor
operator|.
name|visitPatternFieldRef
argument_list|(
name|this
argument_list|,
name|arg
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|SqlKind
name|getKind
parameter_list|()
block|{
return|return
name|SqlKind
operator|.
name|PATTERN_INPUT_REF
return|;
block|}
block|}
end_class

end_unit

