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
name|rex
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
name|SqlLiteral
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
name|SqlNode
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
name|SqlTypeFamily
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
name|TimeString
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
name|TimestampString
import|;
end_import

begin_import
import|import
name|org
operator|.
name|checkerframework
operator|.
name|checker
operator|.
name|nullness
operator|.
name|qual
operator|.
name|Nullable
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
comment|/**  * Standard implementation of {@link RexToSqlNodeConverter}.  */
end_comment

begin_class
specifier|public
class|class
name|RexToSqlNodeConverterImpl
implements|implements
name|RexToSqlNodeConverter
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|private
specifier|final
name|RexSqlConvertletTable
name|convertletTable
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
specifier|public
name|RexToSqlNodeConverterImpl
parameter_list|(
name|RexSqlConvertletTable
name|convertletTable
parameter_list|)
block|{
name|this
operator|.
name|convertletTable
operator|=
name|convertletTable
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
annotation|@
name|Override
specifier|public
annotation|@
name|Nullable
name|SqlNode
name|convertNode
parameter_list|(
name|RexNode
name|node
parameter_list|)
block|{
if|if
condition|(
name|node
operator|instanceof
name|RexLiteral
condition|)
block|{
return|return
name|convertLiteral
argument_list|(
operator|(
name|RexLiteral
operator|)
name|node
argument_list|)
return|;
block|}
if|else if
condition|(
name|node
operator|instanceof
name|RexInputRef
condition|)
block|{
return|return
name|convertInputRef
argument_list|(
operator|(
name|RexInputRef
operator|)
name|node
argument_list|)
return|;
block|}
if|else if
condition|(
name|node
operator|instanceof
name|RexCall
condition|)
block|{
return|return
name|convertCall
argument_list|(
operator|(
name|RexCall
operator|)
name|node
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
comment|// implement RexToSqlNodeConverter
annotation|@
name|Override
specifier|public
annotation|@
name|Nullable
name|SqlNode
name|convertCall
parameter_list|(
name|RexCall
name|call
parameter_list|)
block|{
specifier|final
name|RexSqlConvertlet
name|convertlet
init|=
name|convertletTable
operator|.
name|get
argument_list|(
name|call
argument_list|)
decl_stmt|;
if|if
condition|(
name|convertlet
operator|!=
literal|null
condition|)
block|{
return|return
name|convertlet
operator|.
name|convertCall
argument_list|(
name|this
argument_list|,
name|call
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
annotation|@
name|Nullable
name|SqlNode
name|convertLiteral
parameter_list|(
name|RexLiteral
name|literal
parameter_list|)
block|{
comment|// Numeric
if|if
condition|(
name|SqlTypeFamily
operator|.
name|EXACT_NUMERIC
operator|.
name|getTypeNames
argument_list|()
operator|.
name|contains
argument_list|(
name|literal
operator|.
name|getTypeName
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|SqlLiteral
operator|.
name|createExactNumeric
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|literal
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|,
name|SqlParserPos
operator|.
name|ZERO
argument_list|)
return|;
block|}
if|if
condition|(
name|SqlTypeFamily
operator|.
name|APPROXIMATE_NUMERIC
operator|.
name|getTypeNames
argument_list|()
operator|.
name|contains
argument_list|(
name|literal
operator|.
name|getTypeName
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|SqlLiteral
operator|.
name|createApproxNumeric
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|literal
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|,
name|SqlParserPos
operator|.
name|ZERO
argument_list|)
return|;
block|}
comment|// Timestamp
if|if
condition|(
name|SqlTypeFamily
operator|.
name|TIMESTAMP
operator|.
name|getTypeNames
argument_list|()
operator|.
name|contains
argument_list|(
name|literal
operator|.
name|getTypeName
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|SqlLiteral
operator|.
name|createTimestamp
argument_list|(
name|requireNonNull
argument_list|(
name|literal
operator|.
name|getValueAs
argument_list|(
name|TimestampString
operator|.
name|class
argument_list|)
argument_list|,
literal|"literal.getValueAs(TimestampString.class)"
argument_list|)
argument_list|,
literal|0
argument_list|,
name|SqlParserPos
operator|.
name|ZERO
argument_list|)
return|;
block|}
comment|// Date
if|if
condition|(
name|SqlTypeFamily
operator|.
name|DATE
operator|.
name|getTypeNames
argument_list|()
operator|.
name|contains
argument_list|(
name|literal
operator|.
name|getTypeName
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|SqlLiteral
operator|.
name|createDate
argument_list|(
name|requireNonNull
argument_list|(
name|literal
operator|.
name|getValueAs
argument_list|(
name|DateString
operator|.
name|class
argument_list|)
argument_list|,
literal|"literal.getValueAs(DateString.class)"
argument_list|)
argument_list|,
name|SqlParserPos
operator|.
name|ZERO
argument_list|)
return|;
block|}
comment|// Time
if|if
condition|(
name|SqlTypeFamily
operator|.
name|TIME
operator|.
name|getTypeNames
argument_list|()
operator|.
name|contains
argument_list|(
name|literal
operator|.
name|getTypeName
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|SqlLiteral
operator|.
name|createTime
argument_list|(
name|requireNonNull
argument_list|(
name|literal
operator|.
name|getValueAs
argument_list|(
name|TimeString
operator|.
name|class
argument_list|)
argument_list|,
literal|"literal.getValueAs(TimeString.class)"
argument_list|)
argument_list|,
literal|0
argument_list|,
name|SqlParserPos
operator|.
name|ZERO
argument_list|)
return|;
block|}
comment|// String
if|if
condition|(
name|SqlTypeFamily
operator|.
name|CHARACTER
operator|.
name|getTypeNames
argument_list|()
operator|.
name|contains
argument_list|(
name|literal
operator|.
name|getTypeName
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|SqlLiteral
operator|.
name|createCharString
argument_list|(
name|requireNonNull
argument_list|(
operator|(
name|NlsString
operator|)
name|literal
operator|.
name|getValue
argument_list|()
argument_list|,
literal|"literal.getValue()"
argument_list|)
operator|.
name|getValue
argument_list|()
argument_list|,
name|SqlParserPos
operator|.
name|ZERO
argument_list|)
return|;
block|}
comment|// Boolean
if|if
condition|(
name|SqlTypeFamily
operator|.
name|BOOLEAN
operator|.
name|getTypeNames
argument_list|()
operator|.
name|contains
argument_list|(
name|literal
operator|.
name|getTypeName
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|SqlLiteral
operator|.
name|createBoolean
argument_list|(
operator|(
name|Boolean
operator|)
name|requireNonNull
argument_list|(
name|literal
operator|.
name|getValue
argument_list|()
argument_list|,
literal|"literal.getValue()"
argument_list|)
argument_list|,
name|SqlParserPos
operator|.
name|ZERO
argument_list|)
return|;
block|}
comment|// Null
if|if
condition|(
name|SqlTypeFamily
operator|.
name|NULL
operator|==
name|literal
operator|.
name|getTypeName
argument_list|()
operator|.
name|getFamily
argument_list|()
condition|)
block|{
return|return
name|SqlLiteral
operator|.
name|createNull
argument_list|(
name|SqlParserPos
operator|.
name|ZERO
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
annotation|@
name|Nullable
name|SqlNode
name|convertInputRef
parameter_list|(
name|RexInputRef
name|ref
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

