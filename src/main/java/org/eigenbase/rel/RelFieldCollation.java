begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to DynamoBI Corporation (DynamoBI) under one // or more contributor license agreements.  See the NOTICE file // distributed with this work for additional information // regarding copyright ownership.  DynamoBI licenses this file // to you under the Apache License, Version 2.0 (the // "License"); you may not use this file except in compliance // with the License.  You may obtain a copy of the License at  //   http://www.apache.org/licenses/LICENSE-2.0  // Unless required by applicable law or agreed to in writing, // software distributed under the License is distributed on an // "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY // KIND, either express or implied.  See the License for the // specific language governing permissions and limitations // under the License. */
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
comment|/**  * RelFieldCollation defines the ordering for one field of a RelNode whose  * output is to be sorted.  *  *<p>TODO: collation sequence (including ASC/DESC)  */
end_comment

begin_class
specifier|public
class|class
name|RelFieldCollation
block|{
comment|//~ Static fields/initializers ---------------------------------------------
specifier|public
specifier|static
specifier|final
name|RelFieldCollation
index|[]
name|emptyCollationArray
init|=
operator|new
name|RelFieldCollation
index|[
literal|0
index|]
decl_stmt|;
comment|//~ Enums ------------------------------------------------------------------
comment|/**      * Direction that a field is ordered in.      */
specifier|public
specifier|static
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
block|}
comment|//~ Methods ----------------------------------------------------------------
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
return|;
block|}
block|}
end_class

begin_comment
comment|// End RelFieldCollation.java
end_comment

end_unit

