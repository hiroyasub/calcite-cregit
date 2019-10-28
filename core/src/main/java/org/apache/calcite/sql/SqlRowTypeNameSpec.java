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
name|Pair
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
name|java
operator|.
name|util
operator|.
name|Objects
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Collectors
import|;
end_import

begin_comment
comment|/**  * A sql type name specification of row type.  *  *<p>The grammar definition in SQL-2011 IWD 9075-2:201?(E)  * 6.1&lt;data type&gt; is as following:  *<blockquote><pre>  *&lt;row type&gt; ::=  *   ROW&lt;row type body&gt;  *&lt;row type body&gt; ::=  *&lt;left paren&gt;&lt;field definition&gt;  *   [ {&lt;comma&gt;&lt;field definition&gt; }... ]  *&lt;right paren&gt;  *  *&lt;field definition&gt; ::=  *&lt;field name&gt;&lt;data type&gt;  *</pre></blockquote>  *  *<p>As a extended syntax to the standard SQL, each field type can have a  * [ NULL | NOT NULL ] suffix specification, i.e.  * Row(f0 int null, f1 varchar not null). The default is NOT NULL(not nullable).  */
end_comment

begin_class
specifier|public
class|class
name|SqlRowTypeNameSpec
extends|extends
name|SqlTypeNameSpec
block|{
specifier|private
specifier|final
name|List
argument_list|<
name|SqlIdentifier
argument_list|>
name|fieldNames
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|SqlDataTypeSpec
argument_list|>
name|fieldTypes
decl_stmt|;
comment|/**    * Creates a row type specification.    *    * @param pos        The parser position    * @param fieldNames The field names    * @param fieldTypes The field data types    */
specifier|public
name|SqlRowTypeNameSpec
parameter_list|(
name|SqlParserPos
name|pos
parameter_list|,
name|List
argument_list|<
name|SqlIdentifier
argument_list|>
name|fieldNames
parameter_list|,
name|List
argument_list|<
name|SqlDataTypeSpec
argument_list|>
name|fieldTypes
parameter_list|)
block|{
name|super
argument_list|(
operator|new
name|SqlIdentifier
argument_list|(
name|SqlTypeName
operator|.
name|ROW
operator|.
name|getName
argument_list|()
argument_list|,
name|pos
argument_list|)
argument_list|,
name|pos
argument_list|)
expr_stmt|;
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|fieldNames
argument_list|)
expr_stmt|;
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|fieldTypes
argument_list|)
expr_stmt|;
assert|assert
name|fieldNames
operator|.
name|size
argument_list|()
operator|>
literal|0
assert|;
comment|// there must be at least one field.
name|this
operator|.
name|fieldNames
operator|=
name|fieldNames
expr_stmt|;
name|this
operator|.
name|fieldTypes
operator|=
name|fieldTypes
expr_stmt|;
block|}
specifier|public
name|List
argument_list|<
name|SqlIdentifier
argument_list|>
name|getFieldNames
parameter_list|()
block|{
return|return
name|fieldNames
return|;
block|}
specifier|public
name|List
argument_list|<
name|SqlDataTypeSpec
argument_list|>
name|getFieldTypes
parameter_list|()
block|{
return|return
name|fieldTypes
return|;
block|}
specifier|public
name|int
name|getArity
parameter_list|()
block|{
return|return
name|fieldNames
operator|.
name|size
argument_list|()
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
name|writer
operator|.
name|print
argument_list|(
name|getTypeName
argument_list|()
operator|.
name|getSimple
argument_list|()
argument_list|)
expr_stmt|;
name|SqlWriter
operator|.
name|Frame
name|frame
init|=
name|writer
operator|.
name|startList
argument_list|(
name|SqlWriter
operator|.
name|FrameTypeEnum
operator|.
name|FUN_CALL
argument_list|,
literal|"("
argument_list|,
literal|")"
argument_list|)
decl_stmt|;
for|for
control|(
name|Pair
argument_list|<
name|SqlIdentifier
argument_list|,
name|SqlDataTypeSpec
argument_list|>
name|p
range|:
name|Pair
operator|.
name|zip
argument_list|(
name|this
operator|.
name|fieldNames
argument_list|,
name|this
operator|.
name|fieldTypes
argument_list|)
control|)
block|{
name|writer
operator|.
name|sep
argument_list|(
literal|","
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|p
operator|.
name|left
operator|.
name|unparse
argument_list|(
name|writer
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|p
operator|.
name|right
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
if|if
condition|(
name|p
operator|.
name|right
operator|.
name|getNullable
argument_list|()
operator|!=
literal|null
operator|&&
name|p
operator|.
name|right
operator|.
name|getNullable
argument_list|()
condition|)
block|{
comment|// Row fields default is not nullable.
name|writer
operator|.
name|print
argument_list|(
literal|"NULL"
argument_list|)
expr_stmt|;
block|}
block|}
name|writer
operator|.
name|endList
argument_list|(
name|frame
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
name|node
parameter_list|,
name|Litmus
name|litmus
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|node
operator|instanceof
name|SqlRowTypeNameSpec
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
name|node
argument_list|)
return|;
block|}
name|SqlRowTypeNameSpec
name|that
init|=
operator|(
name|SqlRowTypeNameSpec
operator|)
name|node
decl_stmt|;
if|if
condition|(
name|this
operator|.
name|fieldNames
operator|.
name|size
argument_list|()
operator|!=
name|that
operator|.
name|fieldNames
operator|.
name|size
argument_list|()
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
name|node
argument_list|)
return|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|fieldNames
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
operator|!
name|this
operator|.
name|fieldNames
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|equalsDeep
argument_list|(
name|that
operator|.
name|fieldNames
operator|.
name|get
argument_list|(
name|i
argument_list|)
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
name|node
argument_list|)
return|;
block|}
block|}
if|if
condition|(
name|this
operator|.
name|fieldTypes
operator|.
name|size
argument_list|()
operator|!=
name|that
operator|.
name|fieldTypes
operator|.
name|size
argument_list|()
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
name|node
argument_list|)
return|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|fieldTypes
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
operator|!
name|this
operator|.
name|fieldTypes
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|equals
argument_list|(
name|that
operator|.
name|fieldTypes
operator|.
name|get
argument_list|(
name|i
argument_list|)
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
name|node
argument_list|)
return|;
block|}
block|}
return|return
name|litmus
operator|.
name|succeed
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|RelDataType
name|deriveType
parameter_list|(
name|SqlValidator
name|sqlValidator
parameter_list|)
block|{
specifier|final
name|RelDataTypeFactory
name|typeFactory
init|=
name|sqlValidator
operator|.
name|getTypeFactory
argument_list|()
decl_stmt|;
return|return
name|typeFactory
operator|.
name|createStructType
argument_list|(
name|fieldTypes
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|dt
lambda|->
name|dt
operator|.
name|deriveType
argument_list|(
name|sqlValidator
argument_list|)
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toList
argument_list|()
argument_list|)
argument_list|,
name|fieldNames
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|SqlIdentifier
operator|::
name|toString
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toList
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|// End SqlRowTypeNameSpec.java
end_comment

end_unit

