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
package|;
end_package

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_comment
comment|/**  * Validation tests for the {@code MATCH_RECOGNIZE} clause.  */
end_comment

begin_class
specifier|public
class|class
name|SqlValidatorMatchTest
extends|extends
name|SqlValidatorTestCase
block|{
comment|/** Tries to create a calls to some internal operators in    * MATCH_RECOGNIZE. Should fail. */
annotation|@
name|Test
specifier|public
name|void
name|testMatchRecognizeInternals
parameter_list|()
throws|throws
name|Exception
block|{
name|sql
argument_list|(
literal|"values ^pattern_exclude(1, 2)^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"No match found for function signature .*"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"values ^\"|\"(1, 2)^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"No match found for function signature .*"
argument_list|)
expr_stmt|;
comment|// FINAL and other functions should not be visible outside of
comment|// MATCH_RECOGNIZE
name|sql
argument_list|(
literal|"values ^\"FINAL\"(1, 2)^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"No match found for function signature FINAL\\(<NUMERIC>,<NUMERIC>\\)"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"values ^\"RUNNING\"(1, 2)^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"No match found for function signature RUNNING\\(<NUMERIC>,<NUMERIC>\\)"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"values ^\"FIRST\"(1, 2)^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Function 'FIRST\\(1, 2\\)' can only be used in MATCH_RECOGNIZE"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"values ^\"LAST\"(1, 2)^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Function 'LAST\\(1, 2\\)' can only be used in MATCH_RECOGNIZE"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"values ^\"PREV\"(1, 2)^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Function 'PREV\\(1, 2\\)' can only be used in MATCH_RECOGNIZE"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMatchRecognizeDefines
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"  from emp match_recognize (\n"
operator|+
literal|"    pattern (strt down+ up+)\n"
operator|+
literal|"    define\n"
operator|+
literal|"      down as down.sal< PREV(down.sal),\n"
operator|+
literal|"      up as up.sal> PREV(up.sal)\n"
operator|+
literal|"  ) mr"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMatchRecognizeDefines2
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"  from t match_recognize (\n"
operator|+
literal|"    pattern (strt down+ up+)\n"
operator|+
literal|"    define\n"
operator|+
literal|"      down as down.price< PREV(down.price),\n"
operator|+
literal|"      ^down as up.price> PREV(up.price)^\n"
operator|+
literal|"  ) mr"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Pattern variable 'DOWN' has already been defined"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMatchRecognizeDefines3
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"  from emp match_recognize (\n"
operator|+
literal|"    pattern (strt down+up+)\n"
operator|+
literal|"    define\n"
operator|+
literal|"      down as down.sal< PREV(down.sal),\n"
operator|+
literal|"      up as up.sal> PREV(up.sal)\n"
operator|+
literal|"  ) mr"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMatchRecognizeDefines4
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"  from emp match_recognize (\n"
operator|+
literal|"    pattern (strt down+ up+)\n"
operator|+
literal|"    define\n"
operator|+
literal|"      down as down.sal< PREV(down.sal),\n"
operator|+
literal|"      up as up.sal> FIRST(^PREV(up.sal)^)\n"
operator|+
literal|"  ) mr"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Cannot nest PREV/NEXT under LAST/FIRST 'PREV\\(`UP`\\.`SAL`, 1\\)'"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMatchRecognizeDefines5
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"  from emp match_recognize (\n"
operator|+
literal|"    pattern (strt down+ up+)\n"
operator|+
literal|"    define\n"
operator|+
literal|"      down as down.sal< PREV(down.sal),\n"
operator|+
literal|"      up as up.sal> FIRST(^FIRST(up.sal)^)\n"
operator|+
literal|"  ) mr"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Cannot nest PREV/NEXT under LAST/FIRST 'FIRST\\(`UP`\\.`SAL`, 0\\)'"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMatchRecognizeDefines6
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"  from emp match_recognize (\n"
operator|+
literal|"    pattern (strt down+ up+)\n"
operator|+
literal|"    define\n"
operator|+
literal|"      down as down.sal< PREV(down.sal),\n"
operator|+
literal|"      up as up.sal> ^COUNT(down.sal, up.sal)^\n"
operator|+
literal|"  ) mr"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Invalid number of parameters to COUNT method"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMatchRecognizeMeasures1
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"  from emp match_recognize (\n"
operator|+
literal|"    measures STRT.sal as start_sal,"
operator|+
literal|"      ^LAST(null)^ as bottom_sal,"
operator|+
literal|"      LAST(up.ts) as end_sal"
operator|+
literal|"    pattern (strt down+ up+)\n"
operator|+
literal|"    define\n"
operator|+
literal|"      down as down.sal< PREV(down.sal),\n"
operator|+
literal|"      up as up.sal> prev(up.sal)\n"
operator|+
literal|"  ) mr"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Null parameters in 'LAST\\(NULL, 0\\)'"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMatchRecognizeSkipTo1
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"  from emp match_recognize (\n"
operator|+
literal|"    after match skip to ^null^\n"
operator|+
literal|"    measures\n"
operator|+
literal|"      STRT.sal as start_sal,\n"
operator|+
literal|"      LAST(up.ts) as end_sal\n"
operator|+
literal|"    pattern (strt down+ up+)\n"
operator|+
literal|"    define\n"
operator|+
literal|"      down as down.sal< PREV(down.sal),\n"
operator|+
literal|"      up as up.sal> prev(up.sal)\n"
operator|+
literal|"  ) mr"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Encountered \"to null\" at .*"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMatchRecognizeSkipTo2
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"  from emp match_recognize (\n"
operator|+
literal|"    after match skip to ^no_exists^\n"
operator|+
literal|"    measures\n"
operator|+
literal|"      STRT.sal as start_sal,"
operator|+
literal|"      LAST(up.ts) as end_sal"
operator|+
literal|"    pattern (strt down+ up+)\n"
operator|+
literal|"    define\n"
operator|+
literal|"      down as down.sal< PREV(down.sal),\n"
operator|+
literal|"      up as up.sal> prev(up.sal)\n"
operator|+
literal|"  ) mr"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Encountered \"measures\" at .*"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End SqlValidatorMatchTest.java
end_comment

end_unit

