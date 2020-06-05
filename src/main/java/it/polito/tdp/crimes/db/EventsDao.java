package it.polito.tdp.crimes.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polito.tdp.crimes.model.Adiacenza;
import it.polito.tdp.crimes.model.Event;


public class EventsDao {
	
	public List<Event> listAllEvents(){
		String sql = "SELECT * FROM events" ;
		try {
			Connection conn = DBConnect.getConnection() ;

			PreparedStatement st = conn.prepareStatement(sql) ;
			
			List<Event> list = new ArrayList<>() ;
			
			ResultSet res = st.executeQuery() ;
			
			while(res.next()) {
				try {
					list.add(new Event(res.getLong("incident_id"),
							res.getInt("offense_code"),
							res.getInt("offense_code_extension"), 
							res.getString("offense_type_id"), 
							res.getString("offense_category_id"),
							res.getTimestamp("reported_date").toLocalDateTime(),
							res.getString("incident_address"),
							res.getDouble("geo_lon"),
							res.getDouble("geo_lat"),
							res.getInt("district_id"),
							res.getInt("precinct_id"), 
							res.getString("neighborhood_id"),
							res.getInt("is_crime"),
							res.getInt("is_traffic")));
				} catch (Throwable t) {
					t.printStackTrace();
					System.out.println(res.getInt("id"));
				}
			}
			
			conn.close();
			return list ;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null ;
		}
	}
	
	public List<String> getCategory() {
		String sql = "select distinct(offense_category_id) as category " + 
				"from events ";
		
		List<String> categories = new ArrayList<String>();
		
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();
			
			while(rs.next()) {
				String category = rs.getString("category");
				categories.add(category);
			}
			
			conn.close();
			return categories;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	public List<Integer> getMonth() {
		
		String sql = "select distinct(Month(reported_date)) as months " + 
				"from events ";
		
		List<Integer> months = new ArrayList<Integer>();
		
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();
			
			while(rs.next()) {
				Integer month = rs.getInt("months");
				months.add(month);
			}
			
			conn.close();
			return months;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		
	}

	public List<Adiacenza> getPesoFromTwoVertex(String category, Integer month) {
		String sql = "select e1.offense_type_id as s1, e2.offense_type_id as s2, " +
				"count(distinct(e1.neighborhood_id)) as numQuartieri " + 
				"from events as e1, events as e2 " + 
				"where e1.offense_category_id = ? " + 
				"and e2.offense_category_id = ? " + 
				"and Month(e1.reported_date) = ? " + 
				"and Month(e2.reported_date) = ? " + 
				"and e1.offense_type_id <> e2.offense_type_id " + 
				"and e1.neighborhood_id = e2.neighborhood_id " +
				"group by e1.offense_type_id, e2.offense_type_id";
		
		List<Adiacenza> result = new ArrayList<>();
		
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, category);
			st.setString(2, category);
			st.setInt(3, month);
			st.setInt(4, month);
			ResultSet rs = st.executeQuery();
			
			while(rs.next()) {
				Adiacenza a = new Adiacenza(rs.getString("s1"), rs.getString("s2"), 
													rs.getDouble("numQuartieri"));
				result.add(a);
			}
			
			conn.close();
			return result;
			
		} catch(SQLException e) {
			e.printStackTrace();
			return null;
		}
	}	
}
