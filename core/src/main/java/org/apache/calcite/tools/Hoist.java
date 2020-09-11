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
name|tools
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
name|SqlParseException
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
name|SqlParser
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
name|sql
operator|.
name|util
operator|.
name|SqlShuttle
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

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableList
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
name|collect
operator|.
name|Lists
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|PreparedStatement
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Function
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
import|;
end_import

begin_comment
comment|/**  * Utility that extracts constants from a SQL query.  *  *<p>Simple use:  *  *<blockquote><code>  * final String sql =<br>  *     "select 'x' from emp where deptno&lt; 10";<br>  * final Hoist.Hoisted hoisted =<br>  *     Hoist.create(Hoist.config()).hoist();<br>  * print(hoisted); // "select ?0 from emp where deptno&lt; ?1"  *</code></blockquote>  *  *<p>Calling {@link Hoisted#toString()} generates a string that is similar to  * SQL where a user has manually converted all constants to bind variables, and  * which could then be executed using {@link PreparedStatement#execute()}.  * That is not a goal of this utility, but see  *<a href="https://issues.apache.org/jira/browse/CALCITE-963">[CALCITE-963]  * Hoist literals</a>.  *  *<p>For more advanced formatting, use {@link Hoisted#substitute(Function)}.  *  *<p>Adjust {@link Config} to use a different parser or parsing options.  */
end_comment

