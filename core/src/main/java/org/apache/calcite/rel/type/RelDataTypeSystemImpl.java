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

begin_comment
comment|/** Default implementation of  * {@link org.apache.calcite.rel.type.RelDataTypeSystem},  * providing parameters from the SQL standard.  *  *<p>To implement other type systems, create a derived class and override  * values as needed.  *  *<table border='1'>  *<caption>Parameter values</caption>  *<tr><th>Parameter</th><th>Value</th></tr>  *<tr><td>MAX_NUMERIC_SCALE</td><td>19</td></tr>  *</table>  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|RelDataTypeSystemImpl
implements|implements
name|RelDataTypeSystem
block|{
specifier|public
name|int
name|getMaxScale
parameter_list|(
name|SqlTypeName
name|typeName
parameter_list|)
block|{
switch|switch
condition|(
name|typeName
condition|)
block|{
case|case
name|DECIMAL
case|:
return|return
name|getMaxNumericScale
argument_list|()
return|;
case|case
name|INTERVAL_DAY_TIME
case|:
case|case
name|INTERVAL_YEAR_MONTH
case|:
return|return
name|SqlTypeName
operator|.
name|MAX_INTERVAL_FRACTIONAL_SECOND_PRECISION
return|;
default|default:
return|return
operator|-
literal|1
return|;
block|}
block|}
specifier|public
name|int
name|getDefaultPrecision
parameter_list|(
name|SqlTypeName
name|typeName
parameter_list|)
block|{
comment|//Following BasicSqlType precision as the default
switch|switch
condition|(
name|typeName
condition|)
block|{
case|case
name|CHAR
case|:
case|case
name|BINARY
case|:
case|case
name|VARCHAR
case|:
case|case
name|VARBINARY
case|:
return|return
literal|1
return|;
case|case
name|DECIMAL
case|:
return|return
name|getMaxNumericPrecision
argument_list|()
return|;
case|case
name|INTERVAL_DAY_TIME
case|:
case|case
name|INTERVAL_YEAR_MONTH
case|:
return|return
name|SqlTypeName
operator|.
name|DEFAULT_INTERVAL_START_PRECISION
return|;
case|case
name|BOOLEAN
case|:
return|return
literal|1
return|;
case|case
name|TINYINT
case|:
return|return
literal|3
return|;
case|case
name|SMALLINT
case|:
return|return
literal|5
return|;
case|case
name|INTEGER
case|:
return|return
literal|10
return|;
case|case
name|BIGINT
case|:
return|return
literal|19
return|;
case|case
name|REAL
case|:
return|return
literal|7
return|;
case|case
name|FLOAT
case|:
case|case
name|DOUBLE
case|:
return|return
literal|15
return|;
case|case
name|TIME
case|:
case|case
name|DATE
case|:
return|return
literal|0
return|;
comment|// SQL99 part 2 section 6.1 syntax rule 30
case|case
name|TIMESTAMP
case|:
comment|// farrago supports only 0 (see
comment|// SqlTypeName.getDefaultPrecision), but it should be 6
comment|// (microseconds) per SQL99 part 2 section 6.1 syntax rule 30.
return|return
literal|0
return|;
default|default:
return|return
operator|-
literal|1
return|;
block|}
block|}
specifier|public
name|int
name|getMaxPrecision
parameter_list|(
name|SqlTypeName
name|typeName
parameter_list|)
block|{
switch|switch
condition|(
name|typeName
condition|)
block|{
case|case
name|DECIMAL
case|:
return|return
name|getMaxNumericPrecision
argument_list|()
return|;
case|case
name|VARCHAR
case|:
case|case
name|CHAR
case|:
return|return
literal|65536
return|;
case|case
name|VARBINARY
case|:
case|case
name|BINARY
case|:
return|return
literal|65536
return|;
case|case
name|TIME
case|:
case|case
name|TIMESTAMP
case|:
return|return
name|SqlTypeName
operator|.
name|MAX_DATETIME_PRECISION
return|;
case|case
name|INTERVAL_DAY_TIME
case|:
case|case
name|INTERVAL_YEAR_MONTH
case|:
return|return
name|SqlTypeName
operator|.
name|MAX_INTERVAL_START_PRECISION
return|;
default|default:
return|return
name|getDefaultPrecision
argument_list|(
name|typeName
argument_list|)
return|;
block|}
block|}
specifier|public
name|int
name|getMaxNumericScale
parameter_list|()
block|{
return|return
literal|19
return|;
block|}
specifier|public
name|int
name|getMaxNumericPrecision
parameter_list|()
block|{
return|return
literal|19
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
switch|switch
condition|(
name|typeName
condition|)
block|{
case|case
name|VARBINARY
case|:
case|case
name|VARCHAR
case|:
case|case
name|CHAR
case|:
return|return
literal|"'"
return|;
case|case
name|BINARY
case|:
return|return
name|isPrefix
condition|?
literal|"x'"
else|:
literal|"'"
return|;
case|case
name|TIMESTAMP
case|:
return|return
name|isPrefix
condition|?
literal|"TIMESTAMP '"
else|:
literal|"'"
return|;
case|case
name|INTERVAL_DAY_TIME
case|:
return|return
name|isPrefix
condition|?
literal|"INTERVAL '"
else|:
literal|"' DAY"
return|;
case|case
name|INTERVAL_YEAR_MONTH
case|:
return|return
name|isPrefix
condition|?
literal|"INTERVAL '"
else|:
literal|"' YEAR TO MONTH"
return|;
case|case
name|TIME
case|:
return|return
name|isPrefix
condition|?
literal|"TIME '"
else|:
literal|"'"
return|;
case|case
name|DATE
case|:
return|return
name|isPrefix
condition|?
literal|"DATE '"
else|:
literal|"'"
return|;
case|case
name|ARRAY
case|:
return|return
name|isPrefix
condition|?
literal|"("
else|:
literal|")"
return|;
default|default:
return|return
literal|null
return|;
block|}
block|}
specifier|public
name|boolean
name|isCaseSensitive
parameter_list|(
name|SqlTypeName
name|typeName
parameter_list|)
block|{
switch|switch
condition|(
name|typeName
condition|)
block|{
case|case
name|CHAR
case|:
case|case
name|VARCHAR
case|:
return|return
literal|true
return|;
default|default:
return|return
literal|false
return|;
block|}
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
literal|false
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
if|if
condition|(
name|typeName
operator|.
name|getFamily
argument_list|()
operator|==
name|SqlTypeFamily
operator|.
name|NUMERIC
operator|&&
name|getDefaultPrecision
argument_list|(
name|typeName
argument_list|)
operator|!=
operator|-
literal|1
condition|)
block|{
return|return
literal|10
return|;
block|}
return|return
literal|0
return|;
block|}
block|}
end_class

begin_comment
comment|// End RelDataTypeSystemImpl.java
end_comment

end_unit

