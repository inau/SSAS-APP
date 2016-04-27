package dk.itu.ssas.project;

import java.sql.Connection;
import java.sql.SQLException;
import java.io.InputStream;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import org.apache.commons.codec.digest.DigestUtils;
import java.sql.*;
import java.util.Date;

public class DB {

	public static DBWrapper getDBWrapper() throws SQLException {
		return new DB.DBWrapper();
	}
 
	public static class DBWrapper {
		protected Connection conn;
		
		public DBWrapper() throws SQLException {
			conn = DB.getConnection();
		}
		
		//USERS
		public ResultSet FindUserByName(String name) throws SQLException {
			String sql = "SELECT * FROM ssas_users WHERE username = ?;";
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, name);		
			return st.executeQuery();
		}
		
		public ResultSet login(String username, String pass) throws SQLException {
			ResultSet rs = FindUserByName(username);
			if( !rs.next() ) return rs;
			String salt = rs.getString(3);
			String oldHash = rs.getString(2);
			System.out.println("SALT: "+salt + "\nDBh: "+oldHash );
			
			String hpass = DigestUtils.md5Hex(pass);
			String freshHash = DigestUtils.md5Hex( salt+hpass );
			System.out.println("New: "+freshHash );

			if( oldHash.equals(freshHash) ) {
				System.out.println("match...");
				rs = FindUserByName(username);
				return rs;
			}
			else throw new SQLException();
		}
		
		public void createNewUser(String username, String password) throws SQLException {
				System.out.println("creating new user");
				Date d = new Date();
				String salt = DigestUtils.md5Hex( ""+d.getTime() );
				String hpass = DigestUtils.md5Hex(password);
				String dbpass = DigestUtils.md5Hex( salt+hpass );
				
				String sql = "INSERT INTO ssas_users (username, password, salt)" +
							" values (?,?,?) ON DUPLICATE KEY UPDATE password=?, salt=?;";
				PreparedStatement st = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
				st.setString(1, username);
				st.setString(2, dbpass);
				st.setString(3, salt);
				st.setString(4, dbpass);
				st.setString(5, salt);
				st.executeUpdate();
		}
		
		//COMMENTS
		public void CreateCommentForImageByUser(String image_id, String user, String comment) throws SQLException {	  
			String sql = "INSERT INTO ssas_comments (image_id, user_id, comment) VALUES (?, ?, ?);";
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, image_id);
			st.setString(2, user);
			st.setString(3, comment);
			st.executeUpdate();
		}
		
		public ResultSet getCommentsForImage(String image_id) throws SQLException {
			String sql = "SELECT comments.comment, users.username " + 
            "FROM ssas_comments comments INNER JOIN ssas_users users " + 
            "WHERE users.username = comments.user_id " +
            "AND comments.image_id = ?;";
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, image_id);
		    return st.executeQuery();	
		}
		
		//IMAGES
		public ResultSet createImageForUser(String user, InputStream iis) throws SQLException {
			String sql = "INSERT INTO ssas_images (jpeg, owner) values (?, ?) ;";
			PreparedStatement statement = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
			statement.setBlob(1, iis);
			statement.setString(2, user);
			statement.executeUpdate();

			return statement.getGeneratedKeys();
		}
		
		public ResultSet getImageIdsForUser(String user) throws SQLException {
			String sql = "SELECT DISTINCT image_id FROM ssas_perms perms WHERE perms.user_id = ? ;";
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, user);
		    return st.executeQuery();
		}
		
		public ResultSet getImageOwner(String image_id) throws SQLException {
			String sql = "SELECT username " +
              "FROM ssas_users users INNER JOIN ssas_images images " + 
              "WHERE users.username = images.owner " +
              "AND   images.id = ? ;";
			  PreparedStatement st = conn.prepareStatement(sql);
			  st.setString(1, image_id);
		    return st.executeQuery();
		}
		
		public ResultSet getImageById(String image_id) throws SQLException {
			String sql = "SELECT jpeg FROM ssas_images WHERE id = ? ;";
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, image_id);
			return st.executeQuery();
		}
		
		//PERMS
		public ResultSet getUsersWithPermissionsForImage(String image_id) throws SQLException {
			String sql = 	"SELECT users.username " +
							"FROM ssas_users users INNER JOIN ssas_perms perms " +
							"WHERE users.username = perms.user_id " +
							"AND perms.image_id = ? ;";
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, image_id);
		    return st.executeQuery();
		}
		
		public void createUserPermsForPicture(String image_id, String user) throws SQLException {
			String sql = "INSERT INTO ssas_perms (image_id, user_id) values (?,?) ;";
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, image_id);
			st.setString(2, user);
			st.executeUpdate(); 
		}
		
		public void createCommentForUser(String image_id, String user, String comment) throws SQLException {
			String sql = "INSERT INTO ssas_comments (image_id, user_id, comment) VALUES (?,?,?)";
			PreparedStatement st = conn.prepareStatement(sql);
			 st.setString(1, image_id);
			 st.setString(2, user);
			 st.setString(3, comment);							 
			 st.executeUpdate(); 		
		}
		
		public void shareImageWith(String image_id, String username) throws SQLException {	
			String sql = 
				"INSERT INTO ssas_perms (image_id, user_id) " + 
				"SELECT ?, users.username FROM ssas_users users WHERE users.username = ? ;";
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, image_id);
			st.setString(2, username);
			st.executeUpdate();
		}
	}
	
		public static Connection getConnection() throws SQLException {
		// Better way to do this is setting database connect info in
		// servlet context; but for the SSAS project, that just adds
		// complications. 
		MysqlDataSource ds = null;
		
			ds = new MysqlDataSource();
			ds.setUrl("jdbc:mysql://mydb.itu.dk:3306/ssas0p?allowMultiQueries=true");
			ds.setUser("53cr37");
			ds.setPassword("5up3r5ecr37");
			Connection conn = ds.getConnection();
			
			createTablesIfNotExist(conn);
				
			return conn;
	}
	
	private static void createTablesIfNotExist(Connection c) throws SQLException {
		Statement st = c.createStatement();
		st.executeUpdate("CREATE TABLE IF NOT EXISTS ssas_users"+
				" (username VARCHAR(45) primary key,"+
				"password VARCHAR(128),"+
				"salt VARCHAR(128), isAdmin integer NOT NULL DEFAULT 0);");
			st.executeUpdate("CREATE TABLE IF NOT EXISTS ssas_images"+
				" (id integer auto_increment primary key , jpeg LONGBLOB,"+
				"owner VARCHAR(128));");
			st.executeUpdate("CREATE TABLE IF NOT EXISTS ssas_perms"+
				" (id integer auto_increment primary key , image_id VARCHAR(45),"+
				"user_id VARCHAR(45));");
			st.executeUpdate("CREATE TABLE IF NOT EXISTS ssas_comments"+
				" (id integer auto_increment primary key , image_id VARCHAR(45),"+
				"user_id VARCHAR(45),"+
				"comment VARCHAR(128));");
	}
	
}
