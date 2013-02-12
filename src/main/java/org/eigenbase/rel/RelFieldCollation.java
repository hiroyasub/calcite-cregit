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

begin_comment
comment|/**  * Definition of the ordering of one field of a RelNode whose  * output is to be sorted.  *  * @see RelCollation  */
end_comment

begin_class
specifier|public
class|class
name|RelFieldCollation
block|{
comment|//~ Enums ------------------------------------------------------------------
comment|/**      * Direction that a field is ordered in.      */
specifier|public
enum|enum
name|Direction
block|{
comment|/**          * Ascending direction: A value is always followed by a greater or equal          * value.          */
name|Ascending
block|,
comment|/**          * Strictly ascending direction: A value is always followed by a greater          * value.          */
name|StrictlyAscending
block|,
comment|/**          * Descending direction: A value is always followed by a lesser or equal          * value.          */
name|Descending
block|,
comment|/**          * Strictly descending direction: A value is always followed by a lesser          * value.          */
name|StrictlyDescending
block|,
comment|/**          * Clustered direction: Values occur in no particular order, and the          * same value may occur in contiguous groups, but never occurs after          * that. This sort order tends to occur when values are ordered          * according to a hash-key.          */
name|Clustered
block|,     }
comment|/**      * Ordering of nulls.      */
specifier|public
enum|enum
name|NullDirection
block|{
name|FIRST
block|,
name|LAST
block|,
name|UNSPECIFIED
block|}
comment|//~ Instance fields --------------------------------------------------------
comment|/**      * 0-based index of field being sorted.      */
specifier|private
specifier|final
name|int
name|fieldIndex
decl_stmt|;
comment|/**      * Direction of sorting.      */
specifier|private
specifier|final
name|Direction
name|direction
decl_stmt|;
comment|/**      * Direction of sorting of nulls.      */
specifier|public
specifier|final
name|NullDirection
name|nullDirection
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**      * Creates an ascending field collation.      */
specifier|public
name|RelFieldCollation
parameter_list|(
name|int
name|fieldIndex
parameter_list|)
block|{
name|this
argument_list|(
name|fieldIndex
argument_list|,
name|Direction
operator|.
name|Ascending
argument_list|,
name|NullDirection
operator|.
name|UNSPECIFIED
argument_list|)
expr_stmt|;
block|}
comment|/**      * Creates a field collation with unspecified null direction.      */
specifier|public
name|RelFieldCollation
parameter_list|(
name|int
name|fieldIndex
parameter_list|,
name|Direction
name|direction
parameter_list|)
block|{
name|this
argument_list|(
name|fieldIndex
argument_list|,
name|direction
argument_list|,
name|NullDirection
operator|.
name|UNSPECIFIED
argument_list|)
expr_stmt|;
block|}
comment|/**      * Creates a field collation.      */
specifier|public
name|RelFieldCollation
parameter_list|(
name|int
name|fieldIndex
parameter_list|,
name|Direction
name|direction
parameter_list|,
name|NullDirection
name|nullDirection
parameter_list|)
block|{
name|this
operator|.
name|fieldIndex
operator|=
name|fieldIndex
expr_stmt|;
name|this
operator|.
name|direction
operator|=
name|direction
expr_stmt|;
name|this
operator|.
name|nullDirection
operator|=
name|nullDirection
expr_stmt|;
assert|assert
name|direction
operator|!=
literal|null
assert|;
assert|assert
name|nullDirection
operator|!=
literal|null
assert|;
block|}
comment|//~ Methods ----------------------------------------------------------------
comment|/** Creates a copy of this RelFieldCollation against a different field. */
specifier|public
name|RelFieldCollation
name|copy
parameter_list|(
name|int
name|target
parameter_list|)
block|{
if|if
condition|(
name|target
operator|==
name|fieldIndex
condition|)
block|{
return|return
name|this
return|;
block|}
return|return
operator|new
name|RelFieldCollation
argument_list|(
name|target
argument_list|,
name|direction
argument_list|,
name|nullDirection
argument_list|)
return|;
block|}
comment|// implement Object
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|obj
operator|instanceof
name|RelFieldCollation
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|RelFieldCollation
name|other
init|=
operator|(
name|RelFieldCollation
operator|)
name|obj
decl_stmt|;
return|return
operator|(
name|fieldIndex
operator|==
name|other
operator|.
name|fieldIndex
operator|)
operator|&&
operator|(
name|direction
operator|==
name|other
operator|.
name|direction
operator|)
return|;
block|}
comment|// implement Object
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
operator|(
name|this
operator|.
name|direction
operator|.
name|ordinal
argument_list|()
operator|<<
literal|4
operator|)
operator||
name|this
operator|.
name|fieldIndex
return|;
block|}
specifier|public
name|int
name|getFieldIndex
parameter_list|()
block|{
return|return
name|fieldIndex
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
name|direction
return|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|fieldIndex
operator|+
literal|" "
operator|+
name|direction
operator|+
operator|(
name|nullDirection
operator|==
name|NullDirection
operator|.
name|UNSPECIFIED
condition|?
literal|""
else|:
literal|" "
operator|+
name|nullDirection
operator|)
return|;
block|}
block|}
end_class

begin_comment
comment|// End RelFieldCollation.java
end_comment

end_unit

