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
name|util
operator|.
name|Glossary
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
name|SaffronProperties
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
name|SerializableCharset
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
import|import
name|java
operator|.
name|io
operator|.
name|Serializable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|Charset
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

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|util
operator|.
name|Static
operator|.
name|RESOURCE
import|;
end_import

begin_comment
comment|/**  * A<code>SqlCollation</code> is an object representing a<code>Collate</code>  * statement. It is immutable.  */
end_comment

begin_class
specifier|public
class|class
name|SqlCollation
implements|implements
name|Serializable
block|{
specifier|public
specifier|static
specifier|final
name|SqlCollation
name|COERCIBLE
init|=
operator|new
name|SqlCollation
argument_list|(
name|Coercibility
operator|.
name|COERCIBLE
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|SqlCollation
name|IMPLICIT
init|=
operator|new
name|SqlCollation
argument_list|(
name|Coercibility
operator|.
name|IMPLICIT
argument_list|)
decl_stmt|;
comment|//~ Enums ------------------------------------------------------------------
comment|/**    *<blockquote>A&lt;character value expression&gt; consisting of a column    * reference has the coercibility characteristic Implicit, with collating    * sequence as defined when the column was created. A&lt;character value    * expression&gt; consisting of a value other than a column (e.g., a host    * variable or a literal) has the coercibility characteristic Coercible,    * with the default collation for its character repertoire. A&lt;character    * value expression&gt; simply containing a&lt;collate clause&gt; has the    * coercibility characteristic Explicit, with the collating sequence    * specified in the&lt;collate clause&gt;.</blockquote>    *    * @see Glossary#SQL99 SQL:1999 Part 2 Section 4.2.3    */
specifier|public
enum|enum
name|Coercibility
block|{
comment|/** Strongest coercibility. */
name|EXPLICIT
block|,
name|IMPLICIT
block|,
name|COERCIBLE
block|,
comment|/** Weakest coercibility. */
name|NONE
block|}
comment|//~ Instance fields --------------------------------------------------------
specifier|protected
specifier|final
name|String
name|collationName
decl_stmt|;
specifier|protected
specifier|final
name|SerializableCharset
name|wrappedCharset
decl_stmt|;
specifier|protected
specifier|final
name|Locale
name|locale
decl_stmt|;
specifier|protected
specifier|final
name|String
name|strength
decl_stmt|;
specifier|private
specifier|final
name|Coercibility
name|coercibility
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Creates a Collation by its name and its coercibility    *    * @param collation    Collation specification    * @param coercibility Coercibility    */
specifier|public
name|SqlCollation
parameter_list|(
name|String
name|collation
parameter_list|,
name|Coercibility
name|coercibility
parameter_list|)
block|{
name|this
operator|.
name|coercibility
operator|=
name|coercibility
expr_stmt|;
name|SqlParserUtil
operator|.
name|ParsedCollation
name|parseValues
init|=
name|SqlParserUtil
operator|.
name|parseCollation
argument_list|(
name|collation
argument_list|)
decl_stmt|;
name|Charset
name|charset
init|=
name|parseValues
operator|.
name|getCharset
argument_list|()
decl_stmt|;
name|this
operator|.
name|wrappedCharset
operator|=
name|SerializableCharset
operator|.
name|forCharset
argument_list|(
name|charset
argument_list|)
expr_stmt|;
name|locale
operator|=
name|parseValues
operator|.
name|getLocale
argument_list|()
expr_stmt|;
name|strength
operator|=
name|parseValues
operator|.
name|getStrength
argument_list|()
expr_stmt|;
name|String
name|c
init|=
name|charset
operator|.
name|name
argument_list|()
operator|.
name|toUpperCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
operator|+
literal|"$"
operator|+
name|locale
operator|.
name|toString
argument_list|()
decl_stmt|;
if|if
condition|(
operator|(
name|strength
operator|!=
literal|null
operator|)
operator|&&
operator|(
name|strength
operator|.
name|length
argument_list|()
operator|>
literal|0
operator|)
condition|)
block|{
name|c
operator|+=
literal|"$"
operator|+
name|strength
expr_stmt|;
block|}
name|collationName
operator|=
name|c
expr_stmt|;
block|}
comment|/**    * Creates a SqlCollation with the default collation name and the given    * coercibility.    *    * @param coercibility Coercibility    */
specifier|public
name|SqlCollation
parameter_list|(
name|Coercibility
name|coercibility
parameter_list|)
block|{
name|this
argument_list|(
name|SaffronProperties
operator|.
name|INSTANCE
operator|.
name|defaultCollation
argument_list|()
operator|.
name|get
argument_list|()
argument_list|,
name|coercibility
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
return|return
name|this
operator|==
name|o
operator|||
name|o
operator|instanceof
name|SqlCollation
operator|&&
name|collationName
operator|.
name|equals
argument_list|(
operator|(
operator|(
name|SqlCollation
operator|)
name|o
operator|)
operator|.
name|collationName
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|collationName
operator|.
name|hashCode
argument_list|()
return|;
block|}
comment|/**    * Returns the collating sequence (the collation name) and the coercibility    * for the resulting value of a dyadic operator.    *    * @param col1 first operand for the dyadic operation    * @param col2 second operand for the dyadic operation    * @return the resulting collation sequence. The "no collating sequence"    * result is returned as null.    *    * @see Glossary#SQL99 SQL:1999 Part 2 Section 4.2.3 Table 2    */
specifier|public
specifier|static
name|SqlCollation
name|getCoercibilityDyadicOperator
parameter_list|(
name|SqlCollation
name|col1
parameter_list|,
name|SqlCollation
name|col2
parameter_list|)
block|{
return|return
name|getCoercibilityDyadic
argument_list|(
name|col1
argument_list|,
name|col2
argument_list|)
return|;
block|}
comment|/**    * Returns the collating sequence (the collation name) and the coercibility    * for the resulting value of a dyadic operator.    *    * @param col1 first operand for the dyadic operation    * @param col2 second operand for the dyadic operation    * @return the resulting collation sequence    *    * @throws org.apache.calcite.runtime.CalciteException from    *   {@link org.apache.calcite.runtime.CalciteResource#invalidCompare} or    *   {@link org.apache.calcite.runtime.CalciteResource#differentCollations}    *   if no collating sequence can be deduced    *    * @see Glossary#SQL99 SQL:1999 Part 2 Section 4.2.3 Table 2    */
specifier|public
specifier|static
name|SqlCollation
name|getCoercibilityDyadicOperatorThrows
parameter_list|(
name|SqlCollation
name|col1
parameter_list|,
name|SqlCollation
name|col2
parameter_list|)
block|{
name|SqlCollation
name|ret
init|=
name|getCoercibilityDyadic
argument_list|(
name|col1
argument_list|,
name|col2
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|ret
condition|)
block|{
throw|throw
name|RESOURCE
operator|.
name|invalidCompare
argument_list|(
name|col1
operator|.
name|collationName
argument_list|,
literal|""
operator|+
name|col1
operator|.
name|coercibility
argument_list|,
name|col2
operator|.
name|collationName
argument_list|,
literal|""
operator|+
name|col2
operator|.
name|coercibility
argument_list|)
operator|.
name|ex
argument_list|()
throw|;
block|}
return|return
name|ret
return|;
block|}
comment|/**    * Returns the collating sequence (the collation name) to use for the    * resulting value of a comparison.    *    * @param col1 first operand for the dyadic operation    * @param col2 second operand for the dyadic operation    *    * @return the resulting collation sequence. If no collating    * sequence could be deduced throws a    * {@link org.apache.calcite.runtime.CalciteResource#invalidCompare}    *    * @see Glossary#SQL99 SQL:1999 Part 2 Section 4.2.3 Table 3    */
specifier|public
specifier|static
name|String
name|getCoercibilityDyadicComparison
parameter_list|(
name|SqlCollation
name|col1
parameter_list|,
name|SqlCollation
name|col2
parameter_list|)
block|{
return|return
name|getCoercibilityDyadicOperatorThrows
argument_list|(
name|col1
argument_list|,
name|col2
argument_list|)
operator|.
name|collationName
return|;
block|}
comment|/**    * Returns the result for {@link #getCoercibilityDyadicComparison} and    * {@link #getCoercibilityDyadicOperator}.    */
specifier|protected
specifier|static
name|SqlCollation
name|getCoercibilityDyadic
parameter_list|(
name|SqlCollation
name|col1
parameter_list|,
name|SqlCollation
name|col2
parameter_list|)
block|{
assert|assert
literal|null
operator|!=
name|col1
assert|;
assert|assert
literal|null
operator|!=
name|col2
assert|;
specifier|final
name|Coercibility
name|coercibility1
init|=
name|col1
operator|.
name|getCoercibility
argument_list|()
decl_stmt|;
specifier|final
name|Coercibility
name|coercibility2
init|=
name|col2
operator|.
name|getCoercibility
argument_list|()
decl_stmt|;
switch|switch
condition|(
name|coercibility1
condition|)
block|{
case|case
name|COERCIBLE
case|:
switch|switch
condition|(
name|coercibility2
condition|)
block|{
case|case
name|COERCIBLE
case|:
return|return
operator|new
name|SqlCollation
argument_list|(
name|col2
operator|.
name|collationName
argument_list|,
name|Coercibility
operator|.
name|COERCIBLE
argument_list|)
return|;
case|case
name|IMPLICIT
case|:
return|return
operator|new
name|SqlCollation
argument_list|(
name|col2
operator|.
name|collationName
argument_list|,
name|Coercibility
operator|.
name|IMPLICIT
argument_list|)
return|;
case|case
name|NONE
case|:
return|return
literal|null
return|;
case|case
name|EXPLICIT
case|:
return|return
operator|new
name|SqlCollation
argument_list|(
name|col2
operator|.
name|collationName
argument_list|,
name|Coercibility
operator|.
name|EXPLICIT
argument_list|)
return|;
default|default:
throw|throw
name|Util
operator|.
name|unexpected
argument_list|(
name|coercibility2
argument_list|)
throw|;
block|}
case|case
name|IMPLICIT
case|:
switch|switch
condition|(
name|coercibility2
condition|)
block|{
case|case
name|COERCIBLE
case|:
return|return
operator|new
name|SqlCollation
argument_list|(
name|col1
operator|.
name|collationName
argument_list|,
name|Coercibility
operator|.
name|IMPLICIT
argument_list|)
return|;
case|case
name|IMPLICIT
case|:
if|if
condition|(
name|col1
operator|.
name|collationName
operator|.
name|equals
argument_list|(
name|col2
operator|.
name|collationName
argument_list|)
condition|)
block|{
return|return
operator|new
name|SqlCollation
argument_list|(
name|col2
operator|.
name|collationName
argument_list|,
name|Coercibility
operator|.
name|IMPLICIT
argument_list|)
return|;
block|}
return|return
literal|null
return|;
case|case
name|NONE
case|:
return|return
literal|null
return|;
case|case
name|EXPLICIT
case|:
return|return
operator|new
name|SqlCollation
argument_list|(
name|col2
operator|.
name|collationName
argument_list|,
name|Coercibility
operator|.
name|EXPLICIT
argument_list|)
return|;
default|default:
throw|throw
name|Util
operator|.
name|unexpected
argument_list|(
name|coercibility2
argument_list|)
throw|;
block|}
case|case
name|NONE
case|:
switch|switch
condition|(
name|coercibility2
condition|)
block|{
case|case
name|COERCIBLE
case|:
case|case
name|IMPLICIT
case|:
case|case
name|NONE
case|:
return|return
literal|null
return|;
case|case
name|EXPLICIT
case|:
return|return
operator|new
name|SqlCollation
argument_list|(
name|col2
operator|.
name|collationName
argument_list|,
name|Coercibility
operator|.
name|EXPLICIT
argument_list|)
return|;
default|default:
throw|throw
name|Util
operator|.
name|unexpected
argument_list|(
name|coercibility2
argument_list|)
throw|;
block|}
case|case
name|EXPLICIT
case|:
switch|switch
condition|(
name|coercibility2
condition|)
block|{
case|case
name|COERCIBLE
case|:
case|case
name|IMPLICIT
case|:
case|case
name|NONE
case|:
return|return
operator|new
name|SqlCollation
argument_list|(
name|col1
operator|.
name|collationName
argument_list|,
name|Coercibility
operator|.
name|EXPLICIT
argument_list|)
return|;
case|case
name|EXPLICIT
case|:
if|if
condition|(
name|col1
operator|.
name|collationName
operator|.
name|equals
argument_list|(
name|col2
operator|.
name|collationName
argument_list|)
condition|)
block|{
return|return
operator|new
name|SqlCollation
argument_list|(
name|col2
operator|.
name|collationName
argument_list|,
name|Coercibility
operator|.
name|EXPLICIT
argument_list|)
return|;
block|}
throw|throw
name|RESOURCE
operator|.
name|differentCollations
argument_list|(
name|col1
operator|.
name|collationName
argument_list|,
name|col2
operator|.
name|collationName
argument_list|)
operator|.
name|ex
argument_list|()
throw|;
default|default:
throw|throw
name|Util
operator|.
name|unexpected
argument_list|(
name|coercibility2
argument_list|)
throw|;
block|}
default|default:
throw|throw
name|Util
operator|.
name|unexpected
argument_list|(
name|coercibility1
argument_list|)
throw|;
block|}
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"COLLATE "
operator|+
name|collationName
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
name|keyword
argument_list|(
literal|"COLLATE"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|identifier
argument_list|(
name|collationName
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Charset
name|getCharset
parameter_list|()
block|{
return|return
name|wrappedCharset
operator|.
name|getCharset
argument_list|()
return|;
block|}
specifier|public
specifier|final
name|String
name|getCollationName
parameter_list|()
block|{
return|return
name|collationName
return|;
block|}
specifier|public
specifier|final
name|SqlCollation
operator|.
name|Coercibility
name|getCoercibility
parameter_list|()
block|{
return|return
name|coercibility
return|;
block|}
block|}
end_class

begin_comment
comment|// End SqlCollation.java
end_comment

end_unit

