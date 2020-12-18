import java.util.*;

/* AUTOR DE CÓDIGO DIJKTRAS EN JAVA: Jorge E. Peñaloza A.*/
/* URL: http://jorgep.blogspot.com/2010/10/ruta-mas-corta-solucion-por-el.html */

class Nodo implements Comparable<Nodo> {
	char id;
	int distancia = Integer.MAX_VALUE;
	Nodo procedencia = null;

	Nodo(char x, int d, Nodo p) {
		id = x;
		distancia = d;
		procedencia = p;
	}

	Nodo(char x) {
		this(x, 0, null);
	}

	public int compareTo(Nodo tmp) {
		return this.distancia - tmp.distancia;
	}

	public boolean equals(Object o) {
		Nodo tmp = (Nodo) o;
		if (tmp.id == this.id)
			return true;
		return false;
	}
}

class AlgoritmoDijkstra {
	char[] nodos; // Letras de identificación de nodo
	int[][] grafo; // Matriz de distancias entre nodos
	String rutaMasCorta; // distancia más corta
	int longitudMasCorta = Integer.MAX_VALUE; // ruta más corta
	List<Nodo> listos = null; // nodos revisados Dijkstra

	// construye el grafo con la serie de identificadores de nodo en una cadena
	AlgoritmoDijkstra(String serieNodos) {
		nodos = serieNodos.toCharArray();
		grafo = new int[nodos.length][nodos.length];
	}

	// asigna el tamaño de la arista entre dos nodos
	public void agregarRuta(char origen, char destino, int distancia) {
		int n1 = posicionNodo(origen);
		int n2 = posicionNodo(destino);
		grafo[n1][n2] = distancia;
		grafo[n2][n1] = distancia;
	}

	// retorna la posición en el arreglo de un nodo específico
	private int posicionNodo(char nodo) {
		for (int i = 0; i < nodos.length; i++) {
			if (nodos[i] == nodo)
				return i;
		}
		return -1;
	}

	// encuentra la ruta más corta desde un nodo origen a un nodo destino
	public String encontrarRutaMinimaDijkstra(char inicio, char fin) {
		// calcula la ruta más corta del inicio a los demás
		encontrarRutaMinimaDijkstra(inicio);
		// recupera el nodo final de la lista de terminados
		Nodo tmp = new Nodo(fin);
		if (!listos.contains(tmp)) {
			System.out.println("Error, nodo no alcanzable");
			return "Bye";
		}
		tmp = listos.get(listos.indexOf(tmp));
		int distancia = tmp.distancia;
		// crea una pila para almacenar la ruta desde el nodo final al origen
		Stack<Nodo> pila = new Stack<Nodo>();
		while (tmp != null) {
			pila.add(tmp);
			tmp = tmp.procedencia;
		}
		String ruta = "";
		// recorre la pila para armar la ruta en el orden correcto
		while (!pila.isEmpty())
			ruta += (pila.pop().id + " ");
		return distancia + ": " + ruta;
	}

	// encuentra la ruta más corta desde el nodo inicial a todos los demás
	public void encontrarRutaMinimaDijkstra(char inicio) {
		Queue<Nodo> cola = new PriorityQueue<Nodo>(); // cola de prioridad
		Nodo ni = new Nodo(inicio); // nodo inicial

		listos = new LinkedList<Nodo>();// lista de nodos ya revisados
		cola.add(ni); // Agregar nodo inicial a la cola de prioridad
		while (!cola.isEmpty()) { // mientras que la cola no esta vacia
			Nodo tmp = cola.poll(); // saca el primer elemento
			listos.add(tmp); // lo manda a la lista de terminados
			int p = posicionNodo(tmp.id);
			for (int j = 0; j < grafo[p].length; j++) { // revisa los nodos hijos del nodo tmp
				if (grafo[p][j] == 0)
					continue; // si no hay conexión no lo evalua
				if (estaTerminado(j))
					continue; // si ya fue agregado a la lista de terminados
				Nodo nod = new Nodo(nodos[j], tmp.distancia + grafo[p][j], tmp);
				// si no está en la cola de prioridad, lo agrega
				if (!cola.contains(nod)) {
					cola.add(nod);
					continue;
				}
				// si ya está en la cola de prioridad actualiza la distancia menor
				for (Nodo x : cola) {
					// si la distancia en la cola es mayor que la distancia calculada
					if (x.id == nod.id && x.distancia > nod.distancia) {
						cola.remove(x); // remueve el nodo de la cola
						cola.add(nod); // agrega el nodo con la nueva distancia
						break; // no sigue revisando
					}
				}
			}
		}
	}

