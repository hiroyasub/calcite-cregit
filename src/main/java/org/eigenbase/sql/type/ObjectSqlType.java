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
name|sql
operator|.
name|type
package|;
end_package

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

begin_comment
comment|/**  * ObjectSqlType represents an SQL structured user-defined type.  *  * @author John V. Sichi  * @version $Id$  */
end_comment

begin_class
specifier|public
class|class
name|ObjectSqlType
extends|extends
name|AbstractSqlType
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|private
specifier|final
name|SqlIdentifier
name|sqlIdentifier
decl_stmt|;
specifier|private
specifier|final
name|RelDataTypeComparability
name|comparability
decl_stmt|;
specifier|private
name|RelDataTypeFamily
name|family
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**      * Constructs an object type. This should only be called from a factory      * method.      *      * @param typeName SqlTypeName for this type (either Distinct or Structured)      * @param sqlIdentifier identifier for this type      * @param nullable whether type accepts nulls      * @param fields object attribute definitions      */
specifier|public
name|ObjectSqlType
parameter_list|(
name|SqlTypeName
name|typeName
parameter_list|,
name|SqlIdentifier
name|sqlIdentifier
parameter_list|,
name|boolean
name|nullable
parameter_list|,
name|RelDataTypeField
index|[]
name|fields
parameter_list|,
name|RelDataTypeComparability
name|comparability
parameter_list|)
block|{
name|super
argument_list|(
name|typeName
argument_list|,
name|nullable
argument_list|,
name|fields
argument_list|)
expr_stmt|;
name|this
operator|.
name|sqlIdentifier
operator|=
name|sqlIdentifier
expr_stmt|;
name|this
operator|.
name|comparability
operator|=
name|comparability
expr_stmt|;
name|computeDigest
argument_list|()
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
specifier|public
name|void
name|setFamily
parameter_list|(
name|RelDataTypeFamily
name|family
parameter_list|)
block|{
name|this
operator|.
name|family
operator|=
name|family
expr_stmt|;
block|}
comment|// implement RelDataType
specifier|public
name|RelDataTypeComparability
name|getComparability
parameter_list|()
block|{
return|return
name|comparability
return|;
block|}
comment|// override AbstractSqlType
specifier|public
name|SqlIdentifier
name|getSqlIdentifier
parameter_list|()
block|{
return|return
name|sqlIdentifier
return|;
block|}
comment|// override AbstractSqlType
specifier|public
name|RelDataTypeFamily
name|getFamily
parameter_list|()
block|{
comment|// each UDT is in its own lonely family, until one day when
comment|// we support inheritance (at which time also need to implement
comment|// getPrecedenceList).
return|return
name|family
return|;
block|}
comment|// implement RelDataTypeImpl
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
comment|// TODO jvs 10-Feb-2005:  proper quoting; dump attributes withDetail?
name|sb
operator|.
name|append
argument_list|(
literal|"ObjectSqlType("
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|sqlIdentifier
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
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
comment|// End ObjectSqlType.java
end_comment

end_unit

