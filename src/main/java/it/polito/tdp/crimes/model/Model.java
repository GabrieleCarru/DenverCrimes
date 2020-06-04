package it.polito.tdp.crimes.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.crimes.db.EventsDao;

public class Model {
	
	private EventsDao dao;
	private List<Event> events;
	private Graph<String, DefaultWeightedEdge> graph;
	private List<Adiacenza> adiacenze;
	
	public Model() {
		dao = new EventsDao();
		events = dao.listAllEvents();
	}
	
	public List<Event> listAllEvents() {
		return events;
	}
	
	// Metodo che restituisce una lista di String contenente tutte le categorie presenti nel DB
	public List<String> getAllCategory() {
		List<String> categories = new ArrayList<>();
		for(Event e : events) {
			if(!categories.contains(e.getOffense_category_id())) {
				categories.add(e.getOffense_category_id());
			}
		}
		return categories;
	}
	
	// Metodo che restituisce una lista di Integer corrispondenti ai mesi presenti nel DB
	public List<Integer> getMonths() {
		List<Integer> months = new ArrayList<>();
		for(Event e : events) {
			if(!months.contains(e.getReported_date().getMonthValue())) {
				months.add(e.getReported_date().getMonthValue());
			}
		}
		Collections.sort(months);
		return months;
	}
	
	public void creaGrafo(String category, Integer month) {
		List<Event> eventsByCategory = new ArrayList<>(dao.getEventsByCategory(category));
	
		List<String> typesOfEvent = new ArrayList<>();
		graph = new SimpleWeightedGraph<String, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		
		for(Event e : eventsByCategory) {
			if((e.getReported_date().getMonthValue() == month) 
					&& (!typesOfEvent.contains(e.getOffense_type_id()))) {
				typesOfEvent.add(e.getOffense_type_id());
			}
		}
		
		// Aggiungo i vertici del grafo
		Graphs.addAllVertices(graph, typesOfEvent);
		
		// Aggiungo gli archi al grafo
		adiacenze = new ArrayList<Adiacenza>();
		for(String s1 : typesOfEvent) {
			for(String s2 : typesOfEvent) {
				if(s1.compareTo(s2) != 0) {
					if(!graph.containsEdge(s1, s2)) {
						double peso = dao.getPesoFromTwoVertex(category, s1, s2, month);
						Graphs.addEdge(this.graph, s1, s2, peso);
						Adiacenza a = new Adiacenza(s1, s2, peso);
						adiacenze.add(a);
					}
				}
			}
		}
	}
	
	public List<Adiacenza> getAdiacenzeBiggerThanAVG() {
		
		List<Adiacenza> result = new ArrayList<Adiacenza>();
		
		double sumPeso = 0.0;
		for(Adiacenza a : adiacenze) {
			sumPeso =+ a.getPeso();
		}
		
		double avgPeso = sumPeso/adiacenze.size();
		
		for(Adiacenza a : adiacenze) {
			if(a.getPeso() > avgPeso) {
				result.add(a);
			}
		}
		
		return result;
		
	}
	
	public int numberVertex() {
		return this.graph.vertexSet().size();
	}
	
	public int numberEdge() {
		return this.graph.edgeSet().size();
	}
	
	
	
	
}