	// verifica si un nodo ya está en lista de terminados
	public boolean estaTerminado(int j) {
		Nodo tmp = new Nodo(nodos[j]);
		return listos.contains(tmp);
	}

	// encontrar la ruta mínima por fuerza bruta
	public void encontrarRutaMinimaFuerzaBruta(char inicio, char fin) {
		int p1 = posicionNodo(inicio);
		int p2 = posicionNodo(fin);
		// cola para almacenar cada ruta que está siendo evaluada
		Stack<Integer> resultado = new Stack<Integer>();
		resultado.push(p1);
		recorrerRutas(p1, p2, resultado);
	}

	// recorre recursivamente las rutas entre un nodo inicial y un nodo final
	// almacenando en una cola cada nodo visitado
	private void recorrerRutas(int nodoI, int nodoF, Stack<Integer> resultado) {
		// si el nodo inicial es igual al final se evalúa la ruta en revisión
		if (nodoI == nodoF) {
			int respuesta = evaluar(resultado);
			if (respuesta < longitudMasCorta) {
				longitudMasCorta = respuesta;
				rutaMasCorta = "";
				for (int x : resultado)
					rutaMasCorta += (nodos[x] + " ");
			}
			return;
		}
		// Si el nodoInicial no es igual al final se crea una lista con todos los nodos
		// adyacentes al nodo inicial que no estén en la ruta en evaluación
		List<Integer> lista = new Vector<Integer>();
		for (int i = 0; i < grafo.length; i++) {
			if (grafo[nodoI][i] != 0 && !resultado.contains(i))
				lista.add(i);
		}
		// se recorren todas las rutas formadas con los nodos adyacentes al inicial
		for (int nodo : lista) {
			resultado.push(nodo);
			recorrerRutas(nodo, nodoF, resultado);
			resultado.pop();
		}
	}

	// evaluar la longitud de una ruta
	public int evaluar(Stack<Integer> resultado) {
		int resp = 0;
		int[] r = new int[resultado.size()];
		int i = 0;
		for (int x : resultado)
			r[i++] = x;
		for (i = 1; i < r.length; i++)
			resp += grafo[r[i]][r[i - 1]];
		return resp;
	}
}

/* AUTOR DE CODIFO FLOY WARSHALL: Programiz */
/* URL: https://www.programiz.com/dsa/floyd-warshall-algorithm */

class AlgoritmoFloydWarshall {
	public static int[][] shortestpath(int[][] adj, int[][] path) {
		int n = adj.length;
		int[][] ans = new int[n][n];
		copy(ans, adj);
		for (int k = 0; k < n; k++)
			for (int i = 0; i < n; i++)
				for (int j = 0; j < n; j++)
					if (ans[i][k] + ans[k][j] < ans[i][j]) {
						ans[i][j] = ans[i][k] + ans[k][j];
						path[i][j] = path[k][j];
					}
		return ans;
	}

	public static void copy(int[][] a, int[][] b) {
		for (int i = 0; i < a.length; i++)
			for (int j = 0; j < a[0].length; j++)
				a[i][j] = b[i][j];
	}
}

/* AUTOR DEL CÓDIGO BELLMAN FORD */

class AlgoritmoBellmanFord {
	public static long dist[];
	public static long prev[];
	public static LinkedList<DirectedEdge> edgesList = new LinkedList<DirectedEdge>();

