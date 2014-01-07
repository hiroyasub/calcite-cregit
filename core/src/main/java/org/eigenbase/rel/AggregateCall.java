begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to Julian Hyde under one or more contributor license // agreements. See the NOTICE file distributed with this work for // additional information regarding copyright ownership. // // Julian Hyde licenses this file to you under the Apache License, // Version 2.0 (the "License"); you may not use this file except in // compliance with the License. You may obtain a copy of the License at: // // http://www.apache.org/licenses/LICENSE-2.0 // // Unless required by applicable law or agreed to in writing, software // distributed under the License is distributed on an "AS IS" BASIS, // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. // See the License for the specific language governing permissions and // limitations under the License. */
end_comment

begin_package
package|package
name|org
operator|.
name|eigenbase
operator|.
name|rel
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

begin_comment
comment|/**  * Call to an aggregation function within an {@link AggregateRel}.  */
end_comment

begin_class
specifier|public
class|class
name|AggregateCall
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|private
specifier|final
name|Aggregation
name|aggregation
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|distinct
decl_stmt|;
specifier|public
specifier|final
name|RelDataType
name|type
decl_stmt|;
specifier|public
specifier|final
name|String
name|name
decl_stmt|;
comment|// We considered using ImmutableIntList but we would not save much memory:
comment|// since all values are small, ImmutableList uses cached Integer values.
specifier|private
specifier|final
name|ImmutableList
argument_list|<
name|Integer
argument_list|>
name|argList
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**      * Creates an AggregateCall.      *      * @param aggregation Aggregation      * @param distinct Whether distinct      * @param argList List of ordinals of arguments      * @param type Result type      * @param name Name (may be null)      */
specifier|public
name|AggregateCall
parameter_list|(
name|Aggregation
name|aggregation
parameter_list|,
name|boolean
name|distinct
parameter_list|,
name|List
argument_list|<
name|Integer
argument_list|>
name|argList
parameter_list|,
name|RelDataType
name|type
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
assert|assert
name|aggregation
operator|!=
literal|null
assert|;
assert|assert
name|argList
operator|!=
literal|null
assert|;
assert|assert
name|type
operator|!=
literal|null
assert|;
name|this
operator|.
name|aggregation
operator|=
name|aggregation
expr_stmt|;
name|this
operator|.
name|argList
operator|=
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|argList
argument_list|)
expr_stmt|;
name|this
operator|.
name|distinct
operator|=
name|distinct
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
comment|/**      * Returns whether this AggregateCall is distinct, as in<code>      * COUNT(DISTINCT empno)</code>.      *      * @return whether distinct      */
specifier|public
specifier|final
name|boolean
name|isDistinct
parameter_list|()
block|{
return|return
name|distinct
return|;
block|}
comment|/**      * Returns the Aggregation.      *      * @return aggregation      */
specifier|public
specifier|final
name|Aggregation
name|getAggregation
parameter_list|()
block|{
return|return
name|aggregation
return|;
block|}
comment|/**      * Returns the ordinals of the arguments to this call.      *      *<p>The list is immutable.      *      * @return list of argument ordinals      */
specifier|public
specifier|final
name|List
argument_list|<
name|Integer
argument_list|>
name|getArgList
parameter_list|()
block|{
return|return
name|argList
return|;
block|}
comment|/**      * Returns the result type.      *      * @return result type      */
specifier|public
specifier|final
name|RelDataType
name|getType
parameter_list|()
block|{
return|return
name|type
return|;
block|}
comment|/**      * Returns the name.      *      * @return name      */
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
comment|/**      * Creates an equivalent AggregateCall that has a new name.      *      * @param name New name (may be null)      */
specifier|public
name|AggregateCall
name|rename
parameter_list|(
name|String
name|name
parameter_list|)
block|{
comment|// no need to copy argList - already immutable
return|return
operator|new
name|AggregateCall
argument_list|(
name|aggregation
argument_list|,
name|distinct
argument_list|,
name|argList
argument_list|,
name|type
argument_list|,
name|name
argument_list|)
return|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|buf
init|=
operator|new
name|StringBuilder
argument_list|(
name|aggregation
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|"("
argument_list|)
expr_stmt|;
if|if
condition|(
name|distinct
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
operator|(
name|argList
operator|.
name|size
argument_list|()
operator|==
literal|0
operator|)
condition|?
literal|"DISTINCT"
else|:
literal|"DISTINCT "
argument_list|)
expr_stmt|;
block|}
name|int
name|i
init|=
operator|-
literal|1
decl_stmt|;
for|for
control|(
name|Integer
name|arg
range|:
name|argList
control|)
block|{
if|if
condition|(
operator|++
name|i
operator|>
literal|0
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
block|}
name|buf
operator|.
name|append
argument_list|(
literal|"$"
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|arg
argument_list|)
expr_stmt|;
block|}
name|buf
operator|.
name|append
argument_list|(
literal|")"
argument_list|)
expr_stmt|;
return|return
name|buf
operator|.
name|toString
argument_list|()
return|;
block|}
comment|// override Object
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|o
operator|instanceof
name|AggregateCall
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|AggregateCall
name|other
init|=
operator|(
name|AggregateCall
operator|)
name|o
decl_stmt|;
return|return
name|aggregation
operator|.
name|equals
argument_list|(
name|other
operator|.
name|aggregation
argument_list|)
operator|&&
operator|(
name|distinct
operator|==
name|other
operator|.
name|distinct
operator|)
operator|&&
name|argList
operator|.
name|equals
argument_list|(
name|other
operator|.
name|argList
argument_list|)
return|;
block|}
comment|// override Object
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|aggregation
operator|.
name|hashCode
argument_list|()
operator|+
name|argList
operator|.
name|hashCode
argument_list|()
return|;
block|}
comment|/**      * Creates a binding of this call in the context of an {@link AggregateRel},      * which can then be used to infer the return type.      */
specifier|public
name|AggregateRelBase
operator|.
name|AggCallBinding
name|createBinding
parameter_list|(
name|AggregateRelBase
name|aggregateRelBase
parameter_list|)
block|{
return|return
operator|new
name|AggregateRelBase
operator|.
name|AggCallBinding
argument_list|(
name|aggregateRelBase
operator|.
name|getCluster
argument_list|()
operator|.
name|getTypeFactory
argument_list|()
argument_list|,
operator|(
name|SqlAggFunction
operator|)
name|aggregation
argument_list|,
name|aggregateRelBase
argument_list|,
name|argList
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|// End AggregateCall.java
end_comment

end_unit

