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
name|Objects
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
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
comment|/**  * Factory methods and helpers for {@link Granularity}.  */
end_comment

begin_class
specifier|public
class|class
name|Granularities
block|{
comment|// Private constructor for utility class
specifier|private
name|Granularities
parameter_list|()
block|{
block|}
comment|/** Returns a Granularity that causes all rows to be rolled up into one. */
specifier|public
specifier|static
name|Granularity
name|all
parameter_list|()
block|{
return|return
name|AllGranularity
operator|.
name|INSTANCE
return|;
block|}
comment|/** Creates a Granularity based on a time unit.    *    *<p>When used in a query, Druid will rollup and round time values based on    * specified period and timezone. */
annotation|@
name|Nonnull
specifier|public
specifier|static
name|Granularity
name|createGranularity
parameter_list|(
name|TimeUnitRange
name|timeUnit
parameter_list|,
name|String
name|timeZone
parameter_list|)
block|{
switch|switch
condition|(
name|timeUnit
condition|)
block|{
case|case
name|YEAR
case|:
return|return
operator|new
name|PeriodGranularity
argument_list|(
name|Granularity
operator|.
name|Type
operator|.
name|YEAR
argument_list|,
literal|"P1Y"
argument_list|,
name|timeZone
argument_list|)
return|;
case|case
name|QUARTER
case|:
return|return
operator|new
name|PeriodGranularity
argument_list|(
name|Granularity
operator|.
name|Type
operator|.
name|QUARTER
argument_list|,
literal|"P3M"
argument_list|,
name|timeZone
argument_list|)
return|;
case|case
name|MONTH
case|:
return|return
operator|new
name|PeriodGranularity
argument_list|(
name|Granularity
operator|.
name|Type
operator|.
name|MONTH
argument_list|,
literal|"P1M"
argument_list|,
name|timeZone
argument_list|)
return|;
case|case
name|WEEK
case|:
return|return
operator|new
name|PeriodGranularity
argument_list|(
name|Granularity
operator|.
name|Type
operator|.
name|WEEK
argument_list|,
literal|"P1W"
argument_list|,
name|timeZone
argument_list|)
return|;
case|case
name|DAY
case|:
return|return
operator|new
name|PeriodGranularity
argument_list|(
name|Granularity
operator|.
name|Type
operator|.
name|DAY
argument_list|,
literal|"P1D"
argument_list|,
name|timeZone
argument_list|)
return|;
case|case
name|HOUR
case|:
return|return
operator|new
name|PeriodGranularity
argument_list|(
name|Granularity
operator|.
name|Type
operator|.
name|HOUR
argument_list|,
literal|"PT1H"
argument_list|,
name|timeZone
argument_list|)
return|;
case|case
name|MINUTE
case|:
return|return
operator|new
name|PeriodGranularity
argument_list|(
name|Granularity
operator|.
name|Type
operator|.
name|MINUTE
argument_list|,
literal|"PT1M"
argument_list|,
name|timeZone
argument_list|)
return|;
case|case
name|SECOND
case|:
return|return
operator|new
name|PeriodGranularity
argument_list|(
name|Granularity
operator|.
name|Type
operator|.
name|SECOND
argument_list|,
literal|"PT1S"
argument_list|,
name|timeZone
argument_list|)
return|;
default|default:
throw|throw
operator|new
name|AssertionError
argument_list|(
name|timeUnit
argument_list|)
throw|;
block|}
block|}
comment|/** Implementation of {@link Granularity} for {@link Granularity.Type#ALL}.    * A singleton. */
specifier|private
enum|enum
name|AllGranularity
implements|implements
name|Granularity
block|{
name|INSTANCE
block|;
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
name|writeObject
argument_list|(
literal|"all"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Nonnull
specifier|public
name|Type
name|getType
parameter_list|()
block|{
return|return
name|Type
operator|.
name|ALL
return|;
block|}
block|}
comment|/** Implementation of {@link Granularity} based on a time unit.    * Corresponds to PeriodGranularity in Druid. */
specifier|private
specifier|static
class|class
name|PeriodGranularity
implements|implements
name|Granularity
block|{
specifier|private
specifier|final
name|Type
name|type
decl_stmt|;
specifier|private
specifier|final
name|String
name|period
decl_stmt|;
specifier|private
specifier|final
name|String
name|timeZone
decl_stmt|;
specifier|private
name|PeriodGranularity
parameter_list|(
name|Type
name|type
parameter_list|,
name|String
name|period
parameter_list|,
name|String
name|timeZone
parameter_list|)
block|{
name|this
operator|.
name|type
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|type
argument_list|)
expr_stmt|;
name|this
operator|.
name|period
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|period
argument_list|)
expr_stmt|;
name|this
operator|.
name|timeZone
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|timeZone
argument_list|)
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
literal|"period"
argument_list|)
expr_stmt|;
name|writeFieldIf
argument_list|(
name|generator
argument_list|,
literal|"period"
argument_list|,
name|period
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
name|generator
operator|.
name|writeEndObject
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Nonnull
specifier|public
name|Type
name|getType
parameter_list|()
block|{
return|return
name|type
return|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End Granularities.java
end_comment

end_unit

