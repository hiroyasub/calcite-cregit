begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to Julian Hyde under one or more contributor license // agreements. See the NOTICE file distributed with this work for // additional information regarding copyright ownership. // // Julian Hyde licenses this file to you under the Apache License, // Version 2.0 (the "License"); you may not use this file except in // compliance with the License. You may obtain a copy of the License at: // // http://www.apache.org/licenses/LICENSE-2.0 // // Unless required by applicable law or agreed to in writing, software // distributed under the License is distributed on an "AS IS" BASIS, // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. // See the License for the specific language governing permissions and // limitations under the License. */
end_comment

begin_package
package|package
name|net
operator|.
name|hydromatic
operator|.
name|optiq
package|;
end_package

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|linq4j
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
name|linq4j
operator|.
name|expressions
operator|.
name|FunctionExpression
import|;
end_import

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|linq4j
operator|.
name|expressions
operator|.
name|Primitive
import|;
end_import

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|linq4j
operator|.
name|expressions
operator|.
name|Types
import|;
end_import

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|linq4j
operator|.
name|function
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
name|optiq
operator|.
name|impl
operator|.
name|java
operator|.
name|ReflectiveSchema
import|;
end_import

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|impl
operator|.
name|jdbc
operator|.
name|JdbcSchema
import|;
end_import

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|runtime
operator|.
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Constructor
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Method
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
name|Time
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Timestamp
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
name|javax
operator|.
name|sql
operator|.
name|DataSource
import|;
end_import

begin_comment
comment|/**  * Builtin methods.  */
end_comment

