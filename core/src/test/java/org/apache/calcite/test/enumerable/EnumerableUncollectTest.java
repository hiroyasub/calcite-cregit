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
name|config
operator|.
name|CalciteConnectionProperty
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
name|config
operator|.
name|Lex
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
name|junit
operator|.
name|jupiter
operator|.
name|api
operator|.
name|Test
import|;
end_import

begin_comment
comment|/** Test for {@link org.apache.calcite.adapter.enumerable.EnumerableUncollect}. */
end_comment

begin_class
class|class
name|EnumerableUncollectTest
block|{
annotation|@
name|Test
name|void
name|simpleUnnestArray
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select * from UNNEST(array[3, 4]) as T2(y)"
decl_stmt|;
name|tester
argument_list|()
operator|.
name|query
argument_list|(
name|sql
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"y=3"
argument_list|,
literal|"y=4"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|simpleUnnestNullArray
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT * FROM UNNEST(CAST(null AS INTEGER ARRAY))"
decl_stmt|;
name|tester
argument_list|()
operator|.
name|query
argument_list|(
name|sql
argument_list|)
operator|.
name|returnsCount
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|simpleUnnestArrayOfArrays
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select * from UNNEST(array[array[3], array[4]]) as T2(y)"
decl_stmt|;
name|tester
argument_list|()
operator|.
name|query
argument_list|(
name|sql
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"y=[3]"
argument_list|,
literal|"y=[4]"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|simpleUnnestArrayOfArrays2
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select * from UNNEST(array[array[3, 4], array[4, 5]]) as T2(y)"
decl_stmt|;
name|tester
argument_list|()
operator|.
name|query
argument_list|(
name|sql
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"y=[3, 4]"
argument_list|,
literal|"y=[4, 5]"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|simpleUnnestArrayOfArrays3
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select * from UNNEST("
operator|+
literal|"array[array[array[3,4], array[4,5]], array[array[7,8], array[9,10]]]) as T2(y)"
decl_stmt|;
name|tester
argument_list|()
operator|.
name|query
argument_list|(
name|sql
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"y=[[3, 4], [4, 5]]"
argument_list|,
literal|"y=[[7, 8], [9, 10]]"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|simpleUnnestArrayOfRows
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select * from UNNEST(array[ROW(3), ROW(4)]) as T2(y)"
decl_stmt|;
name|tester
argument_list|()
operator|.
name|query
argument_list|(
name|sql
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"y=3"
argument_list|,
literal|"y=4"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|simpleUnnestArrayOfRows2
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select * from UNNEST(array[ROW(3, 5), ROW(4, 6)]) as T2(y, z)"
decl_stmt|;
name|tester
argument_list|()
operator|.
name|query
argument_list|(
name|sql
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"y=3; z=5"
argument_list|,
literal|"y=4; z=6"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|simpleUnnestArrayOfRows3
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select * from UNNEST(array[ROW(3), ROW(4)]) WITH ORDINALITY as T2(y, o)"
decl_stmt|;
name|tester
argument_list|()
operator|.
name|query
argument_list|(
name|sql
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"y=3; o=1"
argument_list|,
literal|"y=4; o=2"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|simpleUnnestArrayOfRows4
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select * from UNNEST(array[ROW(1, ROW(5, 10)), ROW(2, ROW(6, 12))]) "
operator|+
literal|"as T2(y, z)"
decl_stmt|;
name|tester
argument_list|()
operator|.
name|query
argument_list|(
name|sql
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"y=1; z={5, 10}"
argument_list|,
literal|"y=2; z={6, 12}"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|simpleUnnestArrayOfRows5
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select * from UNNEST(array[ROW(ROW(3)), ROW(ROW(4))]) as T2(y)"
decl_stmt|;
name|tester
argument_list|()
operator|.
name|query
argument_list|(
name|sql
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"y={3}"
argument_list|,
literal|"y={4}"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|chainedUnnestArray
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select * from (values (1), (2)) T1(x),"
operator|+
literal|"UNNEST(array[3, 4]) as T2(y)"
decl_stmt|;
name|tester
argument_list|()
operator|.
name|query
argument_list|(
name|sql
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"x=1; y=3"
argument_list|,
literal|"x=1; y=4"
argument_list|,
literal|"x=2; y=3"
argument_list|,
literal|"x=2; y=4"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|chainedUnnestArrayOfArrays
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select * from (values (1), (2)) T1(x),"
operator|+
literal|"UNNEST(array[array[3], array[4]]) as T2(y)"
decl_stmt|;
name|tester
argument_list|()
operator|.
name|query
argument_list|(
name|sql
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"x=1; y=[3]"
argument_list|,
literal|"x=1; y=[4]"
argument_list|,
literal|"x=2; y=[3]"
argument_list|,
literal|"x=2; y=[4]"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|chainedUnnestArrayOfArrays2
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select * from (values (1), (2)) T1(x),"
operator|+
literal|"UNNEST(array[array[3, 4], array[4, 5]]) as T2(y)"
decl_stmt|;
name|tester
argument_list|()
operator|.
name|query
argument_list|(
name|sql
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"x=1; y=[3, 4]"
argument_list|,
literal|"x=1; y=[4, 5]"
argument_list|,
literal|"x=2; y=[3, 4]"
argument_list|,
literal|"x=2; y=[4, 5]"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|chainedUnnestArrayOfArrays3
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select * from (values (1), (2)) T1(x),"
operator|+
literal|"UNNEST(array[array[array[3,4], array[4,5]], array[array[7,8], array[9,10]]]) as T2(y)"
decl_stmt|;
name|tester
argument_list|()
operator|.
name|query
argument_list|(
name|sql
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"x=1; y=[[3, 4], [4, 5]]"
argument_list|,
literal|"x=1; y=[[7, 8], [9, 10]]"
argument_list|,
literal|"x=2; y=[[3, 4], [4, 5]]"
argument_list|,
literal|"x=2; y=[[7, 8], [9, 10]]"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|chainedUnnestArrayOfRows
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select * from (values (1), (2)) T1(x),"
operator|+
literal|"UNNEST(array[ROW(3), ROW(4)]) as T2(y)"
decl_stmt|;
name|tester
argument_list|()
operator|.
name|query
argument_list|(
name|sql
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"x=1; y=3"
argument_list|,
literal|"x=1; y=4"
argument_list|,
literal|"x=2; y=3"
argument_list|,
literal|"x=2; y=4"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|chainedUnnestArrayOfRows2
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select * from (values (1), (2)) T1(x),"
operator|+
literal|"UNNEST(array[ROW(3, 5), ROW(4, 6)]) as T2(y, z)"
decl_stmt|;
name|tester
argument_list|()
operator|.
name|query
argument_list|(
name|sql
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"x=1; y=3; z=5"
argument_list|,
literal|"x=1; y=4; z=6"
argument_list|,
literal|"x=2; y=3; z=5"
argument_list|,
literal|"x=2; y=4; z=6"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|chainedUnnestArrayOfRows3
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select * from (values (1), (2)) T1(x),"
operator|+
literal|"UNNEST(array[ROW(3), ROW(4)]) WITH ORDINALITY as T2(y, o)"
decl_stmt|;
name|tester
argument_list|()
operator|.
name|query
argument_list|(
name|sql
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"x=1; y=3; o=1"
argument_list|,
literal|"x=1; y=4; o=2"
argument_list|,
literal|"x=2; y=3; o=1"
argument_list|,
literal|"x=2; y=4; o=2"
argument_list|)
expr_stmt|;
block|}
specifier|private
name|CalciteAssert
operator|.
name|AssertThat
name|tester
parameter_list|()
block|{
return|return
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|CalciteConnectionProperty
operator|.
name|LEX
argument_list|,
name|Lex
operator|.
name|JAVA
argument_list|)
operator|.
name|with
argument_list|(
name|CalciteConnectionProperty
operator|.
name|FORCE_DECORRELATE
argument_list|,
literal|false
argument_list|)
return|;
block|}
block|}
end_class

end_unit

