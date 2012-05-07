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
name|java
operator|.
name|io
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

begin_comment
comment|/**  * Abstract base class for SQL implementations of {@link RelDataType}.  *  * @author jhyde  * @version $Id$  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractSqlType
extends|extends
name|RelDataTypeImpl
implements|implements
name|Cloneable
implements|,
name|Serializable
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|protected
specifier|final
name|SqlTypeName
name|typeName
decl_stmt|;
specifier|protected
name|boolean
name|isNullable
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**      * Creates an AbstractSqlType.      *      * @param typeName Type name      * @param isNullable Whether nullable      * @param fields Fields of type, or null if not a record type      */
specifier|protected
name|AbstractSqlType
parameter_list|(
name|SqlTypeName
name|typeName
parameter_list|,
name|boolean
name|isNullable
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
name|typeName
operator|=
name|typeName
expr_stmt|;
name|this
operator|.
name|isNullable
operator|=
name|isNullable
operator|||
operator|(
name|typeName
operator|==
name|SqlTypeName
operator|.
name|NULL
operator|)
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
name|typeName
return|;
block|}
comment|// implement RelDataType
specifier|public
name|boolean
name|isNullable
parameter_list|()
block|{
return|return
name|isNullable
return|;
block|}
comment|// implement RelDataType
specifier|public
name|RelDataTypeFamily
name|getFamily
parameter_list|()
block|{
return|return
name|SqlTypeFamily
operator|.
name|getFamilyForSqlType
argument_list|(
name|typeName
argument_list|)
return|;
block|}
comment|// implement RelDataType
specifier|public
name|RelDataTypePrecedenceList
name|getPrecedenceList
parameter_list|()
block|{
name|RelDataTypePrecedenceList
name|list
init|=
name|SqlTypeExplicitPrecedenceList
operator|.
name|getListForType
argument_list|(
name|this
argument_list|)
decl_stmt|;
if|if
condition|(
name|list
operator|!=
literal|null
condition|)
block|{
return|return
name|list
return|;
block|}
return|return
name|super
operator|.
name|getPrecedenceList
argument_list|()
return|;
block|}
block|}
end_class

begin_comment
comment|// End AbstractSqlType.java
end_comment

end_unit

