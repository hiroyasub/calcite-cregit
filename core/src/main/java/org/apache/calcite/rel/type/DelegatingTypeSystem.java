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
name|type
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
name|type
operator|.
name|SqlTypeName
import|;
end_import

begin_comment
comment|/** Implementation of {@link org.apache.calcite.rel.type.RelDataTypeSystem}  * that sends all methods to an underlying object. */
end_comment

begin_class
specifier|public
class|class
name|DelegatingTypeSystem
implements|implements
name|RelDataTypeSystem
block|{
specifier|private
specifier|final
name|RelDataTypeSystem
name|typeSystem
decl_stmt|;
comment|/** Creates a {@code DelegatingTypeSystem}. */
specifier|protected
name|DelegatingTypeSystem
parameter_list|(
name|RelDataTypeSystem
name|typeSystem
parameter_list|)
block|{
name|this
operator|.
name|typeSystem
operator|=
name|typeSystem
expr_stmt|;
block|}
specifier|public
name|int
name|getMaxScale
parameter_list|(
name|SqlTypeName
name|typeName
parameter_list|)
block|{
return|return
name|typeSystem
operator|.
name|getMaxScale
argument_list|(
name|typeName
argument_list|)
return|;
block|}
specifier|public
name|int
name|getDefaultPrecision
parameter_list|(
name|SqlTypeName
name|typeName
parameter_list|)
block|{
return|return
name|typeSystem
operator|.
name|getDefaultPrecision
argument_list|(
name|typeName
argument_list|)
return|;
block|}
specifier|public
name|int
name|getMaxPrecision
parameter_list|(
name|SqlTypeName
name|typeName
parameter_list|)
block|{
return|return
name|typeSystem
operator|.
name|getMaxPrecision
argument_list|(
name|typeName
argument_list|)
return|;
block|}
specifier|public
name|int
name|getMaxNumericScale
parameter_list|()
block|{
return|return
name|typeSystem
operator|.
name|getMaxNumericScale
argument_list|()
return|;
block|}
specifier|public
name|int
name|getMaxNumericPrecision
parameter_list|()
block|{
return|return
name|typeSystem
operator|.
name|getMaxNumericPrecision
argument_list|()
return|;
block|}
specifier|public
name|String
name|getLiteral
parameter_list|(
name|SqlTypeName
name|typeName
parameter_list|,
name|boolean
name|isPrefix
parameter_list|)
block|{
return|return
name|typeSystem
operator|.
name|getLiteral
argument_list|(
name|typeName
argument_list|,
name|isPrefix
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|isCaseSensitive
parameter_list|(
name|SqlTypeName
name|typeName
parameter_list|)
block|{
return|return
name|typeSystem
operator|.
name|isCaseSensitive
argument_list|(
name|typeName
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|isAutoincrement
parameter_list|(
name|SqlTypeName
name|typeName
parameter_list|)
block|{
return|return
name|typeSystem
operator|.
name|isAutoincrement
argument_list|(
name|typeName
argument_list|)
return|;
block|}
specifier|public
name|int
name|getNumTypeRadix
parameter_list|(
name|SqlTypeName
name|typeName
parameter_list|)
block|{
return|return
name|typeSystem
operator|.
name|getNumTypeRadix
argument_list|(
name|typeName
argument_list|)
return|;
block|}
specifier|public
name|RelDataType
name|deriveSumType
parameter_list|(
name|RelDataTypeFactory
name|typeFactory
parameter_list|,
name|RelDataType
name|argumentType
parameter_list|)
block|{
return|return
name|typeSystem
operator|.
name|deriveSumType
argument_list|(
name|typeFactory
argument_list|,
name|argumentType
argument_list|)
return|;
block|}
specifier|public
name|RelDataType
name|deriveAvgAggType
parameter_list|(
name|RelDataTypeFactory
name|typeFactory
parameter_list|,
name|RelDataType
name|argumentType
parameter_list|)
block|{
return|return
name|typeSystem
operator|.
name|deriveAvgAggType
argument_list|(
name|typeFactory
argument_list|,
name|argumentType
argument_list|)
return|;
block|}
specifier|public
name|RelDataType
name|deriveCovarType
parameter_list|(
name|RelDataTypeFactory
name|typeFactory
parameter_list|,
name|RelDataType
name|arg0Type
parameter_list|,
name|RelDataType
name|arg1Type
parameter_list|)
block|{
return|return
name|typeSystem
operator|.
name|deriveCovarType
argument_list|(
name|typeFactory
argument_list|,
name|arg0Type
argument_list|,
name|arg1Type
argument_list|)
return|;
block|}
specifier|public
name|RelDataType
name|deriveFractionalRankType
parameter_list|(
name|RelDataTypeFactory
name|typeFactory
parameter_list|)
block|{
return|return
name|typeSystem
operator|.
name|deriveFractionalRankType
argument_list|(
name|typeFactory
argument_list|)
return|;
block|}
specifier|public
name|RelDataType
name|deriveRankType
parameter_list|(
name|RelDataTypeFactory
name|typeFactory
parameter_list|)
block|{
return|return
name|typeSystem
operator|.
name|deriveRankType
argument_list|(
name|typeFactory
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|isSchemaCaseSensitive
parameter_list|()
block|{
return|return
name|typeSystem
operator|.
name|isSchemaCaseSensitive
argument_list|()
return|;
block|}
specifier|public
name|boolean
name|shouldConvertRaggedUnionTypesToVarying
parameter_list|()
block|{
return|return
name|typeSystem
operator|.
name|shouldConvertRaggedUnionTypesToVarying
argument_list|()
return|;
block|}
block|}
end_class

begin_comment
comment|// End DelegatingTypeSystem.java
end_comment

end_unit
