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
name|TimeString
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
name|base
operator|.
name|Preconditions
import|;
end_import

begin_comment
comment|/**  * A SQL literal representing a TIME value, for example<code>TIME  * '14:33:44.567'</code>.  *  *<p>Create values using {@link SqlLiteral#createTime}.  */
end_comment

begin_class
specifier|public
class|class
name|SqlTimeLiteral
extends|extends
name|SqlAbstractDateTimeLiteral
block|{
comment|//~ Constructors -----------------------------------------------------------
name|SqlTimeLiteral
parameter_list|(
name|TimeString
name|t
parameter_list|,
name|int
name|precision
parameter_list|,
name|boolean
name|hasTimeZone
parameter_list|,
name|SqlParserPos
name|pos
parameter_list|)
block|{
name|super
argument_list|(
name|t
argument_list|,
name|hasTimeZone
argument_list|,
name|SqlTypeName
operator|.
name|TIME
argument_list|,
name|precision
argument_list|,
name|pos
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|this
operator|.
name|precision
operator|>=
literal|0
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
comment|/** Converts this literal to a {@link TimeString}. */
specifier|protected
name|TimeString
name|getTime
parameter_list|()
block|{
return|return
operator|(
name|TimeString
operator|)
name|value
return|;
block|}
annotation|@
name|Override
specifier|public
name|SqlTimeLiteral
name|clone
parameter_list|(
name|SqlParserPos
name|pos
parameter_list|)
block|{
return|return
operator|new
name|SqlTimeLiteral
argument_list|(
operator|(
name|TimeString
operator|)
name|value
argument_list|,
name|precision
argument_list|,
name|hasTimeZone
argument_list|,
name|pos
argument_list|)
return|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"TIME '"
operator|+
name|toFormattedString
argument_list|()
operator|+
literal|"'"
return|;
block|}
comment|/**    * Returns e.g. '03:05:67.456'.    */
specifier|public
name|String
name|toFormattedString
parameter_list|()
block|{
return|return
name|getTime
argument_list|()
operator|.
name|toString
argument_list|(
name|precision
argument_list|)
return|;
block|}
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

