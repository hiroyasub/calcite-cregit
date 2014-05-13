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
name|test
package|;
end_package

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
name|Types
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
import|;
end_import

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
name|type
operator|.
name|*
import|;
end_import

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|avatica
operator|.
name|ColumnMetaData
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|eigenbase
operator|.
name|sql
operator|.
name|test
operator|.
name|SqlTester
operator|.
name|*
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|CoreMatchers
operator|.
name|*
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Utility methods.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|SqlTests
block|{
comment|//~ Static fields/initializers ---------------------------------------------
specifier|public
specifier|static
specifier|final
name|TypeChecker
name|INTEGER_TYPE_CHECKER
init|=
operator|new
name|SqlTypeChecker
argument_list|(
name|SqlTypeName
operator|.
name|INTEGER
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|TypeChecker
name|BOOLEAN_TYPE_CHECKER
init|=
operator|new
name|SqlTypeChecker
argument_list|(
name|SqlTypeName
operator|.
name|BOOLEAN
argument_list|)
decl_stmt|;
comment|/**    * Checker which allows any type.    */
specifier|public
specifier|static
specifier|final
name|TypeChecker
name|ANY_TYPE_CHECKER
init|=
operator|new
name|TypeChecker
argument_list|()
block|{
specifier|public
name|void
name|checkType
parameter_list|(
name|RelDataType
name|type
parameter_list|)
block|{
block|}
block|}
decl_stmt|;
comment|/**    * Helper function to get the string representation of a RelDataType    * (include precision/scale but no charset or collation)    *    * @param sqlType Type    * @return String representation of type    */
specifier|public
specifier|static
name|String
name|getTypeString
parameter_list|(
name|RelDataType
name|sqlType
parameter_list|)
block|{
switch|switch
condition|(
name|sqlType
operator|.
name|getSqlTypeName
argument_list|()
condition|)
block|{
case|case
name|VARCHAR
case|:
name|String
name|actual
init|=
literal|"VARCHAR("
operator|+
name|sqlType
operator|.
name|getPrecision
argument_list|()
operator|+
literal|")"
decl_stmt|;
return|return
name|sqlType
operator|.
name|isNullable
argument_list|()
condition|?
name|actual
else|:
operator|(
name|actual
operator|+
literal|" NOT NULL"
operator|)
return|;
case|case
name|CHAR
case|:
name|actual
operator|=
literal|"CHAR("
operator|+
name|sqlType
operator|.
name|getPrecision
argument_list|()
operator|+
literal|")"
expr_stmt|;
return|return
name|sqlType
operator|.
name|isNullable
argument_list|()
condition|?
name|actual
else|:
operator|(
name|actual
operator|+
literal|" NOT NULL"
operator|)
return|;
default|default:
comment|// Get rid of the verbose charset/collation stuff.
comment|// TODO: There's probably a better way to do this.
specifier|final
name|String
name|s
init|=
name|sqlType
operator|.
name|getFullTypeString
argument_list|()
decl_stmt|;
return|return
name|s
operator|.
name|replace
argument_list|(
literal|" CHARACTER SET \"ISO-8859-1\" COLLATE \"ISO-8859-1$en_US$primary\""
argument_list|,
literal|""
argument_list|)
return|;
block|}
block|}
specifier|public
specifier|static
name|String
name|generateAggQuery
parameter_list|(
name|String
name|expr
parameter_list|,
name|String
index|[]
name|inputValues
parameter_list|)
block|{
name|StringBuilder
name|buf
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|"SELECT "
argument_list|)
operator|.
name|append
argument_list|(
name|expr
argument_list|)
operator|.
name|append
argument_list|(
literal|" FROM "
argument_list|)
expr_stmt|;
if|if
condition|(
name|inputValues
operator|.
name|length
operator|==
literal|0
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
literal|"(VALUES 1) AS t(x) WHERE false"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|buf
operator|.
name|append
argument_list|(
literal|"("
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|inputValues
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|i
operator|>
literal|0
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
literal|" UNION ALL "
argument_list|)
expr_stmt|;
block|}
name|buf
operator|.
name|append
argument_list|(
literal|"SELECT "
argument_list|)
expr_stmt|;
name|String
name|inputValue
init|=
name|inputValues
index|[
name|i
index|]
decl_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|inputValue
argument_list|)
operator|.
name|append
argument_list|(
literal|" AS x FROM (VALUES (1))"
argument_list|)
expr_stmt|;
block|}
name|buf
operator|.
name|append
argument_list|(
literal|")"
argument_list|)
expr_stmt|;
block|}
return|return
name|buf
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|public
specifier|static
name|String
name|generateWinAggQuery
parameter_list|(
name|String
name|expr
parameter_list|,
name|String
name|windowSpec
parameter_list|,
name|String
index|[]
name|inputValues
parameter_list|)
block|{
name|StringBuilder
name|buf
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|"SELECT "
argument_list|)
operator|.
name|append
argument_list|(
name|expr
argument_list|)
operator|.
name|append
argument_list|(
literal|" OVER ("
argument_list|)
operator|.
name|append
argument_list|(
name|windowSpec
argument_list|)
operator|.
name|append
argument_list|(
literal|") FROM ("
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|inputValues
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|i
operator|>
literal|0
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
literal|" UNION ALL "
argument_list|)
expr_stmt|;
block|}
name|buf
operator|.
name|append
argument_list|(
literal|"SELECT "
argument_list|)
expr_stmt|;
name|String
name|inputValue
init|=
name|inputValues
index|[
name|i
index|]
decl_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|inputValue
argument_list|)
operator|.
name|append
argument_list|(
literal|" AS x FROM (VALUES (1))"
argument_list|)
expr_stmt|;
block|}
name|buf
operator|.
name|append
argument_list|(
literal|")"
argument_list|)
expr_stmt|;
return|return
name|buf
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * Compares the first column of a result set against a String-valued    * reference set, disregarding order entirely.    *    * @param resultSet Result set    * @param refSet    Expected results    * @throws Exception .    */
specifier|public
specifier|static
name|void
name|compareResultSet
parameter_list|(
name|ResultSet
name|resultSet
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|refSet
parameter_list|)
throws|throws
name|Exception
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|actualSet
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|int
name|columnType
init|=
name|resultSet
operator|.
name|getMetaData
argument_list|()
operator|.
name|getColumnType
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|final
name|ColumnMetaData
operator|.
name|Rep
name|rep
init|=
name|rep
argument_list|(
name|columnType
argument_list|)
decl_stmt|;
while|while
condition|(
name|resultSet
operator|.
name|next
argument_list|()
condition|)
block|{
specifier|final
name|String
name|s
init|=
name|resultSet
operator|.
name|getString
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|final
name|String
name|s0
init|=
name|s
operator|==
literal|null
condition|?
literal|"0"
else|:
name|s
decl_stmt|;
specifier|final
name|boolean
name|wasNull0
init|=
name|resultSet
operator|.
name|wasNull
argument_list|()
decl_stmt|;
name|actualSet
operator|.
name|add
argument_list|(
name|s
argument_list|)
expr_stmt|;
switch|switch
condition|(
name|rep
condition|)
block|{
case|case
name|BOOLEAN
case|:
name|assertThat
argument_list|(
name|resultSet
operator|.
name|getBoolean
argument_list|(
literal|1
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|Boolean
operator|.
name|valueOf
argument_list|(
name|s
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
name|BYTE
case|:
case|case
name|SHORT
case|:
case|case
name|INTEGER
case|:
case|case
name|LONG
case|:
specifier|final
name|long
name|l
init|=
name|Long
operator|.
name|parseLong
argument_list|(
name|s0
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|resultSet
operator|.
name|getByte
argument_list|(
literal|1
argument_list|)
argument_list|,
name|equalTo
argument_list|(
operator|(
name|byte
operator|)
name|l
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|resultSet
operator|.
name|getShort
argument_list|(
literal|1
argument_list|)
argument_list|,
name|equalTo
argument_list|(
operator|(
name|short
operator|)
name|l
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|resultSet
operator|.
name|getInt
argument_list|(
literal|1
argument_list|)
argument_list|,
name|equalTo
argument_list|(
operator|(
name|int
operator|)
name|l
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|resultSet
operator|.
name|getLong
argument_list|(
literal|1
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|l
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
name|FLOAT
case|:
case|case
name|DOUBLE
case|:
specifier|final
name|double
name|d
init|=
name|Double
operator|.
name|parseDouble
argument_list|(
name|s0
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|resultSet
operator|.
name|getFloat
argument_list|(
literal|1
argument_list|)
argument_list|,
name|equalTo
argument_list|(
operator|(
name|float
operator|)
name|d
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|resultSet
operator|.
name|getDouble
argument_list|(
literal|1
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|d
argument_list|)
argument_list|)
expr_stmt|;
break|break;
block|}
specifier|final
name|boolean
name|wasNull1
init|=
name|resultSet
operator|.
name|wasNull
argument_list|()
decl_stmt|;
specifier|final
name|Object
name|object
init|=
name|resultSet
operator|.
name|getObject
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|final
name|boolean
name|wasNull2
init|=
name|resultSet
operator|.
name|wasNull
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|object
operator|==
literal|null
argument_list|,
name|equalTo
argument_list|(
name|wasNull0
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|wasNull1
argument_list|,
name|equalTo
argument_list|(
name|wasNull0
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|wasNull2
argument_list|,
name|equalTo
argument_list|(
name|wasNull0
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|resultSet
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|refSet
argument_list|,
name|actualSet
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|ColumnMetaData
operator|.
name|Rep
name|rep
parameter_list|(
name|int
name|columnType
parameter_list|)
block|{
switch|switch
condition|(
name|columnType
condition|)
block|{
case|case
name|Types
operator|.
name|BOOLEAN
case|:
return|return
name|ColumnMetaData
operator|.
name|Rep
operator|.
name|BOOLEAN
return|;
case|case
name|Types
operator|.
name|TINYINT
case|:
return|return
name|ColumnMetaData
operator|.
name|Rep
operator|.
name|BYTE
return|;
case|case
name|Types
operator|.
name|SMALLINT
case|:
return|return
name|ColumnMetaData
operator|.
name|Rep
operator|.
name|SHORT
return|;
case|case
name|Types
operator|.
name|INTEGER
case|:
return|return
name|ColumnMetaData
operator|.
name|Rep
operator|.
name|INTEGER
return|;
case|case
name|Types
operator|.
name|BIGINT
case|:
return|return
name|ColumnMetaData
operator|.
name|Rep
operator|.
name|LONG
return|;
case|case
name|Types
operator|.
name|FLOAT
case|:
return|return
name|ColumnMetaData
operator|.
name|Rep
operator|.
name|FLOAT
return|;
case|case
name|Types
operator|.
name|DOUBLE
case|:
return|return
name|ColumnMetaData
operator|.
name|Rep
operator|.
name|DOUBLE
return|;
case|case
name|Types
operator|.
name|TIME
case|:
return|return
name|ColumnMetaData
operator|.
name|Rep
operator|.
name|JAVA_SQL_TIME
return|;
case|case
name|Types
operator|.
name|TIMESTAMP
case|:
return|return
name|ColumnMetaData
operator|.
name|Rep
operator|.
name|JAVA_SQL_TIMESTAMP
return|;
case|case
name|Types
operator|.
name|DATE
case|:
return|return
name|ColumnMetaData
operator|.
name|Rep
operator|.
name|JAVA_SQL_DATE
return|;
default|default:
return|return
name|ColumnMetaData
operator|.
name|Rep
operator|.
name|OBJECT
return|;
block|}
block|}
comment|/**    * Compares the first column of a result set against a pattern. The result    * set must return exactly one row.    *    * @param resultSet Result set    * @param pattern   Expected pattern    */
specifier|public
specifier|static
name|void
name|compareResultSetWithPattern
parameter_list|(
name|ResultSet
name|resultSet
parameter_list|,
name|Pattern
name|pattern
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
operator|!
name|resultSet
operator|.
name|next
argument_list|()
condition|)
block|{
name|fail
argument_list|(
literal|"Query returned 0 rows, expected 1"
argument_list|)
expr_stmt|;
block|}
name|String
name|actual
init|=
name|resultSet
operator|.
name|getString
argument_list|(
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|resultSet
operator|.
name|next
argument_list|()
condition|)
block|{
name|fail
argument_list|(
literal|"Query returned 2 or more rows, expected 1"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|pattern
operator|.
name|matcher
argument_list|(
name|actual
argument_list|)
operator|.
name|matches
argument_list|()
condition|)
block|{
name|fail
argument_list|(
literal|"Query returned '"
operator|+
name|actual
operator|+
literal|"', expected '"
operator|+
name|pattern
operator|.
name|pattern
argument_list|()
operator|+
literal|"'"
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Compares the first column of a result set against a numeric result,    * within a given tolerance. The result set must return exactly one row.    *    * @param resultSet Result set    * @param expected  Expected result    * @param delta     Tolerance    */
specifier|public
specifier|static
name|void
name|compareResultSetWithDelta
parameter_list|(
name|ResultSet
name|resultSet
parameter_list|,
name|double
name|expected
parameter_list|,
name|double
name|delta
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
operator|!
name|resultSet
operator|.
name|next
argument_list|()
condition|)
block|{
name|fail
argument_list|(
literal|"Query returned 0 rows, expected 1"
argument_list|)
expr_stmt|;
block|}
name|double
name|actual
init|=
name|resultSet
operator|.
name|getDouble
argument_list|(
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|resultSet
operator|.
name|next
argument_list|()
condition|)
block|{
name|fail
argument_list|(
literal|"Query returned 2 or more rows, expected 1"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|(
name|actual
operator|<
operator|(
name|expected
operator|-
name|delta
operator|)
operator|)
operator|||
operator|(
name|actual
operator|>
operator|(
name|expected
operator|+
name|delta
operator|)
operator|)
condition|)
block|{
name|fail
argument_list|(
literal|"Query returned "
operator|+
name|actual
operator|+
literal|", expected "
operator|+
name|expected
operator|+
operator|(
operator|(
name|delta
operator|==
literal|0
operator|)
condition|?
literal|""
else|:
operator|(
literal|"+/-"
operator|+
name|delta
operator|)
operator|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Compares the first column of a result set against a String-valued    * reference set, taking order into account.    *    * @param resultSet Result set    * @param refList   Expected results    * @throws Exception .    */
specifier|public
specifier|static
name|void
name|compareResultList
parameter_list|(
name|ResultSet
name|resultSet
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|refList
parameter_list|)
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|String
argument_list|>
name|actualSet
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
while|while
condition|(
name|resultSet
operator|.
name|next
argument_list|()
condition|)
block|{
name|String
name|s
init|=
name|resultSet
operator|.
name|getString
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|actualSet
operator|.
name|add
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
name|resultSet
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|refList
argument_list|,
name|actualSet
argument_list|)
expr_stmt|;
block|}
comment|/**    * Compares the columns of a result set against several String-valued    * reference lists, taking order into account.    *    * @param resultSet Result set    * @param refLists  vararg of List&lt;String&gt;. The first list is compared    *                  to the first column, the second list to the second column    *                  and so on    */
specifier|public
specifier|static
name|void
name|compareResultLists
parameter_list|(
name|ResultSet
name|resultSet
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
modifier|...
name|refLists
parameter_list|)
throws|throws
name|Exception
block|{
name|int
name|numExpectedColumns
init|=
name|refLists
operator|.
name|length
decl_stmt|;
name|assertTrue
argument_list|(
name|numExpectedColumns
operator|>
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|resultSet
operator|.
name|getMetaData
argument_list|()
operator|.
name|getColumnCount
argument_list|()
operator|>=
name|numExpectedColumns
argument_list|)
expr_stmt|;
name|int
name|numExpectedRows
init|=
operator|-
literal|1
decl_stmt|;
name|List
argument_list|<
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|actualLists
init|=
operator|new
name|ArrayList
argument_list|<
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numExpectedColumns
condition|;
name|i
operator|++
control|)
block|{
name|actualLists
operator|.
name|add
argument_list|(
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|==
literal|0
condition|)
block|{
name|numExpectedRows
operator|=
name|refLists
index|[
name|i
index|]
operator|.
name|size
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|assertEquals
argument_list|(
literal|"num rows differ across ref lists"
argument_list|,
name|numExpectedRows
argument_list|,
name|refLists
index|[
name|i
index|]
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
while|while
condition|(
name|resultSet
operator|.
name|next
argument_list|()
condition|)
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
name|numExpectedColumns
condition|;
name|i
operator|++
control|)
block|{
name|String
name|s
init|=
name|resultSet
operator|.
name|getString
argument_list|(
name|i
operator|+
literal|1
argument_list|)
decl_stmt|;
name|actualLists
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|add
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
block|}
name|resultSet
operator|.
name|close
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numExpectedColumns
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
literal|"column mismatch in column "
operator|+
operator|(
name|i
operator|+
literal|1
operator|)
argument_list|,
name|refLists
index|[
name|i
index|]
argument_list|,
name|actualLists
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|//~ Inner Classes ----------------------------------------------------------
comment|/**    * Checks that a type matches a given SQL type. Does not care about    * nullability.    */
specifier|private
specifier|static
class|class
name|SqlTypeChecker
implements|implements
name|TypeChecker
block|{
specifier|private
specifier|final
name|SqlTypeName
name|typeName
decl_stmt|;
name|SqlTypeChecker
parameter_list|(
name|SqlTypeName
name|typeName
parameter_list|)
block|{
name|this
operator|.
name|typeName
operator|=
name|typeName
expr_stmt|;
block|}
specifier|public
name|void
name|checkType
parameter_list|(
name|RelDataType
name|type
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|typeName
operator|.
name|toString
argument_list|()
argument_list|,
name|type
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Type checker which compares types to a specified string.    *    *<p>The string contains "NOT NULL" constraints, but does not contain    * collations and charsets. For example,    *    *<ul>    *<li><code>INTEGER NOT NULL</code></li>    *<li><code>BOOLEAN</code></li>    *<li><code>DOUBLE NOT NULL MULTISET NOT NULL</code></li>    *<li><code>CHAR(3) NOT NULL</code></li>    *<li><code>RecordType(INTEGER X, VARCHAR(10) Y)</code></li>    *</ul>    */
specifier|public
specifier|static
class|class
name|StringTypeChecker
implements|implements
name|TypeChecker
block|{
specifier|private
specifier|final
name|String
name|expected
decl_stmt|;
specifier|public
name|StringTypeChecker
parameter_list|(
name|String
name|expected
parameter_list|)
block|{
name|this
operator|.
name|expected
operator|=
name|expected
expr_stmt|;
block|}
specifier|public
name|void
name|checkType
parameter_list|(
name|RelDataType
name|type
parameter_list|)
block|{
name|String
name|actual
init|=
name|getTypeString
argument_list|(
name|type
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|actual
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
specifier|static
name|ResultChecker
name|createChecker
parameter_list|(
name|Object
name|result
parameter_list|,
name|double
name|delta
parameter_list|)
block|{
if|if
condition|(
name|result
operator|instanceof
name|Pattern
condition|)
block|{
return|return
operator|new
name|PatternResultChecker
argument_list|(
operator|(
name|Pattern
operator|)
name|result
argument_list|)
return|;
block|}
if|else if
condition|(
name|delta
operator|!=
literal|0
condition|)
block|{
name|assertTrue
argument_list|(
name|result
operator|instanceof
name|Number
argument_list|)
expr_stmt|;
return|return
operator|new
name|ApproximateResultChecker
argument_list|(
operator|(
name|Number
operator|)
name|result
argument_list|,
name|delta
argument_list|)
return|;
block|}
else|else
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|refSet
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
if|if
condition|(
name|result
operator|==
literal|null
condition|)
block|{
name|refSet
operator|.
name|add
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|result
operator|instanceof
name|Collection
condition|)
block|{
name|refSet
operator|.
name|addAll
argument_list|(
operator|(
name|Collection
argument_list|<
name|String
argument_list|>
operator|)
name|result
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|refSet
operator|.
name|add
argument_list|(
name|result
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|RefSetResultChecker
argument_list|(
name|refSet
argument_list|)
return|;
block|}
block|}
comment|/**    * Result checker that checks a result against a regular expression.    */
specifier|public
specifier|static
class|class
name|PatternResultChecker
implements|implements
name|ResultChecker
block|{
specifier|private
specifier|final
name|Pattern
name|pattern
decl_stmt|;
specifier|public
name|PatternResultChecker
parameter_list|(
name|Pattern
name|pattern
parameter_list|)
block|{
name|this
operator|.
name|pattern
operator|=
name|pattern
expr_stmt|;
block|}
specifier|public
name|void
name|checkResult
parameter_list|(
name|ResultSet
name|resultSet
parameter_list|)
throws|throws
name|Exception
block|{
name|compareResultSetWithPattern
argument_list|(
name|resultSet
argument_list|,
name|pattern
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Result checker that checks a result against an expected value. A delta    * value is used for approximate values (double and float).    */
specifier|public
specifier|static
class|class
name|ApproximateResultChecker
implements|implements
name|ResultChecker
block|{
specifier|private
specifier|final
name|Number
name|expected
decl_stmt|;
specifier|private
specifier|final
name|double
name|delta
decl_stmt|;
specifier|public
name|ApproximateResultChecker
parameter_list|(
name|Number
name|expected
parameter_list|,
name|double
name|delta
parameter_list|)
block|{
name|this
operator|.
name|expected
operator|=
name|expected
expr_stmt|;
name|this
operator|.
name|delta
operator|=
name|delta
expr_stmt|;
block|}
specifier|public
name|void
name|checkResult
parameter_list|(
name|ResultSet
name|resultSet
parameter_list|)
throws|throws
name|Exception
block|{
name|compareResultSetWithDelta
argument_list|(
name|resultSet
argument_list|,
name|expected
operator|.
name|doubleValue
argument_list|()
argument_list|,
name|delta
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Result checker that checks a result against a list of expected strings.    */
specifier|public
specifier|static
class|class
name|RefSetResultChecker
implements|implements
name|ResultChecker
block|{
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|expected
decl_stmt|;
specifier|private
name|RefSetResultChecker
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|expected
parameter_list|)
block|{
name|this
operator|.
name|expected
operator|=
name|expected
expr_stmt|;
block|}
specifier|public
name|void
name|checkResult
parameter_list|(
name|ResultSet
name|resultSet
parameter_list|)
throws|throws
name|Exception
block|{
name|compareResultSet
argument_list|(
name|resultSet
argument_list|,
name|expected
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End SqlTests.java
end_comment

end_unit

