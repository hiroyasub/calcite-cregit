begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to you under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|config
package|;
end_package

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|avatica
operator|.
name|ConnectionProperty
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
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
import|;
end_import

begin_import
import|import static
name|net
operator|.
name|hydromatic
operator|.
name|avatica
operator|.
name|ConnectionConfigImpl
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Properties that may be specified on the JDBC connect string.  */
end_comment

begin_enum
specifier|public
enum|enum
name|OptiqConnectionProperty
implements|implements
name|ConnectionProperty
block|{
comment|/** Whether to store query results in temporary tables. */
name|AUTO_TEMP
argument_list|(
literal|"autoTemp"
argument_list|,
name|Type
operator|.
name|BOOLEAN
argument_list|,
literal|false
argument_list|)
block|,
comment|/** Whether Optiq should use materializations. */
name|MATERIALIZATIONS_ENABLED
argument_list|(
literal|"materializationsEnabled"
argument_list|,
name|Type
operator|.
name|BOOLEAN
argument_list|,
literal|true
argument_list|)
block|,
comment|/** Whether Optiq should create materializations. */
name|CREATE_MATERIALIZATIONS
argument_list|(
literal|"createMaterializations"
argument_list|,
name|Type
operator|.
name|BOOLEAN
argument_list|,
literal|true
argument_list|)
block|,
comment|/** URI of the model. */
name|MODEL
argument_list|(
literal|"model"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
literal|null
argument_list|)
block|,
comment|/** Lexical policy. */
name|LEX
argument_list|(
literal|"lex"
argument_list|,
name|Type
operator|.
name|ENUM
argument_list|,
name|Lex
operator|.
name|ORACLE
argument_list|)
block|,
comment|/** How identifiers are quoted.    *  If not specified, value from {@link #LEX} is used. */
name|QUOTING
argument_list|(
literal|"quoting"
argument_list|,
name|Type
operator|.
name|ENUM
argument_list|,
literal|null
argument_list|)
block|,
comment|/** How identifiers are stored if they are quoted.    *  If not specified, value from {@link #LEX} is used. */
name|QUOTED_CASING
argument_list|(
literal|"quotedCasing"
argument_list|,
name|Type
operator|.
name|ENUM
argument_list|,
literal|null
argument_list|)
block|,
comment|/** How identifiers are stored if they are not quoted.    *  If not specified, value from {@link #LEX} is used. */
name|UNQUOTED_CASING
argument_list|(
literal|"unquotedCasing"
argument_list|,
name|Type
operator|.
name|ENUM
argument_list|,
literal|null
argument_list|)
block|,
comment|/** Whether identifiers are matched case-sensitively.    *  If not specified, value from {@link #LEX} is used. */
name|CASE_SENSITIVE
argument_list|(
literal|"caseSensitive"
argument_list|,
name|Type
operator|.
name|BOOLEAN
argument_list|,
literal|null
argument_list|)
block|,
comment|/** Name of initial schema. */
name|SCHEMA
argument_list|(
literal|"schema"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
literal|null
argument_list|)
block|,
comment|/** Specifies whether Spark should be used as the engine for processing that    * cannot be pushed to the source system. If false (the default), Optiq    * generates code that implements the Enumerable interface. */
name|SPARK
argument_list|(
literal|"spark"
argument_list|,
name|Type
operator|.
name|BOOLEAN
argument_list|,
literal|false
argument_list|)
block|,
comment|/** Timezone, for example 'gmt-3'. Default is the JVM's time zone. */
name|TIMEZONE
argument_list|(
literal|"timezone"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
literal|null
argument_list|)
block|;
specifier|private
specifier|final
name|String
name|camelName
decl_stmt|;
specifier|private
specifier|final
name|Type
name|type
decl_stmt|;
specifier|private
specifier|final
name|Object
name|defaultValue
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|OptiqConnectionProperty
argument_list|>
name|NAME_TO_PROPS
decl_stmt|;
static|static
block|{
name|NAME_TO_PROPS
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|OptiqConnectionProperty
argument_list|>
argument_list|()
expr_stmt|;
for|for
control|(
name|OptiqConnectionProperty
name|property
range|:
name|OptiqConnectionProperty
operator|.
name|values
argument_list|()
control|)
block|{
name|NAME_TO_PROPS
operator|.
name|put
argument_list|(
name|property
operator|.
name|camelName
operator|.
name|toUpperCase
argument_list|()
argument_list|,
name|property
argument_list|)
expr_stmt|;
name|NAME_TO_PROPS
operator|.
name|put
argument_list|(
name|property
operator|.
name|name
argument_list|()
argument_list|,
name|property
argument_list|)
expr_stmt|;
block|}
block|}
name|OptiqConnectionProperty
parameter_list|(
name|String
name|camelName
parameter_list|,
name|Type
name|type
parameter_list|,
name|Object
name|defaultValue
parameter_list|)
block|{
name|this
operator|.
name|camelName
operator|=
name|camelName
expr_stmt|;
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
name|this
operator|.
name|defaultValue
operator|=
name|defaultValue
expr_stmt|;
assert|assert
name|defaultValue
operator|==
literal|null
operator|||
name|type
operator|.
name|valid
argument_list|(
name|defaultValue
argument_list|)
assert|;
block|}
specifier|public
name|String
name|camelName
parameter_list|()
block|{
return|return
name|camelName
return|;
block|}
specifier|public
name|Object
name|defaultValue
parameter_list|()
block|{
return|return
name|defaultValue
return|;
block|}
specifier|public
name|Type
name|type
parameter_list|()
block|{
return|return
name|type
return|;
block|}
specifier|public
name|PropEnv
name|wrap
parameter_list|(
name|Properties
name|properties
parameter_list|)
block|{
return|return
operator|new
name|PropEnv
argument_list|(
name|parse
argument_list|(
name|properties
argument_list|,
name|NAME_TO_PROPS
argument_list|)
argument_list|,
name|this
argument_list|)
return|;
block|}
block|}
end_enum

begin_comment
comment|// End OptiqConnectionProperty.java
end_comment

end_unit

