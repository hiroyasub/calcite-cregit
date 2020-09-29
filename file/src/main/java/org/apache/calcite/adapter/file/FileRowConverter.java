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
name|adapter
operator|.
name|file
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
name|java
operator|.
name|JavaTypeFactory
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
name|util
operator|.
name|DateTimeUtils
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
name|rel
operator|.
name|type
operator|.
name|RelDataType
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
name|com
operator|.
name|joestelmach
operator|.
name|natty
operator|.
name|DateGroup
import|;
end_import

begin_import
import|import
name|com
operator|.
name|joestelmach
operator|.
name|natty
operator|.
name|Parser
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jsoup
operator|.
name|nodes
operator|.
name|Element
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jsoup
operator|.
name|select
operator|.
name|Elements
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|NumberFormat
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|ParseException
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
name|HashSet
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
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Matcher
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
import|;
end_import

begin_comment
comment|/**  * FileRowConverter.  */
end_comment

begin_class
class|class
name|FileRowConverter
block|{
comment|// cache for lazy initialization
specifier|private
specifier|final
name|FileReader
name|fileReader
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|fieldConfigs
decl_stmt|;
specifier|private
name|boolean
name|initialized
init|=
literal|false
decl_stmt|;
comment|// row parser configuration
specifier|private
specifier|final
name|List
argument_list|<
name|FieldDef
argument_list|>
name|fields
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
comment|/** Format for parsing numbers. Not thread-safe, but we assume that only    * one thread uses this converter at a time. */
specifier|private
specifier|final
name|NumberFormat
name|numberFormat
init|=
name|NumberFormat
operator|.
name|getInstance
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
decl_stmt|;
comment|/** Format for parsing integers. Not thread-safe, but we assume that only    * one thread uses this converter at a time. */
specifier|private
specifier|final
name|NumberFormat
name|integerFormat
init|=
name|NumberFormat
operator|.
name|getIntegerInstance
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
decl_stmt|;
comment|/** Creates a FileRowConverter. */
name|FileRowConverter
parameter_list|(
name|FileReader
name|fileReader
parameter_list|,
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|fieldConfigs
parameter_list|)
block|{
name|this
operator|.
name|fileReader
operator|=
name|fileReader
expr_stmt|;
name|this
operator|.
name|fieldConfigs
operator|=
name|fieldConfigs
expr_stmt|;
block|}
comment|// initialize() - combine HTML table header information with field definitions
comment|//      to initialize the table reader
comment|// NB:  object initialization is deferred to avoid unnecessary URL reads
specifier|private
name|void
name|initialize
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|initialized
condition|)
block|{
return|return;
block|}
try|try
block|{
specifier|final
name|Elements
name|headerElements
init|=
name|this
operator|.
name|fileReader
operator|.
name|getHeadings
argument_list|()
decl_stmt|;
comment|// create a name to index map for HTML table elements
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|headerMap
init|=
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Element
name|th
range|:
name|headerElements
control|)
block|{
name|String
name|heading
init|=
name|th
operator|.
name|text
argument_list|()
decl_stmt|;
if|if
condition|(
name|headerMap
operator|.
name|containsKey
argument_list|(
name|heading
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|Exception
argument_list|(
literal|"duplicate heading: '"
operator|+
name|heading
operator|+
literal|"'"
argument_list|)
throw|;
block|}
name|headerMap
operator|.
name|put
argument_list|(
name|heading
argument_list|,
name|i
operator|++
argument_list|)
expr_stmt|;
block|}
comment|// instantiate the field definitions
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|colNames
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|sources
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
if|if
condition|(
name|this
operator|.
name|fieldConfigs
operator|!=
literal|null
condition|)
block|{
try|try
block|{
for|for
control|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|fieldConfig
range|:
name|this
operator|.
name|fieldConfigs
control|)
block|{
name|String
name|thName
init|=
operator|(
name|String
operator|)
name|fieldConfig
operator|.
name|get
argument_list|(
literal|"th"
argument_list|)
decl_stmt|;
name|String
name|name
init|=
name|thName
decl_stmt|;
name|String
name|newName
decl_stmt|;
name|FileFieldType
name|type
init|=
literal|null
decl_stmt|;
name|boolean
name|skip
init|=
literal|false
decl_stmt|;
if|if
condition|(
operator|!
name|headerMap
operator|.
name|containsKey
argument_list|(
name|thName
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|Exception
argument_list|(
literal|"bad source column name: '"
operator|+
name|thName
operator|+
literal|"'"
argument_list|)
throw|;
block|}
if|if
condition|(
operator|(
name|newName
operator|=
operator|(
name|String
operator|)
name|fieldConfig
operator|.
name|get
argument_list|(
literal|"name"
argument_list|)
operator|)
operator|!=
literal|null
condition|)
block|{
name|name
operator|=
name|newName
expr_stmt|;
block|}
if|if
condition|(
name|colNames
operator|.
name|contains
argument_list|(
name|name
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|Exception
argument_list|(
literal|"duplicate column name: '"
operator|+
name|name
operator|+
literal|"'"
argument_list|)
throw|;
block|}
name|String
name|typeString
init|=
operator|(
name|String
operator|)
name|fieldConfig
operator|.
name|get
argument_list|(
literal|"type"
argument_list|)
decl_stmt|;
if|if
condition|(
name|typeString
operator|!=
literal|null
condition|)
block|{
name|type
operator|=
name|FileFieldType
operator|.
name|of
argument_list|(
name|typeString
argument_list|)
expr_stmt|;
block|}
name|String
name|sSkip
init|=
operator|(
name|String
operator|)
name|fieldConfig
operator|.
name|get
argument_list|(
literal|"skip"
argument_list|)
decl_stmt|;
if|if
condition|(
name|sSkip
operator|!=
literal|null
condition|)
block|{
name|skip
operator|=
name|Boolean
operator|.
name|parseBoolean
argument_list|(
name|sSkip
argument_list|)
expr_stmt|;
block|}
name|Integer
name|sourceIx
init|=
name|headerMap
operator|.
name|get
argument_list|(
name|thName
argument_list|)
decl_stmt|;
name|colNames
operator|.
name|add
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|sources
operator|.
name|add
argument_list|(
name|thName
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|skip
condition|)
block|{
name|addFieldDef
argument_list|(
name|name
argument_list|,
name|type
argument_list|,
name|fieldConfig
argument_list|,
name|sourceIx
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|e
parameter_list|)
block|{
throw|throw
name|e
throw|;
block|}
catch|catch
parameter_list|(
name|Exception
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
block|}
comment|// pick up any data elements not explicitly defined
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|e
range|:
name|headerMap
operator|.
name|entrySet
argument_list|()
control|)
block|{
specifier|final
name|String
name|name
init|=
name|e
operator|.
name|getKey
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|sources
operator|.
name|contains
argument_list|(
name|name
argument_list|)
operator|&&
operator|!
name|colNames
operator|.
name|contains
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|addFieldDef
argument_list|(
name|name
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|e
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|e
parameter_list|)
block|{
throw|throw
name|e
throw|;
block|}
catch|catch
parameter_list|(
name|Exception
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
name|this
operator|.
name|initialized
operator|=
literal|true
expr_stmt|;
block|}
comment|// add another field definition to the FileRowConverter during initialization
specifier|private
name|void
name|addFieldDef
parameter_list|(
name|String
name|name
parameter_list|,
name|FileFieldType
name|type
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|config
parameter_list|,
name|int
name|sourceCol
parameter_list|)
block|{
name|this
operator|.
name|fields
operator|.
name|add
argument_list|(
operator|new
name|FieldDef
argument_list|(
name|name
argument_list|,
name|type
argument_list|,
name|config
argument_list|,
name|sourceCol
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Converts a row of JSoup Elements to an array of java objects. */
name|Object
name|toRow
parameter_list|(
name|Elements
name|rowElements
parameter_list|,
name|int
index|[]
name|projection
parameter_list|)
block|{
name|initialize
argument_list|()
expr_stmt|;
specifier|final
name|Object
index|[]
name|objects
init|=
operator|new
name|Object
index|[
name|projection
operator|.
name|length
index|]
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
name|projection
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|int
name|field
init|=
name|projection
index|[
name|i
index|]
decl_stmt|;
name|objects
index|[
name|i
index|]
operator|=
name|this
operator|.
name|fields
operator|.
name|get
argument_list|(
name|field
argument_list|)
operator|.
name|convert
argument_list|(
name|rowElements
argument_list|)
expr_stmt|;
block|}
return|return
name|objects
return|;
block|}
name|int
name|width
parameter_list|()
block|{
name|initialize
argument_list|()
expr_stmt|;
return|return
name|this
operator|.
name|fields
operator|.
name|size
argument_list|()
return|;
block|}
name|RelDataType
name|getRowType
parameter_list|(
name|JavaTypeFactory
name|typeFactory
parameter_list|)
block|{
name|initialize
argument_list|()
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|names
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|RelDataType
argument_list|>
name|types
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
comment|// iterate through FieldDefs, populating names and types
for|for
control|(
name|FieldDef
name|f
range|:
name|this
operator|.
name|fields
control|)
block|{
name|names
operator|.
name|add
argument_list|(
name|f
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|FileFieldType
name|fieldType
init|=
name|f
operator|.
name|getType
argument_list|()
decl_stmt|;
name|RelDataType
name|type
decl_stmt|;
if|if
condition|(
name|fieldType
operator|==
literal|null
condition|)
block|{
name|type
operator|=
name|typeFactory
operator|.
name|createJavaType
argument_list|(
name|String
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|type
operator|=
name|fieldType
operator|.
name|toType
argument_list|(
name|typeFactory
argument_list|)
expr_stmt|;
block|}
name|types
operator|.
name|add
argument_list|(
name|type
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|names
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|names
operator|.
name|add
argument_list|(
literal|"line"
argument_list|)
expr_stmt|;
name|types
operator|.
name|add
argument_list|(
name|typeFactory
operator|.
name|createJavaType
argument_list|(
name|String
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|typeFactory
operator|.
name|createStructType
argument_list|(
name|Pair
operator|.
name|zip
argument_list|(
name|names
argument_list|,
name|types
argument_list|)
argument_list|)
return|;
block|}
comment|/** Parses an an HTML table cell. */
specifier|private
specifier|static
class|class
name|CellReader
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
specifier|private
name|String
name|type
decl_stmt|;
specifier|private
name|String
name|selector
decl_stmt|;
specifier|private
name|Integer
name|selectedElement
decl_stmt|;
specifier|private
name|String
name|replaceText
decl_stmt|;
specifier|private
name|Pattern
name|replacePattern
decl_stmt|;
specifier|private
name|String
name|replaceWith
decl_stmt|;
specifier|private
name|String
name|matchText
decl_stmt|;
specifier|private
name|Pattern
name|matchPattern
decl_stmt|;
specifier|private
name|Integer
name|matchSeq
decl_stmt|;
name|CellReader
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|config
parameter_list|)
block|{
if|if
condition|(
name|config
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|type
operator|=
operator|(
name|String
operator|)
name|config
operator|.
name|get
argument_list|(
literal|"type"
argument_list|)
expr_stmt|;
name|this
operator|.
name|selector
operator|=
operator|(
name|String
operator|)
name|config
operator|.
name|get
argument_list|(
literal|"selector"
argument_list|)
expr_stmt|;
name|this
operator|.
name|selectedElement
operator|=
operator|(
name|Integer
operator|)
name|config
operator|.
name|get
argument_list|(
literal|"selectedElement"
argument_list|)
expr_stmt|;
name|this
operator|.
name|replaceText
operator|=
operator|(
name|String
operator|)
name|config
operator|.
name|get
argument_list|(
literal|"replace"
argument_list|)
expr_stmt|;
name|this
operator|.
name|replaceWith
operator|=
operator|(
name|String
operator|)
name|config
operator|.
name|get
argument_list|(
literal|"replaceWith"
argument_list|)
expr_stmt|;
name|this
operator|.
name|matchText
operator|=
operator|(
name|String
operator|)
name|config
operator|.
name|get
argument_list|(
literal|"match"
argument_list|)
expr_stmt|;
name|this
operator|.
name|matchSeq
operator|=
operator|(
name|Integer
operator|)
name|config
operator|.
name|get
argument_list|(
literal|"matchSeq"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|this
operator|.
name|selector
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|selector
operator|=
literal|"*"
expr_stmt|;
block|}
if|if
condition|(
name|this
operator|.
name|replaceText
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|replacePattern
operator|=
name|Pattern
operator|.
name|compile
argument_list|(
name|this
operator|.
name|replaceText
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|this
operator|.
name|replaceWith
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|replaceWith
operator|=
literal|""
expr_stmt|;
block|}
if|if
condition|(
name|this
operator|.
name|matchText
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|matchPattern
operator|=
name|Pattern
operator|.
name|compile
argument_list|(
name|this
operator|.
name|matchText
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|this
operator|.
name|matchSeq
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|matchSeq
operator|=
literal|0
expr_stmt|;
block|}
block|}
name|String
name|read
parameter_list|(
name|Element
name|cell
parameter_list|)
block|{
name|ArrayList
argument_list|<
name|String
argument_list|>
name|cellText
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
if|if
condition|(
name|this
operator|.
name|selectedElement
operator|!=
literal|null
condition|)
block|{
name|cellText
operator|.
name|add
argument_list|(
name|cell
operator|.
name|select
argument_list|(
name|this
operator|.
name|selector
argument_list|)
operator|.
name|get
argument_list|(
name|this
operator|.
name|selectedElement
argument_list|)
operator|.
name|ownText
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
for|for
control|(
name|Element
name|child
range|:
name|cell
operator|.
name|select
argument_list|(
name|this
operator|.
name|selector
argument_list|)
control|)
block|{
comment|//String tagName = child.tag().getName();
name|cellText
operator|.
name|add
argument_list|(
name|child
operator|.
name|ownText
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|String
name|cellString
init|=
name|String
operator|.
name|join
argument_list|(
literal|" "
argument_list|,
name|cellText
argument_list|)
operator|.
name|trim
argument_list|()
decl_stmt|;
comment|// replace
if|if
condition|(
name|this
operator|.
name|replacePattern
operator|!=
literal|null
condition|)
block|{
name|Matcher
name|m
init|=
name|this
operator|.
name|replacePattern
operator|.
name|matcher
argument_list|(
name|cellString
argument_list|)
decl_stmt|;
name|cellString
operator|=
name|m
operator|.
name|replaceAll
argument_list|(
name|this
operator|.
name|replaceWith
argument_list|)
expr_stmt|;
block|}
comment|// match
if|if
condition|(
name|this
operator|.
name|matchPattern
operator|==
literal|null
condition|)
block|{
return|return
name|cellString
return|;
block|}
else|else
block|{
name|List
argument_list|<
name|String
argument_list|>
name|allMatches
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|Matcher
name|m
init|=
name|this
operator|.
name|matchPattern
operator|.
name|matcher
argument_list|(
name|cellString
argument_list|)
decl_stmt|;
while|while
condition|(
name|m
operator|.
name|find
argument_list|()
condition|)
block|{
name|allMatches
operator|.
name|add
argument_list|(
name|m
operator|.
name|group
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|allMatches
operator|.
name|size
argument_list|()
operator|!=
literal|0
condition|)
block|{
return|return
name|allMatches
operator|.
name|get
argument_list|(
name|this
operator|.
name|matchSeq
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
block|}
block|}
comment|/** Responsible for managing field (column) definition,    * and for converting an Element to a java data type. */
specifier|private
class|class
name|FieldDef
block|{
name|String
name|name
decl_stmt|;
name|FileFieldType
name|type
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|config
decl_stmt|;
name|CellReader
name|cellReader
decl_stmt|;
name|int
name|cellSeq
decl_stmt|;
name|FieldDef
parameter_list|(
name|String
name|name
parameter_list|,
name|FileFieldType
name|type
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|config
parameter_list|,
name|int
name|cellSeq
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
name|this
operator|.
name|config
operator|=
name|config
expr_stmt|;
name|this
operator|.
name|cellReader
operator|=
operator|new
name|CellReader
argument_list|(
name|config
argument_list|)
expr_stmt|;
name|this
operator|.
name|cellSeq
operator|=
name|cellSeq
expr_stmt|;
block|}
name|Object
name|convert
parameter_list|(
name|Elements
name|row
parameter_list|)
block|{
return|return
name|toObject
argument_list|(
name|this
operator|.
name|type
argument_list|,
name|this
operator|.
name|cellReader
operator|.
name|read
argument_list|(
name|row
operator|.
name|get
argument_list|(
name|this
operator|.
name|cellSeq
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|this
operator|.
name|name
return|;
block|}
name|FileFieldType
name|getType
parameter_list|()
block|{
return|return
name|this
operator|.
name|type
return|;
block|}
specifier|private
name|java
operator|.
name|util
operator|.
name|Date
name|parseDate
parameter_list|(
name|String
name|string
parameter_list|)
block|{
name|Parser
name|parser
init|=
operator|new
name|Parser
argument_list|(
name|DateTimeUtils
operator|.
name|UTC_ZONE
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|DateGroup
argument_list|>
name|groups
init|=
name|parser
operator|.
name|parse
argument_list|(
name|string
argument_list|)
decl_stmt|;
name|DateGroup
name|group
init|=
name|groups
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
return|return
name|group
operator|.
name|getDates
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
return|;
block|}
specifier|private
name|Object
name|toObject
parameter_list|(
name|FileFieldType
name|fieldType
parameter_list|,
name|String
name|string
parameter_list|)
block|{
if|if
condition|(
operator|(
name|string
operator|==
literal|null
operator|)
operator|||
operator|(
name|string
operator|.
name|length
argument_list|()
operator|==
literal|0
operator|)
condition|)
block|{
return|return
literal|null
return|;
block|}
if|if
condition|(
name|fieldType
operator|==
literal|null
condition|)
block|{
return|return
name|string
return|;
block|}
switch|switch
condition|(
name|fieldType
condition|)
block|{
default|default:
case|case
name|STRING
case|:
return|return
name|string
return|;
case|case
name|BOOLEAN
case|:
return|return
name|Boolean
operator|.
name|parseBoolean
argument_list|(
name|string
argument_list|)
return|;
case|case
name|BYTE
case|:
return|return
name|Byte
operator|.
name|parseByte
argument_list|(
name|string
argument_list|)
return|;
case|case
name|SHORT
case|:
try|try
block|{
return|return
name|integerFormat
operator|.
name|parse
argument_list|(
name|string
argument_list|)
operator|.
name|shortValue
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|ParseException
name|e
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
case|case
name|INT
case|:
try|try
block|{
return|return
name|integerFormat
operator|.
name|parse
argument_list|(
name|string
argument_list|)
operator|.
name|intValue
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|ParseException
name|e
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
case|case
name|LONG
case|:
try|try
block|{
return|return
name|numberFormat
operator|.
name|parse
argument_list|(
name|string
argument_list|)
operator|.
name|longValue
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|ParseException
name|e
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
case|case
name|FLOAT
case|:
try|try
block|{
return|return
name|numberFormat
operator|.
name|parse
argument_list|(
name|string
argument_list|)
operator|.
name|floatValue
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|ParseException
name|e
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
case|case
name|DOUBLE
case|:
try|try
block|{
return|return
name|numberFormat
operator|.
name|parse
argument_list|(
name|string
argument_list|)
operator|.
name|doubleValue
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|ParseException
name|e
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
case|case
name|DATE
case|:
return|return
operator|new
name|java
operator|.
name|sql
operator|.
name|Date
argument_list|(
name|parseDate
argument_list|(
name|string
argument_list|)
operator|.
name|getTime
argument_list|()
argument_list|)
return|;
case|case
name|TIME
case|:
return|return
operator|new
name|java
operator|.
name|sql
operator|.
name|Time
argument_list|(
name|parseDate
argument_list|(
name|string
argument_list|)
operator|.
name|getTime
argument_list|()
argument_list|)
return|;
case|case
name|TIMESTAMP
case|:
return|return
operator|new
name|java
operator|.
name|sql
operator|.
name|Timestamp
argument_list|(
name|parseDate
argument_list|(
name|string
argument_list|)
operator|.
name|getTime
argument_list|()
argument_list|)
return|;
block|}
block|}
block|}
block|}
end_class

end_unit

