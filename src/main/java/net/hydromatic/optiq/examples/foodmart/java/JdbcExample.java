begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|examples
operator|.
name|foodmart
operator|.
name|java
package|;
end_package

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
name|jdbc
operator|.
name|OptiqConnection
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
name|RelDataTypeFactory
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Example of using JLINQ via JDBC.  *  *<p>Schema is specified programmatically.</p>  */
end_comment

begin_class
specifier|public
class|class
name|JdbcExample
block|{
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Exception
block|{
operator|new
name|JdbcExample
argument_list|()
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|run
parameter_list|()
throws|throws
name|ClassNotFoundException
throws|,
name|SQLException
block|{
name|Class
operator|.
name|forName
argument_list|(
literal|"net.hydromatic.optiq.jdbc.Driver"
argument_list|)
expr_stmt|;
name|Connection
name|connection
init|=
name|DriverManager
operator|.
name|getConnection
argument_list|(
literal|"jdbc:optiq:"
argument_list|)
decl_stmt|;
name|OptiqConnection
name|optiqConnection
init|=
name|connection
operator|.
name|unwrap
argument_list|(
name|OptiqConnection
operator|.
name|class
argument_list|)
decl_stmt|;
name|RelDataTypeFactory
name|typeFactory
init|=
name|optiqConnection
operator|.
name|getTypeFactory
argument_list|()
decl_stmt|;
name|optiqConnection
operator|.
name|getRootSchema
argument_list|()
operator|.
name|add
argument_list|(
literal|"hr"
argument_list|,
operator|new
name|ReflectiveSchema
argument_list|(
operator|new
name|HrSchema
argument_list|()
argument_list|,
name|typeFactory
argument_list|)
argument_list|)
expr_stmt|;
name|optiqConnection
operator|.
name|getRootSchema
argument_list|()
operator|.
name|add
argument_list|(
literal|"foodmart"
argument_list|,
operator|new
name|ReflectiveSchema
argument_list|(
operator|new
name|FoodmartSchema
argument_list|()
argument_list|,
name|typeFactory
argument_list|)
argument_list|)
expr_stmt|;
name|Statement
name|statement
init|=
name|connection
operator|.
name|createStatement
argument_list|()
decl_stmt|;
name|ResultSet
name|resultSet
init|=
name|statement
operator|.
name|executeQuery
argument_list|(
literal|"select *\n"
operator|+
literal|"from \"foodmart\".\"sales_fact_1997\" as s\n"
operator|+
literal|"join \"hr\".\"emps\" as e\n"
operator|+
literal|"on e.\"empid\" = s.\"cust_id\""
argument_list|)
decl_stmt|;
name|resultSet
operator|.
name|close
argument_list|()
expr_stmt|;
name|statement
operator|.
name|close
argument_list|()
expr_stmt|;
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
specifier|public
specifier|static
class|class
name|HrSchema
block|{
specifier|public
specifier|final
name|Employee
index|[]
name|emps
init|=
block|{          }
decl_stmt|;
block|}
specifier|public
specifier|static
class|class
name|Employee
block|{
specifier|public
specifier|final
name|int
name|empid
decl_stmt|;
specifier|public
name|Employee
parameter_list|(
name|int
name|empid
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|empid
operator|=
name|empid
expr_stmt|;
block|}
block|}
specifier|public
specifier|static
class|class
name|FoodmartSchema
block|{
specifier|public
specifier|final
name|SalesFact
index|[]
name|sales_fact_1997
init|=
block|{          }
decl_stmt|;
block|}
specifier|public
specifier|static
class|class
name|SalesFact
block|{
specifier|public
specifier|final
name|int
name|cust_id
decl_stmt|;
specifier|public
specifier|final
name|int
name|prod_id
decl_stmt|;
specifier|public
name|SalesFact
parameter_list|(
name|int
name|cust_id
parameter_list|,
name|int
name|prod_id
parameter_list|)
block|{
name|this
operator|.
name|cust_id
operator|=
name|cust_id
expr_stmt|;
name|this
operator|.
name|prod_id
operator|=
name|prod_id
expr_stmt|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End JdbcExample.java
end_comment

end_unit

