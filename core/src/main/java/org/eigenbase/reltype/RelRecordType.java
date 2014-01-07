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
name|reltype
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|*
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

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|sql
operator|.
name|type
operator|.
name|*
import|;
end_import

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|linq4j
operator|.
name|Ord
import|;
end_import

begin_comment
comment|/**  * RelRecordType represents a structured type having named fields.  */
end_comment

begin_class
specifier|public
class|class
name|RelRecordType
extends|extends
name|RelDataTypeImpl
implements|implements
name|Serializable
block|{
comment|//~ Constructors -----------------------------------------------------------
comment|/**      * Creates a<code>RecordType</code>. This should only be called from a      * factory method.      */
specifier|public
name|RelRecordType
parameter_list|(
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
name|computeDigest
argument_list|()
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
comment|// implement RelDataType
specifier|public
name|SqlTypeName
name|getSqlTypeName
parameter_list|()
block|{
return|return
name|SqlTypeName
operator|.
name|ROW
return|;
block|}
comment|// implement RelDataType
specifier|public
name|boolean
name|isNullable
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
comment|// implement RelDataType
specifier|public
name|int
name|getPrecision
parameter_list|()
block|{
comment|// REVIEW: angel 18-Aug-2005 Put in fake implementation for precision
return|return
literal|0
return|;
block|}
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
literal|"RecordType("
argument_list|)
expr_stmt|;
for|for
control|(
name|Ord
argument_list|<
name|RelDataTypeField
argument_list|>
name|ord
range|:
name|Ord
operator|.
name|zip
argument_list|(
name|fieldList
argument_list|)
control|)
block|{
if|if
condition|(
name|ord
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
name|RelDataTypeField
name|field
init|=
name|ord
operator|.
name|e
decl_stmt|;
if|if
condition|(
name|withDetail
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|field
operator|.
name|getType
argument_list|()
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
name|field
operator|.
name|getType
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|field
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|")"
argument_list|)
expr_stmt|;
block|}
comment|/**      * Per {@link Serializable} API, provides a replacement object to be written      * during serialization.      *      *<p>This implementation converts this RelRecordType into a      * SerializableRelRecordType, whose<code>readResolve</code> method converts      * it back to a RelRecordType during deserialization.      */
specifier|private
name|Object
name|writeReplace
parameter_list|()
block|{
return|return
operator|new
name|SerializableRelRecordType
argument_list|(
name|fieldList
argument_list|)
return|;
block|}
comment|//~ Inner Classes ----------------------------------------------------------
comment|/**      * Skinny object which has the same information content as a {@link      * RelRecordType} but skips redundant stuff like digest and the immutable      * list.      */
specifier|private
specifier|static
class|class
name|SerializableRelRecordType
implements|implements
name|Serializable
block|{
specifier|private
name|List
argument_list|<
name|RelDataTypeField
argument_list|>
name|fields
decl_stmt|;
specifier|private
name|SerializableRelRecordType
parameter_list|(
name|List
argument_list|<
name|RelDataTypeField
argument_list|>
name|fields
parameter_list|)
block|{
name|this
operator|.
name|fields
operator|=
name|fields
expr_stmt|;
block|}
comment|/**          * Per {@link Serializable} API. See {@link          * RelRecordType#writeReplace()}.          */
specifier|private
name|Object
name|readResolve
parameter_list|()
block|{
return|return
operator|new
name|RelRecordType
argument_list|(
name|fields
argument_list|)
return|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End RelRecordType.java
end_comment

end_unit

