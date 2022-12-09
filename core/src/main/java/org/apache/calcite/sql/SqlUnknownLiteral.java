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
name|parser
operator|.
name|SqlParserUtil
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
name|NlsString
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
import|import static
name|java
operator|.
name|util
operator|.
name|Objects
operator|.
name|requireNonNull
import|;
end_import

begin_comment
comment|/**  * Literal whose type is not yet known.  */
end_comment

begin_class
specifier|public
class|class
name|SqlUnknownLiteral
extends|extends
name|SqlLiteral
block|{
specifier|public
specifier|final
name|String
name|tag
decl_stmt|;
name|SqlUnknownLiteral
parameter_list|(
name|String
name|tag
parameter_list|,
name|String
name|value
parameter_list|,
name|SqlParserPos
name|pos
parameter_list|)
block|{
name|super
argument_list|(
name|requireNonNull
argument_list|(
name|value
argument_list|,
literal|"value"
argument_list|)
argument_list|,
name|SqlTypeName
operator|.
name|UNKNOWN
argument_list|,
name|pos
argument_list|)
expr_stmt|;
name|this
operator|.
name|tag
operator|=
name|requireNonNull
argument_list|(
name|tag
argument_list|,
literal|"tag"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getValue
parameter_list|()
block|{
return|return
operator|(
name|String
operator|)
name|requireNonNull
argument_list|(
name|super
operator|.
name|getValue
argument_list|()
argument_list|,
literal|"value"
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
specifier|final
name|NlsString
name|nlsString
init|=
operator|new
name|NlsString
argument_list|(
name|getValue
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|writer
operator|.
name|keyword
argument_list|(
name|tag
argument_list|)
expr_stmt|;
name|writer
operator|.
name|literal
argument_list|(
name|nlsString
operator|.
name|asSql
argument_list|(
literal|true
argument_list|,
literal|true
argument_list|,
name|writer
operator|.
name|getDialect
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Converts this unknown literal to a literal of known type. */
specifier|public
name|SqlLiteral
name|resolve
parameter_list|(
name|SqlTypeName
name|typeName
parameter_list|)
block|{
switch|switch
condition|(
name|typeName
condition|)
block|{
case|case
name|DATE
case|:
return|return
name|SqlParserUtil
operator|.
name|parseDateLiteral
argument_list|(
name|getValue
argument_list|()
argument_list|,
name|pos
argument_list|)
return|;
case|case
name|TIME
case|:
return|return
name|SqlParserUtil
operator|.
name|parseTimeLiteral
argument_list|(
name|getValue
argument_list|()
argument_list|,
name|pos
argument_list|)
return|;
case|case
name|TIMESTAMP
case|:
return|return
name|SqlParserUtil
operator|.
name|parseTimestampLiteral
argument_list|(
name|getValue
argument_list|()
argument_list|,
name|pos
argument_list|)
return|;
case|case
name|TIMESTAMP_WITH_LOCAL_TIME_ZONE
case|:
return|return
name|SqlParserUtil
operator|.
name|parseTimestampWithLocalTimeZoneLiteral
argument_list|(
name|getValue
argument_list|()
argument_list|,
name|pos
argument_list|)
return|;
default|default:
throw|throw
name|Util
operator|.
name|unexpected
argument_list|(
name|typeName
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

