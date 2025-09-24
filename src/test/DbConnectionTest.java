package test;
import java.sql.*;
import static org.junit.Assert.*;
import db.DBConnection;
import org.junit.*;

public class DbConnectionTest {
	
	@Test
	public void testSingeltonPattern() {
		System.out.println("Start singleton tests : ");
		DBConnection db1 = DBConnection.getInstance();
		DBConnection db2 = DBConnection.getInstance();
		
		assertSame("DBConnection must be singleton" , db1 , db2);
		assertNotNull("DbConnection must be not null" , db1);
	}

    @Test
    public void testGetConnection(){
        try{
            DBConnection db1 = DBConnection.getInstance();
            Connection conn = db1.getConnection();
            assertNotNull("connection mustnt be null ", conn);
            assertFalse("connection must be open", conn.isClosed());

            System.out.println("✅ get connection done");
        }catch(SQLException e){
            fail("failed to get connection" + e.getMessage());
        }
    }

}
