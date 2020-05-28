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
name|linq4j
operator|.
name|tree
operator|.
name|Primitive
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
name|checkerframework
operator|.
name|checker
operator|.
name|nullness
operator|.
name|qual
operator|.
name|Nullable
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

begin_comment
comment|/**  * Type of a field in a CSV file.  *  *<p>Usually, and unless specified explicitly in the header row, a field is  * of type {@link #STRING}. But specifying the field type in the header row  * makes it easier to write SQL.</p>  */
end_comment

begin_enum
specifier|public
enum|enum
name|CsvFieldType
block|{
name|STRING
argument_list|(
name|String
operator|.
name|class
argument_list|,
literal|"string"
argument_list|)
block|,
name|BOOLEAN
argument_list|(
name|Primitive
operator|.
name|BOOLEAN
argument_list|)
block|,
name|BYTE
argument_list|(
name|Primitive
operator|.
name|BYTE
argument_list|)
block|,
name|CHAR
argument_list|(
name|Primitive
operator|.
name|CHAR
argument_list|)
block|,
name|SHORT
argument_list|(
name|Primitive
operator|.
name|SHORT
argument_list|)
block|,
name|INT
argument_list|(
name|Primitive
operator|.
name|INT
argument_list|)
block|,
name|LONG
argument_list|(
name|Primitive
operator|.
name|LONG
argument_list|)
block|,
name|FLOAT
argument_list|(
name|Primitive
operator|.
name|FLOAT
argument_list|)
block|,
name|DOUBLE
argument_list|(
name|Primitive
operator|.
name|DOUBLE
argument_list|)
block|,
name|DATE
argument_list|(
name|java
operator|.
name|sql
operator|.
name|Date
operator|.
name|class
argument_list|,
literal|"date"
argument_list|)
block|,
name|TIME
argument_list|(
name|java
operator|.
name|sql
operator|.
name|Time
operator|.
name|class
argument_list|,
literal|"time"
argument_list|)
block|,
name|TIMESTAMP
argument_list|(
name|java
operator|.
name|sql
operator|.
name|Timestamp
operator|.
name|class
argument_list|,
literal|"timestamp"
argument_list|)
block|;
specifier|private
specifier|final
name|Class
name|clazz
decl_stmt|;
specifier|private
specifier|final
name|String
name|simpleName
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|CsvFieldType
argument_list|>
name|MAP
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
static|static
block|{
for|for
control|(
name|CsvFieldType
name|value
range|:
name|values
argument_list|()
control|)
block|{
name|MAP
operator|.
name|put
argument_list|(
name|value
operator|.
name|simpleName
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
name|CsvFieldType
parameter_list|(
name|Primitive
name|primitive
parameter_list|)
block|{
name|this
argument_list|(
name|primitive
operator|.
name|getBoxClass
argument_list|()
argument_list|,
name|primitive
operator|.
name|getPrimitiveName
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|CsvFieldType
parameter_list|(
name|Class
name|clazz
parameter_list|,
name|String
name|simpleName
parameter_list|)
block|{
name|this
operator|.
name|clazz
operator|=
name|clazz
expr_stmt|;
name|this
operator|.
name|simpleName
operator|=
name|simpleName
expr_stmt|;
block|}
specifier|public
name|RelDataType
name|toType
parameter_list|(
name|JavaTypeFactory
name|typeFactory
parameter_list|)
block|{
name|RelDataType
name|javaType
init|=
name|typeFactory
operator|.
name|createJavaType
argument_list|(
name|clazz
argument_list|)
decl_stmt|;
name|RelDataType
name|sqlType
init|=
name|typeFactory
operator|.
name|createSqlType
argument_list|(
name|javaType
operator|.
name|getSqlTypeName
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|typeFactory
operator|.
name|createTypeWithNullability
argument_list|(
name|sqlType
argument_list|,
literal|true
argument_list|)
return|;
block|}
specifier|public
specifier|static
annotation|@
name|Nullable
name|CsvFieldType
name|of
parameter_list|(
name|String
name|typeString
parameter_list|)
block|{
return|return
name|MAP
operator|.
name|get
argument_list|(
name|typeString
argument_list|)
return|;
block|}
block|}
end_enum

end_unit

