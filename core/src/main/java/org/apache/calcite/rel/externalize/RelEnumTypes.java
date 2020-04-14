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
name|rel
operator|.
name|externalize
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
name|util
operator|.
name|TimeUnitRange
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
name|core
operator|.
name|TableModify
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
name|JoinConditionType
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
name|JoinType
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
name|SqlExplain
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
name|SqlExplainFormat
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
name|SqlExplainLevel
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
name|SqlInsertKeyword
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
name|SqlJsonConstructorNullClause
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
name|SqlJsonQueryWrapperBehavior
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
name|SqlJsonValueEmptyOrErrorBehavior
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
name|SqlMatchRecognize
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
name|SqlSelectKeyword
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
name|SqlTrimFunction
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

begin_comment
comment|/** Registry of {@link Enum} classes that can be serialized to JSON.  *  *<p>Suppose you want to serialize the value  * {@link SqlTrimFunction.Flag#LEADING} to JSON.  * First, make sure that {@link SqlTrimFunction.Flag} is registered.  * The type will be serialized as "SYMBOL".  * The value will be serialized as the string "LEADING".  *  *<p>When we deserialize, we rely on the fact that the registered  * {@code enum} classes have distinct values. Therefore, knowing that  * {@code (type="SYMBOL", value="LEADING")} we can convert the string "LEADING"  * to the enum {@code Flag.LEADING}. */
end_comment

begin_class
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"rawtypes"
block|,
literal|"unchecked"
block|}
argument_list|)
specifier|public
specifier|abstract
class|class
name|RelEnumTypes
block|{
specifier|private
name|RelEnumTypes
parameter_list|()
block|{
block|}
specifier|private
specifier|static
specifier|final
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|Enum
argument_list|<
name|?
argument_list|>
argument_list|>
name|ENUM_BY_NAME
decl_stmt|;
static|static
block|{
comment|// Build a mapping from enum constants (e.g. LEADING) to the enum
comment|// that contains them (e.g. SqlTrimFunction.Flag). If there two
comment|// enum constants have the same name, the builder will throw.
specifier|final
name|ImmutableMap
operator|.
name|Builder
argument_list|<
name|String
argument_list|,
name|Enum
argument_list|<
name|?
argument_list|>
argument_list|>
name|enumByName
init|=
name|ImmutableMap
operator|.
name|builder
argument_list|()
decl_stmt|;
name|register
argument_list|(
name|enumByName
argument_list|,
name|JoinConditionType
operator|.
name|class
argument_list|)
expr_stmt|;
name|register
argument_list|(
name|enumByName
argument_list|,
name|JoinType
operator|.
name|class
argument_list|)
expr_stmt|;
name|register
argument_list|(
name|enumByName
argument_list|,
name|SqlExplain
operator|.
name|Depth
operator|.
name|class
argument_list|)
expr_stmt|;
name|register
argument_list|(
name|enumByName
argument_list|,
name|SqlExplainFormat
operator|.
name|class
argument_list|)
expr_stmt|;
name|register
argument_list|(
name|enumByName
argument_list|,
name|SqlExplainLevel
operator|.
name|class
argument_list|)
expr_stmt|;
name|register
argument_list|(
name|enumByName
argument_list|,
name|SqlInsertKeyword
operator|.
name|class
argument_list|)
expr_stmt|;
name|register
argument_list|(
name|enumByName
argument_list|,
name|SqlJsonConstructorNullClause
operator|.
name|class
argument_list|)
expr_stmt|;
name|register
argument_list|(
name|enumByName
argument_list|,
name|SqlJsonQueryWrapperBehavior
operator|.
name|class
argument_list|)
expr_stmt|;
name|register
argument_list|(
name|enumByName
argument_list|,
name|SqlJsonValueEmptyOrErrorBehavior
operator|.
name|class
argument_list|)
expr_stmt|;
name|register
argument_list|(
name|enumByName
argument_list|,
name|SqlMatchRecognize
operator|.
name|AfterOption
operator|.
name|class
argument_list|)
expr_stmt|;
name|register
argument_list|(
name|enumByName
argument_list|,
name|SqlSelectKeyword
operator|.
name|class
argument_list|)
expr_stmt|;
name|register
argument_list|(
name|enumByName
argument_list|,
name|SqlTrimFunction
operator|.
name|Flag
operator|.
name|class
argument_list|)
expr_stmt|;
name|register
argument_list|(
name|enumByName
argument_list|,
name|TimeUnitRange
operator|.
name|class
argument_list|)
expr_stmt|;
name|register
argument_list|(
name|enumByName
argument_list|,
name|TableModify
operator|.
name|Operation
operator|.
name|class
argument_list|)
expr_stmt|;
name|ENUM_BY_NAME
operator|=
name|enumByName
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|register
parameter_list|(
name|ImmutableMap
operator|.
name|Builder
argument_list|<
name|String
argument_list|,
name|Enum
argument_list|<
name|?
argument_list|>
argument_list|>
name|builder
parameter_list|,
name|Class
argument_list|<
name|?
extends|extends
name|Enum
argument_list|>
name|aClass
parameter_list|)
block|{
for|for
control|(
name|Enum
name|enumConstant
range|:
name|aClass
operator|.
name|getEnumConstants
argument_list|()
control|)
block|{
name|builder
operator|.
name|put
argument_list|(
name|enumConstant
operator|.
name|name
argument_list|()
argument_list|,
name|enumConstant
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Converts a literal into a value that can be serialized to JSON.    * In particular, if is an enum, converts it to its name. */
specifier|public
specifier|static
name|Object
name|fromEnum
parameter_list|(
name|Object
name|value
parameter_list|)
block|{
return|return
name|value
operator|instanceof
name|Enum
condition|?
name|fromEnum
argument_list|(
operator|(
name|Enum
operator|)
name|value
argument_list|)
else|:
name|value
return|;
block|}
comment|/** Converts an enum into its name.    * Throws if the enum's class is not registered. */
specifier|public
specifier|static
name|String
name|fromEnum
parameter_list|(
name|Enum
name|enumValue
parameter_list|)
block|{
if|if
condition|(
name|ENUM_BY_NAME
operator|.
name|get
argument_list|(
name|enumValue
operator|.
name|name
argument_list|()
argument_list|)
operator|!=
name|enumValue
condition|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"cannot serialize enum value to JSON: "
operator|+
name|enumValue
operator|.
name|getDeclaringClass
argument_list|()
operator|.
name|getCanonicalName
argument_list|()
operator|+
literal|"."
operator|+
name|enumValue
argument_list|)
throw|;
block|}
return|return
name|enumValue
operator|.
name|name
argument_list|()
return|;
block|}
comment|/** Converts a string to an enum value.    * The converse of {@link #fromEnum(Enum)}. */
specifier|static
parameter_list|<
name|E
extends|extends
name|Enum
argument_list|<
name|E
argument_list|>
parameter_list|>
name|E
name|toEnum
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
operator|(
name|E
operator|)
name|ENUM_BY_NAME
operator|.
name|get
argument_list|(
name|name
argument_list|)
return|;
block|}
block|}
end_class

end_unit

