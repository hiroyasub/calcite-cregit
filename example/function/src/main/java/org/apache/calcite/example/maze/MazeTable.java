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
name|example
operator|.
name|maze
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
name|DataContext
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
name|linq4j
operator|.
name|AbstractEnumerable
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
name|linq4j
operator|.
name|Enumerable
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
name|linq4j
operator|.
name|Enumerator
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
name|linq4j
operator|.
name|Linq4j
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
name|schema
operator|.
name|ScannableTable
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
name|impl
operator|.
name|AbstractTable
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
name|Util
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_comment
comment|/**  * User-defined table function that generates a Maze and prints it in text  * form.  */
end_comment

begin_class
specifier|public
class|class
name|MazeTable
extends|extends
name|AbstractTable
implements|implements
name|ScannableTable
block|{
specifier|final
name|int
name|width
decl_stmt|;
specifier|final
name|int
name|height
decl_stmt|;
specifier|final
name|int
name|seed
decl_stmt|;
specifier|final
name|boolean
name|solution
decl_stmt|;
specifier|private
name|MazeTable
parameter_list|(
name|int
name|width
parameter_list|,
name|int
name|height
parameter_list|,
name|int
name|seed
parameter_list|,
name|boolean
name|solution
parameter_list|)
block|{
name|this
operator|.
name|width
operator|=
name|width
expr_stmt|;
name|this
operator|.
name|height
operator|=
name|height
expr_stmt|;
name|this
operator|.
name|seed
operator|=
name|seed
expr_stmt|;
name|this
operator|.
name|solution
operator|=
name|solution
expr_stmt|;
block|}
comment|/** Table function that generates a maze.    *    *<p>Called by reflection based on the definition of the user-defined    * function in the schema.    *    * @param width Width of maze    * @param height Height of maze    * @param seed Random number seed, or -1 to create an unseeded random    * @return Table that prints the maze in text form    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
comment|// called via reflection
specifier|public
specifier|static
name|ScannableTable
name|generate
parameter_list|(
name|int
name|width
parameter_list|,
name|int
name|height
parameter_list|,
name|int
name|seed
parameter_list|)
block|{
return|return
operator|new
name|MazeTable
argument_list|(
name|width
argument_list|,
name|height
argument_list|,
name|seed
argument_list|,
literal|false
argument_list|)
return|;
block|}
comment|/** Table function that generates a maze with a solution.    *    *<p>Called by reflection based on the definition of the user-defined    * function in the schema.    *    * @param width Width of maze    * @param height Height of maze    * @param seed Random number seed, or -1 to create an unseeded random    * @return Table that prints the maze in text form, with solution shown    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
comment|// called via reflection
specifier|public
specifier|static
name|ScannableTable
name|solve
parameter_list|(
name|int
name|width
parameter_list|,
name|int
name|height
parameter_list|,
name|int
name|seed
parameter_list|)
block|{
return|return
operator|new
name|MazeTable
argument_list|(
name|width
argument_list|,
name|height
argument_list|,
name|seed
argument_list|,
literal|true
argument_list|)
return|;
block|}
specifier|public
name|RelDataType
name|getRowType
parameter_list|(
name|RelDataTypeFactory
name|typeFactory
parameter_list|)
block|{
return|return
name|typeFactory
operator|.
name|builder
argument_list|()
operator|.
name|add
argument_list|(
literal|"S"
argument_list|,
name|SqlTypeName
operator|.
name|VARCHAR
argument_list|,
name|width
operator|*
literal|3
operator|+
literal|1
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
specifier|public
name|Enumerable
argument_list|<
name|Object
index|[]
argument_list|>
name|scan
parameter_list|(
name|DataContext
name|root
parameter_list|)
block|{
specifier|final
name|Random
name|random
init|=
name|seed
operator|>=
literal|0
condition|?
operator|new
name|Random
argument_list|(
name|seed
argument_list|)
else|:
operator|new
name|Random
argument_list|()
decl_stmt|;
specifier|final
name|Maze
name|maze
init|=
operator|new
name|Maze
argument_list|(
name|width
argument_list|,
name|height
argument_list|)
decl_stmt|;
specifier|final
name|PrintWriter
name|pw
init|=
name|Util
operator|.
name|printWriter
argument_list|(
name|System
operator|.
name|out
argument_list|)
decl_stmt|;
name|maze
operator|.
name|layout
argument_list|(
name|random
argument_list|,
name|pw
argument_list|)
expr_stmt|;
if|if
condition|(
name|Maze
operator|.
name|DEBUG
condition|)
block|{
name|maze
operator|.
name|print
argument_list|(
name|pw
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|AbstractEnumerable
argument_list|<
name|Object
index|[]
argument_list|>
argument_list|()
block|{
specifier|public
name|Enumerator
argument_list|<
name|Object
index|[]
argument_list|>
name|enumerator
parameter_list|()
block|{
specifier|final
name|Set
argument_list|<
name|Integer
argument_list|>
name|solutionSet
decl_stmt|;
if|if
condition|(
name|solution
condition|)
block|{
name|solutionSet
operator|=
name|maze
operator|.
name|solve
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|solutionSet
operator|=
literal|null
expr_stmt|;
block|}
return|return
name|Linq4j
operator|.
name|transform
argument_list|(
name|maze
operator|.
name|enumerator
argument_list|(
name|solutionSet
argument_list|)
argument_list|,
name|s
lambda|->
operator|new
name|Object
index|[]
block|{
name|s
block|}
argument_list|)
return|;
block|}
block|}
return|;
block|}
block|}
end_class

begin_comment
comment|// End MazeTable.java
end_comment

end_unit

