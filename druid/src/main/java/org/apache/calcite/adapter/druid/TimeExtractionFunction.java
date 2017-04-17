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
name|Locale
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
name|String
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
name|String
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
comment|/**    * Creates the default time format extraction function.    *    * @return the time extraction function    */
specifier|public
specifier|static
name|TimeExtractionFunction
name|createDefault
parameter_list|()
block|{
return|return
operator|new
name|TimeExtractionFunction
argument_list|(
name|ISO_TIME_FORMAT
argument_list|,
literal|null
argument_list|,
literal|"UTC"
argument_list|,
literal|null
argument_list|)
return|;
block|}
comment|/**    * Creates the time format extraction function for the given granularity.    * Only YEAR, MONTH, and DAY granularity are supported.    *    * @param granularity granularity to apply to the column    * @return the time extraction function or null if granularity is not supported    */
specifier|public
specifier|static
name|TimeExtractionFunction
name|createExtractFromGranularity
parameter_list|(
name|Granularity
name|granularity
parameter_list|)
block|{
switch|switch
condition|(
name|granularity
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
literal|"UTC"
argument_list|,
name|Locale
operator|.
name|getDefault
argument_list|()
operator|.
name|toLanguageTag
argument_list|()
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
literal|"UTC"
argument_list|,
name|Locale
operator|.
name|getDefault
argument_list|()
operator|.
name|toLanguageTag
argument_list|()
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
literal|"UTC"
argument_list|,
name|Locale
operator|.
name|getDefault
argument_list|()
operator|.
name|toLanguageTag
argument_list|()
argument_list|)
return|;
default|default:
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"Extraction "
operator|+
name|granularity
operator|.
name|value
operator|+
literal|" is not valid"
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
parameter_list|)
block|{
return|return
operator|new
name|TimeExtractionFunction
argument_list|(
name|ISO_TIME_FORMAT
argument_list|,
name|granularity
operator|.
name|value
argument_list|,
literal|"UTC"
argument_list|,
name|Locale
operator|.
name|getDefault
argument_list|()
operator|.
name|toLanguageTag
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|// End TimeExtractionFunction.java
end_comment

end_unit

