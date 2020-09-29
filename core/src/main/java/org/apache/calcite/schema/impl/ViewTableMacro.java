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
name|schema
operator|.
name|impl
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
name|jdbc
operator|.
name|CalciteConnection
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
name|rel
operator|.
name|type
operator|.
name|RelDataTypeImpl
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
name|FunctionParameter
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
name|Schemas
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
name|TableMacro
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
name|TranslatableTable
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
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Type
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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

begin_comment
comment|/** Table function that implements a view. It returns the operator  * tree of the view's SQL query. */
end_comment

begin_class
specifier|public
class|class
name|ViewTableMacro
implements|implements
name|TableMacro
block|{
specifier|protected
specifier|final
name|String
name|viewSql
decl_stmt|;
specifier|protected
specifier|final
name|CalciteSchema
name|schema
decl_stmt|;
specifier|private
specifier|final
name|Boolean
name|modifiable
decl_stmt|;
comment|/** Typically null. If specified, overrides the path of the schema as the    * context for validating {@code viewSql}. */
specifier|protected
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|schemaPath
decl_stmt|;
specifier|protected
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|viewPath
decl_stmt|;
comment|/**    * Creates a ViewTableMacro.    *    * @param schema     Root schema    * @param viewSql    SQL defining the view    * @param schemaPath Schema path relative to the root schema    * @param viewPath   View path relative to the schema path    * @param modifiable Request that a view is modifiable (dependent on analysis    *                   of {@code viewSql})    */
specifier|public
name|ViewTableMacro
parameter_list|(
name|CalciteSchema
name|schema
parameter_list|,
name|String
name|viewSql
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|schemaPath
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|viewPath
parameter_list|,
name|Boolean
name|modifiable
parameter_list|)
block|{
name|this
operator|.
name|viewSql
operator|=
name|viewSql
expr_stmt|;
name|this
operator|.
name|schema
operator|=
name|schema
expr_stmt|;
name|this
operator|.
name|viewPath
operator|=
name|viewPath
operator|==
literal|null
condition|?
literal|null
else|:
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|viewPath
argument_list|)
expr_stmt|;
name|this
operator|.
name|modifiable
operator|=
name|modifiable
expr_stmt|;
name|this
operator|.
name|schemaPath
operator|=
name|schemaPath
operator|==
literal|null
condition|?
literal|null
else|:
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|schemaPath
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|FunctionParameter
argument_list|>
name|getParameters
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|TranslatableTable
name|apply
parameter_list|(
name|List
argument_list|<
name|Object
argument_list|>
name|arguments
parameter_list|)
block|{
specifier|final
name|CalciteConnection
name|connection
init|=
name|MaterializedViewTable
operator|.
name|MATERIALIZATION_CONNECTION
decl_stmt|;
name|CalcitePrepare
operator|.
name|AnalyzeViewResult
name|parsed
init|=
name|Schemas
operator|.
name|analyzeView
argument_list|(
name|connection
argument_list|,
name|schema
argument_list|,
name|schemaPath
argument_list|,
name|viewSql
argument_list|,
name|viewPath
argument_list|,
name|modifiable
operator|!=
literal|null
operator|&&
name|modifiable
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|schemaPath1
init|=
name|schemaPath
operator|!=
literal|null
condition|?
name|schemaPath
else|:
name|schema
operator|.
name|path
argument_list|(
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
operator|(
name|modifiable
operator|==
literal|null
operator|||
name|modifiable
operator|)
operator|&&
name|parsed
operator|.
name|modifiable
operator|&&
name|parsed
operator|.
name|table
operator|!=
literal|null
condition|)
block|{
return|return
name|modifiableViewTable
argument_list|(
name|parsed
argument_list|,
name|viewSql
argument_list|,
name|schemaPath1
argument_list|,
name|viewPath
argument_list|,
name|schema
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|viewTable
argument_list|(
name|parsed
argument_list|,
name|viewSql
argument_list|,
name|schemaPath1
argument_list|,
name|viewPath
argument_list|)
return|;
block|}
block|}
comment|/** Allows a sub-class to return an extension of {@link ModifiableViewTable}    * by overriding this method. */
specifier|protected
name|ModifiableViewTable
name|modifiableViewTable
parameter_list|(
name|CalcitePrepare
operator|.
name|AnalyzeViewResult
name|parsed
parameter_list|,
name|String
name|viewSql
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|schemaPath
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|viewPath
parameter_list|,
name|CalciteSchema
name|schema
parameter_list|)
block|{
specifier|final
name|JavaTypeFactory
name|typeFactory
init|=
operator|(
name|JavaTypeFactory
operator|)
name|parsed
operator|.
name|typeFactory
decl_stmt|;
specifier|final
name|Type
name|elementType
init|=
name|typeFactory
operator|.
name|getJavaClass
argument_list|(
name|parsed
operator|.
name|rowType
argument_list|)
decl_stmt|;
return|return
operator|new
name|ModifiableViewTable
argument_list|(
name|elementType
argument_list|,
name|RelDataTypeImpl
operator|.
name|proto
argument_list|(
name|parsed
operator|.
name|rowType
argument_list|)
argument_list|,
name|viewSql
argument_list|,
name|schemaPath
argument_list|,
name|viewPath
argument_list|,
name|parsed
operator|.
name|table
argument_list|,
name|Schemas
operator|.
name|path
argument_list|(
name|schema
operator|.
name|root
argument_list|()
argument_list|,
name|parsed
operator|.
name|tablePath
argument_list|)
argument_list|,
name|parsed
operator|.
name|constraint
argument_list|,
name|parsed
operator|.
name|columnMapping
argument_list|)
return|;
block|}
comment|/** Allows a sub-class to return an extension of {@link ViewTable} by    * overriding this method. */
specifier|protected
name|ViewTable
name|viewTable
parameter_list|(
name|CalcitePrepare
operator|.
name|AnalyzeViewResult
name|parsed
parameter_list|,
name|String
name|viewSql
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|schemaPath
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|viewPath
parameter_list|)
block|{
specifier|final
name|JavaTypeFactory
name|typeFactory
init|=
operator|(
name|JavaTypeFactory
operator|)
name|parsed
operator|.
name|typeFactory
decl_stmt|;
specifier|final
name|Type
name|elementType
init|=
name|typeFactory
operator|.
name|getJavaClass
argument_list|(
name|parsed
operator|.
name|rowType
argument_list|)
decl_stmt|;
return|return
operator|new
name|ViewTable
argument_list|(
name|elementType
argument_list|,
name|RelDataTypeImpl
operator|.
name|proto
argument_list|(
name|parsed
operator|.
name|rowType
argument_list|)
argument_list|,
name|viewSql
argument_list|,
name|schemaPath
argument_list|,
name|viewPath
argument_list|)
return|;
block|}
block|}
end_class

end_unit

