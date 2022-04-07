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
name|validate
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
name|fun
operator|.
name|SqlLibrary
import|;
end_import

begin_comment
comment|/**  * Abstract base class for implementing {@link SqlConformance}.  *  *<p>Every method in {@code SqlConformance} is implemented,  * and behaves the same as in {@link SqlConformanceEnum#DEFAULT}.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|SqlAbstractConformance
implements|implements
name|SqlConformance
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|isLiberal
parameter_list|()
block|{
return|return
name|SqlConformanceEnum
operator|.
name|DEFAULT
operator|.
name|isLiberal
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|allowCharLiteralAlias
parameter_list|()
block|{
return|return
name|SqlConformanceEnum
operator|.
name|DEFAULT
operator|.
name|allowCharLiteralAlias
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isGroupByAlias
parameter_list|()
block|{
return|return
name|SqlConformanceEnum
operator|.
name|DEFAULT
operator|.
name|isGroupByAlias
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isGroupByOrdinal
parameter_list|()
block|{
return|return
name|SqlConformanceEnum
operator|.
name|DEFAULT
operator|.
name|isGroupByOrdinal
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isHavingAlias
parameter_list|()
block|{
return|return
name|SqlConformanceEnum
operator|.
name|DEFAULT
operator|.
name|isHavingAlias
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isSortByOrdinal
parameter_list|()
block|{
return|return
name|SqlConformanceEnum
operator|.
name|DEFAULT
operator|.
name|isSortByOrdinal
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isSortByAlias
parameter_list|()
block|{
return|return
name|SqlConformanceEnum
operator|.
name|DEFAULT
operator|.
name|isSortByAlias
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isSortByAliasObscures
parameter_list|()
block|{
return|return
name|SqlConformanceEnum
operator|.
name|DEFAULT
operator|.
name|isSortByAliasObscures
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isFromRequired
parameter_list|()
block|{
return|return
name|SqlConformanceEnum
operator|.
name|DEFAULT
operator|.
name|isFromRequired
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|splitQuotedTableName
parameter_list|()
block|{
return|return
name|SqlConformanceEnum
operator|.
name|DEFAULT
operator|.
name|splitQuotedTableName
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|allowHyphenInUnquotedTableName
parameter_list|()
block|{
return|return
name|SqlConformanceEnum
operator|.
name|DEFAULT
operator|.
name|allowHyphenInUnquotedTableName
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isBangEqualAllowed
parameter_list|()
block|{
return|return
name|SqlConformanceEnum
operator|.
name|DEFAULT
operator|.
name|isBangEqualAllowed
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isMinusAllowed
parameter_list|()
block|{
return|return
name|SqlConformanceEnum
operator|.
name|DEFAULT
operator|.
name|isMinusAllowed
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isApplyAllowed
parameter_list|()
block|{
return|return
name|SqlConformanceEnum
operator|.
name|DEFAULT
operator|.
name|isApplyAllowed
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isInsertSubsetColumnsAllowed
parameter_list|()
block|{
return|return
name|SqlConformanceEnum
operator|.
name|DEFAULT
operator|.
name|isInsertSubsetColumnsAllowed
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|allowNiladicParentheses
parameter_list|()
block|{
return|return
name|SqlConformanceEnum
operator|.
name|DEFAULT
operator|.
name|allowNiladicParentheses
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|allowExplicitRowValueConstructor
parameter_list|()
block|{
return|return
name|SqlConformanceEnum
operator|.
name|DEFAULT
operator|.
name|allowExplicitRowValueConstructor
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|allowExtend
parameter_list|()
block|{
return|return
name|SqlConformanceEnum
operator|.
name|DEFAULT
operator|.
name|allowExtend
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isLimitStartCountAllowed
parameter_list|()
block|{
return|return
name|SqlConformanceEnum
operator|.
name|DEFAULT
operator|.
name|isLimitStartCountAllowed
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isOffsetLimitAllowed
parameter_list|()
block|{
return|return
name|SqlConformanceEnum
operator|.
name|DEFAULT
operator|.
name|isOffsetLimitAllowed
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isPercentRemainderAllowed
parameter_list|()
block|{
return|return
name|SqlConformanceEnum
operator|.
name|DEFAULT
operator|.
name|isPercentRemainderAllowed
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|allowGeometry
parameter_list|()
block|{
return|return
name|SqlConformanceEnum
operator|.
name|DEFAULT
operator|.
name|allowGeometry
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|shouldConvertRaggedUnionTypesToVarying
parameter_list|()
block|{
return|return
name|SqlConformanceEnum
operator|.
name|DEFAULT
operator|.
name|shouldConvertRaggedUnionTypesToVarying
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|allowExtendedTrim
parameter_list|()
block|{
return|return
name|SqlConformanceEnum
operator|.
name|DEFAULT
operator|.
name|allowExtendedTrim
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|allowPluralTimeUnits
parameter_list|()
block|{
return|return
name|SqlConformanceEnum
operator|.
name|DEFAULT
operator|.
name|allowPluralTimeUnits
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|allowQualifyingCommonColumn
parameter_list|()
block|{
return|return
name|SqlConformanceEnum
operator|.
name|DEFAULT
operator|.
name|allowQualifyingCommonColumn
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|allowAliasUnnestItems
parameter_list|()
block|{
return|return
name|SqlConformanceEnum
operator|.
name|DEFAULT
operator|.
name|allowAliasUnnestItems
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|SqlLibrary
name|semantics
parameter_list|()
block|{
return|return
name|SqlConformanceEnum
operator|.
name|DEFAULT
operator|.
name|semantics
argument_list|()
return|;
block|}
block|}
end_class

end_unit

