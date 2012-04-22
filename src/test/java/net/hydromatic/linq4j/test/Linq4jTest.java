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
name|linq4j
operator|.
name|test
package|;
end_package

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
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
name|function
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
name|*
import|;
end_import

begin_comment
comment|/**  * Tests for LINQ4J.  */
end_comment

begin_class
specifier|public
class|class
name|Linq4jTest
extends|extends
name|TestCase
block|{
specifier|public
specifier|static
specifier|final
name|Function1
argument_list|<
name|Employee
argument_list|,
name|String
argument_list|>
name|EMP_NAME_SELECTOR
init|=
operator|new
name|Function1
argument_list|<
name|Employee
argument_list|,
name|String
argument_list|>
argument_list|()
block|{
specifier|public
name|String
name|apply
parameter_list|(
name|Employee
name|employee
parameter_list|)
block|{
return|return
name|employee
operator|.
name|name
return|;
block|}
block|}
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|Function1
argument_list|<
name|Employee
argument_list|,
name|Integer
argument_list|>
name|EMP_DEPTNO_SELECTOR
init|=
operator|new
name|Function1
argument_list|<
name|Employee
argument_list|,
name|Integer
argument_list|>
argument_list|()
block|{
specifier|public
name|Integer
name|apply
parameter_list|(
name|Employee
name|employee
parameter_list|)
block|{
return|return
name|employee
operator|.
name|deptno
return|;
block|}
block|}
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|Function1
argument_list|<
name|Employee
argument_list|,
name|Integer
argument_list|>
name|EMP_EMPNO_SELECTOR
init|=
operator|new
name|Function1
argument_list|<
name|Employee
argument_list|,
name|Integer
argument_list|>
argument_list|()
block|{
specifier|public
name|Integer
name|apply
parameter_list|(
name|Employee
name|employee
parameter_list|)
block|{
return|return
name|employee
operator|.
name|empno
return|;
block|}
block|}
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|Function1
argument_list|<
name|Department
argument_list|,
name|Enumerable
argument_list|<
name|Employee
argument_list|>
argument_list|>
name|DEPT_EMPLOYEES_SELECTOR
init|=
operator|new
name|Function1
argument_list|<
name|Department
argument_list|,
name|Enumerable
argument_list|<
name|Employee
argument_list|>
argument_list|>
argument_list|()
block|{
specifier|public
name|Enumerable
argument_list|<
name|Employee
argument_list|>
name|apply
parameter_list|(
name|Department
name|a0
parameter_list|)
block|{
return|return
name|Linq4j
operator|.
name|asEnumerable
argument_list|(
name|a0
operator|.
name|employees
argument_list|)
return|;
block|}
block|}
decl_stmt|;
specifier|public
name|void
name|testSelect
parameter_list|()
block|{
name|List
argument_list|<
name|String
argument_list|>
name|names
init|=
name|Linq4j
operator|.
name|asEnumerable
argument_list|(
name|emps
argument_list|)
operator|.
name|select
argument_list|(
name|EMP_NAME_SELECTOR
argument_list|)
operator|.
name|toList
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"[Fred, Bill, Eric, Jane]"
argument_list|,
name|names
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testWhere
parameter_list|()
block|{
name|List
argument_list|<
name|String
argument_list|>
name|names
init|=
name|Linq4j
operator|.
name|asEnumerable
argument_list|(
name|emps
argument_list|)
operator|.
name|where
argument_list|(
operator|new
name|Predicate1
argument_list|<
name|Employee
argument_list|>
argument_list|()
block|{
specifier|public
name|boolean
name|apply
parameter_list|(
name|Employee
name|employee
parameter_list|)
block|{
return|return
name|employee
operator|.
name|deptno
operator|<
literal|15
return|;
block|}
block|}
argument_list|)
operator|.
name|select
argument_list|(
name|EMP_NAME_SELECTOR
argument_list|)
operator|.
name|toList
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"[Fred, Eric, Jane]"
argument_list|,
name|names
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testWhereIndexed
parameter_list|()
block|{
comment|// Returns every other employee.
name|List
argument_list|<
name|String
argument_list|>
name|names
init|=
name|Linq4j
operator|.
name|asEnumerable
argument_list|(
name|emps
argument_list|)
operator|.
name|where
argument_list|(
operator|new
name|Predicate2
argument_list|<
name|Employee
argument_list|,
name|Integer
argument_list|>
argument_list|()
block|{
specifier|public
name|boolean
name|apply
parameter_list|(
name|Employee
name|employee
parameter_list|,
name|Integer
name|n
parameter_list|)
block|{
return|return
name|n
operator|%
literal|2
operator|==
literal|0
return|;
block|}
block|}
argument_list|)
operator|.
name|select
argument_list|(
name|EMP_NAME_SELECTOR
argument_list|)
operator|.
name|toList
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"[Fred, Eric]"
argument_list|,
name|names
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testSelectMany
parameter_list|()
block|{
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|nameSeqs
init|=
name|Linq4j
operator|.
name|asEnumerable
argument_list|(
name|depts
argument_list|)
operator|.
name|selectMany
argument_list|(
name|DEPT_EMPLOYEES_SELECTOR
argument_list|)
operator|.
name|select
argument_list|(
operator|new
name|Function2
argument_list|<
name|Employee
argument_list|,
name|Integer
argument_list|,
name|String
argument_list|>
argument_list|()
block|{
specifier|public
name|String
name|apply
parameter_list|(
name|Employee
name|v1
parameter_list|,
name|Integer
name|v2
parameter_list|)
block|{
return|return
literal|"#"
operator|+
name|v2
operator|+
literal|": "
operator|+
name|v1
operator|.
name|name
return|;
block|}
block|}
argument_list|)
operator|.
name|toList
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"[#0: Fred, #1: Eric, #2: Jane, #3: Bill]"
argument_list|,
name|nameSeqs
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testCount
parameter_list|()
block|{
specifier|final
name|int
name|count
init|=
name|Linq4j
operator|.
name|asEnumerable
argument_list|(
name|depts
argument_list|)
operator|.
name|count
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|count
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testCountPredicate
parameter_list|()
block|{
specifier|final
name|int
name|count
init|=
name|Linq4j
operator|.
name|asEnumerable
argument_list|(
name|depts
argument_list|)
operator|.
name|count
argument_list|(
operator|new
name|Predicate1
argument_list|<
name|Department
argument_list|>
argument_list|()
block|{
specifier|public
name|boolean
name|apply
parameter_list|(
name|Department
name|v1
parameter_list|)
block|{
return|return
name|v1
operator|.
name|employees
operator|.
name|size
argument_list|()
operator|>
literal|0
return|;
block|}
block|}
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|count
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testLongCount
parameter_list|()
block|{
specifier|final
name|long
name|count
init|=
name|Linq4j
operator|.
name|asEnumerable
argument_list|(
name|depts
argument_list|)
operator|.
name|longCount
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|count
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testLongCountPredicate
parameter_list|()
block|{
specifier|final
name|long
name|count
init|=
name|Linq4j
operator|.
name|asEnumerable
argument_list|(
name|depts
argument_list|)
operator|.
name|longCount
argument_list|(
operator|new
name|Predicate1
argument_list|<
name|Department
argument_list|>
argument_list|()
block|{
specifier|public
name|boolean
name|apply
parameter_list|(
name|Department
name|v1
parameter_list|)
block|{
return|return
name|v1
operator|.
name|employees
operator|.
name|size
argument_list|()
operator|>
literal|0
return|;
block|}
block|}
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|count
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testToMap
parameter_list|()
block|{
specifier|final
name|Map
argument_list|<
name|Integer
argument_list|,
name|Employee
argument_list|>
name|map
init|=
name|Linq4j
operator|.
name|asEnumerable
argument_list|(
name|emps
argument_list|)
operator|.
name|toMap
argument_list|(
name|EMP_EMPNO_SELECTOR
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|map
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|map
operator|.
name|get
argument_list|(
literal|110
argument_list|)
operator|.
name|name
operator|.
name|equals
argument_list|(
literal|"Bill"
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testToMap2
parameter_list|()
block|{
specifier|final
name|Map
argument_list|<
name|Integer
argument_list|,
name|Integer
argument_list|>
name|map
init|=
name|Linq4j
operator|.
name|asEnumerable
argument_list|(
name|emps
argument_list|)
operator|.
name|toMap
argument_list|(
name|EMP_EMPNO_SELECTOR
argument_list|,
name|EMP_DEPTNO_SELECTOR
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|map
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|map
operator|.
name|get
argument_list|(
literal|110
argument_list|)
operator|==
literal|30
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testToLookup
parameter_list|()
block|{
specifier|final
name|Lookup
argument_list|<
name|Integer
argument_list|,
name|Employee
argument_list|>
name|lookup
init|=
name|Linq4j
operator|.
name|asEnumerable
argument_list|(
name|emps
argument_list|)
operator|.
name|toLookup
argument_list|(
name|EMP_DEPTNO_SELECTOR
argument_list|)
decl_stmt|;
name|int
name|n
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Grouping
argument_list|<
name|Integer
argument_list|,
name|Employee
argument_list|>
name|grouping
range|:
name|lookup
control|)
block|{
operator|++
name|n
expr_stmt|;
switch|switch
condition|(
name|grouping
operator|.
name|getKey
argument_list|()
condition|)
block|{
case|case
literal|10
case|:
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|grouping
operator|.
name|count
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
literal|30
case|:
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|grouping
operator|.
name|count
argument_list|()
argument_list|)
expr_stmt|;
break|break;
default|default:
name|fail
argument_list|(
literal|"unknown department number "
operator|+
name|grouping
argument_list|)
expr_stmt|;
block|}
block|}
name|assertEquals
argument_list|(
name|n
argument_list|,
literal|2
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testToLookupSelector
parameter_list|()
block|{
specifier|final
name|Lookup
argument_list|<
name|Integer
argument_list|,
name|String
argument_list|>
name|lookup
init|=
name|Linq4j
operator|.
name|asEnumerable
argument_list|(
name|emps
argument_list|)
operator|.
name|toLookup
argument_list|(
name|EMP_DEPTNO_SELECTOR
argument_list|,
name|EMP_NAME_SELECTOR
argument_list|)
decl_stmt|;
name|int
name|n
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Grouping
argument_list|<
name|Integer
argument_list|,
name|String
argument_list|>
name|grouping
range|:
name|lookup
control|)
block|{
operator|++
name|n
expr_stmt|;
switch|switch
condition|(
name|grouping
operator|.
name|getKey
argument_list|()
condition|)
block|{
case|case
literal|10
case|:
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|grouping
operator|.
name|count
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|grouping
operator|.
name|contains
argument_list|(
literal|"Fred"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|grouping
operator|.
name|contains
argument_list|(
literal|"Eric"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|grouping
operator|.
name|contains
argument_list|(
literal|"Jane"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|grouping
operator|.
name|contains
argument_list|(
literal|"Bill"
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
literal|30
case|:
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|grouping
operator|.
name|count
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|grouping
operator|.
name|contains
argument_list|(
literal|"Bill"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|grouping
operator|.
name|contains
argument_list|(
literal|"Fred"
argument_list|)
argument_list|)
expr_stmt|;
break|break;
default|default:
name|fail
argument_list|(
literal|"unknown department number "
operator|+
name|grouping
argument_list|)
expr_stmt|;
block|}
block|}
name|assertEquals
argument_list|(
name|n
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"[10:3, 30:1]"
argument_list|,
name|lookup
operator|.
name|applyResultSelector
argument_list|(
operator|new
name|Function2
argument_list|<
name|Integer
argument_list|,
name|Enumerable
argument_list|<
name|String
argument_list|>
argument_list|,
name|Object
argument_list|>
argument_list|()
block|{
specifier|public
name|Object
name|apply
parameter_list|(
name|Integer
name|v1
parameter_list|,
name|Enumerable
argument_list|<
name|String
argument_list|>
name|v2
parameter_list|)
block|{
return|return
name|v1
operator|+
literal|":"
operator|+
name|v2
operator|.
name|count
argument_list|()
return|;
block|}
block|}
argument_list|)
operator|.
name|toList
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testCast
parameter_list|()
block|{
specifier|final
name|List
argument_list|<
name|Number
argument_list|>
name|numbers
init|=
name|Arrays
operator|.
name|asList
argument_list|(
operator|(
name|Number
operator|)
literal|2
argument_list|,
literal|null
argument_list|,
literal|3.14
argument_list|,
literal|5
argument_list|)
decl_stmt|;
specifier|final
name|Enumerator
argument_list|<
name|Integer
argument_list|>
name|enumerator
init|=
name|Linq4j
operator|.
name|asEnumerable
argument_list|(
name|numbers
argument_list|)
operator|.
name|cast
argument_list|(
name|Integer
operator|.
name|class
argument_list|)
operator|.
name|enumerator
argument_list|()
decl_stmt|;
name|checkCast
argument_list|(
name|enumerator
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testIterableCast
parameter_list|()
block|{
specifier|final
name|List
argument_list|<
name|Number
argument_list|>
name|numbers
init|=
name|Arrays
operator|.
name|asList
argument_list|(
operator|(
name|Number
operator|)
literal|2
argument_list|,
literal|null
argument_list|,
literal|3.14
argument_list|,
literal|5
argument_list|)
decl_stmt|;
specifier|final
name|Enumerator
argument_list|<
name|Integer
argument_list|>
name|enumerator
init|=
name|Linq4j
operator|.
name|cast
argument_list|(
name|numbers
argument_list|,
name|Integer
operator|.
name|class
argument_list|)
operator|.
name|enumerator
argument_list|()
decl_stmt|;
name|checkCast
argument_list|(
name|enumerator
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|checkCast
parameter_list|(
name|Enumerator
argument_list|<
name|Integer
argument_list|>
name|enumerator
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|enumerator
operator|.
name|moveNext
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Integer
operator|.
name|valueOf
argument_list|(
literal|2
argument_list|)
argument_list|,
name|enumerator
operator|.
name|current
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|enumerator
operator|.
name|moveNext
argument_list|()
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|enumerator
operator|.
name|current
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|enumerator
operator|.
name|moveNext
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|Object
name|x
init|=
name|enumerator
operator|.
name|current
argument_list|()
decl_stmt|;
name|fail
argument_list|(
literal|"expected error, got "
operator|+
name|x
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClassCastException
name|e
parameter_list|)
block|{
comment|// good
block|}
name|assertTrue
argument_list|(
name|enumerator
operator|.
name|moveNext
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Integer
operator|.
name|valueOf
argument_list|(
literal|5
argument_list|)
argument_list|,
name|enumerator
operator|.
name|current
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|enumerator
operator|.
name|moveNext
argument_list|()
argument_list|)
expr_stmt|;
name|enumerator
operator|.
name|reset
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|enumerator
operator|.
name|moveNext
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Integer
operator|.
name|valueOf
argument_list|(
literal|2
argument_list|)
argument_list|,
name|enumerator
operator|.
name|current
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testOfType
parameter_list|()
block|{
specifier|final
name|List
argument_list|<
name|Number
argument_list|>
name|numbers
init|=
name|Arrays
operator|.
name|asList
argument_list|(
operator|(
name|Number
operator|)
literal|2
argument_list|,
literal|null
argument_list|,
literal|3.14
argument_list|,
literal|5
argument_list|)
decl_stmt|;
specifier|final
name|Enumerator
argument_list|<
name|Integer
argument_list|>
name|enumerator
init|=
name|Linq4j
operator|.
name|asEnumerable
argument_list|(
name|numbers
argument_list|)
operator|.
name|ofType
argument_list|(
name|Integer
operator|.
name|class
argument_list|)
operator|.
name|enumerator
argument_list|()
decl_stmt|;
name|checkIterable
argument_list|(
name|enumerator
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testIterableOfType
parameter_list|()
block|{
specifier|final
name|List
argument_list|<
name|Number
argument_list|>
name|numbers
init|=
name|Arrays
operator|.
name|asList
argument_list|(
operator|(
name|Number
operator|)
literal|2
argument_list|,
literal|null
argument_list|,
literal|3.14
argument_list|,
literal|5
argument_list|)
decl_stmt|;
specifier|final
name|Enumerator
argument_list|<
name|Integer
argument_list|>
name|enumerator
init|=
name|Linq4j
operator|.
name|ofType
argument_list|(
name|numbers
argument_list|,
name|Integer
operator|.
name|class
argument_list|)
operator|.
name|enumerator
argument_list|()
decl_stmt|;
name|checkIterable
argument_list|(
name|enumerator
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|checkIterable
parameter_list|(
name|Enumerator
argument_list|<
name|Integer
argument_list|>
name|enumerator
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|enumerator
operator|.
name|moveNext
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Integer
operator|.
name|valueOf
argument_list|(
literal|2
argument_list|)
argument_list|,
name|enumerator
operator|.
name|current
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|enumerator
operator|.
name|moveNext
argument_list|()
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|enumerator
operator|.
name|current
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|enumerator
operator|.
name|moveNext
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Integer
operator|.
name|valueOf
argument_list|(
literal|5
argument_list|)
argument_list|,
name|enumerator
operator|.
name|current
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|enumerator
operator|.
name|moveNext
argument_list|()
argument_list|)
expr_stmt|;
name|enumerator
operator|.
name|reset
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|enumerator
operator|.
name|moveNext
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Integer
operator|.
name|valueOf
argument_list|(
literal|2
argument_list|)
argument_list|,
name|enumerator
operator|.
name|current
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
class|class
name|Employee
block|{
specifier|final
name|int
name|empno
decl_stmt|;
specifier|final
name|String
name|name
decl_stmt|;
specifier|final
name|int
name|deptno
decl_stmt|;
specifier|public
name|Employee
parameter_list|(
name|int
name|empno
parameter_list|,
name|String
name|name
parameter_list|,
name|int
name|deptno
parameter_list|)
block|{
name|this
operator|.
name|empno
operator|=
name|empno
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|deptno
operator|=
name|deptno
expr_stmt|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"Employee(name: "
operator|+
name|name
operator|+
literal|", deptno:"
operator|+
name|deptno
operator|+
literal|")"
return|;
block|}
block|}
specifier|public
specifier|static
class|class
name|Department
block|{
specifier|final
name|String
name|name
decl_stmt|;
specifier|final
name|int
name|deptno
decl_stmt|;
specifier|final
name|List
argument_list|<
name|Employee
argument_list|>
name|employees
decl_stmt|;
specifier|public
name|Department
parameter_list|(
name|String
name|name
parameter_list|,
name|int
name|deptno
parameter_list|,
name|List
argument_list|<
name|Employee
argument_list|>
name|employees
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|deptno
operator|=
name|deptno
expr_stmt|;
name|this
operator|.
name|employees
operator|=
name|employees
expr_stmt|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"Department(name: "
operator|+
name|name
operator|+
literal|", deptno:"
operator|+
name|deptno
operator|+
literal|", employees: "
operator|+
name|employees
operator|+
literal|")"
return|;
block|}
block|}
specifier|public
specifier|static
specifier|final
name|Employee
index|[]
name|emps
init|=
block|{
operator|new
name|Employee
argument_list|(
literal|100
argument_list|,
literal|"Fred"
argument_list|,
literal|10
argument_list|)
block|,
operator|new
name|Employee
argument_list|(
literal|110
argument_list|,
literal|"Bill"
argument_list|,
literal|30
argument_list|)
block|,
operator|new
name|Employee
argument_list|(
literal|120
argument_list|,
literal|"Eric"
argument_list|,
literal|10
argument_list|)
block|,
operator|new
name|Employee
argument_list|(
literal|130
argument_list|,
literal|"Jane"
argument_list|,
literal|10
argument_list|)
block|,     }
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|Department
index|[]
name|depts
init|=
block|{
operator|new
name|Department
argument_list|(
literal|"Sales"
argument_list|,
literal|10
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|emps
index|[
literal|0
index|]
argument_list|,
name|emps
index|[
literal|2
index|]
argument_list|,
name|emps
index|[
literal|3
index|]
argument_list|)
argument_list|)
block|,
operator|new
name|Department
argument_list|(
literal|"HR"
argument_list|,
literal|20
argument_list|,
name|Collections
operator|.
expr|<
name|Employee
operator|>
name|emptyList
argument_list|()
argument_list|)
block|,
operator|new
name|Department
argument_list|(
literal|"Marketing"
argument_list|,
literal|30
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|emps
index|[
literal|1
index|]
argument_list|)
argument_list|)
block|,     }
decl_stmt|;
block|}
end_class

begin_comment
comment|// End Linq4jTest.java
end_comment

end_unit

