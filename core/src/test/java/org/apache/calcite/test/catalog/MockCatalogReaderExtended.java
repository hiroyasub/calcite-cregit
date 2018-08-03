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
name|catalog
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
name|TableMacro
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
name|TranslatableTable
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

begin_comment
comment|/** Adds some extra tables to the mock catalog. These increase the time and  * complexity of initializing the catalog (because they contain views whose  * SQL needs to be parsed) and so are not used for all tests. */
end_comment

begin_class
specifier|public
class|class
name|MockCatalogReaderExtended
extends|extends
name|MockCatalogReaderSimple
block|{
comment|/**    * Creates a MockCatalogReader.    *    *<p>Caller must then call {@link #init} to populate with data.</p>    *    * @param typeFactory   Type factory    * @param caseSensitive case sensitivity    */
specifier|public
name|MockCatalogReaderExtended
parameter_list|(
name|RelDataTypeFactory
name|typeFactory
parameter_list|,
name|boolean
name|caseSensitive
parameter_list|)
block|{
name|super
argument_list|(
name|typeFactory
argument_list|,
name|caseSensitive
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|MockCatalogReader
name|init
parameter_list|()
block|{
name|super
operator|.
name|init
argument_list|()
expr_stmt|;
name|MockSchema
name|salesSchema
init|=
operator|new
name|MockSchema
argument_list|(
literal|"SALES"
argument_list|)
decl_stmt|;
comment|// Same as "EMP_20" except it uses ModifiableViewTable which populates
comment|// constrained columns with default values on INSERT and has a single constraint on DEPTNO.
name|List
argument_list|<
name|String
argument_list|>
name|empModifiableViewNames
init|=
name|ImmutableList
operator|.
name|of
argument_list|(
name|salesSchema
operator|.
name|getCatalogName
argument_list|()
argument_list|,
name|salesSchema
operator|.
name|getName
argument_list|()
argument_list|,
literal|"EMP_MODIFIABLEVIEW"
argument_list|)
decl_stmt|;
name|TableMacro
name|empModifiableViewMacro
init|=
name|MockModifiableViewRelOptTable
operator|.
name|viewMacro
argument_list|(
name|rootSchema
argument_list|,
literal|"select EMPNO, ENAME, JOB, MGR, HIREDATE, SAL, COMM, SLACKER from EMPDEFAULTS"
operator|+
literal|" where DEPTNO = 20"
argument_list|,
name|empModifiableViewNames
operator|.
name|subList
argument_list|(
literal|0
argument_list|,
literal|2
argument_list|)
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
name|empModifiableViewNames
operator|.
name|get
argument_list|(
literal|2
argument_list|)
argument_list|)
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|TranslatableTable
name|empModifiableView
init|=
name|empModifiableViewMacro
operator|.
name|apply
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|()
argument_list|)
decl_stmt|;
name|MockModifiableViewRelOptTable
name|mockEmpViewTable
init|=
name|MockModifiableViewRelOptTable
operator|.
name|create
argument_list|(
operator|(
name|MockModifiableViewRelOptTable
operator|.
name|MockModifiableViewTable
operator|)
name|empModifiableView
argument_list|,
name|this
argument_list|,
name|empModifiableViewNames
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|empModifiableViewNames
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|,
name|empModifiableViewNames
operator|.
name|get
argument_list|(
literal|2
argument_list|)
argument_list|,
literal|false
argument_list|,
literal|20
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|registerTable
argument_list|(
name|mockEmpViewTable
argument_list|)
expr_stmt|;
comment|// Same as "EMP_MODIFIABLEVIEW" except that all columns are in the view, columns are reordered,
comment|// and there is an `extra` extended column.
name|List
argument_list|<
name|String
argument_list|>
name|empModifiableViewNames2
init|=
name|ImmutableList
operator|.
name|of
argument_list|(
name|salesSchema
operator|.
name|getCatalogName
argument_list|()
argument_list|,
name|salesSchema
operator|.
name|getName
argument_list|()
argument_list|,
literal|"EMP_MODIFIABLEVIEW2"
argument_list|)
decl_stmt|;
name|TableMacro
name|empModifiableViewMacro2
init|=
name|MockModifiableViewRelOptTable
operator|.
name|viewMacro
argument_list|(
name|rootSchema
argument_list|,
literal|"select ENAME, EMPNO, JOB, DEPTNO, SLACKER, SAL, EXTRA, HIREDATE, MGR, COMM"
operator|+
literal|" from EMPDEFAULTS extend (EXTRA boolean)"
operator|+
literal|" where DEPTNO = 20"
argument_list|,
name|empModifiableViewNames2
operator|.
name|subList
argument_list|(
literal|0
argument_list|,
literal|2
argument_list|)
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
name|empModifiableViewNames
operator|.
name|get
argument_list|(
literal|2
argument_list|)
argument_list|)
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|TranslatableTable
name|empModifiableView2
init|=
name|empModifiableViewMacro2
operator|.
name|apply
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|()
argument_list|)
decl_stmt|;
name|MockModifiableViewRelOptTable
name|mockEmpViewTable2
init|=
name|MockModifiableViewRelOptTable
operator|.
name|create
argument_list|(
operator|(
name|MockModifiableViewRelOptTable
operator|.
name|MockModifiableViewTable
operator|)
name|empModifiableView2
argument_list|,
name|this
argument_list|,
name|empModifiableViewNames2
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|empModifiableViewNames2
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|,
name|empModifiableViewNames2
operator|.
name|get
argument_list|(
literal|2
argument_list|)
argument_list|,
literal|false
argument_list|,
literal|20
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|registerTable
argument_list|(
name|mockEmpViewTable2
argument_list|)
expr_stmt|;
comment|// Same as "EMP_MODIFIABLEVIEW" except that comm is not in the view.
name|List
argument_list|<
name|String
argument_list|>
name|empModifiableViewNames3
init|=
name|ImmutableList
operator|.
name|of
argument_list|(
name|salesSchema
operator|.
name|getCatalogName
argument_list|()
argument_list|,
name|salesSchema
operator|.
name|getName
argument_list|()
argument_list|,
literal|"EMP_MODIFIABLEVIEW3"
argument_list|)
decl_stmt|;
name|TableMacro
name|empModifiableViewMacro3
init|=
name|MockModifiableViewRelOptTable
operator|.
name|viewMacro
argument_list|(
name|rootSchema
argument_list|,
literal|"select EMPNO, ENAME, JOB, MGR, HIREDATE, SAL, SLACKER from EMPDEFAULTS"
operator|+
literal|" where DEPTNO = 20"
argument_list|,
name|empModifiableViewNames3
operator|.
name|subList
argument_list|(
literal|0
argument_list|,
literal|2
argument_list|)
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
name|empModifiableViewNames3
operator|.
name|get
argument_list|(
literal|2
argument_list|)
argument_list|)
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|TranslatableTable
name|empModifiableView3
init|=
name|empModifiableViewMacro3
operator|.
name|apply
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|()
argument_list|)
decl_stmt|;
name|MockModifiableViewRelOptTable
name|mockEmpViewTable3
init|=
name|MockModifiableViewRelOptTable
operator|.
name|create
argument_list|(
operator|(
name|MockModifiableViewRelOptTable
operator|.
name|MockModifiableViewTable
operator|)
name|empModifiableView3
argument_list|,
name|this
argument_list|,
name|empModifiableViewNames3
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|empModifiableViewNames3
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|,
name|empModifiableViewNames3
operator|.
name|get
argument_list|(
literal|2
argument_list|)
argument_list|,
literal|false
argument_list|,
literal|20
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|registerTable
argument_list|(
name|mockEmpViewTable3
argument_list|)
expr_stmt|;
name|MockSchema
name|structTypeSchema
init|=
operator|new
name|MockSchema
argument_list|(
literal|"STRUCT"
argument_list|)
decl_stmt|;
name|registerSchema
argument_list|(
name|structTypeSchema
argument_list|)
expr_stmt|;
specifier|final
name|Fixture
name|f
init|=
operator|new
name|Fixture
argument_list|(
name|typeFactory
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|CompoundNameColumn
argument_list|>
name|columnsExtended
init|=
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|CompoundNameColumn
argument_list|(
literal|""
argument_list|,
literal|"K0"
argument_list|,
name|f
operator|.
name|varchar20TypeNull
argument_list|)
argument_list|,
operator|new
name|CompoundNameColumn
argument_list|(
literal|""
argument_list|,
literal|"C1"
argument_list|,
name|f
operator|.
name|varchar20TypeNull
argument_list|)
argument_list|,
operator|new
name|CompoundNameColumn
argument_list|(
literal|"F0"
argument_list|,
literal|"C0"
argument_list|,
name|f
operator|.
name|intType
argument_list|)
argument_list|,
operator|new
name|CompoundNameColumn
argument_list|(
literal|"F1"
argument_list|,
literal|"C1"
argument_list|,
name|f
operator|.
name|intTypeNull
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|CompoundNameColumn
argument_list|>
name|extendedColumns
init|=
operator|new
name|ArrayList
argument_list|<
name|CompoundNameColumn
argument_list|>
argument_list|(
name|columnsExtended
argument_list|)
decl_stmt|;
name|extendedColumns
operator|.
name|add
argument_list|(
operator|new
name|CompoundNameColumn
argument_list|(
literal|"F2"
argument_list|,
literal|"C2"
argument_list|,
name|f
operator|.
name|varchar20Type
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|CompoundNameColumnResolver
name|structExtendedTableResolver
init|=
operator|new
name|CompoundNameColumnResolver
argument_list|(
name|extendedColumns
argument_list|,
literal|"F0"
argument_list|)
decl_stmt|;
specifier|final
name|MockTable
name|structExtendedTypeTable
init|=
name|MockTable
operator|.
name|create
argument_list|(
name|this
argument_list|,
name|structTypeSchema
argument_list|,
literal|"T_EXTEND"
argument_list|,
literal|false
argument_list|,
literal|100
argument_list|,
name|structExtendedTableResolver
argument_list|)
decl_stmt|;
for|for
control|(
name|CompoundNameColumn
name|column
range|:
name|columnsExtended
control|)
block|{
name|structExtendedTypeTable
operator|.
name|addColumn
argument_list|(
name|column
operator|.
name|getName
argument_list|()
argument_list|,
name|column
operator|.
name|type
argument_list|)
expr_stmt|;
block|}
name|registerTable
argument_list|(
name|structExtendedTypeTable
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
block|}
end_class

begin_comment
comment|// End MockCatalogReaderExtended.java
end_comment

end_unit
