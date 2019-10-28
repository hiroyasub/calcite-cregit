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
name|pretty
operator|.
name|SqlPrettyWriter
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
name|ImmutableBeans
import|;
end_import

begin_comment
comment|/** Configuration for {@link SqlWriter} and {@link SqlPrettyWriter}. */
end_comment

begin_interface
specifier|public
interface|interface
name|SqlWriterConfig
block|{
comment|/** Returns the dialect. */
annotation|@
name|ImmutableBeans
operator|.
name|Property
name|SqlDialect
name|dialect
parameter_list|()
function_decl|;
comment|/** Sets {@link #dialect()}. */
name|SqlWriterConfig
name|withDialect
parameter_list|(
name|SqlDialect
name|dialect
parameter_list|)
function_decl|;
comment|/** Returns whether to print keywords (SELECT, AS, etc.) in lower-case.    * Default is false: keywords are printed in upper-case. */
annotation|@
name|ImmutableBeans
operator|.
name|Property
annotation|@
name|ImmutableBeans
operator|.
name|BooleanDefault
argument_list|(
literal|false
argument_list|)
name|boolean
name|keywordsLowerCase
parameter_list|()
function_decl|;
comment|/** Sets {@link #keywordsLowerCase}. */
name|SqlWriterConfig
name|withKeywordsLowerCase
parameter_list|(
name|boolean
name|keywordsLowerCase
parameter_list|)
function_decl|;
comment|/** Returns whether to quote all identifiers, even those which would be    * correct according to the rules of the {@link SqlDialect} if quotation    * marks were omitted. Default is true. */
annotation|@
name|ImmutableBeans
operator|.
name|Property
annotation|@
name|ImmutableBeans
operator|.
name|BooleanDefault
argument_list|(
literal|true
argument_list|)
name|boolean
name|quoteAllIdentifiers
parameter_list|()
function_decl|;
comment|/** Sets {@link #quoteAllIdentifiers}. */
name|SqlWriterConfig
name|withQuoteAllIdentifiers
parameter_list|(
name|boolean
name|quoteAllIdentifiers
parameter_list|)
function_decl|;
comment|/** Returns the number of spaces indentation. Default is 4. */
annotation|@
name|ImmutableBeans
operator|.
name|Property
annotation|@
name|ImmutableBeans
operator|.
name|IntDefault
argument_list|(
literal|4
argument_list|)
name|int
name|indentation
parameter_list|()
function_decl|;
comment|/** Sets {@link #indentation}. */
name|SqlWriterConfig
name|withIndentation
parameter_list|(
name|int
name|indentation
parameter_list|)
function_decl|;
comment|/** Returns whether a clause (FROM, WHERE, GROUP BY, HAVING, WINDOW,    * ORDER BY) starts a new line. Default is true. SELECT is always at the    * start of a line. */
annotation|@
name|ImmutableBeans
operator|.
name|Property
annotation|@
name|ImmutableBeans
operator|.
name|BooleanDefault
argument_list|(
literal|true
argument_list|)
name|boolean
name|clauseStartsLine
parameter_list|()
function_decl|;
comment|/** Sets {@link #clauseStartsLine}. */
name|SqlWriterConfig
name|withClauseStartsLine
parameter_list|(
name|boolean
name|clauseStartsLine
parameter_list|)
function_decl|;
comment|/** Returns whether a clause (FROM, WHERE, GROUP BY, HAVING, WINDOW,    * ORDER BY) is followed by a new line. Default is false. */
annotation|@
name|ImmutableBeans
operator|.
name|Property
annotation|@
name|ImmutableBeans
operator|.
name|BooleanDefault
argument_list|(
literal|false
argument_list|)
name|boolean
name|clauseEndsLine
parameter_list|()
function_decl|;
comment|/** Sets {@link #clauseEndsLine()}. */
name|SqlWriterConfig
name|withClauseEndsLine
parameter_list|(
name|boolean
name|clauseEndsLine
parameter_list|)
function_decl|;
comment|/** Returns whether each item in a SELECT list, GROUP BY list, or ORDER BY    * list is on its own line.    *    *<p>Default is false;    * this property is superseded by {@link #selectFolding()},    * {@link #groupByFolding()}, {@link #orderByFolding()}. */
annotation|@
name|ImmutableBeans
operator|.
name|Property
annotation|@
name|ImmutableBeans
operator|.
name|BooleanDefault
argument_list|(
literal|false
argument_list|)
name|boolean
name|selectListItemsOnSeparateLines
parameter_list|()
function_decl|;
comment|/** Sets {@link #selectListItemsOnSeparateLines}. */
name|SqlWriterConfig
name|withSelectListItemsOnSeparateLines
parameter_list|(
name|boolean
name|selectListItemsOnSeparateLines
parameter_list|)
function_decl|;
comment|/** Returns the line-folding policy for lists in the SELECT, GROUP BY and    * ORDER clauses, for items in the SET clause of UPDATE, and for items in    * VALUES.    *    * @see #foldLength()    *    *<p>If not set, the values of    * {@link #selectListItemsOnSeparateLines()},    * {@link #valuesListNewline()},    * {@link #updateSetListNewline()},    * {@link #windowDeclListNewline()} are used. */
annotation|@
name|ImmutableBeans
operator|.
name|Property
name|LineFolding
name|lineFolding
parameter_list|()
function_decl|;
comment|/** Sets {@link #lineFolding()}. */
name|SqlWriterConfig
name|withLineFolding
parameter_list|(
name|LineFolding
name|lineFolding
parameter_list|)
function_decl|;
comment|/** Returns the line-folding policy for the SELECT clause.    * If not set, the value of {@link #lineFolding()} is used. */
annotation|@
name|ImmutableBeans
operator|.
name|Property
name|LineFolding
name|selectFolding
parameter_list|()
function_decl|;
comment|/** Sets {@link #selectFolding()}. */
name|SqlWriterConfig
name|withSelectFolding
parameter_list|(
name|LineFolding
name|lineFolding
parameter_list|)
function_decl|;
comment|/** Returns the line-folding policy for the FROM clause (and JOIN).    * If not set, the value of {@link #lineFolding()} is used. */
annotation|@
name|ImmutableBeans
operator|.
name|Property
annotation|@
name|ImmutableBeans
operator|.
name|EnumDefault
argument_list|(
literal|"TALL"
argument_list|)
name|LineFolding
name|fromFolding
parameter_list|()
function_decl|;
comment|/** Sets {@link #fromFolding()}. */
name|SqlWriterConfig
name|withFromFolding
parameter_list|(
name|LineFolding
name|lineFolding
parameter_list|)
function_decl|;
comment|/** Returns the line-folding policy for the WHERE clause.    * If not set, the value of {@link #lineFolding()} is used. */
annotation|@
name|ImmutableBeans
operator|.
name|Property
name|LineFolding
name|whereFolding
parameter_list|()
function_decl|;
comment|/** Sets {@link #whereFolding()}. */
name|SqlWriterConfig
name|withWhereFolding
parameter_list|(
name|LineFolding
name|lineFolding
parameter_list|)
function_decl|;
comment|/** Returns the line-folding policy for the GROUP BY clause.    * If not set, the value of {@link #lineFolding()} is used. */
annotation|@
name|ImmutableBeans
operator|.
name|Property
name|LineFolding
name|groupByFolding
parameter_list|()
function_decl|;
comment|/** Sets {@link #groupByFolding()}. */
name|SqlWriterConfig
name|withGroupByFolding
parameter_list|(
name|LineFolding
name|lineFolding
parameter_list|)
function_decl|;
comment|/** Returns the line-folding policy for the HAVING clause.    * If not set, the value of {@link #lineFolding()} is used. */
annotation|@
name|ImmutableBeans
operator|.
name|Property
name|LineFolding
name|havingFolding
parameter_list|()
function_decl|;
comment|/** Sets {@link #havingFolding()}. */
name|SqlWriterConfig
name|withHavingFolding
parameter_list|(
name|LineFolding
name|lineFolding
parameter_list|)
function_decl|;
comment|/** Returns the line-folding policy for the WINDOW clause.    * If not set, the value of {@link #lineFolding()} is used. */
annotation|@
name|ImmutableBeans
operator|.
name|Property
name|LineFolding
name|windowFolding
parameter_list|()
function_decl|;
comment|/** Sets {@link #windowFolding()}. */
name|SqlWriterConfig
name|withWindowFolding
parameter_list|(
name|LineFolding
name|lineFolding
parameter_list|)
function_decl|;
comment|/** Returns the line-folding policy for the MATCH_RECOGNIZE clause.    * If not set, the value of {@link #lineFolding()} is used. */
annotation|@
name|ImmutableBeans
operator|.
name|Property
name|LineFolding
name|matchFolding
parameter_list|()
function_decl|;
comment|/** Sets {@link #matchFolding()}. */
name|SqlWriterConfig
name|withMatchFolding
parameter_list|(
name|LineFolding
name|lineFolding
parameter_list|)
function_decl|;
comment|/** Returns the line-folding policy for the ORDER BY clause.    * If not set, the value of {@link #lineFolding()} is used. */
annotation|@
name|ImmutableBeans
operator|.
name|Property
name|LineFolding
name|orderByFolding
parameter_list|()
function_decl|;
comment|/** Sets {@link #orderByFolding()}. */
name|SqlWriterConfig
name|withOrderByFolding
parameter_list|(
name|LineFolding
name|lineFolding
parameter_list|)
function_decl|;
comment|/** Returns the line-folding policy for the OVER clause or a window    * declaration. If not set, the value of {@link #lineFolding()} is used. */
annotation|@
name|ImmutableBeans
operator|.
name|Property
name|LineFolding
name|overFolding
parameter_list|()
function_decl|;
comment|/** Sets {@link #overFolding()}. */
name|SqlWriterConfig
name|withOverFolding
parameter_list|(
name|LineFolding
name|lineFolding
parameter_list|)
function_decl|;
comment|/** Returns the line-folding policy for the VALUES expression.    * If not set, the value of {@link #lineFolding()} is used. */
annotation|@
name|ImmutableBeans
operator|.
name|Property
name|LineFolding
name|valuesFolding
parameter_list|()
function_decl|;
comment|/** Sets {@link #valuesFolding()}. */
name|SqlWriterConfig
name|withValuesFolding
parameter_list|(
name|LineFolding
name|lineFolding
parameter_list|)
function_decl|;
comment|/** Returns the line-folding policy for the SET clause of an UPDATE statement.    * If not set, the value of {@link #lineFolding()} is used. */
annotation|@
name|ImmutableBeans
operator|.
name|Property
name|LineFolding
name|updateSetFolding
parameter_list|()
function_decl|;
comment|/** Sets {@link #updateSetFolding()}. */
name|SqlWriterConfig
name|withUpdateSetFolding
parameter_list|(
name|LineFolding
name|lineFolding
parameter_list|)
function_decl|;
comment|/**    * Returns whether to use a fix for SELECT list indentations.    *    *<ul>    *<li>If set to "false":    *    *<blockquote><pre>    * SELECT    *     A as A,    *         B as B,    *         C as C,    *     D    *</pre></blockquote>    *    *<li>If set to "true" (the default):    *    *<blockquote><pre>    * SELECT    *     A as A,    *     B as B,    *     C as C,    *     D    *</pre></blockquote>    *</ul>    */
annotation|@
name|ImmutableBeans
operator|.
name|Property
annotation|@
name|ImmutableBeans
operator|.
name|BooleanDefault
argument_list|(
literal|true
argument_list|)
name|boolean
name|selectListExtraIndentFlag
parameter_list|()
function_decl|;
comment|/** Sets {@link #selectListExtraIndentFlag}. */
name|SqlWriterConfig
name|withSelectListExtraIndentFlag
parameter_list|(
name|boolean
name|selectListExtraIndentFlag
parameter_list|)
function_decl|;
comment|/** Returns whether each declaration in a WINDOW clause should be on its own    * line.    *    *<p>Default is true;    * this property is superseded by {@link #windowFolding()}. */
annotation|@
name|ImmutableBeans
operator|.
name|Property
annotation|@
name|ImmutableBeans
operator|.
name|BooleanDefault
argument_list|(
literal|true
argument_list|)
name|boolean
name|windowDeclListNewline
parameter_list|()
function_decl|;
comment|/** Sets {@link #windowDeclListNewline}. */
name|SqlWriterConfig
name|withWindowDeclListNewline
parameter_list|(
name|boolean
name|windowDeclListNewline
parameter_list|)
function_decl|;
comment|/** Returns whether each row in a VALUES clause should be on its own    * line.    *    *<p>Default is true;    * this property is superseded by {@link #valuesFolding()}. */
annotation|@
name|ImmutableBeans
operator|.
name|Property
annotation|@
name|ImmutableBeans
operator|.
name|BooleanDefault
argument_list|(
literal|true
argument_list|)
name|boolean
name|valuesListNewline
parameter_list|()
function_decl|;
comment|/** Sets {@link #valuesListNewline}. */
name|SqlWriterConfig
name|withValuesListNewline
parameter_list|(
name|boolean
name|valuesListNewline
parameter_list|)
function_decl|;
comment|/** Returns whether each assignment in the SET clause of an UPDATE or MERGE    * statement should be on its own line.    *    *<p>Default is true;    * this property is superseded by {@link #updateSetFolding()}. */
annotation|@
name|ImmutableBeans
operator|.
name|Property
annotation|@
name|ImmutableBeans
operator|.
name|BooleanDefault
argument_list|(
literal|true
argument_list|)
name|boolean
name|updateSetListNewline
parameter_list|()
function_decl|;
comment|/** Sets {@link #updateSetListNewline}. */
name|SqlWriterConfig
name|withUpdateSetListNewline
parameter_list|(
name|boolean
name|updateSetListNewline
parameter_list|)
function_decl|;
comment|/** Returns whether a WINDOW clause should start its own line. */
annotation|@
name|ImmutableBeans
operator|.
name|Property
annotation|@
name|ImmutableBeans
operator|.
name|BooleanDefault
argument_list|(
literal|false
argument_list|)
name|boolean
name|windowNewline
parameter_list|()
function_decl|;
comment|/** Sets {@link #windowNewline}. */
name|SqlWriterConfig
name|withWindowNewline
parameter_list|(
name|boolean
name|windowNewline
parameter_list|)
function_decl|;
comment|/** Returns whether commas in SELECT, GROUP BY and ORDER clauses should    * appear at the start of the line. Default is false. */
annotation|@
name|ImmutableBeans
operator|.
name|Property
annotation|@
name|ImmutableBeans
operator|.
name|BooleanDefault
argument_list|(
literal|false
argument_list|)
name|boolean
name|leadingComma
parameter_list|()
function_decl|;
comment|/** Sets {@link #leadingComma()}. */
name|SqlWriterConfig
name|withLeadingComma
parameter_list|(
name|boolean
name|leadingComma
parameter_list|)
function_decl|;
comment|/** Returns the sub-query style.    * Default is {@link SqlWriter.SubQueryStyle#HYDE}. */
annotation|@
name|ImmutableBeans
operator|.
name|Property
annotation|@
name|ImmutableBeans
operator|.
name|EnumDefault
argument_list|(
literal|"HYDE"
argument_list|)
name|SqlWriter
operator|.
name|SubQueryStyle
name|subQueryStyle
parameter_list|()
function_decl|;
comment|/** Sets {@link #subQueryStyle}. */
name|SqlWriterConfig
name|withSubQueryStyle
parameter_list|(
name|SqlWriter
operator|.
name|SubQueryStyle
name|subQueryStyle
parameter_list|)
function_decl|;
comment|/** Returns whether to print a newline before each AND or OR (whichever is    * higher level) in WHERE clauses.    *    *<p>NOTE: Ignored when alwaysUseParentheses is set to true. */
annotation|@
name|ImmutableBeans
operator|.
name|Property
annotation|@
name|ImmutableBeans
operator|.
name|BooleanDefault
argument_list|(
literal|false
argument_list|)
name|boolean
name|whereListItemsOnSeparateLines
parameter_list|()
function_decl|;
comment|/** Sets {@link #whereListItemsOnSeparateLines}. */
name|SqlWriterConfig
name|withWhereListItemsOnSeparateLines
parameter_list|(
name|boolean
name|whereListItemsOnSeparateLines
parameter_list|)
function_decl|;
comment|/** Returns whether expressions should always be included in parentheses.    * Default is false. */
annotation|@
name|ImmutableBeans
operator|.
name|Property
annotation|@
name|ImmutableBeans
operator|.
name|BooleanDefault
argument_list|(
literal|false
argument_list|)
name|boolean
name|alwaysUseParentheses
parameter_list|()
function_decl|;
comment|/** Sets {@link #alwaysUseParentheses}. */
name|SqlWriterConfig
name|withAlwaysUseParentheses
parameter_list|(
name|boolean
name|alwaysUseParentheses
parameter_list|)
function_decl|;
comment|/** Returns the maximum line length. Default is zero, which means there is    * no maximum. */
annotation|@
name|ImmutableBeans
operator|.
name|Property
annotation|@
name|ImmutableBeans
operator|.
name|IntDefault
argument_list|(
literal|0
argument_list|)
name|int
name|lineLength
parameter_list|()
function_decl|;
comment|/** Sets {@link #lineLength}. */
name|SqlWriterConfig
name|withLineLength
parameter_list|(
name|int
name|lineLength
parameter_list|)
function_decl|;
comment|/** Returns the line length at which items are chopped or folded (for clauses    * that have chosen {@link LineFolding#CHOP} or {@link LineFolding#FOLD}).    * Default is 80. */
annotation|@
name|ImmutableBeans
operator|.
name|Property
annotation|@
name|ImmutableBeans
operator|.
name|IntDefault
argument_list|(
literal|80
argument_list|)
name|int
name|foldLength
parameter_list|()
function_decl|;
comment|/** Sets {@link #foldLength()}. */
name|SqlWriterConfig
name|withFoldLength
parameter_list|(
name|int
name|lineLength
parameter_list|)
function_decl|;
comment|/** Returns whether the WHEN, THEN and ELSE clauses of a CASE expression    * appear at the start of a new line. The default is false. */
annotation|@
name|ImmutableBeans
operator|.
name|Property
annotation|@
name|ImmutableBeans
operator|.
name|BooleanDefault
argument_list|(
literal|false
argument_list|)
name|boolean
name|caseClausesOnNewLines
parameter_list|()
function_decl|;
comment|/** Sets {@link #caseClausesOnNewLines}. */
name|SqlWriterConfig
name|withCaseClausesOnNewLines
parameter_list|(
name|boolean
name|caseClausesOnNewLines
parameter_list|)
function_decl|;
comment|/** Policy for how to do deal with long lines.    *    *<p>The following examples all have    * {@link #clauseEndsLine ClauseEndsLine=true},    * {@link #indentation Indentation=4}, and    * {@link #foldLength FoldLength=25} (so that the long {@code SELECT}    * clause folds but the shorter {@code GROUP BY} clause does not).    *    *<p>Note that {@link #clauseEndsLine ClauseEndsLine} is observed in    * STEP and TALL modes, and in CHOP mode when a line is long.    *    *<table border=1>    *<caption>SQL formatted with each Folding value</caption>    *<tr>    *<th>Folding</th>    *<th>Example</th>    *</tr>    *    *<tr>    *<td>WIDE</td>    *<td><pre>    * SELECT abc, def, ghi, jkl, mno, pqr    * FROM t    * GROUP BY abc, def</pre></td>    *</tr>    *    *<tr>    *<td>STEP</td>    *<td><pre>    * SELECT    *     abc, def, ghi, jkl, mno, pqr    * FROM t    * GROUP BY    *     abc, def</pre></td>    *</tr>    *    *<tr>    *<td>FOLD</td>    *<td><pre>    * SELECT abc, def, ghi,    *     jkl, mno, pqr    * FROM t    * GROUP BY abc, def</pre></td>    *</tr>    *    *<tr>    *<td>CHOP</td>    *<td><pre>    * SELECT    *     abc,    *     def,    *     ghi,    *     jkl,    *     mno,    *     pqr    * FROM t    * GROUP BY abc, def</pre></td>    *</tr>    *    *<tr>    *<td>TALL</td>    *<td><pre>    * SELECT    *     abc,    *     def,    *     ghi,    *     jkl,    *     mno,    *     pqr    * FROM t    * GROUP BY    *     abc,    *     def</pre></td>    *</tr>    *</table>    */
enum|enum
name|LineFolding
block|{
comment|/** Do not wrap. Items are on the same line, regardless of length. */
name|WIDE
block|,
comment|/** As {@link #WIDE} but start a new line if {@link #clauseEndsLine()}. */
name|STEP
block|,
comment|/** Wrap if long. Items are on the same line, but if the line's length      * exceeds {@link #foldLength()}, move items to the next line. */
name|FOLD
block|,
comment|/** Chop down if long. Items are on the same line, but if the line grows      * longer than {@link #foldLength()}, put all items on separate lines. */
name|CHOP
block|,
comment|/** Wrap always. Items are on separate lines. */
name|TALL
block|}
block|}
end_interface

end_unit

