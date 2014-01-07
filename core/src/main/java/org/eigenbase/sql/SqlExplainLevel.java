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
package|;
end_package

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
name|SqlParserPos
import|;
end_import

begin_comment
comment|/**  * SqlExplainLevel defines detail levels for EXPLAIN PLAN.  */
end_comment

begin_enum
specifier|public
enum|enum
name|SqlExplainLevel
implements|implements
name|SqlLiteral
operator|.
name|SqlSymbol
block|{
comment|/**    * Suppress all attributes.    */
name|NO_ATTRIBUTES
block|,
comment|/**    * Display only attributes which contribute to the plan output.    */
name|EXPPLAN_ATTRIBUTES
block|,
comment|/**    * Display only attributes which contribute to an expression's digest.    */
name|DIGEST_ATTRIBUTES
block|,
comment|/**    * Display all attributes, including cost.    */
name|ALL_ATTRIBUTES
block|;
comment|/**    * Creates a parse-tree node representing an occurrence of this symbol at    * a particular position in the parsed text.    */
specifier|public
name|SqlLiteral
name|symbol
parameter_list|(
name|SqlParserPos
name|pos
parameter_list|)
block|{
return|return
name|SqlLiteral
operator|.
name|createSymbol
argument_list|(
name|this
argument_list|,
name|pos
argument_list|)
return|;
block|}
block|}
end_enum

begin_comment
comment|// End SqlExplainLevel.java
end_comment

end_unit

