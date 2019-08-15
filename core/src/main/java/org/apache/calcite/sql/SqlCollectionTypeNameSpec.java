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
name|sql
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
name|rel
operator|.
name|type
operator|.
name|RelDataTypeFactory
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
name|parser
operator|.
name|SqlParserPos
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
name|type
operator|.
name|SqlTypeName
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
name|validate
operator|.
name|SqlValidator
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
name|util
operator|.
name|Litmus
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
name|util
operator|.
name|Util
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Objects
import|;
end_import

begin_comment
comment|/**  * A sql type name specification of collection type.  *  *<p>The grammar definition in SQL-2011 IWD 9075-2:201?(E)  * 6.1&lt;collection type&gt; is as following:  *<blockquote><pre>  *&lt;collection type&gt; ::=  *&lt;array type&gt;  *   |&lt;multiset type&gt;  *  *&lt;array type&gt; ::=  *&lt;data type&gt; ARRAY  *   [&lt;left bracket or trigraph&gt;  *&lt;maximum cardinality&gt;  *&lt;right bracket or trigraph&gt; ]  *  *&lt;maximum cardinality&gt; ::=  *&lt;unsigned integer&gt;  *  *&lt;multiset type&gt; ::=  *&lt;data type&gt; MULTISET  *</pre></blockquote>  *  *<p>This class is intended to be used in nested collection type, it can be used as the  * element type name of {@link SqlDataTypeSpec}. i.e. "int array array" or "int array multiset".  * For simple collection type like "int array", {@link SqlBasicTypeNameSpec} is descriptive enough.  */
end_comment

begin_class
specifier|public
class|class
name|SqlCollectionTypeNameSpec
extends|extends
name|SqlTypeNameSpec
block|{
specifier|private
specifier|final
name|SqlTypeNameSpec
name|elementTypeName
decl_stmt|;
specifier|private
specifier|final
name|SqlTypeName
name|collectionTypeName
decl_stmt|;
comment|/**    * Creates a {@code SqlCollectionTypeNameSpec}.    *    * @param elementTypeName    Type of the collection element.    * @param collectionTypeName Collection type name.    * @param pos                Parser position, must not be null.    */
specifier|public
name|SqlCollectionTypeNameSpec
parameter_list|(
name|SqlTypeNameSpec
name|elementTypeName
parameter_list|,
name|SqlTypeName
name|collectionTypeName
parameter_list|,
name|SqlParserPos
name|pos
parameter_list|)
block|{
name|super
argument_list|(
operator|new
name|SqlIdentifier
argument_list|(
name|collectionTypeName
operator|.
name|name
argument_list|()
argument_list|,
name|pos
argument_list|)
argument_list|,
name|pos
argument_list|)
expr_stmt|;
name|this
operator|.
name|elementTypeName
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|elementTypeName
argument_list|)
expr_stmt|;
name|this
operator|.
name|collectionTypeName
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|collectionTypeName
argument_list|)
expr_stmt|;
block|}
specifier|public
name|SqlTypeNameSpec
name|getElementTypeName
parameter_list|()
block|{
return|return
name|elementTypeName
return|;
block|}
annotation|@
name|Override
specifier|public
name|RelDataType
name|deriveType
parameter_list|(
name|RelDataTypeFactory
name|typeFactory
parameter_list|)
block|{
name|RelDataType
name|type
init|=
name|elementTypeName
operator|.
name|deriveType
argument_list|(
name|typeFactory
argument_list|)
decl_stmt|;
if|if
condition|(
name|type
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|createCollectionType
argument_list|(
name|type
argument_list|,
name|typeFactory
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|RelDataType
name|deriveType
parameter_list|(
name|SqlValidator
name|validator
parameter_list|)
block|{
specifier|final
name|RelDataType
name|type
init|=
name|elementTypeName
operator|.
name|deriveType
argument_list|(
name|validator
argument_list|)
decl_stmt|;
return|return
name|createCollectionType
argument_list|(
name|type
argument_list|,
name|validator
operator|.
name|getTypeFactory
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|unparse
parameter_list|(
name|SqlWriter
name|writer
parameter_list|,
name|int
name|leftPrec
parameter_list|,
name|int
name|rightPrec
parameter_list|)
block|{
name|elementTypeName
operator|.
name|unparse
argument_list|(
name|writer
argument_list|,
name|leftPrec
argument_list|,
name|rightPrec
argument_list|)
expr_stmt|;
name|writer
operator|.
name|keyword
argument_list|(
name|collectionTypeName
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|equalsDeep
parameter_list|(
name|SqlTypeNameSpec
name|spec
parameter_list|,
name|Litmus
name|litmus
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|spec
operator|instanceof
name|SqlCollectionTypeNameSpec
operator|)
condition|)
block|{
return|return
name|litmus
operator|.
name|fail
argument_list|(
literal|"{} != {}"
argument_list|,
name|this
argument_list|,
name|spec
argument_list|)
return|;
block|}
name|SqlCollectionTypeNameSpec
name|that
init|=
operator|(
name|SqlCollectionTypeNameSpec
operator|)
name|spec
decl_stmt|;
if|if
condition|(
operator|!
name|this
operator|.
name|elementTypeName
operator|.
name|equalsDeep
argument_list|(
name|that
operator|.
name|elementTypeName
argument_list|,
name|litmus
argument_list|)
condition|)
block|{
return|return
name|litmus
operator|.
name|fail
argument_list|(
literal|"{} != {}"
argument_list|,
name|this
argument_list|,
name|spec
argument_list|)
return|;
block|}
if|if
condition|(
operator|!
name|Objects
operator|.
name|equals
argument_list|(
name|this
operator|.
name|collectionTypeName
argument_list|,
name|that
operator|.
name|collectionTypeName
argument_list|)
condition|)
block|{
return|return
name|litmus
operator|.
name|fail
argument_list|(
literal|"{} != {}"
argument_list|,
name|this
argument_list|,
name|spec
argument_list|)
return|;
block|}
return|return
name|litmus
operator|.
name|succeed
argument_list|()
return|;
block|}
comment|//~ Tools ------------------------------------------------------------------
comment|/**    * Create collection data type.    * @param elementType Type of the collection element.    * @param typeFactory Type factory.    * @return The collection data type, or throw exception if the collection    *         type name does not belong to {@code SqlTypeName} enumerations.    */
specifier|private
name|RelDataType
name|createCollectionType
parameter_list|(
name|RelDataType
name|elementType
parameter_list|,
name|RelDataTypeFactory
name|typeFactory
parameter_list|)
block|{
switch|switch
condition|(
name|collectionTypeName
condition|)
block|{
case|case
name|MULTISET
case|:
return|return
name|typeFactory
operator|.
name|createMultisetType
argument_list|(
name|elementType
argument_list|,
operator|-
literal|1
argument_list|)
return|;
case|case
name|ARRAY
case|:
return|return
name|typeFactory
operator|.
name|createArrayType
argument_list|(
name|elementType
argument_list|,
operator|-
literal|1
argument_list|)
return|;
default|default:
throw|throw
name|Util
operator|.
name|unexpected
argument_list|(
name|collectionTypeName
argument_list|)
throw|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End SqlCollectionTypeNameSpec.java
end_comment

end_unit

