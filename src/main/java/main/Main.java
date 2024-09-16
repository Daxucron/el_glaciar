package main;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Blob;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDateTime;

import glaciar.PenguinDao;
import model.*;
import model.conn.ConnectionExamenes;

public class Main {

	public static void main(String[] args) 
	{
		//testCrud();
		try {
			testRead();
		} catch (SQLException e) {
			System.out.println("Error de glaciar");
			e.printStackTrace();
		}
	}
	
	private static void testRead() throws SQLException
	{			
		PenguinDao pd = new PenguinDao(new ConnectionExamenes());
		Exam[] exams = pd.findAll(Exam.class);
	}
	
	private static void testCrud()
	{
		Main m = new Main();
		byte[] imageBlob = null;
        try {
			imageBlob = m.readImageAsBytes("pinguinos_del_glaciar.jpg");
		} catch (IOException e) {
			e.printStackTrace();
		}
  
		
		try {
			
			User user = new User(4, "STUCOM", LocalDateTime.now(), "stucom2@gmail.com", "123456", null);
			User userToDelete = new User(9, "Carlos", LocalDateTime.now(), "correo1", "12345", imageBlob);
			Category cat = new Category(2,"Random");
			Exam exam = new Exam(7, cat, 2, LocalDateTime.now(), "BÁSICO");
			Question quest = new Question(12, "Como me llamo?", 1, 0.3f, 7);
			Option option = new Option(0, "Carlos", true, quest.getQuestionId());
			Option option2 = new Option(1, "Juan", false, quest.getQuestionId());
			quest.setOptions(new Option[]{option, option2});
			exam.addQuestion(quest);
			
			
			PenguinDao pd = new PenguinDao(new ConnectionExamenes());
			pd.create(cat);
			pd.create(user);
			exam.setCategoryId(cat.getCategoryId());
			exam.setCreatorId(user.getUserId());
			pd.create(exam);
			
			Category[] cats = pd.findAll(Category.class);
			
			//pd.delete(userToDelete);
			user.setName("Paco");
			pd.update(user);
			for(Category c:cats)
			{
				System.out.println(c);
			}
		
			
			
		} catch (SQLException e) {
			System.out.println("Error de glaciar");
			e.printStackTrace();
		}		
	}
	
	private byte[] readImageAsBytes(String relativePath) throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        try (InputStream inputStream = classLoader.getResourceAsStream(relativePath)) {
            if (inputStream == null) {
                throw new IOException("File not found: " + relativePath);
            }
            return inputStream.readAllBytes();
        }
    }

}
