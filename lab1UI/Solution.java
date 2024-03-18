package ui;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;

public class Solution {

    private int node;     
    private static LinkedHashMap<String,Map<String, Float>> mapa=new LinkedHashMap<String,Map<String, Float>>();        
    private static Queue<String> redq=new LinkedList<String>();
    private static Map<String, Integer> heuristika = new LinkedHashMap<>();
    private static boolean consistent=true;
	public static void main(String ... args) throws IOException {
		String alg="";
		String file="";
		String heur="";
		boolean check_opt=false;
		boolean check_heur=false;
		//na pocetku citanje argumenata i onda se s obzirom na procitane argumente nastavlja rad
		for(int i=0;i<args.length;i++) {
			if(args[i].equals("--alg")) {
				//koji algoritam se koristi
				if(args[i+1].equals("astar")) {
					alg="astar";
				} else if(args[i+1].equals("bfs")) {
					alg="bfs";
				} else if(args[i+1].equals("ucs")) {
					alg="ucs";
				} 
			} else if(args[i].equals("--ss")) {
				file=args[i+1];
			}else if(args[i].contains("--h")) {
				heur=args[i+1];
			} else if(args[i].equals("--check-optimistic")) {
				check_opt=true;
			}else if(args[i].equals("--check-consistent")) {
				check_heur=true;
			}
		}
		BufferedReader reader = null;
		BufferedReader readerh = null;
		int brojac=0;
		int bcitanje=0;
		String line=" ";
		boolean ima=false;
		String pocetno = null;
		List<String> konacna = new ArrayList<>();
		 List<String> citanje = new ArrayList<>();
		try {
			reader = new BufferedReader(new FileReader(file));
			while(( (line=reader.readLine())!="")) {
				try {
				if(!line.startsWith("#")) {
					brojac++;
					//pocetno stanje
					if(brojac==1) {
						pocetno=line;
						//konacna stanja
					} else if(brojac==2) {
						String [] polje = line.split(" ");
						for(String p:polje) {
							konacna.add(p);
						}
					} else {
						bcitanje++;
						citanje.add(line);
					}
				}
				
				}catch(Exception e) {
					break;
				}
				} 
			reader.close();
		}catch (IOException e) {
					e.printStackTrace();
				}
		
			//za svako stanje se u mapu stavljaju njegovi susjedi te cijena prijelaza
			for(String l:citanje) {
				String[] polje= l.split(" ");
				int i=0;
				String prvi="";
				for(String p:polje) {
					if(i==0) {
						i++;
						mapa.put(p.substring(0, p.length()-1), new LinkedHashMap<>());
						prvi= p.substring(0, p.length()-1);
						
					} else {
						String[] polje2= p.split(",");
			mapa.get(prvi).put(polje2[0], Float.valueOf(polje2[1]));
					}
				}
			}
			
			if(!heur.equals("")) {
			try {
				readerh = new BufferedReader(new FileReader(heur));
				while(( (line=readerh.readLine())!="")) {
					try {
					if(!line.startsWith("#")) {
						String[] parts = line.split(" ");
						//u mapu se za svako stanje stavlja njegova heuristika
						heuristika.put(parts[0].substring(0, parts[0].length()-1), Integer.valueOf(parts[1]));
					}
					
					}catch(Exception e) {
						break;
					}
					} 
				readerh.close();
			}catch (IOException e) {
						e.printStackTrace();
					}
			}
			
          
			if(alg.equals("bfs")) {
        	List<String> lista =BFS(pocetno,konacna);
			for(int i=0; i<lista.size()-1;i++) {
				System.out.println(lista.get(i));
			}
			} else if(alg.equals("ucs")) {
			List<String> lis =ucs(pocetno,konacna);
			for(int i=0; i<lis.size()-1;i++) {
				System.out.println(lis.get(i));
			}
			
			} else if(alg.equals("astar")) {
			List<String> l =AStar(pocetno,konacna);
			for(int i=0; i<l.size()-1;i++) {
				System.out.println(l.get(i));
			}
			}
			//ako zelimo provjeriti optimalnost provodimo ucs nad svakim stanjem
			if(check_opt) {
			boolean krivo=false;
			for(String s:mapa.keySet()) {
				List<String> ll =ucs(s,konacna);
				if(Float.valueOf(ll.get(ll.size()-1))>heuristika.get(s) || Float.valueOf(ll.get(ll.size()-1))==heuristika.get(s).floatValue()) {
				System.out.println("[CONDITION]: [OK] h("+s+") <= h*: " +heuristika.get(s).floatValue() + " <= "+ Float.valueOf(ll.get(ll.size()-1)));
				}else {
					System.out.println("[CONDITION]: [ERR] h("+s+") <= h*: " +heuristika.get(s).floatValue() + " <= "+ Float.valueOf(ll.get(ll.size()-1)));
					krivo=true;
				}
						
			}
			
			if(krivo==false) {
				System.out.println("[CONCLUSION]: Heuristic is optimistic.");
			}else {
			System.out.println("[CONCLUSION]: Heuristic is not optimistic.");
			}
			check_opt=false;
			}
			
			//provjera konzinstentnosti
			if(check_heur) {
 for(String s: mapa.keySet()) {
	 List<String> l1 = checkConsistency(s);
	 l1.forEach(System.out::println);
 } if(consistent==true) {
	 System.out.println("[CONCLUSION]: Heuristic is consistent.");
 }else {
	 System.out.println("[CONCLUSION]: Heuristic is not consistent.");
 }
}
			check_heur=false;
	}
	
	
	
