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
name|config
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
name|avatica
operator|.
name|ConnectionConfigImpl
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
name|Casing
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
name|Quoting
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
name|runtime
operator|.
name|ConsList
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
name|SqlOperatorTable
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
name|fun
operator|.
name|SqlLibrary
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
name|fun
operator|.
name|SqlLibraryOperatorTableFactory
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
name|validate
operator|.
name|SqlConformance
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
name|validate
operator|.
name|SqlConformanceEnum
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
name|Properties
import|;
end_import

begin_comment
comment|/** Implementation of {@link CalciteConnectionConfig}. */
end_comment

begin_class
specifier|public
class|class
name|CalciteConnectionConfigImpl
extends|extends
name|ConnectionConfigImpl
implements|implements
name|CalciteConnectionConfig
block|{
specifier|public
name|CalciteConnectionConfigImpl
parameter_list|(
name|Properties
name|properties
parameter_list|)
block|{
name|super
argument_list|(
name|properties
argument_list|)
expr_stmt|;
block|}
comment|/** Returns a copy of this configuration with one property changed. */
specifier|public
name|CalciteConnectionConfigImpl
name|set
parameter_list|(
name|CalciteConnectionProperty
name|property
parameter_list|,
name|String
name|value
parameter_list|)
block|{
specifier|final
name|Properties
name|properties1
init|=
operator|new
name|Properties
argument_list|(
name|properties
argument_list|)
decl_stmt|;
name|properties1
operator|.
name|setProperty
argument_list|(
name|property
operator|.
name|camelName
argument_list|()
argument_list|,
name|value
argument_list|)
expr_stmt|;
return|return
operator|new
name|CalciteConnectionConfigImpl
argument_list|(
name|properties1
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|approximateDistinctCount
parameter_list|()
block|{
return|return
name|CalciteConnectionProperty
operator|.
name|APPROXIMATE_DISTINCT_COUNT
operator|.
name|wrap
argument_list|(
name|properties
argument_list|)
operator|.
name|getBoolean
argument_list|()
return|;
block|}
specifier|public
name|boolean
name|approximateTopN
parameter_list|()
block|{
return|return
name|CalciteConnectionProperty
operator|.
name|APPROXIMATE_TOP_N
operator|.
name|wrap
argument_list|(
name|properties
argument_list|)
operator|.
name|getBoolean
argument_list|()
return|;
block|}
specifier|public
name|boolean
name|approximateDecimal
parameter_list|()
block|{
return|return
name|CalciteConnectionProperty
operator|.
name|APPROXIMATE_DECIMAL
operator|.
name|wrap
argument_list|(
name|properties
argument_list|)
operator|.
name|getBoolean
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|nullEqualToEmpty
parameter_list|()
block|{
return|return
name|CalciteConnectionProperty
operator|.
name|NULL_EQUAL_TO_EMPTY
operator|.
name|wrap
argument_list|(
name|properties
argument_list|)
operator|.
name|getBoolean
argument_list|()
return|;
block|}
specifier|public
name|boolean
name|autoTemp
parameter_list|()
block|{
return|return
name|CalciteConnectionProperty
operator|.
name|AUTO_TEMP
operator|.
name|wrap
argument_list|(
name|properties
argument_list|)
operator|.
name|getBoolean
argument_list|()
return|;
block|}
specifier|public
name|boolean
name|materializationsEnabled
parameter_list|()
block|{
return|return
name|CalciteConnectionProperty
operator|.
name|MATERIALIZATIONS_ENABLED
operator|.
name|wrap
argument_list|(
name|properties
argument_list|)
operator|.
name|getBoolean
argument_list|()
return|;
block|}
specifier|public
name|boolean
name|createMaterializations
parameter_list|()
block|{
return|return
name|CalciteConnectionProperty
operator|.
name|CREATE_MATERIALIZATIONS
operator|.
name|wrap
argument_list|(
name|properties
argument_list|)
operator|.
name|getBoolean
argument_list|()
return|;
block|}
specifier|public
name|NullCollation
name|defaultNullCollation
parameter_list|()
block|{
return|return
name|CalciteConnectionProperty
operator|.
name|DEFAULT_NULL_COLLATION
operator|.
name|wrap
argument_list|(
name|properties
argument_list|)
operator|.
name|getEnum
argument_list|(
name|NullCollation
operator|.
name|class
argument_list|,
name|NullCollation
operator|.
name|HIGH
argument_list|)
return|;
block|}
specifier|public
parameter_list|<
name|T
parameter_list|>
name|T
name|fun
parameter_list|(
name|Class
argument_list|<
name|T
argument_list|>
name|operatorTableClass
parameter_list|,
name|T
name|defaultOperatorTable
parameter_list|)
block|{
specifier|final
name|String
name|fun
init|=
name|CalciteConnectionProperty
operator|.
name|FUN
operator|.
name|wrap
argument_list|(
name|properties
argument_list|)
operator|.
name|getString
argument_list|()
decl_stmt|;
if|if
condition|(
name|fun
operator|==
literal|null
operator|||
name|fun
operator|.
name|equals
argument_list|(
literal|""
argument_list|)
operator|||
name|fun
operator|.
name|equals
argument_list|(
literal|"standard"
argument_list|)
condition|)
block|{
return|return
name|defaultOperatorTable
return|;
block|}
specifier|final
name|List
argument_list|<
name|SqlLibrary
argument_list|>
name|libraryList
init|=
name|SqlLibrary
operator|.
name|parse
argument_list|(
name|fun
argument_list|)
decl_stmt|;
specifier|final
name|SqlOperatorTable
name|operatorTable
init|=
name|SqlLibraryOperatorTableFactory
operator|.
name|INSTANCE
operator|.
name|getOperatorTable
argument_list|(
name|ConsList
operator|.
name|of
argument_list|(
name|SqlLibrary
operator|.
name|STANDARD
argument_list|,
name|libraryList
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|operatorTableClass
operator|.
name|cast
argument_list|(
name|operatorTable
argument_list|)
return|;
block|}
specifier|public
name|String
name|model
parameter_list|()
block|{
return|return
name|CalciteConnectionProperty
operator|.
name|MODEL
operator|.
name|wrap
argument_list|(
name|properties
argument_list|)
operator|.
name|getString
argument_list|()
return|;
block|}
specifier|public
name|Lex
name|lex
parameter_list|()
block|{
return|return
name|CalciteConnectionProperty
operator|.
name|LEX
operator|.
name|wrap
argument_list|(
name|properties
argument_list|)
operator|.
name|getEnum
argument_list|(
name|Lex
operator|.
name|class
argument_list|)
return|;
block|}
specifier|public
name|Quoting
name|quoting
parameter_list|()
block|{
return|return
name|CalciteConnectionProperty
operator|.
name|QUOTING
operator|.
name|wrap
argument_list|(
name|properties
argument_list|)
operator|.
name|getEnum
argument_list|(
name|Quoting
operator|.
name|class
argument_list|,
name|lex
argument_list|()
operator|.
name|quoting
argument_list|)
return|;
block|}
specifier|public
name|Casing
name|unquotedCasing
parameter_list|()
block|{
return|return
name|CalciteConnectionProperty
operator|.
name|UNQUOTED_CASING
operator|.
name|wrap
argument_list|(
name|properties
argument_list|)
operator|.
name|getEnum
argument_list|(
name|Casing
operator|.
name|class
argument_list|,
name|lex
argument_list|()
operator|.
name|unquotedCasing
argument_list|)
return|;
block|}
specifier|public
name|Casing
name|quotedCasing
parameter_list|()
block|{
return|return
name|CalciteConnectionProperty
operator|.
name|QUOTED_CASING
operator|.
name|wrap
argument_list|(
name|properties
argument_list|)
operator|.
name|getEnum
argument_list|(
name|Casing
operator|.
name|class
argument_list|,
name|lex
argument_list|()
operator|.
name|quotedCasing
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|caseSensitive
parameter_list|()
block|{
return|return
name|CalciteConnectionProperty
operator|.
name|CASE_SENSITIVE
operator|.
name|wrap
argument_list|(
name|properties
argument_list|)
operator|.
name|getBoolean
argument_list|(
name|lex
argument_list|()
operator|.
name|caseSensitive
argument_list|)
return|;
block|}
specifier|public
parameter_list|<
name|T
parameter_list|>
name|T
name|parserFactory
parameter_list|(
name|Class
argument_list|<
name|T
argument_list|>
name|parserFactoryClass
parameter_list|,
name|T
name|defaultParserFactory
parameter_list|)
block|{
return|return
name|CalciteConnectionProperty
operator|.
name|PARSER_FACTORY
operator|.
name|wrap
argument_list|(
name|properties
argument_list|)
operator|.
name|getPlugin
argument_list|(
name|parserFactoryClass
argument_list|,
name|defaultParserFactory
argument_list|)
return|;
block|}
specifier|public
parameter_list|<
name|T
parameter_list|>
name|T
name|schemaFactory
parameter_list|(
name|Class
argument_list|<
name|T
argument_list|>
name|schemaFactoryClass
parameter_list|,
name|T
name|defaultSchemaFactory
parameter_list|)
block|{
return|return
name|CalciteConnectionProperty
operator|.
name|SCHEMA_FACTORY
operator|.
name|wrap
argument_list|(
name|properties
argument_list|)
operator|.
name|getPlugin
argument_list|(
name|schemaFactoryClass
argument_list|,
name|defaultSchemaFactory
argument_list|)
return|;
block|}
specifier|public
name|JsonSchema
operator|.
name|Type
name|schemaType
parameter_list|()
block|{
return|return
name|CalciteConnectionProperty
operator|.
name|SCHEMA_TYPE
operator|.
name|wrap
argument_list|(
name|properties
argument_list|)
operator|.
name|getEnum
argument_list|(
name|JsonSchema
operator|.
name|Type
operator|.
name|class
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|spark
parameter_list|()
block|{
return|return
name|CalciteConnectionProperty
operator|.
name|SPARK
operator|.
name|wrap
argument_list|(
name|properties
argument_list|)
operator|.
name|getBoolean
argument_list|()
return|;
block|}
specifier|public
name|boolean
name|forceDecorrelate
parameter_list|()
block|{
return|return
name|CalciteConnectionProperty
operator|.
name|FORCE_DECORRELATE
operator|.
name|wrap
argument_list|(
name|properties
argument_list|)
operator|.
name|getBoolean
argument_list|()
return|;
block|}
specifier|public
parameter_list|<
name|T
parameter_list|>
name|T
name|typeSystem
parameter_list|(
name|Class
argument_list|<
name|T
argument_list|>
name|typeSystemClass
parameter_list|,
name|T
name|defaultTypeSystem
parameter_list|)
block|{
return|return
name|CalciteConnectionProperty
operator|.
name|TYPE_SYSTEM
operator|.
name|wrap
argument_list|(
name|properties
argument_list|)
operator|.
name|getPlugin
argument_list|(
name|typeSystemClass
argument_list|,
name|defaultTypeSystem
argument_list|)
return|;
block|}
specifier|public
name|SqlConformance
name|conformance
parameter_list|()
block|{
return|return
name|CalciteConnectionProperty
operator|.
name|CONFORMANCE
operator|.
name|wrap
argument_list|(
name|properties
argument_list|)
operator|.
name|getEnum
argument_list|(
name|SqlConformanceEnum
operator|.
name|class
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|timeZone
parameter_list|()
block|{
return|return
name|CalciteConnectionProperty
operator|.
name|TIME_ZONE
operator|.
name|wrap
argument_list|(
name|properties
argument_list|)
operator|.
name|getString
argument_list|()
return|;
block|}
block|}
end_class

begin_comment
comment|// End CalciteConnectionConfigImpl.java
end_comment

end_unit