	public static class DirectedEdge {
		protected int v;
		protected int w; // edge
		protected int weight; // weight;

		public DirectedEdge(int v, int w, int weight) {
			this.v = v;
			this.w = w;
			this.weight = weight;
		}

		public int from() {
			return v;
		}

		public int to() {
			return w;
		}

		public int weight() {
			return weight;
		}

		public String toString() {
			return v + "->" + w + " " + String.format("%d", weight);
		}

	}

	public static class WeightableDiGraph {
		protected int V; // vertex;
		protected int E; // edges;
		protected ArrayList<DirectedEdge>[] adj;

		public WeightableDiGraph(int V) {
			this.V = V;
			this.E = 0;
			adj = (ArrayList<DirectedEdge>[]) new ArrayList[V];
			for (int v = 0; v < V; v++) {
				adj[v] = new ArrayList<DirectedEdge>();
			}

		}

		public int V() {
			return this.V;
		}

		public int E() {
			return this.E;
		}

		public void addEdge(int v, int w, int weight) {
			this.E++;
			DirectedEdge item = new DirectedEdge(v, w, weight);
			adj[v].add(item);
			edgesList.add(item);
		}

		public String toString() {
			StringBuilder s = new StringBuilder();
			String NEWLINE = System.getProperty("line.separator");
			s.append(V + " vertices, " + E + " edges " + NEWLINE);
			for (int v = 0; v < V; v++) {
				s.append(String.format("%d: ", v));
				for (DirectedEdge e : adj[v]) {
					s.append(e + "  ");
				}
				s.append(NEWLINE);
			}
			return s.toString();
		}

		public Iterable<DirectedEdge> adj(int v) {
			if (v < 0 || v >= V)
				throw new IndexOutOfBoundsException();
			return this.adj[v];
		}

	}

	public static int BellmanFord(WeightableDiGraph g) {
		Arrays.fill(dist, Long.MAX_VALUE);
		Arrays.fill(prev, -1);
		dist[0] = 0;
		for (int i = 1; i < g.V() - 1; i++) {
			for (DirectedEdge e : g.adj(i)) {

				if (dist[e.to()] > dist[e.from()] + e.weight()) {
					dist[e.to()] = dist[e.from()] + e.weight();
					prev[e.to()] = e.to();
				}
			}
		}

		for (int i = 0; i < g.V(); i++) {
			for (DirectedEdge e : g.adj(i)) {
				if (dist[e.to()] > dist[e.from()] + e.weight())
					return 1;
			}
		}
		return 0;
	}
}

