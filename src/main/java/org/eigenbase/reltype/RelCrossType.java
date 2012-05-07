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
name|org
operator|.
name|eigenbase
operator|.
name|oj
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_comment
comment|// REVIEW jvs 17-Dec-2004:  does this still need to exist?  Is it supposed
end_comment

begin_comment
comment|// to have fields?
end_comment

begin_comment
comment|/**  * Type of the cartesian product of two or more sets of records.  *  *<p>Its fields are those of its constituent records, but unlike a {@link  * RelRecordType}, those fields' names are not necessarily distinct.</p>  *  * @author jhyde  * @version $Id$  */
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
name|RelDataType
index|[]
name|types
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**      * Creates a cartesian product type. This should only be called from a      * factory method.      *      * @pre types != null      * @pre types.length>= 1      * @pre !(types[i] instanceof CrossType)      */
specifier|public
name|RelCrossType
parameter_list|(
name|RelDataType
index|[]
name|types
parameter_list|,
name|RelDataTypeField
index|[]
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
name|types
expr_stmt|;
assert|assert
operator|(
name|types
operator|!=
literal|null
operator|)
assert|;
assert|assert
operator|(
name|types
operator|.
name|length
operator|>=
literal|1
operator|)
assert|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|types
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
assert|assert
operator|(
operator|!
operator|(
name|types
index|[
name|i
index|]
operator|instanceof
name|RelCrossType
operator|)
operator|)
assert|;
block|}
name|computeDigest
argument_list|()
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
specifier|public
name|boolean
name|isStruct
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
specifier|public
name|RelDataTypeField
name|getField
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"not applicable to a join type"
argument_list|)
throw|;
block|}
specifier|public
name|int
name|getFieldOrdinal
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
specifier|final
name|int
name|ordinal
init|=
name|OJSyntheticClass
operator|.
name|getOrdinal
argument_list|(
name|fieldName
argument_list|,
literal|false
argument_list|)
decl_stmt|;
if|if
condition|(
name|ordinal
operator|>=
literal|0
condition|)
block|{
return|return
name|ordinal
return|;
block|}
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"not applicable to a join type"
argument_list|)
throw|;
block|}
specifier|public
name|RelDataTypeField
index|[]
name|getFields
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"not applicable to a join type"
argument_list|)
throw|;
block|}
specifier|public
name|RelDataType
index|[]
name|getTypes
parameter_list|()
block|{
return|return
name|types
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
literal|"CrossType("
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|types
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
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
name|RelDataType
name|type
init|=
name|types
index|[
name|i
index|]
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
name|type
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

begin_comment
comment|// End RelCrossType.java
end_comment

end_unit