begin_enum
specifier|public
enum|enum
name|BuiltinMethod
block|{
name|QUERYABLE_SELECT
argument_list|(
name|Queryable
operator|.
name|class
argument_list|,
literal|"select"
argument_list|,
name|FunctionExpression
operator|.
name|class
argument_list|)
block|,
name|AS_QUERYABLE
argument_list|(
name|Enumerable
operator|.
name|class
argument_list|,
literal|"asQueryable"
argument_list|)
block|,
name|ABSTRACT_ENUMERABLE_CTOR
argument_list|(
name|AbstractEnumerable
operator|.
name|class
argument_list|)
block|,
name|INTO
argument_list|(
name|ExtendedEnumerable
operator|.
name|class
argument_list|,
literal|"into"
argument_list|,
name|Collection
operator|.
name|class
argument_list|)
block|,
name|SCHEMA_GET_SUB_SCHEMA
argument_list|(
name|Schema
operator|.
name|class
argument_list|,
literal|"getSubSchema"
argument_list|,
name|String
operator|.
name|class
argument_list|)
block|,
name|SCHEMA_GET_TABLE
argument_list|(
name|Schema
operator|.
name|class
argument_list|,
literal|"getTable"
argument_list|,
name|String
operator|.
name|class
argument_list|,
name|Class
operator|.
name|class
argument_list|)
block|,
name|REFLECTIVE_SCHEMA_GET_TARGET
argument_list|(
name|ReflectiveSchema
operator|.
name|class
argument_list|,
literal|"getTarget"
argument_list|)
block|,
name|DATA_CONTEXT_GET
argument_list|(
name|DataContext
operator|.
name|class
argument_list|,
literal|"get"
argument_list|,
name|String
operator|.
name|class
argument_list|)
block|,
name|DATA_CONTEXT_GET_ROOT_SCHEMA
argument_list|(
name|DataContext
operator|.
name|class
argument_list|,
literal|"getRootSchema"
argument_list|)
block|,
name|JDBC_SCHEMA_DATA_SOURCE
argument_list|(
name|JdbcSchema
operator|.
name|class
argument_list|,
literal|"getDataSource"
argument_list|)
block|,
name|RESULT_SET_ENUMERABLE_OF
argument_list|(
name|ResultSetEnumerable
operator|.
name|class
argument_list|,
literal|"of"
argument_list|,
name|DataSource
operator|.
name|class
argument_list|,
name|String
operator|.
name|class
argument_list|,
name|Function1
operator|.
name|class
argument_list|)
block|,
name|JOIN
argument_list|(
name|ExtendedEnumerable
operator|.
name|class
argument_list|,
literal|"join"
argument_list|,
name|Enumerable
operator|.
name|class
argument_list|,
name|Function1
operator|.
name|class
argument_list|,
name|Function1
operator|.
name|class
argument_list|,
name|Function2
operator|.
name|class
argument_list|)
block|,
name|SELECT
argument_list|(
name|ExtendedEnumerable
operator|.
name|class
argument_list|,
literal|"select"
argument_list|,
name|Function1
operator|.
name|class
argument_list|)
block|,
name|SELECT2
argument_list|(
name|ExtendedEnumerable
operator|.
name|class
argument_list|,
literal|"select"
argument_list|,
name|Function2
operator|.
name|class
argument_list|)
block|,
name|WHERE
argument_list|(
name|ExtendedEnumerable
operator|.
name|class
argument_list|,
literal|"where"
argument_list|,
name|Predicate1
operator|.
name|class
argument_list|)
block|,
name|WHERE2
argument_list|(
name|ExtendedEnumerable
operator|.
name|class
argument_list|,
literal|"where"
argument_list|,
name|Predicate2
operator|.
name|class
argument_list|)
block|,
name|GROUP_BY
argument_list|(
name|ExtendedEnumerable
operator|.
name|class
argument_list|,
literal|"groupBy"
argument_list|,
name|Function1
operator|.
name|class
argument_list|)
block|,
name|GROUP_BY2
argument_list|(
name|ExtendedEnumerable
operator|.
name|class
argument_list|,
literal|"groupBy"
argument_list|,
name|Function1
operator|.
name|class
argument_list|,
name|Function0
operator|.
name|class
argument_list|,
name|Function2
operator|.
name|class
argument_list|,
name|Function2
operator|.
name|class
argument_list|)
block|,
name|AGGREGATE
argument_list|(
name|ExtendedEnumerable
operator|.
name|class
argument_list|,
literal|"aggregate"
argument_list|,
name|Object
operator|.
name|class
argument_list|,
name|Function2
operator|.
name|class
argument_list|,
name|Function1
operator|.
name|class
argument_list|)
block|,
name|ORDER_BY
argument_list|(
name|ExtendedEnumerable
operator|.
name|class
argument_list|,
literal|"orderBy"
argument_list|,
name|Function1
operator|.
name|class
argument_list|,
name|Comparator
operator|.
name|class
argument_list|)
block|,
name|UNION
argument_list|(
name|ExtendedEnumerable
operator|.
name|class
argument_list|,
literal|"union"
argument_list|,
name|Enumerable
operator|.
name|class
argument_list|)
block|,
name|CONCAT
argument_list|(
name|ExtendedEnumerable
operator|.
name|class
argument_list|,
literal|"concat"
argument_list|,
name|Enumerable
operator|.
name|class
argument_list|)
block|,
name|INTERSECT
argument_list|(
name|ExtendedEnumerable
operator|.
name|class
argument_list|,
literal|"intersect"
argument_list|,
name|Enumerable
operator|.
name|class
argument_list|)
block|,
name|EXCEPT
argument_list|(
name|ExtendedEnumerable
operator|.
name|class
argument_list|,
literal|"except"
argument_list|,
name|Enumerable
operator|.
name|class
argument_list|)
block|,
name|SKIP
argument_list|(
name|ExtendedEnumerable
operator|.
name|class
argument_list|,
literal|"skip"
argument_list|,
name|int
operator|.
name|class
argument_list|)
block|,
name|TAKE
argument_list|(
name|ExtendedEnumerable
operator|.
name|class
argument_list|,
literal|"take"
argument_list|,
name|int
operator|.
name|class
argument_list|)
block|,
name|SINGLETON_ENUMERABLE
argument_list|(
name|Linq4j
operator|.
name|class
argument_list|,
literal|"singletonEnumerable"
argument_list|,
name|Object
operator|.
name|class
argument_list|)
block|,
name|NULLS_COMPARATOR
argument_list|(
name|Functions
operator|.
name|class
argument_list|,
literal|"nullsComparator"
argument_list|,
name|boolean
operator|.
name|class
argument_list|,
name|boolean
operator|.
name|class
argument_list|)
block|,
name|ARRAY_COMPARER
argument_list|(
name|Functions
operator|.
name|class
argument_list|,
literal|"arrayComparer"
argument_list|)
block|,
name|ARRAYS_AS_LIST
argument_list|(
name|FlatLists
operator|.
name|class
argument_list|,
literal|"of"
argument_list|,
name|Object
index|[]
operator|.
expr|class
argument_list|)
block|,
name|LIST2
argument_list|(
name|FlatLists
operator|.
name|class
argument_list|,
literal|"of"
argument_list|,
name|Object
operator|.
name|class
argument_list|,
name|Object
operator|.
name|class
argument_list|)
block|,
name|LIST3
argument_list|(
name|FlatLists
operator|.
name|class
argument_list|,
literal|"of"
argument_list|,
name|Object
operator|.
name|class
argument_list|,
name|Object
operator|.
name|class
argument_list|,
name|Object
operator|.
name|class
argument_list|)
block|,
name|IDENTITY_SELECTOR
argument_list|(
name|Functions
operator|.
name|class
argument_list|,
literal|"identitySelector"
argument_list|)
block|,
name|AS_ENUMERABLE
argument_list|(
name|Linq4j
operator|.
name|class
argument_list|,
literal|"asEnumerable"
argument_list|,
name|Object
index|[]
operator|.
expr|class
argument_list|)
block|,
name|AS_ENUMERABLE2
argument_list|(
name|Linq4j
operator|.
name|class
argument_list|,
literal|"asEnumerable"
argument_list|,
name|Iterable
operator|.
name|class
argument_list|)
block|,
name|AS_LIST
argument_list|(
name|Primitive
operator|.
name|class
argument_list|,
literal|"asList"
argument_list|,
name|Object
operator|.
name|class
argument_list|)
block|,
name|ENUMERATOR_CURRENT
argument_list|(
name|Enumerator
operator|.
name|class
argument_list|,
literal|"current"
argument_list|)
block|,
name|ENUMERATOR_MOVE_NEXT
argument_list|(
name|Enumerator
operator|.
name|class
argument_list|,
literal|"moveNext"
argument_list|)
block|,
name|ENUMERATOR_CLOSE
argument_list|(
name|Enumerator
operator|.
name|class
argument_list|,
literal|"close"
argument_list|)
block|,
name|ENUMERATOR_RESET
argument_list|(
name|Enumerator
operator|.
name|class
argument_list|,
literal|"reset"
argument_list|)
block|,
name|ENUMERABLE_ENUMERATOR
argument_list|(
name|Enumerable
operator|.
name|class
argument_list|,
literal|"enumerator"
argument_list|)
block|,
name|ENUMERABLE_FOREACH
argument_list|(
name|Enumerable
operator|.
name|class
argument_list|,
literal|"foreach"
argument_list|,
name|Function1
operator|.
name|class
argument_list|)
block|,
name|TYPED_GET_ELEMENT_TYPE
argument_list|(
name|Typed
operator|.
name|class
argument_list|,
literal|"getElementType"
argument_list|)
block|,
name|BINDABLE_BIND
argument_list|(
name|Bindable
operator|.
name|class
argument_list|,
literal|"bind"
argument_list|,
name|DataContext
operator|.
name|class
argument_list|)
block|,
name|RESULT_SET_GET_DATE2
argument_list|(
name|ResultSet
operator|.
name|class
argument_list|,
literal|"getDate"
argument_list|,
name|int
operator|.
name|class
argument_list|,
name|Calendar
operator|.
name|class
argument_list|)
block|,
name|RESULT_SET_GET_TIME2
argument_list|(
name|ResultSet
operator|.
name|class
argument_list|,
literal|"getTime"
argument_list|,
name|int
operator|.
name|class
argument_list|,
name|Calendar
operator|.
name|class
argument_list|)
block|,
name|RESULT_SET_GET_TIMESTAMP2
argument_list|(
name|ResultSet
operator|.
name|class
argument_list|,
literal|"getTimestamp"
argument_list|,
name|int
operator|.
name|class
argument_list|,
name|Calendar
operator|.
name|class
argument_list|)
block|,
name|TIME_ZONE_GET_OFFSET
argument_list|(
name|TimeZone
operator|.
name|class
argument_list|,
literal|"getOffset"
argument_list|,
name|long
operator|.
name|class
argument_list|)
block|,
name|LONG_VALUE
argument_list|(
name|Number
operator|.
name|class
argument_list|,
literal|"longValue"
argument_list|)
block|,
name|COMPARATOR_COMPARE
argument_list|(
name|Comparator
operator|.
name|class
argument_list|,
literal|"compare"
argument_list|,
name|Object
operator|.
name|class
argument_list|,
name|Object
operator|.
name|class
argument_list|)
block|,
name|COLLECTIONS_REVERSE_ORDER
argument_list|(
name|Collections
operator|.
name|class
argument_list|,
literal|"reverseOrder"
argument_list|)
block|,
name|COLLECTIONS_EMPTY_LIST
argument_list|(
name|Collections
operator|.
name|class
argument_list|,
literal|"emptyList"
argument_list|)
block|,
name|COLLECTIONS_SINGLETON_LIST
argument_list|(
name|Collections
operator|.
name|class
argument_list|,
literal|"singletonList"
argument_list|,
name|Object
operator|.
name|class
argument_list|)
block|,
name|COLLECTION_SIZE
argument_list|(
name|Collection
operator|.
name|class
argument_list|,
literal|"size"
argument_list|)
block|,
name|MAP_CLEAR
argument_list|(
name|Map
operator|.
name|class
argument_list|,
literal|"clear"
argument_list|)
block|,
name|MAP_GET
argument_list|(
name|Map
operator|.
name|class
argument_list|,
literal|"get"
argument_list|,
name|Object
operator|.
name|class
argument_list|)
block|,
name|MAP_PUT
argument_list|(
name|Map
operator|.
name|class
argument_list|,
literal|"put"
argument_list|,
name|Object
operator|.
name|class
argument_list|,
name|Object
operator|.
name|class
argument_list|)
block|,
name|COLLECTION_ADD
argument_list|(
name|Collection
operator|.
name|class
argument_list|,
literal|"add"
argument_list|,
name|Object
operator|.
name|class
argument_list|)
block|,
name|LIST_GET
argument_list|(
name|List
operator|.
name|class
argument_list|,
literal|"get"
argument_list|,
name|int
operator|.
name|class
argument_list|)
block|,
name|ITERATOR_HAS_NEXT
argument_list|(
name|Iterator
operator|.
name|class
argument_list|,
literal|"hasNext"
argument_list|)
block|,
name|ITERATOR_NEXT
argument_list|(
name|Iterator
operator|.
name|class
argument_list|,
literal|"next"
argument_list|)
block|,
name|MATH_MAX
argument_list|(
name|Math
operator|.
name|class
argument_list|,
literal|"max"
argument_list|,
name|int
operator|.
name|class
argument_list|,
name|int
operator|.
name|class
argument_list|)
block|,
name|MATH_MIN
argument_list|(
name|Math
operator|.
name|class
argument_list|,
literal|"min"
argument_list|,
name|int
operator|.
name|class
argument_list|,
name|int
operator|.
name|class
argument_list|)
block|,
name|SORTED_MULTI_MAP_PUT_MULTI
argument_list|(
name|SortedMultiMap
operator|.
name|class
argument_list|,
literal|"putMulti"
argument_list|,
name|Object
operator|.
name|class
argument_list|,
name|Object
operator|.
name|class
argument_list|)
block|,
name|SORTED_MULTI_MAP_ARRAYS
argument_list|(
name|SortedMultiMap
operator|.
name|class
argument_list|,
literal|"arrays"
argument_list|,
name|Comparator
operator|.
name|class
argument_list|)
block|,
name|SORTED_MULTI_MAP_SINGLETON
argument_list|(
name|SortedMultiMap
operator|.
name|class
argument_list|,
literal|"singletonArrayIterator"
argument_list|,
name|Comparator
operator|.
name|class
argument_list|,
name|List
operator|.
name|class
argument_list|)
block|,
name|ARRAY_ITEM
argument_list|(
name|SqlFunctions
operator|.
name|class
argument_list|,
literal|"arrayItem"
argument_list|,
name|List
operator|.
name|class
argument_list|,
name|int
operator|.
name|class
argument_list|)
block|,
name|MAP_ITEM
argument_list|(
name|SqlFunctions
operator|.
name|class
argument_list|,
literal|"mapItem"
argument_list|,
name|Map
operator|.
name|class
argument_list|,
name|Object
operator|.
name|class
argument_list|)
block|,
name|ANY_ITEM
argument_list|(
name|SqlFunctions
operator|.
name|class
argument_list|,
literal|"item"
argument_list|,
name|Object
operator|.
name|class
argument_list|,
name|Object
operator|.
name|class
argument_list|)
block|,
name|UPPER
argument_list|(
name|SqlFunctions
operator|.
name|class
argument_list|,
literal|"upper"
argument_list|,
name|String
operator|.
name|class
argument_list|)
block|,
name|LOWER
argument_list|(
name|SqlFunctions
operator|.
name|class
argument_list|,
literal|"lower"
argument_list|,
name|String
operator|.
name|class
argument_list|)
block|,
name|INITCAP
argument_list|(
name|SqlFunctions
operator|.
name|class
argument_list|,
literal|"initcap"
argument_list|,
name|String
operator|.
name|class
argument_list|)
block|,
name|SUBSTRING
argument_list|(
name|SqlFunctions
operator|.
name|class
argument_list|,
literal|"substring"
argument_list|,
name|String
operator|.
name|class
argument_list|,
name|int
operator|.
name|class
argument_list|,
name|int
operator|.
name|class
argument_list|)
block|,
name|CHAR_LENGTH
argument_list|(
name|SqlFunctions
operator|.
name|class
argument_list|,
literal|"charLength"
argument_list|,
name|String
operator|.
name|class
argument_list|)
block|,
name|STRING_CONCAT
argument_list|(
name|SqlFunctions
operator|.
name|class
argument_list|,
literal|"concat"
argument_list|,
name|String
operator|.
name|class
argument_list|,
name|String
operator|.
name|class
argument_list|)
block|,
name|OVERLAY
argument_list|(
name|SqlFunctions
operator|.
name|class
argument_list|,
literal|"overlay"
argument_list|,
name|String
operator|.
name|class
argument_list|,
name|String
operator|.
name|class
argument_list|,
name|int
operator|.
name|class
argument_list|)
block|,
name|OVERLAY3
argument_list|(
name|SqlFunctions
operator|.
name|class
argument_list|,
literal|"overlay"
argument_list|,
name|String
operator|.
name|class
argument_list|,
name|String
operator|.
name|class
argument_list|,
name|int
operator|.
name|class
argument_list|,
name|int
operator|.
name|class
argument_list|)
block|,
name|POSITION
argument_list|(
name|SqlFunctions
operator|.
name|class
argument_list|,
literal|"position"
argument_list|,
name|String
operator|.
name|class
argument_list|,
name|String
operator|.
name|class
argument_list|)
block|,
name|TRUNCATE
argument_list|(
name|SqlFunctions
operator|.
name|class
argument_list|,
literal|"truncate"
argument_list|,
name|String
operator|.
name|class
argument_list|,
name|int
operator|.
name|class
argument_list|)
block|,
name|TRIM
argument_list|(
name|SqlFunctions
operator|.
name|class
argument_list|,
literal|"trim"
argument_list|,
name|boolean
operator|.
name|class
argument_list|,
name|boolean
operator|.
name|class
argument_list|,
name|String
operator|.
name|class
argument_list|,
name|String
operator|.
name|class
argument_list|)
block|,
name|LTRIM
argument_list|(
name|SqlFunctions
operator|.
name|class
argument_list|,
literal|"ltrim"
argument_list|,
name|String
operator|.
name|class
argument_list|)
block|,
name|RTRIM
argument_list|(
name|SqlFunctions
operator|.
name|class
argument_list|,
literal|"rtrim"
argument_list|,
name|String
operator|.
name|class
argument_list|)
block|,
name|LIKE
argument_list|(
name|SqlFunctions
operator|.
name|class
argument_list|,
literal|"like"
argument_list|,
name|String
operator|.
name|class
argument_list|,
name|String
operator|.
name|class
argument_list|)
block|,
name|SIMILAR
argument_list|(
name|SqlFunctions
operator|.
name|class
argument_list|,
literal|"similar"
argument_list|,
name|String
operator|.
name|class
argument_list|,
name|String
operator|.
name|class
argument_list|)
block|,
name|IS_TRUE
argument_list|(
name|SqlFunctions
operator|.
name|class
argument_list|,
literal|"isTrue"
argument_list|,
name|Boolean
operator|.
name|class
argument_list|)
block|,
name|IS_NOT_FALSE
argument_list|(
name|SqlFunctions
operator|.
name|class
argument_list|,
literal|"isNotFalse"
argument_list|,
name|Boolean
operator|.
name|class
argument_list|)
block|,
name|MODIFIABLE_TABLE_GET_MODIFIABLE_COLLECTION
argument_list|(
name|ModifiableTable
operator|.
name|class
argument_list|,
literal|"getModifiableCollection"
argument_list|)
block|,
name|STRING_TO_BOOLEAN
argument_list|(
name|SqlFunctions
operator|.
name|class
argument_list|,
literal|"toBoolean"
argument_list|,
name|String
operator|.
name|class
argument_list|)
block|,
name|UNIX_DATE_TO_STRING
argument_list|(
name|SqlFunctions
operator|.
name|class
argument_list|,
literal|"unixDateToString"
argument_list|,
name|int
operator|.
name|class
argument_list|)
block|,
name|UNIX_TIME_TO_STRING
argument_list|(
name|SqlFunctions
operator|.
name|class
argument_list|,
literal|"unixTimeToString"
argument_list|,
name|int
operator|.
name|class
argument_list|)
block|,
name|UNIX_TIMESTAMP_TO_STRING
argument_list|(
name|SqlFunctions
operator|.
name|class
argument_list|,
literal|"unixTimestampToString"
argument_list|,
name|long
operator|.
name|class
argument_list|)
block|,
name|INTERVAL_YEAR_MONTH_TO_STRING
argument_list|(
name|SqlFunctions
operator|.
name|class
argument_list|,
literal|"intervalYearMonthToString"
argument_list|,
name|int
operator|.
name|class
argument_list|,
name|SqlFunctions
operator|.
name|TimeUnitRange
operator|.
name|class
argument_list|)
block|,
name|INTERVAL_DAY_TIME_TO_STRING
argument_list|(
name|SqlFunctions
operator|.
name|class
argument_list|,
literal|"intervalDayTimeToString"
argument_list|,
name|long
operator|.
name|class
argument_list|,
name|SqlFunctions
operator|.
name|TimeUnitRange
operator|.
name|class
argument_list|,
name|int
operator|.
name|class
argument_list|)
block|,
name|CURRENT_TIMESTAMP
argument_list|(
name|SqlFunctions
operator|.
name|class
argument_list|,
literal|"currentTimestamp"
argument_list|,
name|DataContext
operator|.
name|class
argument_list|)
block|,
name|CURRENT_TIME
argument_list|(
name|SqlFunctions
operator|.
name|class
argument_list|,
literal|"currentTime"
argument_list|,
name|DataContext
operator|.
name|class
argument_list|)
block|,
name|CURRENT_DATE
argument_list|(
name|SqlFunctions
operator|.
name|class
argument_list|,
literal|"currentDate"
argument_list|,
name|DataContext
operator|.
name|class
argument_list|)
block|,
name|LOCAL_TIMESTAMP
argument_list|(
name|SqlFunctions
operator|.
name|class
argument_list|,
literal|"localTimestamp"
argument_list|,
name|DataContext
operator|.
name|class
argument_list|)
block|,
name|LOCAL_TIME
argument_list|(
name|SqlFunctions
operator|.
name|class
argument_list|,
literal|"localTime"
argument_list|,
name|DataContext
operator|.
name|class
argument_list|)
block|,
name|BOOLEAN_TO_STRING
argument_list|(
name|SqlFunctions
operator|.
name|class
argument_list|,
literal|"toString"
argument_list|,
name|boolean
operator|.
name|class
argument_list|)
block|,
name|ROUND_LONG
argument_list|(
name|SqlFunctions
operator|.
name|class
argument_list|,
literal|"round"
argument_list|,
name|long
operator|.
name|class
argument_list|,
name|long
operator|.
name|class
argument_list|)
block|,
name|ROUND_INT
argument_list|(
name|SqlFunctions
operator|.
name|class
argument_list|,
literal|"round"
argument_list|,
name|int
operator|.
name|class
argument_list|,
name|int
operator|.
name|class
argument_list|)
block|,
name|DATE_TO_INT
argument_list|(
name|SqlFunctions
operator|.
name|class
argument_list|,
literal|"toInt"
argument_list|,
name|java
operator|.
name|util
operator|.
name|Date
operator|.
name|class
argument_list|)
block|,
name|TIME_TO_INT
argument_list|(
name|SqlFunctions
operator|.
name|class
argument_list|,
literal|"toInt"
argument_list|,
name|Time
operator|.
name|class
argument_list|)
block|,
name|TIMESTAMP_TO_LONG
argument_list|(
name|SqlFunctions
operator|.
name|class
argument_list|,
literal|"toLong"
argument_list|,
name|Timestamp
operator|.
name|class
argument_list|)
block|,   ;
specifier|public
specifier|final
name|Method
name|method
decl_stmt|;
specifier|public
specifier|final
name|Constructor
name|constructor
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|HashMap
argument_list|<
name|Method
argument_list|,
name|BuiltinMethod
argument_list|>
name|MAP
init|=
operator|new
name|HashMap
argument_list|<
name|Method
argument_list|,
name|BuiltinMethod
argument_list|>
argument_list|()
decl_stmt|;
static|static
block|{
for|for
control|(
name|BuiltinMethod
name|builtinMethod
range|:
name|BuiltinMethod
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|builtinMethod
operator|.
name|method
operator|!=
literal|null
condition|)
block|{
name|MAP
operator|.
name|put
argument_list|(
name|builtinMethod
operator|.
name|method
argument_list|,
name|builtinMethod
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|BuiltinMethod
parameter_list|(
name|Class
name|clazz
parameter_list|,
name|String
name|methodName
parameter_list|,
name|Class
modifier|...
name|argumentTypes
parameter_list|)
block|{
name|this
operator|.
name|method
operator|=
name|Types
operator|.
name|lookupMethod
argument_list|(
name|clazz
argument_list|,
name|methodName
argument_list|,
name|argumentTypes
argument_list|)
expr_stmt|;
name|this
operator|.
name|constructor
operator|=
literal|null
expr_stmt|;
block|}
name|BuiltinMethod
parameter_list|(
name|Class
name|clazz
parameter_list|,
name|Class
modifier|...
name|argumentTypes
parameter_list|)
block|{
name|this
operator|.
name|method
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|constructor
operator|=
name|Types
operator|.
name|lookupConstructor
argument_list|(
name|clazz
argument_list|,
name|argumentTypes
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
name|BuiltinMethod
name|lookup
parameter_list|(
name|Method
name|method
parameter_list|)
block|{
return|return
name|MAP
operator|.
name|get
argument_list|(
name|method
argument_list|)
return|;
block|}
block|}
end_enum

begin_comment
comment|// End BuiltinMethod.java
end_comment

end_unit

