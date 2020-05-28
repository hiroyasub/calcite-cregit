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
name|util
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
name|SqlCall
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
name|SqlCharStringLiteral
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
name|SqlKind
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
name|SqlSpecialOperator
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
name|SqlWriter
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
name|fun
operator|.
name|SqlTrimFunction
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
import|import static
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|sql
operator|.
name|fun
operator|.
name|SqlLibraryOperators
operator|.
name|REGEXP_REPLACE
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
comment|/**  * Utilities used by multiple dialect for RelToSql conversion.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|RelToSqlConverterUtil
block|{
comment|/**    * For usage of TRIM, LTRIM and RTRIM in Hive, see    *<a href="https://cwiki.apache.org/confluence/display/Hive/LanguageManual+UDF">Hive UDF usage</a>.    */
specifier|public
specifier|static
name|void
name|unparseHiveTrim
parameter_list|(
name|SqlWriter
name|writer
parameter_list|,
name|SqlCall
name|call
parameter_list|,
name|int
name|leftPrec
parameter_list|,
name|int
name|rightPrec
parameter_list|)
block|{
specifier|final
name|SqlLiteral
name|valueToTrim
init|=
name|call
operator|.
name|operand
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|String
name|value
init|=
name|requireNonNull
argument_list|(
name|valueToTrim
operator|.
name|toValue
argument_list|()
argument_list|,
parameter_list|()
lambda|->
literal|"call.operand(1).toValue() for call "
operator|+
name|call
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|.
name|matches
argument_list|(
literal|"\\s+"
argument_list|)
condition|)
block|{
name|unparseTrimWithSpace
argument_list|(
name|writer
argument_list|,
name|call
argument_list|,
name|leftPrec
argument_list|,
name|rightPrec
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// SELECT TRIM(both 'A' from "ABC") -> SELECT REGEXP_REPLACE("ABC", '^(A)*', '')
specifier|final
name|SqlLiteral
name|trimFlag
init|=
name|call
operator|.
name|operand
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|SqlCharStringLiteral
name|regexNode
init|=
name|createRegexPatternLiteral
argument_list|(
name|call
operator|.
name|operand
argument_list|(
literal|1
argument_list|)
argument_list|,
name|trimFlag
argument_list|)
decl_stmt|;
specifier|final
name|SqlCharStringLiteral
name|blankLiteral
init|=
name|SqlLiteral
operator|.
name|createCharString
argument_list|(
literal|""
argument_list|,
name|call
operator|.
name|getParserPosition
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|SqlNode
index|[]
name|trimOperands
init|=
operator|new
name|SqlNode
index|[]
block|{
name|call
operator|.
name|operand
argument_list|(
literal|2
argument_list|)
block|,
name|regexNode
block|,
name|blankLiteral
block|}
decl_stmt|;
specifier|final
name|SqlCall
name|regexReplaceCall
init|=
name|REGEXP_REPLACE
operator|.
name|createCall
argument_list|(
name|SqlParserPos
operator|.
name|ZERO
argument_list|,
name|trimOperands
argument_list|)
decl_stmt|;
name|regexReplaceCall
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
block|}
block|}
comment|/**    * Unparses TRIM function with value as space.    *    *<p>For example :    *    *<blockquote><pre>    * SELECT TRIM(both ' ' from "ABC")&rarr; SELECT TRIM(ABC)    *</pre></blockquote>    *    * @param writer writer    * @param call the call    */
specifier|private
specifier|static
name|void
name|unparseTrimWithSpace
parameter_list|(
name|SqlWriter
name|writer
parameter_list|,
name|SqlCall
name|call
parameter_list|,
name|int
name|leftPrec
parameter_list|,
name|int
name|rightPrec
parameter_list|)
block|{
specifier|final
name|String
name|operatorName
decl_stmt|;
specifier|final
name|SqlLiteral
name|trimFlag
init|=
name|call
operator|.
name|operand
argument_list|(
literal|0
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|trimFlag
operator|.
name|getValueAs
argument_list|(
name|SqlTrimFunction
operator|.
name|Flag
operator|.
name|class
argument_list|)
condition|)
block|{
case|case
name|LEADING
case|:
name|operatorName
operator|=
literal|"LTRIM"
expr_stmt|;
break|break;
case|case
name|TRAILING
case|:
name|operatorName
operator|=
literal|"RTRIM"
expr_stmt|;
break|break;
default|default:
name|operatorName
operator|=
name|call
operator|.
name|getOperator
argument_list|()
operator|.
name|getName
argument_list|()
expr_stmt|;
break|break;
block|}
specifier|final
name|SqlWriter
operator|.
name|Frame
name|trimFrame
init|=
name|writer
operator|.
name|startFunCall
argument_list|(
name|operatorName
argument_list|)
decl_stmt|;
name|call
operator|.
name|operand
argument_list|(
literal|2
argument_list|)
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
name|endFunCall
argument_list|(
name|trimFrame
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates regex pattern based on the TRIM flag.    *    * @param call     SqlCall contains the values that need to be trimmed    * @param trimFlag the trimFlag, either BOTH, LEADING or TRAILING    * @return the regex pattern of the character to be trimmed    */
specifier|public
specifier|static
name|SqlCharStringLiteral
name|createRegexPatternLiteral
parameter_list|(
name|SqlNode
name|call
parameter_list|,
name|SqlLiteral
name|trimFlag
parameter_list|)
block|{
specifier|final
name|String
name|regexPattern
init|=
name|requireNonNull
argument_list|(
operator|(
operator|(
name|SqlCharStringLiteral
operator|)
name|call
operator|)
operator|.
name|toValue
argument_list|()
argument_list|,
parameter_list|()
lambda|->
literal|"null value for SqlNode "
operator|+
name|call
argument_list|)
decl_stmt|;
name|String
name|escaped
init|=
name|escapeSpecialChar
argument_list|(
name|regexPattern
argument_list|)
decl_stmt|;
specifier|final
name|StringBuilder
name|builder
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
switch|switch
condition|(
name|trimFlag
operator|.
name|getValueAs
argument_list|(
name|SqlTrimFunction
operator|.
name|Flag
operator|.
name|class
argument_list|)
condition|)
block|{
case|case
name|LEADING
case|:
name|builder
operator|.
name|append
argument_list|(
literal|"^("
argument_list|)
operator|.
name|append
argument_list|(
name|escaped
argument_list|)
operator|.
name|append
argument_list|(
literal|")*"
argument_list|)
expr_stmt|;
break|break;
case|case
name|TRAILING
case|:
name|builder
operator|.
name|append
argument_list|(
literal|"("
argument_list|)
operator|.
name|append
argument_list|(
name|escaped
argument_list|)
operator|.
name|append
argument_list|(
literal|")*$"
argument_list|)
expr_stmt|;
break|break;
default|default:
name|builder
operator|.
name|append
argument_list|(
literal|"^("
argument_list|)
operator|.
name|append
argument_list|(
name|escaped
argument_list|)
operator|.
name|append
argument_list|(
literal|")*|("
argument_list|)
operator|.
name|append
argument_list|(
name|escaped
argument_list|)
operator|.
name|append
argument_list|(
literal|")*$"
argument_list|)
expr_stmt|;
break|break;
block|}
return|return
name|SqlLiteral
operator|.
name|createCharString
argument_list|(
name|builder
operator|.
name|toString
argument_list|()
argument_list|,
name|call
operator|.
name|getParserPosition
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Escapes the special character.    *    * @param inputString the string    * @return escape character if any special character is present in the string    */
specifier|private
specifier|static
name|String
name|escapeSpecialChar
parameter_list|(
name|String
name|inputString
parameter_list|)
block|{
specifier|final
name|String
index|[]
name|specialCharacters
init|=
block|{
literal|"\\"
block|,
literal|"^"
block|,
literal|"$"
block|,
literal|"{"
block|,
literal|"}"
block|,
literal|"["
block|,
literal|"]"
block|,
literal|"("
block|,
literal|")"
block|,
literal|"."
block|,
literal|"*"
block|,
literal|"+"
block|,
literal|"?"
block|,
literal|"|"
block|,
literal|"<"
block|,
literal|">"
block|,
literal|"-"
block|,
literal|"&"
block|,
literal|"%"
block|,
literal|"@"
block|}
decl_stmt|;
for|for
control|(
name|String
name|specialCharacter
range|:
name|specialCharacters
control|)
block|{
if|if
condition|(
name|inputString
operator|.
name|contains
argument_list|(
name|specialCharacter
argument_list|)
condition|)
block|{
name|inputString
operator|=
name|inputString
operator|.
name|replace
argument_list|(
name|specialCharacter
argument_list|,
literal|"\\"
operator|+
name|specialCharacter
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|inputString
return|;
block|}
comment|/** Returns a {@link SqlSpecialOperator} with given operator name, mainly used for    * unparse override. */
specifier|public
specifier|static
name|SqlSpecialOperator
name|specialOperatorByName
parameter_list|(
name|String
name|opName
parameter_list|)
block|{
return|return
operator|new
name|SqlSpecialOperator
argument_list|(
name|opName
argument_list|,
name|SqlKind
operator|.
name|OTHER_FUNCTION
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|unparse
parameter_list|(
name|SqlWriter
name|writer
parameter_list|,
name|SqlCall
name|call
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
name|getName
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
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
name|SqlNode
name|operand
range|:
name|call
operator|.
name|getOperandList
argument_list|()
control|)
block|{
name|writer
operator|.
name|sep
argument_list|(
literal|","
argument_list|)
expr_stmt|;
name|operand
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
block|}
name|writer
operator|.
name|endList
argument_list|(
name|frame
argument_list|)
expr_stmt|;
block|}
block|}
return|;
block|}
block|}
end_class

end_unit

