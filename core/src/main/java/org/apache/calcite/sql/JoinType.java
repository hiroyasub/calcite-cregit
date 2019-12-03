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
name|java
operator|.
name|util
operator|.
name|Locale
import|;
end_import

begin_comment
comment|/**  * Enumerates the types of join.  */
end_comment

begin_enum
specifier|public
enum|enum
name|JoinType
block|{
comment|/**    * Inner join.    */
name|INNER
block|,
comment|/**    * Full outer join.    */
name|FULL
block|,
comment|/**    * Cross join (also known as Cartesian product).    */
name|CROSS
block|,
comment|/**    * Left outer join.    */
name|LEFT
block|,
comment|/**    * Right outer join.    */
name|RIGHT
block|,
comment|/**    * Left semi join.    *    *<p>Not used by Calcite; only in Babel's Hive dialect.    */
name|LEFT_SEMI_JOIN
block|,
comment|/**    * Comma join: the good old-fashioned SQL<code>FROM</code> clause,    * where table expressions are specified with commas between them, and    * join conditions are specified in the<code>WHERE</code> clause.    */
name|COMMA
block|;
comment|/** Lower-case name. */
specifier|public
specifier|final
name|String
name|lowerName
init|=
name|name
argument_list|()
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
decl_stmt|;
comment|/**    * Returns whether a join of this type may generate NULL values on the    * left-hand side.    */
specifier|public
name|boolean
name|generatesNullsOnLeft
parameter_list|()
block|{
return|return
name|this
operator|==
name|RIGHT
operator|||
name|this
operator|==
name|FULL
return|;
block|}
comment|/**    * Returns whether a join of this type may generate NULL values on the    * right-hand side.    */
specifier|public
name|boolean
name|generatesNullsOnRight
parameter_list|()
block|{
return|return
name|this
operator|==
name|LEFT
operator|||
name|this
operator|==
name|FULL
return|;
block|}
comment|/**    * Creates a parse-tree node representing an occurrence of this    * condition type keyword at a particular position in the parsed    * text.    */
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

end_unit

