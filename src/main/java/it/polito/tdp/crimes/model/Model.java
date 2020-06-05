package it.polito.tdp.crimes.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
		return dao.getCategory();
	}
	
	// Metodo che restituisce una lista di Integer corrispondenti ai mesi presenti nel DB
	public List<Integer> getMonths() {
		List<Integer> months = dao.getMonth();
		Collections.sort(months);
		return months;
	}
	
	public void creaGrafo(String category, Integer month) {
		graph = new SimpleWeightedGraph<String, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		adiacenze = new ArrayList<>();
		
		// Aggiungo i vertici 
		List<String> vertexList = dao.getVertexList(category, month);
		Graphs.addAllVertices(graph, vertexList);
		
		//Aggiungo gli archi
		for(String s1 : vertexList) {
			for(String s2 : vertexList) {
				if((!graph.containsEdge(s1, s2)) && (s1.compareTo(s2) != 0)) {
					double peso = dao.getPesoFromTwoVertex(category, s1, s2, month);
					if(peso != 0.0) {
						adiacenze.add(new Adiacenza(s1, s2, peso));
						Graphs.addEdge(graph, s1, s2, peso);
					}
				}
			}
		}
	}
	
	public List<Adiacenza> getAdiacenzeBiggerThanAVG() {
		
		List<Adiacenza> adiacenzeSuperioriMedia = new ArrayList<Adiacenza>();
		
		double avg = this.getAVG();
		for(Adiacenza a : adiacenze) {
			if(a.getPeso() > avg) {
				adiacenzeSuperioriMedia.add(a);
			}
		}
		
		return adiacenzeSuperioriMedia;
		
	}
	
	private double getAVG() {
		
		double avgAdiacenze = 0.0;
		double sumAdiacenze = 0.0;
		for(Adiacenza a : adiacenze) {
			sumAdiacenze += a.getPeso();
		}
		
		avgAdiacenze = sumAdiacenze/adiacenze.size();
		
		return avgAdiacenze;
	}

	public int numberVertex() {
		return this.graph.vertexSet().size();
	}
	
	public int numberEdge() {
		return this.graph.edgeSet().size();
	}
	
	
	
	
}
