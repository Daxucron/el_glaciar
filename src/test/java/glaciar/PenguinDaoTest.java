package glaciar;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.sql.SQLException;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import model.Category;
import models.conn.ConnectionExamenesTest;

public class PenguinDaoTest {
	
	private static PenguinDao pd;
	
	@Before
	public void setUpBeforeClass() throws SQLException {
		pd = new PenguinDao(new ConnectionExamenesTest());
	}
	
	@Test
	public void testCreate() throws SQLException
	{		
		Category catA = new Category(1,"Java");
		Category catB = new Category(1,"Photoshop");
		
		int num = pd.create(catA);		
		num+=pd.create(catB);
		
		pd.delete(catA);
		pd.delete(catB);
		
		assertEquals(2,num);
	}	
	
	@Test
	public void testRead() throws SQLException
	{
		Category catA = new Category(1,"Java");		
		Category catB = new Category(2,"Photoshop");		
		Category[] catsHardcoded = {catA,catB};		
		Category[] cats = pd.findAll(Category.class);
		assertArrayEquals(catsHardcoded,cats);		
	}
	
	@Test
	public void testUpdate()
	{
		Category catA = new Category(1,"Java 2");
	}
	
}