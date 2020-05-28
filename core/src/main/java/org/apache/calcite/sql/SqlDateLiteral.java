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
name|util
operator|.
name|DateString
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
comment|/**  * A SQL literal representing a DATE value, such as<code>DATE  * '2004-10-22'</code>.  *  *<p>Create values using {@link SqlLiteral#createDate}.  */
end_comment

begin_class
specifier|public
class|class
name|SqlDateLiteral
extends|extends
name|SqlAbstractDateTimeLiteral
block|{
comment|//~ Constructors -----------------------------------------------------------
name|SqlDateLiteral
parameter_list|(
name|DateString
name|d
parameter_list|,
name|SqlParserPos
name|pos
parameter_list|)
block|{
name|super
argument_list|(
name|d
argument_list|,
literal|false
argument_list|,
name|SqlTypeName
operator|.
name|DATE
argument_list|,
literal|0
argument_list|,
name|pos
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
comment|/** Converts this literal to a {@link DateString}. */
specifier|protected
name|DateString
name|getDate
parameter_list|()
block|{
return|return
operator|(
name|DateString
operator|)
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|value
argument_list|,
literal|"value"
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|SqlDateLiteral
name|clone
parameter_list|(
name|SqlParserPos
name|pos
parameter_list|)
block|{
return|return
operator|new
name|SqlDateLiteral
argument_list|(
name|getDate
argument_list|()
argument_list|,
name|pos
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"DATE '"
operator|+
name|toFormattedString
argument_list|()
operator|+
literal|"'"
return|;
block|}
comment|/**    * Returns e.g. '1969-07-21'.    */
annotation|@
name|Override
specifier|public
name|String
name|toFormattedString
parameter_list|()
block|{
return|return
name|getDate
argument_list|()
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|RelDataType
name|createSqlType
parameter_list|(
name|RelDataTypeFactory
name|typeFactory
parameter_list|)
block|{
return|return
name|typeFactory
operator|.
name|createSqlType
argument_list|(
name|getTypeName
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
name|writer
operator|.
name|getDialect
argument_list|()
operator|.
name|unparseDateTimeLiteral
argument_list|(
name|writer
argument_list|,
name|this
argument_list|,
name|leftPrec
argument_list|,
name|rightPrec
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

