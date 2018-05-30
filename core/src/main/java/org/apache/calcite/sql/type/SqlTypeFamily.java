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
name|avatica
operator|.
name|util
operator|.
name|TimeUnit
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
name|rel
operator|.
name|type
operator|.
name|RelDataTypeFamily
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
name|SqlIntervalQualifier
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
name|parser
operator|.
name|SqlParserPos
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
name|ImmutableList
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
name|ImmutableMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Types
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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
comment|/**  * SqlTypeFamily provides SQL type categorization.  *  *<p>The<em>primary</em> family categorization is a complete disjoint  * partitioning of SQL types into families, where two types are members of the  * same primary family iff instances of the two types can be the operands of an  * SQL equality predicate such as<code>WHERE v1 = v2</code>. Primary families  * are returned by RelDataType.getFamily().  *  *<p>There is also a<em>secondary</em> family categorization which overlaps  * with the primary categorization. It is used in type strategies for more  * specific or more general categorization than the primary families. Secondary  * families are never returned by RelDataType.getFamily().  */
end_comment

begin_enum
specifier|public
enum|enum
name|SqlTypeFamily
implements|implements
name|RelDataTypeFamily
block|{
comment|// Primary families.
name|CHARACTER
block|,
name|BINARY
block|,
name|NUMERIC
block|,
name|DATE
block|,
name|TIME
block|,
name|TIMESTAMP
block|,
name|BOOLEAN
block|,
name|INTERVAL_YEAR_MONTH
block|,
name|INTERVAL_DAY_TIME
block|,
comment|// Secondary families.
name|STRING
block|,
name|APPROXIMATE_NUMERIC
block|,
name|EXACT_NUMERIC
block|,
name|DECIMAL
block|,
name|INTEGER
block|,
name|DATETIME
block|,
name|DATETIME_INTERVAL
block|,
name|MULTISET
block|,
name|ARRAY
block|,
name|MAP
block|,
name|NULL
block|,
name|ANY
block|,
name|CURSOR
block|,
name|COLUMN_LIST
block|,
name|GEO
block|;
specifier|private
specifier|static
specifier|final
name|Map
argument_list|<
name|Integer
argument_list|,
name|SqlTypeFamily
argument_list|>
name|JDBC_TYPE_TO_FAMILY
init|=
name|ImmutableMap
operator|.
expr|<
name|Integer
decl_stmt|,
name|SqlTypeFamily
decl|>
name|builder
argument_list|()
comment|// Not present:
comment|// SqlTypeName.MULTISET shares Types.ARRAY with SqlTypeName.ARRAY;
comment|// SqlTypeName.MAP has no corresponding JDBC type
comment|// SqlTypeName.COLUMN_LIST has no corresponding JDBC type
decl|.
name|put
argument_list|(
name|Types
operator|.
name|BIT
argument_list|,
name|NUMERIC
argument_list|)
decl|.
name|put
argument_list|(
name|Types
operator|.
name|TINYINT
argument_list|,
name|NUMERIC
argument_list|)
decl|.
name|put
argument_list|(
name|Types
operator|.
name|SMALLINT
argument_list|,
name|NUMERIC
argument_list|)
decl|.
name|put
argument_list|(
name|Types
operator|.
name|BIGINT
argument_list|,
name|NUMERIC
argument_list|)
decl|.
name|put
argument_list|(
name|Types
operator|.
name|INTEGER
argument_list|,
name|NUMERIC
argument_list|)
decl|.
name|put
argument_list|(
name|Types
operator|.
name|NUMERIC
argument_list|,
name|NUMERIC
argument_list|)
decl|.
name|put
argument_list|(
name|Types
operator|.
name|DECIMAL
argument_list|,
name|NUMERIC
argument_list|)
decl|.
name|put
argument_list|(
name|Types
operator|.
name|FLOAT
argument_list|,
name|NUMERIC
argument_list|)
decl|.
name|put
argument_list|(
name|Types
operator|.
name|REAL
argument_list|,
name|NUMERIC
argument_list|)
decl|.
name|put
argument_list|(
name|Types
operator|.
name|DOUBLE
argument_list|,
name|NUMERIC
argument_list|)
decl|.
name|put
argument_list|(
name|Types
operator|.
name|CHAR
argument_list|,
name|CHARACTER
argument_list|)
decl|.
name|put
argument_list|(
name|Types
operator|.
name|VARCHAR
argument_list|,
name|CHARACTER
argument_list|)
decl|.
name|put
argument_list|(
name|Types
operator|.
name|LONGVARCHAR
argument_list|,
name|CHARACTER
argument_list|)
decl|.
name|put
argument_list|(
name|Types
operator|.
name|CLOB
argument_list|,
name|CHARACTER
argument_list|)
decl|.
name|put
argument_list|(
name|Types
operator|.
name|BINARY
argument_list|,
name|BINARY
argument_list|)
decl|.
name|put
argument_list|(
name|Types
operator|.
name|VARBINARY
argument_list|,
name|BINARY
argument_list|)
decl|.
name|put
argument_list|(
name|Types
operator|.
name|LONGVARBINARY
argument_list|,
name|BINARY
argument_list|)
decl|.
name|put
argument_list|(
name|Types
operator|.
name|BLOB
argument_list|,
name|BINARY
argument_list|)
decl|.
name|put
argument_list|(
name|Types
operator|.
name|DATE
argument_list|,
name|DATE
argument_list|)
decl|.
name|put
argument_list|(
name|Types
operator|.
name|TIME
argument_list|,
name|TIME
argument_list|)
decl|.
name|put
argument_list|(
name|ExtraSqlTypes
operator|.
name|TIME_WITH_TIMEZONE
argument_list|,
name|TIME
argument_list|)
decl|.
name|put
argument_list|(
name|Types
operator|.
name|TIMESTAMP
argument_list|,
name|TIMESTAMP
argument_list|)
decl|.
name|put
argument_list|(
name|ExtraSqlTypes
operator|.
name|TIMESTAMP_WITH_TIMEZONE
argument_list|,
name|TIMESTAMP
argument_list|)
decl|.
name|put
argument_list|(
name|Types
operator|.
name|BOOLEAN
argument_list|,
name|BOOLEAN
argument_list|)
decl|.
name|put
argument_list|(
name|ExtraSqlTypes
operator|.
name|REF_CURSOR
argument_list|,
name|CURSOR
argument_list|)
decl|.
name|put
argument_list|(
name|Types
operator|.
name|ARRAY
argument_list|,
name|ARRAY
argument_list|)
decl|.
name|build
argument_list|()
decl_stmt|;
comment|/**    * Gets the primary family containing a JDBC type.    *    * @param jdbcType the JDBC type of interest    * @return containing family    */
specifier|public
specifier|static
name|SqlTypeFamily
name|getFamilyForJdbcType
parameter_list|(
name|int
name|jdbcType
parameter_list|)
block|{
return|return
name|JDBC_TYPE_TO_FAMILY
operator|.
name|get
argument_list|(
name|jdbcType
argument_list|)
return|;
block|}
comment|/**    * @return collection of {@link SqlTypeName}s included in this family    */
specifier|public
name|Collection
argument_list|<
name|SqlTypeName
argument_list|>
name|getTypeNames
parameter_list|()
block|{
switch|switch
condition|(
name|this
condition|)
block|{
case|case
name|CHARACTER
case|:
return|return
name|SqlTypeName
operator|.
name|CHAR_TYPES
return|;
case|case
name|BINARY
case|:
return|return
name|SqlTypeName
operator|.
name|BINARY_TYPES
return|;
case|case
name|NUMERIC
case|:
return|return
name|SqlTypeName
operator|.
name|NUMERIC_TYPES
return|;
case|case
name|DECIMAL
case|:
return|return
name|ImmutableList
operator|.
name|of
argument_list|(
name|SqlTypeName
operator|.
name|DECIMAL
argument_list|)
return|;
case|case
name|DATE
case|:
return|return
name|ImmutableList
operator|.
name|of
argument_list|(
name|SqlTypeName
operator|.
name|DATE
argument_list|)
return|;
case|case
name|TIME
case|:
return|return
name|ImmutableList
operator|.
name|of
argument_list|(
name|SqlTypeName
operator|.
name|TIME
argument_list|,
name|SqlTypeName
operator|.
name|TIME_WITH_LOCAL_TIME_ZONE
argument_list|)
return|;
case|case
name|TIMESTAMP
case|:
return|return
name|ImmutableList
operator|.
name|of
argument_list|(
name|SqlTypeName
operator|.
name|TIMESTAMP
argument_list|,
name|SqlTypeName
operator|.
name|TIMESTAMP_WITH_LOCAL_TIME_ZONE
argument_list|)
return|;
case|case
name|BOOLEAN
case|:
return|return
name|SqlTypeName
operator|.
name|BOOLEAN_TYPES
return|;
case|case
name|INTERVAL_YEAR_MONTH
case|:
return|return
name|SqlTypeName
operator|.
name|YEAR_INTERVAL_TYPES
return|;
case|case
name|INTERVAL_DAY_TIME
case|:
return|return
name|SqlTypeName
operator|.
name|DAY_INTERVAL_TYPES
return|;
case|case
name|STRING
case|:
return|return
name|SqlTypeName
operator|.
name|STRING_TYPES
return|;
case|case
name|APPROXIMATE_NUMERIC
case|:
return|return
name|SqlTypeName
operator|.
name|APPROX_TYPES
return|;
case|case
name|EXACT_NUMERIC
case|:
return|return
name|SqlTypeName
operator|.
name|EXACT_TYPES
return|;
case|case
name|INTEGER
case|:
return|return
name|SqlTypeName
operator|.
name|INT_TYPES
return|;
case|case
name|DATETIME
case|:
return|return
name|SqlTypeName
operator|.
name|DATETIME_TYPES
return|;
case|case
name|DATETIME_INTERVAL
case|:
return|return
name|SqlTypeName
operator|.
name|INTERVAL_TYPES
return|;
case|case
name|GEO
case|:
return|return
name|ImmutableList
operator|.
name|of
argument_list|(
name|SqlTypeName
operator|.
name|GEOMETRY
argument_list|)
return|;
case|case
name|MULTISET
case|:
return|return
name|ImmutableList
operator|.
name|of
argument_list|(
name|SqlTypeName
operator|.
name|MULTISET
argument_list|)
return|;
case|case
name|ARRAY
case|:
return|return
name|ImmutableList
operator|.
name|of
argument_list|(
name|SqlTypeName
operator|.
name|ARRAY
argument_list|)
return|;
case|case
name|MAP
case|:
return|return
name|ImmutableList
operator|.
name|of
argument_list|(
name|SqlTypeName
operator|.
name|MAP
argument_list|)
return|;
case|case
name|NULL
case|:
return|return
name|ImmutableList
operator|.
name|of
argument_list|(
name|SqlTypeName
operator|.
name|NULL
argument_list|)
return|;
case|case
name|ANY
case|:
return|return
name|SqlTypeName
operator|.
name|ALL_TYPES
return|;
case|case
name|CURSOR
case|:
return|return
name|ImmutableList
operator|.
name|of
argument_list|(
name|SqlTypeName
operator|.
name|CURSOR
argument_list|)
return|;
case|case
name|COLUMN_LIST
case|:
return|return
name|ImmutableList
operator|.
name|of
argument_list|(
name|SqlTypeName
operator|.
name|COLUMN_LIST
argument_list|)
return|;
default|default:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|()
throw|;
block|}
block|}
comment|/**    * @return Default {@link RelDataType} belongs to this family.    */
specifier|public
name|RelDataType
name|getDefaultConcreteType
parameter_list|(
name|RelDataTypeFactory
name|factory
parameter_list|)
block|{
switch|switch
condition|(
name|this
condition|)
block|{
case|case
name|CHARACTER
case|:
return|return
name|factory
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|VARCHAR
argument_list|)
return|;
case|case
name|BINARY
case|:
return|return
name|factory
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|VARBINARY
argument_list|)
return|;
case|case
name|NUMERIC
case|:
return|return
name|SqlTypeUtil
operator|.
name|getMaxPrecisionScaleDecimal
argument_list|(
name|factory
argument_list|)
return|;
case|case
name|DATE
case|:
return|return
name|factory
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|DATE
argument_list|)
return|;
case|case
name|TIME
case|:
return|return
name|factory
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|TIME
argument_list|)
return|;
case|case
name|TIMESTAMP
case|:
return|return
name|factory
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|TIMESTAMP
argument_list|)
return|;
case|case
name|BOOLEAN
case|:
return|return
name|factory
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|BOOLEAN
argument_list|)
return|;
case|case
name|STRING
case|:
return|return
name|factory
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|VARCHAR
argument_list|)
return|;
case|case
name|APPROXIMATE_NUMERIC
case|:
return|return
name|factory
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|DOUBLE
argument_list|)
return|;
case|case
name|EXACT_NUMERIC
case|:
return|return
name|SqlTypeUtil
operator|.
name|getMaxPrecisionScaleDecimal
argument_list|(
name|factory
argument_list|)
return|;
case|case
name|INTEGER
case|:
return|return
name|factory
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|BIGINT
argument_list|)
return|;
case|case
name|DECIMAL
case|:
return|return
name|factory
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|DECIMAL
argument_list|)
return|;
case|case
name|DATETIME
case|:
return|return
name|factory
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|TIMESTAMP
argument_list|)
return|;
case|case
name|INTERVAL_DAY_TIME
case|:
return|return
name|factory
operator|.
name|createSqlIntervalType
argument_list|(
operator|new
name|SqlIntervalQualifier
argument_list|(
name|TimeUnit
operator|.
name|DAY
argument_list|,
name|TimeUnit
operator|.
name|SECOND
argument_list|,
name|SqlParserPos
operator|.
name|ZERO
argument_list|)
argument_list|)
return|;
case|case
name|INTERVAL_YEAR_MONTH
case|:
return|return
name|factory
operator|.
name|createSqlIntervalType
argument_list|(
operator|new
name|SqlIntervalQualifier
argument_list|(
name|TimeUnit
operator|.
name|YEAR
argument_list|,
name|TimeUnit
operator|.
name|MONTH
argument_list|,
name|SqlParserPos
operator|.
name|ZERO
argument_list|)
argument_list|)
return|;
case|case
name|GEO
case|:
return|return
name|factory
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|GEOMETRY
argument_list|)
return|;
case|case
name|MULTISET
case|:
return|return
name|factory
operator|.
name|createMultisetType
argument_list|(
name|factory
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|ANY
argument_list|)
argument_list|,
operator|-
literal|1
argument_list|)
return|;
case|case
name|ARRAY
case|:
return|return
name|factory
operator|.
name|createArrayType
argument_list|(
name|factory
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|ANY
argument_list|)
argument_list|,
operator|-
literal|1
argument_list|)
return|;
case|case
name|MAP
case|:
return|return
name|factory
operator|.
name|createMapType
argument_list|(
name|factory
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|ANY
argument_list|)
argument_list|,
name|factory
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|ANY
argument_list|)
argument_list|)
return|;
case|case
name|NULL
case|:
return|return
name|factory
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|NULL
argument_list|)
return|;
case|case
name|CURSOR
case|:
return|return
name|factory
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|CURSOR
argument_list|)
return|;
case|case
name|COLUMN_LIST
case|:
return|return
name|factory
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|COLUMN_LIST
argument_list|)
return|;
default|default:
return|return
literal|null
return|;
block|}
block|}
specifier|public
name|boolean
name|contains
parameter_list|(
name|RelDataType
name|type
parameter_list|)
block|{
return|return
name|SqlTypeUtil
operator|.
name|isOfSameTypeName
argument_list|(
name|getTypeNames
argument_list|()
argument_list|,
name|type
argument_list|)
return|;
block|}
block|}
end_enum

begin_comment
comment|// End SqlTypeFamily.java
end_comment

end_unit

