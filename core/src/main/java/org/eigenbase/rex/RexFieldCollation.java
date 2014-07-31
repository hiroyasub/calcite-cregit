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
name|rex
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|rel
operator|.
name|RelFieldCollation
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
name|SqlKind
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|util
operator|.
name|Pair
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
name|ImmutableSet
import|;
end_import

begin_comment
comment|/**  * Expression combined with sort flags (DESCENDING, NULLS LAST).  */
end_comment

begin_class
specifier|public
class|class
name|RexFieldCollation
extends|extends
name|Pair
argument_list|<
name|RexNode
argument_list|,
name|ImmutableSet
argument_list|<
name|SqlKind
argument_list|>
argument_list|>
block|{
specifier|public
name|RexFieldCollation
parameter_list|(
name|RexNode
name|left
parameter_list|,
name|Set
argument_list|<
name|SqlKind
argument_list|>
name|right
parameter_list|)
block|{
name|super
argument_list|(
name|left
argument_list|,
name|ImmutableSet
operator|.
name|copyOf
argument_list|(
name|right
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|String
name|s
init|=
name|left
operator|.
name|toString
argument_list|()
decl_stmt|;
for|for
control|(
name|SqlKind
name|operator
range|:
name|right
control|)
block|{
switch|switch
condition|(
name|operator
condition|)
block|{
case|case
name|DESCENDING
case|:
name|s
operator|+=
literal|" DESC"
expr_stmt|;
break|break;
case|case
name|NULLS_FIRST
case|:
name|s
operator|+=
literal|" NULLS FIRST"
expr_stmt|;
break|break;
case|case
name|NULLS_LAST
case|:
name|s
operator|+=
literal|" NULLS LAST"
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|AssertionError
argument_list|(
name|operator
argument_list|)
throw|;
block|}
block|}
return|return
name|s
return|;
block|}
specifier|public
name|RelFieldCollation
operator|.
name|Direction
name|getDirection
parameter_list|()
block|{
return|return
name|right
operator|.
name|contains
argument_list|(
name|SqlKind
operator|.
name|DESCENDING
argument_list|)
condition|?
name|RelFieldCollation
operator|.
name|Direction
operator|.
name|DESCENDING
else|:
name|RelFieldCollation
operator|.
name|Direction
operator|.
name|ASCENDING
return|;
block|}
specifier|public
name|RelFieldCollation
operator|.
name|NullDirection
name|getNullDirection
parameter_list|()
block|{
return|return
name|right
operator|.
name|contains
argument_list|(
name|SqlKind
operator|.
name|NULLS_LAST
argument_list|)
condition|?
name|RelFieldCollation
operator|.
name|NullDirection
operator|.
name|LAST
else|:
name|right
operator|.
name|contains
argument_list|(
name|SqlKind
operator|.
name|NULLS_FIRST
argument_list|)
condition|?
name|RelFieldCollation
operator|.
name|NullDirection
operator|.
name|FIRST
else|:
name|RelFieldCollation
operator|.
name|NullDirection
operator|.
name|UNSPECIFIED
return|;
block|}
block|}
end_class

begin_comment
comment|// End RexFieldCollation.java
end_comment

end_unit

