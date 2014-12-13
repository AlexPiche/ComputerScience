import java.util.*;
import java.io.*;

// This class implements a google-like search engine
public class searchEngine {

    public HashMap<String, LinkedList<String> > wordIndex;                  // this will contain a set of pairs (String, LinkedList of Strings)	
    public directedGraph internet;             // this is our internet graph
    
    
    
    // Constructor initializes everything to empty data structures
    // It also sets the location of the internet files

    searchEngine() {
	// Below is the directory that contains all the internet files
	htmlParsing.internetFilesLocation = "internetFiles";
	wordIndex = new HashMap<String, LinkedList<String> > ();		
	internet = new directedGraph();				
    } // end of constructor2014
    
    
    // Returns a String description of a searchEngine

    public String toString () {
	return "wordIndex:\n" + wordIndex + "\ninternet:\n" + internet;
    }
    
    
    // This does a graph traversal of the internet, starting at the given url.
    // For each new vertex seen, it updates the wordIndex, the internet graph,
    // and the set of visited vertices.
    
    

    void traverseInternet(String url) throws Exception {
        //We will traverse the tree using a depth-first search algorithm
        //When we will visit a vertex for the first time we will first construct the graph
        //which consist of creating a vertex and an edge to the previous vertex, 
        //mark it as visited, parse its content and call the recursive method traverseInternet
        Stack<String> myStack = new Stack<String>();
        myStack.push(url);
        internet.setVisited(url,true);

            String current = (String) myStack.peek();
            LinkedList<String> links = htmlParsing.getLinks(current);
            Iterator<String> i = links.iterator();
        
            while(!myStack.isEmpty()){
                
                while(i.hasNext()){

                    String next = (String) i.next(); 
                
                    if(!internet.getVisited(next)){
                        internet.setVisited(next, true);
                        internet.addVertex(next);
                        internet.addEdge(current, next);
                        wordIndex.put(next, htmlParsing.getContent(next));
                        myStack.push(next);
                        traverseInternet(next);
                    }else{
                        internet.addEdge(current, next);
                    }
            }
        
        
        
            myStack.pop();
        
        

    	    }

	
    } 
    
    
    void computePageRanks() {
        // We first create a linked list of all the vertices in our graph
        // and assign them the page rank 1. We reinizialize the index of the
        // iterator and start iterating to solve the linear system of page rank.
        
        LinkedList<String> vertices = internet.getVertices();
        Iterator<String> i = vertices.iterator();
        double d = 0.5;


        while(i.hasNext()){
        
            String myCurrent = (String) i.next();
            internet.setPageRank(myCurrent,1);
        }
    
        i = vertices.iterator();


        //We will visit all the vertice i in the graph every iterations and
        //add the pagerank divided by the edges out of all its neighbours j 
        //after we did that for all its neighour we correct for the damping factor d
        //reinitialize the index i and reiterate with our new values

        for(int k=1; k<100; k++){

            while(i.hasNext()){
                    
                String current = (String) i.next(); 
                LinkedList<String> neighbours = internet.getEdgesInto(current);
                Iterator<String> j = neighbours.iterator();
                double temp = 0;

                while(j.hasNext()){
                
                    String next = (String) j.next(); 
                    double PRj = internet.getPageRank(next);
                    double CWj = internet.getOutDegree(next);
                    temp = (temp + (PRj / CWj));

                }

                temp = d + (1-d)*temp;
                System.out.println(current);
                System.out.println(temp);
                internet.setPageRank(current,temp);
            
            }
        i = vertices.iterator();
        }
	
    } 
    
	
    String getBestURL(String query) {
        //Simply assign the current highest page rank to the first url in our
        //linked list, reinitialize the index and try to find a page containing 
        //the query which has an higher ranking than the temporary highest rank page
        
        LinkedList<String> myList = internet.getVertices();
        Iterator<String> i = myList.iterator();

        String tmpHighest = i.next();
        i = myList.iterator();

        while(i.hasNext()){
        
            String current = (String) i.next();
            LinkedList<String> words = wordIndex.get(current); 
       
            if(internet.getPageRank(current)>internet.getPageRank(tmpHighest) 
                    && words.contains(query) ){

                tmpHighest = current;
            
            }
        
        }
        i = myList.iterator();
            LinkedList<String> words = wordIndex.get(tmpHighest); 

            // Verify that the page we found is not just the first one 
	    // and that no page contains the query
        if(words.contains(query)){
	
            return tmpHighest + " " + internet.getPageRank(tmpHighest); 
        
        }else{
        
            return "oops I could not find your query";
        }
    } // end of getBestURL
    
    
	
    public static void main(String args[]) throws Exception{		
	searchEngine mySearchEngine = new searchEngine();
	// to debug your program, start with.
	//mySearchEngine.traverseInternet("http://www.cs.mcgill.ca/~blanchem/250/a.html");
	
	// When your program is working on the small example, move on to
	mySearchEngine.traverseInternet("http://www.cs.mcgill.ca");
	
	// this is just for debugging purposes. REMOVE THIS BEFORE SUBMITTING
	System.out.println(mySearchEngine);
	
	mySearchEngine.computePageRanks();
	
	BufferedReader stndin = new BufferedReader(new InputStreamReader(System.in));
	String query;
	do {
	    System.out.print("Enter query: ");
	    query = stndin.readLine();
	    if ( query != null && query.length() > 0 ) {
		System.out.println("Best site = " + mySearchEngine.getBestURL(query));
	    }
	} while (query!=null && query.length()>0);				
    } // end of main
}
