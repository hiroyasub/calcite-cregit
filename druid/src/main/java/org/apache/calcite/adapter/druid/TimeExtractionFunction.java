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
name|druid
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
name|rex
operator|.
name|RexCall
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
name|rex
operator|.
name|RexLiteral
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
name|rex
operator|.
name|RexNode
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
name|type
operator|.
name|SqlTypeFamily
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
name|type
operator|.
name|SqlTypeName
import|;
end_import

begin_import
import|import
name|com
operator|.
name|fasterxml
operator|.
name|jackson
operator|.
name|core
operator|.
name|JsonGenerator
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
name|ImmutableSet
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
name|Sets
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
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
name|TimeZone
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nullable
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
name|druid
operator|.
name|DruidQuery
operator|.
name|writeFieldIf
import|;
end_import

begin_comment
comment|/**  * Implementation of Druid time format extraction function.  *  *<p>These functions return the dimension value formatted according to the given format string,  * time zone, and locale.  *  *<p>For __time dimension values, this formats the time value bucketed by the aggregation  * granularity.  */
end_comment

begin_class
specifier|public
class|class
name|TimeExtractionFunction
implements|implements
name|ExtractionFunction
block|{
specifier|private
specifier|static
specifier|final
name|ImmutableSet
argument_list|<
name|TimeUnitRange
argument_list|>
name|VALID_TIME_EXTRACT
init|=
name|Sets
operator|.
name|immutableEnumSet
argument_list|(
name|TimeUnitRange
operator|.
name|YEAR
argument_list|,
name|TimeUnitRange
operator|.
name|MONTH
argument_list|,
name|TimeUnitRange
operator|.
name|DAY
argument_list|,
name|TimeUnitRange
operator|.
name|WEEK
argument_list|,
name|TimeUnitRange
operator|.
name|HOUR
argument_list|,
name|TimeUnitRange
operator|.
name|MINUTE
argument_list|,
name|TimeUnitRange
operator|.
name|SECOND
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|ImmutableSet
argument_list|<
name|TimeUnitRange
argument_list|>
name|VALID_TIME_FLOOR
init|=
name|Sets
operator|.
name|immutableEnumSet
argument_list|(
name|TimeUnitRange
operator|.
name|YEAR
argument_list|,
name|TimeUnitRange
operator|.
name|MONTH
argument_list|,
name|TimeUnitRange
operator|.
name|DAY
argument_list|,
name|TimeUnitRange
operator|.
name|WEEK
argument_list|,
name|TimeUnitRange
operator|.
name|HOUR
argument_list|,
name|TimeUnitRange
operator|.
name|MINUTE
argument_list|,
name|TimeUnitRange
operator|.
name|SECOND
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|ISO_TIME_FORMAT
init|=
literal|"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
decl_stmt|;
specifier|private
specifier|final
name|String
name|format
decl_stmt|;
specifier|private
specifier|final
name|Granularity
name|granularity
decl_stmt|;
specifier|private
specifier|final
name|String
name|timeZone
decl_stmt|;
specifier|private
specifier|final
name|String
name|local
decl_stmt|;
specifier|public
name|TimeExtractionFunction
parameter_list|(
name|String
name|format
parameter_list|,
name|Granularity
name|granularity
parameter_list|,
name|String
name|timeZone
parameter_list|,
name|String
name|local
parameter_list|)
block|{
name|this
operator|.
name|format
operator|=
name|format
expr_stmt|;
name|this
operator|.
name|granularity
operator|=
name|granularity
expr_stmt|;
name|this
operator|.
name|timeZone
operator|=
name|timeZone
expr_stmt|;
name|this
operator|.
name|local
operator|=
name|local
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|write
parameter_list|(
name|JsonGenerator
name|generator
parameter_list|)
throws|throws
name|IOException
block|{
name|generator
operator|.
name|writeStartObject
argument_list|()
expr_stmt|;
name|generator
operator|.
name|writeStringField
argument_list|(
literal|"type"
argument_list|,
literal|"timeFormat"
argument_list|)
expr_stmt|;
name|writeFieldIf
argument_list|(
name|generator
argument_list|,
literal|"format"
argument_list|,
name|format
argument_list|)
expr_stmt|;
name|writeFieldIf
argument_list|(
name|generator
argument_list|,
literal|"granularity"
argument_list|,
name|granularity
argument_list|)
expr_stmt|;
name|writeFieldIf
argument_list|(
name|generator
argument_list|,
literal|"timeZone"
argument_list|,
name|timeZone
argument_list|)
expr_stmt|;
name|writeFieldIf
argument_list|(
name|generator
argument_list|,
literal|"locale"
argument_list|,
name|local
argument_list|)
expr_stmt|;
name|generator
operator|.
name|writeEndObject
argument_list|()
expr_stmt|;
block|}
specifier|public
name|String
name|getFormat
parameter_list|()
block|{
return|return
name|format
return|;
block|}
specifier|public
name|Granularity
name|getGranularity
parameter_list|()
block|{
return|return
name|granularity
return|;
block|}
comment|/**    * Creates the default time format extraction function.    *    * @return the time extraction function    */
specifier|public
specifier|static
name|TimeExtractionFunction
name|createDefault
parameter_list|(
name|String
name|timeZone
parameter_list|)
block|{
return|return
operator|new
name|TimeExtractionFunction
argument_list|(
name|ISO_TIME_FORMAT
argument_list|,
literal|null
argument_list|,
name|timeZone
argument_list|,
literal|null
argument_list|)
return|;
block|}
comment|/**    * Creates the time format extraction function for the given granularity.    *    * @param granularity granularity to apply to the column    * @return the time extraction function corresponding to the granularity input unit    * {@link TimeExtractionFunction#VALID_TIME_EXTRACT} for supported granularity    */
specifier|public
specifier|static
name|TimeExtractionFunction
name|createExtractFromGranularity
parameter_list|(
name|Granularity
name|granularity
parameter_list|,
name|String
name|timeZone
parameter_list|)
block|{
specifier|final
name|String
name|local
init|=
name|Locale
operator|.
name|US
operator|.
name|toLanguageTag
argument_list|()
decl_stmt|;
switch|switch
condition|(
name|granularity
operator|.
name|getType
argument_list|()
condition|)
block|{
case|case
name|DAY
case|:
return|return
operator|new
name|TimeExtractionFunction
argument_list|(
literal|"d"
argument_list|,
literal|null
argument_list|,
name|timeZone
argument_list|,
name|local
argument_list|)
return|;
case|case
name|MONTH
case|:
return|return
operator|new
name|TimeExtractionFunction
argument_list|(
literal|"M"
argument_list|,
literal|null
argument_list|,
name|timeZone
argument_list|,
name|local
argument_list|)
return|;
case|case
name|YEAR
case|:
return|return
operator|new
name|TimeExtractionFunction
argument_list|(
literal|"yyyy"
argument_list|,
literal|null
argument_list|,
name|timeZone
argument_list|,
name|local
argument_list|)
return|;
case|case
name|WEEK
case|:
return|return
operator|new
name|TimeExtractionFunction
argument_list|(
literal|"w"
argument_list|,
literal|null
argument_list|,
name|timeZone
argument_list|,
name|local
argument_list|)
return|;
case|case
name|HOUR
case|:
return|return
operator|new
name|TimeExtractionFunction
argument_list|(
literal|"H"
argument_list|,
literal|null
argument_list|,
name|timeZone
argument_list|,
name|local
argument_list|)
return|;
case|case
name|MINUTE
case|:
return|return
operator|new
name|TimeExtractionFunction
argument_list|(
literal|"m"
argument_list|,
literal|null
argument_list|,
name|timeZone
argument_list|,
name|local
argument_list|)
return|;
case|case
name|SECOND
case|:
return|return
operator|new
name|TimeExtractionFunction
argument_list|(
literal|"s"
argument_list|,
literal|null
argument_list|,
name|timeZone
argument_list|,
name|local
argument_list|)
return|;
default|default:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Granularity ["
operator|+
name|granularity
operator|+
literal|"] is not supported"
argument_list|)
throw|;
block|}
block|}
comment|/**    * Creates time format floor time extraction function using a given granularity.    *    * @param granularity granularity to apply to the column    * @return the time extraction function or null if granularity is not supported    */
specifier|public
specifier|static
name|TimeExtractionFunction
name|createFloorFromGranularity
parameter_list|(
name|Granularity
name|granularity
parameter_list|,
name|String
name|timeZone
parameter_list|)
block|{
return|return
operator|new
name|TimeExtractionFunction
argument_list|(
name|ISO_TIME_FORMAT
argument_list|,
name|granularity
argument_list|,
name|timeZone
argument_list|,
name|Locale
operator|.
name|ROOT
operator|.
name|toLanguageTag
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Returns whether the RexCall contains a valid extract unit that we can    * serialize to Druid.    *    * @param rexNode Extract expression    *    * @return true if the extract unit is valid    */
specifier|public
specifier|static
name|boolean
name|isValidTimeExtract
parameter_list|(
name|RexNode
name|rexNode
parameter_list|)
block|{
specifier|final
name|RexCall
name|call
init|=
operator|(
name|RexCall
operator|)
name|rexNode
decl_stmt|;
if|if
condition|(
name|call
operator|.
name|getKind
argument_list|()
operator|!=
name|SqlKind
operator|.
name|EXTRACT
operator|||
name|call
operator|.
name|getOperands
argument_list|()
operator|.
name|size
argument_list|()
operator|!=
literal|2
condition|)
block|{
return|return
literal|false
return|;
block|}
specifier|final
name|RexLiteral
name|flag
init|=
operator|(
name|RexLiteral
operator|)
name|call
operator|.
name|operands
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|TimeUnitRange
name|timeUnit
init|=
operator|(
name|TimeUnitRange
operator|)
name|flag
operator|.
name|getValue
argument_list|()
decl_stmt|;
return|return
name|timeUnit
operator|!=
literal|null
operator|&&
name|VALID_TIME_EXTRACT
operator|.
name|contains
argument_list|(
name|timeUnit
argument_list|)
return|;
block|}
comment|/**    * Returns whether the RexCall contains a valid FLOOR unit that we can    * serialize to Druid.    *    * @param rexNode Extract expression    *    * @return true if the extract unit is valid    */
specifier|public
specifier|static
name|boolean
name|isValidTimeFloor
parameter_list|(
name|RexNode
name|rexNode
parameter_list|)
block|{
if|if
condition|(
name|rexNode
operator|.
name|getKind
argument_list|()
operator|!=
name|SqlKind
operator|.
name|FLOOR
condition|)
block|{
return|return
literal|false
return|;
block|}
specifier|final
name|RexCall
name|call
init|=
operator|(
name|RexCall
operator|)
name|rexNode
decl_stmt|;
if|if
condition|(
name|call
operator|.
name|operands
operator|.
name|size
argument_list|()
operator|!=
literal|2
condition|)
block|{
return|return
literal|false
return|;
block|}
specifier|final
name|RexLiteral
name|flag
init|=
operator|(
name|RexLiteral
operator|)
name|call
operator|.
name|operands
operator|.
name|get
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|final
name|TimeUnitRange
name|timeUnit
init|=
operator|(
name|TimeUnitRange
operator|)
name|flag
operator|.
name|getValue
argument_list|()
decl_stmt|;
return|return
name|timeUnit
operator|!=
literal|null
operator|&&
name|VALID_TIME_FLOOR
operator|.
name|contains
argument_list|(
name|timeUnit
argument_list|)
return|;
block|}
comment|/**    * @param rexNode cast RexNode    * @param timeZone timezone    *    * @return Druid Time extraction function or null when can not translate the cast.    */
annotation|@
name|Nullable
specifier|public
specifier|static
name|TimeExtractionFunction
name|translateCastToTimeExtract
parameter_list|(
name|RexNode
name|rexNode
parameter_list|,
name|TimeZone
name|timeZone
parameter_list|)
block|{
assert|assert
name|rexNode
operator|.
name|getKind
argument_list|()
operator|==
name|SqlKind
operator|.
name|CAST
assert|;
specifier|final
name|RexCall
name|rexCall
init|=
operator|(
name|RexCall
operator|)
name|rexNode
decl_stmt|;
specifier|final
name|String
name|castFormat
init|=
name|DruidSqlCastConverter
operator|.
name|dateTimeFormatString
argument_list|(
name|rexCall
operator|.
name|getType
argument_list|()
operator|.
name|getSqlTypeName
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|String
name|timeZoneId
init|=
name|timeZone
operator|==
literal|null
condition|?
literal|null
else|:
name|timeZone
operator|.
name|getID
argument_list|()
decl_stmt|;
if|if
condition|(
name|castFormat
operator|==
literal|null
condition|)
block|{
comment|// unknown format
return|return
literal|null
return|;
block|}
if|if
condition|(
name|rexCall
operator|.
name|getType
argument_list|()
operator|.
name|getFamily
argument_list|()
operator|==
name|SqlTypeFamily
operator|.
name|DATE
condition|)
block|{
return|return
operator|new
name|TimeExtractionFunction
argument_list|(
name|castFormat
argument_list|,
name|Granularities
operator|.
name|createGranularity
argument_list|(
name|TimeUnitRange
operator|.
name|DAY
argument_list|,
name|timeZoneId
argument_list|)
argument_list|,
name|timeZoneId
argument_list|,
name|Locale
operator|.
name|ENGLISH
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
if|if
condition|(
name|rexCall
operator|.
name|getType
argument_list|()
operator|.
name|getSqlTypeName
argument_list|()
operator|==
name|SqlTypeName
operator|.
name|TIMESTAMP
operator|||
name|rexCall
operator|.
name|getType
argument_list|()
operator|.
name|getSqlTypeName
argument_list|()
operator|==
name|SqlTypeName
operator|.
name|TIMESTAMP_WITH_LOCAL_TIME_ZONE
condition|)
block|{
return|return
operator|new
name|TimeExtractionFunction
argument_list|(
name|castFormat
argument_list|,
literal|null
argument_list|,
name|timeZoneId
argument_list|,
name|Locale
operator|.
name|ENGLISH
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
block|}
end_class

begin_comment
comment|// End TimeExtractionFunction.java
end_comment

end_unit

