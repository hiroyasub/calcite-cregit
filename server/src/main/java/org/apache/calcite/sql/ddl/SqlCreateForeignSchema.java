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
name|ddl
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
name|adapter
operator|.
name|jdbc
operator|.
name|JdbcSchema
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
name|avatica
operator|.
name|AvaticaUtils
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
name|jdbc
operator|.
name|CalcitePrepare
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
name|jdbc
operator|.
name|CalciteSchema
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
name|model
operator|.
name|JsonSchema
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
name|schema
operator|.
name|Schema
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
name|schema
operator|.
name|SchemaFactory
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
name|schema
operator|.
name|SchemaPlus
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
name|SqlCreate
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
name|SqlExecutableStatement
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
name|SqlIdentifier
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
name|SqlNodeList
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
name|SqlOperator
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
name|SqlUtil
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
name|util
operator|.
name|ImmutableNullableList
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
name|Pair
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
name|java
operator|.
name|util
operator|.
name|AbstractList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedHashMap
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
name|Locale
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
name|Objects
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
comment|/**  * Parse tree for {@code CREATE FOREIGN SCHEMA} statement.  */
end_comment

begin_class
specifier|public
class|class
name|SqlCreateForeignSchema
extends|extends
name|SqlCreate
implements|implements
name|SqlExecutableStatement
block|{
specifier|private
specifier|final
name|SqlIdentifier
name|name
decl_stmt|;
specifier|private
specifier|final
name|SqlNode
name|type
decl_stmt|;
specifier|private
specifier|final
name|SqlNode
name|library
decl_stmt|;
specifier|private
specifier|final
name|SqlNodeList
name|optionList
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
literal|"CREATE FOREIGN SCHEMA"
argument_list|,
name|SqlKind
operator|.
name|CREATE_FOREIGN_SCHEMA
argument_list|)
decl_stmt|;
comment|/** Creates a SqlCreateForeignSchema. */
name|SqlCreateForeignSchema
parameter_list|(
name|SqlParserPos
name|pos
parameter_list|,
name|boolean
name|replace
parameter_list|,
name|boolean
name|ifNotExists
parameter_list|,
name|SqlIdentifier
name|name
parameter_list|,
name|SqlNode
name|type
parameter_list|,
name|SqlNode
name|library
parameter_list|,
name|SqlNodeList
name|optionList
parameter_list|)
block|{
name|super
argument_list|(
name|OPERATOR
argument_list|,
name|pos
argument_list|,
name|replace
argument_list|,
name|ifNotExists
argument_list|)
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
name|this
operator|.
name|library
operator|=
name|library
expr_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
operator|(
name|type
operator|==
literal|null
operator|)
operator|!=
operator|(
name|library
operator|==
literal|null
operator|)
argument_list|,
literal|"of type and library, exactly one must be specified"
argument_list|)
expr_stmt|;
name|this
operator|.
name|optionList
operator|=
name|optionList
expr_stmt|;
comment|// may be null
block|}
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|SqlNode
argument_list|>
name|getOperandList
parameter_list|()
block|{
return|return
name|ImmutableNullableList
operator|.
name|of
argument_list|(
name|name
argument_list|,
name|type
argument_list|,
name|library
argument_list|,
name|optionList
argument_list|)
return|;
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
if|if
condition|(
name|getReplace
argument_list|()
condition|)
block|{
name|writer
operator|.
name|keyword
argument_list|(
literal|"CREATE OR REPLACE"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|writer
operator|.
name|keyword
argument_list|(
literal|"CREATE"
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|keyword
argument_list|(
literal|"FOREIGN SCHEMA"
argument_list|)
expr_stmt|;
if|if
condition|(
name|ifNotExists
condition|)
block|{
name|writer
operator|.
name|keyword
argument_list|(
literal|"IF NOT EXISTS"
argument_list|)
expr_stmt|;
block|}
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
name|library
operator|!=
literal|null
condition|)
block|{
name|writer
operator|.
name|keyword
argument_list|(
literal|"LIBRARY"
argument_list|)
expr_stmt|;
name|library
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
if|if
condition|(
name|type
operator|!=
literal|null
condition|)
block|{
name|writer
operator|.
name|keyword
argument_list|(
literal|"TYPE"
argument_list|)
expr_stmt|;
name|type
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
if|if
condition|(
name|optionList
operator|!=
literal|null
condition|)
block|{
name|writer
operator|.
name|keyword
argument_list|(
literal|"OPTIONS"
argument_list|)
expr_stmt|;
name|SqlWriter
operator|.
name|Frame
name|frame
init|=
name|writer
operator|.
name|startList
argument_list|(
literal|"("
argument_list|,
literal|")"
argument_list|)
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Pair
argument_list|<
name|SqlIdentifier
argument_list|,
name|SqlNode
argument_list|>
name|c
range|:
name|options
argument_list|(
name|optionList
argument_list|)
control|)
block|{
if|if
condition|(
name|i
operator|++
operator|>
literal|0
condition|)
block|{
name|writer
operator|.
name|sep
argument_list|(
literal|","
argument_list|)
expr_stmt|;
block|}
name|c
operator|.
name|left
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
name|c
operator|.
name|right
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
specifier|public
name|void
name|execute
parameter_list|(
name|CalcitePrepare
operator|.
name|Context
name|context
parameter_list|)
block|{
specifier|final
name|Pair
argument_list|<
name|CalciteSchema
argument_list|,
name|String
argument_list|>
name|pair
init|=
name|SqlDdlNodes
operator|.
name|schema
argument_list|(
name|context
argument_list|,
literal|true
argument_list|,
name|name
argument_list|)
decl_stmt|;
specifier|final
name|SchemaPlus
name|subSchema0
init|=
name|pair
operator|.
name|left
operator|.
name|plus
argument_list|()
operator|.
name|getSubSchema
argument_list|(
name|pair
operator|.
name|right
argument_list|)
decl_stmt|;
if|if
condition|(
name|subSchema0
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
operator|!
name|getReplace
argument_list|()
operator|&&
operator|!
name|ifNotExists
condition|)
block|{
throw|throw
name|SqlUtil
operator|.
name|newContextException
argument_list|(
name|name
operator|.
name|getParserPosition
argument_list|()
argument_list|,
name|RESOURCE
operator|.
name|schemaExists
argument_list|(
name|pair
operator|.
name|right
argument_list|)
argument_list|)
throw|;
block|}
block|}
specifier|final
name|Schema
name|subSchema
decl_stmt|;
specifier|final
name|String
name|libraryName
decl_stmt|;
if|if
condition|(
name|type
operator|!=
literal|null
condition|)
block|{
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|library
operator|==
literal|null
argument_list|)
expr_stmt|;
specifier|final
name|String
name|typeName
init|=
operator|(
name|String
operator|)
name|value
argument_list|(
name|this
operator|.
name|type
argument_list|)
decl_stmt|;
specifier|final
name|JsonSchema
operator|.
name|Type
name|type
init|=
name|Util
operator|.
name|enumVal
argument_list|(
name|JsonSchema
operator|.
name|Type
operator|.
name|class
argument_list|,
name|typeName
operator|.
name|toUpperCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|type
operator|!=
literal|null
condition|)
block|{
switch|switch
condition|(
name|type
condition|)
block|{
case|case
name|JDBC
case|:
name|libraryName
operator|=
name|JdbcSchema
operator|.
name|Factory
operator|.
name|class
operator|.
name|getName
argument_list|()
expr_stmt|;
break|break;
default|default:
name|libraryName
operator|=
literal|null
expr_stmt|;
block|}
block|}
else|else
block|{
name|libraryName
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|libraryName
operator|==
literal|null
condition|)
block|{
throw|throw
name|SqlUtil
operator|.
name|newContextException
argument_list|(
name|this
operator|.
name|type
operator|.
name|getParserPosition
argument_list|()
argument_list|,
name|RESOURCE
operator|.
name|schemaInvalidType
argument_list|(
name|typeName
argument_list|,
name|Arrays
operator|.
name|toString
argument_list|(
name|JsonSchema
operator|.
name|Type
operator|.
name|values
argument_list|()
argument_list|)
argument_list|)
argument_list|)
throw|;
block|}
block|}
else|else
block|{
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|library
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|libraryName
operator|=
operator|(
name|String
operator|)
name|value
argument_list|(
name|library
argument_list|)
expr_stmt|;
block|}
specifier|final
name|SchemaFactory
name|schemaFactory
init|=
name|AvaticaUtils
operator|.
name|instantiatePlugin
argument_list|(
name|SchemaFactory
operator|.
name|class
argument_list|,
name|libraryName
argument_list|)
decl_stmt|;
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|operandMap
init|=
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Pair
argument_list|<
name|SqlIdentifier
argument_list|,
name|SqlNode
argument_list|>
name|option
range|:
name|options
argument_list|(
name|optionList
argument_list|)
control|)
block|{
name|operandMap
operator|.
name|put
argument_list|(
name|option
operator|.
name|left
operator|.
name|getSimple
argument_list|()
argument_list|,
name|value
argument_list|(
name|option
operator|.
name|right
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|subSchema
operator|=
name|schemaFactory
operator|.
name|create
argument_list|(
name|pair
operator|.
name|left
operator|.
name|plus
argument_list|()
argument_list|,
name|pair
operator|.
name|right
argument_list|,
name|operandMap
argument_list|)
expr_stmt|;
name|pair
operator|.
name|left
operator|.
name|add
argument_list|(
name|pair
operator|.
name|right
argument_list|,
name|subSchema
argument_list|)
expr_stmt|;
block|}
comment|/** Returns the value of a literal, converting    * {@link NlsString} into String. */
specifier|private
specifier|static
name|Comparable
name|value
parameter_list|(
name|SqlNode
name|node
parameter_list|)
block|{
specifier|final
name|Comparable
name|v
init|=
name|SqlLiteral
operator|.
name|value
argument_list|(
name|node
argument_list|)
decl_stmt|;
return|return
name|v
operator|instanceof
name|NlsString
condition|?
operator|(
operator|(
name|NlsString
operator|)
name|v
operator|)
operator|.
name|getValue
argument_list|()
else|:
name|v
return|;
block|}
specifier|private
specifier|static
name|List
argument_list|<
name|Pair
argument_list|<
name|SqlIdentifier
argument_list|,
name|SqlNode
argument_list|>
argument_list|>
name|options
parameter_list|(
specifier|final
name|SqlNodeList
name|optionList
parameter_list|)
block|{
return|return
operator|new
name|AbstractList
argument_list|<
name|Pair
argument_list|<
name|SqlIdentifier
argument_list|,
name|SqlNode
argument_list|>
argument_list|>
argument_list|()
block|{
specifier|public
name|Pair
argument_list|<
name|SqlIdentifier
argument_list|,
name|SqlNode
argument_list|>
name|get
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
name|Pair
operator|.
name|of
argument_list|(
operator|(
name|SqlIdentifier
operator|)
name|optionList
operator|.
name|get
argument_list|(
name|index
operator|*
literal|2
argument_list|)
argument_list|,
name|optionList
operator|.
name|get
argument_list|(
name|index
operator|*
literal|2
operator|+
literal|1
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|optionList
operator|.
name|size
argument_list|()
operator|/
literal|2
return|;
block|}
block|}
return|;
block|}
block|}
end_class

begin_comment
comment|// End SqlCreateForeignSchema.java
end_comment

end_unit