	//BFS algoritam
	 static List<String> BFS(String pocetno, List<String> konacno)  {  
	       Map<String, Boolean> nodes = new LinkedHashMap<>();  
	       Map<String, String> roditelji = new LinkedHashMap<>();
	       List<String> ispis = new ArrayList<>();
	       Set<String> set = new HashSet<String> ();
	       String a;  
	       nodes.put(pocetno, true); 
	       set.add(pocetno);
	       redq.add(pocetno);    
	       
	        while (redq.size() != 0) {  
	        	
	            pocetno = redq.poll();   
	       //ako smo nasli finalno stanje
	            if(konacno.contains(pocetno)) { 
	            	float cijena=0;
	            	List<String> put = new ArrayList<>();
	            	String cvor = roditelji.get(pocetno);
	            	put.add(pocetno);
	            	    while(cvor!=null) {
	            		   put.add(0, cvor);
	 	                   cijena= cijena + mapa.get(cvor).get(pocetno);
	 	                   cvor = roditelji.get(cvor);
	 	                   pocetno = roditelji.get(pocetno);
	            	     }
	               ispis.add("# BFS");
	               ispis.add("[FOUND_SOLUTION]: yes");
	               ispis.add("[STATES_VISITED]: "+(set.size() -redq.size()));
	 	           ispis.add("[PATH_LENGTH]: " + put.size());
	 	           ispis.add("[PATH_COST]: " + cijena);
	 	          String res = "";
	                for (String s : put) {
	                    res += s + " => ";
	                }
	                if (!put.isEmpty()) {
	                    res = res.substring(0, res.length() - 3);
	                }
	                ispis.add("[PATH]: " + res);
	               ispis.add(Integer.toString(set.size()-redq.size()));
	               return ispis;  
	             }
	            //sortiramo stanja abecedno
	            List<String> l = new ArrayList<>();
	            for (int i = 0; i < mapa.get(pocetno).size(); i++) {
	            	  a = (String) mapa.get(pocetno).keySet().toArray()[i];  
	            	  l.add(a);
	            }
	            Collections.sort(l);
	            
	                    //iteriramo kroz cvorove i gledamo jesu li posjeceni
	                   for(String s:l) {
	                    if (nodes.get(s)==null) {  
	                   //pocetno postaje roditelj od s 
	                	   roditelji.put(s,pocetno);
	                	   //oznacimo cvor kao posjecen
	                       nodes.put(s, true);
	                      set.add(s);
	                       redq.add(s);  
	                    }  
	           }  
	            
	        }
	        ispis.add("[FOUND_SOLUTION]: no");
		    ispis.add(Float.toString(0));
		    return ispis;
			
	    }  

	 
	//UCS algoritam
	public static List<String> ucs(String pocetno, List<String> konacno) {
		List<String> ispis=new ArrayList<>();
	    PriorityQueue<Node> red = new PriorityQueue<>();
	    Map<String, String> roditelji = new LinkedHashMap<>();
	    Map<String, Float> udalj = new LinkedHashMap<>();
	    Set<String> set = new HashSet<String> (); 
	    red.add(new Node(pocetno, 0));
	    set.add(pocetno);
	    udalj.put(pocetno, (float) 0);

	    while (red.size()!=0) {
	        Node cvor = red.poll();
         //ako smo nasli finalno stanje
	        if (konacno.contains(cvor.state)) {
	            List<String> put = new ArrayList<>();
	            float cijena = cvor.cost;
	            while (cvor != null) {
	                put.add(0, cvor.state);
	                if(roditelji.get(cvor.state)!=null) {
	                	cvor = new Node(roditelji.get(cvor.state),0);
	                } else {
	                	cvor=null;
	                }
	            }
	            ispis.add("# UCS");
	            ispis.add("[FOUND_SOLUTION]: yes");
	            ispis.add("[STATES_VISITED]: "+(set.size()-red.size()));
	            ispis.add("[PATH_LENGTH]: " + put.size());
	            String res = "";
                for (String s : put) {
                    res += s + " => ";
                }
                if (!put.isEmpty()) {
                    res = res.substring(0, res.length() - 3);
                }
                ispis.add("[PATH]: " + res);
	            ispis.add("[TOTAL_COST]: " + cijena);
	            ispis.add(Float.toString(cijena));
	            return ispis;
	        }
            //iteriramo po susjedima koji su sortirani prvo po cijeni pa abecedno
	        Map<String, Float> m = new TreeMap<>();
	        String k;
	        float v;
            for (int i = 0; i < mapa.get(cvor.state).size(); i++) {
            	  k = (String) mapa.get(cvor.state).keySet().toArray()[i];  
            	  v=(Float) mapa.get(cvor.state).values().toArray()[i];
            	  m.put(k, v);
            }
 
                for(Map.Entry<String, Float> susj:m.entrySet()) {
                	String stanje=susj.getKey();
                	float cijena=susj.getValue();
                	float novacij = udalj.get(cvor.state) + cijena;
                	//ako nema stanja u listi ili je cijena manja od prethodne
	            if (!udalj.containsKey(stanje) || novacij<udalj.get(stanje)) {
	                udalj.put(stanje, novacij);
	                roditelji.put(stanje, cvor.state);
	                red.add(new Node(stanje, novacij));
	                set.add(stanje);
	            }
	        }
	    }

	    ispis.add("[FOUND_SOLUTION]: no");
	    ispis.add(Float.toString(0));
	    return ispis;
	}
	
