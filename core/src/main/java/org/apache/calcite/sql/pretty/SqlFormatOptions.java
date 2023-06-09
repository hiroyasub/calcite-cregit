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
operator|.
name|pretty
package|;
end_package

begin_comment
comment|/**  * Data structure to hold options for  * {@link SqlPrettyWriter#setFormatOptions(SqlFormatOptions)}.  */
end_comment

begin_class
specifier|public
class|class
name|SqlFormatOptions
block|{
specifier|private
name|boolean
name|alwaysUseParentheses
init|=
literal|false
decl_stmt|;
specifier|private
name|boolean
name|caseClausesOnNewLines
init|=
literal|false
decl_stmt|;
specifier|private
name|boolean
name|clauseStartsLine
init|=
literal|true
decl_stmt|;
specifier|private
name|boolean
name|keywordsLowercase
init|=
literal|false
decl_stmt|;
specifier|private
name|boolean
name|quoteAllIdentifiers
init|=
literal|true
decl_stmt|;
specifier|private
name|boolean
name|selectListItemsOnSeparateLines
init|=
literal|false
decl_stmt|;
specifier|private
name|boolean
name|whereListItemsOnSeparateLines
init|=
literal|false
decl_stmt|;
specifier|private
name|boolean
name|windowDeclarationStartsLine
init|=
literal|true
decl_stmt|;
specifier|private
name|boolean
name|windowListItemsOnSeparateLines
init|=
literal|true
decl_stmt|;
specifier|private
name|int
name|indentation
init|=
literal|4
decl_stmt|;
specifier|private
name|int
name|lineLength
init|=
literal|0
decl_stmt|;
comment|/**    * Constructs a set of default SQL format options.    */
specifier|public
name|SqlFormatOptions
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
comment|/**    * Constructs a complete set of SQL format options.    *    * @param alwaysUseParentheses           Always use parentheses    * @param caseClausesOnNewLines          Case clauses on new lines    * @param clauseStartsLine               Clause starts line    * @param keywordsLowercase              Keywords in lower case    * @param quoteAllIdentifiers            Quote all identifiers    * @param selectListItemsOnSeparateLines Select items on separate lines    * @param whereListItemsOnSeparateLines  Where items on separate lines    * @param windowDeclarationStartsLine    Window declaration starts line    * @param windowListItemsOnSeparateLines Window list items on separate lines    * @param indentation                    Indentation    * @param lineLength                     Line length    */
specifier|public
name|SqlFormatOptions
parameter_list|(
name|boolean
name|alwaysUseParentheses
parameter_list|,
name|boolean
name|caseClausesOnNewLines
parameter_list|,
name|boolean
name|clauseStartsLine
parameter_list|,
name|boolean
name|keywordsLowercase
parameter_list|,
name|boolean
name|quoteAllIdentifiers
parameter_list|,
name|boolean
name|selectListItemsOnSeparateLines
parameter_list|,
name|boolean
name|whereListItemsOnSeparateLines
parameter_list|,
name|boolean
name|windowDeclarationStartsLine
parameter_list|,
name|boolean
name|windowListItemsOnSeparateLines
parameter_list|,
name|int
name|indentation
parameter_list|,
name|int
name|lineLength
parameter_list|)
block|{
name|this
argument_list|()
expr_stmt|;
name|this
operator|.
name|alwaysUseParentheses
operator|=
name|alwaysUseParentheses
expr_stmt|;
name|this
operator|.
name|caseClausesOnNewLines
operator|=
name|caseClausesOnNewLines
expr_stmt|;
name|this
operator|.
name|clauseStartsLine
operator|=
name|clauseStartsLine
expr_stmt|;
name|this
operator|.
name|keywordsLowercase
operator|=
name|keywordsLowercase
expr_stmt|;
name|this
operator|.
name|quoteAllIdentifiers
operator|=
name|quoteAllIdentifiers
expr_stmt|;
name|this
operator|.
name|selectListItemsOnSeparateLines
operator|=
name|selectListItemsOnSeparateLines
expr_stmt|;
name|this
operator|.
name|whereListItemsOnSeparateLines
operator|=
name|whereListItemsOnSeparateLines
expr_stmt|;
name|this
operator|.
name|windowDeclarationStartsLine
operator|=
name|windowDeclarationStartsLine
expr_stmt|;
name|this
operator|.
name|windowListItemsOnSeparateLines
operator|=
name|windowListItemsOnSeparateLines
expr_stmt|;
name|this
operator|.
name|indentation
operator|=
name|indentation
expr_stmt|;
name|this
operator|.
name|lineLength
operator|=
name|lineLength
expr_stmt|;
block|}
specifier|public
name|boolean
name|isAlwaysUseParentheses
parameter_list|()
block|{
return|return
name|alwaysUseParentheses
return|;
block|}
specifier|public
name|void
name|setAlwaysUseParentheses
parameter_list|(
name|boolean
name|alwaysUseParentheses
parameter_list|)
block|{
name|this
operator|.
name|alwaysUseParentheses
operator|=
name|alwaysUseParentheses
expr_stmt|;
block|}
specifier|public
name|boolean
name|isCaseClausesOnNewLines
parameter_list|()
block|{
return|return
name|caseClausesOnNewLines
return|;
block|}
specifier|public
name|void
name|setCaseClausesOnNewLines
parameter_list|(
name|boolean
name|caseClausesOnNewLines
parameter_list|)
block|{
name|this
operator|.
name|caseClausesOnNewLines
operator|=
name|caseClausesOnNewLines
expr_stmt|;
block|}
specifier|public
name|boolean
name|isClauseStartsLine
parameter_list|()
block|{
return|return
name|clauseStartsLine
return|;
block|}
specifier|public
name|void
name|setClauseStartsLine
parameter_list|(
name|boolean
name|clauseStartsLine
parameter_list|)
block|{
name|this
operator|.
name|clauseStartsLine
operator|=
name|clauseStartsLine
expr_stmt|;
block|}
specifier|public
name|boolean
name|isKeywordsLowercase
parameter_list|()
block|{
return|return
name|keywordsLowercase
return|;
block|}
specifier|public
name|void
name|setKeywordsLowercase
parameter_list|(
name|boolean
name|keywordsLowercase
parameter_list|)
block|{
name|this
operator|.
name|keywordsLowercase
operator|=
name|keywordsLowercase
expr_stmt|;
block|}
specifier|public
name|boolean
name|isQuoteAllIdentifiers
parameter_list|()
block|{
return|return
name|quoteAllIdentifiers
return|;
block|}
specifier|public
name|void
name|setQuoteAllIdentifiers
parameter_list|(
name|boolean
name|quoteAllIdentifiers
parameter_list|)
block|{
name|this
operator|.
name|quoteAllIdentifiers
operator|=
name|quoteAllIdentifiers
expr_stmt|;
block|}
specifier|public
name|boolean
name|isSelectListItemsOnSeparateLines
parameter_list|()
block|{
return|return
name|selectListItemsOnSeparateLines
return|;
block|}
specifier|public
name|void
name|setSelectListItemsOnSeparateLines
parameter_list|(
name|boolean
name|selectListItemsOnSeparateLines
parameter_list|)
block|{
name|this
operator|.
name|selectListItemsOnSeparateLines
operator|=
name|selectListItemsOnSeparateLines
expr_stmt|;
block|}
specifier|public
name|boolean
name|isWhereListItemsOnSeparateLines
parameter_list|()
block|{
return|return
name|whereListItemsOnSeparateLines
return|;
block|}
specifier|public
name|void
name|setWhereListItemsOnSeparateLines
parameter_list|(
name|boolean
name|whereListItemsOnSeparateLines
parameter_list|)
block|{
name|this
operator|.
name|whereListItemsOnSeparateLines
operator|=
name|whereListItemsOnSeparateLines
expr_stmt|;
block|}
specifier|public
name|boolean
name|isWindowDeclarationStartsLine
parameter_list|()
block|{
return|return
name|windowDeclarationStartsLine
return|;
block|}
specifier|public
name|void
name|setWindowDeclarationStartsLine
parameter_list|(
name|boolean
name|windowDeclarationStartsLine
parameter_list|)
block|{
name|this
operator|.
name|windowDeclarationStartsLine
operator|=
name|windowDeclarationStartsLine
expr_stmt|;
block|}
specifier|public
name|boolean
name|isWindowListItemsOnSeparateLines
parameter_list|()
block|{
return|return
name|windowListItemsOnSeparateLines
return|;
block|}
specifier|public
name|void
name|setWindowListItemsOnSeparateLines
parameter_list|(
name|boolean
name|windowListItemsOnSeparateLines
parameter_list|)
block|{
name|this
operator|.
name|windowListItemsOnSeparateLines
operator|=
name|windowListItemsOnSeparateLines
expr_stmt|;
block|}
specifier|public
name|int
name|getLineLength
parameter_list|()
block|{
return|return
name|lineLength
return|;
block|}
specifier|public
name|void
name|setLineLength
parameter_list|(
name|int
name|lineLength
parameter_list|)
block|{
name|this
operator|.
name|lineLength
operator|=
name|lineLength
expr_stmt|;
block|}
specifier|public
name|int
name|getIndentation
parameter_list|()
block|{
return|return
name|indentation
return|;
block|}
specifier|public
name|void
name|setIndentation
parameter_list|(
name|int
name|indentation
parameter_list|)
block|{
name|this
operator|.
name|indentation
operator|=
name|indentation
expr_stmt|;
block|}
block|}
end_class

end_unit

