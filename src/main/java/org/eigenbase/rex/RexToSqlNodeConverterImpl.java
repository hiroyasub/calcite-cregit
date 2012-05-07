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
name|rex
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
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

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|sql
operator|.
name|parser
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
name|type
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
name|util
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Standard implementation of {@link RexToSqlNodeConverter}.  *  * @author Sunny Choi  * @version $Id$  */
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
comment|// implement RexToSqlNodeConverter
specifier|public
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
specifier|public
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
comment|// implement RexToSqlNodeConverter
specifier|public
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
name|literal
operator|.
name|getValue
argument_list|()
operator|.
name|toString
argument_list|()
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
name|literal
operator|.
name|getValue
argument_list|()
operator|.
name|toString
argument_list|()
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
operator|(
name|Calendar
operator|)
name|literal
operator|.
name|getValue
argument_list|()
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
operator|(
name|Calendar
operator|)
name|literal
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
operator|(
operator|(
name|NlsString
operator|)
operator|(
name|literal
operator|.
name|getValue
argument_list|()
operator|)
operator|)
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
name|literal
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
return|return
literal|null
return|;
block|}
comment|// implement RexToSqlNodeConverter
specifier|public
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

begin_comment
comment|// End RexToSqlNodeConverterImpl.java
end_comment

end_unit

