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
name|parser
operator|.
name|SqlParserPos
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
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|util
operator|.
name|Litmus
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

begin_comment
comment|/**  * A SQL literal representing a time interval.  *  *<p>Examples:  *  *<ul>  *<li>INTERVAL '1' SECOND</li>  *<li>INTERVAL '1:00:05.345' HOUR</li>  *<li>INTERVAL '3:4' YEAR TO MONTH</li>  *</ul>  *  *<p>YEAR/MONTH intervals are not implemented yet.</p>  *  *<p>The interval string, such as '1:00:05.345', is not parsed yet.</p>  */
end_comment

begin_class
specifier|public
class|class
name|SqlIntervalLiteral
extends|extends
name|SqlLiteral
block|{
comment|//~ Constructors -----------------------------------------------------------
specifier|protected
name|SqlIntervalLiteral
parameter_list|(
name|int
name|sign
parameter_list|,
name|String
name|intervalStr
parameter_list|,
name|SqlIntervalQualifier
name|intervalQualifier
parameter_list|,
name|SqlTypeName
name|sqlTypeName
parameter_list|,
name|SqlParserPos
name|pos
parameter_list|)
block|{
name|this
argument_list|(
operator|new
name|IntervalValue
argument_list|(
name|intervalQualifier
argument_list|,
name|sign
argument_list|,
name|intervalStr
argument_list|)
argument_list|,
name|sqlTypeName
argument_list|,
name|pos
argument_list|)
expr_stmt|;
block|}
specifier|private
name|SqlIntervalLiteral
parameter_list|(
name|IntervalValue
name|intervalValue
parameter_list|,
name|SqlTypeName
name|sqlTypeName
parameter_list|,
name|SqlParserPos
name|pos
parameter_list|)
block|{
name|super
argument_list|(
name|intervalValue
argument_list|,
name|sqlTypeName
argument_list|,
name|pos
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
annotation|@
name|Override
specifier|public
name|SqlIntervalLiteral
name|clone
parameter_list|(
name|SqlParserPos
name|pos
parameter_list|)
block|{
return|return
operator|new
name|SqlIntervalLiteral
argument_list|(
operator|(
name|IntervalValue
operator|)
name|value
argument_list|,
name|getTypeName
argument_list|()
argument_list|,
name|pos
argument_list|)
return|;
block|}
specifier|public
name|void
name|unparse
parameter_list|(
name|SqlWriter
name|writer
parameter_list|,
name|int
name|leftPrec
parameter_list|,
name|int
name|rightPrec
parameter_list|)
block|{
name|writer
operator|.
name|getDialect
argument_list|()
operator|.
name|unparseSqlIntervalLiteral
argument_list|(
name|writer
argument_list|,
name|this
argument_list|,
name|leftPrec
argument_list|,
name|rightPrec
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
specifier|public
name|int
name|signum
parameter_list|()
block|{
return|return
operator|(
operator|(
name|IntervalValue
operator|)
name|value
operator|)
operator|.
name|signum
argument_list|()
return|;
block|}
comment|//~ Inner Classes ----------------------------------------------------------
comment|/**    * A Interval value.    */
specifier|public
specifier|static
class|class
name|IntervalValue
block|{
specifier|private
specifier|final
name|SqlIntervalQualifier
name|intervalQualifier
decl_stmt|;
specifier|private
specifier|final
name|String
name|intervalStr
decl_stmt|;
specifier|private
specifier|final
name|int
name|sign
decl_stmt|;
comment|/**      * Creates an interval value.      *      * @param intervalQualifier Interval qualifier      * @param sign              Sign (+1 or -1)      * @param intervalStr       Interval string      */
name|IntervalValue
parameter_list|(
name|SqlIntervalQualifier
name|intervalQualifier
parameter_list|,
name|int
name|sign
parameter_list|,
name|String
name|intervalStr
parameter_list|)
block|{
assert|assert
operator|(
name|sign
operator|==
operator|-
literal|1
operator|)
operator|||
operator|(
name|sign
operator|==
literal|1
operator|)
assert|;
assert|assert
name|intervalQualifier
operator|!=
literal|null
assert|;
assert|assert
name|intervalStr
operator|!=
literal|null
assert|;
name|this
operator|.
name|intervalQualifier
operator|=
name|intervalQualifier
expr_stmt|;
name|this
operator|.
name|sign
operator|=
name|sign
expr_stmt|;
name|this
operator|.
name|intervalStr
operator|=
name|intervalStr
expr_stmt|;
block|}
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|obj
operator|instanceof
name|IntervalValue
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|IntervalValue
name|that
init|=
operator|(
name|IntervalValue
operator|)
name|obj
decl_stmt|;
return|return
name|this
operator|.
name|intervalStr
operator|.
name|equals
argument_list|(
name|that
operator|.
name|intervalStr
argument_list|)
operator|&&
operator|(
name|this
operator|.
name|sign
operator|==
name|that
operator|.
name|sign
operator|)
operator|&&
name|this
operator|.
name|intervalQualifier
operator|.
name|equalsDeep
argument_list|(
name|that
operator|.
name|intervalQualifier
argument_list|,
name|Litmus
operator|.
name|IGNORE
argument_list|)
return|;
block|}
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|Objects
operator|.
name|hash
argument_list|(
name|sign
argument_list|,
name|intervalStr
argument_list|,
name|intervalQualifier
argument_list|)
return|;
block|}
specifier|public
name|SqlIntervalQualifier
name|getIntervalQualifier
parameter_list|()
block|{
return|return
name|intervalQualifier
return|;
block|}
specifier|public
name|String
name|getIntervalLiteral
parameter_list|()
block|{
return|return
name|intervalStr
return|;
block|}
specifier|public
name|int
name|getSign
parameter_list|()
block|{
return|return
name|sign
return|;
block|}
specifier|public
name|int
name|signum
parameter_list|()
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|intervalStr
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|char
name|ch
init|=
name|intervalStr
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|ch
operator|>=
literal|'1'
operator|&&
name|ch
operator|<=
literal|'9'
condition|)
block|{
comment|// If non zero return sign.
return|return
name|getSign
argument_list|()
return|;
block|}
block|}
return|return
literal|0
return|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|intervalStr
return|;
block|}
block|}
block|}
end_class

end_unit

