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
	private List<String> best;
	
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
		adiacenze = dao.getPesoFromTwoVertex(category, month);
			
		// Aggiungo i vertici 
		for(Adiacenza a : adiacenze) {
			if(!this.graph.containsVertex(a.getS1())) {
				this.graph.addVertex(a.getS1());
			}
			if(!this.graph.containsVertex(a.getS2())) {
				this.graph.addVertex(a.getS2());
			}
			
			if(this.graph.getEdge(a.getS1(), a.getS2()) == null) {
				Graphs.addEdgeWithVertices(this.graph, a.getS1(), a.getS2(), a.getPeso());
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
	
	public List<String> trovaPercorso(String partenza, String arrivo) {
		List<String> parziale = new ArrayList<>();
		this.best = new ArrayList<>();
		parziale.add(partenza);
		trovaRiscorsivo(arrivo, parziale, 0);
		return this.best;
	}

	private void trovaRiscorsivo(String arrivo, List<String> parziale, int i) {
		
		// Condizione di terminazione: -> l'ultimo vertice inserito in paziale è uguale ad 'arrivo'
		if(parziale.get(parziale.size()-1).equals(arrivo)) {
			if(parziale.size() > this.best.size()) {
				this.best = new ArrayList<>(parziale);
			}
			return;
		}
		
		// Scorro i vicini dell'ultimo vertice inserito in parziale
		for(String vicino : Graphs.neighborListOf(this.graph, parziale.get(parziale.size()-1))) {
			// cerchiamo cammini aciclici -> controllo che il vertice non sia già in parziale
			if(!parziale.contains(vicino)) {
				// provo ad aggiungere
				parziale.add(vicino);
				// continuo la ricorsione
				this.trovaRiscorsivo(arrivo, parziale, i+1);
				// faccio backtracking
				parziale.remove(parziale.size()-1);
			}
		}
		
		
	}
	
	
}
