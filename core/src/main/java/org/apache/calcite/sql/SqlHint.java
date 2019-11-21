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
name|type
operator|.
name|SqlTypeName
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
name|ImmutableMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Collectors
import|;
end_import

begin_comment
comment|/**  * A<code>SqlHint</code> is a node of a parse tree which represents  * a sql hint expression.  *  *<p>Basic hint grammar is: hint_name[(option1, option2 ...)].  * The hint_name should be a simple identifier, the options part is optional.  * For every option, it can be a simple identifier or a key value pair whose key  * is a simple identifier and value is a string literal. The identifier option and key  * value pair can not be mixed in, they should be either all simple identifiers  * or all key value pairs.  *  *<p>We support 2 kinds of hints in the parser:  *<ul>  *<li>Query hint, right after the select keyword, i.e.:  *<pre>  *     select&#47;&#42;&#43; hint1, hint2, ...&#42;&#47; ...  *</pre>  *</li>  *<li>Table hint: right after the referenced table name, i.e.:  *<pre>  *     select f0, f1, f2 from t1&#47;&#42;&#43; hint1, hint2, ...&#42;&#47; ...  *</pre>  *</li>  *</ul>  */
end_comment

begin_class
specifier|public
class|class
name|SqlHint
extends|extends
name|SqlCall
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|private
specifier|final
name|SqlIdentifier
name|name
decl_stmt|;
specifier|private
specifier|final
name|SqlNodeList
name|options
decl_stmt|;
specifier|private
specifier|final
name|HintOptionFormat
name|optionFormat
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|SqlOperator
name|OPERATOR
init|=
operator|new
name|SqlSpecialOperator
argument_list|(
literal|"HINT"
argument_list|,
name|SqlKind
operator|.
name|HINT
argument_list|)
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
specifier|public
name|SqlHint
parameter_list|(
name|SqlParserPos
name|pos
parameter_list|,
name|SqlIdentifier
name|name
parameter_list|,
name|SqlNodeList
name|options
parameter_list|)
block|{
name|super
argument_list|(
name|pos
argument_list|)
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|optionFormat
operator|=
name|inferHintOptionFormat
argument_list|(
name|options
argument_list|)
expr_stmt|;
name|this
operator|.
name|options
operator|=
name|options
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
specifier|public
name|SqlOperator
name|getOperator
parameter_list|()
block|{
return|return
name|OPERATOR
return|;
block|}
specifier|public
name|List
argument_list|<
name|SqlNode
argument_list|>
name|getOperandList
parameter_list|()
block|{
return|return
name|ImmutableList
operator|.
name|of
argument_list|(
name|name
argument_list|,
name|options
argument_list|)
return|;
block|}
comment|/**    * Returns the sql hint name.    */
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
operator|.
name|getSimple
argument_list|()
return|;
block|}
comment|/** Returns the hint option format. */
specifier|public
name|HintOptionFormat
name|getOptionFormat
parameter_list|()
block|{
return|return
name|optionFormat
return|;
block|}
comment|/**    * Returns a string list if the hint option is a list of    * simple SQL identifier, else an empty list.    */
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getOptionList
parameter_list|()
block|{
if|if
condition|(
name|optionFormat
operator|==
name|HintOptionFormat
operator|.
name|ID_LIST
condition|)
block|{
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|attrs
init|=
name|options
operator|.
name|getList
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|node
lambda|->
operator|(
operator|(
name|SqlIdentifier
operator|)
name|node
operator|)
operator|.
name|getSimple
argument_list|()
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toList
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|attrs
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|ImmutableList
operator|.
name|of
argument_list|()
return|;
block|}
block|}
comment|/**    * Returns a key value string map if the hint option is a list of    * pair, each pair contains a simple SQL identifier and a string literal;    * else returns an empty map.    */
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getOptionKVPairs
parameter_list|()
block|{
if|if
condition|(
name|optionFormat
operator|==
name|HintOptionFormat
operator|.
name|KV_LIST
condition|)
block|{
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|attrs
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|options
operator|.
name|size
argument_list|()
operator|-
literal|1
condition|;
name|i
operator|+=
literal|2
control|)
block|{
specifier|final
name|SqlNode
name|k
init|=
name|options
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
specifier|final
name|SqlNode
name|v
init|=
name|options
operator|.
name|get
argument_list|(
name|i
operator|+
literal|1
argument_list|)
decl_stmt|;
name|attrs
operator|.
name|put
argument_list|(
operator|(
operator|(
name|SqlIdentifier
operator|)
name|k
operator|)
operator|.
name|getSimple
argument_list|()
argument_list|,
operator|(
operator|(
name|SqlLiteral
operator|)
name|v
operator|)
operator|.
name|getValueAs
argument_list|(
name|String
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|ImmutableMap
operator|.
name|copyOf
argument_list|(
name|attrs
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|ImmutableMap
operator|.
name|of
argument_list|()
return|;
block|}
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
name|name
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
if|if
condition|(
name|this
operator|.
name|options
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
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
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|options
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|SqlNode
name|option
init|=
name|options
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|SqlNode
name|nextOption
init|=
name|i
operator|<
name|options
operator|.
name|size
argument_list|()
operator|-
literal|1
condition|?
name|options
operator|.
name|get
argument_list|(
name|i
operator|+
literal|1
argument_list|)
else|:
literal|null
decl_stmt|;
name|writer
operator|.
name|sep
argument_list|(
literal|","
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|option
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
if|if
condition|(
name|nextOption
operator|instanceof
name|SqlLiteral
condition|)
block|{
name|writer
operator|.
name|print
argument_list|(
literal|"="
argument_list|)
expr_stmt|;
name|nextOption
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
name|i
operator|+=
literal|1
expr_stmt|;
block|}
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
comment|/** Enumeration that represents hint option format. */
enum|enum
name|HintOptionFormat
block|{
comment|/**      * The hint has no options.      */
name|EMPTY
block|,
comment|/**      * The hint options are as simple identifier list.      */
name|ID_LIST
block|,
comment|/**      * The hint options are list of key-value pairs. For each pair,      * the key is a simple identifier, the value is a string literal.      */
name|KV_LIST
block|}
comment|//~ Tools ------------------------------------------------------------------
comment|/** Infer the hint options format. */
specifier|private
specifier|static
name|HintOptionFormat
name|inferHintOptionFormat
parameter_list|(
name|SqlNodeList
name|options
parameter_list|)
block|{
if|if
condition|(
name|options
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
name|HintOptionFormat
operator|.
name|EMPTY
return|;
block|}
if|if
condition|(
name|options
operator|.
name|getList
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|allMatch
argument_list|(
name|opt
lambda|->
name|opt
operator|instanceof
name|SqlIdentifier
argument_list|)
condition|)
block|{
return|return
name|HintOptionFormat
operator|.
name|ID_LIST
return|;
block|}
if|if
condition|(
name|isOptionsAsKVPairs
argument_list|(
name|options
argument_list|)
condition|)
block|{
return|return
name|HintOptionFormat
operator|.
name|KV_LIST
return|;
block|}
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"The hint options should either be empty, "
operator|+
literal|"or simple identifier list, "
operator|+
literal|"or key-value pairs whose pair key is simple identifier and value is string literal."
argument_list|)
throw|;
block|}
comment|/** Decides if the hint options is as key-value pair format. */
specifier|private
specifier|static
name|boolean
name|isOptionsAsKVPairs
parameter_list|(
name|SqlNodeList
name|options
parameter_list|)
block|{
if|if
condition|(
name|options
operator|.
name|size
argument_list|()
operator|>
literal|0
operator|&&
name|options
operator|.
name|size
argument_list|()
operator|%
literal|2
operator|==
literal|0
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|options
operator|.
name|size
argument_list|()
operator|-
literal|1
condition|;
name|i
operator|+=
literal|2
control|)
block|{
name|boolean
name|isKVPair
init|=
name|options
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|instanceof
name|SqlIdentifier
operator|&&
name|options
operator|.
name|get
argument_list|(
name|i
operator|+
literal|1
argument_list|)
operator|instanceof
name|SqlLiteral
operator|&&
operator|(
operator|(
name|SqlLiteral
operator|)
name|options
operator|.
name|get
argument_list|(
name|i
operator|+
literal|1
argument_list|)
operator|)
operator|.
name|getTypeName
argument_list|()
operator|==
name|SqlTypeName
operator|.
name|CHAR
decl_stmt|;
if|if
condition|(
operator|!
name|isKVPair
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
block|}
end_class

begin_comment
comment|// End SqlHint.java
end_comment

end_unit

