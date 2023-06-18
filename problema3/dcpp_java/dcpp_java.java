/**
 * Codigo Java para solucionar o Problema do Carteiro Chines em grafos
 * orientados.
 */
// import java.io.*;
import java.util.*;

public class dcpp_java {
	int N; // Numero de vertices
	int delta[]; // fator de desbalanceamento dos vertices
	int neg[], pos[]; // vertices desbalanceados
	int arcs[][]; // matriz de adjacencia, conta os arcos entre os vertices
	Vector label[][]; // vector de nome dos arcos (para cada par de vertices)
	int f[][]; // arcos repetidos no CPT
	float c[][]; // custos do caminho minimo
	String cheapestLabel[][]; // nome dos caminhos minimos
	boolean defined[][]; // Verifica se o custo esta definido entre os vertices
	int path[][]; // Caminho
	float basicCost; // Custo do percurso por arco

	void solve() {
		leastCostPaths();
		checkValid();
		findUnbalanced();
		findFeasible();
		while (improvements())
			;
	}

	// alloca memoria do array, e instancia o objecto grafo
	dcpp_java(int vertices) {
		if ((N = vertices) <= 0)
			throw new Error("Graph is empty");
		delta = new int[N];
		defined = new boolean[N][N];
		label = new Vector[N][N];
		c = new float[N][N];
		f = new int[N][N];
		arcs = new int[N][N];
		cheapestLabel = new String[N][N];
		path = new int[N][N];
		basicCost = 0;
	}

	// Adiciona arco
	dcpp_java addArc(String lab, int u, int v, float cost) {
		if (!defined[u][v])
			label[u][v] = new Vector();
		label[u][v].addElement(lab);
		basicCost += cost;
		if (!defined[u][v] || c[u][v] > cost) {
			c[u][v] = cost;
			cheapestLabel[u][v] = lab;
			defined[u][v] = true;
			path[u][v] = v;
		}
		arcs[u][v]++;
		delta[u]++;
		delta[v]--;
		return this;
	}

	/**
	 * Algoritmo de Floyd-Warshall para encontrar caminho minimo entre todos
	 * vertices
	 * No grafo nao temos ciclos negativos.
	 */

	void leastCostPaths() {
		for (int k = 0; k < N; k++)
			for (int i = 0; i < N; i++)
				if (defined[i][k])
					for (int j = 0; j < N; j++)
						if (defined[k][j]
								&& (!defined[i][j] || c[i][j] > c[i][k] + c[k][j])) {
							path[i][j] = path[i][k];
							c[i][j] = c[i][k] + c[k][j];
							defined[i][j] = true;
							if (i == j && c[i][j] < 0)
								return; // stop on negative cycle
						}
	}

	void checkValid() {
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++)
				if (!defined[i][j])
					throw new Error("Graph is not strongly connected");
			if (c[i][i] < 0)
				throw new Error("Graph has a negative cycle");
		}
	}

	float cost() {
		return basicCost + phi();
	}

	float phi() {
		float phi = 0;
		for (int i = 0; i < N; i++)
			for (int j = 0; j < N; j++)
				phi += c[i][j] * f[i][j];
		return phi;
	}

	void findUnbalanced() {
		int nn = 0, np = 0; // numero de vertices com fator de balenceamento negativo/positivo

		for (int i = 0; i < N; i++)
			if (delta[i] < 0)
				nn++;
			else if (delta[i] > 0)
				np++;

		neg = new int[nn];
		pos = new int[np];
		nn = np = 0;
		for (int i = 0; i < N; i++)
			if (delta[i] < 0)
				neg[nn++] = i;
			else if (delta[i] > 0)
				pos[np++] = i;
	}

	/*
	 * Resolucao do problema de optimizacao no modelo de PPL descrito.
	 */
	void findFeasible() { // delete next 3 lines to be faster, but non-reentrant
		int delta[] = new int[N];
		for (int i = 0; i < N; i++)
			delta[i] = this.delta[i];

		for (int u = 0; u < neg.length; u++) {
			int i = neg[u];
			for (int v = 0; v < pos.length; v++) {
				int j = pos[v];
				f[i][j] = -delta[i] < delta[j] ? -delta[i] : delta[j];
				delta[i] += f[i][j];
				delta[j] -= f[i][j];
			}
		}
	}

	boolean improvements() {
		dcpp_java residual = new dcpp_java(N);
		for (int u = 0; u < neg.length; u++) {
			int i = neg[u];
			for (int v = 0; v < pos.length; v++) {
				int j = pos[v];
				residual.addArc(null, i, j, c[i][j]);
				if (f[i][j] != 0)
					residual.addArc(null, j, i, -c[i][j]);
			}
		}
		residual.leastCostPaths();
		for (int i = 0; i < N; i++)
			if (residual.c[i][i] < 0) {
				int k = 0, u, v;
				boolean kunset = true;
				u = i;
				do // find k to cancel
				{
					v = residual.path[u][i];
					if (residual.c[u][v] < 0 && (kunset || k > f[v][u])) {
						k = f[v][u];
						kunset = false;
					}
				} while ((u = v) != i);
				u = i;
				do {
					v = residual.path[u][i];
					if (residual.c[u][v] < 0)
						f[v][u] -= k;
					else
						f[u][v] += k;
				} while ((u = v) != i);
				return true;
			}
		return false;
	}

	static final int NONE = -1;

	int findPath(int from, int f[][]) // encontra um caminho entre os vertices nao balanceados
	{
		for (int i = 0; i < N; i++)
			if (f[from][i] > 0)
				return i;
		return NONE;
	}

	void printCPT(int startVertex) {
		int v = startVertex;

		int arcs[][] = new int[N][N];
		int f[][] = new int[N][N];
		for (int i = 0; i < N; i++)
			for (int j = 0; j < N; j++) {
				arcs[i][j] = this.arcs[i][j];
				f[i][j] = this.f[i][j];
			}

		while (true) {
			int u = v;
			if ((v = findPath(u, f)) != NONE) {
				f[u][v]--;
				for (int p; u != v; u = p) {
					p = path[u][v];
					System.out.println("Take arc " + cheapestLabel[u][p]
							+ " from " + u + " to " + p);
				}
			} else {
				int bridgeVertex = path[u][startVertex];
				if (arcs[u][bridgeVertex] == 0)
					break;
				v = bridgeVertex;
				for (int i = 0; i < N; i++)
					if (i != bridgeVertex && arcs[u][i] > 0) {
						v = i;
						break;
					}
				arcs[u][v]--;
				System.out.println("Take arc " + label[u][v].elementAt(arcs[u][v])
						+ " from " + u + " to " + v);
			}
		}
	}
}