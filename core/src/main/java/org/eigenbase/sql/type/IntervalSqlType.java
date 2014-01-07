begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to Julian Hyde under one or more contributor license // agreements. See the NOTICE file distributed with this work for // additional information regarding copyright ownership. // // Julian Hyde licenses this file to you under the Apache License, // Version 2.0 (the "License"); you may not use this file except in // compliance with the License. You may obtain a copy of the License at: // // http://www.apache.org/licenses/LICENSE-2.0 // // Unless required by applicable law or agreed to in writing, software // distributed under the License is distributed on an "AS IS" BASIS, // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. // See the License for the specific language governing permissions and // limitations under the License. */
end_comment

begin_package
package|package
name|org
operator|.
name|eigenbase
operator|.
name|sql
operator|.
name|type
package|;
end_package

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|reltype
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|sql
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|sql
operator|.
name|parser
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * IntervalSqlType represents a standard SQL datetime interval type.  */
end_comment

begin_class
specifier|public
class|class
name|IntervalSqlType
extends|extends
name|AbstractSqlType
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|private
name|SqlIntervalQualifier
name|intervalQualifier
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Constructs an IntervalSqlType. This should only be called from a factory    * method.    */
specifier|public
name|IntervalSqlType
parameter_list|(
name|SqlIntervalQualifier
name|intervalQualifier
parameter_list|,
name|boolean
name|isNullable
parameter_list|)
block|{
name|super
argument_list|(
name|intervalQualifier
operator|.
name|isYearMonth
argument_list|()
condition|?
name|SqlTypeName
operator|.
name|INTERVAL_YEAR_MONTH
else|:
name|SqlTypeName
operator|.
name|INTERVAL_DAY_TIME
argument_list|,
name|isNullable
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|this
operator|.
name|intervalQualifier
operator|=
name|intervalQualifier
expr_stmt|;
name|computeDigest
argument_list|()
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
comment|// implement RelDataTypeImpl
specifier|protected
name|void
name|generateTypeString
parameter_list|(
name|StringBuilder
name|sb
parameter_list|,
name|boolean
name|withDetail
parameter_list|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"INTERVAL "
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|intervalQualifier
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// implement RelDataType
specifier|public
name|SqlIntervalQualifier
name|getIntervalQualifier
parameter_list|()
block|{
return|return
name|intervalQualifier
return|;
block|}
comment|/**    * Combines two IntervalTypes and returns the result. E.g. the result of    * combining<br>    *<code>INTERVAL DAY TO HOUR</code><br>    * with<br>    *<code>INTERVAL SECOND</code> is<br>    *<code>INTERVAL DAY TO SECOND</code>    */
specifier|public
name|IntervalSqlType
name|combine
parameter_list|(
name|RelDataTypeFactoryImpl
name|typeFactory
parameter_list|,
name|IntervalSqlType
name|that
parameter_list|)
block|{
assert|assert
name|this
operator|.
name|intervalQualifier
operator|.
name|isYearMonth
argument_list|()
operator|==
name|that
operator|.
name|intervalQualifier
operator|.
name|isYearMonth
argument_list|()
assert|;
name|boolean
name|nullable
init|=
name|isNullable
operator|||
name|that
operator|.
name|isNullable
decl_stmt|;
name|SqlIntervalQualifier
operator|.
name|TimeUnit
name|thisStart
init|=
name|this
operator|.
name|intervalQualifier
operator|.
name|getStartUnit
argument_list|()
decl_stmt|;
name|SqlIntervalQualifier
operator|.
name|TimeUnit
name|thisEnd
init|=
name|this
operator|.
name|intervalQualifier
operator|.
name|getEndUnit
argument_list|()
decl_stmt|;
name|SqlIntervalQualifier
operator|.
name|TimeUnit
name|thatStart
init|=
name|that
operator|.
name|intervalQualifier
operator|.
name|getStartUnit
argument_list|()
decl_stmt|;
name|SqlIntervalQualifier
operator|.
name|TimeUnit
name|thatEnd
init|=
name|that
operator|.
name|intervalQualifier
operator|.
name|getEndUnit
argument_list|()
decl_stmt|;
assert|assert
literal|null
operator|!=
name|thisStart
assert|;
assert|assert
literal|null
operator|!=
name|thatStart
assert|;
name|int
name|secondPrec
init|=
name|this
operator|.
name|intervalQualifier
operator|.
name|getStartPrecisionPreservingDefault
argument_list|()
decl_stmt|;
name|int
name|fracPrec
init|=
name|SqlIntervalQualifier
operator|.
name|combineFractionalSecondPrecisionPreservingDefault
argument_list|(
name|this
operator|.
name|intervalQualifier
argument_list|,
name|that
operator|.
name|intervalQualifier
argument_list|)
decl_stmt|;
if|if
condition|(
name|thisStart
operator|.
name|ordinal
argument_list|()
operator|>
name|thatStart
operator|.
name|ordinal
argument_list|()
condition|)
block|{
name|thisEnd
operator|=
name|thisStart
expr_stmt|;
name|thisStart
operator|=
name|thatStart
expr_stmt|;
name|secondPrec
operator|=
name|that
operator|.
name|intervalQualifier
operator|.
name|getStartPrecisionPreservingDefault
argument_list|()
expr_stmt|;
block|}
if|else if
condition|(
name|thisStart
operator|.
name|ordinal
argument_list|()
operator|==
name|thatStart
operator|.
name|ordinal
argument_list|()
condition|)
block|{
name|secondPrec
operator|=
name|SqlIntervalQualifier
operator|.
name|combineStartPrecisionPreservingDefault
argument_list|(
name|this
operator|.
name|intervalQualifier
argument_list|,
name|that
operator|.
name|intervalQualifier
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
operator|(
literal|null
operator|==
name|thisEnd
operator|)
operator|||
operator|(
name|thisEnd
operator|.
name|ordinal
argument_list|()
operator|<
name|thatStart
operator|.
name|ordinal
argument_list|()
operator|)
condition|)
block|{
name|thisEnd
operator|=
name|thatStart
expr_stmt|;
block|}
if|if
condition|(
literal|null
operator|!=
name|thatEnd
condition|)
block|{
if|if
condition|(
operator|(
literal|null
operator|==
name|thisEnd
operator|)
operator|||
operator|(
name|thisEnd
operator|.
name|ordinal
argument_list|()
operator|<
name|thatEnd
operator|.
name|ordinal
argument_list|()
operator|)
condition|)
block|{
name|thisEnd
operator|=
name|thatEnd
expr_stmt|;
block|}
block|}
name|RelDataType
name|intervalType
init|=
name|typeFactory
operator|.
name|createSqlIntervalType
argument_list|(
operator|new
name|SqlIntervalQualifier
argument_list|(
name|thisStart
argument_list|,
name|secondPrec
argument_list|,
name|thisEnd
argument_list|,
name|fracPrec
argument_list|,
name|SqlParserPos
operator|.
name|ZERO
argument_list|)
argument_list|)
decl_stmt|;
name|intervalType
operator|=
name|typeFactory
operator|.
name|createTypeWithNullability
argument_list|(
name|intervalType
argument_list|,
name|nullable
argument_list|)
expr_stmt|;
return|return
operator|(
name|IntervalSqlType
operator|)
name|intervalType
return|;
block|}
comment|// implement RelDataType
specifier|public
name|int
name|getPrecision
parameter_list|()
block|{
return|return
name|intervalQualifier
operator|.
name|getStartPrecision
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getScale
parameter_list|()
block|{
comment|// TODO Auto-generated method stub
return|return
name|intervalQualifier
operator|.
name|getFractionalSecondPrecision
argument_list|()
return|;
block|}
block|}
end_class

begin_comment
comment|// End IntervalSqlType.java
end_comment

end_unit