public class PruebaAlgoritmos {
	public static void main(String[] args) {
		AlgoritmoDijkstra dijkstra = new AlgoritmoDijkstra("abcdef");
		AlgoritmoFloydWarshall floyWarshall = new AlgoritmoFloydWarshall();
		AlgoritmoBellmanFord bellmanFord = new AlgoritmoBellmanFord();
		long dist[];
		long prev[];

		// Menu
		Scanner entrada1 = new Scanner(System.in);
		Scanner stdin = new Scanner(System.in);
		int e = 0;
		boolean salir = false;
		e = 0;

		do {
			System.out.println("\n\t\t-----------------------ALGORITMOS DE CAMINO MÁS CORTO-----------------------\n");
			System.out.println();

			System.out.println("\n1) Algoritmo Dijkstra");
			System.out.println("2) Algoritmo Floyd Warshall");
			System.out.println("3) AlgoritmoBellman Ford");
			System.out.println("4) Salir");
			System.out.println();
			System.out.print("Ingrese el número de la opción a elegir: ");
			System.out.println();
			int opc = entrada1.nextInt();

			switch (opc) {
			case 1:
				System.out.println("\t---------------Algoritmo Dijkstra---------------\n");
				dijkstra.agregarRuta('a', 'b', 3);
				dijkstra.agregarRuta('a', 'e', 6);
				dijkstra.agregarRuta('a', 'f', 10);
				dijkstra.agregarRuta('b', 'c', 5);
				dijkstra.agregarRuta('b', 'e', 2);
				dijkstra.agregarRuta('c', 'd', 8);
				dijkstra.agregarRuta('c', 'e', 9);
				dijkstra.agregarRuta('c', 'f', 7);
				dijkstra.agregarRuta('d', 'f', 4);
				dijkstra.agregarRuta('e', 'f', 4);
				char inicio = 'a';
				char fin = 'd';
				String respuesta = dijkstra.encontrarRutaMinimaDijkstra(inicio, fin);
				System.out.println(respuesta);
				break;

			case 2:
				System.out.println("\t---------------Algoritmo Floyd Warshall---------------\n");
				int[][] m = new int[5][5];
				m[0][0] = 0;
				m[0][1] = 4;
				m[0][2] = 8;
				m[0][3] = 10000;
				m[0][4] = 10000;
				m[1][0] = 4;
				m[1][1] = 0;
				m[1][2] = 1;
				m[1][3] = 2;
				m[1][4] = 10000;
				m[2][0] = 8;
				m[2][1] = 1;
				m[2][2] = 0;
				m[2][3] = 4;
				m[2][4] = 2;
				m[3][0] = 10000;
				m[3][1] = 2;
				m[3][2] = 4;
				m[3][3] = 0;
				m[3][4] = 7;
				m[4][0] = 10000;
				m[4][1] = 10000;
				m[4][2] = 2;
				m[4][3] = 7;
				m[4][4] = 0;
				int[][] shortpath;
				int[][] path = new int[5][5];

				for (int i = 0; i < 5; i++)
					for (int j = 0; j < 5; j++)
						if (m[i][j] == 10000)
							path[i][j] = -1;
						else
							path[i][j] = i;
				for (int i = 0; i < 5; i++)
					path[i][i] = i;

				shortpath = AlgoritmoFloydWarshall.shortestpath(m, path);
				// Prints out shortest distances.
				System.out.println("  0 1 2 3 4");
				System.out.println("  ---------");
				for (int i = 0; i < 5; i++) {
					System.out.print(i + "|");
					for (int j = 0; j < 5; j++)
						System.out.print(shortpath[i][j] + " ");
					System.out.println();
				}
				System.out.println("Ruta más corta de un vértice a otro (0 a 4)");
				System.out.print("Vértice inicial: ");
				int start = stdin.nextInt();
				System.out.print("Vértice final: ");
				int end = stdin.nextInt();
				String myPath = end + "";
				System.out.println();
				System.out.println("  0 1 2 3 4");
				System.out.println("  ---------");
				for (int i = 0; i < 5; i++) {
					System.out.print(i + "|");
					for (int j = 0; j < 5; j++)
						System.out.print(path[i][j] + " ");
					System.out.println();
				}
				while (path[start][end] != start) {
					myPath = path[start][end] + " -> " + myPath;
					end = path[start][end];
				}
				myPath = start + " -> " + myPath;
				System.out.println("Esta es la ruta: " + myPath);
				break;

			case 3:
				System.out.println("\t---------------AlgoritmoBellmanFord---------------\n");
				int n, mn, v, w, weight;
				Scanner sc = new Scanner(System.in);
				n = sc.nextInt();
				mn = sc.nextInt();
				dist = new long[n];
				prev = new long[n];
				AlgoritmoBellmanFord.WeightableDiGraph g = new AlgoritmoBellmanFord.WeightableDiGraph(n);
				for (int i = 0; i < mn; i++) {
					v = sc.nextInt();
					w = sc.nextInt();
					weight = sc.nextInt();
					g.addEdge(v - 1, w - 1, weight);
				}

				System.out.println(AlgoritmoBellmanFord.BellmanFord(g));
				break;

			case 4:
				salir = true;
				break;

			default:
				break;
			}
		} while (!salir);
		System.out.println("------------------------------------------------------------");

	}
}
