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
name|geode
operator|.
name|simple
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
name|DataContext
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
name|AbstractEnumerable
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
name|Enumerable
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
name|Enumerator
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
name|rel
operator|.
name|type
operator|.
name|RelDataTypeFactory
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
name|ScannableTable
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
name|impl
operator|.
name|AbstractTable
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|geode
operator|.
name|cache
operator|.
name|client
operator|.
name|ClientCache
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
name|adapter
operator|.
name|geode
operator|.
name|util
operator|.
name|GeodeUtils
operator|.
name|convertToRowValues
import|;
end_import

begin_comment
comment|/**  * Geode Simple Scannable Table abstraction.  */
end_comment

begin_class
specifier|public
class|class
name|GeodeSimpleScannableTable
extends|extends
name|AbstractTable
implements|implements
name|ScannableTable
block|{
specifier|private
specifier|final
name|RelDataType
name|relDataType
decl_stmt|;
specifier|private
name|String
name|regionName
decl_stmt|;
specifier|private
name|ClientCache
name|clientCache
decl_stmt|;
specifier|public
name|GeodeSimpleScannableTable
parameter_list|(
name|String
name|regionName
parameter_list|,
name|RelDataType
name|relDataType
parameter_list|,
name|ClientCache
name|clientCache
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|regionName
operator|=
name|regionName
expr_stmt|;
name|this
operator|.
name|clientCache
operator|=
name|clientCache
expr_stmt|;
name|this
operator|.
name|relDataType
operator|=
name|relDataType
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
literal|"GeodeSimpleScannableTable"
return|;
block|}
annotation|@
name|Override
specifier|public
name|RelDataType
name|getRowType
parameter_list|(
name|RelDataTypeFactory
name|typeFactory
parameter_list|)
block|{
return|return
name|relDataType
return|;
block|}
annotation|@
name|Override
specifier|public
name|Enumerable
argument_list|<
name|Object
index|[]
argument_list|>
name|scan
parameter_list|(
name|DataContext
name|root
parameter_list|)
block|{
return|return
operator|new
name|AbstractEnumerable
argument_list|<
name|Object
index|[]
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Enumerator
argument_list|<
name|Object
index|[]
argument_list|>
name|enumerator
parameter_list|()
block|{
return|return
operator|new
name|GeodeSimpleEnumerator
argument_list|<
name|Object
index|[]
argument_list|>
argument_list|(
name|clientCache
argument_list|,
name|regionName
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|Object
index|[]
name|convert
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
name|Object
name|values
init|=
name|convertToRowValues
argument_list|(
name|relDataType
operator|.
name|getFieldList
argument_list|()
argument_list|,
name|obj
argument_list|)
decl_stmt|;
if|if
condition|(
name|values
operator|instanceof
name|Object
index|[]
condition|)
block|{
return|return
operator|(
name|Object
index|[]
operator|)
name|values
return|;
block|}
return|return
operator|new
name|Object
index|[]
block|{
name|values
block|}
return|;
block|}
block|}
return|;
block|}
block|}
return|;
block|}
block|}
end_class

end_unit

