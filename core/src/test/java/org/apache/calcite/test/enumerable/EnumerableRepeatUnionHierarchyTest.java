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
name|test
operator|.
name|enumerable
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
name|adapter
operator|.
name|enumerable
operator|.
name|EnumerableRepeatUnion
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
name|adapter
operator|.
name|java
operator|.
name|ReflectiveSchema
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
name|RelNode
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
name|core
operator|.
name|JoinRelType
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
name|rex
operator|.
name|RexNode
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
name|schema
operator|.
name|Schema
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
name|test
operator|.
name|CalciteAssert
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
name|test
operator|.
name|schemata
operator|.
name|hr
operator|.
name|HierarchySchema
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
name|tools
operator|.
name|RelBuilder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|jupiter
operator|.
name|params
operator|.
name|ParameterizedTest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|jupiter
operator|.
name|params
operator|.
name|provider
operator|.
name|MethodSource
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Function
import|;
end_import

begin_comment
comment|/**  * Unit tests for  * {@link EnumerableRepeatUnion}  *<a href="https://issues.apache.org/jira/browse/CALCITE-2812">[CALCITE-2812]  * Add algebraic operators to allow expressing recursive queries</a>.  */
end_comment

begin_class
class|class
name|EnumerableRepeatUnionHierarchyTest
block|{
comment|// Tests for the following hierarchy:
comment|//      Emp1
comment|//      /  \
comment|//    Emp2  Emp4
comment|//    /  \
comment|// Emp3   Emp5
specifier|private
specifier|static
specifier|final
name|String
name|EMP1
init|=
literal|"empid=1; name=Emp1"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|EMP2
init|=
literal|"empid=2; name=Emp2"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|EMP3
init|=
literal|"empid=3; name=Emp3"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|EMP4
init|=
literal|"empid=4; name=Emp4"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|EMP5
init|=
literal|"empid=5; name=Emp5"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
index|[]
name|ID1
init|=
operator|new
name|int
index|[]
block|{
literal|1
block|}
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|ID1_STR
init|=
name|Arrays
operator|.
name|toString
argument_list|(
name|ID1
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
index|[]
name|ID2
init|=
operator|new
name|int
index|[]
block|{
literal|2
block|}
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|ID2_STR
init|=
name|Arrays
operator|.
name|toString
argument_list|(
name|ID2
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
index|[]
name|ID3
init|=
operator|new
name|int
index|[]
block|{
literal|3
block|}
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|ID3_STR
init|=
name|Arrays
operator|.
name|toString
argument_list|(
name|ID3
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
index|[]
name|ID4
init|=
operator|new
name|int
index|[]
block|{
literal|4
block|}
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|ID4_STR
init|=
name|Arrays
operator|.
name|toString
argument_list|(
name|ID4
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
index|[]
name|ID5
init|=
operator|new
name|int
index|[]
block|{
literal|5
block|}
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|ID5_STR
init|=
name|Arrays
operator|.
name|toString
argument_list|(
name|ID5
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
index|[]
name|ID3_5
init|=
operator|new
name|int
index|[]
block|{
literal|3
block|,
literal|5
block|}
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|ID3_5_STR
init|=
name|Arrays
operator|.
name|toString
argument_list|(
name|ID3_5
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
index|[]
name|ID1_3
init|=
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|3
block|}
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|ID1_3_STR
init|=
name|Arrays
operator|.
name|toString
argument_list|(
name|ID1_3
argument_list|)
decl_stmt|;
specifier|public
specifier|static
name|Iterable
argument_list|<
name|Object
index|[]
argument_list|>
name|data
parameter_list|()
block|{
return|return
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|Object
index|[]
index|[]
block|{
block|{
literal|true
block|,
name|ID1
block|,
name|ID1_STR
block|,
literal|true
block|,
operator|-
literal|1
block|,
operator|new
name|String
index|[]
block|{
name|EMP1
block|}
block|}
block|,
block|{
literal|true
block|,
name|ID2
block|,
name|ID2_STR
block|,
literal|true
block|,
operator|-
literal|2
block|,
operator|new
name|String
index|[]
block|{
name|EMP2
block|,
name|EMP1
block|}
block|}
block|,
block|{
literal|true
block|,
name|ID3
block|,
name|ID3_STR
block|,
literal|true
block|,
operator|-
literal|1
block|,
operator|new
name|String
index|[]
block|{
name|EMP3
block|,
name|EMP2
block|,
name|EMP1
block|}
block|}
block|,
block|{
literal|true
block|,
name|ID4
block|,
name|ID4_STR
block|,
literal|true
block|,
operator|-
literal|5
block|,
operator|new
name|String
index|[]
block|{
name|EMP4
block|,
name|EMP1
block|}
block|}
block|,
block|{
literal|true
block|,
name|ID5
block|,
name|ID5_STR
block|,
literal|true
block|,
operator|-
literal|1
block|,
operator|new
name|String
index|[]
block|{
name|EMP5
block|,
name|EMP2
block|,
name|EMP1
block|}
block|}
block|,
block|{
literal|true
block|,
name|ID3
block|,
name|ID3_STR
block|,
literal|true
block|,
literal|0
block|,
operator|new
name|String
index|[]
block|{
name|EMP3
block|}
block|}
block|,
block|{
literal|true
block|,
name|ID3
block|,
name|ID3_STR
block|,
literal|true
block|,
literal|1
block|,
operator|new
name|String
index|[]
block|{
name|EMP3
block|,
name|EMP2
block|}
block|}
block|,
block|{
literal|true
block|,
name|ID3
block|,
name|ID3_STR
block|,
literal|true
block|,
literal|2
block|,
operator|new
name|String
index|[]
block|{
name|EMP3
block|,
name|EMP2
block|,
name|EMP1
block|}
block|}
block|,
block|{
literal|true
block|,
name|ID3
block|,
name|ID3_STR
block|,
literal|true
block|,
literal|10
block|,
operator|new
name|String
index|[]
block|{
name|EMP3
block|,
name|EMP2
block|,
name|EMP1
block|}
block|}
block|,
block|{
literal|true
block|,
name|ID1
block|,
name|ID1_STR
block|,
literal|false
block|,
operator|-
literal|1
block|,
operator|new
name|String
index|[]
block|{
name|EMP1
block|,
name|EMP2
block|,
name|EMP4
block|,
name|EMP3
block|,
name|EMP5
block|}
block|}
block|,
block|{
literal|true
block|,
name|ID2
block|,
name|ID2_STR
block|,
literal|false
block|,
operator|-
literal|10
block|,
operator|new
name|String
index|[]
block|{
name|EMP2
block|,
name|EMP3
block|,
name|EMP5
block|}
block|}
block|,
block|{
literal|true
block|,
name|ID3
block|,
name|ID3_STR
block|,
literal|false
block|,
operator|-
literal|100
block|,
operator|new
name|String
index|[]
block|{
name|EMP3
block|}
block|}
block|,
block|{
literal|true
block|,
name|ID4
block|,
name|ID4_STR
block|,
literal|false
block|,
operator|-
literal|1
block|,
operator|new
name|String
index|[]
block|{
name|EMP4
block|}
block|}
block|,
block|{
literal|true
block|,
name|ID1
block|,
name|ID1_STR
block|,
literal|false
block|,
literal|0
block|,
operator|new
name|String
index|[]
block|{
name|EMP1
block|}
block|}
block|,
block|{
literal|true
block|,
name|ID1
block|,
name|ID1_STR
block|,
literal|false
block|,
literal|1
block|,
operator|new
name|String
index|[]
block|{
name|EMP1
block|,
name|EMP2
block|,
name|EMP4
block|}
block|}
block|,
block|{
literal|true
block|,
name|ID1
block|,
name|ID1_STR
block|,
literal|false
block|,
literal|2
block|,
operator|new
name|String
index|[]
block|{
name|EMP1
block|,
name|EMP2
block|,
name|EMP4
block|,
name|EMP3
block|,
name|EMP5
block|}
block|}
block|,
block|{
literal|true
block|,
name|ID1
block|,
name|ID1_STR
block|,
literal|false
block|,
literal|20
block|,
operator|new
name|String
index|[]
block|{
name|EMP1
block|,
name|EMP2
block|,
name|EMP4
block|,
name|EMP3
block|,
name|EMP5
block|}
block|}
block|,
comment|// tests to verify all=true vs all=false
block|{
literal|true
block|,
name|ID3_5
block|,
name|ID3_5_STR
block|,
literal|true
block|,
operator|-
literal|1
block|,
operator|new
name|String
index|[]
block|{
name|EMP3
block|,
name|EMP5
block|,
name|EMP2
block|,
name|EMP2
block|,
name|EMP1
block|,
name|EMP1
block|}
block|}
block|,
block|{
literal|false
block|,
name|ID3_5
block|,
name|ID3_5_STR
block|,
literal|true
block|,
operator|-
literal|1
block|,
operator|new
name|String
index|[]
block|{
name|EMP3
block|,
name|EMP5
block|,
name|EMP2
block|,
name|EMP1
block|}
block|}
block|,
block|{
literal|true
block|,
name|ID3_5
block|,
name|ID3_5_STR
block|,
literal|true
block|,
literal|0
block|,
operator|new
name|String
index|[]
block|{
name|EMP3
block|,
name|EMP5
block|}
block|}
block|,
block|{
literal|false
block|,
name|ID3_5
block|,
name|ID3_5_STR
block|,
literal|true
block|,
literal|0
block|,
operator|new
name|String
index|[]
block|{
name|EMP3
block|,
name|EMP5
block|}
block|}
block|,
block|{
literal|true
block|,
name|ID3_5
block|,
name|ID3_5_STR
block|,
literal|true
block|,
literal|1
block|,
operator|new
name|String
index|[]
block|{
name|EMP3
block|,
name|EMP5
block|,
name|EMP2
block|,
name|EMP2
block|}
block|}
block|,
block|{
literal|false
block|,
name|ID3_5
block|,
name|ID3_5_STR
block|,
literal|true
block|,
literal|1
block|,
operator|new
name|String
index|[]
block|{
name|EMP3
block|,
name|EMP5
block|,
name|EMP2
block|}
block|}
block|,
block|{
literal|true
block|,
name|ID1_3
block|,
name|ID1_3_STR
block|,
literal|false
block|,
operator|-
literal|1
block|,
operator|new
name|String
index|[]
block|{
name|EMP1
block|,
name|EMP3
block|,
name|EMP2
block|,
name|EMP4
block|,
name|EMP3
block|,
name|EMP5
block|}
block|}
block|,
block|{
literal|false
block|,
name|ID1_3
block|,
name|ID1_3_STR
block|,
literal|false
block|,
operator|-
literal|1
block|,
operator|new
name|String
index|[]
block|{
name|EMP1
block|,
name|EMP3
block|,
name|EMP2
block|,
name|EMP4
block|,
name|EMP5
block|}
block|}
block|,     }
argument_list|)
return|;
block|}
annotation|@
name|ParameterizedTest
argument_list|(
name|name
operator|=
literal|"{index} : hierarchy(startIds:{2}, ascendant:{3}, "
operator|+
literal|"maxDepth:{4}, all:{0})"
argument_list|)
annotation|@
name|MethodSource
argument_list|(
literal|"data"
argument_list|)
specifier|public
name|void
name|testHierarchy
parameter_list|(
name|boolean
name|all
parameter_list|,
name|int
index|[]
name|startIds
parameter_list|,
name|String
name|startIdsStr
parameter_list|,
name|boolean
name|ascendant
parameter_list|,
name|int
name|maxDepth
parameter_list|,
name|String
index|[]
name|expected
parameter_list|)
block|{
specifier|final
name|String
name|fromField
decl_stmt|;
specifier|final
name|String
name|toField
decl_stmt|;
if|if
condition|(
name|ascendant
condition|)
block|{
name|fromField
operator|=
literal|"subordinateid"
expr_stmt|;
name|toField
operator|=
literal|"managerid"
expr_stmt|;
block|}
else|else
block|{
name|fromField
operator|=
literal|"managerid"
expr_stmt|;
name|toField
operator|=
literal|"subordinateid"
expr_stmt|;
block|}
specifier|final
name|Schema
name|schema
init|=
operator|new
name|ReflectiveSchema
argument_list|(
operator|new
name|HierarchySchema
argument_list|()
argument_list|)
decl_stmt|;
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|withSchema
argument_list|(
literal|"s"
argument_list|,
name|schema
argument_list|)
operator|.
name|withRel
argument_list|(
name|buildHierarchy
argument_list|(
name|all
argument_list|,
name|startIds
argument_list|,
name|fromField
argument_list|,
name|toField
argument_list|,
name|maxDepth
argument_list|)
argument_list|)
operator|.
name|returnsOrdered
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
specifier|private
name|Function
argument_list|<
name|RelBuilder
argument_list|,
name|RelNode
argument_list|>
name|buildHierarchy
parameter_list|(
name|boolean
name|all
parameter_list|,
name|int
index|[]
name|startIds
parameter_list|,
name|String
name|fromField
parameter_list|,
name|String
name|toField
parameter_list|,
name|int
name|maxDepth
parameter_list|)
block|{
comment|//   WITH RECURSIVE delta(empid, name) as (
comment|//       SELECT empid, name FROM emps WHERE empid IN (<startIds>)
comment|//     UNION [ALL]
comment|//       SELECT e.empid, e.name FROM delta d
comment|//                              JOIN hierarchies h ON d.empid = h.<fromField>
comment|//                              JOIN emps e        ON h.<toField> = e.empid
comment|//   )
comment|//   SELECT empid, name FROM delta
return|return
name|builder
lambda|->
block|{
name|builder
operator|.
name|scan
argument_list|(
literal|"s"
argument_list|,
literal|"emps"
argument_list|)
expr_stmt|;
specifier|final
name|List
argument_list|<
name|RexNode
argument_list|>
name|filters
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|startId
range|:
name|startIds
control|)
block|{
name|filters
operator|.
name|add
argument_list|(
name|builder
operator|.
name|equals
argument_list|(
name|builder
operator|.
name|field
argument_list|(
literal|"empid"
argument_list|)
argument_list|,
name|builder
operator|.
name|literal
argument_list|(
name|startId
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|filter
argument_list|(
name|builder
operator|.
name|or
argument_list|(
name|filters
argument_list|)
argument_list|)
operator|.
name|project
argument_list|(
name|builder
operator|.
name|field
argument_list|(
literal|"emps"
argument_list|,
literal|"empid"
argument_list|)
argument_list|,
name|builder
operator|.
name|field
argument_list|(
literal|"emps"
argument_list|,
literal|"name"
argument_list|)
argument_list|)
operator|.
name|transientScan
argument_list|(
literal|"#DELTA#"
argument_list|)
operator|.
name|scan
argument_list|(
literal|"s"
argument_list|,
literal|"hierarchies"
argument_list|)
operator|.
name|join
argument_list|(
name|JoinRelType
operator|.
name|INNER
argument_list|,
name|builder
operator|.
name|equals
argument_list|(
name|builder
operator|.
name|field
argument_list|(
literal|2
argument_list|,
literal|"#DELTA#"
argument_list|,
literal|"empid"
argument_list|)
argument_list|,
name|builder
operator|.
name|field
argument_list|(
literal|2
argument_list|,
literal|"hierarchies"
argument_list|,
name|fromField
argument_list|)
argument_list|)
argument_list|)
operator|.
name|scan
argument_list|(
literal|"s"
argument_list|,
literal|"emps"
argument_list|)
operator|.
name|join
argument_list|(
name|JoinRelType
operator|.
name|INNER
argument_list|,
name|builder
operator|.
name|equals
argument_list|(
name|builder
operator|.
name|field
argument_list|(
literal|2
argument_list|,
literal|"hierarchies"
argument_list|,
name|toField
argument_list|)
argument_list|,
name|builder
operator|.
name|field
argument_list|(
literal|2
argument_list|,
literal|"emps"
argument_list|,
literal|"empid"
argument_list|)
argument_list|)
argument_list|)
operator|.
name|project
argument_list|(
name|builder
operator|.
name|field
argument_list|(
literal|"emps"
argument_list|,
literal|"empid"
argument_list|)
argument_list|,
name|builder
operator|.
name|field
argument_list|(
literal|"emps"
argument_list|,
literal|"name"
argument_list|)
argument_list|)
operator|.
name|repeatUnion
argument_list|(
literal|"#DELTA#"
argument_list|,
name|all
argument_list|,
name|maxDepth
argument_list|)
expr_stmt|;
return|return
name|builder
operator|.
name|build
argument_list|()
return|;
block|}
return|;
block|}
block|}
end_class

end_unit

