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
name|util
package|;
end_package

begin_import
import|import
name|org
operator|.
name|checkerframework
operator|.
name|checker
operator|.
name|nullness
operator|.
name|qual
operator|.
name|Nullable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|math
operator|.
name|BigDecimal
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|ResultSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|SQLException
import|;
end_import

begin_comment
comment|/**  * Maps Java types to their corresponding getters in JDBC.  *  *<p>In future we could add more types,  * and add more methods  * (e.g. {@link java.sql.PreparedStatement#setInt(int, int)}).  *  * @param<T> Value type  */
end_comment

begin_annotation
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
end_annotation

begin_expr_stmt
specifier|public
expr|interface
name|JdbcType
operator|<
name|T
expr|extends @
name|Nullable
name|Object
operator|>
block|{
name|JdbcType
argument_list|<
name|Boolean
argument_list|>
name|BOOLEAN
operator|=
name|JdbcTypeImpl
operator|.
name|BOOLEAN
block|;
name|JdbcType
argument_list|<
annotation|@
name|Nullable
name|Boolean
argument_list|>
name|BOOLEAN_NULLABLE
operator|=
name|JdbcTypeImpl
operator|.
name|BOOLEAN_NULLABLE
block|;
name|JdbcType
argument_list|<
name|BigDecimal
argument_list|>
name|BIG_DECIMAL
operator|=
name|JdbcTypeImpl
operator|.
name|BIG_DECIMAL
block|;
name|JdbcType
argument_list|<
annotation|@
name|Nullable
name|BigDecimal
argument_list|>
name|BIG_DECIMAL_NULLABLE
operator|=
name|JdbcTypeImpl
operator|.
name|BIG_DECIMAL_NULLABLE
block|;
name|JdbcType
argument_list|<
name|Double
argument_list|>
name|DOUBLE
operator|=
name|JdbcTypeImpl
operator|.
name|DOUBLE
block|;
name|JdbcType
argument_list|<
annotation|@
name|Nullable
name|Double
argument_list|>
name|DOUBLE_NULLABLE
operator|=
name|JdbcTypeImpl
operator|.
name|DOUBLE_NULLABLE
block|;
name|JdbcType
argument_list|<
name|Integer
argument_list|>
name|INTEGER
operator|=
name|JdbcTypeImpl
operator|.
name|INTEGER
block|;
name|JdbcType
argument_list|<
annotation|@
name|Nullable
name|Integer
argument_list|>
name|INTEGER_NULLABLE
operator|=
name|JdbcTypeImpl
operator|.
name|INTEGER_NULLABLE
block|;
name|JdbcType
argument_list|<
name|String
argument_list|>
name|STRING
operator|=
name|JdbcTypeImpl
operator|.
name|STRING
block|;
name|JdbcType
argument_list|<
annotation|@
name|Nullable
name|String
argument_list|>
name|STRING_NULLABLE
operator|=
name|JdbcTypeImpl
operator|.
name|STRING_NULLABLE
block|;
comment|/** Returns the value of column {@code column} from a JDBC result set.    *    *<p>For example, {@code INTEGER.get(i, resultSet)} calls    * {@link ResultSet#getInt(int)}. */
name|T
name|get
argument_list|(
name|int
name|column
argument_list|,
name|ResultSet
name|resultSet
argument_list|)
throws|throws
name|SQLException
block|; }
end_expr_stmt

end_unit
