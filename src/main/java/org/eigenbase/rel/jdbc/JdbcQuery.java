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
name|rel
operator|.
name|jdbc
package|;
end_package

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
name|javax
operator|.
name|sql
operator|.
name|*
import|;
end_import

begin_import
import|import
name|openjava
operator|.
name|ptree
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
name|oj
operator|.
name|rel
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
name|oj
operator|.
name|util
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
name|rel
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
name|rel
operator|.
name|metadata
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
name|relopt
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
name|fun
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
name|parser
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
name|util
operator|.
name|SqlString
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * A<code>JdbcQuery</code> is a relational expression whose source is a SQL  * statement executed against a JDBC data source. It has {@link  * CallingConvention#RESULT_SET result set calling convention}.  *  * @author jhyde  * @version $Id$  * @since 2 August, 2002  */
end_comment

begin_class
specifier|public
class|class
name|JdbcQuery
extends|extends
name|AbstractRelNode
implements|implements
name|ResultSetRel
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|private
specifier|final
name|DataSource
name|dataSource
decl_stmt|;
name|SqlDialect
name|dialect
decl_stmt|;
name|SqlSelect
name|sql
decl_stmt|;
comment|/**      * For debug. Set on register.      */
specifier|protected
name|SqlString
name|queryString
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**      * Creates a<code>JdbcQuery</code>.      *      * @param cluster {@link RelOptCluster}  this relational expression belongs      * to      * @param sql SQL parse tree, may be null, otherwise must be a SELECT      * statement      * @param dataSource Provides a JDBC connection to run this query against.      *      *<p>In saffron, if the query is implementing a JDBC table, then the      * connection's schema will implement<code>      * net.sf.saffron.ext.JdbcSchema</code>, and data source will typically be      * the same as calling the<code>getDataSource()</code> method on that      * schema. But non-JDBC schemas are also acceptable.      *      * @pre sql == null || sql.isA(SqlNode.Kind.Select)      * @pre dataSource != null      */
specifier|public
name|JdbcQuery
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|RelDataType
name|rowType
parameter_list|,
name|SqlDialect
name|dialect
parameter_list|,
name|SqlSelect
name|sql
parameter_list|,
name|DataSource
name|dataSource
parameter_list|)
block|{
name|super
argument_list|(
name|cluster
argument_list|,
name|cluster
operator|.
name|traitSetOf
argument_list|(
name|CallingConvention
operator|.
name|RESULT_SET
argument_list|)
argument_list|)
expr_stmt|;
name|Util
operator|.
name|pre
argument_list|(
name|dataSource
operator|!=
literal|null
argument_list|,
literal|"dataSource != null"
argument_list|)
expr_stmt|;
name|this
operator|.
name|rowType
operator|=
name|rowType
expr_stmt|;
name|this
operator|.
name|dialect
operator|=
name|dialect
expr_stmt|;
if|if
condition|(
name|sql
operator|==
literal|null
condition|)
block|{
name|sql
operator|=
name|SqlStdOperatorTable
operator|.
name|selectOperator
operator|.
name|createCall
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|SqlParserPos
operator|.
name|ZERO
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|Util
operator|.
name|pre
argument_list|(
name|sql
operator|.
name|getKind
argument_list|()
operator|==
name|SqlKind
operator|.
name|SELECT
argument_list|,
literal|"sql == null || sql.isA(SqlNode.Kind.Select)"
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|sql
operator|=
name|sql
expr_stmt|;
name|this
operator|.
name|dataSource
operator|=
name|dataSource
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
comment|/**      * Returns the JDBC data source      *      * @return data source      */
specifier|public
name|DataSource
name|getDataSource
parameter_list|()
block|{
return|return
name|dataSource
return|;
block|}
comment|/**      * @return the SQL dialect understood by the data source      */
specifier|public
name|SqlDialect
name|getDialect
parameter_list|()
block|{
return|return
name|dialect
return|;
block|}
comment|// override RelNode
specifier|public
name|void
name|explain
parameter_list|(
name|RelOptPlanWriter
name|pw
parameter_list|)
block|{
name|pw
operator|.
name|explain
argument_list|(
name|this
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"foreignSql"
block|}
argument_list|,
operator|new
name|Object
index|[]
block|{
name|getForeignSql
argument_list|()
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**      * Returns the SQL that this query will execute against the foreign      * database, in the SQL dialect of that database.      *      * @return foreign SQL      *      * @see #getSql()      */
specifier|public
name|SqlString
name|getForeignSql
parameter_list|()
block|{
if|if
condition|(
name|queryString
operator|==
literal|null
condition|)
block|{
name|queryString
operator|=
name|sql
operator|.
name|toSqlString
argument_list|(
name|dialect
argument_list|)
expr_stmt|;
block|}
return|return
name|queryString
return|;
block|}
specifier|public
name|RelNode
name|copy
parameter_list|(
name|RelTraitSet
name|traitSet
parameter_list|,
name|List
argument_list|<
name|RelNode
argument_list|>
name|inputs
parameter_list|)
block|{
assert|assert
name|inputs
operator|.
name|isEmpty
argument_list|()
assert|;
assert|assert
name|traitSet
operator|.
name|comprises
argument_list|(
name|CallingConvention
operator|.
name|RESULT_SET
argument_list|)
assert|;
return|return
operator|new
name|JdbcQuery
argument_list|(
name|getCluster
argument_list|()
argument_list|,
name|rowType
argument_list|,
name|dialect
argument_list|,
name|sql
argument_list|,
name|dataSource
argument_list|)
return|;
block|}
specifier|public
name|RelOptCost
name|computeSelfCost
parameter_list|(
name|RelOptPlanner
name|planner
parameter_list|)
block|{
comment|// Very difficult to estimate the cost of a remote query: (a) we don't
comment|// know what plans are available to the remote RDBMS, (b) we don't
comment|// know relative speed of the other CPU, or the bandwidth. This
comment|// estimate selfishly deals with the cost to THIS system, but it still
comment|// neglects the effects of latency.
name|double
name|rows
init|=
name|RelMetadataQuery
operator|.
name|getRowCount
argument_list|(
name|this
argument_list|)
operator|/
literal|2
decl_stmt|;
comment|// Very difficult to estimate the cost of a remote query: (a) we don't
comment|// know what plans are available to the remote RDBMS, (b) we don't
comment|// know relative speed of the other CPU, or the bandwidth. This
comment|// estimate selfishly deals with the cost to THIS system, but it still
comment|// neglects the effects of latency.
name|double
name|cpu
init|=
literal|0
decl_stmt|;
comment|// Very difficult to estimate the cost of a remote query: (a) we don't
comment|// know what plans are available to the remote RDBMS, (b) we don't
comment|// know relative speed of the other CPU, or the bandwidth. This
comment|// estimate selfishly deals with the cost to THIS system, but it still
comment|// neglects the effects of latency.
name|double
name|io
init|=
literal|0
comment|/*rows*/
decl_stmt|;
return|return
name|planner
operator|.
name|makeCost
argument_list|(
name|rows
argument_list|,
name|cpu
argument_list|,
name|io
argument_list|)
return|;
block|}
specifier|public
name|RelNode
name|onRegister
parameter_list|(
name|RelOptPlanner
name|planner
parameter_list|)
block|{
name|JdbcQuery
name|r
init|=
operator|(
name|JdbcQuery
operator|)
name|super
operator|.
name|onRegister
argument_list|(
name|planner
argument_list|)
decl_stmt|;
name|Util
operator|.
name|discard
argument_list|(
name|r
operator|.
name|getForeignSql
argument_list|()
argument_list|)
expr_stmt|;
comment|// compute query string now
return|return
name|r
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|register
parameter_list|(
name|RelOptPlanner
name|planner
parameter_list|)
block|{
comment|// nothing for now
name|super
operator|.
name|register
argument_list|(
name|planner
argument_list|)
expr_stmt|;
block|}
specifier|public
name|ParseTree
name|implement
parameter_list|(
name|JavaRelImplementor
name|implementor
parameter_list|)
block|{
comment|// Generate
comment|//   ((javax.sql.DataSource) connection).getConnection().
comment|//       createStatement().executeQuery(<<query string>>);
comment|//
comment|// The above assumes that the datasource expression is the default,
comment|// namely
comment|//
comment|//   (javax.sql.DataSource) connection
comment|//
comment|// Issue#1. We should really wrap this in
comment|//
comment|// Statement statement = null;
comment|// try {
comment|//   ...
comment|//   statement = connection.getConnection.createStatement();
comment|//   ...
comment|// } catch (java.sql.SQLException e) {
comment|//    throw new saffron.runtime.SaffronError(e);
comment|// } finally {
comment|//    if (stmt != null) {
comment|//       try {
comment|//          stmt.close();
comment|//       } catch {}
comment|//    }
comment|// }
comment|//
comment|// This is all a horrible hack. Need a way to 'freeze' a DataSource
comment|// into a Java expression which can be 'thawed' into a DataSource
comment|// at run-time. We should use the OJConnectionRegistry somehow.
comment|// This is all old Saffron stuff; Farrago uses its own
comment|// mechanism which works just fine.
assert|assert
name|dataSource
operator|instanceof
name|JdbcDataSource
assert|;
comment|// hack
comment|// DriverManager.getConnection("jdbc...", "scott", "tiger");
specifier|final
name|String
name|url
init|=
operator|(
operator|(
name|JdbcDataSource
operator|)
name|dataSource
operator|)
operator|.
name|getUrl
argument_list|()
decl_stmt|;
specifier|final
name|MethodCall
name|connectionExpr
init|=
operator|new
name|MethodCall
argument_list|(
name|OJUtil
operator|.
name|typeNameForClass
argument_list|(
name|java
operator|.
name|sql
operator|.
name|DriverManager
operator|.
name|class
argument_list|)
argument_list|,
literal|"getConnection"
argument_list|,
operator|new
name|ExpressionList
argument_list|(
name|Literal
operator|.
name|makeLiteral
argument_list|(
name|url
argument_list|)
argument_list|,
name|Literal
operator|.
name|makeLiteral
argument_list|(
literal|"SA"
argument_list|)
argument_list|,
name|Literal
operator|.
name|makeLiteral
argument_list|(
literal|""
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
return|return
operator|new
name|MethodCall
argument_list|(
operator|new
name|MethodCall
argument_list|(
name|connectionExpr
argument_list|,
literal|"createStatement"
argument_list|,
literal|null
argument_list|)
argument_list|,
literal|"executeQuery"
argument_list|,
operator|new
name|ExpressionList
argument_list|(
name|Literal
operator|.
name|makeLiteral
argument_list|(
name|queryString
operator|.
name|getSql
argument_list|()
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
comment|/**      * Returns the parse tree of the SQL statement that populates this query.      *      * @return SQL query      */
specifier|public
name|SqlSelect
name|getSql
parameter_list|()
block|{
return|return
name|sql
return|;
block|}
block|}
end_class

begin_comment
comment|// End JdbcQuery.java
end_comment

end_unit

