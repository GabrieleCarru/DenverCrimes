package it.polito.tdp.crimes.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	
	public List<Event> getEventsByCategory(String category) {
		String sql = "select * " + 
				"from event " + 
				"where offense_category_id = ?";
		
		try {
			
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, category);
			ResultSet res = st.executeQuery();
			
			List<Event> result = new ArrayList<>();
			
			while(res.next()) {
				try {
						result.add(new Event(res.getLong("incident_id"),
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
			return result;
			
			
		} catch(SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public int getPesoFromTwoVertex(String category, String s1, String s2, Integer month) {
		String sql = "select count(neighborhood_id) as numQuartieri " + 
				"from events as e1, events as e2 " + 
				"where e1.offense_category_id = ? " + 
				"and e2.offense_category_id = ? " + 
				"and Month(e1.reported_date) = ? " + 
				"and Month(e2.reported_date) = ? " + 
				"and e1.offense_type_id = ? " + 
				"and e2.offense_type_id = ? " + 
				"and e1.neighborhood_id = e2.neighborhood_id";
		
		int result = 0;
		
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, category);
			st.setString(2, category);
			st.setInt(3, month);
			st.setInt(4, month);
			st.setString(5, s1);
			st.setString(6, s2);
			ResultSet rs = st.executeQuery();
			
			while(rs.next()) {
				result = rs.getInt("numQuartieri");
			}
			
			conn.close();
			return result;
			
		} catch(SQLException e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	

	
}