begin_class
specifier|public
class|class
name|Hoist
block|{
specifier|private
specifier|final
name|Config
name|config
decl_stmt|;
comment|/** Creates a Config. */
specifier|public
specifier|static
name|Config
name|config
parameter_list|()
block|{
return|return
name|ImmutableBeans
operator|.
name|create
argument_list|(
name|Config
operator|.
name|class
argument_list|)
operator|.
name|withParserConfig
argument_list|(
name|SqlParser
operator|.
name|config
argument_list|()
argument_list|)
return|;
block|}
comment|/** Creates a Hoist. */
specifier|public
specifier|static
name|Hoist
name|create
parameter_list|(
name|Config
name|config
parameter_list|)
block|{
return|return
operator|new
name|Hoist
argument_list|(
name|config
argument_list|)
return|;
block|}
specifier|private
name|Hoist
parameter_list|(
name|Config
name|config
parameter_list|)
block|{
name|this
operator|.
name|config
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|config
argument_list|)
expr_stmt|;
block|}
comment|/** Converts a {@link Variable} to a string "?N",    * where N is the {@link Variable#ordinal}. */
specifier|public
specifier|static
name|String
name|ordinalString
parameter_list|(
name|Variable
name|v
parameter_list|)
block|{
return|return
literal|"?"
operator|+
name|v
operator|.
name|ordinal
return|;
block|}
comment|/** Converts a {@link Variable} to a string "?N",    * where N is the {@link Variable#ordinal},    * if the fragment is a character literal. Other fragments are unchanged. */
specifier|public
specifier|static
name|String
name|ordinalStringIfChar
parameter_list|(
name|Variable
name|v
parameter_list|)
block|{
if|if
condition|(
name|v
operator|.
name|node
operator|instanceof
name|SqlLiteral
operator|&&
operator|(
operator|(
name|SqlLiteral
operator|)
name|v
operator|.
name|node
operator|)
operator|.
name|getTypeName
argument_list|()
operator|==
name|SqlTypeName
operator|.
name|CHAR
condition|)
block|{
return|return
literal|"?"
operator|+
name|v
operator|.
name|ordinal
return|;
block|}
else|else
block|{
return|return
name|v
operator|.
name|sql
argument_list|()
return|;
block|}
block|}
comment|/** Hoists literals in a given SQL string, returning a {@link Hoisted}. */
specifier|public
name|Hoisted
name|hoist
parameter_list|(
name|String
name|sql
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|Variable
argument_list|>
name|variables
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|SqlParser
name|parser
init|=
name|SqlParser
operator|.
name|create
argument_list|(
name|sql
argument_list|,
name|config
operator|.
name|parserConfig
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|SqlNode
name|node
decl_stmt|;
try|try
block|{
name|node
operator|=
name|parser
operator|.
name|parseQuery
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SqlParseException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
name|node
operator|.
name|accept
argument_list|(
operator|new
name|SqlShuttle
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|SqlNode
name|visit
parameter_list|(
name|SqlLiteral
name|literal
parameter_list|)
block|{
name|variables
operator|.
name|add
argument_list|(
operator|new
name|Variable
argument_list|(
name|sql
argument_list|,
name|variables
operator|.
name|size
argument_list|()
argument_list|,
name|literal
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|super
operator|.
name|visit
argument_list|(
name|literal
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
return|return
operator|new
name|Hoisted
argument_list|(
name|sql
argument_list|,
name|variables
argument_list|)
return|;
block|}
comment|/** Configuration. */
specifier|public
interface|interface
name|Config
block|{
comment|/** Returns the configuration for the SQL parser. */
annotation|@
name|ImmutableBeans
operator|.
name|Property
annotation|@
name|Nonnull
name|SqlParser
operator|.
name|Config
name|parserConfig
parameter_list|()
function_decl|;
comment|/** Sets {@link #parserConfig()}. */
name|Config
name|withParserConfig
parameter_list|(
name|SqlParser
operator|.
name|Config
name|parserConfig
parameter_list|)
function_decl|;
block|}
comment|/** Variable. */
specifier|public
specifier|static
class|class
name|Variable
block|{
comment|/** Original SQL of whole statement. */
specifier|public
specifier|final
name|String
name|originalSql
decl_stmt|;
comment|/** Zero-based ordinal in statement. */
specifier|public
specifier|final
name|int
name|ordinal
decl_stmt|;
comment|/** Parse tree node (typically a literal). */
specifier|public
specifier|final
name|SqlNode
name|node
decl_stmt|;
comment|/** Zero-based position within the SQL text of start of node. */
specifier|public
specifier|final
name|int
name|start
decl_stmt|;
comment|/** Zero-based position within the SQL text after end of node. */
specifier|public
specifier|final
name|int
name|end
decl_stmt|;
specifier|private
name|Variable
parameter_list|(
name|String
name|originalSql
parameter_list|,
name|int
name|ordinal
parameter_list|,
name|SqlNode
name|node
parameter_list|)
block|{
name|this
operator|.
name|originalSql
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|originalSql
argument_list|)
expr_stmt|;
name|this
operator|.
name|ordinal
operator|=
name|ordinal
expr_stmt|;
name|this
operator|.
name|node
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|node
argument_list|)
expr_stmt|;
specifier|final
name|SqlParserPos
name|pos
init|=
name|node
operator|.
name|getParserPosition
argument_list|()
decl_stmt|;
name|start
operator|=
name|SqlParserUtil
operator|.
name|lineColToIndex
argument_list|(
name|originalSql
argument_list|,
name|pos
operator|.
name|getLineNum
argument_list|()
argument_list|,
name|pos
operator|.
name|getColumnNum
argument_list|()
argument_list|)
expr_stmt|;
name|end
operator|=
name|SqlParserUtil
operator|.
name|lineColToIndex
argument_list|(
name|originalSql
argument_list|,
name|pos
operator|.
name|getEndLineNum
argument_list|()
argument_list|,
name|pos
operator|.
name|getEndColumnNum
argument_list|()
argument_list|)
operator|+
literal|1
expr_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|ordinal
operator|>=
literal|0
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|start
operator|>=
literal|0
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|start
operator|<=
name|end
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|end
operator|<=
name|originalSql
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/** Returns SQL text of the region of the statement covered by this      * Variable. */
specifier|public
name|String
name|sql
parameter_list|()
block|{
return|return
name|originalSql
operator|.
name|substring
argument_list|(
name|start
argument_list|,
name|end
argument_list|)
return|;
block|}
block|}
comment|/** Result of hoisting. */
specifier|public
specifier|static
class|class
name|Hoisted
block|{
specifier|public
specifier|final
name|String
name|originalSql
decl_stmt|;
specifier|public
specifier|final
name|List
argument_list|<
name|Variable
argument_list|>
name|variables
decl_stmt|;
name|Hoisted
parameter_list|(
name|String
name|originalSql
parameter_list|,
name|List
argument_list|<
name|Variable
argument_list|>
name|variables
parameter_list|)
block|{
name|this
operator|.
name|originalSql
operator|=
name|originalSql
expr_stmt|;
name|this
operator|.
name|variables
operator|=
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|variables
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|substitute
argument_list|(
name|Hoist
operator|::
name|ordinalString
argument_list|)
return|;
block|}
comment|/** Returns the SQL string with variables replaced according to the      * given substitution function. */
specifier|public
name|String
name|substitute
parameter_list|(
name|Function
argument_list|<
name|Variable
argument_list|,
name|String
argument_list|>
name|fn
parameter_list|)
block|{
specifier|final
name|StringBuilder
name|b
init|=
operator|new
name|StringBuilder
argument_list|(
name|originalSql
argument_list|)
decl_stmt|;
for|for
control|(
name|Variable
name|variable
range|:
name|Lists
operator|.
name|reverse
argument_list|(
name|variables
argument_list|)
control|)
block|{
specifier|final
name|String
name|s
init|=
name|fn
operator|.
name|apply
argument_list|(
name|variable
argument_list|)
decl_stmt|;
name|b
operator|.
name|replace
argument_list|(
name|variable
operator|.
name|start
argument_list|,
name|variable
operator|.
name|end
argument_list|,
name|s
argument_list|)
expr_stmt|;
block|}
return|return
name|b
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