	//AStar algoritam
	public static List<String> AStar(String pocetno, List<String> konacno) {
	      PriorityQueue<NodeA> red = new PriorityQueue<>();
	      Map<String, String> roditelji = new LinkedHashMap<>();
	      Map<String, Float> closed = new LinkedHashMap<>();
	      Map<String, Float> udalj = new LinkedHashMap<>();
	      Set<String> set = new HashSet<String> (); 
	      
	    red.add(new NodeA(pocetno, (float)0,heuristika.get(pocetno)));
	    List<String> ispis = new ArrayList<>();
	    udalj.put(pocetno, (float) 0);
        set.add(pocetno);
        
	          while (!red.isEmpty()) {
	            NodeA trenutni = red.poll();
	            closed.put(trenutni.state, (float) trenutni.cost);
	            //ako smo dosli do konacnog stanja
	               if (konacno.contains(trenutni.state)) {
	                 List<String> path = new ArrayList<>();
	                 float cost = trenutni.cost;
	                 
	                     while (trenutni != null) {
	                       path.add(0, trenutni.state);
	                          if(roditelji.get(trenutni.state) != null) {
	                         	 trenutni = new NodeA(roditelji.get(trenutni.state), udalj.get(roditelji.get(trenutni.state)),heuristika.get(roditelji.get(trenutni.state)));
	                            } else {
	                	          trenutni=null;
	                          }
	                    }
	            
                          ispis.add("# A-STAR");
	                      ispis.add("[FOUND_SOLUTION]: yes");
	                      ispis.add("[STATES_VISITED]: "+(set.size()-red.size()));
	                      ispis.add("[PATH_LENGTH]: " + path.size());
	                      String res = "";
	                      for (String s : path) {
	                          res += s + " => ";
	                      }
	                      if (!path.isEmpty()) {
	                          res = res.substring(0, res.length() - 3);
	                      }
	                      ispis.add("[PATH]: " + res);
                          ispis.add("[TOTAL_COST]: " + cost);
                          ispis.add(Float.toString(cost));
	                      return ispis;
	                }
            
	        //iteriranje po susjedima
	        for (Map.Entry<String, Float> susj : mapa.get(trenutni.state).entrySet()) {
	              String state = susj.getKey();
	              int h = heuristika.get(state);
	              float cijena = susj.getValue();
	              float novaCij = udalj.get(trenutni.state)+cijena;
	              float hCij = novaCij+h;
	            //ako nije u closed ili ako je nova cijena manja od one u closed,
	            //  ako je u redu sortiram po cijeni pa ce se opet vratit
	                if (!closed.containsKey(state) || novaCij<closed.get(state)) {
	                    udalj.put(state, novaCij);
	                    closed.put(state, novaCij);
	                    roditelji.put(state, trenutni.state);
	                    red.add(new NodeA(state, novaCij, hCij));
	                    set.add(state);
	                }
	        }
	    }

	    ispis.add("[FOUND_SOLUTION]: no");
		return ispis;
	}
//cvor za astar - u compareTo se prvo usporeduje f(n), pa po cijeni, pa abecedno
	private static class NodeA implements Comparable<NodeA> {
	    String state;
	    float cost;
	    float heuristic;
	    NodeA(String state, Float float1, float hcost) {
	        this.state = state;
	        this.cost = float1;
	        this.heuristic = hcost;
	    }
	    @Override
	    public int compareTo(NodeA that){
	    	int heurComp = Double.compare(this.heuristic, that.heuristic);
	          if (heurComp != 0) {
	             return heurComp;
	          } else {
	        	 int costComp = Double.compare(this.cost, that.cost);
		              if (costComp != 0) {
		                  return costComp;
		              }
	                }
	        return this.state.compareTo(that.state);
	    
	    }
	}

	
//cvor za ucs - compare to radi tako da se prvo provjeri cijena a nakon toga se sortira abecedno
	private static class Node implements Comparable<Node> {
	    String state;
	    float cost;
	       Node(String state, float newCost) {
	          this.state = state;
	          this.cost = newCost;
	       }
	       @Override
	        public int compareTo(Node that) {
	    	  int costComp = Double.compare(this.cost, that.cost);
	          if (costComp != 0) {
	            return costComp;
	          }
	        return this.state.compareTo(that.state);
	       }
	}
///funkcija radi po principu h(stanje1)<=h(stanje2)+c
	public static List<String> checkConsistency(String s) {
		List<String> cons = new ArrayList<>();
		
		for (Entry<String, Float> neighbor : mapa.get(s).entrySet()) {
			if(heuristika.get(s)<=(heuristika.get(neighbor.getKey())+neighbor.getValue())) {
				cons.add("[CONDITION]: [OK] h("+s+") <= h("+neighbor.getKey()+") + c: "+heuristika.get(s).floatValue()+" <= "+heuristika.get(neighbor.getKey()).floatValue()+" + "+neighbor.getValue());
			} else {
				cons.add("[CONDITION]: [ERR] h("+s+") <= h("+neighbor.getKey()+") + c: "+heuristika.get(s).floatValue()+" <= "+heuristika.get(neighbor.getKey()).floatValue()+" + "+neighbor.getValue());
				consistent=false;
			}
		}
		return cons;
	}
	
	
	
	
	
	
}